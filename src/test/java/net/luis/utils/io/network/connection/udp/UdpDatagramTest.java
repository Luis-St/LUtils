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

package net.luis.utils.io.network.connection.udp;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UdpDatagram}.<br>
 *
 * @author Luis-St
 */
class UdpDatagramTest {
	
	@Test
	void construct() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		byte[] data = "Hello".getBytes();
		
		UdpDatagram datagram = new UdpDatagram(endpoint, data);
		
		assertEquals(endpoint, datagram.endpoint());
		assertArrayEquals(data, datagram.data());
	}
	
	@Test
	void constructWithNullEndpointThrows() {
		assertThrows(NullPointerException.class, () -> new UdpDatagram(null, "data".getBytes()));
	}
	
	@Test
	void constructWithNullDataThrows() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		assertThrows(NullPointerException.class, () -> new UdpDatagram(endpoint, null));
	}
	
	@Test
	void length() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		UdpDatagram empty = new UdpDatagram(endpoint, ArrayUtils.EMPTY_BYTE_ARRAY);
		assertEquals(0, empty.length());
		
		UdpDatagram small = new UdpDatagram(endpoint, new byte[10]);
		assertEquals(10, small.length());
		
		UdpDatagram larger = new UdpDatagram(endpoint, new byte[1024]);
		assertEquals(1024, larger.length());
	}
	
	@Test
	void dataCopy() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		byte[] original = { 1, 2, 3, 4, 5 };
		
		UdpDatagram datagram = new UdpDatagram(endpoint, original);
		byte[] copy = datagram.dataCopy();
		
		assertArrayEquals(original, copy);
		assertNotSame(original, copy);
		assertNotSame(datagram.data(), copy);
		
		copy[0] = 99;
		assertEquals(1, datagram.data()[0]);
	}
}
