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

package net.luis.utils.io.token.rule;

import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRuleMatch}.<br>
 *
 * @author Luis-St
 */
class TokenRuleMatchTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullMatchedTokens() {
		assertThrows(NullPointerException.class, () -> new TokenRuleMatch(0, 1, null, TokenRules.alwaysMatch()));
	}
	
	@Test
	void constructorWithNullMatchingRule() {
		assertThrows(NullPointerException.class, () -> new TokenRuleMatch(0, 1, Collections.emptyList(), null));
	}
	
	@Test
	void constructorWithValidParameters() {
		List<Token> tokens = List.of(createToken("test"));
		
		assertDoesNotThrow(() -> new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch()));
	}
	
	@Test
	void constructorWithEmptyTokenList() {
		assertDoesNotThrow(() -> new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch()));
	}
	
	@Test
	void constructorWithMultipleTokens() {
		List<Token> tokens = List.of(createToken("test1"), createToken("test2"));
		
		assertDoesNotThrow(() -> new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch()));
	}
	
	@Test
	void constructorWithNegativeIndices() {
		List<Token> tokens = List.of(createToken("test"));
		
		assertDoesNotThrow(() -> new TokenRuleMatch(-1, 0, tokens, TokenRules.alwaysMatch()));
	}
	
	@Test
	void constructorWithSameStartAndEndIndex() {
		assertDoesNotThrow(() -> new TokenRuleMatch(5, 5, Collections.emptyList(), TokenRules.alwaysMatch()));
	}
	
	@Test
	void emptyCreatesCorrectMatch() {
		TokenRuleMatch match = TokenRuleMatch.empty(0, TokenRules.alwaysMatch());
		
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(TokenRules.alwaysMatch(), match.matchingTokenRule());
	}
	
	@Test
	void emptyWithDifferentIndex() {
		TokenRuleMatch match = TokenRuleMatch.empty(5, TokenRules.alwaysMatch());
		
		assertEquals(5, match.startIndex());
		assertEquals(5, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(TokenRules.alwaysMatch(), match.matchingTokenRule());
	}
	
	@Test
	void emptyWithNegativeIndex() {
		TokenRuleMatch match = TokenRuleMatch.empty(-1, TokenRules.alwaysMatch());
		
		assertEquals(-1, match.startIndex());
		assertEquals(-1, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void startIndexReturnsCorrectValue() {
		TokenRuleMatch match = new TokenRuleMatch(5, 10, Collections.emptyList(), TokenRules.alwaysMatch());
		
		assertEquals(5, match.startIndex());
	}
	
	@Test
	void startIndexWithZero() {
		TokenRuleMatch match = new TokenRuleMatch(0, 1, Collections.emptyList(), TokenRules.alwaysMatch());
		
		assertEquals(0, match.startIndex());
	}
	
	@Test
	void endIndexReturnsCorrectValue() {
		TokenRuleMatch match = new TokenRuleMatch(5, 10, Collections.emptyList(), TokenRules.alwaysMatch());
		
		assertEquals(10, match.endIndex());
	}
	
	@Test
	void endIndexWithZero() {
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		assertEquals(0, match.endIndex());
	}
	
	@Test
	void matchedTokensReturnsCorrectList() {
		List<Token> tokens = List.of(createToken("test1"), createToken("test2"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		assertEquals(tokens, match.matchedTokens());
		assertEquals(2, match.matchedTokens().size());
		assertEquals("test1", match.matchedTokens().get(0).value());
		assertEquals("test2", match.matchedTokens().get(1).value());
	}
	
	@Test
	void matchedTokensWithEmptyList() {
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchedTokensWithSingleToken() {
		List<Token> tokens = List.of(createToken("single"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		assertEquals(1, match.matchedTokens().size());
		assertEquals("single", match.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchingTokenRuleReturnsCorrectRule() {
		TokenRuleMatch match = new TokenRuleMatch(0, 1, Collections.emptyList(), TokenRules.alwaysMatch());
		
		assertSame(TokenRules.alwaysMatch(), match.matchingTokenRule());
	}
	
	@Test
	void matchingTokenRuleWithEndRule() {
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.endDocument());
		
		assertEquals(TokenRules.endDocument(), match.matchingTokenRule());
	}
	
	@Test
	void matchingTokenRuleWithCustomRule() {
		var customRule = TokenRules.pattern("\\d+");
		TokenRuleMatch match = new TokenRuleMatch(0, 1, Collections.emptyList(), customRule);
		
		assertSame(customRule, match.matchingTokenRule());
	}
	
	@Test
	void matchSpansMultipleIndices() {
		List<Token> tokens = List.of(createToken("a"), createToken("b"), createToken("c"));
		TokenRuleMatch match = new TokenRuleMatch(1, 4, tokens, TokenRules.alwaysMatch());
		
		assertEquals(1, match.startIndex());
		assertEquals(4, match.endIndex());
		assertEquals(3, match.matchedTokens().size());
	}
	
	@Test
	void matchWithLargeIndices() {
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(1000, 1001, tokens, TokenRules.alwaysMatch());
		
		assertEquals(1000, match.startIndex());
		assertEquals(1001, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
	}
	
	@Test
	void equalMatchesHaveSameHashCode() {
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match1 = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		TokenRuleMatch match2 = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		assertEquals(match1.hashCode(), match2.hashCode());
	}
}
