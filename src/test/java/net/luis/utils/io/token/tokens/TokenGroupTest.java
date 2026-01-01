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

package net.luis.utils.io.token.tokens;

import net.luis.utils.io.token.TokenPosition;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenGroup}.<br>
 *
 * @author Luis-St
 */
class TokenGroupTest {
	
	private static @NonNull Token createToken(@NonNull String value, @NonNull TokenPosition position) {
		return new SimpleToken(value, position);
	}
	
	@Test
	void constructorWithNullTokens() {
		assertThrows(NullPointerException.class, () -> new TokenGroup(null));
	}
	
	@Test
	void constructorWithNullTokenInList() {
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0));
		
		assertThrows(NullPointerException.class, () -> new TokenGroup(List.of(token1, null)));
	}
	
	@Test
	void constructorWithEmptyTokenList() {
		assertThrows(IllegalArgumentException.class, () -> new TokenGroup(Collections.emptyList()));
	}
	
	@Test
	void constructorWithSingleToken() {
		Token token = createToken("hello", new TokenPosition(0, 0, 0));
		
		assertDoesNotThrow(() -> new TokenGroup(List.of(token)));
	}
	
	@Test
	void tokensReturnsCorrectList() {
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5));
		List<Token> originalTokens = List.of(token1, token2);
		TokenGroup group = new TokenGroup(originalTokens);
		
		assertEquals(originalTokens, group.tokens());
		assertEquals(2, group.tokens().size());
		assertEquals(token1, group.tokens().get(0));
		assertEquals(token2, group.tokens().get(1));
	}
	
	@Test
	void tokensReturnsImmutableList() {
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5));
		TokenGroup group = new TokenGroup(List.of(token1, token2));
		
		assertThrows(UnsupportedOperationException.class, () -> group.tokens().add(createToken("!", new TokenPosition(0, 10, 10))));
		assertThrows(UnsupportedOperationException.class, () -> group.tokens().remove(0));
		assertThrows(UnsupportedOperationException.class, () -> group.tokens().clear());
	}
	
	@Test
	void tokensWithMultipleTokens() {
		Token token1 = createToken("A", new TokenPosition(0, 0, 0));
		Token token2 = createToken("B", new TokenPosition(0, 1, 1));
		Token token3 = createToken("C", new TokenPosition(0, 2, 2));
		Token token4 = createToken("D", new TokenPosition(0, 3, 3));
		List<Token> tokens = List.of(token1, token2, token3, token4);
		TokenGroup group = new TokenGroup(tokens);
		
		assertEquals(4, group.tokens().size());
		assertEquals(tokens, group.tokens());
	}
	
	@Test
	void valueWithTwoTokens() {
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5));
		TokenGroup group = new TokenGroup(List.of(token1, token2));
		
		assertEquals("helloworld", group.value());
	}
	
	@Test
	void valueWithThreeTokens() {
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0));
		Token token2 = createToken(" ", new TokenPosition(0, 5, 5));
		Token token3 = createToken("world", new TokenPosition(0, 6, 6));
		TokenGroup group = new TokenGroup(List.of(token1, token2, token3));
		
		assertEquals("hello world", group.value());
	}
	
	@Test
	void valueWithNumberTokens() {
		Token token1 = createToken("123", new TokenPosition(0, 0, 0));
		Token token2 = createToken("456", new TokenPosition(0, 3, 3));
		TokenGroup group = new TokenGroup(List.of(token1, token2));
		
		assertEquals("123456", group.value());
	}
	
	@Test
	void valueWithEmptyTokens() {
		Token token1 = createToken("", TokenPosition.UNPOSITIONED);
		Token token2 = createToken("hello", new TokenPosition(0, 0, 0));
		TokenGroup group = new TokenGroup(List.of(token1, token2));
		
		assertEquals("hello", group.value());
	}
	
	@Test
	void valueWithSpecialCharacters() {
		Token token1 = createToken("!@#", new TokenPosition(0, 0, 0));
		Token token2 = createToken("$%^", new TokenPosition(0, 3, 3));
		TokenGroup group = new TokenGroup(List.of(token1, token2));
		
		assertEquals("!@#$%^", group.value());
	}
	
	@Test
	void valueWithManyTokens() {
		Token token1 = createToken("A", new TokenPosition(0, 0, 0));
		Token token2 = createToken("B", new TokenPosition(0, 1, 1));
		Token token3 = createToken("C", new TokenPosition(0, 2, 2));
		Token token4 = createToken("D", new TokenPosition(0, 3, 3));
		Token token5 = createToken("E", new TokenPosition(0, 4, 4));
		TokenGroup group = new TokenGroup(List.of(token1, token2, token3, token4, token5));
		
		assertEquals("ABCDE", group.value());
	}
	
	@Test
	void positionFromFirstToken() {
		TokenPosition firstStart = new TokenPosition(0, 0, 0);
		Token token1 = createToken("hello", firstStart);
		Token token2 = createToken("world", new TokenPosition(0, 5, 5));
		TokenGroup group = new TokenGroup(List.of(token1, token2));
		
		assertEquals(firstStart, group.position());
		assertEquals(0, group.position().line());
		assertEquals(0, group.position().character());
		assertEquals(0, group.position().characterInLine());
	}
	
	@Test
	void positionWithDifferentPositions() {
		TokenPosition firstStart = new TokenPosition(5, 10, 100);
		Token token1 = createToken("test", firstStart);
		Token token2 = createToken("data", new TokenPosition(5, 14, 104));
		TokenGroup group = new TokenGroup(List.of(token1, token2));
		
		assertEquals(firstStart, group.position());
		assertEquals(5, group.position().line());
		assertEquals(10, group.position().characterInLine());
		assertEquals(100, group.position().character());
	}
	
	@Test
	void positionWithUnpositionedFirst() {
		Token token1 = SimpleToken.createUnpositioned("hello");
		Token token2 = createToken("world", new TokenPosition(0, 5, 5));
		TokenGroup group = new TokenGroup(List.of(token1, token2));
		
		assertEquals(TokenPosition.UNPOSITIONED, group.position());
	}
	
	@Test
	void tokenGroupImplementsInterface() {
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5));
		TokenGroup group = new TokenGroup(List.of(token1, token2));
		
		assertInstanceOf(Token.class, group);
		assertEquals("helloworld", group.value());
		assertNotNull(group.position());
	}
	
	@Test
	void tokenGroupWithLargeNumberOfTokens() {
		Token[] tokens = new Token[100];
		
		for (int i = 0; i < 100; i++) {
			tokens[i] = createToken(String.valueOf(i % 10), new TokenPosition(0, i, i));
		}
		
		TokenGroup group = new TokenGroup(List.of(tokens));
		assertEquals(100, group.tokens().size());
		assertEquals(100, group.value().length());
	}
	
	@Test
	void tokenGroupWithVaryingTokenLengths() {
		Token shortToken = createToken("a", new TokenPosition(0, 0, 0));
		Token mediumToken = createToken("hello", new TokenPosition(0, 1, 1));
		Token longToken = createToken("verylongtoken", new TokenPosition(0, 6, 6));
		
		TokenGroup group = new TokenGroup(List.of(shortToken, mediumToken, longToken));
		assertEquals("ahelloverylongtoken", group.value());
		assertEquals(3, group.tokens().size());
	}
	
	@Test
	void tokenGroupAcrossMultipleLines() {
		Token token1 = createToken("line1", TokenPosition.UNPOSITIONED);
		Token token2 = createToken("line2", new TokenPosition(1, 0, 10));
		Token token3 = createToken("line3", new TokenPosition(2, 0, 20));
		
		TokenGroup group = new TokenGroup(List.of(token1, token2, token3));
		assertEquals("line1line2line3", group.value());
		assertEquals(TokenPosition.UNPOSITIONED, group.position());
	}
	
	@Test
	void toStringContainsTokenGroupInfo() {
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5));
		TokenGroup group = new TokenGroup(List.of(token1, token2));
		
		String groupString = group.toString();
		assertTrue(groupString.contains("TokenGroup"));
		assertTrue(groupString.contains("tokens"));
	}
	
	@Test
	void equalTokenGroupsHaveSameHashCode() {
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5));
		List<Token> tokens = List.of(token1, token2);
		
		TokenGroup group1 = new TokenGroup(tokens);
		TokenGroup group2 = new TokenGroup(tokens);
		
		assertEquals(group1.hashCode(), group2.hashCode());
	}
	
}
