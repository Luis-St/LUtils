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

package net.luis.utils.io.token.type;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CustomTokenType}.<br>
 *
 * @author Luis-St
 */
class CustomTokenTypeTest {
	
	@Test
	void constructorWithValidNameAndSuperType() {
		String name = "CustomType";
		TokenType superType = StandardTokenType.SYNTAX;
		
		CustomTokenType tokenType = new CustomTokenType(name, superType);
		
		assertEquals(name, tokenType.name());
		assertEquals(superType, tokenType.superType());
	}
	
	@Test
	void constructorWithValidNameAndNullSuperType() {
		String name = "CustomType";
		
		CustomTokenType tokenType = new CustomTokenType(name, null);
		
		assertEquals(name, tokenType.name());
		assertNull(tokenType.superType());
	}
	
	@Test
	void constructorWithNullName() {
		TokenType superType = StandardTokenType.SYNTAX;
		
		assertThrows(NullPointerException.class, () -> new CustomTokenType(null, superType));
	}
	
	@Test
	void constructorWithEmptyName() {
		TokenType superType = StandardTokenType.SYNTAX;
		
		assertThrows(IllegalArgumentException.class, () -> new CustomTokenType("", superType));
	}
	
	@Test
	void constructorWithWhitespaceName() {
		TokenType superType = StandardTokenType.SYNTAX;
		
		assertDoesNotThrow(() -> new CustomTokenType(" ", superType));
		assertDoesNotThrow(() -> new CustomTokenType("  ", superType));
	}
	
	@Test
	void getName() {
		String name = "TestType";
		CustomTokenType tokenType = new CustomTokenType(name, null);
		
		assertEquals(name, tokenType.getName());
	}
	
	@Test
	void getSuperType() {
		TokenType superType = StandardTokenType.LITERAL;
		CustomTokenType tokenType = new CustomTokenType("TestType", superType);
		
		assertEquals(superType, tokenType.getSuperType());
	}
	
	@Test
	void getSuperTypeWithNull() {
		CustomTokenType tokenType = new CustomTokenType("TestType", null);
		
		assertNull(tokenType.getSuperType());
	}
	
	@Test
	void equalsAndHashCode() {
		String name = "TestType";
		TokenType superType = StandardTokenType.SYNTAX;
		
		CustomTokenType type1 = new CustomTokenType(name, superType);
		CustomTokenType type2 = new CustomTokenType(name, superType);
		
		assertEquals(type1, type2);
		assertEquals(type1.hashCode(), type2.hashCode());
	}
	
	@Test
	void notEqualsWithDifferentName() {
		TokenType superType = StandardTokenType.SYNTAX;
		
		CustomTokenType type1 = new CustomTokenType("Type1", superType);
		CustomTokenType type2 = new CustomTokenType("Type2", superType);
		
		assertNotEquals(type1, type2);
	}
	
	@Test
	void notEqualsWithDifferentSuperType() {
		String name = "TestType";
		
		CustomTokenType type1 = new CustomTokenType(name, StandardTokenType.SYNTAX);
		CustomTokenType type2 = new CustomTokenType(name, StandardTokenType.LITERAL);
		
		assertNotEquals(type1, type2);
	}
	
	@Test
	void notEqualsWithNullSuperType() {
		String name = "TestType";
		
		CustomTokenType type1 = new CustomTokenType(name, StandardTokenType.SYNTAX);
		CustomTokenType type2 = new CustomTokenType(name, null);
		
		assertNotEquals(type1, type2);
	}
	
	@Test
	void toStringTest() {
		CustomTokenType tokenType = new CustomTokenType("TestType", StandardTokenType.SYNTAX);
		
		String result = tokenType.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("CustomTokenType"));
	}
}
