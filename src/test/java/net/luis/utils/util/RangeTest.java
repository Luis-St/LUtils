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
		assertDoesNotThrow(() -> Range.of(9.0, 0.0));
		assertEquals(Range.EMPTY, Range.of(9.0, 0.0));
		assertNotNull(Range.of(9.0));
		assertNotNull(Range.of(0.0, 0));
		assertNotNull(Range.of(0.0, 9));
	}
	
	@Test
	void parse() {
		assertDoesNotThrow(() -> Range.parse(null));
		assertEquals(Range.EMPTY, Range.parse(""));
		assertEquals(Range.EMPTY, Range.parse(" "));
		assertEquals(Range.EMPTY, Range.parse("0..1"));
		assertEquals(Range.EMPTY, Range.parse("[0;1]"));
		assertEquals(Range.EMPTY, Range.parse("[0..1..2]"));
		assertEquals(Range.of(0, 1), Range.parse("[0..1]"));
		assertEquals(Range.of(0.05, 0.95), Range.parse("[0.05..0.95]"));
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
	void getRange() {
		assertEquals(9.0, Range.of(0.0, 9.0).getRange());
		assertEquals(9.0, Range.of(-9.0, 0.0).getRange());
		assertNotEquals(8.0, Range.of(0.0, 9.0).getRange());
	}
	
	@Test
	void move() {
		assertEquals(Range.of(0.0, 9.0), Range.of(0.0, 9.0).move(0.0));
		assertEquals(Range.of(1.0, 10.0), Range.of(0.0, 9.0).move(1.0));
		assertEquals(Range.of(-1.0, 8.0), Range.of(0.0, 9.0).move(-1.0));
	}
	
	@Test
	void expand() {
		assertEquals(Range.of(0.0, 9.0), Range.of(0.0, 9.0).expand(0.0));
		assertEquals(Range.of(-1.0, 10.0), Range.of(0.0, 9.0).expand(1.0));
		assertEquals(Range.of(-1.0, 10.0), Range.of(0.0, 9.0).expand(-1.0));
	}
	
	@Test
	void expandMax() {
		assertEquals(Range.of(0.0, 9.0), Range.of(0.0, 9.0).expandMax(0.0));
		assertEquals(Range.of(0.0, 10.0), Range.of(0.0, 9.0).expandMax(1.0));
		assertEquals(Range.of(0.0, 10.0), Range.of(0.0, 9.0).expandMax(-1.0));
	}
	
	@Test
	void expandMin() {
		assertEquals(Range.of(0.0, 9.0), Range.of(0.0, 9.0).expandMin(0.0));
		assertEquals(Range.of(-1.0, 9.0), Range.of(0.0, 9.0).expandMin(1.0));
		assertEquals(Range.of(-1.0, 9.0), Range.of(0.0, 9.0).expandMin(-1.0));
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