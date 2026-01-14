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

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Integration tests for TCP client and server.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class TcpClientServerTest {

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

				// Give server time to register the client
				Thread.sleep(100);
				assertEquals(1, server.getClientCount());
			}
		}
	}

	@Test
	void clientSendAndReceive() throws Exception {
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
	void clientConnectToNonExistentServerThrows() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 59999);

		TcpClientConfig config = TcpClientConfig.builder()
			.connectTimeout(Duration.ofSeconds(2))
			.build();

		try (TcpClient client = new TcpClient(config)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class,
				() -> client.connect(endpoint));

			assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
		}
	}

	@Test
	void clientSendWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class,
				() -> client.send("data".getBytes()));

			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
		}
	}

	@Test
	void clientReceiveWithoutConnectThrows() {
		try (TcpClient client = new TcpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class,
				client::receive);

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

				NetworkConnectionException exception = assertThrows(NetworkConnectionException.class,
					() -> client.connect(serverEndpoint));

				assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
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
