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

import net.luis.utils.io.network.*;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.exception.*;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslSockets}.<br>
 *
 * @author Luis-St
 */
@Tag("tls")
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SslSocketsTest {
	
	private static final String ECDHE_SUITE = "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256";
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	private static SslServerConfig serverConfig;
	private static SslUpgradeConfig clientConfig;
	private static ExecutorService executor;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SslTestContext.serverContext();
		clientContext = SslTestContext.clientContext();
		serverConfig = SslServerConfig.builder(serverContext).build();
		clientConfig = SslUpgradeConfig.builder().sslContext(clientContext).build();
		executor = Executors.newCachedThreadPool();
	}
	
	@AfterAll
	static void cleanUp() {
		executor.shutdownNow();
	}
	
	private static SocketPair pair() throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
			Socket client = new Socket("127.0.0.1", serverSocket.getLocalPort());
			Socket server = serverSocket.accept();
			return new SocketPair(client, server);
		}
	}
	
	private static IpEndpoint loopback(SocketPair pair) {
		return new IpEndpoint(Ipv4Address.LOOPBACK, pair.client().getPort());
	}
	
	private static Future<SSLSocket> layerServer(Socket socket, byte[] consumed, SslServerConfig config) {
		return executor.submit(() -> SslSockets.layerServer(socket, consumed, config));
	}
	
	private static Future<SSLSocket> layerServer(SocketPair pair, SslServerConfig config) {
		return layerServer(pair.server(), new byte[0], config);
	}
	
	private static Future<SSLSocket> layerClient(SocketPair pair, Endpoint peer, SslUpgradeConfig config) {
		return executor.submit(() -> SslSockets.layerClient(pair.client(), peer, config));
	}
	
	@Test
	void constructorIsPrivate() {
		Constructor<?>[] constructors = SslSockets.class.getDeclaredConstructors();
		
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
	}
	
	@Test
	void layerClientWithNullArguments() throws Exception {
		try (SocketPair pair = pair()) {
			IpEndpoint peer = loopback(pair);
			
			assertThrows(NullPointerException.class, () -> SslSockets.layerClient(null, peer, clientConfig));
			assertThrows(NullPointerException.class, () -> SslSockets.layerClient(pair.client(), null, clientConfig));
			assertThrows(NullPointerException.class, () -> SslSockets.layerClient(pair.client(), peer, null));
		}
	}
	
	@Test
	void layerClientWithUnconnectedSocket() throws Exception {
		IpEndpoint peer = new IpEndpoint(Ipv4Address.LOOPBACK, 443);
		try (Socket socket = new Socket()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslSockets.layerClient(socket, peer, clientConfig));
			
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
			assertEquals(peer, exception.endpoint());
		}
	}
	
	@Test
	void layerClientWithClosedSocket() throws Exception {
		try (SocketPair pair = pair()) {
			pair.client().close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslSockets.layerClient(pair.client(), loopback(pair), clientConfig));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void layerClientWithUntrustedCertificate() throws Exception {
		try (SocketPair pair = pair()) {
			Future<SSLSocket> server = layerServer(pair, serverConfig);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslSockets.layerClient(pair.client(), loopback(pair), SslUpgradeConfig.DEFAULT));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertInstanceOf(IOException.class, exception.getCause());
			assertThrows(ExecutionException.class, () -> server.get(10, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void layerClientWithHostnameMismatch() throws Exception {
		try (SocketPair pair = pair()) {
			Future<SSLSocket> server = layerServer(pair, serverConfig);
			HostEndpoint peer = new HostEndpoint("example.invalid", pair.client().getPort());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslSockets.layerClient(pair.client(), peer, clientConfig));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertThrows(ExecutionException.class, () -> server.get(10, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void layerClientWithUnknownCipherSuite() throws Exception {
		SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(clientContext).enabledCipherSuites(List.of("NOT_A_SUITE")).build();
		try (SocketPair pair = pair()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslSockets.layerClient(pair.client(), loopback(pair), config));
			
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertInstanceOf(IllegalArgumentException.class, exception.getCause());
		}
	}
	
	@Test
	void layerClientHandshakeTimesOut() throws Exception {
		try (SocketPair pair = pair()) {
			pair.client().setSoTimeout(200);
			
			NetworkTimeoutException exception = assertThrows(NetworkTimeoutException.class, () -> SslSockets.layerClient(pair.client(), loopback(pair), clientConfig));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertEquals(Duration.ofMillis(200), exception.timeout());
		}
	}
	
	@Test
	void layerServerWithNullArguments() throws Exception {
		try (SocketPair pair = pair()) {
			assertThrows(NullPointerException.class, () -> SslSockets.layerServer(null, new byte[0], serverConfig));
			assertThrows(NullPointerException.class, () -> SslSockets.layerServer(pair.server(), null, serverConfig));
			assertThrows(NullPointerException.class, () -> SslSockets.layerServer(pair.server(), new byte[0], null));
		}
	}
	
	@Test
	void layerServerWithClosedSocket() throws Exception {
		try (SocketPair pair = pair()) {
			pair.server().close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslSockets.layerServer(pair.server(), new byte[0], serverConfig));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void layerServerHandshakeFails() throws Exception {
		try (SocketPair pair = pair()) {
			pair.client().getOutputStream().write("GARBAGE PLAINTEXT THAT IS NOT A CLIENT HELLO\r\n".getBytes(StandardCharsets.US_ASCII));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslSockets.layerServer(pair.server(), new byte[0], serverConfig));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
		}
	}
	
	@Test
	void layerServerRequiredClientAuthWithoutCertificate() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext).clientAuth(SslClientAuth.REQUIRED).build();
		SslUpgradeConfig trustOnly = SslUpgradeConfig.builder().sslContext(SslTestContext.trustOnlyClientContext()).build();
		try (SocketPair pair = pair()) {
			Future<SSLSocket> server = layerServer(pair, config);
			
			boolean clientFailed;
			try (SSLSocket client = SslSockets.layerClient(pair.client(), loopback(pair), trustOnly)) {
				clientFailed = client.getInputStream().read() == -1;
			} catch (NetworkConnectionException e) {
				assertEquals(NetworkErrorType.HANDSHAKE_FAILED, e.errorType());
				clientFailed = true;
			} catch (IOException e) {
				clientFailed = true;
			}
			
			ExecutionException serverFailure = assertThrows(ExecutionException.class, () -> server.get(10, TimeUnit.SECONDS));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, assertInstanceOf(NetworkConnectionException.class, serverFailure.getCause()).errorType());
			assertTrue(clientFailed);
		}
	}
	
	@Test
	void layerClientAndServerWithDefaults() throws Exception {
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, serverConfig);
			
			try (SSLSocket client = SslSockets.layerClient(pair.client(), loopback(pair), clientConfig); SSLSocket server = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertTrue(client.getSession().isValid());
				assertTrue(server.getSession().isValid());
				assertFalse(server.getNeedClientAuth());
				assertFalse(server.getWantClientAuth());
			}
		}
	}
	
	@Test
	void layerClientWithoutHostnameVerification() throws Exception {
		SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(clientContext).verifyHostname(false).build();
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, serverConfig);
			HostEndpoint peer = new HostEndpoint("example.invalid", pair.client().getPort());
			
			try (SSLSocket client = SslSockets.layerClient(pair.client(), peer, config); SSLSocket _ = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertTrue(client.getSession().isValid());
			}
		}
	}
	
	@Test
	void layerClientWithHostnameVerificationOnLiteralAddress() throws Exception {
		SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(clientContext).verifyHostname(true).build();
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, serverConfig);
			
			try (SSLSocket client = SslSockets.layerClient(pair.client(), loopback(pair), config); SSLSocket _ = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertTrue(client.getSession().isValid());
				assertEquals("127.0.0.1", client.getSession().getPeerHost());
			}
		}
	}
	
	@Test
	void layerClientSendsHostnameAsServerName() throws Exception {
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, serverConfig);
			HostEndpoint peer = new HostEndpoint("localhost", pair.client().getPort());
			
			try (SSLSocket client = SslSockets.layerClient(pair.client(), peer, clientConfig); SSLSocket _ = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertEquals("localhost", client.getSession().getPeerHost());
			}
		}
	}
	
	@Test
	void layerClientWithEnabledProtocols() throws Exception {
		SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(clientContext).enabledProtocols(List.of(TlsProtocol.TLS_V1_2)).build();
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, serverConfig);
			
			try (SSLSocket client = SslSockets.layerClient(pair.client(), loopback(pair), config); SSLSocket _ = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertEquals("TLSv1.2", client.getSession().getProtocol());
			}
		}
	}
	
	@Test
	void layerClientWithEnabledCipherSuites() throws Exception {
		SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(clientContext).enabledProtocols(List.of(TlsProtocol.TLS_V1_3)).enabledCipherSuites(List.of("TLS_AES_128_GCM_SHA256")).build();
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, serverConfig);
			
			try (SSLSocket client = SslSockets.layerClient(pair.client(), loopback(pair), config); SSLSocket _ = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertEquals("TLS_AES_128_GCM_SHA256", client.getSession().getCipherSuite());
			}
		}
	}
	
	@Test
	void layerClientUsesConfiguredSslContext() throws Exception {
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, serverConfig);
			
			try (SSLSocket client = assertDoesNotThrow(() -> SslSockets.layerClient(pair.client(), loopback(pair), clientConfig)); SSLSocket _ = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertTrue(client.getSession().isValid());
			}
		}
	}
	
	@Test
	void layerServerWithRequestedClientAuth() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext).clientAuth(SslClientAuth.REQUESTED).build();
		SslUpgradeConfig trustOnly = SslUpgradeConfig.builder().sslContext(SslTestContext.trustOnlyClientContext()).build();
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, config);
			
			try (SSLSocket client = SslSockets.layerClient(pair.client(), loopback(pair), trustOnly); SSLSocket server = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertTrue(server.getWantClientAuth());
				assertTrue(client.getSession().isValid());
			}
		}
	}
	
	@Test
	void layerServerWithRequiredClientAuth() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext).clientAuth(SslClientAuth.REQUIRED).build();
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, config);
			
			try (SSLSocket _ = SslSockets.layerClient(pair.client(), loopback(pair), clientConfig); SSLSocket server = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertTrue(server.getNeedClientAuth());
				assertTrue(server.getSession().getPeerCertificates().length > 0);
			}
		}
	}
	
	@Test
	void layerServerWithEnabledProtocols() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext).enabledProtocols(List.of(TlsProtocol.TLS_V1_2)).build();
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, config);
			
			try (SSLSocket client = SslSockets.layerClient(pair.client(), loopback(pair), clientConfig); SSLSocket server = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertEquals("TLSv1.2", server.getSession().getProtocol());
				assertEquals("TLSv1.2", client.getSession().getProtocol());
			}
		}
	}
	
	@Test
	void layerServerWithUnknownCipherSuite() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext).enabledCipherSuites(List.of("NOT_A_SUITE")).build();
		try (SocketPair pair = pair()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslSockets.layerServer(pair.server(), new byte[0], config));
			
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertInstanceOf(IllegalArgumentException.class, exception.getCause());
		}
	}
	
	@Test
	void layerServerWithConsumedBytes() throws Exception {
		try (SocketPair pair = pair()) {
			Future<SSLSocket> clientFuture = layerClient(pair, loopback(pair), clientConfig);
			byte[] consumed = pair.server().getInputStream().readNBytes(5);
			assertEquals(5, consumed.length);
			
			try (SSLSocket server = SslSockets.layerServer(pair.server(), consumed, serverConfig); SSLSocket client = clientFuture.get(10, TimeUnit.SECONDS)) {
				assertTrue(server.getSession().isValid());
				assertTrue(client.getSession().isValid());
			}
		}
	}
	
	@Test
	void layerServerWithEmptyConsumedBytes() throws Exception {
		try (SocketPair pair = pair()) {
			Future<SSLSocket> clientFuture = layerClient(pair, loopback(pair), clientConfig);
			
			try (SSLSocket server = SslSockets.layerServer(pair.server(), new byte[0], serverConfig); SSLSocket _ = clientFuture.get(10, TimeUnit.SECONDS)) {
				assertTrue(server.getSession().isValid());
			}
		}
	}
	
	@Test
	void layeredSocketsExchangeData() throws Exception {
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, serverConfig);
			
			try (SSLSocket client = SslSockets.layerClient(pair.client(), loopback(pair), clientConfig); SSLSocket server = serverFuture.get(10, TimeUnit.SECONDS)) {
				client.getOutputStream().write("hello".getBytes(StandardCharsets.US_ASCII));
				client.getOutputStream().flush();
				
				assertEquals("hello", new String(server.getInputStream().readNBytes(5), StandardCharsets.US_ASCII));
			}
		}
	}
	
	@Test
	void closingLayeredSocketClosesPlainSocket() throws Exception {
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, serverConfig);
			SSLSocket client = SslSockets.layerClient(pair.client(), loopback(pair), clientConfig);
			SSLSocket server = serverFuture.get(10, TimeUnit.SECONDS);
			
			client.close();
			
			assertTrue(pair.client().isClosed());
			server.close();
		}
	}
	
	@Test
	void failedLayerClosesPlainSocket() throws Exception {
		SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(clientContext).enabledCipherSuites(List.of("NOT_A_SUITE")).build();
		try (SocketPair pair = pair()) {
			assertThrows(NetworkConnectionException.class, () -> SslSockets.layerClient(pair.client(), loopback(pair), config));
			
			assertTrue(pair.client().isClosed());
		}
	}
	
	@Test
	void mutualTlsWithRestrictedProtocolsAndSuites() throws Exception {
		SslServerConfig server = SslServerConfig.builder(serverContext).enabledProtocols(List.of(TlsProtocol.TLS_V1_2)).enabledCipherSuites(List.of(ECDHE_SUITE)).clientAuth(SslClientAuth.REQUIRED).build();
		SslUpgradeConfig client = SslUpgradeConfig.builder().sslContext(clientContext).enabledProtocols(List.of(TlsProtocol.TLS_V1_2)).enabledCipherSuites(List.of(ECDHE_SUITE)).build();
		try (SocketPair pair = pair()) {
			Future<SSLSocket> serverFuture = layerServer(pair, server);
			
			try (SSLSocket clientSocket = SslSockets.layerClient(pair.client(), loopback(pair), client); SSLSocket serverSocket = serverFuture.get(10, TimeUnit.SECONDS)) {
				assertEquals("TLSv1.2", clientSocket.getSession().getProtocol());
				assertEquals(clientSocket.getSession().getProtocol(), serverSocket.getSession().getProtocol());
				assertEquals(ECDHE_SUITE, clientSocket.getSession().getCipherSuite());
				assertEquals(ECDHE_SUITE, serverSocket.getSession().getCipherSuite());
				assertTrue(serverSocket.getSession().getPeerCertificates().length > 0);
			}
		}
	}
	
	private record SocketPair(Socket client, Socket server) implements AutoCloseable {
		
		@Override
		public void close() throws IOException {
			this.client.close();
			this.server.close();
		}
	}
}
