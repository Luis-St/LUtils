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
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TCP client and server communication.<br>
 * Tests message encoding/decoding, data integrity, and various communication scenarios.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TcpIntegrationTest {
	
	@Test
	void serverStartAndStop() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			assertFalse(server.isRunning());
			assertEquals(0, server.getClientCount());
			
			server.start();
			assertTrue(server.isRunning());
			assertNotEquals(0, server.boundEndpoint().port());
			
			server.stop();
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void serverImplementsNetworkServer() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			assertInstanceOf(NetworkServer.class, server);
		}
	}
	
	@Test
	void clientImplementsNetworkClient() {
		try (TcpClient client = new TcpClient()) {
			assertInstanceOf(NetworkClient.class, client);
		}
	}
	
	@Test
	void clientNotConnectedInitially() {
		try (TcpClient client = new TcpClient()) {
			assertFalse(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientConnectToServer() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(serverEndpoint);
				
				assertTrue(client.isActive());
				assertTrue(client.localEndpoint().isPresent());
				assertTrue(client.remoteEndpoint().isPresent());
				assertEquals(serverEndpoint, client.remoteEndpoint().get());
				
				Thread.sleep(100);
				assertEquals(1, server.getClientCount());
			}
		}
	}
	
	@Test
	void clientConnectToNonExistentServerThrows() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 59999);
		
		TcpClientConfig config = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(2))
			.build();
		
		try (TcpClient client = new TcpClient(config)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(endpoint));
			
			assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
		}
	}
	
	@Test
	void clientSendWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send("data".getBytes()));
			
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientReceiveWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientDoubleConnectThrows() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(serverEndpoint);
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(serverEndpoint));
				
				assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
			}
		}
	}
	
	@Test
	void sendAndReceiveBinaryData() throws Exception {
		byte[] binaryData = new byte[256];
		for (int i = 0; i < 256; i++) {
			binaryData[i] = (byte) i;
		}
		
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.executorStrategy(ClientExecutorStrategy.virtualThreads())
			.onMessage((server, conn, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				client.send(binaryData);
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals(binaryData, receivedData.get());
			}
		}
	}
	
	@Test
	void sendAndReceiveSingleByte() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage((server, conn, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				client.send(new byte[] { 42 });
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals(new byte[] { 42 }, receivedData.get());
			}
		}
	}
	
	@Test
	void clientSendAndReceiveWithAck() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.executorStrategy(ClientExecutorStrategy.virtualThreads())
			.onMessage((server, conn, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
				try {
					conn.send("ACK".getBytes());
				} catch (NetworkConnectionException e) {
					fail("Failed to send response: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.readTimeout(Duration.ofSeconds(5))
				.build();
			
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(serverEndpoint);
				
				client.send("Hello, Server!".getBytes());
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals("Hello, Server!".getBytes(), receivedData.get());
				
				byte[] response = client.receive();
				assertArrayEquals("ACK".getBytes(), response);
			}
		}
	}
	
	@Test
	void sendAndReceiveUtf8SpecialCharacters() throws Exception {
		String specialChars = "Hello \u4e16\u754c! \u041f\u0440\u0438\u0432\u0435\u0442 \u043c\u0438\u0440! \u0645\u0631\u062d\u0628\u0627 \u0627\u0644\u0639\u0627\u0644\u0645";
		byte[] data = specialChars.getBytes(StandardCharsets.UTF_8);
		
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage((server, conn, d) -> {
				receivedData.set(d);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				client.send(data);
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				String received = new String(receivedData.get(), StandardCharsets.UTF_8);
				assertEquals(specialChars, received);
			}
		}
	}
	
	@Test
	void echoServerRoundTrip() throws Exception {
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.readTimeout(Duration.ofSeconds(5))
				.build();
			
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				
				byte[] original = "Hello, Echo Server!".getBytes();
				client.send(original);
				
				byte[] echoed = client.receive();
				assertArrayEquals(original, echoed);
			}
		}
	}
	
	@Test
	void echoServerRoundTripWithBinaryData() throws Exception {
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.readTimeout(Duration.ofSeconds(5))
				.build();
			
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				
				byte[] original = { 0, 127, -128, -1, 64, 32, 16, 8, 4, 2, 1 };
				client.send(original);
				
				byte[] echoed = client.receive();
				assertArrayEquals(original, echoed);
			}
		}
	}
	
	@Test
	void sendAndReceiveLargeMessageViaEcho() throws Exception {
		byte[] largeData = new byte[5000];
		new Random(42).nextBytes(largeData);
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.readTimeout(Duration.ofSeconds(10))
				.build();
			
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				client.send(largeData);
				
				byte[] received = client.receive();
				assertArrayEquals(largeData, received);
			}
		}
	}
	
	@Test
	void sendMultipleSequentialMessagesWithDelay() throws Exception {
		List<byte[]> expectedMessages = List.of(
			"First".getBytes(),
			"Second".getBytes(),
			"Third".getBytes()
		);
		
		CountDownLatch allReceived = new CountDownLatch(3);
		List<byte[]> receivedMessages = Collections.synchronizedList(new ArrayList<>());
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage((server, conn, data) -> {
				receivedMessages.add(data);
				allReceived.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				
				for (byte[] message : expectedMessages) {
					client.send(message);
					Thread.sleep(50);
				}
				
				assertTrue(allReceived.await(5, TimeUnit.SECONDS));
				assertEquals(3, receivedMessages.size());
				
				assertArrayEquals(expectedMessages.get(0), receivedMessages.get(0));
				assertArrayEquals(expectedMessages.get(1), receivedMessages.get(1));
				assertArrayEquals(expectedMessages.get(2), receivedMessages.get(2));
			}
		}
	}
	
	@Test
	void sendAndReceiveMultipleRoundTrips() throws Exception {
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Send failed");
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.readTimeout(Duration.ofSeconds(5))
				.build();
			
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				
				for (int i = 0; i < 10; i++) {
					byte[] message = ("Message " + i).getBytes();
					client.send(message);
					byte[] response = client.receive();
					assertArrayEquals(message, response);
				}
			}
		}
	}
	
	@Test
	void receiveTimeoutExpires() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			
			TcpClientConfig config = TcpClientConfig.builder()
				.readTimeout(Duration.ofMillis(500))
				.build();
			
			try (TcpClient client = new TcpClient(config)) {
				client.connect(server.boundEndpoint());
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
				assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
			}
		}
	}
	
	@Test
	void receiveThrowsOnPeerClose() throws Exception {
		CountDownLatch clientConnected = new CountDownLatch(1);
		AtomicReference<TcpConnection> connectionRef = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> clientConnected.countDown())
			.onMessage((server, conn, data) -> connectionRef.set(conn))
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.readTimeout(Duration.ofSeconds(5))
				.build();
			
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				assertTrue(clientConnected.await(5, TimeUnit.SECONDS));
				
				client.send("test".getBytes());
				Thread.sleep(100);
				
				TcpConnection serverSideConn = connectionRef.get();
				if (serverSideConn != null) {
					serverSideConn.close();
				}
				
				assertArrayEquals(ArrayUtils.EMPTY_BYTE_ARRAY, client.receive());
			}
		}
	}
	
	@Test
	void clientReceiveWithMaxBytesSmallerThanFrameThrows() throws Exception {
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage((server, conn, data) -> {
				try {
					conn.send("Hello World".getBytes());
				} catch (NetworkConnectionException e) {
					fail("Failed to send response: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.readTimeout(Duration.ofSeconds(5))
				.build();
			
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				client.send("trigger".getBytes());
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.receive(5));
				assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			}
		}
	}
	
	@Test
	void clientReceiveThrowsOnServerCloseMidFrame() throws Exception {
		CountDownLatch clientConnected = new CountDownLatch(1);
		AtomicReference<Connection> connectionRef = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectionRef.set(connection);
				clientConnected.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.readTimeout(Duration.ofSeconds(5))
				.build();
			
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				assertTrue(clientConnected.await(5, TimeUnit.SECONDS));
				
				Connection serverSideConnection = connectionRef.get();
				OutputStream out = serverSideConnection.getOutputStream();
				out.write(new byte[] { 0, 0, 0, 10, 1, 2, 3 });
				out.flush();
				serverSideConnection.close();
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
				assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
			}
		}
	}
	
	@Test
	void clientReceiveEmptyMessageDoesNotDisconnect() throws Exception {
		CountDownLatch clientConnected = new CountDownLatch(1);
		CountDownLatch disconnectLatch = new CountDownLatch(1);
		AtomicReference<Connection> connectionRef = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectionRef.set(connection);
				clientConnected.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.readTimeout(Duration.ofSeconds(5))
				.onDisconnect((connection, local, remote, timestamp) -> disconnectLatch.countDown())
				.build();
			
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				assertTrue(clientConnected.await(5, TimeUnit.SECONDS));
				
				connectionRef.get().send(new byte[0]);
				
				byte[] received = client.receive();
				assertNotNull(received);
				assertEquals(0, received.length);
				assertTrue(client.isActive());
				assertEquals(1, disconnectLatch.getCount());
				
				byte[] second = "Still Alive".getBytes();
				connectionRef.get().send(second);
				assertArrayEquals(second, client.receive());
			}
		}
	}
	
	@Test
	void sendAndReceiveMessageDeliveredInFragmentedWrites() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onMessage((server, conn, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				
				byte[] payload = "Fragmented End To End".getBytes();
				ByteArrayOutputStream frameBytes = new ByteArrayOutputStream();
				NetworkUtils.writeFrame(frameBytes, payload);
				byte[] frame = frameBytes.toByteArray();
				
				OutputStream out = client.getOutputStream();
				for (int i = 0; i < frame.length; i += 3) {
					int end = Math.min(i + 3, frame.length);
					out.write(frame, i, end - i);
					out.flush();
					Thread.sleep(10);
				}
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals(payload, receivedData.get());
			}
		}
	}
	
	@RepeatedTest(2)
	void errorHandlerDoesNotReceiveIOError() throws Exception {
		CountDownLatch errorLatch = new CountDownLatch(1);
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onError((connection, errorType, message, cause) -> errorLatch.countDown())
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				Thread.sleep(100);
				client.close();
			}
			
			Thread.sleep(500);
			assertEquals(1, errorLatch.getCount());
		}
	}
	
	@Test
	void multipleClientsConnectAndSendSimultaneously() throws Exception {
		int clientCount = 5;
		CountDownLatch allConnected = new CountDownLatch(clientCount);
		CountDownLatch allMessagesReceived = new CountDownLatch(clientCount);
		Set<String> receivedMessages = Collections.synchronizedSet(new HashSet<>());
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> allConnected.countDown())
			.onMessage((server, conn, data) -> {
				receivedMessages.add(new String(data));
				allMessagesReceived.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			List<TcpClient> clients = new ArrayList<>();
			try {
				for (int i = 0; i < clientCount; i++) {
					TcpClient client = new TcpClient();
					client.connect(server.boundEndpoint());
					clients.add(client);
				}
				
				assertTrue(allConnected.await(5, TimeUnit.SECONDS));
				assertEquals(clientCount, server.getClientCount());
				
				for (int i = 0; i < clientCount; i++) {
					clients.get(i).send(("Client " + i).getBytes());
				}
				
				assertTrue(allMessagesReceived.await(5, TimeUnit.SECONDS));
				assertEquals(clientCount, receivedMessages.size());
			} finally {
				for (TcpClient client : clients) {
					client.close();
				}
			}
		}
	}
	
	@Test
	void serverBroadcast() throws Exception {
		CountDownLatch clientsConnected = new CountDownLatch(2);
		CountDownLatch messagesReceived = new CountDownLatch(2);
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> clientsConnected.countDown())
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.readTimeout(Duration.ofSeconds(5))
				.build();
			
			try (TcpClient client1 = new TcpClient(clientConfig);
				 TcpClient client2 = new TcpClient(clientConfig)) {
				
				client1.connect(serverEndpoint);
				client2.connect(serverEndpoint);
				
				assertTrue(clientsConnected.await(5, TimeUnit.SECONDS));
				
				server.broadcast("Broadcast message".getBytes());
				
				Thread receiver1 = new Thread(() -> {
					try {
						byte[] data = client1.receive();
						if (data.length > 0) messagesReceived.countDown();
					} catch (NetworkConnectionException ignored) {}
				});
				
				Thread receiver2 = new Thread(() -> {
					try {
						byte[] data = client2.receive();
						if (data.length > 0) messagesReceived.countDown();
					} catch (NetworkConnectionException ignored) {}
				});
				
				receiver1.start();
				receiver2.start();
				
				assertTrue(messagesReceived.await(5, TimeUnit.SECONDS));
			}
		}
	}
	
	@Test
	void clientSendThrowsExceptionWhenMessageExceedsBufferSize() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			
			TcpClientConfig config = TcpClientConfig.builder()
				.bufferSize(100)
				.build();
			
			try (TcpClient client = new TcpClient(config)) {
				client.connect(server.boundEndpoint());
				
				byte[] oversizedData = new byte[150];
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(oversizedData));
				
				assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
				assertTrue(exception.getMessage().contains("150"));
				assertTrue(exception.getMessage().contains("100"));
			}
		}
	}
	
	@Test
	void clientSendSucceedsWhenMessageEqualsBufferSize() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		TcpServerConfig serverConfig = TcpServerConfig.builder()
			.onMessage((server, conn, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, serverConfig)) {
			server.start();
			
			TcpClientConfig clientConfig = TcpClientConfig.builder()
				.bufferSize(100)
				.build();
			
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				
				byte[] exactSizeData = new byte[100];
				Arrays.fill(exactSizeData, (byte) 42);
				client.send(exactSizeData);
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals(exactSizeData, receivedData.get());
			}
		}
	}
	
	@Test
	void serverConnectionSendThrowsExceptionWhenMessageExceedsBufferSize() throws Exception {
		CountDownLatch clientConnected = new CountDownLatch(1);
		AtomicReference<NetworkConnectionException> exceptionRef = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.clientBufferSize(100)
			.onClientConnect((connection, local, remote, timestamp) -> clientConnected.countDown())
			.onMessage((server, conn, data) -> {
				try {
					byte[] oversizedData = new byte[150];
					conn.send(oversizedData);
				} catch (NetworkConnectionException e) {
					exceptionRef.set(e);
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				assertTrue(clientConnected.await(5, TimeUnit.SECONDS));
				
				client.send("trigger".getBytes());
				Thread.sleep(200);
				
				assertNotNull(exceptionRef.get());
				assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exceptionRef.get().errorType());
			}
		}
	}
	
	@Test
	void serverEventHandlers() throws Exception {
		CountDownLatch connectLatch = new CountDownLatch(1);
		CountDownLatch disconnectLatch = new CountDownLatch(1);
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> connectLatch.countDown())
			.onClientDisconnect((connection, local, remote, timestamp) -> disconnectLatch.countDown())
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			TcpClient client = new TcpClient();
			client.connect(serverEndpoint);
			
			assertTrue(connectLatch.await(5, TimeUnit.SECONDS));
			
			client.close();
			
			assertTrue(disconnectLatch.await(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void clientEventHandlers() throws Exception {
		CountDownLatch connectLatch = new CountDownLatch(1);
		CountDownLatch disconnectLatch = new CountDownLatch(1);
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			TcpClientConfig config = TcpClientConfig.builder()
				.onConnect((connection, local, remote, timestamp) -> connectLatch.countDown())
				.onDisconnect((connection, local, remote, timestamp) -> disconnectLatch.countDown())
				.build();
			
			try (TcpClient client = new TcpClient(config)) {
				client.connect(serverEndpoint);
				assertTrue(connectLatch.await(5, TimeUnit.SECONDS));
			}
			
			assertTrue(disconnectLatch.await(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void serverClientConnectEventProvidesConnection() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<Connection> connectionRef = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectionRef.set(connection);
				latch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				assertTrue(latch.await(5, TimeUnit.SECONDS));
				
				assertNotNull(connectionRef.get());
				assertInstanceOf(TcpConnection.class, connectionRef.get());
				assertTrue(connectionRef.get().isActive());
			}
		}
	}
	
	@Test
	void serverClientDisconnectEventProvidesConnection() throws Exception {
		CountDownLatch connectLatch = new CountDownLatch(1);
		CountDownLatch disconnectLatch = new CountDownLatch(1);
		AtomicReference<Connection> connectRef = new AtomicReference<>();
		AtomicReference<Connection> disconnectRef = new AtomicReference<>();
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectRef.set(connection);
				connectLatch.countDown();
			})
			.onClientDisconnect((connection, local, remote, timestamp) -> {
				disconnectRef.set(connection);
				disconnectLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			TcpClient client = new TcpClient();
			client.connect(server.boundEndpoint());
			assertTrue(connectLatch.await(5, TimeUnit.SECONDS));
			
			client.close();
			assertTrue(disconnectLatch.await(5, TimeUnit.SECONDS));
			
			assertNotNull(disconnectRef.get());
			assertSame(connectRef.get(), disconnectRef.get());
		}
	}
	
	@Test
	void clientConnectEventConnectionIsNull() throws Exception {
		CountDownLatch connectLatch = new CountDownLatch(1);
		AtomicReference<Connection> connectionRef = new AtomicReference<>();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			TcpClientConfig config = TcpClientConfig.builder()
				.onConnect((connection, local, remote, timestamp) -> {
					connectionRef.set(connection);
					connectLatch.countDown();
				})
				.build();
			
			try (TcpClient client = new TcpClient(config)) {
				client.connect(serverEndpoint);
				assertTrue(connectLatch.await(5, TimeUnit.SECONDS));
				
				assertNull(connectionRef.get());
			}
		}
	}
	
	@Test
	void clientDisconnectEventConnectionIsNull() throws Exception {
		CountDownLatch connectLatch = new CountDownLatch(1);
		CountDownLatch disconnectLatch = new CountDownLatch(1);
		AtomicReference<Connection> connectionRef = new AtomicReference<>();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			TcpClientConfig config = TcpClientConfig.builder()
				.onConnect((connection, local, remote, timestamp) -> connectLatch.countDown())
				.onDisconnect((connection, local, remote, timestamp) -> {
					connectionRef.set(connection);
					disconnectLatch.countDown();
				})
				.build();
			
			try (TcpClient client = new TcpClient(config)) {
				client.connect(serverEndpoint);
				assertTrue(connectLatch.await(5, TimeUnit.SECONDS));
			}
			
			assertTrue(disconnectLatch.await(5, TimeUnit.SECONDS));
			assertNull(connectionRef.get());
		}
	}
	
	@Test
	void serverBroadcastErrorProvidesConnection() throws Exception {
		CountDownLatch clientConnected = new CountDownLatch(1);
		CountDownLatch errorLatch = new CountDownLatch(1);
		AtomicReference<Connection> connectedConnection = new AtomicReference<>();
		AtomicReference<Connection> errorConnection = new AtomicReference<>();
		
		ErrorEventHandler onError = (connection, errorType, message, cause) -> {
			errorConnection.set(connection);
			errorLatch.countDown();
		};
		
		TcpServerConfig config = TcpServerConfig.builder()
			.clientBufferSize(50)
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectedConnection.set(connection);
				clientConnected.countDown();
			})
			.onError(onError)
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				assertTrue(clientConnected.await(5, TimeUnit.SECONDS));
				
				server.broadcast(new byte[100]);
				
				assertTrue(errorLatch.await(5, TimeUnit.SECONDS));
				assertNotNull(errorConnection.get());
				assertSame(connectedConnection.get(), errorConnection.get());
			}
		}
	}
	
	@Test
	void clientConnectErrorProvidesNullConnection() throws Exception {
		CountDownLatch errorLatch = new CountDownLatch(1);
		AtomicReference<Connection> errorConnection = new AtomicReference<>();
		
		ErrorEventHandler onError = (connection, errorType, message, cause) -> {
			errorConnection.set(connection);
			errorLatch.countDown();
		};
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 59998);
		TcpClientConfig config = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(2))
			.onError(onError)
			.build();
		
		try (TcpClient client = new TcpClient(config)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(endpoint));
			assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
		}
		
		assertTrue(errorLatch.await(5, TimeUnit.SECONDS));
		assertNull(errorConnection.get());
	}
	
	@Test
	void serverMessageHandlerErrorProvidesConnection() throws Exception {
		CountDownLatch clientConnected = new CountDownLatch(1);
		CountDownLatch errorLatch = new CountDownLatch(1);
		AtomicReference<Connection> connectedConnection = new AtomicReference<>();
		AtomicReference<Connection> errorConnection = new AtomicReference<>();
		
		ErrorEventHandler onError = (connection, errorType, message, cause) -> {
			errorConnection.set(connection);
			errorLatch.countDown();
		};
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectedConnection.set(connection);
				clientConnected.countDown();
			})
			.onMessage((server, conn, data) -> {
				throw new RuntimeException("boom");
			})
			.onError(onError)
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				assertTrue(clientConnected.await(5, TimeUnit.SECONDS));
				
				client.send("trigger".getBytes());
				
				assertTrue(errorLatch.await(5, TimeUnit.SECONDS));
				assertNotNull(errorConnection.get());
				assertSame(connectedConnection.get(), errorConnection.get());
			}
		}
	}
	
	@Test
	void serverClientErrorProvidesConnection() throws Exception {
		CountDownLatch clientConnected = new CountDownLatch(1);
		CountDownLatch errorLatch = new CountDownLatch(1);
		AtomicReference<Connection> connectedConnection = new AtomicReference<>();
		AtomicReference<Connection> errorConnection = new AtomicReference<>();
		
		ErrorEventHandler onError = (connection, errorType, message, cause) -> {
			errorConnection.set(connection);
			errorLatch.countDown();
		};
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectedConnection.set(connection);
				clientConnected.countDown();
			})
			.onError(onError)
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, config)) {
			server.start();
			
			try (Socket rawSocket = new Socket()) {
				rawSocket.connect(server.boundEndpoint().toInetSocketAddress());
				assertTrue(clientConnected.await(5, TimeUnit.SECONDS));
				
				rawSocket.setSoLinger(true, 0);
				rawSocket.close();
				
				assertTrue(errorLatch.await(5, TimeUnit.SECONDS));
				assertNotNull(errorConnection.get());
				assertSame(connectedConnection.get(), errorConnection.get());
			}
		}
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new TcpClient(null));
	}
	
	@Test
	void clientConnectWithNullEndpointThrows() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(NullPointerException.class, () -> client.connect(null));
		}
	}
	
	@Test
	void clientConnectToWithNullEndpointThrows() {
		assertThrows(NullPointerException.class, () -> TcpClient.connectTo(null));
		assertThrows(NullPointerException.class, () -> TcpClient.connectTo(null, TcpClientConfig.DEFAULT));
	}
	
	@Test
	void clientConnectToWithNullConfigThrows() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 59999);
		assertThrows(NullPointerException.class, () -> TcpClient.connectTo(endpoint, null));
	}
	
	@Test
	void clientSendWithNullDataThrows() throws Exception {
		try (TcpClient unconnected = new TcpClient()) {
			assertThrows(NullPointerException.class, () -> unconnected.send(null));
		}
		
		withEchoServer(client -> assertThrows(NullPointerException.class, () -> client.send(null)));
	}
	
	@Test
	void clientReceiveWithZeroMaxBytesThrows() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
		}
	}
	
	@Test
	void clientReceiveWithNegativeMaxBytesThrows() {
		try (TcpClient client = new TcpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(-1));
		}
	}
	
	@Test
	void clientGetInputStreamWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getInputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientGetOutputStreamWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getOutputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientRemoteEndpointEmptyBeforeConnect() {
		try (TcpClient client = new TcpClient()) {
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientRemoteEndpointAfterConnect() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				
				assertTrue(client.remoteEndpoint().isPresent());
				assertEquals(server.boundEndpoint().port(), client.remoteEndpoint().orElseThrow().port());
				assertTrue(client.remoteEndpoint().orElseThrow().toInetSocketAddress().getAddress().isLoopbackAddress());
			}
		}
	}
	
	@Test
	void clientRemoteEndpointEmptyAfterClose() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			
			TcpClient client = new TcpClient();
			client.connect(server.boundEndpoint());
			assertTrue(client.remoteEndpoint().isPresent());
			
			client.close();
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientLocalEndpointEmptyBeforeConnect() {
		try (TcpClient client = new TcpClient()) {
			assertTrue(client.localEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientReceiveReusesBufferAcrossCalls() throws Exception {
		withEchoServer(client -> {
			client.send("first".getBytes());
			assertArrayEquals("first".getBytes(), receiveExactly(client, 5, 1024));
			
			client.send("secnd".getBytes());
			assertArrayEquals("secnd".getBytes(), receiveExactly(client, 5, 1024));
		});
	}
	
	@Test
	void clientReceiveGrowsBufferForLargerMaxBytes() throws Exception {
		withEchoServer(client -> {
			client.send(new byte[8]);
			assertEquals(8, receiveExactly(client, 8, 16).length);
			
			byte[] large = filled(500, (byte) 0x42);
			client.send(large);
			assertArrayEquals(large, receiveExactly(client, 500, 4096));
		});
	}
	
	@Test
	void serverBoundEndpointBeforeStartReturnsBindEndpoint() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			assertEquals(endpoint, server.boundEndpoint());
		}
	}
	
	@Test
	void serverBoundEndpointAfterStartReturnsActualPort() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			
			assertNotEquals(0, server.boundEndpoint().port());
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void clientConnectToHostEndpoint() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, echoConfig())) {
			server.start();
			HostEndpoint target = new HostEndpoint("localhost", server.boundEndpoint().port());
			
			try (TcpClient client = new TcpClient(readTimeoutConfig())) {
				client.connect(target);
				
				assertTrue(client.isActive());
				client.send("Hello".getBytes());
				assertArrayEquals("Hello".getBytes(), receiveExactly(client, 5, 1024));
			}
		}
	}
	
	@Test
	void clientConnectToWithHostEndpoint() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			HostEndpoint target = new HostEndpoint("localhost", server.boundEndpoint().port());
			
			try (TcpClient client = TcpClient.connectTo(target)) {
				assertTrue(client.isActive());
				assertEquals(server.boundEndpoint().port(), client.remoteEndpoint().orElseThrow().port());
			}
		}
	}
	
	@Test
	void clientRemoteEndpointIsAlwaysIpEndpointEvenWhenConnectedByName() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			
			try (TcpClient client = TcpClient.connectTo(new HostEndpoint("localhost", server.boundEndpoint().port()))) {
				assertInstanceOf(IpEndpoint.class, client.remoteEndpoint().orElseThrow());
				assertInstanceOf(IpEndpoint.class, client.localEndpoint().orElseThrow());
			}
		}
	}
	
	@Test
	void clientConnectToUnresolvableHostEndpointThrows() {
		HostEndpoint endpoint = new HostEndpoint("this-host-does-not-exist.invalid", 8080);
		
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(endpoint));
			assertEquals(NetworkErrorType.CONNECTION_FAILED, exception.errorType());
			assertSame(endpoint, exception.endpoint());
		}
	}
	
	@Test
	void clientLocalAndRemoteEndpointsDiffer() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			
			try (TcpClient client = new TcpClient()) {
				client.connect(server.boundEndpoint());
				
				assertTrue(client.localEndpoint().isPresent());
				assertTrue(client.remoteEndpoint().isPresent());
				assertNotEquals(client.localEndpoint().orElseThrow().port(), client.remoteEndpoint().orElseThrow().port());
			}
		}
	}
	
	@Test
	void clientGetInputStreamAfterConnect() throws Exception {
		withEchoServer(client -> {
			InputStream stream = client.getInputStream();
			assertNotNull(stream);
			
			client.send("Hi".getBytes());
			assertArrayEquals("Hi".getBytes(), NetworkUtils.readFrame(stream, 1024));
		});
	}
	
	@Test
	void clientGetOutputStreamAfterConnect() throws Exception {
		withEchoServer(client -> {
			OutputStream stream = client.getOutputStream();
			assertNotNull(stream);
			
			NetworkUtils.writeFrame(stream, "Hi".getBytes());
			assertArrayEquals("Hi".getBytes(), receiveExactly(client, 2, 1024));
		});
	}
	
	@Test
	void connectToClosesClientWhenConnectFails() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 59999);
		TcpClientConfig config = TcpClientConfig.builder().connectTimeout(Duration.ofSeconds(2)).build();
		
		NetworkConnectionException first = assertThrows(NetworkConnectionException.class, () -> TcpClient.connectTo(endpoint, config));
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, first.errorType());
		
		NetworkConnectionException second = assertThrows(NetworkConnectionException.class, () -> TcpClient.connectTo(endpoint, config));
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, second.errorType());
	}
	
	@Test
	void clientUsableThroughNetworkClientInterface() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, echoConfig())) {
			server.start();
			
			try (NetworkClient<byte[]> client = TcpClient.connectTo(server.boundEndpoint(), readTimeoutConfig())) {
				assertTrue(client.isActive());
				assertTrue(client.localEndpoint().isPresent());
				assertTrue(client.remoteEndpoint().isPresent());
				
				client.send("Hello".getBytes());
				assertArrayEquals("Hello".getBytes(), client.receive(1024));
			}
		}
	}
	
	@Test
	void clientReceiveDoesNotLeakPreviousResponse() throws Exception {
		withEchoServer(client -> {
			byte[] large = filled(200, (byte) 0x41);
			client.send(large);
			assertArrayEquals(large, receiveExactly(client, 200, 1024));
			
			client.send("abc".getBytes());
			byte[] second = receiveExactly(client, 3, 1024);
			assertEquals(3, second.length);
			assertArrayEquals("abc".getBytes(), second);
		});
	}
	
	@Test
	void clientMultipleRoundTripsWithVaryingSizes() throws Exception {
		withEchoServer(client -> {
			int[] sizes = { 10, 500, 20, 2000, 5 };
			for (int size : sizes) {
				byte[] payload = filled(size, (byte) (size % 128));
				client.send(payload);
				assertArrayEquals(payload, receiveExactly(client, size, 4096));
			}
		});
	}
	
	@Test
	void connectEventReceivesSuppliedEndpointOnFallback() throws Exception {
		AtomicReference<Endpoint> localRef = new AtomicReference<>();
		AtomicReference<Endpoint> remoteRef = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			
			TcpClientConfig config = TcpClientConfig.builder()
				.onConnect((connection, local, remote, timestamp) -> {
					localRef.set(local);
					remoteRef.set(remote);
					latch.countDown();
				})
				.build();
			
			try (TcpClient client = new TcpClient(config)) {
				client.connect(server.boundEndpoint());
				assertTrue(latch.await(5, TimeUnit.SECONDS));
				
				assertInstanceOf(IpEndpoint.class, localRef.get());
				assertInstanceOf(IpEndpoint.class, remoteRef.get());
			}
		}
	}
	
	@Test
	void connectEventHandlerAcceptsEndpointSupertype() throws Exception {
		AtomicReference<String> hostRef = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			
			TcpClientConfig config = TcpClientConfig.builder()
				.onConnect((Connection connection, Endpoint local, Endpoint remote, Instant timestamp) -> {
					hostRef.set(hostPartOf(remote));
					latch.countDown();
				})
				.build();
			
			try (TcpClient client = new TcpClient(config)) {
				client.connect(server.boundEndpoint());
				assertTrue(latch.await(5, TimeUnit.SECONDS));
				assertEquals("127.0.0.1", hostRef.get());
			}
		}
	}
	
	@Test
	void disconnectEventHandlerAcceptsEndpointSupertype() throws Exception {
		AtomicBoolean bothPresent = new AtomicBoolean(false);
		CountDownLatch latch = new CountDownLatch(1);
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint)) {
			server.start();
			
			TcpClientConfig config = TcpClientConfig.builder()
				.onDisconnect((Connection connection, Endpoint local, Endpoint remote, Instant timestamp) -> {
					bothPresent.set(local != null && remote != null);
					latch.countDown();
				})
				.build();
			
			TcpClient client = new TcpClient(config);
			client.connect(server.boundEndpoint());
			client.close();
			
			assertTrue(latch.await(5, TimeUnit.SECONDS));
			assertTrue(bothPresent.get());
		}
	}
	
	@Test
	void unframedClientSendAndReceive() throws Exception {
		withEchoServer(false, client -> {
			byte[] payload = "Hello".getBytes(StandardCharsets.UTF_8);
			client.send(payload);
			
			assertArrayEquals(payload, readUntil(client, payload.length, 8192));
		});
	}
	
	@Test
	void framedClientSendAndReceive() throws Exception {
		withEchoServer(true, client -> {
			byte[] payload = "Hello".getBytes(StandardCharsets.UTF_8);
			client.send(payload);
			
			byte[] received = client.receive();
			assertEquals(payload.length, received.length);
			assertArrayEquals(payload, received);
		});
	}
	
	@Test
	void unframedClientReceiveRespectsMaxBytes() throws Exception {
		withEchoServer(false, client -> {
			client.send(filled(50, (byte) 0x42));
			
			byte[] first = client.receive(10);
			assertTrue(first.length > 0);
			assertTrue(first.length <= 10);
			
			byte[] rest = readUntil(client, 50 - first.length, 8192);
			assertEquals(50, first.length + rest.length);
		});
	}
	
	@Test
	void unframedClientReceiveOnServerCloseReturnsEmpty() throws Exception {
		AtomicInteger disconnects = new AtomicInteger(0);
		TcpServerConfig serverConfig = TcpServerConfig.builder()
			.framing(false)
			.onMessage((server, conn, data) -> conn.close())
			.build();
		TcpClientConfig clientConfig = TcpClientConfig.builder()
			.framing(false)
			.readTimeout(Duration.ofSeconds(5))
			.onDisconnect((connection, local, remote, timestamp) -> disconnects.incrementAndGet())
			.build();
		
		try (TcpServer server = new TcpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), serverConfig)) {
			server.start();
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				client.send("Hi".getBytes(StandardCharsets.UTF_8));
				
				assertEquals(0, client.receive().length);
				assertFalse(client.isActive());
				assertEquals(1, disconnects.get());
			}
		}
	}
	
	@Test
	void unframedServerReceivesClientMessages() throws Exception {
		byte[] payload = "Hello".getBytes(StandardCharsets.UTF_8);
		ByteArrayOutputStream delivered = new ByteArrayOutputStream();
		CountDownLatch complete = new CountDownLatch(1);
		TcpServerConfig serverConfig = TcpServerConfig.builder()
			.framing(false)
			.onMessage((server, conn, data) -> {
				synchronized (delivered) {
					delivered.writeBytes(data);
					if (delivered.size() >= payload.length) {
						complete.countDown();
					}
				}
			})
			.build();
		
		try (TcpServer server = new TcpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), serverConfig)) {
			server.start();
			try (TcpClient client = new TcpClient(unframedClientConfig())) {
				client.connect(server.boundEndpoint());
				client.send(payload);
				
				assertTrue(complete.await(10, TimeUnit.SECONDS));
				synchronized (delivered) {
					assertArrayEquals(payload, delivered.toByteArray());
				}
			}
		}
	}
	
	@Test
	void unframedServerNotifiesClientDisconnect() throws Exception {
		CountDownLatch disconnected = new CountDownLatch(1);
		TcpServerConfig serverConfig = TcpServerConfig.builder()
			.framing(false)
			.onClientDisconnect((connection, local, remote, timestamp) -> disconnected.countDown())
			.build();
		
		try (TcpServer server = new TcpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), serverConfig)) {
			server.start();
			TcpClient client = new TcpClient(unframedClientConfig());
			client.connect(server.boundEndpoint());
			client.close();
			
			assertTrue(disconnected.await(10, TimeUnit.SECONDS));
			assertTrue(awaitClientCount(server, 0));
		}
	}
	
	@Test
	void unframedRoundTripLosesMessageBoundaries() throws Exception {
		withEchoServer(false, client -> {
			client.send("AAA".getBytes(StandardCharsets.UTF_8));
			client.send("BBB".getBytes(StandardCharsets.UTF_8));
			
			assertArrayEquals("AAABBB".getBytes(StandardCharsets.UTF_8), readUntil(client, 6, 8192));
		});
	}
	
	@Test
	void framedRoundTripKeepsMessageBoundaries() throws Exception {
		withEchoServer(true, client -> {
			client.send("AAA".getBytes(StandardCharsets.UTF_8));
			client.send("BBB".getBytes(StandardCharsets.UTF_8));
			
			assertArrayEquals("AAA".getBytes(StandardCharsets.UTF_8), client.receive());
			assertArrayEquals("BBB".getBytes(StandardCharsets.UTF_8), client.receive());
		});
	}
	
	@Test
	void unframedClientReusesScratchBufferAcrossReceives() throws Exception {
		withEchoServer(false, client -> {
			client.send(filled(8, (byte) 0x01));
			assertArrayEquals(filled(8, (byte) 0x01), readUntil(client, 8, 8192));
			
			byte[] larger = filled(4096, (byte) 0x02);
			client.send(larger);
			assertArrayEquals(larger, readUntil(client, larger.length, 8192));
			
			client.send(filled(4, (byte) 0x03));
			assertArrayEquals(filled(4, (byte) 0x03), readUntil(client, 4, 8192));
		});
	}
	
	@Test
	void framedServerWithUnframedClientDoesNotInteroperate() throws Exception {
		byte[] payload = "Hello".getBytes(StandardCharsets.UTF_8);
		TcpServerConfig serverConfig = TcpServerConfig.builder()
			.framing(true)
			.onClientConnect((connection, local, remote, timestamp) -> {
				try {
					connection.send(payload);
				} catch (NetworkConnectionException e) {
					fail("Server send failed: " + e.getMessage());
				}
			})
			.build();
		
		try (TcpServer server = new TcpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), serverConfig)) {
			server.start();
			try (TcpClient client = new TcpClient(unframedClientConfig())) {
				client.connect(server.boundEndpoint());
				
				byte[] received = readUntil(client, payload.length + 4, 8192);
				assertEquals(payload.length + 4, received.length);
				assertArrayEquals(new byte[] { 0, 0, 0, 5 }, Arrays.copyOf(received, 4));
				assertFalse(Arrays.equals(payload, received));
			}
		}
	}
	
	@Test
	void unframedLargeMessageRoundTrip() throws Exception {
		byte[] payload = filled(65536, (byte) 0x42);
		TcpServerConfig serverConfig = TcpServerConfig.builder()
			.framing(false)
			.clientBufferSize(65536)
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
		TcpClientConfig clientConfig = TcpClientConfig.builder()
			.framing(false)
			.bufferSize(65536)
			.readTimeout(Duration.ofSeconds(10))
			.build();
		
		try (TcpServer server = new TcpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), serverConfig)) {
			server.start();
			try (TcpClient client = new TcpClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				client.send(payload);
				
				assertArrayEquals(payload, readUntil(client, payload.length, 65536));
			}
		}
	}
	
	//region Helper methods
	
	private static String hostPartOf(Endpoint endpoint) {
		return switch (endpoint) {
			case HostEndpoint hostEndpoint -> hostEndpoint.hostname();
			case IpEndpoint ipEndpoint -> ipEndpoint.address().toString();
		};
	}
	
	private static byte[] filled(int length, byte value) {
		byte[] data = new byte[length];
		Arrays.fill(data, value);
		return data;
	}
	
	private static TcpServerConfig echoConfig() {
		return echoConfig(true);
	}
	
	private static TcpServerConfig echoConfig(boolean framing) {
		return TcpServerConfig.builder()
			.framing(framing)
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
	}
	
	private static TcpClientConfig readTimeoutConfig() {
		return TcpClientConfig.builder().readTimeout(Duration.ofSeconds(5)).build();
	}
	
	private static byte[] receiveExactly(TcpClient client, int expected, int maxBytes) throws Exception {
		byte[] received = client.receive(maxBytes);
		assertEquals(expected, received.length);
		return received;
	}
	
	private static void withEchoServer(ClientConsumer body) throws Exception {
		withEchoServer(true, body);
	}
	
	private static void withEchoServer(boolean framing, ClientConsumer body) throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (TcpServer server = new TcpServer(endpoint, echoConfig(framing))) {
			server.start();
			
			try (TcpClient client = new TcpClient(framing ? readTimeoutConfig() : unframedClientConfig())) {
				client.connect(server.boundEndpoint());
				body.accept(client);
			}
		}
	}
	
	private static TcpClientConfig unframedClientConfig() {
		return TcpClientConfig.builder().framing(false).readTimeout(Duration.ofSeconds(5)).build();
	}
	
	private static byte[] readUntil(TcpClient client, int expected, int maxBytes) throws Exception {
		ByteArrayOutputStream reassembled = new ByteArrayOutputStream();
		while (reassembled.size() < expected) {
			reassembled.writeBytes(client.receive(maxBytes));
		}
		return reassembled.toByteArray();
	}
	
	private static boolean awaitClientCount(TcpServer server, int expected) throws Exception {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
		while (System.nanoTime() < deadline) {
			if (server.getClientCount() == expected) {
				return true;
			}
			Thread.sleep(20);
		}
		return false;
	}
	
	@FunctionalInterface
	private interface ClientConsumer {
		
		void accept(TcpClient client) throws Exception;
	}
	//endregion
}
