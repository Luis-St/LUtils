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


package net.luis.utils.io.network.connection.ssl;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslServer}.<br>
 *
 * @author Luis-St
 */
class SslServerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	
	private static SSLContext serverContext;
	private static TlsProtocol supportedProtocol;
	private static String supportedCipherSuite;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SslTestContext.serverContext();
		supportedProtocol = TlsProtocol.byName(serverContext.getSupportedSSLParameters().getProtocols()[0]).orElseThrow();
		supportedCipherSuite = serverContext.getSupportedSSLParameters().getCipherSuites()[0];
	}
	
	private static SslServerConfig defaultConfig() {
		return SslServerConfig.builder(serverContext).build();
	}
	
	@Test
	void constructWithBindEndpointAndConfig() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			assertFalse(server.isRunning());
			assertEquals(0, server.getClientCount());
			assertEquals(EPHEMERAL, server.boundEndpoint());
		}
	}
	
	@Test
	void constructWithNullBindEndpoint() {
		assertThrows(NullPointerException.class, () -> new SslServer(null, defaultConfig()));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SslServer(EPHEMERAL, null));
	}
	
	@Test
	void startOnWithNullBindEndpoint() {
		assertThrows(NullPointerException.class, () -> SslServer.startOn(null, defaultConfig()));
	}
	
	@Test
	void startOnWithNullConfig() {
		assertThrows(NullPointerException.class, () -> SslServer.startOn(EPHEMERAL, null));
	}
	
	@Test
	void broadcastWithNullData() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			assertThrows(NullPointerException.class, () -> server.broadcast(null));
		}
	}
	
	@Test
	void startOnWithInvalidCipherSuiteClosesServerAndRethrows() {
		SslServerConfig config = SslServerConfig.builder(serverContext).enabledCipherSuites(List.of("NO-SUCH-SUITE")).build();
		
		assertThrows(IllegalArgumentException.class, () -> SslServer.startOn(EPHEMERAL, config));
	}
	
	@Test
	void isRunningFalseBeforeStart() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void isRunningTrueAfterStart() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void isRunningFalseAfterStop() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			server.start();
			server.stop();
			
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void boundEndpointReturnsConfiguredEndpointBeforeStart() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			assertEquals(EPHEMERAL, server.boundEndpoint());
			assertEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void boundEndpointReturnsActualPortAfterStart() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			server.start();
			
			assertNotEquals(0, server.boundEndpoint().port());
			assertEquals(Ipv4Address.LOOPBACK, server.boundEndpoint().address());
		}
	}
	
	@Test
	void startTwiceIsNoOp() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			server.start();
			IpEndpoint bound = server.boundEndpoint();
			
			assertDoesNotThrow(server::start);
			assertTrue(server.isRunning());
			assertEquals(bound, server.boundEndpoint());
		}
	}
	
	@Test
	void startWithoutEnabledProtocolsUsesContextDefaults() {
		SslServerConfig config = SslServerConfig.builder(serverContext).build();
		assertTrue(config.enabledProtocols().isEmpty());
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void startWithEnabledProtocolsApplied() {
		SslServerConfig config = SslServerConfig.builder(serverContext).enabledProtocols(List.of(supportedProtocol)).build();
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void startWithoutEnabledCipherSuitesUsesContextDefaults() {
		SslServerConfig config = SslServerConfig.builder(serverContext).build();
		assertTrue(config.enabledCipherSuites().isEmpty());
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void startWithEnabledCipherSuitesApplied() {
		SslServerConfig config = SslServerConfig.builder(serverContext).enabledCipherSuites(List.of(supportedCipherSuite)).build();
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void startWithClientAuthNone() {
		SslServerConfig config = SslServerConfig.builder(serverContext).clientAuth(SslClientAuth.NONE).build();
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void startWithClientAuthRequested() {
		SslServerConfig config = SslServerConfig.builder(serverContext).clientAuth(SslClientAuth.REQUESTED).build();
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void startWithClientAuthRequired() {
		SslServerConfig config = SslServerConfig.builder(serverContext).clientAuth(SslClientAuth.REQUIRED).build();
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void startOnPortAlreadyInUseReportsAddressInUse() {
		AtomicReference<NetworkErrorType> reported = new AtomicReference<>();
		SslServerConfig config = SslServerConfig.builder(serverContext).onError((conn, type, message, cause) -> reported.set(type)).build();
		
		try (SslServer first = new SslServer(EPHEMERAL, defaultConfig())) {
			first.start();
			
			try (SslServer second = new SslServer(first.boundEndpoint(), config)) {
				assertDoesNotThrow(second::start);
				assertFalse(second.isRunning());
				assertEquals(NetworkErrorType.ADDRESS_IN_USE, reported.get());
			}
		}
	}
	
	@Test
	void stopBeforeStartIsNoOp() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			assertDoesNotThrow(server::stop);
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void stopTwiceIsNoOp() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			server.start();
			
			assertDoesNotThrow(server::stop);
			assertDoesNotThrow(server::stop);
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void stopLeavesCustomExecutorRunning() {
		ExecutorService custom = Executors.newSingleThreadExecutor();
		SslServerConfig config = SslServerConfig.builder(serverContext).executorStrategy(ClientExecutorStrategy.custom(custom)).build();
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			server.stop();
			
			assertFalse(custom.isShutdown());
		} finally {
			custom.shutdownNow();
		}
	}
	
	@Test
	void broadcastWithoutConnectionsDoesNothing() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			server.start();
			
			assertDoesNotThrow(() -> server.broadcast("data".getBytes()));
			assertEquals(0, server.getClientCount());
		}
	}
	
	@Test
	void closeDelegatesToStop() {
		SslServer server = new SslServer(EPHEMERAL, defaultConfig());
		server.start();
		assertTrue(server.isRunning());
		
		server.close();
		assertFalse(server.isRunning());
	}
	
	@Test
	void getClientCountZeroBeforeStart() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			assertEquals(0, server.getClientCount());
		}
	}
	
	@Test
	void getClientCountZeroWhileRunningWithoutClients() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			server.start();
			
			assertEquals(0, server.getClientCount());
		}
	}
	
	@Test
	void getClientCountZeroAfterStop() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			server.start();
			server.stop();
			
			assertEquals(0, server.getClientCount());
		}
	}
	
	@Test
	void startOnReturnsRunningServer() {
		try (SslServer server = SslServer.startOn(EPHEMERAL, defaultConfig())) {
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void closeBeforeStartIsNoOp() {
		SslServer server = new SslServer(EPHEMERAL, defaultConfig());
		
		assertDoesNotThrow(server::close);
		assertFalse(server.isRunning());
	}
	
	@Test
	void implementsNetworkServer() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			assertInstanceOf(NetworkServer.class, server);
		}
	}
	
	@Test
	void startOnIpv6LoopbackEndpoint() {
		try (SslServer server = new SslServer(new IpEndpoint(Ipv6Address.LOOPBACK, 0), defaultConfig())) {
			server.start();
			
			assertTrue(server.isRunning());
			assertEquals(6, server.boundEndpoint().address().version());
		}
	}
	
	@Test
	void startStopRestartCycleConsistency() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
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
	void startWithProtocolsCipherSuitesAndClientAuthCombined() {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.enabledProtocols(List.of(supportedProtocol))
			.enabledCipherSuites(List.of(supportedCipherSuite))
			.clientAuth(SslClientAuth.REQUIRED)
			.build();
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
		}
	}
	
	@Test
	void broadcastAndClientCountConsistencyAcrossLifecycle() {
		try (SslServer server = new SslServer(EPHEMERAL, defaultConfig())) {
			assertEquals(0, server.getClientCount());
			assertDoesNotThrow(() -> server.broadcast(new byte[1]));
			assertThrows(NullPointerException.class, () -> server.broadcast(null));
			
			server.start();
			assertEquals(0, server.getClientCount());
			assertDoesNotThrow(() -> server.broadcast(new byte[1]));
			
			server.stop();
			assertEquals(0, server.getClientCount());
			assertThrows(NullPointerException.class, () -> server.broadcast(null));
		}
	}
	
	@Test
	void startWithUnsupportedCipherSuiteRejectedAtStart() {
		SslServerConfig config = SslServerConfig.builder(serverContext).enabledCipherSuites(List.of("TLS_NO_SUCH_SUITE")).build();
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			assertThrows(IllegalArgumentException.class, server::start);
			assertEquals(EPHEMERAL, server.boundEndpoint());
		}
	}
}
