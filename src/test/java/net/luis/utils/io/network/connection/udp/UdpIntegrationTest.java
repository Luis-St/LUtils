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

import net.luis.utils.io.network.HostEndpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
	
	private static final String UNRESOLVABLE_HOSTNAME = "this-host-does-not-exist.invalid";
	
	/**
	 * Returns the address {@code localhost} actually resolves to, on the given port.<br>
	 * Binding to this rather than to a hardcoded {@code 127.0.0.1} keeps the address family
	 * consistent with what a {@code HostEndpoint("localhost", ...)} destination resolves to,
	 * which matters on dual stack hosts where {@code localhost} may map to the IPv6 loopback.<br>
	 */
	private static IpEndpoint resolvedLocalhost(int port) {
		return new HostEndpoint("localhost", port).resolve().orElseThrow();
	}
	
	private static UdpClientConfig receiveTimeoutConfig() {
		return UdpClientConfig.builder().receiveTimeout(Duration.ofSeconds(5)).build();
	}
	
	private static UdpServerConfig echoConfig() {
		return UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				try {
					server.send(datagram.endpoint(), data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
	}
	
	@Test
	void serverStartAndStop() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint)) {
			assertFalse(server.isRunning());
			
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
		try (UdpServer server = new UdpServer(endpoint)) {
			assertInstanceOf(NetworkServer.class, server);
		}
	}
	
	@Test
	void serverSendWithoutStartThrows() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint)) {
			IpEndpoint destination = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> server.send(destination, "data".getBytes()));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		}
	}
	
	@Test
	void clientImplementsNetworkClient() {
		try (UdpClient client = new UdpClient()) {
			assertInstanceOf(NetworkClient.class, client);
		}
	}
	
	@Test
	void clientNotBoundInitially() {
		try (UdpClient client = new UdpClient()) {
			assertFalse(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientBind() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpClient client = new UdpClient()) {
			client.bind(endpoint);
			
			assertTrue(client.isActive());
			assertTrue(client.localEndpoint().isPresent());
		}
	}
	
	@Test
	void clientDoubleBindThrows() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpClient client = new UdpClient()) {
			client.bind(endpoint);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.bind(endpoint));
			
			assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientReceiveWithoutBindThrows() {
		try (UdpClient client = new UdpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientSendWithoutBindCreatesSocket() throws Exception {
		IpEndpoint serverEndpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(serverEndpoint)) {
			server.start();
			IpEndpoint actualServerEndpoint = server.boundEndpoint();
			
			try (UdpClient client = new UdpClient()) {
				assertFalse(client.isActive());
				client.send(actualServerEndpoint, "Test".getBytes());
				assertTrue(client.isActive());
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
	
	@Test
	void clientSendAndReceiveWithAck() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		UdpServerConfig config = UdpServerConfig.builder()
			.executorStrategy(ClientExecutorStrategy.virtualThreads())
			.onMessage((server, datagram, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
				try {
					server.send(datagram.endpoint(), "ACK".getBytes());
				} catch (NetworkConnectionException e) {
					fail("Failed to send response: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint serverEndpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(serverEndpoint, config)) {
			server.start();
			IpEndpoint actualServerEndpoint = server.boundEndpoint();
			
			UdpClientConfig clientConfig = UdpClientConfig.builder()
				.receiveTimeout(Duration.ofSeconds(5))
				.build();
			
			try (UdpClient client = new UdpClient(clientConfig)) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				
				client.send(actualServerEndpoint, "Hello, Server!".getBytes());
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals("Hello, Server!".getBytes(), receivedData.get());
				
				UdpDatagram response = client.receive();
				assertArrayEquals("ACK".getBytes(), response.data());
			}
		}
	}
	
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
	
	@Test
	void udpDatagramSend() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> messageLatch.countDown())
			.build();
		
		IpEndpoint serverEndpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(serverEndpoint, config)) {
			server.start();
			IpEndpoint actualServerEndpoint = server.boundEndpoint();
			
			try (UdpClient client = new UdpClient()) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				
				UdpDatagram datagram = new UdpDatagram(actualServerEndpoint, "Test".getBytes());
				client.send(datagram);
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
			}
		}
	}
	
	@Test
	void serverSendDatagram() throws Exception {
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				try {
					UdpDatagram response = new UdpDatagram(datagram.endpoint(), "Response".getBytes());
					server.send(response);
				} catch (NetworkConnectionException e) {
					fail("Failed to send response: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint serverEndpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(serverEndpoint, config)) {
			server.start();
			IpEndpoint actualServerEndpoint = server.boundEndpoint();
			
			UdpClientConfig clientConfig = UdpClientConfig.builder()
				.receiveTimeout(Duration.ofSeconds(5))
				.build();
			
			try (UdpClient client = new UdpClient(clientConfig)) {
				client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
				
				client.send(actualServerEndpoint, "Request".getBytes());
				
				UdpDatagram response = client.receive();
				assertArrayEquals("Response".getBytes(), response.data());
			}
		}
	}
	
	@Test
	void clientSendThrowsExceptionWhenMessageExceedsBufferSize() throws Exception {
		UdpClientConfig config = UdpClientConfig.builder()
			.bufferSize(100)
			.build();
		
		IpEndpoint serverEndpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 9999);
		
		try (UdpClient client = new UdpClient(config)) {
			client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
			
			byte[] oversizedData = new byte[150];
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(serverEndpoint, oversizedData));
			
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
	
	@Test
	void clientSendWithNullDatagramThrows() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null));
		}
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new UdpClient(null));
	}
	
	@Test
	void clientBindWithNullEndpointThrows() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(NullPointerException.class, () -> client.bind(null));
		}
	}
	
	@Test
	void clientBindToWithNullEndpointThrows() {
		assertThrows(NullPointerException.class, () -> UdpClient.bindTo(null));
		assertThrows(NullPointerException.class, () -> UdpClient.bindTo(null, UdpClientConfig.DEFAULT));
	}
	
	@Test
	void clientBindToWithNullConfigThrows() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		assertThrows(NullPointerException.class, () -> UdpClient.bindTo(endpoint, null));
	}
	
	@Test
	void clientSendWithNullDestinationThrows() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(null, "data".getBytes()));
		}
	}
	
	@Test
	void clientSendWithNullDataThrows() {
		IpEndpoint destination = new IpEndpoint(Ipv4Address.LOOPBACK, 9999);
		try (UdpClient client = new UdpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(destination, null));
		}
	}
	
	@Test
	void clientReceiveWithZeroMaxBytesThrows() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
		}
	}
	
	@Test
	void clientReceiveWithNegativeMaxBytesThrows() {
		try (UdpClient client = new UdpClient()) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(-1));
		}
	}
	
	@Test
	void serverSendWithNullDestinationThrows() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint)) {
			assertThrows(NullPointerException.class, () -> server.send(null, "data".getBytes()));
		}
	}
	
	@Test
	void serverSendWithNullDataThrows() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		IpEndpoint destination = new IpEndpoint(Ipv4Address.LOOPBACK, 9999);
		try (UdpServer server = new UdpServer(endpoint)) {
			assertThrows(NullPointerException.class, () -> server.send(destination, null));
		}
	}
	
	@Test
	void bindToClosesClientWhenBindFails() throws Exception {
		UdpClientConfig config = UdpClientConfig.builder().reuseAddress(false).build();
		
		try (UdpClient occupying = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), config)) {
			IpEndpoint taken = occupying.localEndpoint().orElseThrow();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> UdpClient.bindTo(taken, config));
			assertEquals(NetworkErrorType.ADDRESS_IN_USE, exception.errorType());
			assertEquals(taken, exception.endpoint());
		}
	}
	
	@Test
	void clientRemoteEndpointIsAlwaysEmpty() throws Exception {
		UdpClient client = new UdpClient();
		assertTrue(client.remoteEndpoint().isEmpty());
		
		client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
		assertTrue(client.remoteEndpoint().isEmpty());
		
		client.close();
		assertTrue(client.remoteEndpoint().isEmpty());
	}
	
	@Test
	void serverBoundEndpointBeforeStartReturnsBindEndpoint() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint)) {
			assertEquals(endpoint, server.boundEndpoint());
		}
	}
	
	@Test
	void serverBoundEndpointAfterStartReturnsActualPort() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint)) {
			server.start();
			
			assertNotEquals(0, server.boundEndpoint().port());
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void clientLocalEndpointEmptyBeforeBind() {
		try (UdpClient client = new UdpClient()) {
			assertTrue(client.localEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientLocalEndpointEmptyAfterClose() throws Exception {
		UdpClient client = new UdpClient();
		client.bind(new IpEndpoint(Ipv4Address.LOOPBACK, 0));
		assertTrue(client.localEndpoint().isPresent());
		
		client.close();
		assertTrue(client.localEndpoint().isEmpty());
	}
	
	@Test
	void clientBindToHostEndpoint() throws Exception {
		try (UdpClient client = new UdpClient()) {
			client.bind(new HostEndpoint("localhost", 0));
			
			assertTrue(client.isActive());
			assertTrue(client.localEndpoint().isPresent());
			assertNotEquals(0, client.localEndpoint().orElseThrow().port());
		}
	}
	
	@Test
	void clientBindToWithHostEndpoint() throws Exception {
		try (UdpClient client = UdpClient.bindTo(new HostEndpoint("localhost", 0))) {
			assertTrue(client.isActive());
			assertTrue(client.localEndpoint().isPresent());
		}
	}
	
	@Test
	void clientLocalEndpointIsIpEndpointWhenBoundByName() throws Exception {
		try (UdpClient client = UdpClient.bindTo(new HostEndpoint("localhost", 0))) {
			assertInstanceOf(IpEndpoint.class, client.localEndpoint().orElseThrow());
		}
	}
	
	@Test
	void clientSendDatagramDelegatesToEndpointAndData() throws Exception {
		UdpClientConfig config = UdpClientConfig.builder().receiveTimeout(Duration.ofSeconds(5)).build();
		
		try (UdpClient receiver = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), config);
			 UdpClient sender = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), config)) {
			IpEndpoint target = receiver.localEndpoint().orElseThrow();
			
			sender.send(new UdpDatagram(target, "Hello".getBytes()));
			
			UdpDatagram received = receiver.receive();
			assertArrayEquals("Hello".getBytes(), received.data());
			assertEquals(sender.localEndpoint().orElseThrow().port(), received.endpoint().port());
		}
	}
	
	@Test
	void clientUsableThroughNetworkClientInterface() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, echoConfig())) {
			server.start();
			UdpClientConfig config = UdpClientConfig.builder().receiveTimeout(Duration.ofSeconds(5)).build();
			
			try (NetworkClient<UdpDatagram> client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), config)) {
				assertTrue(client.isActive());
				assertTrue(client.localEndpoint().isPresent());
				assertTrue(client.remoteEndpoint().isEmpty());
				
				client.send(new UdpDatagram(server.boundEndpoint(), "Hello".getBytes()));
				assertArrayEquals("Hello".getBytes(), client.receive(1024).data());
			}
		}
	}
	
	@Test
	void datagramRoundTripThroughInterfaceSend() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, echoConfig())) {
			server.start();
			UdpClientConfig config = UdpClientConfig.builder().receiveTimeout(Duration.ofSeconds(5)).build();
			
			try (NetworkClient<UdpDatagram> client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), config)) {
				client.send(new UdpDatagram(server.boundEndpoint(), "ping".getBytes()));
				
				UdpDatagram response = client.receive();
				assertArrayEquals("ping".getBytes(), response.data());
				assertEquals(server.boundEndpoint().port(), response.endpoint().port());
				
				client.send(new UdpDatagram(response.endpoint(), response.data()));
				assertArrayEquals("ping".getBytes(), client.receive().data());
			}
		}
	}
	
	@Test
	void serverSendValidatesMessageSizeThroughNetworkUtils() throws Exception {
		AtomicReference<NetworkConnectionException> exceptionRef = new AtomicReference<>();
		AtomicReference<Boolean> atLimitSucceeded = new AtomicReference<>(false);
		CountDownLatch messageLatch = new CountDownLatch(1);
		
		UdpServerConfig config = UdpServerConfig.builder()
			.bufferSize(100)
			.onMessage((server, datagram, data) -> {
				try {
					server.send(datagram.endpoint(), new byte[101]);
				} catch (NetworkConnectionException e) {
					exceptionRef.set(e);
				}
				try {
					server.send(datagram.endpoint(), new byte[100]);
					atLimitSucceeded.set(true);
				} catch (NetworkConnectionException e) {
					fail("At-limit send failed: " + e.getMessage());
				}
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0))) {
				IpEndpoint destination = server.boundEndpoint();
				client.send(destination, "trigger".getBytes());
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exceptionRef.get().errorType());
				assertEquals(client.localEndpoint().orElseThrow(), exceptionRef.get().endpoint());
				assertTrue(atLimitSucceeded.get());
			}
		}
	}
	
	@Test
	void clientAndServerBoundEndpointsAgree() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<IpEndpoint> sourceRef = new AtomicReference<>();
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				sourceRef.set(datagram.endpoint());
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (UdpServer server = new UdpServer(endpoint, config)) {
			server.start();
			
			try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0))) {
				client.send(server.boundEndpoint(), "hello".getBytes());
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertEquals(client.localEndpoint().orElseThrow().port(), sourceRef.get().port());
			}
		}
	}
	
	@Test
	void clientSendToUnresolvableHostEndpointThrows() throws Exception {
		HostEndpoint destination = new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999);
		
		try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0))) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send(destination, "data".getBytes()));
			
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
			assertSame(destination, exception.endpoint());
			assertInstanceOf(UnknownHostException.class, exception.getCause());
		}
	}
	
	@Test
	void serverSendToUnresolvableHostEndpointThrows() {
		HostEndpoint destination = new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999);
		
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0))) {
			server.start();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> server.send(destination, "data".getBytes()));
			
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
			assertSame(destination, exception.endpoint());
			assertInstanceOf(UnknownHostException.class, exception.getCause());
		}
	}
	
	@Test
	void clientSendToUnresolvableHostNotifiesErrorHandler() throws Exception {
		AtomicReference<NetworkErrorType> capturedType = new AtomicReference<>();
		AtomicReference<Throwable> capturedCause = new AtomicReference<>();
		AtomicBoolean invokedTwice = new AtomicBoolean(false);
		
		UdpClientConfig config = UdpClientConfig.builder()
			.onError((connection, type, message, cause) -> {
				invokedTwice.set(capturedType.get() != null);
				capturedType.set(type);
				capturedCause.set(cause);
			})
			.build();
		
		try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), config)) {
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> client.send(new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999), "data".getBytes())
			);
			
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, capturedType.get());
			assertSame(exception.getCause(), capturedCause.get());
			assertFalse(invokedTwice.get());
		}
	}
	
	@Test
	void serverSendToUnresolvableHostNotifiesErrorHandler() {
		AtomicReference<NetworkErrorType> capturedType = new AtomicReference<>();
		AtomicReference<Throwable> capturedCause = new AtomicReference<>();
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onError((connection, type, message, cause) -> {
				capturedType.set(type);
				capturedCause.set(cause);
			})
			.build();
		
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), config)) {
			server.start();
			
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> server.send(new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999), "data".getBytes())
			);
			
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, capturedType.get());
			assertSame(exception.getCause(), capturedCause.get());
		}
	}
	
	@Test
	void clientSendResolveFailureCauseIdentifiesHost() throws Exception {
		try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0))) {
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> client.send(new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999), "data".getBytes())
			);
			
			assertEquals(UNRESOLVABLE_HOSTNAME, exception.getCause().getMessage());
		}
	}
	
	@Test
	void serverSendResolveFailureCauseIdentifiesHost() {
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0))) {
			server.start();
			
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> server.send(new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999), "data".getBytes())
			);
			
			assertEquals(UNRESOLVABLE_HOSTNAME, exception.getCause().getMessage());
		}
	}
	
	@Test
	void clientSendToResolvableHostEndpoint() throws Exception {
		try (UdpServer server = new UdpServer(resolvedLocalhost(0), echoConfig())) {
			server.start();
			HostEndpoint destination = new HostEndpoint("localhost", server.boundEndpoint().port());
			
			try (UdpClient client = UdpClient.bindTo(resolvedLocalhost(0), receiveTimeoutConfig())) {
				client.send(destination, "Hello".getBytes());
				
				assertArrayEquals("Hello".getBytes(), client.receive().data());
			}
		}
	}
	
	@Test
	void serverSendToResolvableHostEndpoint() throws Exception {
		AtomicReference<Integer> clientPort = new AtomicReference<>();
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				try {
					server.send(new HostEndpoint("localhost", clientPort.get()), data);
				} catch (NetworkConnectionException e) {
					fail("Reply failed: " + e.getMessage());
				}
			})
			.build();
		
		try (UdpServer server = new UdpServer(resolvedLocalhost(0), config)) {
			server.start();
			
			try (UdpClient client = UdpClient.bindTo(resolvedLocalhost(0), receiveTimeoutConfig())) {
				clientPort.set(client.localEndpoint().orElseThrow().port());
				client.send(server.boundEndpoint(), "Ping".getBytes());
				
				assertArrayEquals("Ping".getBytes(), client.receive().data());
			}
		}
	}
	
	@Test
	void clientSendToIpEndpointStillWorks() throws Exception {
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), echoConfig())) {
			server.start();
			
			try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), receiveTimeoutConfig())) {
				client.send(server.boundEndpoint(), "Hello".getBytes());
				
				assertArrayEquals("Hello".getBytes(), client.receive().data());
			}
		}
	}
	
	@Test
	void serverSendToIpEndpointStillWorks() throws Exception {
		AtomicReference<IpEndpoint> source = new AtomicReference<>();
		
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				source.set(datagram.endpoint());
				try {
					server.send(datagram.endpoint(), data);
				} catch (NetworkConnectionException e) {
					fail("Reply failed: " + e.getMessage());
				}
			})
			.build();
		
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), config)) {
			server.start();
			
			try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), receiveTimeoutConfig())) {
				client.send(server.boundEndpoint(), "Pong".getBytes());
				
				assertArrayEquals("Pong".getBytes(), client.receive().data());
				assertEquals(client.localEndpoint().orElseThrow().port(), source.get().port());
			}
		}
	}
	
	@Test
	void clientSendOversizedToUnresolvableHostReportsSizeFirst() {
		UdpClientConfig config = UdpClientConfig.builder().bufferSize(100).build();
		
		try (UdpClient client = new UdpClient(config)) {
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> client.send(new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999), new byte[101])
			);
			
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void serverSendToUnresolvableHostWhenNotRunningReportsStateFirst() {
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0))) {
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> server.send(new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999), "data".getBytes())
			);
			
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		}
	}
	
	@Test
	void clientSendWithNullDataToUnresolvableHostThrows() {
		HostEndpoint destination = new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999);
		
		try (UdpClient client = new UdpClient()) {
			assertThrows(NullPointerException.class, () -> client.send(destination, null));
		}
	}
	
	@Test
	void clientSendCreatesSocketBeforeResolveFailure() {
		try (UdpClient client = new UdpClient()) {
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> client.send(new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999), "data".getBytes())
			);
			
			assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
			assertTrue(client.isActive());
		}
	}
	
	@Test
	void clientSendDatagramWithIpEndpointStillDelegates() throws Exception {
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), echoConfig())) {
			server.start();
			
			try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), receiveTimeoutConfig())) {
				client.send(new UdpDatagram(server.boundEndpoint(), "Hello".getBytes()));
				
				assertArrayEquals("Hello".getBytes(), client.receive().data());
			}
		}
	}
	
	//region Helper methods
	
	@Test
	void serverSendDatagramWithIpEndpointStillDelegates() throws Exception {
		UdpServerConfig config = UdpServerConfig.builder()
			.onMessage((server, datagram, data) -> {
				try {
					server.send(new UdpDatagram(datagram.endpoint(), data));
				} catch (NetworkConnectionException e) {
					fail("Reply failed: " + e.getMessage());
				}
			})
			.build();
		
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), config)) {
			server.start();
			
			try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), receiveTimeoutConfig())) {
				client.send(server.boundEndpoint(), "Echo".getBytes());
				
				assertArrayEquals("Echo".getBytes(), client.receive().data());
			}
		}
	}
	
	@Test
	void clientSendMixesHostAndIpDestinations() throws Exception {
		try (UdpServer server = new UdpServer(resolvedLocalhost(0), echoConfig())) {
			server.start();
			int port = server.boundEndpoint().port();
			HostEndpoint byName = new HostEndpoint("localhost", port);
			IpEndpoint byAddress = server.boundEndpoint();
			
			try (UdpClient client = UdpClient.bindTo(resolvedLocalhost(0), receiveTimeoutConfig())) {
				for (int i = 0; i < 2; i++) {
					client.send(byName, "name".getBytes());
					assertArrayEquals("name".getBytes(), client.receive().data());
					
					client.send(byAddress, "addr".getBytes());
					assertArrayEquals("addr".getBytes(), client.receive().data());
				}
			}
		}
	}
	
	@Test
	void clientSendRecoversAfterResolveFailure() throws Exception {
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), echoConfig())) {
			server.start();
			
			try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), receiveTimeoutConfig())) {
				assertThrows(NetworkConnectionException.class, () -> client.send(new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999), "lost".getBytes()));
				
				client.send(server.boundEndpoint(), "Hello".getBytes());
				assertArrayEquals("Hello".getBytes(), client.receive().data());
			}
		}
	}
	
	@Test
	void serverSendRecoversAfterResolveFailure() throws Exception {
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0))) {
			server.start();
			
			assertThrows(NetworkConnectionException.class, () -> server.send(new HostEndpoint(UNRESOLVABLE_HOSTNAME, 9999), "lost".getBytes()));
			assertTrue(server.isRunning());
			
			try (UdpClient client = UdpClient.bindTo(new IpEndpoint(Ipv4Address.LOOPBACK, 0), receiveTimeoutConfig())) {
				server.send(client.localEndpoint().orElseThrow(), "Hello".getBytes());
				
				assertArrayEquals("Hello".getBytes(), client.receive().data());
			}
		}
	}
	//endregion
}
