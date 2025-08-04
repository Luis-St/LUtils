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

package net.luis.utils.io.token.rule.actions.filters;

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRules;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SkipTokenAction}.<br>
 *
 * @author Luis-St
 */
class SkipTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullFilter() {
		assertThrows(NullPointerException.class, () -> new SkipTokenAction(null));
	}
	
	@Test
	void constructorWithValidFilter() {
		Predicate<Token> filter = token -> token.value().length() < 3;
		
		assertDoesNotThrow(() -> new SkipTokenAction(filter));
	}
	
	@Test
	void filterReturnsCorrectPredicate() {
		Predicate<Token> filter = token -> token.value().startsWith("skip");
		SkipTokenAction action = new SkipTokenAction(filter);
		
		assertEquals(filter, action.filter());
	}
	
	@Test
	void applyWithNullMatch() {
		SkipTokenAction action = new SkipTokenAction(token -> false);
		
		assertThrows(NullPointerException.class, () -> action.apply(null));
	}
	
	@Test
	void applyWithEmptyTokenList() {
		SkipTokenAction action = new SkipTokenAction(token -> true);
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("test")));
	}
	
	@Test
	void applyWithAlwaysTruePredicate() {
		SkipTokenAction action = new SkipTokenAction(token -> true);
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithAlwaysFalsePredicate() {
		SkipTokenAction action = new SkipTokenAction(token -> false);
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(token1, result.get(0));
		assertEquals(token2, result.get(1));
	}
	
	@Test
	void applyWithLengthSkipFilter() {
		SkipTokenAction action = new SkipTokenAction(token -> token.value().length() <= 2);
		Token short1 = createToken("hi");
		Token long1 = createToken("hello");
		Token short2 = createToken("no");
		Token long2 = createToken("world");
		List<Token> tokens = List.of(short1, long1, short2, long2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(long1, result.get(0));
		assertEquals(long2, result.get(1));
	}
	
	@Test
	void applyWithPrefixSkipFilter() {
		SkipTokenAction action = new SkipTokenAction(token -> token.value().startsWith("skip"));
		Token keep1 = createToken("keep1");
		Token skip1 = createToken("skip1");
		Token keep2 = createToken("keep2");
		Token skip2 = createToken("skipthis");
		List<Token> tokens = List.of(keep1, skip1, keep2, skip2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(keep1, result.get(0));
		assertEquals(keep2, result.get(1));
	}
	
	@Test
	void applyWithNumericSkipFilter() {
		SkipTokenAction action = new SkipTokenAction(token -> token.value().matches("\\d+"));
		Token text1 = createToken("abc");
		Token number1 = createToken("123");
		Token text2 = createToken("def");
		Token number2 = createToken("456");
		List<Token> tokens = List.of(text1, number1, text2, number2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(text1, result.get(0));
		assertEquals(text2, result.get(1));
	}
	
	@Test
	void applyWithSingleTokenToSkip() {
		SkipTokenAction action = new SkipTokenAction(token -> token.value().equals("skip"));
		Token skip = createToken("skip");
		List<Token> tokens = List.of(skip);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithSingleTokenToKeep() {
		SkipTokenAction action = new SkipTokenAction(token -> token.value().equals("skip"));
		Token keep = createToken("keep");
		List<Token> tokens = List.of(keep);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals(keep, result.getFirst());
	}
	
	@Test
	void applyWithComplexSkipFilter() {
		SkipTokenAction action = new SkipTokenAction(token ->
			token.value().length() < 3 ||
				token.value().contains("bad") ||
				token.value().matches("\\d+")
		);
		
		Token keep1 = createToken("good");
		Token skip1 = createToken("no");
		Token keep2 = createToken("excellent");
		Token skip2 = createToken("badword");
		Token skip3 = createToken("123");
		Token keep3 = createToken("perfect");
		
		List<Token> tokens = List.of(keep1, skip1, keep2, skip2, skip3, keep3);
		TokenRuleMatch match = new TokenRuleMatch(0, 6, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals(keep1, result.getFirst());
		assertEquals(keep2, result.get(1));
		assertEquals(keep3, result.get(2));
	}
	
	@Test
	void applyWithEmptyStringSkipFilter() {
		SkipTokenAction action = new SkipTokenAction(token -> token.value().isEmpty());
		Token empty = createToken("");
		Token nonEmpty1 = createToken("a");
		Token nonEmpty2 = createToken("test");
		List<Token> tokens = List.of(empty, nonEmpty1, nonEmpty2);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(nonEmpty1, result.getFirst());
		assertEquals(nonEmpty2, result.get(1));
	}
	
	@Test
	void applyWithWhitespaceSkipFilter() {
		SkipTokenAction action = new SkipTokenAction(token -> token.value().trim().isEmpty());
		Token space = createToken(" ");
		Token tab = createToken("\t");
		Token newline = createToken("\n");
		Token text = createToken("text");
		List<Token> tokens = List.of(space, tab, text, newline);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals(text, result.getFirst());
	}
	
	@Test
	void applyWithSpecialCharacterSkipFilter() {
		SkipTokenAction action = new SkipTokenAction(token -> token.value().matches("[!@#$%^&*()]+"));
		Token normal1 = createToken("text");
		Token special1 = createToken("!!!");
		Token normal2 = createToken("123");
		Token special2 = createToken("@#$");
		List<Token> tokens = List.of(normal1, special1, normal2, special2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(normal1, result.getFirst());
		assertEquals(normal2, result.get(1));
	}
	
	@Test
	void applyResultIsUnmodifiable() {
		SkipTokenAction action = new SkipTokenAction(token -> false);
		Token token = createToken("test");
		List<Token> tokens = List.of(token);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
		assertThrows(UnsupportedOperationException.class, result::removeFirst);
		assertThrows(UnsupportedOperationException.class, result::clear);
	}
	
	@Test
	void applyPreservesTokenOrder() {
		SkipTokenAction action = new SkipTokenAction(token -> token.value().length() == 2);
		Token a = createToken("a");
		Token bb = createToken("bb");
		Token c = createToken("c");
		Token dd = createToken("dd");
		Token e = createToken("e");
		List<Token> tokens = List.of(a, bb, c, dd, e);
		TokenRuleMatch match = new TokenRuleMatch(0, 5, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals(a, result.getFirst());
		assertEquals(c, result.get(1));
		assertEquals(e, result.get(2));
	}
	
	@Test
	void applyWithAlternatingSkipKeepPattern() {
		SkipTokenAction action = new SkipTokenAction(token -> token.value().startsWith("s"));
		Token keep1 = createToken("keep1");
		Token skip1 = createToken("skip1");
		Token keep2 = createToken("keep2");
		Token skip2 = createToken("skip2");
		Token keep3 = createToken("keep3");
		List<Token> tokens = List.of(keep1, skip1, keep2, skip2, keep3);
		TokenRuleMatch match = new TokenRuleMatch(0, 5, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals(keep1, result.getFirst());
		assertEquals(keep2, result.get(1));
		assertEquals(keep3, result.get(2));
	}
	
	@Test
	void skipActionIsInverseOfFilter() {
		Predicate<Token> keepShort = token -> token.value().length() <= 3;
		FilterTokenAction filterAction = new FilterTokenAction(keepShort);
		SkipTokenAction skipAction = new SkipTokenAction(keepShort);
		
		Token short1 = createToken("hi");
		Token long1 = createToken("hello");
		Token short2 = createToken("bye");
		Token long2 = createToken("world");
		List<Token> tokens = List.of(short1, long1, short2, long2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> filterResult = filterAction.apply(match);
		List<Token> skipResult = skipAction.apply(match);
		
		assertEquals(2, filterResult.size());
		assertEquals(2, skipResult.size());
		
		assertEquals(short1, filterResult.getFirst());
		assertEquals(short2, filterResult.get(1));
		
		assertEquals(long1, skipResult.getFirst());
		assertEquals(long2, skipResult.get(1));
	}
	
	@Test
	void equalActionsHaveSameHashCode() {
		Predicate<Token> filter = token -> token.value().length() < 5;
		SkipTokenAction action1 = new SkipTokenAction(filter);
		SkipTokenAction action2 = new SkipTokenAction(filter);
		
		assertEquals(action1.hashCode(), action2.hashCode());
	}
	
	@Test
	void toStringContainsFilterInfo() {
		SkipTokenAction action = new SkipTokenAction(token -> true);
		String actionString = action.toString();
		
		assertTrue(actionString.contains("SkipTokenAction"));
		assertTrue(actionString.contains("filter"));
	}
}
