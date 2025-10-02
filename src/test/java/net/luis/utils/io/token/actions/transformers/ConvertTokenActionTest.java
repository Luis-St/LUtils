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
import net.luis.utils.io.token.actions.core.TokenConverter;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ConvertTokenAction}.<br>
 *
 * @author Luis-St
 */
class ConvertTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNullConverter() {
		assertThrows(NullPointerException.class, () -> new ConvertTokenAction(null));
	}
	
	@Test
	void constructorWithValidConverter() {
		TokenConverter converter = token -> createToken(token.value().toUpperCase());
		
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		assertEquals(converter, action.converter());
	}
	
	@Test
	void applyWithNullMatch() {
		TokenConverter converter = token -> createToken("converted");
		ConvertTokenAction action = new ConvertTokenAction(converter);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of()));
		
		assertThrows(NullPointerException.class, () -> action.apply(null, context));
	}
	
	@Test
	void applyWithNullContext() {
		TokenConverter converter = token -> createToken("converted");
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithEmptyTokens() {
		TokenConverter converter = token -> createToken(token.value().toUpperCase());
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithSingleToken() {
		TokenConverter converter = token -> createToken(token.value().toUpperCase());
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		List<Token> tokens = List.of(createToken("hello"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(1, result.size());
		assertEquals("HELLO", result.getFirst().value());
	}
	
	@Test
	void applyWithMultipleTokens() {
		TokenConverter converter = token -> createToken(token.value() + "_converted");
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("first_converted", result.get(0).value());
		assertEquals("second_converted", result.get(1).value());
		assertEquals("third_converted", result.get(2).value());
	}
	
	@Test
	void applyMaintainsOrder() {
		TokenConverter converter = token -> createToken(String.valueOf(token.value().length()));
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		List<Token> tokens = List.of(createToken("a"), createToken("bb"), createToken("ccc"));
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("1", result.get(0).value());
		assertEquals("2", result.get(1).value());
		assertEquals("3", result.get(2).value());
	}
	
	@Test
	void applyWithComplexConverter() {
		TokenConverter converter = token -> {
			String value = token.value();
			if (value.isEmpty()) {
				return createToken("empty");
			} else if (value.length() == 1) {
				return createToken("single_" + value);
			} else {
				return createToken("multi_" + value.length());
			}
		};
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		List<Token> tokens = List.of(createToken(""), createToken("x"), createToken("hello"));
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(3, result.size());
		assertEquals("empty", result.get(0).value());
		assertEquals("single_x", result.get(1).value());
		assertEquals("multi_5", result.get(2).value());
	}
	
	@Test
	void applyWithConverterThrowingException() {
		TokenConverter converter = token -> {
			throw new RuntimeException("Converter exception");
		};
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		assertThrows(RuntimeException.class, () -> action.apply(match, context));
	}
	
	@Test
	void applyReturnsImmutableList() {
		TokenConverter converter = token -> token;
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void applyWithIdentityConverter() {
		TokenConverter converter = token -> token;
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		List<Token> tokens = List.of(createToken("unchanged"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertEquals(1, result.size());
		assertEquals("unchanged", result.getFirst().value());
		assertSame(tokens.getFirst(), result.getFirst());
	}
	
	@Test
	void toStringTest() {
		TokenConverter converter = token -> token;
		ConvertTokenAction action = new ConvertTokenAction(converter);
		
		String result = action.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("ConvertTokenAction"));
	}
	
	@Test
	void equalsAndHashCodeTest() {
		TokenConverter converter1 = token -> createToken("converted");
		TokenConverter converter2 = token -> createToken("converted");
		
		ConvertTokenAction action1 = new ConvertTokenAction(converter1);
		ConvertTokenAction action2 = new ConvertTokenAction(converter1);
		ConvertTokenAction action3 = new ConvertTokenAction(converter2);
		
		assertEquals(action1, action2);
		assertNotEquals(action1, action3);
	}
}
