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
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.connection.NetworkClient;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.executor.ClientExecutorStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UdpServer}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class UdpServerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	private static final DatagramHandler NO_OP = (server, datagram) -> {};
	private static final DatagramHandler ECHO = (server, datagram) -> server.peer().send(datagram);
	private static final UdpPeerConfig CLIENT = UdpPeerConfig.builder().receiveTimeout(Duration.ofSeconds(5)).build();
	
	private static UdpPeer client() throws NetworkConnectionException {
		return UdpPeer.bindTo(EPHEMERAL, CLIENT);
	}
	
	private static byte[] bytes(String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}
	
	private static String string(byte[] value) {
		return new String(value, StandardCharsets.UTF_8);
	}
	
	@Test
	void constructWithDefaultConfig() {
		try (UdpServer server = new UdpServer(EPHEMERAL, NO_OP)) {
			assertSame(UdpServerConfig.DEFAULT, server.config());
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void constructWithConfig() {
		UdpServerConfig config = UdpServerConfig.builder().maxDatagramSize(100).build();
		
		try (UdpServer server = new UdpServer(EPHEMERAL, config, NO_OP)) {
			assertSame(config, server.config());
			assertFalse(server.isRunning());
		}
	}
	
	@Test
	void constructWithNullArguments() {
		assertThrows(NullPointerException.class, () -> new UdpServer(null, NO_OP));
		assertThrows(NullPointerException.class, () -> new UdpServer(EPHEMERAL, null));
		assertThrows(NullPointerException.class, () -> new UdpServer(null, UdpServerConfig.DEFAULT, NO_OP));
		assertThrows(NullPointerException.class, () -> new UdpServer(EPHEMERAL, null, NO_OP));
		assertThrows(NullPointerException.class, () -> new UdpServer(EPHEMERAL, UdpServerConfig.DEFAULT, null));
	}
	
	@Test
	void startOnWithNullArguments() {
		assertThrows(NullPointerException.class, () -> UdpServer.startOn(null, NO_OP));
		assertThrows(NullPointerException.class, () -> UdpServer.startOn(EPHEMERAL, null));
		assertThrows(NullPointerException.class, () -> UdpServer.startOn(null, UdpServerConfig.DEFAULT, NO_OP));
		assertThrows(NullPointerException.class, () -> UdpServer.startOn(EPHEMERAL, null, NO_OP));
		assertThrows(NullPointerException.class, () -> UdpServer.startOn(EPHEMERAL, UdpServerConfig.DEFAULT, null));
	}
	
	@Test
	void startOnPortInUse() throws Exception {
		try (UdpServer first = UdpServer.startOn(EPHEMERAL, NO_OP)) {
			UdpServer second = new UdpServer(first.boundEndpoint(), NO_OP);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, second::start);
			assertEquals(NetworkErrorType.ADDRESS_IN_USE, exception.errorType());
			assertFalse(second.isRunning());
			assertThrows(IllegalStateException.class, second::peer);
		}
	}
	
	@Test
	void peerBeforeStart() {
		UdpServer server = new UdpServer(EPHEMERAL, NO_OP);
		
		assertThrows(IllegalStateException.class, server::peer);
	}
	
	@Test
	void stopWithNullGracePeriod() throws Exception {
		try (UdpServer server = new UdpServer(EPHEMERAL, NO_OP)) {
			assertThrows(NullPointerException.class, () -> server.stop(null));
			
			server.start();
			assertThrows(NullPointerException.class, () -> server.stop(null));
		}
	}
	
	@Test
	void stopWithNegativeGracePeriod() throws Exception {
		try (UdpServer server = new UdpServer(EPHEMERAL, NO_OP)) {
			assertThrows(IllegalArgumentException.class, () -> server.stop(Duration.ofMillis(-1)));
			
			server.start();
			assertThrows(IllegalArgumentException.class, () -> server.stop(Duration.ofMillis(-1)));
		}
	}
	
	@Test
	void isRunningLifecycle() throws Exception {
		UdpServer server = new UdpServer(EPHEMERAL, NO_OP);
		assertFalse(server.isRunning());
		
		server.start();
		assertTrue(server.isRunning());
		
		server.stop();
		assertFalse(server.isRunning());
	}
	
	@Test
	void boundEndpointLifecycle() throws Exception {
		UdpServer server = new UdpServer(EPHEMERAL, NO_OP);
		assertEquals(0, server.boundEndpoint().port());
		
		server.start();
		assertTrue(server.boundEndpoint().port() > 0);
		
		server.stop();
		assertEquals(0, server.boundEndpoint().port());
	}
	
	@Test
	void peerAfterStart() throws Exception {
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, NO_OP)) {
			UdpPeer peer = server.peer();
			
			assertTrue(peer.isActive());
			assertEquals(server.boundEndpoint(), peer.localEndpoint().orElseThrow());
		}
	}
	
	@Test
	void startOnReturnsRunningServer() throws Exception {
		UdpServerConfig config = UdpServerConfig.builder().maxDatagramSize(100).build();
		
		try (UdpServer first = UdpServer.startOn(EPHEMERAL, NO_OP); UdpServer second = UdpServer.startOn(EPHEMERAL, config, NO_OP)) {
			assertTrue(first.isRunning());
			assertTrue(first.boundEndpoint().port() > 0);
			assertSame(UdpServerConfig.DEFAULT, first.config());
			assertTrue(second.isRunning());
			assertTrue(second.boundEndpoint().port() > 0);
			assertSame(config, second.config());
			assertTrue(second.peer().isActive());
			assertEquals(second.boundEndpoint(), second.peer().localEndpoint().orElseThrow());
		}
	}
	
	@Test
	void startOnIpv6LoopbackEndpoint() throws Exception {
		try (UdpServer server = new UdpServer(new IpEndpoint(Ipv6Address.LOOPBACK, 0), NO_OP)) {
			server.start();
			
			assertTrue(server.isRunning());
			assertEquals(6, server.boundEndpoint().address().version());
		}
	}
	
	@Test
	void startTwiceIsNoOp() throws Exception {
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, NO_OP)) {
			UdpPeer peer = server.peer();
			
			server.start();
			
			assertSame(peer, server.peer());
		}
	}
	
	@Test
	void stopBeforeStartIsNoOp() {
		UdpServer server = new UdpServer(EPHEMERAL, NO_OP);
		
		assertDoesNotThrow(() -> server.stop());
		assertDoesNotThrow(() -> server.stop(Duration.ofSeconds(1)));
		assertFalse(server.isRunning());
	}
	
	@Test
	void closeBeforeStartIsNoOp() {
		UdpServer server = new UdpServer(EPHEMERAL, NO_OP);
		
		assertDoesNotThrow(server::close);
		assertFalse(server.isRunning());
	}
	
	@Test
	void stopTwiceIsNoOp() throws Exception {
		UdpServer server = UdpServer.startOn(EPHEMERAL, NO_OP);
		
		server.stop();
		assertDoesNotThrow(() -> server.stop());
		assertFalse(server.isRunning());
	}
	
	@Test
	void stopClosesPeer() throws Exception {
		UdpServer server = UdpServer.startOn(EPHEMERAL, NO_OP);
		
		server.stop();
		
		assertFalse(server.peer().isActive());
	}
	
	@Test
	void handlerReceivesDatagram() throws Exception {
		CompletableFuture<UdpServer> receivedServer = new CompletableFuture<>();
		CompletableFuture<UdpDatagram> receivedDatagram = new CompletableFuture<>();
		DatagramHandler handler = (server, datagram) -> {
			receivedServer.complete(server);
			receivedDatagram.complete(datagram);
		};
		
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, handler); UdpPeer client = client()) {
			client.send(server.boundEndpoint(), bytes("hello"));
			UdpDatagram datagram = receivedDatagram.get(5, TimeUnit.SECONDS);
			
			assertSame(server, receivedServer.get(5, TimeUnit.SECONDS));
			assertEquals("hello", string(datagram.data()));
			assertEquals(client.localEndpoint().orElseThrow(), datagram.endpoint());
		}
	}
	
	@Test
	void handlerExceptionIsReported() throws Exception {
		CompletableFuture<NetworkClient<?>> errorClient = new CompletableFuture<>();
		CompletableFuture<Throwable> error = new CompletableFuture<>();
		UdpServerConfig config = UdpServerConfig.builder().onError((client, throwable) -> {
			errorClient.complete(client);
			error.complete(throwable);
		}).build();
		IllegalStateException failure = new IllegalStateException("handler failed");
		
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, config, (s, datagram) -> {
			throw failure;
		}); UdpPeer client = client()) {
			client.send(server.boundEndpoint(), bytes("fail"));
			
			assertSame(failure, error.get(5, TimeUnit.SECONDS));
			assertSame(server.peer(), errorClient.get(5, TimeUnit.SECONDS));
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
		UdpServerConfig config = UdpServerConfig.builder().executorStrategy(ClientExecutorStrategy.custom(executor)).build();
		IllegalStateException failure = new IllegalStateException("handler failed");
		
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, config, (s, datagram) -> {
			throw failure;
		}); UdpPeer client = client()) {
			client.send(server.boundEndpoint(), bytes("fail"));
			
			assertSame(failure, uncaught.get(5, TimeUnit.SECONDS));
		} finally {
			executor.shutdownNow();
		}
	}
	
	@Test
	void receiveTimeoutDoesNotStopServer() throws Exception {
		AtomicInteger errors = new AtomicInteger();
		CompletableFuture<String> received = new CompletableFuture<>();
		UdpServerConfig config = UdpServerConfig.builder().receiveTimeout(Duration.ofMillis(50)).onError((client, throwable) -> errors.incrementAndGet()).build();
		
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, config, (s, datagram) -> received.complete(string(datagram.data()))); UdpPeer client = client()) {
			Thread.sleep(300);
			client.send(server.boundEndpoint(), bytes("late"));
			
			assertEquals("late", received.get(5, TimeUnit.SECONDS));
			assertTrue(server.isRunning());
			assertEquals(0, errors.get());
		}
	}
	
	@Test
	void gracefulStopWaitsForRunningHandler() throws Exception {
		CountDownLatch arrived = new CountDownLatch(1);
		DatagramHandler handler = (server, datagram) -> {
			arrived.countDown();
			Thread.sleep(200);
			server.peer().send(datagram.endpoint(), bytes("reply"));
		};
		
		UdpServer server = UdpServer.startOn(EPHEMERAL, handler);
		try (UdpPeer client = client()) {
			client.send(server.boundEndpoint(), bytes("request"));
			assertTrue(arrived.await(5, TimeUnit.SECONDS));
			
			server.stop(Duration.ofSeconds(5));
			
			assertEquals("reply", string(client.receive().orElseThrow().data()));
		}
	}
	
	@Test
	void immediateStopClosesPeerUnderRunningHandler() throws Exception {
		CountDownLatch arrived = new CountDownLatch(1);
		CompletableFuture<NetworkErrorType> failure = new CompletableFuture<>();
		DatagramHandler handler = (server, datagram) -> {
			arrived.countDown();
			Thread.sleep(200);
			try {
				server.peer().send(datagram.endpoint(), bytes("reply"));
				failure.complete(null);
			} catch (NetworkConnectionException e) {
				failure.complete(e.errorType());
			}
		};
		
		UdpServer server = UdpServer.startOn(EPHEMERAL, handler);
		try (UdpPeer client = client()) {
			client.send(server.boundEndpoint(), bytes("request"));
			assertTrue(arrived.await(5, TimeUnit.SECONDS));
			
			server.stop();
			
			assertEquals(NetworkErrorType.SOCKET_CLOSED, failure.get(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void datagramsAfterStopAreNotHandled() throws Exception {
		AtomicInteger handled = new AtomicInteger();
		
		UdpServer server = UdpServer.startOn(EPHEMERAL, (s, datagram) -> handled.incrementAndGet());
		IpEndpoint endpoint = server.boundEndpoint();
		server.stop();
		try (UdpPeer client = client()) {
			client.send(endpoint, bytes("too late"));
			Thread.sleep(200);
			
			assertEquals(0, handled.get());
		}
	}
	
	@Test
	void stopShutsDownOwnedExecutor() throws Exception {
		UdpServerConfig config = UdpServerConfig.builder().executorStrategy(ClientExecutorStrategy.fixedPool(2)).build();
		
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, config, ECHO); UdpPeer client = client()) {
			client.send(server.boundEndpoint(), bytes("first"));
			assertEquals("first", string(client.receive().orElseThrow().data()));
			server.stop();
			
			server.start();
			client.send(server.boundEndpoint(), bytes("second"));
			
			assertEquals("second", string(client.receive().orElseThrow().data()));
		}
	}
	
	@Test
	void stopLeavesCustomExecutorRunning() throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();
		UdpServerConfig config = UdpServerConfig.builder().executorStrategy(ClientExecutorStrategy.custom(executor)).build();
		
		try {
			UdpServer server = UdpServer.startOn(EPHEMERAL, config, NO_OP);
			server.stop();
			
			assertFalse(executor.isShutdown());
		} finally {
			executor.shutdownNow();
		}
	}
	
	@Test
	void rejectedExecutionKeepsTrackerBalanced() throws Exception {
		ExecutorService executor = Executors.newCachedThreadPool();
		UdpServerConfig config = UdpServerConfig.builder().executorStrategy(ClientExecutorStrategy.custom(executor)).build();
		AtomicInteger handled = new AtomicInteger();
		
		UdpServer server = UdpServer.startOn(EPHEMERAL, config, (s, datagram) -> handled.incrementAndGet());
		executor.shutdown();
		try (UdpPeer client = client()) {
			client.send(server.boundEndpoint(), bytes("rejected"));
			Thread.sleep(200);
			
			long start = System.nanoTime();
			server.stop(Duration.ofSeconds(20));
			long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
			
			assertEquals(0, handled.get());
			assertTrue(elapsed < 10000, "Elapsed: " + elapsed + " ms");
		}
	}
	
	@Test
	void closeStopsServer() throws Exception {
		UdpServer server = UdpServer.startOn(EPHEMERAL, NO_OP);
		
		server.close();
		
		assertFalse(server.isRunning());
		assertFalse(server.peer().isActive());
	}
	
	@Test
	void echoServer() throws Exception {
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, ECHO); UdpPeer client = client()) {
			client.send(server.boundEndpoint(), bytes("echo"));
			
			assertEquals("echo", string(client.receive().orElseThrow().data()));
		}
	}
	
	@Test
	void maxDatagramSizeAppliesToServerPeer() throws Exception {
		UdpServerConfig config = UdpServerConfig.builder().maxDatagramSize(8).build();
		CompletableFuture<Integer> length = new CompletableFuture<>();
		
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, config, (s, datagram) -> length.complete(datagram.length())); UdpPeer client = client()) {
			client.send(server.boundEndpoint(), new byte[20]);
			
			assertEquals(8, length.get(5, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void handlesDatagramsConcurrently() throws Exception {
		CountDownLatch together = new CountDownLatch(2);
		CountDownLatch completed = new CountDownLatch(2);
		DatagramHandler handler = (server, datagram) -> {
			together.countDown();
			if (together.await(5, TimeUnit.SECONDS)) {
				completed.countDown();
			}
		};
		
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, handler); UdpPeer first = client(); UdpPeer second = client()) {
			first.send(server.boundEndpoint(), bytes("first"));
			second.send(server.boundEndpoint(), bytes("second"));
			
			assertTrue(completed.await(10, TimeUnit.SECONDS));
		}
	}
	
	@Test
	void manyClientsEcho() throws Exception {
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, ECHO); ExecutorService executor = Executors.newFixedThreadPool(20)) {
			List<Future<List<String>>> futures = new ArrayList<>();
			for (int clientId = 0; clientId < 20; clientId++) {
				int id = clientId;
				futures.add(executor.submit(() -> {
					List<String> echoes = new ArrayList<>();
					try (UdpPeer client = client()) {
						for (int i = 0; i < 5; i++) {
							client.send(server.boundEndpoint(), bytes(id + "-" + i));
							echoes.add(string(client.receive().orElseThrow().data()));
						}
					}
					return echoes;
				}));
			}
			
			for (int clientId = 0; clientId < 20; clientId++) {
				List<String> expected = List.of(clientId + "-0", clientId + "-1", clientId + "-2", clientId + "-3", clientId + "-4");
				assertEquals(expected, futures.get(clientId).get(15, TimeUnit.SECONDS));
			}
		}
	}
	
	@Test
	void restartAfterStop() throws Exception {
		try (UdpServer server = UdpServer.startOn(EPHEMERAL, ECHO); UdpPeer client = client()) {
			UdpPeer oldPeer = server.peer();
			server.stop();
			
			server.start();
			client.send(server.boundEndpoint(), bytes("again"));
			
			assertNotSame(oldPeer, server.peer());
			assertFalse(oldPeer.isActive());
			assertEquals("again", string(client.receive().orElseThrow().data()));
		}
	}
}
