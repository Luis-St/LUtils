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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Ipv4SubnetMask}.<br>
 *
 * @author Luis-St
 */
class Ipv4SubnetMaskTest {
	
	@Test
	void constructWithNonContiguousMask() {
		assertThrows(IllegalArgumentException.class, () -> new Ipv4SubnetMask(0x0F0F0F0F, false));
	}
	
	@Test
	void constructValidMask() {
		Ipv4SubnetMask mask = new Ipv4SubnetMask(0xFFFFFF00, false);
		assertEquals("255.255.255.0", mask.toString());
		assertFalse(mask.isWildcard());
	}
	
	@Test
	void constructValidWildcard() {
		Ipv4SubnetMask wildcard = new Ipv4SubnetMask(0x000000FF, true);
		assertEquals("0.0.0.255", wildcard.toString());
		assertTrue(wildcard.isWildcard());
	}
	
	@Test
	void fromPrefixLengthInvalidRange() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4SubnetMask.fromPrefixLength(-1));
		assertThrows(IllegalArgumentException.class, () -> Ipv4SubnetMask.fromPrefixLength(33));
	}
	
	@Test
	void fromPrefixLength0() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(0);
		assertEquals("0.0.0.0", mask.toString());
		assertEquals(0, mask.toPrefixLength());
	}
	
	@Test
	void fromPrefixLength8() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(8);
		assertEquals("255.0.0.0", mask.toString());
		assertEquals(8, mask.toPrefixLength());
	}
	
	@Test
	void fromPrefixLength16() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(16);
		assertEquals("255.255.0.0", mask.toString());
		assertEquals(16, mask.toPrefixLength());
	}
	
	@Test
	void fromPrefixLength24() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		assertEquals("255.255.255.0", mask.toString());
		assertEquals(24, mask.toPrefixLength());
	}
	
	@Test
	void fromPrefixLength32() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(32);
		assertEquals("255.255.255.255", mask.toString());
		assertEquals(32, mask.toPrefixLength());
	}
	
	@Test
	void parseNull() {
		assertThrows(NullPointerException.class, () -> Ipv4SubnetMask.parse(null));
	}
	
	@Test
	void parseInvalidFormat() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4SubnetMask.parse("invalid"));
		assertThrows(IllegalArgumentException.class, () -> Ipv4SubnetMask.parse("256.0.0.0"));
	}
	
	@Test
	void parseNonContiguous() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4SubnetMask.parse("255.0.255.0"));
	}
	
	@Test
	void parseValid() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.parse("255.255.255.0");
		assertEquals("255.255.255.0", mask.toString());
		assertEquals(24, mask.toPrefixLength());
	}
	
	@Test
	void tryParseNull() {
		assertThrows(NullPointerException.class, () -> Ipv4SubnetMask.tryParse(null));
	}
	
	@Test
	void tryParseInvalid() {
		assertTrue(Ipv4SubnetMask.tryParse("invalid").isEmpty());
		assertTrue(Ipv4SubnetMask.tryParse("255.0.255.0").isEmpty());
	}
	
	@Test
	void tryParseValid() {
		assertTrue(Ipv4SubnetMask.tryParse("255.255.255.0").isPresent());
		assertEquals("255.255.255.0", Ipv4SubnetMask.tryParse("255.255.255.0").get().toString());
	}
	
	@Test
	void fromWildcardStringNull() {
		assertThrows(NullPointerException.class, () -> Ipv4SubnetMask.fromWildcard(null));
	}
	
	@Test
	void fromWildcardStringInvalid() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4SubnetMask.fromWildcard("invalid"));
	}
	
	@Test
	void fromWildcardString() {
		Ipv4SubnetMask wildcard = Ipv4SubnetMask.fromWildcard("0.0.0.255");
		assertTrue(wildcard.isWildcard());
		assertEquals("0.0.0.255", wildcard.toString());
	}
	
	@Test
	void fromWildcardIntInvalid() {
		assertThrows(IllegalArgumentException.class, () -> Ipv4SubnetMask.fromWildcard(0x0F0F0F0F));
	}
	
	@Test
	void fromWildcardInt() {
		Ipv4SubnetMask wildcard = Ipv4SubnetMask.fromWildcard(0x000000FF);
		assertTrue(wildcard.isWildcard());
	}
	
	@Test
	void toPrefixLength() {
		assertEquals(0, Ipv4SubnetMask.MASK_0.toPrefixLength());
		assertEquals(8, Ipv4SubnetMask.MASK_8.toPrefixLength());
		assertEquals(16, Ipv4SubnetMask.MASK_16.toPrefixLength());
		assertEquals(24, Ipv4SubnetMask.MASK_24.toPrefixLength());
		assertEquals(32, Ipv4SubnetMask.MASK_32.toPrefixLength());
	}
	
	@Test
	void toWildcardAlreadyWildcard() {
		Ipv4SubnetMask wildcard = Ipv4SubnetMask.fromWildcard("0.0.0.255");
		assertSame(wildcard, wildcard.toWildcard());
	}
	
	@Test
	void toWildcard() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		Ipv4SubnetMask wildcard = mask.toWildcard();
		
		assertTrue(wildcard.isWildcard());
		assertEquals("0.0.0.255", wildcard.toString());
	}
	
	@Test
	void toSubnetMaskAlreadySubnetMask() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		assertSame(mask, mask.toSubnetMask());
	}
	
	@Test
	void toSubnetMask() {
		Ipv4SubnetMask wildcard = Ipv4SubnetMask.fromWildcard("0.0.0.255");
		Ipv4SubnetMask mask = wildcard.toSubnetMask();
		
		assertFalse(mask.isWildcard());
		assertEquals("255.255.255.0", mask.toString());
	}
	
	@Test
	void hostCountSlash32() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(32);
		assertEquals(1L, mask.hostCount());
	}
	
	@Test
	void hostCountSlash31() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(31);
		assertEquals(2L, mask.hostCount());
	}
	
	@Test
	void hostCountSlash24() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		assertEquals(254L, mask.hostCount());
	}
	
	@Test
	void hostCountSlash8() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(8);
		assertEquals(16777214L, mask.hostCount());
	}
	
	@Test
	void applyToNull() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		assertThrows(NullPointerException.class, () -> mask.applyTo(null));
	}
	
	@Test
	void applyTo() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 100);
		Ipv4Address network = mask.applyTo(address);
		
		assertEquals("192.168.1.0", network.toString());
	}
	
	@Test
	void applyToWithWildcard() {
		Ipv4SubnetMask wildcard = Ipv4SubnetMask.fromWildcard("0.0.0.255");
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 100);
		Ipv4Address network = wildcard.applyTo(address);
		
		assertEquals("192.168.1.0", network.toString());
	}
	
	@Test
	void getHostPartNull() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		assertThrows(NullPointerException.class, () -> mask.getHostPart(null));
	}
	
	@Test
	void getHostPart() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		Ipv4Address address = Ipv4Address.fromOctets(192, 168, 1, 100);
		Ipv4Address host = mask.getHostPart(address);
		
		assertEquals("0.0.0.100", host.toString());
	}
	
	@Test
	void toBytes() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		byte[] bytes = mask.toBytes();
		
		assertEquals(4, bytes.length);
		assertEquals((byte) 255, bytes[0]);
		assertEquals((byte) 255, bytes[1]);
		assertEquals((byte) 255, bytes[2]);
		assertEquals(0, bytes[3]);
	}
	
	@Test
	void toDottedDecimal() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		assertEquals("255.255.255.0", mask.toDottedDecimal());
	}
	
	@Test
	void getOctetInvalidIndex() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		assertThrows(IndexOutOfBoundsException.class, () -> mask.getOctet(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> mask.getOctet(4));
	}
	
	@Test
	void getOctet() {
		Ipv4SubnetMask mask = Ipv4SubnetMask.fromPrefixLength(24);
		assertEquals(255, mask.getOctet(0));
		assertEquals(255, mask.getOctet(1));
		assertEquals(255, mask.getOctet(2));
		assertEquals(0, mask.getOctet(3));
	}
	
	@Test
	void toStringFormat() {
		assertEquals("255.255.255.0", Ipv4SubnetMask.fromPrefixLength(24).toString());
	}
	
	@Test
	void constantsAreDefined() {
		assertNotNull(Ipv4SubnetMask.MASK_0);
		assertNotNull(Ipv4SubnetMask.MASK_8);
		assertNotNull(Ipv4SubnetMask.MASK_16);
		assertNotNull(Ipv4SubnetMask.MASK_24);
		assertNotNull(Ipv4SubnetMask.MASK_32);
	}
	
	@Test
	void constantsHaveCorrectValues() {
		assertEquals("0.0.0.0", Ipv4SubnetMask.MASK_0.toString());
		assertEquals("255.0.0.0", Ipv4SubnetMask.MASK_8.toString());
		assertEquals("255.255.0.0", Ipv4SubnetMask.MASK_16.toString());
		assertEquals("255.255.255.0", Ipv4SubnetMask.MASK_24.toString());
		assertEquals("255.255.255.255", Ipv4SubnetMask.MASK_32.toString());
	}
	
	@Test
	void recordEquals() {
		Ipv4SubnetMask mask1 = Ipv4SubnetMask.fromPrefixLength(24);
		Ipv4SubnetMask mask2 = Ipv4SubnetMask.fromPrefixLength(24);
		Ipv4SubnetMask mask3 = Ipv4SubnetMask.fromPrefixLength(16);
		
		assertEquals(mask1, mask2);
		assertNotEquals(mask1, mask3);
	}
	
	@Test
	void recordHashCode() {
		Ipv4SubnetMask mask1 = Ipv4SubnetMask.fromPrefixLength(24);
		Ipv4SubnetMask mask2 = Ipv4SubnetMask.fromPrefixLength(24);
		
		assertEquals(mask1.hashCode(), mask2.hashCode());
	}
}
