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

import net.luis.utils.exception.NotInitializedException;
import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Supplier;

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
	void defaultConstructor() {
		assertDoesNotThrow(() -> new LazyTokenRule());
	}
	
	@Test
	void getOnUninitializedRule() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		assertThrows(NotInitializedException.class, lazyRule::get);
	}
	
	@Test
	void setWithNullTokenRule() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		assertThrows(NullPointerException.class, () -> lazyRule.set(null));
	}
	
	@Test
	void setWithValidTokenRule() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = createRule("test");
		
		assertDoesNotThrow(() -> lazyRule.set(testRule));
	}
	
	@Test
	void getAfterSet() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = createRule("test");
		
		lazyRule.set(testRule);
		
		assertSame(testRule, lazyRule.get());
	}
	
	@Test
	void setOverwritesPreviousRule() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule firstRule = createRule("first");
		TokenRule secondRule = createRule("second");
		
		lazyRule.set(firstRule);
		assertSame(firstRule, lazyRule.get());
		
		lazyRule.set(secondRule);
		assertSame(secondRule, lazyRule.get());
	}
	
	@Test
	void lazyTokenRuleReturnsSupplier() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		Supplier<TokenRule> supplier = lazyRule.lazyTokenRule();
		
		assertNotNull(supplier);
		assertThrows(NotInitializedException.class, supplier::get);
	}
	
	@Test
	void lazyTokenRuleSupplierAfterSet() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = createRule("test");
		
		lazyRule.set(testRule);
		Supplier<TokenRule> supplier = lazyRule.lazyTokenRule();
		
		assertNotNull(supplier);
		assertSame(testRule, supplier.get());
	}
	
	@Test
	void matchWithNullTokenStream() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		assertThrows(NullPointerException.class, () -> lazyRule.match(null));
	}
	
	@Test
	void matchWithUninitializedRule() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertNull(lazyRule.match(stream));
	}
	
	@Test
	void matchWithInitializedRuleThatMatches() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = createRule("test");
		lazyRule.set(testRule);
		
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
	void matchWithInitializedRuleThatDoesNotMatch() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = createRule("expected");
		lazyRule.set(testRule);
		
		Token token = createToken("actual");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertNull(lazyRule.match(stream));
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		lazyRule.set(TokenRules.alwaysMatch());
		
		assertNull(lazyRule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchConsistentBehavior() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = createRule("consistent");
		lazyRule.set(testRule);
		
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
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		TokenRule sequenceRule = TokenRules.sequence(
			TokenRules.pattern("\\w+"),
			TokenRules.pattern("\\d+")
		);
		lazyRule.set(sequenceRule);
		
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
	void matchAfterInitialization() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertNull(lazyRule.match(stream));
		
		TokenRule testRule = createRule("test");
		lazyRule.set(testRule);
		
		TokenRuleMatch match = lazyRule.match(new TokenStream(tokens, 0));
		assertNotNull(match);
	}
	
	@Test
	void notReturnsNegatedRule() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		TokenRule notRule = lazyRule.not();
		
		assertNotNull(notRule);
		assertInstanceOf(LazyTokenRule.class, notRule);
		assertNotSame(lazyRule, notRule);
	}
	
	@Test
	void doubleNegationReturnsOriginalRule() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		TokenRule notRule = lazyRule.not();
		TokenRule doubleNotRule = notRule.not();
		
		assertSame(lazyRule, doubleNotRule);
	}
	
	@Test
	void negatedRuleWithUninitializedRule() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule notRule = lazyRule.not();
		
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertDoesNotThrow(() -> notRule.match(stream));
	}
	
	@Test
	void negatedRuleMatchesWhenOriginalDoesNot() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = TokenRules.pattern("expected");
		lazyRule.set(testRule);
		
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
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = TokenRules.pattern("match");
		lazyRule.set(testRule);
		
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
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = TokenRules.pattern("expected");
		lazyRule.set(testRule);
		
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
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		TokenRule recursiveRule = TokenRules.sequence(
			TokenRules.pattern("\\("),
			lazyRule,
			TokenRules.pattern("\\)")
		);
		
		TokenRule finalRule = TokenRules.any(
			TokenRules.pattern("\\w+"),
			recursiveRule
		);
		
		lazyRule.set(finalRule);
		
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
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = createRule("test");
		lazyRule.set(testRule);
		
		TokenRule optional = lazyRule.optional();
		TokenRule repeated = lazyRule.repeatAtLeast(1);
		TokenRule lookahead = lazyRule.lookahead();
		
		assertInstanceOf(TokenRule.class, optional);
		assertInstanceOf(TokenRule.class, repeated);
		assertInstanceOf(TokenRule.class, lookahead);
	}
	
	@Test
	void lazyRuleInComplexExpression() {
		LazyTokenRule lazyRule1 = new LazyTokenRule();
		LazyTokenRule lazyRule2 = new LazyTokenRule();
		
		TokenRule complexRule = TokenRules.sequence(
			lazyRule1,
			TokenRules.pattern("\\s+"),
			lazyRule2
		);
		
		lazyRule1.set(TokenRules.pattern("\\w+"));
		lazyRule2.set(TokenRules.pattern("\\d+"));
		
		assertNotNull(complexRule);
	}
	
	@Test
	void lazyRuleMemorizationBehavior() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		TokenRule originalRule = createRule("test");
		lazyRule.set(originalRule);
		
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
		LazyTokenRule lazyRule1 = new LazyTokenRule();
		LazyTokenRule lazyRule2 = new LazyTokenRule();
		
		lazyRule1.set(createRule("first"));
		
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens1 = List.of(token1);
		List<Token> tokens2 = List.of(token2);
		TokenStream stream1 = new TokenStream(tokens1, 0);
		TokenStream stream2 = new TokenStream(tokens2, 0);
		
		assertNotNull(lazyRule1.match(stream1));
		assertNull(lazyRule2.match(stream2));
		
		lazyRule2.set(createRule("second"));
		
		assertNotNull(lazyRule1.match(new TokenStream(tokens1, 0)));
		assertNotNull(lazyRule2.match(stream2));
	}
	
	@Test
	void supplierInterfaceImplementation() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = createRule("test");
		
		assertThrows(NotInitializedException.class, ((Supplier<TokenRule>) lazyRule)::get);
		
		lazyRule.set(testRule);
		assertSame(testRule, lazyRule.get());
	}
	
	@Test
	void memoizationBehaviorOfInternalSupplier() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = createRule("test");
		lazyRule.set(testRule);
		
		TokenRule rule1 = lazyRule.get();
		TokenRule rule2 = lazyRule.get();
		
		assertSame(rule1, rule2);
		assertSame(testRule, rule1);
	}
	
	@Test
	void negatedRuleGetBehavior() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		TokenRule testRule = TokenRules.pattern("test");
		lazyRule.set(testRule);
		
		TokenRule notRule = lazyRule.not();
		
		Supplier<?> supplier = assertInstanceOf(Supplier.class, notRule);
		TokenRule negatedInner = assertInstanceOf(TokenRule.class, supplier.get());
		assertNotNull(negatedInner);
	}
	
	@Test
	void notInitializedExceptionMessage() {
		LazyTokenRule lazyRule = new LazyTokenRule();
		
		NotInitializedException exception = assertThrows(NotInitializedException.class, lazyRule::get);
		
		assertTrue(exception.getMessage().contains("not been initialized"));
	}
	
	@Test
	void equalityAndHashCode() {
		LazyTokenRule rule1 = new LazyTokenRule();
		LazyTokenRule rule2 = new LazyTokenRule();
		
		assertEquals(rule1, rule2);
		assertEquals(rule1.hashCode(), rule2.hashCode());
		
		assertEquals(rule1, rule1);
		assertEquals(rule1.hashCode(), rule1.hashCode());
		
		rule1.set(createRule("test1"));
		rule2.set(createRule("test2"));
		
		assertEquals(0, rule1.hashCode());
		assertEquals(0, rule2.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		LazyTokenRule rule = new LazyTokenRule();
		String ruleString = rule.toString();
		
		assertTrue(ruleString.contains("LazyTokenRule"));
		assertTrue(ruleString.contains("lazyTokenRule="));
		
		rule.set(TokenRules.alwaysMatch());
		String initializedRuleString = rule.toString();
		
		assertTrue(initializedRuleString.contains("LazyTokenRule"));
		assertTrue(initializedRuleString.contains("lazyTokenRule="));
	}
}
