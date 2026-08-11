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
import net.luis.utils.io.network.connection.context.ConnectionContext;
import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.exception.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NetworkUtils}.<br>
 *
 * @author Luis-St
 */
class NetworkUtilsTest {
	
	@Test
	void handleErrorWithNullErrorType() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.handleError(null, null, "message", new RuntimeException()));
	}
	
	@Test
	void handleErrorWithNullMessage() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.handleError(null, NetworkErrorType.IO_ERROR, null, new RuntimeException()));
	}
	
	@Test
	void handleErrorWithNullCause() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.handleError(null, NetworkErrorType.IO_ERROR, "message", null));
	}
	
	@Test
	void handleErrorWithNullHandler() {
		assertDoesNotThrow(() -> NetworkUtils.handleError(null, NetworkErrorType.IO_ERROR, "message", new RuntimeException()));
	}
	
	@Test
	void handleErrorInvokesHandler() {
		AtomicReference<NetworkErrorType> capturedType = new AtomicReference<>();
		AtomicReference<String> capturedMessage = new AtomicReference<>();
		AtomicReference<Throwable> capturedCause = new AtomicReference<>();
		
		RuntimeException cause = new RuntimeException("test");
		NetworkUtils.handleError((conn, type, msg, c) -> {
			capturedType.set(type);
			capturedMessage.set(msg);
			capturedCause.set(c);
		}, NetworkErrorType.CONNECTION_REFUSED, "Connection failed", cause);
		
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, capturedType.get());
		assertEquals("Connection failed", capturedMessage.get());
		assertSame(cause, capturedCause.get());
	}
	
	@Test
	void handleErrorWithConnectionAndNullErrorType() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.handleError(null, new StubConnection(), null, "message", new RuntimeException()));
	}
	
	@Test
	void handleErrorWithConnectionAndNullMessage() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.handleError(null, new StubConnection(), NetworkErrorType.IO_ERROR, null, new RuntimeException()));
	}
	
	@Test
	void handleErrorWithConnectionAndNullCause() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.handleError(null, new StubConnection(), NetworkErrorType.IO_ERROR, "message", null));
	}
	
	@Test
	void handleErrorWithNullHandlerAndConnection() {
		assertDoesNotThrow(() -> NetworkUtils.handleError(null, new StubConnection(), NetworkErrorType.IO_ERROR, "message", new RuntimeException()));
	}
	
	@Test
	void handleErrorWithConnectionInvokesHandlerWithConnection() {
		AtomicReference<Connection> capturedConnection = new AtomicReference<>();
		AtomicReference<NetworkErrorType> capturedType = new AtomicReference<>();
		AtomicReference<String> capturedMessage = new AtomicReference<>();
		AtomicReference<Throwable> capturedCause = new AtomicReference<>();
		
		Connection connection = new StubConnection();
		RuntimeException cause = new RuntimeException("test");
		ErrorEventHandler handler = (conn, errorType, message, c) -> {
			capturedConnection.set(conn);
			capturedType.set(errorType);
			capturedMessage.set(message);
			capturedCause.set(c);
		};
		
		NetworkUtils.handleError(handler, connection, NetworkErrorType.CONNECTION_REFUSED, "Connection failed", cause);
		
		assertSame(connection, capturedConnection.get());
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, capturedType.get());
		assertEquals("Connection failed", capturedMessage.get());
		assertSame(cause, capturedCause.get());
	}
	
	@Test
	void handleErrorWithNullConnectionInvokesHandler() {
		AtomicReference<Connection> capturedConnection = new AtomicReference<>();
		AtomicBoolean handlerInvoked = new AtomicBoolean(false);
		
		ErrorEventHandler handler = (conn, errorType, message, cause) -> {
			capturedConnection.set(conn);
			handlerInvoked.set(true);
		};
		
		NetworkUtils.handleError(handler, null, NetworkErrorType.IO_ERROR, "message", new RuntimeException());
		
		assertTrue(handlerInvoked.get());
		assertNull(capturedConnection.get());
	}
	
	@Test
	void handleErrorFourArgOverloadPassesNullConnection() {
		AtomicReference<Connection> capturedConnection = new AtomicReference<>();
		AtomicBoolean handlerInvoked = new AtomicBoolean(false);
		
		ErrorEventHandler handler = (conn, errorType, message, cause) -> {
			capturedConnection.set(conn);
			handlerInvoked.set(true);
		};
		
		NetworkUtils.handleError(handler, NetworkErrorType.IO_ERROR, "msg", new RuntimeException());
		
		assertTrue(handlerInvoked.get());
		assertNull(capturedConnection.get());
	}
	
	@Test
	void shutdownExecutorWithNullExecutor() {
		assertDoesNotThrow(() -> NetworkUtils.shutdownExecutor(null, true));
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
	}
	
	@Test
	void shutdownExecutorWaitsForCompletion() throws InterruptedException {
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
	void writeFrameWithNullOutputStreamThrows() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.writeFrame(null, new byte[0]));
	}
	
	@Test
	void writeFrameWithNullDataThrows() {
		OutputStream out = OutputStream.nullOutputStream();
		assertThrows(NullPointerException.class, () -> NetworkUtils.writeFrame(out, null));
	}
	
	@Test
	void readFrameWithNullInputStreamThrows() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.readFrame(null, 10));
	}
	
	@Test
	void readFrameTruncatedHeaderThrowsEOFException() {
		InputStream in = new ByteArrayInputStream(new byte[] { 0, 0 });
		assertThrows(EOFException.class, () -> NetworkUtils.readFrame(in, 100));
	}
	
	@Test
	void readFrameTruncatedPayloadThrowsEOFException() {
		InputStream in = new ByteArrayInputStream(new byte[] { 0, 0, 0, 10, 1, 2, 3 });
		assertThrows(EOFException.class, () -> NetworkUtils.readFrame(in, 100));
	}
	
	@Test
	void readFrameNegativeLengthThrowsIOException() {
		InputStream in = new ByteArrayInputStream(new byte[] { -1, -1, -1, -1 });
		IOException exception = assertThrows(IOException.class, () -> NetworkUtils.readFrame(in, 100));
		assertFalse(exception instanceof FrameTooLargeException);
	}
	
	@Test
	void readFrameExceedingMaxBytesThrowsFrameTooLargeException() {
		InputStream in = new ByteArrayInputStream(new byte[] { 0, 0, 0, 20 });
		FrameTooLargeException exception = assertThrows(FrameTooLargeException.class, () -> NetworkUtils.readFrame(in, 10));
		assertEquals(20, exception.frameLength());
		assertEquals(10, exception.maxBytes());
		assertTrue(exception.getMessage().contains("20"));
		assertTrue(exception.getMessage().contains("10"));
	}
	
	@Test
	void readFrameReturnsNullOnEmptyStream() throws IOException {
		assertNull(NetworkUtils.readFrame(InputStream.nullInputStream(), 10));
	}
	
	@Test
	void readFrameAcceptsFrameEqualToMaxBytes() throws IOException {
		byte[] payload = new byte[10];
		Arrays.fill(payload, (byte) 7);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NetworkUtils.writeFrame(out, payload);
		
		byte[] received = NetworkUtils.readFrame(new ByteArrayInputStream(out.toByteArray()), 10);
		assertArrayEquals(payload, received);
	}
	
	@Test
	void readFrameWithZeroLengthPayloadReturnsEmptyArray() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NetworkUtils.writeFrame(out, new byte[0]);
		
		byte[] received = NetworkUtils.readFrame(new ByteArrayInputStream(out.toByteArray()), 10);
		assertNotNull(received);
		assertEquals(0, received.length);
	}
	
	@Test
	void writeFrameProducesCorrectHeaderBytes() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NetworkUtils.writeFrame(out, new byte[] { 1, 2, 3, 4, 5 });
		
		byte[] bytes = out.toByteArray();
		assertEquals(9, bytes.length);
		assertArrayEquals(new byte[] { 0, 0, 0, 5 }, Arrays.copyOfRange(bytes, 0, 4));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5 }, Arrays.copyOfRange(bytes, 4, 9));
	}
	
	@Test
	void writeFrameThenReadFrameRoundTripsSimpleMessage() throws IOException {
		byte[] data = "Hello".getBytes();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NetworkUtils.writeFrame(out, data);
		
		byte[] received = NetworkUtils.readFrame(new ByteArrayInputStream(out.toByteArray()), 100);
		assertArrayEquals(data, received);
	}
	
	@Test
	void writeFrameWithEmptyArrayRoundTrips() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NetworkUtils.writeFrame(out, new byte[0]);
		
		byte[] received = NetworkUtils.readFrame(new ByteArrayInputStream(out.toByteArray()), 100);
		assertNotNull(received);
		assertEquals(0, received.length);
	}
	
	@Test
	void readFrameReassemblesFragmentedDelivery() throws IOException {
		byte[] data = "Fragmented Message Body".getBytes();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NetworkUtils.writeFrame(out, data);
		
		InputStream fragmented = new FragmentedInputStream(out.toByteArray(), 3);
		byte[] received = NetworkUtils.readFrame(fragmented, 100);
		assertArrayEquals(data, received);
	}
	
	@Test
	void writeFrameThenReadFrameRoundTripsMultipleSequentialMessages() throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NetworkUtils.writeFrame(out, "First".getBytes());
		NetworkUtils.writeFrame(out, "Second".getBytes());
		
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		byte[] first = NetworkUtils.readFrame(in, 100);
		byte[] second = NetworkUtils.readFrame(in, 100);
		
		assertArrayEquals("First".getBytes(), first);
		assertArrayEquals("Second".getBytes(), second);
	}
	
	@Test
	void readFrameWithLargeBinaryPayloadRoundTrips() throws IOException {
		byte[] data = new byte[65536];
		new Random(42).nextBytes(data);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		NetworkUtils.writeFrame(out, data);
		
		byte[] received = NetworkUtils.readFrame(new ByteArrayInputStream(out.toByteArray()), data.length);
		assertArrayEquals(data, received);
	}
	
	private static final class StubConnection implements Connection {
		
		private final ConnectionContext context = new ConnectionContext();
		
		@Override
		public @NonNull ConnectionContext context() {
			return this.context;
		}
		
		@Override
		public @NonNull IpEndpoint remoteEndpoint() {
			return new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		}
		
		@Override
		public @NonNull IpEndpoint localEndpoint() {
			return new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		}
		
		@Override
		public void send(byte @NonNull [] data) {}
		
		@Override
		public byte @NonNull [] receive() {
			return new byte[0];
		}
		
		@Override
		public byte @NonNull [] receive(int maxBytes) {
			return new byte[0];
		}
		
		@Override
		public @NonNull InputStream getInputStream() {
			return InputStream.nullInputStream();
		}
		
		@Override
		public @NonNull OutputStream getOutputStream() {
			return OutputStream.nullOutputStream();
		}
		
		@Override
		public boolean isActive() {
			return true;
		}
		
		@Override
		public void close() {}
	}
	
	//region validateMessageSize
	
	@Test
	void validateMessageSizeWithNullData() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.validateMessageSize(null, 1024, null));
	}
	
	@Test
	void validateMessageSizeWithinBufferSize() {
		assertDoesNotThrow(() -> NetworkUtils.validateMessageSize(new byte[512], 1024, null));
	}
	
	@Test
	void validateMessageSizeEqualToBufferSize() {
		assertDoesNotThrow(() -> NetworkUtils.validateMessageSize(new byte[1024], 1024, null));
	}
	
	@Test
	void validateMessageSizeExceedingBufferSize() {
		NetworkConnectionException exception = assertThrows(
			NetworkConnectionException.class,
			() -> NetworkUtils.validateMessageSize(new byte[1025], 1024, null)
		);
		assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		assertTrue(exception.getMessage().contains("1025"));
		assertTrue(exception.getMessage().contains("1024"));
	}
	
	@Test
	void validateMessageSizeWithEmptyData() {
		assertDoesNotThrow(() -> NetworkUtils.validateMessageSize(new byte[0], 1024, null));
	}
	
	@Test
	void validateMessageSizeAcceptsHostEndpoint() {
		HostEndpoint endpoint = new HostEndpoint("example.com", 443);
		
		NetworkConnectionException exception = assertThrows(
			NetworkConnectionException.class,
			() -> NetworkUtils.validateMessageSize(new byte[2], 1, endpoint)
		);
		assertSame(endpoint, exception.endpoint());
	}
	//endregion
	
	//region writeAll
	
	@Test
	void writeAllWithNullSocket() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.writeAll(null, new byte[1], true, null, null, null));
	}
	
	@Test
	void writeAllWithNullData() throws Exception {
		withPair((local, peer) -> assertThrows(NullPointerException.class, () -> NetworkUtils.writeAll(local, null, true, null, null, null)));
	}
	
	@Test
	void writeAllWritesAndFlushesFramedData() throws Exception {
		withPair((local, peer) -> {
			NetworkUtils.writeAll(local, "Hello".getBytes(), true, null, null, null);
			
			byte[] framed = new byte[9];
			assertEquals(9, peer.getInputStream().read(framed));
			assertArrayEquals(new byte[] { 0, 0, 0, 5 }, Arrays.copyOfRange(framed, 0, 4));
			assertArrayEquals("Hello".getBytes(), Arrays.copyOfRange(framed, 4, 9));
		});
	}
	
	@Test
	void writeAllWithEmptyData() throws Exception {
		withPair((local, peer) -> assertDoesNotThrow(() -> NetworkUtils.writeAll(local, new byte[0], true, null, null, null)));
	}
	
	@Test
	void writeAllOnClosedSocketRunsDisconnect() throws Exception {
		withPair((local, peer) -> {
			AtomicBoolean disconnected = new AtomicBoolean(false);
			local.close();
			
			assertThrows(NetworkConnectionException.class, () -> NetworkUtils.writeAll(local, new byte[1], true, null, null, () -> disconnected.set(true)));
			assertTrue(disconnected.get());
		});
	}
	
	@Test
	void writeAllOnClosedSocketWithNullDisconnect() throws Exception {
		withPair((local, peer) -> {
			local.close();
			assertThrows(NetworkConnectionException.class, () -> NetworkUtils.writeAll(local, new byte[1], true, null, null, null));
		});
	}
	
	@Test
	void writeAllOnClosedSocketNotifiesErrorHandler() throws Exception {
		RecordingErrorHandler handler = new RecordingErrorHandler();
		
		NetworkConnectionException exception = assertThrows(
			NetworkConnectionException.class,
			() -> NetworkUtils.writeAll(new FailingSocket(), new byte[1], true, handler, null, null)
		);
		assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
		assertEquals(1, handler.errorTypes.size());
		assertEquals(NetworkErrorType.IO_ERROR, handler.errorTypes.getFirst());
	}
	
	@Test
	void writeAllOnResetDoesNotNotifyErrorHandler() throws Exception {
		withPair((local, peer) -> {
			RecordingErrorHandler handler = new RecordingErrorHandler();
			AtomicBoolean disconnected = new AtomicBoolean(false);
			local.close();
			
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> NetworkUtils.writeAll(local, new byte[1], true, handler, null, () -> disconnected.set(true))
			);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
			assertTrue(disconnected.get());
			assertTrue(handler.errorTypes.isEmpty());
		});
	}
	
	@Test
	void writeAllAttachesEndpointToException() throws Exception {
		withPair((local, peer) -> {
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
			local.close();
			
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> NetworkUtils.writeAll(local, new byte[1], true, null, endpoint, null)
			);
			assertSame(endpoint, exception.endpoint());
		});
	}
	//endregion
	
	//region readAvailable
	
	@Test
	void readAvailableWithNullSocket() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.readAvailable(null, null, 16, true, Duration.ofSeconds(1), null, null, null));
	}
	
	@Test
	void readAvailableWithNullReadTimeout() throws Exception {
		withPair((local, peer) -> assertThrows(NullPointerException.class, () -> NetworkUtils.readAvailable(local, null, 16, true, null, null, null, null)));
	}
	
	@Test
	void readAvailableReturnsReceivedData() throws Exception {
		withPair((local, peer) -> {
			NetworkUtils.writeFrame(peer.getOutputStream(), "Hello".getBytes());
			
			byte[] received = NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, null, null);
			assertEquals(5, received.length);
			assertArrayEquals("Hello".getBytes(), received);
		});
	}
	
	@Test
	void readAvailableReturnsExactPayloadLength() throws Exception {
		withPair((local, peer) -> {
			NetworkUtils.writeFrame(peer.getOutputStream(), new byte[] { 1, 2, 3 });
			
			byte[] received = NetworkUtils.readAvailable(local, null, 4096, true, Duration.ofSeconds(5), null, null, null);
			assertEquals(3, received.length);
			assertArrayEquals(new byte[] { 1, 2, 3 }, received);
		});
	}
	
	@Test
	void readAvailableReturnsEmptyArrayOnPeerClose() throws Exception {
		withPair((local, peer) -> {
			AtomicBoolean disconnected = new AtomicBoolean(false);
			peer.close();
			
			byte[] received = NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, null, () -> disconnected.set(true));
			assertEquals(0, received.length);
			assertTrue(disconnected.get());
		});
	}
	
	@Test
	void readAvailableOnPeerCloseWithNullDisconnect() throws Exception {
		withPair((local, peer) -> {
			peer.close();
			
			byte[] received = assertDoesNotThrow(() -> NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, null, null));
			assertEquals(0, received.length);
		});
	}
	
	@Test
	void readAvailableTimesOut() throws Exception {
		withPair((local, peer) -> {
			local.setSoTimeout(100);
			Duration readTimeout = Duration.ofMillis(100);
			
			NetworkTimeoutException exception = assertThrows(
				NetworkTimeoutException.class,
				() -> NetworkUtils.readAvailable(local, null, 1024, true, readTimeout, null, null, null)
			);
			assertEquals(NetworkErrorType.READ_TIMEOUT, exception.errorType());
			assertEquals(readTimeout, exception.timeout());
		});
	}
	
	@Test
	void readAvailableTimeoutDoesNotRunDisconnect() throws Exception {
		withPair((local, peer) -> {
			AtomicBoolean disconnected = new AtomicBoolean(false);
			local.setSoTimeout(100);
			
			assertThrows(
				NetworkTimeoutException.class,
				() -> NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofMillis(100), null, null, () -> disconnected.set(true))
			);
			assertFalse(disconnected.get());
		});
	}
	
	@Test
	void readAvailableOnClosedSocketRunsDisconnect() throws Exception {
		withPair((local, peer) -> {
			AtomicBoolean disconnected = new AtomicBoolean(false);
			local.close();
			
			assertThrows(
				NetworkConnectionException.class,
				() -> NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, null, () -> disconnected.set(true))
			);
			assertTrue(disconnected.get());
		});
	}
	
	@Test
	void readAvailableOnClosedSocketWithNullDisconnect() throws Exception {
		withPair((local, peer) -> {
			local.close();
			assertThrows(
				NetworkConnectionException.class,
				() -> NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, null, null)
			);
		});
	}
	
	@Test
	void readAvailableOnResetDoesNotNotifyErrorHandler() throws Exception {
		withPair((local, peer) -> {
			RecordingErrorHandler handler = new RecordingErrorHandler();
			AtomicBoolean disconnected = new AtomicBoolean(false);
			local.close();
			
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), handler, null, () -> disconnected.set(true))
			);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
			assertTrue(disconnected.get());
			assertTrue(handler.errorTypes.isEmpty());
		});
	}
	
	@Test
	void readAvailableNotifiesErrorHandlerOnIoError() {
		RecordingErrorHandler handler = new RecordingErrorHandler();
		
		NetworkConnectionException exception = assertThrows(
			NetworkConnectionException.class,
			() -> NetworkUtils.readAvailable(new FailingSocket(), null, 16, true, Duration.ofSeconds(1), handler, null, null)
		);
		assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
		assertEquals(1, handler.errorTypes.size());
		assertEquals(NetworkErrorType.IO_ERROR, handler.errorTypes.getFirst());
	}
	
	@Test
	void readAvailableAttachesEndpointToException() throws Exception {
		withPair((local, peer) -> {
			IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
			local.close();
			
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, endpoint, null)
			);
			assertSame(endpoint, exception.endpoint());
		});
	}
	
	@Test
	void readAvailableReadsSequentialFramesIndividually() throws Exception {
		withPair((local, peer) -> {
			byte[] first = new byte[100];
			Arrays.fill(first, (byte) 0x41);
			
			NetworkUtils.writeFrame(peer.getOutputStream(), first);
			NetworkUtils.writeFrame(peer.getOutputStream(), "abcde".getBytes());
			
			assertArrayEquals(first, NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, null, null));
			assertArrayEquals("abcde".getBytes(), NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, null, null));
		});
	}
	
	@Test
	void readAvailableRejectsFrameLargerThanMaxBytes() throws Exception {
		withPair((local, peer) -> {
			NetworkUtils.writeFrame(peer.getOutputStream(), new byte[2048]);
			
			NetworkConnectionException exception = assertThrows(
				NetworkConnectionException.class,
				() -> NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, null, null)
			);
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		});
	}
	
	@Test
	void writeAllAndReadAvailableRoundTrip() throws Exception {
		withPair((local, peer) -> {
			byte[] small = "small".getBytes();
			NetworkUtils.writeAll(local, small, true, null, null, null);
			assertArrayEquals(small, NetworkUtils.readFrame(peer.getInputStream(), 4096));
			
			byte[] large = new byte[2048];
			Arrays.fill(large, (byte) 0x42);
			NetworkUtils.writeAll(peer, large, true, null, null, null);
			assertArrayEquals(large, NetworkUtils.readAvailable(local, null, 4096, true, Duration.ofSeconds(5), null, null, null));
		});
	}
	//endregion
	
	@Test
	void readAvailableWithoutFramingRequiresBuffer() throws Exception {
		withPair((local, peer) -> assertThrows(
			NullPointerException.class,
			() -> NetworkUtils.readAvailable(local, null, 1024, false, Duration.ofSeconds(5), null, null, null)
		));
	}
	
	@Test
	void readAvailableWithoutFramingReturnsRawBytes() throws Exception {
		withPair((local, peer) -> {
			peer.getOutputStream().write("Hello".getBytes());
			peer.getOutputStream().flush();
			
			byte[] received = NetworkUtils.readAvailable(local, new byte[1024], 1024, false, Duration.ofSeconds(5), null, null, null);
			assertArrayEquals("Hello".getBytes(), received);
		});
	}
	
	@Test
	void writeAllWithoutFramingWritesNoHeader() throws Exception {
		withPair((local, peer) -> {
			NetworkUtils.writeAll(local, "Hello".getBytes(), false, null, null, null);
			
			byte[] received = new byte[5];
			assertEquals(5, peer.getInputStream().read(received));
			assertArrayEquals("Hello".getBytes(), received);
		});
	}
	
	@Test
	void unframedRoundTripCoalescesAdjacentWrites() throws Exception {
		withPair((local, peer) -> {
			NetworkUtils.writeAll(peer, "AAA".getBytes(), false, null, null, null);
			NetworkUtils.writeAll(peer, "BBB".getBytes(), false, null, null, null);
			
			// without framing the two writes may arrive as one read, which is exactly what framing prevents
			byte[] received = NetworkUtils.readAvailable(local, new byte[1024], 1024, false, Duration.ofSeconds(5), null, null, null);
			assertTrue(received.length == 3 || received.length == 6);
		});
	}
	
	@Test
	void framedRoundTripKeepsAdjacentWritesSeparate() throws Exception {
		withPair((local, peer) -> {
			NetworkUtils.writeAll(peer, "AAA".getBytes(), true, null, null, null);
			NetworkUtils.writeAll(peer, "BBB".getBytes(), true, null, null, null);
			
			assertArrayEquals("AAA".getBytes(), NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, null, null));
			assertArrayEquals("BBB".getBytes(), NetworkUtils.readAvailable(local, null, 1024, true, Duration.ofSeconds(5), null, null, null));
		});
	}
	//endregion
	
	//region mapConnectFailure
	
	@Test
	void mapConnectFailureWithNullCause() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.mapConnectFailure(null, ENDPOINT, Duration.ofSeconds(1), null));
	}
	
	@Test
	void mapConnectFailureWithNullEndpoint() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.mapConnectFailure(new IOException("x"), null, Duration.ofSeconds(1), null));
	}
	
	@Test
	void mapConnectFailureWithNullConnectTimeout() {
		assertThrows(NullPointerException.class, () -> NetworkUtils.mapConnectFailure(new IOException("x"), ENDPOINT, null, null));
	}
	
	@Test
	void mapConnectFailureWithTimeout() {
		Duration connectTimeout = Duration.ofSeconds(3);
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(new SocketTimeoutException("t"), ENDPOINT, connectTimeout, null);
		NetworkTimeoutException timeout = assertInstanceOf(NetworkTimeoutException.class, mapped);
		assertEquals(NetworkErrorType.CONNECTION_TIMEOUT, timeout.errorType());
		assertEquals(connectTimeout, timeout.timeout());
		assertSame(ENDPOINT, timeout.endpoint());
		assertTrue(timeout.getMessage().contains(ENDPOINT.toString()));
	}
	
	@Test
	void mapConnectFailureWithHandshakeFailure() {
		SSLHandshakeException cause = new SSLHandshakeException("handshake");
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1), null);
		assertEquals(NetworkErrorType.HANDSHAKE_FAILED, mapped.errorType());
		assertSame(cause, mapped.getCause());
		assertFalse(mapped instanceof NetworkTimeoutException);
	}
	
	@Test
	void mapConnectFailureWithConnectionRefused() {
		ConnectException cause = new ConnectException("refused");
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1), null);
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, mapped.errorType());
		assertSame(cause, mapped.getCause());
	}
	
	@Test
	void mapConnectFailureWithNoRouteToHost() {
		NoRouteToHostException cause = new NoRouteToHostException("no route");
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1), null);
		assertEquals(NetworkErrorType.HOST_UNREACHABLE, mapped.errorType());
		assertSame(cause, mapped.getCause());
	}
	
	@Test
	void mapConnectFailureWithGenericIoException() {
		IOException cause = new IOException("boom");
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1), null);
		assertEquals(NetworkErrorType.CONNECTION_FAILED, mapped.errorType());
		assertSame(cause, mapped.getCause());
	}
	
	@Test
	void mapConnectFailureNotifiesErrorHandler() {
		RecordingErrorHandler handler = new RecordingErrorHandler();
		ConnectException cause = new ConnectException("refused");
		
		NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1), handler);
		assertEquals(1, handler.errorTypes.size());
		assertEquals(NetworkErrorType.CONNECTION_REFUSED, handler.errorTypes.getFirst());
		assertSame(cause, handler.causes.getFirst());
	}
	
	@Test
	void mapConnectFailureWithNullErrorHandler() {
		NetworkConnectionException mapped = assertDoesNotThrow(
			() -> NetworkUtils.mapConnectFailure(new ConnectException("refused"), ENDPOINT, Duration.ofSeconds(1), null)
		);
		assertNotNull(mapped);
	}
	
	@Test
	void mapConnectFailureNotifiesHandlerForEveryArm() {
		RecordingErrorHandler handler = new RecordingErrorHandler();
		List<IOException> causes = List.of(
			new SocketTimeoutException("t"),
			new SSLHandshakeException("h"),
			new ConnectException("r"),
			new NoRouteToHostException("n"),
			new IOException("g")
		);
		
		causes.forEach(cause -> NetworkUtils.mapConnectFailure(cause, ENDPOINT, Duration.ofSeconds(1), handler));
		
		List<NetworkErrorType> expected = List.of(
			NetworkErrorType.CONNECTION_TIMEOUT,
			NetworkErrorType.HANDSHAKE_FAILED,
			NetworkErrorType.CONNECTION_REFUSED,
			NetworkErrorType.HOST_UNREACHABLE,
			NetworkErrorType.CONNECTION_FAILED
		);
		assertEquals(expected, handler.errorTypes);
	}
	
	@Test
	void mapConnectFailureReturnsRatherThanThrows() {
		NetworkConnectionException mapped = assertDoesNotThrow(
			() -> NetworkUtils.mapConnectFailure(new IOException("boom"), ENDPOINT, Duration.ofSeconds(1), null)
		);
		assertNotNull(mapped);
	}
	
	@Test
	void mapConnectFailureWithHostEndpoint() {
		HostEndpoint endpoint = new HostEndpoint("example.com", 443);
		
		NetworkConnectionException mapped = NetworkUtils.mapConnectFailure(new IOException("boom"), endpoint, Duration.ofSeconds(1), null);
		assertTrue(mapped.getMessage().contains("example.com:443"));
		assertSame(endpoint, mapped.endpoint());
	}
	//endregion
	
	//region shutdownExecutor
	
	@Test
	void shutdownExecutorForcesShutdownOnTimeout() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		CountDownLatch started = new CountDownLatch(1);
		executor.submit(() -> {
			started.countDown();
			long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(20);
			while (System.nanoTime() < deadline) {
				Thread.onSpinWait();
			}
		});
		assertTrue(started.await(5, TimeUnit.SECONDS));
		
		long elapsed = System.nanoTime();
		NetworkUtils.shutdownExecutor(executor, true);
		elapsed = System.nanoTime() - elapsed;
		
		assertTrue(executor.isShutdown());
		assertTrue(TimeUnit.NANOSECONDS.toSeconds(elapsed) < 15);
	}
	
	@Test
	void shutdownExecutorRestoresInterruptFlagWhenInterrupted() throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		CountDownLatch started = new CountDownLatch(1);
		executor.submit(() -> {
			started.countDown();
			long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(20);
			while (System.nanoTime() < deadline) {
				Thread.onSpinWait();
			}
		});
		assertTrue(started.await(5, TimeUnit.SECONDS));
		
		AtomicBoolean interruptFlag = new AtomicBoolean(false);
		Thread shutdownThread = new Thread(() -> {
			NetworkUtils.shutdownExecutor(executor, true);
			interruptFlag.set(Thread.currentThread().isInterrupted());
		});
		shutdownThread.start();
		Thread.sleep(200);
		shutdownThread.interrupt();
		shutdownThread.join(TimeUnit.SECONDS.toMillis(10));
		
		assertTrue(interruptFlag.get());
		assertTrue(executor.isShutdown());
	}
	//endregion
	
	//region Helper methods and test doubles
	
	private static final IpEndpoint ENDPOINT = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
	
	private static void withPair(PairConsumer body) throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (Socket local = new Socket("127.0.0.1", port);
				 Socket peer = serverSocket.accept()) {
				body.accept(local, peer);
			}
		}
	}
	
	@FunctionalInterface
	private interface PairConsumer {
		
		void accept(Socket local, Socket peer) throws Exception;
	}
	
	/**
	 * Records every error reported through {@link ErrorEventHandler} so tests can assert on
	 * whether a handler was notified at all, and with which type.<br>
	 */
	private static final class RecordingErrorHandler implements ErrorEventHandler {
		
		private final List<NetworkErrorType> errorTypes = new ArrayList<>();
		private final List<Throwable> causes = new ArrayList<>();
		
		@Override
		public void handle(Connection connection, @NonNull NetworkErrorType errorType, @NonNull String message, @NonNull Throwable cause) {
			this.errorTypes.add(errorType);
			this.causes.add(cause);
		}
	}
	
	/**
	 * A socket whose streams fail with a plain {@link IOException} rather than a {@link java.net.SocketException},
	 * which is the only way to reach the {@code IO_ERROR} arm of the shared read and write paths.<br>
	 */
	private static final class FailingSocket extends Socket {
		
		@Override
		public InputStream getInputStream() throws IOException {
			throw new IOException("stream unavailable");
		}
		
		@Override
		public OutputStream getOutputStream() throws IOException {
			throw new IOException("stream unavailable");
		}
	}
	
	private static final class FragmentedInputStream extends InputStream {
		
		private final byte[] data;
		private final int chunkSize;
		private int position;
		
		private FragmentedInputStream(byte[] data, int chunkSize) {
			this.data = data;
			this.chunkSize = chunkSize;
		}
		
		@Override
		public int read() {
			if (this.position >= this.data.length) {
				return -1;
			}
			return this.data[this.position++] & 0xFF;
		}
		
		@Override
		public int read(byte @NonNull [] b, int off, int len) {
			if (this.position >= this.data.length) {
				return -1;
			}
			
			int toCopy = Math.min(Math.min(len, this.chunkSize), this.data.length - this.position);
			System.arraycopy(this.data, this.position, b, off, toCopy);
			this.position += toCopy;
			return toCopy;
		}
	}
	//endregion
}
