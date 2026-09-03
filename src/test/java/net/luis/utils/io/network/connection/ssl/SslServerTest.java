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
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslServer}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class SslServerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	private static TlsProtocol supportedProtocol;
	private static String supportedCipherSuite;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SslTestContext.serverContext();
		clientContext = SslTestContext.clientContext();
		supportedProtocol = TlsProtocol.byName(serverContext.getSupportedSSLParameters().getProtocols()[0]).orElseThrow();
		supportedCipherSuite = serverContext.getSupportedSSLParameters().getCipherSuites()[0];
	}
	
	private static SslServerConfig defaultConfig() {
		return SslServerConfig.builder(serverContext).build();
	}
	
	private static void withServer(SslServerConfig config, ServerBody body) throws Exception {
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			body.accept(server);
		}
	}
	
	private static SSLSocket connect(SslServer server) throws Exception {
		SSLSocket socket = (SSLSocket) clientContext.getSocketFactory().createSocket("127.0.0.1", server.boundEndpoint().port());
		socket.startHandshake();
		return socket;
	}
	
	private static void awaitClientCount(SslServer server, int expected) throws Exception {
		for (int attempt = 0; attempt < 400 && server.getClientCount() != expected; attempt++) {
			Thread.sleep(25);
		}
		assertEquals(expected, server.getClientCount());
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
	
	@Test
	void connectionHandlerFailureReportedToErrorHandler() throws Exception {
		AtomicReference<NetworkErrorType> reportedType = new AtomicReference<>();
		AtomicReference<String> reportedMessage = new AtomicReference<>();
		AtomicReference<Throwable> reportedCause = new AtomicReference<>();
		CountDownLatch reported = new CountDownLatch(1);
		IllegalStateException failure = new IllegalStateException("handler failed");
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onConnection((server, connection) -> {
				throw failure;
			})
			.onError((connection, errorType, message, cause) -> {
				reportedType.set(errorType);
				reportedMessage.set(message);
				reportedCause.set(cause);
				reported.countDown();
			})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				assertTrue(reported.await(10, TimeUnit.SECONDS));
			}
		});
		
		assertEquals(NetworkErrorType.IO_ERROR, reportedType.get());
		assertEquals("Error in connection handler", reportedMessage.get());
		assertSame(failure, reportedCause.get());
	}
	
	@Test
	void connectionHandlerNetworkFailureReportedAsClientError() throws Exception {
		AtomicReference<NetworkErrorType> reportedType = new AtomicReference<>();
		AtomicReference<String> reportedMessage = new AtomicReference<>();
		CountDownLatch reported = new CountDownLatch(1);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onConnection((server, connection) -> {
				throw new NetworkConnectionException("peer vanished", NetworkErrorType.CONNECTION_RESET);
			})
			.onError((connection, errorType, message, cause) -> {
				reportedType.set(errorType);
				reportedMessage.set(message);
				reported.countDown();
			})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				assertTrue(reported.await(10, TimeUnit.SECONDS));
			}
		});
		
		assertEquals(NetworkErrorType.CONNECTION_RESET, reportedType.get());
		assertTrue(reportedMessage.get().startsWith("Client error: "));
	}
	
	@Test
	void connectionHandlerReadTimeoutIsSuppressed() throws Exception {
		AtomicBoolean errored = new AtomicBoolean(false);
		CountDownLatch finished = new CountDownLatch(1);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.clientReadTimeout(Duration.ofMillis(100))
			.onConnection((server, connection) -> {
				try {
					connection.receive();
				} finally {
					finished.countDown();
				}
			})
			.onError((connection, errorType, message, cause) -> errored.set(true))
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				assertTrue(finished.await(10, TimeUnit.SECONDS));
				Thread.sleep(250);
			}
		});
		
		assertFalse(errored.get());
	}
	
	@Test
	void connectionHandlerFailureStillClosesConnection() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onConnection((server, connection) -> {
				throw new IllegalStateException("handler failed");
			})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				assertEquals(-1, client.getInputStream().read());
			}
			awaitClientCount(server, 0);
		});
	}
	
	@Test
	void handleClientWithConnectionHandler() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.framing(false)
			.onConnection((server, connection) -> {
				OutputStream writer = connection.getOutputStream();
				writer.write("220 Ready\r\n".getBytes(StandardCharsets.US_ASCII));
				writer.flush();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
				reader.readLine();
			})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
				
				assertEquals("220 Ready", reader.readLine());
				
				client.getOutputStream().write("QUIT\r\n".getBytes(StandardCharsets.US_ASCII));
				client.getOutputStream().flush();
			}
		});
	}
	
	@Test
	void handleClientWithoutConnectionHandler() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.framing(false)
			.onMessage((server, connection, data) -> assertDoesNotThrow(() -> connection.send(data)))
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				client.getOutputStream().write("ping".getBytes(StandardCharsets.US_ASCII));
				client.getOutputStream().flush();
				
				assertEquals("ping", new String(client.getInputStream().readNBytes(4), StandardCharsets.US_ASCII));
			}
		});
	}
	
	@Test
	void connectionHandlerReturnClosesConnection() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onConnection((server, connection) -> {})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				assertEquals(-1, client.getInputStream().read());
			}
		});
	}
	
	@Test
	void connectionHandlerRunsOnServerManagedThread() throws Exception {
		AtomicReference<Thread> captured = new AtomicReference<>();
		CountDownLatch invoked = new CountDownLatch(1);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onConnection((server, connection) -> {
				captured.set(Thread.currentThread());
				invoked.countDown();
			})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				assertTrue(invoked.await(10, TimeUnit.SECONDS));
			}
		});
		
		assertNotSame(Thread.currentThread(), captured.get());
		assertTrue(captured.get().isVirtual());
	}
	
	@Test
	void connectionHandlerFiresConnectAndDisconnectEvents() throws Exception {
		List<String> events = Collections.synchronizedList(new ArrayList<>());
		CountDownLatch disconnected = new CountDownLatch(1);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onClientConnect((connection, local, remote, timestamp) -> events.add("connect"))
			.onConnection((server, connection) -> events.add("handle"))
			.onClientDisconnect((connection, local, remote, timestamp) -> {
				events.add("disconnect");
				disconnected.countDown();
			})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				assertTrue(disconnected.await(10, TimeUnit.SECONDS));
			}
		});
		
		assertEquals(List.of("connect", "handle", "disconnect"), events);
	}
	
	@Test
	void connectionHandlerClosingConnectionSkipsDisconnectEvent() throws Exception {
		AtomicBoolean disconnected = new AtomicBoolean(false);
		CountDownLatch handled = new CountDownLatch(1);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onConnection((server, connection) -> {
				connection.close();
				handled.countDown();
			})
			.onClientDisconnect((connection, local, remote, timestamp) -> disconnected.set(true))
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				assertTrue(handled.await(10, TimeUnit.SECONDS));
			}
			awaitClientCount(server, 0);
		});
		
		assertFalse(disconnected.get());
	}
	
	@Test
	void connectionHandlerEchoesOneLine() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.framing(false)
			.onConnection((server, connection) -> {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
				OutputStream writer = connection.getOutputStream();
				
				writer.write(("250 " + reader.readLine() + "\r\n").getBytes(StandardCharsets.US_ASCII));
				writer.flush();
			})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				client.getOutputStream().write("HELO\r\n".getBytes(StandardCharsets.US_ASCII));
				client.getOutputStream().flush();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
				assertEquals("250 HELO", reader.readLine());
			}
		});
	}
	
	@Test
	void connectionHandlerReadsRawStream() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.framing(false)
			.onConnection((server, connection) -> {
				byte[] received = connection.getInputStream().readNBytes(4);
				connection.getOutputStream().write(received);
				connection.getOutputStream().flush();
			})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				client.getOutputStream().write(new byte[] { 1, 2, 3, 4 });
				client.getOutputStream().flush();
				
				assertArrayEquals(new byte[] { 1, 2, 3, 4 }, client.getInputStream().readNBytes(4));
			}
		});
	}
	
	@Test
	void connectionHandlerServesMultipleClientsConcurrently() throws Exception {
		AtomicInteger invocations = new AtomicInteger(0);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.framing(false)
			.onConnection((server, connection) -> {
				invocations.incrementAndGet();
				OutputStream writer = connection.getOutputStream();
				writer.write("220 Ready\r\n".getBytes(StandardCharsets.US_ASCII));
				writer.flush();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
				writer.write(("250 " + reader.readLine() + "\r\n").getBytes(StandardCharsets.US_ASCII));
				writer.flush();
			})
			.build();
		
		withServer(config, server -> {
			for (int index = 0; index < 3; index++) {
				try (SSLSocket client = connect(server)) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
					assertEquals("220 Ready", reader.readLine());
					
					client.getOutputStream().write(("CMD" + index + "\r\n").getBytes(StandardCharsets.US_ASCII));
					client.getOutputStream().flush();
					assertEquals("250 CMD" + index, reader.readLine());
				}
			}
			
			awaitClientCount(server, 0);
			assertEquals(3, invocations.get());
		});
	}
	
	@Test
	void connectionHandlerMultiStepConversation() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.framing(false)
			.onConnection((server, connection) -> {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
				OutputStream writer = connection.getOutputStream();
				writer.write("220 Ready\r\n".getBytes(StandardCharsets.US_ASCII));
				writer.flush();
				
				String line;
				while ((line = reader.readLine()) != null) {
					boolean quit = line.startsWith("QUIT");
					writer.write((quit ? "221 Bye\r\n" : "250 Ok\r\n").getBytes(StandardCharsets.US_ASCII));
					writer.flush();
					if (quit) {
						break;
					}
				}
			})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
				OutputStream writer = client.getOutputStream();
				assertEquals("220 Ready", reader.readLine());
				
				for (String command : List.of("HELO\r\n", "MAIL\r\n")) {
					writer.write(command.getBytes(StandardCharsets.US_ASCII));
					writer.flush();
					assertEquals("250 Ok", reader.readLine());
				}
				
				writer.write("QUIT\r\n".getBytes(StandardCharsets.US_ASCII));
				writer.flush();
				assertEquals("221 Bye", reader.readLine());
				assertNull(reader.readLine());
			}
		});
	}
	
	@Test
	void connectionHandlerSurvivesStopWhileRunning() throws Exception {
		CountDownLatch started = new CountDownLatch(1);
		CountDownLatch ended = new CountDownLatch(1);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.framing(false)
			.onConnection((server, connection) -> {
				started.countDown();
				try {
					connection.getInputStream().read();
				} catch (IOException _) {
				} finally {
					ended.countDown();
				}
			})
			.build();
		
		try (SslServer server = new SslServer(EPHEMERAL, config)) {
			server.start();
			try (SSLSocket client = connect(server)) {
				assertTrue(started.await(10, TimeUnit.SECONDS));
				
				server.stop();
				
				assertTrue(ended.await(10, TimeUnit.SECONDS));
				assertFalse(server.isRunning());
			}
		}
	}
	
	@Test
	void connectionHandlerRunsAfterHandshake() throws Exception {
		AtomicReference<String> cipherSuite = new AtomicReference<>();
		CountDownLatch invoked = new CountDownLatch(1);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onConnection((server, connection) -> {
				cipherSuite.set(connection.getSession().getCipherSuite());
				invoked.countDown();
			})
			.build();
		
		withServer(config, server -> {
			try (SSLSocket client = connect(server)) {
				assertTrue(invoked.await(10, TimeUnit.SECONDS));
			}
		});
		
		assertNotNull(cipherSuite.get());
		assertNotEquals("SSL_NULL_WITH_NULL_NULL", cipherSuite.get());
	}
	
	@Test
	void connectionHandlerNotInvokedOnHandshakeFailure() throws Exception {
		AtomicBoolean invoked = new AtomicBoolean(false);
		AtomicReference<NetworkErrorType> reported = new AtomicReference<>();
		CountDownLatch errored = new CountDownLatch(1);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.clientAuth(SslClientAuth.REQUIRED)
			.onConnection((server, connection) -> invoked.set(true))
			.onError((connection, errorType, message, cause) -> {
				reported.set(errorType);
				errored.countDown();
			})
			.build();
		
		withServer(config, server -> {
			SSLContext trustOnly = SslTestContext.trustOnlyClientContext();
			try (SSLSocket client = (SSLSocket) trustOnly.getSocketFactory().createSocket("127.0.0.1", server.boundEndpoint().port())) {
				client.startHandshake();
				client.getInputStream().read();
			} catch (IOException _) {}
			
			assertTrue(errored.await(10, TimeUnit.SECONDS));
		});
		
		assertFalse(invoked.get());
		assertEquals(NetworkErrorType.HANDSHAKE_FAILED, reported.get());
	}
	
	@FunctionalInterface
	private interface ServerBody {
		
		void accept(SslServer server) throws Exception;
	}
}
