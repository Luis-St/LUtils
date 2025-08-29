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
 * Test class for {@link LookbehindTokenRule}.<br>
 *
 * @author Luis-St
 */
class LookbehindTokenRuleTest {
	
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
		assertThrows(NullPointerException.class, () -> new LookbehindTokenRule(null, LookMatchMode.POSITIVE));
		assertThrows(NullPointerException.class, () -> new LookbehindTokenRule(null, LookMatchMode.NEGATIVE));
	}
	
	@Test
	void constructorWithNullMode() {
		TokenRule innerRule = createRule("test");
		
		assertThrows(NullPointerException.class, () -> new LookbehindTokenRule(innerRule, null));
	}
	
	@Test
	void constructorWithValidTokenRule() {
		TokenRule innerRule = createRule("test");
		
		assertDoesNotThrow(() -> new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE));
		assertDoesNotThrow(() -> new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE));
	}
	
	@Test
	void tokenRuleReturnsCorrectRule() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		assertEquals(innerRule, lookbehind.tokenRule());
	}
	
	@Test
	void positiveReturnsCorrectValue() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookbehindTokenRule negativeLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		assertEquals(LookMatchMode.POSITIVE, positiveLookbehind.mode());
		assertEquals(LookMatchMode.NEGATIVE, negativeLookbehind.mode());
	}
	
	@Test
	void matchWithNullTokenStream() {
		LookbehindTokenRule rule = new LookbehindTokenRule(createRule("test"), LookMatchMode.POSITIVE);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyTokenStream() {
		LookbehindTokenRule rule = new LookbehindTokenRule(createRule("test"), LookMatchMode.POSITIVE);
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		LookbehindTokenRule rule = new LookbehindTokenRule(createRule("test"), LookMatchMode.POSITIVE);
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 1)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void matchAtStartWithPositiveLookbehind() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token first = createToken("first");
		List<Token> tokens = List.of(first);
		
		assertNull(lookbehind.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchAtStartWithNegativeLookbehind() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		Token first = createToken("first");
		List<Token> tokens = List.of(first);
		
		TokenRuleMatch match = lookbehind.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void positiveLookbehindWithMatchingPrevious() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token before = createToken("before");
		Token current = createToken("current");
		List<Token> tokens = List.of(before, current);
		
		TokenRuleMatch match = lookbehind.match(new TokenStream(tokens, 1));
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void positiveLookbehindWithNonMatchingPrevious() {
		TokenRule innerRule = createRule("expected");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token other = createToken("other");
		Token current = createToken("current");
		List<Token> tokens = List.of(other, current);
		
		assertNull(lookbehind.match(new TokenStream(tokens, 1)));
	}
	
	@Test
	void negativeLookbehindWithMatchingPrevious() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		Token before = createToken("before");
		Token current = createToken("current");
		List<Token> tokens = List.of(before, current);
		
		assertNull(lookbehind.match(new TokenStream(tokens, 1)));
	}
	
	@Test
	void negativeLookbehindWithNonMatchingPrevious() {
		TokenRule innerRule = createRule("expected");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		Token other = createToken("other");
		Token current = createToken("current");
		List<Token> tokens = List.of(other, current);
		
		TokenRuleMatch match = lookbehind.match(new TokenStream(tokens, 1));
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lookbehindDoesNotConsumeTokens() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token before = createToken("before");
		Token current = createToken("current");
		List<Token> tokens = List.of(before, current);
		
		TokenRuleMatch match = lookbehind.match(new TokenStream(tokens, 1));
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lookbehindWithMultipleTokens() {
		TokenRule innerRule = createRule("first");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token first = createToken("first");
		Token second = createToken("second");
		Token third = createToken("third");
		List<Token> tokens = List.of(first, second, third);
		
		TokenRuleMatch matchAtSecond = lookbehind.match(new TokenStream(tokens, 1));
		assertNotNull(matchAtSecond);
		assertTrue(matchAtSecond.matchedTokens().isEmpty());
		
		assertNull(lookbehind.match(new TokenStream(tokens, 2)));
	}
	
	@Test
	void lookbehindWithComplexRule() {
		TokenRule sequence = TokenRules.sequence(createRule("a"), createRule("b"));
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(sequence, LookMatchMode.POSITIVE);
		Token b = createToken("b");
		Token a = createToken("a");
		Token current = createToken("current");
		List<Token> tokens = List.of(b, a, current);
		
		TokenRuleMatch match = lookbehind.match(new TokenStream(tokens, 2));
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lookbehindWithLongerSequence() {
		TokenRule innerRule = createRule("target");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token one = createToken("one");
		Token two = createToken("two");
		Token target = createToken("target");
		Token four = createToken("four");
		Token five = createToken("five");
		List<Token> tokens = List.of(one, two, target, four, five);
		
		assertNull(lookbehind.match(new TokenStream(tokens, 1)));
		assertNull(lookbehind.match(new TokenStream(tokens, 2)));
		
		TokenRuleMatch match = lookbehind.match(new TokenStream(tokens, 3));
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		
		assertNull(lookbehind.match(new TokenStream(tokens, 4)));
	}
	
	@Test
	void lookbehindWithAlwaysMatchRule() {
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(TokenRules.alwaysMatch(), LookMatchMode.POSITIVE);
		Token first = createToken("first");
		Token second = createToken("second");
		List<Token> tokens = List.of(first, second);
		
		assertNull(lookbehind.match(new TokenStream(tokens, 0)));
		
		TokenRuleMatch match = lookbehind.match(new TokenStream(tokens, 1));
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void lookbehindWithNeverMatchRule() {
		TokenRule neverMatch = (stream) -> null;
		
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(neverMatch, LookMatchMode.POSITIVE);
		LookbehindTokenRule negativeLookbehind = new LookbehindTokenRule(neverMatch, LookMatchMode.NEGATIVE);
		Token first = createToken("first");
		Token second = createToken("second");
		List<Token> tokens = List.of(first, second);
		
		assertNull(positiveLookbehind.match(new TokenStream(tokens, 1)));
		
		TokenRuleMatch negativeMatch = negativeLookbehind.match(new TokenStream(tokens, 1));
		assertNotNull(negativeMatch);
		assertTrue(negativeMatch.matchedTokens().isEmpty());
	}
	
	@Test
	void lookbehindAtDifferentPositions() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token before = createToken("before");
		Token other = createToken("other");
		Token current1 = createToken("current1");
		Token current2 = createToken("current2");
		List<Token> tokens = List.of(before, other, current1, current2);
		
		assertNull(lookbehind.match(new TokenStream(tokens, 0)));
		
		TokenRuleMatch matchAfterBefore = lookbehind.match(new TokenStream(tokens, 1));
		assertNotNull(matchAfterBefore);
		
		assertNull(lookbehind.match(new TokenStream(tokens, 2)));
		assertNull(lookbehind.match(new TokenStream(tokens, 3)));
	}
	
	@Test
	void lookbehindWithReversedSequence() {
		TokenRule innerRule = createRule("second");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token first = createToken("first");
		Token second = createToken("second");
		Token third = createToken("third");
		List<Token> tokens = List.of(first, second, third);
		
		assertNull(lookbehind.match(new TokenStream(tokens, 1)));
		
		TokenRuleMatch match = lookbehind.match(new TokenStream(tokens, 2));
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchResultsAreConsistent() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		Token before = createToken("before");
		Token current = createToken("current");
		List<Token> tokens = List.of(before, current);
		
		TokenRuleMatch match1 = lookbehind.match(new TokenStream(tokens, 1));
		TokenRuleMatch match2 = lookbehind.match(new TokenStream(tokens, 1));
		
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
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookbehindTokenRule negativeLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenRule negatedPositive = positiveLookbehind.not();
		TokenRule negatedNegative = negativeLookbehind.not();
		
		assertNotNull(negatedPositive);
		assertNotNull(negatedNegative);
		assertNotSame(positiveLookbehind, negatedPositive);
		assertNotSame(negativeLookbehind, negatedNegative);
		
		assertEquals(LookMatchMode.NEGATIVE, assertInstanceOf(LookbehindTokenRule.class, negatedPositive).mode());
		assertEquals(LookMatchMode.POSITIVE, assertInstanceOf(LookbehindTokenRule.class, negatedNegative).mode());
	}
	
	@Test
	void doubleNegationReturnsOriginalRule() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookbehindTokenRule negativeLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		
		TokenRule negatedPositive = positiveLookbehind.not();
		TokenRule doubleNegatedPositive = negatedPositive.not();
		
		TokenRule negatedNegative = negativeLookbehind.not();
		TokenRule doubleNegatedNegative = negatedNegative.not();
		
		assertEquals(LookMatchMode.POSITIVE, assertInstanceOf(LookbehindTokenRule.class, doubleNegatedPositive).mode());
		assertEquals(LookMatchMode.NEGATIVE, assertInstanceOf(LookbehindTokenRule.class, doubleNegatedNegative).mode());
		assertEquals(innerRule, ((LookbehindTokenRule) doubleNegatedPositive).tokenRule());
		assertEquals(innerRule, ((LookbehindTokenRule) doubleNegatedNegative).tokenRule());
	}
	
	@Test
	void negatedPositiveLookbehindBehavesLikeNegativeLookbehind() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookbehindTokenRule negativeLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		TokenRule negatedPositive = positiveLookbehind.not();
		
		Token before = createToken("before");
		Token other = createToken("other");
		Token current = createToken("current");
		
		List<Token> tokensWithBefore = List.of(before, current);
		
		TokenRuleMatch negativeMatch = negativeLookbehind.match(new TokenStream(tokensWithBefore, 1));
		TokenRuleMatch negatedPositiveMatch = negatedPositive.match(new TokenStream(tokensWithBefore, 1));
		
		assertNotNull(negativeMatch);
		assertNotNull(negatedPositiveMatch);
		assertEquals(negativeMatch.startIndex(), negatedPositiveMatch.startIndex());
		assertEquals(negativeMatch.endIndex(), negatedPositiveMatch.endIndex());
		assertEquals(negativeMatch.matchedTokens(), negatedPositiveMatch.matchedTokens());
		
		List<Token> tokensWithOther = List.of(other, current);
		
		TokenRuleMatch negativeMatchOther = negativeLookbehind.match(new TokenStream(tokensWithOther, 1));
		TokenRuleMatch negatedPositiveMatchOther = negatedPositive.match(new TokenStream(tokensWithOther, 1));
		
		assertNotNull(negativeMatchOther);
		assertNotNull(negatedPositiveMatchOther);
		assertEquals(negativeMatchOther.startIndex(), negatedPositiveMatchOther.startIndex());
		assertEquals(negativeMatchOther.endIndex(), negatedPositiveMatchOther.endIndex());
		assertEquals(negativeMatchOther.matchedTokens(), negatedPositiveMatchOther.matchedTokens());
	}
	
	@Test
	void negatedNegativeLookbehindBehavesLikePositiveLookbehind() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookbehindTokenRule negativeLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		TokenRule negatedNegative = negativeLookbehind.not();
		
		Token before = createToken("before");
		Token other = createToken("other");
		Token current = createToken("current");
		
		List<Token> tokensWithBefore = List.of(before, current);
		TokenStream streamWithBefore = new TokenStream(tokensWithBefore, 1);
		
		TokenRuleMatch positiveMatch = positiveLookbehind.match(streamWithBefore);
		TokenRuleMatch negatedNegativeMatch = negatedNegative.match(streamWithBefore);
		
		if (positiveMatch == null) {
			assertNull(negatedNegativeMatch);
		} else {
			assertNotNull(negatedNegativeMatch);
			assertEquals(positiveMatch.startIndex(), negatedNegativeMatch.startIndex());
			assertEquals(positiveMatch.endIndex(), negatedNegativeMatch.endIndex());
			assertEquals(positiveMatch.matchedTokens(), negatedNegativeMatch.matchedTokens());
		}
		
		List<Token> tokensWithOther = List.of(other, current);
		TokenStream streamWithOther = new TokenStream(tokensWithOther, 1);
		
		TokenRuleMatch positiveMatchOther = positiveLookbehind.match(streamWithOther);
		TokenRuleMatch negatedNegativeMatchOther = negatedNegative.match(streamWithOther);
		
		if (positiveMatchOther == null) {
			assertNull(negatedNegativeMatchOther);
		} else {
			assertNotNull(negatedNegativeMatchOther);
			assertEquals(positiveMatchOther.startIndex(), negatedNegativeMatchOther.startIndex());
			assertEquals(positiveMatchOther.endIndex(), negatedNegativeMatchOther.endIndex());
			assertEquals(positiveMatchOther.matchedTokens(), negatedNegativeMatchOther.matchedTokens());
		}
	}
	
	@Test
	void negatedRuleConsistentBehavior() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenRule negatedRule = positiveLookbehind.not();
		
		Token other = createToken("other");
		Token current = createToken("current");
		List<Token> tokens = List.of(other, current);
		TokenStream stream1 = new TokenStream(tokens, 1);
		TokenStream stream2 = new TokenStream(tokens, 1);
		
		TokenRuleMatch match1 = negatedRule.match(stream1);
		TokenRuleMatch match2 = negatedRule.match(stream2);
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
	}
	
	@Test
	void negatedRuleWithNullTokenStream() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenRule negatedRule = positiveLookbehind.not();
		
		assertThrows(NullPointerException.class, () -> negatedRule.match(null));
	}
	
	@Test
	void negatedRuleDoesNotConsumeTokens() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		TokenRule negatedRule = positiveLookbehind.not();
		
		Token other = createToken("other");
		Token current = createToken("current");
		List<Token> tokens = List.of(other, current);
		TokenStream stream = new TokenStream(tokens, 1);
		
		TokenRuleMatch match = negatedRule.match(stream);
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void negatedRuleWithComplexInnerRule() {
		TokenRule sequence = TokenRules.sequence(createRule("a"), createRule("b"));
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(sequence, LookMatchMode.POSITIVE);
		TokenRule negatedRule = positiveLookbehind.not();
		
		Token b = createToken("b");
		Token a = createToken("a");
		Token c = createToken("c");
		Token current = createToken("current");
		
		List<Token> matchingTokens = List.of(b, a, current);
		TokenStream matchingStream = new TokenStream(matchingTokens, 2);
		
		assertNotNull(positiveLookbehind.match(matchingStream));
		assertNull(negatedRule.match(matchingStream));
		
		List<Token> nonMatchingTokens = List.of(c, b, current);
		TokenStream nonMatchingStream = new TokenStream(nonMatchingTokens, 2);
		
		assertNull(positiveLookbehind.match(nonMatchingStream));
		assertNotNull(negatedRule.match(nonMatchingStream));
	}
	
	@Test
	void negatedRuleWithEmptyTokenStream() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookbehindTokenRule negativeLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		TokenRule negatedPositive = positiveLookbehind.not();
		TokenRule negatedNegative = negativeLookbehind.not();
		
		TokenStream emptyStream = new TokenStream(Collections.emptyList(), 0);
		
		assertNull(positiveLookbehind.match(emptyStream));
		assertNull(negativeLookbehind.match(emptyStream));
		
		assertNull(negatedPositive.match(emptyStream));
		assertNull(negatedNegative.match(emptyStream));
	}
	
	@Test
	void negatedRuleAtStartPosition() {
		TokenRule innerRule = createRule("before");
		LookbehindTokenRule positiveLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookbehindTokenRule negativeLookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.NEGATIVE);
		TokenRule negatedPositive = positiveLookbehind.not();
		TokenRule negatedNegative = negativeLookbehind.not();
		
		Token first = createToken("first");
		List<Token> tokens = List.of(first);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertNull(positiveLookbehind.match(stream));
		assertNotNull(negativeLookbehind.match(stream));
		
		assertNotNull(negatedPositive.match(stream));
		assertNull(negatedNegative.match(stream));
	}
	
	@Test
	void equalRulesHaveSameHashCode() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule rule1 = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		LookbehindTokenRule rule2 = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		TokenRule innerRule = createRule("test");
		LookbehindTokenRule lookbehind = new LookbehindTokenRule(innerRule, LookMatchMode.POSITIVE);
		String ruleString = lookbehind.toString();
		
		assertTrue(ruleString.contains("LookbehindTokenRule"));
		assertTrue(ruleString.contains("POSITIVE"));
	}
}
