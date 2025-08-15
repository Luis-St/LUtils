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
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(NullPointerException.class, () -> new SimpleToken(null, "123", position));
	}
	
	@Test
	void constructorWithNullValue() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(NullPointerException.class, () -> new SimpleToken(definition, null, position));
	}
	
	@Test
	void constructorWithNullPosition() {
		TokenDefinition definition = createNumberDefinition();
		
		assertThrows(NullPointerException.class, () -> new SimpleToken(definition, "123", null));
	}
	
	@Test
	void constructorWithValidParameters() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertDoesNotThrow(() -> new SimpleToken(definition, "123", position));
		assertDoesNotThrow(() -> new SimpleToken(definition, "456", position));
		assertDoesNotThrow(() -> new SimpleToken(definition, "0", new TokenPosition(0, 0, 0)));
	}
	
	@Test
	void constructorWithNonMatchingDefinition() {
		TokenDefinition numberDefinition = createNumberDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(IllegalArgumentException.class, () -> new SimpleToken(numberDefinition, "abc", position));
		assertThrows(IllegalArgumentException.class, () -> new SimpleToken(numberDefinition, "12a", position));
		assertThrows(IllegalArgumentException.class, () -> new SimpleToken(numberDefinition, "", position));
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
		assertEquals(TokenPosition.UNPOSITIONED, token1.position());
		assertEquals("123", token1.value());
		
		SimpleToken token2 = assertDoesNotThrow(() -> SimpleToken.createUnpositioned(numberDefinition, "0"));
		assertEquals("0", token2.value());
		
		SimpleToken token3 = assertDoesNotThrow(() -> SimpleToken.createUnpositioned(numberDefinition, "999999"));
		assertEquals("999999", token3.value());
	}
	
	@Test
	void definitionReturnsCorrectValue() {
		TokenDefinition definition = createNumberDefinition();
		SimpleToken token = new SimpleToken(definition, "123", new TokenPosition(0, 0, 0));
		
		assertEquals(definition, token.definition());
	}
	
	@Test
	void valueReturnsCorrectString() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertEquals("123", new SimpleToken(definition, "123", position).value());
		assertEquals("456", new SimpleToken(definition, "456", position).value());
		assertEquals("0", new SimpleToken(definition, "0", new TokenPosition(0, 0, 0)).value());
		assertEquals("999", new SimpleToken(definition, "999", position).value());
	}
	
	@Test
	void valueWithDifferentContentTypes() {
		TokenDefinition textDefinition = word -> word.matches("[a-zA-Z]+");
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertEquals("hello", new SimpleToken(textDefinition, "hello", position).value());
		assertEquals("WORLD", new SimpleToken(textDefinition, "WORLD", position).value());
		assertEquals("Test", new SimpleToken(textDefinition, "Test", position).value());
		
		TokenDefinition symbolDefinition = word -> word.matches("[!@#$%^&*()]+");
		TokenPosition symbolStart = new TokenPosition(0, 0, 0);
		
		assertEquals("!!!", new SimpleToken(symbolDefinition, "!!!", symbolStart).value());
		assertEquals("@#$", new SimpleToken(symbolDefinition, "@#$", symbolStart).value());
	}
	
	@Test
	void positionReturnsCorrectValue() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition position = new TokenPosition(5, 10, 25);
		SimpleToken token = new SimpleToken(definition, "123", position);
		
		assertEquals(position, token.position());
		assertEquals(5, token.position().line());
		assertEquals(10, token.position().characterInLine());
		assertEquals(25, token.position().character());
	}
	
	@Test
	void positionsWithUnpositionedToken() {
		TokenDefinition definition = createNumberDefinition();
		SimpleToken token = SimpleToken.createUnpositioned(definition, "123");
		
		assertEquals(TokenPosition.UNPOSITIONED, token.position());
		assertFalse(token.position().isPositioned());
	}
	
	@Test
	void positionsSpanCorrectRange() {
		TokenDefinition definition = createNumberDefinition();
		
		SimpleToken token1 = new SimpleToken(definition, "5", new TokenPosition(0, 0, 0));
		assertEquals(0, token1.position().character());
		
		SimpleToken token2 = new SimpleToken(definition, "123", new TokenPosition(0, 5, 10));
		assertEquals(10, token2.position().character());
		
		SimpleToken token3 = new SimpleToken(definition, "123456789", new TokenPosition(1, 0, 50));
		assertEquals(50, token3.position().character());
	}
	
	@Test
	void positionsAcrossLines() {
		TokenDefinition definition = createNumberDefinition();
		
		SimpleToken sameLine = new SimpleToken(definition, "123", new TokenPosition(2, 5, 25));
		assertEquals(2, sameLine.position().line());
		
		SimpleToken singleChar = new SimpleToken(definition, "9", new TokenPosition(5, 0, 100));
		assertEquals(5, singleChar.position().line());
		assertEquals(0, singleChar.position().characterInLine());
	}
	
	@Test
	void toStringContainsTokenInfo() {
		TokenDefinition definition = createNumberDefinition();
		SimpleToken token = new SimpleToken(definition, "123", new TokenPosition(0, 0, 0));
		
		String tokenString = token.toString();
		
		assertTrue(tokenString.contains("SimpleToken"));
		assertTrue(tokenString.contains("123"));
		assertTrue(tokenString.contains("definition"));
		assertTrue(tokenString.contains("value"));
		assertTrue(tokenString.contains("position"));
	}
	
	@Test
	void toStringWithSpecialCharacters() {
		TokenDefinition specialDefinition = word -> "\t".equals(word) || "\n".equals(word) || " ".equals(word);
		
		SimpleToken tabToken = new SimpleToken(specialDefinition, "\t", TokenPosition.UNPOSITIONED);
		String tabString = tabToken.toString();
		assertTrue(tabString.contains("\\t"));
		
		SimpleToken newlineToken = new SimpleToken(specialDefinition, "\n", TokenPosition.UNPOSITIONED);
		String newlineString = newlineToken.toString();
		assertTrue(newlineString.contains("\\n"));
	}
	
	@Test
	void toStringWithEmptyValue() {
		TokenDefinition emptyDefinition = String::isEmpty;
		SimpleToken emptyToken = new SimpleToken(emptyDefinition, "", TokenPosition.UNPOSITIONED);
		
		String tokenString = emptyToken.toString();
		assertTrue(tokenString.contains("SimpleToken"));
		assertTrue(tokenString.contains("value="));
	}
	
	@Test
	void equalTokensHaveSameHashCode() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		SimpleToken token1 = new SimpleToken(definition, "123", position);
		SimpleToken token2 = new SimpleToken(definition, "123", position);
		
		assertEquals(token1.hashCode(), token2.hashCode());
	}
	
	@Test
	void tokensWithDifferentValues() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		SimpleToken token1 = new SimpleToken(definition, "123", position);
		SimpleToken token2 = new SimpleToken(definition, "456", position);
		
		assertNotEquals(token1.value(), token2.value());
	}
	
	@Test
	void tokensWithDifferentDefinitions() {
		TokenDefinition numberDef = createNumberDefinition();
		TokenDefinition textDef = word -> word.matches("[a-z]+");
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		SimpleToken numberToken = new SimpleToken(numberDef, "123", position);
		SimpleToken textToken = new SimpleToken(textDef, "abc", position);
		
		assertNotEquals(numberToken.definition(), textToken.definition());
	}
	
	@Test
	void tokenImplementsInterface() {
		TokenDefinition definition = createNumberDefinition();
		SimpleToken token = SimpleToken.createUnpositioned(definition, "123");
		
		assertInstanceOf(Token.class, token);
		assertEquals(definition, token.definition());
		assertEquals("123", token.value());
		assertNotNull(token.position());
	}
	
	@Test
	void immutablePositions() {
		TokenDefinition definition = createNumberDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		SimpleToken token = new SimpleToken(definition, "123", position);
		
		assertSame(position, token.position());
	}
	
	@Test
	void tokensWithVariousLengths() {
		TokenDefinition anyDefinition = word -> true;
		
		SimpleToken single = new SimpleToken(anyDefinition, "a", new TokenPosition(0, 0, 0));
		assertEquals(1, single.value().length());
		
		SimpleToken shortToken = new SimpleToken(anyDefinition, "hello", new TokenPosition(0, 0, 0));
		assertEquals(5, shortToken.value().length());
		
		String longValue = "a".repeat(100);
		SimpleToken longToken = new SimpleToken(anyDefinition, longValue, new TokenPosition(0, 0, 0));
		assertEquals(100, longToken.value().length());
	}
	
	@Test
	void tokensWithSpecialValues() {
		TokenDefinition anyDefinition = word -> true;
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		TokenDefinition emptyDef = String::isEmpty;
		SimpleToken emptyToken = new SimpleToken(emptyDef, "", position);
		assertEquals("", emptyToken.value());
		
		TokenDefinition whitespaceDef = word -> word.trim().isEmpty();
		SimpleToken spaceToken = new SimpleToken(whitespaceDef, " ", position);
		assertEquals(" ", spaceToken.value());
		
		TokenDefinition specialDef = word -> word.matches("[!@#$%^&*()]+");
		SimpleToken specialToken = new SimpleToken(specialDef, "!@#", position);
		assertEquals("!@#", specialToken.value());
	}
}
