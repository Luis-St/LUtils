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
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(NullPointerException.class, () -> new EscapedToken(null, "\\n", position));
	}
	
	@Test
	void constructorWithNullValue() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(NullPointerException.class, () -> new EscapedToken(definition, null, position));
	}
	
	@Test
	void constructorWithNullPosition() {
		TokenDefinition definition = createEscapedDefinition();
		
		assertThrows(NullPointerException.class, () -> new EscapedToken(definition, "\\n", null));
	}
	
	@Test
	void constructorWithValidParameters() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\n", position));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\t", position));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\\\", position));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\\"", position));
	}
	
	@Test
	void constructorWithInvalidValueLength() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "n", position));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "\\\\n", position));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "", position));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "abc", position));
	}
	
	@Test
	void constructorWithInvalidValueFormat() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "nn", position));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "n\\", position));
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(definition, "ab", position));
	}
	
	@Test
	void constructorWithNonMatchingDefinition() {
		TokenDefinition wrongDefinition = word -> word.matches("[a-z]+");
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertThrows(IllegalArgumentException.class, () -> new EscapedToken(wrongDefinition, "\\n", position));
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
		assertEquals(TokenPosition.UNPOSITIONED, token1.position());
		
		EscapedToken token2 = assertDoesNotThrow(() -> EscapedToken.createUnpositioned(definition, "\\t"));
		assertEquals("\\t", token2.value());
		
		EscapedToken token3 = assertDoesNotThrow(() -> EscapedToken.createUnpositioned(definition, "\\\\"));
		assertEquals("\\\\", token3.value());
	}
	
	@Test
	void definitionReturnsCorrectValue() {
		TokenDefinition definition = createEscapedDefinition();
		EscapedToken token = new EscapedToken(definition, "\\n", new TokenPosition(0, 0, 0));
		
		assertEquals(definition, token.definition());
	}
	
	@Test
	void valueReturnsCorrectString() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertEquals("\\n", new EscapedToken(definition, "\\n", position).value());
		assertEquals("\\t", new EscapedToken(definition, "\\t", position).value());
		assertEquals("\\\\", new EscapedToken(definition, "\\\\", position).value());
		assertEquals("\\\"", new EscapedToken(definition, "\\\"", position).value());
		assertEquals("\\'", new EscapedToken(definition, "\\'", position).value());
		assertEquals("\\r", new EscapedToken(definition, "\\r", position).value());
		assertEquals("\\0", new EscapedToken(definition, "\\0", position).value());
	}
	
	@Test
	void valueWithSpecialEscapeSequences() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertEquals("\\n", new EscapedToken(definition, "\\n", position).value());
		assertEquals("\\t", new EscapedToken(definition, "\\t", position).value());
		assertEquals("\\r", new EscapedToken(definition, "\\r", position).value());
		assertEquals("\\b", new EscapedToken(definition, "\\b", position).value());
		assertEquals("\\f", new EscapedToken(definition, "\\f", position).value());
		
		assertEquals("\\\"", new EscapedToken(definition, "\\\"", position).value());
		assertEquals("\\'", new EscapedToken(definition, "\\'", position).value());
		
		assertEquals("\\\\", new EscapedToken(definition, "\\\\", position).value());
	}
	
	@Test
	void positionReturnsCorrectValue() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition position = new TokenPosition(5, 10, 25);
		EscapedToken token = new EscapedToken(definition, "\\n", position);
		
		assertEquals(position, token.position());
		assertEquals(5, token.position().line());
		assertEquals(10, token.position().characterInLine());
		assertEquals(25, token.position().character());
	}
	
	@Test
	void positionsWithUnpositionedToken() {
		TokenDefinition definition = createEscapedDefinition();
		EscapedToken token = EscapedToken.createUnpositioned(definition, "\\n");
		
		assertEquals(TokenPosition.UNPOSITIONED, token.position());
		assertFalse(token.position().isPositioned());
	}
	
	@Test
	void positionsSpanCorrectRange() {
		TokenDefinition definition = createEscapedDefinition();
		
		EscapedToken token1 = new EscapedToken(definition, "\\n", new TokenPosition(0, 0, 0));
		assertEquals(0, token1.position().character());
		
		EscapedToken token2 = new EscapedToken(definition, "\\t", new TokenPosition(2, 5, 10));
		assertEquals(10, token2.position().character());
		
		EscapedToken token3 = new EscapedToken(definition, "\\\\", new TokenPosition(1, 0, 50));
		assertEquals(50, token3.position().character());
	}
	
	@Test
	void toStringContainsEscapeSequence() {
		TokenDefinition definition = createEscapedDefinition();
		EscapedToken token = new EscapedToken(definition, "\\n", new TokenPosition(0, 0, 0));
		
		String tokenString = token.toString();
		
		assertTrue(tokenString.contains("EscapedToken"));
		assertTrue(tokenString.contains("\\n") || tokenString.contains("\\\\n"));
		assertTrue(tokenString.contains("definition"));
		assertTrue(tokenString.contains("value"));
		assertTrue(tokenString.contains("position"));
	}
	
	@Test
	void toStringWithSpecialCharacters() {
		TokenDefinition definition = createEscapedDefinition();
		
		EscapedToken tabToken = new EscapedToken(definition, "\\t", TokenPosition.UNPOSITIONED);
		String tabString = tabToken.toString();
		assertTrue(tabString.contains("\\t") || tabString.contains("\\\\t"));
		
		EscapedToken newlineToken = new EscapedToken(definition, "\\n", TokenPosition.UNPOSITIONED);
		String newlineString = newlineToken.toString();
		assertTrue(newlineString.contains("\\n") || newlineString.contains("\\\\n"));
	}
	
	@Test
	void equalTokensHaveSameHashCode() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		EscapedToken token1 = new EscapedToken(definition, "\\n", position);
		EscapedToken token2 = new EscapedToken(definition, "\\n", position);
		
		assertEquals(token1.hashCode(), token2.hashCode());
	}
	
	@Test
	void differentValuesWithSameEscapeFormat() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\a", position));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\z", position));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\1", position));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\9", position));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\!", position));
		assertDoesNotThrow(() -> new EscapedToken(definition, "\\@", position));
	}
	
	@Test
	void tokenImplementsInterface() {
		TokenDefinition definition = createEscapedDefinition();
		EscapedToken token = EscapedToken.createUnpositioned(definition, "\\n");
		
		assertInstanceOf(Token.class, token);
		assertEquals(definition, token.definition());
		assertEquals("\\n", token.value());
		assertNotNull(token.position());
	}
	
	@Test
	void immutablePositions() {
		TokenDefinition definition = createEscapedDefinition();
		TokenPosition position = new TokenPosition(0, 0, 0);
		EscapedToken token = new EscapedToken(definition, "\\n", position);
		
		assertSame(position, token.position());
	}
}
