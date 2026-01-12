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

package net.luis.utils.io.network.address.ipv6;

import net.luis.utils.io.network.address.AddressType;
import net.luis.utils.io.network.address.ipv4.Ipv4Address;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Ipv6Address}.<br>
 *
 * @author Luis-St
 */
class Ipv6AddressTest {
	
	@Test
	void constructWithHighLowBits() {
		Ipv6Address address = new Ipv6Address(0x20010DB8L << 32, 1L);
		assertEquals(0x20010DB800000000L, address.highBits());
		assertEquals(1L, address.lowBits());
	}
	
	@Test
	void constructWithZoneId() {
		Ipv6Address address = new Ipv6Address(0L, 1L, "eth0");
		assertEquals("eth0", address.zoneId());
	}
	
	@Test
	void fromNullInet6Address() {
		assertThrows(NullPointerException.class, () -> Ipv6Address.from(null));
	}
	
	@Test
	void fromInet6Address() throws Exception {
		Inet6Address inet = (Inet6Address) Inet6Address.getByName("::1");
		Ipv6Address address = Ipv6Address.from(inet);
		assertEquals(0L, address.highBits());
		assertEquals(1L, address.lowBits());
	}
	
	@Test
	void fromBits() {
		Ipv6Address address = Ipv6Address.fromBits(0x20010DB800000000L, 0x0000000000000001L);
		assertEquals(0x20010DB800000000L, address.highBits());
		assertEquals(1L, address.lowBits());
	}
	
	@Test
	void fromBytesNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Address.fromBytes(null));
	}
	
	@Test
	void fromBytesInvalidLength() {
		assertThrows(IllegalArgumentException.class, () -> Ipv6Address.fromBytes(new byte[15]));
		assertThrows(IllegalArgumentException.class, () -> Ipv6Address.fromBytes(new byte[17]));
	}
	
	@Test
	void fromBytes() {
		byte[] bytes = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };
		Ipv6Address address = Ipv6Address.fromBytes(bytes);
		assertEquals(Ipv6Address.LOOPBACK, address);
	}
	
	@Test
	void version() {
		assertEquals(6, Ipv6Address.LOOPBACK.version());
	}
	
	@Test
	void bitLength() {
		assertEquals(128, Ipv6Address.LOOPBACK.bitLength());
	}
	
	@Test
	void toBytes() {
		byte[] bytes = Ipv6Address.LOOPBACK.toBytes();
		assertEquals(16, bytes.length);
		for (int i = 0; i < 15; i++) {
			assertEquals(0, bytes[i]);
		}
		assertEquals(1, bytes[15]);
	}
	
	@Test
	void toBigInteger() {
		BigInteger value = Ipv6Address.LOOPBACK.toBigInteger();
		assertEquals(BigInteger.ONE, value);
	}
	
	@Test
	void getBit() {
		Ipv6Address address = Ipv6Address.LOOPBACK;
		assertFalse(address.getBit(0));
		assertTrue(address.getBit(127));
	}
	
	@Test
	void getBitInvalidIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv6Address.LOOPBACK.getBit(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv6Address.LOOPBACK.getBit(128));
	}
	
	@Test
	void withBit() {
		Ipv6Address address = Ipv6Address.UNSPECIFIED;
		Ipv6Address modified = address.withBit(127, true);
		assertEquals(Ipv6Address.LOOPBACK, modified);
	}
	
	@Test
	void withBitInvalidIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv6Address.LOOPBACK.withBit(-1, true));
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv6Address.LOOPBACK.withBit(128, true));
	}
	
	@Test
	void next() {
		assertTrue(Ipv6Address.LOOPBACK.next().isPresent());
		Ipv6Address next = Ipv6Address.LOOPBACK.next().get();
		assertEquals(0L, next.highBits());
		assertEquals(2L, next.lowBits());
	}
	
	@Test
	void nextAtMaxReturnsEmpty() {
		assertTrue(Ipv6Address.MAX.next().isEmpty());
	}
	
	@Test
	void nextOverflowLowBits() {
		Ipv6Address address = new Ipv6Address(0L, -1L);
		assertTrue(address.next().isPresent());
		Ipv6Address next = address.next().get();
		assertEquals(1L, next.highBits());
		assertEquals(0L, next.lowBits());
	}
	
	@Test
	void previous() {
		Ipv6Address address = new Ipv6Address(0L, 2L);
		assertTrue(address.previous().isPresent());
		assertEquals(Ipv6Address.LOOPBACK, address.previous().get());
	}
	
	@Test
	void previousAtMinReturnsEmpty() {
		assertTrue(Ipv6Address.UNSPECIFIED.previous().isEmpty());
	}
	
	@Test
	void previousUnderflowLowBits() {
		Ipv6Address address = new Ipv6Address(1L, 0L);
		assertTrue(address.previous().isPresent());
		Ipv6Address prev = address.previous().get();
		assertEquals(0L, prev.highBits());
		assertEquals(-1L, prev.lowBits());
	}
	
	@Test
	void distanceToNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Address.LOOPBACK.distanceTo(null));
	}
	
	@Test
	void distanceTo() {
		Ipv6Address addr1 = Ipv6Address.LOOPBACK;
		Ipv6Address addr2 = new Ipv6Address(0L, 10L);
		assertEquals(BigInteger.valueOf(9), addr1.distanceTo(addr2));
	}
	
	@Test
	void isUnspecified() {
		assertTrue(Ipv6Address.UNSPECIFIED.isUnspecified());
		assertFalse(Ipv6Address.LOOPBACK.isUnspecified());
	}
	
	@Test
	void isLoopback() {
		assertTrue(Ipv6Address.LOOPBACK.isLoopback());
		assertFalse(Ipv6Address.UNSPECIFIED.isLoopback());
	}
	
	@Test
	void isMulticast() {
		Ipv6Address multicast = new Ipv6Address(0xFF02000000000000L, 1L);
		assertTrue(multicast.isMulticast());
		assertFalse(Ipv6Address.LOOPBACK.isMulticast());
	}
	
	@Test
	void isLinkLocal() {
		Ipv6Address linkLocal = new Ipv6Address(0xFE80000000000000L, 1L);
		assertTrue(linkLocal.isLinkLocal());
		assertFalse(Ipv6Address.LOOPBACK.isLinkLocal());
	}
	
	@Test
	void isPrivate() {
		Ipv6Address private6 = new Ipv6Address(0xFC00000000000000L, 1L);
		assertTrue(private6.isPrivate());
		assertFalse(Ipv6Address.LOOPBACK.isPrivate());
	}
	
	@Test
	void isUniqueLocal() {
		Ipv6Address ula = new Ipv6Address(0xFC00000000000000L, 1L);
		assertTrue(ula.isUniqueLocal());
	}
	
	@Test
	void isDocumentation() {
		Ipv6Address doc = new Ipv6Address(0x20010DB800000000L, 1L);
		assertTrue(doc.isDocumentation());
		assertFalse(Ipv6Address.LOOPBACK.isDocumentation());
	}
	
	@Test
	void isGlobalUnicast() {
		// 2003:: is global unicast (not Teredo 2001:0000::/32 or documentation 2001:db8::/32)
		Ipv6Address global = new Ipv6Address(0x2003000000000000L, 1L);
		assertTrue(global.isGlobalUnicast());
		assertFalse(Ipv6Address.LOOPBACK.isGlobalUnicast());
		// Teredo addresses are not global unicast
		assertFalse(new Ipv6Address(0x2001000000000000L, 1L).isGlobalUnicast());
	}
	
	@Test
	void getAddressType() {
		assertEquals(AddressType.UNSPECIFIED, Ipv6Address.UNSPECIFIED.getAddressType());
		assertEquals(AddressType.LOOPBACK, Ipv6Address.LOOPBACK.getAddressType());
		assertEquals(AddressType.MULTICAST, new Ipv6Address(0xFF02000000000000L, 1L).getAddressType());
		assertEquals(AddressType.LINK_LOCAL, new Ipv6Address(0xFE80000000000000L, 1L).getAddressType());
		assertEquals(AddressType.PRIVATE, new Ipv6Address(0xFC00000000000000L, 1L).getAddressType());
		assertEquals(AddressType.DOCUMENTATION, new Ipv6Address(0x20010DB800000000L, 1L).getAddressType());
	}
	
	@Test
	void toReverseDnsName() {
		Ipv6Address address = Ipv6Address.LOOPBACK;
		String reverseDns = address.toReverseDnsName();
		assertTrue(reverseDns.endsWith(".ip6.arpa"));
		assertTrue(reverseDns.startsWith("1.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0.0."));
	}
	
	@Test
	void toInetAddress() {
		Ipv6Address address = Ipv6Address.LOOPBACK;
		assertInstanceOf(Inet6Address.class, address.toInetAddress());
	}
	
	@Test
	void toSocketAddressInvalidPort() {
		assertThrows(IllegalArgumentException.class, () -> Ipv6Address.LOOPBACK.toSocketAddress(-1));
		assertThrows(IllegalArgumentException.class, () -> Ipv6Address.LOOPBACK.toSocketAddress(65536));
	}
	
	@Test
	void toSocketAddress() {
		InetSocketAddress socketAddr = Ipv6Address.LOOPBACK.toSocketAddress(8080);
		assertEquals(8080, socketAddr.getPort());
	}
	
	@Test
	void minValue() {
		assertEquals(Ipv6Address.UNSPECIFIED, Ipv6Address.LOOPBACK.minValue());
	}
	
	@Test
	void maxValue() {
		Ipv6Address max = Ipv6Address.LOOPBACK.maxValue();
		assertEquals(-1L, max.highBits());
		assertEquals(-1L, max.lowBits());
	}
	
	@Test
	void compareTo() {
		Ipv6Address addr1 = Ipv6Address.LOOPBACK;
		Ipv6Address addr2 = new Ipv6Address(0L, 2L);
		Ipv6Address addr3 = Ipv6Address.LOOPBACK;
		
		assertTrue(addr1.compareTo(addr2) < 0);
		assertTrue(addr2.compareTo(addr1) > 0);
		assertEquals(0, addr1.compareTo(addr3));
	}
	
	@Test
	void getZoneId() {
		Ipv6Address withZone = new Ipv6Address(0L, 1L, "eth0");
		Ipv6Address withoutZone = Ipv6Address.LOOPBACK;
		
		assertTrue(withZone.getZoneId().isPresent());
		assertEquals("eth0", withZone.getZoneId().get());
		assertTrue(withoutZone.getZoneId().isEmpty());
	}
	
	@Test
	void withZoneIdNull() {
		assertThrows(NullPointerException.class, () -> Ipv6Address.LOOPBACK.withZoneId(null));
	}
	
	@Test
	void withZoneId() {
		Ipv6Address address = Ipv6Address.LOOPBACK.withZoneId("eth0");
		assertEquals("eth0", address.zoneId());
	}
	
	@Test
	void withoutZoneId() {
		Ipv6Address withZone = new Ipv6Address(0L, 1L, "eth0");
		Ipv6Address withoutZone = withZone.withoutZoneId();
		
		assertNull(withoutZone.zoneId());
	}
	
	@Test
	void withoutZoneIdAlreadyWithout() {
		Ipv6Address address = Ipv6Address.LOOPBACK;
		assertSame(address, address.withoutZoneId());
	}
	
	@Test
	void getHextet() {
		Ipv6Address address = new Ipv6Address(0x20010DB8ABCD0001L, 0x0002000300040005L);
		assertEquals(0x2001, address.getHextet(0));
		assertEquals(0x0DB8, address.getHextet(1));
		assertEquals(0xABCD, address.getHextet(2));
		assertEquals(0x0001, address.getHextet(3));
		assertEquals(0x0002, address.getHextet(4));
		assertEquals(0x0003, address.getHextet(5));
		assertEquals(0x0004, address.getHextet(6));
		assertEquals(0x0005, address.getHextet(7));
	}
	
	@Test
	void getHextetInvalidIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv6Address.LOOPBACK.getHextet(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv6Address.LOOPBACK.getHextet(8));
	}
	
	@Test
	void withHextet() {
		Ipv6Address address = Ipv6Address.UNSPECIFIED;
		Ipv6Address modified = address.withHextet(0, 0x2001);
		assertEquals(0x2001, modified.getHextet(0));
	}
	
	@Test
	void withHextetInvalidIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv6Address.LOOPBACK.withHextet(-1, 0));
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv6Address.LOOPBACK.withHextet(8, 0));
	}
	
	@Test
	void withHextetInvalidValue() {
		assertThrows(IllegalArgumentException.class, () -> Ipv6Address.LOOPBACK.withHextet(0, -1));
		assertThrows(IllegalArgumentException.class, () -> Ipv6Address.LOOPBACK.withHextet(0, 0x10000));
	}
	
	@Test
	void isIpv4Mapped() {
		Ipv6Address mapped = new Ipv6Address(0L, 0xFFFF00000000L | 0xC0A80101L);
		assertTrue(mapped.isIpv4Mapped());
		assertFalse(Ipv6Address.LOOPBACK.isIpv4Mapped());
	}
	
	@Test
	@SuppressWarnings("deprecation")
	void isIpv4Compatible() {
		Ipv6Address compatible = new Ipv6Address(0L, 0xC0A80101L);
		assertTrue(compatible.isIpv4Compatible());
		assertFalse(Ipv6Address.LOOPBACK.isIpv4Compatible());
		assertFalse(Ipv6Address.UNSPECIFIED.isIpv4Compatible());
	}
	
	@Test
	void isIpv4Translatable() {
		Ipv6Address translatable = new Ipv6Address(0x0064FF9B00000000L, 0xC0A80101L);
		assertTrue(translatable.isIpv4Translatable());
		assertFalse(Ipv6Address.LOOPBACK.isIpv4Translatable());
	}
	
	@Test
	void toIpv4FromMapped() {
		Ipv6Address mapped = new Ipv6Address(0L, 0xFFFF00000000L | 0xC0A80101L);
		assertTrue(mapped.toIpv4().isPresent());
		assertEquals(Ipv4Address.fromOctets(192, 168, 1, 1), mapped.toIpv4().get());
	}
	
	@Test
	void toIpv4NotMapped() {
		assertTrue(Ipv6Address.LOOPBACK.toIpv4().isEmpty());
	}
	
	@Test
	void isTeredo() {
		Ipv6Address teredo = new Ipv6Address(0x2001000000000000L, 0L);
		assertTrue(teredo.isTeredo());
		assertFalse(Ipv6Address.LOOPBACK.isTeredo());
	}
	
	@Test
	void getTeredoInfoNotTeredo() {
		assertTrue(Ipv6Address.LOOPBACK.getTeredoInfo().isEmpty());
	}
	
	@Test
	void toSolicitedNodeMulticast() {
		Ipv6Address address = new Ipv6Address(0x20010DB800000000L, 0x0000000000000001L);
		Ipv6Address solicited = address.toSolicitedNodeMulticast();
		
		assertEquals(0xFF02000000000000L, solicited.highBits());
	}
	
	@Test
	void getMulticastScopeNotMulticast() {
		assertThrows(IllegalStateException.class, () -> Ipv6Address.LOOPBACK.getMulticastScope());
	}
	
	@Test
	void getMulticastScope() {
		Ipv6Address linkLocalMulticast = new Ipv6Address(0xFF02000000000000L, 1L);
		assertEquals(Ipv6MulticastScope.LINK_LOCAL, linkLocalMulticast.getMulticastScope());
	}
	
	@Test
	void toInet6Address() {
		Inet6Address inet = Ipv6Address.LOOPBACK.toInet6Address();
		assertNotNull(inet);
	}
	
	@Test
	void constantsAreDefined() {
		assertEquals(128, Ipv6Address.BIT_LENGTH);
		assertEquals(8, Ipv6Address.HEXTET_COUNT);
		assertNotNull(Ipv6Address.UNSPECIFIED);
		assertNotNull(Ipv6Address.LOOPBACK);
		assertNotNull(Ipv6Address.MAX);
	}
	
	@Test
	void recordEquals() {
		Ipv6Address addr1 = new Ipv6Address(0L, 1L);
		Ipv6Address addr2 = new Ipv6Address(0L, 1L);
		Ipv6Address addr3 = new Ipv6Address(0L, 2L);
		
		assertEquals(addr1, addr2);
		assertNotEquals(addr1, addr3);
	}
	
	@Test
	void recordHashCode() {
		Ipv6Address addr1 = new Ipv6Address(0L, 1L);
		Ipv6Address addr2 = new Ipv6Address(0L, 1L);
		
		assertEquals(addr1.hashCode(), addr2.hashCode());
	}
}
