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

package net.luis.utils.io.token.rule.actions.transformers;

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
 * Test class for {@link WrapTokenAction}.<br>
 *
 * @author Luis-St
 */
class WrapTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullPrefixToken() {
		Token suffix = createToken(")");
		
		assertThrows(NullPointerException.class, () -> new WrapTokenAction(null, suffix));
	}
	
	@Test
	void constructorWithNullSuffixToken() {
		Token prefix = createToken("(");
		
		assertThrows(NullPointerException.class, () -> new WrapTokenAction(prefix, null));
	}
	
	@Test
	void constructorWithValidTokens() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		
		assertDoesNotThrow(() -> new WrapTokenAction(prefix, suffix));
	}
	
	@Test
	void prefixTokenReturnsCorrectValue() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		assertEquals(prefix, action.prefixToken());
	}
	
	@Test
	void suffixTokenReturnsCorrectValue() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		assertEquals(suffix, action.suffixToken());
	}
	
	@Test
	void applyWithNullMatch() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		
		assertThrows(NullPointerException.class, () -> action.apply(null));
	}
	
	@Test
	void applyWithEmptyTokenList() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithSingleToken() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		Token content = createToken("content");
		List<Token> tokens = List.of(content);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals(prefix, result.get(0));
		assertEquals(content, result.get(1));
		assertEquals(suffix, result.get(2));
		assertEquals("(", result.get(0).value());
		assertEquals("content", result.get(1).value());
		assertEquals(")", result.get(2).value());
	}
	
	@Test
	void applyWithMultipleTokens() {
		Token prefix = createToken("[");
		Token suffix = createToken("]");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		Token token1 = createToken("hello");
		Token token2 = createToken("world");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(4, result.size());
		assertEquals(prefix, result.get(0));
		assertEquals(token1, result.get(1));
		assertEquals(token2, result.get(2));
		assertEquals(suffix, result.get(3));
		assertEquals("[", result.get(0).value());
		assertEquals("hello", result.get(1).value());
		assertEquals("world", result.get(2).value());
		assertEquals("]", result.get(3).value());
	}
	
	@Test
	void applyWithDifferentWrapperTokens() {
		Token prefix = createToken("START");
		Token suffix = createToken("END");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		Token content = createToken("middle");
		List<Token> tokens = List.of(content);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("START", result.get(0).value());
		assertEquals("middle", result.get(1).value());
		assertEquals("END", result.get(2).value());
	}
	
	@Test
	void applyWithSamePrefixAndSuffix() {
		Token wrapper = createToken("\"");
		WrapTokenAction action = new WrapTokenAction(wrapper, wrapper);
		Token content = createToken("quoted");
		List<Token> tokens = List.of(content);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("\"", result.get(0).value());
		assertEquals("quoted", result.get(1).value());
		assertEquals("\"", result.get(2).value());
		assertSame(wrapper, result.get(0));
		assertSame(wrapper, result.get(2));
	}
	
	@Test
	void applyWithNumericTokens() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		Token num1 = createToken("1");
		Token num2 = createToken("2");
		Token num3 = createToken("3");
		List<Token> tokens = List.of(num1, num2, num3);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(5, result.size());
		assertEquals("(", result.get(0).value());
		assertEquals("1", result.get(1).value());
		assertEquals("2", result.get(2).value());
		assertEquals("3", result.get(3).value());
		assertEquals(")", result.get(4).value());
	}
	
	@Test
	void applyWithSpecialCharacterTokens() {
		Token prefix = createToken("<");
		Token suffix = createToken(">");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		Token special1 = createToken("@");
		Token special2 = createToken("#");
		List<Token> tokens = List.of(special1, special2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(4, result.size());
		assertEquals("<", result.get(0).value());
		assertEquals("@", result.get(1).value());
		assertEquals("#", result.get(2).value());
		assertEquals(">", result.get(3).value());
	}
	
	@Test
	void applyWithDifferentMatchIndices() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		Token content = createToken("test");
		List<Token> tokens = List.of(content);
		TokenRuleMatch match = new TokenRuleMatch(5, 6, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals("(", result.get(0).value());
		assertEquals("test", result.get(1).value());
		assertEquals(")", result.get(2).value());
	}
	
	@Test
	void applyPreservesOriginalTokens() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		Token original = createToken("original");
		List<Token> tokens = List.of(original);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertSame(prefix, result.get(0));
		assertSame(original, result.get(1));
		assertSame(suffix, result.get(2));
	}
	
	@Test
	void applyResultIsUnmodifiable() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		Token content = createToken("test");
		List<Token> tokens = List.of(content);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
		assertThrows(UnsupportedOperationException.class, result::removeFirst);
		assertThrows(UnsupportedOperationException.class, result::clear);
		assertThrows(UnsupportedOperationException.class, () -> result.set(0, createToken("replacement")));
	}
	
	@Test
	void applyWithLongTokenSequence() {
		Token prefix = createToken("{");
		Token suffix = createToken("}");
		WrapTokenAction action = new WrapTokenAction(prefix, suffix);
		List<Token> manyTokens = List.of(
			createToken("a"), createToken("b"), createToken("c"), createToken("d"), createToken("e")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, 5, manyTokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(7, result.size());
		assertEquals("{", result.get(0).value());
		assertEquals("a", result.get(1).value());
		assertEquals("b", result.get(2).value());
		assertEquals("c", result.get(3).value());
		assertEquals("d", result.get(4).value());
		assertEquals("e", result.get(5).value());
		assertEquals("}", result.get(6).value());
	}
	
	@Test
	void equalActionsHaveSameHashCode() {
		Token prefix = createToken("(");
		Token suffix = createToken(")");
		WrapTokenAction action1 = new WrapTokenAction(prefix, suffix);
		WrapTokenAction action2 = new WrapTokenAction(prefix, suffix);
		
		assertEquals(action1.hashCode(), action2.hashCode());
	}
}
