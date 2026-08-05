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
import net.luis.utils.io.network.connection.Connection;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TcpConnection}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TcpConnectionTest {
	
	@Test
	void sendWithNullDataThrows() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				assertThrows(NullPointerException.class, () -> connection.send(null));
			}
		}
	}
	
	@Test
	void sendWithValidData() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				byte[] data = "Hello".getBytes();
				
				assertDoesNotThrow(() -> connection.send(data));
				
				byte[] received = new byte[data.length];
				int bytesRead = clientSocket.getInputStream().read(received);
				assertEquals(data.length, bytesRead);
				assertArrayEquals(data, received);
			}
		}
	}
	
	@Test
	void sendWithEmptyArray() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				assertDoesNotThrow(() -> connection.send(new byte[0]));
			}
		}
	}
	
	@Test
	void sendDataExceedingBufferSizeThrows() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 100, Duration.ofSeconds(5));
				byte[] largeData = new byte[101];
				
				NetworkConnectionException exception = assertThrows(
					NetworkConnectionException.class,
					() -> connection.send(largeData)
				);
				assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			}
		}
	}
	
	@Test
	void sendDataEqualToBufferSize() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 100, Duration.ofSeconds(5));
				byte[] exactData = new byte[100];
				for (int i = 0; i < 100; i++) {
					exactData[i] = (byte) i;
				}
				
				assertDoesNotThrow(() -> connection.send(exactData));
				
				byte[] received = new byte[100];
				int bytesRead = clientSocket.getInputStream().read(received);
				assertEquals(100, bytesRead);
				assertArrayEquals(exactData, received);
			}
		}
	}
	
	@Test
	void sendWhenConnectionClosedThrows() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				connection.close();
				
				NetworkConnectionException exception = assertThrows(
					NetworkConnectionException.class,
					() -> connection.send("test".getBytes())
				);
				assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
			}
		}
	}
	
	@Test
	void receiveReturnsData() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				byte[] dataToSend = "Hello World".getBytes();
				
				clientSocket.getOutputStream().write(dataToSend);
				clientSocket.getOutputStream().flush();
				
				byte[] received = connection.receive();
				assertArrayEquals(dataToSend, received);
			}
		}
	}
	
	@Test
	void receiveWithCustomMaxBytes() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				byte[] dataToSend = "Hello World".getBytes();
				
				clientSocket.getOutputStream().write(dataToSend);
				clientSocket.getOutputStream().flush();
				
				byte[] received = connection.receive(5);
				assertEquals(5, received.length);
				assertArrayEquals("Hello".getBytes(), received);
			}
		}
	}
	
	@Test
	void receiveWithZeroMaxBytesThrows() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				assertThrows(IllegalArgumentException.class, () -> connection.receive(0));
			}
		}
	}
	
	@Test
	void receiveWithNegativeMaxBytesThrows() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				assertThrows(IllegalArgumentException.class, () -> connection.receive(-1));
			}
		}
	}
	
	@Test
	void receiveWhenConnectionClosedThrows() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				connection.close();
				
				NetworkConnectionException exception = assertThrows(
					NetworkConnectionException.class,
					() -> connection.receive()
				);
				assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
			}
		}
	}
	
	@Test
	void receiveReturnsEmptyArrayOnPeerClose() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			Socket clientSocket = new Socket("127.0.0.1", port);
			try (Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				
				clientSocket.close();
				
				byte[] received = connection.receive();
				assertEquals(0, received.length);
			}
		}
	}
	
	@Test
	void getInputStreamReturnsStream() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				
				InputStream inputStream = connection.getInputStream();
				assertNotNull(inputStream);
			}
		}
	}
	
	@Test
	void getInputStreamWhenClosedThrows() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				connection.close();
				
				NetworkConnectionException exception = assertThrows(
					NetworkConnectionException.class,
					() -> connection.getInputStream()
				);
				assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
			}
		}
	}
	
	@Test
	void getOutputStreamReturnsStream() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				
				OutputStream outputStream = connection.getOutputStream();
				assertNotNull(outputStream);
			}
		}
	}
	
	@Test
	void getOutputStreamWhenClosedThrows() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				connection.close();
				
				NetworkConnectionException exception = assertThrows(
					NetworkConnectionException.class,
					() -> connection.getOutputStream()
				);
				assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
			}
		}
	}
	
	@Test
	void isActiveReturnsTrueForActiveConnection() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				assertTrue(connection.isActive());
			}
		}
	}
	
	@Test
	void isActiveReturnsFalseAfterClose() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				assertTrue(connection.isActive());
				connection.close();
				assertFalse(connection.isActive());
			}
		}
	}
	
	@Test
	void localEndpointReturnsCorrectEndpoint() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				
				IpEndpoint localEndpoint = connection.localEndpoint();
				assertNotNull(localEndpoint);
				assertEquals(port, localEndpoint.port());
			}
		}
	}
	
	@Test
	void remoteEndpointReturnsCorrectEndpoint() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				
				IpEndpoint remoteEndpoint = connection.remoteEndpoint();
				assertNotNull(remoteEndpoint);
				assertEquals(clientSocket.getLocalPort(), remoteEndpoint.port());
			}
		}
	}
	
	@Test
	void closeClosesConnection() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				assertTrue(connection.isActive());
				
				connection.close();
				
				assertFalse(connection.isActive());
			}
		}
	}
	
	@Test
	void closeIsIdempotent() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				
				assertDoesNotThrow(() -> {
					connection.close();
					connection.close();
					connection.close();
				});
				
				assertFalse(connection.isActive());
			}
		}
	}
	
	@Test
	void implementsConnectionInterface() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				assertInstanceOf(Connection.class, connection);
			}
		}
	}
	
	@Test
	void constructWithSocketBufferSizeAndTimeout() throws Exception {
		this.withPair((clientSocket, connection) -> {
			assertTrue(connection.isActive());
			assertEquals(clientSocket.getLocalPort(), connection.remoteEndpoint().port());
			assertEquals(clientSocket.getPort(), connection.localEndpoint().port());
		});
	}
	
	@Test
	void constructWithNullSocket() {
		assertThrows(NullPointerException.class, () -> new TcpConnection(null, 8192, Duration.ofSeconds(5)));
	}
	
	@Test
	void constructWithNullReadTimeout() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				assertThrows(NullPointerException.class, () -> new TcpConnection(serverSideSocket, 8192, null));
			}
		}
	}
	
	@Test
	void receiveReusesBufferAcrossCalls() throws Exception {
		this.withPair((clientSocket, connection) -> {
			clientSocket.getOutputStream().write("first".getBytes());
			clientSocket.getOutputStream().flush();
			assertArrayEquals("first".getBytes(), receiveExactly(connection, 5, 1024));
			
			clientSocket.getOutputStream().write("second".getBytes());
			clientSocket.getOutputStream().flush();
			assertArrayEquals("second".getBytes(), receiveExactly(connection, 6, 1024));
		});
	}
	
	@Test
	void receiveGrowsBufferForLargerMaxBytes() throws Exception {
		this.withPair((clientSocket, connection) -> {
			clientSocket.getOutputStream().write(new byte[8]);
			clientSocket.getOutputStream().flush();
			assertEquals(8, receiveExactly(connection, 8, 16).length);
			
			byte[] large = filled(100, (byte) 0x42);
			clientSocket.getOutputStream().write(large);
			clientSocket.getOutputStream().flush();
			assertArrayEquals(large, receiveExactly(connection, 100, 4096));
		});
	}
	
	@Test
	void receiveWithSmallerMaxBytesAfterLargerReusesBuffer() throws Exception {
		this.withPair((clientSocket, connection) -> {
			clientSocket.getOutputStream().write(new byte[10]);
			clientSocket.getOutputStream().flush();
			assertEquals(10, receiveExactly(connection, 10, 4096).length);
			
			byte[] payload = filled(50, (byte) 0x43);
			clientSocket.getOutputStream().write(payload);
			clientSocket.getOutputStream().flush();
			
			byte[] limited = connection.receive(8);
			assertTrue(limited.length <= 8);
			assertTrue(limited.length > 0);
			
			byte[] rest = receiveExactly(connection, 50 - limited.length, 4096);
			assertEquals(50, limited.length + rest.length);
		});
	}
	
	@Test
	void receiveDoesNotLeakPreviousPayload() throws Exception {
		this.withPair((clientSocket, connection) -> {
			byte[] long_ = filled(200, (byte) 0x41);
			clientSocket.getOutputStream().write(long_);
			clientSocket.getOutputStream().flush();
			assertArrayEquals(long_, receiveExactly(connection, 200, 1024));
			
			clientSocket.getOutputStream().write("abc".getBytes());
			clientSocket.getOutputStream().flush();
			byte[] second = receiveExactly(connection, 3, 1024);
			assertEquals(3, second.length);
			assertArrayEquals("abc".getBytes(), second);
		});
	}
	
	@Test
	void receiveAfterEmptyReadStillWorks() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket serverSideSocket = openPeerAndClose(port, serverSocket)) {
				TcpConnection connection = new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5));
				assertEquals(0, connection.receive(1024).length);
			}
		}
	}
	
	@Test
	void receiveMultipleTimesWithVaryingSizes() throws Exception {
		this.withPair((clientSocket, connection) -> {
			int[] sizes = { 10, 500, 20, 2000, 5 };
			for (int size : sizes) {
				byte[] payload = filled(size, (byte) (size % 128));
				clientSocket.getOutputStream().write(payload);
				clientSocket.getOutputStream().flush();
				assertArrayEquals(payload, receiveExactly(connection, size, 4096));
			}
		});
	}
	
	@Test
	void receiveReturnedArraysAreIndependent() throws Exception {
		this.withPair((clientSocket, connection) -> {
			clientSocket.getOutputStream().write("first".getBytes());
			clientSocket.getOutputStream().flush();
			byte[] first = receiveExactly(connection, 5, 1024);
			
			clientSocket.getOutputStream().write("second".getBytes());
			clientSocket.getOutputStream().flush();
			byte[] second = receiveExactly(connection, 6, 1024);
			
			assertArrayEquals("first".getBytes(), first);
			assertNotSame(first, second);
		});
	}
	
	@Test
	void sendAndReceiveRoundTripAfterBufferGrowth() throws Exception {
		this.withPair((clientSocket, connection) -> {
			byte[] large = filled(2000, (byte) 0x44);
			clientSocket.getOutputStream().write(large);
			clientSocket.getOutputStream().flush();
			assertArrayEquals(large, receiveExactly(connection, 2000, 4096));
			
			connection.send(new byte[] { 0x7F });
			byte[] echoed = new byte[1];
			assertEquals(1, clientSocket.getInputStream().read(echoed));
			assertEquals(0x7F, echoed[0]);
			
			clientSocket.getOutputStream().write(new byte[] { 0x01 });
			clientSocket.getOutputStream().flush();
			assertArrayEquals(new byte[] { 0x01 }, receiveExactly(connection, 1, 4096));
		});
	}
	
	private static byte[] filled(int length, byte value) {
		byte[] data = new byte[length];
		java.util.Arrays.fill(data, value);
		return data;
	}
	
	private static byte[] receiveExactly(TcpConnection connection, int expected, int maxBytes) throws Exception {
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
	
	private static Socket openPeerAndClose(int port, ServerSocket serverSocket) throws Exception {
		try (Socket peer = new Socket("127.0.0.1", port)) {
			Socket accepted = serverSocket.accept();
			peer.close();
			return accepted;
		}
	}
	
	private void withPair(PairConsumer body) throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket clientSocket = new Socket("127.0.0.1", port);
			     Socket serverSideSocket = serverSocket.accept()) {
				body.accept(clientSocket, new TcpConnection(serverSideSocket, 8192, Duration.ofSeconds(5)));
			}
		}
	}
	
	@FunctionalInterface
	private interface PairConsumer {
		
		void accept(Socket clientSocket, TcpConnection connection) throws Exception;
	}
}
