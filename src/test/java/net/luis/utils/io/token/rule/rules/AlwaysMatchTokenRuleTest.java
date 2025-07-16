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

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AlwaysMatchTokenRule}.<br>
 *
 * @author Luis-St
 */
class AlwaysMatchTokenRuleTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void matchWithNullTokenList() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		assertThrows(NullPointerException.class, () -> rule.match(null, 0));
	}
	
	@Test
	void matchWithEmptyTokenList() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		
		assertNull(rule.match(Collections.emptyList(), 0));
	}
	
	@Test
	void matchWithIndexOutOfBounds() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		List<Token> tokens = List.of(createToken("test"));
		
		assertNull(rule.match(tokens, 1));
		assertNull(rule.match(tokens, 5));
		assertNull(rule.match(tokens, -1));
	}
	
	@Test
	void matchWithValidIndex() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(token, match.matchedTokens().getFirst());
		assertSame(rule, match.matchingTokenRule());
	}
	
	@Test
	void matchWithFirstTokenInList() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		Token first = createToken("first");
		Token second = createToken("second");
		List<Token> tokens = List.of(first, second);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(first, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithSecondTokenInList() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		Token first = createToken("first");
		Token second = createToken("second");
		List<Token> tokens = List.of(first, second);
		
		TokenRuleMatch match = rule.match(tokens, 1);
		
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(2, match.endIndex());
		assertEquals(1, match.matchedTokens().size());
		assertEquals(second, match.matchedTokens().getFirst());
	}
	
	@Test
	void matchWithDifferentTokenTypes() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		Token text = createToken("text");
		Token number = createToken("123");
		Token symbol = createToken("!");
		List<Token> tokens = List.of(text, number, symbol);
		
		TokenRuleMatch textMatch = rule.match(tokens, 0);
		TokenRuleMatch numberMatch = rule.match(tokens, 1);
		TokenRuleMatch symbolMatch = rule.match(tokens, 2);
		
		assertNotNull(textMatch);
		assertEquals(text, textMatch.matchedTokens().getFirst());
		
		assertNotNull(numberMatch);
		assertEquals(number, numberMatch.matchedTokens().getFirst());
		
		assertNotNull(symbolMatch);
		assertEquals(symbol, symbolMatch.matchedTokens().getFirst());
	}
	
	@Test
	void matchAlwaysMatchesExactlyOneToken() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		Token token1 = createToken("token1");
		Token token2 = createToken("token2");
		Token token3 = createToken("token3");
		List<Token> tokens = List.of(token1, token2, token3);
		
		for (int i = 0; i < tokens.size(); i++) {
			TokenRuleMatch match = rule.match(tokens, i);
			assertNotNull(match);
			assertEquals(i, match.startIndex());
			assertEquals(i + 1, match.endIndex());
			assertEquals(1, match.matchedTokens().size());
			assertEquals(tokens.get(i), match.matchedTokens().getFirst());
		}
	}
	
	@Test
	void matchWithLargeTokenList() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		List<Token> largeList = IntStream.range(0, 100).mapToObj(i -> createToken("token" + i)).toList();
		
		TokenRuleMatch firstMatch = rule.match(largeList, 0);
		TokenRuleMatch middleMatch = rule.match(largeList, 50);
		TokenRuleMatch lastMatch = rule.match(largeList, 99);
		
		assertNotNull(firstMatch);
		assertEquals(0, firstMatch.startIndex());
		assertEquals(1, firstMatch.endIndex());
		
		assertNotNull(middleMatch);
		assertEquals(50, middleMatch.startIndex());
		assertEquals(51, middleMatch.endIndex());
		
		assertNotNull(lastMatch);
		assertEquals(99, lastMatch.startIndex());
		assertEquals(100, lastMatch.endIndex());
	}
	
	@Test
	void matchWithSingleCharacterTokens() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		Token a = createToken("a");
		Token b = createToken("b");
		Token c = createToken("c");
		List<Token> tokens = List.of(a, b, c);
		
		TokenRuleMatch matchA = rule.match(tokens, 0);
		TokenRuleMatch matchB = rule.match(tokens, 1);
		TokenRuleMatch matchC = rule.match(tokens, 2);
		
		assertNotNull(matchA);
		assertEquals("a", matchA.matchedTokens().getFirst().value());
		assertNotNull(matchB);
		assertEquals("b", matchB.matchedTokens().getFirst().value());
		assertNotNull(matchC);
		assertEquals("c", matchC.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithEmptyValueTokens() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		Token emptyToken = createToken("");
		List<Token> tokens = List.of(emptyToken);
		
		TokenRuleMatch match = rule.match(tokens, 0);
		
		assertNotNull(match);
		assertEquals(emptyToken, match.matchedTokens().getFirst());
		assertEquals("", match.matchedTokens().getFirst().value());
	}
	
	@Test
	void matchWithSpecialCharacterTokens() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		Token space = createToken(" ");
		Token tab = createToken("\t");
		Token newline = createToken("\n");
		Token backslash = createToken("\\");
		List<Token> tokens = List.of(space, tab, newline, backslash);
		
		for (int i = 0; i < tokens.size(); i++) {
			TokenRuleMatch match = rule.match(tokens, i);
			assertNotNull(match);
			assertEquals(tokens.get(i), match.matchedTokens().getFirst());
		}
	}
	
	@Test
	void matchResultsAreConsistent() {
		AlwaysMatchTokenRule rule = AlwaysMatchTokenRule.INSTANCE;
		Token token = createToken("consistent");
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
	void equalInstancesHaveSameHashCode() {
		assertEquals(AlwaysMatchTokenRule.INSTANCE.hashCode(), AlwaysMatchTokenRule.INSTANCE.hashCode());
	}
}
