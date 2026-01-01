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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NumberType}.<br>
 *
 * @author Luis-St
 */
class NumberTypeTest {
	
	@Test
	void getBySuffixReturnsCorrectType() {
		assertEquals(NumberType.BYTE, NumberType.getBySuffix('b'));
		assertEquals(NumberType.SHORT, NumberType.getBySuffix('s'));
		assertEquals(NumberType.INTEGER, NumberType.getBySuffix('i'));
		assertEquals(NumberType.LONG, NumberType.getBySuffix('l'));
		assertEquals(NumberType.FLOAT, NumberType.getBySuffix('f'));
		assertEquals(NumberType.DOUBLE, NumberType.getBySuffix('d'));
	}
	
	@Test
	void getBySuffixReturnsNullForInvalidSuffix() {
		assertNull(NumberType.getBySuffix('\0'));
		assertNull(NumberType.getBySuffix('x'));
		assertNull(NumberType.getBySuffix('B'));
		assertNull(NumberType.getBySuffix('1'));
		assertNull(NumberType.getBySuffix(' '));
	}
	
	@Test
	void getNumberClassReturnsCorrectClass() {
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
	void getBitSizeReturnsCorrectSize() {
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
	void getMinValueReturnsCorrectValue() {
		assertEquals(Byte.MIN_VALUE, NumberType.BYTE.getMinValue());
		assertEquals(Short.MIN_VALUE, NumberType.SHORT.getMinValue());
		assertEquals(Integer.MIN_VALUE, NumberType.INTEGER.getMinValue());
		assertEquals(Long.MIN_VALUE, NumberType.LONG.getMinValue());
		assertNull(NumberType.BIG_INTEGER.getMinValue());
		assertEquals(-Float.MAX_VALUE, NumberType.FLOAT.getMinValue());
		assertEquals(-Double.MAX_VALUE, NumberType.DOUBLE.getMinValue());
		assertNull(NumberType.BIG_DECIMAL.getMinValue());
	}
	
	@Test
	void getMaxValueReturnsCorrectValue() {
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
	void isInfiniteIdentifiesCorrectTypes() {
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
	void isInRangeFailsWithNull() {
		assertThrows(NullPointerException.class, () -> NumberType.BYTE.isInRange(null));
		assertThrows(NullPointerException.class, () -> NumberType.SHORT.isInRange(null));
		assertThrows(NullPointerException.class, () -> NumberType.INTEGER.isInRange(null));
		assertThrows(NullPointerException.class, () -> NumberType.LONG.isInRange(null));
		assertThrows(NullPointerException.class, () -> NumberType.BIG_INTEGER.isInRange(null));
		assertThrows(NullPointerException.class, () -> NumberType.FLOAT.isInRange(null));
		assertThrows(NullPointerException.class, () -> NumberType.DOUBLE.isInRange(null));
		assertThrows(NullPointerException.class, () -> NumberType.BIG_DECIMAL.isInRange(null));
	}
	
	@Test
	void isInRangeWorksForByte() {
		assertTrue(NumberType.BYTE.isInRange(Byte.MIN_VALUE));
		assertTrue(NumberType.BYTE.isInRange(Byte.MAX_VALUE));
		assertTrue(NumberType.BYTE.isInRange((byte) 0));
		assertFalse(NumberType.BYTE.isInRange(Byte.MIN_VALUE - 1));
		assertFalse(NumberType.BYTE.isInRange(Byte.MAX_VALUE + 1));
		assertFalse(NumberType.BYTE.isInRange(1000));
	}
	
	@Test
	void isInRangeWorksForShort() {
		assertTrue(NumberType.SHORT.isInRange(Short.MIN_VALUE));
		assertTrue(NumberType.SHORT.isInRange(Short.MAX_VALUE));
		assertTrue(NumberType.SHORT.isInRange((short) 0));
		assertFalse(NumberType.SHORT.isInRange(Short.MIN_VALUE - 1));
		assertFalse(NumberType.SHORT.isInRange(Short.MAX_VALUE + 1));
		assertFalse(NumberType.SHORT.isInRange(100000));
	}
	
	@Test
	void isInRangeWorksForInteger() {
		assertTrue(NumberType.INTEGER.isInRange(Integer.MIN_VALUE));
		assertTrue(NumberType.INTEGER.isInRange(Integer.MAX_VALUE));
		assertTrue(NumberType.INTEGER.isInRange(0));
		assertFalse(NumberType.INTEGER.isInRange(Integer.MIN_VALUE - 1L));
		assertFalse(NumberType.INTEGER.isInRange(Integer.MAX_VALUE + 1L));
	}
	
	@Test
	void isInRangeWorksForLong() {
		assertTrue(NumberType.LONG.isInRange(Long.MIN_VALUE));
		assertTrue(NumberType.LONG.isInRange(Long.MAX_VALUE));
		assertTrue(NumberType.LONG.isInRange(0L));
	}
	
	@Test
	void isInRangeWorksForBigInteger() {
		assertTrue(NumberType.BIG_INTEGER.isInRange(BigInteger.valueOf(Long.MIN_VALUE)));
		assertTrue(NumberType.BIG_INTEGER.isInRange(BigInteger.valueOf(Long.MAX_VALUE)));
		assertTrue(NumberType.BIG_INTEGER.isInRange(BigInteger.ZERO));
		assertTrue(NumberType.BIG_INTEGER.isInRange(new BigInteger("9999999999999999999999999999")));
	}
	
	@Test
	void isInRangeWorksForFloat() {
		assertTrue(NumberType.FLOAT.isInRange(Float.MIN_VALUE));
		assertTrue(NumberType.FLOAT.isInRange(Float.MAX_VALUE));
		assertTrue(NumberType.FLOAT.isInRange(0.0f));
		assertFalse(NumberType.FLOAT.isInRange(-Double.MAX_VALUE));
		assertFalse(NumberType.FLOAT.isInRange(Double.MAX_VALUE));
	}
	
	@Test
	void isInRangeWorksForDouble() {
		assertTrue(NumberType.DOUBLE.isInRange(Double.MIN_VALUE));
		assertTrue(NumberType.DOUBLE.isInRange(Double.MAX_VALUE));
		assertTrue(NumberType.DOUBLE.isInRange(0.0));
		assertFalse(NumberType.DOUBLE.isInRange(new BigDecimal("1.7976931348623157e512")));
		assertFalse(NumberType.DOUBLE.isInRange(new BigDecimal("-1.7976931348623157e512")));
	}
	
	@Test
	void isInRangeWorksForBigDecimal() {
		assertTrue(NumberType.BIG_DECIMAL.isInRange(Double.NEGATIVE_INFINITY));
		assertTrue(NumberType.BIG_DECIMAL.isInRange(Double.POSITIVE_INFINITY));
		assertTrue(NumberType.BIG_DECIMAL.isInRange(BigDecimal.ZERO));
		assertTrue(NumberType.BIG_DECIMAL.isInRange(new BigDecimal("999999999999999999999999999.999999999")));
	}
	
	@Test
	void isFloatingPointIdentifiesCorrectTypes() {
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
	void getSuffixReturnsCorrectSuffix() {
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
	void getSupportedRadicesReturnsCorrectRadices() {
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
	void isSupportedRadixHandlesNull() {
		assertFalse(NumberType.BYTE.isSupportedRadix(null));
		assertFalse(NumberType.FLOAT.isSupportedRadix(null));
		assertFalse(NumberType.BIG_DECIMAL.isSupportedRadix(null));
	}
	
	@Test
	void isSupportedRadixWorksForIntegers() {
		for (Radix radix : Radix.values()) {
			assertTrue(NumberType.BYTE.isSupportedRadix(radix));
			assertTrue(NumberType.SHORT.isSupportedRadix(radix));
			assertTrue(NumberType.INTEGER.isSupportedRadix(radix));
			assertTrue(NumberType.LONG.isSupportedRadix(radix));
			assertTrue(NumberType.BIG_INTEGER.isSupportedRadix(radix));
		}
	}
	
	@Test
	void isSupportedRadixWorksForFloatingPoints() {
		assertTrue(NumberType.FLOAT.isSupportedRadix(Radix.DECIMAL));
		assertTrue(NumberType.FLOAT.isSupportedRadix(Radix.HEXADECIMAL));
		assertFalse(NumberType.FLOAT.isSupportedRadix(Radix.BINARY));
		assertFalse(NumberType.FLOAT.isSupportedRadix(Radix.OCTAL));
		
		assertTrue(NumberType.DOUBLE.isSupportedRadix(Radix.DECIMAL));
		assertTrue(NumberType.DOUBLE.isSupportedRadix(Radix.HEXADECIMAL));
		assertFalse(NumberType.DOUBLE.isSupportedRadix(Radix.BINARY));
		assertFalse(NumberType.DOUBLE.isSupportedRadix(Radix.OCTAL));
		
		assertTrue(NumberType.BIG_DECIMAL.isSupportedRadix(Radix.DECIMAL));
		assertTrue(NumberType.BIG_DECIMAL.isSupportedRadix(Radix.HEXADECIMAL));
		assertFalse(NumberType.BIG_DECIMAL.isSupportedRadix(Radix.BINARY));
		assertFalse(NumberType.BIG_DECIMAL.isSupportedRadix(Radix.OCTAL));
	}
	
	@Test
	void canConvertToFailsWithNull() {
		assertThrows(NullPointerException.class, () -> NumberType.BYTE.canConvertTo(null));
		assertThrows(NullPointerException.class, () -> NumberType.FLOAT.canConvertTo(null));
	}
	
	@Test
	void canConvertToSameType() {
		for (NumberType type : NumberType.values()) {
			assertTrue(type.canConvertTo(type));
		}
	}
	
	@Test
	void canConvertToSmallerIntegerTypes() {
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.SHORT));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.INTEGER));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.LONG));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.INTEGER));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.LONG));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.LONG));
		
		assertFalse(NumberType.SHORT.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.INTEGER.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.INTEGER.canConvertTo(NumberType.SHORT));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.INTEGER));
	}
	
	@Test
	void canConvertToFloatingPointTypes() {
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.LONG.canConvertTo(NumberType.DOUBLE));
		assertTrue(NumberType.FLOAT.canConvertTo(NumberType.DOUBLE));
		
		assertFalse(NumberType.FLOAT.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.FLOAT));
	}
	
	@Test
	void canConvertToInfiniteTypes() {
		for (NumberType type : NumberType.values()) {
			if (!type.isInfinite()) {
				if (!type.isFloatingPoint()) {
					assertTrue(type.canConvertTo(NumberType.BIG_INTEGER), "Failed for " + type);
				}
				assertTrue(type.canConvertTo(NumberType.BIG_DECIMAL), "Failed for " + type);
			}
		}
		
		assertTrue(NumberType.BIG_INTEGER.canConvertTo(NumberType.BIG_DECIMAL));
		assertFalse(NumberType.BIG_INTEGER.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.DOUBLE));
	}
	
	@Test
	void canConvertToStringFailsWithNullRadix() {
		assertThrows(NullPointerException.class, () -> NumberType.BYTE.canConvertTo("123", null));
	}
	
	@Test
	void canConvertToStringHandlesInvalidInputs() {
		assertFalse(NumberType.BYTE.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.BYTE.canConvertTo("", Radix.DECIMAL));
		assertFalse(NumberType.BYTE.canConvertTo("   ", Radix.DECIMAL));
		assertFalse(NumberType.FLOAT.canConvertTo("123", Radix.BINARY));
	}
	
	@Test
	void canConvertToStringDetectsValidNumbers() {
		assertTrue(NumberType.BYTE.canConvertTo("127", Radix.DECIMAL));
		assertTrue(NumberType.BYTE.canConvertTo("-128", Radix.DECIMAL));
		assertFalse(NumberType.BYTE.canConvertTo("128", Radix.DECIMAL));
		assertFalse(NumberType.BYTE.canConvertTo("-129", Radix.DECIMAL));
		
		assertTrue(NumberType.INTEGER.canConvertTo("0x7FFFFFFF", Radix.HEXADECIMAL));
		assertFalse(NumberType.INTEGER.canConvertTo("0x80000000", Radix.HEXADECIMAL));
		
		assertTrue(NumberType.FLOAT.canConvertTo("3.14", Radix.DECIMAL));
		assertTrue(NumberType.FLOAT.canConvertTo("0x1.0p0", Radix.HEXADECIMAL));
	}
	
	@Test
	void canConvertToStringHandlesSuffixes() {
		assertFalse(NumberType.BYTE.canConvertTo("10s", Radix.DECIMAL));
		assertTrue(NumberType.BYTE.canConvertTo("10b", Radix.DECIMAL));
		assertFalse(NumberType.SHORT.canConvertTo("10i", Radix.DECIMAL));
		assertTrue(NumberType.SHORT.canConvertTo("10s", Radix.DECIMAL));
	}
	
	@Test
	void parseNumberFailsWithUnsupportedRadix() {
		assertThrows(IllegalArgumentException.class, () -> NumberType.FLOAT.parseNumber("123", Radix.BINARY));
		assertThrows(IllegalArgumentException.class, () -> NumberType.DOUBLE.parseNumber("123", Radix.OCTAL));
	}
	
	@Test
	void parseNumberHandlesNullAndEmpty() {
		assertEquals((byte) 0, (byte) NumberType.BYTE.parseNumber(null, Radix.DECIMAL));
		assertEquals((byte) 0, (byte) NumberType.BYTE.parseNumber("", Radix.DECIMAL));
		assertEquals((byte) 0, (byte) NumberType.BYTE.parseNumber("   ", Radix.DECIMAL));
	}
	
	@Test
	void parseNumberDetectsRadixFromPrefix() {
		assertEquals((byte) 15, (byte) NumberType.BYTE.parseNumber("0xF", null));
		assertEquals((byte) 7, (byte) NumberType.BYTE.parseNumber("0b111", null));
		assertEquals((byte) 10, (byte) NumberType.BYTE.parseNumber("10", null));
	}
	
	@Test
	void parseNumberHandlesSuffixes() {
		assertEquals((byte) 10, (byte) NumberType.BYTE.parseNumber("10b", Radix.DECIMAL));
		assertEquals((short) 10, (short) NumberType.SHORT.parseNumber("10s", Radix.DECIMAL));
		assertEquals(10, (int) NumberType.INTEGER.parseNumber("10i", Radix.DECIMAL));
		assertEquals(10L, (long) NumberType.LONG.parseNumber("10l", Radix.DECIMAL));
		assertEquals(10.0f, (float) NumberType.FLOAT.parseNumber("10f", Radix.DECIMAL));
		assertEquals(10.0, (double) NumberType.DOUBLE.parseNumber("10d", Radix.DECIMAL));
	}
	
	@Test
	void parseNumberWorksForAllTypes() {
		assertEquals(Byte.MAX_VALUE, (byte) NumberType.BYTE.parseNumber("127", Radix.DECIMAL));
		assertEquals(Short.MAX_VALUE, (short) NumberType.SHORT.parseNumber("32767", Radix.DECIMAL));
		assertEquals(Integer.MAX_VALUE, (int) NumberType.INTEGER.parseNumber("2147483647", Radix.DECIMAL));
		assertEquals(Long.MAX_VALUE, (long) NumberType.LONG.parseNumber("9223372036854775807", Radix.DECIMAL));
		assertEquals(BigInteger.valueOf(Long.MAX_VALUE), NumberType.BIG_INTEGER.parseNumber("9223372036854775807", Radix.DECIMAL));
		assertEquals(3.14f, (float) NumberType.FLOAT.parseNumber("3.14", Radix.DECIMAL), 0.001f);
		assertEquals(3.14, (double) NumberType.DOUBLE.parseNumber("3.14", Radix.DECIMAL), 0.001);
		assertEquals(new BigDecimal("3.14"), NumberType.BIG_DECIMAL.parseNumber("3.14", Radix.DECIMAL));
	}
	
	@Test
	void parseNumberStrictFailsWithNulls() {
		assertThrows(NullPointerException.class, () -> NumberType.BYTE.parseNumberStrict(null, Radix.DECIMAL));
		assertThrows(NullPointerException.class, () -> NumberType.BYTE.parseNumberStrict("123", null));
	}
	
	@Test
	void parseNumberStrictFailsWithEmptyString() {
		assertThrows(IllegalArgumentException.class, () -> NumberType.BYTE.parseNumberStrict("", Radix.DECIMAL));
		assertThrows(IllegalArgumentException.class, () -> NumberType.BYTE.parseNumberStrict("   ", Radix.DECIMAL));
	}
	
	@Test
	void parseNumberStrictFailsWithUnsupportedRadix() {
		assertThrows(IllegalArgumentException.class, () -> NumberType.FLOAT.parseNumberStrict("123", Radix.BINARY));
	}
	
	@Test
	void parseNumberStrictFailsWithPrefixes() {
		assertThrows(NumberFormatException.class, () -> NumberType.BYTE.parseNumberStrict("0x7F", Radix.HEXADECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.INTEGER.parseNumberStrict("0b1010", Radix.BINARY));
	}
	
	@Test
	void parseNumberStrictFailsWithSuffixes() {
		assertThrows(NumberFormatException.class, () -> NumberType.BYTE.parseNumberStrict("10b", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.FLOAT.parseNumberStrict("10f", Radix.DECIMAL));
	}
	
	@Test
	void parseNumberStrictWorksCorrectly() {
		assertEquals((byte) 127, (byte) NumberType.BYTE.parseNumberStrict("127", Radix.DECIMAL));
		assertEquals((byte) -128, (byte) NumberType.BYTE.parseNumberStrict("-128", Radix.DECIMAL));
		assertEquals((byte) 127, (byte) NumberType.BYTE.parseNumberStrict("7F", Radix.HEXADECIMAL));
		assertEquals((byte) -128, (byte) NumberType.BYTE.parseNumberStrict("-80", Radix.HEXADECIMAL));
		
		assertEquals(10, (int) NumberType.INTEGER.parseNumberStrict("1010", Radix.BINARY));
		assertEquals(255, (int) NumberType.INTEGER.parseNumberStrict("FF", Radix.HEXADECIMAL));
		assertEquals(64, (int) NumberType.INTEGER.parseNumberStrict("100", Radix.OCTAL));
		
		assertEquals(3.14f, (float) NumberType.FLOAT.parseNumberStrict("3.14", Radix.DECIMAL), 0.001f);
		assertEquals(16.0f, (float) NumberType.FLOAT.parseNumberStrict("0x1.0p4", Radix.HEXADECIMAL), 0.001f);
	}
	
	@Test
	void toStringReturnsReadableFormat() {
		assertEquals("byte", NumberType.BYTE.toString());
		assertEquals("short", NumberType.SHORT.toString());
		assertEquals("integer", NumberType.INTEGER.toString());
		assertEquals("long", NumberType.LONG.toString());
		assertEquals("big integer", NumberType.BIG_INTEGER.toString());
		assertEquals("float", NumberType.FLOAT.toString());
		assertEquals("double", NumberType.DOUBLE.toString());
		assertEquals("big decimal", NumberType.BIG_DECIMAL.toString());
	}
}
