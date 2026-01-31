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
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
			.onClientConnect(event -> clientConnected.countDown())
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
	
	@RepeatedTest(2)
	void errorHandlerDoesNotReceiveIOError() throws Exception {
		CountDownLatch errorLatch = new CountDownLatch(1);
		
		TcpServerConfig config = TcpServerConfig.builder()
			.onError((errorType, message, cause) -> errorLatch.countDown())
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
			.onClientConnect(event -> allConnected.countDown())
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
			.onClientConnect(event -> clientsConnected.countDown())
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
			.onClientConnect(event -> clientConnected.countDown())
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
			.onClientConnect(event -> connectLatch.countDown())
			.onClientDisconnect(event -> disconnectLatch.countDown())
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
				.onConnect(event -> connectLatch.countDown())
				.onDisconnect(event -> disconnectLatch.countDown())
				.build();
			
			try (TcpClient client = new TcpClient(config)) {
				client.connect(serverEndpoint);
				assertTrue(connectLatch.await(5, TimeUnit.SECONDS));
			}
			
			assertTrue(disconnectLatch.await(5, TimeUnit.SECONDS));
		}
	}
}
