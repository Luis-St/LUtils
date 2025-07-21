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

package net.luis.utils.io.reader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringScope}.<br>
 *
 * @author Luis-St
 */
public class StringScopeTest {
	
	@Test
	void constructorWithNullCharacters() {
		assertThrows(IllegalArgumentException.class, () -> new StringScope('\0', '\''));
		assertThrows(IllegalArgumentException.class, () -> new StringScope('\'', '\0'));
		assertThrows(IllegalArgumentException.class, () -> new StringScope('\0', '\0'));
	}
	
	@Test
	void constructorWithSameCharacters() {
		assertThrows(IllegalArgumentException.class, () -> new StringScope('"', '"'));
		assertThrows(IllegalArgumentException.class, () -> new StringScope('a', 'a'));
		assertThrows(IllegalArgumentException.class, () -> new StringScope('1', '1'));
	}
	
	@Test
	void constructorWithValidCharacters() {
		assertDoesNotThrow(() -> new StringScope('(', ')'));
		assertDoesNotThrow(() -> new StringScope('[', ']'));
		assertDoesNotThrow(() -> new StringScope('{', '}'));
		assertDoesNotThrow(() -> new StringScope('<', '>'));
	}
	
	@Test
	void constructorWithCustomCharacters() {
		StringScope custom1 = assertDoesNotThrow(() -> new StringScope('a', 'z'));
		assertEquals('a', custom1.open());
		assertEquals('z', custom1.close());
		
		StringScope custom2 = assertDoesNotThrow(() -> new StringScope('1', '9'));
		assertEquals('1', custom2.open());
		assertEquals('9', custom2.close());
	}
	
	@Test
	void constructorRegistersScope() {
		char open = 'X';
		char close = 'Y';
		
		assertFalse(StringScope.SCOPE_REGISTRY.containsKey(open));
		
		StringScope scope = new StringScope(open, close);
		
		assertTrue(StringScope.SCOPE_REGISTRY.containsKey(open));
		assertEquals(close, StringScope.SCOPE_REGISTRY.get(open));
	}
	
	@Test
	void predefinedScopesAreRegistered() {
		assertTrue(StringScope.SCOPE_REGISTRY.containsKey('('));
		assertEquals(')', StringScope.SCOPE_REGISTRY.get('('));
		
		assertTrue(StringScope.SCOPE_REGISTRY.containsKey('{'));
		assertEquals('}', StringScope.SCOPE_REGISTRY.get('{'));
		
		assertTrue(StringScope.SCOPE_REGISTRY.containsKey('['));
		assertEquals(']', StringScope.SCOPE_REGISTRY.get('['));
		
		assertTrue(StringScope.SCOPE_REGISTRY.containsKey('<'));
		assertEquals('>', StringScope.SCOPE_REGISTRY.get('<'));
	}
	
	@Test
	void predefinedScopesHaveCorrectValues() {
		assertEquals('(', StringScope.PARENTHESES.open());
		assertEquals(')', StringScope.PARENTHESES.close());
		
		assertEquals('{', StringScope.CURLY_BRACKETS.open());
		assertEquals('}', StringScope.CURLY_BRACKETS.close());
		
		assertEquals('[', StringScope.SQUARE_BRACKETS.open());
		assertEquals(']', StringScope.SQUARE_BRACKETS.close());
		
		assertEquals('<', StringScope.ANGLE_BRACKETS.open());
		assertEquals('>', StringScope.ANGLE_BRACKETS.close());
	}
	
	@Test
	void scopeEquality() {
		StringScope scope1 = new StringScope('A', 'B');
		StringScope scope2 = new StringScope('A', 'B');
		StringScope scope3 = new StringScope('C', 'D');
		
		assertEquals(scope1, scope2);
		assertNotEquals(scope1, scope3);
		assertNotEquals(scope2, scope3);
	}
	
	@Test
	void scopeHashCode() {
		StringScope scope1 = new StringScope('E', 'F');
		StringScope scope2 = new StringScope('E', 'F');
		
		if (scope1.equals(scope2)) {
			assertEquals(scope1.hashCode(), scope2.hashCode());
		}
	}
	
	@Test
	void scopeToString() {
		StringScope scope = new StringScope('G', 'H');
		String toString = scope.toString();
		
		assertNotNull(toString);
		assertTrue(toString.contains("G"));
		assertTrue(toString.contains("H"));
	}
}
