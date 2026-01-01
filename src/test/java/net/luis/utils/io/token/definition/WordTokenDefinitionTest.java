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
 * Test class for {@link WordTokenDefinition}.<br>
 *
 * @author Luis-St
 */
class WordTokenDefinitionTest {
	
	@Test
	void instanceIsSingleton() {
		assertSame(WordTokenDefinition.INSTANCE, WordTokenDefinition.INSTANCE);
	}
	
	@Test
	void matchesWithNullInput() {
		assertThrows(NullPointerException.class, () -> WordTokenDefinition.INSTANCE.matches(null));
	}
	
	@Test
	void doesNotMatchWithEmptyString() {
		assertFalse(WordTokenDefinition.INSTANCE.matches(""));
	}
	
	@Test
	void matchesWithSingleCharacter() {
		assertTrue(WordTokenDefinition.INSTANCE.matches("a"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("A"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("z"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("Z"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("0"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("9"));
	}
	
	@Test
	void matchesWithMultipleCharacters() {
		assertTrue(WordTokenDefinition.INSTANCE.matches("abc"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("ABC"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("123"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("abc123"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("123abc"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("AbC123"));
	}
	
	@Test
	void matchesWithLongWords() {
		assertTrue(WordTokenDefinition.INSTANCE.matches("hello"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("HELLO"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("Hello"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("helloWorld"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("HelloWorld123"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("a".repeat(1000)));
	}
	
	@Test
	void matchesWithNumericStrings() {
		assertTrue(WordTokenDefinition.INSTANCE.matches("123"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("0"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("999999"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("42"));
	}
	
	@Test
	void matchesWithMixedAlphanumeric() {
		assertTrue(WordTokenDefinition.INSTANCE.matches("test123"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("123test"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("a1b2c3"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("Test123ABC"));
	}
	
	@Test
	void matchesWithSpecialCharacters() {
		assertTrue(WordTokenDefinition.INSTANCE.matches("!"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("@"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("#"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("$"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("%"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("^"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("&"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("*"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("("));
		assertTrue(WordTokenDefinition.INSTANCE.matches(")"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("-"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("_"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("="));
		assertTrue(WordTokenDefinition.INSTANCE.matches("+"));
	}
	
	@Test
	void matchesWithWhitespace() {
		assertTrue(WordTokenDefinition.INSTANCE.matches(" "));
		assertTrue(WordTokenDefinition.INSTANCE.matches("\t"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("\n"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("\r"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("\f"));
	}
	
	@Test
	void matchesWithMixedValidAndInvalidCharacters() {
		assertTrue(WordTokenDefinition.INSTANCE.matches("hello world"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("test!"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("!test"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("te!st"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("123-456"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("abc_def"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("test.txt"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("hello@world"));
	}
	
	@Test
	void matchesWithPunctuation() {
		assertTrue(WordTokenDefinition.INSTANCE.matches("."));
		assertTrue(WordTokenDefinition.INSTANCE.matches(","));
		assertTrue(WordTokenDefinition.INSTANCE.matches(";"));
		assertTrue(WordTokenDefinition.INSTANCE.matches(":"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("?"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("'"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("\""));
		assertTrue(WordTokenDefinition.INSTANCE.matches("["));
		assertTrue(WordTokenDefinition.INSTANCE.matches("]"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("{"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("}"));
	}
	
	@Test
	void matchesWithEscapeSequences() {
		assertTrue(WordTokenDefinition.INSTANCE.matches("\\"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("\\n"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("\\t"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("\\\\"));
		assertTrue(WordTokenDefinition.INSTANCE.matches("test\\n"));
	}
	
	@Test
	void equalInstancesHaveSameHashCode() {
		assertEquals(WordTokenDefinition.INSTANCE.hashCode(), WordTokenDefinition.INSTANCE.hashCode());
	}
	
	@Test
	void toStringReturnsExpectedValue() {
		assertEquals("WORD", WordTokenDefinition.INSTANCE.toString());
	}
	
	@Test
	void equalsReturnsTrueForSameInstance() {
		assertTrue(WordTokenDefinition.INSTANCE.equals(WordTokenDefinition.INSTANCE));
	}
	
	@Test
	void equalsReturnsFalseForNull() {
		assertFalse(WordTokenDefinition.INSTANCE.equals(null));
	}
}
