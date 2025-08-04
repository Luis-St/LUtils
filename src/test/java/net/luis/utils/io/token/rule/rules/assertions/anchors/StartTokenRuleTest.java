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

package net.luis.utils.io.token.rule.rules.assertions.anchors;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.rule.rules.assertions.anchors.StartTokenRule;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StartTokenRule}.<br>
 *
 * @author Luis-St
 */
class StartTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void instanceIsSingleton() {
		assertSame(StartTokenRule.INSTANCE, StartTokenRule.INSTANCE);
	}
	
	@Test
	void matchWithNullTokenList() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void matchWithEmptyTokenListAtStartIndex() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		
		TokenRuleMatch match = rule.match(Collections.emptyList(), 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchAtStartIndex() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token token = createToken("first");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchAtNonStartIndex() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token first = createToken("first");
		Token second = createToken("second");
		List<Token> tokens = List.of(first, second);
		
		assertNull(rule.match(tokens, 1));
	}
	
	@Test
	void matchWithMultipleTokensOnlyMatchesAtStart() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token token1 = createToken("token1");
		Token token2 = createToken("token2");
		Token token3 = createToken("token3");
		List<Token> tokens = List.of(token1, token2, token3);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		
		assertNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 2));
	}
	
	@Test
	void matchWithNegativeIndex() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertNull(rule.match(tokens, -1));
		assertNull(rule.match(tokens, -5));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		assertNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 5));
		assertNull(rule.match(tokens, 100));
	}
	
	@Test
	void matchDoesNotConsumeTokens() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token first = createToken("first");
		Token second = createToken("second");
		List<Token> tokens = List.of(first, second);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithDifferentTokenTypes() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token text = createToken("text");
		Token number = createToken("123");
		Token symbol = createToken("!");
		Token space = createToken(" ");
		List<Token> tokens = List.of(text, number, symbol, space);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithSingleToken() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token single = createToken("single");
		List<Token> tokens = List.of(single);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithLargeTokenList() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		List<Token> largeList = IntStream.range(0, 100).mapToObj(i -> createToken("token" + i)).toList();
		
		TokenRuleMatch match = rule.match(largeList, 0);
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
		
		for (int i = 1; i < largeList.size(); i++) {
			assertNull(rule.match(largeList, i));
		}
	}
	
	@Test
	void matchWithEmptyValueTokens() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token empty = createToken("");
		List<Token> tokens = List.of(empty);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchWithSpecialCharacterTokens() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token tab = createToken("\t");
		Token newline = createToken("\n");
		Token backslash = createToken("\\");
		List<Token> tokens = List.of(tab, newline, backslash);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertTrue(match.matchedTokens().isEmpty());
	}
	
	@Test
	void matchResultsAreConsistent() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match1 = rule.match(tokens, 0);
		TokenRuleMatch match2 = rule.match(tokens, 0);
		
		assertNotNull(match1);
		assertNotNull(match2);
		assertEquals(match1.startIndex(), match2.startIndex());
		assertEquals(match1.endIndex(), match2.endIndex());
		assertEquals(match1.matchedTokens(), match2.matchedTokens());
		assertSame(match1.matchingTokenRule(), match2.matchingTokenRule());
	}
	
	@Test
	void matchOnlyAtExactStartPosition() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		List<Token> tokens = IntStream.range(0, 10).mapToObj(i -> createToken("token" + i)).toList();
		
		for (int i = 0; i < tokens.size(); i++) {
			if (i == 0) {
				TokenRuleMatch match = rule.match(tokens, i);
				assertNotNull(match);
				assertTrue(match.matchedTokens().isEmpty());
			} else {
				assertNull(rule.match(tokens, i));
			}
		}
	}
	
	@Test
	void matchWithRepeatedCalls() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		for (int i = 0; i < 100; i++) {
			TokenRuleMatch match = rule.match(tokens, 0);
			assertNotNull(match);
			assertTrue(match.matchedTokens().isEmpty());
		}
	}
	
	@Test
	void singletonPattern() {
		StartTokenRule rule1 = StartTokenRule.INSTANCE;
		StartTokenRule rule2 = StartTokenRule.INSTANCE;
		
		assertSame(rule1, rule2);
		assertEquals(rule1.hashCode(), rule2.hashCode());
	}
	
	@Test
	void factoryMethodReturnsInstance() {
		assertSame(StartTokenRule.INSTANCE, TokenRules.start());
	}
	
	@Test
	void equalInstancesHaveSameHashCode() {
		assertEquals(StartTokenRule.INSTANCE.hashCode(), StartTokenRule.INSTANCE.hashCode());
	}
	
	@Test
	void toStringContainsClassName() {
		String ruleString = StartTokenRule.INSTANCE.toString();
		
		assertTrue(ruleString.contains("StartTokenRule"));
	}
	
	@Test
	void matchBehaviorIndependentOfTokenContent() {
		StartTokenRule rule = StartTokenRule.INSTANCE;
		Token unicode = createToken("🚀");
		Token longText = createToken("verylongtokenvalue");
		Token number = createToken("42");
		
		List<Token> unicodeTokens = List.of(unicode);
		TokenRuleMatch unicodeMatch = rule.match(unicodeTokens, 0);
		assertNotNull(unicodeMatch);
		assertTrue(unicodeMatch.matchedTokens().isEmpty());
		
		List<Token> longTokens = List.of(longText);
		TokenRuleMatch longMatch = rule.match(longTokens, 0);
		assertNotNull(longMatch);
		assertTrue(longMatch.matchedTokens().isEmpty());
		
		List<Token> numberTokens = List.of(number);
		TokenRuleMatch numberMatch = rule.match(numberTokens, 0);
		assertNotNull(numberMatch);
		assertTrue(numberMatch.matchedTokens().isEmpty());
	}
}
