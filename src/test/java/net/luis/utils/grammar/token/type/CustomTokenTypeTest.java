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

package net.luis.utils.grammar.token.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CustomTokenType}.<br>
 *
 * @author Luis-St
 */
class CustomTokenTypeTest {
	
	@Test
	void constructWithNameAndNullSuperType() {
		CustomTokenType type = new CustomTokenType("Custom", null);
		assertEquals("Custom", type.getName());
		assertNull(type.getSuperType());
	}
	
	@Test
	void constructWithNameAndSuperType() {
		CustomTokenType type = new CustomTokenType("Custom", StandardTokenType.KEYWORD);
		assertEquals("Custom", type.getName());
		assertEquals(StandardTokenType.KEYWORD, type.getSuperType());
	}
	
	@Test
	void constructWithNullNameThrows() {
		assertThrows(NullPointerException.class, () -> new CustomTokenType(null, null));
	}
	
	@Test
	void constructWithEmptyNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> new CustomTokenType("", null));
	}
	
	@Test
	void constructWithNonEmptyNameSucceeds() {
		CustomTokenType type = new CustomTokenType("A", null);
		assertEquals("A", type.getName());
	}
	
	@Test
	void getNameReturnsConstructorValue() {
		CustomTokenType type = new CustomTokenType("Identifier", null);
		assertEquals("Identifier", type.getName());
	}
	
	@Test
	void getSuperTypeReturnsConstructorValue() {
		CustomTokenType parent = new CustomTokenType("Parent", null);
		CustomTokenType child = new CustomTokenType("Child", parent);
		assertEquals(parent, child.getSuperType());
	}
	
	@Test
	void recordEqualsAndHashCodeConsiderNameAndSuperType() {
		CustomTokenType first = new CustomTokenType("X", StandardTokenType.KEYWORD);
		CustomTokenType second = new CustomTokenType("X", StandardTokenType.KEYWORD);
		CustomTokenType third = new CustomTokenType("Y", StandardTokenType.OPERATOR);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, third);
	}
}
