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
 * Test class for {@link LookaheadTokenRule}.<br>
 *
 * @author Luis-St
 */
class LookaheadTokenRuleTest {
	
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
	void constructorWithNullTokenRule() {
		assertThrows(NullPointerException.class, () -> new LookaheadTokenRule(null, LookMatchMode.POSITIVE));
		assertThrows(NullPointerException.class, () -> new LookaheadTokenRule(null, LookMatchMode.NEGATIVE));
	}
	
	@Test
	void constructorWithNullMode() {
		TokenRule innerRule = createRule("test");
		
		assertThrows(NullPointerException.class, () -> new LookaheadTokenRule(innerRule, null));
	}
	
	@Test
	void constructorWithValidTokenRule() {
		TokenRule innerRule = createRule("test");
		
		assertDoesNotThrow(() -> new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE));
		assertDoesNotThrow(() -> new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE));
	}
	
	@Test
	void tokenRuleReturnsCorrectRule() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule lookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		assertEquals(innerRule, lookahead.tokenRule());
	}
	
	@Test
	void positiveReturnsCorrectValue() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookaheadTokenRule negativeLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		assertEquals(LookMatchMode.POSITIVE, positiveLookahead.mode());
		assertEquals(LookMatchMode.NEGATIVE, negativeLookahead.mode());
	}
	
	@Test
	void matchWithNullTokenStream() {
		LookaheadTokenRule rule = new LookaheadTokenRule(createRule("test"), LookMatchMode.POSITIVE);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		LookaheadTokenRule rule = new LookaheadTokenRule(createRule("test"), LookMatchMode.POSITIVE);
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		LookaheadTokenRule rule = new LookaheadTokenRule(createRule("test"), LookMatchMode.POSITIVE);
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 1)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void positiveLookaheadWithMatchingRule() {
		TokenRule innerRule = createRule("target");
		LookaheadTokenRule lookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token target = createToken("target");
		List<Token> tokens = List.of(target);
		
		TokenRuleMatch match = lookahead.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void positiveLookaheadWithNonMatchingRule() {
		TokenRule innerRule = createRule("target");
		LookaheadTokenRule lookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token other = createToken("other");
		List<Token> tokens = List.of(other);
		
		assertNull(lookahead.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void negativeLookaheadWithMatchingRule() {
		TokenRule innerRule = createRule("target");
		LookaheadTokenRule lookahead = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		Token target = createToken("target");
		List<Token> tokens = List.of(target);
		
		assertNull(lookahead.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void negativeLookaheadWithNonMatchingRule() {
		TokenRule innerRule = createRule("target");
		LookaheadTokenRule lookahead = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		Token other = createToken("other");
		List<Token> tokens = List.of(other);
		
		TokenRuleMatch match = lookahead.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lookaheadDoesNotConsumeTokens() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule lookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token test = createToken("test");
		Token other = createToken("other");
		List<Token> tokens = List.of(test, other);
		
		TokenRuleMatch match = lookahead.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lookaheadWithMultipleTokens() {
		TokenRule innerRule = createRule("second");
		LookaheadTokenRule lookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token first = createToken("first");
		Token second = createToken("second");
		Token third = createToken("third");
		List<Token> tokens = List.of(first, second, third);
		
		assertNull(lookahead.match(new TokenStream(tokens, 0)));
		
		TokenRuleMatch match = lookahead.match(new TokenStream(tokens, 1));
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
		
		assertNull(lookahead.match(new TokenStream(tokens, 2)));
	}
	
	@Test
	void lookaheadWithAlwaysMatchRule() {
		LookaheadTokenRule lookahead = new LookaheadTokenRule(TokenRules.alwaysMatch(), LookMatchMode.POSITIVE);
		Token token = createToken("anything");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = lookahead.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lookaheadWithNeverMatchRule() {
		TokenRule neverMatch = new TokenRule() {
			@Override
			public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
				return null;
			}
		};
		
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(neverMatch, LookMatchMode.POSITIVE);
		LookaheadTokenRule negativeLookahead = new LookaheadTokenRule(neverMatch, LookMatchMode.NEGATIVE);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertNull(positiveLookahead.match(new TokenStream(tokens, 0)));
		
		TokenRuleMatch negativeMatch = negativeLookahead.match(new TokenStream(tokens, 0));
		assertNotNull(negativeMatch);
		assertTrue(negativeMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void lookaheadWithComplexRule() {
		TokenRule sequence = TokenRules.sequence(createRule("a"), createRule("b"));
		LookaheadTokenRule lookahead = new LookaheadTokenRule(sequence, LookMatchMode.POSITIVE);
		Token a = createToken("a");
		Token b = createToken("b");
		Token c = createToken("c");
		List<Token> tokens = List.of(a, b, c);
		
		TokenRuleMatch match = lookahead.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lookaheadWithOptionalRule() {
		TokenRule optional = TokenRules.optional(createRule("maybe"));
		LookaheadTokenRule lookahead = new LookaheadTokenRule(optional, LookMatchMode.POSITIVE);
		Token maybe = createToken("maybe");
		Token other = createToken("other");
		
		List<Token> withMaybe = List.of(maybe);
		TokenRuleMatch matchWithMaybe = lookahead.match(new TokenStream(withMaybe, 0));
		assertNotNull(matchWithMaybe);
		assertTrue(matchWithMaybe.matchedTokens().isEmpty());
		
		List<Token> withOther = List.of(other);
		TokenRuleMatch matchWithOther = lookahead.match(new TokenStream(withOther, 0));
		assertNotNull(matchWithOther);
		assertTrue(matchWithOther.matchedTokens().isEmpty());
	}
	
	@Test
	void lookaheadDoesNotAdvancePosition() {
		TokenRule innerRule = createRule("target");
		LookaheadTokenRule lookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token target = createToken("target");
		Token after = createToken("after");
		List<Token> tokens = List.of(target, after);
		
		TokenRuleMatch match1 = lookahead.match(new TokenStream(tokens, 0));
		assertNotNull(match1);
		assertEquals(0, match1.startIndex());
		assertEquals(0, match1.endIndex());
		
		TokenRuleMatch match2 = innerRule.match(new TokenStream(tokens, 0));
		assertNotNull(match2);
		assertEquals(0, match2.startIndex());
		assertEquals(1, match2.endIndex());
	}
	
	@Test
	void matchResultsAreConsistent() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule lookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match1 = lookahead.match(new TokenStream(tokens, 0));
		TokenRuleMatch match2 = lookahead.match(new TokenStream(tokens, 0));
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
		assertSame(match1.matchingTokenRule(), match2.matchingTokenRule());
	}
	
	@Test
	void notReturnsNegatedRule() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookaheadTokenRule negativeLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenRule negatedPositive = positiveLookahead.not();
		TokenRule negatedNegative = negativeLookahead.not();
		
		assertNotNull(negatedPositive);
		assertNotNull(negatedNegative);
		assertNotSame(positiveLookahead, negatedPositive);
		assertNotSame(negativeLookahead, negatedNegative);
		
		assertEquals(LookMatchMode.NEGATIVE, assertInstanceOf(LookaheadTokenRule.class, negatedPositive).mode());
		assertEquals(LookMatchMode.POSITIVE, assertInstanceOf(LookaheadTokenRule.class, negatedNegative).mode());
	}
	
	@Test
	void doubleNegationReturnsOriginalRule() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookaheadTokenRule negativeLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenRule negatedPositive = positiveLookahead.not();
		TokenRule doubleNegatedPositive = negatedPositive.not();
		
		TokenRule negatedNegative = negativeLookahead.not();
		TokenRule doubleNegatedNegative = negatedNegative.not();
		
		assertEquals(LookMatchMode.POSITIVE, assertInstanceOf(LookaheadTokenRule.class, doubleNegatedPositive).mode());
		assertEquals(LookMatchMode.NEGATIVE, assertInstanceOf(LookaheadTokenRule.class, doubleNegatedNegative).mode());
		assertEquals(innerRule, ((LookaheadTokenRule) doubleNegatedPositive).tokenRule());
		assertEquals(innerRule, ((LookaheadTokenRule) doubleNegatedNegative).tokenRule());
	}
	
	@Test
	void negatedPositiveLookaheadBehavesLikeNegativeLookahead() {
		TokenRule innerRule = createRule("target");
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookaheadTokenRule negativeLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		TokenRule negatedPositive = positiveLookahead.not();
		
		Token target = createToken("target");
		Token other = createToken("other");
		
		List<Token> tokensWithTarget = List.of(target);
		
		TokenRuleMatch negativeMatch = negativeLookahead.match(new TokenStream(tokensWithTarget, 0));
		TokenRuleMatch negatedPositiveMatch = negatedPositive.match(new TokenStream(tokensWithTarget, 0));
		
		assertNull(negativeMatch);
		assertNull(negatedPositiveMatch);
		
		List<Token> tokensWithOther = List.of(other);
		
		TokenRuleMatch negativeMatchOther = negativeLookahead.match(new TokenStream(tokensWithOther, 0));
		TokenRuleMatch negatedPositiveMatchOther = negatedPositive.match(new TokenStream(tokensWithOther, 0));
		
		assertNotNull(negativeMatchOther);
		assertNotNull(negatedPositiveMatchOther);
		assertEquals(negativeMatchOther.startIndex(), negatedPositiveMatchOther.startIndex());
		assertEquals(negativeMatchOther.endIndex(), negatedPositiveMatchOther.endIndex());
		assertEquals(negativeMatchOther.matchedTokens(), negatedPositiveMatchOther.matchedTokens());
	}
	
	@Test
	void negatedNegativeLookaheadBehavesLikePositiveLookahead() {
		TokenRule innerRule = createRule("target");
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookaheadTokenRule negativeLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		TokenRule negatedNegative = negativeLookahead.not();
		
		Token target = createToken("target");
		Token other = createToken("other");
		
		List<Token> tokensWithTarget = List.of(target);
		
		TokenRuleMatch positiveMatch = positiveLookahead.match(new TokenStream(tokensWithTarget, 0));
		TokenRuleMatch negatedNegativeMatch = negatedNegative.match(new TokenStream(tokensWithTarget, 0));
		
		assertNotNull(positiveMatch);
		assertNotNull(negatedNegativeMatch);
		assertEquals(positiveMatch.startIndex(), negatedNegativeMatch.startIndex());
		assertEquals(positiveMatch.endIndex(), negatedNegativeMatch.endIndex());
		assertEquals(positiveMatch.matchedTokens(), negatedNegativeMatch.matchedTokens());
		
		List<Token> tokensWithOther = List.of(other);
		
		TokenRuleMatch positiveMatchOther = positiveLookahead.match(new TokenStream(tokensWithOther, 0));
		TokenRuleMatch negatedNegativeMatchOther = negatedNegative.match(new TokenStream(tokensWithOther, 0));
		
		assertNull(positiveMatchOther);
		assertNull(negatedNegativeMatchOther);
	}
	
	@Test
	void negatedRuleConsistentBehavior() {
		TokenRule innerRule = createRule("after");
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenRule negatedRule = positiveLookahead.not();
		
		List<Token> tokens = List.of(createToken("test"));
		
		TokenRuleMatch match1 = negatedRule.match(new TokenStream(tokens, 0));
		TokenRuleMatch match2 = negatedRule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
	}
	
	@Test
	void negatedRuleWithNullTokenStream() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenRule negatedRule = positiveLookahead.not();
		
		assertThrows(NullPointerException.class, () -> negatedRule.match(null));
	}
	
	@Test
	void negatedRuleDoesNotConsumeTokens() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenRule negatedRule = positiveLookahead.not();
		
		Token other = createToken("other");
		List<Token> tokens = List.of(other);
		TokenStream stream = new TokenStream(tokens, 0);
		
		TokenRuleMatch match = negatedRule.match(stream);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void negatedRuleWithComplexInnerRule() {
		TokenRule sequence = TokenRules.sequence(createRule("a"), createRule("b"));
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(sequence, LookMatchMode.POSITIVE);
		TokenRule negatedRule = positiveLookahead.not();
		
		Token a = createToken("a");
		Token b = createToken("b");
		Token c = createToken("c");
		
		List<Token> matchingTokens = List.of(a, b, c);
		TokenStream matchingStream = new TokenStream(matchingTokens, 0);
		
		assertNotNull(positiveLookahead.match(matchingStream));
		assertNull(negatedRule.match(matchingStream));
		
		List<Token> nonMatchingTokens = List.of(a, c, b);
		TokenStream nonMatchingStream = new TokenStream(nonMatchingTokens, 0);
		
		assertNull(positiveLookahead.match(nonMatchingStream));
		assertNotNull(negatedRule.match(nonMatchingStream));
	}
	
	@Test
	void negatedRuleWithEmptyTokenStream() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule positiveLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookaheadTokenRule negativeLookahead = new LookaheadTokenRule(innerRule, LookMatchMode.NEGATIVE);
		TokenRule negatedPositive = positiveLookahead.not();
		TokenRule negatedNegative = negativeLookahead.not();
		
		TokenStream emptyStream = new TokenStream(Collections.emptyList(), 0);
		
		assertNull(positiveLookahead.match(emptyStream));
		assertNotNull(negativeLookahead.match(emptyStream));
		
		assertNotNull(negatedPositive.match(emptyStream));
		assertNull(negatedNegative.match(emptyStream));
	}
	
	@Test
	void equalRulesHaveSameHashCode() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule rule1 = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookaheadTokenRule rule2 = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		TokenRule innerRule = createRule("test");
		LookaheadTokenRule lookahead = new LookaheadTokenRule(innerRule, LookMatchMode.POSITIVE);
		String ruleString = lookahead.toString();
		
		assertTrue(ruleString.contains("LookaheadTokenRule"));
		assertTrue(ruleString.contains("POSITIVE"));
	}
}
