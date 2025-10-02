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

package net.luis.utils.io.token.definition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenDefinition}.<br>
 *
 * @author Luis-St
 */
class TokenDefinitionTest {
	
	@Test
	void ofCharacterCreatesCharTokenDefinition() {
		TokenDefinition definition = TokenDefinition.of('a');
		
		assertInstanceOf(CharTokenDefinition.class, definition);
		assertEquals('a', ((CharTokenDefinition) definition).token());
	}
	
	@Test
	void ofCharacterWithSpecialCharacters() {
		assertInstanceOf(CharTokenDefinition.class, TokenDefinition.of('\\'));
		assertInstanceOf(CharTokenDefinition.class, TokenDefinition.of('\n'));
		assertInstanceOf(CharTokenDefinition.class, TokenDefinition.of('\t'));
		assertInstanceOf(CharTokenDefinition.class, TokenDefinition.of(' '));
	}
	
	@Test
	void ofStringWithNullToken() {
		assertThrows(NullPointerException.class, () -> TokenDefinition.of(null, false));
		assertThrows(NullPointerException.class, () -> TokenDefinition.of(null, true));
	}
	
	@Test
	void ofStringWithEmptyToken() {
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.of("", false));
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.of("", true));
	}
	
	@Test
	void ofStringCreatesStringTokenDefinition() {
		TokenDefinition definition = TokenDefinition.of("hello", false);
		
		assertInstanceOf(StringTokenDefinition.class, definition);
		assertEquals("hello", ((StringTokenDefinition) definition).token());
		assertFalse(((StringTokenDefinition) definition).equalsIgnoreCase());
	}
	
	@Test
	void ofStringWithCaseInsensitive() {
		TokenDefinition definition = TokenDefinition.of("hello", true);
		
		assertInstanceOf(StringTokenDefinition.class, definition);
		assertEquals("hello", ((StringTokenDefinition) definition).token());
		assertTrue(((StringTokenDefinition) definition).equalsIgnoreCase());
	}
	
	@Test
	void ofStringWithSpecialContent() {
		assertInstanceOf(StringTokenDefinition.class, TokenDefinition.of(" ", false));
		assertInstanceOf(StringTokenDefinition.class, TokenDefinition.of("\t\n", false));
		assertInstanceOf(StringTokenDefinition.class, TokenDefinition.of("!@#$", false));
	}
	
	@Test
	void ofEscapedCreatesEscapedTokenDefinition() {
		TokenDefinition definition = TokenDefinition.ofEscaped('a');
		
		assertInstanceOf(EscapedTokenDefinition.class, definition);
		assertEquals('a', ((EscapedTokenDefinition) definition).token());
	}
	
	@Test
	void ofEscapedWithSpecialCharacters() {
		assertInstanceOf(EscapedTokenDefinition.class, TokenDefinition.ofEscaped('\\'));
		assertInstanceOf(EscapedTokenDefinition.class, TokenDefinition.ofEscaped('\n'));
		assertInstanceOf(EscapedTokenDefinition.class, TokenDefinition.ofEscaped('\t'));
		assertInstanceOf(EscapedTokenDefinition.class, TokenDefinition.ofEscaped('"'));
	}
	
	@Test
	void customDefinitionMatches() {
		TokenDefinition customDefinition = word -> word.startsWith("test");
		
		assertTrue(customDefinition.matches("test"));
		assertTrue(customDefinition.matches("testing"));
		assertFalse(customDefinition.matches("abc"));
		assertFalse(customDefinition.matches(""));
	}
}
