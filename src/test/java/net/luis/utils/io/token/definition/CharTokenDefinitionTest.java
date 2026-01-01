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
 * Test class for {@link CharTokenDefinition}.<br>
 *
 * @author Luis-St
 */
class CharTokenDefinitionTest {
	
	@Test
	void constructorWithRegularCharacter() {
		assertDoesNotThrow(() -> new CharTokenDefinition('a'));
		assertDoesNotThrow(() -> new CharTokenDefinition('Z'));
		assertDoesNotThrow(() -> new CharTokenDefinition('5'));
		assertDoesNotThrow(() -> new CharTokenDefinition('!'));
	}
	
	@Test
	void constructorWithSpecialCharacters() {
		assertDoesNotThrow(() -> new CharTokenDefinition('\\'));
		assertDoesNotThrow(() -> new CharTokenDefinition('\n'));
		assertDoesNotThrow(() -> new CharTokenDefinition('\t'));
		assertDoesNotThrow(() -> new CharTokenDefinition(' '));
		assertDoesNotThrow(() -> new CharTokenDefinition('('));
		assertDoesNotThrow(() -> new CharTokenDefinition(')'));
	}
	
	@Test
	void tokenReturnsCorrectCharacter() {
		assertEquals('a', new CharTokenDefinition('a').token());
		assertEquals('Z', new CharTokenDefinition('Z').token());
		assertEquals('5', new CharTokenDefinition('5').token());
		assertEquals('\\', new CharTokenDefinition('\\').token());
		assertEquals('\n', new CharTokenDefinition('\n').token());
		assertEquals('\t', new CharTokenDefinition('\t').token());
		assertEquals(' ', new CharTokenDefinition(' ').token());
	}
	
	@Test
	void matchesWithNullInput() {
		CharTokenDefinition definition = new CharTokenDefinition('a');
		
		assertThrows(NullPointerException.class, () -> definition.matches(null));
	}
	
	@Test
	void matchesWithEmptyString() {
		CharTokenDefinition definition = new CharTokenDefinition('a');
		
		assertFalse(definition.matches(""));
	}
	
	@Test
	void matchesWithExactCharacter() {
		CharTokenDefinition definition = new CharTokenDefinition('a');
		
		assertTrue(definition.matches("a"));
	}
	
	@Test
	void matchesWithDifferentCharacter() {
		CharTokenDefinition definition = new CharTokenDefinition('a');
		
		assertFalse(definition.matches("b"));
		assertFalse(definition.matches("A"));
		assertFalse(definition.matches("1"));
		assertFalse(definition.matches("!"));
	}
	
	@Test
	void matchesWithMultipleCharacters() {
		CharTokenDefinition definition = new CharTokenDefinition('a');
		
		assertFalse(definition.matches("aa"));
		assertFalse(definition.matches("ab"));
		assertFalse(definition.matches("abc"));
	}
	
	@Test
	void matchesWithSpecialCharacters() {
		assertTrue(new CharTokenDefinition('\\').matches("\\"));
		assertTrue(new CharTokenDefinition('\n').matches("\n"));
		assertTrue(new CharTokenDefinition('\t').matches("\t"));
		assertTrue(new CharTokenDefinition(' ').matches(" "));
		assertTrue(new CharTokenDefinition('(').matches("("));
		assertTrue(new CharTokenDefinition(')').matches(")"));
	}
	
	@Test
	void matchesCaseSensitive() {
		CharTokenDefinition lowerCase = new CharTokenDefinition('a');
		CharTokenDefinition upperCase = new CharTokenDefinition('A');
		
		assertTrue(lowerCase.matches("a"));
		assertFalse(lowerCase.matches("A"));
		assertTrue(upperCase.matches("A"));
		assertFalse(upperCase.matches("a"));
	}
	
	@Test
	void matchesWithNumericCharacters() {
		CharTokenDefinition definition = new CharTokenDefinition('5');
		
		assertTrue(definition.matches("5"));
		assertFalse(definition.matches("0"));
		assertFalse(definition.matches("9"));
		assertFalse(definition.matches("55"));
	}
	
	@Test
	void matchesWithPunctuationCharacters() {
		assertTrue(new CharTokenDefinition('!').matches("!"));
		assertTrue(new CharTokenDefinition('@').matches("@"));
		assertTrue(new CharTokenDefinition('#').matches("#"));
		assertTrue(new CharTokenDefinition('$').matches("$"));
		assertTrue(new CharTokenDefinition('%').matches("%"));
		
		assertFalse(new CharTokenDefinition('!').matches("?"));
		assertFalse(new CharTokenDefinition('@').matches("#"));
	}
	
	@Test
	void equalDefinitionsHaveSameHashCode() {
		CharTokenDefinition def1 = new CharTokenDefinition('a');
		CharTokenDefinition def2 = new CharTokenDefinition('a');
		
		assertEquals(def1.hashCode(), def2.hashCode());
	}
	
	@Test
	void toStringContainsToken() {
		CharTokenDefinition definition = new CharTokenDefinition('a');
		String toString = definition.toString();
		
		assertTrue(toString.contains("a"));
		assertTrue(toString.contains("CharTokenDefinition"));
	}
	
	@Test
	void toStringWithSpecialCharacters() {
		CharTokenDefinition tabDef = new CharTokenDefinition('\t');
		CharTokenDefinition newlineDef = new CharTokenDefinition('\n');
		
		String tabString = tabDef.toString();
		String newlineString = newlineDef.toString();
		
		assertTrue(tabString.contains("\\t"));
		assertTrue(newlineString.contains("\\n"));
	}
}
