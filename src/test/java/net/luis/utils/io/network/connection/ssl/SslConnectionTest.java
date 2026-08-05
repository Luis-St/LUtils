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
import java.io.*;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Arrays;
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
	
	@Test
	void constructWithSocketBufferSizeAndTimeout() throws Exception {
		this.withPair(8192, (client, connection) -> {
			assertTrue(connection.isActive());
			assertEquals(client.getLocalPort(), connection.remoteEndpoint().port());
			assertEquals(client.getPort(), connection.localEndpoint().port());
		});
	}
	
	@Test
	void constructWithNullSocket() {
		assertThrows(NullPointerException.class, () -> new SslConnection(null, 8192, Duration.ofSeconds(5)));
	}
	
	@Test
	void constructWithNullReadTimeout() throws Exception {
		this.withPair(8192, (client, connection) -> assertThrows(NullPointerException.class, () -> new SslConnection(client, 8192, null)));
	}
	
	@Test
	void receiveReusesBufferAcrossCalls() throws Exception {
		this.withPair(8192, (client, connection) -> {
			writeAndFlush(client, "first".getBytes());
			assertArrayEquals("first".getBytes(), receiveExactly(connection, 5, 1024));
			
			writeAndFlush(client, "second".getBytes());
			assertArrayEquals("second".getBytes(), receiveExactly(connection, 6, 1024));
		});
	}
	
	@Test
	void receiveGrowsBufferForLargerMaxBytes() throws Exception {
		this.withPair(8192, (client, connection) -> {
			writeAndFlush(client, new byte[8]);
			assertEquals(8, receiveExactly(connection, 8, 16).length);
			
			byte[] large = filled(500, (byte) 0x42);
			writeAndFlush(client, large);
			assertArrayEquals(large, receiveExactly(connection, 500, 4096));
		});
	}
	
	@Test
	void receiveWithSmallerMaxBytesAfterLargerReusesBuffer() throws Exception {
		this.withPair(8192, (client, connection) -> {
			writeAndFlush(client, new byte[10]);
			assertEquals(10, receiveExactly(connection, 10, 4096).length);
			
			writeAndFlush(client, filled(50, (byte) 0x43));
			byte[] limited = connection.receive(8);
			assertTrue(limited.length <= 8);
			assertTrue(limited.length > 0);
			
			byte[] rest = receiveExactly(connection, 50 - limited.length, 4096);
			assertEquals(50, limited.length + rest.length);
		});
	}
	
	@Test
	void receiveDoesNotLeakPreviousPayload() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] first = filled(200, (byte) 0x41);
			writeAndFlush(client, first);
			assertArrayEquals(first, receiveExactly(connection, 200, 1024));
			
			writeAndFlush(client, "abc".getBytes());
			byte[] second = receiveExactly(connection, 3, 1024);
			assertEquals(3, second.length);
			assertArrayEquals("abc".getBytes(), second);
		});
	}
	
	@Test
	void receiveAfterPeerCloseReturnsEmptyArray() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.close();
			assertEquals(0, connection.receive(1024).length);
		});
	}
	
	@Test
	void receiveMultipleTimesWithVaryingSizes() throws Exception {
		this.withPair(8192, (client, connection) -> {
			int[] sizes = { 10, 500, 20, 2000, 5 };
			for (int size : sizes) {
				byte[] payload = filled(size, (byte) (size % 128));
				writeAndFlush(client, payload);
				assertArrayEquals(payload, receiveExactly(connection, size, 4096));
			}
		});
	}
	
	@Test
	void receiveReturnedArraysAreIndependent() throws Exception {
		this.withPair(8192, (client, connection) -> {
			writeAndFlush(client, "first".getBytes());
			byte[] first = receiveExactly(connection, 5, 1024);
			
			writeAndFlush(client, "second".getBytes());
			byte[] second = receiveExactly(connection, 6, 1024);
			
			assertArrayEquals("first".getBytes(), first);
			assertNotSame(first, second);
		});
	}
	
	@Test
	void sendAndReceiveRoundTripAfterBufferGrowth() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] large = filled(2000, (byte) 0x44);
			writeAndFlush(client, large);
			assertArrayEquals(large, receiveExactly(connection, 2000, 4096));
			
			connection.send(new byte[] { 0x7F });
			byte[] echoed = new byte[1];
			assertEquals(1, client.getInputStream().read(echoed));
			assertEquals(0x7F, echoed[0]);
			
			writeAndFlush(client, new byte[] { 0x01 });
			assertArrayEquals(new byte[] { 0x01 }, receiveExactly(connection, 1, 4096));
		});
	}
	
	@Test
	void receiveLargePayloadSpanningMultipleTlsRecords() throws Exception {
		this.withPair(65536, (client, connection) -> {
			byte[] payload = filled(65536, (byte) 0x45);
			Thread writer = new Thread(() -> {
				try {
					writeAndFlush(client, payload);
				} catch (Exception _) {}
			});
			writer.start();
			
			byte[] received = receiveExactly(connection, payload.length, 65536);
			writer.join(TimeUnit.SECONDS.toMillis(15));
			assertArrayEquals(payload, received);
		});
	}
	
	
	//region Helper methods
	
	private static byte[] filled(int length, byte value) {
		byte[] data = new byte[length];
		Arrays.fill(data, value);
		return data;
	}
	
	private static void writeAndFlush(SSLSocket socket, byte[] data) throws Exception {
		socket.getOutputStream().write(data);
		socket.getOutputStream().flush();
	}
	
	private static byte[] receiveExactly(SslConnection connection, int expected, int maxBytes) throws Exception {
		ByteArrayOutputStream accumulated = new ByteArrayOutputStream();
		while (accumulated.size() < expected) {
			byte[] chunk = connection.receive(maxBytes);
			if (chunk.length == 0) {
				break;
			}
			accumulated.write(chunk);
		}
		return accumulated.toByteArray();
	}
	
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
