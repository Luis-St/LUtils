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

package net.luis.utils.io.token.rule.actions.core;

import net.luis.utils.io.token.definition.TokenDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenDefinitionProvider}.<br>
 *
 * @author Luis-St
 */
class TokenDefinitionProviderTest {
	
	@Test
	void constantWithNullDefinition() {
		assertThrows(NullPointerException.class, () -> TokenDefinitionProvider.constant(null));
	}
	
	@Test
	void constantWithValidDefinition() {
		TokenDefinition definition = word -> word.length() > 3;
		TokenDefinitionProvider provider = TokenDefinitionProvider.constant(definition);
		
		assertSame(definition, provider.provide("test"));
		assertSame(definition, provider.provide("hello"));
		assertSame(definition, provider.provide("world"));
	}
	
	@Test
	void constantProviderConsistency() {
		TokenDefinition definition = word -> word.startsWith("prefix");
		TokenDefinitionProvider provider = TokenDefinitionProvider.constant(definition);
		
		TokenDefinition result1 = provider.provide("value1");
		TokenDefinition result2 = provider.provide("value2");
		TokenDefinition result3 = provider.provide("differentvalue");
		
		assertSame(definition, result1);
		assertSame(definition, result2);
		assertSame(definition, result3);
	}
	
	@Test
	void acceptAllProvider() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.acceptAll();
		
		TokenDefinition def1 = provider.provide("anything");
		TokenDefinition def2 = provider.provide("123");
		TokenDefinition def3 = provider.provide("!@#$");
		TokenDefinition def4 = provider.provide("");
		
		assertTrue(def1.matches("anything"));
		assertTrue(def2.matches("123"));
		assertTrue(def3.matches("!@#$"));
		assertTrue(def4.matches(""));
	}
	
	@Test
	void acceptAllProviderWithDifferentValues() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.acceptAll();
		
		TokenDefinition numberDef = provider.provide("123");
		TokenDefinition textDef = provider.provide("hello");
		TokenDefinition symbolDef = provider.provide("@#$");
		TokenDefinition spaceDef = provider.provide(" ");
		TokenDefinition emptyDef = provider.provide("");
		
		assertTrue(numberDef.matches("123"));
		assertTrue(numberDef.matches("hello"));
		assertTrue(numberDef.matches("anything"));
		
		assertTrue(textDef.matches("hello"));
		assertTrue(textDef.matches("123"));
		assertTrue(textDef.matches("@#$"));
		
		assertTrue(symbolDef.matches("@#$"));
		assertTrue(spaceDef.matches(" "));
		assertTrue(emptyDef.matches(""));
	}
	
	@Test
	void patternBasedProviderWithNumbers() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.patternBased();
		
		TokenDefinition numberDef = provider.provide("123");
		
		assertTrue(numberDef.matches("123"));
		assertTrue(numberDef.matches("456"));
		assertTrue(numberDef.matches("0"));
		assertFalse(numberDef.matches("abc"));
		assertFalse(numberDef.matches("12a"));
		assertFalse(numberDef.matches(""));
	}
	
	@Test
	void patternBasedProviderWithLetters() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.patternBased();
		
		TokenDefinition letterDef = provider.provide("hello");
		
		assertTrue(letterDef.matches("hello"));
		assertTrue(letterDef.matches("world"));
		assertTrue(letterDef.matches("ABC"));
		assertTrue(letterDef.matches("test"));
		assertFalse(letterDef.matches("123"));
		assertFalse(letterDef.matches("hello123"));
		assertFalse(letterDef.matches("test!"));
		assertFalse(letterDef.matches(""));
	}
	
	@Test
	void patternBasedProviderWithWhitespace() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.patternBased();
		
		TokenDefinition spaceDef = provider.provide("   ");
		TokenDefinition tabDef = provider.provide("\t\t");
		TokenDefinition newlineDef = provider.provide("\n");
		
		assertTrue(spaceDef.matches("   "));
		assertTrue(spaceDef.matches(" "));
		assertTrue(spaceDef.matches("    "));
		assertFalse(spaceDef.matches("a"));
		assertFalse(spaceDef.matches("123"));
		
		assertTrue(tabDef.matches("\t"));
		assertTrue(tabDef.matches("\t\t\t"));
		
		assertTrue(newlineDef.matches("\n"));
		assertTrue(newlineDef.matches("\n\n"));
	}
	
	@Test
	void patternBasedProviderWithSpecialCharacters() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.patternBased();
		
		TokenDefinition specialDef = provider.provide("!@#$");
		
		assertTrue(specialDef.matches("!@#$"));
		assertTrue(specialDef.matches("anything"));
		assertTrue(specialDef.matches("123"));
		assertTrue(specialDef.matches("abc"));
		assertTrue(specialDef.matches(""));
	}
	
	@Test
	void patternBasedProviderWithEmptyString() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.patternBased();
		
		TokenDefinition emptyDef = provider.provide("");
		
		assertTrue(emptyDef.matches(""));
		assertTrue(emptyDef.matches("anything"));
		assertTrue(emptyDef.matches("123"));
		assertTrue(emptyDef.matches("abc"));
	}
	
	@Test
	void patternBasedProviderWithMixedContent() {
		TokenDefinitionProvider provider = TokenDefinitionProvider.patternBased();
		
		TokenDefinition mixedDef = provider.provide("abc123");
		
		assertTrue(mixedDef.matches("abc123"));
		assertTrue(mixedDef.matches("anything"));
		assertFalse(mixedDef.matches(""));
	}
	
	@Test
	void customProviderImplementation() {
		TokenDefinitionProvider custom = value -> {
			if (value.startsWith("num_")) {
				return word -> word.startsWith("num_") && word.substring(4).matches("\\d+");
			} else if (value.startsWith("str_")) {
				return word -> word.startsWith("str_") && word.substring(4).matches("[a-zA-Z]+");
			} else {
				return word -> word.equals(value);
			}
		};
		
		TokenDefinition numDef = custom.provide("num_123");
		TokenDefinition strDef = custom.provide("str_hello");
		TokenDefinition exactDef = custom.provide("exact");
		
		assertTrue(numDef.matches("num_123"));
		assertTrue(numDef.matches("num_456"));
		assertFalse(numDef.matches("num_abc"));
		assertFalse(numDef.matches("str_123"));
		
		assertTrue(strDef.matches("str_hello"));
		assertTrue(strDef.matches("str_world"));
		assertFalse(strDef.matches("str_123"));
		assertFalse(strDef.matches("num_hello"));
		
		assertTrue(exactDef.matches("exact"));
		assertFalse(exactDef.matches("other"));
		assertFalse(exactDef.matches("exact_modified"));
	}
	
	@Test
	void providerFunctionalInterfaceProperty() {
		TokenDefinitionProvider lambda = value -> word -> word.equals(value);
		
		TokenDefinition definition = lambda.provide("test");
		assertTrue(definition.matches("test"));
		assertFalse(definition.matches("other"));
	}
	
	@Test
	void providerMethodReference() {
		TokenDefinitionProvider methodRef = value -> {
			String expected = "HELPER_" + value.toUpperCase();
			return word -> word.equals(expected);
		};
		
		TokenDefinition definition = methodRef.provide("input");
		assertTrue(definition.matches("HELPER_INPUT"));
		assertFalse(definition.matches("input"));
	}
}
