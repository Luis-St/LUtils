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
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.*;
import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.*;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

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
	
	//region Helper methods
	
	private SslClientConfigBuilder clientConfig() {
		return SslClientConfig.builder().sslContext(clientContext).verifyHostname(false);
	}
	//endregion
}
