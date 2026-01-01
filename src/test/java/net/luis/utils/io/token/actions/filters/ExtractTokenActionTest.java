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

package net.luis.utils.io.token.actions.filters;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ExtractTokenAction}.<br>
 *
 * @author Luis-St
 */
class ExtractTokenActionTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullFilter() {
		Consumer<Token> extractor = token -> {};
		
		assertThrows(NullPointerException.class, () -> new ExtractTokenAction(null, extractor));
	}
	
	@Test
	void constructorWithNullExtractor() {
		Predicate<Token> filter = token -> true;
		
		assertThrows(NullPointerException.class, () -> new ExtractTokenAction(filter, null));
	}
	
	@Test
	void constructorWithValidParameters() {
		Predicate<Token> filter = token -> "extract".equals(token.value());
		Consumer<Token> extractor = token -> {};
		
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		
		assertEquals(filter, action.filter());
		assertEquals(extractor, action.extractor());
	}
	
	@Test
	void applyWithNullMatch() {
		Predicate<Token> filter = token -> true;
		Consumer<Token> extractor = token -> {};
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of()));
		
		assertThrows(NullPointerException.class, () -> action.apply(null, context));
	}
	
	@Test
	void applyWithNullContext() {
		Predicate<Token> filter = token -> true;
		Consumer<Token> extractor = token -> {};
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithMatchingTokens() {
		List<String> extracted = new ArrayList<>();
		Predicate<Token> filter = token -> "extract".equals(token.value());
		Consumer<Token> extractor = token -> extracted.add(token.value());
		
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		
		List<Token> tokens = List.of(
			createToken("keep"),
			createToken("extract"),
			createToken("keep"),
			createToken("extract")
		);
		TokenRuleMatch match = new TokenRuleMatch(0, 4, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("keep", result.get(0).value());
		assertEquals("keep", result.get(1).value());
		
		assertEquals(2, extracted.size());
		assertEquals("extract", extracted.get(0));
		assertEquals("extract", extracted.get(1));
	}
	
	@Test
	void applyWithNoMatchingTokens() {
		List<String> extracted = new ArrayList<>();
		Predicate<Token> filter = token -> "nonexistent".equals(token.value());
		Consumer<Token> extractor = token -> extracted.add(token.value());
		
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		
		List<Token> tokens = List.of(createToken("keep1"), createToken("keep2"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("keep1", result.get(0).value());
		assertEquals("keep2", result.get(1).value());
		assertTrue(extracted.isEmpty());
	}
	
	@Test
	void applyWithAllMatchingTokens() {
		List<String> extracted = new ArrayList<>();
		Predicate<Token> filter = token -> true;
		Consumer<Token> extractor = token -> extracted.add(token.value());
		
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		
		List<Token> tokens = List.of(createToken("extract1"), createToken("extract2"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
		assertEquals(2, extracted.size());
		assertEquals("extract1", extracted.get(0));
		assertEquals("extract2", extracted.get(1));
	}
	
	@Test
	void applyWithEmptyTokens() {
		List<String> extracted = new ArrayList<>();
		Predicate<Token> filter = token -> true;
		Consumer<Token> extractor = token -> extracted.add(token.value());
		
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
		assertTrue(extracted.isEmpty());
	}
	
	@Test
	void applyWithComplexFilter() {
		List<String> extracted = new ArrayList<>();
		Predicate<Token> filter = token -> token.value().length() > 3;
		Consumer<Token> extractor = token -> extracted.add(token.value());
		
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		
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
		assertEquals("hi", result.get(0).value());
		assertEquals("bye", result.get(1).value());
		
		assertEquals(2, extracted.size());
		assertEquals("hello", extracted.get(0));
		assertEquals("world", extracted.get(1));
	}
	
	@Test
	void applyReturnsImmutableList() {
		Predicate<Token> filter = token -> false;
		Consumer<Token> extractor = token -> {};
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void toStringTest() {
		Predicate<Token> filter = token -> true;
		Consumer<Token> extractor = token -> {};
		ExtractTokenAction action = new ExtractTokenAction(filter, extractor);
		
		String result = action.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("ExtractTokenAction"));
	}
	
	@Test
	void equalsAndHashCodeTest() {
		Predicate<Token> filter1 = token -> true;
		Predicate<Token> filter2 = token -> true;
		Consumer<Token> extractor1 = token -> {};
		Consumer<Token> extractor2 = token -> {};
		
		ExtractTokenAction action1 = new ExtractTokenAction(filter1, extractor1);
		ExtractTokenAction action2 = new ExtractTokenAction(filter1, extractor1);
		ExtractTokenAction action3 = new ExtractTokenAction(filter2, extractor2);
		
		assertEquals(action1, action2);
		assertNotEquals(action1, action3);
	}
}
