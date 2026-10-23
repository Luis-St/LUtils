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

import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.network.connection.exception.*;
import net.luis.utils.io.network.connection.tcp.TcpConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Connection}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class ConnectionTest {
	
	private static final Framing FRAMING = Framing.lengthPrefixed();
	
	private static SocketPair pair() throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
			Socket local = new Socket("127.0.0.1", serverSocket.getLocalPort());
			Socket peer = serverSocket.accept();
			return new SocketPair(local, peer);
		}
	}
	
	private static TcpConnection connection(Socket socket) throws NetworkConnectionException {
		return new TcpConnection(socket, FRAMING, 1024, null);
	}
	
	private static byte[] bytes(String value) {
		return value.getBytes(StandardCharsets.UTF_8);
	}
	
	private static void writeRaw(Socket socket, byte... data) throws IOException {
		socket.getOutputStream().write(data);
		socket.getOutputStream().flush();
	}
	
	private static void reset(Socket socket) throws IOException {
		socket.setSoLinger(true, 0);
		socket.close();
	}
	
	private static void awaitBlocked(Thread thread) throws InterruptedException {
		long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
		while (thread.getState() != Thread.State.WAITING) {
			assertTrue(System.nanoTime() < deadline, "Thread did not block: " + thread.getState());
			Thread.sleep(10);
		}
	}
	
	@Test
	void constructWithConnectedSocket() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = new TcpConnection(pair.local(), FRAMING, 1024, null)) {
			assertTrue(connection.isActive());
			assertEquals(FRAMING, connection.framing());
			assertEquals(1024, connection.maxMessageSize());
			assertTrue(connection.readTimeout().isEmpty());
		}
	}
	
	@Test
	void constructWithReadTimeout() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = new TcpConnection(pair.local(), FRAMING, 1024, Duration.ofSeconds(2))) {
			assertEquals(Optional.of(Duration.ofSeconds(2)), connection.readTimeout());
			assertEquals(2000, pair.local().getSoTimeout());
		}
	}
	
	@Test
	void constructWithoutReadTimeoutResetsSocketTimeout() throws Exception {
		try (SocketPair pair = pair()) {
			pair.local().setSoTimeout(500);
			
			try (TcpConnection _ = connection(pair.local())) {
				assertEquals(0, pair.local().getSoTimeout());
			}
		}
	}
	
	@Test
	void constructWithMaxMessageSizeOne() throws Exception {
		try (SocketPair pair = pair()) {
			TcpConnection connection = assertDoesNotThrow(() -> new TcpConnection(pair.local(), FRAMING, 1, null));
			assertEquals(1, connection.maxMessageSize());
			connection.close();
		}
	}
	
	@Test
	void constructWithNullSocket() {
		assertThrows(NullPointerException.class, () -> new TcpConnection(null, FRAMING, 1024, null));
	}
	
	@Test
	void constructWithNullFraming() throws Exception {
		try (SocketPair pair = pair()) {
			assertThrows(NullPointerException.class, () -> new TcpConnection(pair.local(), null, 1024, null));
		}
	}
	
	@Test
	void constructWithUnconnectedSocket() throws Exception {
		try (Socket socket = new Socket()) {
			assertThrows(IllegalArgumentException.class, () -> connection(socket));
		}
	}
	
	@Test
	void constructWithClosedSocket() throws Exception {
		try (SocketPair pair = pair()) {
			pair.local().close();
			
			assertThrows(IllegalArgumentException.class, () -> connection(pair.local()));
		}
	}
	
	@Test
	void constructWithInvalidMaxMessageSize() throws Exception {
		try (SocketPair pair = pair()) {
			assertThrows(IllegalArgumentException.class, () -> new TcpConnection(pair.local(), FRAMING, 0, null));
			assertThrows(IllegalArgumentException.class, () -> new TcpConnection(pair.local(), FRAMING, -1, null));
		}
	}
	
	@Test
	void constructWithInvalidReadTimeout() throws Exception {
		try (SocketPair pair = pair()) {
			assertThrows(IllegalArgumentException.class, () -> new TcpConnection(pair.local(), FRAMING, 1024, Duration.ZERO));
			assertThrows(IllegalArgumentException.class, () -> new TcpConnection(pair.local(), FRAMING, 1024, Duration.ofSeconds(-1)));
		}
	}
	
	@Test
	void sendWithNullData() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			assertThrows(NullPointerException.class, () -> connection.send(null));
		}
	}
	
	@Test
	void sendTooLargeMessage() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = new TcpConnection(pair.local(), FRAMING, 4, null)) {
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> connection.send(new byte[5]));
			
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertEquals(0, pair.peer().getInputStream().available());
		}
	}
	
	@Test
	void sendTooLargeMessageOnClosedConnection() throws Exception {
		try (SocketPair pair = pair()) {
			TcpConnection connection = new TcpConnection(pair.local(), FRAMING, 4, null);
			connection.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> connection.send(new byte[5]));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void sendOnClosedConnection() throws Exception {
		try (SocketPair pair = pair()) {
			TcpConnection connection = connection(pair.local());
			connection.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> connection.send(bytes("data")));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
			assertEquals(connection.remoteEndpoint().orElseThrow(), exception.endpoint());
		}
	}
	
	@Test
	void receiveOnClosedConnection() throws Exception {
		try (SocketPair pair = pair()) {
			TcpConnection connection = connection(pair.local());
			connection.close();
			
			assertTrue(connection.receive().isEmpty());
		}
	}
	
	@Test
	void receiveTooLargeMessage() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = new TcpConnection(pair.local(), FRAMING, 4, null)) {
			writeRaw(pair.peer(), (byte) 0, (byte) 0, (byte) 0, (byte) 5, (byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::receive);
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertInstanceOf(FrameTooLargeException.class, exception.getCause());
		}
	}
	
	@Test
	void receiveTimesOut() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = new TcpConnection(pair.local(), FRAMING, 1024, Duration.ofMillis(200))) {
			NetworkTimeoutException exception = assertThrows(NetworkTimeoutException.class, connection::receive);
			
			assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
			assertEquals(Duration.ofMillis(200), exception.timeout());
		}
	}
	
	@Test
	void receivePeerClosesInsideMessage() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			writeRaw(pair.peer(), (byte) 0, (byte) 0, (byte) 0, (byte) 10, (byte) 1, (byte) 2, (byte) 3);
			pair.peer().close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::receive);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
		}
	}
	
	@Test
	void receivePeerResetsConnection() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			reset(pair.peer());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::receive);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
		}
	}
	
	@Test
	void getStreamsOnClosedConnection() throws Exception {
		try (SocketPair pair = pair()) {
			TcpConnection connection = connection(pair.local());
			connection.close();
			
			assertEquals(NetworkErrorType.SOCKET_CLOSED, assertThrows(NetworkConnectionException.class, connection::getInputStream).errorType());
			assertEquals(NetworkErrorType.SOCKET_CLOSED, assertThrows(NetworkConnectionException.class, connection::getOutputStream).errorType());
		}
	}
	
	@Test
	void mapWithNullCodec() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			assertThrows(NullPointerException.class, () -> connection.map(null));
		}
	}
	
	@Test
	void mapWithNoneFraming() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = new TcpConnection(pair.local(), Framing.none(), 1024, null)) {
			assertThrows(IllegalStateException.class, () -> connection.map(Codecs.STRING));
		}
	}
	
	@Test
	void receiveReturnsMessage() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local()); TcpConnection peer = connection(pair.peer())) {
			peer.send(bytes("hello"));
			
			assertArrayEquals(bytes("hello"), connection.receive().orElseThrow());
		}
	}
	
	@Test
	void receivePeerClosedReturnsEmpty() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			pair.peer().close();
			
			assertTrue(connection.receive().isEmpty());
			assertTrue(connection.receive().isEmpty());
		}
	}
	
	@Test
	void receiveClosedWhileWaitingReturnsEmpty() throws Exception {
		try (SocketPair pair = pair()) {
			TcpConnection connection = connection(pair.local());
			FutureTask<Optional<byte[]>> result = new FutureTask<>(connection::receive);
			Thread receiver = Thread.ofVirtual().start(result);
			
			awaitBlocked(receiver);
			connection.close();
			
			assertTrue(result.get(5, TimeUnit.SECONDS).isEmpty());
		}
	}
	
	@Test
	void receiveWithoutTimeoutUsesZeroInException() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			pair.local().setSoTimeout(100);
			
			NetworkTimeoutException exception = assertThrows(NetworkTimeoutException.class, connection::receive);
			assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
			assertEquals(Duration.ZERO, exception.timeout());
		}
	}
	
	@Test
	void sendAfterPeerReset() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			reset(pair.peer());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> {
				for (int i = 0; i < 1000; i++) {
					connection.send(new byte[512]);
					Thread.sleep(5);
				}
			});
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
		}
	}
	
	@Test
	void isActiveFalseAfterClose() throws Exception {
		try (SocketPair pair = pair()) {
			TcpConnection connection = connection(pair.local());
			assertTrue(connection.isActive());
			
			connection.close();
			
			assertFalse(connection.isActive());
		}
	}
	
	@Test
	void isActiveFalseWhenSocketClosedExternally() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			pair.local().close();
			
			assertFalse(connection.isActive());
		}
	}
	
	@Test
	void endpointsMatchSocket() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			assertEquals(pair.local().getLocalPort(), connection.localEndpoint().orElseThrow().port());
			assertEquals(pair.peer().getLocalPort(), connection.remoteEndpoint().orElseThrow().port());
			assertEquals(pair.peer().getPort(), connection.localEndpoint().orElseThrow().port());
		}
	}
	
	@Test
	void endpointsPresentAfterClose() throws Exception {
		try (SocketPair pair = pair()) {
			TcpConnection connection = connection(pair.local());
			connection.close();
			
			assertEquals(pair.local().getLocalPort(), connection.localEndpoint().orElseThrow().port());
			assertEquals(pair.peer().getLocalPort(), connection.remoteEndpoint().orElseThrow().port());
		}
	}
	
	@Test
	void getInputStreamReturnsSameInstance() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			assertSame(connection.getInputStream(), connection.getInputStream());
		}
	}
	
	@Test
	void getOutputStreamReturnsSameInstance() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			assertSame(connection.getOutputStream(), connection.getOutputStream());
		}
	}
	
	@Test
	void mapWithLengthPrefixedFraming() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			CodecClient<String> client = connection.map(Codecs.STRING);
			
			assertSame(connection, client.connection());
			assertSame(Codecs.STRING, client.codec());
		}
	}
	
	@Test
	void mapWithDelimitedFraming() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = new TcpConnection(pair.local(), Framing.delimited((byte) 0), 1024, null)) {
			assertThrows(IllegalStateException.class, () -> connection.map(Codecs.STRING));
		}
	}
	
	@Test
	void closeIsIdempotent() throws Exception {
		try (SocketPair pair = pair()) {
			TcpConnection connection = connection(pair.local());
			
			assertDoesNotThrow(connection::close);
			assertDoesNotThrow(connection::close);
			assertTrue(pair.local().isClosed());
		}
	}
	
	@Test
	void sendMessageAtMaxSize() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = new TcpConnection(pair.local(), FRAMING, 4, null); TcpConnection peer = connection(pair.peer())) {
			connection.send(bytes("abcd"));
			
			assertArrayEquals(bytes("abcd"), peer.receive().orElseThrow());
		}
	}
	
	@Test
	void sendEmptyMessage() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local()); TcpConnection peer = connection(pair.peer())) {
			connection.send(new byte[0]);
			
			assertArrayEquals(new byte[0], peer.receive().orElseThrow());
			assertTrue(connection.isActive());
		}
	}
	
	@Test
	void sendAndReceiveBetweenTwoConnections() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local()); TcpConnection peer = connection(pair.peer())) {
			connection.send(bytes("ping"));
			assertArrayEquals(bytes("ping"), peer.receive().orElseThrow());
			
			peer.send(bytes("pong"));
			assertArrayEquals(bytes("pong"), connection.receive().orElseThrow());
		}
	}
	
	@Test
	void sendWithLinesFraming() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = new TcpConnection(pair.local(), Framing.lines(), 1024, null)) {
			connection.send(bytes("hello"));
			
			assertArrayEquals(bytes("hello\r\n"), pair.peer().getInputStream().readNBytes(7));
		}
	}
	
	@Test
	void concurrentSendsDoNotInterleave() throws Exception {
		Set<String> sent = ConcurrentHashMap.newKeySet();
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local()); TcpConnection peer = connection(pair.peer());
			 ExecutorService executor = Executors.newFixedThreadPool(8)) {
			List<Future<?>> futures = new ArrayList<>();
			for (int thread = 0; thread < 8; thread++) {
				int id = thread;
				futures.add(executor.submit(() -> {
					for (int i = 0; i < 50; i++) {
						String message = (id + "-" + i + "-").repeat(300).substring(0, 1024);
						sent.add(message);
						connection.send(bytes(message));
					}
					return null;
				}));
			}
			
			Set<String> received = new HashSet<>();
			for (int i = 0; i < 400; i++) {
				received.add(new String(peer.receive().orElseThrow(), StandardCharsets.UTF_8));
			}
			for (Future<?> future : futures) {
				future.get(10, TimeUnit.SECONDS);
			}
			assertEquals(400, received.size());
			assertEquals(sent, received);
		}
	}
	
	@Test
	void concurrentReceivesDoNotSplitMessages() throws Exception {
		Set<String> sent = new HashSet<>();
		Set<String> received = ConcurrentHashMap.newKeySet();
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local()); ExecutorService executor = Executors.newFixedThreadPool(4)) {
			List<Future<?>> futures = new ArrayList<>();
			for (int thread = 0; thread < 4; thread++) {
				futures.add(executor.submit(() -> {
					Optional<byte[]> message;
					while ((message = connection.receive()).isPresent()) {
						received.add(new String(message.get(), StandardCharsets.UTF_8));
					}
					return null;
				}));
			}
			
			try (TcpConnection peer = connection(pair.peer())) {
				for (int i = 0; i < 200; i++) {
					String message = "message-" + i + "-" + "x".repeat(i);
					sent.add(message);
					peer.send(bytes(message));
				}
			}
			for (Future<?> future : futures) {
				future.get(10, TimeUnit.SECONDS);
			}
		}
		assertEquals(sent, received);
	}
	
	@Test
	void rawReadAndReceiveShareBuffer() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local())) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			out.write(bytes("PING\n"));
			FRAMING.write(out, bytes("frame"));
			writeRaw(pair.peer(), out.toByteArray());
			
			assertArrayEquals(bytes("PING\n"), connection.getInputStream().readNBytes(5));
			assertArrayEquals(bytes("frame"), connection.receive().orElseThrow());
		}
	}
	
	@Test
	void manySequentialMessages() throws Exception {
		try (SocketPair pair = pair(); TcpConnection connection = connection(pair.local()); TcpConnection peer = connection(pair.peer());
			 ExecutorService executor = Executors.newSingleThreadExecutor()) {
			Future<?> sender = executor.submit(() -> {
				for (int i = 0; i < 1000; i++) {
					byte[] message = new byte[i * 1024 / 1000 + 1];
					Arrays.fill(message, (byte) i);
					connection.send(message);
				}
				return null;
			});
			
			for (int i = 0; i < 1000; i++) {
				byte[] message = peer.receive().orElseThrow();
				assertEquals(i * 1024 / 1000 + 1, message.length);
				assertEquals((byte) i, message[0]);
			}
			sender.get(10, TimeUnit.SECONDS);
		}
	}
	
	private record SocketPair(Socket local, Socket peer) implements AutoCloseable {
		
		@Override
		public void close() throws IOException {
			this.local.close();
			this.peer.close();
		}
	}
}
