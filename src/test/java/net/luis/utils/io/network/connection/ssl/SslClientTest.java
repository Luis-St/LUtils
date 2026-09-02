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
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslClient}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class SslClientTest {
	
	private static final IpEndpoint ENDPOINT = new IpEndpoint(Ipv4Address.LOOPBACK, 8443);
	private static final SslClientConfig SMALL_BUFFER_CONFIG = SslClientConfig.builder().bufferSize(16).build();
	
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SslTestContext.serverContext();
		clientContext = SslTestContext.clientContext();
	}
	
	private static SslClientConfigBuilder clientConfig() {
		return SslClientConfig.builder().sslContext(clientContext).verifyHostname(false);
	}
	
	private static SSLServerSocket secureServerSocket() throws Exception {
		SSLServerSocket serverSocket = (SSLServerSocket) serverContext.getServerSocketFactory().createServerSocket();
		serverSocket.bind(new java.net.InetSocketAddress("127.0.0.1", 0));
		return serverSocket;
	}
	
	private static SSLSocket handshake(SSLServerSocket serverSocket) throws Exception {
		SSLSocket accepted = (SSLSocket) serverSocket.accept();
		accepted.startHandshake();
		return accepted;
	}
	
	private static void withSecureServer(ServerBody body) throws Exception {
		withSecureServer(clientConfig().build(), body);
	}
	
	private static void withSecureServer(SslClientConfig config, ServerBody body) throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try (SSLServerSocket serverSocket = secureServerSocket()) {
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort());
			Future<SSLSocket> accepted = executor.submit(() -> handshake(serverSocket));
			
			try (SslClient client = new SslClient(config)) {
				client.connect(endpoint);
				try (SSLSocket server = accepted.get(15, TimeUnit.SECONDS)) {
					body.accept(client, server);
				}
			}
		} finally {
			executor.shutdownNow();
		}
	}
	
	private static byte[] readUntil(SslClient client, int expected, int maxBytes) throws Exception {
		ByteArrayOutputStream reassembled = new ByteArrayOutputStream();
		while (reassembled.size() < expected) {
			reassembled.writeBytes(client.receive(maxBytes));
		}
		return reassembled.toByteArray();
	}
	
	@Test
	void constructWithDefaultConfig() {
		try (SslClient client = new SslClient()) {
			assertFalse(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void constructWithCustomConfig() {
		try (SslClient client = new SslClient(SMALL_BUFFER_CONFIG)) {
			assertFalse(client.isActive());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SslClient(null));
	}
	
	@Test
	void connectToWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> SslClient.connectTo(null));
	}
	
	@Test
	void connectToWithNullEndpointAndConfig() {
		assertThrows(NullPointerException.class, () -> SslClient.connectTo(null, SslClientConfig.DEFAULT));
	}
	
	@Test
	void connectToWithNullConfig() {
		assertThrows(NullPointerException.class, () -> SslClient.connectTo(ENDPOINT, null));
	}
	
	@Test
	void connectWithNullEndpoint() {
		try (SslClient client = new SslClient()) {
			assertThrows(NullPointerException.class, () -> client.connect(null));
		}
	}
	
	@Test
	void sendWithNullData() {
		try (SslClient client = new SslClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null));
		}
	}
	
	@Test
	void upgradeWithNullSocketThrows() {
		assertThrows(NullPointerException.class, () -> SslClient.upgrade(null, ENDPOINT, SslClientConfig.DEFAULT));
	}
	
	@Test
	void upgradeWithNullEndpointThrows() throws Exception {
		try (Socket socket = new Socket()) {
			assertThrows(NullPointerException.class, () -> SslClient.upgrade(socket, null, SslClientConfig.DEFAULT));
		}
	}
	
	@Test
	void upgradeWithNullConfigThrows() throws Exception {
		try (Socket socket = new Socket()) {
			assertThrows(NullPointerException.class, () -> SslClient.upgrade(socket, ENDPOINT, null));
		}
	}
	
	@Test
	void upgradeUnconnectedSocketThrows() throws Exception {
		try (Socket socket = new Socket()) {
			assertFalse(socket.isConnected());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, ENDPOINT, SslClientConfig.DEFAULT));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
			assertEquals(ENDPOINT, exception.endpoint());
		}
	}
	
	@Test
	void upgradeClosedSocketThrows() throws Exception {
		Socket socket = new Socket();
		socket.close();
		assertTrue(socket.isClosed());
		
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, ENDPOINT, SslClientConfig.DEFAULT));
		assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
	}
	
	@Test
	void getSessionWithoutConnectThrows() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getSession);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendWithoutConnectThrowsNotConnected() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[1]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataExceedingBufferSizeThrowsBeforeConnectionCheck() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8193]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertNotEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataEqualToBufferSizePassesSizeCheck() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8192]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendEmptyDataPassesSizeCheck() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[0]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataExceedingConfiguredBufferSize() {
		try (SslClient client = new SslClient(SMALL_BUFFER_CONFIG)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			
			NetworkConnectionException accepted = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[16]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, accepted.errorType());
		}
	}
	
	@Test
	void receiveWithZeroMaxBytesThrows() {
		try (SslClient client = new SslClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
		}
	}
	
	@Test
	void receiveWithNegativeMaxBytesThrows() {
		try (SslClient client = new SslClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(-1));
		}
	}
	
	@Test
	void receiveWithMinimumMaxBytesReachesConnectionCheck() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.receive(1));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void receiveWithoutConnectThrowsNotConnected() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void getInputStreamWithoutConnectThrows() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getInputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void getOutputStreamWithoutConnectThrows() {
		try (SslClient client = new SslClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getOutputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void isActiveFalseWithoutConnect() {
		try (SslClient client = new SslClient()) {
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void localEndpointEmptyWithoutConnect() {
		try (SslClient client = new SslClient()) {
			assertTrue(client.localEndpoint().isEmpty());
		}
	}
	
	@Test
	void remoteEndpointEmptyWithoutConnect() {
		try (SslClient client = new SslClient()) {
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void closeWithoutConnectDoesNothing() {
		SslClient client = new SslClient();
		
		assertDoesNotThrow(client::close);
		assertFalse(client.isActive());
	}
	
	@Test
	void closeIsIdempotentWithoutConnect() {
		SslClient client = new SslClient();
		
		assertDoesNotThrow(client::close);
		assertDoesNotThrow(client::close);
		assertDoesNotThrow(client::close);
		assertFalse(client.isActive());
	}
	
	@Test
	void operationsAfterCloseStillReportNotConnected() {
		SslClient client = new SslClient();
		client.close();
		
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, () -> client.send(new byte[1])).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::receive).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::getSession).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::getInputStream).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::getOutputStream).errorType());
	}
	
	@Test
	void implementsNetworkClient() {
		try (SslClient client = new SslClient()) {
			assertInstanceOf(NetworkClient.class, client);
		}
	}
	
	@Test
	void upgradeWithUnboundSocketThrowsWithEndpointAttached() throws Exception {
		try (Socket socket = new Socket()) {
			assertFalse(socket.isBound());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, ENDPOINT, SslClientConfig.DEFAULT));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
			assertEquals(ENDPOINT, exception.endpoint());
		}
	}
	
	@Test
	void guardPrecedenceConsistencyOnUnconnectedClient() {
		try (SslClient client = new SslClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null));
			
			NetworkConnectionException tooLarge = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8193]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, tooLarge.errorType());
			
			NetworkConnectionException notConnected = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8192]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, notConnected.errorType());
		}
	}
	
	@Test
	void upgradeGuardPrecedenceOnClosedSocket() throws Exception {
		Socket socket = new Socket();
		socket.close();
		
		assertThrows(NullPointerException.class, () -> SslClient.upgrade(socket, null, SslClientConfig.DEFAULT));
		
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, ENDPOINT, SslClientConfig.DEFAULT));
		assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
	}
	
	@Test
	void getInputStreamAfterConnectedCloseReportsNotConnected() throws Exception {
		withSecureServer((client, server) -> {
			assertNotNull(client.getInputStream());
			client.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getInputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		});
	}
	
	@Test
	void getInputStreamReturnsSameInstance() throws Exception {
		withSecureServer((client, server) -> {
			InputStream first = client.getInputStream();
			
			assertSame(first, client.getInputStream());
			assertInstanceOf(BufferedInputStream.class, first);
		});
	}
	
	@Test
	void getOutputStreamReturnsSameInstance() throws Exception {
		withSecureServer((client, server) -> {
			OutputStream first = client.getOutputStream();
			
			assertSame(first, client.getOutputStream());
		});
	}
	
	@Test
	void receiveAfterStreamReadReturnsBufferedRemainder() throws Exception {
		withSecureServer(clientConfig().framing(false).build(), (client, server) -> {
			server.getOutputStream().write("ABCD".getBytes(StandardCharsets.US_ASCII));
			server.getOutputStream().flush();
			
			assertEquals('A', client.getInputStream().read());
			
			assertArrayEquals("BCD".getBytes(StandardCharsets.US_ASCII), readUntil(client, 3, 8192));
		});
	}
	
	@Test
	void reconnectReplacesStreams() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try (SSLServerSocket serverSocket = secureServerSocket()) {
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort());
			try (SslClient client = new SslClient(clientConfig().build())) {
				Future<SSLSocket> first = executor.submit(() -> handshake(serverSocket));
				client.connect(endpoint);
				InputStream firstStream;
				try (SSLSocket ignored = first.get(15, TimeUnit.SECONDS)) {
					firstStream = client.getInputStream();
				}
				client.close();
				
				Future<SSLSocket> second = executor.submit(() -> handshake(serverSocket));
				client.connect(endpoint);
				try (SSLSocket accepted = second.get(15, TimeUnit.SECONDS)) {
					accepted.getOutputStream().write('B');
					accepted.getOutputStream().flush();
					
					InputStream reconnected = client.getInputStream();
					assertNotSame(firstStream, reconnected);
					assertEquals('B', reconnected.read());
				}
			}
		} finally {
			executor.shutdownNow();
		}
	}
	
	@Test
	void upgradeInstallsStreamsOverSecureSocket() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try (SSLServerSocket serverSocket = secureServerSocket()) {
			Future<SSLSocket> accepted = executor.submit(() -> handshake(serverSocket));
			
			try (Socket plain = new Socket("127.0.0.1", serverSocket.getLocalPort())) {
				IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort());
				try (SslClient client = SslClient.upgrade(plain, endpoint, clientConfig().build())) {
					try (SSLSocket server = accepted.get(15, TimeUnit.SECONDS)) {
						server.getOutputStream().write("Hello".getBytes(StandardCharsets.US_ASCII));
						server.getOutputStream().flush();
						
						InputStream in = client.getInputStream();
						assertEquals("Hello", new String(in.readNBytes(5), StandardCharsets.US_ASCII));
						assertSame(in, client.getInputStream());
					}
				}
			}
		} finally {
			executor.shutdownNow();
		}
	}
	
	@Test
	void upgradeAcceptsSocketWithPendingInput() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort());
			Future<Void> peer = executor.submit(() -> {
				try (Socket accepted = serverSocket.accept()) {
					accepted.getOutputStream().write("EXTRA\r\n".getBytes(StandardCharsets.US_ASCII));
					accepted.getOutputStream().flush();
					Thread.sleep(500);
				}
				return null;
			});
			
			try (Socket plain = new Socket("127.0.0.1", serverSocket.getLocalPort())) {
				NetworkConnectionException exception = assertThrows(
					NetworkConnectionException.class,
					() -> SslClient.upgrade(plain, endpoint, clientConfig().build())
				);
				assertFalse(exception.getMessage().contains("Unread plaintext is pending"));
			}
			peer.get(15, TimeUnit.SECONDS);
		} finally {
			executor.shutdownNow();
		}
	}
	
	@Test
	void getInputStreamReadsServerData() throws Exception {
		withSecureServer((client, server) -> {
			server.getOutputStream().write("Hello".getBytes(StandardCharsets.US_ASCII));
			server.getOutputStream().flush();
			
			assertEquals("Hello", new String(client.getInputStream().readNBytes(5), StandardCharsets.US_ASCII));
		});
	}
	
	@Test
	void getOutputStreamWritesToServer() throws Exception {
		withSecureServer((client, server) -> {
			OutputStream out = client.getOutputStream();
			out.write("Hello".getBytes(StandardCharsets.US_ASCII));
			out.flush();
			
			assertEquals("Hello", new String(server.getInputStream().readNBytes(5), StandardCharsets.US_ASCII));
		});
	}
	
	@Test
	void userBuiltReaderKeepsBufferedLines() throws Exception {
		withSecureServer((client, server) -> {
			server.getOutputStream().write("220 Ready\r\n250 Second\r\n".getBytes(StandardCharsets.US_ASCII));
			server.getOutputStream().flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
			
			assertEquals("220 Ready", reader.readLine());
			assertEquals("250 Second", reader.readLine());
		});
	}
	
	@Test
	void lineBasedConversationWithServer() throws Exception {
		withSecureServer((client, server) -> {
			BufferedReader clientIn = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
			OutputStream clientOut = client.getOutputStream();
			BufferedReader serverIn = new BufferedReader(new InputStreamReader(server.getInputStream(), StandardCharsets.US_ASCII));
			OutputStream serverOut = server.getOutputStream();
			
			serverOut.write("220 Ready\r\n".getBytes(StandardCharsets.US_ASCII));
			serverOut.flush();
			assertEquals("220 Ready", clientIn.readLine());
			
			clientOut.write("QUIT\r\n".getBytes(StandardCharsets.US_ASCII));
			clientOut.flush();
			assertEquals("QUIT", serverIn.readLine());
			
			serverOut.write("221 Bye\r\n".getBytes(StandardCharsets.US_ASCII));
			serverOut.flush();
			assertEquals("221 Bye", clientIn.readLine());
		});
	}
	
	@Test
	void streamsSurviveAcrossSendAndReceive() throws Exception {
		withSecureServer(clientConfig().framing(false).build(), (client, server) -> {
			InputStream in = client.getInputStream();
			server.getOutputStream().write("ABCD".getBytes(StandardCharsets.US_ASCII));
			server.getOutputStream().flush();
			
			assertEquals('A', in.read());
			assertArrayEquals("BCD".getBytes(StandardCharsets.US_ASCII), readUntil(client, 3, 8192));
			
			client.send("hi".getBytes(StandardCharsets.US_ASCII));
			assertEquals("hi", new String(server.getInputStream().readNBytes(2), StandardCharsets.US_ASCII));
			
			assertSame(in, client.getInputStream());
		});
	}
	
	@FunctionalInterface
	private interface ServerBody {
		
		void accept(SslClient client, SSLSocket server) throws Exception;
	}
}
