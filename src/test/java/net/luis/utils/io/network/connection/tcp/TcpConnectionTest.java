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
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.InputStream;
import java.io.OutputStream;
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
}
