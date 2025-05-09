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
	void constructor() {
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(-1, 0, 0));
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(0, -1, 0));
		assertThrows(IllegalArgumentException.class, () -> new TokenPosition(0, 0, -1));
		
		assertDoesNotThrow(() -> new TokenPosition(0, 0, 0));
	}
	
	@Test
	void line() {
		assertEquals(0, new TokenPosition(0, 0, 0).line());
		assertEquals(-1, TokenPosition.UNPOSITIONED.line());
	}
	
	@Test
	void characterInLine() {
		assertEquals(0, new TokenPosition(0, 0, 0).characterInLine());
		assertEquals(-1, TokenPosition.UNPOSITIONED.characterInLine());
	}
	
	@Test
	void character() {
		assertEquals(0, new TokenPosition(0, 0, 0).character());
		assertEquals(-1, TokenPosition.UNPOSITIONED.character());
	}
	
	@Test
	void isPositioned() {
		assertTrue(new TokenPosition(0, 0, 0).isPositioned());
		assertFalse(TokenPosition.UNPOSITIONED.isPositioned());
	}
}
