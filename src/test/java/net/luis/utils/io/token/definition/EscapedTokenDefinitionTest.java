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

package net.luis.utils.io.token.definition;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EscapedTokenDefinition}.<br>
 *
 * @author Luis-St
 */
class EscapedTokenDefinitionTest {
	
	@Test
	void constructorWithRegularCharacter() {
		assertDoesNotThrow(() -> new EscapedTokenDefinition('a'));
		assertDoesNotThrow(() -> new EscapedTokenDefinition('Z'));
		assertDoesNotThrow(() -> new EscapedTokenDefinition('5'));
		assertDoesNotThrow(() -> new EscapedTokenDefinition('!'));
	}
	
	@Test
	void constructorWithSpecialCharacters() {
		assertDoesNotThrow(() -> new EscapedTokenDefinition('\\'));
		assertDoesNotThrow(() -> new EscapedTokenDefinition('\n'));
		assertDoesNotThrow(() -> new EscapedTokenDefinition('\t'));
		assertDoesNotThrow(() -> new EscapedTokenDefinition('"'));
		assertDoesNotThrow(() -> new EscapedTokenDefinition('\''));
	}
	
	@Test
	void tokenReturnsCorrectCharacter() {
		assertEquals('a', new EscapedTokenDefinition('a').token());
		assertEquals('Z', new EscapedTokenDefinition('Z').token());
		assertEquals('\\', new EscapedTokenDefinition('\\').token());
		assertEquals('\n', new EscapedTokenDefinition('\n').token());
		assertEquals('\t', new EscapedTokenDefinition('\t').token());
		assertEquals('"', new EscapedTokenDefinition('"').token());
	}
	
	@Test
	void matchesWithNullInput() {
		EscapedTokenDefinition definition = new EscapedTokenDefinition('a');
		
		assertThrows(NullPointerException.class, () -> definition.matches(null));
	}
	
	@Test
	void matchesWithEmptyString() {
		EscapedTokenDefinition definition = new EscapedTokenDefinition('a');
		
		assertFalse(definition.matches(""));
	}
	
	@Test
	void matchesWithSingleCharacter() {
		EscapedTokenDefinition definition = new EscapedTokenDefinition('a');
		
		assertFalse(definition.matches("a"));
		assertFalse(definition.matches("\\"));
		assertFalse(definition.matches("b"));
	}
	
	@Test
	void matchesWithCorrectEscapeSequence() {
		EscapedTokenDefinition definition = new EscapedTokenDefinition('a');
		
		assertTrue(definition.matches("\\a"));
	}
	
	@Test
	void matchesWithIncorrectEscapeSequence() {
		EscapedTokenDefinition definition = new EscapedTokenDefinition('a');
		
		assertFalse(definition.matches("\\b"));
		assertFalse(definition.matches("\\A"));
		assertFalse(definition.matches("\\1"));
		assertFalse(definition.matches("\\!"));
	}
	
	@Test
	void matchesWithReversedOrder() {
		EscapedTokenDefinition definition = new EscapedTokenDefinition('a');
		
		assertFalse(definition.matches("a\\"));
	}
	
	@Test
	void matchesWithTooLongString() {
		EscapedTokenDefinition definition = new EscapedTokenDefinition('a');
		
		assertFalse(definition.matches("\\aa"));
		assertFalse(definition.matches("\\abc"));
		assertFalse(definition.matches("a\\a"));
	}
	
	@Test
	void matchesWithTooShortString() {
		EscapedTokenDefinition definition = new EscapedTokenDefinition('a');
		
		assertFalse(definition.matches("\\"));
		assertFalse(definition.matches("a"));
	}
	
	@Test
	void matchesWithCommonEscapeSequences() {
		assertTrue(new EscapedTokenDefinition('n').matches("\\n"));
		assertTrue(new EscapedTokenDefinition('t').matches("\\t"));
		assertTrue(new EscapedTokenDefinition('r').matches("\\r"));
		assertTrue(new EscapedTokenDefinition('\\').matches("\\\\"));
		assertTrue(new EscapedTokenDefinition('"').matches("\\\""));
		assertTrue(new EscapedTokenDefinition('\'').matches("\\'"));
	}
	
	@Test
	void matchesWithNumericCharacters() {
		assertTrue(new EscapedTokenDefinition('0').matches("\\0"));
		assertTrue(new EscapedTokenDefinition('1').matches("\\1"));
		assertTrue(new EscapedTokenDefinition('9').matches("\\9"));
		
		assertFalse(new EscapedTokenDefinition('0').matches("\\1"));
		assertFalse(new EscapedTokenDefinition('1').matches("\\0"));
	}
	
	@Test
	void matchesWithSpecialCharacters() {
		assertTrue(new EscapedTokenDefinition('(').matches("\\("));
		assertTrue(new EscapedTokenDefinition(')').matches("\\)"));
		assertTrue(new EscapedTokenDefinition('[').matches("\\["));
		assertTrue(new EscapedTokenDefinition(']').matches("\\]"));
		assertTrue(new EscapedTokenDefinition('{').matches("\\{"));
		assertTrue(new EscapedTokenDefinition('}').matches("\\}"));
	}
	
	@Test
	void matchesCaseSensitive() {
		EscapedTokenDefinition lowerCase = new EscapedTokenDefinition('a');
		EscapedTokenDefinition upperCase = new EscapedTokenDefinition('A');
		
		assertTrue(lowerCase.matches("\\a"));
		assertFalse(lowerCase.matches("\\A"));
		assertTrue(upperCase.matches("\\A"));
		assertFalse(upperCase.matches("\\a"));
	}
	
	@Test
	void matchesWithWhitespaceCharacters() {
		assertTrue(new EscapedTokenDefinition(' ').matches("\\ "));
		assertTrue(new EscapedTokenDefinition('\t').matches("\\\t"));
		assertTrue(new EscapedTokenDefinition('\n').matches("\\\n"));
		assertTrue(new EscapedTokenDefinition('\r').matches("\\\r"));
	}
	
	@Test
	void equalDefinitionsHaveSameHashCode() {
		EscapedTokenDefinition def1 = new EscapedTokenDefinition('a');
		EscapedTokenDefinition def2 = new EscapedTokenDefinition('a');
		
		assertEquals(def1.hashCode(), def2.hashCode());
	}
	
	@Test
	void toStringContainsEscapeSequence() {
		EscapedTokenDefinition definition = new EscapedTokenDefinition('a');
		String toString = definition.toString();
		
		assertTrue(toString.contains("\\a"));
		assertTrue(toString.contains("CharTokenDefinition"));
	}
	
	@Test
	void toStringWithSpecialCharacters() {
		EscapedTokenDefinition tabDef = new EscapedTokenDefinition('\t');
		EscapedTokenDefinition newlineDef = new EscapedTokenDefinition('\n');
		
		String tabString = tabDef.toString();
		String newlineString = newlineDef.toString();
		
		assertTrue(tabString.contains("\\t"));
		assertTrue(newlineString.contains("\\n"));
	}
}
