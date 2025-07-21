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

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenDefinition}.<br>
 *
 * @author Luis-St
 */
class TokenDefinitionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		TokenDefinition anyDefinition = word -> word.equals(value);
		return createToken(anyDefinition, value);
	}
	
	private static @NotNull Token createToken(@NotNull TokenDefinition definition, @NotNull String value) {
		return SimpleToken.createUnpositioned(definition, value);
	}
	
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
	void combineWithNullArray() {
		assertThrows(NullPointerException.class, () -> TokenDefinition.combine((TokenDefinition[]) null));
	}
	
	@Test
	void combineWithEmptyArray() {
		assertThrows(IllegalArgumentException.class, () -> TokenDefinition.combine(new TokenDefinition[0]));
	}
	
	@Test
	void combineWithSingleDefinition() {
		TokenDefinition charDefinition = TokenDefinition.of('a');
		TokenDefinition result = TokenDefinition.combine(charDefinition);
		
		assertSame(charDefinition, result);
	}
	
	@Test
	void combineCharTokenDefinitions() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.of('a'),
			TokenDefinition.of('b'),
			TokenDefinition.of('c')
		);
		
		assertInstanceOf(StringTokenDefinition.class, combined);
		assertTrue(combined.matches("abc"));
		assertEquals("abc", ((StringTokenDefinition) combined).token());
		assertFalse(((StringTokenDefinition) combined).equalsIgnoreCase());
	}
	
	@Test
	void combineStringTokenDefinitions() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.of("hello", false),
			TokenDefinition.of("world", false)
		);
		
		assertInstanceOf(StringTokenDefinition.class, combined);
		assertTrue(combined.matches("helloworld"));
		assertEquals("helloworld", ((StringTokenDefinition) combined).token());
		assertFalse(((StringTokenDefinition) combined).equalsIgnoreCase());
	}
	
	@Test
	void combineEscapedTokenDefinitions() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.ofEscaped('\\'),
			TokenDefinition.of('n')
		);
		
		assertInstanceOf(StringTokenDefinition.class, combined);
		assertTrue(combined.matches("\\\\n"));
		assertEquals("\\\\n", ((StringTokenDefinition) combined).token());
	}
	
	@Test
	void combineMixedTokenDefinitions() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.of("hello", false),
			TokenDefinition.of('-'),
			TokenDefinition.ofEscaped('!')
		);
		
		assertInstanceOf(StringTokenDefinition.class, combined);
		assertTrue(combined.matches("hello-\\!"));
		assertEquals("hello-\\!", ((StringTokenDefinition) combined).token());
	}
	
	@Test
	void combinePreservesCase() {
		TokenDefinition combined = TokenDefinition.combine(
			TokenDefinition.of("HELLO", false),
			TokenDefinition.of("world", false)
		);
		
		assertTrue(combined.matches("HELLOworld"));
		assertFalse(combined.matches("helloworld"));
		assertFalse(combined.matches("HELLOWorld"));
	}
	
	@Test
	void combineWithUnsupportedDefinition() {
		TokenDefinition customDefinition = word -> word.equals("custom");
		
		assertSame(customDefinition, TokenDefinition.combine(customDefinition));
		assertThrows(IllegalArgumentException.class, () ->
			TokenDefinition.combine(TokenDefinition.of('a'), customDefinition));
	}
	
	@Test
	void matchWithNullTokenList() {
		TokenDefinition definition = TokenDefinition.of('a');
		
		assertThrows(NullPointerException.class, () -> definition.match(null, 0));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		TokenDefinition definition = TokenDefinition.of('a');
		List<Token> tokens = List.of(createToken("a"));
		
		assertNull(definition.match(tokens, 1));
		assertNull(definition.match(tokens, 5));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		TokenDefinition definition = TokenDefinition.of('a');
		
		assertNull(definition.match(Collections.emptyList(), 0));
	}
	
	@Test
	void matchWithNonMatchingToken() {
		TokenDefinition definition = TokenDefinition.of('a');
		List<Token> tokens = List.of(createToken("b"), createToken("c"));
		
		assertNull(definition.match(tokens, 0));
		assertNull(definition.match(tokens, 1));
	}
	
	@Test
	void matchWithMatchingToken() {
		TokenDefinition definition = TokenDefinition.of('a');
		Token matchingToken = createToken(definition, "a");
		List<Token> tokens = List.of(matchingToken, createToken("b"));
		
		TokenRuleMatch match = definition.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(matchingToken, match.matchedTokens().getFirst());
		assertEquals(definition, match.matchingTokenRule());
	}
	
	@Test
	void matchWithMatchingTokenAtDifferentIndex() {
		TokenDefinition definition = TokenDefinition.of('b');
		Token nonMatchingToken = createToken("a");
		Token matchingToken = createToken(definition, "b");
		List<Token> tokens = List.of(nonMatchingToken, matchingToken, createToken("c"));
		
		TokenRuleMatch match = definition.match(tokens, 1);
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(matchingToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithStringDefinition() {
		TokenDefinition definition = TokenDefinition.of("hello", false);
		Token matchingToken = createToken(definition, "hello");
		List<Token> tokens = List.of(matchingToken);
		
		TokenRuleMatch match = definition.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(matchingToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithCaseInsensitiveDefinition() {
		TokenDefinition definition = TokenDefinition.of("hello", true);
		Token matchingToken = createToken(definition, "HELLO");
		List<Token> tokens = List.of(matchingToken);
		
		TokenRuleMatch match = definition.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(matchingToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithEscapedDefinition() {
		TokenDefinition definition = TokenDefinition.ofEscaped('n');
		Token matchingToken = createToken(definition, "\\n");
		List<Token> tokens = List.of(matchingToken);
		
		TokenRuleMatch match = definition.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(matchingToken, match.matchedTokens().getFirst());
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
