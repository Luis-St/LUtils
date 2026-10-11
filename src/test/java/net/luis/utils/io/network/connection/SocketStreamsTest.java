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

import net.luis.utils.io.network.connection.exception.NetworkConnectionException;
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SocketStreams}.<br>
 *
 * @author Luis-St
 */
@Timeout(value = 30, unit = TimeUnit.SECONDS)
class SocketStreamsTest {
	
	private static void withPair(PairConsumer body) throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (
				Socket peer = new Socket("127.0.0.1", port);
				Socket local = serverSocket.accept()
			) {
				body.accept(peer, local);
			}
		}
	}
	
	private static void write(Socket socket, String data) throws Exception {
		socket.getOutputStream().write(data.getBytes(StandardCharsets.US_ASCII));
		socket.getOutputStream().flush();
	}
	
	private static String read(InputStream stream, int length) throws Exception {
		return new String(stream.readNBytes(length), StandardCharsets.US_ASCII);
	}
	
	private static boolean awaitPendingInput(SocketStreams streams) throws InterruptedException {
		for (int attempt = 0; attempt < 200; attempt++) {
			if (streams.hasPendingInput()) {
				return true;
			}
			Thread.sleep(10);
		}
		return false;
	}
	
	@Test
	void constructWithSocket() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = assertDoesNotThrow(() -> new SocketStreams(local));
			
			assertFalse(streams.hasPendingInput());
		});
	}
	
	@Test
	void constructWithNullSocket() {
		assertThrows(NullPointerException.class, () -> new SocketStreams(null));
	}
	
	@Test
	void constructWithUnconnectedSocket() throws Exception {
		try (Socket socket = new Socket()) {
			SocketStreams streams = assertDoesNotThrow(() -> new SocketStreams(socket));
			
			assertFalse(streams.hasPendingInput());
		}
	}
	
	@Test
	void inputOnClosedSocketThrowsConnectionReset() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = new SocketStreams(local);
			local.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, streams::input);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
			assertEquals("Connection reset", exception.getMessage());
		});
	}
	
	@Test
	void inputAfterFirstCallStillReportsClosedSocket() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = new SocketStreams(local);
			assertNotNull(streams.input());
			
			local.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, streams::input);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
		});
	}
	
	@Test
	void inputOnUnconnectedSocketThrowsConnectionReset() throws Exception {
		try (Socket socket = new Socket()) {
			SocketStreams streams = new SocketStreams(socket);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, streams::input);
			assertEquals(NetworkErrorType.CONNECTION_RESET, exception.errorType());
			assertEquals("Connection reset", exception.getMessage());
		}
	}
	
	@Test
	void inputWithFailingSocketThrowsIoError() throws Exception {
		try (Socket socket = new FailingInputSocket()) {
			SocketStreams streams = new SocketStreams(socket);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, streams::input);
			assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
			assertEquals("Failed to get input stream", exception.getMessage());
			assertEquals("stream unavailable", exception.getCause().getMessage());
		}
	}
	
	@Test
	void outputOnClosedSocketThrowsIoError() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = new SocketStreams(local);
			local.close();
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, streams::output);
			assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
			assertEquals("Failed to get output stream", exception.getMessage());
		});
	}
	
	@Test
	void outputOnUnconnectedSocketThrowsIoError() throws Exception {
		try (Socket socket = new Socket()) {
			SocketStreams streams = new SocketStreams(socket);
			
			NetworkConnectionException exception = assertThrows(NetworkConnectionException.class, streams::output);
			assertEquals(NetworkErrorType.IO_ERROR, exception.errorType());
			assertEquals("Failed to get output stream", exception.getMessage());
		}
	}
	
	@Test
	void inputReturnsSameInstanceOnEveryCall() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = new SocketStreams(local);
			
			assertSame(streams.input(), streams.input());
			assertInstanceOf(BufferedInputStream.class, streams.input());
		});
	}
	
	@Test
	void outputReturnsSameInstanceOnEveryCall() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = new SocketStreams(local);
			
			assertSame(streams.output(), streams.output());
		});
	}
	
	@Test
	void outputAfterFirstCallStillReturnsCachedStream() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = new SocketStreams(local);
			OutputStream first = streams.output();
			
			local.close();
			
			assertSame(first, assertDoesNotThrow(streams::output));
		});
	}
	
	@Test
	void inputBuffersAheadSoBytesSurviveASecondLookup() throws Exception {
		withPair((peer, local) -> {
			write(peer, "AB");
			
			SocketStreams streams = new SocketStreams(local);
			assertEquals('A', streams.input().read());
			assertTrue(streams.hasPendingInput());
			
			assertEquals('B', streams.input().read());
		});
	}
	
	@Test
	void hasPendingInputBeforeAnyRead() throws Exception {
		withPair((peer, local) -> {
			write(peer, "AB");
			
			SocketStreams streams = new SocketStreams(local);
			
			assertFalse(streams.hasPendingInput());
		});
	}
	
	@Test
	void hasPendingInputWithBufferedBytes() throws Exception {
		withPair((peer, local) -> {
			write(peer, "AB");
			
			SocketStreams streams = new SocketStreams(local);
			assertEquals('A', streams.input().read());
			
			assertTrue(streams.hasPendingInput());
		});
	}
	
	@Test
	void hasPendingInputWithUnreadSocketBytes() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = new SocketStreams(local);
			assertNotNull(streams.input());
			
			write(peer, "AB");
			
			assertTrue(awaitPendingInput(streams));
		});
	}
	
	@Test
	void hasPendingInputWithDrainedStream() throws Exception {
		withPair((peer, local) -> {
			write(peer, "A");
			
			SocketStreams streams = new SocketStreams(local);
			assertEquals('A', streams.input().read());
			
			assertFalse(streams.hasPendingInput());
		});
	}
	
	@Test
	void hasPendingInputOnClosedSocket() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = new SocketStreams(local);
			assertNotNull(streams.input());
			
			local.close();
			
			assertFalse(streams.hasPendingInput());
		});
	}
	
	@Test
	void inputReadsWhatThePeerSent() throws Exception {
		withPair((peer, local) -> {
			write(peer, "Hello");
			
			SocketStreams streams = new SocketStreams(local);
			
			assertEquals("Hello", read(streams.input(), 5));
		});
	}
	
	@Test
	void outputWritesUnbuffered() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = new SocketStreams(local);
			OutputStream out = streams.output();
			assertFalse(out instanceof BufferedOutputStream);
			
			out.write("Hello".getBytes(StandardCharsets.US_ASCII));
			out.flush();
			
			assertEquals("Hello", read(peer.getInputStream(), 5));
		});
	}
	
	@Test
	void inputAndOutputAreIndependent() throws Exception {
		withPair((peer, local) -> {
			SocketStreams streams = new SocketStreams(local);
			InputStream in = streams.input();
			OutputStream out = streams.output();
			
			out.write("ping".getBytes(StandardCharsets.US_ASCII));
			out.flush();
			assertEquals("ping", read(peer.getInputStream(), 4));
			
			write(peer, "pong");
			assertEquals("pong", read(in, 4));
			
			assertSame(in, streams.input());
			assertSame(out, streams.output());
		});
	}
	
	@Test
	void userBuiltReaderOverInputKeepsBufferedLines() throws Exception {
		withPair((peer, local) -> {
			write(peer, "line1\r\nline2\r\n");
			
			SocketStreams streams = new SocketStreams(local);
			BufferedReader reader = new BufferedReader(new InputStreamReader(streams.input(), StandardCharsets.US_ASCII));
			
			assertEquals("line1", reader.readLine());
			assertEquals("line2", reader.readLine());
		});
	}
	
	@FunctionalInterface
	private interface PairConsumer {
		
		void accept(Socket peer, Socket local) throws Exception;
	}
	
	private static final class FailingInputSocket extends Socket {
		
		@Override
		public InputStream getInputStream() throws IOException {
			throw new IOException("stream unavailable");
		}
	}
}
