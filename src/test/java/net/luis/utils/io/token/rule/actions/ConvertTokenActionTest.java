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

package net.luis.utils.io.token.rule.actions;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConvertTokenAction}.<br>
 *
 * @author Luis-St
 */
class ConvertTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullConverter() {
		assertThrows(NullPointerException.class, () -> new ConvertTokenAction(null));
	}
	
	@Test
	void constructorWithValidConverter() {
		TokenConverter converter = token -> token;
		
		assertDoesNotThrow(() -> new ConvertTokenAction(converter));
	}
	
	@Test
	void converterReturnsCorrectConverter() {
		TokenConverter converter = token -> createToken(token.value().toUpperCase());
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		assertEquals(converter, action.converter());
	}
	
	@Test
	void applyWithNullMatch() {
		ConvertTokenAction action = new ConvertTokenAction(token -> token);
		
		assertThrows(NullPointerException.class, () -> action.apply(null));
	}
	
	@Test
	void applyWithEmptyTokenList() {
		ConvertTokenAction action = new ConvertTokenAction(token -> token);
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("test")));
	}
	
	@Test
	void applyWithIdentityConverter() {
		ConvertTokenAction action = new ConvertTokenAction(token -> token);
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertSame(token1, result.getFirst());
		assertSame(token2, result.get(1));
	}
	
	@Test
	void applyWithUppercaseConverter() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken(token.value().toUpperCase()));
		Token token = createToken("hello");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("HELLO", result.getFirst().value());
		assertNotSame(token, result.getFirst());
	}
	
	@Test
	void applyWithLowercaseConverter() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken(token.value().toLowerCase()));
		Token token = createToken("WORLD");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("world", result.getFirst().value());
	}
	
	@Test
	void applyWithPrefixConverter() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken("PREFIX_" + token.value()));
		Token token1 = createToken("test");
		Token token2 = createToken("data");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals("PREFIX_test", result.get(0).value());
		assertEquals("PREFIX_data", result.get(1).value());
	}
	
	@Test
	void applyWithSuffixConverter() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken(token.value() + "_SUFFIX"));
		Token token = createToken("base");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("base_SUFFIX", result.getFirst().value());
	}
	
	@Test
	void applyWithReverseConverter() {
		ConvertTokenAction action = new ConvertTokenAction(token ->
			createToken(new StringBuilder(token.value()).reverse().toString()));
		Token token = createToken("hello");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("olleh", result.getFirst().value());
	}
	
	@Test
	void applyWithTrimConverter() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken(token.value().trim()));
		Token spacedToken = createToken("  spaced  ");
		List<Token> tokens = List.of(spacedToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("spaced", result.getFirst().value());
	}
	
	@Test
	void applyWithReplaceConverter() {
		ConvertTokenAction action = new ConvertTokenAction(token ->
			createToken(token.value().replace("old", "new")));
		Token token = createToken("oldValue");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("newValue", result.getFirst().value());
	}
	
	@Test
	void applyWithComplexConverter() {
		ConvertTokenAction action = new ConvertTokenAction(token -> {
			String value = token.value();
			if (value.matches("\\d+")) {
				return createToken("NUMBER_" + value);
			} else if (value.matches("[a-zA-Z]+")) {
				return createToken("WORD_" + value.toUpperCase());
			} else {
				return createToken("OTHER_" + value);
			}
		});
		
		Token number = createToken("123");
		Token word = createToken("hello");
		Token symbol = createToken("@#$");
		List<Token> tokens = List.of(number, word, symbol);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("NUMBER_123", result.getFirst().value());
		assertEquals("WORD_HELLO", result.get(1).value());
		assertEquals("OTHER_@#$", result.get(2).value());
	}
	
	@Test
	void applyWithSingleToken() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken("converted"));
		Token token = createToken("original");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("converted", result.getFirst().value());
	}
	
	@Test
	void applyWithMultipleTokens() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken("*" + token.value() + "*"));
		Token token1 = createToken("one");
		Token token2 = createToken("two");
		Token token3 = createToken("three");
		List<Token> tokens = List.of(token1, token2, token3);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("*one*", result.getFirst().value());
		assertEquals("*two*", result.get(1).value());
		assertEquals("*three*", result.get(2).value());
	}
	
	@Test
	void applyWithEmptyStringConverter() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken(""));
		Token token = createToken("anything");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("", result.getFirst().value());
	}
	
	@Test
	void applyWithSpecialCharacters() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken(token.value().replace("\n", "\\n")));
		Token newlineToken = createToken("line1\nline2");
		List<Token> tokens = List.of(newlineToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("line1\\nline2", result.getFirst().value());
	}
	
	@Test
	void applyWithDifferentMatchIndices() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken(token.value() + "_converted"));
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(5, 6, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("test_converted", result.getFirst().value());
	}
	
	@Test
	void applyResultIsUnmodifiable() {
		ConvertTokenAction action = new ConvertTokenAction(token -> token);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
		assertThrows(UnsupportedOperationException.class, result::removeFirst);
		assertThrows(UnsupportedOperationException.class, result::clear);
	}
	
	@Test
	void applyPreservesListSize() {
		ConvertTokenAction action = new ConvertTokenAction(token -> createToken("X"));
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		Token token3 = createToken("third");
		List<Token> tokens = List.of(token1, token2, token3);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("X", result.get(0).value());
		assertEquals("X", result.get(1).value());
		assertEquals("X", result.get(2).value());
	}
	
	@Test
	void equalActionsHaveSameHashCode() {
		TokenConverter converter = token -> createToken(token.value().toUpperCase());
		ConvertTokenAction action1 = new ConvertTokenAction(converter);
		ConvertTokenAction action2 = new ConvertTokenAction(converter);
		
		assertEquals(action1.hashCode(), action2.hashCode());
	}
	
	@Test
	void toStringContainsConverterInfo() {
		ConvertTokenAction action = new ConvertTokenAction(token -> token);
		String actionString = action.toString();
		
		assertTrue(actionString.contains("ConvertTokenAction"));
		assertTrue(actionString.contains("converter"));
	}
}
