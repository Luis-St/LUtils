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
import net.luis.utils.io.network.connection.event.ErrorEventHandler;
import net.luis.utils.io.network.connection.exception.FrameTooLargeException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
}
