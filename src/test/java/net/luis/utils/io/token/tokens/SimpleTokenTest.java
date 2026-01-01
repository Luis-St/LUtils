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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SimpleToken}.<br>
 *
 * @author Luis-St
 */
class SimpleTokenTest {
	
	@Test
	void constructorWithNullValue() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(NullPointerException.class, () -> new SimpleToken(null, position));
	}
	
	@Test
	void constructorWithNullPosition() {
		assertThrows(NullPointerException.class, () -> new SimpleToken("123", null));
	}
	
	@Test
	void constructorWithValidParameters() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertDoesNotThrow(() -> new SimpleToken("123", position));
		assertDoesNotThrow(() -> new SimpleToken("456", position));
		assertDoesNotThrow(() -> new SimpleToken("0", new TokenPosition(0, 0, 0)));
	}
	
	@Test
	void createUnpositionedWithNullValue() {
		assertThrows(NullPointerException.class, () -> SimpleToken.createUnpositioned(null));
	}
	
	@Test
	void createUnpositionedWithValidValues() {
		SimpleToken token1 = assertDoesNotThrow(() -> SimpleToken.createUnpositioned("123"));
		assertEquals(TokenPosition.UNPOSITIONED, token1.position());
		assertEquals("123", token1.value());
		
		SimpleToken token2 = assertDoesNotThrow(() -> SimpleToken.createUnpositioned("0"));
		assertEquals("0", token2.value());
		
		SimpleToken token3 = assertDoesNotThrow(() -> SimpleToken.createUnpositioned("999999"));
		assertEquals("999999", token3.value());
	}
	
	@Test
	void valueReturnsCorrectString() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertEquals("123", new SimpleToken("123", position).value());
		assertEquals("456", new SimpleToken("456", position).value());
		assertEquals("0", new SimpleToken("0", new TokenPosition(0, 0, 0)).value());
		assertEquals("999", new SimpleToken("999", position).value());
	}
	
	@Test
	void valueWithDifferentContentTypes() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertEquals("hello", new SimpleToken("hello", position).value());
		assertEquals("WORLD", new SimpleToken("WORLD", position).value());
		assertEquals("Test", new SimpleToken("Test", position).value());
		
		TokenPosition symbolStart = new TokenPosition(0, 0, 0);
		
		assertEquals("!!!", new SimpleToken("!!!", symbolStart).value());
		assertEquals("@#$", new SimpleToken("@#$", symbolStart).value());
	}
	
	@Test
	void positionReturnsCorrectValue() {
		TokenPosition position = new TokenPosition(5, 10, 25);
		SimpleToken token = new SimpleToken("123", position);
		
		assertEquals(position, token.position());
		assertEquals(5, token.position().line());
		assertEquals(10, token.position().characterInLine());
		assertEquals(25, token.position().character());
	}
	
	@Test
	void positionsWithUnpositionedToken() {
		SimpleToken token = SimpleToken.createUnpositioned("123");
		
		assertEquals(TokenPosition.UNPOSITIONED, token.position());
		assertFalse(token.position().isPositioned());
	}
	
	@Test
	void positionsSpanCorrectRange() {
		SimpleToken token1 = new SimpleToken("5", new TokenPosition(0, 0, 0));
		assertEquals(0, token1.position().character());
		
		SimpleToken token2 = new SimpleToken("123", new TokenPosition(0, 5, 10));
		assertEquals(10, token2.position().character());
		
		SimpleToken token3 = new SimpleToken("123456789", new TokenPosition(1, 0, 50));
		assertEquals(50, token3.position().character());
	}
	
	@Test
	void positionsAcrossLines() {
		SimpleToken sameLine = new SimpleToken("123", new TokenPosition(2, 5, 25));
		assertEquals(2, sameLine.position().line());
		
		SimpleToken singleChar = new SimpleToken("9", new TokenPosition(5, 0, 100));
		assertEquals(5, singleChar.position().line());
		assertEquals(0, singleChar.position().characterInLine());
	}
	
	@Test
	void tokensWithDifferentValues() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		SimpleToken token1 = new SimpleToken("123", position);
		SimpleToken token2 = new SimpleToken("456", position);
		
		assertNotEquals(token1.value(), token2.value());
	}
	
	@Test
	void immutablePositions() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		SimpleToken token = new SimpleToken("123", position);
		
		assertSame(position, token.position());
	}
	
	@Test
	void tokensWithVariousLengths() {
		SimpleToken single = new SimpleToken("a", new TokenPosition(0, 0, 0));
		assertEquals(1, single.value().length());
		
		SimpleToken shortToken = new SimpleToken("hello", new TokenPosition(0, 0, 0));
		assertEquals(5, shortToken.value().length());
		
		String longValue = "a".repeat(100);
		SimpleToken longToken = new SimpleToken(longValue, new TokenPosition(0, 0, 0));
		assertEquals(100, longToken.value().length());
	}
	
	@Test
	void tokensWithSpecialValues() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		SimpleToken emptyToken = new SimpleToken("", position);
		assertEquals("", emptyToken.value());
		
		SimpleToken spaceToken = new SimpleToken(" ", position);
		assertEquals(" ", spaceToken.value());
		
		SimpleToken specialToken = new SimpleToken("!@#", position);
		assertEquals("!@#", specialToken.value());
	}
	
	@Test
	void toStringContainsTokenInfo() {
		SimpleToken token = new SimpleToken("123", new TokenPosition(0, 0, 0));
		
		String tokenString = token.toString();
		
		assertTrue(tokenString.contains("SimpleToken"));
		assertTrue(tokenString.contains("123"));
		assertTrue(tokenString.contains("value"));
		assertTrue(tokenString.contains("position"));
	}
	
	@Test
	void toStringWithSpecialCharacters() {
		SimpleToken tabToken = new SimpleToken("\t", TokenPosition.UNPOSITIONED);
		String tabString = tabToken.toString();
		assertTrue(tabString.contains("\\\\t"));
		
		SimpleToken newlineToken = new SimpleToken("\n", TokenPosition.UNPOSITIONED);
		String newlineString = newlineToken.toString();
		assertTrue(newlineString.contains("\\\\n"));
	}
	
	@Test
	void toStringWithEmptyValue() {
		SimpleToken emptyToken = new SimpleToken("", TokenPosition.UNPOSITIONED);
		
		String tokenString = emptyToken.toString();
		assertTrue(tokenString.contains("SimpleToken"));
		assertTrue(tokenString.contains("value="));
	}
	
	@Test
	void equalTokensHaveSameHashCode() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		SimpleToken token1 = new SimpleToken("123", position);
		SimpleToken token2 = new SimpleToken("123", position);
		
		assertEquals(token1.hashCode(), token2.hashCode());
	}
}
