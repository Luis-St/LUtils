/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Range}.<br>
 *
 * @author Luis-St
 */
class RangeTest {
	
	@Test
	void of() {
		assertNotNull(Range.of(9.0));
		assertNotNull(Range.of(0.0, 0));
		assertNotNull(Range.of(0.0, 9));
		assertThrows(IllegalArgumentException.class, () -> Range.of(9.0, 0.0));
	}
	
	@Test
	void getMin() {
		assertEquals(0.0, Range.of(0.0, 9.0).getMin());
		assertNotEquals(1.0, Range.of(0.0, 9.0).getMin());
	}
	
	@Test
	void getMax() {
		assertEquals(9.0, Range.of(0.0, 9.0).getMax());
		assertNotEquals(8.0, Range.of(0.0, 9.0).getMax());
	}
	
	@Test
	void isInRange() {
		assertFalse(Range.of(0.0, 9.0).isInRange(-1.0));
		for (int i = 0; i < 10; i++) {
			assertTrue(Range.of(0.0, 9.0).isInRange(i));
		}
		assertFalse(Range.of(0.0, 9.0).isInRange(10.0));
	}
}