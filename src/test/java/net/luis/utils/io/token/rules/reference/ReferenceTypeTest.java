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

package net.luis.utils.io.token.rules.reference;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReferenceTypeTest {
	
	@Test
	void enumValues() {
		ReferenceType[] values = ReferenceType.values();
		
		assertEquals(3, values.length);
		assertEquals(ReferenceType.RULE, values[0]);
		assertEquals(ReferenceType.TOKENS, values[1]);
		assertEquals(ReferenceType.DYNAMIC, values[2]);
	}
	
	@Test
	void enumValueOf() {
		assertEquals(ReferenceType.RULE, ReferenceType.valueOf("RULE"));
		assertEquals(ReferenceType.TOKENS, ReferenceType.valueOf("TOKENS"));
		assertEquals(ReferenceType.DYNAMIC, ReferenceType.valueOf("DYNAMIC"));
	}
	
	@Test
	void enumValueOfWithInvalidName() {
		assertThrows(IllegalArgumentException.class, () -> ReferenceType.valueOf("INVALID"));
	}
	
	@Test
	void enumValueOfWithNull() {
		assertThrows(NullPointerException.class, () -> ReferenceType.valueOf(null));
	}
	
	@Test
	void toStringTest() {
		assertEquals("RULE", ReferenceType.RULE.toString());
		assertEquals("TOKENS", ReferenceType.TOKENS.toString());
		assertEquals("DYNAMIC", ReferenceType.DYNAMIC.toString());
	}
}
