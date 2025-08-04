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

package net.luis.utils.io.token.rule.rules.assertions;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NotTokenRule}.<br>
 *
 * @author Luis-St
 */
class NotTokenRuleTest {
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
				Objects.requireNonNull(tokens, "Tokens list must not be null");
				if (startIndex >= tokens.size() || startIndex < 0) {
					return null;
				}
				
				Token token = tokens.get(startIndex);
				if (token.value().equals(value)) {
					return new TokenRuleMatch(startIndex, startIndex + 1, List.of(token), this);
				}
				return null;
			}
		};
	}
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullTokenRule() {
		assertThrows(NullPointerException.class, () -> new NotTokenRule(null));
	}
	
	@Test
	void constructorWithValidTokenRule() {
		TokenRule innerRule = createRule("test");
		
		assertDoesNotThrow(() -> new NotTokenRule(innerRule));
	}
	
	@Test
	void tokenRuleReturnsCorrectRule() {
		TokenRule innerRule = createRule("test");
		NotTokenRule notRule = new NotTokenRule(innerRule);
		
		assertEquals(innerRule, notRule.tokenRule());
	}
	
	@Test
	void matchWithNullTokenList() {
		NotTokenRule rule = new NotTokenRule(createRule("test"));
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		NotTokenRule rule = new NotTokenRule(createRule("test"));
		
		assertNull(rule.match(Collections.emptyList(), 0));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		NotTokenRule rule = new NotTokenRule(createRule("test"));
		List<Token> tokens = List.of(createToken("test"));
		
		assertNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 5));
		assertNull(rule.match(tokens, -1));
	}
	
	@Test
	void matchWithInnerRuleMatches() {
		TokenRule innerRule = createRule("target");
		NotTokenRule notRule = new NotTokenRule(innerRule);
		Token target = createToken("target");
		List<Token> tokens = List.of(target);
		
		assertNull(notRule.match(tokens, 0));
	}
	
	@Test
	void matchWithInnerRuleDoesNotMatch() {
		TokenRule innerRule = createRule("target");
		NotTokenRule notRule = new NotTokenRule(innerRule);
		Token other = createToken("other");
		List<Token> tokens = List.of(other);
		
		TokenRuleMatch match = notRule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithMultipleTokens() {
		TokenRule innerRule = createRule("target");
		NotTokenRule notRule = new NotTokenRule(innerRule);
		Token target = createToken("target");
		Token other1 = createToken("other1");
		Token other2 = createToken("other2");
		List<Token> tokens = List.of(target, other1, other2);
		
		assertNull(notRule.match(tokens, 0));
		
		TokenRuleMatch match1 = notRule.match(tokens, 1);
		assertNotNull(match1);
		assertTrue(match1.matchedTokens().isEmpty());
		
		TokenRuleMatch match2 = notRule.match(tokens, 2);
		assertNotNull(match2);
		assertTrue(match2.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		NotTokenRule notRule = new NotTokenRule(TokenRules.alwaysMatch());
		Token token = createToken("anything");
		List<Token> tokens = List.of(token);
		
		assertNull(notRule.match(tokens, 0));
	}
	
	@Test
	void matchWithNeverMatchRule() {
		TokenRule neverMatch = (tokens, startIndex) -> null;
		
		NotTokenRule notRule = new NotTokenRule(neverMatch);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = notRule.match(tokens, 0);
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithComplexInnerRule() {
		TokenRule sequence = TokenRules.sequence(createRule("a"), createRule("b"));
		NotTokenRule notRule = new NotTokenRule(sequence);
		Token a = createToken("a");
		Token b = createToken("b");
		Token c = createToken("c");
		
		List<Token> matchingSequence = List.of(a, b);
		assertNull(notRule.match(matchingSequence, 0));
		
		List<Token> nonMatchingSequence = List.of(a, c);
		TokenRuleMatch match = notRule.match(nonMatchingSequence, 0);
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithOptionalRule() {
		TokenRule optional = TokenRules.optional(createRule("maybe"));
		NotTokenRule notRule = new NotTokenRule(optional);
		Token maybe = createToken("maybe");
		Token other = createToken("other");
		
		List<Token> withMaybe = List.of(maybe);
		assertNull(notRule.match(withMaybe, 0));
		
		List<Token> withOther = List.of(other);
		assertNull(notRule.match(withOther, 0));
	}
	
	@Test
	void matchWithRepeatedRule() {
		TokenRule repeated = TokenRules.repeatExactly(createRule("x"), 2);
		NotTokenRule notRule = new NotTokenRule(repeated);
		Token x = createToken("x");
		Token y = createToken("y");
		
		List<Token> exactMatch = List.of(x, x);
		assertNull(notRule.match(exactMatch, 0));
		
		List<Token> noMatch = List.of(x, y);
		TokenRuleMatch match = notRule.match(noMatch, 0);
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		
		List<Token> singleX = List.of(x);
		TokenRuleMatch singleMatch = notRule.match(singleX, 0);
		assertNotNull(singleMatch);
		assertTrue(singleMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithAnyOfRule() {
		TokenRule anyOf = TokenRules.any(createRule("a"), createRule("b"));
		NotTokenRule notRule = new NotTokenRule(anyOf);
		Token a = createToken("a");
		Token b = createToken("b");
		Token c = createToken("c");
		
		List<Token> matchesA = List.of(a);
		assertNull(notRule.match(matchesA, 0));
		
		List<Token> matchesB = List.of(b);
		assertNull(notRule.match(matchesB, 0));
		
		List<Token> noMatch = List.of(c);
		TokenRuleMatch match = notRule.match(noMatch, 0);
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchDoesNotConsumeTokens() {
		TokenRule innerRule = createRule("target");
		NotTokenRule notRule = new NotTokenRule(innerRule);
		Token other = createToken("other");
		List<Token> tokens = List.of(other);
		
		TokenRuleMatch match = notRule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithDifferentTokenTypes() {
		TokenRule innerRule = createRule("number");
		NotTokenRule notRule = new NotTokenRule(innerRule);
		Token text = createToken("text");
		Token symbol = createToken("!");
		Token space = createToken(" ");
		Token number = createToken("number");
		List<Token> tokens = List.of(text, symbol, space, number);
		
		TokenRuleMatch textMatch = notRule.match(tokens, 0);
		assertNotNull(textMatch);
		assertTrue(textMatch.matchedTokens().isEmpty());
		
		TokenRuleMatch symbolMatch = notRule.match(tokens, 1);
		assertNotNull(symbolMatch);
		assertTrue(symbolMatch.matchedTokens().isEmpty());
		
		TokenRuleMatch spaceMatch = notRule.match(tokens, 2);
		assertNotNull(spaceMatch);
		assertTrue(spaceMatch.matchedTokens().isEmpty());
		
		assertNull(notRule.match(tokens, 3));
	}
	
	@Test
	void matchResultsAreConsistent() {
		TokenRule innerRule = createRule("target");
		NotTokenRule notRule = new NotTokenRule(innerRule);
		Token other = createToken("other");
		List<Token> tokens = List.of(other);
		
		TokenRuleMatch match1 = notRule.match(tokens, 0);
		TokenRuleMatch match2 = notRule.match(tokens, 0);
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
		assertSame(match1.matchingTokenRule(), match2.matchingTokenRule());
	}
	
	@Test
	void doubleNegation() {
		TokenRule innerRule = createRule("test");
		NotTokenRule firstNot = new NotTokenRule(innerRule);
		NotTokenRule doubleNot = new NotTokenRule(firstNot);
		Token test = createToken("test");
		Token other = createToken("other");
		
		List<Token> testTokens = List.of(test);
		TokenRuleMatch testMatch = doubleNot.match(testTokens, 0);
		assertNotNull(testMatch);
		assertTrue(testMatch.matchedTokens().isEmpty());
		
		List<Token> otherTokens = List.of(other);
		assertNull(doubleNot.match(otherTokens, 0));
	}
	
	@Test
	void equalRulesHaveSameHashCode() {
		TokenRule innerRule = createRule("test");
		NotTokenRule rule1 = new NotTokenRule(innerRule);
		NotTokenRule rule2 = new NotTokenRule(innerRule);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		TokenRule innerRule = createRule("test");
		NotTokenRule notRule = new NotTokenRule(innerRule);
		String ruleString = notRule.toString();
		
		assertTrue(ruleString.contains("NotTokenRule"));
		assertTrue(ruleString.contains("tokenRule"));
	}
}
