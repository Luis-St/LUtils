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

package net.luis.utils.io.network.address;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpEndpoint}.<br>
 *
 * @author Luis-St
 */
class IpEndpointTest {
	
	@Test
	void constantsAreDefined() {
		assertEquals(0, IpEndpoint.MIN_PORT);
		assertEquals(65535, IpEndpoint.MAX_PORT);
	}
	
	@Test
	void constructWithNullAddress() {
		assertThrows(NullPointerException.class, () -> new IpEndpoint(null, 8080));
	}
	
	@Test
	void constructWithNegativePort() {
		assertThrows(IllegalArgumentException.class, () -> new IpEndpoint(Ipv4Address.LOOPBACK, -1));
	}
	
	@Test
	void constructWithPortAboveMax() {
		assertThrows(IllegalArgumentException.class, () -> new IpEndpoint(Ipv4Address.LOOPBACK, 65536));
	}
	
	@Test
	void constructWithMinPort() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 0);
		assertEquals(0, endpoint.port());
	}
	
	@Test
	void constructWithMaxPort() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 65535);
		assertEquals(65535, endpoint.port());
	}
	
	@Test
	void constructWithIpv4Address() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		IpEndpoint endpoint = new IpEndpoint(address, 8080);
		assertEquals(address, endpoint.address());
		assertEquals(8080, endpoint.port());
	}
	
	@Test
	void constructWithIpv6Address() {
		Ipv6Address address = Ipv6Address.LOOPBACK;
		IpEndpoint endpoint = new IpEndpoint(address, 443);
		assertEquals(address, endpoint.address());
		assertEquals(443, endpoint.port());
	}
	
	@Test
	void toInetSocketAddressWithIpv4() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		IpEndpoint endpoint = new IpEndpoint(address, 8080);
		InetSocketAddress socketAddress = endpoint.toInetSocketAddress();
		assertEquals(8080, socketAddress.getPort());
		assertEquals("/192.168.1.1", socketAddress.getAddress().toString());
	}
	
	@Test
	void toInetSocketAddressWithIpv6() {
		Ipv6Address address = Ipv6Address.LOOPBACK;
		IpEndpoint endpoint = new IpEndpoint(address, 443);
		InetSocketAddress socketAddress = endpoint.toInetSocketAddress();
		assertEquals(443, socketAddress.getPort());
		assertNotNull(socketAddress.getAddress());
	}
	
	@Test
	void recordEquals() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		IpEndpoint endpoint1 = new IpEndpoint(address, 8080);
		IpEndpoint endpoint2 = new IpEndpoint(address, 8080);
		IpEndpoint endpoint3 = new IpEndpoint(address, 9090);
		IpEndpoint endpoint4 = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		assertEquals(endpoint1, endpoint2);
		assertNotEquals(endpoint1, endpoint3);
		assertNotEquals(endpoint1, endpoint4);
	}
	
	@Test
	void recordHashCode() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		IpEndpoint endpoint1 = new IpEndpoint(address, 8080);
		IpEndpoint endpoint2 = new IpEndpoint(address, 8080);
		assertEquals(endpoint1.hashCode(), endpoint2.hashCode());
	}
	
	@Test
	void toStringWithIpv4Address() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		IpEndpoint endpoint = new IpEndpoint(address, 8080);
		assertEquals("192.168.1.1:8080", endpoint.toString());
	}
	
	@Test
	void toStringWithIpv6Address() {
		Ipv6Address address = Ipv6Address.LOOPBACK;
		IpEndpoint endpoint = new IpEndpoint(address, 443);
		assertTrue(endpoint.toString().startsWith("["));
		assertTrue(endpoint.toString().endsWith("]:443"));
	}

	@Test
	void fromWithNullAddress() {
		assertThrows(NullPointerException.class, () -> IpEndpoint.from(null));
	}

	@Test
	void fromWithIpv4Address() {
		InetSocketAddress socketAddress = new InetSocketAddress("192.168.1.1", 8080);
		IpEndpoint endpoint = IpEndpoint.from(socketAddress);
		assertEquals(8080, endpoint.port());
		assertInstanceOf(Ipv4Address.class, endpoint.address());
		assertEquals("192.168.1.1:8080", endpoint.toString());
	}

	@Test
	void fromWithIpv6Address() {
		InetSocketAddress socketAddress = new InetSocketAddress("::1", 443);
		IpEndpoint endpoint = IpEndpoint.from(socketAddress);
		assertEquals(443, endpoint.port());
		assertInstanceOf(Ipv6Address.class, endpoint.address());
	}

	@Test
	void fromRoundTrip() {
		Ipv4Address address = Ipv4Address.fromOctets(10, 0, 0, 1);
		IpEndpoint original = new IpEndpoint(address, 9999);
		InetSocketAddress socketAddress = original.toInetSocketAddress();
		IpEndpoint roundTripped = IpEndpoint.from(socketAddress);
		assertEquals(original, roundTripped);
	}
}
