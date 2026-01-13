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

package net.luis.utils.io.network.address.ipv4;

import net.luis.utils.io.network.IpEndpoint;
import net.luis.utils.io.network.address.AddressType;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Ipv4Address}.<br>
 *
 * @author Luis-St
 */
class Ipv4AddressTest {
	
	@Test
	void constructWithValue() {
		Ipv4Address address = new Ipv4Address(0xC0A80101);
		assertEquals("192.168.1.1", address.toString());
	}
	
	@Test
	void fromNullInet4Address() {
		assertThrows(NullPointerException.class, () -> Ipv4Address.from(null));
	}
	
	@Test
	void fromInet4Address() throws Exception {
		Inet4Address inet = (Inet4Address) Inet4Address.getByName("192.168.1.1");
		Ipv4Address address = Ipv4Address.from(inet);
		assertEquals("192.168.1.1", address.toString());
	}
	
	@Test
	void fromValue() {
		Ipv4Address address = Ipv4Address.fromValue(0x7F000001);
		assertEquals("127.0.0.1", address.toString());
	}
	
	@Test
	void fromUnsignedLong() {
		Ipv4Address address = Ipv4Address.fromUnsignedLong(3232235777L);
		assertEquals("192.168.1.1", address.toString());
	}
	
	@Test
	void fromUnsignedLongInvalidRange() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromUnsignedLong(-1));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromUnsignedLong(0x100000000L));
	}
	
	@Test
	void fromNullBytes() {
		assertThrows(NullPointerException.class, () -> Ipv4Address.fromBytes(null));
	}
	
	@Test
	void fromBytesInvalidLength() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromBytes(new byte[] { 1, 2, 3 }));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromBytes(new byte[] { 1, 2, 3, 4, 5 }));
	}
	
	@Test
	void fromBytes() {
		Ipv4Address address = Ipv4Address.fromBytes(new byte[] { (byte) 192, (byte) 168, 1, 1 });
		assertEquals("192.168.1.1", address.toString());
	}
	
	@Test
	void fromOctets() {
		Ipv4Address address = Ipv4Address.fromOctets(10, 0, 0, 1);
		assertEquals("10.0.0.1", address.toString());
	}
	
	@Test
	void fromOctetsInvalidRange() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromOctets(-1, 0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromOctets(256, 0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromOctets(0, -1, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromOctets(0, 256, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromOctets(0, 0, -1, 0));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromOctets(0, 0, 256, 0));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromOctets(0, 0, 0, -1));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.fromOctets(0, 0, 0, 256));
	}
	
	@Test
	void version() {
		assertEquals(4, Ipv4Address.LOOPBACK.version());
	}
	
	@Test
	void bitLength() {
		assertEquals(32, Ipv4Address.LOOPBACK.bitLength());
	}
	
	@Test
	void toBytes() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		byte[] bytes = address.toBytes();
		
		assertEquals(4, bytes.length);
		assertEquals((byte) 192, bytes[0]);
		assertEquals((byte) 168, bytes[1]);
		assertEquals(1, bytes[2]);
		assertEquals(1, bytes[3]);
	}
	
	@Test
	void toBigInteger() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		BigInteger expected = BigInteger.valueOf(3232235777L);
		assertEquals(expected, address.toBigInteger());
	}
	
	@Test
	void getBit() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 0, 0, 0);
		assertTrue(address.getBit(0));
		assertTrue(address.getBit(1));
		assertFalse(address.getBit(2));
		assertFalse(address.getBit(3));
	}
	
	@Test
	void getBitInvalidIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv4Address.LOOPBACK.getBit(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv4Address.LOOPBACK.getBit(32));
	}
	
	@Test
	void withBit() {
		Ipv4Address address = Ipv4Address.UNSPECIFIED;
		Ipv4Address modified = address.withBit(0, true);
		assertTrue(modified.getBit(0));
		assertEquals(Ipv4Address.fromOctets(128, 0, 0, 0), modified);
	}
	
	@Test
	void withBitInvalidIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv4Address.LOOPBACK.withBit(-1, true));
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv4Address.LOOPBACK.withBit(32, true));
	}
	
	@Test
	void next() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		assertTrue(address.next().isPresent());
		assertEquals("192.168.1.2", address.next().get().toString());
	}
	
	@Test
	void nextAtMaxReturnsEmpty() {
		assertTrue(Ipv4Address.BROADCAST.next().isEmpty());
	}
	
	@Test
	void nextOverflowOctet() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 255);
		assertTrue(address.next().isPresent());
		assertEquals("192.168.2.0", address.next().get().toString());
	}
	
	@Test
	void previous() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 2);
		assertTrue(address.previous().isPresent());
		assertEquals("192.168.1.1", address.previous().get().toString());
	}
	
	@Test
	void previousAtMinReturnsEmpty() {
		assertTrue(Ipv4Address.UNSPECIFIED.previous().isEmpty());
	}
	
	@Test
	void distanceToNull() {
		assertThrows(NullPointerException.class, () -> Ipv4Address.LOOPBACK.distanceTo(null));
	}
	
	@Test
	void distanceTo() {
		Ipv4Address addr1 = Ipv4Address.fromOctets(192, 168, 1, 1);
		Ipv4Address addr2 = Ipv4Address.fromOctets(192, 168, 1, 10);
		assertEquals(BigInteger.valueOf(9), addr1.distanceTo(addr2));
		assertEquals(BigInteger.valueOf(-9), addr2.distanceTo(addr1));
	}
	
	@Test
	void isUnspecified() {
		assertTrue(Ipv4Address.UNSPECIFIED.isUnspecified());
		assertFalse(Ipv4Address.LOOPBACK.isUnspecified());
	}
	
	@Test
	void isLoopback() {
		assertTrue(Ipv4Address.LOOPBACK.isLoopback());
		assertTrue(Ipv4Address.fromOctets(127, 0, 0, 1).isLoopback());
		assertTrue(Ipv4Address.fromOctets(127, 255, 255, 255).isLoopback());
		assertFalse(Ipv4Address.fromOctets(128, 0, 0, 1).isLoopback());
	}
	
	@Test
	void isMulticast() {
		assertTrue(Ipv4Address.fromOctets(224, 0, 0, 1).isMulticast());
		assertTrue(Ipv4Address.fromOctets(239, 255, 255, 255).isMulticast());
		assertFalse(Ipv4Address.fromOctets(223, 255, 255, 255).isMulticast());
		assertFalse(Ipv4Address.fromOctets(240, 0, 0, 0).isMulticast());
	}
	
	@Test
	void isLinkLocal() {
		assertTrue(Ipv4Address.fromOctets(169, 254, 0, 1).isLinkLocal());
		assertTrue(Ipv4Address.fromOctets(169, 254, 255, 255).isLinkLocal());
		assertFalse(Ipv4Address.fromOctets(169, 253, 0, 0).isLinkLocal());
		assertFalse(Ipv4Address.fromOctets(169, 255, 0, 0).isLinkLocal());
	}
	
	@Test
	void isPrivateClassA() {
		assertTrue(Ipv4Address.fromOctets(10, 0, 0, 0).isPrivate());
		assertTrue(Ipv4Address.fromOctets(10, 255, 255, 255).isPrivate());
		assertFalse(Ipv4Address.fromOctets(11, 0, 0, 0).isPrivate());
	}
	
	@Test
	void isPrivateClassB() {
		assertTrue(Ipv4Address.fromOctets(172, 16, 0, 0).isPrivate());
		assertTrue(Ipv4Address.fromOctets(172, 31, 255, 255).isPrivate());
		assertFalse(Ipv4Address.fromOctets(172, 15, 0, 0).isPrivate());
		assertFalse(Ipv4Address.fromOctets(172, 32, 0, 0).isPrivate());
	}
	
	@Test
	void isPrivateClassC() {
		assertTrue(Ipv4Address.fromOctets(192, 168, 0, 0).isPrivate());
		assertTrue(Ipv4Address.fromOctets(192, 168, 255, 255).isPrivate());
		assertFalse(Ipv4Address.fromOctets(192, 169, 0, 0).isPrivate());
	}
	
	@Test
	void isDocumentationTestNet1() {
		assertTrue(Ipv4Address.fromOctets(192, 0, 2, 0).isDocumentation());
		assertTrue(Ipv4Address.fromOctets(192, 0, 2, 255).isDocumentation());
		assertFalse(Ipv4Address.fromOctets(192, 0, 3, 0).isDocumentation());
	}
	
	@Test
	void isDocumentationTestNet2() {
		assertTrue(Ipv4Address.fromOctets(198, 51, 100, 0).isDocumentation());
		assertTrue(Ipv4Address.fromOctets(198, 51, 100, 255).isDocumentation());
		assertFalse(Ipv4Address.fromOctets(198, 51, 101, 0).isDocumentation());
	}
	
	@Test
	void isDocumentationTestNet3() {
		assertTrue(Ipv4Address.fromOctets(203, 0, 113, 0).isDocumentation());
		assertTrue(Ipv4Address.fromOctets(203, 0, 113, 255).isDocumentation());
		assertFalse(Ipv4Address.fromOctets(203, 0, 114, 0).isDocumentation());
	}
	
	@Test
	void isBroadcast() {
		assertTrue(Ipv4Address.BROADCAST.isBroadcast());
		assertTrue(Ipv4Address.fromOctets(255, 255, 255, 255).isBroadcast());
		assertFalse(Ipv4Address.fromOctets(255, 255, 255, 254).isBroadcast());
	}
	
	@Test
	void isSharedAddressSpace() {
		assertTrue(Ipv4Address.fromOctets(100, 64, 0, 0).isSharedAddressSpace());
		assertTrue(Ipv4Address.fromOctets(100, 127, 255, 255).isSharedAddressSpace());
		assertFalse(Ipv4Address.fromOctets(100, 63, 255, 255).isSharedAddressSpace());
		assertFalse(Ipv4Address.fromOctets(100, 128, 0, 0).isSharedAddressSpace());
	}
	
	@Test
	void isGlobalUnicast() {
		assertTrue(Ipv4Address.fromOctets(8, 8, 8, 8).isGlobalUnicast());
		assertFalse(Ipv4Address.LOOPBACK.isGlobalUnicast());
		assertFalse(Ipv4Address.fromOctets(10, 0, 0, 1).isGlobalUnicast());
		assertFalse(Ipv4Address.fromOctets(224, 0, 0, 1).isGlobalUnicast());
	}
	
	@Test
	void isReserved() {
		assertTrue(Ipv4Address.fromOctets(0, 1, 2, 3).isReserved());
		assertTrue(Ipv4Address.fromOctets(240, 0, 0, 1).isReserved());
		assertFalse(Ipv4Address.UNSPECIFIED.isReserved());
		assertFalse(Ipv4Address.BROADCAST.isReserved());
	}
	
	@Test
	void getAddressType() {
		assertEquals(AddressType.UNSPECIFIED, Ipv4Address.UNSPECIFIED.getAddressType());
		assertEquals(AddressType.LOOPBACK, Ipv4Address.LOOPBACK.getAddressType());
		assertEquals(AddressType.PRIVATE, Ipv4Address.fromOctets(10, 0, 0, 1).getAddressType());
		assertEquals(AddressType.LINK_LOCAL, Ipv4Address.fromOctets(169, 254, 0, 1).getAddressType());
		assertEquals(AddressType.MULTICAST, Ipv4Address.fromOctets(224, 0, 0, 1).getAddressType());
		assertEquals(AddressType.BROADCAST, Ipv4Address.BROADCAST.getAddressType());
		assertEquals(AddressType.DOCUMENTATION, Ipv4Address.fromOctets(192, 0, 2, 1).getAddressType());
		assertEquals(AddressType.SHARED_ADDRESS_SPACE, Ipv4Address.fromOctets(100, 64, 0, 1).getAddressType());
		assertEquals(AddressType.GLOBAL_UNICAST, Ipv4Address.fromOctets(8, 8, 8, 8).getAddressType());
		assertEquals(AddressType.RESERVED, Ipv4Address.fromOctets(240, 0, 0, 1).getAddressType());
	}
	
	@Test
	void toReverseDnsName() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		assertEquals("1.1.168.192.in-addr.arpa", address.toReverseDnsName());
	}
	
	@Test
	void toInetAddress() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		assertInstanceOf(Inet4Address.class, address.toInetAddress());
		assertEquals("/192.168.1.1", address.toInetAddress().toString());
	}
	
	@Test
	void toSocketAddressInvalidPort() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.LOOPBACK.toSocketAddress(-1));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.LOOPBACK.toSocketAddress(65536));
	}
	
	@Test
	void toSocketAddress() {
		InetSocketAddress socketAddr = Ipv4Address.LOOPBACK.toSocketAddress(8080);
		assertEquals(8080, socketAddr.getPort());
		assertEquals("/127.0.0.1", socketAddr.getAddress().toString());
	}
	
	@Test
	void toEndpointInvalidPort() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.LOOPBACK.toEndpoint(-1));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.LOOPBACK.toEndpoint(65536));
	}
	
	@Test
	void toEndpoint() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		IpEndpoint endpoint = address.toEndpoint(8080);
		assertEquals(address, endpoint.address());
		assertEquals(8080, endpoint.port());
		assertEquals("192.168.1.1:8080", endpoint.toString());
	}
	
	@Test
	void minValue() {
		assertEquals(Ipv4Address.UNSPECIFIED, Ipv4Address.LOOPBACK.minValue());
	}
	
	@Test
	void maxValue() {
		assertEquals(Ipv4Address.BROADCAST, Ipv4Address.LOOPBACK.maxValue());
	}
	
	@Test
	void compareTo() {
		Ipv4Address addr1 = Ipv4Address.fromOctets(192, 168, 1, 1);
		Ipv4Address addr2 = Ipv4Address.fromOctets(192, 168, 1, 2);
		Ipv4Address addr3 = Ipv4Address.fromOctets(192, 168, 1, 1);
		
		assertTrue(addr1.compareTo(addr2) < 0);
		assertTrue(addr2.compareTo(addr1) > 0);
		assertEquals(0, addr1.compareTo(addr3));
	}
	
	@Test
	void getOctet() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 100);
		assertEquals(192, address.getOctet(0));
		assertEquals(168, address.getOctet(1));
		assertEquals(1, address.getOctet(2));
		assertEquals(100, address.getOctet(3));
	}
	
	@Test
	void getOctetInvalidIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv4Address.LOOPBACK.getOctet(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv4Address.LOOPBACK.getOctet(4));
	}
	
	@Test
	void withOctet() {
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 1);
		assertEquals("10.168.1.1", address.withOctet(0, 10).toString());
		assertEquals("192.10.1.1", address.withOctet(1, 10).toString());
		assertEquals("192.168.10.1", address.withOctet(2, 10).toString());
		assertEquals("192.168.1.10", address.withOctet(3, 10).toString());
	}
	
	@Test
	void withOctetInvalidIndex() {
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv4Address.LOOPBACK.withOctet(-1, 0));
		assertThrows(IndexOutOfBoundsException.class, () -> Ipv4Address.LOOPBACK.withOctet(4, 0));
	}
	
	@Test
	void withOctetInvalidValue() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.LOOPBACK.withOctet(0, -1));
		assertThrows(IllegalArgumentException.class, () -> Ipv4Address.LOOPBACK.withOctet(0, 256));
	}
	
	@Test
	void toUnsignedLong() {
		assertEquals(0L, Ipv4Address.UNSPECIFIED.toUnsignedLong());
		assertEquals(2130706433L, Ipv4Address.LOOPBACK.toUnsignedLong());
		assertEquals(4294967295L, Ipv4Address.BROADCAST.toUnsignedLong());
	}
	
	@Test
	void toStringFormat() {
		assertEquals("0.0.0.0", Ipv4Address.UNSPECIFIED.toString());
		assertEquals("127.0.0.1", Ipv4Address.LOOPBACK.toString());
		assertEquals("255.255.255.255", Ipv4Address.BROADCAST.toString());
		assertEquals("192.168.1.1", Ipv4Address.fromOctets(192, 168, 1, 1).toString());
	}
	
	@Test
	void constantsAreDefined() {
		assertEquals(32, Ipv4Address.BIT_LENGTH);
		assertEquals(4, Ipv4Address.OCTET_COUNT);
		assertNotNull(Ipv4Address.UNSPECIFIED);
		assertNotNull(Ipv4Address.LOOPBACK);
		assertNotNull(Ipv4Address.BROADCAST);
	}
	
	@Test
	void recordEquals() {
		Ipv4Address addr1 = Ipv4Address.fromOctets(192, 168, 1, 1);
		Ipv4Address addr2 = Ipv4Address.fromOctets(192, 168, 1, 1);
		Ipv4Address addr3 = Ipv4Address.fromOctets(192, 168, 1, 2);
		
		assertEquals(addr1, addr2);
		assertNotEquals(addr1, addr3);
	}
	
	@Test
	void recordHashCode() {
		Ipv4Address addr1 = Ipv4Address.fromOctets(192, 168, 1, 1);
		Ipv4Address addr2 = Ipv4Address.fromOctets(192, 168, 1, 1);
		
		assertEquals(addr1.hashCode(), addr2.hashCode());
	}
}
