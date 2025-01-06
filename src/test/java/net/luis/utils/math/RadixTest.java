/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
	void getRadix() {
		assertEquals(2, Radix.BINARY.getRadix());
		assertEquals(8, Radix.OCTAL.getRadix());
		assertEquals(10, Radix.DECIMAL.getRadix());
		assertEquals(16, Radix.HEXADECIMAL.getRadix());
	}
	
	@Test
	void getPrefix() {
		assertEquals("0b", Radix.BINARY.getPrefix());
		assertEquals("0", Radix.OCTAL.getPrefix());
		assertEquals("", Radix.DECIMAL.getPrefix());
		assertEquals("0x", Radix.HEXADECIMAL.getPrefix());
	}
}
