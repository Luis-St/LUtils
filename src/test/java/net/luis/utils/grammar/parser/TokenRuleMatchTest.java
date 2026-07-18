/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.grammar.parser;

import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.token.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRuleMatch}.<br>
 *
 * @author Luis-St
 */
class TokenRuleMatchTest {
	
	private static final TokenRule NULL_MATCH_RULE = (_, _) -> null;
	
	@Test
	void constructWithValidValues() {
		Token token1 = new SimpleToken("a", TokenPosition.UNPOSITIONED);
		Token token2 = new SimpleToken("b", TokenPosition.UNPOSITIONED);
		List<Token> matchedTokens = List.of(token1, token2);
		
		TokenRuleMatch match = new TokenRuleMatch(0, 2, matchedTokens, NULL_MATCH_RULE);
		
		assertEquals(0, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(matchedTokens, match.matchedTokens());
		assertSame(NULL_MATCH_RULE, match.matchingTokenRule());
	}
	
	@Test
	void createEmptyMatch() {
		TokenRuleMatch match = TokenRuleMatch.empty(5, NULL_MATCH_RULE);
		
		assertEquals(5, match.startIndex());
		assertEquals(5, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertSame(NULL_MATCH_RULE, match.matchingTokenRule());
	}
	
	@Test
	void constructWithNullMatchedTokens() {
		assertThrows(NullPointerException.class, () -> new TokenRuleMatch(0, 0, null, NULL_MATCH_RULE));
	}
	
	@Test
	void constructWithNullMatchingTokenRule() {
		assertThrows(NullPointerException.class, () -> new TokenRuleMatch(0, 0, List.of(), null));
	}
	
	@Test
	void createEmptyMatchWithNullMatchingTokenRule() {
		assertThrows(NullPointerException.class, () -> TokenRuleMatch.empty(0, null));
	}
	
	@Test
	void constructWithNonNullMatchedTokensDoesNotThrow() {
		TokenRuleMatch match = assertDoesNotThrow(() -> new TokenRuleMatch(0, 0, List.of(), NULL_MATCH_RULE));
		
		assertNotNull(match);
	}
	
	@Test
	void constructWithNonNullMatchingTokenRuleDoesNotThrow() {
		TokenRuleMatch match = assertDoesNotThrow(() -> new TokenRuleMatch(0, 0, List.of(), NULL_MATCH_RULE));
		
		assertNotNull(match);
	}
	
	@Test
	void createEmptyMatchWithNonNullMatchingTokenRuleDoesNotThrow() {
		TokenRuleMatch match = assertDoesNotThrow(() -> TokenRuleMatch.empty(0, NULL_MATCH_RULE));
		
		assertNotNull(match);
	}
	
	@Test
	void constructWithEmptyMatchedTokensList() {
		TokenRuleMatch match = new TokenRuleMatch(0, 0, List.of(), NULL_MATCH_RULE);
		
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void constructWithStartIndexEqualToEndIndex() {
		TokenRuleMatch match = new TokenRuleMatch(3, 3, List.of(), NULL_MATCH_RULE);
		
		assertEquals(match.startIndex(), match.endIndex());
		assertEquals(3, match.startIndex());
	}
	
	@Test
	void constructWithStartIndexGreaterThanEndIndex() {
		TokenRuleMatch match = assertDoesNotThrow(() -> new TokenRuleMatch(10, 2, List.of(), NULL_MATCH_RULE));
		
		assertEquals(10, match.startIndex());
		assertEquals(2, match.endIndex());
	}
	
	@Test
	void createEmptyMatchAtIndexZero() {
		TokenRuleMatch match = TokenRuleMatch.empty(0, NULL_MATCH_RULE);
		
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void constructWithMultipleMatchedTokens() {
		Token token1 = new SimpleToken("a", TokenPosition.UNPOSITIONED);
		Token token2 = new SimpleToken("b", TokenPosition.UNPOSITIONED);
		Token token3 = new SimpleToken("c", TokenPosition.UNPOSITIONED);
		
		TokenRuleMatch match = new TokenRuleMatch(0, 3, List.of(token1, token2, token3), NULL_MATCH_RULE);
		
		assertEquals(3, match.matchedTokens().size());
		assertSame(token1, match.matchedTokens().get(0));
		assertSame(token2, match.matchedTokens().get(1));
		assertSame(token3, match.matchedTokens().get(2));
	}
}
