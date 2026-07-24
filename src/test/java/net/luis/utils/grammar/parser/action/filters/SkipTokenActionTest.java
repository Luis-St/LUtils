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
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import net.luis.utils.grammar.token.Token;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SkipTokenAction}.<br>
 *
 * @author Luis-St
 */
class SkipTokenActionTest {
	
	private static final TokenRule RULE = (stream, ctx) -> null;
	private static final TokenActionContext CTX = new TokenActionContext(TokenStream.EMPTY);
	
	private static @NonNull Token token(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	private static @NonNull TokenRuleMatch matchOf(Token... tokens) {
		List<Token> list = List.of(tokens);
		return new TokenRuleMatch(0, list.size(), list, RULE);
	}
	
	@Test
	void constructWithValidFilter() {
		Predicate<Token> predicate = t -> true;
		SkipTokenAction action = new SkipTokenAction(predicate);
		assertNotNull(action.filter());
		assertSame(predicate, action.filter());
	}
	
	@Test
	void constructWithNullFilter() {
		assertThrows(NullPointerException.class, () -> new SkipTokenAction(null));
	}
	
	@Test
	void applyWithNullMatchThrows() {
		SkipTokenAction action = new SkipTokenAction(t -> true);
		assertThrows(NullPointerException.class, () -> action.apply(null, CTX));
	}
	
	@Test
	void applyWithNullContextThrows() {
		SkipTokenAction action = new SkipTokenAction(t -> true);
		TokenRuleMatch match = matchOf(token("a"));
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyMatchedTokensReturnsEmptyList() {
		SkipTokenAction action = new SkipTokenAction(t -> true);
		TokenRuleMatch match = TokenRuleMatch.empty(0, RULE);
		assertTrue(action.apply(match, CTX).isEmpty());
	}
	
	@Test
	void applySkipsMatchingToken() {
		SkipTokenAction action = new SkipTokenAction(t -> "a".equals(t.value()));
		TokenRuleMatch match = matchOf(token("a"));
		assertTrue(action.apply(match, CTX).isEmpty());
	}
	
	@Test
	void applyKeepsNonMatchingToken() {
		SkipTokenAction action = new SkipTokenAction(t -> "a".equals(t.value()));
		Token b = token("b");
		TokenRuleMatch match = matchOf(b);
		assertEquals(List.of(b), action.apply(match, CTX));
	}
	
	@Test
	void applyWithMultipleTokensSkipsOnlyMatchingOnesInOrder() {
		Token a = token("a");
		Token b = token("b");
		Token c = token("c");
		SkipTokenAction action = new SkipTokenAction(t -> "a".equals(t.value()) || "c".equals(t.value()));
		TokenRuleMatch match = matchOf(a, b, c);
		assertEquals(List.of(b), action.apply(match, CTX));
	}
	
	@Test
	void applyWithNoTokensMatchingKeepsAll() {
		Token a = token("a");
		Token b = token("b");
		Token c = token("c");
		SkipTokenAction action = new SkipTokenAction(t -> false);
		TokenRuleMatch match = matchOf(a, b, c);
		assertEquals(List.of(a, b, c), action.apply(match, CTX));
	}
	
	@Test
	void applyReturnsUnmodifiableList() {
		SkipTokenAction action = new SkipTokenAction(t -> false);
		TokenRuleMatch match = matchOf(token("a"));
		List<Token> result = action.apply(match, CTX);
		assertThrows(UnsupportedOperationException.class, () -> result.add(token("x")));
	}
	
	@Test
	void applyWithAllTokensMatchingSkipsAll() {
		Token a = token("a");
		Token b = token("b");
		Token c = token("c");
		SkipTokenAction action = new SkipTokenAction(t -> true);
		TokenRuleMatch match = matchOf(a, b, c);
		assertTrue(action.apply(match, CTX).isEmpty());
	}
	
	@Test
	void applyIsInverseOfFilterTokenActionForSamePredicate() {
		Token a = token("a");
		Token b = token("bb");
		Token c = token("c");
		Token d = token("dd");
		Predicate<Token> predicate = t -> t.value().length() > 1;
		SkipTokenAction skipAction = new SkipTokenAction(predicate);
		FilterTokenAction filterAction = new FilterTokenAction(predicate);
		TokenRuleMatch match = matchOf(a, b, c, d);
		
		List<Token> skipped = skipAction.apply(match, CTX);
		List<Token> filtered = filterAction.apply(match, CTX);
		
		Set<Token> combined = new HashSet<>(skipped);
		combined.addAll(filtered);
		
		assertTrue(skipped.stream().noneMatch(filtered::contains));
		assertEquals(Set.of(a, b, c, d), combined);
	}
}
