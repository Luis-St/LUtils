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

import net.luis.utils.io.token.definition.StringTokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.*;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.tokens.TokenGroup;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenGroupRule}.<br>
 *
 * @author Luis-St
 */
class TokenGroupRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	private static @NotNull TokenGroup createTokenGroup(@NotNull List<Token> tokens) {
		String concatenated = tokens.stream().map(Token::value).reduce("", String::concat);
		StringTokenDefinition definition = new StringTokenDefinition(concatenated, false);
		return new TokenGroup(tokens, definition);
	}
	
	@Test
	void constructorWithNullRule() {
		assertThrows(NullPointerException.class, () -> new TokenGroupRule(null));
	}
	
	@Test
	void constructorWithValidRule() {
		TokenRule innerRule = TokenRules.alwaysMatch();
		assertDoesNotThrow(() -> new TokenGroupRule(innerRule));
	}
	
	@Test
	void tokenRuleReturnsCorrectRule() {
		TokenRule innerRule = TokenRules.pattern("test");
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		
		assertEquals(innerRule, groupRule.tokenRule());
	}
	
	@Test
	void matchWithNullTokenList() {
		TokenGroupRule groupRule = new TokenGroupRule(TokenRules.alwaysMatch());
		
		assertThrows(NullPointerException.class, () -> groupRule.match(null, 0));
	}
	
	@Test
	void matchWithNegativeStartIndex() {
		TokenGroupRule groupRule = new TokenGroupRule(TokenRules.alwaysMatch());
		List<Token> tokens = List.of(createToken("test"));
		
		TokenRuleMatch result = groupRule.match(tokens, -1);
		
		assertNull(result);
	}
	
	@Test
	void matchWithStartIndexOutOfBounds() {
		TokenGroupRule groupRule = new TokenGroupRule(TokenRules.alwaysMatch());
		List<Token> tokens = List.of(createToken("test"));
		
		TokenRuleMatch result = groupRule.match(tokens, 1);
		
		assertNull(result);
	}
	
	@Test
	void matchWithEmptyTokenList() {
		TokenGroupRule groupRule = new TokenGroupRule(TokenRules.alwaysMatch());
		List<Token> tokens = List.of();
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNull(result);
	}
	
	@Test
	void matchWithNonGroupToken() {
		TokenGroupRule groupRule = new TokenGroupRule(TokenRules.alwaysMatch());
		Token regularToken = createToken("test");
		List<Token> tokens = List.of(regularToken);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNull(result);
	}
	
	@Test
	void matchWithGroupTokenAndAlwaysMatchRule() {
		TokenGroupRule groupRule = new TokenGroupRule(TokenRules.alwaysMatch());
		Token token1 = createToken("hello");
		Token token2 = createToken("world");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals(tokenGroup, result.matchedTokens().getFirst());
		assertEquals(groupRule, result.matchingTokenRule());
	}
	
	@Test
	void matchWithGroupTokenAndNonMatchingInnerRule() {
		TokenRule innerRule = TokenRules.pattern("nomatch");
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		Token token1 = createToken("hello");
		Token token2 = createToken("world");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNull(result);
	}
	
	@Test
	void matchWithGroupTokenAndMatchingPatternRule() {
		TokenRule innerRule = TokenRules.pattern("hello");
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		Token token1 = createToken("hello");
		Token token2 = createToken("world");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals(tokenGroup, result.matchedTokens().getFirst());
		assertEquals(groupRule, result.matchingTokenRule());
	}
	
	@Test
	void matchWithGroupTokenAndSequenceRule() {
		TokenRule innerRule = TokenRules.sequence(
			TokenRules.pattern("hello"),
			TokenRules.pattern("world")
		);
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		Token token1 = createToken("hello");
		Token token2 = createToken("world");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals(tokenGroup, result.matchedTokens().getFirst());
		assertEquals(groupRule, result.matchingTokenRule());
	}
	
	@Test
	void matchWithGroupTokenAndNonMatchingSequenceRule() {
		TokenRule innerRule = TokenRules.sequence(
			TokenRules.pattern("hello"),
			TokenRules.pattern("goodbye")
		);
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		Token token1 = createToken("hello");
		Token token2 = createToken("world");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNull(result);
	}
	
	@Test
	void matchWithGroupTokenAndOptionalRule() {
		TokenRule innerRule = TokenRules.optional(TokenRules.pattern("hello"));
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		Token token1 = createToken("hello");
		Token token2 = createToken("world");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals(tokenGroup, result.matchedTokens().getFirst());
		assertEquals(groupRule, result.matchingTokenRule());
	}
	
	@Test
	void matchWithGroupTokenAndRepeatedRule() {
		TokenRule innerRule = TokenRules.repeatAtLeast(TokenRules.pattern("test"), 2);
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		Token token1 = createToken("test");
		Token token2 = createToken("test");
		Token token3 = createToken("test");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2, token3));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals(tokenGroup, result.matchedTokens().getFirst());
		assertEquals(groupRule, result.matchingTokenRule());
	}
	
	@Test
	void matchWithGroupTokenAndNonMatchingRepeatedRule() {
		TokenRule innerRule = TokenRules.repeatAtLeast(TokenRules.pattern("test"), 5);
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		Token token1 = createToken("test");
		Token token2 = createToken("test");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNull(result);
	}
	
	@Test
	void matchWithMultipleTokensIncludingGroup() {
		TokenRule innerRule = TokenRules.pattern("hello");
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		Token regularToken = createToken("regular");
		Token token1 = createToken("hello");
		Token token2 = createToken("world");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2));
		Token anotherRegularToken = createToken("another");
		List<Token> tokens = List.of(regularToken, tokenGroup, anotherRegularToken);
		
		TokenRuleMatch result = groupRule.match(tokens, 1);
		
		assertNotNull(result);
		assertEquals(1, result.startIndex());
		assertEquals(2, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals(tokenGroup, result.matchedTokens().getFirst());
		assertEquals(groupRule, result.matchingTokenRule());
		
		TokenRuleMatch result0 = groupRule.match(tokens, 0);
		assertNull(result0);
		
		TokenRuleMatch result2 = groupRule.match(tokens, 2);
		assertNull(result2);
	}
	
	@Test
	void matchWithComplexGroupAndComplexRule() {
		TokenRule innerRule = TokenRules.sequence(
			TokenRules.pattern("start"),
			TokenRules.repeatInfinitely(TokenRules.pattern("middle")),
			TokenRules.pattern("end")
		);
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		
		Token start = createToken("start");
		Token middle1 = createToken("middle");
		Token middle2 = createToken("middle");
		Token end = createToken("end");
		TokenGroup tokenGroup = createTokenGroup(List.of(start, middle1, middle2, end));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals(tokenGroup, result.matchedTokens().getFirst());
		assertEquals(groupRule, result.matchingTokenRule());
	}
	
	@Test
	void matchWithSingleTokenGroup() {
		TokenRule innerRule = TokenRules.pattern("single");
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		Token token1 = createToken("single");
		Token token2 = createToken("dummy");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals(tokenGroup, result.matchedTokens().getFirst());
		assertEquals(groupRule, result.matchingTokenRule());
	}
	
	@Test
	void matchWithEmptyGroupTokensHandling() {
		TokenRule innerRule = TokenRules.start();
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		Token token1 = createToken("hello");
		Token token2 = createToken("world");
		TokenGroup tokenGroup = createTokenGroup(List.of(token1, token2));
		List<Token> tokens = List.of(tokenGroup);
		
		TokenRuleMatch result = groupRule.match(tokens, 0);
		
		assertNotNull(result);
		assertEquals(0, result.startIndex());
		assertEquals(1, result.endIndex());
		assertEquals(1, result.matchedTokens().size());
		assertEquals(tokenGroup, result.matchedTokens().getFirst());
		assertEquals(groupRule, result.matchingTokenRule());
	}
	
	@Test
	void equalGroupRulesHaveSameHashCode() {
		TokenRule innerRule = TokenRules.pattern("test");
		TokenGroupRule groupRule1 = new TokenGroupRule(innerRule);
		TokenGroupRule groupRule2 = new TokenGroupRule(innerRule);
		
		assertEquals(groupRule1.hashCode(), groupRule2.hashCode());
	}
	
	@Test
	void toStringContainsInnerRuleInfo() {
		TokenRule innerRule = TokenRules.pattern("test");
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		String ruleString = groupRule.toString();
		
		assertTrue(ruleString.contains("TokenGroupRule"));
		assertTrue(ruleString.contains("tokenRule"));
	}
	
	@Test
	void matchWithDifferentGroupSizes() {
		TokenRule innerRule = TokenRules.repeatExactly(TokenRules.pattern("item"), 3);
		TokenGroupRule groupRule = new TokenGroupRule(innerRule);
		
		Token item1 = createToken("item");
		Token item2 = createToken("item");
		Token item3 = createToken("item");
		TokenGroup matchingGroup = createTokenGroup(List.of(item1, item2, item3));
		
		Token item4 = createToken("item");
		Token item5 = createToken("item");
		TokenGroup nonMatchingGroup = createTokenGroup(List.of(item4, item5));
		
		List<Token> tokens = List.of(matchingGroup, nonMatchingGroup);
		
		TokenRuleMatch result1 = groupRule.match(tokens, 0);
		assertNotNull(result1);
		assertEquals(matchingGroup, result1.matchedTokens().getFirst());
		
		TokenRuleMatch result2 = groupRule.match(tokens, 1);
		assertNull(result2);
	}
}
