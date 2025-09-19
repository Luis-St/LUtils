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

package net.luis.utils.io.token;

import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.rules.NeverMatchTokenRule;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRuleMatch}.<br>
 *
 * @author Luis-St
 */
class TokenRuleMatchTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullMatchedTokens() {
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> new TokenRuleMatch(0, 1, null, rule));
	}
	
	@Test
	void constructorWithNullMatchingTokenRule() {
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(NullPointerException.class, () -> new TokenRuleMatch(0, 1, tokens, null));
	}
	
	@Test
	void constructorWithValidParameters() {
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = new TokenRuleMatch(5, 10, tokens, rule);
		
		assertEquals(5, match.startIndex());
		assertEquals(10, match.endIndex());
		assertEquals(tokens, match.matchedTokens());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void constructorWithSameStartAndEndIndex() {
		List<Token> tokens = List.of(createToken("test"));
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = new TokenRuleMatch(3, 3, tokens, rule);
		
		assertEquals(3, match.startIndex());
		assertEquals(3, match.endIndex());
		assertEquals(tokens, match.matchedTokens());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void constructorWithEmptyTokenList() {
		List<Token> emptyTokens = List.of();
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = new TokenRuleMatch(0, 0, emptyTokens, rule);
		
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void constructorWithNegativeStartIndex() {
		List<Token> tokens = List.of(createToken("test"));
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = new TokenRuleMatch(-1, 5, tokens, rule);
		
		assertEquals(-1, match.startIndex());
		assertEquals(5, match.endIndex());
		assertEquals(tokens, match.matchedTokens());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void constructorWithEndIndexSmallerThanStartIndex() {
		List<Token> tokens = List.of(createToken("test"));
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = new TokenRuleMatch(10, 5, tokens, rule);
		
		assertEquals(10, match.startIndex());
		assertEquals(5, match.endIndex());
		assertEquals(tokens, match.matchedTokens());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void constructorWithZeroIndices() {
		List<Token> tokens = List.of(createToken("test"));
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, rule);
		
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertEquals(tokens, match.matchedTokens());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void constructorWithLargeIndices() {
		List<Token> tokens = List.of(createToken("test"));
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = new TokenRuleMatch(Integer.MAX_VALUE - 1, Integer.MAX_VALUE, tokens, rule);
		
		assertEquals(Integer.MAX_VALUE - 1, match.startIndex());
		assertEquals(Integer.MAX_VALUE, match.endIndex());
		assertEquals(tokens, match.matchedTokens());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void emptyWithNullMatchingTokenRule() {
		assertThrows(NullPointerException.class, () -> TokenRuleMatch.empty(0, null));
	}
	
	@Test
	void emptyWithValidParameters() {
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = TokenRuleMatch.empty(5, rule);
		
		assertEquals(5, match.startIndex());
		assertEquals(5, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void emptyWithNegativeIndex() {
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = TokenRuleMatch.empty(-10, rule);
		
		assertEquals(-10, match.startIndex());
		assertEquals(-10, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void emptyWithZeroIndex() {
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = TokenRuleMatch.empty(0, rule);
		
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void emptyWithLargeIndex() {
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = TokenRuleMatch.empty(Integer.MAX_VALUE, rule);
		
		assertEquals(Integer.MAX_VALUE, match.startIndex());
		assertEquals(Integer.MAX_VALUE, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(rule, match.matchingTokenRule());
	}
	
	@Test
	void testEquals() {
		List<Token> tokens1 = List.of(createToken("hello"));
		List<Token> tokens2 = List.of(createToken("hello"));
		List<Token> tokens3 = List.of(createToken("world"));
		TokenRule rule1 = AlwaysMatchTokenRule.INSTANCE;
		TokenRule rule2 = NeverMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match1 = new TokenRuleMatch(0, 1, tokens1, rule1);
		TokenRuleMatch match2 = new TokenRuleMatch(0, 1, tokens1, rule1);
		TokenRuleMatch match3 = new TokenRuleMatch(0, 1, tokens2, rule1);
		TokenRuleMatch match4 = new TokenRuleMatch(1, 2, tokens1, rule1);
		TokenRuleMatch match5 = new TokenRuleMatch(0, 1, tokens3, rule1);
		TokenRuleMatch match6 = new TokenRuleMatch(0, 1, tokens1, rule2);
		
		assertEquals(match1, match2);
		assertEquals(match1, match3);
		assertNotEquals(match1, match4);
		assertNotEquals(match1, match5);
		assertNotEquals(match1, match6);
		assertNotEquals(match1, null);
		assertNotEquals(match1, "string");
	}
	
	@Test
	void testHashCode() {
		List<Token> tokens = List.of(createToken("test"));
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match1 = new TokenRuleMatch(0, 1, tokens, rule);
		TokenRuleMatch match2 = new TokenRuleMatch(0, 1, tokens, rule);
		TokenRuleMatch match3 = new TokenRuleMatch(1, 2, tokens, rule);
		
		assertEquals(match1.hashCode(), match2.hashCode());
	}
	
	@Test
	void testToString() {
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		TokenRuleMatch match = new TokenRuleMatch(5, 10, tokens, rule);
		String toString = match.toString();
		
		assertNotNull(toString);
		assertTrue(toString.contains("TokenRuleMatch"));
		assertTrue(toString.contains("startIndex"));
		assertTrue(toString.contains("endIndex"));
		assertTrue(toString.contains("matchedTokens"));
		assertTrue(toString.contains("matchingTokenRule"));
		assertTrue(toString.contains("5"));
		assertTrue(toString.contains("10"));
	}
	
	@Test
	void testToStringWithEmptyMatch() {
		TokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		TokenRuleMatch match = TokenRuleMatch.empty(0, rule);
		
		String toString = match.toString();
		assertNotNull(toString);
		assertTrue(toString.contains("TokenRuleMatch"));
	}
}
