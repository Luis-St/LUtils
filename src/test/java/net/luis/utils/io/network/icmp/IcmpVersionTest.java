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

package net.luis.utils.io.network.icmp;

import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IcmpVersion}.<br>
 *
 * @author Luis-St
 */
class IcmpVersionTest {
	
	@Test
	void ofWithNullAddress() {
		assertThrows(NullPointerException.class, () -> IcmpVersion.of(null));
	}
	
	@Test
	void supportsWithNullAddress() {
		assertThrows(NullPointerException.class, () -> IcmpVersion.ICMP_V4.supports(null));
	}
	
	@Test
	void ofIpv4AddressReturnsIcmpV4() {
		assertEquals(IcmpVersion.ICMP_V4, IcmpVersion.of(Ipv4Address.LOOPBACK));
	}
	
	@Test
	void ofIpv6AddressReturnsIcmpV6() {
		assertEquals(IcmpVersion.ICMP_V6, IcmpVersion.of(Ipv6Address.LOOPBACK));
	}
	
	@Test
	void supportsMatchingVersion() {
		assertTrue(IcmpVersion.ICMP_V4.supports(Ipv4Address.LOOPBACK));
		assertTrue(IcmpVersion.ICMP_V6.supports(Ipv6Address.LOOPBACK));
	}
	
	@Test
	void supportsMismatchingVersion() {
		assertFalse(IcmpVersion.ICMP_V4.supports(Ipv6Address.LOOPBACK));
		assertFalse(IcmpVersion.ICMP_V6.supports(Ipv4Address.LOOPBACK));
	}
	
	@Test
	void icmpV4Constants() {
		assertEquals(4, IcmpVersion.ICMP_V4.ipVersion());
		assertEquals(8, IcmpVersion.ICMP_V4.echoRequestType());
		assertEquals(0, IcmpVersion.ICMP_V4.echoReplyType());
	}
	
	@Test
	void icmpV6Constants() {
		assertEquals(6, IcmpVersion.ICMP_V6.ipVersion());
		assertEquals(128, IcmpVersion.ICMP_V6.echoRequestType());
		assertEquals(129, IcmpVersion.ICMP_V6.echoReplyType());
	}
	
	@Test
	void valuesContainsBothVersions() {
		IcmpVersion[] values = IcmpVersion.values();
		assertEquals(2, values.length);
		assertEquals(IcmpVersion.ICMP_V4, values[0]);
		assertEquals(IcmpVersion.ICMP_V6, values[1]);
		assertEquals(IcmpVersion.ICMP_V6, IcmpVersion.valueOf("ICMP_V6"));
	}
	
	@Test
	void ofWithZonedIpv6Address() {
		Ipv6Address address = Ipv6Address.LOOPBACK.withZoneId("1");
		assertEquals(IcmpVersion.ICMP_V6, IcmpVersion.of(address));
		assertTrue(IcmpVersion.ICMP_V6.supports(address));
	}
	
	@Test
	void ofAndSupportsAreConsistent() {
		Ipv4Address ipv4 = Ipv4Address.fromOctets(192, 0, 2, 1);
		Ipv6Address ipv6 = Ipv6Address.fromBits(0x20010DB800000000L, 1L);
		assertTrue(IcmpVersion.of(ipv4).supports(ipv4));
		assertTrue(IcmpVersion.of(ipv6).supports(ipv6));
	}
}
