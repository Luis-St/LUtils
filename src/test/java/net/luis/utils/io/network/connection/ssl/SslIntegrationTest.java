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

import net.luis.utils.io.network.*;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for SSL client and server communication.<br>
 * Tests the TLS handshake, encrypted data integrity, hostname verification, and mutual TLS.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SslIntegrationTest {
	
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SslTestContext.serverContext();
		clientContext = SslTestContext.clientContext();
	}
	
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
	
	private static byte[] receiveExactly(SslClient client, int expected, int maxBytes) throws Exception {
		byte[] received = client.receive(maxBytes);
		assertEquals(expected, received.length);
		return received;
	}
	
	private static void withNonTlsPeer(PeerConsumer body) throws Exception {
		try (ServerSocket peer = new ServerSocket(0, 0, InetAddress.getLoopbackAddress())) {
			Thread acceptor = new Thread(() -> {
				try (Socket accepted = peer.accept()) {
					accepted.getOutputStream().write("NOT-TLS\n".getBytes());
					accepted.getOutputStream().flush();
					Thread.sleep(5000);
				} catch (Exception _) {}
			});
			acceptor.setDaemon(true);
			acceptor.start();
			
			body.accept(new IpEndpoint(Ipv4Address.LOOPBACK, peer.getLocalPort()));
			acceptor.interrupt();
		}
	}
	
	private static byte[] readUntil(SslClient client, int expected, int maxBytes) throws Exception {
		ByteArrayOutputStream reassembled = new ByteArrayOutputStream();
		while (reassembled.size() < expected) {
			reassembled.writeBytes(client.receive(maxBytes));
		}
		return reassembled.toByteArray();
	}
	
	private static boolean awaitClientCount(SslServer server, int expected) throws Exception {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
		while (System.nanoTime() < deadline) {
			if (server.getClientCount() == expected) {
				return true;
			}
			Thread.sleep(20);
		}
		return false;
	}
	
	@Test
	void serverStartAndStop() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
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
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			assertInstanceOf(NetworkServer.class, server);
		}
	}
	
	@Test
	void clientImplementsNetworkClient() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			assertInstanceOf(NetworkClient.class, client);
		}
	}
	
	@Test
	void clientNotConnectedInitially() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			assertFalse(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientConnectToServer() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			try (SslClient client = new SslClient(this.clientConfig().build())) {
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
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 59998);
		
		SslClientConfig config = this.clientConfig().connectTimeout(Duration.ofSeconds(2)).build();
		
		try (SslClient client = new SslClient(config)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(endpoint));
			assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
		}
	}
	
	@Test
	void clientSendWithoutConnectThrows() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send("data".getBytes()));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientReceiveWithoutConnectThrows() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientDoubleConnectThrows() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			try (SslClient client = new SslClient(this.clientConfig().build())) {
				client.connect(serverEndpoint);
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(serverEndpoint));
				assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
			}
		}
	}
	
	@Test
	void clientGetSessionAfterConnect() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().build())) {
				client.connect(server.boundEndpoint());
				
				SSLSession session = client.getSession();
				assertNotNull(session);
				assertTrue(session.getProtocol().startsWith("TLS"));
				assertTrue(session.isValid());
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
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().build())) {
				client.connect(server.boundEndpoint());
				client.send(binaryData);
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals(binaryData, receivedData.get());
			}
		}
	}
	
	@Test
	void echoServerRoundTrip() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build())) {
				client.connect(server.boundEndpoint());
				
				byte[] original = "Hello, Echo Server!".getBytes();
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
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().readTimeout(Duration.ofSeconds(10)).build())) {
				client.connect(server.boundEndpoint());
				client.send(largeData);
				
				byte[] received = client.receive();
				assertArrayEquals(largeData, received);
			}
		}
	}
	
	@Test
	void sendAndReceiveMultipleRoundTrips() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Send failed");
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build())) {
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
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().readTimeout(Duration.ofMillis(500)).build())) {
				client.connect(server.boundEndpoint());
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
				assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
			}
		}
	}
	
	@Test
	void serverEventHandlers() throws Exception {
		CountDownLatch connectLatch = new CountDownLatch(1);
		CountDownLatch disconnectLatch = new CountDownLatch(1);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onClientConnect((connection, local, remote, timestamp) -> connectLatch.countDown())
			.onClientDisconnect((connection, local, remote, timestamp) -> disconnectLatch.countDown())
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			SslClient client = new SslClient(this.clientConfig().build());
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
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			SslClientConfig config = this.clientConfig()
				.onConnect((connection, local, remote, timestamp) -> connectLatch.countDown())
				.onDisconnect((connection, local, remote, timestamp) -> disconnectLatch.countDown())
				.build();
			
			try (SslClient client = new SslClient(config)) {
				client.connect(serverEndpoint);
				assertTrue(connectLatch.await(5, TimeUnit.SECONDS));
			}
			
			assertTrue(disconnectLatch.await(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void multipleClientsConnectAndSendSimultaneously() throws Exception {
		int clientCount = 5;
		CountDownLatch allConnected = new CountDownLatch(clientCount);
		CountDownLatch allMessagesReceived = new CountDownLatch(clientCount);
		Set<String> receivedMessages = Collections.synchronizedSet(new HashSet<>());
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onClientConnect((connection, local, remote, timestamp) -> allConnected.countDown())
			.onMessage((server, conn, data) -> {
				receivedMessages.add(new String(data));
				allMessagesReceived.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			List<SslClient> clients = new ArrayList<>();
			try {
				for (int i = 0; i < clientCount; i++) {
					SslClient client = new SslClient(this.clientConfig().build());
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
				for (SslClient client : clients) {
					client.close();
				}
			}
		}
	}
	
	@Test
	void serverBroadcast() throws Exception {
		CountDownLatch clientsConnected = new CountDownLatch(2);
		CountDownLatch messagesReceived = new CountDownLatch(2);
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onClientConnect((connection, local, remote, timestamp) -> clientsConnected.countDown())
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			try (SslClient client1 = new SslClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build());
				 SslClient client2 = new SslClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build())) {
				
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
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().bufferSize(100).build())) {
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
	void clientReceiveWithMaxBytesSmallerThanFrameThrows() throws Exception {
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				try {
					conn.send("Hello World".getBytes());
				} catch (NetworkConnectionException e) {
					fail("Failed to send response: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build())) {
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
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectionRef.set(connection);
				clientConnected.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build())) {
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
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectionRef.set(connection);
				clientConnected.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			SslClientConfig clientConfig = this.clientConfig()
				.readTimeout(Duration.ofSeconds(5))
				.onDisconnect((connection, local, remote, timestamp) -> disconnectLatch.countDown())
				.build();
			
			try (SslClient client = new SslClient(clientConfig)) {
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
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().build())) {
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
	
	@Test
	void hostnameVerificationSucceedsForLoopback() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			SslClientConfig config = SslClientConfig.builder()
				.sslContext(clientContext)
				.verifyHostname(true)
				.build();
			
			try (SslClient client = new SslClient(config)) {
				assertDoesNotThrow(() -> client.connect(server.boundEndpoint()));
				assertTrue(client.isActive());
			}
		}
	}
	
	@Test
	void handshakeFailsWithUntrustedServer() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			SslClientConfig config = SslClientConfig.builder()
				.verifyHostname(false)
				.build();
			
			try (SslClient client = new SslClient(config)) {
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(server.boundEndpoint()));
				assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			}
		}
	}
	
	@Test
	void mutualTlsWithRequiredClientAuthSucceeds() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.clientAuth(SslClientAuth.REQUIRED)
			.onMessage((server, conn, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().build())) {
				client.connect(server.boundEndpoint());
				client.send("mtls".getBytes());
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals("mtls".getBytes(), receivedData.get());
			}
		}
	}
	
	@Test
	void mutualTlsRejectsClientWithoutCertificate() throws Exception {
		SSLContext trustOnly = SslTestContext.trustOnlyClientContext();
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.enabledProtocols(List.of("TLSv1.2"))
			.clientAuth(SslClientAuth.REQUIRED)
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			SslClientConfig clientConfig = SslClientConfig.builder()
				.sslContext(trustOnly)
				.enabledProtocols(List.of("TLSv1.2"))
				.verifyHostname(false)
				.build();
			
			try (SslClient client = new SslClient(clientConfig)) {
				assertThrows(NetworkConnectionException.class, () -> client.connect(server.boundEndpoint()));
				assertFalse(client.isActive());
			}
		}
	}
	
	@Test
	void serverClientConnectEventProvidesConnection() throws Exception {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<Connection> connectionRef = new AtomicReference<>();
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectionRef.set(connection);
				latch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().build())) {
				client.connect(server.boundEndpoint());
				assertTrue(latch.await(5, TimeUnit.SECONDS));
				
				assertNotNull(connectionRef.get());
				assertInstanceOf(SslConnection.class, connectionRef.get());
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
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
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
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			SslClient client = new SslClient(this.clientConfig().build());
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
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			SslClientConfig config = this.clientConfig()
				.onConnect((connection, local, remote, timestamp) -> {
					connectionRef.set(connection);
					connectLatch.countDown();
				})
				.build();
			
			try (SslClient client = new SslClient(config)) {
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
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			SslClientConfig config = this.clientConfig()
				.onConnect((connection, local, remote, timestamp) -> connectLatch.countDown())
				.onDisconnect((connection, local, remote, timestamp) -> {
					connectionRef.set(connection);
					disconnectLatch.countDown();
				})
				.build();
			
			try (SslClient client = new SslClient(config)) {
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
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.clientBufferSize(50)
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectedConnection.set(connection);
				clientConnected.countDown();
			})
			.onError(onError)
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().build())) {
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
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 59997);
		SslClientConfig config = this.clientConfig()
			.connectTimeout(Duration.ofSeconds(2))
			.onError(onError)
			.build();
		
		try (SslClient client = new SslClient(config)) {
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
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
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
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().build())) {
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
		
		SslServerConfig config = SslServerConfig.builder(serverContext)
			.onClientConnect((connection, local, remote, timestamp) -> {
				connectedConnection.set(connection);
				clientConnected.countDown();
			})
			.onError(onError)
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, config)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			try (Socket rawTcpSocket = new Socket()) {
				rawTcpSocket.connect(serverEndpoint.toInetSocketAddress());
				
				SSLSocket sslSocket = (SSLSocket) clientContext.getSocketFactory().createSocket(rawTcpSocket, serverEndpoint.address().toString(), serverEndpoint.port(), false);
				sslSocket.startHandshake();
				assertTrue(clientConnected.await(5, TimeUnit.SECONDS));
				
				rawTcpSocket.setSoLinger(true, 0);
				rawTcpSocket.close();
				
				assertTrue(errorLatch.await(5, TimeUnit.SECONDS));
				assertNotNull(errorConnection.get());
				assertSame(connectedConnection.get(), errorConnection.get());
			}
		}
	}
	
	@Test
	void unframedClientSendAndReceive() throws Exception {
		this.withEchoServer(false, client -> {
			byte[] payload = "Hello".getBytes();
			client.send(payload);
			
			assertArrayEquals(payload, readUntil(client, payload.length, 8192));
		});
	}
	
	@Test
	void framedClientSendAndReceive() throws Exception {
		this.withEchoServer(true, client -> {
			byte[] payload = "Hello".getBytes();
			client.send(payload);
			
			byte[] received = client.receive();
			assertEquals(payload.length, received.length);
			assertArrayEquals(payload, received);
		});
	}
	
	@Test
	void unframedClientReceiveRespectsMaxBytes() throws Exception {
		this.withEchoServer(false, client -> {
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
		SslServerConfig serverConfig = SslServerConfig.builder(serverContext)
			.framing(false)
			.onMessage((server, conn, data) -> conn.close())
			.build();
		SslClientConfig clientConfig = this.clientConfig()
			.framing(false)
			.readTimeout(Duration.ofSeconds(5))
			.onDisconnect((connection, local, remote, timestamp) -> disconnects.incrementAndGet())
			.build();
		
		try (SslServer server = new SslServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), serverConfig)) {
			server.start();
			try (SslClient client = new SslClient(clientConfig)) {
				client.connect(server.boundEndpoint());
				client.send("Hi".getBytes());
				
				assertEquals(0, client.receive().length);
				assertFalse(client.isActive());
				assertEquals(1, disconnects.get());
			}
		}
	}
	
	@Test
	void unframedServerReceivesClientMessages() throws Exception {
		byte[] payload = "Hello".getBytes();
		ByteArrayOutputStream delivered = new ByteArrayOutputStream();
		CountDownLatch complete = new CountDownLatch(1);
		SslServerConfig serverConfig = SslServerConfig.builder(serverContext)
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
		
		try (SslServer server = new SslServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), serverConfig)) {
			server.start();
			try (SslClient client = new SslClient(this.clientConfig().framing(false).readTimeout(Duration.ofSeconds(5)).build())) {
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
		SslServerConfig serverConfig = SslServerConfig.builder(serverContext)
			.framing(false)
			.onClientDisconnect((connection, local, remote, timestamp) -> disconnected.countDown())
			.build();
		
		try (SslServer server = new SslServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), serverConfig)) {
			server.start();
			SslClient client = new SslClient(this.clientConfig().framing(false).readTimeout(Duration.ofSeconds(5)).build());
			client.connect(server.boundEndpoint());
			client.close();
			
			assertTrue(disconnected.await(10, TimeUnit.SECONDS));
			assertTrue(awaitClientCount(server, 0));
		}
	}
	
	@Test
	void unframedRoundTripLosesMessageBoundaries() throws Exception {
		this.withEchoServer(false, client -> {
			client.send("AAA".getBytes());
			client.send("BBB".getBytes());
			
			assertArrayEquals("AAABBB".getBytes(), readUntil(client, 6, 8192));
		});
	}
	
	@Test
	void framedRoundTripKeepsMessageBoundaries() throws Exception {
		this.withEchoServer(true, client -> {
			client.send("AAA".getBytes());
			client.send("BBB".getBytes());
			
			assertArrayEquals("AAA".getBytes(), client.receive());
			assertArrayEquals("BBB".getBytes(), client.receive());
		});
	}
	
	@Test
	void unframedClientReusesScratchBufferAcrossReceives() throws Exception {
		this.withEchoServer(false, client -> {
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
	void unframedLargeMessageSpansMultipleTlsRecords() throws Exception {
		byte[] payload = filled(65536, (byte) 0x42);
		SslServerConfig serverConfig = SslServerConfig.builder(serverContext)
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
		
		try (SslServer server = new SslServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), serverConfig)) {
			server.start();
			try (SslClient client = new SslClient(this.clientConfig().framing(false).bufferSize(65536).readTimeout(Duration.ofSeconds(10)).build())) {
				client.connect(server.boundEndpoint());
				client.send(payload);
				
				assertArrayEquals(payload, readUntil(client, payload.length, 65536));
			}
		}
	}
	
	@Test
	void framedServerWithUnframedClientDoesNotInteroperate() throws Exception {
		byte[] payload = "Hello".getBytes();
		SslServerConfig serverConfig = SslServerConfig.builder(serverContext)
			.framing(true)
			.onClientConnect((connection, local, remote, timestamp) -> {
				try {
					connection.send(payload);
				} catch (NetworkConnectionException e) {
					fail("Server send failed: " + e.getMessage());
				}
			})
			.build();
		
		try (SslServer server = new SslServer(new IpEndpoint(Ipv4Address.LOOPBACK, 0), serverConfig)) {
			server.start();
			try (SslClient client = new SslClient(this.clientConfig().framing(false).readTimeout(Duration.ofSeconds(5)).build())) {
				client.connect(server.boundEndpoint());
				
				byte[] received = readUntil(client, payload.length + 4, 8192);
				assertEquals(payload.length + 4, received.length);
				assertArrayEquals(new byte[] { 0, 0, 0, 5 }, Arrays.copyOf(received, 4));
				assertFalse(Arrays.equals(payload, received));
			}
		}
	}
	
	@Test
	void upgradeWithUntrustedServerThrows() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = SslClientConfig.builder().verifyHostname(false).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, server.boundEndpoint(), config));
				assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			}
		}
	}
	
	@Test
	void upgradeWithMismatchedHostnameThrows() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.UNSPECIFIED, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			HostEndpoint target = new HostEndpoint("127.0.0.2", server.boundEndpoint().port());
			SslClientConfig config = this.clientConfig().verifyHostname(true).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(target.toInetSocketAddress(), 5000);
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, target, config));
				assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			}
		}
	}
	
	@Test
	void upgradeWithNonTlsPeerThrows() throws Exception {
		withNonTlsPeer(peer -> {
			try (Socket socket = new Socket()) {
				socket.connect(peer.toInetSocketAddress(), 5000);
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, peer, this.clientConfig().build()));
				assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
			}
		});
	}
	
	@Test
	void upgradeNotifiesErrorHandlerOnHandshakeFailure() throws Exception {
		AtomicReference<NetworkErrorType> recorded = new AtomicReference<>();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = SslClientConfig.builder()
				.verifyHostname(false)
				.onError((connection, errorType, message, cause) -> recorded.set(errorType))
				.build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, server.boundEndpoint(), config));
				assertEquals(NetworkErrorType.HANDSHAKE_FAILED, recorded.get());
			}
		}
	}
	
	@Test
	void upgradeNotifiesErrorHandlerOnIoFailure() throws Exception {
		AtomicReference<NetworkErrorType> recorded = new AtomicReference<>();
		
		withNonTlsPeer(peer -> {
			SslClientConfig config = this.clientConfig().onError((connection, errorType, message, cause) -> recorded.set(errorType)).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(peer.toInetSocketAddress(), 5000);
				
				assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, peer, config));
				assertEquals(NetworkErrorType.IO_ERROR, recorded.get());
			}
		});
	}
	
	@Test
	void upgradeConnectedSocketEstablishesSession() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = this.clientConfig().readTimeout(Duration.ofSeconds(5)).build();
			
			Socket socket = new Socket();
			socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
			assertFalse(socket.isClosed());
			
			try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), config)) {
				assertTrue(client.isActive());
				assertTrue(client.getSession().getProtocol().startsWith("TLS"));
				
				client.send("Hello".getBytes());
				assertArrayEquals("Hello".getBytes(), receiveExactly(client, 5, 4096));
			}
		}
	}
	
	@Test
	void upgradeClosedConnectedSocketThrows() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			
			Socket socket = new Socket();
			socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
			socket.close();
			assertTrue(socket.isConnected());
			assertTrue(socket.isClosed());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, server.boundEndpoint(), this.clientConfig().build()));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
			assertEquals(server.boundEndpoint(), exception.endpoint());
		}
	}
	
	@Test
	void upgradeWithIpEndpointVerifiesAddress() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = this.clientConfig().verifyHostname(true).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				SslClient client = assertDoesNotThrow(() -> SslClient.upgrade(socket, server.boundEndpoint(), config));
				assertTrue(client.isActive());
				client.close();
			}
		}
	}
	
	@Test
	void upgradeWithHostEndpointVerifiesHostname() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			HostEndpoint target = new HostEndpoint("localhost", server.boundEndpoint().port());
			SslClientConfig config = this.clientConfig().verifyHostname(true).readTimeout(Duration.ofSeconds(5)).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = assertDoesNotThrow(() -> SslClient.upgrade(socket, target, config))) {
					client.send("Hello".getBytes());
					assertArrayEquals("Hello".getBytes(), receiveExactly(client, 5, 4096));
				}
			}
		}
	}
	
	@Test
	void upgradeUsesEndpointHostnameNotPeerAddress() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.UNSPECIFIED, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			HostEndpoint target = new HostEndpoint("localhost", server.boundEndpoint().port());
			SslClientConfig config = this.clientConfig().verifyHostname(true).readTimeout(Duration.ofSeconds(5)).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(new HostEndpoint("127.0.0.2", server.boundEndpoint().port()).toInetSocketAddress(), 5000);
				
				try (SslClient client = assertDoesNotThrow(() -> SslClient.upgrade(socket, target, config))) {
					assertTrue(client.getSession().getProtocol().startsWith("TLS"));
					client.send("Hello".getBytes());
					assertArrayEquals("Hello".getBytes(), receiveExactly(client, 5, 4096));
				}
			}
		}
	}
	
	@Test
	void upgradeWithoutHostnameVerification() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.UNSPECIFIED, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			HostEndpoint target = new HostEndpoint("127.0.0.2", server.boundEndpoint().port());
			SslClientConfig config = this.clientConfig().verifyHostname(false).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(target.toInetSocketAddress(), 5000);
				
				try (SslClient client = assertDoesNotThrow(() -> SslClient.upgrade(socket, target, config))) {
					assertTrue(client.isActive());
				}
			}
		}
	}
	
	@Test
	void upgradeWithEnabledProtocols() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = this.clientConfig().enabledProtocols(List.of("TLSv1.2")).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), config)) {
					assertEquals("TLSv1.2", client.getSession().getProtocol());
				}
			}
		}
	}
	
	@Test
	void upgradeWithoutEnabledProtocols() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = this.clientConfig().build();
			assertTrue(config.enabledProtocols().isEmpty());
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), config)) {
					assertTrue(client.getSession().getProtocol().startsWith("TLS"));
				}
			}
		}
	}
	
	@Test
	void upgradeWithEnabledCipherSuites() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = this.clientConfig()
				.enabledProtocols(List.of("TLSv1.3"))
				.enabledCipherSuites(List.of("TLS_AES_128_GCM_SHA256"))
				.build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), config)) {
					assertEquals("TLS_AES_128_GCM_SHA256", client.getSession().getCipherSuite());
				}
			}
		}
	}
	
	@Test
	void upgradeWithoutEnabledCipherSuites() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = this.clientConfig().build();
			assertTrue(config.enabledCipherSuites().isEmpty());
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), config)) {
					assertNotNull(client.getSession().getCipherSuite());
					assertNotEquals("SSL_NULL_WITH_NULL_NULL", client.getSession().getCipherSuite());
				}
			}
		}
	}
	
	@Test
	void upgradeWithReadTimeoutExpires() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			SslClientConfig config = this.clientConfig().readTimeout(Duration.ofMillis(500)).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), config)) {
					NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
					assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
				}
			}
		}
	}
	
	@Test
	void upgradeWithoutReadTimeoutDoesNotExpire() throws Exception {
		SslServerConfig serverConfig = SslServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				try {
					Thread.sleep(1500);
					conn.send(data);
				} catch (Exception e) {
					fail("Delayed echo failed: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, serverConfig)) {
			server.start();
			SslClientConfig config = this.clientConfig().readTimeout(Duration.ZERO).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), config)) {
					client.send("Hello".getBytes());
					assertArrayEquals("Hello".getBytes(), receiveExactly(client, 5, 4096));
				}
			}
		}
	}
	
	@Test
	void upgradeDoesNotFireConnectHandler() throws Exception {
		AtomicInteger connectCount = new AtomicInteger(0);
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = this.clientConfig().onConnect((connection, local, remote, timestamp) -> connectCount.incrementAndGet()).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), config)) {
					assertTrue(client.isActive());
					assertEquals(0, connectCount.get());
				}
			}
		}
	}
	
	@Test
	void upgradedClientReportsEndpoints() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), this.clientConfig().build())) {
					assertTrue(client.localEndpoint().isPresent());
					assertTrue(client.remoteEndpoint().isPresent());
					assertInstanceOf(IpEndpoint.class, client.localEndpoint().orElseThrow());
					assertEquals(server.boundEndpoint().port(), client.remoteEndpoint().orElseThrow().port());
				}
			}
		}
	}
	
	@Test
	void upgradedClientExposesSession() throws Exception {
		this.withUpgradedClient(client -> {
			SSLSession session = client.getSession();
			
			assertNotNull(session);
			assertTrue(session.getProtocol().startsWith("TLS"));
			assertNotEquals(0, session.getPeerCertificates().length);
		});
	}
	
	@Test
	void upgradedClientRoundTripsMessage() throws Exception {
		this.withUpgradedClient(client -> {
			byte[] payload = "Upgraded message".getBytes();
			client.send(payload);
			
			assertArrayEquals(payload, receiveExactly(client, payload.length, 4096));
		});
	}
	
	//region Helper methods
	
	@Test
	void upgradedClientUsableThroughNetworkClientInterface() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = this.clientConfig().readTimeout(Duration.ofSeconds(5)).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (NetworkClient<byte[]> client = SslClient.upgrade(socket, server.boundEndpoint(), config)) {
					assertInstanceOf(NetworkClient.class, client);
					assertTrue(client.isActive());
					
					client.send("Hello".getBytes());
					assertArrayEquals("Hello".getBytes(), client.receive(1024));
				}
			}
		}
	}
	
	@Test
	void closeUpgradedClientClosesUnderlyingSocket() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			
			Socket socket = new Socket();
			socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
			SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), this.clientConfig().build());
			assertFalse(socket.isClosed());
			
			client.close();
			
			assertTrue(socket.isClosed());
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void failedUpgradeLeavesSocketToCaller() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = SslClientConfig.builder().verifyHostname(false).build();
			
			Socket socket = new Socket();
			socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> SslClient.upgrade(socket, server.boundEndpoint(), config));
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			assertTrue(socket.isClosed());
			assertDoesNotThrow(socket::close);
		}
	}
	
	@Test
	void upgradedClientMultipleRoundTripsWithVaryingSizes() throws Exception {
		this.withUpgradedClient(client -> {
			int[] sizes = { 10, 500, 20, 2000, 5 };
			for (int size : sizes) {
				byte[] payload = filled(size, (byte) (size % 128));
				client.send(payload);
				assertArrayEquals(payload, receiveExactly(client, size, 4096));
			}
		});
	}
	
	@Test
	void unframedUpgradedClientSendAndReceive() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig(false))) {
			server.start();
			SslClientConfig config = this.clientConfig().framing(false).readTimeout(Duration.ofSeconds(5)).build();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), config)) {
					byte[] payload = "Raw bytes".getBytes();
					client.send(payload);
					
					assertArrayEquals(payload, readUntil(client, payload.length, 4096));
				}
			}
		}
	}
	
	@Test
	void upgradeTwoClientsOnSameServer() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = this.clientConfig().readTimeout(Duration.ofSeconds(5)).build();
			
			try (Socket first = new Socket(); Socket second = new Socket()) {
				first.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				second.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient firstClient = SslClient.upgrade(first, server.boundEndpoint(), config); SslClient secondClient = SslClient.upgrade(second, server.boundEndpoint(), config)) {
					assertTrue(firstClient.getSession().getProtocol().startsWith("TLS"));
					assertTrue(secondClient.getSession().getProtocol().startsWith("TLS"));
					assertTrue(awaitClientCount(server, 2));
					
					firstClient.send("First".getBytes());
					secondClient.send("Second".getBytes());
					assertArrayEquals("First".getBytes(), receiveExactly(firstClient, 5, 4096));
					assertArrayEquals("Second".getBytes(), receiveExactly(secondClient, 6, 4096));
				}
			}
		}
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SslClient(null));
	}
	
	@Test
	void clientConnectWithNullEndpointThrows() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			assertThrows(NullPointerException.class, () -> client.connect(null));
		}
	}
	
	@Test
	void clientConnectToWithNullEndpointThrows() {
		assertThrows(NullPointerException.class, () -> SslClient.connectTo(null));
		assertThrows(NullPointerException.class, () -> SslClient.connectTo(null, SslClientConfig.DEFAULT));
	}
	
	@Test
	void clientConnectToWithNullConfigThrows() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 59999);
		assertThrows(NullPointerException.class, () -> SslClient.connectTo(endpoint, null));
	}
	
	@Test
	void clientSendWithNullDataThrows() throws Exception {
		try (SslClient unconnected = new SslClient(this.clientConfig().build())) {
			assertThrows(NullPointerException.class, () -> unconnected.send(null));
		}
		
		this.withEchoServer(client -> assertThrows(NullPointerException.class, () -> client.send(null)));
	}
	
	@Test
	void clientReceiveWithZeroMaxBytesThrows() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(0));
		}
	}
	
	@Test
	void clientReceiveWithNegativeMaxBytesThrows() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			assertThrows(IllegalArgumentException.class, () -> client.receive(-1));
		}
	}
	
	@Test
	void clientGetInputStreamWithoutConnectThrows() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getInputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientGetOutputStreamWithoutConnectThrows() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getOutputStream);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientGetSessionWithoutConnectThrows() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::getSession);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientRemoteEndpointEmptyBeforeConnect() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientRemoteEndpointAfterConnect() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().build())) {
				client.connect(server.boundEndpoint());
				
				assertTrue(client.remoteEndpoint().isPresent());
				assertEquals(server.boundEndpoint().port(), client.remoteEndpoint().orElseThrow().port());
			}
		}
	}
	
	@Test
	void clientRemoteEndpointEmptyAfterClose() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			SslClient client = new SslClient(this.clientConfig().build());
			client.connect(server.boundEndpoint());
			assertTrue(client.remoteEndpoint().isPresent());
			
			client.close();
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientLocalEndpointEmptyBeforeConnect() {
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			assertTrue(client.localEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientReceiveReusesBufferAcrossCalls() throws Exception {
		this.withEchoServer(client -> {
			client.send("first".getBytes());
			assertArrayEquals("first".getBytes(), receiveExactly(client, 5, 1024));
			
			client.send("secnd".getBytes());
			assertArrayEquals("secnd".getBytes(), receiveExactly(client, 5, 1024));
		});
	}
	
	@Test
	void clientReceiveGrowsBufferForLargerMaxBytes() throws Exception {
		this.withEchoServer(client -> {
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
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			assertEquals(endpoint, server.boundEndpoint());
		}
	}
	
	@Test
	void serverBoundEndpointAfterStartReturnsActualPort() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			assertNotEquals(0, server.boundEndpoint().port());
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void clientConnectToHostEndpoint() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			HostEndpoint target = new HostEndpoint("localhost", server.boundEndpoint().port());
			
			try (SslClient client = new SslClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build())) {
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
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			HostEndpoint target = new HostEndpoint("localhost", server.boundEndpoint().port());
			
			try (SslClient client = SslClient.connectTo(target, this.clientConfig().build())) {
				assertTrue(client.isActive());
				assertEquals(server.boundEndpoint().port(), client.remoteEndpoint().orElseThrow().port());
			}
		}
	}
	
	@Test
	void clientRemoteEndpointIsIpEndpointWhenConnectedByName() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			HostEndpoint target = new HostEndpoint("localhost", server.boundEndpoint().port());
			
			try (SslClient client = SslClient.connectTo(target, this.clientConfig().build())) {
				assertInstanceOf(IpEndpoint.class, client.remoteEndpoint().orElseThrow());
				assertInstanceOf(IpEndpoint.class, client.localEndpoint().orElseThrow());
			}
		}
	}
	
	@Test
	void clientConnectToUnresolvableHostEndpointThrows() {
		HostEndpoint endpoint = new HostEndpoint("this-host-does-not-exist.invalid", 8443);
		
		try (SslClient client = new SslClient(this.clientConfig().build())) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(endpoint));
			assertSame(endpoint, exception.endpoint());
		}
	}
	
	@Test
	void sessionReportsPeerHostWhenConnectedByName() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			HostEndpoint target = new HostEndpoint("localhost", server.boundEndpoint().port());
			
			try (SslClient client = SslClient.connectTo(target, this.clientConfig().build())) {
				assertEquals("localhost", client.getSession().getPeerHost());
			}
		}
	}
	
	@Test
	void clientGetInputStreamAfterConnect() throws Exception {
		this.withEchoServer(client -> {
			InputStream stream = client.getInputStream();
			assertNotNull(stream);
			
			client.send("Hi".getBytes());
			assertArrayEquals("Hi".getBytes(), NetworkUtils.readFrame(stream, 1024));
		});
	}
	
	@Test
	void clientGetOutputStreamAfterConnect() throws Exception {
		this.withEchoServer(client -> {
			OutputStream stream = client.getOutputStream();
			assertNotNull(stream);
			
			NetworkUtils.writeFrame(stream, "Hi".getBytes());
			assertArrayEquals("Hi".getBytes(), receiveExactly(client, 2, 1024));
		});
	}
	
	@Test
	void connectToClosesClientWhenConnectFails() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 59999);
		SslClientConfig config = this.clientConfig().connectTimeout(Duration.ofSeconds(2)).build();
		
		NetworkConnectionException first = assertThrows(NetworkConnectionException.class, () -> SslClient.connectTo(endpoint, config));
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, first.errorType());
		
		NetworkConnectionException second = assertThrows(NetworkConnectionException.class, () -> SslClient.connectTo(endpoint, config));
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, second.errorType());
	}
	
	@Test
	void connectToClosesClientWhenHandshakeFails() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			SslClientConfig config = SslClientConfig.builder().verifyHostname(false).build();
			
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> SslClient.connectTo(server.boundEndpoint(), config)
			);
			assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
		}
	}
	
	@Test
	void hostnameVerificationSucceedsWhenConnectedByName() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			HostEndpoint target = new HostEndpoint("localhost", server.boundEndpoint().port());
			
			SslClientConfig config = SslClientConfig.builder()
				.sslContext(clientContext)
				.verifyHostname(true)
				.readTimeout(Duration.ofSeconds(5))
				.build();
			
			try (SslClient client = new SslClient(config)) {
				assertDoesNotThrow(() -> client.connect(target));
				
				client.send("Hello".getBytes());
				assertArrayEquals("Hello".getBytes(), receiveExactly(client, 5, 1024));
			}
		}
	}
	
	@Test
	void hostnameVerificationFailsForMismatchedName() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.UNSPECIFIED, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			HostEndpoint target = new HostEndpoint("127.0.0.2", server.boundEndpoint().port());
			
			SslClientConfig config = SslClientConfig.builder()
				.sslContext(clientContext)
				.verifyHostname(true)
				.build();
			
			try (SslClient client = new SslClient(config)) {
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(target));
				assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			}
		}
	}
	
	@Test
	void clientUsableThroughNetworkClientInterface() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			SslClientConfig config = this.clientConfig().readTimeout(Duration.ofSeconds(5)).build();
			
			try (NetworkClient<byte[]> client = SslClient.connectTo(server.boundEndpoint(), config)) {
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
		this.withEchoServer(client -> {
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
		this.withEchoServer(client -> {
			int[] sizes = { 10, 500, 20, 2000, 5 };
			for (int size : sizes) {
				byte[] payload = filled(size, (byte) (size % 128));
				client.send(payload);
				assertArrayEquals(payload, receiveExactly(client, size, 4096));
			}
		});
	}
	
	@Test
	void connectEventHandlerAcceptsEndpointSupertype() throws Exception {
		AtomicReference<String> hostRef = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			SslClientConfig config = this.clientConfig()
				.onConnect((Connection connection, Endpoint local, Endpoint remote, Instant timestamp) -> {
					hostRef.set(hostPartOf(remote));
					latch.countDown();
				})
				.build();
			
			try (SslClient client = new SslClient(config)) {
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
		try (SslServer server = new SslServer(endpoint, SslServerConfig.builder(serverContext).build())) {
			server.start();
			
			SslClientConfig config = this.clientConfig()
				.onDisconnect((Connection connection, Endpoint local, Endpoint remote, Instant timestamp) -> {
					bothPresent.set(local != null && remote != null);
					latch.countDown();
				})
				.build();
			
			SslClient client = new SslClient(config);
			client.connect(server.boundEndpoint());
			client.close();
			
			assertTrue(latch.await(5, TimeUnit.SECONDS));
			assertTrue(bothPresent.get());
		}
	}
	
	private SslServerConfig echoConfig() {
		return this.echoConfig(true);
	}
	
	private SslServerConfig echoConfig(boolean framing) {
		return SslServerConfig.builder(serverContext)
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
	
	private void withEchoServer(ClientConsumer body) throws Exception {
		this.withEchoServer(true, body);
	}
	
	private void withEchoServer(boolean framing, ClientConsumer body) throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig(framing))) {
			server.start();
			
			try (SslClient client = new SslClient(this.clientConfig().framing(framing).readTimeout(Duration.ofSeconds(5)).build())) {
				client.connect(server.boundEndpoint());
				body.accept(client);
			}
		}
	}
	
	private void withUpgradedClient(ClientConsumer body) throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SslServer server = new SslServer(endpoint, this.echoConfig())) {
			server.start();
			
			try (Socket socket = new Socket()) {
				socket.connect(server.boundEndpoint().toInetSocketAddress(), 5000);
				
				try (SslClient client = SslClient.upgrade(socket, server.boundEndpoint(), this.clientConfig().readTimeout(Duration.ofSeconds(5)).build())) {
					body.accept(client);
				}
			}
		}
	}
	
	/**
	 * Returns a client config builder that trusts the test server certificate and skips hostname verification.<br>
	 */
	private SslClientConfigBuilder clientConfig() {
		return SslClientConfig.builder().sslContext(clientContext).verifyHostname(false);
	}
	
	@FunctionalInterface
	private interface ClientConsumer {
		
		void accept(SslClient client) throws Exception;
	}
	
	@FunctionalInterface
	private interface PeerConsumer {
		
		void accept(IpEndpoint endpoint) throws Exception;
	}
	//endregion
}
