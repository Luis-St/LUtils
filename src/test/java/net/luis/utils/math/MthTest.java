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

package net.luis.utils.math;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Mth}.<br>
 *
 * @author Luis-St
 */
class MthTest {
	
	@Test
	void sumIntCalculatesCorrectly() {
		assertEquals(10, Mth.sum(127));
		assertEquals(15, Mth.sum(159));
		assertEquals(1, Mth.sum(100));
		assertEquals(0, Mth.sum(0));
		assertEquals(9, Mth.sum(9));
	}
	
	@Test
	void sumIntHandlesNegativeNumbers() {
		assertEquals(5, Mth.sum(-23));
		assertEquals(10, Mth.sum(-127));
		assertEquals(1, Mth.sum(-1));
		assertEquals(1, Mth.sum(-10));
	}
	
	@Test
	void sumIntHandlesLargeNumbers() {
		assertEquals(45, Mth.sum(123456789));
		assertEquals(46, Mth.sum(Integer.MAX_VALUE));
		assertEquals(-47, Mth.sum(Integer.MIN_VALUE));
	}
	
	@Test
	void sumLongCalculatesCorrectly() {
		assertEquals(10, Mth.sum(127L));
		assertEquals(15, Mth.sum(159L));
		assertEquals(1, Mth.sum(100L));
		assertEquals(0, Mth.sum(0L));
		assertEquals(9, Mth.sum(9L));
	}
	
	@Test
	void sumLongHandlesNegativeNumbers() {
		assertEquals(5, Mth.sum(-23L));
		assertEquals(10, Mth.sum(-127L));
		assertEquals(1, Mth.sum(-1L));
		assertEquals(1, Mth.sum(-10L));
	}
	
	@Test
	void sumLongHandlesLargeNumbers() {
		assertEquals(60, Mth.sum(123456789012345L));
		assertEquals(88, Mth.sum(Long.MAX_VALUE));
		assertEquals(-89, Mth.sum(Long.MIN_VALUE));
	}
	
	@Test
	void randomIntFailsWithNullRandom() {
		assertThrows(NullPointerException.class, () -> Mth.randomInt(null, 0, 10));
	}
	
	@Test
	void randomIntFailsWithInvalidRange() {
		Random rng = new Random(0);
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInt(rng, 10, 0));
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInt(rng, 10, 10));
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInt(rng, 5, 4));
	}
	
	@Test
	void randomIntGeneratesInRange() {
		Random rng = new Random(0);
		for (int i = 0; i < 1000; i++) {
			int value = Mth.randomInt(rng, 0, 10);
			assertTrue(value >= 0 && value < 10, "Value " + value + " not in range [0, 10)");
		}
	}
	
	@Test
	void randomIntGeneratesInNegativeRange() {
		Random rng = new Random(0);
		for (int i = 0; i < 100; i++) {
			int value = Mth.randomInt(rng, -10, -5);
			assertTrue(value >= -10 && value < -5, "Value " + value + " not in range [-10, -5)");
		}
	}
	
	@Test
	void randomExclusiveIntFailsWithNullRandom() {
		assertThrows(NullPointerException.class, () -> Mth.randomExclusiveInt(null, 0, 10));
	}
	
	@Test
	void randomExclusiveIntFailsWithInvalidRange() {
		Random rng = new Random(0);
		assertThrows(IllegalArgumentException.class, () -> Mth.randomExclusiveInt(rng, 10, 0));
		assertThrows(IllegalArgumentException.class, () -> Mth.randomExclusiveInt(rng, 10, 10));
	}
	
	@Test
	void randomExclusiveIntGeneratesInRange() {
		Random rng = new Random(0);
		for (int i = 0; i < 1000; i++) {
			int value = Mth.randomExclusiveInt(rng, 0, 10);
			assertTrue(value > 0 && value < 10, "Value " + value + " not in range (0, 10)");
		}
	}
	
	@Test
	void randomInclusiveIntFailsWithNullRandom() {
		assertThrows(NullPointerException.class, () -> Mth.randomInclusiveInt(null, 0, 10));
	}
	
	@Test
	void randomInclusiveIntFailsWithInvalidRange() {
		Random rng = new Random(0);
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInclusiveInt(rng, 10, 0));
		assertThrows(IllegalArgumentException.class, () -> Mth.randomInclusiveInt(rng, 10, 10));
	}
	
	@Test
	void randomInclusiveIntGeneratesInRange() {
		Random rng = new Random(0);
		for (int i = 0; i < 1000; i++) {
			int value = Mth.randomInclusiveInt(rng, 0, 10);
			assertTrue(value >= 0 && value <= 10, "Value " + value + " not in range [0, 10]");
		}
	}
	
	@Test
	void roundToWorksWithPositiveDigits() {
		assertEquals(1234.568, Mth.roundTo(1234.5678, 3), 0.0001);
		assertEquals(1234.57, Mth.roundTo(1234.5678, 2), 0.0001);
		assertEquals(1234.6, Mth.roundTo(1234.5678, 1), 0.0001);
		assertEquals(1235.0, Mth.roundTo(1234.5678, 0), 0.0001);
	}
	
	@Test
	void roundToWorksWithNegativeDigits() {
		assertEquals(1230.0, Mth.roundTo(1234.5678, -1), 0.0001);
		assertEquals(1200.0, Mth.roundTo(1234.5678, -2), 0.0001);
		assertEquals(1000.0, Mth.roundTo(1234.5678, -3), 0.0001);
		assertEquals(0.0, Mth.roundTo(1234.5678, -4), 0.0001);
	}
	
	@Test
	void roundToHandlesEdgeCases() {
		assertEquals(0.0, Mth.roundTo(0.0, 2), 0.0001);
		assertEquals(-1234.57, Mth.roundTo(-1234.5678, 2), 0.0001);
		assertEquals(1000.0, Mth.roundTo(999.999, 0), 0.0001);
	}
	
	@Test
	void isInBoundsWorksCorrectly() {
		assertTrue(Mth.isInBounds(1.0, 0.0, 2.0));
		assertTrue(Mth.isInBounds(1.0, 0.0, 1.0));
		assertTrue(Mth.isInBounds(1.0, 1.0, 2.0));
		assertTrue(Mth.isInBounds(1.0, 1.0, 1.0));
		assertFalse(Mth.isInBounds(3.0, 0.0, 2.0));
		assertFalse(Mth.isInBounds(-1.0, 0.0, 2.0));
	}
	
	@Test
	void isInBoundsHandlesEdgeCases() {
		assertTrue(Mth.isInBounds(Double.MIN_VALUE, 0.0, 1.0));
		assertTrue(Mth.isInBounds(Double.MAX_VALUE, 0.0, Double.MAX_VALUE));
		assertFalse(Mth.isInBounds(Double.POSITIVE_INFINITY, 0.0, 1.0));
		assertTrue(Mth.isInBounds(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, 0.0));
	}
	
	@Test
	void sameValuesHandlesEmptyAndSingle() {
		assertFalse(Mth.sameValues());
		assertFalse(Mth.sameValues((Number[]) null));
		assertTrue(Mth.sameValues(0));
		assertTrue(Mth.sameValues(5));
	}
	
	@Test
	void sameValuesWorksCorrectly() {
		assertTrue(Mth.sameValues(1, 1.0, 1));
		assertTrue(Mth.sameValues(0, 0.0, 0L));
		assertFalse(Mth.sameValues(0, 1.0, 0.5));
		assertFalse(Mth.sameValues(1, 2, 3));
	}
	
	@Test
	void sameValuesHandlesMixedTypes() {
		assertTrue(Mth.sameValues(5, 5.0, 5L, (byte) 5));
		assertFalse(Mth.sameValues(5, 5.7, 5L));
		assertTrue(Mth.sameValues(-1, -1.0, -1L));
	}
	
	@Test
	void fracReturnsCorrectFraction() {
		assertEquals(0.5, Mth.frac(1.5), 0.0001);
		assertEquals(0.25, Mth.frac(2.25), 0.0001);
		assertEquals(0.0, Mth.frac(3.0), 0.0001);
		assertEquals(0.999, Mth.frac(0.999), 0.0001);
	}
	
	@Test
	void fracHandlesNegativeNumbers() {
		assertEquals(-0.5, Mth.frac(-1.5), 0.0001);
		assertEquals(-0.25, Mth.frac(-2.25), 0.0001);
		assertEquals(0.0, Mth.frac(-3.0), 0.0001);
	}
	
	@Test
	void clampIntWorksCorrectly() {
		assertEquals(0, Mth.clamp(-1, 0, 9));
		assertEquals(4, Mth.clamp(4, 0, 9));
		assertEquals(9, Mth.clamp(10, 0, 9));
		assertEquals(5, Mth.clamp(5, 5, 5));
	}
	
	@Test
	void clampLongWorksCorrectly() {
		assertEquals(0L, Mth.clamp(-1L, 0L, 9L));
		assertEquals(4L, Mth.clamp(4L, 0L, 9L));
		assertEquals(9L, Mth.clamp(10L, 0L, 9L));
		assertEquals(5L, Mth.clamp(5L, 5L, 5L));
	}
	
	@Test
	void clampDoubleWorksCorrectly() {
		assertEquals(0.0, Mth.clamp(-1.0, 0.0, 9.0));
		assertEquals(4.0, Mth.clamp(4.0, 0.0, 9.0));
		assertEquals(9.0, Mth.clamp(10.0, 0.0, 9.0));
		assertEquals(5.0, Mth.clamp(5.0, 5.0, 5.0));
	}
	
	@Test
	void minIntHandlesEmpty() {
		assertEquals(Integer.MAX_VALUE, Mth.min((int[]) null));
		assertEquals(Integer.MAX_VALUE, Mth.min());
	}
	
	@Test
	void minIntFindsMinimum() {
		assertEquals(0, Mth.min(0, 1, 2, 3, 4));
		assertEquals(-5, Mth.min(1, 2, -5, 3, 4));
		assertEquals(1, Mth.min(1));
		assertEquals(Integer.MIN_VALUE, Mth.min(Integer.MIN_VALUE, 0, Integer.MAX_VALUE));
	}
	
	@Test
	void minLongHandlesEmpty() {
		assertEquals(Long.MAX_VALUE, Mth.min((long[]) null));
	}
	
	@Test
	void minLongFindsMinimum() {
		assertEquals(0L, Mth.min(0L, 1L, 2L, 3L, 4L));
		assertEquals(-5L, Mth.min(1L, 2L, -5L, 3L, 4L));
		assertEquals(Long.MIN_VALUE, Mth.min(Long.MIN_VALUE, 0L, Long.MAX_VALUE));
	}
	
	@Test
	void minDoubleHandlesEmpty() {
		assertEquals(Double.MAX_VALUE, Mth.min((double[]) null));
	}
	
	@Test
	void minDoubleFindsMinimum() {
		assertEquals(0.0, Mth.min(0.0, 1.0, 2.0, 3.0, 4.0));
		assertEquals(-5.5, Mth.min(1.0, 2.0, -5.5, 3.0, 4.0));
		assertEquals(-Double.MAX_VALUE, Mth.min(-Double.MAX_VALUE, 0.0, Double.MAX_VALUE));
	}
	
	@Test
	void maxIntHandlesEmpty() {
		assertEquals(Integer.MIN_VALUE, Mth.max((int[]) null));
		assertEquals(Integer.MIN_VALUE, Mth.max());
	}
	
	@Test
	void maxIntFindsMaximum() {
		assertEquals(4, Mth.max(0, 1, 2, 3, 4));
		assertEquals(5, Mth.max(1, 2, -5, 3, 5));
		assertEquals(1, Mth.max(1));
		assertEquals(Integer.MAX_VALUE, Mth.max(Integer.MIN_VALUE, 0, Integer.MAX_VALUE));
	}
	
	@Test
	void maxLongHandlesEmpty() {
		assertEquals(Long.MIN_VALUE, Mth.max((long[]) null));
	}
	
	@Test
	void maxLongFindsMaximum() {
		assertEquals(4L, Mth.max(0L, 1L, 2L, 3L, 4L));
		assertEquals(5L, Mth.max(1L, 2L, -5L, 3L, 5L));
		assertEquals(Long.MAX_VALUE, Mth.max(Long.MIN_VALUE, 0L, Long.MAX_VALUE));
	}
	
	@Test
	void maxDoubleHandlesEmpty() {
		assertEquals(Double.MIN_VALUE, Mth.max((double[]) null));
	}
	
	@Test
	void maxDoubleFindsMaximum() {
		assertEquals(4.0, Mth.max(0.0, 1.0, 2.0, 3.0, 4.0));
		assertEquals(5.5, Mth.max(1.0, 2.0, -5.0, 3.0, 5.5));
		assertEquals(Double.MAX_VALUE, Mth.max(Double.MIN_VALUE, 0.0, Double.MAX_VALUE));
	}
	
	@Test
	void averageIntHandlesEmpty() {
		assertTrue(Double.isNaN(Mth.average()));
		assertTrue(Double.isNaN(Mth.average((int[]) null)));
	}
	
	@Test
	void averageIntCalculatesCorrectly() {
		assertEquals(2.0, Mth.average(0, 1, 2, 3, 4));
		assertEquals(1.2, Mth.average(1, 2, -1, 3, 1));
		assertEquals(5.0, Mth.average(5));
		assertEquals(0.0, Mth.average(0, 0, 0));
	}
	
	@Test
	void averageLongHandlesEmpty() {
		assertTrue(Double.isNaN(Mth.average((long[]) null)));
	}
	
	@Test
	void averageLongCalculatesCorrectly() {
		assertEquals(2.0, Mth.average(0L, 1L, 2L, 3L, 4L));
		assertEquals(1.2, Mth.average(1L, 2L, -1L, 3L, 1L));
		assertEquals(5.0, Mth.average(5L));
	}
	
	@Test
	void averageDoubleHandlesEmpty() {
		assertTrue(Double.isNaN(Mth.average((double[]) null)));
	}
	
	@Test
	void averageDoubleCalculatesCorrectly() {
		assertEquals(2.0, Mth.average(0.0, 1.0, 2.0, 3.0, 4.0));
		assertEquals(1.2, Mth.average(1.0, 2.0, -1.0, 3.0, 1.0));
		assertEquals(5.0, Mth.average(5.0));
	}
	
	@Test
	void isPowerOfTwoWorksCorrectly() {
		assertFalse(Mth.isPowerOfTwo(0));
		assertTrue(Mth.isPowerOfTwo(1));
		assertTrue(Mth.isPowerOfTwo(2));
		assertFalse(Mth.isPowerOfTwo(3));
		assertTrue(Mth.isPowerOfTwo(4));
		assertFalse(Mth.isPowerOfTwo(5));
		assertTrue(Mth.isPowerOfTwo(8));
		assertTrue(Mth.isPowerOfTwo(16));
		assertFalse(Mth.isPowerOfTwo(15));
	}
	
	@Test
	void isPowerOfTwoHandlesLargeNumbers() {
		assertTrue(Mth.isPowerOfTwo(1024));
		assertTrue(Mth.isPowerOfTwo(1073741824)); // 2^30
		assertFalse(Mth.isPowerOfTwo(1000));
		assertFalse(Mth.isPowerOfTwo(Integer.MAX_VALUE));
	}
	
	@Test
	void isPowerOfTwoHandlesNegativeNumbers() {
		assertFalse(Mth.isPowerOfTwo(-1));
		assertFalse(Mth.isPowerOfTwo(-2));
		assertFalse(Mth.isPowerOfTwo(-4));
		assertFalse(Mth.isPowerOfTwo(Integer.MIN_VALUE));
	}
	
	@Test
	void parseHexToBigDecimalFailsWithNull() {
		assertThrows(NullPointerException.class, () -> Mth.parseHexToBigDecimal(null));
	}
	
	@Test
	void parseHexToBigDecimalFailsWithInvalidFormat() {
		assertThrows(NumberFormatException.class, () -> Mth.parseHexToBigDecimal("10"));
		assertThrows(NumberFormatException.class, () -> Mth.parseHexToBigDecimal("0xf.f"));
		assertThrows(NumberFormatException.class, () -> Mth.parseHexToBigDecimal("0xFF"));
	}
	
	@Test
	void parseHexToBigDecimalWorksCorrectly() {
		assertEquals(new BigDecimal("255"), Mth.parseHexToBigDecimal("0xF.Fp+4"));
		assertEquals(new BigDecimal("0.10009765625"), Mth.parseHexToBigDecimal("0x1.9Ap-4"));
		assertEquals(new BigDecimal("-2"), Mth.parseHexToBigDecimal("-0x1p+1"));
		assertEquals(new BigDecimal("16"), Mth.parseHexToBigDecimal("+0x1p+4"));
		assertEquals(new BigDecimal("4"), Mth.parseHexToBigDecimal("0x1.p2"));
		assertEquals(new BigDecimal("1"), Mth.parseHexToBigDecimal("0x10p-4"));
	}
	
	@Test
	void parseHexToBigDecimalHandlesLargeNumbers() {
		assertEquals(new BigDecimal(BigInteger.valueOf(2).pow(4096)), Mth.parseHexToBigDecimal("0x1p+4096"));
		assertEquals(new BigDecimal(BigInteger.valueOf(2).pow(1000)), Mth.parseHexToBigDecimal("0x1p+1000"));
	}
	
	@Test
	void parseHexToBigDecimalHandlesFractionalParts() {
		assertEquals(new BigDecimal("0.5"), Mth.parseHexToBigDecimal("0x1p-1"));
		assertEquals(new BigDecimal("0.25"), Mth.parseHexToBigDecimal("0x1p-2"));
		assertEquals(new BigDecimal("1.5"), Mth.parseHexToBigDecimal("0x1.8p0"));
	}
}
