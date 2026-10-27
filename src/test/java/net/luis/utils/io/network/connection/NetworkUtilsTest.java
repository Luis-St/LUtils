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

import net.luis.utils.io.network.HostEndpoint;
import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.ssl.SslTestContext;
import net.luis.utils.io.network.connection.tcp.TcpClientConfig;
import net.luis.utils.io.network.connection.udp.UdpPeer;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import javax.net.ssl.*;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.net.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NetworkUtils}.<br>
 *
 * @author Luis-St
 */
class NetworkUtilsTest {
	
	private static final IpEndpoint ENDPOINT = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
	
	private static Future<?> submitSpinningTask(ExecutorService executor, CountDownLatch started, AtomicBoolean interrupted) {
		return executor.submit(() -> {
			started.countDown();
			try {
				Thread.sleep(Duration.ofSeconds(30));
			} catch (InterruptedException e) {
				interrupted.set(true);
			}
		});
	}
	
	private static SaturatedListener saturatedListener() throws IOException {
		ServerSocket serverSocket = new ServerSocket(0, 1, InetAddress.getLoopbackAddress());
		List<Socket> fillers = new ArrayList<>();
		for (int attempt = 0; attempt < 64; attempt++) {
			Socket filler = new Socket();
			try {
				filler.connect(serverSocket.getLocalSocketAddress(), 1000);
				fillers.add(filler);
			} catch (SocketTimeoutException e) {
				filler.close();
				return new SaturatedListener(serverSocket, fillers);
			} catch (ConnectException e) {
				filler.close();
				new SaturatedListener(serverSocket, fillers).close();
				Assumptions.abort("The operating system refuses connections to a full accept queue instead of dropping them");
			}
		}
		new SaturatedListener(serverSocket, fillers).close();
		throw new IllegalStateException("The accept queue of the listener never filled up");
	}
	
	@Test
	void constructorIsPrivate() {
		Constructor<?>[] constructors = NetworkUtils.class.getDeclaredConstructors();
		
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
	}
	
	@Test
	void validateMessageSizeWithNullData() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.validateMessageSize(null, 1024, null));
	}
	
	@Test
	void validateMessageSizeExceedingMax() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 80);
		
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> NetworkUtils.validateMessageSize(new byte[11], 10, endpoint));
		assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		assertTrue(exception.getMessage().contains("11"));
		assertTrue(exception.getMessage().contains("10"));
		assertEquals(endpoint, exception.endpoint());
	}
	
	@Test
	void validateTimeoutWithNullName() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.validateTimeout(null, null));
		assertThrows(NullPointerException.class, () -> NetworkUtils.validateTimeout(Duration.ofSeconds(1), null));
	}
	
	@Test
	void validateTimeoutWithZero() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> NetworkUtils.validateTimeout(Duration.ZERO, "Read timeout"));
		
		assertTrue(exception.getMessage().startsWith("Read timeout"));
	}
	
	@Test
	void validateTimeoutWithNegative() {
		assertThrows(IllegalArgumentException.class, () -> NetworkUtils.validateTimeout(Duration.ofMillis(-1), "Timeout"));
	}
	
	@Test
	void validateGracePeriodWithNull() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.validateGracePeriod(null));
	}
	
	@Test
	void validateGracePeriodWithNegative() {
		assertThrows(IllegalArgumentException.class, () -> NetworkUtils.validateGracePeriod(Duration.ofMillis(-1)));
	}
	
	@Test
	void toTimeoutMillisWithZeroOrNegative() {
		assertThrows(IllegalArgumentException.class, () -> NetworkUtils.toTimeoutMillis(Duration.ZERO));
		assertThrows(IllegalArgumentException.class, () -> NetworkUtils.toTimeoutMillis(Duration.ofMillis(-1)));
	}
	
	@Test
	void connectSocketWithNullArguments() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.connectSocket(null, TcpClientConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> NetworkUtils.connectSocket(ENDPOINT, null));
	}
	
	@Test
	void connectSocketRefused() throws Exception {
		try (Socket unlistened = new Socket()) {
			unlistened.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, unlistened.getLocalPort());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> NetworkUtils.connectSocket(endpoint, TcpClientConfig.DEFAULT));
			assertEquals(NetworkErrorType.CONNECTION_REFUSED, exception.errorType());
			assertEquals(endpoint, exception.endpoint());
		}
	}
	
	@Test
	void connectSocketUnresolvedHost() {
		HostEndpoint endpoint = new HostEndpoint("does-not-exist.invalid", 80);
		
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> NetworkUtils.connectSocket(endpoint, TcpClientConfig.DEFAULT));
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, exception.errorType());
	}
	
	@Test
	void mapConnectFailureWithNullArguments() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.mapConnectFailure(null, ENDPOINT, Duration.ofSeconds(1)));
		assertThrows(NullPointerException.class, () -> NetworkUtils.mapConnectFailure(new IOException("x"), null, Duration.ofSeconds(1)));
		assertThrows(NullPointerException.class, () -> NetworkUtils.mapConnectFailure(new IOException("x"), ENDPOINT, null));
	}
	
	@Test
	void mapIoFailureWithNullArguments() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.mapIoFailure(null, "message", ENDPOINT));
		assertThrows(NullPointerException.class, () -> NetworkUtils.mapIoFailure(new IOException("x"), null, ENDPOINT));
	}
	
	@Test
	void reportErrorWithNullError() {
		AtomicInteger calls = new AtomicInteger();
		
		assertThrows(NullPointerException.class, () -> NetworkUtils.reportError((client, error) -> calls.incrementAndGet(), null, null));
		assertEquals(0, calls.get());
	}
	
	@Test
	void validateMessageSizeWithinMax() {
		assertDoesNotThrow(() -> NetworkUtils.validateMessageSize(new byte[512], 1024, null));
	}
	
	@Test
	void validateMessageSizeEqualToMax() {
		assertDoesNotThrow(() -> NetworkUtils.validateMessageSize(new byte[1024], 1024, null));
	}
	
	@Test
	void validateMessageSizeWithEmptyData() {
		assertDoesNotThrow(() -> NetworkUtils.validateMessageSize(new byte[0], 1024, null));
	}
	
	@Test
	void validateMessageSizeWithNullEndpoint() {
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> NetworkUtils.validateMessageSize(new byte[2], 1, null));
		
		assertNull(exception.endpoint());
	}
	
	@Test
	void validateMessageSizeAcceptsHostEndpoint() {
		HostEndpoint endpoint = new HostEndpoint("example.com", 443);
		
		NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> NetworkUtils.validateMessageSize(new byte[2], 1, endpoint));
		assertSame(endpoint, exception.endpoint());
	}
	
	@Test
	void validateTimeoutWithNullTimeout() {
		assertDoesNotThrow(() -> NetworkUtils.validateTimeout(null, "Timeout"));
	}
	
	@Test
	void validateTimeoutWithPositive() {
		assertDoesNotThrow(() -> NetworkUtils.validateTimeout(Duration.ofNanos(1), "Timeout"));
		assertDoesNotThrow(() -> NetworkUtils.validateTimeout(Duration.ofDays(1), "Timeout"));
	}
	
	@Test
	void validateGracePeriodWithZero() {
		assertDoesNotThrow(() -> NetworkUtils.validateGracePeriod(Duration.ZERO));
		assertDoesNotThrow(() -> NetworkUtils.validateGracePeriod(Duration.ofSeconds(1)));
	}
	
	@Test
	void toTimeoutMillisWithNull() {
		assertEquals(0, NetworkUtils.toTimeoutMillis(null));
	}
	
	@Test
	void toTimeoutMillisClampsToOne() {
		assertEquals(1, NetworkUtils.toTimeoutMillis(Duration.ofNanos(1)));
	}
	
	@Test
	void toTimeoutMillisClampsToIntegerMax() {
		assertEquals(Integer.MAX_VALUE, NetworkUtils.toTimeoutMillis(Duration.ofDays(36500)));
	}
	
	@Test
	void toTimeoutMillisConvertsMillis() {
		assertEquals(1500, NetworkUtils.toTimeoutMillis(Duration.ofMillis(1500)));
	}
	
	@Test
	void mapConnectFailurePassesNetworkConnectionExceptionThrough() {
		NetworkConnectionException cause = new NetworkConnectionException("failed", NetworkErrorType.IO_ERROR);
		
		assertSame(cause, NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1)));
	}
	
	@Test
	void mapConnectFailureSocketTimeout() {
		Duration connectTimeout = Duration.ofSeconds(3);
		SocketTimeoutException cause = new SocketTimeoutException("t");
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(cause, ENDPOINT, connectTimeout);
		NetworkTimeoutException timeout = assertInstanceOf(NetworkTimeoutException.class, mapped);
		assertEquals(NetworkErrorType.CONNECTION_TIMEOUT, timeout.errorType());
		assertEquals(connectTimeout, timeout.timeout());
		assertSame(cause, timeout.getCause());
		assertSame(ENDPOINT, timeout.endpoint());
		assertTrue(timeout.getMessage().contains(ENDPOINT.toString()));
	}
	
	@Test
	void mapConnectFailureSslHandshake() {
		SSLHandshakeException cause = new SSLHandshakeException("handshake");
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1));
		assertEquals(NetworkErrorType.HANDSHAKE_FAILED, mapped.errorType());
		assertSame(cause, mapped.getCause());
		assertFalse(mapped instanceof NetworkTimeoutException);
	}
	
	@Test
	void mapConnectFailureConnectException() {
		ConnectException cause = new ConnectException("refused");
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1));
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, mapped.errorType());
		assertSame(cause, mapped.getCause());
	}
	
	@Test
	void mapConnectFailureNoRouteToHost() {
		NoRouteToHostException cause = new NoRouteToHostException("no route");
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1));
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, mapped.errorType());
		assertSame(cause, mapped.getCause());
	}
	
	@Test
	void mapConnectFailureUnknownHost() {
		UnknownHostException cause = new UnknownHostException("unknown");
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1));
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, mapped.errorType());
		assertSame(cause, mapped.getCause());
	}
	
	@Test
	void mapConnectFailureOtherIoException() {
		IOException cause = new IOException("boom");
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1));
		assertEquals(NetworkErrorType.CONNECTION_FAILED, mapped.errorType());
		assertSame(ENDPOINT, mapped.endpoint());
		assertSame(cause, mapped.getCause());
	}
	
	@Test
	void mapConnectFailureWithHostEndpoint() {
		HostEndpoint endpoint = new HostEndpoint("example.com", 443);
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(new IOException("boom"), endpoint, Duration.ofSeconds(1));
		assertTrue(mapped.getMessage().contains("example.com:443"));
		assertSame(endpoint, mapped.endpoint());
	}
	
	@Test
	void mapIoFailurePassesNetworkConnectionExceptionThrough() {
		NetworkConnectionException cause = new NetworkConnectionException("failed", NetworkErrorType.IO_ERROR);
		
		assertSame(cause, NetworkUtils.mapIoFailure(cause, "message", ENDPOINT));
	}
	
	@Test
	void mapIoFailureSocketException() {
		SocketException cause = new SocketException("reset");
		
		NetworkConnectionException mapped = NetworkUtils.mapIoFailure(cause, "Failed to send", ENDPOINT);
		assertEquals(NetworkErrorType.CONNECTION_RESET, mapped.errorType());
		assertEquals("Failed to send, connection reset", mapped.getMessage());
		assertSame(cause, mapped.getCause());
		assertSame(ENDPOINT, mapped.endpoint());
	}
	
	@Test
	void mapIoFailureOtherIoException() {
		IOException cause = new IOException("boom");
		
		NetworkConnectionException mapped = NetworkUtils.mapIoFailure(cause, "Failed to send", ENDPOINT);
		assertEquals(NetworkErrorType.IO_ERROR, mapped.errorType());
		assertEquals("Failed to send", mapped.getMessage());
		assertSame(cause, mapped.getCause());
	}
	
	@Test
	void mapIoFailureWithNullEndpoint() {
		NetworkConnectionException mapped = NetworkUtils.mapIoFailure(new IOException("boom"), "message", null);
		
		assertNull(mapped.endpoint());
	}
	
	@Test
	void reportErrorWithHandler() throws Exception {
		AtomicReference<NetworkClient<?>> receivedClient = new AtomicReference<>();
		AtomicReference<Throwable> receivedError = new AtomicReference<>();
		AtomicInteger uncaught = new AtomicInteger();
		ErrorHandler handler = (client, error) -> {
			receivedClient.set(client);
			receivedError.set(error);
		};
		RuntimeException error = new RuntimeException("error");
		UdpPeer peer = UdpPeer.ephemeral();
		peer.close();
		
		Thread thread = Thread.ofPlatform().unstarted(() -> {
			NetworkUtils.reportError(handler, null, error);
			assertNull(receivedClient.get());
			NetworkUtils.reportError(handler, peer, error);
		});
		thread.setUncaughtExceptionHandler((t, e) -> uncaught.incrementAndGet());
		thread.start();
		thread.join(5000);
		
		assertSame(peer, receivedClient.get());
		assertSame(error, receivedError.get());
		assertEquals(0, uncaught.get());
	}
	
	@Test
	void reportErrorWithoutHandler() throws Exception {
		AtomicReference<Thread> receivedThread = new AtomicReference<>();
		AtomicReference<Throwable> receivedError = new AtomicReference<>();
		RuntimeException error = new RuntimeException("error");
		
		Thread thread = Thread.ofPlatform().unstarted(() -> NetworkUtils.reportError(null, null, error));
		thread.setUncaughtExceptionHandler((t, e) -> {
			receivedThread.set(t);
			receivedError.set(e);
		});
		thread.start();
		thread.join(5000);
		
		assertSame(thread, receivedThread.get());
		assertSame(error, receivedError.get());
	}
	
	@Test
	void closeQuietlyWithNull() {
		assertDoesNotThrow(() -> NetworkUtils.closeQuietly(null));
	}
	
	@Test
	void closeQuietlyClosesResource() {
		AtomicInteger closed = new AtomicInteger();
		
		NetworkUtils.closeQuietly(closed::incrementAndGet);
		
		assertEquals(1, closed.get());
	}
	
	@Test
	void closeQuietlySwallowsIoException() {
		Closeable failing = () -> {
			throw new IOException("close failed");
		};
		
		assertDoesNotThrow(() -> NetworkUtils.closeQuietly(failing));
	}
	
	@Test
	void closeQuietlyClosesUnconnectedSslSocket() throws Exception {
		Socket socket = SSLContext.getDefault().getSocketFactory().createSocket();
		
		NetworkUtils.closeQuietly(socket);
		
		assertTrue(socket.isClosed());
	}
	
	@Test
	void closeQuietlyWithClosedSslSocket() throws Exception {
		Socket socket = SSLContext.getDefault().getSocketFactory().createSocket();
		socket.close();
		
		assertDoesNotThrow(() -> NetworkUtils.closeQuietly(socket));
		assertTrue(socket.isClosed());
	}
	
	@Test
	void shutdownExecutorWithNullExecutor() {
		assertDoesNotThrow(() -> NetworkUtils.shutdownExecutor(null, true));
		assertDoesNotThrow(() -> NetworkUtils.shutdownExecutor(null, false));
	}
	
	@Test
	void shutdownExecutorWhenNotOwned() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		NetworkUtils.shutdownExecutor(executor, false);
		
		assertFalse(executor.isShutdown());
		executor.shutdown();
	}
	
	@Test
	void shutdownExecutorWhenOwned() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		
		NetworkUtils.shutdownExecutor(executor, true);
		
		assertTrue(executor.isShutdown());
		assertTrue(executor.isTerminated());
	}
	
	@Test
	void shutdownExecutorWaitsForCompletion() {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		AtomicBoolean taskCompleted = new AtomicBoolean(false);
		executor.submit(() -> {
			try {
				Thread.sleep(100);
				taskCompleted.set(true);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});
		
		NetworkUtils.shutdownExecutor(executor, true);
		
		assertTrue(executor.isShutdown());
		assertTrue(taskCompleted.get());
	}
	
	@Test
	@Timeout(value = 15, unit = TimeUnit.SECONDS)
	void shutdownExecutorForcesShutdownAfterTimeout() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		CountDownLatch started = new CountDownLatch(1);
		AtomicBoolean interrupted = new AtomicBoolean(false);
		submitSpinningTask(executor, started, interrupted);
		assertTrue(started.await(5, TimeUnit.SECONDS));
		
		long start = System.nanoTime();
		NetworkUtils.shutdownExecutor(executor, true);
		long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
		
		assertTrue(executor.isShutdown());
		assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
		assertTrue(interrupted.get());
		assertTrue(elapsed >= 4500 && elapsed < 10000, "Elapsed: " + elapsed + " ms");
	}
	
	@Test
	void shutdownExecutorRestoresInterruptFlag() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		CountDownLatch started = new CountDownLatch(1);
		AtomicBoolean interrupted = new AtomicBoolean(false);
		submitSpinningTask(executor, started, interrupted);
		assertTrue(started.await(5, TimeUnit.SECONDS));
		
		Thread.currentThread().interrupt();
		NetworkUtils.shutdownExecutor(executor, true);
		boolean flagSet = Thread.interrupted();
		
		assertTrue(flagSet);
		assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
		assertTrue(interrupted.get());
	}
	
	@Test
	void connectSocketAppliesConfig() throws Exception {
		TcpClientConfig config = TcpClientConfig.builder().tcpNoDelay(false).keepAlive(false).readTimeout(Duration.ofMillis(1500)).build();
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort());
			
			try (Socket socket = NetworkUtils.connectSocket(endpoint, config)) {
				assertTrue(socket.isConnected());
				assertFalse(socket.getTcpNoDelay());
				assertFalse(socket.getKeepAlive());
				assertEquals(1500, socket.getSoTimeout());
			}
		}
	}
	
	@Test
	void connectSocketWithoutReadTimeout() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, serverSocket.getLocalPort());
			
			try (Socket socket = NetworkUtils.connectSocket(endpoint, TcpClientConfig.DEFAULT)) {
				assertEquals(0, socket.getSoTimeout());
			}
		}
	}
	
	@Test
	@Timeout(value = 15, unit = TimeUnit.SECONDS)
	void connectSocketTimesOut() throws Exception {
		TcpClientConfig config = TcpClientConfig.builder().connectTimeout(Duration.ofMillis(300)).build();
		
		try (SaturatedListener listener = saturatedListener()) {
			NetworkTimeoutException exception = assertThrows(NetworkTimeoutException.class, () -> NetworkUtils.connectSocket(listener.endpoint(), config));
			assertEquals(NetworkErrorType.CONNECTION_TIMEOUT, exception.errorType());
			assertEquals(Duration.ofMillis(300), exception.timeout());
		}
	}
	
	@Test
	@Timeout(value = 15, unit = TimeUnit.SECONDS)
	void closeQuietlyDoesNotHangOnSilentSslPeer() throws Exception {
		SSLServerSocketFactory serverFactory = SslTestContext.serverContext().getServerSocketFactory();
		try (SSLServerSocket serverSocket = (SSLServerSocket) serverFactory.createServerSocket(0, 1, InetAddress.getLoopbackAddress())) {
			CompletableFuture<SSLSocket> accepted = new CompletableFuture<>();
			Thread.ofPlatform().daemon().start(() -> {
				try {
					SSLSocket socket = (SSLSocket) serverSocket.accept();
					socket.startHandshake();
					accepted.complete(socket);
				} catch (IOException e) {
					accepted.completeExceptionally(e);
				}
			});
			SSLSocket client = (SSLSocket) SslTestContext.clientContext().getSocketFactory().createSocket(InetAddress.getLoopbackAddress(), serverSocket.getLocalPort());
			client.startHandshake();
			SSLSocket server = accepted.get(5, TimeUnit.SECONDS);
			
			long start = System.nanoTime();
			NetworkUtils.closeQuietly(client);
			long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
			
			assertTrue(client.isClosed());
			assertTrue(elapsed < 10000, "Elapsed: " + elapsed + " ms");
			server.close();
		}
	}
	
	private record SaturatedListener(ServerSocket serverSocket, List<Socket> fillers) implements AutoCloseable {
		
		private IpEndpoint endpoint() {
			return new IpEndpoint(Ipv4Address.LOOPBACK, this.serverSocket.getLocalPort());
		}
		
		@Override
		public void close() throws IOException {
			for (Socket filler : this.fillers) {
				filler.close();
			}
			this.serverSocket.close();
		}
	}
}
