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

package net.luis.utils.io.database.function.window.frame.bound;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PrecedingFrameBound}.<br>
 *
 * @author Luis-St
 */
class PrecedingFrameBoundTest {
	
	@Test
	void constructWithPositiveOffset() {
		assertEquals(5, new PrecedingFrameBound(5).offset());
	}
	
	@Test
	void constructWithNegativeOffset() {
		assertThrows(IllegalArgumentException.class, () -> new PrecedingFrameBound(-1));
	}
	
	@Test
	void constructWithZeroOffset() {
		assertEquals(0, new PrecedingFrameBound(0).offset());
	}
	
	@Test
	void constructWithLargeOffset() {
		assertEquals(Integer.MAX_VALUE, new PrecedingFrameBound(Integer.MAX_VALUE).offset());
	}
	
	@Test
	void recordEqualityByComponents() {
		PrecedingFrameBound first = new PrecedingFrameBound(3);
		PrecedingFrameBound second = new PrecedingFrameBound(3);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
}
