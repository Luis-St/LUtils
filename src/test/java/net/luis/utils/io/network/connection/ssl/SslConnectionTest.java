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
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.*;

import javax.net.ssl.*;
import java.io.*;
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
			
			InputStream clientIn = client.getInputStream();
			byte[] header = clientIn.readNBytes(4);
			int declaredLength = ((header[0] & 0xFF) << 24) | ((header[1] & 0xFF) << 16) | ((header[2] & 0xFF) << 8) | (header[3] & 0xFF);
			assertEquals(data.length, declaredLength);
			
			byte[] received = clientIn.readNBytes(data.length);
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
			NetworkUtils.writeFrame(client.getOutputStream(), dataToSend);
			
			byte[] received = connection.receive();
			assertArrayEquals(dataToSend, received);
		});
	}
	
	@Test
	void receiveWithMaxBytesSmallerThanFrameThrows() throws Exception {
		this.withPair(8192, (client, connection) -> {
			NetworkUtils.writeFrame(client.getOutputStream(), "Hello World".getBytes());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> connection.receive(5));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertTrue(exception.getMessage().contains("11"));
			assertTrue(exception.getMessage().contains("5"));
		});
	}
	
	@Test
	void receiveWithMaxBytesLargerThanFrame() throws Exception {
		this.withPair(8192, (client, connection) -> {
			NetworkUtils.writeFrame(client.getOutputStream(), "Hello".getBytes());
			
			byte[] received = connection.receive(100);
			assertArrayEquals("Hello".getBytes(), received);
		});
	}
	
	@Test
	void receiveWithMaxBytesEqualToFrameLength() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] dataToSend = "Hello World".getBytes();
			NetworkUtils.writeFrame(client.getOutputStream(), dataToSend);
			
			byte[] received = connection.receive(dataToSend.length);
			assertArrayEquals(dataToSend, received);
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
	void receiveThrowsOnPeerCloseMidHeader() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.getOutputStream().write(new byte[] { 0, 0 });
			client.getOutputStream().flush();
			client.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::receive);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
		});
	}
	
	@Test
	void receiveThrowsOnPeerCloseMidPayload() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.getOutputStream().write(new byte[] { 0, 0, 0, 10, 1, 2, 3, 4 });
			client.getOutputStream().flush();
			client.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::receive);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
		});
	}
	
	@Test
	void receiveEmptyMessageDoesNotCloseConnection() throws Exception {
		this.withPair(8192, (client, connection) -> {
			NetworkUtils.writeFrame(client.getOutputStream(), new byte[0]);
			byte[] received = connection.receive();
			assertNotNull(received);
			assertEquals(0, received.length);
			assertTrue(connection.isActive());
			
			byte[] second = "Still Alive".getBytes();
			NetworkUtils.writeFrame(client.getOutputStream(), second);
			byte[] receivedSecond = connection.receive();
			assertArrayEquals(second, receivedSecond);
		});
	}
	
	@Test
	void receiveReassemblesMessageDeliveredInFragmentedWrites() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] payload = "Reassembled Across Fragments".getBytes();
			ByteArrayOutputStream frameBytes = new ByteArrayOutputStream();
			NetworkUtils.writeFrame(frameBytes, payload);
			byte[] frame = frameBytes.toByteArray();
			
			OutputStream clientOut = client.getOutputStream();
			for (int i = 0; i < frame.length; i += 3) {
				int end = Math.min(i + 3, frame.length);
				clientOut.write(frame, i, end - i);
				clientOut.flush();
				Thread.sleep(10);
			}
			
			byte[] received = connection.receive();
			assertArrayEquals(payload, received);
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
	
	@FunctionalInterface
	private interface PairConsumer {
		
		void accept(SSLSocket client, SslConnection connection) throws Exception;
	}
	//endregion
}
