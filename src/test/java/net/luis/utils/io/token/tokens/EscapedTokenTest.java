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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EscapedToken}.<br>
 *
 * @author Luis-St
 */
class EscapedTokenTest {
	
	@Test
	void constructorWithNullValue() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(NullPointerException.class, () -> new EscapedToken(null, position));
	}
	
	@Test
	void constructorWithNullPosition() {
		assertThrows(NullPointerException.class, () -> new EscapedToken("\\n", null));
	}
	
	@Test
	void constructorWithValidParameters() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertDoesNotThrow(() -> new EscapedToken("\\n", position));
		assertDoesNotThrow(() -> new EscapedToken("\\t", position));
		assertDoesNotThrow(() -> new EscapedToken("\\\\", position));
		assertDoesNotThrow(() -> new EscapedToken("\\\"", position));
	}
	
	@Test
	void constructorWithInvalidValueLength() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("n", position));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("\\\\n", position));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("", position));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("abc", position));
	}
	
	@Test
	void constructorWithInvalidValueFormat() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("nn", position));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("n\\", position));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken("ab", position));
	}
	
	@Test
	void createUnpositionedWithNullValue() {
		assertThrows(NullPointerException.class, () -> EscapedToken.createUnpositioned(null));
	}
	
	@Test
	void createUnpositionedWithInvalidValue() {
		assertThrows(IllegalArgumentException.class, () -> EscapedToken.createUnpositioned("n"));
		assertThrows(IllegalArgumentException.class, () -> EscapedToken.createUnpositioned("\\\\n"));
		assertThrows(IllegalArgumentException.class, () -> EscapedToken.createUnpositioned("nn"));
	}
	
	@Test
	void createUnpositionedWithValidValues() {
		EscapedToken token1 = assertDoesNotThrow(() -> EscapedToken.createUnpositioned("\\n"));
		assertEquals(TokenPosition.UNPOSITIONED, token1.position());
		
		EscapedToken token2 = assertDoesNotThrow(() -> EscapedToken.createUnpositioned("\\t"));
		assertEquals("\\t", token2.value());
		
		EscapedToken token3 = assertDoesNotThrow(() -> EscapedToken.createUnpositioned("\\\\"));
		assertEquals("\\\\", token3.value());
	}
	
	@Test
	void valueReturnsCorrectString() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertEquals("\\n", new EscapedToken("\\n", position).value());
		assertEquals("\\t", new EscapedToken("\\t", position).value());
		assertEquals("\\\\", new EscapedToken("\\\\", position).value());
		assertEquals("\\\"", new EscapedToken("\\\"", position).value());
		assertEquals("\\'", new EscapedToken("\\'", position).value());
		assertEquals("\\r", new EscapedToken("\\r", position).value());
		assertEquals("\\0", new EscapedToken("\\0", position).value());
	}
	
	@Test
	void valueWithSpecialEscapeSequences() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertEquals("\\n", new EscapedToken("\\n", position).value());
		assertEquals("\\t", new EscapedToken("\\t", position).value());
		assertEquals("\\r", new EscapedToken("\\r", position).value());
		assertEquals("\\b", new EscapedToken("\\b", position).value());
		assertEquals("\\f", new EscapedToken("\\f", position).value());
		
		assertEquals("\\\"", new EscapedToken("\\\"", position).value());
		assertEquals("\\'", new EscapedToken("\\'", position).value());
		
		assertEquals("\\\\", new EscapedToken("\\\\", position).value());
	}
	
	@Test
	void positionReturnsCorrectValue() {
		TokenPosition position = new TokenPosition(5, 10, 25);
		EscapedToken token = new EscapedToken("\\n", position);
		
		assertEquals(position, token.position());
		assertEquals(5, token.position().line());
		assertEquals(10, token.position().characterInLine());
		assertEquals(25, token.position().character());
	}
	
	@Test
	void positionsWithUnpositionedToken() {
		EscapedToken token = EscapedToken.createUnpositioned("\\n");
		
		assertEquals(TokenPosition.UNPOSITIONED, token.position());
		assertFalse(token.position().isPositioned());
	}
	
	@Test
	void toStringContainsEscapeSequence() {
		EscapedToken token = new EscapedToken("\\n", new TokenPosition(0, 0, 0));
		
		String tokenString = token.toString();
		
		assertTrue(tokenString.contains("EscapedToken"));
		assertTrue(tokenString.contains("\\\\n"));
		assertTrue(tokenString.contains("value"));
		assertTrue(tokenString.contains("position"));
	}
	
	@Test
	void differentValuesWithSameEscapeFormat() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertDoesNotThrow(() -> new EscapedToken("\\a", position));
		assertDoesNotThrow(() -> new EscapedToken("\\z", position));
		assertDoesNotThrow(() -> new EscapedToken("\\1", position));
		assertDoesNotThrow(() -> new EscapedToken("\\9", position));
		assertDoesNotThrow(() -> new EscapedToken("\\!", position));
		assertDoesNotThrow(() -> new EscapedToken("\\@", position));
	}
	
	@Test
	void immutablePositions() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		EscapedToken token = new EscapedToken("\\n", position);
		
		assertSame(position, token.position());
	}
	
	@Test
	void toStringWithSpecialCharacters() {
		EscapedToken tabToken = new EscapedToken("\\t", TokenPosition.UNPOSITIONED);
		String tabString = tabToken.toString();
		assertTrue(tabString.contains("\\t") || tabString.contains("\\\\t"));
		
		EscapedToken newlineToken = new EscapedToken("\\n", TokenPosition.UNPOSITIONED);
		String newlineString = newlineToken.toString();
		assertTrue(newlineString.contains("\\n") || newlineString.contains("\\\\n"));
	}
	
	@Test
	void equalTokensHaveSameHashCode() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		EscapedToken token1 = new EscapedToken("\\n", position);
		EscapedToken token2 = new EscapedToken("\\n", position);
		
		assertEquals(token1.hashCode(), token2.hashCode());
	}
}
