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
import net.luis.utils.io.network.connection.Connection;
import net.luis.utils.io.network.connection.NetworkUtils;
import net.luis.utils.io.network.connection.context.ConnectionContext;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.*;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SslConnection}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SslConnectionTest {
	
	private static SSLContext serverContext;
	private static SSLContext clientContext;
	
	@BeforeAll
	static void setUp() throws Exception {
		serverContext = SslTestContext.serverContext();
		clientContext = SslTestContext.clientContext();
	}
	
	private static byte[] filled(int length, byte value) {
		byte[] data = new byte[length];
		Arrays.fill(data, value);
		return data;
	}
	
	private static void writeAndFlush(SSLSocket socket, byte[] data) throws Exception {
		NetworkUtils.writeFrame(socket.getOutputStream(), data);
	}
	
	private static byte[] receiveExactly(SslConnection connection, int expected, int maxBytes) throws Exception {
		byte[] received = connection.receive(maxBytes);
		assertEquals(expected, received.length);
		return received;
	}
	
	private static byte[] readUntil(SslConnection connection, int expected, int maxBytes) throws Exception {
		ByteArrayOutputStream reassembled = new ByteArrayOutputStream();
		while (reassembled.size() < expected) {
			reassembled.writeBytes(connection.receive(maxBytes));
		}
		return reassembled.toByteArray();
	}
	
	@Test
	void sendWithNullDataThrows() throws Exception {
		this.withPair(8192, (client, connection) -> assertThrows(NullPointerException.class, () -> connection.send(null)));
	}
	
	@Test
	void sendWithValidData() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] data = "Hello".getBytes();
			assertDoesNotThrow(() -> connection.send(data));
			
			InputStream clientIn = client.getInputStream();
			byte[] header = clientIn.readNBytes(4);
			int declaredLength = ((header[0] & 0xFF) << 24) | ((header[1] & 0xFF) << 16) | ((header[2] & 0xFF) << 8) | (header[3] & 0xFF);
			assertEquals(data.length, declaredLength);
			
			byte[] received = clientIn.readNBytes(data.length);
			assertArrayEquals(data, received);
		});
	}
	
	@Test
	void sendDataExceedingBufferSizeThrows() throws Exception {
		this.withPair(100, (client, connection) -> {
			byte[] largeData = new byte[101];
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> connection.send(largeData));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		});
	}
	
	@Test
	void sendWhenConnectionClosedThrows() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.close();
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> connection.send("test".getBytes()));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		});
	}
	
	@Test
	void receiveReturnsData() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] dataToSend = "Hello World".getBytes();
			NetworkUtils.writeFrame(client.getOutputStream(), dataToSend);
			
			byte[] received = connection.receive();
			assertArrayEquals(dataToSend, received);
		});
	}
	
	@Test
	void receiveWithMaxBytesSmallerThanFrameThrows() throws Exception {
		this.withPair(8192, (client, connection) -> {
			NetworkUtils.writeFrame(client.getOutputStream(), "Hello World".getBytes());
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> connection.receive(5));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
			assertTrue(exception.getMessage().contains("11"));
			assertTrue(exception.getMessage().contains("5"));
		});
	}
	
	@Test
	void receiveWithMaxBytesLargerThanFrame() throws Exception {
		this.withPair(8192, (client, connection) -> {
			NetworkUtils.writeFrame(client.getOutputStream(), "Hello".getBytes());
			
			byte[] received = connection.receive(100);
			assertArrayEquals("Hello".getBytes(), received);
		});
	}
	
	@Test
	void receiveWithMaxBytesEqualToFrameLength() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] dataToSend = "Hello World".getBytes();
			NetworkUtils.writeFrame(client.getOutputStream(), dataToSend);
			
			byte[] received = connection.receive(dataToSend.length);
			assertArrayEquals(dataToSend, received);
		});
	}
	
	@Test
	void receiveWithZeroMaxBytesThrows() throws Exception {
		this.withPair(8192, (client, connection) -> assertThrows(IllegalArgumentException.class, () -> connection.receive(0)));
	}
	
	@Test
	void receiveWithNegativeMaxBytesThrows() throws Exception {
		this.withPair(8192, (client, connection) -> assertThrows(IllegalArgumentException.class, () -> connection.receive(-1)));
	}
	
	@Test
	void receiveWhenConnectionClosedThrows() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.close();
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::receive);
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		});
	}
	
	@Test
	void receiveReturnsEmptyArrayOnPeerClose() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.close();
			byte[] received = connection.receive();
			assertEquals(0, received.length);
		});
	}
	
	@Test
	void receiveThrowsOnPeerCloseMidHeader() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.getOutputStream().write(new byte[] { 0, 0 });
			client.getOutputStream().flush();
			client.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::receive);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
		});
	}
	
	@Test
	void receiveThrowsOnPeerCloseMidPayload() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.getOutputStream().write(new byte[] { 0, 0, 0, 10, 1, 2, 3, 4 });
			client.getOutputStream().flush();
			client.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::receive);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
		});
	}
	
	@Test
	void receiveEmptyMessageDoesNotCloseConnection() throws Exception {
		this.withPair(8192, (client, connection) -> {
			NetworkUtils.writeFrame(client.getOutputStream(), new byte[0]);
			byte[] received = connection.receive();
			assertNotNull(received);
			assertEquals(0, received.length);
			assertTrue(connection.isActive());
			
			byte[] second = "Still Alive".getBytes();
			NetworkUtils.writeFrame(client.getOutputStream(), second);
			byte[] receivedSecond = connection.receive();
			assertArrayEquals(second, receivedSecond);
		});
	}
	
	@Test
	void receiveReassemblesMessageDeliveredInFragmentedWrites() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] payload = "Reassembled Across Fragments".getBytes();
			ByteArrayOutputStream frameBytes = new ByteArrayOutputStream();
			NetworkUtils.writeFrame(frameBytes, payload);
			byte[] frame = frameBytes.toByteArray();
			
			OutputStream clientOut = client.getOutputStream();
			for (int i = 0; i < frame.length; i += 3) {
				int end = Math.min(i + 3, frame.length);
				clientOut.write(frame, i, end - i);
				clientOut.flush();
				Thread.sleep(10);
			}
			
			byte[] received = connection.receive();
			assertArrayEquals(payload, received);
		});
	}
	
	@Test
	void getInputStreamReturnsStream() throws Exception {
		this.withPair(8192, (client, connection) -> {
			InputStream inputStream = connection.getInputStream();
			assertNotNull(inputStream);
		});
	}
	
	@Test
	void getInputStreamWhenClosedThrows() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.close();
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::getInputStream);
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		});
	}
	
	@Test
	void getOutputStreamReturnsStream() throws Exception {
		this.withPair(8192, (client, connection) -> {
			OutputStream outputStream = connection.getOutputStream();
			assertNotNull(outputStream);
		});
	}
	
	@Test
	void getOutputStreamWhenClosedThrows() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.close();
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::getOutputStream);
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		});
	}
	
	@Test
	void getSessionReturnsNegotiatedSession() throws Exception {
		this.withPair(8192, (client, connection) -> {
			SSLSession session = connection.getSession();
			assertNotNull(session);
			assertTrue(session.getProtocol().startsWith("TLS"));
			assertNotNull(session.getCipherSuite());
		});
	}
	
	@Test
	void isActiveReturnsTrueForActiveConnection() throws Exception {
		this.withPair(8192, (client, connection) -> assertTrue(connection.isActive()));
	}
	
	@Test
	void isActiveReturnsFalseAfterClose() throws Exception {
		this.withPair(8192, (client, connection) -> {
			assertTrue(connection.isActive());
			connection.close();
			assertFalse(connection.isActive());
		});
	}
	
	@Test
	void localEndpointReturnsCorrectEndpoint() throws Exception {
		this.withPair(8192, (client, connection) -> {
			IpEndpoint localEndpoint = connection.localEndpoint();
			assertNotNull(localEndpoint);
			assertEquals(client.getPort(), localEndpoint.port());
		});
	}
	
	@Test
	void remoteEndpointReturnsCorrectEndpoint() throws Exception {
		this.withPair(8192, (client, connection) -> {
			IpEndpoint remoteEndpoint = connection.remoteEndpoint();
			assertNotNull(remoteEndpoint);
			assertEquals(client.getLocalPort(), remoteEndpoint.port());
		});
	}
	
	@Test
	void closeIsIdempotent() throws Exception {
		this.withPair(8192, (client, connection) -> {
			assertDoesNotThrow(() -> {
				connection.close();
				connection.close();
				connection.close();
			});
			assertFalse(connection.isActive());
		});
	}
	
	@Test
	void implementsConnectionInterface() throws Exception {
		this.withPair(8192, (client, connection) -> assertInstanceOf(Connection.class, connection));
	}
	
	@Test
	void constructWithSocketBufferSizeAndTimeout() throws Exception {
		this.withPair(8192, (client, connection) -> {
			assertTrue(connection.isActive());
			assertEquals(client.getLocalPort(), connection.remoteEndpoint().port());
			assertEquals(client.getPort(), connection.localEndpoint().port());
		});
	}
	
	@Test
	void constructWithNullSocket() {
		assertThrows(NullPointerException.class, () -> new SslConnection(null, 8192, true, Duration.ofSeconds(5)));
	}
	
	@Test
	void constructWithNullReadTimeout() throws Exception {
		this.withPair(8192, (client, connection) -> assertThrows(NullPointerException.class, () -> new SslConnection(client, 8192, true, null)));
	}
	
	@Test
	void receiveReadsSequentialFramesAcrossCalls() throws Exception {
		this.withPair(8192, (client, connection) -> {
			writeAndFlush(client, "first".getBytes());
			assertArrayEquals("first".getBytes(), receiveExactly(connection, 5, 1024));
			
			writeAndFlush(client, "second".getBytes());
			assertArrayEquals("second".getBytes(), receiveExactly(connection, 6, 1024));
		});
	}
	
	@Test
	void receiveHandlesIncreasingMaxBytes() throws Exception {
		this.withPair(8192, (client, connection) -> {
			writeAndFlush(client, new byte[8]);
			assertEquals(8, receiveExactly(connection, 8, 16).length);
			
			byte[] large = filled(500, (byte) 0x42);
			writeAndFlush(client, large);
			assertArrayEquals(large, receiveExactly(connection, 500, 4096));
		});
	}
	
	@Test
	void receiveRejectsFrameLargerThanMaxBytes() throws Exception {
		this.withPair(8192, (client, connection) -> {
			writeAndFlush(client, new byte[10]);
			assertEquals(10, receiveExactly(connection, 10, 4096).length);
			
			writeAndFlush(client, filled(50, (byte) 0x43));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> connection.receive(8));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		});
	}
	
	@Test
	void receiveDoesNotLeakPreviousPayload() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] first = filled(200, (byte) 0x41);
			writeAndFlush(client, first);
			assertArrayEquals(first, receiveExactly(connection, 200, 1024));
			
			writeAndFlush(client, "abc".getBytes());
			byte[] second = receiveExactly(connection, 3, 1024);
			assertEquals(3, second.length);
			assertArrayEquals("abc".getBytes(), second);
		});
	}
	
	@Test
	void receiveAfterPeerCloseReturnsEmptyArray() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.close();
			assertEquals(0, connection.receive(1024).length);
		});
	}
	
	@Test
	void receiveMultipleTimesWithVaryingSizes() throws Exception {
		this.withPair(8192, (client, connection) -> {
			int[] sizes = { 10, 500, 20, 2000, 5 };
			for (int size : sizes) {
				byte[] payload = filled(size, (byte) (size % 128));
				writeAndFlush(client, payload);
				assertArrayEquals(payload, receiveExactly(connection, size, 4096));
			}
		});
	}
	
	@Test
	void receiveReturnedArraysAreIndependent() throws Exception {
		this.withPair(8192, (client, connection) -> {
			writeAndFlush(client, "first".getBytes());
			byte[] first = receiveExactly(connection, 5, 1024);
			
			writeAndFlush(client, "second".getBytes());
			byte[] second = receiveExactly(connection, 6, 1024);
			
			assertArrayEquals("first".getBytes(), first);
			assertNotSame(first, second);
		});
	}
	
	@Test
	void sendAndReceiveRoundTripWithVaryingSizes() throws Exception {
		this.withPair(8192, (client, connection) -> {
			byte[] large = filled(2000, (byte) 0x44);
			writeAndFlush(client, large);
			assertArrayEquals(large, receiveExactly(connection, 2000, 4096));
			
			connection.send(new byte[] { 0x7F });
			assertArrayEquals(new byte[] { 0x7F }, NetworkUtils.readFrame(client.getInputStream(), 4096));
			
			writeAndFlush(client, new byte[] { 0x01 });
			assertArrayEquals(new byte[] { 0x01 }, receiveExactly(connection, 1, 4096));
		});
	}
	
	@Test
	void receiveLargePayloadSpanningMultipleTlsRecords() throws Exception {
		this.withPair(65536, (client, connection) -> {
			byte[] payload = filled(65536, (byte) 0x45);
			Thread writer = new Thread(() -> {
				try {
					writeAndFlush(client, payload);
				} catch (Exception _) {}
			});
			writer.start();
			
			byte[] received = receiveExactly(connection, payload.length, 65536);
			writer.join(TimeUnit.SECONDS.toMillis(15));
			assertArrayEquals(payload, received);
		});
	}
	
	@Test
	void contextIsInitiallyEmpty() throws Exception {
		this.withPair(8192, (client, connection) -> {
			ConnectionContext context = connection.context();
			
			assertNotNull(context);
			assertTrue(context.isEmpty());
			assertEquals(0, context.size());
		});
	}
	
	@Test
	void contextReturnsSameInstanceOnEveryCall() throws Exception {
		this.withPair(8192, (client, connection) -> assertSame(connection.context(), connection.context()));
	}
	
	@Test
	void contextRetainsStoredValues() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.context().set("user", "Luis");
			
			assertEquals("Luis", connection.context().getString("user").orElseThrow());
			assertEquals(1, connection.context().size());
		});
	}
	
	@Test
	void contextIsAccessibleAfterClose() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.context().set("user", "Luis");
			connection.close();
			
			assertFalse(connection.isActive());
			assertEquals("Luis", assertDoesNotThrow(() -> connection.context().getString("user").orElseThrow()));
		});
	}
	
	@Test
	void contextIsIndependentPerConnection() throws Exception {
		AtomicReference<ConnectionContext> firstContext = new AtomicReference<>();
		this.withPair(8192, (client, connection) -> {
			connection.context().set("user", "Luis");
			firstContext.set(connection.context());
		});
		
		this.withPair(8192, (client, connection) -> {
			assertNotSame(firstContext.get(), connection.context());
			assertTrue(connection.context().isEmpty());
			assertFalse(connection.context().contains("user"));
		});
	}
	
	@Test
	void contextIsUnaffectedByHandshake() throws Exception {
		this.withPair(8192, (client, connection) -> {
			connection.context().set("user", "Luis");
			
			writeAndFlush(client, "Hello".getBytes());
			assertArrayEquals("Hello".getBytes(), connection.receive());
			connection.send("World".getBytes());
			
			assertEquals("Luis", connection.context().getString("user").orElseThrow());
			assertNotNull(connection.getSession());
		});
	}
	
	@Test
	void constructWithFramingDisabled() throws Exception {
		this.withPair(8192, false, (client, connection) -> {
			assertTrue(connection.isActive());
			assertNotNull(connection.getSession());
		});
	}
	
	@Test
	void sendWithoutFramingWritesRawBytes() throws Exception {
		this.withPair(8192, false, (client, connection) -> {
			byte[] data = "Hello".getBytes();
			connection.send(data);
			
			assertArrayEquals(data, client.getInputStream().readNBytes(5));
		});
	}
	
	@Test
	void receiveWithoutFramingReturnsAvailableBytes() throws Exception {
		this.withPair(8192, false, (client, connection) -> {
			client.getOutputStream().write("Hello".getBytes());
			client.getOutputStream().flush();
			
			assertArrayEquals("Hello".getBytes(), readUntil(connection, 5, 8192));
		});
	}
	
	@Test
	void receiveWithFramingKeepsMessageBoundaries() throws Exception {
		this.withPair(8192, true, (client, connection) -> {
			writeAndFlush(client, "Hello".getBytes());
			writeAndFlush(client, "World".getBytes());
			
			assertArrayEquals("Hello".getBytes(), connection.receive());
			assertArrayEquals("World".getBytes(), connection.receive());
		});
	}
	
	@Test
	void constructWithUnconnectedSocketDoesNotOpenStreams() throws Exception {
		try (SSLSocket socket = (SSLSocket) clientContext.getSocketFactory().createSocket()) {
			SslConnection connection = assertDoesNotThrow(() -> new SslConnection(socket, 8192, true, Duration.ofSeconds(5)));
			
			assertFalse(connection.isActive());
		}
	}
	
	@Test
	void getInputStreamReturnsSameInstance() throws Exception {
		this.withPair(8192, (client, connection) -> {
			InputStream first = connection.getInputStream();
			
			assertSame(first, connection.getInputStream());
			assertInstanceOf(BufferedInputStream.class, first);
		});
	}
	
	@Test
	void getOutputStreamReturnsSameInstance() throws Exception {
		this.withPair(8192, (client, connection) -> {
			OutputStream first = connection.getOutputStream();
			
			assertSame(first, connection.getOutputStream());
		});
	}
	
	@Test
	void receiveAfterStreamReadReturnsBufferedRemainder() throws Exception {
		this.withPair(8192, false, (client, connection) -> {
			client.getOutputStream().write("ABCD".getBytes());
			client.getOutputStream().flush();
			
			assertEquals('A', connection.getInputStream().read());
			
			assertArrayEquals("BCD".getBytes(), readUntil(connection, 3, 8192));
		});
	}
	
	@Test
	void streamReadAfterReceiveContinues() throws Exception {
		this.withPair(8192, false, (client, connection) -> {
			client.getOutputStream().write("AB".getBytes());
			client.getOutputStream().flush();
			assertArrayEquals("AB".getBytes(), readUntil(connection, 2, 8192));
			
			client.getOutputStream().write("CD".getBytes());
			client.getOutputStream().flush();
			
			InputStream in = connection.getInputStream();
			assertEquals('C', in.read());
			assertEquals('D', in.read());
		});
	}
	
	@Test
	void streamReadAfterFramedReceiveStopsAtFrameBoundary() throws Exception {
		this.withPair(8192, true, (client, connection) -> {
			ByteArrayOutputStream queued = new ByteArrayOutputStream();
			NetworkUtils.writeFrame(queued, "Hello".getBytes());
			queued.writeBytes("XYZ".getBytes());
			client.getOutputStream().write(queued.toByteArray());
			client.getOutputStream().flush();
			
			assertArrayEquals("Hello".getBytes(), connection.receive());
			
			assertEquals("XYZ", new String(connection.getInputStream().readNBytes(3), StandardCharsets.US_ASCII));
		});
	}
	
	@Test
	void getInputStreamReadsPeerData() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.getOutputStream().write("Hello".getBytes());
			client.getOutputStream().flush();
			
			assertEquals("Hello", new String(connection.getInputStream().readNBytes(5), StandardCharsets.US_ASCII));
		});
	}
	
	@Test
	void getOutputStreamWritesToPeer() throws Exception {
		this.withPair(8192, (client, connection) -> {
			OutputStream out = connection.getOutputStream();
			out.write("Hello".getBytes());
			out.flush();
			
			assertEquals("Hello", new String(client.getInputStream().readNBytes(5), StandardCharsets.US_ASCII));
		});
	}
	
	@Test
	void userBuiltReaderKeepsBufferedLines() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.getOutputStream().write("line1\r\nline2\r\n".getBytes());
			client.getOutputStream().flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
			
			assertEquals("line1", reader.readLine());
			assertEquals("line2", reader.readLine());
		});
	}
	
	@Test
	void readerFromRefetchedStreamContinues() throws Exception {
		this.withPair(8192, (client, connection) -> {
			client.getOutputStream().write("line1\r\nline2\r\n".getBytes());
			client.getOutputStream().flush();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
			assertEquals("line1", reader.readLine());
			
			assertSame(connection.getInputStream(), connection.getInputStream());
			assertEquals("line2", reader.readLine());
		});
	}
	
	@Test
	void fullDuplexConversation() throws Exception {
		this.withPair(8192, (client, connection) -> {
			InputStream in = connection.getInputStream();
			OutputStream out = connection.getOutputStream();
			
			for (int round = 0; round < 3; round++) {
				client.getOutputStream().write(("ping" + round).getBytes());
				client.getOutputStream().flush();
				assertEquals("ping" + round, new String(in.readNBytes(5), StandardCharsets.US_ASCII));
				
				out.write(("pong" + round).getBytes());
				out.flush();
				assertEquals("pong" + round, new String(client.getInputStream().readNBytes(5), StandardCharsets.US_ASCII));
			}
			
			assertSame(in, connection.getInputStream());
			assertSame(out, connection.getOutputStream());
		});
	}
	
	//region Helper methods
	
	@Test
	void receiveWithoutFramingReturnsEmptyArrayOnPeerClose() throws Exception {
		this.withPair(8192, false, (client, connection) -> {
			client.close();
			
			assertEquals(0, connection.receive().length);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, connection::receive);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
		});
	}
	
	@Test
	void receiveWithoutFramingRespectsMaxBytes() throws Exception {
		this.withPair(8192, false, (client, connection) -> {
			byte[] payload = filled(50, (byte) 0x42);
			client.getOutputStream().write(payload);
			client.getOutputStream().flush();
			
			byte[] first = connection.receive(10);
			assertTrue(first.length > 0);
			assertTrue(first.length <= 10);
			
			byte[] rest = readUntil(connection, 50 - first.length, 8192);
			assertEquals(50, first.length + rest.length);
		});
	}
	
	@Test
	void receiveWithoutFramingReusesScratchBufferAcrossCalls() throws Exception {
		this.withPair(8192, false, (client, connection) -> {
			client.getOutputStream().write("AAAA".getBytes());
			client.getOutputStream().flush();
			assertArrayEquals("AAAA".getBytes(), readUntil(connection, 4, 4096));
			
			client.getOutputStream().write("BB".getBytes());
			client.getOutputStream().flush();
			assertArrayEquals("BB".getBytes(), readUntil(connection, 2, 4096));
			
			client.getOutputStream().write("CCC".getBytes());
			client.getOutputStream().flush();
			assertArrayEquals("CCC".getBytes(), readUntil(connection, 3, 4096));
		});
	}
	
	@Test
	void unframedConnectionSurvivesTlsRecordFragmentation() throws Exception {
		this.withPair(32768, false, (client, connection) -> {
			byte[] payload = filled(20480, (byte) 0x42);
			client.getOutputStream().write(payload);
			client.getOutputStream().flush();
			
			assertArrayEquals(payload, readUntil(connection, payload.length, 32768));
		});
	}
	
	private void withPair(int bufferSize, PairConsumer body) throws Exception {
		this.withPair(bufferSize, true, body);
	}
	
	private void withPair(int bufferSize, boolean framing, PairConsumer body) throws Exception {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		try (SSLServerSocket serverSocket = (SSLServerSocket) serverContext.getServerSocketFactory().createServerSocket()) {
			serverSocket.bind(new InetSocketAddress("127.0.0.1", 0));
			int port = serverSocket.getLocalPort();
			
			Future<SSLSocket> serverFuture = executor.submit(() -> {
				SSLSocket accepted = (SSLSocket) serverSocket.accept();
				accepted.startHandshake();
				return accepted;
			});
			
			try (SSLSocket client = (SSLSocket) clientContext.getSocketFactory().createSocket("127.0.0.1", port)) {
				client.startHandshake();
				SSLSocket serverSide = serverFuture.get(15, TimeUnit.SECONDS);
				SslConnection connection = new SslConnection(serverSide, bufferSize, framing, Duration.ofSeconds(5));
				try {
					body.accept(client, connection);
				} finally {
					connection.close();
				}
			}
		} finally {
			executor.shutdownNow();
		}
	}
	
	@FunctionalInterface
	private interface PairConsumer {
		
		void accept(SSLSocket client, SslConnection connection) throws Exception;
	}
	//endregion
}
