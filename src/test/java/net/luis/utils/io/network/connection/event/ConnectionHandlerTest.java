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

package net.luis.utils.io.network.connection.event;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.Connection;
import net.luis.utils.io.network.connection.context.ConnectionContext;
import net.luis.utils.io.network.connection.ssl.*;
import net.luis.utils.io.network.connection.tcp.*;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConnectionHandler}.<br>
 *
 * @author Luis-St
 */
class ConnectionHandlerTest {
	
	private static final IpEndpoint EPHEMERAL = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
	
	@Test
	void handlerCheckedExceptionPropagatesToCaller() {
		ConnectionHandler<TcpServer, Connection> handler = (server, connection) -> {
			throw new IOException("boom");
		};
		Connection connection = new StubConnection();
		
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			IOException exception = assertThrows(IOException.class, () -> handler.handle(server, connection));
			assertEquals("boom", exception.getMessage());
		}
	}
	
	@Test
	void handlerRuntimeExceptionPropagatesToCaller() {
		ConnectionHandler<TcpServer, Connection> handler = (server, connection) -> {
			throw new IllegalStateException("failed");
		};
		Connection connection = new StubConnection();
		
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			IllegalStateException exception = assertThrows(IllegalStateException.class, () -> handler.handle(server, connection));
			assertEquals("failed", exception.getMessage());
		}
	}
	
	@Test
	void handleReceivesServerAndConnection() throws Exception {
		AtomicReference<TcpServer> capturedServer = new AtomicReference<>();
		AtomicReference<Connection> capturedConnection = new AtomicReference<>();
		Connection connection = new StubConnection();
		
		ConnectionHandler<TcpServer, Connection> handler = (server, conn) -> {
			capturedServer.set(server);
			capturedConnection.set(conn);
		};
		
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			handler.handle(server, connection);
			
			assertSame(server, capturedServer.get());
			assertSame(connection, capturedConnection.get());
		}
	}
	
	@Test
	void handleAsLambda() throws Exception {
		AtomicBoolean invoked = new AtomicBoolean(false);
		
		ConnectionHandler<TcpServer, Connection> handler = (server, connection) -> invoked.set(true);
		
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			handler.handle(server, new StubConnection());
			
			assertTrue(invoked.get());
		}
	}
	
	@Test
	void handleAsAnonymousClass() throws Exception {
		AtomicBoolean invoked = new AtomicBoolean(false);
		
		ConnectionHandler<TcpServer, Connection> handler = new ConnectionHandler<>() {
			
			@Override
			public void handle(@NonNull TcpServer server, @NonNull Connection connection) {
				invoked.set(true);
			}
		};
		
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			handler.handle(server, new StubConnection());
			
			assertTrue(invoked.get());
		}
	}
	
	@Test
	void handleWithTcpParameterisationCompiles() {
		ConnectionHandler<TcpServer, TcpConnection> handler = (server, connection) -> {};
		
		TcpServerConfig config = TcpServerConfig.builder().onConnection(handler).build();
		
		assertNotNull(config.onConnection());
		assertSame(handler, config.onConnection());
	}
	
	@Test
	void handleWithSslParameterisationCompiles() throws Exception {
		ConnectionHandler<SslServer, SslConnection> handler = (server, connection) -> {};
		
		SslServerConfig config = SslServerConfig.builder(SSLContext.getDefault()).onConnection(handler).build();
		
		assertNotNull(config.onConnection());
		assertSame(handler, config.onConnection());
	}
	
	@Test
	void handleAsNamedClassSharedAcrossConnections() throws Exception {
		CountingHandler handler = new CountingHandler();
		Connection first = new StubConnection();
		Connection second = new StubConnection();
		
		try (TcpServer server = new TcpServer(EPHEMERAL)) {
			handler.handle(server, first);
			handler.handle(server, second);
			
			assertEquals(2, handler.invocations.get());
			assertSame(first, handler.seen.getFirst());
			assertSame(second, handler.seen.getLast());
		}
	}
	
	@Test
	void handleUsesConnectionStreamsForAConversation() throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(0)) {
			int port = serverSocket.getLocalPort();
			try (
				Socket peer = new Socket("127.0.0.1", port);
				Socket local = serverSocket.accept()
			) {
				peer.getOutputStream().write("PING\r\n".getBytes(StandardCharsets.US_ASCII));
				peer.getOutputStream().flush();
				
				ConnectionHandler<TcpServer, Connection> handler = (server, connection) -> {
					BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.US_ASCII));
					OutputStream writer = connection.getOutputStream();
					writer.write((reader.readLine() + " OK\r\n").getBytes(StandardCharsets.US_ASCII));
					writer.flush();
				};
				
				try (TcpServer server = new TcpServer(EPHEMERAL)) {
					assertDoesNotThrow(() -> handler.handle(server, new StubConnection(local)));
				}
				
				BufferedReader peerReader = new BufferedReader(new InputStreamReader(peer.getInputStream(), StandardCharsets.US_ASCII));
				assertEquals("PING OK", peerReader.readLine());
			}
		}
	}
	
	private static final class CountingHandler implements ConnectionHandler<TcpServer, Connection> {
		
		private final AtomicInteger invocations = new AtomicInteger(0);
		private final List<Connection> seen = new ArrayList<>();
		
		@Override
		public void handle(@NonNull TcpServer server, @NonNull Connection connection) {
			this.invocations.incrementAndGet();
			this.seen.add(connection);
		}
	}
	
	private static final class StubConnection implements Connection {
		
		private final ConnectionContext context = new ConnectionContext();
		private final InputStream input;
		private final OutputStream output;
		
		private StubConnection() {
			this.input = InputStream.nullInputStream();
			this.output = OutputStream.nullOutputStream();
		}
		
		private StubConnection(Socket socket) throws IOException {
			this.input = socket.getInputStream();
			this.output = socket.getOutputStream();
		}
		
		@Override
		public boolean isActive() {
			return true;
		}
		
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
			return this.input;
		}
		
		@Override
		public @NonNull OutputStream getOutputStream() {
			return this.output;
		}
		
		@Override
		public void close() {}
	}
}
