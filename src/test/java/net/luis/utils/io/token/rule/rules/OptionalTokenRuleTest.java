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
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link OptionalTokenRule}.<br>
 *
 * @author Luis-St
 */
class OptionalTokenRuleTest {
	
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
		assertThrows(NullPointerException.class, () -> new OptionalTokenRule(null));
	}
	
	@Test
	void constructorWithValidTokenRule() {
		TokenRule rule = createRule("test");
		
		assertDoesNotThrow(() -> new OptionalTokenRule(rule));
	}
	
	@Test
	void tokenRuleReturnsCorrectRule() {
		TokenRule rule = createRule("test");
		OptionalTokenRule optional = new OptionalTokenRule(rule);
		
		assertEquals(rule, optional.tokenRule());
	}
	
	@Test
	void matchWithNullTokenList() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		
		assertNull(rule.match(Collections.emptyList(), 0));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		List<Token> tokens = List.of(createToken("test"));
		
		assertNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 5));
		assertNull(rule.match(tokens, -1));
	}
	
	@Test
	void matchWithMatchingToken() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("match"));
		Token matchingToken = createToken("match");
		List<Token> tokens = List.of(matchingToken);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(matchingToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithNonMatchingTokenReturnsEmptyMatch() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("expected"));
		Token nonMatchingToken = createToken("different");
		List<Token> tokens = List.of(nonMatchingToken);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		assertEquals(TokenRules.alwaysMatch(), match.matchingTokenRule());
	}
	
	@Test
	void matchAtDifferentIndices() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("target"));
		Token other = createToken("other");
		Token target = createToken("target");
		Token another = createToken("another");
		List<Token> tokens = List.of(other, target, another);
		
		TokenRuleMatch match0 = rule.match(tokens, 0);
		assertNotNull(match0);
		assertTrue(match0.matchedTokens().isEmpty());
		assertEquals(0, match0.startIndex());
		assertEquals(0, match0.endIndex());
		
		TokenRuleMatch match1 = rule.match(tokens, 1);
		assertNotNull(match1);
		assertEquals(1, match1.matchedTokens().size());
		assertEquals(target, match1.matchedTokens().getFirst());
		assertEquals(1, match1.startIndex());
		assertEquals(2, match1.endIndex());
		
		TokenRuleMatch match2 = rule.match(tokens, 2);
		assertNotNull(match2);
		assertTrue(match2.matchedTokens().isEmpty());
		assertEquals(2, match2.startIndex());
		assertEquals(2, match2.endIndex());
	}
	
	@Test
	void matchWithComplexInnerRule() {
		TokenRule numberRule = TokenRules.pattern("\\d+");
		OptionalTokenRule optional = new OptionalTokenRule(numberRule);
		Token numberToken = createToken("123");
		Token textToken = createToken("abc");
		
		TokenRuleMatch numberMatch = optional.match(List.of(numberToken), 0);
		assertNotNull(numberMatch);
		assertEquals(1, numberMatch.matchedTokens().size());
		assertEquals(numberToken, numberMatch.matchedTokens().getFirst());
		
		TokenRuleMatch textMatch = optional.match(List.of(textToken), 0);
		assertNotNull(textMatch);
		assertTrue(textMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		OptionalTokenRule optional = new OptionalTokenRule(TokenRules.alwaysMatch());
		Token anyToken = createToken("anything");
		List<Token> tokens = List.of(anyToken);
		
		TokenRuleMatch match = optional.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(1, match.matchedTokens().size());
		assertEquals(anyToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithSequenceRule() {
		TokenRule sequence = TokenRules.sequence(createRule("hello"), createRule("world"));
		OptionalTokenRule optional = new OptionalTokenRule(sequence);
		
		List<Token> matchingTokens = List.of(createToken("hello"), createToken("world"));
		TokenRuleMatch match1 = optional.match(matchingTokens, 0);
		assertNotNull(match1);
		assertEquals(2, match1.matchedTokens().size());
		
		List<Token> nonMatchingTokens = List.of(createToken("hello"), createToken("universe"));
		TokenRuleMatch match2 = optional.match(nonMatchingTokens, 0);
		assertNotNull(match2);
		assertTrue(match2.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithRepeatedRule() {
		TokenRule repeated = TokenRules.repeatExactly(createRule("x"), 2);
		OptionalTokenRule optional = new OptionalTokenRule(repeated);
		
		List<Token> exactTokens = List.of(createToken("x"), createToken("x"));
		TokenRuleMatch exactMatch = optional.match(exactTokens, 0);
		assertNotNull(exactMatch);
		assertEquals(2, exactMatch.matchedTokens().size());
		
		List<Token> insufficientTokens = List.of(createToken("x"));
		TokenRuleMatch insufficientMatch = optional.match(insufficientTokens, 0);
		assertNotNull(insufficientMatch);
		assertTrue(insufficientMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithAnyOfRule() {
		TokenRule anyOf = TokenRules.any(createRule("option1"), createRule("option2"));
		OptionalTokenRule optional = new OptionalTokenRule(anyOf);
		
		TokenRuleMatch match1 = optional.match(List.of(createToken("option1")), 0);
		assertNotNull(match1);
		assertEquals(1, match1.matchedTokens().size());
		
		TokenRuleMatch match2 = optional.match(List.of(createToken("option2")), 0);
		assertNotNull(match2);
		assertEquals(1, match2.matchedTokens().size());
		
		TokenRuleMatch match3 = optional.match(List.of(createToken("option3")), 0);
		assertNotNull(match3);
		assertTrue(match3.matchedTokens().isEmpty());
	}
	
	@Test
	void matchConsistencyAcrossMultipleCalls() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match1 = rule.match(tokens, 0);
		TokenRuleMatch match2 = rule.match(tokens, 0);
		
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens().size(), match2.matchedTokens().size());
		if (!match1.matchedTokens().isEmpty()) {
			assertEquals(match1.matchedTokens().getFirst(), match2.matchedTokens().getFirst());
		}
	}
	
	@Test
	void matchEmptyMatchStructure() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("nomatch"));
		Token token = createToken("different");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch emptyMatch = rule.match(tokens, 0);
		
		assertNotNull(emptyMatch);
		assertEquals(0, emptyMatch.startIndex());
		assertEquals(0, emptyMatch.endIndex());
		assertTrue(emptyMatch.matchedTokens().isEmpty());
		assertEquals(0, emptyMatch.matchedTokens().size());
		assertThrows(UnsupportedOperationException.class, () -> emptyMatch.matchedTokens().add(token));
	}
	
	@Test
	void matchAlwaysReturnsNonNull() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("anything"));
		
		assertNotNull(rule.match(List.of(createToken("anything")), 0));
		assertNotNull(rule.match(List.of(createToken("different")), 0));
		assertNotNull(rule.match(List.of(createToken("a"), createToken("b")), 0));
		assertNotNull(rule.match(List.of(createToken("a"), createToken("b")), 1));
	}
	
	@Test
	void nestedOptionalRules() {
		OptionalTokenRule inner = new OptionalTokenRule(createRule("inner"));
		OptionalTokenRule outer = new OptionalTokenRule(inner);
		
		TokenRuleMatch match1 = outer.match(List.of(createToken("inner")), 0);
		assertNotNull(match1);
		assertEquals(1, match1.matchedTokens().size());
		
		TokenRuleMatch match2 = outer.match(List.of(createToken("other")), 0);
		assertNotNull(match2);
		assertTrue(match2.matchedTokens().isEmpty());
	}
	
	@Test
	void equalRulesHaveSameHashCode() {
		TokenRule innerRule = createRule("test");
		OptionalTokenRule rule1 = new OptionalTokenRule(innerRule);
		OptionalTokenRule rule2 = new OptionalTokenRule(innerRule);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
}
