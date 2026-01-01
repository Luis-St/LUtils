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

package net.luis.utils.io.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenPosition}.<br>
 *
 * @author Luis-St
 */
class TokenPositionTest {
	
	@Test
	void constructorWithValidParameters() {
		assertDoesNotThrow(() -> new TokenPosition(0, 0, 0));
		assertDoesNotThrow(() -> new TokenPosition(5, 10, 15));
		assertDoesNotThrow(() -> new TokenPosition(100, 50, 200));
	}
	
	@Test
	void constructorWithNegativeLine() {
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(-1, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(-5, 10, 15));
	}
	
	@Test
	void constructorWithNegativeCharacterInLine() {
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(0, -1, 0));
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(5, -3, 15));
	}
	
	@Test
	void constructorWithNegativeCharacter() {
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(0, 0, -1));
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(5, 10, -7));
	}
	
	@Test
	void constructorWithMultipleNegativeValues() {
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(-1, -1, 0));
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(-1, 0, -1));
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(0, -1, -1));
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(-1, -1, -1));
	}
	
	@Test
	void lineReturnsCorrectValue() {
		assertEquals(0, new TokenPosition(0, 5, 10).line());
		assertEquals(3, new TokenPosition(3, 7, 20).line());
		assertEquals(100, new TokenPosition(100, 0, 500).line());
	}
	
	@Test
	void lineForUnpositioned() {
		assertEquals(-1, TokenPosition.UNPOSITIONED.line());
	}
	
	@Test
	void characterInLineReturnsCorrectValue() {
		assertEquals(0, new TokenPosition(5, 0, 10).characterInLine());
		assertEquals(7, new TokenPosition(3, 7, 20).characterInLine());
		assertEquals(50, new TokenPosition(10, 50, 500).characterInLine());
	}
	
	@Test
	void characterInLineForUnpositioned() {
		assertEquals(-1, TokenPosition.UNPOSITIONED.characterInLine());
	}
	
	@Test
	void characterReturnsCorrectValue() {
		assertEquals(0, new TokenPosition(5, 10, 0).character());
		assertEquals(20, new TokenPosition(3, 7, 20).character());
		assertEquals(500, new TokenPosition(10, 50, 500).character());
	}
	
	@Test
	void characterForUnpositioned() {
		assertEquals(-1, TokenPosition.UNPOSITIONED.character());
	}
	
	@Test
	void isPositionedReturnsTrueForValidPositions() {
		assertTrue(new TokenPosition(0, 0, 0).isPositioned());
		assertTrue(new TokenPosition(1, 2, 3).isPositioned());
		assertTrue(new TokenPosition(100, 200, 300).isPositioned());
	}
	
	@Test
	void isPositionedReturnsFalseForUnpositioned() {
		assertFalse(TokenPosition.UNPOSITIONED.isPositioned());
	}
	
	@Test
	void unpositionedConstantHasCorrectValues() {
		assertEquals(-1, TokenPosition.UNPOSITIONED.line());
		assertEquals(-1, TokenPosition.UNPOSITIONED.characterInLine());
		assertEquals(-1, TokenPosition.UNPOSITIONED.character());
		assertFalse(TokenPosition.UNPOSITIONED.isPositioned());
	}
	
	@Test
	void equalPositionsHaveSameHashCode() {
		TokenPosition pos1 = new TokenPosition(5, 10, 15);
		TokenPosition pos2 = new TokenPosition(5, 10, 15);
		
		assertEquals(pos1.hashCode(), pos2.hashCode());
	}
}
