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

import net.luis.utils.io.network.address.exception.IpParseException;
import net.luis.utils.io.network.address.ipv6.Ipv6Address;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MacAddresses}.<br>
 *
 * @author Luis-St
 */
class MacAddressesTest {
	
	@Test
	void parseNullThrows() {
		assertThrows(NullPointerException.class, () -> MacAddresses.parse(null));
	}
	
	@Test
	void parseEmptyThrows() {
		assertThrows(IpParseException.class, () -> MacAddresses.parse(""));
	}
	
	@Test
	void parseColonFormat() {
		MacAddress address = MacAddresses.parse("00:1A:2B:3C:4D:5E");
		assertEquals(0x001A2B3C4D5EL, address.value());
	}
	
	@Test
	void parseDashFormat() {
		MacAddress address = MacAddresses.parse("00-1A-2B-3C-4D-5E");
		assertEquals(0x001A2B3C4D5EL, address.value());
	}
	
	@Test
	void parseCiscoFormat() {
		MacAddress address = MacAddresses.parse("001A.2B3C.4D5E");
		assertEquals(0x001A2B3C4D5EL, address.value());
	}
	
	@Test
	void parseBareFormat() {
		MacAddress address = MacAddresses.parse("001A2B3C4D5E");
		assertEquals(0x001A2B3C4D5EL, address.value());
	}
	
	@Test
	void parseLowercaseHex() {
		MacAddress upper = MacAddresses.parse("00:1A:2B:3C:4D:5E");
		MacAddress lower = MacAddresses.parse("00:1a:2b:3c:4d:5e");
		assertEquals(upper, lower);
	}
	
	@Test
	void parseInvalidFormatThrows() {
		assertThrows(IpParseException.class, () -> MacAddresses.parse("invalid"));
		assertThrows(IpParseException.class, () -> MacAddresses.parse("00:1A:2B:3C:4D"));
		assertThrows(IpParseException.class, () -> MacAddresses.parse("00:1A:2B:3C:4D:5E:6F"));
		assertThrows(IpParseException.class, () -> MacAddresses.parse("GG:1A:2B:3C:4D:5E"));
	}
	
	@Test
	void tryParseNullReturnsEmpty() {
		Optional<MacAddress> result = MacAddresses.tryParse(null);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void tryParseEmptyReturnsEmpty() {
		Optional<MacAddress> result = MacAddresses.tryParse("");
		assertTrue(result.isEmpty());
	}
	
	@Test
	void tryParseValidReturnsPresent() {
		Optional<MacAddress> result = MacAddresses.tryParse("00:1A:2B:3C:4D:5E");
		assertTrue(result.isPresent());
		assertEquals(0x001A2B3C4D5EL, result.get().value());
	}
	
	@Test
	void tryParseInvalidReturnsEmpty() {
		assertTrue(MacAddresses.tryParse("invalid").isEmpty());
		assertTrue(MacAddresses.tryParse("00:1A:2B").isEmpty());
		assertTrue(MacAddresses.tryParse("GG:HH:II:JJ:KK:LL").isEmpty());
	}
	
	@Test
	void isValidNullReturnsFalse() {
		assertFalse(MacAddresses.isValid(null));
	}
	
	@Test
	void isValidEmptyReturnsFalse() {
		assertFalse(MacAddresses.isValid(""));
	}
	
	@Test
	void isValidWithValidFormats() {
		assertTrue(MacAddresses.isValid("00:1A:2B:3C:4D:5E"));
		assertTrue(MacAddresses.isValid("00-1A-2B-3C-4D-5E"));
		assertTrue(MacAddresses.isValid("001A.2B3C.4D5E"));
		assertTrue(MacAddresses.isValid("001A2B3C4D5E"));
		assertTrue(MacAddresses.isValid("00:1a:2b:3c:4d:5e"));
	}
	
	@Test
	void isValidWithInvalidFormat() {
		assertFalse(MacAddresses.isValid("invalid"));
		assertFalse(MacAddresses.isValid("00:1A:2B"));
		assertFalse(MacAddresses.isValid("00:1A:2B:3C:4D:5E:6F"));
		assertFalse(MacAddresses.isValid("GG:1A:2B:3C:4D:5E"));
	}
	
	@Test
	void ofBytesNullThrows() {
		assertThrows(NullPointerException.class, () -> MacAddresses.of((byte[]) null));
	}
	
	@Test
	void ofBytesInvalidLengthThrows() {
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(new byte[] { 1, 2, 3 }));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(new byte[] { 1, 2, 3, 4, 5, 6, 7 }));
	}
	
	@Test
	void ofBytesValidArray() {
		MacAddress address = MacAddresses.of(new byte[] { 0x00, 0x1A, 0x2B, 0x3C, 0x4D, 0x5E });
		assertEquals(0x001A2B3C4D5EL, address.value());
	}
	
	@Test
	void ofLongValue() {
		MacAddress address = MacAddresses.of(0x001A2B3C4D5EL);
		assertEquals(0x001A2B3C4D5EL, address.value());
	}
	
	@Test
	void ofLongValueMasksHighBits() {
		MacAddress address = MacAddresses.of(0xFFFF_001A2B3C4D5EL);
		assertEquals(0x001A2B3C4D5EL, address.value());
	}
	
	@Test
	void ofOctetsValid() {
		MacAddress address = MacAddresses.of(0x00, 0x1A, 0x2B, 0x3C, 0x4D, 0x5E);
		assertEquals(0x001A2B3C4D5EL, address.value());
	}
	
	@Test
	void ofOctetsInvalidRange() {
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(-1, 0, 0, 0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(256, 0, 0, 0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(0, -1, 0, 0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(0, 256, 0, 0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(0, 0, -1, 0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(0, 0, 256, 0, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(0, 0, 0, -1, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(0, 0, 0, 256, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(0, 0, 0, 0, -1, 0));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(0, 0, 0, 0, 256, 0));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(0, 0, 0, 0, 0, -1));
		assertThrows(IllegalArgumentException.class, () -> MacAddresses.of(0, 0, 0, 0, 0, 256));
	}
	
	@Test
	void broadcastReturnsCorrectAddress() {
		MacAddress broadcast = MacAddresses.broadcast();
		assertEquals(0xFFFFFFFFFFFFL, broadcast.value());
		assertTrue(broadcast.isBroadcast());
	}
	
	@Test
	void zeroReturnsCorrectAddress() {
		MacAddress zero = MacAddresses.zero();
		assertEquals(0L, zero.value());
	}
	
	@Test
	void fromEui64NullThrows() {
		assertThrows(NullPointerException.class, () -> MacAddresses.fromEui64(null));
	}
	
	@Test
	void fromEui64WithValidEui64() {
		MacAddress originalMac = new MacAddress(0x001A2B3C4D5EL);
		Ipv6Address linkLocal = originalMac.toLinkLocalIpv6();
		
		Optional<MacAddress> extracted = MacAddresses.fromEui64(linkLocal);
		assertTrue(extracted.isPresent());
		assertEquals(originalMac, extracted.get());
	}
	
	@Test
	void fromEui64WithoutEui64ReturnsEmpty() {
		Ipv6Address nonEui64 = new Ipv6Address(0x2001_0DB8_0000_0000L, 0x0000_0000_0000_0001L);
		Optional<MacAddress> result = MacAddresses.fromEui64(nonEui64);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void randomIsLocallyAdministered() {
		MacAddress random = MacAddresses.random();
		assertTrue(random.isLocal());
	}
	
	@Test
	void randomIsUnicast() {
		MacAddress random = MacAddresses.random();
		assertTrue(random.isUnicast());
	}
	
	@Test
	void randomGeneratesUniqueAddresses() {
		Set<MacAddress> addresses = new HashSet<>();
		for (int i = 0; i < 100; i++) {
			addresses.add(MacAddresses.random());
		}
		assertEquals(100, addresses.size());
	}
	
	@Test
	void randomMulticastIsLocallyAdministered() {
		MacAddress random = MacAddresses.randomMulticast();
		assertTrue(random.isLocal());
	}
	
	@Test
	void randomMulticastIsMulticast() {
		MacAddress random = MacAddresses.randomMulticast();
		assertTrue(random.isMulticast());
	}
}
