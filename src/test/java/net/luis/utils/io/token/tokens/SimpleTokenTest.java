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
 * Test class for {@link SimpleToken}.<br>
 *
 * @author Luis-St
 */
class SimpleTokenTest {
	
	private static @NotNull TokenDefinition createNumberDefinition() {
		return word -> word.matches("\\d+");
	}
	
	@Test
	void constructorWithNullDefinition() {
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 2, 2);
		
		assertThrows(NullPointerException.class, () -> new SimpleToken(null, "123", start, end));
	}
	
	@Test
	void constructorWithNullValue() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 2, 2);
		
		assertThrows(NullPointerException.class, () -> new SimpleToken(definition, null, start, end));
	}
	
	@Test
	void constructorWithNullStartPosition() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition end = new TokenPosition(0, 2, 2);
		
		assertThrows(NullPointerException.class, () -> new SimpleToken(definition, "123", null, end));
	}
	
	@Test
	void constructorWithNullEndPosition() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		
		assertThrows(NullPointerException.class, () -> new SimpleToken(definition, "123", start, null));
	}
	
	@Test
	void constructorWithValidParameters() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 2, 2);
		
		assertDoesNotThrow(() -> new SimpleToken(definition, "123", start, end));
		assertDoesNotThrow(() -> new SimpleToken(definition, "456", start, end));
		assertDoesNotThrow(() -> new SimpleToken(definition, "0", new TokenPosition(0, 0, 0), new TokenPosition(0, 0, 0)));
	}
	
	@Test
	void constructorWithNonMatchingDefinition() {
		TokenDefinition numberDefinition = createNumberDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 2, 2);
		
		assertThrows(IllegalArgumentException.class, () -> new SimpleToken(numberDefinition, "abc", start, end));
		assertThrows(IllegalArgumentException.class, () -> new SimpleToken(numberDefinition, "12a", start, end));
		assertThrows(IllegalArgumentException.class, () -> new SimpleToken(numberDefinition, "", start, end));
	}
	
	@Test
	void constructorWithInconsistentPositions() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition wrongEnd = new TokenPosition(0, 3, 3);
		
		assertDoesNotThrow(() -> new SimpleToken(definition, "123", start, wrongEnd));
	}
	
	@Test
	void constructorWithCorrectPositions() {
		TokenDefinition definition = createNumberDefinition();
		
		TokenPosition start1 = new TokenPosition(0, 0, 0);
		TokenPosition end1 = new TokenPosition(0, 0, 0);
		assertDoesNotThrow(() -> new SimpleToken(definition, "5", start1, end1));
		
		TokenPosition start2 = new TokenPosition(0, 5, 5);
		TokenPosition end2 = new TokenPosition(0, 7, 7);
		assertDoesNotThrow(() -> new SimpleToken(definition, "123", start2, end2));
		
		TokenPosition start3 = new TokenPosition(2, 0, 100);
		TokenPosition end3 = new TokenPosition(2, 9, 109);
		assertDoesNotThrow(() -> new SimpleToken(definition, "1234567890", start3, end3));
	}
	
	@Test
	void constructorWithUnpositionedTokens() {
		TokenDefinition definition = createNumberDefinition();
		
		assertDoesNotThrow(() -> new SimpleToken(definition, "123", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED));
		assertDoesNotThrow(() -> new SimpleToken(definition, "456", new TokenPosition(0, 0, 0), TokenPosition.UNPOSITIONED));
		assertDoesNotThrow(() -> new SimpleToken(definition, "789", TokenPosition.UNPOSITIONED, new TokenPosition(0, 2, 2)));
	}
	
	@Test
	void createUnpositionedWithNullDefinition() {
		assertThrows(NullPointerException.class, () -> SimpleToken.createUnpositioned(null, "123"));
	}
	
	@Test
	void createUnpositionedWithNullValue() {
		TokenDefinition definition = createNumberDefinition();
		
		assertThrows(NullPointerException.class, () -> SimpleToken.createUnpositioned(definition, null));
	}
	
	@Test
	void createUnpositionedWithNonMatchingDefinition() {
		TokenDefinition numberDefinition = createNumberDefinition();
		
		assertThrows(IllegalArgumentException.class, () -> SimpleToken.createUnpositioned(numberDefinition, "abc"));
		assertThrows(IllegalArgumentException.class, () -> SimpleToken.createUnpositioned(numberDefinition, "12a"));
	}
	
	@Test
	void createUnpositionedWithValidValues() {
		TokenDefinition numberDefinition = createNumberDefinition();
		
		SimpleToken token1 = assertDoesNotThrow(() -> SimpleToken.createUnpositioned(numberDefinition, "123"));
		assertEquals(TokenPosition.UNPOSITIONED, token1.startPosition());
		assertEquals(TokenPosition.UNPOSITIONED, token1.endPosition());
		assertEquals("123", token1.value());
		
		SimpleToken token2 = assertDoesNotThrow(() -> SimpleToken.createUnpositioned(numberDefinition, "0"));
		assertEquals("0", token2.value());
		
		SimpleToken token3 = assertDoesNotThrow(() -> SimpleToken.createUnpositioned(numberDefinition, "999999"));
		assertEquals("999999", token3.value());
	}
	
	@Test
	void definitionReturnsCorrectValue() {
		TokenDefinition definition = createNumberDefinition();
		SimpleToken token = new SimpleToken(definition, "123", new TokenPosition(0, 0, 0), new TokenPosition(0, 2, 2));
		
		assertEquals(definition, token.definition());
	}
	
	@Test
	void valueReturnsCorrectString() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 2, 2);
		
		assertEquals("123", new SimpleToken(definition, "123", start, end).value());
		assertEquals("456", new SimpleToken(definition, "456", start, end).value());
		assertEquals("0", new SimpleToken(definition, "0", new TokenPosition(0, 0, 0), new TokenPosition(0, 0, 0)).value());
		assertEquals("999", new SimpleToken(definition, "999", start, end).value());
	}
	
	@Test
	void valueWithDifferentContentTypes() {
		TokenDefinition textDefinition = word -> word.matches("[a-zA-Z]+");
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 4, 4);
		
		assertEquals("hello", new SimpleToken(textDefinition, "hello", start, end).value());
		assertEquals("WORLD", new SimpleToken(textDefinition, "WORLD", start, end).value());
		assertEquals("Test", new SimpleToken(textDefinition, "Test", start, new TokenPosition(0, 3, 3)).value());
		
		TokenDefinition symbolDefinition = word -> word.matches("[!@#$%^&*()]+");
		TokenPosition symbolStart = new TokenPosition(0, 0, 0);
		TokenPosition symbolEnd = new TokenPosition(0, 2, 2);
		
		assertEquals("!!!", new SimpleToken(symbolDefinition, "!!!", symbolStart, symbolEnd).value());
		assertEquals("@#$", new SimpleToken(symbolDefinition, "@#$", symbolStart, symbolEnd).value());
	}
	
	@Test
	void startPositionReturnsCorrectValue() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition start = new TokenPosition(5, 10, 25);
		TokenPosition end = new TokenPosition(5, 12, 27);
		SimpleToken token = new SimpleToken(definition, "123", start, end);
		
		assertEquals(start, token.startPosition());
		assertEquals(5, token.startPosition().line());
		assertEquals(10, token.startPosition().characterInLine());
		assertEquals(25, token.startPosition().character());
	}
	
	@Test
	void endPositionReturnsCorrectValue() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition start = new TokenPosition(3, 7, 15);
		TokenPosition end = new TokenPosition(3, 9, 17);
		SimpleToken token = new SimpleToken(definition, "456", start, end);
		
		assertEquals(end, token.endPosition());
		assertEquals(3, token.endPosition().line());
		assertEquals(9, token.endPosition().characterInLine());
		assertEquals(17, token.endPosition().character());
	}
	
	@Test
	void positionsWithUnpositionedToken() {
		TokenDefinition definition = createNumberDefinition();
		SimpleToken token = SimpleToken.createUnpositioned(definition, "123");
		
		assertEquals(TokenPosition.UNPOSITIONED, token.startPosition());
		assertEquals(TokenPosition.UNPOSITIONED, token.endPosition());
		assertFalse(token.startPosition().isPositioned());
		assertFalse(token.endPosition().isPositioned());
	}
	
	@Test
	void positionsSpanCorrectRange() {
		TokenDefinition definition = createNumberDefinition();
		
		SimpleToken token1 = new SimpleToken(definition, "5", new TokenPosition(0, 0, 0), new TokenPosition(0, 0, 0));
		assertEquals(0, token1.startPosition().character());
		assertEquals(0, token1.endPosition().character());
		
		SimpleToken token2 = new SimpleToken(definition, "123", new TokenPosition(0, 5, 10), new TokenPosition(0, 7, 12));
		assertEquals(10, token2.startPosition().character());
		assertEquals(12, token2.endPosition().character());
		
		SimpleToken token3 = new SimpleToken(definition, "123456789", new TokenPosition(1, 0, 50), new TokenPosition(1, 8, 58));
		assertEquals(50, token3.startPosition().character());
		assertEquals(58, token3.endPosition().character());
	}
	
	@Test
	void positionsAcrossLines() {
		TokenDefinition definition = createNumberDefinition();
		
		SimpleToken sameLine = new SimpleToken(definition, "123", new TokenPosition(2, 5, 25), new TokenPosition(2, 7, 27));
		assertEquals(2, sameLine.startPosition().line());
		assertEquals(2, sameLine.endPosition().line());
		
		SimpleToken singleChar = new SimpleToken(definition, "9", new TokenPosition(5, 0, 100), new TokenPosition(5, 0, 100));
		assertEquals(5, singleChar.startPosition().line());
		assertEquals(5, singleChar.endPosition().line());
		assertEquals(0, singleChar.startPosition().characterInLine());
		assertEquals(0, singleChar.endPosition().characterInLine());
	}
	
	@Test
	void toStringContainsTokenInfo() {
		TokenDefinition definition = createNumberDefinition();
		SimpleToken token = new SimpleToken(definition, "123", new TokenPosition(0, 0, 0), new TokenPosition(0, 2, 2));
		
		String tokenString = token.toString();
		
		assertTrue(tokenString.contains("SimpleToken"));
		assertTrue(tokenString.contains("123"));
		assertTrue(tokenString.contains("definition"));
		assertTrue(tokenString.contains("value"));
		assertTrue(tokenString.contains("startPosition"));
		assertTrue(tokenString.contains("endPosition"));
	}
	
	@Test
	void toStringWithSpecialCharacters() {
		TokenDefinition specialDefinition = word -> "\t".equals(word) || "\n".equals(word) || " ".equals(word);
		
		SimpleToken tabToken = new SimpleToken(specialDefinition, "\t", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
		String tabString = tabToken.toString();
		assertTrue(tabString.contains("\\t"));
		
		SimpleToken newlineToken = new SimpleToken(specialDefinition, "\n", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
		String newlineString = newlineToken.toString();
		assertTrue(newlineString.contains("\\n"));
	}
	
	@Test
	void toStringWithEmptyValue() {
		TokenDefinition emptyDefinition = String::isEmpty;
		SimpleToken emptyToken = new SimpleToken(emptyDefinition, "", TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
		
		String tokenString = emptyToken.toString();
		assertTrue(tokenString.contains("SimpleToken"));
		assertTrue(tokenString.contains("value="));
	}
	
	@Test
	void equalTokensHaveSameHashCode() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 2, 2);
		
		SimpleToken token1 = new SimpleToken(definition, "123", start, end);
		SimpleToken token2 = new SimpleToken(definition, "123", start, end);
		
		assertEquals(token1.hashCode(), token2.hashCode());
	}
	
	@Test
	void tokensWithDifferentValues() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 2, 2);
		
		SimpleToken token1 = new SimpleToken(definition, "123", start, end);
		SimpleToken token2 = new SimpleToken(definition, "456", start, end);
		
		assertNotEquals(token1.value(), token2.value());
	}
	
	@Test
	void tokensWithDifferentDefinitions() {
		TokenDefinition numberDef = createNumberDefinition();
		TokenDefinition textDef = word -> word.matches("[a-z]+");
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 2, 2);
		
		SimpleToken numberToken = new SimpleToken(numberDef, "123", start, end);
		SimpleToken textToken = new SimpleToken(textDef, "abc", start, end);
		
		assertNotEquals(numberToken.definition(), textToken.definition());
	}
	
	@Test
	void tokenImplementsInterface() {
		TokenDefinition definition = createNumberDefinition();
		SimpleToken token = SimpleToken.createUnpositioned(definition, "123");
		
		assertInstanceOf(Token.class, token);
		assertEquals(definition, token.definition());
		assertEquals("123", token.value());
		assertNotNull(token.startPosition());
		assertNotNull(token.endPosition());
	}
	
	@Test
	void immutablePositions() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 2, 2);
		SimpleToken token = new SimpleToken(definition, "123", start, end);
		
		assertSame(start, token.startPosition());
		assertSame(end, token.endPosition());
	}
	
	@Test
	void tokensWithVariousLengths() {
		TokenDefinition anyDefinition = word -> true;
		
		SimpleToken single = new SimpleToken(anyDefinition, "a", new TokenPosition(0, 0, 0), new TokenPosition(0, 0, 0));
		assertEquals(1, single.value().length());
		
		SimpleToken shortToken = new SimpleToken(anyDefinition, "hello", new TokenPosition(0, 0, 0), new TokenPosition(0, 4, 4));
		assertEquals(5, shortToken.value().length());
		
		String longValue = "a".repeat(100);
		SimpleToken longToken = new SimpleToken(anyDefinition, longValue, new TokenPosition(0, 0, 0), new TokenPosition(0, 99, 99));
		assertEquals(100, longToken.value().length());
	}
	
	@Test
	void tokensWithSpecialValues() {
		TokenDefinition anyDefinition = word -> true;
		TokenPosition start = new TokenPosition(0, 0, 0);
		TokenPosition end = new TokenPosition(0, 0, 0);
		
		TokenDefinition emptyDef = String::isEmpty;
		SimpleToken emptyToken = new SimpleToken(emptyDef, "", start, TokenPosition.UNPOSITIONED);
		assertEquals("", emptyToken.value());
		
		TokenDefinition whitespaceDef = word -> word.trim().isEmpty();
		SimpleToken spaceToken = new SimpleToken(whitespaceDef, " ", start, end);
		assertEquals(" ", spaceToken.value());
		
		TokenDefinition specialDef = word -> word.matches("[!@#$%^&*()]+");
		SimpleToken specialToken = new SimpleToken(specialDef, "!@#", start, new TokenPosition(0, 2, 2));
		assertEquals("!@#", specialToken.value());
	}
}
