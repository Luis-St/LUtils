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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EscapedToken}.<br>
 *
 * @author Luis-St
 */
class EscapedTokenTest {
	
	private static @NotNull TokenDefinition createEscapedDefinition() {
		return word -> word.length() == 2 && word.charAt(0) == '\\';
	}
	
	@Test
	void constructorWithNullDefinition() {
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		assertThrows(NullPointerException.class, () -> new EscapedToken(null, "\\n", start, end));
	}
	
	@Test
	void constructorWithNullValue() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		assertThrows(NullPointerException.class, () -> new EscapedToken(definition, null, start, end));
	}
	
	@Test
	void constructorWithNullStartPosition() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		assertThrows(NullPointerException.class, () -> new EscapedToken(definition, "\\n", null, end));
	}
	
	@Test
	void constructorWithNullEndPosition() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		
		assertThrows(NullPointerException.class, () -> new EscapedToken(definition, "\\n", start, null));
	}
	
	@Test
	void constructorWithValidParameters() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\n", start, end));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\t", start, end));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\\\", start, end));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\\"", start, end));
	}
	
	@Test
	void constructorWithInvalidValueLength() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "n", start, end));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "\\\\n", start, end));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "", start, end));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "abc", start, end));
	}
	
	@Test
	void constructorWithInvalidValueFormat() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "nn", start, end));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "n\\", start, end));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "ab", start, end));
	}
	
	@Test
	void constructorWithNonMatchingDefinition() {
		TokenDefinition wrongDefinition = word -> word.matches("[a-z]+");
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(wrongDefinition, "\\n", start, end));
	}
	
	@Test
	void constructorWithInconsistentPositions() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition wrongEnd = new TokenPosition(0, 2, 2);
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "\\n", start, wrongEnd));
	}
	
	@Test
	void constructorWithUnpositionedTokens() {
		TokenDefinition definition = createEscapedDefinition();
		
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\n", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\t", new TokenPosition(0, 0, 0), TokenPosition.UNPOSITIONED));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\\\", TokenPosition.UNPOSITIONED, new TokenPosition(0, 1, 1)));
	}
	
	@Test
	void createUnpositionedWithNullDefinition() {
		assertThrows(NullPointerException.class, () -> EscapedToken.createUnpositioned(null, "\\n"));
	}
	
	@Test
	void createUnpositionedWithNullValue() {
		TokenDefinition definition = createEscapedDefinition();
		
		assertThrows(NullPointerException.class, () -> EscapedToken.createUnpositioned(definition, null));
	}
	
	@Test
	void createUnpositionedWithInvalidValue() {
		TokenDefinition definition = createEscapedDefinition();
		
		assertThrows(IllegalArgumentException.class, () -> EscapedToken.createUnpositioned(definition, "n"));
		assertThrows(IllegalArgumentException.class, () -> EscapedToken.createUnpositioned(definition, "\\\\n"));
		assertThrows(IllegalArgumentException.class, () -> EscapedToken.createUnpositioned(definition, "nn"));
	}
	
	@Test
	void createUnpositionedWithValidValues() {
		TokenDefinition definition = createEscapedDefinition();
		
		EscapedToken token1 = assertDoesNotThrow(() -> EscapedToken.createUnpositioned(definition, "\\n"));
		assertEquals(TokenPosition.UNPOSITIONED, token1.startPosition());
		assertEquals(TokenPosition.UNPOSITIONED, token1.endPosition());
		
		EscapedToken token2 = assertDoesNotThrow(() -> EscapedToken.createUnpositioned(definition, "\\t"));
		assertEquals("\\t", token2.value());
		
		EscapedToken token3 = assertDoesNotThrow(() -> EscapedToken.createUnpositioned(definition, "\\\\"));
		assertEquals("\\\\", token3.value());
	}
	
	@Test
	void definitionReturnsCorrectValue() {
		TokenDefinition definition = createEscapedDefinition();
		EscapedToken token = new EscapedToken(definition, "\\n", new TokenPosition(0, 0, 0), new TokenPosition(0, 1, 1));
		
		assertEquals(definition, token.definition());
	}
	
	@Test
	void valueReturnsCorrectString() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		assertEquals("\\n", new EscapedToken(definition, "\\n", start, end).value());
		assertEquals("\\t", new EscapedToken(definition, "\\t", start, end).value());
		assertEquals("\\\\", new EscapedToken(definition, "\\\\", start, end).value());
		assertEquals("\\\"", new EscapedToken(definition, "\\\"", start, end).value());
		assertEquals("\\'", new EscapedToken(definition, "\\'", start, end).value());
		assertEquals("\\r", new EscapedToken(definition, "\\r", start, end).value());
		assertEquals("\\0", new EscapedToken(definition, "\\0", start, end).value());
	}
	
	@Test
	void valueWithSpecialEscapeSequences() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		assertEquals("\\n", new EscapedToken(definition, "\\n", start, end).value());
		assertEquals("\\t", new EscapedToken(definition, "\\t", start, end).value());
		assertEquals("\\r", new EscapedToken(definition, "\\r", start, end).value());
		assertEquals("\\b", new EscapedToken(definition, "\\b", start, end).value());
		assertEquals("\\f", new EscapedToken(definition, "\\f", start, end).value());
		
		assertEquals("\\\"", new EscapedToken(definition, "\\\"", start, end).value());
		assertEquals("\\'", new EscapedToken(definition, "\\'", start, end).value());
		
		assertEquals("\\\\", new EscapedToken(definition, "\\\\", start, end).value());
	}
	
	@Test
	void startPositionReturnsCorrectValue() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(5, 10, 25);
		TokenPosition end = new TokenPosition(5, 11, 26);
		EscapedToken token = new EscapedToken(definition, "\\n", start, end);
		
		assertEquals(start, token.startPosition());
		assertEquals(5, token.startPosition().line());
		assertEquals(10, token.startPosition().characterInLine());
		assertEquals(25, token.startPosition().character());
	}
	
	@Test
	void endPositionReturnsCorrectValue() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(3, 7, 15);
		TokenPosition end = new TokenPosition(3, 8, 16);
		EscapedToken token = new EscapedToken(definition, "\\t", start, end);
		
		assertEquals(end, token.endPosition());
		assertEquals(3, token.endPosition().line());
		assertEquals(8, token.endPosition().characterInLine());
		assertEquals(16, token.endPosition().character());
	}
	
	@Test
	void positionsWithUnpositionedToken() {
		TokenDefinition definition = createEscapedDefinition();
		EscapedToken token = EscapedToken.createUnpositioned(definition, "\\n");
		
		assertEquals(TokenPosition.UNPOSITIONED, token.startPosition());
		assertEquals(TokenPosition.UNPOSITIONED, token.endPosition());
		assertFalse(token.startPosition().isPositioned());
		assertFalse(token.endPosition().isPositioned());
	}
	
	@Test
	void positionsSpanCorrectRange() {
		TokenDefinition definition = createEscapedDefinition();
		
		EscapedToken token1 = new EscapedToken(definition, "\\n", new TokenPosition(0, 0, 0), new TokenPosition(0, 1, 1));
		assertEquals(0, token1.startPosition().character());
		assertEquals(1, token1.endPosition().character());
		
		EscapedToken token2 = new EscapedToken(definition, "\\t", new TokenPosition(2, 5, 10), new TokenPosition(2, 6, 11));
		assertEquals(10, token2.startPosition().character());
		assertEquals(11, token2.endPosition().character());
		
		EscapedToken token3 = new EscapedToken(definition, "\\\\", new TokenPosition(1, 0, 50), new TokenPosition(1, 1, 51));
		assertEquals(50, token3.startPosition().character());
		assertEquals(51, token3.endPosition().character());
	}
	
	@Test
	void toStringContainsEscapeSequence() {
		TokenDefinition definition = createEscapedDefinition();
		EscapedToken token = new EscapedToken(definition, "\\n", new TokenPosition(0, 0, 0), new TokenPosition(0, 1, 1));
		
		String tokenString = token.toString();
		
		assertTrue(tokenString.contains("EscapedToken"));
		assertTrue(tokenString.contains("\\n") || tokenString.contains("\\\\n"));
		assertTrue(tokenString.contains("definition"));
		assertTrue(tokenString.contains("value"));
		assertTrue(tokenString.contains("startPosition"));
		assertTrue(tokenString.contains("endPosition"));
	}
	
	@Test
	void toStringWithSpecialCharacters() {
		TokenDefinition definition = createEscapedDefinition();
		
		EscapedToken tabToken = new EscapedToken(definition, "\\t", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
		String tabString = tabToken.toString();
		assertTrue(tabString.contains("\\t") || tabString.contains("\\\\t"));
		
		EscapedToken newlineToken = new EscapedToken(definition, "\\n", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
		String newlineString = newlineToken.toString();
		assertTrue(newlineString.contains("\\n") || newlineString.contains("\\\\n"));
	}
	
	@Test
	void equalTokensHaveSameHashCode() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		EscapedToken token1 = new EscapedToken(definition, "\\n", start, end);
		EscapedToken token2 = new EscapedToken(definition, "\\n", start, end);
		
		assertEquals(token1.hashCode(), token2.hashCode());
	}
	
	@Test
	void differentValuesWithSameEscapeFormat() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\a", start, end));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\z", start, end));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\1", start, end));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\9", start, end));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\!", start, end));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\@", start, end));
	}
	
	@Test
	void tokenImplementsInterface() {
		TokenDefinition definition = createEscapedDefinition();
		EscapedToken token = EscapedToken.createUnpositioned(definition, "\\n");
		
		assertInstanceOf(Token.class, token);
		assertEquals(definition, token.definition());
		assertEquals("\\n", token.value());
		assertNotNull(token.startPosition());
		assertNotNull(token.endPosition());
	}
	
	@Test
	void immutablePositions() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 1, 1);
		EscapedToken token = new EscapedToken(definition, "\\n", start, end);
		
		assertSame(start, token.startPosition());
		assertSame(end, token.endPosition());
	}
}
