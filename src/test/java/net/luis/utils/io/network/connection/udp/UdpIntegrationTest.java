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

package net.luis.utils.io.network.connection.udp;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UDP client and server communication.<br>
 * Tests message encoding/decoding, data integrity, and various communication scenarios.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class UdpIntegrationTest {
	
	//region Binary Data Tests
	@Test
	void sendAndReceiveBinaryData() throws Exception {
		byte[] binaryData = new byte[256];
		for (int i = 0; i < 256; i++) {
			binaryData[i] = (byte) i;
		}
		
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			try (UdpClient client = new UdpClient()) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				client.send(server.boundEndpoint(), binaryData);
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals(binaryData, receivedData.get());
			}
		}
	}
	
	@Test
	void sendAndReceiveSingleByte() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			try (UdpClient client = new UdpClient()) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				client.send(server.boundEndpoint(), new byte[] { 42 });
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals(new byte[] { 42 }, receivedData.get());
			}
		}
	}
	//endregion
	
	//region Echo Round-Trip Tests
	@Test
	void echoServerRoundTrip() throws Exception {
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				try {
					server.send(datagram.endpoint(), data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			UdpClientConfig clientConfig = UdpClientConfig.builder()
				.receiveTimeout(Duration.ofSeconds(5))
				.build();
			
			try (UdpClient client = new UdpClient(clientConfig)) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				
				byte[] original = "Hello, UDP Echo!".getBytes();
				client.send(server.boundEndpoint(), original);
				
				UdpDatagram response = client.receive();
				assertArrayEquals(original, response.data());
			}
		}
	}
	
	@Test
	void echoServerRoundTripWithBinaryData() throws Exception {
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				try {
					server.send(datagram.endpoint(), data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed");
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			UdpClientConfig clientConfig = UdpClientConfig.builder()
				.receiveTimeout(Duration.ofSeconds(5))
				.build();
			
			try (UdpClient client = new UdpClient(clientConfig)) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				
				byte[] original = { 0, 127, -128, -1, 64, 32, 16, 8, 4, 2, 1 };
				client.send(server.boundEndpoint(), original);
				
				UdpDatagram response = client.receive();
				assertArrayEquals(original, response.data());
			}
		}
	}
	//endregion
	
	//region Multiple Datagram Tests
	@Test
	void sendAndReceiveMultipleDatagrams() throws Exception {
		int messageCount = 10;
		CountDownLatch allReceived = new CountDownLatch(messageCount);
		Set<String> receivedMessages = Collections.synchronizedSet(new HashSet<>());
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				receivedMessages.add(new String(data));
				allReceived.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			try (UdpClient client = new UdpClient()) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				
				for (int i = 0; i < messageCount; i++) {
					client.send(server.boundEndpoint(), ("Message " + i).getBytes());
				}
				
				assertTrue(allReceived.await(5, TimeUnit.SECONDS));
				assertEquals(messageCount, receivedMessages.size());
			}
		}
	}
	
	@Test
	void sendAndReceiveMultipleRoundTrips() throws Exception {
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				try {
					server.send(datagram.endpoint(), data);
				} catch (NetworkConnectionException e) {
					fail("Send failed");
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			UdpClientConfig clientConfig = UdpClientConfig.builder()
				.receiveTimeout(Duration.ofSeconds(5))
				.build();
			
			try (UdpClient client = new UdpClient(clientConfig)) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				
				for (int i = 0; i < 10; i++) {
					byte[] message = ("Message " + i).getBytes();
					client.send(server.boundEndpoint(), message);
					UdpDatagram response = client.receive();
					assertArrayEquals(message, response.data());
				}
			}
		}
	}
	//endregion
	
	//region Special Character Tests
	@Test
	void sendAndReceiveUtf8SpecialCharacters() throws Exception {
		String specialChars = "Hello \u4e16\u754c! \u041f\u0440\u0438\u0432\u0435\u0442 \u043c\u0438\u0440! \u0645\u0631\u062d\u0628\u0627 \u0627\u0644\u0639\u0627\u0644\u0645";
		byte[] data = specialChars.getBytes(StandardCharsets.UTF_8);
		
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, d) -> {
				receivedData.set(d);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			try (UdpClient client = new UdpClient()) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				client.send(server.boundEndpoint(), data);
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				String received = new String(receivedData.get(), StandardCharsets.UTF_8);
				assertEquals(specialChars, received);
			}
		}
	}
	//endregion
	
	//region Timeout Tests
	@Test
	void receiveTimeoutExpires() throws Exception {
		UdpClientConfig config = UdpClientConfig.builder()
			.receiveTimeout(Duration.ofMillis(500))
			.build();
		
		try (UdpClient client = new UdpClient(config)) {
			client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
		}
	}
	//endregion
	
	//region Multiple Clients Tests
	@Test
	void multipleClientsSendToServer() throws Exception {
		int clientCount = 5;
		CountDownLatch allReceived = new CountDownLatch(clientCount);
		Set<String> receivedMessages = Collections.synchronizedSet(new HashSet<>());
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				receivedMessages.add(new String(data));
				allReceived.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			List<UdpClient> clients = new ArrayList<>();
			try {
				for (int i = 0; i < clientCount; i++) {
					UdpClient client = new UdpClient();
					client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
					clients.add(client);
				}
				
				for (int i = 0; i < clientCount; i++) {
					clients.get(i).send(server.boundEndpoint(), ("Client " + i).getBytes());
				}
				
				assertTrue(allReceived.await(5, TimeUnit.SECONDS));
				assertEquals(clientCount, receivedMessages.size());
			} finally {
				for (UdpClient client : clients) {
					client.close();
				}
			}
		}
	}
	//endregion
	
	//region Datagram Source Verification Tests
	@Test
	void datagramContainsCorrectSourceEndpoint() throws Exception {
		AtomicReference<IpEndpoint> capturedSource = new AtomicReference<>();
		CountDownLatch messageLatch = new CountDownLatch(1);
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				capturedSource.set(datagram.endpoint());
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			try (UdpClient client = new UdpClient()) {
				IpEndpoint clientEndpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
				client.bind(clientEndpoint);
				
				IpEndpoint actualClientEndpoint = client.localEndpoint().orElseThrow();
				client.send(server.boundEndpoint(), "test".getBytes());
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertEquals(actualClientEndpoint, capturedSource.get());
			}
		}
	}
	//endregion
	
	//region Message Size Validation Tests
	@Test
	void clientSendThrowsExceptionWhenMessageExceedsBufferSize() throws Exception {
		UdpClientConfig config = UdpClientConfig.builder()
			.bufferSize(100)
			.build();
		
		IpEndpoint serverEndpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 9999);
		
		try (UdpClient client = new UdpClient(config)) {
			client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
			
			byte[] oversizedData = new byte[150];
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class,
				() -> client.send(serverEndpoint, oversizedData));
			
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertTrue(exception.getMessage().contains("150"));
			assertTrue(exception.getMessage().contains("100"));
		}
	}
	
	@Test
	void clientSendSucceedsWhenMessageEqualsBufferSize() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		UdpServerConfig serverConfig = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, serverConfig)) {
			server.start();
			
			UdpClientConfig clientConfig = UdpClientConfig.builder()
				.bufferSize(100)
				.build();
			
			try (UdpClient client = new UdpClient(clientConfig)) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				
				byte[] exactSizeData = new byte[100];
				Arrays.fill(exactSizeData, (byte) 42);
				client.send(server.boundEndpoint(), exactSizeData);
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals(exactSizeData, receivedData.get());
			}
		}
	}
	
	@Test
	void serverSendThrowsExceptionWhenMessageExceedsBufferSize() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<NetworkConnectionException> exceptionRef = new AtomicReference<>();
		
		UdpServerConfig config = UdpServerConfig.builder()
			.bufferSize(100)
			.onMessage((server, datagram, data) -> {
				try {
					byte[] oversizedData = new byte[150];
					server.send(datagram.endpoint(), oversizedData);
				} catch (NetworkConnectionException e) {
					exceptionRef.set(e);
				}
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			try (UdpClient client = new UdpClient()) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				client.send(server.boundEndpoint(), "trigger".getBytes());
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertNotNull(exceptionRef.get());
				assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exceptionRef.get().errorType());
			}
		}
	}
	//endregion
}
