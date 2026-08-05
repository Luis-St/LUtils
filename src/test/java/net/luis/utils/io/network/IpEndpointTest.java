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

package net.luis.utils.io.network;

import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import org.junit.jupiter.api.Test;

import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpEndpoint}.<br>
 *
 * @author Luis-St
 */
class IpEndpointTest {
	
	@Test
	void constructWithValidIpv4() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		assertEquals(Ipv4Address.LOOPBACK, endpoint.address());
		assertEquals(8080, endpoint.port());
	}
	
	@Test
	void constructWithValidIpv6() {
		IpEndpoint endpoint = new IpEndpoint(Ipv6Address.LOOPBACK, 443);
		assertEquals(Ipv6Address.LOOPBACK, endpoint.address());
		assertEquals(443, endpoint.port());
	}
	
	@Test
	void constructWithNullAddress() {
		assertThrows(NullPointerException.class, () -> new IpEndpoint(null, 8080));
	}
	
	@Test
	void constructWithPortBelowMin() {
		assertThrows(IllegalArgumentException.class, () -> new IpEndpoint(Ipv4Address.LOOPBACK, -1));
	}
	
	@Test
	void constructWithPortAboveMax() {
		assertThrows(IllegalArgumentException.class, () -> new IpEndpoint(Ipv4Address.LOOPBACK, 65536));
	}
	
	@Test
	void fromWithNullSocketAddress() {
		assertThrows(NullPointerException.class, () -> IpEndpoint.from(null));
	}
	
	@Test
	void constructWithMinPortBoundary() {
		IpEndpoint endpoint = assertDoesNotThrow(() -> new IpEndpoint(Ipv4Address.LOOPBACK, 0));
		assertEquals(0, endpoint.port());
	}
	
	@Test
	void constructWithMaxPortBoundary() {
		IpEndpoint endpoint = assertDoesNotThrow(() -> new IpEndpoint(Ipv4Address.LOOPBACK, 65535));
		assertEquals(65535, endpoint.port());
	}
	
	@Test
	void toStringWithIpv4Address() {
		assertEquals("127.0.0.1:8080", new IpEndpoint(Ipv4Address.LOOPBACK, 8080).toString());
	}
	
	@Test
	void toStringWithIpv6Address() {
		assertEquals("[0:0:0:0:0:0:0:1]:8080", new IpEndpoint(Ipv6Address.LOOPBACK, 8080).toString());
	}
	
	@Test
	void fromIpv4SocketAddress() {
		IpEndpoint endpoint = IpEndpoint.from(new InetSocketAddress("127.0.0.1", 8080));
		assertEquals(8080, endpoint.port());
		assertEquals(4, endpoint.address().version());
		assertEquals("127.0.0.1:8080", endpoint.toString());
	}
	
	@Test
	void fromIpv6SocketAddress() {
		IpEndpoint endpoint = IpEndpoint.from(new InetSocketAddress("::1", 443));
		assertEquals(443, endpoint.port());
		assertEquals(6, endpoint.address().version());
	}
	
	@Test
	void toInetSocketAddressIpv4() {
		InetSocketAddress socket = new IpEndpoint(Ipv4Address.LOOPBACK, 8080).toInetSocketAddress();
		assertEquals(8080, socket.getPort());
		assertEquals("127.0.0.1", socket.getAddress().getHostAddress());
	}
	
	@Test
	void toInetSocketAddressIpv6() {
		InetSocketAddress socket = new IpEndpoint(Ipv6Address.LOOPBACK, 443).toInetSocketAddress();
		assertEquals(443, socket.getPort());
		assertInstanceOf(Inet6Address.class, socket.getAddress());
		assertTrue(socket.getAddress().isLoopbackAddress());
	}
	
	@Test
	void accessorsReturnComponents() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 1234);
		assertEquals(Ipv4Address.LOOPBACK, endpoint.address());
		assertEquals(1234, endpoint.port());
	}
	
	@Test
	void portConstantsHaveExpectedValues() {
		assertEquals(0, IpEndpoint.MIN_PORT);
		assertEquals(65535, IpEndpoint.MAX_PORT);
	}
	
	@Test
	void fromRoundTripIpv4() {
		InetSocketAddress original = new InetSocketAddress("127.0.0.1", 8080);
		InetSocketAddress roundTrip = IpEndpoint.from(original).toInetSocketAddress();
		assertEquals(original.getAddress(), roundTrip.getAddress());
		assertEquals(original.getPort(), roundTrip.getPort());
	}
	
	@Test
	void toInetSocketAddressToStringConsistency() {
		assertEquals("127.0.0.1:0", new IpEndpoint(Ipv4Address.LOOPBACK, 0).toString());
	}
	
	@Test
	void equalEndpointsAreEqual() {
		IpEndpoint first = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		IpEndpoint second = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void endpointsDifferByPort() {
		IpEndpoint first = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		IpEndpoint second = new IpEndpoint(Ipv4Address.LOOPBACK, 8081);
		assertNotEquals(first, second);
	}
	
	@Test
	void endpointsDifferByAddress() {
		IpEndpoint first = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		IpEndpoint second = new IpEndpoint(Ipv6Address.LOOPBACK, 8080);
		assertNotEquals(first, second);
	}
	
	@Test
	void fromWithUnresolvedSocketAddress() {
		InetSocketAddress unresolved = InetSocketAddress.createUnresolved("example.invalid", 8080);
		assertThrows(NullPointerException.class, () -> IpEndpoint.from(unresolved));
	}
	
	@Test
	void resolveReturnsSameInstance() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		Optional<IpEndpoint> resolved = endpoint.resolve();
		assertTrue(resolved.isPresent());
		assertSame(endpoint, resolved.orElseThrow());
	}
	
	@Test
	void resolveWithIpv6ReturnsSameInstance() {
		IpEndpoint endpoint = new IpEndpoint(Ipv6Address.LOOPBACK, 443);
		
		Optional<IpEndpoint> resolved = endpoint.resolve();
		assertTrue(resolved.isPresent());
		assertSame(endpoint, resolved.orElseThrow());
	}
	
	@Test
	void resolveIsNeverEmpty() {
		assertTrue(new IpEndpoint(Ipv4Address.LOOPBACK, 0).resolve().isPresent());
		assertTrue(new IpEndpoint(Ipv4Address.LOOPBACK, 8080).resolve().isPresent());
		assertTrue(new IpEndpoint(Ipv4Address.LOOPBACK, 65535).resolve().isPresent());
	}
	
	@Test
	void implementsEndpointInterface() {
		assertInstanceOf(Endpoint.class, new IpEndpoint(Ipv4Address.LOOPBACK, 8080));
	}
	
	@Test
	void portConstantsAreInheritedFromEndpoint() {
		assertEquals(Endpoint.MIN_PORT, IpEndpoint.MIN_PORT);
		assertEquals(Endpoint.MAX_PORT, IpEndpoint.MAX_PORT);
	}
	
	@Test
	void endpointInterfaceMethodsWorkThroughSupertype() {
		Endpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		assertEquals(8080, endpoint.port());
		assertEquals(8080, endpoint.toInetSocketAddress().getPort());
		assertTrue(endpoint.resolve().isPresent());
	}
	
	@Test
	void resolveRoundTripPreservesEndpoint() {
		IpEndpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		IpEndpoint twiceResolved = endpoint.resolve().orElseThrow().resolve().orElseThrow();
		assertSame(endpoint, twiceResolved);
	}
	
	@Test
	void ipEndpointDoesNotEqualHostEndpoint() {
		assertNotEquals(new IpEndpoint(Ipv4Address.LOOPBACK, 8080), new HostEndpoint("127.0.0.1", 8080));
	}
}
