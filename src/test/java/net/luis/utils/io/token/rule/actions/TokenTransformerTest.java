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

package net.luis.utils.io.token.rule.actions;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.tokens.SimpleToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenTransformer}.<br>
 *
 * @author Luis-St
 */
class TokenTransformerTest {
	
	private static @NotNull Token createToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(word -> word.equals(value), value);
	}
	
	@Test
	void identityTransformerReturnsUnmodifiedTokens() {
		TokenTransformer identity = List::copyOf;
		List<Token> tokens = List.of(createToken("test1"), createToken("test2"));
		List<Token> result = identity.transform(tokens);
		
		assertEquals(tokens.size(), result.size());
		assertEquals(tokens, result);
		assertSame(tokens, result);
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void identityTransformerWithEmptyList() {
		TokenTransformer identity = List::copyOf;
		List<Token> tokens = Collections.emptyList();
		List<Token> result = identity.transform(tokens);
		
		assertTrue(result.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void identityTransformerWithSingleToken() {
		TokenTransformer identity = List::copyOf;
		Token token = createToken("single");
		List<Token> tokens = List.of(token);
		List<Token> result = identity.transform(tokens);
		
		assertEquals(1, result.size());
		assertEquals(token, result.get(0));
		assertEquals("single", result.get(0).value());
	}
	
	@Test
	void uppercaseTransformerModifiesTokenValues() {
		TokenTransformer uppercase = tokens -> tokens.stream().map(token -> createToken(token.value().toUpperCase())).toList();
		List<Token> tokens = List.of(createToken("hello"), createToken("world"));
		List<Token> result = uppercase.transform(tokens);
		
		assertEquals(2, result.size());
		assertEquals("HELLO", result.get(0).value());
		assertEquals("WORLD", result.get(1).value());
	}
	
	@Test
	void filterTransformerRemovesTokens() {
		TokenTransformer filter = tokens -> tokens.stream().filter(token -> token.value().startsWith("keep")).toList();
		List<Token> tokens = List.of(
			createToken("keep1"),
			createToken("remove1"),
			createToken("keep2"),
			createToken("remove2")
		);
		List<Token> result = filter.transform(tokens);
		
		assertEquals(2, result.size());
		assertEquals("keep1", result.get(0).value());
		assertEquals("keep2", result.get(1).value());
	}
	
	@Test
	void filterTransformerWithNoMatches() {
		TokenTransformer filter = tokens -> tokens.stream().filter(token -> token.value().startsWith("nonexistent")).toList();
		List<Token> tokens = List.of(createToken("test1"), createToken("test2"));
		List<Token> result = filter.transform(tokens);
		
		assertTrue(result.isEmpty());
	}
	
	@Test
	void duplicateTransformerAddsTokens() {
		TokenTransformer duplicate = tokens -> tokens.stream().flatMap(token -> List.of(token, createToken(token.value() + "_copy")).stream()).toList();
		List<Token> tokens = List.of(createToken("original"));
		List<Token> result = duplicate.transform(tokens);
		
		assertEquals(2, result.size());
		assertEquals("original", result.get(0).value());
		assertEquals("original_copy", result.get(1).value());
	}
	
	@Test
	void reverseTransformerChangesOrder() {
		TokenTransformer reverse = tokens -> {
			List<Token> reversed = Lists.newArrayList(tokens.stream().toList());
			Collections.reverse(reversed);
			return Collections.unmodifiableList(reversed);
		};
		List<Token> tokens = List.of(
			createToken("first"),
			createToken("second"),
			createToken("third")
		);
		List<Token> result = reverse.transform(tokens);
		
		assertEquals(3, result.size());
		assertEquals("third", result.get(0).value());
		assertEquals("second", result.get(1).value());
		assertEquals("first", result.get(2).value());
	}
	
	@Test
	void combinedTransformerAppliesMultipleOperations() {
		TokenTransformer combined = tokens -> tokens.stream().filter(token -> token.value().length() > 3).map(token -> createToken(token.value().toUpperCase())).toList();
		List<Token> tokens = List.of(
			createToken("a"),
			createToken("hello"),
			createToken("hi"),
			createToken("world")
		);
		List<Token> result = combined.transform(tokens);
		
		assertEquals(2, result.size());
		assertEquals("HELLO", result.get(0).value());
		assertEquals("WORLD", result.get(1).value());
	}
	
	@Test
	void emptyTransformerReturnsEmptyList() {
		TokenTransformer empty = tokens -> Collections.emptyList();
		List<Token> tokens = List.of(createToken("test1"), createToken("test2"));
		List<Token> result = empty.transform(tokens);
		
		assertTrue(result.isEmpty());
		assertThrows(UnsupportedOperationException.class, () -> result.add(createToken("new")));
	}
	
	@Test
	void prefixTransformerModifiesAllTokens() {
		TokenTransformer prefix = tokens -> tokens.stream().map(token -> createToken("prefix_" + token.value())).toList();
		List<Token> tokens = List.of(createToken("one"), createToken("two"));
		List<Token> result = prefix.transform(tokens);
		
		assertEquals(2, result.size());
		assertEquals("prefix_one", result.get(0).value());
		assertEquals("prefix_two", result.get(1).value());
	}
	
	@Test
	void conditionalTransformerAppliesDifferentRules() {
		TokenTransformer conditional = tokens -> tokens.stream().map(token -> {
			if (token.value().startsWith("num")) {
				return createToken(token.value().toUpperCase());
			} else {
				return createToken(token.value().toLowerCase());
			}
		}).toList();
		
		List<Token> tokens = List.of(
			createToken("num123"),
			createToken("TEXT"),
			createToken("numABC")
		);
		
		List<Token> result = conditional.transform(tokens);
		
		assertEquals(3, result.size());
		assertEquals("NUM123", result.get(0).value());
		assertEquals("text", result.get(1).value());
		assertEquals("NUMABC", result.get(2).value());
	}
	
	@Test
	void transformerWithNullTokenHandling() {
		TokenTransformer safeTransformer = tokens -> tokens.stream().filter(token -> token != null && !token.value().isEmpty()).map(token -> createToken(token.value().trim())).toList();
		
		List<Token> tokens = List.of(
			createToken("  hello  "),
			createToken("world"),
			createToken("")
		);
		
		List<Token> result = safeTransformer.transform(tokens);
		
		assertEquals(2, result.size());
		assertEquals("hello", result.get(0).value());
		assertEquals("world", result.get(1).value());
	}
}
