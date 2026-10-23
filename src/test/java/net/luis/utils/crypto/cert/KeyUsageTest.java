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

package net.luis.utils.crypto.cert;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KeyUsage}.<br>
 *
 * @author Luis-St
 */
class KeyUsageTest {
	
	@Test
	void constantsDeclared() {
		List<KeyUsage> expected = List.of(
			KeyUsage.DIGITAL_SIGNATURE, KeyUsage.CONTENT_COMMITMENT, KeyUsage.KEY_ENCIPHERMENT, KeyUsage.DATA_ENCIPHERMENT, KeyUsage.KEY_AGREEMENT,
			KeyUsage.KEY_CERT_SIGN, KeyUsage.CRL_SIGN, KeyUsage.ENCIPHER_ONLY, KeyUsage.DECIPHER_ONLY
		);
		assertEquals(9, KeyUsage.values().length);
		assertEquals(expected, List.of(KeyUsage.values()));
	}
	
	@Test
	void valueOfWithNull() {
		assertThrows(NullPointerException.class, () -> KeyUsage.valueOf(null));
	}
	
	@Test
	void valueOfWithUnknownName() {
		assertThrows(IllegalArgumentException.class, () -> KeyUsage.valueOf("NON_REPUDIATION"));
		assertThrows(IllegalArgumentException.class, () -> KeyUsage.valueOf("digital_signature"));
	}
	
	@Test
	void bitOfFirstUsage() {
		assertEquals(0, KeyUsage.DIGITAL_SIGNATURE.bit());
	}
	
	@Test
	void bitOfLastUsage() {
		assertEquals(8, KeyUsage.DECIPHER_ONLY.bit());
	}
	
	@Test
	void bitOfEveryUsage() {
		assertEquals(0, KeyUsage.DIGITAL_SIGNATURE.bit());
		assertEquals(1, KeyUsage.CONTENT_COMMITMENT.bit());
		assertEquals(2, KeyUsage.KEY_ENCIPHERMENT.bit());
		assertEquals(3, KeyUsage.DATA_ENCIPHERMENT.bit());
		assertEquals(4, KeyUsage.KEY_AGREEMENT.bit());
		assertEquals(5, KeyUsage.KEY_CERT_SIGN.bit());
		assertEquals(6, KeyUsage.CRL_SIGN.bit());
		assertEquals(7, KeyUsage.ENCIPHER_ONLY.bit());
		assertEquals(8, KeyUsage.DECIPHER_ONLY.bit());
	}
	
	@Test
	void bitMatchesOrdinal() {
		for (KeyUsage usage : KeyUsage.values()) {
			assertEquals(usage.ordinal(), usage.bit(), usage.name());
		}
	}
	
	@Test
	void bitsAreUniqueAndContiguous() {
		TreeSet<Integer> bits = new TreeSet<>();
		for (KeyUsage usage : KeyUsage.values()) {
			assertTrue(bits.add(usage.bit()), usage.name());
		}
		assertEquals(9, bits.size());
		assertEquals(0, bits.first());
		assertEquals(8, bits.last());
	}
}
