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

package net.luis.utils.io.token.rule.rules.combinators;

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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnyOfTokenRule}.<br>
 *
 * @author Luis-St
 */
class AnyOfTokenRuleTest {
	
	private static @NotNull TokenRule createRule(@NotNull String value) {
		return new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
				Objects.requireNonNull(stream, "Token stream must not be null");
				if (!stream.hasToken()) {
					return null;
				}
				
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
	void constructorWithNullTokenRules() {
		assertThrows(NullPointerException.class, () -> new AnyOfTokenRule(null));
	}
	
	@Test
	void constructorWithEmptyTokenRules() {
		assertThrows(IllegalArgumentException.class, () -> new AnyOfTokenRule(Collections.emptySet()));
	}
	
	@Test
	void constructorWithValidTokenRules() {
		Set<TokenRule> rules = Set.of(createRule("test1"), createRule("test2"));
		
		assertDoesNotThrow(() -> new AnyOfTokenRule(rules));
	}
	
	@Test
	void constructorWithSingleTokenRule() {
		Set<TokenRule> rules = Set.of(createRule("single"));
		
		assertDoesNotThrow(() -> new AnyOfTokenRule(rules));
	}
	
	@Test
	void tokenRulesReturnsCorrectSet() {
		TokenRule rule1 = createRule("test1");
		TokenRule rule2 = createRule("test2");
		Set<TokenRule> originalRules = Set.of(rule1, rule2);
		AnyOfTokenRule anyRule = new AnyOfTokenRule(originalRules);
		
		Set<TokenRule> returnedRules = anyRule.tokenRules();
		
		assertEquals(originalRules, returnedRules);
		assertTrue(returnedRules.contains(rule1));
		assertTrue(returnedRules.contains(rule2));
	}
	
	@Test
	void tokenRulesReturnsUnmodifiableSet() {
		Set<TokenRule> rules = Set.of(createRule("test"));
		AnyOfTokenRule anyRule = new AnyOfTokenRule(rules);
		
		Set<TokenRule> returnedRules = anyRule.tokenRules();
		
		assertThrows(UnsupportedOperationException.class, () -> returnedRules.add(createRule("new")));
		assertThrows(UnsupportedOperationException.class, () -> returnedRules.remove(rules.iterator().next()));
		assertThrows(UnsupportedOperationException.class, returnedRules::clear);
	}
	
	@Test
	void matchWithNullTokenStream() {
		AnyOfTokenRule rule = new AnyOfTokenRule(Set.of(createRule("test")));
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		AnyOfTokenRule rule = new AnyOfTokenRule(Set.of(createRule("test")));
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		AnyOfTokenRule rule = new AnyOfTokenRule(Set.of(createRule("test")));
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 1)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void matchWithFirstMatchingRule() {
		TokenRule rule1 = createRule("match");
		TokenRule rule2 = createRule("other");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(Set.of(rule1, rule2));
		Token token = createToken("match");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = anyRule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(token, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithSecondMatchingRule() {
		TokenRule rule1 = createRule("nomatch");
		TokenRule rule2 = createRule("match");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(Set.of(rule1, rule2));
		Token token = createToken("match");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = anyRule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(token, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithNoMatchingRules() {
		TokenRule rule1 = createRule("nomatch1");
		TokenRule rule2 = createRule("nomatch2");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(Set.of(rule1, rule2));
		Token token = createToken("different");
		List<Token> tokens = List.of(token);
		
		assertNull(anyRule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchReturnsFirstMatchInOrder() {
		TokenRule alwaysMatch1 = TokenRules.alwaysMatch();
		TokenRule alwaysMatch2 = TokenRules.alwaysMatch();
		LinkedHashSet<TokenRule> orderedRules = new LinkedHashSet<>();
		orderedRules.add(alwaysMatch1);
		orderedRules.add(alwaysMatch2);
		AnyOfTokenRule anyRule = new AnyOfTokenRule(orderedRules);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = anyRule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(alwaysMatch1, match.matchingTokenRule());
	}
	
	@Test
	void matchWithSingleRule() {
		TokenRule rule = createRule("single");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(Set.of(rule));
		Token matchingToken = createToken("single");
		Token nonMatchingToken = createToken("other");
		
		TokenRuleMatch match1 = anyRule.match(new TokenStream(List.of(matchingToken), 0));
		TokenRuleMatch match2 = anyRule.match(new TokenStream(List.of(nonMatchingToken), 0));
		
		assertNotNull(match1);
		assertEquals(matchingToken, match1.matchedTokens().getFirst());
		
		assertNull(match2);
	}
	
	@Test
	void matchWithMultipleMatchingRules() {
		TokenRule rule1 = createRule("test");
		TokenRule rule2 = createRule("test");
		TokenRule rule3 = createRule("other");
		LinkedHashSet<TokenRule> rules = new LinkedHashSet<>();
		rules.add(rule1);
		rules.add(rule2);
		rules.add(rule3);
		AnyOfTokenRule anyRule = new AnyOfTokenRule(rules);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = anyRule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(rule1, match.matchingTokenRule());
	}
	
	@Test
	void matchAtDifferentIndices() {
		TokenRule rule1 = createRule("first");
		TokenRule rule2 = createRule("second");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(Set.of(rule1, rule2));
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		Token token3 = createToken("other");
		List<Token> tokens = List.of(token1, token2, token3);
		
		TokenRuleMatch match0 = anyRule.match(new TokenStream(tokens, 0));
		TokenRuleMatch match1 = anyRule.match(new TokenStream(tokens, 1));
		TokenRuleMatch match2 = anyRule.match(new TokenStream(tokens, 2));
		
		assertNotNull(match0);
		assertEquals(token1, match0.matchedTokens().getFirst());
		
		assertNotNull(match1);
		assertEquals(token2, match1.matchedTokens().getFirst());
		
		assertNull(match2);
	}
	
	@Test
	void matchWithComplexRules() {
		TokenRule patternRule = TokenRules.pattern("\\d+");
		TokenRule stringRule = createRule("text");
		AnyOfTokenRule anyRule = new AnyOfTokenRule(Set.of(patternRule, stringRule));
		Token numberToken = createToken("123");
		Token textToken = createToken("text");
		Token otherToken = createToken("other");
		
		TokenRuleMatch numberMatch = anyRule.match(new TokenStream(List.of(numberToken), 0));
		TokenRuleMatch textMatch = anyRule.match(new TokenStream(List.of(textToken), 0));
		TokenRuleMatch otherMatch = anyRule.match(new TokenStream(List.of(otherToken), 0));
		
		assertNotNull(numberMatch);
		assertEquals(numberToken, numberMatch.matchedTokens().getFirst());
		
		assertNotNull(textMatch);
		assertEquals(textToken, textMatch.matchedTokens().getFirst());
		
		assertNull(otherMatch);
	}
	
	@Test
	void matchWithManyRules() {
		Set<TokenRule> manyRules = Set.of(
			createRule("rule1"), createRule("rule2"), createRule("rule3"),
			createRule("rule4"), createRule("rule5"), createRule("rule6")
		);
		AnyOfTokenRule anyRule = new AnyOfTokenRule(manyRules);
		Token matchingToken = createToken("rule3");
		Token nonMatchingToken = createToken("norule");
		
		TokenRuleMatch match1 = anyRule.match(new TokenStream(List.of(matchingToken), 0));
		TokenRuleMatch match2 = anyRule.match(new TokenStream(List.of(nonMatchingToken), 0));
		
		assertNotNull(match1);
		assertEquals(matchingToken, match1.matchedTokens().getFirst());
		
		assertNull(match2);
	}
	
	@Test
	void equalRulesHaveSameHashCode() {
		Set<TokenRule> rules = Set.of(createRule("test1"), createRule("test2"));
		AnyOfTokenRule rule1 = new AnyOfTokenRule(rules);
		AnyOfTokenRule rule2 = new AnyOfTokenRule(rules);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
}
