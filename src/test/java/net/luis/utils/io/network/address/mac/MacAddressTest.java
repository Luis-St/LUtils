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

package net.luis.utils.io.network.address.mac;

import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import net.luis.utils.io.network.address.ipv6.Ipv6Network;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MacAddress}.<br>
 *
 * @author Luis-St
 */
class MacAddressTest {
	
	@Test
	void constructWithValue() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertEquals(0x001A2B3C4D5EL, address.value());
		assertEquals("00:1a:2b:3c:4d:5e", address.toString());
	}
	
	@Test
	void constructMasksHighBits() {
		MacAddress address = new MacAddress(0xFFFF_001A2B3C4D5EL);
		assertEquals(0x001A2B3C4D5EL, address.value());
	}
	
	@Test
	void ofWithValue() {
		MacAddress address = MacAddress.of(0x001A2B3C4D5EL);
		assertEquals(0x001A2B3C4D5EL, address.value());
	}
	
	@Test
	void toBytesReturnsCorrectArray() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		byte[] bytes = address.toBytes();
		
		assertEquals(6, bytes.length);
		assertEquals((byte) 0x00, bytes[0]);
		assertEquals((byte) 0x1A, bytes[1]);
		assertEquals((byte) 0x2B, bytes[2]);
		assertEquals((byte) 0x3C, bytes[3]);
		assertEquals((byte) 0x4D, bytes[4]);
		assertEquals((byte) 0x5E, bytes[5]);
	}
	
	@Test
	void toLongReturnsValue() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertEquals(0x001A2B3C4D5EL, address.toLong());
	}
	
	@Test
	void getOctetValidIndices() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		
		assertEquals(0x00, address.getOctet(0));
		assertEquals(0x1A, address.getOctet(1));
		assertEquals(0x2B, address.getOctet(2));
		assertEquals(0x3C, address.getOctet(3));
		assertEquals(0x4D, address.getOctet(4));
		assertEquals(0x5E, address.getOctet(5));
	}
	
	@Test
	void getOctetInvalidIndexNegative() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertThrows(IndexOutOfBoundsException.class, () -> address.getOctet(-1));
	}
	
	@Test
	void getOctetInvalidIndexTooLarge() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertThrows(IndexOutOfBoundsException.class, () -> address.getOctet(6));
		assertThrows(IndexOutOfBoundsException.class, () -> address.getOctet(7));
	}
	
	@Test
	void withOctetReturnsNewAddress() {
		MacAddress original = new MacAddress(0x001A2B3C4D5EL);
		MacAddress modified = original.withOctet(0, 0xFF);
		
		assertNotSame(original, modified);
		assertEquals(0x00, original.getOctet(0));
		assertEquals(0xFF, modified.getOctet(0));
		assertEquals(0x1A, modified.getOctet(1));
	}
	
	@Test
	void withOctetInvalidIndex() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertThrows(IndexOutOfBoundsException.class, () -> address.withOctet(-1, 0x00));
		assertThrows(IndexOutOfBoundsException.class, () -> address.withOctet(6, 0x00));
	}
	
	@Test
	void withOctetInvalidValue() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertThrows(IllegalArgumentException.class, () -> address.withOctet(0, -1));
		assertThrows(IllegalArgumentException.class, () -> address.withOctet(0, 256));
	}
	
	@Test
	void getOuiReturnsFirstThreeOctets() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		byte[] oui = address.getOui();
		
		assertEquals(3, oui.length);
		assertEquals((byte) 0x00, oui[0]);
		assertEquals((byte) 0x1A, oui[1]);
		assertEquals((byte) 0x2B, oui[2]);
	}
	
	@Test
	void getOuiStringFormat() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertEquals("00:1A:2B", address.getOuiString());
	}
	
	@Test
	void isUnicastWhenIGBitZero() {
		MacAddress unicast = new MacAddress(0x001A2B3C4D5EL);
		assertTrue(unicast.isUnicast());
		assertFalse(unicast.isMulticast());
	}
	
	@Test
	void isMulticastWhenIGBitOne() {
		MacAddress multicast = new MacAddress(0x011A2B3C4D5EL);
		assertTrue(multicast.isMulticast());
		assertFalse(multicast.isUnicast());
	}
	
	@Test
	void isUniversalWhenULBitZero() {
		MacAddress universal = new MacAddress(0x001A2B3C4D5EL);
		assertTrue(universal.isUniversal());
		assertFalse(universal.isLocal());
	}
	
	@Test
	void isLocalWhenULBitOne() {
		MacAddress local = new MacAddress(0x021A2B3C4D5EL);
		assertTrue(local.isLocal());
		assertFalse(local.isUniversal());
	}
	
	@Test
	void isBroadcastForBroadcastAddress() {
		assertTrue(MacAddress.BROADCAST.isBroadcast());
		assertTrue(new MacAddress(0xFFFFFFFFFFFFL).isBroadcast());
	}
	
	@Test
	void isBroadcastForNonBroadcast() {
		assertFalse(MacAddress.ZERO.isBroadcast());
		assertFalse(new MacAddress(0x001A2B3C4D5EL).isBroadcast());
	}
	
	@Test
	void toModifiedEui64InsertsFFEE() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		byte[] eui64 = address.toModifiedEui64();
		
		assertEquals(8, eui64.length);
		assertEquals((byte) 0x02, eui64[0]);
		assertEquals((byte) 0x1A, eui64[1]);
		assertEquals((byte) 0x2B, eui64[2]);
		assertEquals((byte) 0xFF, eui64[3]);
		assertEquals((byte) 0xFE, eui64[4]);
		assertEquals((byte) 0x3C, eui64[5]);
		assertEquals((byte) 0x4D, eui64[6]);
		assertEquals((byte) 0x5E, eui64[7]);
	}
	
	@Test
	void toLinkLocalIpv6GeneratesCorrectAddress() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		Ipv6Address ipv6 = address.toLinkLocalIpv6();
		
		assertTrue(ipv6.isLinkLocal());
		assertEquals(0xFE80_0000_0000_0000L, ipv6.highBits());
	}
	
	@Test
	void toIpv6WithValidPrefix() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		Ipv6Network prefix = new Ipv6Network(new Ipv6Address(0x2001_0DB8_0000_0000L, 0L), 64);
		Ipv6Address ipv6 = address.toIpv6(prefix);
		
		assertEquals(0x2001_0DB8_0000_0000L, ipv6.highBits());
	}
	
	@Test
	void toIpv6WithNullPrefix() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertThrows(NullPointerException.class, () -> address.toIpv6(null));
	}
	
	@Test
	void toIpv6WithNon64Prefix() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		Ipv6Network prefix48 = new Ipv6Network(new Ipv6Address(0x2001_0DB8_0000_0000L, 0L), 48);
		assertThrows(IllegalArgumentException.class, () -> address.toIpv6(prefix48));
	}
	
	@Test
	void toLocallyAdministeredSetsULBit() {
		MacAddress universal = new MacAddress(0x001A2B3C4D5EL);
		assertTrue(universal.isUniversal());
		
		MacAddress local = universal.toLocallyAdministered();
		assertTrue(local.isLocal());
		assertNotSame(universal, local);
	}
	
	@Test
	void toUniversallyAdministeredClearsULBit() {
		MacAddress local = new MacAddress(0x021A2B3C4D5EL);
		assertTrue(local.isLocal());
		
		MacAddress universal = local.toUniversallyAdministered();
		assertTrue(universal.isUniversal());
		assertNotSame(local, universal);
	}
	
	@Test
	void compareToWithDifferentAddresses() {
		MacAddress low = new MacAddress(0x000000000001L);
		MacAddress mid = new MacAddress(0x000000000002L);
		MacAddress high = new MacAddress(0xFFFFFFFFFFFFL);
		
		assertTrue(low.compareTo(mid) < 0);
		assertTrue(mid.compareTo(low) > 0);
		assertEquals(0, low.compareTo(low));
		assertTrue(low.compareTo(high) < 0);
		assertTrue(high.compareTo(low) > 0);
	}
	
	@Test
	void toColonStringLowercaseWithColons() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertEquals("00:1a:2b:3c:4d:5e", address.toColonString());
	}
	
	@Test
	void toDashStringUppercaseWithDashes() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertEquals("00-1A-2B-3C-4D-5E", address.toDashString());
	}
	
	@Test
	void toCiscoStringDotNotation() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertEquals("001a.2b3c.4d5e", address.toCiscoString());
	}
	
	@Test
	void toBareStringNoSeparators() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertEquals("001a2b3c4d5e", address.toBareString());
	}
	
	@Test
	void toStringMatchesColonString() {
		MacAddress address = new MacAddress(0x001A2B3C4D5EL);
		assertEquals(address.toColonString(), address.toString());
	}
	
	@Test
	void broadcastConstantIsCorrect() {
		assertEquals(0xFFFFFFFFFFFFL, MacAddress.BROADCAST.value());
		assertEquals("ff:ff:ff:ff:ff:ff", MacAddress.BROADCAST.toString());
	}
	
	@Test
	void zeroConstantIsCorrect() {
		assertEquals(0L, MacAddress.ZERO.value());
		assertEquals("00:00:00:00:00:00", MacAddress.ZERO.toString());
	}
	
	@Test
	void equalsAndHashCodeConsistency() {
		MacAddress address1 = new MacAddress(0x001A2B3C4D5EL);
		MacAddress address2 = new MacAddress(0x001A2B3C4D5EL);
		MacAddress address3 = new MacAddress(0x001A2B3C4D5FL);
		
		assertEquals(address1, address2);
		assertEquals(address1.hashCode(), address2.hashCode());
		assertNotEquals(address1, address3);
	}
	
	@Test
	void bitLengthConstant() {
		assertEquals(48, MacAddress.BIT_LENGTH);
	}
	
	@Test
	void octetCountConstant() {
		assertEquals(6, MacAddress.OCTET_COUNT);
	}
}
