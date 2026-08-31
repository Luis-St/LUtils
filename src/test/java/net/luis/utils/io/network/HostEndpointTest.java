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
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HostEndpoint}.<br>
 *
 * @author Luis-St
 */
class HostEndpointTest {
	
	private static final String UNRESOLVABLE_HOSTNAME = "this-host-does-not-exist.invalid";
	
	@Test
	void constructWithValidHostname() {
		HostEndpoint endpoint = new HostEndpoint("example.com", 443);
		assertEquals("example.com", endpoint.hostname());
		assertEquals(443, endpoint.port());
	}
	
	@Test
	void constructWithNullHostname() {
		assertThrows(NullPointerException.class, () -> new HostEndpoint(null, 443));
	}
	
	@Test
	void constructWithEmptyHostname() {
		assertThrows(IllegalArgumentException.class, () -> new HostEndpoint("", 443));
	}
	
	@Test
	void constructWithWhitespaceOnlyHostname() {
		assertThrows(IllegalArgumentException.class, () -> new HostEndpoint("   ", 443));
	}
	
	@Test
	void constructWithHostnameExceedingMaxLength() {
		String hostname = "a".repeat(HostEndpoint.MAX_HOSTNAME_LENGTH + 1);
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new HostEndpoint(hostname, 443));
		assertTrue(exception.getMessage().contains("254"));
	}
	
	@Test
	void constructWithHostnameContainingSpace() {
		assertThrows(IllegalArgumentException.class, () -> new HostEndpoint("exa mple.com", 443));
	}
	
	@Test
	void constructWithHostnameContainingTab() {
		assertThrows(IllegalArgumentException.class, () -> new HostEndpoint("example\t.com", 443));
	}
	
	@Test
	void constructWithHostnameStartingWithWhitespace() {
		assertThrows(IllegalArgumentException.class, () -> new HostEndpoint(" example.com", 443));
	}
	
	@Test
	void constructWithHostnameEndingWithWhitespace() {
		assertThrows(IllegalArgumentException.class, () -> new HostEndpoint("example.com ", 443));
	}
	
	@Test
	void constructWithPortBelowMin() {
		assertThrows(IllegalArgumentException.class, () -> new HostEndpoint("example.com", -1));
	}
	
	@Test
	void constructWithPortAboveMax() {
		assertThrows(IllegalArgumentException.class, () -> new HostEndpoint("example.com", 65536));
	}
	
	@Test
	void constructWithBlankHostnameExceedingMaxLength() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new HostEndpoint(" ".repeat(300), 443));
		assertEquals("Hostname must not be blank", exception.getMessage());
	}
	
	@Test
	void constructWithLongHostnameContainingWhitespace() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new HostEndpoint("a".repeat(300) + " ", 443));
		assertTrue(exception.getMessage().contains("301"));
		assertFalse(exception.getMessage().contains("whitespace"));
	}
	
	@Test
	void constructWithInvalidHostnameAndInvalidPort() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new HostEndpoint("exa mple.com", 70000));
		assertTrue(exception.getMessage().contains("whitespace"));
	}
	
	@Test
	void constructWithHostnameAtMaxLength() {
		String hostname = "a".repeat(HostEndpoint.MAX_HOSTNAME_LENGTH);
		
		HostEndpoint endpoint = assertDoesNotThrow(() -> new HostEndpoint(hostname, 443));
		assertEquals(253, endpoint.hostname().length());
	}
	
	@Test
	void constructWithHostnameWithoutWhitespace() {
		HostEndpoint endpoint = assertDoesNotThrow(() -> new HostEndpoint("sub.domain.example.com", 443));
		assertEquals("sub.domain.example.com", endpoint.hostname());
	}
	
	@Test
	void constructWithSingleCharacterHostname() {
		HostEndpoint endpoint = new HostEndpoint("a", 80);
		assertEquals("a", endpoint.hostname());
		assertEquals(80, endpoint.port());
	}
	
	@Test
	void constructWithMinPortBoundary() {
		HostEndpoint endpoint = assertDoesNotThrow(() -> new HostEndpoint("example.com", 0));
		assertEquals(0, endpoint.port());
	}
	
	@Test
	void constructWithMaxPortBoundary() {
		HostEndpoint endpoint = assertDoesNotThrow(() -> new HostEndpoint("example.com", 65535));
		assertEquals(65535, endpoint.port());
	}
	
	@Test
	void resolveReturnsAddressForResolvableHostname() {
		Optional<IpEndpoint> resolved = new HostEndpoint("localhost", 8080).resolve();
		
		assertTrue(resolved.isPresent());
		assertEquals(8080, resolved.orElseThrow().port());
		assertTrue(resolved.orElseThrow().toInetSocketAddress().getAddress().isLoopbackAddress());
	}
	
	@Test
	void resolveReturnsEmptyForUnresolvableHostname() {
		assertTrue(new HostEndpoint(UNRESOLVABLE_HOSTNAME, 8080).resolve().isEmpty());
	}
	
	@Test
	void toInetSocketAddressResolvesKnownHostname() {
		InetSocketAddress address = new HostEndpoint("localhost", 8080).toInetSocketAddress();
		
		assertFalse(address.isUnresolved());
		assertEquals(8080, address.getPort());
		assertEquals("localhost", address.getHostString());
	}
	
	@Test
	void toInetSocketAddressWithUnresolvableHostname() {
		InetSocketAddress address = assertDoesNotThrow(() -> new HostEndpoint(UNRESOLVABLE_HOSTNAME, 8080).toInetSocketAddress());
		
		assertTrue(address.isUnresolved());
		assertEquals(UNRESOLVABLE_HOSTNAME, address.getHostString());
		assertEquals(8080, address.getPort());
	}
	
	@Test
	void maxHostnameLengthConstantHasExpectedValue() {
		assertEquals(253, HostEndpoint.MAX_HOSTNAME_LENGTH);
	}
	
	@Test
	void portConstantsAreInheritedFromEndpoint() {
		assertEquals(0, HostEndpoint.MIN_PORT);
		assertEquals(65535, HostEndpoint.MAX_PORT);
	}
	
	@Test
	void toStringFormatsHostAndPort() {
		assertEquals("smtp.example.com:587", new HostEndpoint("smtp.example.com", 587).toString());
	}
	
	@Test
	void toStringWithZeroPort() {
		assertEquals("example.com:0", new HostEndpoint("example.com", 0).toString());
	}
	
	@Test
	void accessorsReturnComponents() {
		HostEndpoint endpoint = new HostEndpoint("example.com", 1234);
		assertEquals("example.com", endpoint.hostname());
		assertEquals(1234, endpoint.port());
	}
	
	@Test
	void implementsEndpointInterface() {
		assertInstanceOf(Endpoint.class, new HostEndpoint("example.com", 443));
	}
	
	@Test
	void equalEndpointsAreEqual() {
		HostEndpoint first = new HostEndpoint("example.com", 443);
		HostEndpoint second = new HostEndpoint("example.com", 443);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void endpointsDifferByPort() {
		assertNotEquals(new HostEndpoint("example.com", 443), new HostEndpoint("example.com", 8443));
	}
	
	@Test
	void endpointsDifferByHostname() {
		assertNotEquals(new HostEndpoint("a.example.com", 443), new HostEndpoint("b.example.com", 443));
	}
	
	@Test
	void hostnameComparisonIsCaseSensitive() {
		assertNotEquals(new HostEndpoint("Example.com", 443), new HostEndpoint("example.com", 443));
	}
	
	@Test
	void hostEndpointDoesNotEqualIpEndpoint() {
		assertNotEquals(new HostEndpoint("127.0.0.1", 8080), new IpEndpoint(Ipv4Address.LOOPBACK, 8080));
	}
	
	@Test
	void resolveRoundTripToInetSocketAddress() {
		HostEndpoint endpoint = new HostEndpoint("localhost", 8080);
		
		IpEndpoint resolved = endpoint.resolve().orElseThrow();
		assertEquals(endpoint.toInetSocketAddress().getAddress(), resolved.toInetSocketAddress().getAddress());
		assertEquals(8080, resolved.port());
		assertEquals(8080, endpoint.toInetSocketAddress().getPort());
	}
	
	@Test
	void constructWithIpLiteralAsHostname() {
		HostEndpoint endpoint = new HostEndpoint("127.0.0.1", 8080);
		
		assertEquals("127.0.0.1:8080", endpoint.toString());
		assertTrue(endpoint.resolve().isPresent());
		assertTrue(endpoint.resolve().orElseThrow().toInetSocketAddress().getAddress().isLoopbackAddress());
	}
	
	@Test
	void constructWithTrailingDotFqdn() {
		HostEndpoint endpoint = assertDoesNotThrow(() -> new HostEndpoint("example.com.", 443));
		assertEquals("example.com.", endpoint.hostname());
	}
}
