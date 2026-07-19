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
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DisconnectEvent}.<br>
 *
 * @author Luis-St
 */
class DisconnectEventTest {
	
	@Test
	void constructWithConnection() {
		Connection connection = new StubConnection();
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		Instant timestamp = Instant.now();
		
		DisconnectEvent event = new DisconnectEvent(connection, local, remote, timestamp);
		
		assertSame(connection, event.connection());
		assertEquals(local, event.localEndpoint());
		assertEquals(remote, event.remoteEndpoint());
		assertEquals(timestamp, event.timestamp());
	}
	
	@Test
	void constructWithNullConnection() {
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		Instant timestamp = Instant.now();
		
		DisconnectEvent event = assertDoesNotThrow(() -> new DisconnectEvent(null, local, remote, timestamp));
		
		assertNull(event.connection());
	}
	
	@Test
	void constructWithNullLocalEndpointThrows() {
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		Instant timestamp = Instant.now();
		
		assertThrows(NullPointerException.class, () -> new DisconnectEvent(null, null, remote, timestamp));
	}
	
	@Test
	void constructWithNullRemoteEndpointThrows() {
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		Instant timestamp = Instant.now();
		
		assertThrows(NullPointerException.class, () -> new DisconnectEvent(null, local, null, timestamp));
	}
	
	@Test
	void constructWithNullTimestampThrows() {
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		assertThrows(NullPointerException.class, () -> new DisconnectEvent(null, local, remote, null));
	}
	
	@Test
	void nowWithNullLocalEndpointThrows() {
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		assertThrows(NullPointerException.class, () -> DisconnectEvent.now(null, null, remote));
	}
	
	@Test
	void nowWithNullRemoteEndpointThrows() {
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		
		assertThrows(NullPointerException.class, () -> DisconnectEvent.now(null, local, null));
	}
	
	@Test
	void now() {
		Connection connection = new StubConnection();
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		Instant before = Instant.now();
		DisconnectEvent event = DisconnectEvent.now(connection, local, remote);
		Instant after = Instant.now();
		
		assertSame(connection, event.connection());
		assertEquals(local, event.localEndpoint());
		assertEquals(remote, event.remoteEndpoint());
		assertFalse(event.timestamp().isBefore(before));
		assertFalse(event.timestamp().isAfter(after));
	}
	
	@Test
	void nowWithNullConnection() {
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		DisconnectEvent event = assertDoesNotThrow(() -> DisconnectEvent.now(null, local, remote));
		
		assertNull(event.connection());
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
