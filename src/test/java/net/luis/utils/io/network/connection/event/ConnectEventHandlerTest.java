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

import net.luis.utils.io.network.*;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.connection.Connection;
import net.luis.utils.io.network.connection.context.ConnectionContext;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConnectEventHandler}.<br>
 *
 * @author Luis-St
 */
class ConnectEventHandlerTest {
	
	private static final IpEndpoint LOCAL = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
	private static final IpEndpoint REMOTE = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
	
	private static void ignoreEvent(@Nullable Connection connection, @NonNull Endpoint localEndpoint, @NonNull Endpoint remoteEndpoint, @NonNull Instant timestamp) {}
	
	@Test
	void handleReceivesConnectionEndpointsAndTimestamp() {
		AtomicReference<Connection> capturedConnection = new AtomicReference<>();
		AtomicReference<Endpoint> capturedLocal = new AtomicReference<>();
		AtomicReference<Endpoint> capturedRemote = new AtomicReference<>();
		AtomicReference<Instant> capturedTimestamp = new AtomicReference<>();
		Connection connection = new StubConnection();
		Instant timestamp = Instant.now();
		
		ConnectEventHandler handler = (conn, local, remote, time) -> {
			capturedConnection.set(conn);
			capturedLocal.set(local);
			capturedRemote.set(remote);
			capturedTimestamp.set(time);
		};
		
		handler.handle(connection, LOCAL, REMOTE, timestamp);
		
		assertSame(connection, capturedConnection.get());
		assertEquals(LOCAL, capturedLocal.get());
		assertEquals(REMOTE, capturedRemote.get());
		assertSame(timestamp, capturedTimestamp.get());
	}
	
	@Test
	void handleAcceptsNullConnectionWhenNotAvailable() {
		AtomicReference<Connection> capturedConnection = new AtomicReference<>();
		
		ConnectEventHandler handler = (conn, local, remote, time) -> capturedConnection.set(conn);
		
		assertDoesNotThrow(() -> handler.handle(null, LOCAL, REMOTE, Instant.now()));
		
		assertNull(capturedConnection.get());
	}
	
	@Test
	void handleAcceptsHostEndpoints() {
		AtomicReference<Endpoint> capturedLocal = new AtomicReference<>();
		AtomicReference<Endpoint> capturedRemote = new AtomicReference<>();
		HostEndpoint local = new HostEndpoint("localhost", 12345);
		HostEndpoint remote = new HostEndpoint("localhost", 8080);
		
		ConnectEventHandler handler = (conn, first, second, time) -> {
			capturedLocal.set(first);
			capturedRemote.set(second);
		};
		
		handler.handle(null, local, remote, Instant.now());
		
		assertInstanceOf(HostEndpoint.class, capturedLocal.get());
		assertSame(local, capturedLocal.get());
		assertSame(remote, capturedRemote.get());
	}
	
	@Test
	void handleInvokedMultipleTimesAccumulates() {
		AtomicInteger callCount = new AtomicInteger(0);
		AtomicReference<Endpoint> capturedRemote = new AtomicReference<>();
		IpEndpoint third = new IpEndpoint(Ipv4Address.LOOPBACK, 9090);
		
		ConnectEventHandler handler = (conn, local, remote, time) -> {
			callCount.incrementAndGet();
			capturedRemote.set(remote);
		};
		
		handler.handle(null, LOCAL, REMOTE, Instant.now());
		handler.handle(null, LOCAL, LOCAL, Instant.now());
		handler.handle(null, LOCAL, third, Instant.now());
		
		assertEquals(3, callCount.get());
		assertEquals(third, capturedRemote.get());
	}
	
	@Test
	void handlerUsableAsLambdaAndMethodReference() {
		ConnectEventHandler lambda = (conn, local, remote, time) -> {};
		ConnectEventHandler methodReference = ConnectEventHandlerTest::ignoreEvent;
		
		assertDoesNotThrow(() -> lambda.handle(null, LOCAL, REMOTE, Instant.now()));
		assertDoesNotThrow(() -> methodReference.handle(null, LOCAL, REMOTE, Instant.now()));
	}
	
	private static final class StubConnection implements Connection {
		
		private final ConnectionContext context = new ConnectionContext();
		
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
			return REMOTE;
		}
		
		@Override
		public @NonNull IpEndpoint localEndpoint() {
			return LOCAL;
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
		public void close() {}
	}
}
