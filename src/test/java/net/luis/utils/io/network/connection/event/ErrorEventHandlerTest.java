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
import net.luis.utils.io.network.connection.exception.NetworkErrorType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ErrorEventHandler}.<br>
 *
 * @author Luis-St
 */
class ErrorEventHandlerTest {
	
	@Test
	void defaultHandleDelegatesToThreeArgHandle() {
		AtomicReference<NetworkErrorType> capturedType = new AtomicReference<>();
		AtomicReference<String> capturedMessage = new AtomicReference<>();
		AtomicReference<Throwable> capturedCause = new AtomicReference<>();
		RuntimeException cause = new RuntimeException("test");
		
		ErrorEventHandler handler = (errorType, message, c) -> {
			capturedType.set(errorType);
			capturedMessage.set(message);
			capturedCause.set(c);
		};
		
		handler.handle(new StubConnection(), NetworkErrorType.IO_ERROR, "msg", cause);
		
		assertEquals(NetworkErrorType.IO_ERROR, capturedType.get());
		assertEquals("msg", capturedMessage.get());
		assertSame(cause, capturedCause.get());
	}
	
	@Test
	void defaultHandleWithNullConnectionDelegates() {
		AtomicReference<NetworkErrorType> capturedType = new AtomicReference<>();
		AtomicReference<String> capturedMessage = new AtomicReference<>();
		AtomicReference<Throwable> capturedCause = new AtomicReference<>();
		RuntimeException cause = new RuntimeException("test");
		
		ErrorEventHandler handler = (errorType, message, c) -> {
			capturedType.set(errorType);
			capturedMessage.set(message);
			capturedCause.set(c);
		};
		
		assertDoesNotThrow(() -> handler.handle(null, NetworkErrorType.IO_ERROR, "msg", cause));
		
		assertEquals(NetworkErrorType.IO_ERROR, capturedType.get());
		assertEquals("msg", capturedMessage.get());
		assertSame(cause, capturedCause.get());
	}
	
	@Test
	void overriddenHandleReceivesConnection() {
		AtomicReference<Connection> capturedConnection = new AtomicReference<>();
		AtomicBoolean threeArgCalled = new AtomicBoolean(false);
		Connection connection = new StubConnection();
		
		ErrorEventHandler handler = new ErrorEventHandler() {
			@Override
			public void handle(NetworkErrorType errorType, String message, Throwable cause) {
				threeArgCalled.set(true);
			}
			
			@Override
			public void handle(Connection conn, NetworkErrorType errorType, String message, Throwable cause) {
				capturedConnection.set(conn);
			}
		};
		
		handler.handle(connection, NetworkErrorType.IO_ERROR, "msg", new RuntimeException("test"));
		
		assertSame(connection, capturedConnection.get());
		assertFalse(threeArgCalled.get());
	}
	
	@Test
	void overriddenHandleReceivesNullConnectionWhenNotAvailable() {
		AtomicReference<Connection> capturedConnection = new AtomicReference<>();
		AtomicBoolean threeArgCalled = new AtomicBoolean(false);
		
		ErrorEventHandler handler = new ErrorEventHandler() {
			@Override
			public void handle(NetworkErrorType errorType, String message, Throwable cause) {
				threeArgCalled.set(true);
			}
			
			@Override
			public void handle(Connection conn, NetworkErrorType errorType, String message, Throwable cause) {
				capturedConnection.set(conn);
			}
		};
		
		handler.handle(null, NetworkErrorType.IO_ERROR, "msg", new RuntimeException("test"));
		
		assertNull(capturedConnection.get());
		assertFalse(threeArgCalled.get());
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
}
