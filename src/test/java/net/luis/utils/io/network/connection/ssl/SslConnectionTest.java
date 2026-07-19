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
import net.luis.utils.io.network.connection.Connection;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.*;

import javax.net.ssl.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslConnection}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SslConnectionTest {
	
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SslTestContext.serverContext();
		clientContext = SslTestContext.clientContext();
	}
	
	@Test
	void sendWithNullDataThrows() throws Exception {
		this.withPair(8192, (client, connection) -> assertThrows(NullPointerException.class, () -> connection.send(null)));
	}
	
	@Test
	void sendWithValidData() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] data = "Hello".getBytes();
			assertDoesNotThrow(() -> connection.send(data));
			
			byte[] received = new byte[data.length];
			int bytesRead = client.getInputStream().read(received);
			assertEquals(data.length, bytesRead);
			assertArrayEquals(data, received);
		});
	}
	
	@Test
	void sendDataExceedingBufferSizeThrows() throws Exception {
		this.withPair(100, (client, connection) -> {
			byte[] largeData = new byte[101];
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> connection.send(largeData));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		});
	}
	
	@Test
	void sendWhenConnectionClosedThrows() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.close();
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> connection.send("test".getBytes()));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		});
	}
	
	@Test
	void receiveReturnsData() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] dataToSend = "Hello World".getBytes();
			client.getOutputStream().write(dataToSend);
			client.getOutputStream().flush();
			
			byte[] received = connection.receive();
			assertArrayEquals(dataToSend, received);
		});
	}
	
	@Test
	void receiveWithCustomMaxBytes() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] dataToSend = "Hello World".getBytes();
			client.getOutputStream().write(dataToSend);
			client.getOutputStream().flush();
			
			byte[] received = connection.receive(5);
			assertEquals(5, received.length);
			assertArrayEquals("Hello".getBytes(), received);
		});
	}
	
	@Test
	void receiveWithZeroMaxBytesThrows() throws Exception {
		this.withPair(8192, (client, connection) -> assertThrows(IllegalArgumentException.class, () -> connection.receive(0)));
	}
	
	@Test
	void receiveWithNegativeMaxBytesThrows() throws Exception {
		this.withPair(8192, (client, connection) -> assertThrows(IllegalArgumentException.class, () -> connection.receive(-1)));
	}
	
	@Test
	void receiveWhenConnectionClosedThrows() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.close();
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::receive);
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		});
	}
	
	@Test
	void receiveReturnsEmptyArrayOnPeerClose() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.close();
			byte[] received = connection.receive();
			assertEquals(0, received.length);
		});
	}
	
	@Test
	void getInputStreamReturnsStream() throws Exception {
		this.withPair(8192, (client, connection) -> {
			InputStream inputStream = connection.getInputStream();
			assertNotNull(inputStream);
		});
	}
	
	@Test
	void getInputStreamWhenClosedThrows() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.close();
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::getInputStream);
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		});
	}
	
	@Test
	void getOutputStreamReturnsStream() throws Exception {
		this.withPair(8192, (client, connection) -> {
			OutputStream outputStream = connection.getOutputStream();
			assertNotNull(outputStream);
		});
	}
	
	@Test
	void getOutputStreamWhenClosedThrows() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.close();
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::getOutputStream);
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		});
	}
	
	@Test
	void getSessionReturnsNegotiatedSession() throws Exception {
		this.withPair(8192, (client, connection) -> {
			SSLSession session = connection.getSession();
			assertNotNull(session);
			assertTrue(session.getProtocol().startsWith("TLS"));
			assertNotNull(session.getCipherSuite());
		});
	}
	
	@Test
	void isActiveReturnsTrueForActiveConnection() throws Exception {
		this.withPair(8192, (client, connection) -> assertTrue(connection.isActive()));
	}
	
	@Test
	void isActiveReturnsFalseAfterClose() throws Exception {
		this.withPair(8192, (client, connection) -> {
			assertTrue(connection.isActive());
			connection.close();
			assertFalse(connection.isActive());
		});
	}
	
	@Test
	void localEndpointReturnsCorrectEndpoint() throws Exception {
		this.withPair(8192, (client, connection) -> {
			IpEndpoint localEndpoint = connection.localEndpoint();
			assertNotNull(localEndpoint);
			assertEquals(client.getPort(), localEndpoint.port());
		});
	}
	
	@Test
	void remoteEndpointReturnsCorrectEndpoint() throws Exception {
		this.withPair(8192, (client, connection) -> {
			IpEndpoint remoteEndpoint = connection.remoteEndpoint();
			assertNotNull(remoteEndpoint);
			assertEquals(client.getLocalPort(), remoteEndpoint.port());
		});
	}
	
	@Test
	void closeIsIdempotent() throws Exception {
		this.withPair(8192, (client, connection) -> {
			assertDoesNotThrow(() -> {
				connection.close();
				connection.close();
				connection.close();
			});
			assertFalse(connection.isActive());
		});
	}
	
	@Test
	void implementsConnectionInterface() throws Exception {
		this.withPair(8192, (client, connection) -> assertInstanceOf(Connection.class, connection));
	}
	
	//region Helper methods
	
	/**
	 * Opens a fully handshaked {@link SSLSocket} pair, wraps the server side in an {@link SslConnection},
	 * runs the given test body, and closes everything afterwards.<br>
	 */
	private void withPair(int bufferSize, PairConsumer body) throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try (SSLServerSocket serverSocket = (SSLServerSocket) serverContext.getServerSocketFactory().createServerSocket()) {
			serverSocket.bind(new InetSocketAddress("127.0.0.1", 0));
			int port = serverSocket.getLocalPort();
			
			Future<SSLSocket> serverFuture = executor.submit(() -> {
				SSLSocket accepted = (SSLSocket) serverSocket.accept();
				accepted.startHandshake();
				return accepted;
			});
			
			try (SSLSocket client = (SSLSocket) clientContext.getSocketFactory().createSocket("127.0.0.1", port)) {
				client.startHandshake();
				SSLSocket serverSide = serverFuture.get(15, TimeUnit.SECONDS);
				SslConnection connection = new SslConnection(serverSide, bufferSize, Duration.ofSeconds(5));
				try {
					body.accept(client, connection);
				} finally {
					connection.close();
				}
			}
		} finally {
			executor.shutdownNow();
		}
	}
	
	/**
	 * Callback receiving a handshaked client socket and the server-side connection wrapping its peer.<br>
	 */
	@FunctionalInterface
	private interface PairConsumer {
		
		void accept(SSLSocket client, SslConnection connection) throws Exception;
	}
	//endregion
}
