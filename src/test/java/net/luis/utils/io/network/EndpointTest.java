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

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Endpoint}.<br>
 *
 * @author Luis-St
 */
class EndpointTest {
	
	@Test
	void fromWithNullSocketAddress() {
		assertThrows(NullPointerException.class, () -> Endpoint.from(null));
	}
	
	@Test
	void fromUnresolvedSocketAddressCreatesHostEndpoint() {
		Endpoint endpoint = Endpoint.from(InetSocketAddress.createUnresolved("example.invalid", 8080));
		
		HostEndpoint hostEndpoint = assertInstanceOf(HostEndpoint.class, endpoint);
		assertEquals("example.invalid", hostEndpoint.hostname());
		assertEquals(8080, hostEndpoint.port());
	}
	
	@Test
	void fromResolvedSocketAddressCreatesIpEndpoint() {
		Endpoint endpoint = Endpoint.from(new InetSocketAddress("127.0.0.1", 8080));
		
		IpEndpoint ipEndpoint = assertInstanceOf(IpEndpoint.class, endpoint);
		assertEquals(8080, ipEndpoint.port());
		assertEquals(4, ipEndpoint.address().version());
	}
	
	@Test
	void fromResolvedIpv6SocketAddressCreatesIpEndpoint() {
		Endpoint endpoint = Endpoint.from(new InetSocketAddress("::1", 443));
		
		IpEndpoint ipEndpoint = assertInstanceOf(IpEndpoint.class, endpoint);
		assertEquals(443, ipEndpoint.port());
		assertEquals(6, ipEndpoint.address().version());
	}
	
	@Test
	void portConstantsHaveExpectedValues() {
		assertEquals(0, Endpoint.MIN_PORT);
		assertEquals(65535, Endpoint.MAX_PORT);
	}
	
	@Test
	void fromResolvedSocketAddressDiscardsHostname() {
		Endpoint endpoint = Endpoint.from(new InetSocketAddress("localhost", 8080));
		
		assertInstanceOf(IpEndpoint.class, endpoint);
		assertFalse(endpoint instanceof HostEndpoint);
	}
	
	@Test
	void resolveOnIpEndpointReturnsItself() {
		Endpoint endpoint = new IpEndpoint(Ipv4Address.LOOPBACK, 8080);
		
		Optional<IpEndpoint> resolved = endpoint.resolve();
		assertTrue(resolved.isPresent());
		assertSame(endpoint, resolved.orElseThrow());
	}
	
	@Test
	void resolveOnHostEndpointResolvesHostname() {
		Endpoint endpoint = new HostEndpoint("localhost", 8080);
		
		Optional<IpEndpoint> resolved = endpoint.resolve();
		assertTrue(resolved.isPresent());
		assertEquals(8080, resolved.orElseThrow().port());
		assertTrue(resolved.orElseThrow().toInetSocketAddress().getAddress().isLoopbackAddress());
	}
	
	@Test
	void fromWildcardSocketAddress() {
		Endpoint endpoint = Endpoint.from(new InetSocketAddress(8080));
		
		IpEndpoint ipEndpoint = assertInstanceOf(IpEndpoint.class, endpoint);
		assertEquals(8080, ipEndpoint.port());
		assertEquals(Ipv4Address.UNSPECIFIED, ipEndpoint.address());
	}
	
	@Test
	void fromSocketAddressWithBoundaryPorts() {
		Endpoint min = Endpoint.from(new InetSocketAddress("127.0.0.1", Endpoint.MIN_PORT));
		Endpoint max = Endpoint.from(new InetSocketAddress("127.0.0.1", Endpoint.MAX_PORT));
		Endpoint unresolvedMax = Endpoint.from(InetSocketAddress.createUnresolved("example.invalid", Endpoint.MAX_PORT));
		
		assertEquals(0, min.port());
		assertEquals(65535, max.port());
		assertEquals(65535, unresolvedMax.port());
		assertInstanceOf(HostEndpoint.class, unresolvedMax);
	}
	
	@Test
	void patternMatchingCoversSealedHierarchy() {
		List<Endpoint> endpoints = List.of(new HostEndpoint("example.com", 443), new IpEndpoint(Ipv4Address.LOOPBACK, 8080));
		
		List<String> hosts = endpoints.stream().map(endpoint -> switch (endpoint) {
			case HostEndpoint hostEndpoint -> hostEndpoint.hostname();
			case IpEndpoint ipEndpoint -> ipEndpoint.address().toString();
		}).toList();
		
		assertEquals(List.of("example.com", "127.0.0.1"), hosts);
	}
	
	@Test
	void fromRoundTripUnresolvedAddress() {
		InetSocketAddress original = InetSocketAddress.createUnresolved("example.invalid", 9999);
		
		InetSocketAddress roundTrip = Endpoint.from(original).toInetSocketAddress();
		assertEquals("example.invalid", roundTrip.getHostString());
		assertEquals(9999, roundTrip.getPort());
		assertTrue(roundTrip.isUnresolved());
	}
	
	@Test
	void fromRoundTripResolvedAddress() {
		InetSocketAddress original = new InetSocketAddress("127.0.0.1", 8080);
		
		InetSocketAddress roundTrip = Endpoint.from(original).toInetSocketAddress();
		assertEquals(original.getAddress(), roundTrip.getAddress());
		assertEquals(original.getPort(), roundTrip.getPort());
	}
	
	@Test
	void bothImplementationsAreEndpoints() {
		Endpoint host = new HostEndpoint("example.com", 443);
		Endpoint ip = new IpEndpoint(Ipv6Address.LOOPBACK, 8080);
		
		assertInstanceOf(Endpoint.class, host);
		assertInstanceOf(Endpoint.class, ip);
		assertEquals(443, host.port());
		assertEquals(8080, ip.port());
	}
}
