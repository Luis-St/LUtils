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

package net.luis.utils.io.network.connection;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.tcp.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConnectionHandler}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class ConnectionHandlerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	
	private static byte[] bytes(String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}
	
	private static String string(byte[] value) {
		return new String(value, StandardCharsets.UTF_8);
	}
	
	private static void awaitConnectionCount(TcpServer server, int expected) throws InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
		while (System.nanoTime() < deadline && server.connections().size() != expected) {
			Thread.sleep(10);
		}
	}
	
	@Test
	void perMessageWithNullHandler() {
		assertThrows(NullPointerException.class, () -> ConnectionHandler.perMessage(null));
	}
	
	@Test
	void perMessageHandlerExceptionEndsConnection() throws Exception {
		CompletableFuture<NetworkClient<?>> errorClient = new CompletableFuture<>();
		CompletableFuture<Throwable> error = new CompletableFuture<>();
		TcpServerConfig config = TcpServerConfig.builder().onError((client, throwable) -> {
			errorClient.complete(client);
			error.complete(throwable);
		}).build();
		ConnectionHandler<TcpServer, TcpConnection> handler = ConnectionHandler.perMessage((server, connection, data) -> {
			throw new IllegalStateException("handler failed");
		});
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config, handler); TcpConnection client = TcpConnection.connectTo(server.boundEndpoint())) {
			client.send(bytes("first"));
			
			assertTrue(client.receive().isEmpty());
			assertInstanceOf(IllegalStateException.class, error.get(5, TimeUnit.SECONDS));
			assertNotNull(errorClient.get(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void perMessageCallsHandlerForEachMessage() throws Exception {
		List<String> received = new CopyOnWriteArrayList<>();
		CountDownLatch latch = new CountDownLatch(3);
		ConnectionHandler<TcpServer, TcpConnection> handler = ConnectionHandler.perMessage((server, connection, data) -> {
			received.add(string(data));
			latch.countDown();
		});
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, handler); TcpConnection client = TcpConnection.connectTo(server.boundEndpoint())) {
			client.send(bytes("one"));
			client.send(bytes("two"));
			client.send(bytes("three"));
			
			assertTrue(latch.await(5, TimeUnit.SECONDS));
			assertEquals(List.of("one", "two", "three"), received);
		}
	}
	
	@Test
	void perMessageReturnsWhenClientCloses() throws Exception {
		AtomicInteger calls = new AtomicInteger();
		ConnectionHandler<TcpServer, TcpConnection> handler = ConnectionHandler.perMessage((server, connection, data) -> calls.incrementAndGet());
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, handler)) {
			TcpConnection client = TcpConnection.connectTo(server.boundEndpoint());
			awaitConnectionCount(server, 1);
			assertEquals(1, server.connections().size());
			
			client.close();
			awaitConnectionCount(server, 0);
			
			assertEquals(0, server.connections().size());
			assertEquals(0, calls.get());
		}
	}
	
	@Test
	void perMessagePassesServerAndConnection() throws Exception {
		AtomicReference<TcpServer> receivedServer = new AtomicReference<>();
		CompletableFuture<TcpConnection> receivedConnection = new CompletableFuture<>();
		ConnectionHandler<TcpServer, TcpConnection> handler = ConnectionHandler.perMessage((server, connection, data) -> {
			receivedServer.set(server);
			receivedConnection.complete(connection);
		});
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, handler); TcpConnection client = TcpConnection.connectTo(server.boundEndpoint())) {
			client.send(bytes("ping"));
			TcpConnection connection = receivedConnection.get(5, TimeUnit.SECONDS);
			
			assertSame(server, receivedServer.get());
			assertEquals(client.localEndpoint().orElseThrow().port(), connection.remoteEndpoint().orElseThrow().port());
		}
	}
	
	@Test
	void perMessageEchoHandler() throws Exception {
		ConnectionHandler<TcpServer, TcpConnection> handler = ConnectionHandler.perMessage((server, connection, data) -> connection.send(data));
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, handler); TcpConnection client = TcpConnection.connectTo(server.boundEndpoint())) {
			client.send(bytes("hello"));
			assertEquals("hello", string(client.receive().orElseThrow()));
			
			client.send(bytes("world"));
			assertEquals("world", string(client.receive().orElseThrow()));
		}
	}
	
	@Test
	void handleCanBeImplementedAsLambda() throws Exception {
		CompletableFuture<String> received = new CompletableFuture<>();
		ConnectionHandler<TcpServer, TcpConnection> handler = (server, connection) -> received.complete(string(connection.receive().orElseThrow()));
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, handler); TcpConnection client = TcpConnection.connectTo(server.boundEndpoint())) {
			client.send(bytes("only"));
			
			assertEquals("only", received.get(5, TimeUnit.SECONDS));
			assertTrue(client.receive().isEmpty());
		}
	}
	
	@Test
	void perMessageServesSeveralClientsIndependently() throws Exception {
		ConnectionHandler<TcpServer, TcpConnection> handler = ConnectionHandler.perMessage((server, connection, data) -> connection.send(data));
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, handler); ExecutorService executor = Executors.newFixedThreadPool(3)) {
			List<Future<List<String>>> futures = new ArrayList<>();
			for (int clientId = 0; clientId < 3; clientId++) {
				int id = clientId;
				futures.add(executor.submit(() -> {
					List<String> echoes = new ArrayList<>();
					try (TcpConnection client = TcpConnection.connectTo(server.boundEndpoint())) {
						for (int i = 0; i < 5; i++) {
							client.send(bytes(id + "-" + i));
							echoes.add(string(client.receive().orElseThrow()));
						}
					}
					return echoes;
				}));
			}
			
			for (int clientId = 0; clientId < 3; clientId++) {
				List<String> expected = List.of(clientId + "-0", clientId + "-1", clientId + "-2", clientId + "-3", clientId + "-4");
				assertEquals(expected, futures.get(clientId).get(10, TimeUnit.SECONDS));
			}
		}
	}
	
	@Test
	void perMessageWithEmptyMessages() throws Exception {
		List<byte[]> received = new CopyOnWriteArrayList<>();
		CountDownLatch latch = new CountDownLatch(3);
		ConnectionHandler<TcpServer, TcpConnection> handler = ConnectionHandler.perMessage((server, connection, data) -> {
			received.add(data);
			latch.countDown();
		});
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, handler); TcpConnection client = TcpConnection.connectTo(server.boundEndpoint())) {
			client.send(bytes("a"));
			client.send(new byte[0]);
			client.send(bytes("b"));
			
			assertTrue(latch.await(5, TimeUnit.SECONDS));
			assertEquals("a", string(received.get(0)));
			assertArrayEquals(new byte[0], received.get(1));
			assertEquals("b", string(received.get(2)));
		}
	}
}
