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

package net.luis.utils.io.token.tokens;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.definition.TokenDefinition;
import org.jetbrains.annotations.NotNull;
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
	
	private static @NotNull TokenDefinition createAnyDefinition() {
		return word -> true;
	}
	
	private static @NotNull TokenDefinition createNumberDefinition() {
		return word -> word.matches("\\d+");
	}
	
	private static @NotNull TokenDefinition createTextDefinition() {
		return word -> word.matches("[a-zA-Z ]+");
	}
	
	private static @NotNull Token createToken(@NotNull String value, @NotNull TokenPosition start, @NotNull TokenPosition end) {
		return new SimpleToken(createAnyDefinition(), value, start, end);
	}
	
	private static @NotNull Token createUnpositionedToken(@NotNull String value) {
		return SimpleToken.createUnpositioned(createAnyDefinition(), value);
	}
	
	@Test
	void constructorWithNullTokens() {
		TokenDefinition definition = createAnyDefinition();
		
		assertThrows(NullPointerException.class, () -> new TokenGroup(null, definition));
	}
	
	@Test
	void constructorWithNullDefinition() {
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		
		assertThrows(NullPointerException.class, () -> new TokenGroup(List.of(token1, token2), null));
	}
	
	@Test
	void constructorWithNullTokenInList() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		
		assertThrows(NullPointerException.class, () -> new TokenGroup(List.of(token1, null), definition));
	}
	
	@Test
	void constructorWithEmptyTokenList() {
		TokenDefinition definition = createAnyDefinition();
		
		assertThrows(IllegalArgumentException.class, () -> new TokenGroup(Collections.emptyList(), definition));
	}
	
	@Test
	void constructorWithSingleToken() {
		TokenDefinition definition = createAnyDefinition();
		Token token = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		
		assertThrows(IllegalArgumentException.class, () -> new TokenGroup(List.of(token), definition));
	}
	
	@Test
	void constructorWithNonMatchingDefinition() {
		TokenDefinition numberDefinition = createNumberDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		
		assertThrows(IllegalArgumentException.class, () -> new TokenGroup(List.of(token1, token2), numberDefinition));
	}
	
	@Test
	void constructorWithPositionMismatch() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("AB", new TokenPosition(0, 0, 0), new TokenPosition(0, 1, 1));
		Token token2 = createToken("CD", new TokenPosition(0, 4, 4), new TokenPosition(0, 5, 5));
		
		assertDoesNotThrow(() -> new TokenGroup(List.of(token1, token2), definition));
	}
	
	@Test
	void constructorWithValidPositions() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("AB", new TokenPosition(0, 0, 0), new TokenPosition(0, 1, 1));
		Token token2 = createToken("CD", new TokenPosition(0, 2, 2), new TokenPosition(0, 3, 3));
		
		assertDoesNotThrow(() -> new TokenGroup(List.of(token1, token2), definition));
	}
	
	@Test
	void constructorWithUnpositionedTokens() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createUnpositionedToken("hello");
		Token token2 = createUnpositionedToken("world");
		
		assertDoesNotThrow(() -> new TokenGroup(List.of(token1, token2), definition));
	}
	
	@Test
	void constructorWithMixedPositionedTokens() {
		TokenDefinition definition = createAnyDefinition();
		Token positionedToken = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token unpositionedToken = createUnpositionedToken("world");
		
		assertDoesNotThrow(() -> new TokenGroup(List.of(positionedToken, unpositionedToken), definition));
	}
	
	@Test
	void constructorWithMatchingDefinition() {
		TokenDefinition numberDefinition = createNumberDefinition();
		Token token1 = createToken("123", new TokenPosition(0, 0, 0), new TokenPosition(0, 2, 2));
		Token token2 = createToken("456", new TokenPosition(0, 3, 3), new TokenPosition(0, 5, 5));
		
		assertDoesNotThrow(() -> new TokenGroup(List.of(token1, token2), numberDefinition));
	}
	
	@Test
	void constructorWithComplexValidTokens() {
		TokenDefinition textDefinition = createTextDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken(" ", new TokenPosition(0, 5, 5), new TokenPosition(0, 5, 5));
		Token token3 = createToken("world", new TokenPosition(0, 6, 6), new TokenPosition(0, 10, 10));
		
		assertDoesNotThrow(() -> new TokenGroup(List.of(token1, token2, token3), textDefinition));
	}
	
	@Test
	void tokensReturnsCorrectList() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		List<Token> originalTokens = List.of(token1, token2);
		TokenGroup group = new TokenGroup(originalTokens, definition);
		
		assertEquals(originalTokens, group.tokens());
		assertEquals(2, group.tokens().size());
		assertEquals(token1, group.tokens().get(0));
		assertEquals(token2, group.tokens().get(1));
	}
	
	@Test
	void tokensReturnsImmutableList() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		assertThrows(UnsupportedOperationException.class, () -> group.tokens().add(createToken("!", new TokenPosition(0, 10, 10), new TokenPosition(0, 10, 10))));
		assertThrows(UnsupportedOperationException.class, () -> group.tokens().remove(0));
		assertThrows(UnsupportedOperationException.class, () -> group.tokens().clear());
	}
	
	@Test
	void tokensWithMultipleTokens() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("A", new TokenPosition(0, 0, 0), new TokenPosition(0, 0, 0));
		Token token2 = createToken("B", new TokenPosition(0, 1, 1), new TokenPosition(0, 1, 1));
		Token token3 = createToken("C", new TokenPosition(0, 2, 2), new TokenPosition(0, 2, 2));
		Token token4 = createToken("D", new TokenPosition(0, 3, 3), new TokenPosition(0, 3, 3));
		List<Token> tokens = List.of(token1, token2, token3, token4);
		TokenGroup group = new TokenGroup(tokens, definition);
		
		assertEquals(4, group.tokens().size());
		assertEquals(tokens, group.tokens());
	}
	
	@Test
	void definitionReturnsCorrectValue() {
		TokenDefinition numberDefinition = createNumberDefinition();
		Token token1 = createToken("123", new TokenPosition(0, 0, 0), new TokenPosition(0, 2, 2));
		Token token2 = createToken("456", new TokenPosition(0, 3, 3), new TokenPosition(0, 5, 5));
		TokenGroup group = new TokenGroup(List.of(token1, token2), numberDefinition);
		
		assertEquals(numberDefinition, group.definition());
	}
	
	@Test
	void definitionWithDifferentTypes() {
		TokenDefinition textDefinition = createTextDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken(" ", new TokenPosition(0, 5, 5), new TokenPosition(0, 5, 5));
		Token token3 = createToken("world", new TokenPosition(0, 6, 6), new TokenPosition(0, 10, 10));
		TokenGroup group = new TokenGroup(List.of(token1, token2, token3), textDefinition);
		
		assertEquals(textDefinition, group.definition());
		assertNotEquals(createNumberDefinition(), group.definition());
	}
	
	@Test
	void valueWithTwoTokens() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		assertEquals("helloworld", group.value());
	}
	
	@Test
	void valueWithThreeTokens() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken(" ", new TokenPosition(0, 5, 5), new TokenPosition(0, 5, 5));
		Token token3 = createToken("world", new TokenPosition(0, 6, 6), new TokenPosition(0, 10, 10));
		TokenGroup group = new TokenGroup(List.of(token1, token2, token3), definition);
		
		assertEquals("hello world", group.value());
	}
	
	@Test
	void valueWithNumberTokens() {
		TokenDefinition definition = createNumberDefinition();
		Token token1 = createToken("123", new TokenPosition(0, 0, 0), new TokenPosition(0, 2, 2));
		Token token2 = createToken("456", new TokenPosition(0, 3, 3), new TokenPosition(0, 5, 5));
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		assertEquals("123456", group.value());
	}
	
	@Test
	void valueWithEmptyTokens() {
		TokenDefinition emptyDefinition = word -> word.isEmpty() || word.matches("[a-z]+");
		Token token1 = createToken("", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
		Token token2 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		TokenGroup group = new TokenGroup(List.of(token1, token2), emptyDefinition);
		
		assertEquals("hello", group.value());
	}
	
	@Test
	void valueWithSpecialCharacters() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("!@#", new TokenPosition(0, 0, 0), new TokenPosition(0, 2, 2));
		Token token2 = createToken("$%^", new TokenPosition(0, 3, 3), new TokenPosition(0, 5, 5));
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		assertEquals("!@#$%^", group.value());
	}
	
	@Test
	void valueWithManyTokens() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("A", new TokenPosition(0, 0, 0), new TokenPosition(0, 0, 0));
		Token token2 = createToken("B", new TokenPosition(0, 1, 1), new TokenPosition(0, 1, 1));
		Token token3 = createToken("C", new TokenPosition(0, 2, 2), new TokenPosition(0, 2, 2));
		Token token4 = createToken("D", new TokenPosition(0, 3, 3), new TokenPosition(0, 3, 3));
		Token token5 = createToken("E", new TokenPosition(0, 4, 4), new TokenPosition(0, 4, 4));
		TokenGroup group = new TokenGroup(List.of(token1, token2, token3, token4, token5), definition);
		
		assertEquals("ABCDE", group.value());
	}
	
	@Test
	void startPositionFromFirstToken() {
		TokenDefinition definition = createAnyDefinition();
		TokenPosition firstStart = new TokenPosition(0, 0, 0);
		Token token1 = createToken("hello", firstStart, new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		assertEquals(firstStart, group.startPosition());
		assertEquals(0, group.startPosition().line());
		assertEquals(0, group.startPosition().character());
		assertEquals(0, group.startPosition().characterInLine());
	}
	
	@Test
	void startPositionWithDifferentPositions() {
		TokenDefinition definition = createAnyDefinition();
		TokenPosition firstStart = new TokenPosition(5, 10, 100);
		Token token1 = createToken("test", firstStart, new TokenPosition(5, 13, 103));
		Token token2 = createToken("data", new TokenPosition(5, 14, 104), new TokenPosition(5, 17, 107));
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		assertEquals(firstStart, group.startPosition());
		assertEquals(5, group.startPosition().line());
		assertEquals(10, group.startPosition().characterInLine());
		assertEquals(100, group.startPosition().character());
	}
	
	@Test
	void startPositionWithUnpositionedFirst() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createUnpositionedToken("hello");
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		assertEquals(TokenPosition.UNPOSITIONED, group.startPosition());
	}
	
	@Test
	void endPositionFromLastToken() {
		TokenDefinition definition = createAnyDefinition();
		TokenPosition lastEnd = new TokenPosition(0, 9, 9);
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), lastEnd);
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		assertEquals(lastEnd, group.endPosition());
		assertEquals(0, group.endPosition().line());
		assertEquals(9, group.endPosition().character());
		assertEquals(9, group.endPosition().characterInLine());
	}
	
	@Test
	void endPositionWithDifferentPositions() {
		TokenDefinition definition = createAnyDefinition();
		TokenPosition lastEnd = new TokenPosition(3, 13, 188);
		Token token1 = createToken("start", new TokenPosition(3, 0, 175), new TokenPosition(3, 4, 179));
		Token token2 = createToken("middle", new TokenPosition(3, 5, 180), new TokenPosition(3, 10, 185));
		Token token3 = createToken("end", new TokenPosition(3, 11, 186), lastEnd);
		TokenGroup group = new TokenGroup(List.of(token1, token2, token3), definition);
		
		assertEquals(lastEnd, group.endPosition());
		assertEquals(3, group.endPosition().line());
		assertEquals(13, group.endPosition().characterInLine());
		assertEquals(188, group.endPosition().character());
	}
	
	@Test
	void endPositionWithUnpositionedLast() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createUnpositionedToken("world");
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		assertEquals(TokenPosition.UNPOSITIONED, group.endPosition());
	}
	
	@Test
	void toStringContainsTokenGroupInfo() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		String groupString = group.toString();
		assertTrue(groupString.contains("TokenGroup"));
		assertTrue(groupString.contains("tokens"));
		assertTrue(groupString.contains("definition"));
	}
	
	@Test
	void equalTokenGroupsHaveSameHashCode() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		List<Token> tokens = List.of(token1, token2);
		
		TokenGroup group1 = new TokenGroup(tokens, definition);
		TokenGroup group2 = new TokenGroup(tokens, definition);
		
		assertEquals(group1.hashCode(), group2.hashCode());
	}
	
	@Test
	void tokenGroupImplementsInterface() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		Token token2 = createToken("world", new TokenPosition(0, 5, 5), new TokenPosition(0, 9, 9));
		TokenGroup group = new TokenGroup(List.of(token1, token2), definition);
		
		assertInstanceOf(Token.class, group);
		assertEquals(definition, group.definition());
		assertEquals("helloworld", group.value());
		assertNotNull(group.startPosition());
		assertNotNull(group.endPosition());
	}
	
	@Test
	void tokenGroupWithLargeNumberOfTokens() {
		TokenDefinition definition = createAnyDefinition();
		Token[] tokens = new Token[100];
		
		for (int i = 0; i < 100; i++) {
			tokens[i] = createToken(String.valueOf(i % 10), new TokenPosition(0, i, i), new TokenPosition(0, i, i));
		}
		
		TokenGroup group = new TokenGroup(List.of(tokens), definition);
		assertEquals(100, group.tokens().size());
		assertEquals(100, group.value().length());
	}
	
	@Test
	void tokenGroupWithVaryingTokenLengths() {
		TokenDefinition definition = createAnyDefinition();
		Token shortToken = createToken("a", new TokenPosition(0, 0, 0), new TokenPosition(0, 0, 0));
		Token mediumToken = createToken("hello", new TokenPosition(0, 1, 1), new TokenPosition(0, 5, 5));
		Token longToken = createToken("verylongtoken", new TokenPosition(0, 6, 6), new TokenPosition(0, 18, 18));
		
		TokenGroup group = new TokenGroup(List.of(shortToken, mediumToken, longToken), definition);
		assertEquals("ahelloverylongtoken", group.value());
		assertEquals(3, group.tokens().size());
	}
	
	@Test
	void tokenGroupAcrossMultipleLines() {
		TokenDefinition definition = createAnyDefinition();
		Token token1 = createToken("line1", TokenPosition.UNPOSITIONED, new TokenPosition(0, 4, 4));
		Token token2 = createToken("line2", new TokenPosition(1, 0, 10), new TokenPosition(1, 4, 14));
		Token token3 = createToken("line3", new TokenPosition(2, 0, 20), new TokenPosition(2, 4, 24));
		
		TokenGroup group = new TokenGroup(List.of(token1, token2, token3), definition);
		assertEquals("line1line2line3", group.value());
		assertEquals(TokenPosition.UNPOSITIONED, group.startPosition());
		assertEquals(2, group.endPosition().line());
	}
}
