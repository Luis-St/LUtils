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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NegatableTokenRule}.<br>
 *
 * @author Luis-St
 */
class NegatableTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	private static @NotNull NegatableTokenRule createAlwaysTrueRule() {
		return token -> true;
	}
	
	private static @NotNull NegatableTokenRule createAlwaysFalseRule() {
		return token -> false;
	}
	
	private static @NotNull NegatableTokenRule createLengthRule(int exactLength) {
		return token -> token.value().length() == exactLength;
	}
	
	private static @NotNull NegatableTokenRule createContainsRule() {
		return token -> token.value().contains("test");
	}
	
	@Test
	void defaultMatchStreamWithNullTokenStream() {
		NegatableTokenRule rule = createAlwaysTrueRule();
		
		assertThrows(NullPointerException.class, () -> rule.match((TokenStream) null));
	}
	
	@Test
	void defaultMatchStreamWithEmptyTokenList() {
		NegatableTokenRule rule = createAlwaysTrueRule();
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void defaultMatchStreamWithIndexOutOfBounds() {
		NegatableTokenRule rule = createAlwaysTrueRule();
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void defaultMatchStreamWithMatchingToken() {
		NegatableTokenRule rule = createAlwaysTrueRule();
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(token, match.matchedTokens().getFirst());
		assertSame(rule, match.matchingTokenRule());
	}
	
	@Test
	void defaultMatchStreamWithNonMatchingToken() {
		NegatableTokenRule rule = createAlwaysFalseRule();
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void defaultMatchStreamUsesTokenMatchMethod() {
		NegatableTokenRule lengthRule = createLengthRule(4);
		Token matchingToken = createToken("test");
		Token nonMatchingToken = createToken("hi");
		
		List<Token> matchingTokens = List.of(matchingToken);
		TokenRuleMatch match = lengthRule.match(new TokenStream(matchingTokens, 0));
		assertNotNull(match);
		assertEquals(matchingToken, match.matchedTokens().getFirst());
		
		List<Token> nonMatchingTokens = List.of(nonMatchingToken);
		assertNull(lengthRule.match(new TokenStream(nonMatchingTokens, 0)));
	}
	
	@Test
	void defaultMatchStreamConsumesToken() {
		NegatableTokenRule rule = createAlwaysTrueRule();
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertEquals(0, stream.getCurrentIndex());
		TokenRuleMatch match = rule.match(stream);
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
		assertEquals(1, match.endIndex());
	}
	
	@Test
	void defaultMatchStreamAdvancesCorrectly() {
		NegatableTokenRule rule = createAlwaysTrueRule();
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens = List.of(token1, token2);
		TokenStream stream = new TokenStream(tokens, 0);
		
		TokenRuleMatch firstMatch = rule.match(stream);
		assertNotNull(firstMatch);
		assertEquals(token1, firstMatch.matchedTokens().getFirst());
		assertEquals(1, stream.getCurrentIndex());
		
		TokenRuleMatch secondMatch = rule.match(stream);
		assertNotNull(secondMatch);
		assertEquals(token2, secondMatch.matchedTokens().getFirst());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void defaultNotCreateNegatedRule() {
		NegatableTokenRule rule = createAlwaysTrueRule();
		
		TokenRule negatedRule = rule.not();
		
		assertNotNull(negatedRule);
		assertNotSame(rule, negatedRule);
	}
	
	@Test
	void defaultNotNegatesMatchingLogic() {
		NegatableTokenRule alwaysTrueRule = createAlwaysTrueRule();
		NegatableTokenRule alwaysFalseRule = createAlwaysFalseRule();
		
		TokenRule negatedTrue = alwaysTrueRule.not();
		TokenRule negatedFalse = alwaysFalseRule.not();
		
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertNotNull(alwaysTrueRule.match(new TokenStream(tokens, 0)));
		assertNull(negatedTrue.match(new TokenStream(tokens, 0)));
		
		assertNull(alwaysFalseRule.match(new TokenStream(tokens, 0)));
		assertNotNull(negatedFalse.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void defaultNotNegatedRuleConsumesToken() {
		NegatableTokenRule alwaysFalseRule = createAlwaysFalseRule();
		TokenRule negatedRule = alwaysFalseRule.not();
		
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertEquals(0, stream.getCurrentIndex());
		TokenRuleMatch match = negatedRule.match(stream);
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
		assertEquals(1, match.endIndex());
		assertEquals(token, match.matchedTokens().getFirst());
	}
	
	@Test
	void defaultNotDoubleNegationReturnsOriginal() {
		NegatableTokenRule originalRule = createAlwaysTrueRule();
		
		TokenRule negatedRule = originalRule.not();
		TokenRule doubleNegatedRule = negatedRule.not();
		
		assertSame(originalRule, doubleNegatedRule);
	}
	
	@Test
	void defaultNotTripleNegationWorks() {
		NegatableTokenRule originalRule = createAlwaysTrueRule();
		
		TokenRule negatedRule = originalRule.not();
		TokenRule doubleNegatedRule = negatedRule.not();
		TokenRule tripleNegatedRule = doubleNegatedRule.not();
		
		assertSame(originalRule, doubleNegatedRule);
		assertNotSame(originalRule, tripleNegatedRule);
		assertNotSame(negatedRule, tripleNegatedRule);
	}
	
	@Test
	void defaultNotWithComplexRule() {
		NegatableTokenRule containsRule = createContainsRule();
		TokenRule negatedRule = containsRule.not();
		
		Token matchingToken = createToken("testing");
		Token nonMatchingToken = createToken("hello");
		
		assertNotNull(containsRule.match(new TokenStream(List.of(matchingToken), 0)));
		assertNull(containsRule.match(new TokenStream(List.of(nonMatchingToken), 0)));
		
		assertNull(negatedRule.match(new TokenStream(List.of(matchingToken), 0)));
		assertNotNull(negatedRule.match(new TokenStream(List.of(nonMatchingToken), 0)));
	}
	
	@Test
	void defaultNotWithMultipleTokensInStream() {
		NegatableTokenRule lengthRule = createLengthRule(3);
		TokenRule negatedRule = lengthRule.not();
		
		Token matchingToken = createToken("abc");
		Token nonMatchingToken = createToken("abcd");
		List<Token> tokens = List.of(matchingToken, nonMatchingToken);
		
		assertNotNull(lengthRule.match(new TokenStream(tokens, 0)));
		assertNull(lengthRule.match(new TokenStream(tokens, 1)));
		
		assertNull(negatedRule.match(new TokenStream(tokens, 0)));
		assertNotNull(negatedRule.match(new TokenStream(tokens, 1)));
	}
	
	@Test
	void defaultNotNegatedRuleHasCorrectMatchInfo() {
		NegatableTokenRule alwaysFalseRule = createAlwaysFalseRule();
		TokenRule negatedRule = alwaysFalseRule.not();
		
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = negatedRule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(token, match.matchedTokens().getFirst());
		assertSame(negatedRule, match.matchingTokenRule());
	}
	
	@Test
	void defaultNotWithEmptyTokenValue() {
		NegatableTokenRule emptyRule = token -> token.value().isEmpty();
		TokenRule negatedRule = emptyRule.not();
		
		Token emptyToken = createToken("");
		Token nonEmptyToken = createToken("content");
		
		assertNotNull(emptyRule.match(new TokenStream(List.of(emptyToken), 0)));
		assertNull(emptyRule.match(new TokenStream(List.of(nonEmptyToken), 0)));
		
		assertNull(negatedRule.match(new TokenStream(List.of(emptyToken), 0)));
		assertNotNull(negatedRule.match(new TokenStream(List.of(nonEmptyToken), 0)));
	}
	
	@Test
	void defaultNotWithSpecialCharacters() {
		NegatableTokenRule specialCharRule = token -> token.value().matches("\\W+");
		TokenRule negatedRule = specialCharRule.not();
		
		Token specialToken = createToken("!@#");
		Token alphanumericToken = createToken("abc123");
		
		assertNotNull(specialCharRule.match(new TokenStream(List.of(specialToken), 0)));
		assertNull(specialCharRule.match(new TokenStream(List.of(alphanumericToken), 0)));
		
		assertNull(negatedRule.match(new TokenStream(List.of(specialToken), 0)));
		assertNotNull(negatedRule.match(new TokenStream(List.of(alphanumericToken), 0)));
	}
	
	@Test
	void defaultNotConsistencyAcrossMultipleCalls() {
		NegatableTokenRule rule = createLengthRule(5);
		TokenRule negatedRule = rule.not();
		
		Token matchingToken = createToken("hello");
		Token nonMatchingToken = createToken("hi");
		List<Token> tokens = List.of(matchingToken, nonMatchingToken);
		
		assertNull(negatedRule.match(new TokenStream(tokens, 0)));
		assertNull(negatedRule.match(new TokenStream(tokens, 0)));
		
		assertNotNull(negatedRule.match(new TokenStream(tokens, 1)));
		assertNotNull(negatedRule.match(new TokenStream(tokens, 1)));
	}
	
	@Test
	void defaultNotNegatedRuleWorksWithEmptyStream() {
		NegatableTokenRule rule = createAlwaysTrueRule();
		TokenRule negatedRule = rule.not();
		
		assertNull(negatedRule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void defaultNotNegatedRuleThrowsWithNullStream() {
		NegatableTokenRule rule = createAlwaysTrueRule();
		TokenRule negatedRule = rule.not();
		
		assertThrows(NullPointerException.class, () -> negatedRule.match(null));
	}
	
	@Test
	void defaultNotWithCaseInsensitiveRule() {
		NegatableTokenRule caseInsensitiveRule = token -> "hello".equalsIgnoreCase(token.value());
		TokenRule negatedRule = caseInsensitiveRule.not();
		
		Token lowerToken = createToken("hello");
		Token upperToken = createToken("HELLO");
		Token otherToken = createToken("world");
		
		assertNotNull(caseInsensitiveRule.match(new TokenStream(List.of(lowerToken), 0)));
		assertNotNull(caseInsensitiveRule.match(new TokenStream(List.of(upperToken), 0)));
		assertNull(caseInsensitiveRule.match(new TokenStream(List.of(otherToken), 0)));
		
		assertNull(negatedRule.match(new TokenStream(List.of(lowerToken), 0)));
		assertNull(negatedRule.match(new TokenStream(List.of(upperToken), 0)));
		assertNotNull(negatedRule.match(new TokenStream(List.of(otherToken), 0)));
	}
	
	@Test
	void defaultNotAlternatingNegations() {
		NegatableTokenRule originalRule = createAlwaysTrueRule();
		
		TokenRule firstNegation = originalRule.not();
		TokenRule secondNegation = firstNegation.not();
		TokenRule thirdNegation = secondNegation.not();
		TokenRule fourthNegation = thirdNegation.not();
		
		assertSame(originalRule, secondNegation);
		assertSame(originalRule, fourthNegation);
		assertNotSame(originalRule, firstNegation);
		assertNotSame(originalRule, thirdNegation);
	}
	
	@Test
	void defaultNotPreservesOriginalRuleBehavior() {
		NegatableTokenRule lengthRule = createLengthRule(4);
		TokenRule doubleNegated = lengthRule.not().not();
		
		Token matchingToken = createToken("test");
		Token nonMatchingToken = createToken("hello world");
		
		TokenRuleMatch originalMatch = lengthRule.match(new TokenStream(List.of(matchingToken), 0));
		TokenRuleMatch doubleNegatedMatch = doubleNegated.match(new TokenStream(List.of(matchingToken), 0));
		
		assertNotNull(originalMatch);
		assertNotNull(doubleNegatedMatch);
		assertEquals(originalMatch.startIndex(), doubleNegatedMatch.startIndex());
		assertEquals(originalMatch.endIndex(), doubleNegatedMatch.endIndex());
		assertEquals(originalMatch.matchedTokens(), doubleNegatedMatch.matchedTokens());
		
		assertNull(lengthRule.match(new TokenStream(List.of(nonMatchingToken), 0)));
		assertNull(doubleNegated.match(new TokenStream(List.of(nonMatchingToken), 0)));
	}
	
	@Test
	void functionalInterfaceWorksWithLambda() {
		NegatableTokenRule lambdaRule = token -> token.value().startsWith("prefix");
		
		Token matchingToken = createToken("prefixTest");
		Token nonMatchingToken = createToken("test");
		
		assertTrue(lambdaRule.match(matchingToken));
		assertFalse(lambdaRule.match(nonMatchingToken));
		
		assertNotNull(lambdaRule.match(new TokenStream(List.of(matchingToken), 0)));
		assertNull(lambdaRule.match(new TokenStream(List.of(nonMatchingToken), 0)));
	}
	
	@Test
	void functionalInterfaceWorksWithMethodReference() {
		NegatableTokenRule methodRefRule = token -> token.value().matches("\\d+");
		
		Token numericToken = createToken("123");
		Token nonNumericToken = createToken("abc");
		
		assertTrue(methodRefRule.match(numericToken));
		assertFalse(methodRefRule.match(nonNumericToken));
		
		assertNotNull(methodRefRule.match(new TokenStream(List.of(numericToken), 0)));
		assertNull(methodRefRule.match(new TokenStream(List.of(nonNumericToken), 0)));
	}
}
