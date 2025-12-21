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
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FilterTokenAction}.<br>
 *
 * @author Luis-St
 */
class FilterTokenActionTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullFilter() {
		assertThrows(NullPointerException.class, () -> new FilterTokenAction(null));
	}
	
	@Test
	void constructorWithValidFilter() {
		Predicate<Token> filter = token -> "keep".equals(token.value());
		
		FilterTokenAction action = new FilterTokenAction(filter);
		
		assertEquals(filter, action.filter());
	}
	
	@Test
	void applyWithNullMatch() {
		Predicate<Token> filter = token -> true;
		FilterTokenAction action = new FilterTokenAction(filter);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of()));
		
		assertThrows(NullPointerException.class, () -> action.apply(null, context));
	}
	
	@Test
	void applyWithNullContext() {
		Predicate<Token> filter = token -> true;
		FilterTokenAction action = new FilterTokenAction(filter);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithMatchingTokens() {
		Predicate<Token> filter = token -> "keep".equals(token.value());
		FilterTokenAction action = new FilterTokenAction(filter);
		
		List<Token> tokens = List.of(
			createToken("keep"),
			createToken("remove"),
			createToken("keep"),
			createToken("remove")
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
		FilterTokenAction action = new FilterTokenAction(filter);
		
		List<Token> tokens = List.of(createToken("remove1"), createToken("remove2"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithAllMatchingTokens() {
		Predicate<Token> filter = token -> true;
		FilterTokenAction action = new FilterTokenAction(filter);
		
		List<Token> tokens = List.of(createToken("keep1"), createToken("keep2"), createToken("keep3"));
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("keep1", result.get(0).value());
		assertEquals("keep2", result.get(1).value());
		assertEquals("keep3", result.get(2).value());
	}
	
	@Test
	void applyWithEmptyTokens() {
		Predicate<Token> filter = token -> true;
		FilterTokenAction action = new FilterTokenAction(filter);
		
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithComplexFilter() {
		Predicate<Token> filter = token -> token.value().length() > 3 && token.value().startsWith("a");
		FilterTokenAction action = new FilterTokenAction(filter);
		
		List<Token> tokens = List.of(
			createToken("abc"),
			createToken("apple"),
			createToken("banana"),
			createToken("avocado"),
			createToken("ax")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, 5, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("apple", result.get(0).value());
		assertEquals("avocado", result.get(1).value());
	}
	
	@Test
	void applyMaintainsOrder() {
		Predicate<Token> filter = token -> !"remove".equals(token.value());
		FilterTokenAction action = new FilterTokenAction(filter);
		
		List<Token> tokens = List.of(
			createToken("first"),
			createToken("remove"),
			createToken("second"),
			createToken("third"),
			createToken("remove")
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
	void applyReturnsImmutableList() {
		Predicate<Token> filter = token -> true;
		FilterTokenAction action = new FilterTokenAction(filter);
		
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
		FilterTokenAction action = new FilterTokenAction(filter);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		assertThrows(RuntimeException.class, () -> action.apply(match, context));
	}
	
	@Test
	void toStringTest() {
		Predicate<Token> filter = token -> true;
		FilterTokenAction action = new FilterTokenAction(filter);
		
		String result = action.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("FilterTokenAction"));
	}
	
	@Test
	void equalsAndHashCodeTest() {
		Predicate<Token> filter1 = token -> true;
		Predicate<Token> filter2 = token -> true;
		
		FilterTokenAction action1 = new FilterTokenAction(filter1);
		FilterTokenAction action2 = new FilterTokenAction(filter1);
		FilterTokenAction action3 = new FilterTokenAction(filter2);
		
		assertEquals(action1, action2);
		assertNotEquals(action1, action3);
	}
}
