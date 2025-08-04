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
import net.luis.utils.io.token.rule.actions.core.TokenTransformer;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TransformTokenAction}.<br>
 *
 * @author Luis-St
 */
class TransformTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullTransformer() {
		assertThrows(NullPointerException.class, () -> new TransformTokenAction(null));
	}
	
	@Test
	void constructorWithValidTransformer() {
		TokenTransformer transformer = List::copyOf;
		
		assertDoesNotThrow(() -> new TransformTokenAction(transformer));
	}
	
	@Test
	void transformerReturnsCorrectValue() {
		TokenTransformer transformer = List::copyOf;
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		assertSame(transformer, action.transformer());
	}
	
	@Test
	void applyWithNullMatch() {
		TokenTransformer transformer = List::copyOf;
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		assertThrows(NullPointerException.class, () -> action.apply(null));
	}
	
	@Test
	void applyWithEmptyTokenList() {
		TokenTransformer transformer = List::copyOf;
		TransformTokenAction action = new TransformTokenAction(transformer);
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("test")));
	}
	
	@Test
	void applyWithIdentityTransformer() {
		TokenTransformer identity = List::copyOf;
		TransformTokenAction action = new TransformTokenAction(identity);
		Token token1 = createToken("test1");
		Token token2 = createToken("test2");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(token1, result.get(0));
		assertEquals(token2, result.get(1));
		assertEquals("test1", result.get(0).value());
		assertEquals("test2", result.get(1).value());
	}
	
	@Test
	void applyWithUppercaseTransformer() {
		TokenTransformer uppercase = tokens -> tokens.stream()
			.map(token -> createToken(token.value().toUpperCase()))
			.toList();
		TransformTokenAction action = new TransformTokenAction(uppercase);
		Token token = createToken("hello");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("HELLO", result.getFirst().value());
		assertNotSame(token, result.getFirst());
	}
	
	@Test
	void applyWithFilteringTransformer() {
		TokenTransformer filter = tokens -> tokens.stream().filter(token -> token.value().startsWith("keep")).toList();
		TransformTokenAction action = new TransformTokenAction(filter);
		Token keep1 = createToken("keep1");
		Token remove1 = createToken("remove1");
		Token keep2 = createToken("keep2");
		List<Token> tokens = List.of(keep1, remove1, keep2);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals("keep1", result.get(0).value());
		assertEquals("keep2", result.get(1).value());
	}
	
	@Test
	void applyWithDuplicatingTransformer() {
		TokenTransformer duplicate = tokens -> tokens.stream().flatMap(token -> Stream.of(token, createToken(token.value() + "_copy"))).toList();
		TransformTokenAction action = new TransformTokenAction(duplicate);
		Token token = createToken("original");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals("original", result.get(0).value());
		assertEquals("original_copy", result.get(1).value());
	}
	
	@Test
	void applyWithEmptyResultTransformer() {
		TokenTransformer empty = tokens -> Collections.emptyList();
		TransformTokenAction action = new TransformTokenAction(empty);
		Token token1 = createToken("test1");
		Token token2 = createToken("test2");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithComplexTransformer() {
		TokenTransformer complex = tokens -> tokens.stream().filter(token -> token.value().length() > 2).map(token -> createToken("PREFIX_" + token.value().toUpperCase())).toList();
		TransformTokenAction action = new TransformTokenAction(complex);
		Token short1 = createToken("a");
		Token long1 = createToken("hello");
		Token short2 = createToken("hi");
		Token long2 = createToken("world");
		List<Token> tokens = List.of(short1, long1, short2, long2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals("PREFIX_HELLO", result.get(0).value());
		assertEquals("PREFIX_WORLD", result.get(1).value());
	}
	
	@Test
	void applyWithDifferentMatchIndices() {
		TokenTransformer transformer = tokens -> tokens.stream().map(token -> createToken(token.value() + "_transformed")).toList();
		TransformTokenAction action = new TransformTokenAction(transformer);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(5, 6, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("test_transformed", result.getFirst().value());
	}
	
	@Test
	void applyWithSingleTokenTransformer() {
		TokenTransformer reverse = tokens -> tokens.stream().map(token -> createToken(new StringBuilder(token.value()).reverse().toString())).toList();
		TransformTokenAction action = new TransformTokenAction(reverse);
		Token token = createToken("hello");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("olleh", result.getFirst().value());
	}
	
	@Test
	void applyWithMultipleTokenTransformer() {
		TokenTransformer joiner = tokens -> {
			String joined = tokens.stream().map(Token::value).reduce("", (a, b) -> a + b);
			return List.of(createToken(joined));
		};
		TransformTokenAction action = new TransformTokenAction(joiner);
		Token token1 = createToken("hello");
		Token token2 = createToken("world");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("helloworld", result.getFirst().value());
	}
	
	@Test
	void applyResultIsUnmodifiable() {
		TokenTransformer transformer = List::copyOf;
		TransformTokenAction action = new TransformTokenAction(transformer);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
		assertThrows(UnsupportedOperationException.class, result::removeFirst);
		assertThrows(UnsupportedOperationException.class, result::clear);
	}
	
	@Test
	void equalActionsHaveSameHashCode() {
		TokenTransformer transformer = List::copyOf;
		TransformTokenAction action1 = new TransformTokenAction(transformer);
		TransformTokenAction action2 = new TransformTokenAction(transformer);
		
		assertEquals(action1.hashCode(), action2.hashCode());
	}
}
