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
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConnectionEvent}.<br>
 *
 * @author Luis-St
 */
class ConnectionEventTest {
	
	@Test
	void construct() {
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		Instant timestamp = Instant.now();
		
		ConnectionEvent event = new ConnectionEvent(local, remote, timestamp);
		
		assertEquals(local, event.localEndpoint());
		assertEquals(remote, event.remoteEndpoint());
		assertEquals(timestamp, event.timestamp());
	}
	
	@Test
	void constructWithNullLocalEndpointThrows() {
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		Instant timestamp = Instant.now();
		
		assertThrows(NullPointerException.class, () -> new ConnectionEvent(null, remote, timestamp));
	}
	
	@Test
	void constructWithNullRemoteEndpointThrows() {
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		Instant timestamp = Instant.now();
		
		assertThrows(NullPointerException.class, () -> new ConnectionEvent(local, null, timestamp));
	}
	
	@Test
	void constructWithNullTimestampThrows() {
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		assertThrows(NullPointerException.class, () -> new ConnectionEvent(local, remote, null));
	}
	
	@Test
	void now() {
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		Instant before = Instant.now();
		ConnectionEvent event = ConnectionEvent.now(local, remote);
		Instant after = Instant.now();
		
		assertEquals(local, event.localEndpoint());
		assertEquals(remote, event.remoteEndpoint());
		assertFalse(event.timestamp().isBefore(before));
		assertFalse(event.timestamp().isAfter(after));
	}
	
	@Test
	void nowWithNullLocalEndpointThrows() {
		IpEndpoint remote = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		assertThrows(NullPointerException.class, () -> ConnectionEvent.now(null, remote));
	}
	
	@Test
	void nowWithNullRemoteEndpointThrows() {
		IpEndpoint local = new IpEndpoint(Ipv4Address.LOOPBACK, 12345);
		
		assertThrows(NullPointerException.class, () -> ConnectionEvent.now(local, null));
	}
}
