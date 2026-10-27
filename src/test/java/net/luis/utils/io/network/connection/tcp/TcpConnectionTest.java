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

import net.luis.utils.io.network.*;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.Framing;
import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.ssl.*;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TcpConnection}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TcpConnectionTest {
	
	private static SslUpgradeConfig clientUpgradeConfig;
	private static SslServerConfig serverUpgradeConfig;
	private static ExecutorService executor;
	
	@BeforeAll
	static void setUp() throws Exception {
		clientUpgradeConfig = SslUpgradeConfig.builder().sslContext(SslTestContext.clientContext()).build();
		serverUpgradeConfig = SslServerConfig.builder(SslTestContext.serverContext()).build();
		executor = Executors.newCachedThreadPool();
	}
	
	@AfterAll
	static void cleanUp() {
		executor.shutdownNow();
	}
	
	private static ConnectionPair pair(Endpoint endpoint, TcpClientConfig config, ServerSocket serverSocket) throws IOException {
		TcpConnection client = TcpConnection.connectTo(endpoint, config);
		Socket accepted = serverSocket.accept();
		TcpConnection server = new TcpConnection(accepted, config.framing(), config.maxMessageSize(), config.readTimeout());
		return new ConnectionPair(client, server);
	}
	
	private static ConnectionPair pair(TcpClientConfig config) throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
			return pair(new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort()), config, serverSocket);
		}
	}
	
	private static ConnectionPair pair() throws IOException {
		return pair(TcpClientConfig.DEFAULT);
	}
	
	private static SaturatedListener saturatedListener() throws IOException {
		ServerSocket serverSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
		List<Socket> fillers = new ArrayList<>();
		for (int attempt = 0; attempt < 64; attempt++) {
			Socket filler = new Socket();
			try {
				filler.connect(serverSocket.getLocalSocketAddress(), 1000);
				fillers.add(filler);
			} catch (SocketTimeoutException e) {
				filler.close();
				return new SaturatedListener(serverSocket, fillers);
			} catch (ConnectException e) {
				filler.close();
				new SaturatedListener(serverSocket, fillers).close();
				Assumptions.abort("The operating system refuses connections to a full accept queue instead of dropping them");
			}
		}
		new SaturatedListener(serverSocket, fillers).close();
		throw new IllegalStateException("The accept queue of the listener never filled up");
	}
	
	private static void awaitAvailable(TcpConnection connection, int bytes) throws Exception {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
		while (connection.getInputStream().available() < bytes) {
			assertTrue(System.nanoTime() < deadline, "Expected " + bytes + " bytes to arrive");
			Thread.sleep(10);
		}
	}
	
	private static Future<SslConnection> upgradeServer(TcpConnection server) {
		return executor.submit(() -> server.upgrade(serverUpgradeConfig));
	}
	
	private static byte[] bytes(String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}
	
	private static String string(byte[] value) {
		return new String(value, StandardCharsets.UTF_8);
	}
	
	@Test
	void constructWithConnectedSocket() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress()); Socket socket = new Socket("127.0.0.1", serverSocket.getLocalPort())) {
			TcpConnection connection = new TcpConnection(socket, Framing.lengthPrefixed(), 1024, null);
			
			assertTrue(connection.isActive());
			assertFalse(connection.isUpgraded());
			assertEquals(serverSocket.getLocalPort(), connection.remoteEndpoint().orElseThrow().port());
			connection.close();
		}
	}
	
	@Test
	void constructWithNullSocket() {
		assertThrows(NullPointerException.class, () -> new TcpConnection(null, Framing.lengthPrefixed(), 1024, null));
	}
	
	@Test
	void constructWithNullFraming() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress()); Socket socket = new Socket("127.0.0.1", serverSocket.getLocalPort())) {
			assertThrows(NullPointerException.class, () -> new TcpConnection(socket, null, 1024, null));
		}
	}
	
	@Test
	void connectToWithDefaultConfig() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
			 TcpConnection connection = TcpConnection.connectTo(new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort()))) {
			assertTrue(connection.isActive());
			assertEquals(Framing.lengthPrefixed(), connection.framing());
			assertEquals(8192, connection.maxMessageSize());
			assertTrue(connection.readTimeout().isEmpty());
		}
	}
	
	@Test
	void connectToWithConfig() throws Exception {
		TcpClientConfig config = TcpClientConfig.builder().framing(Framing.lines()).maxMessageSize(100).readTimeout(Duration.ofSeconds(1)).build();
		
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
			 TcpConnection connection = TcpConnection.connectTo(new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort()), config)) {
			assertEquals(Framing.lines(), connection.framing());
			assertEquals(100, connection.maxMessageSize());
			assertEquals(Optional.of(Duration.ofSeconds(1)), connection.readTimeout());
		}
	}
	
	@Test
	void connectToWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> TcpConnection.connectTo(null));
		assertThrows(NullPointerException.class, () -> TcpConnection.connectTo(null, TcpClientConfig.DEFAULT));
	}
	
	@Test
	void connectToWithNullConfig() {
		assertThrows(NullPointerException.class, () -> TcpConnection.connectTo(new IpEndpoint(Ipv4Address.LOOPBACK, 80), null));
	}
	
	@Test
	void connectToRefused() throws Exception {
		try (Socket unlistened = new Socket()) {
			unlistened.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> TcpConnection.connectTo(new IpEndpoint(Ipv4Address.LOOPBACK, unlistened.getLocalPort())));
			assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
		}
	}
	
	@Test
	void connectToUnknownHost() {
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> TcpConnection.connectTo(new HostEndpoint("does-not-exist.invalid", 80)));
		
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
	}
	
	@Test
	void upgradeWithNullUpgradeConfig() throws Exception {
		try (ConnectionPair pair = pair()) {
			assertThrows(NullPointerException.class, () -> pair.client().upgrade((SslUpgradeConfig) null));
		}
	}
	
	@Test
	void upgradeWithNullServerConfig() throws Exception {
		try (ConnectionPair pair = pair()) {
			assertThrows(NullPointerException.class, () -> pair.server().upgrade((SslServerConfig) null));
		}
	}
	
	@Test
	@Tag("tls")
	void upgradeClosedConnection() throws Exception {
		try (ConnectionPair pair = pair()) {
			pair.client().close();
			pair.server().close();
			
			assertEquals(NetworkErrorType.SOCKET_CLOSED, assertThrows(NetworkConnectionException.class, () -> pair.client().upgrade(clientUpgradeConfig)).errorType());
			assertEquals(NetworkErrorType.SOCKET_CLOSED, assertThrows(NetworkConnectionException.class, () -> pair.server().upgrade(serverUpgradeConfig)).errorType());
			assertFalse(pair.client().isUpgraded());
			assertFalse(pair.server().isUpgraded());
		}
	}
	
	@Test
	@Tag("tls")
	void upgradeTwice() throws Exception {
		try (ConnectionPair pair = pair()) {
			Future<SslConnection> server = upgradeServer(pair.server());
			SslConnection secured = pair.client().upgrade(clientUpgradeConfig);
			server.get(10, TimeUnit.SECONDS).close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> pair.client().upgrade(clientUpgradeConfig));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
			secured.close();
		}
	}
	
	@Test
	@Tag("tls")
	void upgradeWithPendingInput() throws Exception {
		try (ConnectionPair pair = pair()) {
			ByteArrayOutputStream frames = new ByteArrayOutputStream();
			Framing.lengthPrefixed().write(frames, bytes("first"));
			Framing.lengthPrefixed().write(frames, bytes("second"));
			pair.server().getOutputStream().write(frames.toByteArray());
			assertEquals("first", string(pair.client().receive().orElseThrow()));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> pair.client().upgrade(clientUpgradeConfig));
			assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
			assertTrue(pair.client().isActive());
			assertFalse(pair.client().isUpgraded());
			assertEquals("second", string(pair.client().receive().orElseThrow()));
		}
	}
	
	@Test
	@Tag("tls")
	void upgradeClientHandshakeFails() throws Exception {
		try (ConnectionPair pair = pair()) {
			Future<SslConnection> server = upgradeServer(pair.server());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> pair.client().upgrade(SslUpgradeConfig.DEFAULT));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertFalse(pair.client().isUpgraded());
			assertThrows(ExecutionException.class, () -> server.get(10, TimeUnit.SECONDS));
		}
	}
	
	@Test
	@Tag("tls")
	void upgradeServerHandshakeFails() throws Exception {
		try (ConnectionPair pair = pair()) {
			pair.client().getOutputStream().write(bytes("GARBAGE PLAINTEXT THAT IS NOT A CLIENT HELLO\r\n"));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> pair.server().upgrade(serverUpgradeConfig));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertFalse(pair.server().isUpgraded());
		}
	}
	
	@Test
	@Tag("tls")
	void constructUsesRemoteAddressAsPeer() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress()); Socket socket = new Socket("127.0.0.1", serverSocket.getLocalPort());
			 TcpConnection client = new TcpConnection(socket, Framing.lengthPrefixed(), 1024, null);
			 TcpConnection server = new TcpConnection(serverSocket.accept(), Framing.lengthPrefixed(), 1024, null)) {
			Future<SslConnection> serverUpgrade = upgradeServer(server);
			SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(SslTestContext.clientContext()).verifyHostname(true).build();
			
			try (SslConnection secured = assertDoesNotThrow(() -> client.upgrade(config)); SslConnection _ = serverUpgrade.get(10, TimeUnit.SECONDS)) {
				assertTrue(secured.isActive());
				assertTrue(client.isUpgraded());
				assertEquals("127.0.0.1", secured.getSession().getPeerHost());
			}
		}
	}
	
	@Test
	@Tag("tls")
	void connectToUsesGivenEndpointAsPeer() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
			 ConnectionPair pair = pair(new HostEndpoint("localhost", serverSocket.getLocalPort()), TcpClientConfig.DEFAULT, serverSocket)) {
			Future<SslConnection> serverUpgrade = upgradeServer(pair.server());
			SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(SslTestContext.clientContext()).verifyHostname(true).build();
			
			try (SslConnection secured = pair.client().upgrade(config); SslConnection _ = serverUpgrade.get(10, TimeUnit.SECONDS)) {
				assertTrue(secured.isActive());
				assertEquals("localhost", secured.getSession().getPeerHost());
			}
		}
	}
	
	@Test
	@Tag("tls")
	void upgradeClientAndServer() throws Exception {
		try (ConnectionPair pair = pair()) {
			Future<SslConnection> serverUpgrade = upgradeServer(pair.server());
			
			try (SslConnection client = pair.client().upgrade(clientUpgradeConfig); SslConnection server = serverUpgrade.get(10, TimeUnit.SECONDS)) {
				assertNotNull(client);
				assertNotNull(server);
				assertTrue(pair.client().isUpgraded());
				assertTrue(pair.server().isUpgraded());
				assertFalse(pair.client().isActive());
				assertFalse(pair.server().isActive());
			}
		}
	}
	
	@Test
	@Tag("tls")
	void upgradeServerPassesBufferedInput() throws Exception {
		try (ConnectionPair pair = pair()) {
			pair.client().send(bytes("STARTTLS"));
			Future<SslConnection> clientUpgrade = executor.submit(() -> pair.client().upgrade(clientUpgradeConfig));
			
			awaitAvailable(pair.server(), Integer.BYTES + "STARTTLS".length() + 1);
			assertEquals("STARTTLS", string(pair.server().receive().orElseThrow()));
			
			try (SslConnection server = pair.server().upgrade(serverUpgradeConfig); SslConnection client = clientUpgrade.get(10, TimeUnit.SECONDS)) {
				client.send(bytes("secured"));
				
				assertEquals("secured", string(server.receive().orElseThrow()));
			}
		}
	}
	
	@Test
	@Tag("tls")
	void upgradeKeepsTransportSettings() throws Exception {
		TcpClientConfig config = TcpClientConfig.builder().framing(Framing.delimited((byte) 0)).maxMessageSize(100).readTimeout(Duration.ofSeconds(5)).build();
		try (ConnectionPair pair = pair(config)) {
			Future<SslConnection> serverUpgrade = upgradeServer(pair.server());
			
			try (SslConnection client = pair.client().upgrade(clientUpgradeConfig); SslConnection _ = serverUpgrade.get(10, TimeUnit.SECONDS)) {
				assertEquals(Framing.delimited((byte) 0), client.framing());
				assertEquals(100, client.maxMessageSize());
				assertEquals(Optional.of(Duration.ofSeconds(5)), client.readTimeout());
			}
		}
	}
	
	@Test
	@Tag("tls")
	void closeAfterUpgradeKeepsSocketOpen() throws Exception {
		try (ConnectionPair pair = pair()) {
			Future<SslConnection> serverUpgrade = upgradeServer(pair.server());
			
			try (SslConnection client = pair.client().upgrade(clientUpgradeConfig); SslConnection server = serverUpgrade.get(10, TimeUnit.SECONDS)) {
				pair.client().close();
				pair.server().close();
				
				client.send(bytes("still open"));
				assertEquals("still open", string(server.receive().orElseThrow()));
				assertTrue(client.isActive());
			}
		}
	}
	
	@Test
	@Tag("tls")
	void upgradeWithDefaultConfig() throws Exception {
		try (ConnectionPair pair = pair()) {
			Future<SslConnection> server = upgradeServer(pair.server());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> pair.client().upgrade());
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertThrows(ExecutionException.class, () -> server.get(10, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void isUpgradedFalseInitially() throws Exception {
		try (ConnectionPair pair = pair()) {
			assertFalse(pair.client().isUpgraded());
			assertFalse(pair.server().isUpgraded());
		}
	}
	
	@Test
	void connectToHostEndpoint() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress());
			 TcpConnection connection = TcpConnection.connectTo(new HostEndpoint("localhost", serverSocket.getLocalPort()))) {
			assertTrue(connection.isActive());
			assertEquals(serverSocket.getLocalPort(), connection.remoteEndpoint().orElseThrow().port());
		}
	}
	
	@Test
	@Tag("tls")
	void sendAfterUpgrade() throws Exception {
		try (ConnectionPair pair = pair()) {
			Future<SslConnection> serverUpgrade = upgradeServer(pair.server());
			
			try (SslConnection client = pair.client().upgrade(clientUpgradeConfig); SslConnection server = serverUpgrade.get(10, TimeUnit.SECONDS)) {
				client.send(bytes("to server"));
				assertEquals("to server", string(server.receive().orElseThrow()));
				
				server.send(bytes("to client"));
				assertEquals("to client", string(client.receive().orElseThrow()));
			}
		}
	}
	
	@Test
	@Tag("tls")
	void startTlsExchange() throws Exception {
		try (ConnectionPair pair = pair(TcpClientConfig.builder().framing(Framing.lines()).build())) {
			pair.client().send(bytes("STARTTLS"));
			assertEquals("STARTTLS", string(pair.server().receive().orElseThrow()));
			pair.server().send(bytes("220 Go ahead"));
			assertEquals("220 Go ahead", string(pair.client().receive().orElseThrow()));
			Future<SslConnection> serverUpgrade = upgradeServer(pair.server());
			
			try (SslConnection client = pair.client().upgrade(clientUpgradeConfig); SslConnection server = serverUpgrade.get(10, TimeUnit.SECONDS)) {
				for (String line : List.of("EHLO client", "MAIL FROM:<a@b.c>", "QUIT")) {
					client.send(bytes(line));
					assertEquals(line, string(server.receive().orElseThrow()));
				}
				assertTrue(List.of("TLSv1.3", "TLSv1.2").contains(client.getSession().getProtocol()));
			}
		}
	}
	
	@Test
	@Tag("tls")
	void upgradeWithRestrictedProtocol() throws Exception {
		SslUpgradeConfig config = SslUpgradeConfig.builder().sslContext(SslTestContext.clientContext()).enabledProtocols(List.of(TlsProtocol.TLS_V1_2)).build();
		try (ConnectionPair pair = pair()) {
			Future<SslConnection> serverUpgrade = upgradeServer(pair.server());
			
			try (SslConnection client = pair.client().upgrade(config); SslConnection _ = serverUpgrade.get(10, TimeUnit.SECONDS)) {
				assertEquals("TLSv1.2", client.getSession().getProtocol());
			}
		}
	}
	
	@Test
	void connectToTimesOut() throws Exception {
		TcpClientConfig config = TcpClientConfig.builder().connectTimeout(Duration.ofMillis(300)).build();
		
		try (SaturatedListener listener = saturatedListener()) {
			NetworkTimeoutException exception = assertThrows(NetworkTimeoutException.class, () -> TcpConnection.connectTo(new IpEndpoint(Ipv4Address.LOOPBACK, listener.serverSocket().getLocalPort()), config));
			assertEquals(NetworkErrorType.CONNECTION_TIMEOUT, exception.errorType());
		}
	}
	
	private record ConnectionPair(TcpConnection client, TcpConnection server) implements AutoCloseable {
		
		@Override
		public void close() {
			this.client.close();
			this.server.close();
		}
	}
	
	private record SaturatedListener(ServerSocket serverSocket, List<Socket> fillers) implements AutoCloseable {
		
		@Override
		public void close() throws IOException {
			for (Socket filler : this.fillers) {
				filler.close();
			}
			this.serverSocket.close();
		}
	}
}
