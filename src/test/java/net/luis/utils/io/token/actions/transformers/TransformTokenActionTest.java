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

package net.luis.utils.io.token.actions.transformers;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.actions.core.TokenTransformer;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TransformTokenAction}.<br>
 *
 * @author Luis-St
 */
class TransformTokenActionTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullTransformer() {
		assertThrows(NullPointerException.class, () -> new TransformTokenAction(null));
	}
	
	@Test
	void constructorWithValidTransformer() {
		TokenTransformer transformer = tokens -> tokens.stream().map(token -> createToken(token.value().toUpperCase())).toList();
		
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		assertEquals(transformer, action.transformer());
	}
	
	@Test
	void applyWithNullMatch() {
		TokenTransformer transformer = List::copyOf;
		TransformTokenAction action = new TransformTokenAction(transformer);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of()));
		
		assertThrows(NullPointerException.class, () -> action.apply(null, context));
	}
	
	@Test
	void applyWithNullContext() {
		TokenTransformer transformer = List::copyOf;
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyTokens() {
		TokenTransformer transformer = tokens -> tokens.stream().map(token -> createToken(token.value() + "_suffix")).toList();
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithSimpleTransformer() {
		TokenTransformer transformer = tokens -> tokens.stream().map(token -> createToken(token.value().toUpperCase())).toList();
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("HELLO", result.get(0).value());
		assertEquals("WORLD", result.get(1).value());
	}
	
	@Test
	void applyWithTokenAddingTransformer() {
		TokenTransformer transformer = tokens -> {
			List<Token> result = new ArrayList<>(tokens);
			result.add(createToken("added"));
			return List.copyOf(result);
		};
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of(createToken("original"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("original", result.get(0).value());
		assertEquals("added", result.get(1).value());
	}
	
	@Test
	void applyWithTokenRemovingTransformer() {
		TokenTransformer transformer = tokens -> tokens.stream().filter(token -> !"remove".equals(token.value())).toList();
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of(createToken("keep"), createToken("remove"), createToken("keep"));
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(2, result.size());
		assertEquals("keep", result.get(0).value());
		assertEquals("keep", result.get(1).value());
	}
	
	@Test
	void applyWithReversingTransformer() {
		TokenTransformer transformer = tokens -> {
			List<Token> reversed = new ArrayList<>(tokens);
			Collections.reverse(reversed);
			return List.copyOf(reversed);
		};
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("third", result.get(0).value());
		assertEquals("second", result.get(1).value());
		assertEquals("first", result.get(2).value());
	}
	
	@Test
	void applyWithComplexTransformer() {
		TokenTransformer transformer = tokens -> {
			List<Token> result = new ArrayList<>();
			for (int i = 0; i < tokens.size(); i++) {
				Token token = tokens.get(i);
				result.add(createToken("[" + i + "]"));
				result.add(token);
			}
			return List.copyOf(result);
		};
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of(createToken("a"), createToken("b"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(4, result.size());
		assertEquals("[0]", result.get(0).value());
		assertEquals("a", result.get(1).value());
		assertEquals("[1]", result.get(2).value());
		assertEquals("b", result.get(3).value());
	}
	
	@Test
	void applyWithIdentityTransformer() {
		TokenTransformer transformer = List::copyOf;
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of(createToken("unchanged"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(1, result.size());
		assertEquals("unchanged", result.getFirst().value());
	}
	
	@Test
	void applyWithTransformerThrowingException() {
		TokenTransformer transformer = tokens -> {
			throw new RuntimeException("Transformer exception");
		};
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		assertThrows(RuntimeException.class, () -> action.apply(match, context));
	}
	
	@Test
	void applyReturnsImmutableList() {
		TokenTransformer transformer = List::copyOf;
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void applyWithEmptyResultTransformer() {
		TokenTransformer transformer = tokens -> List.of();
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		List<Token> tokens = List.of(createToken("remove"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void toStringTest() {
		TokenTransformer transformer = List::copyOf;
		TransformTokenAction action = new TransformTokenAction(transformer);
		
		String result = action.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("TransformTokenAction"));
	}
	
	@Test
	void equalsAndHashCodeTest() {
		TokenTransformer transformer1 = List::copyOf;
		TokenTransformer transformer2 = List::copyOf;
		
		TransformTokenAction action1 = new TransformTokenAction(transformer1);
		TransformTokenAction action2 = new TransformTokenAction(transformer1);
		TransformTokenAction action3 = new TransformTokenAction(transformer2);
		
		assertEquals(action1, action2);
		assertNotEquals(action1, action3);
	}
}
