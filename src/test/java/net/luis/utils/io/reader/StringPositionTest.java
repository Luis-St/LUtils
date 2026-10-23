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

package net.luis.utils.io.reader;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringPosition}.<br>
 *
 * @author Luis-St
 */
class StringPositionTest {
	
	@Test
	void constructValidPosition() {
		StringPosition position = new StringPosition(7, 2, 5);
		assertEquals(7, position.index());
		assertEquals(2, position.line());
		assertEquals(5, position.column());
	}
	
	@Test
	void constructAtBoundaries() {
		assertDoesNotThrow(() -> new StringPosition(0, 1, 1));
	}
	
	@Test
	void constructWithNegativeIndex() {
		assertThrows(IllegalArgumentException.class, () -> new StringPosition(-1, 1, 1));
	}
	
	@Test
	void constructWithInvalidLine() {
		assertThrows(IllegalArgumentException.class, () -> new StringPosition(0, 0, 1));
		assertThrows(IllegalArgumentException.class, () -> new StringPosition(0, -1, 1));
	}
	
	@Test
	void constructWithInvalidColumn() {
		assertThrows(IllegalArgumentException.class, () -> new StringPosition(0, 1, 0));
		assertThrows(IllegalArgumentException.class, () -> new StringPosition(0, 1, -1));
	}
	
	@Test
	void toStringContainsLineAndColumn() {
		assertEquals("line 2, column 5", new StringPosition(7, 2, 5).toString());
	}
	
	@Test
	void equalPositions() {
		StringPosition position = new StringPosition(7, 2, 5);
		assertEquals(position, new StringPosition(7, 2, 5));
		assertEquals(position.hashCode(), new StringPosition(7, 2, 5).hashCode());
		assertNotEquals(position, new StringPosition(8, 2, 5));
	}
}
