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

package net.luis.utils.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Radix}.<br>
 *
 * @author Luis-St
 */
class RadixTest {
	
	@Test
	void getRadixReturnsCorrectValues() {
		assertEquals(2, Radix.BINARY.getRadix());
		assertEquals(8, Radix.OCTAL.getRadix());
		assertEquals(10, Radix.DECIMAL.getRadix());
		assertEquals(16, Radix.HEXADECIMAL.getRadix());
	}
	
	@Test
	void getPrefixReturnsCorrectPrefixes() {
		assertEquals("0b", Radix.BINARY.getPrefix());
		assertEquals("0", Radix.OCTAL.getPrefix());
		assertEquals("", Radix.DECIMAL.getPrefix());
		assertEquals("0x", Radix.HEXADECIMAL.getPrefix());
	}
	
	@Test
	void getPrefixReturnsNonNullValues() {
		for (Radix radix : Radix.values()) {
			assertNotNull(radix.getPrefix());
		}
	}
	
	@Test
	void getRadixReturnsPositiveValues() {
		for (Radix radix : Radix.values()) {
			assertTrue(radix.getRadix() > 0);
		}
	}
	
	@Test
	void allRadicesHaveUniqueValues() {
		Radix[] radices = Radix.values();
		for (int i = 0; i < radices.length; i++) {
			for (int j = i + 1; j < radices.length; j++) {
				assertNotEquals(radices[i].getRadix(), radices[j].getRadix(), "Radices " + radices[i] + " and " + radices[j] + " have same radix value");
			}
		}
	}
	
	@Test
	void allRadicesHaveUniquePrefixes() {
		Radix[] radices = Radix.values();
		for (int i = 0; i < radices.length; i++) {
			for (int j = i + 1; j < radices.length; j++) {
				assertNotEquals(radices[i].getPrefix(), radices[j].getPrefix(), "Radices " + radices[i] + " and " + radices[j] + " have same prefix");
			}
		}
	}
	
	@Test
	void toStringReturnsLowercaseName() {
		assertEquals("binary", Radix.BINARY.toString());
		assertEquals("octal", Radix.OCTAL.toString());
		assertEquals("decimal", Radix.DECIMAL.toString());
		assertEquals("hexadecimal", Radix.HEXADECIMAL.toString());
	}
	
	@Test
	void toStringReturnsNonEmptyStrings() {
		for (Radix radix : Radix.values()) {
			assertFalse(radix.toString().isEmpty());
		}
	}
	
	@Test
	void radixValuesAreValidNumberBases() {
		assertTrue(Radix.BINARY.getRadix() >= 2);
		assertTrue(Radix.OCTAL.getRadix() >= 2);
		assertTrue(Radix.DECIMAL.getRadix() >= 2);
		assertTrue(Radix.HEXADECIMAL.getRadix() >= 2);
		assertTrue(Radix.HEXADECIMAL.getRadix() <= 36);
	}
	
	@Test
	void prefixesAreValidFormatting() {
		assertTrue(Radix.BINARY.getPrefix().startsWith("0"));
		assertEquals("0", Radix.OCTAL.getPrefix());
		assertTrue(Radix.DECIMAL.getPrefix().isEmpty());
		assertTrue(Radix.HEXADECIMAL.getPrefix().startsWith("0"));
	}
	
	@Test
	void enumConstantsExist() {
		assertNotNull(Radix.BINARY);
		assertNotNull(Radix.OCTAL);
		assertNotNull(Radix.DECIMAL);
		assertNotNull(Radix.HEXADECIMAL);
	}
	
	@Test
	void valuesReturnsAllRadices() {
		Radix[] values = Radix.values();
		assertEquals(4, values.length);
		assertArrayEquals(new Radix[] { Radix.BINARY, Radix.OCTAL, Radix.DECIMAL, Radix.HEXADECIMAL }, values);
	}
	
	@Test
	void valueOfWorksForAllConstants() {
		assertEquals(Radix.BINARY, Radix.valueOf("BINARY"));
		assertEquals(Radix.OCTAL, Radix.valueOf("OCTAL"));
		assertEquals(Radix.DECIMAL, Radix.valueOf("DECIMAL"));
		assertEquals(Radix.HEXADECIMAL, Radix.valueOf("HEXADECIMAL"));
	}
	
	@Test
	void valueOfFailsForInvalidNames() {
		assertThrows(IllegalArgumentException.class, () -> Radix.valueOf("INVALID"));
		assertThrows(IllegalArgumentException.class, () -> Radix.valueOf("binary"));
		assertThrows(IllegalArgumentException.class, () -> Radix.valueOf(""));
		assertThrows(NullPointerException.class, () -> Radix.valueOf(null));
	}
}
