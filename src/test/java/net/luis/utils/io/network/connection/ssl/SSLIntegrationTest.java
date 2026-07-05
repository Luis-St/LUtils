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
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
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
class SSLIntegrationTest {
	
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SSLTestContext.serverContext();
		clientContext = SSLTestContext.clientContext();
	}
	
	@Test
	void serverStartAndStop() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, SSLServerConfig.builder(serverContext).build())) {
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
		try (SSLServer server = new SSLServer(endpoint, SSLServerConfig.builder(serverContext).build())) {
			assertInstanceOf(NetworkServer.class, server);
		}
	}
	
	@Test
	void clientImplementsNetworkClient() {
		try (SSLClient client = new SSLClient(this.clientConfig().build())) {
			assertInstanceOf(NetworkClient.class, client);
		}
	}
	
	@Test
	void clientNotConnectedInitially() {
		try (SSLClient client = new SSLClient(this.clientConfig().build())) {
			assertFalse(client.isActive());
			assertTrue(client.localEndpoint().isEmpty());
			assertTrue(client.remoteEndpoint().isEmpty());
		}
	}
	
	@Test
	void clientConnectToServer() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, SSLServerConfig.builder(serverContext).build())) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			try (SSLClient client = new SSLClient(this.clientConfig().build())) {
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
		
		SSLClientConfig config = this.clientConfig().connectTimeout(Duration.ofSeconds(2)).build();
		
		try (SSLClient client = new SSLClient(config)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(endpoint));
			assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
		}
	}
	
	@Test
	void clientSendWithoutConnectThrows() {
		try (SSLClient client = new SSLClient(this.clientConfig().build())) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send("data".getBytes()));
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientReceiveWithoutConnectThrows() {
		try (SSLClient client = new SSLClient(this.clientConfig().build())) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientDoubleConnectThrows() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, SSLServerConfig.builder(serverContext).build())) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			try (SSLClient client = new SSLClient(this.clientConfig().build())) {
				client.connect(serverEndpoint);
				
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(serverEndpoint));
				assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
			}
		}
	}
	
	@Test
	void clientGetSessionAfterConnect() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, SSLServerConfig.builder(serverContext).build())) {
			server.start();
			
			try (SSLClient client = new SSLClient(this.clientConfig().build())) {
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
		
		SSLServerConfig config = SSLServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, config)) {
			server.start();
			
			try (SSLClient client = new SSLClient(this.clientConfig().build())) {
				client.connect(server.boundEndpoint());
				client.send(binaryData);
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals(binaryData, receivedData.get());
			}
		}
	}
	
	@Test
	void echoServerRoundTrip() throws Exception {
		SSLServerConfig config = SSLServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, config)) {
			server.start();
			
			try (SSLClient client = new SSLClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build())) {
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
		
		SSLServerConfig config = SSLServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Echo failed: " + e.getMessage());
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, config)) {
			server.start();
			
			try (SSLClient client = new SSLClient(this.clientConfig().readTimeout(Duration.ofSeconds(10)).build())) {
				client.connect(server.boundEndpoint());
				client.send(largeData);
				
				byte[] received = client.receive();
				assertArrayEquals(largeData, received);
			}
		}
	}
	
	@Test
	void sendAndReceiveMultipleRoundTrips() throws Exception {
		SSLServerConfig config = SSLServerConfig.builder(serverContext)
			.onMessage((server, conn, data) -> {
				try {
					conn.send(data);
				} catch (NetworkConnectionException e) {
					fail("Send failed");
				}
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, config)) {
			server.start();
			
			try (SSLClient client = new SSLClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build())) {
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
		try (SSLServer server = new SSLServer(endpoint, SSLServerConfig.builder(serverContext).build())) {
			server.start();
			
			try (SSLClient client = new SSLClient(this.clientConfig().readTimeout(Duration.ofMillis(500)).build())) {
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
		
		SSLServerConfig config = SSLServerConfig.builder(serverContext)
			.onClientConnect(event -> connectLatch.countDown())
			.onClientDisconnect(event -> disconnectLatch.countDown())
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, config)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			SSLClient client = new SSLClient(this.clientConfig().build());
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
		try (SSLServer server = new SSLServer(endpoint, SSLServerConfig.builder(serverContext).build())) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			SSLClientConfig config = this.clientConfig()
				.onConnect(event -> connectLatch.countDown())
				.onDisconnect(event -> disconnectLatch.countDown())
				.build();
			
			try (SSLClient client = new SSLClient(config)) {
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
		
		SSLServerConfig config = SSLServerConfig.builder(serverContext)
			.onClientConnect(event -> allConnected.countDown())
			.onMessage((server, conn, data) -> {
				receivedMessages.add(new String(data));
				allMessagesReceived.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, config)) {
			server.start();
			
			List<SSLClient> clients = new ArrayList<>();
			try {
				for (int i = 0; i < clientCount; i++) {
					SSLClient client = new SSLClient(this.clientConfig().build());
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
				for (SSLClient client : clients) {
					client.close();
				}
			}
		}
	}
	
	@Test
	void serverBroadcast() throws Exception {
		CountDownLatch clientsConnected = new CountDownLatch(2);
		CountDownLatch messagesReceived = new CountDownLatch(2);
		
		SSLServerConfig config = SSLServerConfig.builder(serverContext)
			.onClientConnect(event -> clientsConnected.countDown())
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, config)) {
			server.start();
			IpEndpoint serverEndpoint = server.boundEndpoint();
			
			try (SSLClient client1 = new SSLClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build());
			     SSLClient client2 = new SSLClient(this.clientConfig().readTimeout(Duration.ofSeconds(5)).build())) {
				
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
		try (SSLServer server = new SSLServer(endpoint, SSLServerConfig.builder(serverContext).build())) {
			server.start();
			
			try (SSLClient client = new SSLClient(this.clientConfig().bufferSize(100).build())) {
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
	void hostnameVerificationSucceedsForLoopback() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, SSLServerConfig.builder(serverContext).build())) {
			server.start();
			
			SSLClientConfig config = SSLClientConfig.builder()
				.sslContext(clientContext)
				.verifyHostname(true)
				.build();
			
			try (SSLClient client = new SSLClient(config)) {
				assertDoesNotThrow(() -> client.connect(server.boundEndpoint()));
				assertTrue(client.isActive());
			}
		}
	}
	
	@Test
	void handshakeFailsWithUntrustedServer() throws Exception {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, SSLServerConfig.builder(serverContext).build())) {
			server.start();
			
			SSLClientConfig config = SSLClientConfig.builder()
				.verifyHostname(false)
				.build();
			
			try (SSLClient client = new SSLClient(config)) {
				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.connect(server.boundEndpoint()));
				assertEquals(NetworkErrorType.HANDSHAKE_FAILED, exception.errorType());
			}
		}
	}
	
	@Test
	void mutualTlsWithRequiredClientAuthSucceeds() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		
		SSLServerConfig config = SSLServerConfig.builder(serverContext)
			.clientAuth(SSLClientAuth.REQUIRED)
			.onMessage((server, conn, data) -> {
				receivedData.set(data);
				messageLatch.countDown();
			})
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, config)) {
			server.start();
			
			try (SSLClient client = new SSLClient(this.clientConfig().build())) {
				client.connect(server.boundEndpoint());
				client.send("mtls".getBytes());
				
				assertTrue(messageLatch.await(5, TimeUnit.SECONDS));
				assertArrayEquals("mtls".getBytes(), receivedData.get());
			}
		}
	}
	
	@Test
	void mutualTlsRejectsClientWithoutCertificate() throws Exception {
		SSLContext trustOnly = SSLTestContext.trustOnlyClientContext();
		
		SSLServerConfig config = SSLServerConfig.builder(serverContext)
			.enabledProtocols(List.of("TLSv1.2"))
			.clientAuth(SSLClientAuth.REQUIRED)
			.build();
		
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		try (SSLServer server = new SSLServer(endpoint, config)) {
			server.start();
			
			SSLClientConfig clientConfig = SSLClientConfig.builder()
				.sslContext(trustOnly)
				.enabledProtocols(List.of("TLSv1.2"))
				.verifyHostname(false)
				.build();
			
			try (SSLClient client = new SSLClient(clientConfig)) {
				// A client without a certificate is rejected by a server requiring client auth. The exact error
				// type is TLS-stack dependent (handshake alert vs. connection reset), so only assert the rejection.
				assertThrows(NetworkConnectionException.class, () -> client.connect(server.boundEndpoint()));
				assertFalse(client.isActive());
			}
		}
	}
	
	//region Helper methods
	
	/**
	 * Returns a client config builder that trusts the test server certificate and skips hostname verification.<br>
	 */
	private SSLClientConfigBuilder clientConfig() {
		return SSLClientConfig.builder().sslContext(clientContext).verifyHostname(false);
	}
	//endregion
}
