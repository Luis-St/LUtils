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

import net.luis.utils.io.network.address.exception.IpParseException;
import net.luis.utils.io.network.address.format.IpParseOptions;
import net.luis.utils.io.network.address.ipv4.*;
import net.luis.utils.io.network.address.ipv6.*;
import org.junit.jupiter.api.Test;

import java.net.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpAddresses}.<br>
 *
 * @author Luis-St
 */
class IpAddressesTest {
	
	@Test
	void ipv4LoopbackNetworkIsDefined() {
		assertNotNull(IpAddresses.IPV4_LOOPBACK_NETWORK);
		assertTrue(IpAddresses.IPV4_LOOPBACK_NETWORK.contains(Ipv4Address.LOOPBACK));
	}
	
	@Test
	void ipv4LinkLocalNetworkIsDefined() {
		assertNotNull(IpAddresses.IPV4_LINK_LOCAL_NETWORK);
		assertEquals(16, IpAddresses.IPV4_LINK_LOCAL_NETWORK.prefixLength());
	}
	
	@Test
	void ipv4PrivateClassAIsDefined() {
		assertNotNull(IpAddresses.IPV4_PRIVATE_CLASS_A);
		assertEquals(8, IpAddresses.IPV4_PRIVATE_CLASS_A.prefixLength());
	}
	
	@Test
	void ipv4PrivateClassBIsDefined() {
		assertNotNull(IpAddresses.IPV4_PRIVATE_CLASS_B);
		assertEquals(12, IpAddresses.IPV4_PRIVATE_CLASS_B.prefixLength());
	}
	
	@Test
	void ipv4PrivateClassCIsDefined() {
		assertNotNull(IpAddresses.IPV4_PRIVATE_CLASS_C);
		assertEquals(16, IpAddresses.IPV4_PRIVATE_CLASS_C.prefixLength());
	}
	
	@Test
	void ipv4SharedAddressSpaceIsDefined() {
		assertNotNull(IpAddresses.IPV4_SHARED_ADDRESS_SPACE);
		assertEquals(10, IpAddresses.IPV4_SHARED_ADDRESS_SPACE.prefixLength());
	}
	
	@Test
	void ipv6LoopbackNetworkIsDefined() {
		assertNotNull(IpAddresses.IPV6_LOOPBACK_NETWORK);
		assertEquals(128, IpAddresses.IPV6_LOOPBACK_NETWORK.prefixLength());
	}
	
	@Test
	void ipv6LinkLocalNetworkIsDefined() {
		assertNotNull(IpAddresses.IPV6_LINK_LOCAL_NETWORK);
		assertEquals(10, IpAddresses.IPV6_LINK_LOCAL_NETWORK.prefixLength());
	}
	
	@Test
	void ipv6UniqueLocalNetworkIsDefined() {
		assertNotNull(IpAddresses.IPV6_UNIQUE_LOCAL_NETWORK);
		assertEquals(7, IpAddresses.IPV6_UNIQUE_LOCAL_NETWORK.prefixLength());
	}
	
	@Test
	void ipv6DocumentationNetworkIsDefined() {
		assertNotNull(IpAddresses.IPV6_DOCUMENTATION_NETWORK);
		assertEquals(32, IpAddresses.IPV6_DOCUMENTATION_NETWORK.prefixLength());
	}
	
	@Test
	void ipv6MulticastNetworkIsDefined() {
		assertNotNull(IpAddresses.IPV6_MULTICAST_NETWORK);
		assertEquals(8, IpAddresses.IPV6_MULTICAST_NETWORK.prefixLength());
	}
	
	@Test
	void parseIpv4Null() {
		assertThrows(NullPointerException.class, () -> IpAddresses.parseIpv4(null));
	}
	
	@Test
	void parseIpv4Invalid() {
		assertThrows(IpParseException.class, () -> IpAddresses.parseIpv4("invalid"));
		assertThrows(IpParseException.class, () -> IpAddresses.parseIpv4("256.0.0.1"));
	}
	
	@Test
	void parseIpv4Valid() {
		Ipv4Address address = IpAddresses.parseIpv4("192.168.1.1");
		assertEquals("192.168.1.1", address.toString());
	}
	
	@Test
	void parseIpv4WithOptions() {
		IpParseOptions options = IpParseOptions.builder().allowLeadingZeros(true).build();
		Ipv4Address address = IpAddresses.parseIpv4("192.168.001.001", options);
		assertEquals("192.168.1.1", address.toString());
	}
	
	@Test
	void tryParseIpv4Null() {
		assertThrows(NullPointerException.class, () -> IpAddresses.tryParseIpv4(null));
	}
	
	@Test
	void tryParseIpv4Invalid() {
		assertTrue(IpAddresses.tryParseIpv4("invalid").isEmpty());
		assertTrue(IpAddresses.tryParseIpv4("256.0.0.1").isEmpty());
	}
	
	@Test
	void tryParseIpv4Valid() {
		assertTrue(IpAddresses.tryParseIpv4("192.168.1.1").isPresent());
		assertEquals("192.168.1.1", IpAddresses.tryParseIpv4("192.168.1.1").get().toString());
	}
	
	@Test
	void parseIpv6Null() {
		assertThrows(NullPointerException.class, () -> IpAddresses.parseIpv6(null));
	}
	
	@Test
	void parseIpv6Invalid() {
		assertThrows(IpParseException.class, () -> IpAddresses.parseIpv6("invalid"));
		assertThrows(IpParseException.class, () -> IpAddresses.parseIpv6("gggg::1"));
	}
	
	@Test
	void parseIpv6Valid() {
		Ipv6Address address = IpAddresses.parseIpv6("2001:db8::1");
		assertNotNull(address);
	}
	
	@Test
	void parseIpv6FullForm() {
		Ipv6Address address = IpAddresses.parseIpv6("2001:0db8:0000:0000:0000:0000:0000:0001");
		assertNotNull(address);
	}
	
	@Test
	void parseIpv6WithZoneId() {
		Ipv6Address address = IpAddresses.parseIpv6("fe80::1%eth0");
		assertNotNull(address.zoneId());
		assertEquals("eth0", address.zoneId());
	}
	
	@Test
	void tryParseIpv6Null() {
		assertThrows(NullPointerException.class, () -> IpAddresses.tryParseIpv6(null));
	}
	
	@Test
	void tryParseIpv6Invalid() {
		assertTrue(IpAddresses.tryParseIpv6("invalid").isEmpty());
	}
	
	@Test
	void tryParseIpv6Valid() {
		assertTrue(IpAddresses.tryParseIpv6("2001:db8::1").isPresent());
	}
	
	@Test
	void parseNull() {
		assertThrows(NullPointerException.class, () -> IpAddresses.parse(null));
	}
	
	@Test
	void parseInvalid() {
		assertThrows(IpParseException.class, () -> IpAddresses.parse("invalid"));
	}
	
	@Test
	void parseDetectsIpv4() {
		IpAddress<?> address = IpAddresses.parse("192.168.1.1");
		assertInstanceOf(Ipv4Address.class, address);
	}
	
	@Test
	void parseDetectsIpv6() {
		IpAddress<?> address = IpAddresses.parse("2001:db8::1");
		assertInstanceOf(Ipv6Address.class, address);
	}
	
	@Test
	void tryParseNull() {
		assertThrows(NullPointerException.class, () -> IpAddresses.tryParse(null));
	}
	
	@Test
	void tryParseInvalid() {
		assertTrue(IpAddresses.tryParse("invalid").isEmpty());
	}
	
	@Test
	void tryParseValid() {
		assertTrue(IpAddresses.tryParse("192.168.1.1").isPresent());
		assertTrue(IpAddresses.tryParse("2001:db8::1").isPresent());
	}
	
	@Test
	void isValidIpv4Null() {
		assertThrows(NullPointerException.class, () -> IpAddresses.isValidIpv4(null));
	}
	
	@Test
	void isValidIpv4() {
		assertTrue(IpAddresses.isValidIpv4("192.168.1.1"));
		assertFalse(IpAddresses.isValidIpv4("invalid"));
		assertFalse(IpAddresses.isValidIpv4("2001:db8::1"));
	}
	
	@Test
	void isValidIpv6Null() {
		assertThrows(NullPointerException.class, () -> IpAddresses.isValidIpv6(null));
	}
	
	@Test
	void isValidIpv6() {
		assertTrue(IpAddresses.isValidIpv6("2001:db8::1"));
		assertFalse(IpAddresses.isValidIpv6("invalid"));
		assertFalse(IpAddresses.isValidIpv6("192.168.1.1"));
	}
	
	@Test
	void isValidNull() {
		assertThrows(NullPointerException.class, () -> IpAddresses.isValid(null));
	}
	
	@Test
	void isValid() {
		assertTrue(IpAddresses.isValid("192.168.1.1"));
		assertTrue(IpAddresses.isValid("2001:db8::1"));
		assertFalse(IpAddresses.isValid("invalid"));
	}
	
	@Test
	void parseIpv4NetworkNull() {
		assertThrows(NullPointerException.class, () -> IpAddresses.parseIpv4Network(null));
	}
	
	@Test
	void parseIpv4NetworkInvalid() {
		assertThrows(IpParseException.class, () -> IpAddresses.parseIpv4Network("invalid"));
		assertThrows(IpParseException.class, () -> IpAddresses.parseIpv4Network("192.168.1.1"));
	}
	
	@Test
	void parseIpv4NetworkValid() {
		Ipv4Network network = IpAddresses.parseIpv4Network("192.168.1.0/24");
		assertEquals(24, network.prefixLength());
	}
	
	@Test
	void tryParseIpv4NetworkNull() {
		assertThrows(NullPointerException.class, () -> IpAddresses.tryParseIpv4Network(null));
	}
	
	@Test
	void tryParseIpv4NetworkInvalid() {
		assertTrue(IpAddresses.tryParseIpv4Network("invalid").isEmpty());
	}
	
	@Test
	void tryParseIpv4NetworkValid() {
		assertTrue(IpAddresses.tryParseIpv4Network("192.168.1.0/24").isPresent());
	}
	
	@Test
	void parseIpv6NetworkNull() {
		assertThrows(NullPointerException.class, () -> IpAddresses.parseIpv6Network(null));
	}
	
	@Test
	void parseIpv6NetworkInvalid() {
		assertThrows(IpParseException.class, () -> IpAddresses.parseIpv6Network("invalid"));
	}
	
	@Test
	void parseIpv6NetworkValid() {
		Ipv6Network network = IpAddresses.parseIpv6Network("2001:db8::/32");
		assertEquals(32, network.prefixLength());
	}
	
	@Test
	void tryParseIpv6NetworkNull() {
		assertThrows(NullPointerException.class, () -> IpAddresses.tryParseIpv6Network(null));
	}
	
	@Test
	void tryParseIpv6NetworkInvalid() {
		assertTrue(IpAddresses.tryParseIpv6Network("invalid").isEmpty());
	}
	
	@Test
	void tryParseIpv6NetworkValid() {
		assertTrue(IpAddresses.tryParseIpv6Network("2001:db8::/32").isPresent());
	}
	
	@Test
	void fromInetAddressNull() {
		assertThrows(NullPointerException.class, () -> IpAddresses.from((InetAddress) null));
	}
	
	@Test
	void fromInet4Address() throws UnknownHostException {
		Inet4Address inet = (Inet4Address) InetAddress.getByName("192.168.1.1");
		IpAddress<?> address = IpAddresses.from(inet);
		assertInstanceOf(Ipv4Address.class, address);
	}
	
	@Test
	void fromInet6Address() throws UnknownHostException {
		Inet6Address inet = (Inet6Address) InetAddress.getByName("::1");
		IpAddress<?> address = IpAddresses.from(inet);
		assertInstanceOf(Ipv6Address.class, address);
	}
	
	@Test
	void ipv4Range() {
		Ipv4Address start = Ipv4Address.fromOctets(192, 168, 1, 1);
		Ipv4Address end = Ipv4Address.fromOctets(192, 168, 1, 10);
		Ipv4Range range = IpAddresses.ipv4Range(start, end);
		
		assertEquals(start, range.start());
		assertEquals(end, range.end());
	}
	
	@Test
	void ipv6Range() {
		Ipv6Address start = new Ipv6Address(0L, 1L);
		Ipv6Address end = new Ipv6Address(0L, 10L);
		Ipv6Range range = IpAddresses.ipv6Range(start, end);
		
		assertEquals(start, range.start());
		assertEquals(end, range.end());
	}
}
