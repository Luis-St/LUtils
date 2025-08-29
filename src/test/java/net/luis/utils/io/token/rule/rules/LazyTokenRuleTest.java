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

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.util.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LazyTokenRule}.<br>
 *
 * @author Luis-St
 */
class LazyTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
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
	
	@Test
	void constructorWithNullLazy() {
		assertThrows(NullPointerException.class, () -> new LazyTokenRule((Lazy<TokenRule>) null));
	}
	
	@Test
	void constructorWithValidLazy() {
		Lazy<TokenRule> lazy = new Lazy<>();
		
		assertDoesNotThrow(() -> new LazyTokenRule(lazy));
	}
	
	@Test
	void matchWithNullTokenStream() {
		Lazy<TokenRule> lazy = new Lazy<>(TokenRules.alwaysMatch());
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		
		assertThrows(NullPointerException.class, () -> lazyRule.match(null));
	}
	
	@Test
	void matchWithUninitializedLazy() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertNull(lazyRule.match(stream));
	}
	
	@Test
	void matchWithNullInitializedLazy() {
		Lazy<TokenRule> lazy = new Lazy<>((TokenRule) null);
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertNull(lazyRule.match(stream));
	}
	
	@Test
	void matchWithInitializedLazyThatMatches() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		TokenRule testRule = createRule("test");
		lazy.set(testRule);
		
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		TokenRuleMatch match = lazyRule.match(stream);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(List.of(token), match.matchedTokens());
	}
	
	@Test
	void matchWithInitializedLazyThatDoesNotMatch() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		TokenRule testRule = createRule("expected");
		lazy.set(testRule);
		
		Token token = createToken("actual");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertNull(lazyRule.match(stream));
	}
	
	@Test
	void matchWithPreInitializedLazy() {
		TokenRule testRule = createRule("match");
		Lazy<TokenRule> lazy = new Lazy<>(testRule);
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		
		Token token = createToken("match");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		TokenRuleMatch match = lazyRule.match(stream);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(List.of(token), match.matchedTokens());
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		lazy.set(TokenRules.alwaysMatch());
		
		assertNull(lazyRule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchConsistentBehavior() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		TokenRule testRule = createRule("consistent");
		lazy.set(testRule);
		
		Token token = createToken("consistent");
		List<Token> tokens = List.of(token);
		TokenStream stream1 = new TokenStream(tokens, 0);
		TokenStream stream2 = new TokenStream(tokens, 0);
		
		TokenRuleMatch match1 = lazyRule.match(stream1);
		TokenRuleMatch match2 = lazyRule.match(stream2);
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
	}
	
	@Test
	void matchWithComplexRule() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		
		TokenRule sequenceRule = TokenRules.sequence(
			TokenRules.pattern("\\w+"),
			TokenRules.pattern("\\d+")
		);
		lazy.set(sequenceRule);
		
		Token word = createToken("word");
		Token number = createToken("123");
		List<Token> tokens = List.of(word, number);
		TokenStream stream = new TokenStream(tokens, 0);
		
		TokenRuleMatch match = lazyRule.match(stream);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(2, match.endIndex());
	}
	
	@Test
	void matchAfterLazyInitialization() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertNull(lazyRule.match(stream));
		
		TokenRule testRule = createRule("test");
		lazy.set(testRule);
		
		TokenRuleMatch match = lazyRule.match(new TokenStream(tokens, 0));
		assertNotNull(match);
	}
	
	@Test
	void notReturnsNegatedRule() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		
		TokenRule notRule = lazyRule.not();
		
		assertNotNull(notRule);
		assertInstanceOf(LazyTokenRule.class, notRule);
		assertNotSame(lazyRule, notRule);
	}
	
	@Test
	void doubleNegationReturnsOriginalRule() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		
		TokenRule notRule = lazyRule.not();
		TokenRule doubleNotRule = notRule.not();
		
		assertSame(lazyRule, doubleNotRule);
	}
	
	@Test
	void negatedRuleWithUninitializedLazy() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		TokenRule notRule = lazyRule.not();
		
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertDoesNotThrow(() -> notRule.match(stream));
	}
	
	@Test
	void negatedRuleMatchesWhenOriginalDoesNot() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		TokenRule testRule = TokenRules.pattern("expected");
		lazy.set(testRule);
		
		TokenRule notRule = lazyRule.not();
		
		Token token = createToken("actual");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertNull(lazyRule.match(stream));
		
		TokenRuleMatch negatedMatch = notRule.match(stream);
		assertNotNull(negatedMatch);
		assertEquals(0, negatedMatch.startIndex());
		assertEquals(1, negatedMatch.endIndex());
		assertEquals(1, negatedMatch.matchedTokens().size());
	}
	
	@Test
	void negatedRuleDoesNotMatchWhenOriginalMatches() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		TokenRule testRule = TokenRules.pattern("match");
		lazy.set(testRule);
		
		TokenRule notRule = lazyRule.not();
		
		Token token = createToken("match");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		TokenRuleMatch originalMatch = lazyRule.match(stream);
		assertNotNull(originalMatch);
		
		assertNull(notRule.match(stream));
	}
	
	@Test
	void negatedRuleConsistentBehavior() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		TokenRule testRule = TokenRules.pattern("expected");
		lazy.set(testRule);
		
		TokenRule notRule = lazyRule.not();
		
		Token token = createToken("actual");
		List<Token> tokens = List.of(token);
		TokenStream stream1 = new TokenStream(tokens, 0);
		TokenStream stream2 = new TokenStream(tokens, 0);
		
		TokenRuleMatch match1 = notRule.match(stream1);
		TokenRuleMatch match2 = notRule.match(stream2);
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
	}
	
	@Test
	void recursiveRuleExample() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		
		TokenRule recursiveRule = TokenRules.sequence(
			TokenRules.pattern("\\("),
			lazyRule,
			TokenRules.pattern("\\)")
		);
		
		TokenRule finalRule = TokenRules.any(
			TokenRules.pattern("\\w+"),
			recursiveRule
		);
		
		lazy.set(finalRule);
		
		Token word = createToken("word");
		List<Token> tokens = List.of(word);
		TokenStream stream = new TokenStream(tokens, 0);
		
		TokenRuleMatch match = lazyRule.match(stream);
		
		assertNotNull(match);
		assertTrue(match.startIndex() >= 0);
		assertTrue(match.endIndex() >= match.startIndex());
	}
	
	@Test
	void lazyRuleWithTokenRulesOperations() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		TokenRule testRule = createRule("test");
		lazy.set(testRule);
		
		TokenRule optional = lazyRule.optional();
		TokenRule repeated = lazyRule.repeatAtLeast(1);
		TokenRule lookahead = lazyRule.lookahead();
		
		assertInstanceOf(TokenRule.class, optional);
		assertInstanceOf(TokenRule.class, repeated);
		assertInstanceOf(TokenRule.class, lookahead);
	}
	
	@Test
	void lazyRuleInComplexExpression() {
		Lazy<TokenRule> lazy1 = new Lazy<>();
		Lazy<TokenRule> lazy2 = new Lazy<>();
		LazyTokenRule lazyRule1 = new LazyTokenRule(lazy1);
		LazyTokenRule lazyRule2 = new LazyTokenRule(lazy2);
		
		TokenRule complexRule = TokenRules.sequence(
			lazyRule1,
			TokenRules.pattern("\\s+"),
			lazyRule2
		);
		
		lazy1.set(TokenRules.pattern("\\w+"));
		lazy2.set(TokenRules.pattern("\\d+"));
		
		assertNotNull(complexRule);
	}
	
	@Test
	void lazyRuleMemorizationBehavior() {
		Lazy<TokenRule> lazy = new Lazy<>();
		LazyTokenRule lazyRule = new LazyTokenRule(lazy);
		
		TokenRule originalRule = createRule("test");
		lazy.set(originalRule);
		
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream1 = new TokenStream(tokens, 0);
		TokenStream stream2 = new TokenStream(tokens, 0);
		
		TokenRuleMatch match1 = lazyRule.match(stream1);
		TokenRuleMatch match2 = lazyRule.match(stream2);
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
	}
	
	@Test
	void lazyRuleStateIndependence() {
		Lazy<TokenRule> lazy1 = new Lazy<>();
		Lazy<TokenRule> lazy2 = new Lazy<>();
		LazyTokenRule lazyRule1 = new LazyTokenRule(lazy1);
		LazyTokenRule lazyRule2 = new LazyTokenRule(lazy2);
		
		lazy1.set(createRule("first"));
		
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens1 = List.of(token1);
		List<Token> tokens2 = List.of(token2);
		TokenStream stream1 = new TokenStream(tokens1, 0);
		TokenStream stream2 = new TokenStream(tokens2, 0);
		
		assertNotNull(lazyRule1.match(stream1));
		assertNull(lazyRule2.match(stream2));
		
		lazy2.set(createRule("second"));
		
		assertNotNull(lazyRule1.match(new TokenStream(tokens1, 0)));
		assertNotNull(lazyRule2.match(stream2));
	}
	
	@Test
	void equalityAndHashCode() {
		TokenRule simpleRule = createRule("test");
		
		LazyTokenRule rule1 = new LazyTokenRule(new Lazy<>(TokenRules.alwaysMatch()));
		LazyTokenRule rule2 = new LazyTokenRule(new Lazy<>(TokenRules.alwaysMatch()));
		
		assertEquals(rule1, rule2);
		
		assertEquals(rule1, rule1);
		assertEquals(rule1.hashCode(), rule1.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		LazyTokenRule rule = new LazyTokenRule(new Lazy<>(TokenRules.alwaysMatch()));
		String ruleString = rule.toString();
		
		assertTrue(ruleString.contains("LazyTokenRule"));
		assertTrue(ruleString.contains("lazyTokenRule="));
	}
}
