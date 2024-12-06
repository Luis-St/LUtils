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

package net.luis.utils.math;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NumberType}.<br>
 *
 * @author Luis-St
 */
class NumberTypeTest {
	
	@Test
	void getBySuffix() {
	}
	
	@Test
	void getNumberClass() {
		assertEquals(Byte.class, NumberType.BYTE.getNumberClass());
		assertEquals(Short.class, NumberType.SHORT.getNumberClass());
		assertEquals(Integer.class, NumberType.INTEGER.getNumberClass());
		assertEquals(Long.class, NumberType.LONG.getNumberClass());
		assertEquals(BigInteger.class, NumberType.BIG_INTEGER.getNumberClass());
		assertEquals(Float.class, NumberType.FLOAT.getNumberClass());
		assertEquals(Double.class, NumberType.DOUBLE.getNumberClass());
		assertEquals(BigDecimal.class, NumberType.BIG_DECIMAL.getNumberClass());
	}
	
	@Test
	void getBitSize() {
		assertEquals(Byte.SIZE, NumberType.BYTE.getBitSize());
		assertEquals(Short.SIZE, NumberType.SHORT.getBitSize());
		assertEquals(Integer.SIZE, NumberType.INTEGER.getBitSize());
		assertEquals(Long.SIZE, NumberType.LONG.getBitSize());
		assertEquals(-1, NumberType.BIG_INTEGER.getBitSize());
		assertEquals(Float.SIZE, NumberType.FLOAT.getBitSize());
		assertEquals(Double.SIZE, NumberType.DOUBLE.getBitSize());
		assertEquals(-1, NumberType.BIG_DECIMAL.getBitSize());
	}
	
	@Test
	void getMinValue() {
		assertEquals(Byte.MIN_VALUE, NumberType.BYTE.getMinValue());
		assertEquals(Short.MIN_VALUE, NumberType.SHORT.getMinValue());
		assertEquals(Integer.MIN_VALUE, NumberType.INTEGER.getMinValue());
		assertEquals(Long.MIN_VALUE, NumberType.LONG.getMinValue());
		assertNull(NumberType.BIG_INTEGER.getMinValue());
		assertEquals(Float.MIN_VALUE, NumberType.FLOAT.getMinValue());
		assertEquals(Double.MIN_VALUE, NumberType.DOUBLE.getMinValue());
		assertNull(NumberType.BIG_DECIMAL.getMinValue());
	}
	
	@Test
	void getMaxValue() {
		assertEquals(Byte.MAX_VALUE, NumberType.BYTE.getMaxValue());
		assertEquals(Short.MAX_VALUE, NumberType.SHORT.getMaxValue());
		assertEquals(Integer.MAX_VALUE, NumberType.INTEGER.getMaxValue());
		assertEquals(Long.MAX_VALUE, NumberType.LONG.getMaxValue());
		assertNull(NumberType.BIG_INTEGER.getMaxValue());
		assertEquals(Float.MAX_VALUE, NumberType.FLOAT.getMaxValue());
		assertEquals(Double.MAX_VALUE, NumberType.DOUBLE.getMaxValue());
		assertNull(NumberType.BIG_DECIMAL.getMaxValue());
	}
	
	@Test
	void isInfinite() {
		assertFalse(NumberType.BYTE.isInfinite());
		assertFalse(NumberType.SHORT.isInfinite());
		assertFalse(NumberType.INTEGER.isInfinite());
		assertFalse(NumberType.LONG.isInfinite());
		assertTrue(NumberType.BIG_INTEGER.isInfinite());
		assertFalse(NumberType.FLOAT.isInfinite());
		assertFalse(NumberType.DOUBLE.isInfinite());
		assertTrue(NumberType.BIG_DECIMAL.isInfinite());
	}
	
	@Test
	void isInRange() {
		assertThrows(NullPointerException.class, () -> NumberType.BYTE.isInRange(null));
		assertTrue(NumberType.BYTE.isInRange(Byte.MIN_VALUE));
		assertTrue(NumberType.BYTE.isInRange(Byte.MAX_VALUE));
		assertFalse(NumberType.BYTE.isInRange(Byte.MIN_VALUE - 1));
		assertFalse(NumberType.BYTE.isInRange(Byte.MAX_VALUE + 1));
		
		assertThrows(NullPointerException.class, () -> NumberType.SHORT.isInRange(null));
		assertTrue(NumberType.SHORT.isInRange(Short.MIN_VALUE));
		assertTrue(NumberType.SHORT.isInRange(Short.MAX_VALUE));
		assertFalse(NumberType.SHORT.isInRange(Short.MIN_VALUE - 1));
		assertFalse(NumberType.SHORT.isInRange(Short.MAX_VALUE + 1));
		
		assertThrows(NullPointerException.class, () -> NumberType.INTEGER.isInRange(null));
		assertTrue(NumberType.INTEGER.isInRange(Integer.MIN_VALUE));
		assertTrue(NumberType.INTEGER.isInRange(Integer.MAX_VALUE));
		assertFalse(NumberType.INTEGER.isInRange(Integer.MIN_VALUE - 1L));
		assertFalse(NumberType.INTEGER.isInRange(Integer.MAX_VALUE + 1L));
		
		assertThrows(NullPointerException.class, () -> NumberType.LONG.isInRange(null));
		assertTrue(NumberType.LONG.isInRange(Long.MIN_VALUE));
		assertTrue(NumberType.LONG.isInRange(Long.MAX_VALUE));
		// This test is not possible because of a numeric overflow
		//assertFalse(NumberType.LONG.isInRange(Long.MIN_VALUE - 1));
		//assertFalse(NumberType.LONG.isInRange(Long.MAX_VALUE + 1));
		
		assertThrows(NullPointerException.class, () -> NumberType.BIG_INTEGER.isInRange(null));
		assertTrue(NumberType.BIG_INTEGER.isInRange(BigInteger.valueOf(Long.MIN_VALUE)));
		assertTrue(NumberType.BIG_INTEGER.isInRange(BigInteger.valueOf(Long.MAX_VALUE)));
		
		assertThrows(NullPointerException.class, () -> NumberType.FLOAT.isInRange(null));
		assertTrue(NumberType.FLOAT.isInRange(Float.MIN_VALUE));
		assertTrue(NumberType.FLOAT.isInRange(Float.MAX_VALUE));
		assertFalse(NumberType.FLOAT.isInRange(Float.MIN_VALUE - 1));
		assertFalse(NumberType.FLOAT.isInRange(Float.MAX_VALUE * 2.0D));
		
		assertThrows(NullPointerException.class, () -> NumberType.DOUBLE.isInRange(null));
		assertTrue(NumberType.DOUBLE.isInRange(Double.MIN_VALUE));
		assertTrue(NumberType.DOUBLE.isInRange(Double.MAX_VALUE));
		assertFalse(NumberType.DOUBLE.isInRange(Double.MIN_VALUE - 1));
		assertFalse(NumberType.DOUBLE.isInRange(Double.MAX_VALUE * 2.0D));
		
		assertThrows(NullPointerException.class, () -> NumberType.BIG_DECIMAL.isInRange(null));
		assertTrue(NumberType.BIG_DECIMAL.isInRange(Double.NEGATIVE_INFINITY));
		assertTrue(NumberType.BIG_DECIMAL.isInRange(Double.POSITIVE_INFINITY));
	}
	
	@Test
	void isFloatingPoint() {
		assertFalse(NumberType.BYTE.isFloatingPoint());
		assertFalse(NumberType.SHORT.isFloatingPoint());
		assertFalse(NumberType.INTEGER.isFloatingPoint());
		assertFalse(NumberType.LONG.isFloatingPoint());
		assertFalse(NumberType.BIG_INTEGER.isFloatingPoint());
		assertTrue(NumberType.FLOAT.isFloatingPoint());
		assertTrue(NumberType.DOUBLE.isFloatingPoint());
		assertTrue(NumberType.BIG_DECIMAL.isFloatingPoint());
	}
	
	@Test
	void getSuffix() {
		assertEquals('b', NumberType.BYTE.getSuffix());
		assertEquals('s', NumberType.SHORT.getSuffix());
		assertEquals('i', NumberType.INTEGER.getSuffix());
		assertEquals('l', NumberType.LONG.getSuffix());
		assertEquals('\0', NumberType.BIG_INTEGER.getSuffix());
		assertEquals('f', NumberType.FLOAT.getSuffix());
		assertEquals('d', NumberType.DOUBLE.getSuffix());
		assertEquals('\0', NumberType.BIG_DECIMAL.getSuffix());
	}
	
	@Test
	void getSupportedRadices() {
		assertEquals(Set.of(Radix.values()), NumberType.BYTE.getSupportedRadices());
		assertEquals(Set.of(Radix.values()), NumberType.SHORT.getSupportedRadices());
		assertEquals(Set.of(Radix.values()), NumberType.INTEGER.getSupportedRadices());
		assertEquals(Set.of(Radix.values()), NumberType.LONG.getSupportedRadices());
		assertEquals(Set.of(Radix.values()), NumberType.BIG_INTEGER.getSupportedRadices());
		assertEquals(Set.of(Radix.DECIMAL, Radix.HEXADECIMAL), NumberType.FLOAT.getSupportedRadices());
		assertEquals(Set.of(Radix.DECIMAL, Radix.HEXADECIMAL), NumberType.DOUBLE.getSupportedRadices());
		assertEquals(Set.of(Radix.DECIMAL, Radix.HEXADECIMAL), NumberType.BIG_DECIMAL.getSupportedRadices());
	}
	
	@Test
	void isSupportedRadix() {
		assertDoesNotThrow(() -> NumberType.BYTE.isSupportedRadix(null));
		assertDoesNotThrow(() -> NumberType.SHORT.isSupportedRadix(null));
		assertDoesNotThrow(() -> NumberType.INTEGER.isSupportedRadix(null));
		assertDoesNotThrow(() -> NumberType.LONG.isSupportedRadix(null));
		assertDoesNotThrow(() -> NumberType.BIG_INTEGER.isSupportedRadix(null));
		assertDoesNotThrow(() -> NumberType.FLOAT.isSupportedRadix(null));
		assertDoesNotThrow(() -> NumberType.DOUBLE.isSupportedRadix(null));
		assertDoesNotThrow(() -> NumberType.BIG_DECIMAL.isSupportedRadix(null));
		
		for (Radix radix : Radix.values()) {
			assertTrue(NumberType.BYTE.isSupportedRadix(radix));
			assertTrue(NumberType.SHORT.isSupportedRadix(radix));
			assertTrue(NumberType.INTEGER.isSupportedRadix(radix));
			assertTrue(NumberType.LONG.isSupportedRadix(radix));
			assertTrue(NumberType.BIG_INTEGER.isSupportedRadix(radix));
		}
		
		for (Radix radix : Set.of(Radix.DECIMAL, Radix.HEXADECIMAL)) {
			assertTrue(NumberType.FLOAT.isSupportedRadix(radix));
			assertTrue(NumberType.DOUBLE.isSupportedRadix(radix));
			assertTrue(NumberType.BIG_DECIMAL.isSupportedRadix(radix));
		}
		for (Radix radix : Set.of(Radix.BINARY, Radix.OCTAL)) {
			assertFalse(NumberType.FLOAT.isSupportedRadix(radix));
			assertFalse(NumberType.DOUBLE.isSupportedRadix(radix));
			assertFalse(NumberType.BIG_DECIMAL.isSupportedRadix(radix));
		}
	}
	
	@Test
	void canConvertTo() {
		assertThrows(NullPointerException.class, () -> NumberType.BYTE.canConvertTo(null));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.BYTE));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.SHORT));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.INTEGER));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.LONG));
		assertFalse(NumberType.BYTE.canConvertTo(NumberType.BIG_INTEGER));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.DOUBLE));
		assertFalse(NumberType.BYTE.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.SHORT.canConvertTo(null));
		assertFalse(NumberType.SHORT.canConvertTo(NumberType.BYTE));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.SHORT));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.INTEGER));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.LONG));
		assertFalse(NumberType.SHORT.canConvertTo(NumberType.BIG_INTEGER));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.DOUBLE));
		assertFalse(NumberType.SHORT.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.INTEGER.canConvertTo(null));
		assertFalse(NumberType.INTEGER.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.INTEGER.canConvertTo(NumberType.SHORT));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.INTEGER));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.LONG));
		assertFalse(NumberType.INTEGER.canConvertTo(NumberType.BIG_INTEGER));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.DOUBLE));
		assertFalse(NumberType.INTEGER.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.LONG.canConvertTo(null));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.SHORT));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.INTEGER));
		assertTrue(NumberType.LONG.canConvertTo(NumberType.LONG));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.BIG_INTEGER));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.LONG.canConvertTo(NumberType.DOUBLE));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.BIG_INTEGER.canConvertTo(null));
		assertFalse(NumberType.BIG_INTEGER.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.BIG_INTEGER.canConvertTo(NumberType.SHORT));
		assertFalse(NumberType.BIG_INTEGER.canConvertTo(NumberType.INTEGER));
		assertFalse(NumberType.BIG_INTEGER.canConvertTo(NumberType.LONG));
		assertTrue(NumberType.BIG_INTEGER.canConvertTo(NumberType.BIG_INTEGER));
		assertFalse(NumberType.BIG_INTEGER.canConvertTo(NumberType.FLOAT));
		assertFalse(NumberType.BIG_INTEGER.canConvertTo(NumberType.DOUBLE));
		assertTrue(NumberType.BIG_INTEGER.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.FLOAT.canConvertTo(null));
		assertFalse(NumberType.FLOAT.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.FLOAT.canConvertTo(NumberType.SHORT));
		assertFalse(NumberType.FLOAT.canConvertTo(NumberType.INTEGER));
		assertFalse(NumberType.FLOAT.canConvertTo(NumberType.LONG));
		assertFalse(NumberType.FLOAT.canConvertTo(NumberType.BIG_INTEGER));
		assertTrue(NumberType.FLOAT.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.FLOAT.canConvertTo(NumberType.DOUBLE));
		assertFalse(NumberType.FLOAT.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.DOUBLE.canConvertTo(null));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.SHORT));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.INTEGER));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.LONG));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.BIG_INTEGER));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.DOUBLE.canConvertTo(NumberType.DOUBLE));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.BIG_DECIMAL.canConvertTo(null));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.SHORT));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.INTEGER));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.LONG));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.BIG_INTEGER));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.FLOAT));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.DOUBLE));
		assertTrue(NumberType.BIG_DECIMAL.canConvertTo(NumberType.BIG_DECIMAL));
	}
	
	@Test
	void parseNumber() {
	}
}
