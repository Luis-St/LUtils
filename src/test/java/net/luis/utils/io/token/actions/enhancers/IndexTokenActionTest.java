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

package net.luis.utils.io.token.actions.enhancers;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.rules.AlwaysMatchTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IndexTokenAction}.<br>
 *
 * @author Luis-St
 */
class IndexTokenActionTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void constructorWithNegativeStartIndex() {
		assertThrows(IllegalArgumentException.class, () -> new IndexTokenAction(-1));
	}
	
	@Test
	void constructorWithDefaultStartIndex() {
		IndexTokenAction action = new IndexTokenAction();
		
		assertEquals(0, action.startIndex());
	}
	
	@Test
	void constructorWithValidStartIndex() {
		IndexTokenAction action = new IndexTokenAction(5);
		
		assertEquals(5, action.startIndex());
	}
	
	@Test
	void constructorWithZeroStartIndex() {
		IndexTokenAction action = new IndexTokenAction(0);
		
		assertEquals(0, action.startIndex());
	}
	
	@Test
	void applyWithNullMatch() {
		IndexTokenAction action = new IndexTokenAction();
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of()));
		
		assertThrows(NullPointerException.class, () -> action.apply(null, context));
	}
	
	@Test
	void applyWithNullContext() {
		IndexTokenAction action = new IndexTokenAction();
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> action.apply(match, null));
	}
	
	@Test
	void applyWithSimpleTokens() {
		IndexTokenAction action = new IndexTokenAction(10);
		
		List<Token> tokens = List.of(createToken("first"), createToken("second"), createToken("third"));
		TokenRuleMatch match = new TokenRuleMatch(0, 3, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		assertEquals(3, result.size());
		
		IndexedToken indexed1 = assertInstanceOf(IndexedToken.class, result.get(0));
		IndexedToken indexed2 = assertInstanceOf(IndexedToken.class, result.get(1));
		IndexedToken indexed3 = assertInstanceOf(IndexedToken.class, result.get(2));
		
		assertEquals("first", indexed1.value());
		assertEquals("second", indexed2.value());
		assertEquals("third", indexed3.value());
		assertEquals(10, indexed1.index());
		assertEquals(11, indexed2.index());
		assertEquals(12, indexed3.index());
	}
	
	@Test
	void applyWithAlreadyIndexedTokens() {
		IndexedToken existingIndexed = new IndexedToken(createToken("test"), 5);
		IndexTokenAction action = new IndexTokenAction(0);
		
		List<Token> tokens = List.of(existingIndexed, createToken("new"));
		TokenRuleMatch match = new TokenRuleMatch(0, 2, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		assertEquals(2, result.size());
		
		IndexedToken first = assertInstanceOf(IndexedToken.class, result.get(0));
		IndexedToken second = assertInstanceOf(IndexedToken.class, result.get(1));
		
		assertEquals("test", first.value());
		assertEquals("new", second.value());
		assertEquals(5, first.index());
		assertEquals(1, second.index());
	}
	
	@Test
	void applyWithEmptyTokens() {
		IndexTokenAction action = new IndexTokenAction(0);
		
		List<Token> tokens = List.of();
		TokenRuleMatch match = new TokenRuleMatch(0, 0, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(List.of(createToken("dummy"))));
		
		List<Token> result = action.apply(match, context);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void applyWithSingleToken() {
		IndexTokenAction action = new IndexTokenAction(100);
		
		List<Token> tokens = List.of(createToken("single"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		assertEquals(1, result.size());
		
		
		IndexedToken indexed = assertInstanceOf(IndexedToken.class, result.getFirst());
		assertEquals("single", indexed.value());
		assertEquals(100, indexed.index());
	}
	
	@Test
	void applyReturnsImmutableList() {
		IndexTokenAction action = new IndexTokenAction();
		
		List<Token> tokens = List.of(createToken("test"));
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void applyPreservesTokenProperties() {
		IndexTokenAction action = new IndexTokenAction(0);
		Token originalToken = createToken("test");
		
		List<Token> tokens = List.of(originalToken);
		TokenRuleMatch match = new TokenRuleMatch(0, 1, tokens, AlwaysMatchTokenRule.INSTANCE);
		TokenActionContext context = new TokenActionContext(TokenStream.createImmutable(tokens));
		
		List<Token> result = action.apply(match, context);
		IndexedToken indexed = (IndexedToken) result.getFirst();
		
		assertEquals(originalToken.value(), indexed.value());
		assertEquals(originalToken.position(), indexed.position());
		assertEquals(originalToken.types(), indexed.types());
	}
	
	@Test
	void toStringTest() {
		IndexTokenAction action = new IndexTokenAction(42);
		
		String result = action.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("IndexTokenAction"));
		assertTrue(result.contains("42"));
	}
	
	@Test
	void equalsAndHashCodeTest() {
		IndexTokenAction action1 = new IndexTokenAction(5);
		IndexTokenAction action2 = new IndexTokenAction(5);
		IndexTokenAction action3 = new IndexTokenAction(10);
		
		assertEquals(action1, action2);
		assertNotEquals(action1, action3);
		assertEquals(action1.hashCode(), action2.hashCode());
	}
}
