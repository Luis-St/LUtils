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
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import net.luis.utils.io.network.connection.ssl.*;
import net.luis.utils.io.network.connection.tcp.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StreamServer}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class StreamServerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final ConnectionHandler<TcpServer, TcpConnection> NO_OP = (server, connection) -> {};
	private static final ConnectionHandler<TcpServer, TcpConnection> BLOCKING = (server, connection) -> connection.receive();
	private static final ConnectionHandler<TcpServer, TcpConnection> ECHO = ConnectionHandler.perMessage((server, connection, data) -> connection.send(data));
	
	private static boolean await(BooleanSupplier condition) throws InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
		while (System.nanoTime() < deadline) {
			if (condition.getAsBoolean()) {
				return true;
			}
			Thread.sleep(10);
		}
		return condition.getAsBoolean();
	}
	
	private static NetworkConnectionException awaitConnectFailure(IpEndpoint endpoint) throws InterruptedException {
		// The listening socket is only released once the accept thread has left accept, which can lag behind close
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
		while (System.nanoTime() < deadline) {
			try {
				TcpConnection.connectTo(endpoint).close();
			} catch (NetworkConnectionException e) {
				return e;
			}
			Thread.sleep(10);
		}
		return fail("Connections to " + endpoint + " are still accepted after the server was closed");
	}
	
	private static void awaitBlocked(AtomicReference<Thread> thread) throws InterruptedException {
		assertTrue(await(() -> thread.get() != null && thread.get().getState() == Thread.State.WAITING), "Handler did not block in receive");
	}
	
	private static TcpConnection connect(TcpServer server) throws NetworkConnectionException {
		return TcpConnection.connectTo(server.boundEndpoint());
	}
	
	private static byte[] bytes(String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}
	
	private static String string(byte[] value) {
		return new String(value, StandardCharsets.UTF_8);
	}
	
	@Test
	void constructWithNullBindEndpoint() {
		assertThrows(NullPointerException.class, () -> new TcpServer(null, NO_OP));
	}
	
	@Test
	void constructWithNullHandler() {
		assertThrows(NullPointerException.class, () -> new TcpServer(EPHEMERAL, TcpServerConfig.DEFAULT, null));
	}
	
	@Test
	void constructDoesNotStart() {
		try (TcpServer server = new TcpServer(EPHEMERAL, NO_OP)) {
			assertFalse(server.isRunning());
			assertTrue(server.connections().isEmpty());
		}
	}
	
	@Test
	void startOnPortInUse() throws Exception {
		try (TcpServer first = TcpServer.startOn(EPHEMERAL, NO_OP)) {
			IpEndpoint occupied = first.boundEndpoint();
			TcpServer second = new TcpServer(occupied, NO_OP);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, second::start);
			assertEquals(NetworkErrorType.ADDRESS_IN_USE, exception.errorType());
			assertEquals(occupied, exception.endpoint());
			assertFalse(second.isRunning());
		}
	}
	
	@Test
	void stopWithNullGracePeriod() throws Exception {
		try (TcpServer server = new TcpServer(EPHEMERAL, NO_OP)) {
			assertThrows(NullPointerException.class, () -> server.stop(null));
			
			server.start();
			assertThrows(NullPointerException.class, () -> server.stop(null));
		}
	}
	
	@Test
	void stopWithNegativeGracePeriod() {
		try (TcpServer server = new TcpServer(EPHEMERAL, NO_OP)) {
			assertThrows(IllegalArgumentException.class, () -> server.stop(Duration.ofMillis(-1)));
		}
	}
	
	@Test
	void broadcastWithNullData() {
		try (TcpServer server = new TcpServer(EPHEMERAL, NO_OP)) {
			assertThrows(NullPointerException.class, () -> server.broadcast(null));
		}
	}
	
	@Test
	void isRunningLifecycle() throws Exception {
		TcpServer server = new TcpServer(EPHEMERAL, NO_OP);
		assertFalse(server.isRunning());
		
		server.start();
		assertTrue(server.isRunning());
		
		server.stop();
		assertFalse(server.isRunning());
	}
	
	@Test
	void boundEndpointLifecycle() throws Exception {
		TcpServer server = new TcpServer(EPHEMERAL, NO_OP);
		assertEquals(0, server.boundEndpoint().port());
		
		server.start();
		assertTrue(server.boundEndpoint().port() > 0);
		
		server.stop();
		assertEquals(0, server.boundEndpoint().port());
	}
	
	@Test
	void startTwiceIsNoOp() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, NO_OP)) {
			int port = server.boundEndpoint().port();
			
			server.start();
			
			assertEquals(port, server.boundEndpoint().port());
			assertTrue(server.isRunning());
		}
	}
	
	@Test
	void stopBeforeStartIsNoOp() {
		TcpServer server = new TcpServer(EPHEMERAL, NO_OP);
		
		assertDoesNotThrow(() -> server.stop());
		assertDoesNotThrow(() -> server.stop(Duration.ofSeconds(1)));
		assertFalse(server.isRunning());
	}
	
	@Test
	void stopTwiceIsNoOp() throws Exception {
		TcpServer server = TcpServer.startOn(EPHEMERAL, NO_OP);
		
		server.stop();
		assertDoesNotThrow(() -> server.stop());
		assertFalse(server.isRunning());
	}
	
	@Test
	void closeStopsServer() throws Exception {
		TcpServer server = TcpServer.startOn(EPHEMERAL, NO_OP);
		IpEndpoint endpoint = server.boundEndpoint();
		
		server.close();
		
		assertFalse(server.isRunning());
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, awaitConnectFailure(endpoint).errorType());
	}
	
	@Test
	void serverClosesConnectionWhenHandlerReturns() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, NO_OP); TcpConnection client = connect(server)) {
			assertTrue(client.receive().isEmpty());
			assertTrue(await(() -> server.connections().isEmpty()));
		}
	}
	
	@Test
	void handlerExceptionIsReported() throws Exception {
		CompletableFuture<NetworkClient<?>> errorClient = new CompletableFuture<>();
		CompletableFuture<Throwable> error = new CompletableFuture<>();
		CompletableFuture<TcpConnection> handled = new CompletableFuture<>();
		TcpServerConfig config = TcpServerConfig.builder().onError((client, throwable) -> {
			errorClient.complete(client);
			error.complete(throwable);
		}).build();
		IllegalStateException failure = new IllegalStateException("handler failed");
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config, (s, connection) -> {
			handled.complete(connection);
			throw failure;
		}); TcpConnection _ = connect(server)) {
			assertSame(failure, error.get(5, TimeUnit.SECONDS));
			assertSame(handled.get(5, TimeUnit.SECONDS), errorClient.get(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void handlerExceptionWithoutErrorHandlerGoesToUncaughtHandler() throws Exception {
		CompletableFuture<Throwable> uncaught = new CompletableFuture<>();
		ExecutorService executor = Executors.newCachedThreadPool(runnable -> {
			Thread thread = new Thread(runnable);
			thread.setUncaughtExceptionHandler((t, e) -> uncaught.complete(e));
			return thread;
		});
		TcpServerConfig config = TcpServerConfig.builder().executorStrategy(ClientExecutorStrategy.custom(executor)).build();
		IllegalStateException failure = new IllegalStateException("handler failed");
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config, (s, connection) -> {
			throw failure;
		}); TcpConnection _ = connect(server)) {
			assertSame(failure, uncaught.get(5, TimeUnit.SECONDS));
		} finally {
			executor.shutdownNow();
		}
	}
	
	@Test
	void handlerExceptionDuringStopIsNotReported() throws Exception {
		AtomicInteger errors = new AtomicInteger();
		AtomicReference<Thread> receiving = new AtomicReference<>();
		CountDownLatch returned = new CountDownLatch(1);
		TcpServerConfig config = TcpServerConfig.builder().onError((client, throwable) -> errors.incrementAndGet()).build();
		ConnectionHandler<TcpServer, TcpConnection> handler = (s, connection) -> {
			receiving.set(Thread.currentThread());
			connection.receive();
			returned.countDown();
		};
		
		TcpServer server = TcpServer.startOn(EPHEMERAL, config, handler);
		try (TcpConnection _ = connect(server)) {
			awaitBlocked(receiving);
			server.stop();
			
			assertTrue(returned.await(5, TimeUnit.SECONDS));
			assertEquals(0, errors.get());
		}
	}
	
	@Test
	void handlerExceptionAfterStopIsNotReported() throws Exception {
		AtomicInteger errors = new AtomicInteger();
		AtomicReference<Thread> receiving = new AtomicReference<>();
		CountDownLatch throwing = new CountDownLatch(1);
		TcpServerConfig config = TcpServerConfig.builder().onError((client, throwable) -> errors.incrementAndGet()).build();
		ConnectionHandler<TcpServer, TcpConnection> handler = (s, connection) -> {
			receiving.set(Thread.currentThread());
			if (connection.receive().isEmpty()) {
				throwing.countDown();
				throw new IllegalStateException("connection ended");
			}
		};
		
		TcpServer server = TcpServer.startOn(EPHEMERAL, config, handler);
		try (TcpConnection _ = connect(server)) {
			awaitBlocked(receiving);
			server.stop();
			
			assertTrue(throwing.await(5, TimeUnit.SECONDS));
			Thread.sleep(100);
			assertEquals(0, errors.get());
		}
	}
	
	@Test
	void stopClosesOpenConnections() throws Exception {
		AtomicReference<Thread> receiving = new AtomicReference<>();
		CountDownLatch returned = new CountDownLatch(1);
		ConnectionHandler<TcpServer, TcpConnection> handler = (s, connection) -> {
			receiving.set(Thread.currentThread());
			connection.receive();
			returned.countDown();
		};
		
		TcpServer server = TcpServer.startOn(EPHEMERAL, handler);
		try (TcpConnection client = connect(server)) {
			awaitBlocked(receiving);
			server.stop();
			
			assertTrue(returned.await(5, TimeUnit.SECONDS));
			assertTrue(client.receive().isEmpty());
		}
	}
	
	@Test
	void gracefulStopWaitsForHandler() throws Exception {
		CountDownLatch received = new CountDownLatch(1);
		AtomicBoolean finished = new AtomicBoolean(false);
		ConnectionHandler<TcpServer, TcpConnection> handler = (s, connection) -> {
			connection.receive();
			received.countDown();
			Thread.sleep(200);
			connection.send(bytes("reply"));
			finished.set(true);
		};
		
		TcpServer server = TcpServer.startOn(EPHEMERAL, handler);
		try (TcpConnection client = connect(server)) {
			client.send(bytes("request"));
			assertTrue(received.await(5, TimeUnit.SECONDS));
			
			server.stop(Duration.ofSeconds(5));
			
			assertTrue(finished.get());
			assertEquals("reply", string(client.receive().orElseThrow()));
		}
	}
	
	@Test
	void gracefulStopInterruptsAfterGracePeriod() throws Exception {
		AtomicReference<Thread> receiving = new AtomicReference<>();
		CountDownLatch returned = new CountDownLatch(1);
		ConnectionHandler<TcpServer, TcpConnection> handler = (s, connection) -> {
			receiving.set(Thread.currentThread());
			connection.receive();
			returned.countDown();
		};
		
		TcpServer server = TcpServer.startOn(EPHEMERAL, handler);
		try (TcpConnection _ = connect(server)) {
			awaitBlocked(receiving);
			
			long start = System.nanoTime();
			server.stop(Duration.ofMillis(200));
			long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
			
			assertTrue(elapsed >= 200 && elapsed < 10000, "Elapsed: " + elapsed + " ms");
			assertTrue(returned.await(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void stopShutsDownOwnedExecutor() throws Exception {
		TcpServerConfig config = TcpServerConfig.builder().executorStrategy(ClientExecutorStrategy.fixedPool(2)).build();
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config, ECHO)) {
			try (TcpConnection client = connect(server)) {
				client.send(bytes("first"));
				assertEquals("first", string(client.receive().orElseThrow()));
			}
			server.stop();
			server.start();
			
			try (TcpConnection client = connect(server)) {
				client.send(bytes("second"));
				assertEquals("second", string(client.receive().orElseThrow()));
			}
		}
	}
	
	@Test
	void stopLeavesCustomExecutorRunning() throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();
		TcpServerConfig config = TcpServerConfig.builder().executorStrategy(ClientExecutorStrategy.custom(executor)).build();
		
		try {
			TcpServer server = TcpServer.startOn(EPHEMERAL, config, NO_OP);
			server.stop();
			
			assertFalse(executor.isShutdown());
		} finally {
			executor.shutdownNow();
		}
	}
	
	@Test
	void rejectedExecutionClosesSocket() throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();
		TcpServerConfig config = TcpServerConfig.builder().executorStrategy(ClientExecutorStrategy.custom(executor)).build();
		AtomicInteger handled = new AtomicInteger();
		
		TcpServer server = TcpServer.startOn(EPHEMERAL, config, (s, connection) -> handled.incrementAndGet());
		executor.shutdown();
		try (TcpConnection client = connect(server)) {
			assertTrue(client.receive().isEmpty());
			
			long start = System.nanoTime();
			server.stop(Duration.ofSeconds(20));
			long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
			
			assertTrue(elapsed < 10000, "Elapsed: " + elapsed + " ms");
			assertEquals(0, handled.get());
		}
	}
	
	@Test
	void connectionsTracksHandledConnections() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, BLOCKING)) {
			TcpConnection client = connect(server);
			assertTrue(await(() -> server.connections().size() == 1));
			
			client.close();
			
			assertTrue(await(() -> server.connections().isEmpty()));
		}
	}
	
	@Test
	void connectionsReturnsUnmodifiableSnapshot() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, BLOCKING); TcpConnection first = connect(server)) {
			assertTrue(await(() -> server.connections().size() == 1));
			Set<TcpConnection> snapshot = server.connections();
			
			try (TcpConnection _ = connect(server)) {
				assertTrue(await(() -> server.connections().size() == 2));
				
				assertThrows(UnsupportedOperationException.class, () -> snapshot.add(first));
				assertEquals(1, snapshot.size());
			}
		}
	}
	
	@Test
	void broadcastReachesAllConnections() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, BLOCKING);
			 TcpConnection first = connect(server); TcpConnection second = connect(server); TcpConnection third = connect(server)) {
			assertTrue(await(() -> server.connections().size() == 3));
			
			server.broadcast(bytes("hello"));
			
			assertEquals("hello", string(first.receive().orElseThrow()));
			assertEquals("hello", string(second.receive().orElseThrow()));
			assertEquals("hello", string(third.receive().orElseThrow()));
		}
	}
	
	@Test
	void broadcastSkipsInactiveConnections() throws Exception {
		SslServerConfig serverConfig = SslServerConfig.builder(SslTestContext.serverContext()).build();
		SslUpgradeConfig clientConfig = SslUpgradeConfig.builder().sslContext(SslTestContext.clientContext()).build();
		AtomicInteger errors = new AtomicInteger();
		TcpServerConfig config = TcpServerConfig.builder().onError((client, throwable) -> errors.incrementAndGet()).build();
		ConnectionHandler<TcpServer, TcpConnection> handler = (s, connection) -> {
			Optional<byte[]> message = connection.receive();
			if (message.isPresent() && "STARTTLS".equals(string(message.get()))) {
				connection.send(bytes("OK"));
				try (SslConnection secured = connection.upgrade(serverConfig)) {
					secured.receive();
				}
			}
		};
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config, handler);
			 TcpConnection plainFirst = connect(server); TcpConnection plainSecond = connect(server); TcpConnection upgrading = connect(server)) {
			upgrading.send(bytes("STARTTLS"));
			assertEquals("OK", string(upgrading.receive().orElseThrow()));
			try (SslConnection _ = upgrading.upgrade(clientConfig)) {
				assertTrue(await(() -> server.connections().size() == 3 && server.connections().stream().filter(c -> !c.isActive()).count() == 1));
				
				server.broadcast(bytes("news"));
				
				assertEquals("news", string(plainFirst.receive().orElseThrow()));
				assertEquals("news", string(plainSecond.receive().orElseThrow()));
				assertEquals(0, errors.get());
			}
		}
	}
	
	@Test
	void broadcastReportsFailedSend() throws Exception {
		List<NetworkClient<?>> clients = new CopyOnWriteArrayList<>();
		List<Throwable> errors = new CopyOnWriteArrayList<>();
		TcpServerConfig config = TcpServerConfig.builder().maxMessageSize(4).onError((client, throwable) -> {
			clients.add(client);
			errors.add(throwable);
		}).build();
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config, BLOCKING); TcpConnection _ = connect(server); TcpConnection _ = connect(server)) {
			assertTrue(await(() -> server.connections().size() == 2));
			Set<TcpConnection> connections = server.connections();
			
			assertDoesNotThrow(() -> server.broadcast(new byte[5]));
			
			assertEquals(2, errors.size());
			for (Throwable error : errors) {
				assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, assertInstanceOf(NetworkConnectionException.class, error).errorType());
			}
			assertEquals(connections, new HashSet<>(clients));
		}
	}
	
	@Test
	void broadcastWithoutConnections() throws Exception {
		AtomicInteger errors = new AtomicInteger();
		TcpServerConfig config = TcpServerConfig.builder().onError((client, throwable) -> errors.incrementAndGet()).build();
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config, NO_OP)) {
			assertDoesNotThrow(() -> server.broadcast(bytes("nobody")));
			assertEquals(0, errors.get());
		}
	}
	
	@Test
	void serveAppliesConfiguredFraming() throws Exception {
		TcpServerConfig config = TcpServerConfig.builder().framing(Framing.lines()).build();
		CompletableFuture<String> received = new CompletableFuture<>();
		
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, config, (s, connection) -> received.complete(string(connection.receive().orElseThrow())));
			 Socket socket = new Socket("127.0.0.1", server.boundEndpoint().port())) {
			socket.getOutputStream().write(bytes("hi\r\n"));
			socket.getOutputStream().flush();
			
			assertEquals("hi", received.get(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void restartAfterStop() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, ECHO); TcpConnection oldClient = connect(server)) {
			assertTrue(await(() -> server.connections().size() == 1));
			server.stop();
			
			server.start();
			
			assertTrue(server.isRunning());
			assertTrue(oldClient.receive().isEmpty());
			try (TcpConnection newClient = connect(server)) {
				newClient.send(bytes("again"));
				assertEquals("again", string(newClient.receive().orElseThrow()));
			}
		}
	}
	
	@Test
	void manyConcurrentClients() throws Exception {
		try (TcpServer server = TcpServer.startOn(EPHEMERAL, ECHO); ExecutorService executor = Executors.newFixedThreadPool(10)) {
			List<Future<String>> futures = new ArrayList<>();
			for (int i = 0; i < 50; i++) {
				String message = "client-" + i;
				futures.add(executor.submit(() -> {
					try (TcpConnection client = connect(server)) {
						client.send(bytes(message));
						return string(client.receive().orElseThrow());
					}
				}));
			}
			
			for (int i = 0; i < 50; i++) {
				assertEquals("client-" + i, futures.get(i).get(10, TimeUnit.SECONDS));
			}
			assertTrue(await(() -> server.connections().isEmpty()));
		}
	}
	
	@Test
	void stopWhileClientsAreConnecting() throws Exception {
		AtomicInteger errors = new AtomicInteger();
		AtomicBoolean stopped = new AtomicBoolean(false);
		AtomicBoolean lateHandler = new AtomicBoolean(false);
		AtomicBoolean connecting = new AtomicBoolean(true);
		TcpServerConfig config = TcpServerConfig.builder().onError((client, throwable) -> errors.incrementAndGet()).build();
		ConnectionHandler<TcpServer, TcpConnection> handler = (s, connection) -> {
			if (stopped.get()) {
				lateHandler.set(true);
			}
			connection.receive();
		};
		
		TcpServer server = TcpServer.startOn(EPHEMERAL, config, handler);
		IpEndpoint endpoint = server.boundEndpoint();
		List<TcpConnection> clients = new CopyOnWriteArrayList<>();
		Thread connector = Thread.ofPlatform().start(() -> {
			while (connecting.get()) {
				try {
					clients.add(TcpConnection.connectTo(endpoint));
				} catch (IOException e) {
					return;
				}
			}
		});
		
		Thread.sleep(100);
		server.stop();
		stopped.set(true);
		connecting.set(false);
		connector.join(5000);
		Thread.sleep(100);
		clients.forEach(TcpConnection::close);
		
		assertFalse(server.isRunning());
		assertFalse(lateHandler.get());
		assertEquals(0, errors.get());
	}
}
