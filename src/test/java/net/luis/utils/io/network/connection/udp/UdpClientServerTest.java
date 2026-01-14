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
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.NetworkServer;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UDP client and server.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class UdpClientServerTest {
	
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
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class,
				() -> client.bind(endpoint));
			
			assertEquals(NetworkErrorType.ALREADY_CONNECTED, exception.errorType());
		}
	}
	
	@Test
	void clientSendAndReceive() throws Exception {
		CountDownLatch messageLatch = new CountDownLatch(1);
		AtomicReference<byte[]> receivedData = new AtomicReference<>();
		AtomicReference<IpEndpoint> sourceEndpoint = new AtomicReference<>();
		
		UdpServerConfig config = UdpServerConfig.builder()
			.executorStrategy(ClientExecutorStrategy.virtualThreads())
			.onMessage((server, datagram, data) -> {
				receivedData.set(data);
				sourceEndpoint.set(datagram.endpoint());
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
	void clientReceiveWithoutBindThrows() {
		try (UdpClient client = new UdpClient()) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class,
				client::receive);
			
			assertEquals(NetworkErrorType.NOT_CONNECTED, exception.errorType());
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
		CountDownLatch receivedLatch = new CountDownLatch(1);
		AtomicReference<UdpDatagram> receivedDatagram = new AtomicReference<>();
		
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
}
