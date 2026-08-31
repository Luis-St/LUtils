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


package net.luis.utils.io.network.connection.tcp;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TcpServer}.<br>
 *
 * @author Luis-St
 */
class TcpServerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	
	@Test
	void constructWithBindEndpoint() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			assertFalse(server.isRunning());
			assertEquals(0, server.getClientCount());
			assertEquals(EPHEMERAL, server.boundEndpoint());
		}
	}
	
	@Test
	void constructWithBindEndpointAndConfig() {
		TcpServerConfig config = TcpServerConfig.builder().backlog(1).build();
		
		try (TcpServer server = new TcpServer(EPHEMERAL, config)) {
			assertFalse(server.isRunning());
			assertEquals(0, server.getClientCount());
		}
	}
	
	@Test
	void constructWithNullBindEndpoint() {
		assertThrows(NullPointerException.class, () -> new TcpServer(null, TcpServerConfig.DEFAULT));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new TcpServer(EPHEMERAL, null));
	}
	
	@Test
	void constructWithNullBindEndpointSingleArgument() {
		assertThrows(NullPointerException.class, () -> new TcpServer(null));
	}
	
	@Test
	void startOnWithNullBindEndpoint() {
		assertThrows(NullPointerException.class, () -> TcpServer.startOn(null));
	}
	
	@Test
	void startOnWithNullBindEndpointAndConfig() {
		assertThrows(NullPointerException.class, () -> TcpServer.startOn(null, TcpServerConfig.DEFAULT));
	}
	
	@Test
	void startOnWithNullConfig() {
		assertThrows(NullPointerException.class, () -> TcpServer.startOn(EPHEMERAL, null));
	}
	
	@Test
	void broadcastWithNullData() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			assertThrows(NullPointerException.class, () -> server.broadcast(null));
		}
	}
	
	@Test
	void isRunningFalseBeforeStart() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void isRunningTrueAfterStart() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void isRunningFalseAfterStop() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			server.start();
			server.stop();
			
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void boundEndpointReturnsConfiguredEndpointBeforeStart() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			assertEquals(EPHEMERAL, server.boundEndpoint());
			assertEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void boundEndpointReturnsActualPortAfterStart() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			server.start();
			
			assertNotEquals(0, server.boundEndpoint().port());
			assertEquals(Ipv4Address.LOOPBACK, server.boundEndpoint().address());
		}
	}
	
	@Test
	void startTwiceIsNoOp() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
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
		TcpServerConfig config = TcpServerConfig.builder().onError((conn, type, message, cause) -> reported.set(type)).build();
		
		try (TcpServer first = new TcpServer(EPHEMERAL)) {
			first.start();
			
			try (TcpServer second = new TcpServer(first.boundEndpoint(), config)) {
				assertDoesNotThrow(second::start);
				assertFalse(second.isRunning());
				assertEquals(NetworkErrorType.ADDRESS_IN_USE, reported.get());
			}
		}
	}
	
	@Test
	void stopBeforeStartIsNoOp() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			assertDoesNotThrow(server::stop);
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void stopTwiceIsNoOp() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			server.start();
			
			assertDoesNotThrow(server::stop);
			assertDoesNotThrow(server::stop);
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void stopShutsDownOwnedExecutor() {
		TcpServerConfig config = TcpServerConfig.builder().executorStrategy(ClientExecutorStrategy.fixedPool(1)).build();
		
		try (TcpServer server = new TcpServer(EPHEMERAL, config)) {
			server.start();
			assertTrue(server.isRunning());
			
			assertDoesNotThrow(server::stop);
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void stopLeavesCustomExecutorRunning() {
		ExecutorService custom = Executors.newSingleThreadExecutor();
		TcpServerConfig config = TcpServerConfig.builder().executorStrategy(ClientExecutorStrategy.custom(custom)).build();
		
		try (TcpServer server = new TcpServer(EPHEMERAL, config)) {
			server.start();
			server.stop();
			
			assertFalse(custom.isShutdown());
		} finally {
			custom.shutdownNow();
		}
	}
	
	@Test
	void broadcastWithoutConnectionsDoesNothing() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			server.start();
			
			assertDoesNotThrow(() -> server.broadcast("data".getBytes()));
			assertEquals(0, server.getClientCount());
		}
	}
	
	@Test
	void closeDelegatesToStop() {
		TcpServer server = new TcpServer(EPHEMERAL);
		server.start();
		assertTrue(server.isRunning());
		
		server.close();
		assertFalse(server.isRunning());
	}
	
	@Test
	void getClientCountZeroBeforeStart() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			assertEquals(0, server.getClientCount());
		}
	}
	
	@Test
	void getClientCountZeroWhileRunningWithoutClients() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			server.start();
			
			assertEquals(0, server.getClientCount());
		}
	}
	
	@Test
	void getClientCountZeroAfterStop() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			server.start();
			server.stop();
			
			assertEquals(0, server.getClientCount());
		}
	}
	
	@Test
	void startOnReturnsRunningServer() {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL)) {
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void startOnWithConfigReturnsRunningServer() {
		TcpServerConfig config = TcpServerConfig.builder().backlog(1).build();
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config)) {
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void closeBeforeStartIsNoOp() {
		TcpServer server = new TcpServer(EPHEMERAL);
		
		assertDoesNotThrow(server::close);
		assertFalse(server.isRunning());
	}
	
	@Test
	void implementsNetworkServer() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			assertInstanceOf(NetworkServer.class, server);
		}
	}
	
	@Test
	void startOnIpv6LoopbackEndpoint() {
		try (TcpServer server = new TcpServer(new IpEndpoint(Ipv6Address.LOOPBACK, 0))) {
			server.start();
			
			assertTrue(server.isRunning());
			assertEquals(6, server.boundEndpoint().address().version());
		}
	}
	
	@Test
	void startStopRestartCycleConsistency() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
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
	void broadcastAndClientCountConsistencyAcrossLifecycle() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			assertEquals(0, server.getClientCount());
			assertDoesNotThrow(() -> server.broadcast(new byte[1]));
			assertThrows(NullPointerException.class, () -> server.broadcast(null));
			
			server.start();
			assertEquals(0, server.getClientCount());
			assertDoesNotThrow(() -> server.broadcast(new byte[1]));
			assertThrows(NullPointerException.class, () -> server.broadcast(null));
			
			server.stop();
			assertEquals(0, server.getClientCount());
			assertDoesNotThrow(() -> server.broadcast(new byte[1]));
			assertThrows(NullPointerException.class, () -> server.broadcast(null));
		}
	}
	
	@Test
	void boundEndpointConsistencyAcrossLifecycle() {
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			assertEquals(EPHEMERAL, server.boundEndpoint());
			
			server.start();
			IpEndpoint running = server.boundEndpoint();
			assertNotEquals(0, running.port());
			
			server.stop();
			assertEquals(running, server.boundEndpoint());
		}
	}
}
