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
 * Test class for {@link FilterTokenAction}.<br>
 *
 * @author Luis-St
 */
class FilterTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void constructorWithNullFilter() {
		assertThrows(NullPointerException.class, () -> new FilterTokenAction(null));
	}
	
	@Test
	void constructorWithValidFilter() {
		Predicate<Token> filter = token -> token.value().length() > 2;
		
		assertDoesNotThrow(() -> new FilterTokenAction(filter));
	}
	
	@Test
	void filterReturnsCorrectPredicate() {
		Predicate<Token> filter = token -> token.value().startsWith("test");
		FilterTokenAction action = new FilterTokenAction(filter);
		
		assertEquals(filter, action.filter());
	}
	
	@Test
	void applyWithNullMatch() {
		FilterTokenAction action = new FilterTokenAction(token -> true);
		
		assertThrows(NullPointerException.class, () -> action.apply(null));
	}
	
	@Test
	void applyWithEmptyTokenList() {
		FilterTokenAction action = new FilterTokenAction(token -> true);
		TokenRuleMatch match = new TokenRuleMatch(0, 0, Collections.emptyList(), TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("test")));
	}
	
	@Test
	void applyWithAlwaysTruePredicate() {
		FilterTokenAction action = new FilterTokenAction(token -> true);
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
	void applyWithAlwaysFalsePredicate() {
		FilterTokenAction action = new FilterTokenAction(token -> false);
		Token token1 = createToken("first");
		Token token2 = createToken("second");
		List<Token> tokens = List.of(token1, token2);
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithLengthFilter() {
		FilterTokenAction action = new FilterTokenAction(token -> token.value().length() > 3);
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
	void applyWithPrefixFilter() {
		FilterTokenAction action = new FilterTokenAction(token -> token.value().startsWith("test"));
		Token matching1 = createToken("test1");
		Token nonMatching1 = createToken("other1");
		Token matching2 = createToken("testing");
		Token nonMatching2 = createToken("data");
		List<Token> tokens = List.of(matching1, nonMatching1, matching2, nonMatching2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(matching1, result.get(0));
		assertEquals(matching2, result.get(1));
	}
	
	@Test
	void applyWithNumericFilter() {
		FilterTokenAction action = new FilterTokenAction(token -> token.value().matches("\\d+"));
		Token number1 = createToken("123");
		Token text1 = createToken("abc");
		Token number2 = createToken("456");
		Token text2 = createToken("def");
		List<Token> tokens = List.of(number1, text1, number2, text2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(number1, result.get(0));
		assertEquals(number2, result.get(1));
	}
	
	@Test
	void applyWithSingleTokenMatching() {
		FilterTokenAction action = new FilterTokenAction(token -> "target".equals(token.value()));
		Token target = createToken("target");
		List<Token> tokens = List.of(target);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(1, result.size());
		assertEquals(target, result.getFirst());
	}
	
	@Test
	void applyWithSingleTokenNotMatching() {
		FilterTokenAction action = new FilterTokenAction(token -> "target".equals(token.value()));
		Token other = createToken("other");
		List<Token> tokens = List.of(other);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithComplexFilter() {
		FilterTokenAction action = new FilterTokenAction(token ->
			token.value().length() >= 3 &&
				token.value().contains("a") &&
				!token.value().startsWith("bad")
		);
		
		Token good1 = createToken("cat");
		Token bad1 = createToken("no");
		Token good2 = createToken("bat");
		Token bad2 = createToken("badcat");
		Token bad3 = createToken("bet");
		Token good3 = createToken("day");
		
		List<Token> tokens = List.of(good1, bad1, good2, bad2, bad3, good3);
		TokenRuleMatch match = new TokenRuleMatch(0, 6, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals(good1, result.get(0));
		assertEquals(good2, result.get(1));
		assertEquals(good3, result.get(2));
	}
	
	@Test
	void applyWithEmptyStringFilter() {
		FilterTokenAction action = new FilterTokenAction(token -> !token.value().isEmpty());
		Token empty = createToken("");
		Token nonEmpty1 = createToken("a");
		Token nonEmpty2 = createToken("test");
		List<Token> tokens = List.of(empty, nonEmpty1, nonEmpty2);
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(nonEmpty1, result.get(0));
		assertEquals(nonEmpty2, result.get(1));
	}
	
	@Test
	void applyWithSpecialCharacterFilter() {
		FilterTokenAction action = new FilterTokenAction(token -> token.value().matches("[!@#$%^&*()]+"));
		Token special1 = createToken("!!!");
		Token normal1 = createToken("text");
		Token special2 = createToken("@#$");
		Token normal2 = createToken("123");
		List<Token> tokens = List.of(special1, normal1, special2, normal2);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(2, result.size());
		assertEquals(special1, result.get(0));
		assertEquals(special2, result.get(1));
	}
	
	@Test
	void applyResultIsUnmodifiable() {
		FilterTokenAction action = new FilterTokenAction(token -> true);
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
		FilterTokenAction action = new FilterTokenAction(token -> token.value().length() == 1);
		Token a = createToken("a");
		Token bb = createToken("bb");
		Token c = createToken("c");
		Token dd = createToken("dd");
		Token e = createToken("e");
		List<Token> tokens = List.of(a, bb, c, dd, e);
		TokenRuleMatch match = new TokenRuleMatch(0, 5, tokens, TokenRules.alwaysMatch());
		
		List<Token> result = action.apply(match);
		
		assertEquals(3, result.size());
		assertEquals(a, result.get(0));
		assertEquals(c, result.get(1));
		assertEquals(e, result.get(2));
	}
	
	@Test
	void equalActionsHaveSameHashCode() {
		Predicate<Token> filter = token -> token.value().length() > 5;
		FilterTokenAction action1 = new FilterTokenAction(filter);
		FilterTokenAction action2 = new FilterTokenAction(filter);
		
		assertEquals(action1.hashCode(), action2.hashCode());
	}
	
	@Test
	void toStringContainsFilterInfo() {
		FilterTokenAction action = new FilterTokenAction(token -> true);
		String actionString = action.toString();
		
		assertTrue(actionString.contains("FilterTokenAction"));
		assertTrue(actionString.contains("filter"));
	}
}
