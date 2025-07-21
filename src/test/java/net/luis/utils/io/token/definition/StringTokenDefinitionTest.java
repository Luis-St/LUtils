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
 * Test class for {@link StringTokenDefinition}.<br>
 *
 * @author Luis-St
 */
class StringTokenDefinitionTest {
	
	@Test
	void constructorWithNullToken() {
		assertThrows(NullPointerException.class, () -> new StringTokenDefinition(null, false));
		assertThrows(NullPointerException.class, () -> new StringTokenDefinition(null, true));
	}
	
	@Test
	void constructorWithEmptyToken() {
		assertThrows(IllegalArgumentException.class, () -> new StringTokenDefinition("", false));
		assertThrows(IllegalArgumentException.class, () -> new StringTokenDefinition("", true));
	}
	
	@Test
	void constructorWithValidTokens() {
		assertDoesNotThrow(() -> new StringTokenDefinition("a", false));
		assertDoesNotThrow(() -> new StringTokenDefinition("a", true));
		assertDoesNotThrow(() -> new StringTokenDefinition(" ", false));
		assertDoesNotThrow(() -> new StringTokenDefinition("hello", false));
		assertDoesNotThrow(() -> new StringTokenDefinition("HELLO", true));
		assertDoesNotThrow(() -> new StringTokenDefinition("123", false));
		assertDoesNotThrow(() -> new StringTokenDefinition("!@#$", false));
	}
	
	@Test
	void tokenReturnsCorrectValue() {
		assertEquals("a", new StringTokenDefinition("a", false).token());
		assertEquals("hello", new StringTokenDefinition("hello", false).token());
		assertEquals("HELLO", new StringTokenDefinition("HELLO", true).token());
		assertEquals(" ", new StringTokenDefinition(" ", false).token());
		assertEquals("123", new StringTokenDefinition("123", false).token());
		assertEquals("!@#", new StringTokenDefinition("!@#", false).token());
	}
	
	@Test
	void equalsIgnoreCaseReturnsCorrectValue() {
		assertFalse(new StringTokenDefinition("a", false).equalsIgnoreCase());
		assertTrue(new StringTokenDefinition("a", true).equalsIgnoreCase());
		assertFalse(new StringTokenDefinition("hello", false).equalsIgnoreCase());
		assertTrue(new StringTokenDefinition("hello", true).equalsIgnoreCase());
	}
	
	@Test
	void matchesWithNullInput() {
		StringTokenDefinition definition = new StringTokenDefinition("a", false);
		
		assertThrows(NullPointerException.class, () -> definition.matches(null));
	}
	
	@Test
	void matchesCaseSensitiveWithEmptyString() {
		StringTokenDefinition definition = new StringTokenDefinition("a", false);
		
		assertFalse(definition.matches(""));
	}
	
	@Test
	void matchesCaseSensitiveWithExactMatch() {
		StringTokenDefinition definition = new StringTokenDefinition("hello", false);
		
		assertTrue(definition.matches("hello"));
	}
	
	@Test
	void matchesCaseSensitiveWithWrongCase() {
		StringTokenDefinition definition = new StringTokenDefinition("hello", false);
		
		assertFalse(definition.matches("HELLO"));
		assertFalse(definition.matches("Hello"));
		assertFalse(definition.matches("HeLLo"));
	}
	
	@Test
	void matchesCaseSensitiveWithDifferentString() {
		StringTokenDefinition definition = new StringTokenDefinition("hello", false);
		
		assertFalse(definition.matches("world"));
		assertFalse(definition.matches("hell"));
		assertFalse(definition.matches("helloo"));
		assertFalse(definition.matches("ahello"));
	}
	
	@Test
	void matchesCaseInsensitiveWithEmptyString() {
		StringTokenDefinition definition = new StringTokenDefinition("a", true);
		
		assertFalse(definition.matches(""));
	}
	
	@Test
	void matchesCaseInsensitiveWithExactMatch() {
		StringTokenDefinition definition = new StringTokenDefinition("hello", true);
		
		assertTrue(definition.matches("hello"));
	}
	
	@Test
	void matchesCaseInsensitiveWithDifferentCase() {
		StringTokenDefinition definition = new StringTokenDefinition("hello", true);
		
		assertTrue(definition.matches("HELLO"));
		assertTrue(definition.matches("Hello"));
		assertTrue(definition.matches("HeLLo"));
		assertTrue(definition.matches("hELLO"));
	}
	
	@Test
	void matchesCaseInsensitiveWithDifferentString() {
		StringTokenDefinition definition = new StringTokenDefinition("hello", true);
		
		assertFalse(definition.matches("world"));
		assertFalse(definition.matches("WORLD"));
		assertFalse(definition.matches("hell"));
		assertFalse(definition.matches("HELL"));
		assertFalse(definition.matches("helloo"));
		assertFalse(definition.matches("HELLOO"));
	}
	
	@Test
	void matchesWithSingleCharacter() {
		StringTokenDefinition caseSensitive = new StringTokenDefinition("a", false);
		StringTokenDefinition caseInsensitive = new StringTokenDefinition("a", true);
		
		assertTrue(caseSensitive.matches("a"));
		assertFalse(caseSensitive.matches("A"));
		
		assertTrue(caseInsensitive.matches("a"));
		assertTrue(caseInsensitive.matches("A"));
	}
	
	@Test
	void matchesWithNumericStrings() {
		StringTokenDefinition definition = new StringTokenDefinition("123", false);
		
		assertTrue(definition.matches("123"));
		assertFalse(definition.matches("124"));
		assertFalse(definition.matches("12"));
		assertFalse(definition.matches("1234"));
	}
	
	@Test
	void matchesWithSpecialCharacters() {
		StringTokenDefinition definition = new StringTokenDefinition("!@#", false);
		
		assertTrue(definition.matches("!@#"));
		assertFalse(definition.matches("!@$"));
		assertFalse(definition.matches("!@"));
		assertFalse(definition.matches("!@##"));
	}
	
	@Test
	void matchesWithWhitespace() {
		StringTokenDefinition spaceDefinition = new StringTokenDefinition(" ", false);
		StringTokenDefinition tabDefinition = new StringTokenDefinition("\t", false);
		StringTokenDefinition newlineDefinition = new StringTokenDefinition("\n", false);
		
		assertTrue(spaceDefinition.matches(" "));
		assertTrue(tabDefinition.matches("\t"));
		assertTrue(newlineDefinition.matches("\n"));
		
		assertFalse(spaceDefinition.matches("\t"));
		assertFalse(tabDefinition.matches(" "));
	}
	
	@Test
	void matchesWithMixedContent() {
		StringTokenDefinition definition = new StringTokenDefinition("Hello123!", false);
		
		assertTrue(definition.matches("Hello123!"));
		assertFalse(definition.matches("hello123!"));
		assertFalse(definition.matches("Hello124!"));
		assertFalse(definition.matches("Hello123"));
	}
	
	@Test
	void matchesCaseInsensitiveWithMixedContent() {
		StringTokenDefinition definition = new StringTokenDefinition("Hello123!", true);
		
		assertTrue(definition.matches("Hello123!"));
		assertTrue(definition.matches("hello123!"));
		assertTrue(definition.matches("HELLO123!"));
		assertTrue(definition.matches("HeLLo123!"));
		assertFalse(definition.matches("Hello124!"));
		assertFalse(definition.matches("Hello123"));
	}
	
	@Test
	void equalDefinitionsHaveSameHashCode() {
		StringTokenDefinition def1 = new StringTokenDefinition("hello", false);
		StringTokenDefinition def2 = new StringTokenDefinition("hello", false);
		
		assertEquals(def1.hashCode(), def2.hashCode());
		
		StringTokenDefinition def3 = new StringTokenDefinition("hello", true);
		StringTokenDefinition def4 = new StringTokenDefinition("hello", true);
		
		assertEquals(def3.hashCode(), def4.hashCode());
	}
	
	@Test
	void toStringContainsTokenAndFlag() {
		StringTokenDefinition caseSensitive = new StringTokenDefinition("hello", false);
		StringTokenDefinition caseInsensitive = new StringTokenDefinition("hello", true);
		
		String sensitiveString = caseSensitive.toString();
		String insensitiveString = caseInsensitive.toString();
		
		assertTrue(sensitiveString.contains("hello"));
		assertTrue(sensitiveString.contains("false"));
		assertTrue(insensitiveString.contains("hello"));
		assertTrue(insensitiveString.contains("true"));
	}
	
	@Test
	void toStringWithSpecialCharacters() {
		StringTokenDefinition tabDef = new StringTokenDefinition("\t", false);
		StringTokenDefinition newlineDef = new StringTokenDefinition("\n", false);
		
		String tabString = tabDef.toString();
		String newlineString = newlineDef.toString();
		
		assertTrue(tabString.contains("\\t"));
		assertTrue(newlineString.contains("\\n"));
	}
}
