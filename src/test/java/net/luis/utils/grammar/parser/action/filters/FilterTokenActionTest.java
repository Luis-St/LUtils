/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.grammar.parser.action.filters;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenActionContext;
import net.luis.utils.grammar.parser.rule.AlwaysMatchTokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.*;
import net.luis.utils.grammar.token.type.StandardTokenType;
import net.luis.utils.grammar.token.type.TokenType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FilterTokenAction}.<br>
 *
 * @author Luis-St
 */
class FilterTokenActionTest {
	
	private static final TokenActionContext CONTEXT = new TokenActionContext(TokenStream.createImmutable(List.of()));
	
	private static Token token(String value) {
		return new SimpleToken(value, TokenPosition.UNPOSITIONED);
	}
	
	private static TokenRuleMatch match(List<Token> tokens) {
		return new TokenRuleMatch(0, tokens.size(), tokens, AlwaysMatchTokenRule.INSTANCE);
	}
	
	@Test
	void constructWithValidFilter() {
		Predicate<Token> filter = t -> true;
		FilterTokenAction action = new FilterTokenAction(filter);
		assertNotNull(action.filter());
		assertEquals(filter, action.filter());
	}
	
	@Test
	void constructWithNullFilter() {
		assertThrows(NullPointerException.class, () -> new FilterTokenAction(null));
	}
	
	@Test
	void applyWithNullMatchThrows() {
		FilterTokenAction action = new FilterTokenAction(t -> true);
		assertThrows(NullPointerException.class, () -> action.apply(null, CONTEXT));
	}
	
	@Test
	void applyWithNullContextThrows() {
		FilterTokenAction action = new FilterTokenAction(t -> true);
		TokenRuleMatch match = match(List.of());
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyMatchedTokensReturnsEmptyList() {
		FilterTokenAction action = new FilterTokenAction(t -> true);
		TokenRuleMatch match = match(List.of());
		List<Token> result = action.apply(match, CONTEXT);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyKeepsMatchingToken() {
		Token a = token("a");
		FilterTokenAction action = new FilterTokenAction(t -> "a".equals(t.value()));
		TokenRuleMatch match = match(List.of(a));
		List<Token> result = action.apply(match, CONTEXT);
		assertEquals(List.of(a), result);
	}
	
	@Test
	void applyExcludesNonMatchingToken() {
		Token b = token("b");
		FilterTokenAction action = new FilterTokenAction(t -> "a".equals(t.value()));
		TokenRuleMatch match = match(List.of(b));
		List<Token> result = action.apply(match, CONTEXT);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithMultipleTokensKeepsOnlyMatchingOnesInOrder() {
		Token a = token("a");
		Token b = token("b");
		Token c = token("c");
		FilterTokenAction action = new FilterTokenAction(t -> "a".equals(t.value()) || "c".equals(t.value()));
		TokenRuleMatch match = match(List.of(a, b, c));
		List<Token> result = action.apply(match, CONTEXT);
		assertEquals(List.of(a, c), result);
	}
	
	@Test
	void applyWithAllTokensMatchingKeepsAll() {
		Token a = token("a");
		Token b = token("b");
		Token c = token("c");
		FilterTokenAction action = new FilterTokenAction(t -> true);
		TokenRuleMatch match = match(List.of(a, b, c));
		List<Token> result = action.apply(match, CONTEXT);
		assertEquals(List.of(a, b, c), result);
	}
	
	@Test
	void applyReturnsUnmodifiableList() {
		FilterTokenAction action = new FilterTokenAction(t -> true);
		TokenRuleMatch match = match(List.of(token("a")));
		List<Token> result = action.apply(match, CONTEXT);
		assertThrows(UnsupportedOperationException.class, () -> result.add(token("z")));
	}
	
	@Test
	void applyWithNoTokensMatchingReturnsEmptyList() {
		Token a = token("a");
		Token b = token("b");
		Token c = token("c");
		FilterTokenAction action = new FilterTokenAction(t -> false);
		TokenRuleMatch match = match(List.of(a, b, c));
		List<Token> result = action.apply(match, CONTEXT);
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithPredicateBasedOnTokenTypeFiltersCorrectly() {
		TokenType keyword = StandardTokenType.KEYWORD;
		Token typed1 = new SimpleToken("if", TokenPosition.UNPOSITIONED, Set.of(keyword));
		Token untyped = new SimpleToken("x", TokenPosition.UNPOSITIONED, Set.of());
		Token typed2 = new SimpleToken("while", TokenPosition.UNPOSITIONED, Set.of(keyword));
		FilterTokenAction action = new FilterTokenAction(t -> t.types().contains(keyword));
		TokenRuleMatch match = match(List.of(typed1, untyped, typed2));
		List<Token> result = action.apply(match, CONTEXT);
		assertEquals(List.of(typed1, typed2), result);
	}
}
