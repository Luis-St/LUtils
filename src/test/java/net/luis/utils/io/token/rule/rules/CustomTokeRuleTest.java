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
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CustomTokeRule}.<br>
 *
 * @author Luis-St
 */
class CustomTokeRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullCondition() {
		assertThrows(NullPointerException.class, () -> new CustomTokeRule(null));
	}
	
	@Test
	void constructorWithValidCondition() {
		Predicate<Token> condition = token -> token.value().equals("test");
		
		assertDoesNotThrow(() -> new CustomTokeRule(condition));
	}
	
	@Test
	void conditionReturnsCorrectPredicate() {
		Predicate<Token> condition = token -> token.value().startsWith("prefix");
		CustomTokeRule rule = new CustomTokeRule(condition);
		
		assertEquals(condition, rule.condition());
	}
	
	@Test
	void matchWithNullTokenStream() {
		CustomTokeRule rule = new CustomTokeRule(token -> true);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		CustomTokeRule rule = new CustomTokeRule(token -> true);
		
		assertNull(rule.match(new TokenStream(Collections.emptyList())));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		CustomTokeRule rule = new CustomTokeRule(token -> true);
		List<Token> tokens = List.of(createToken("test"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 1)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, 5)));
		assertThrows(IndexOutOfBoundsException.class, () -> rule.match(new TokenStream(tokens, -1)));
	}
	
	@Test
	void matchWithAlwaysTrueCondition() {
		CustomTokeRule rule = new CustomTokeRule(token -> true);
		Token token = createToken("anything");
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
	void matchWithAlwaysFalseCondition() {
		CustomTokeRule rule = new CustomTokeRule(token -> false);
		Token token = createToken("anything");
		List<Token> tokens = List.of(token);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
	}
	
	@Test
	void matchWithSpecificValueCondition() {
		CustomTokeRule rule = new CustomTokeRule(token -> "target".equals(token.value()));
		Token matchingToken = createToken("target");
		Token nonMatchingToken = createToken("other");
		
		List<Token> matchingTokens = List.of(matchingToken);
		TokenRuleMatch match = rule.match(new TokenStream(matchingTokens, 0));
		assertNotNull(match);
		assertEquals(matchingToken, match.matchedTokens().getFirst());
		
		List<Token> nonMatchingTokens = List.of(nonMatchingToken);
		assertNull(rule.match(new TokenStream(nonMatchingTokens, 0)));
	}
	
	@Test
	void matchWithLengthCondition() {
		CustomTokeRule rule = new CustomTokeRule(token -> token.value().length() > 3);
		Token shortToken = createToken("hi");
		Token longToken = createToken("hello");
		
		List<Token> shortTokens = List.of(shortToken);
		assertNull(rule.match(new TokenStream(shortTokens, 0)));
		
		List<Token> longTokens = List.of(longToken);
		TokenRuleMatch match = rule.match(new TokenStream(longTokens, 0));
		assertNotNull(match);
		assertEquals(longToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithPatternCondition() {
		CustomTokeRule rule = new CustomTokeRule(token -> token.value().matches("\\d+"));
		Token numberToken = createToken("123");
		Token textToken = createToken("abc");
		Token mixedToken = createToken("12a");
		
		List<Token> numberTokens = List.of(numberToken);
		TokenRuleMatch numberMatch = rule.match(new TokenStream(numberTokens, 0));
		assertNotNull(numberMatch);
		assertEquals(numberToken, numberMatch.matchedTokens().getFirst());
		
		List<Token> textTokens = List.of(textToken);
		assertNull(rule.match(new TokenStream(textTokens, 0)));
		
		List<Token> mixedTokens = List.of(mixedToken);
		assertNull(rule.match(new TokenStream(mixedTokens, 0)));
	}
	
	@Test
	void matchWithPrefixCondition() {
		CustomTokeRule rule = new CustomTokeRule(token -> token.value().startsWith("pre"));
		Token prefixToken = createToken("prefix");
		Token preToken = createToken("pre");
		Token otherToken = createToken("other");
		
		List<Token> prefixTokens = List.of(prefixToken);
		TokenRuleMatch prefixMatch = rule.match(new TokenStream(prefixTokens, 0));
		assertNotNull(prefixMatch);
		assertEquals(prefixToken, prefixMatch.matchedTokens().getFirst());
		
		List<Token> preTokens = List.of(preToken);
		TokenRuleMatch preMatch = rule.match(new TokenStream(preTokens, 0));
		assertNotNull(preMatch);
		assertEquals(preToken, preMatch.matchedTokens().getFirst());
		
		List<Token> otherTokens = List.of(otherToken);
		assertNull(rule.match(new TokenStream(otherTokens, 0)));
	}
	
	@Test
	void matchWithSuffixCondition() {
		CustomTokeRule rule = new CustomTokeRule(token -> token.value().endsWith("ing"));
		Token runningToken = createToken("running");
		Token jumpingToken = createToken("jumping");
		Token walkToken = createToken("walk");
		
		List<Token> runningTokens = List.of(runningToken);
		TokenRuleMatch runningMatch = rule.match(new TokenStream(runningTokens, 0));
		assertNotNull(runningMatch);
		assertEquals(runningToken, runningMatch.matchedTokens().getFirst());
		
		List<Token> jumpingTokens = List.of(jumpingToken);
		TokenRuleMatch jumpingMatch = rule.match(new TokenStream(jumpingTokens, 0));
		assertNotNull(jumpingMatch);
		assertEquals(jumpingToken, jumpingMatch.matchedTokens().getFirst());
		
		List<Token> walkTokens = List.of(walkToken);
		assertNull(rule.match(new TokenStream(walkTokens, 0)));
	}
	
	@Test
	void matchWithEmptyTokenCondition() {
		CustomTokeRule rule = new CustomTokeRule(token -> token.value().isEmpty());
		Token emptyToken = createToken("");
		Token nonEmptyToken = createToken("content");
		
		List<Token> emptyTokens = List.of(emptyToken);
		TokenRuleMatch emptyMatch = rule.match(new TokenStream(emptyTokens, 0));
		assertNotNull(emptyMatch);
		assertEquals(emptyToken, emptyMatch.matchedTokens().getFirst());
		
		List<Token> nonEmptyTokens = List.of(nonEmptyToken);
		assertNull(rule.match(new TokenStream(nonEmptyTokens, 0)));
	}
	
	@Test
	void matchWithCaseInsensitiveCondition() {
		CustomTokeRule rule = new CustomTokeRule(token -> "hello".equalsIgnoreCase(token.value()));
		Token lowerToken = createToken("hello");
		Token upperToken = createToken("HELLO");
		Token mixedToken = createToken("HeLLo");
		Token otherToken = createToken("world");
		
		List<Token> lowerTokens = List.of(lowerToken);
		TokenRuleMatch lowerMatch = rule.match(new TokenStream(lowerTokens, 0));
		assertNotNull(lowerMatch);
		assertEquals(lowerToken, lowerMatch.matchedTokens().getFirst());
		
		List<Token> upperTokens = List.of(upperToken);
		TokenRuleMatch upperMatch = rule.match(new TokenStream(upperTokens, 0));
		assertNotNull(upperMatch);
		assertEquals(upperToken, upperMatch.matchedTokens().getFirst());
		
		List<Token> mixedTokens = List.of(mixedToken);
		TokenRuleMatch mixedMatch = rule.match(new TokenStream(mixedTokens, 0));
		assertNotNull(mixedMatch);
		assertEquals(mixedToken, mixedMatch.matchedTokens().getFirst());
		
		List<Token> otherTokens = List.of(otherToken);
		assertNull(rule.match(new TokenStream(otherTokens, 0)));
	}
	
	@Test
	void matchWithComplexCondition() {
		CustomTokeRule rule = new CustomTokeRule(token ->
			token.value().length() >= 3 &&
				token.value().contains("a") &&
				!token.value().startsWith("z")
		);
		
		Token validToken = createToken("cat");
		Token tooShortToken = createToken("at");
		Token noAToken = createToken("hello");
		Token startsWithZToken = createToken("zebra");
		
		List<Token> validTokens = List.of(validToken);
		TokenRuleMatch validMatch = rule.match(new TokenStream(validTokens, 0));
		assertNotNull(validMatch);
		assertEquals(validToken, validMatch.matchedTokens().getFirst());
		
		List<Token> tooShortTokens = List.of(tooShortToken);
		assertNull(rule.match(new TokenStream(tooShortTokens, 0)));
		
		List<Token> noATokens = List.of(noAToken);
		assertNull(rule.match(new TokenStream(noATokens, 0)));
		
		List<Token> startsWithZTokens = List.of(startsWithZToken);
		assertNull(rule.match(new TokenStream(startsWithZTokens, 0)));
	}
	
	@Test
	void matchWithMultipleTokensInList() {
		CustomTokeRule rule = new CustomTokeRule(token -> "match".equals(token.value()));
		Token matchToken = createToken("match");
		Token otherToken1 = createToken("other1");
		Token otherToken2 = createToken("other2");
		List<Token> tokens = List.of(otherToken1, matchToken, otherToken2);
		
		assertNull(rule.match(new TokenStream(tokens, 0)));
		
		TokenRuleMatch match = rule.match(new TokenStream(tokens, 1));
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(matchToken, match.matchedTokens().getFirst());
		
		assertNull(rule.match(new TokenStream(tokens, 2)));
	}
	
	@Test
	void matchAlwaysMatchesExactlyOneToken() {
		CustomTokeRule rule = new CustomTokeRule(token -> true);
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
			assertEquals(tokens.get(i), match.matchedTokens().getFirst());
		}
	}
	
	@Test
	void matchWithSpecialCharacters() {
		CustomTokeRule rule = new CustomTokeRule(token -> token.value().matches("[!@#$%^&*()]+"));
		Token symbolsToken = createToken("!@#");
		Token mixedToken = createToken("a!@#");
		Token textToken = createToken("hello");
		
		List<Token> symbolsTokens = List.of(symbolsToken);
		TokenRuleMatch symbolsMatch = rule.match(new TokenStream(symbolsTokens, 0));
		assertNotNull(symbolsMatch);
		assertEquals(symbolsToken, symbolsMatch.matchedTokens().getFirst());
		
		List<Token> mixedTokens = List.of(mixedToken);
		assertNull(rule.match(new TokenStream(mixedTokens, 0)));
		
		List<Token> textTokens = List.of(textToken);
		assertNull(rule.match(new TokenStream(textTokens, 0)));
	}
	
	@Test
	void matchWithUnicodeCharacters() {
		CustomTokeRule rule = new CustomTokeRule(token -> token.value().contains("ü"));
		Token unicodeToken = createToken("hüllo");
		Token regularToken = createToken("hello");
		
		List<Token> unicodeTokens = List.of(unicodeToken);
		TokenRuleMatch unicodeMatch = rule.match(new TokenStream(unicodeTokens, 0));
		assertNotNull(unicodeMatch);
		assertEquals(unicodeToken, unicodeMatch.matchedTokens().getFirst());
		
		List<Token> regularTokens = List.of(regularToken);
		assertNull(rule.match(new TokenStream(regularTokens, 0)));
	}
	
	@Test
	void matchResultsAreConsistent() {
		CustomTokeRule rule = new CustomTokeRule(token -> "test".equals(token.value()));
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
	void conditionIsAppliedOncePerMatch() {
		int[] callCount = {0};
		CustomTokeRule rule = new CustomTokeRule(token -> {
			callCount[0]++;
			return "test".equals(token.value());
		});
		Token testToken = createToken("test");
		Token otherToken = createToken("other");
		List<Token> tokens = List.of(testToken);
		
		rule.match(new TokenStream(tokens, 0));
		assertEquals(1, callCount[0]);
		
		List<Token> otherTokens = List.of(otherToken);
		rule.match(new TokenStream(otherTokens, 0));
		assertEquals(2, callCount[0]);
	}
	
	@Test
	void matchOnlyConsumesTokenWhenConditionMatches() {
		CustomTokeRule rule = new CustomTokeRule(token -> "match".equals(token.value()));
		Token matchToken = createToken("match");
		Token otherToken = createToken("other");
		List<Token> tokens = List.of(matchToken, otherToken);
		
		TokenStream stream = new TokenStream(tokens, 0);
		TokenRuleMatch match = rule.match(stream);
		
		assertNotNull(match);
		assertEquals(1, match.endIndex());
		assertEquals(matchToken, match.matchedTokens().getFirst());
	}
	
	@Test
	void equalRulesWithSameConditionHaveSameHashCode() {
		Predicate<Token> condition = token -> token.value().length() > 5;
		CustomTokeRule rule1 = new CustomTokeRule(condition);
		CustomTokeRule rule2 = new CustomTokeRule(condition);
		
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void toStringContainsRuleInfo() {
		CustomTokeRule rule = new CustomTokeRule(token -> "test".equals(token.value()));
		String ruleString = rule.toString();
		
		assertTrue(ruleString.contains("CustomTokeRule"));
	}
}
