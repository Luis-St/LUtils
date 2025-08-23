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

package net.luis.utils.io.token.rule.rules.matchers;

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LengthTokenRule}.<br>
 *
 * @author Luis-St
 */
class LengthTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNegativeMinLength() {
		assertThrows(IllegalArgumentException.class, () -> new LengthTokenRule(-1, 5));
	}
	
	@Test
	void constructorWithNegativeMaxLength() {
		assertThrows(IllegalArgumentException.class, () -> new LengthTokenRule(0, -1));
	}
	
	@Test
	void constructorWithMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> new LengthTokenRule(5, 3));
	}
	
	@Test
	void constructorWithValidParameters() {
		assertDoesNotThrow(() -> new LengthTokenRule(0, 0));
		assertDoesNotThrow(() -> new LengthTokenRule(1, 1));
		assertDoesNotThrow(() -> new LengthTokenRule(0, 10));
		assertDoesNotThrow(() -> new LengthTokenRule(5, 15));
	}
	
	@Test
	void exactLengthWithNegativeLength() {
		assertThrows(IllegalArgumentException.class, () -> new LengthTokenRule(-1, -1));
	}
	
	@Test
	void exactLengthWithValidLength() {
		LengthTokenRule rule = new LengthTokenRule(5, 5);
		
		assertEquals(5, rule.minLength());
		assertEquals(5, rule.maxLength());
	}
	
	@Test
	void exactLengthWithZero() {
		LengthTokenRule rule = new LengthTokenRule(0, 0);
		
		assertEquals(0, rule.minLength());
		assertEquals(0, rule.maxLength());
	}
	
	@Test
	void minLengthReturnsCorrectValue() {
		LengthTokenRule rule = new LengthTokenRule(3, 7);
		
		assertEquals(3, rule.minLength());
	}
	
	@Test
	void maxLengthReturnsCorrectValue() {
		LengthTokenRule rule = new LengthTokenRule(3, 7);
		
		assertEquals(7, rule.maxLength());
	}
	
	@Test
	void matchWithNullTokenStream() {
		LengthTokenRule rule = new LengthTokenRule(1, 5);
		
		assertThrows(NullPointerException.class, () -> rule.match((TokenStream) null));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		LengthTokenRule rule = new LengthTokenRule(1, 5);
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		LengthTokenRule rule = new LengthTokenRule(1, 5);
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void matchWithTokenTooShort() {
		LengthTokenRule rule = new LengthTokenRule(3, 8);
		Token shortToken = createToken("hi");
		List<Token> tokens = List.of(shortToken);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchWithTokenTooLong() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		Token longToken = createToken("verylongtoken");
		List<Token> tokens = List.of(longToken);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchWithTokenAtMinLength() {
		LengthTokenRule rule = new LengthTokenRule(3, 8);
		Token token = createToken("abc");
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
	void matchWithTokenAtMaxLength() {
		LengthTokenRule rule = new LengthTokenRule(1, 5);
		Token token = createToken("hello");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(token, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithTokenInRange() {
		LengthTokenRule rule = new LengthTokenRule(2, 10);
		Token token = createToken("middle");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(token, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithExactLengthRule() {
		LengthTokenRule rule = new LengthTokenRule(4, 4);
		Token exactToken = createToken("test");
		Token shortToken = createToken("hi");
		Token longToken = createToken("toolong");
		
		List<Token> exactTokens = List.of(exactToken);
		TokenRuleMatch exactMatch = rule.match(new TokenStream(exactTokens, 0));
		assertNotNull(exactMatch);
		assertEquals(exactToken, exactMatch.matchedTokens().getFirst());
		
		List<Token> shortTokens = List.of(shortToken);
		assertNull(rule.match(new TokenStream(shortTokens, 0)));
		
		List<Token> longTokens = List.of(longToken);
		assertNull(rule.match(new TokenStream(longTokens, 0)));
	}
	
	@Test
	void matchWithEmptyToken() {
		LengthTokenRule rule = new LengthTokenRule(0, 5);
		Token emptyToken = createToken("");
		List<Token> tokens = List.of(emptyToken);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(emptyToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithEmptyTokenAndMinLengthOne() {
		LengthTokenRule rule = new LengthTokenRule(1, 5);
		Token emptyToken = createToken("");
		List<Token> tokens = List.of(emptyToken);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchWithSingleCharacterToken() {
		LengthTokenRule rule = new LengthTokenRule(1, 1);
		Token singleChar = createToken("a");
		List<Token> tokens = List.of(singleChar);
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match);
		assertEquals(singleChar, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithMultipleTokensInList() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		Token valid1 = createToken("abc");
		Token invalid = createToken("toolongtoken");
		Token valid2 = createToken("hello");
		List<Token> tokens = List.of(valid1, invalid, valid2);
		
		TokenRuleMatch match1 = rule.match(new TokenStream(tokens, 0));
		assertNotNull(match1);
		assertEquals(valid1, match1.matchedTokens().getFirst());
		
		assertNull(rule.match(new TokenStream(tokens, 1)));
		
		TokenRuleMatch match3 = rule.match(new TokenStream(tokens, 2));
		assertNotNull(match3);
		assertEquals(valid2, match3.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithSpecialCharacters() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		Token space = createToken(" ");
		Token tab = createToken("\t");
		Token newline = createToken("\n");
		Token symbols = createToken("!@#");
		List<Token> tokens = List.of(space, tab, newline, symbols);
		
		for (int i = 0; i < tokens.size(); i++) {
			TokenRuleMatch match = rule.match(new TokenStream(tokens, i));
			assertNotNull(match);
			assertEquals(tokens.get(i), match.matchedTokens().getFirst());
		}
	}
	
	@Test
	void matchWithUnicodeCharacters() {
		LengthTokenRule rule = new LengthTokenRule(1, 10);
		Token unicode = createToken("héllo");
		Token emoji = createToken("😀");
		List<Token> tokens = List.of(unicode, emoji);
		
		TokenRuleMatch unicodeMatch = rule.match(new TokenStream(tokens, 0));
		assertNotNull(unicodeMatch);
		assertEquals(unicode, unicodeMatch.matchedTokens().getFirst());
		
		TokenRuleMatch emojiMatch = rule.match(new TokenStream(tokens, 1));
		assertNotNull(emojiMatch);
		assertEquals(emoji, emojiMatch.matchedTokens().getFirst());
	}
	
	@Test
	void matchAlwaysMatchesExactlyOneToken() {
		LengthTokenRule rule = new LengthTokenRule(1, 10);
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		Token token3 = createToken("third");
		List<Token> tokens = List.of(token1, token2, token3);
		
		for (int i = 0; i < tokens.size(); i++) {
			TokenRuleMatch match = rule.match(new TokenStream(tokens, i));
			assertNotNull(match);
			assertEquals(i, match.startIndex());
			assertEquals(i + 1, match.endIndex());
			assertEquals(1, match.matchedTokens().size());
		}
	}
	
	@Test
	void matchWithBoundaryLengths() {
		LengthTokenRule rule = new LengthTokenRule(0, Integer.MAX_VALUE);
		Token empty = createToken("");
		Token veryLong = createToken("a".repeat(1000));
		List<Token> tokens = List.of(empty, veryLong);
		
		TokenRuleMatch emptyMatch = rule.match(new TokenStream(tokens, 0));
		assertNotNull(emptyMatch);
		assertEquals(empty, emptyMatch.matchedTokens().getFirst());
		
		TokenRuleMatch longMatch = rule.match(new TokenStream(tokens, 1));
		assertNotNull(longMatch);
		assertEquals(veryLong, longMatch.matchedTokens().getFirst());
	}
	
	@Test
	void matchResultsAreConsistent() {
		LengthTokenRule rule = new LengthTokenRule(2, 6);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match1 = rule.match(new TokenStream(tokens, 0));
		TokenRuleMatch match2 = rule.match(new TokenStream(tokens, 0));
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
		assertSame(match1.matchingTokenRule(), match2.matchingTokenRule());
	}
	
	@Test
	void notReturnsNegatedRule() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		
		TokenRule negatedRule = rule.not();
		
		assertNotNull(negatedRule);
		assertNotSame(rule, negatedRule);
	}
	
	@Test
	void notNegatesMatchingLogic() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		TokenRule negatedRule = rule.not();
		
		Token matchingToken = createToken("test");
		Token nonMatchingToken = createToken("hi");
		
		assertNotNull(rule.match(new TokenStream(List.of(matchingToken), 0)));
		assertNull(rule.match(new TokenStream(List.of(nonMatchingToken), 0)));
		
		assertNull(negatedRule.match(new TokenStream(List.of(matchingToken), 0)));
		assertNotNull(negatedRule.match(new TokenStream(List.of(nonMatchingToken), 0)));
	}
	
	@Test
	void notDoubleNegationReturnsOriginal() {
		LengthTokenRule originalRule = new LengthTokenRule(2, 6);
		
		TokenRule negatedRule = originalRule.not();
		TokenRule doubleNegatedRule = negatedRule.not();
		
		assertSame(originalRule, doubleNegatedRule);
	}
	
	@Test
	void notWithExactLengthRule() {
		LengthTokenRule exactRule = new LengthTokenRule(4, 4);
		TokenRule negatedRule = exactRule.not();
		
		Token exactToken = createToken("test");
		Token shortToken = createToken("hi");
		Token longToken = createToken("toolong");
		
		assertNotNull(exactRule.match(new TokenStream(List.of(exactToken), 0)));
		assertNull(exactRule.match(new TokenStream(List.of(shortToken), 0)));
		assertNull(exactRule.match(new TokenStream(List.of(longToken), 0)));
		
		assertNull(negatedRule.match(new TokenStream(List.of(exactToken), 0)));
		assertNotNull(negatedRule.match(new TokenStream(List.of(shortToken), 0)));
		assertNotNull(negatedRule.match(new TokenStream(List.of(longToken), 0)));
	}
	
	@Test
	void notWithZeroLengthRule() {
		LengthTokenRule zeroRule = new LengthTokenRule(0, 0);
		TokenRule negatedRule = zeroRule.not();
		
		Token emptyToken = createToken("");
		Token nonEmptyToken = createToken("content");
		
		assertNotNull(zeroRule.match(new TokenStream(List.of(emptyToken), 0)));
		assertNull(zeroRule.match(new TokenStream(List.of(nonEmptyToken), 0)));
		
		assertNull(negatedRule.match(new TokenStream(List.of(emptyToken), 0)));
		assertNotNull(negatedRule.match(new TokenStream(List.of(nonEmptyToken), 0)));
	}
	
	@Test
	void notWithMinLengthOnlyRule() {
		LengthTokenRule rule = new LengthTokenRule(3, Integer.MAX_VALUE);
		TokenRule negatedRule = rule.not();
		
		Token shortToken = createToken("hi");
		Token exactToken = createToken("abc");
		Token longToken = createToken("verylongtoken");
		
		assertNull(rule.match(new TokenStream(List.of(shortToken), 0)));
		assertNotNull(rule.match(new TokenStream(List.of(exactToken), 0)));
		assertNotNull(rule.match(new TokenStream(List.of(longToken), 0)));
		
		assertNotNull(negatedRule.match(new TokenStream(List.of(shortToken), 0)));
		assertNull(negatedRule.match(new TokenStream(List.of(exactToken), 0)));
		assertNull(negatedRule.match(new TokenStream(List.of(longToken), 0)));
	}
	
	@Test
	void notConsistencyAcrossMultipleCalls() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		TokenRule negatedRule = rule.not();
		
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match1 = negatedRule.match(new TokenStream(tokens, 0));
		TokenRuleMatch match2 = negatedRule.match(new TokenStream(tokens, 0));
		
		assertNull(match1);
		assertNull(match2);
	}
	
	@Test
	void notNegatedRuleConsumesToken() {
		LengthTokenRule rule = new LengthTokenRule(5, 10);
		TokenRule negatedRule = rule.not();
		
		Token shortToken = createToken("hi");
		List<Token> tokens = List.of(shortToken);
		TokenStream stream = new TokenStream(tokens, 0);
		
		assertEquals(0, stream.getCurrentIndex());
		TokenRuleMatch match = negatedRule.match(stream);
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
		assertEquals(1, match.endIndex());
		assertEquals(shortToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void notNegatedRuleHasCorrectMatchInfo() {
		LengthTokenRule rule = new LengthTokenRule(10, 20);
		TokenRule negatedRule = rule.not();
		
		Token token = createToken("short");
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
	void notWithSpecialCharacterTokens() {
		LengthTokenRule rule = new LengthTokenRule(1, 1);
		TokenRule negatedRule = rule.not();
		
		Token singleChar = createToken("a");
		Token multiChar = createToken("abc");
		Token empty = createToken("");
		
		assertNotNull(rule.match(new TokenStream(List.of(singleChar), 0)));
		assertNull(rule.match(new TokenStream(List.of(multiChar), 0)));
		assertNull(rule.match(new TokenStream(List.of(empty), 0)));
		
		assertNull(negatedRule.match(new TokenStream(List.of(singleChar), 0)));
		assertNotNull(negatedRule.match(new TokenStream(List.of(multiChar), 0)));
		assertNotNull(negatedRule.match(new TokenStream(List.of(empty), 0)));
	}
	
	@Test
	void equalRulesHaveSameHashCode() {
		LengthTokenRule rule1 = new LengthTokenRule(3, 7);
		LengthTokenRule rule2 = new LengthTokenRule(3, 7);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringContainsLengthInfo() {
		LengthTokenRule rule = new LengthTokenRule(5, 10);
		String ruleString = rule.toString();
		
		assertTrue(ruleString.contains("LengthTokenRule"));
		assertTrue(ruleString.contains("5"));
		assertTrue(ruleString.contains("10"));
	}
}
