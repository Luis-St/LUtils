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

package net.luis.utils.io.token.actions.filters;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

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
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullFilter() {
		assertThrows(NullPointerException.class, () -> new SkipTokenAction(null));
	}
	
	@Test
	void constructorWithValidFilter() {
		Predicate<Token> filter = token -> "skip".equals(token.value());
		
		SkipTokenAction action = new SkipTokenAction(filter);
		
		assertEquals(filter, action.filter());
	}
	
	@Test
	void applyWithNullMatch() {
		Predicate<Token> filter = token -> true;
		SkipTokenAction action = new SkipTokenAction(filter);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of()));
		
		assertThrows(NullPointerException.class, () -> action.apply(null, context));
	}
	
	@Test
	void applyWithNullContext() {
		Predicate<Token> filter = token -> true;
		SkipTokenAction action = new SkipTokenAction(filter);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithMatchingTokens() {
		Predicate<Token> filter = token -> "skip".equals(token.value());
		SkipTokenAction action = new SkipTokenAction(filter);
		
		List<Token> tokens = List.of(
			createToken("keep"),
			createToken("skip"),
			createToken("keep"),
			createToken("skip")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("keep", result.get(0).value());
		assertEquals("keep", result.get(1).value());
	}
	
	@Test
	void applyWithNoMatchingTokens() {
		Predicate<Token> filter = token -> "nonexistent".equals(token.value());
		SkipTokenAction action = new SkipTokenAction(filter);
		
		List<Token> tokens = List.of(createToken("keep1"), createToken("keep2"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("keep1", result.get(0).value());
		assertEquals("keep2", result.get(1).value());
	}
	
	@Test
	void applyWithAllMatchingTokens() {
		Predicate<Token> filter = token -> true;
		SkipTokenAction action = new SkipTokenAction(filter);
		
		List<Token> tokens = List.of(createToken("skip1"), createToken("skip2"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithEmptyTokens() {
		Predicate<Token> filter = token -> true;
		SkipTokenAction action = new SkipTokenAction(filter);
		
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithComplexFilter() {
		Predicate<Token> filter = token -> token.value().length() <= 3;
		SkipTokenAction action = new SkipTokenAction(filter);
		
		List<Token> tokens = List.of(
			createToken("hi"),
			createToken("hello"),
			createToken("bye"),
			createToken("world")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("hello", result.get(0).value());
		assertEquals("world", result.get(1).value());
	}
	
	@Test
	void applyMaintainsOrder() {
		Predicate<Token> filter = token -> "skip".equals(token.value());
		SkipTokenAction action = new SkipTokenAction(filter);
		
		List<Token> tokens = List.of(
			createToken("first"),
			createToken("skip"),
			createToken("second"),
			createToken("third"),
			createToken("skip")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, 5, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("first", result.get(0).value());
		assertEquals("second", result.get(1).value());
		assertEquals("third", result.get(2).value());
	}
	
	@Test
	void applyIsInverseOfFilter() {
		Predicate<Token> filter = token -> token.value().startsWith("skip");
		SkipTokenAction skipAction = new SkipTokenAction(filter);
		FilterTokenAction filterAction = new FilterTokenAction(filter.negate());
		
		List<Token> tokens = List.of(
			createToken("keep1"),
			createToken("skip1"),
			createToken("keep2"),
			createToken("skip2")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> skipResult = skipAction.apply(match, context);
		List<Token> filterResult = filterAction.apply(match, context);
		
		assertEquals(filterResult.size(), skipResult.size());
		for (int i = 0; i < skipResult.size(); i++) {
			assertEquals(filterResult.get(i).value(), skipResult.get(i).value());
		}
	}
	
	@Test
	void applyReturnsImmutableList() {
		Predicate<Token> filter = token -> false;
		SkipTokenAction action = new SkipTokenAction(filter);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void applyWithPredicateThrowingException() {
		Predicate<Token> filter = token -> {
			throw new RuntimeException("Filter exception");
		};
		SkipTokenAction action = new SkipTokenAction(filter);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		assertThrows(RuntimeException.class, () -> action.apply(match, context));
	}
	
	@Test
	void toStringTest() {
		Predicate<Token> filter = token -> true;
		SkipTokenAction action = new SkipTokenAction(filter);
		
		String result = action.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("SkipTokenAction"));
	}
	
	@Test
	void equalsAndHashCodeTest() {
		Predicate<Token> filter1 = token -> true;
		Predicate<Token> filter2 = token -> true;
		
		SkipTokenAction action1 = new SkipTokenAction(filter1);
		SkipTokenAction action2 = new SkipTokenAction(filter1);
		SkipTokenAction action3 = new SkipTokenAction(filter2);
		
		assertEquals(action1, action2);
		assertNotEquals(action1, action3);
	}
}
