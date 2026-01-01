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
	void ofSingleValueCreatesRange() {
		Range range = Range.of(5.0);
		assertEquals(0.0, range.getMin());
		assertEquals(5.0, range.getMax());
		assertEquals(5.0, range.getRange());
	}
	
	@Test
	void ofSingleNegativeValueReturnsEmpty() {
		Range range = Range.of(-1.0);
		assertSame(Range.EMPTY, range);
	}
	
	@Test
	void ofTwoValuesCreatesValidRange() {
		Range range = Range.of(2.0, 8.0);
		assertEquals(2.0, range.getMin());
		assertEquals(8.0, range.getMax());
		assertEquals(6.0, range.getRange());
	}
	
	@Test
	void ofInvertedValuesReturnsEmpty() {
		Range range = Range.of(9.0, 0.0);
		assertSame(Range.EMPTY, range);
	}
	
	@Test
	void ofEqualValuesCreatesValidRange() {
		Range range = Range.of(5.0, 5.0);
		assertEquals(5.0, range.getMin());
		assertEquals(5.0, range.getMax());
		assertEquals(0.0, range.getRange());
	}
	
	@Test
	void ofCharValuesConvertsToDouble() {
		Range range = Range.of('A', 'Z');
		assertEquals(65.0, range.getMin());
		assertEquals(90.0, range.getMax());
	}
	
	@Test
	void parseValidRangeString() {
		assertEquals(Range.of(0, 1), Range.parse("[0..1]"));
		assertEquals(Range.of(0.05, 0.95), Range.parse("[0.05..0.95]"));
		assertEquals(Range.of(-5, 10), Range.parse("[-5..10]"));
		assertEquals(Range.of(0, 0), Range.parse("[0..0]"));
	}
	
	@Test
	void parseWithWhitespace() {
		assertEquals(Range.of(1, 5), Range.parse("[ 1 .. 5 ]"));
		assertEquals(Range.of(2.5, 7.5), Range.parse("[  2.5  ..  7.5  ]"));
	}
	
	@Test
	void parseInvalidFormatsReturnsEmpty() {
		assertSame(Range.EMPTY, Range.parse(null));
		assertSame(Range.EMPTY, Range.parse(""));
		assertSame(Range.EMPTY, Range.parse(" "));
		assertSame(Range.EMPTY, Range.parse("0..1"));
		assertSame(Range.EMPTY, Range.parse("[0;1]"));
		assertSame(Range.EMPTY, Range.parse("[0..1..2]"));
		assertSame(Range.EMPTY, Range.parse("[1..0]"));
		assertSame(Range.EMPTY, Range.parse("invalid"));
	}
	
	@Test
	void getMinReturnsMinimumValue() {
		assertEquals(0.0, Range.of(0.0, 9.0).getMin());
		assertEquals(-5.0, Range.of(-5.0, 5.0).getMin());
		assertEquals(3.5, Range.of(3.5, 7.2).getMin());
	}
	
	@Test
	void getMaxReturnsMaximumValue() {
		assertEquals(9.0, Range.of(0.0, 9.0).getMax());
		assertEquals(5.0, Range.of(-5.0, 5.0).getMax());
		assertEquals(7.2, Range.of(3.5, 7.2).getMax());
	}
	
	@Test
	void getRangeCalculatesDifference() {
		assertEquals(9.0, Range.of(0.0, 9.0).getRange());
		assertEquals(10.0, Range.of(-5.0, 5.0).getRange());
		assertEquals(0.0, Range.of(5.0, 5.0).getRange());
		assertEquals(3.7, Range.of(3.5, 7.2).getRange(), 0.0001);
	}
	
	@Test
	void moveShiftsBothBounds() {
		Range original = Range.of(2.0, 8.0);
		
		assertEquals(Range.of(2.0, 8.0), original.move(0.0));
		assertEquals(Range.of(3.0, 9.0), original.move(1.0));
		assertEquals(Range.of(1.0, 7.0), original.move(-1.0));
		assertEquals(Range.of(4.5, 10.5), original.move(2.5));
	}
	
	@Test
	void expandIncreasesBothBounds() {
		Range original = Range.of(2.0, 8.0);
		
		assertEquals(Range.of(2.0, 8.0), original.expand(0.0));
		assertEquals(Range.of(1.0, 9.0), original.expand(1.0));
		assertEquals(Range.of(1.0, 9.0), original.expand(-1.0));
		assertEquals(Range.of(-0.5, 10.5), original.expand(2.5));
	}
	
	@Test
	void expandMaxIncreasesMaximumOnly() {
		Range original = Range.of(2.0, 8.0);
		
		assertEquals(Range.of(2.0, 8.0), original.expandMax(0.0));
		assertEquals(Range.of(2.0, 9.0), original.expandMax(1.0));
		assertEquals(Range.of(2.0, 9.0), original.expandMax(-1.0));
		assertEquals(Range.of(2.0, 10.5), original.expandMax(2.5));
	}
	
	@Test
	void expandMinDecreasesMinimumOnly() {
		Range original = Range.of(2.0, 8.0);
		
		assertEquals(Range.of(2.0, 8.0), original.expandMin(0.0));
		assertEquals(Range.of(1.0, 8.0), original.expandMin(1.0));
		assertEquals(Range.of(1.0, 8.0), original.expandMin(-1.0));
		assertEquals(Range.of(-0.5, 8.0), original.expandMin(2.5));
	}
	
	@Test
	void expandMinCanCreateEmptyRange() {
		Range original = Range.of(2.0, 5.0);
		Range expanded = original.expandMin(10.0);
		
		assertTrue(expanded.getMin() < original.getMax());
	}
	
	@Test
	void isInRangeChecksInclusive() {
		Range range = Range.of(0.0, 10.0);
		
		assertTrue(range.isInRange(0.0));
		assertTrue(range.isInRange(5.0));
		assertTrue(range.isInRange(10.0));
		assertTrue(range.isInRange(0.1));
		assertTrue(range.isInRange(9.9));
		
		assertFalse(range.isInRange(-0.1));
		assertFalse(range.isInRange(10.1));
		assertFalse(range.isInRange(-5.0));
		assertFalse(range.isInRange(15.0));
	}
	
	@Test
	void isInRangeForSinglePointRange() {
		Range point = Range.of(5.0, 5.0);
		
		assertTrue(point.isInRange(5.0));
		assertFalse(point.isInRange(4.9));
		assertFalse(point.isInRange(5.1));
	}
	
	@Test
	void equalsComparesMinAndMax() {
		Range range1 = Range.of(1.0, 5.0);
		Range range2 = Range.of(1.0, 5.0);
		Range differentMin = Range.of(2.0, 5.0);
		Range differentMax = Range.of(1.0, 6.0);
		
		assertEquals(range1, range2);
		assertNotEquals(range1, differentMin);
		assertNotEquals(range1, differentMax);
		assertNotEquals(range1, null);
		assertNotEquals(range1, "not a range");
	}
	
	@Test
	void hashCodeIsSameForEqualObjects() {
		Range range1 = Range.of(1.0, 5.0);
		Range range2 = Range.of(1.0, 5.0);
		
		assertEquals(range1.hashCode(), range2.hashCode());
	}
	
	@Test
	void toStringFormatsCorrectly() {
		assertEquals("[0.0..5.0]", Range.of(0.0, 5.0).toString());
		assertEquals("[-2.5..7.8]", Range.of(-2.5, 7.8).toString());
		assertEquals("[3.0..3.0]", Range.of(3.0, 3.0).toString());
		assertEquals("[0.0..0.0]", Range.EMPTY.toString());
	}
	
	@Test
	void emptyConstantProperties() {
		assertEquals(0.0, Range.EMPTY.getMin());
		assertEquals(0.0, Range.EMPTY.getMax());
		assertEquals(0.0, Range.EMPTY.getRange());
		assertTrue(Range.EMPTY.isInRange(0.0));
		assertFalse(Range.EMPTY.isInRange(1.0));
		assertFalse(Range.EMPTY.isInRange(-1.0));
	}
}
