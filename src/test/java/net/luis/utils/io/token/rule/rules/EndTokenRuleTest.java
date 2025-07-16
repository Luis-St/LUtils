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

package net.luis.utils.io.token.rule.rules;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EndTokenRule}.<br>
 *
 * @author Luis-St
 */
class EndTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	private static @NotNull @Unmodifiable List<Token> createTokenList() {
		return java.util.stream.IntStream.range(0, 100).mapToObj(i -> createToken("token" + i)).toList();
	}
	
	@Test
	void matchWithNullTokenList() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void matchWithEmptyTokenListAtIndexZero() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		
		TokenRuleMatch match = rule.match(Collections.emptyList(), 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(rule, match.matchingTokenRule());
	}
	
	@Test
	void matchWithEmptyTokenListAtHigherIndex() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		
		TokenRuleMatch match = rule.match(Collections.emptyList(), 5);
		
		assertNotNull(match);
		assertEquals(5, match.startIndex());
		assertEquals(5, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithTokensAtValidEndIndex() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		
		TokenRuleMatch match = rule.match(tokens, 2);
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(rule, match.matchingTokenRule());
	}
	
	@Test
	void matchWithTokensAtIndexBeyondEnd() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		List<Token> tokens = List.of(createToken("test"));
		
		TokenRuleMatch match = rule.match(tokens, 5);
		
		assertNotNull(match);
		assertEquals(5, match.startIndex());
		assertEquals(5, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void noMatchWithTokensAtValidTokenIndex() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		List<Token> tokens = List.of(createToken("first"), createToken("second"));
		
		assertNull(rule.match(tokens, 0));
		assertNull(rule.match(tokens, 1));
	}
	
	@Test
	void noMatchWithSingleTokenAtIndexZero() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		List<Token> tokens = List.of(createToken("single"));
		
		assertNull(rule.match(tokens, 0));
	}
	
	@Test
	void matchWithLargeTokenListAtEnd() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		List<Token> largeList = createTokenList();
		
		TokenRuleMatch match = rule.match(largeList, 100);
		
		assertNotNull(match);
		assertEquals(100, match.startIndex());
		assertEquals(100, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void noMatchWithLargeTokenListInMiddle() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		List<Token> largeList = createTokenList();
		
		assertNull(rule.match(largeList, 50));
		assertNull(rule.match(largeList, 99));
	}
	
	@Test
	void matchAtNegativeIndexBeyondSize() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		List<Token> tokens = List.of(createToken("test"));
		
		TokenRuleMatch match = rule.match(tokens, -5);
		assertNull(match);
	}
	
	@Test
	void matchConsistencyAcrossMultipleCalls() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		List<Token> tokens = List.of(createToken("test"));
		
		TokenRuleMatch match1 = rule.match(tokens, 1);
		TokenRuleMatch match2 = rule.match(tokens, 1);
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
		assertSame(match1.matchingTokenRule(), match2.matchingTokenRule());
	}
	
	@Test
	void matchWithDifferentTokenTypes() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		List<Token> tokens = List.of(
			createToken("text"),
			createToken("123"),
			createToken("!@#")
		);
		
		TokenRuleMatch match = rule.match(tokens, 3);
		
		assertNotNull(match);
		assertEquals(3, match.startIndex());
		assertEquals(3, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchEmptyTokensListIsAlwaysEmpty() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		
		for (int i = 0; i <= 10; i++) {
			TokenRuleMatch match = rule.match(Collections.emptyList(), i);
			assertNotNull(match);
			assertTrue(match.matchedTokens().isEmpty());
			assertEquals(i, match.startIndex());
			assertEquals(i, match.endIndex());
		}
	}
	
	@Test
	void matchBehaviorWithZeroIndex() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		
		TokenRuleMatch emptyMatch = rule.match(Collections.emptyList(), 0);
		assertNotNull(emptyMatch);
		
		List<Token> nonEmpty = List.of(createToken("test"));
		assertNull(rule.match(nonEmpty, 0));
	}
	
	@Test
	void matchResultsHaveCorrectStructure() {
		EndTokenRule rule = EndTokenRule.INSTANCE;
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		
		TokenRuleMatch match = rule.match(tokens, 2);
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(0, match.matchedTokens().size());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(rule, match.matchingTokenRule());
		assertThrows(UnsupportedOperationException.class, () -> match.matchedTokens().add(createToken("new")));
	}
	
	@Test
	void equalInstancesHaveSameHashCode() {
		assertEquals(EndTokenRule.INSTANCE.hashCode(), EndTokenRule.INSTANCE.hashCode());
	}
}

