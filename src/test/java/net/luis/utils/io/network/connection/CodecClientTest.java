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

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.Codecs;
import net.luis.utils.io.codec.decoder.Decoder;
import net.luis.utils.io.codec.decoder.DecoderException;
import net.luis.utils.io.codec.encoder.Encoder;
import net.luis.utils.io.codec.encoder.EncoderException;
import net.luis.utils.io.codec.provider.TypeProvider;
import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import net.luis.utils.io.network.connection.tcp.TcpConnection;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CodecClient}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class CodecClientTest {
	
	private static final Encoder<String> FAILING_ENCODER = new Encoder<>() {
		@Override
		public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable String value) throws EncoderException {
			throw new EncoderException("encoding failed");
		}
	};
	private static final Encoder<String> NULL_ENCODER = new Encoder<>() {
		@Override
		@SuppressWarnings("DataFlowIssue")
		public <R> @NonNull R encode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable String value) {
			return null;
		}
	};
	private static final Decoder<String> NULL_DECODER = new Decoder<>() {
		@Override
		public <R> @Nullable String decode(@NonNull TypeProvider<R> provider, @NonNull R current, @Nullable R value) throws DecoderException {
			return null;
		}
	};
	
	private static ConnectionPair pair(int maxMessageSize) throws IOException {
		try (ServerSocket serverSocket = new ServerSocket(0, 50, InetAddress.getLoopbackAddress())) {
			Socket localSocket = new Socket("127.0.0.1", serverSocket.getLocalPort());
			Socket peerSocket = serverSocket.accept();
			return new ConnectionPair(
				new TcpConnection(localSocket, Framing.lengthPrefixed(), maxMessageSize, null),
				new TcpConnection(peerSocket, Framing.lengthPrefixed(), maxMessageSize, null)
			);
		}
	}
	
	private static ConnectionPair pair() throws IOException {
		return pair(8192);
	}
	
	@Test
	void constructThroughMap() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codecs.STRING);
			
			assertSame(pair.local(), client.connection());
			assertSame(Codecs.STRING, client.codec());
		}
	}
	
	@Test
	void constructWithNullConnection() {
		assertThrows(NullPointerException.class, () -> new CodecClient<>(null, Codecs.STRING));
	}
	
	@Test
	void constructWithNullCodec() throws Exception {
		try (ConnectionPair pair = pair()) {
			assertThrows(NullPointerException.class, () -> new CodecClient<String>(pair.local(), null));
		}
	}
	
	@Test
	void sendWithNullValue() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codecs.STRING);
			
			assertThrows(NullPointerException.class, () -> client.send(null));
		}
	}
	
	@Test
	void sendEncoderFails() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codec.of(String.class, FAILING_ENCODER, Codecs.STRING, "failing"));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send("value"));
			assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
			assertInstanceOf(EncoderException.class, exception.getCause());
			assertEquals(pair.local().remoteEndpoint().orElseThrow(), exception.endpoint());
			assertEquals(0, pair.peer().getInputStream().available());
		}
	}
	
	@Test
	void sendEncoderReturnsNull() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codec.of(String.class, NULL_ENCODER, Codecs.STRING, "null"));
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send("value"));
			assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
		}
	}
	
	@Test
	void sendOnClosedConnection() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codecs.STRING);
			pair.local().close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send("value"));
			assertEquals(NetworkErrorType.SOCKET_CLOSED, exception.errorType());
		}
	}
	
	@Test
	void sendValueExceedingMaxMessageSize() throws Exception {
		try (ConnectionPair pair = pair(8)) {
			CodecClient<String> client = pair.local().map(Codecs.STRING);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, () -> client.send("x".repeat(100)));
			assertEquals(NetworkErrorType.MESSAGE_TOO_LARGE, exception.errorType());
		}
	}
	
	@Test
	void receiveInvalidBinary() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codecs.STRING);
			pair.peer().send(new byte[] { (byte) 0xFF });
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
		}
	}
	
	@Test
	void receiveWrongType() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<Integer> client = pair.local().map(Codecs.INTEGER);
			pair.peer().map(Codecs.STRING).send("not a number");
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
			assertInstanceOf(DecoderException.class, exception.getCause());
		}
	}
	
	@Test
	void receiveDecoderReturnsNull() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codec.of(String.class, Codecs.STRING, NULL_DECODER, "null"));
			pair.peer().map(Codecs.STRING).send("value");
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, client::receive);
			assertEquals(NetworkErrorType.PROTOCOL_ERROR, exception.errorType());
		}
	}
	
	@Test
	void sendAndReceiveString() throws Exception {
		try (ConnectionPair pair = pair()) {
			pair.local().map(Codecs.STRING).send("hello");
			
			assertEquals("hello", pair.peer().map(Codecs.STRING).receive().orElseThrow());
		}
	}
	
	@Test
	void receiveAfterPeerClosed() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codecs.STRING);
			pair.peer().close();
			
			assertTrue(client.receive().isEmpty());
		}
	}
	
	@Test
	void isActiveDelegates() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codecs.STRING);
			assertTrue(client.isActive());
			
			client.connection().close();
			
			assertFalse(client.isActive());
		}
	}
	
	@Test
	void endpointsDelegate() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codecs.STRING);
			
			assertEquals(pair.local().localEndpoint(), client.localEndpoint());
			assertEquals(pair.local().remoteEndpoint(), client.remoteEndpoint());
		}
	}
	
	@Test
	void closeClosesConnection() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> client = pair.local().map(Codecs.STRING);
			
			client.close();
			
			assertFalse(client.connection().isActive());
		}
	}
	
	@Test
	void sendAndReceiveInteger() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<Integer> sender = pair.local().map(Codecs.INTEGER);
			CodecClient<Integer> receiver = pair.peer().map(Codecs.INTEGER);
			
			for (int value : new int[] { 0, -1, Integer.MAX_VALUE }) {
				sender.send(value);
				assertEquals(value, receiver.receive().orElseThrow());
			}
		}
	}
	
	@Test
	void sendAndReceiveEmptyString() throws Exception {
		try (ConnectionPair pair = pair()) {
			pair.local().map(Codecs.STRING).send("");
			
			assertEquals("", pair.peer().map(Codecs.STRING).receive().orElseThrow());
		}
	}
	
	@Test
	void sendAndReceiveUnicodeString() throws Exception {
		String value = "Grüße ÄÖÜ 😀";
		try (ConnectionPair pair = pair()) {
			pair.local().map(Codecs.STRING).send(value);
			
			assertEquals(value, pair.peer().map(Codecs.STRING).receive().orElseThrow());
		}
	}
	
	@Test
	void multipleValuesInSequence() throws Exception {
		List<String> values = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			values.add("value-" + i);
		}
		try (ConnectionPair pair = pair()) {
			CodecClient<String> sender = pair.local().map(Codecs.STRING);
			CodecClient<String> receiver = pair.peer().map(Codecs.STRING);
			for (String value : values) {
				sender.send(value);
			}
			
			List<String> received = new ArrayList<>();
			for (int i = 0; i < values.size(); i++) {
				received.add(receiver.receive().orElseThrow());
			}
			assertEquals(values, received);
		}
	}
	
	@Test
	void bothDirectionsOnOneConnection() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> left = pair.local().map(Codecs.STRING);
			CodecClient<String> right = pair.peer().map(Codecs.STRING);
			
			left.send("ping");
			assertEquals("ping", right.receive().orElseThrow());
			right.send("pong");
			assertEquals("pong", left.receive().orElseThrow());
			left.send("done");
			assertEquals("done", right.receive().orElseThrow());
		}
	}
	
	@Test
	void mapTwiceSharesConnection() throws Exception {
		try (ConnectionPair pair = pair()) {
			CodecClient<String> strings = pair.local().map(Codecs.STRING);
			CodecClient<Integer> integers = pair.local().map(Codecs.INTEGER);
			
			strings.send("first");
			integers.send(2);
			
			assertSame(strings.connection(), integers.connection());
			assertEquals("first", pair.peer().map(Codecs.STRING).receive().orElseThrow());
			assertEquals(2, pair.peer().map(Codecs.INTEGER).receive().orElseThrow());
		}
	}
	
	private record ConnectionPair(TcpConnection local, TcpConnection peer) implements AutoCloseable {
		
		@Override
		public void close() {
			this.local.close();
			this.peer.close();
		}
	}
}
