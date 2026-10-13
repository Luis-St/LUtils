/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */


package net.luis.utils.io.network.connection.udp;

import net.luis.utils.io.network.HostEndpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UdpServer}.<br>
 *
 * @author Luis-St
 */
class UdpServerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final IpEndpoint DESTINATION = new IpEndpoint(Ipv4Address.LOOPBACK, 9999);
	private static final HostEndpoint UNRESOLVABLE = new HostEndpoint("no-such-host.invalid", 9999);
	
	@Test
	void constructWithBindEndpoint() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			assertFalse(server.isRunning());
			assertEquals(EPHEMERAL, server.boundEndpoint());
		}
	}
	
	@Test
	void constructWithBindEndpointAndConfig() {
		UdpServerConfig config = UdpServerConfig.builder().bufferSize(16).build();
		
		try (UdpServer server = new UdpServer(EPHEMERAL, config)) {
			assertFalse(server.isRunning());
			assertEquals(EPHEMERAL, server.boundEndpoint());
		}
	}
	
	@Test
	void constructWithNullBindEndpoint() {
		assertThrows(NullPointerException.class, () -> new UdpServer(null, UdpServerConfig.DEFAULT));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new UdpServer(EPHEMERAL, null));
	}
	
	@Test
	void constructWithNullBindEndpointSingleArgument() {
		assertThrows(NullPointerException.class, () -> new UdpServer(null));
	}
	
	@Test
	void startOnWithNullBindEndpoint() {
		assertThrows(NullPointerException.class, () -> UdpServer.startOn(null));
	}
	
	@Test
	void startOnWithNullBindEndpointAndConfig() {
		assertThrows(NullPointerException.class, () -> UdpServer.startOn(null, UdpServerConfig.DEFAULT));
	}
	
	@Test
	void startOnWithNullConfig() {
		assertThrows(NullPointerException.class, () -> UdpServer.startOn(EPHEMERAL, null));
	}
	
	@Test
	void sendWithNullDestination() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			server.start();
			
			assertThrows(NullPointerException.class, () -> server.send(null, new byte[1]));
		}
	}
	
	@Test
	void sendWithNullData() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			server.start();
			
			assertThrows(NullPointerException.class, () -> server.send(DESTINATION, null));
		}
	}
	
	@Test
	void sendDatagramWithNullDatagram() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			server.start();
			
			assertThrows(NullPointerException.class, () -> server.send(null));
		}
	}
	
	@Test
	void isRunningFalseBeforeStart() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void isRunningTrueAfterStart() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void isRunningFalseAfterStop() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			server.start();
			server.stop();
			
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void boundEndpointReturnsConfiguredEndpointBeforeStart() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			assertEquals(EPHEMERAL, server.boundEndpoint());
			assertEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void boundEndpointReturnsActualPortAfterStart() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			server.start();
			
			assertNotEquals(0, server.boundEndpoint().port());
			assertEquals(Ipv4Address.LOOPBACK, server.boundEndpoint().address());
		}
	}
	
	@Test
	void startTwiceIsNoOp() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			server.start();
			IpEndpoint bound = server.boundEndpoint();
			
			assertDoesNotThrow(server::start);
			assertTrue(server.isRunning());
			assertEquals(bound, server.boundEndpoint());
		}
	}
	
	@Test
	void startOnPortAlreadyInUseReportsAddressInUse() {
		AtomicReference<NetworkErrorType> reported = new AtomicReference<>();
		UdpServerConfig config = UdpServerConfig.builder().onError((conn, type, message, cause) -> reported.set(type)).build();
		
		try (UdpServer first = new UdpServer(EPHEMERAL)) {
			first.start();
			
			try (UdpServer second = new UdpServer(first.boundEndpoint(), config)) {
				assertDoesNotThrow(second::start);
				assertFalse(second.isRunning());
				assertEquals(NetworkErrorType.ADDRESS_IN_USE, reported.get());
			}
		}
	}
	
	@Test
	void stopBeforeStartIsNoOp() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			assertDoesNotThrow(server::stop);
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void stopTwiceIsNoOp() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			server.start();
			
			assertDoesNotThrow(server::stop);
			assertDoesNotThrow(server::stop);
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void stopLeavesCustomExecutorRunning() {
		ExecutorService custom = Executors.newSingleThreadExecutor();
		UdpServerConfig config = UdpServerConfig.builder().executorStrategy(ClientExecutorStrategy.custom(custom)).build();
		
		try (UdpServer server = new UdpServer(EPHEMERAL, config)) {
			server.start();
			server.stop();
			
			assertFalse(custom.isShutdown());
		} finally {
			custom.shutdownNow();
		}
	}
	
	@Test
	void sendWithoutStartThrowsSocketClosed() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> server.send(DESTINATION, new byte[1]));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		}
	}
	
	@Test
	void sendAfterStopThrowsSocketClosed() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			server.start();
			server.stop();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> server.send(DESTINATION, new byte[1]));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		}
	}
	
	@Test
	void sendDataExceedingBufferSizeThrowsBeforeRunningCheck() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> server.send(DESTINATION, new byte[65536]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertNotEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		}
	}
	
	@Test
	void sendDataEqualToBufferSizePassesSizeCheck() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> server.send(DESTINATION, new byte[65535]));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		}
	}
	
	@Test
	void sendDataExceedingConfiguredBufferSize() {
		UdpServerConfig config = UdpServerConfig.builder().bufferSize(16).build();
		
		try (UdpServer server = new UdpServer(EPHEMERAL, config)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> server.send(DESTINATION, new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void sendToUnresolvedHostThrowsHostUnreachable() {
		AtomicInteger errorCount = new AtomicInteger(0);
		UdpServerConfig config = UdpServerConfig.builder().onError((conn, type, message, cause) -> errorCount.incrementAndGet()).build();
		
		try (UdpServer server = new UdpServer(EPHEMERAL, config)) {
			server.start();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> server.send(UNRESOLVABLE, new byte[1]));
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
			assertInstanceOf(UnknownHostException.class, exception.getCause());
			assertEquals(1, errorCount.get());
		}
	}
	
	@Test
	void closeDelegatesToStop() {
		UdpServer server = new UdpServer(EPHEMERAL);
		server.start();
		assertTrue(server.isRunning());
		
		server.close();
		assertFalse(server.isRunning());
	}
	
	@Test
	void startOnReturnsRunningServer() {
		try (UdpServer server = UdpServer.startOn(EPHEMERAL)) {
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void startOnWithConfigReturnsRunningServer() {
		UdpServerConfig config = UdpServerConfig.builder().bufferSize(1024).build();
		
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, config)) {
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void closeBeforeStartIsNoOp() {
		UdpServer server = new UdpServer(EPHEMERAL);
		
		assertDoesNotThrow(server::close);
		assertFalse(server.isRunning());
	}
	
	@Test
	void implementsNetworkServer() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			assertInstanceOf(NetworkServer.class, server);
		}
	}
	
	@Test
	void startOnIpv6LoopbackEndpoint() {
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv6Address.LOOPBACK, 0))) {
			server.start();
			
			assertTrue(server.isRunning());
			assertEquals(6, server.boundEndpoint().address().version());
		}
	}
	
	@Test
	void sendDatagramWithoutStartThrowsSocketClosed() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			UdpDatagram datagram = new UdpDatagram(DESTINATION, new byte[1]);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> server.send(datagram));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		}
	}
	
	@Test
	void startStopRestartCycleConsistency() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			server.start();
			assertTrue(server.isRunning());
			
			server.stop();
			assertFalse(server.isRunning());
			
			server.start();
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void sendGuardPrecedenceOnStoppedServer() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			assertThrows(NullPointerException.class, () -> server.send(null, new byte[1]));
			assertThrows(NullPointerException.class, () -> server.send(DESTINATION, null));
			
			NetworkConnectionException tooLarge = assertThrows(NetworkConnectionException.class, () -> server.send(DESTINATION, new byte[65536]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, tooLarge.errorType());
			
			NetworkConnectionException closed = assertThrows(NetworkConnectionException.class, () -> server.send(DESTINATION, new byte[1]));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, closed.errorType());
		}
	}
	
	@Test
	void sendDatagramDelegatesGuardsToEndpointOverload() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			UdpDatagram datagram = new UdpDatagram(DESTINATION, new byte[65536]);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> server.send(datagram));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void boundEndpointConsistencyAcrossLifecycle() {
		try (UdpServer server = new UdpServer(EPHEMERAL)) {
			assertEquals(EPHEMERAL, server.boundEndpoint());
			
			server.start();
			assertNotEquals(0, server.boundEndpoint().port());
			
			server.stop();
			assertThrows(NullPointerException.class, server::boundEndpoint);
		}
	}
}
