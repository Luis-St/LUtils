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

package net.luis.utils.grammar.token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenPosition}.<br>
 *
 * @author Luis-St
 */
class TokenPositionTest {
	
	@Test
	void constructValidPosition() {
		TokenPosition position = new TokenPosition(2, 5, 42);
		assertEquals(2, position.line());
		assertEquals(5, position.characterInLine());
		assertEquals(42, position.character());
	}
	
	@Test
	void constructWithZeroValues() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		assertEquals(0, position.line());
		assertEquals(0, position.characterInLine());
		assertEquals(0, position.character());
		assertTrue(position.isPositioned());
	}
	
	@Test
	void constructWithNegativeLineThrows() {
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(-1, 0, 0));
	}
	
	@Test
	void constructWithNegativeCharacterInLineThrows() {
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(0, -1, 0));
	}
	
	@Test
	void constructWithNegativeCharacterThrows() {
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(0, 0, -1));
	}
	
	@Test
	void constructWithValueBelowNegativeOneThrows() {
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(-2, 0, 0));
	}
	
	@Test
	void unpositionedConstantHasNegativeOneValues() {
		assertEquals(-1, TokenPosition.UNPOSITIONED.line());
		assertEquals(-1, TokenPosition.UNPOSITIONED.characterInLine());
		assertEquals(-1, TokenPosition.UNPOSITIONED.character());
	}
	
	@Test
	void isPositionedReturnsFalseForUnpositionedConstant() {
		assertFalse(TokenPosition.UNPOSITIONED.isPositioned());
	}
	
	@Test
	void isPositionedReturnsTrueForRegularPosition() {
		TokenPosition position = new TokenPosition(1, 1, 1);
		assertTrue(position.isPositioned());
	}
	
	@Test
	void isPositionedReturnsTrueForValueEqualToUnpositionedFields() {
		TokenPosition position = new TokenPosition(0, 0, 0);
		assertTrue(position.isPositioned());
		assertNotEquals(position, TokenPosition.UNPOSITIONED);
	}
	
	@Test
	void constructWithLargeValues() {
		TokenPosition position = new TokenPosition(1000, 80, 50000);
		assertEquals(1000, position.line());
		assertEquals(80, position.characterInLine());
		assertEquals(50000, position.character());
	}
}
