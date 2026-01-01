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

package net.luis.utils.io.token.actions.core;

import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenTransformer}.<br>
 *
 * @author Luis-St
 */
class TokenTransformerTest {
	
	private static @NonNull Token createToken(@NonNull String value) {
		return SimpleToken.createUnpositioned(value);
	}
	
	@Test
	void transformWithValidTokens() {
		TokenTransformer transformer = tokens ->
			tokens.stream().map(token -> createToken(token.value().toUpperCase())).toList();
		
		List<Token> input = List.of(createToken("hello"), createToken("world"));
		
		List<Token> result = transformer.transform(input);
		
		assertEquals(2, result.size());
		assertEquals("HELLO", result.get(0).value());
		assertEquals("WORLD", result.get(1).value());
	}
	
	@Test
	void transformWithEmptyList() {
		TokenTransformer transformer = tokens -> tokens.stream().map(token -> createToken("transformed")).toList();
		
		List<Token> input = List.of();
		
		List<Token> result = transformer.transform(input);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void transformWithSingleToken() {
		TokenTransformer transformer = tokens ->
			tokens.stream().map(token -> createToken(token.value() + "_suffix")).toList();
		
		List<Token> input = List.of(createToken("test"));
		
		List<Token> result = transformer.transform(input);
		
		assertEquals(1, result.size());
		assertEquals("test_suffix", result.getFirst().value());
	}
	
	@Test
	void transformReturnsImmutableList() {
		TokenTransformer transformer = List::copyOf;
		
		List<Token> input = List.of(createToken("test"));
		
		List<Token> result = transformer.transform(input);
		
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void transformCanAddTokens() {
		TokenTransformer transformer = tokens -> {
			var result = new ArrayList<>(tokens);
			result.add(createToken("added"));
			return List.copyOf(result);
		};
		
		List<Token> input = List.of(createToken("original"));
		
		List<Token> result = transformer.transform(input);
		
		assertEquals(2, result.size());
		assertEquals("original", result.get(0).value());
		assertEquals("added", result.get(1).value());
	}
	
	@Test
	void transformCanRemoveTokens() {
		TokenTransformer transformer = tokens ->
			tokens.stream().filter(token -> !"remove".equals(token.value())).toList();
		
		List<Token> input = List.of(createToken("keep"), createToken("remove"), createToken("keep"));
		
		List<Token> result = transformer.transform(input);
		
		assertEquals(2, result.size());
		assertEquals("keep", result.get(0).value());
		assertEquals("keep", result.get(1).value());
	}
	
	@Test
	void transformCanReplaceTokens() {
		TokenTransformer transformer = tokens ->
			tokens.stream().map(token ->
				"old".equals(token.value()) ? createToken("new") : token
			).toList();
		
		List<Token> input = List.of(createToken("keep"), createToken("old"), createToken("keep"));
		
		List<Token> result = transformer.transform(input);
		
		assertEquals(3, result.size());
		assertEquals("keep", result.get(0).value());
		assertEquals("new", result.get(1).value());
		assertEquals("keep", result.get(2).value());
	}
}
