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

package net.luis.utils.io.token.rule.rules.quantifiers;

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RepeatedTokenRule}.<br>
 *
 * @author Luis-St
 */
class RepeatedTokenRuleTest {
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
				Objects.requireNonNull(stream, "Token stream must not be null");
				
				int startIndex = stream.getCurrentIndex();
				Token token = stream.getCurrentToken();
				if (token.value().equals(value)) {
					return new TokenRuleMatch(startIndex, stream.consumeToken(), List.of(token), this);
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
		assertThrows(NullPointerException.class, () -> new RepeatedTokenRule(null, 1));
		assertThrows(NullPointerException.class, () -> new RepeatedTokenRule(null, 1, 3));
	}
	
	@Test
	void constructorWithNegativeOccurrences() {
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(rule, -1));
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(rule, -1, 3));
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(rule, 1, -1));
	}
	
	@Test
	void constructorWithInvalidRange() {
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(rule, 5, 3));
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(rule, 10, 5));
	}
	
	@Test
	void constructorWithZeroMaxOccurrences() {
		TokenRule rule = createRule("test");
		
		assertThrows(IllegalArgumentException.class, () -> new RepeatedTokenRule(rule, 0, 0));
	}
	
	@Test
	void constructorWithValidParameters() {
		TokenRule rule = createRule("test");
		
		assertDoesNotThrow(() -> new RepeatedTokenRule(rule, 0, 1));
		assertDoesNotThrow(() -> new RepeatedTokenRule(rule, 1, 3));
		assertDoesNotThrow(() -> new RepeatedTokenRule(rule, 5));
		assertDoesNotThrow(() -> new RepeatedTokenRule(rule, 0, Integer.MAX_VALUE));
	}
	
	@Test
	void tokenRuleReturnsCorrectValue() {
		TokenRule rule = createRule("test");
		RepeatedTokenRule repeated = new RepeatedTokenRule(rule, 1, 3);
		
		assertSame(rule, repeated.tokenRule());
	}
	
	@Test
	void minOccurrencesReturnsCorrectValue() {
		TokenRule rule = createRule("test");
		
		assertEquals(0, new RepeatedTokenRule(rule, 0, 5).minOccurrences());
		assertEquals(2, new RepeatedTokenRule(rule, 2, 5).minOccurrences());
		assertEquals(7, new RepeatedTokenRule(rule, 7).minOccurrences());
	}
	
	@Test
	void maxOccurrencesReturnsCorrectValue() {
		TokenRule rule = createRule("test");
		
		assertEquals(5, new RepeatedTokenRule(rule, 0, 5).maxOccurrences());
		assertEquals(3, new RepeatedTokenRule(rule, 1, 3).maxOccurrences());
		assertEquals(7, new RepeatedTokenRule(rule, 7).maxOccurrences());
	}
	
	@Test
	void matchWithNullTokenStream() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("test"), 1, 3);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("test"), 1, 3);
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("test"), 1, 3);
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void matchWithExactOccurrences() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("x"), 3);
		List<Token> exactTokens = List.of(createToken("x"), createToken("x"), createToken("x"));
		List<Token> insufficientTokens = List.of(createToken("x"), createToken("x"));
		List<Token> excessiveTokens = List.of(createToken("x"), createToken("x"), createToken("x"), createToken("x"));
		
		TokenRuleMatch exactMatch = rule.match(new TokenStream(exactTokens, 0));
		assertNotNull(exactMatch);
		assertEquals(0, exactMatch.startIndex());
		assertEquals(3, exactMatch.endIndex());
		assertEquals(3, exactMatch.matchedTokens().size());
		
		assertNull(rule.match(new TokenStream(insufficientTokens, 0)));
		
		assertNull(rule.match(new TokenStream(excessiveTokens, 0)));
		
		TokenRuleMatch excessiveWithOffsetMatch = rule.match(new TokenStream(excessiveTokens, 1));
		assertNotNull(excessiveWithOffsetMatch);
		assertEquals(1, excessiveWithOffsetMatch.startIndex());
		assertEquals(4, excessiveWithOffsetMatch.endIndex());
		assertEquals(3, excessiveWithOffsetMatch.matchedTokens().size());
	}
	
	@Test
	void matchWithMinimumOccurrences() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("a"), 2, 5);
		
		List<Token> tooFew = List.of(createToken("a"));
		assertNull(rule.match(new TokenStream(tooFew, 0)));
		
		List<Token> atMin = List.of(createToken("a"), createToken("a"));
		TokenRuleMatch minMatch = rule.match(new TokenStream(atMin, 0));
		assertNotNull(minMatch);
		assertEquals(2, minMatch.matchedTokens().size());
		
		List<Token> aboveMin = List.of(createToken("a"), createToken("a"), createToken("a"));
		TokenRuleMatch aboveMatch = rule.match(new TokenStream(aboveMin, 0));
		assertNotNull(aboveMatch);
		assertEquals(3, aboveMatch.matchedTokens().size());
	}
	
	@Test
	void matchWithMaximumOccurrences() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("b"), 1, 3);
		
		List<Token> atMax = List.of(createToken("b"), createToken("b"), createToken("b"));
		TokenRuleMatch maxMatch = rule.match(new TokenStream(atMax, 0));
		assertNotNull(maxMatch);
		assertEquals(3, maxMatch.matchedTokens().size());
		
		List<Token> aboveMax = List.of(createToken("b"), createToken("b"), createToken("b"), createToken("b"), createToken("b"));
		assertNull(rule.match(new TokenStream(aboveMax, 0)));
	}
	
	@Test
	void matchWithZeroMinimumOccurrences() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("optional"), 0, 2);
		
		List<Token> noMatch = List.of(createToken("other"));
		TokenRuleMatch noMatchResult = rule.match(new TokenStream(noMatch, 0));
		assertNotNull(noMatchResult);
		assertEquals(0, noMatchResult.startIndex());
		assertEquals(0, noMatchResult.endIndex());
		assertTrue(noMatchResult.matchedTokens().isEmpty());
		
		List<Token> oneMatch = List.of(createToken("optional"));
		TokenRuleMatch oneMatchResult = rule.match(new TokenStream(oneMatch, 0));
		assertNotNull(oneMatchResult);
		assertEquals(1, oneMatchResult.matchedTokens().size());
		
		List<Token> twoMatch = List.of(createToken("optional"), createToken("optional"));
		TokenRuleMatch twoMatchResult = rule.match(new TokenStream(twoMatch, 0));
		assertNotNull(twoMatchResult);
		assertEquals(2, twoMatchResult.matchedTokens().size());
	}
	
	@Test
	void matchWithMixedTokens() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("target"), 2, 4);
		List<Token> mixedTokens = List.of(
			createToken("target"),
			createToken("target"),
			createToken("other"),
			createToken("target")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(mixedTokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(2, match.matchedTokens().size());
		assertEquals("target", match.matchedTokens().get(0).value());
		assertEquals("target", match.matchedTokens().get(1).value());
	}
	
	@Test
	void matchAtDifferentIndices() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("repeat"), 1, 2);
		List<Token> tokens = List.of(
			createToken("other"),
			createToken("repeat"),
			createToken("repeat"),
			createToken("different")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 1));
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(3, match.endIndex());
		assertEquals(2, match.matchedTokens().size());
	}
	
	@Test
	void matchWithComplexInnerRule() {
		TokenRule numberRule = TokenRules.pattern("\\d+");
		RepeatedTokenRule rule = new RepeatedTokenRule(numberRule, 2, 3);
		List<Token> tokens = List.of(
			createToken("123"),
			createToken("456"),
			createToken("abc"),
			createToken("789")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
		assertEquals("123", match.matchedTokens().get(0).value());
		assertEquals("456", match.matchedTokens().get(1).value());
	}
	
	@Test
	void matchWithAlwaysMatchRule() {
		RepeatedTokenRule rule = new RepeatedTokenRule(TokenRules.alwaysMatch(), 2, 4);
		List<Token> tokens = List.of(
			createToken("any1"),
			createToken("any2"),
			createToken("any3"),
			createToken("any4"),
			createToken("any5")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		assertNull(match);
	}
	
	@Test
	void matchWithOptionalInnerRule() {
		OptionalTokenRule optional = new OptionalTokenRule(createRule("maybe"));
		RepeatedTokenRule rule = new RepeatedTokenRule(optional, 1, 3);
		List<Token> tokens = List.of(createToken("maybe"), createToken("other"));
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		assertNull(match);
	}
	
	@Test
	void matchWithUnlimitedMaximum() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("unlimited"), 1, 10000);
		List<Token> manyTokens = IntStream.range(0, 1000).mapToObj(i -> createToken("unlimited")).toList();
		
		TokenRuleMatch match = rule.match(new TokenStream(manyTokens, 0));
		
		assertNotNull(match);
		assertEquals(1000, match.matchedTokens().size());
	}
	
	@Test
	void matchStopsAtFirstNonMatch() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("good"), 1, 10);
		List<Token> tokens = List.of(
			createToken("good"),
			createToken("good"),
			createToken("bad"),
			createToken("good")
		);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(2, match.matchedTokens().size());
		assertEquals(2, match.endIndex());
	}
	
	@Test
	void matchWithInsufficientTokens() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("need"), 5, 10);
		List<Token> tokens = List.of(
			createToken("need"),
			createToken("need"),
			createToken("need")
		);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchConsistencyAcrossMultipleCalls() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("test"), 2, 3);
		List<Token> tokens = List.of(createToken("test"), createToken("test"), createToken("test"));
		
		TokenRuleMatch match1 = rule.match(new TokenStream(tokens, 0));
		TokenRuleMatch match2 = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens().size(), match2.matchedTokens().size());
	}
	
	@Test
	void notThrowsUnsupportedOperationException() {
		OptionalTokenRule rule = new OptionalTokenRule(createRule("test"));
		
		assertThrows(UnsupportedOperationException.class, rule::not);
	}
	
	@Test
	void equalRulesHaveSameHashCode() {
		TokenRule innerRule = createRule("test");
		RepeatedTokenRule rule1 = new RepeatedTokenRule(innerRule, 2, 5);
		RepeatedTokenRule rule2 = new RepeatedTokenRule(innerRule, 2, 5);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		RepeatedTokenRule rule = new RepeatedTokenRule(createRule("test"), 2, 5);
		String ruleString = rule.toString();
		
		assertTrue(ruleString.contains("RepeatedTokenRule"));
		assertTrue(ruleString.contains("tokenRule="));
		assertTrue(ruleString.contains("minOccurrences=2"));
		assertTrue(ruleString.contains("maxOccurrences=5"));
	}
}
