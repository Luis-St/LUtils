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

package net.luis.utils.io.reader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringScope}.<br>
 *
 * @author Luis-St
 */
class StringScopeTest {
	
	@Test
	void constructWithValidCharacters() {
		StringScope parentheses = new StringScope('(', ')');
		assertEquals('(', parentheses.open());
		assertEquals(')', parentheses.close());
		
		StringScope letters = new StringScope('a', 'z');
		assertEquals('a', letters.open());
		assertEquals('z', letters.close());
	}
	
	@Test
	void constructWithSameCharacters() {
		assertThrows(IllegalArgumentException.class, () -> new StringScope('"', '"'));
		assertThrows(IllegalArgumentException.class, () -> new StringScope('a', 'a'));
	}
	
	@Test
	void constructWithNullCharacter() {
		assertThrows(IllegalArgumentException.class, () -> new StringScope('\0', ')'));
		assertThrows(IllegalArgumentException.class, () -> new StringScope('(', '\0'));
	}
	
	@Test
	void constantScopes() {
		assertEquals(new StringScope('(', ')'), StringScope.PARENTHESES);
		assertEquals(new StringScope('{', '}'), StringScope.CURLY_BRACKETS);
		assertEquals(new StringScope('[', ']'), StringScope.SQUARE_BRACKETS);
		assertEquals(new StringScope('<', '>'), StringScope.ANGLE_BRACKETS);
	}
	
	@Test
	void equalScopes() {
		StringScope scope = new StringScope('(', ')');
		assertEquals(StringScope.PARENTHESES, scope);
		assertEquals(StringScope.PARENTHESES.hashCode(), scope.hashCode());
	}
}
