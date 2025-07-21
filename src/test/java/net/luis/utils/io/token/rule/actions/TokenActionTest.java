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
 * Test class for {@link TokenAction}.<br>
 *
 * @author Luis-St
 */
class TokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void identityReturnsNonNullAction() {
		TokenAction identity = TokenAction.identity();
		
		assertNotNull(identity);
	}
	
	@Test
	void identityWithEmptyTokenList() {
		TokenAction identity = TokenAction.identity();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		List<Token> result = identity.apply(match);
		
		assertTrue(result.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("test")));
	}
	
	@Test
	void identityWithSingleToken() {
		TokenAction identity = TokenAction.identity();
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = identity.apply(match);
		
		assertEquals(1, result.size());
		assertEquals(token, result.getFirst());
		assertEquals("test", result.getFirst().value());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("another")));
	}
	
	@Test
	void identityWithMultipleTokens() {
		TokenAction identity = TokenAction.identity();
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		Token token3 = createToken("third");
		List<Token> tokens = List.of(token1, token2, token3);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = identity.apply(match);
		
		assertEquals(3, result.size());
		assertEquals(token1, result.get(0));
		assertEquals(token2, result.get(1));
		assertEquals(token3, result.get(2));
		assertEquals("first", result.get(0).value());
		assertEquals("second", result.get(1).value());
		assertEquals("third", result.get(2).value());
	}
	
	@Test
	void identityPreservesTokenOrder() {
		TokenAction identity = TokenAction.identity();
		Token tokenA = createToken("A");
		Token tokenB = createToken("B");
		Token tokenC = createToken("C");
		List<Token> tokens = List.of(tokenA, tokenB, tokenC);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = identity.apply(match);
		
		assertEquals(tokens, result);
		assertSame(tokens, result);
	}
	
	@Test
	void identityWithDifferentMatchIndices() {
		TokenAction identity = TokenAction.identity();
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(5, 6, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = identity.apply(match);
		
		assertEquals(1, result.size());
		assertEquals(token, result.getFirst());
	}
	
	@Test
	void identityResultIsImmutable() {
		TokenAction identity = TokenAction.identity();
		Token token1 = createToken("one");
		Token token2 = createToken("two");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = identity.apply(match);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("three")));
		assertThrows(UnsupportedOperationException.class, result::removeFirst);
		assertThrows(UnsupportedOperationException.class, result::clear);
		assertThrows(UnsupportedOperationException.class, () -> result.set(0, createToken("replacement")));
	}
	
	@Test
	void customActionCanModifyTokens() {
		TokenAction uppercaseAction = match -> match.matchedTokens().stream().map(token -> createToken(token.value().toUpperCase())).toList();
		
		Token token = createToken("hello");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = uppercaseAction.apply(match);
		
		assertEquals(1, result.size());
		assertEquals("HELLO", result.get(0).value());
	}
	
	@Test
	void customActionCanFilterTokens() {
		TokenAction filterAction = match -> match.matchedTokens().stream().filter(token -> token.value().startsWith("keep")).toList();
		
		Token keep1 = createToken("keep1");
		Token remove1 = createToken("remove1");
		Token keep2 = createToken("keep2");
		List<Token> tokens = List.of(keep1, remove1, keep2);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = filterAction.apply(match);
		
		assertEquals(2, result.size());
		assertEquals("keep1", result.get(0).value());
		assertEquals("keep2", result.get(1).value());
	}
	
	@Test
	void customActionCanAddTokens() {
		TokenAction duplicateAction = match -> {
			List<Token> duplicated = match.matchedTokens().stream().flatMap(token -> List.of(token, createToken(token.value() + "_copy")).stream()).toList();
			return duplicated;
		};
		
		Token token = createToken("original");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = duplicateAction.apply(match);
		
		assertEquals(2, result.size());
		assertEquals("original", result.get(0).value());
		assertEquals("original_copy", result.get(1).value());
	}
	
	@Test
	void customActionCanReturnEmptyList() {
		TokenAction removeAllAction = match -> Collections.emptyList();
		
		Token token1 = createToken("test1");
		Token token2 = createToken("test2");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = removeAllAction.apply(match);
		
		assertTrue(result.isEmpty());
	}
}
