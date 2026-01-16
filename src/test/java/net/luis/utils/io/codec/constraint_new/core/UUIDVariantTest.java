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

package net.luis.utils.io.codec.constraint_new.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link UUIDVariant}.<br>
 *
 * @author Luis-St
 */
class UUIDVariantTest {
	
	@Test
	void valuesContainsAllConstants() {
		UUIDVariant[] values = UUIDVariant.values();
		assertEquals(4, values.length);
		assertEquals(UUIDVariant.NFC, values[0]);
		assertEquals(UUIDVariant.RFC_4122, values[1]);
		assertEquals(UUIDVariant.MICROSOFT, values[2]);
		assertEquals(UUIDVariant.RESERVED, values[3]);
	}
	
	@Test
	void valueOfReturnsCorrectConstant() {
		assertEquals(UUIDVariant.NFC, UUIDVariant.valueOf("NFC"));
		assertEquals(UUIDVariant.RFC_4122, UUIDVariant.valueOf("RFC_4122"));
		assertEquals(UUIDVariant.MICROSOFT, UUIDVariant.valueOf("MICROSOFT"));
		assertEquals(UUIDVariant.RESERVED, UUIDVariant.valueOf("RESERVED"));
	}
	
	@Test
	void ordinalValues() {
		assertEquals(0, UUIDVariant.NFC.ordinal());
		assertEquals(1, UUIDVariant.RFC_4122.ordinal());
		assertEquals(2, UUIDVariant.MICROSOFT.ordinal());
		assertEquals(3, UUIDVariant.RESERVED.ordinal());
	}
}
