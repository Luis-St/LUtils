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
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.ssl.*;
import org.junit.jupiter.api.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TcpClient}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TcpClientTest {
	
	private static final IpEndpoint ENDPOINT = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
	private static final TcpClientConfig SMALL_BUFFER_CONFIG = TcpClientConfig.builder().bufferSize(16).build();
	private static final String KEYSTORE_PASSWORD = "changeit";
	
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = createContext();
		clientContext = createContext();
	}
	
	private static SSLContext createContext() throws Exception {
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		try (InputStream stream = TcpClientTest.class.getResourceAsStream("/ssl/keystore.p12")) {
			if (stream == null) {
				throw new IllegalStateException("Test keystore /ssl/keystore.p12 not found on the classpath");
			}
			keyStore.load(stream, KEYSTORE_PASSWORD.toCharArray());
		}
		
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(keyStore);
		
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
		return context;
	}
	
	private static SslUpgradeConfigBuilder upgradeConfig() {
		return SslUpgradeConfig.builder().sslContext(clientContext).verifyHostname(false);
	}
	
	private static void withServer(ServerBody body) throws Exception {
		withServer(TcpClientConfig.builder().build(), body);
	}
	
	private static void withServer(TcpClientConfig config, ServerBody body) throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort());
			try (TcpClient client = new TcpClient(config)) {
				client.connect(endpoint);
				try (Socket server = serverSocket.accept()) {
					body.accept(client, server);
				}
			}
		}
	}
	
	private static void withUpgradeServer(UpgradeBody body) throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				body.accept(client);
			}
		}
	}
	
	private static byte[] readUntil(TcpClient client, int expected, int maxBytes) throws Exception {
		ByteArrayOutputStream reassembled = new ByteArrayOutputStream();
		while (reassembled.size() < expected) {
			reassembled.writeBytes(client.receive(maxBytes));
		}
		return reassembled.toByteArray();
	}
	
	@Test
	void constructWithDefaultConfig() {
		try (TcpClient client = new TcpClient()) {
			assertFalse(client.isActive());
			assertFalse(client.isUpgraded());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void constructWithCustomConfig() {
		try (TcpClient client = new TcpClient(SMALL_BUFFER_CONFIG)) {
			assertFalse(client.isActive());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new TcpClient(null));
	}
	
	@Test
	void connectToWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> TcpClient.connectTo(null));
	}
	
	@Test
	void connectToWithNullEndpointAndConfig() {
		assertThrows(NullPointerException.class, () -> TcpClient.connectTo(null, TcpClientConfig.DEFAULT));
	}
	
	@Test
	void connectToWithNullConfig() {
		assertThrows(NullPointerException.class, () -> TcpClient.connectTo(ENDPOINT, null));
	}
	
	@Test
	void connectWithNullEndpoint() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(NullPointerException.class, () -> client.connect(null));
		}
	}
	
	@Test
	void sendWithNullData() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null));
		}
	}
	
	@Test
	void upgradeWithNullConfigThrows() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(NullPointerException.class, () -> client.upgrade(null));
		}
	}
	
	@Test
	void upgradeWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.upgrade(SslUpgradeConfig.DEFAULT));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void upgradeWithDefaultsWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::upgrade);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void upgradeAfterCloseWithoutConnectThrows() {
		TcpClient client = new TcpClient();
		assertDoesNotThrow(client::close);
		
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.upgrade(SslUpgradeConfig.DEFAULT));
		assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		assertFalse(client.isUpgraded());
	}
	
	@Test
	void sendWithoutConnectThrowsNotConnected() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[1]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataExceedingBufferSizeThrowsBeforeConnectionCheck() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8193]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertNotEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataEqualToBufferSizePassesSizeCheck() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8192]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendEmptyDataPassesSizeCheck() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[0]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void sendDataExceedingConfiguredBufferSize() {
		try (TcpClient client = new TcpClient(SMALL_BUFFER_CONFIG)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[17]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			
			NetworkConnectionException accepted = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[16]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, accepted.errorType());
		}
	}
	
	@Test
	void receiveWithZeroMaxBytesThrows() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
		}
	}
	
	@Test
	void receiveWithNegativeMaxBytesThrows() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(-1));
		}
	}
	
	@Test
	void receiveWithMinimumMaxBytesReachesConnectionCheck() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.receive(1));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void receiveWithoutConnectThrowsNotConnected() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void getInputStreamWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getInputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void getOutputStreamWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getOutputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void isActiveFalseWithoutConnect() {
		try (TcpClient client = new TcpClient()) {
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void isUpgradedFalseInitially() {
		try (TcpClient client = new TcpClient()) {
			assertFalse(client.isUpgraded());
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void localEndpointEmptyWithoutConnect() {
		try (TcpClient client = new TcpClient()) {
			assertTrue(client.localEndpoint().isEmpty());
		}
	}
	
	@Test
	void remoteEndpointEmptyWithoutConnect() {
		try (TcpClient client = new TcpClient()) {
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void closeWithoutConnectDoesNothing() {
		TcpClient client = new TcpClient();
		
		assertDoesNotThrow(client::close);
		assertFalse(client.isActive());
	}
	
	@Test
	void closeIsIdempotentWithoutConnect() {
		TcpClient client = new TcpClient();
		
		assertDoesNotThrow(client::close);
		assertDoesNotThrow(client::close);
		assertDoesNotThrow(client::close);
		assertFalse(client.isActive());
	}
	
	@Test
	void operationsAfterCloseStillReportNotConnected() {
		TcpClient client = new TcpClient();
		client.close();
		
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, () -> client.send(new byte[1])).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::receive).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::getInputStream).errorType());
		assertEquals(NetworkErrorType.NOT_CONNECTED, assertThrows(NetworkConnectionException.class, client::getOutputStream).errorType());
	}
	
	@Test
	void implementsNetworkClient() {
		try (TcpClient client = new TcpClient()) {
			assertInstanceOf(NetworkClient.class, client);
		}
	}
	
	@Test
	void guardPrecedenceConsistencyOnUnconnectedClient() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null));
			
			NetworkConnectionException tooLarge = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8193]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, tooLarge.errorType());
			
			NetworkConnectionException notConnected = assertThrows(NetworkConnectionException.class, () -> client.send(new byte[8192]));
			assertEquals(NetworkErrorType.NOT_CONNECTED, notConnected.errorType());
		}
	}
	
	@Test
	void receiveGuardPrecedenceConsistencyOnUnconnectedClient() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.receive(1));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void getInputStreamAfterConnectedCloseReportsNotConnected() throws Exception {
		withServer((client, server) -> {
			assertNotNull(client.getInputStream());
			client.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getInputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		});
	}
	
	@Test
	void upgradeWithPendingInput() throws Exception {
		withServer((client, server) -> {
			server.getOutputStream().write("220 OK\r\nEXTRA\r\n".getBytes(StandardCharsets.US_ASCII));
			server.getOutputStream().flush();
			
			assertEquals("220 OK\r\n", new String(client.getInputStream().readNBytes(8), StandardCharsets.US_ASCII));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.upgrade(upgradeConfig().build()));
			assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
			assertTrue(exception.getMessage().contains("Unread plaintext is pending"));
			assertTrue(client.isActive());
		});
	}
	
	@Test
	void getInputStreamReturnsSameInstance() throws Exception {
		withServer((client, server) -> {
			InputStream first = client.getInputStream();
			
			assertSame(first, client.getInputStream());
			assertInstanceOf(BufferedInputStream.class, first);
		});
	}
	
	@Test
	void getOutputStreamReturnsSameInstance() throws Exception {
		withServer((client, server) -> {
			OutputStream first = client.getOutputStream();
			
			assertSame(first, client.getOutputStream());
		});
	}
	
	@Test
	void receiveAfterStreamReadReturnsBufferedRemainder() throws Exception {
		withServer(TcpClientConfig.builder().framing(false).build(), (client, server) -> {
			server.getOutputStream().write("ABCD".getBytes(StandardCharsets.US_ASCII));
			server.getOutputStream().flush();
			
			assertEquals('A', client.getInputStream().read());
			
			assertArrayEquals("BCD".getBytes(StandardCharsets.US_ASCII), readUntil(client, 3, 8192));
		});
	}
	
	@Test
	void reconnectReplacesStreams() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort());
			try (TcpClient client = new TcpClient()) {
				client.connect(endpoint);
				InputStream first;
				try (Socket ignored = serverSocket.accept()) {
					first = client.getInputStream();
				}
				client.close();
				
				client.connect(endpoint);
				try (Socket second = serverSocket.accept()) {
					second.getOutputStream().write('B');
					second.getOutputStream().flush();
					
					InputStream reconnected = client.getInputStream();
					assertNotSame(first, reconnected);
					assertEquals('B', reconnected.read());
				}
			}
		}
	}
	
	@Test
	void upgradeWithoutPendingInput() throws Exception {
		withUpgradeServer(client -> {
			try (SslClient secure = assertDoesNotThrow(() -> client.upgrade(upgradeConfig().build()))) {
				assertNotNull(secure);
				assertTrue(client.isUpgraded());
				assertFalse(client.isActive());
			}
		});
	}
	
	@Test
	void upgradeClearsStreams() throws Exception {
		withUpgradeServer(client -> {
			try (SslClient secure = client.upgrade(upgradeConfig().build())) {
				assertNotNull(secure.getInputStream());
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getInputStream);
				assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
			}
		});
	}
	
	@Test
	void upgradeWithSocketBytesNotYetBuffered() throws Exception {
		withServer(TcpClientConfig.builder().readTimeout(Duration.ofSeconds(5)).build(), (client, server) -> {
			server.getOutputStream().write("EXTRA\r\n".getBytes(StandardCharsets.US_ASCII));
			server.getOutputStream().flush();
			Thread.sleep(100);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.upgrade(upgradeConfig().build()));
			assertFalse(exception.getMessage().contains("Unread plaintext is pending"));
		});
	}
	
	@Test
	void getInputStreamReadsServerData() throws Exception {
		withServer((client, server) -> {
			server.getOutputStream().write("Hello".getBytes(StandardCharsets.US_ASCII));
			server.getOutputStream().flush();
			
			assertEquals("Hello", new String(client.getInputStream().readNBytes(5), StandardCharsets.US_ASCII));
		});
	}
	
	@Test
	void getOutputStreamWritesToServer() throws Exception {
		withServer((client, server) -> {
			OutputStream out = client.getOutputStream();
			out.write("Hello".getBytes(StandardCharsets.US_ASCII));
			out.flush();
			
			assertEquals("Hello", new String(server.getInputStream().readNBytes(5), StandardCharsets.US_ASCII));
		});
	}
	
	@Test
	void userBuiltReaderKeepsBufferedLines() throws Exception {
		withServer((client, server) -> {
			server.getOutputStream().write("220 Ready\r\n250 Second\r\n".getBytes(StandardCharsets.US_ASCII));
			server.getOutputStream().flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.US_ASCII));
			
			assertEquals("220 Ready", reader.readLine());
			assertEquals("250 Second", reader.readLine());
		});
	}
	
	@Test
	void lineBasedConversationWithServer() throws Exception {
		withServer((client, server) -> {
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
		withServer(TcpClientConfig.builder().framing(false).build(), (client, server) -> {
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
		
		void accept(TcpClient client, Socket server) throws Exception;
	}
	
	@FunctionalInterface
	private interface UpgradeBody {
		
		void accept(TcpClient client) throws Exception;
	}
}
