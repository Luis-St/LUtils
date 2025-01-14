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
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.BIG_INTEGER));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.DOUBLE));
		assertTrue(NumberType.BYTE.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.SHORT.canConvertTo(null));
		assertFalse(NumberType.SHORT.canConvertTo(NumberType.BYTE));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.SHORT));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.INTEGER));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.LONG));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.BIG_INTEGER));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.DOUBLE));
		assertTrue(NumberType.SHORT.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.INTEGER.canConvertTo(null));
		assertFalse(NumberType.INTEGER.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.INTEGER.canConvertTo(NumberType.SHORT));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.INTEGER));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.LONG));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.BIG_INTEGER));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.DOUBLE));
		assertTrue(NumberType.INTEGER.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.LONG.canConvertTo(null));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.SHORT));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.INTEGER));
		assertTrue(NumberType.LONG.canConvertTo(NumberType.LONG));
		assertTrue(NumberType.LONG.canConvertTo(NumberType.BIG_INTEGER));
		assertFalse(NumberType.LONG.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.LONG.canConvertTo(NumberType.DOUBLE));
		assertTrue(NumberType.LONG.canConvertTo(NumberType.BIG_DECIMAL));
		
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
		assertTrue(NumberType.FLOAT.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.DOUBLE.canConvertTo(null));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.SHORT));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.INTEGER));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.LONG));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.BIG_INTEGER));
		assertFalse(NumberType.DOUBLE.canConvertTo(NumberType.FLOAT));
		assertTrue(NumberType.DOUBLE.canConvertTo(NumberType.DOUBLE));
		assertTrue(NumberType.DOUBLE.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.BIG_DECIMAL.canConvertTo(null));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.BYTE));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.SHORT));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.INTEGER));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.LONG));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.BIG_INTEGER));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.FLOAT));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(NumberType.DOUBLE));
		assertTrue(NumberType.BIG_DECIMAL.canConvertTo(NumberType.BIG_DECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.BYTE.canConvertTo("", null));
		assertDoesNotThrow(() -> NumberType.BYTE.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.BYTE.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.BYTE.canConvertTo("", Radix.DECIMAL));
		assertFalse(NumberType.BYTE.canConvertTo("10s", Radix.DECIMAL));
		assertTrue(NumberType.BYTE.canConvertTo("127", Radix.DECIMAL));
		assertTrue(NumberType.BYTE.canConvertTo("-128", Radix.DECIMAL));
		assertTrue(NumberType.BYTE.canConvertTo("0x7F", Radix.HEXADECIMAL));
		assertTrue(NumberType.BYTE.canConvertTo("-0x80", Radix.HEXADECIMAL));
		assertFalse(NumberType.BYTE.canConvertTo("0x80", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.SHORT.canConvertTo("", null));
		assertDoesNotThrow(() -> NumberType.SHORT.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.SHORT.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.SHORT.canConvertTo("", Radix.DECIMAL));
		assertFalse(NumberType.SHORT.canConvertTo("10i", Radix.DECIMAL));
		assertTrue(NumberType.SHORT.canConvertTo("32767", Radix.DECIMAL));
		assertTrue(NumberType.SHORT.canConvertTo("-32768", Radix.DECIMAL));
		assertTrue(NumberType.SHORT.canConvertTo("0x7FFF", Radix.HEXADECIMAL));
		assertTrue(NumberType.SHORT.canConvertTo("-0x8000", Radix.HEXADECIMAL));
		assertFalse(NumberType.SHORT.canConvertTo("0x8000", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.INTEGER.canConvertTo("", null));
		assertDoesNotThrow(() -> NumberType.INTEGER.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.INTEGER.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.INTEGER.canConvertTo("", Radix.DECIMAL));
		assertFalse(NumberType.INTEGER.canConvertTo("10l", Radix.DECIMAL));
		assertTrue(NumberType.INTEGER.canConvertTo("2147483647", Radix.DECIMAL));
		assertTrue(NumberType.INTEGER.canConvertTo("-2147483648", Radix.DECIMAL));
		assertTrue(NumberType.INTEGER.canConvertTo("0x7FFFFFFF", Radix.HEXADECIMAL));
		assertTrue(NumberType.INTEGER.canConvertTo("-0x80000000", Radix.HEXADECIMAL));
		assertFalse(NumberType.INTEGER.canConvertTo("0x80000000", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.LONG.canConvertTo("", null));
		assertDoesNotThrow(() -> NumberType.LONG.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.LONG.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.LONG.canConvertTo("", Radix.DECIMAL));
		assertFalse(NumberType.LONG.canConvertTo("10b", Radix.DECIMAL));
		assertTrue(NumberType.LONG.canConvertTo("9223372036854775807", Radix.DECIMAL));
		assertTrue(NumberType.LONG.canConvertTo("-9223372036854775808", Radix.DECIMAL));
		assertTrue(NumberType.LONG.canConvertTo("0x7FFFFFFFFFFFFFFF", Radix.HEXADECIMAL));
		assertTrue(NumberType.LONG.canConvertTo("-0x8000000000000000", Radix.HEXADECIMAL));
		assertFalse(NumberType.LONG.canConvertTo("0x8000000000000000", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.BIG_INTEGER.canConvertTo("", null));
		assertDoesNotThrow(() -> NumberType.BIG_INTEGER.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.BIG_INTEGER.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.BIG_INTEGER.canConvertTo("", Radix.DECIMAL));
		assertFalse(NumberType.BIG_INTEGER.canConvertTo("10l", Radix.DECIMAL));
		assertTrue(NumberType.BIG_INTEGER.canConvertTo("9".repeat(100), Radix.DECIMAL));
		assertTrue(NumberType.BIG_INTEGER.canConvertTo("-" + "9".repeat(100), Radix.DECIMAL));
		assertTrue(NumberType.BIG_INTEGER.canConvertTo("0x" + "F".repeat(100), Radix.HEXADECIMAL));
		assertTrue(NumberType.BIG_INTEGER.canConvertTo("-0x" + "F".repeat(100), Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.FLOAT.canConvertTo("", null));
		assertDoesNotThrow(() -> NumberType.FLOAT.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.FLOAT.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.FLOAT.canConvertTo("", Radix.DECIMAL));
		assertFalse(NumberType.FLOAT.canConvertTo("10d", Radix.DECIMAL));
		assertTrue(NumberType.FLOAT.canConvertTo("3.4028235e38", Radix.DECIMAL));
		assertTrue(NumberType.FLOAT.canConvertTo("-3.4028235e38", Radix.DECIMAL));
		assertTrue(NumberType.FLOAT.canConvertTo("0x0.000002p-125", Radix.HEXADECIMAL));
		assertTrue(NumberType.FLOAT.canConvertTo("0x1.FFFFFEp127", Radix.HEXADECIMAL));
		// Test not possible because how floating point numbers are handling overflow during conversion
		//assertFalse(NumberType.FLOAT.canConvertTo("0x1.FFFFFFp128", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.DOUBLE.canConvertTo("", null));
		assertDoesNotThrow(() -> NumberType.DOUBLE.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.DOUBLE.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.DOUBLE.canConvertTo("", Radix.DECIMAL));
		assertFalse(NumberType.DOUBLE.canConvertTo("10f", Radix.DECIMAL));
		assertTrue(NumberType.DOUBLE.canConvertTo("1.7976931348623157e308", Radix.DECIMAL));
		assertTrue(NumberType.DOUBLE.canConvertTo("-1.7976931348623157e308", Radix.DECIMAL));
		assertTrue(NumberType.DOUBLE.canConvertTo("0x0.0000000000001p-1022", Radix.HEXADECIMAL));
		assertTrue(NumberType.DOUBLE.canConvertTo("0x1.FFFFFFFFFFFFFp1023", Radix.HEXADECIMAL));
		// Test not possible because how floating point numbers are handling overflow during conversion
		//assertFalse(NumberType.DOUBLE.canConvertTo("0x1.10000000000000p1024", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.BIG_DECIMAL.canConvertTo("", null));
		assertDoesNotThrow(() -> NumberType.BIG_DECIMAL.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo(null, Radix.DECIMAL));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo("", Radix.DECIMAL));
		assertFalse(NumberType.BIG_DECIMAL.canConvertTo("10d", Radix.DECIMAL));
		assertTrue(NumberType.BIG_DECIMAL.canConvertTo("0.9e+999999999", Radix.DECIMAL));
		assertTrue(NumberType.BIG_DECIMAL.canConvertTo("-0.9e-999999999", Radix.DECIMAL));
		assertTrue(NumberType.BIG_DECIMAL.canConvertTo("0x1.Fp+999999999", Radix.HEXADECIMAL));
		assertTrue(NumberType.BIG_DECIMAL.canConvertTo("-0x1.Fp-999999999", Radix.HEXADECIMAL));
	}
	
	@Test
	void parseNumber() {
		assertEquals((byte) 0, assertDoesNotThrow(() -> (byte) NumberType.BYTE.parseNumber(null, Radix.DECIMAL)));
		assertEquals((byte) 0, assertDoesNotThrow(() -> (byte) NumberType.BYTE.parseNumber("0", Radix.DECIMAL)));
		assertThrows(IllegalArgumentException.class, () -> NumberType.BYTE.parseNumber("F", null));
		assertEquals((byte) 15, (byte) NumberType.BYTE.parseNumber("0xF", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.BYTE.parseNumber("10s", Radix.DECIMAL));
		assertEquals((byte) 10, assertDoesNotThrow(() -> (byte) NumberType.BYTE.parseNumber("10b", Radix.DECIMAL)));
		assertEquals(Byte.MAX_VALUE, assertDoesNotThrow(() -> (byte) NumberType.BYTE.parseNumber("0x7F", Radix.HEXADECIMAL)));
		assertEquals((byte) 10, (byte) NumberType.BYTE.parseNumber("10", Radix.DECIMAL));
		assertEquals(Byte.MAX_VALUE, (byte) NumberType.BYTE.parseNumber("127", Radix.DECIMAL));
		assertEquals(Byte.MIN_VALUE, (byte) NumberType.BYTE.parseNumber("-128", Radix.DECIMAL));
		assertEquals(Byte.MAX_VALUE, (byte) NumberType.BYTE.parseNumber("7F", Radix.HEXADECIMAL));
		assertEquals(Byte.MIN_VALUE, (byte) NumberType.BYTE.parseNumber("-80", Radix.HEXADECIMAL));
		
		assertEquals((short) 0, assertDoesNotThrow(() -> (short) NumberType.SHORT.parseNumber(null, Radix.DECIMAL)));
		assertEquals((short) 0, assertDoesNotThrow(() -> (short) NumberType.SHORT.parseNumber("0", Radix.DECIMAL)));
		assertThrows(IllegalArgumentException.class, () -> NumberType.SHORT.parseNumber("F", null));
		assertEquals((short) 15, (short) NumberType.SHORT.parseNumber("0xF", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.SHORT.parseNumber("10i", Radix.DECIMAL));
		assertEquals((short) 10, assertDoesNotThrow(() -> (short) NumberType.SHORT.parseNumber("10s", Radix.DECIMAL)));
		assertEquals(Short.MAX_VALUE, assertDoesNotThrow(() -> (short) NumberType.SHORT.parseNumber("0x7FFF", Radix.HEXADECIMAL)));
		assertEquals((short) 10, (short) NumberType.SHORT.parseNumber("10", Radix.DECIMAL));
		assertEquals(Short.MAX_VALUE, (short) NumberType.SHORT.parseNumber("32767", Radix.DECIMAL));
		assertEquals(Short.MIN_VALUE, (short) NumberType.SHORT.parseNumber("-32768", Radix.DECIMAL));
		assertEquals(Short.MAX_VALUE, (short) NumberType.SHORT.parseNumber("7FFF", Radix.HEXADECIMAL));
		assertEquals(Short.MIN_VALUE, (short) NumberType.SHORT.parseNumber("-8000", Radix.HEXADECIMAL));
		
		assertEquals(0, assertDoesNotThrow(() -> (int) NumberType.INTEGER.parseNumber(null, Radix.DECIMAL)));
		assertEquals(0, assertDoesNotThrow(() -> (int) NumberType.INTEGER.parseNumber("0", Radix.DECIMAL)));
		assertThrows(IllegalArgumentException.class, () -> NumberType.INTEGER.parseNumber("F", null));
		assertEquals(15, (int) NumberType.INTEGER.parseNumber("0xF", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.INTEGER.parseNumber("10l", Radix.DECIMAL));
		assertEquals(10, assertDoesNotThrow(() -> (int) NumberType.INTEGER.parseNumber("10i", Radix.DECIMAL)));
		assertEquals(Integer.MAX_VALUE, assertDoesNotThrow(() -> (int) NumberType.INTEGER.parseNumber("0x7FFFFFFF", Radix.HEXADECIMAL)));
		assertEquals(10, (int) NumberType.INTEGER.parseNumber("10", Radix.DECIMAL));
		assertEquals(Integer.MAX_VALUE, (int) NumberType.INTEGER.parseNumber("2147483647", Radix.DECIMAL));
		assertEquals(Integer.MIN_VALUE, (int) NumberType.INTEGER.parseNumber("-2147483648", Radix.DECIMAL));
		assertEquals(Integer.MAX_VALUE, (int) NumberType.INTEGER.parseNumber("7FFFFFFF", Radix.HEXADECIMAL));
		assertEquals(Integer.MIN_VALUE, (int) NumberType.INTEGER.parseNumber("-80000000", Radix.HEXADECIMAL));
		
		assertEquals(0L, assertDoesNotThrow(() -> (long) NumberType.LONG.parseNumber(null, Radix.DECIMAL)));
		assertEquals(0L, assertDoesNotThrow(() -> (long) NumberType.LONG.parseNumber("0", Radix.DECIMAL)));
		assertThrows(IllegalArgumentException.class, () -> NumberType.LONG.parseNumber("F", null));
		assertEquals(15L, (long) NumberType.LONG.parseNumber("0xF", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.LONG.parseNumber("10b", Radix.DECIMAL));
		assertEquals(10L, assertDoesNotThrow(() -> (long) NumberType.LONG.parseNumber("10l", Radix.DECIMAL)));
		assertEquals(Long.MAX_VALUE, assertDoesNotThrow(() -> (long) NumberType.LONG.parseNumber("0x7FFFFFFFFFFFFFFF", Radix.HEXADECIMAL)));
		assertEquals(10L, (long) NumberType.LONG.parseNumber("10", Radix.DECIMAL));
		assertEquals(Long.MAX_VALUE, (long) NumberType.LONG.parseNumber("9223372036854775807", Radix.DECIMAL));
		assertEquals(Long.MIN_VALUE, (long) NumberType.LONG.parseNumber("-9223372036854775808", Radix.DECIMAL));
		assertEquals(Long.MAX_VALUE, (long) NumberType.LONG.parseNumber("7FFFFFFFFFFFFFFF", Radix.HEXADECIMAL));
		assertEquals(Long.MIN_VALUE, (long) NumberType.LONG.parseNumber("-8000000000000000", Radix.HEXADECIMAL));
		
		assertEquals(BigInteger.ZERO, assertDoesNotThrow(() -> NumberType.BIG_INTEGER.parseNumber(null, Radix.DECIMAL)));
		assertEquals(BigInteger.ZERO, assertDoesNotThrow(() -> NumberType.BIG_INTEGER.parseNumber("0", Radix.DECIMAL)));
		assertThrows(IllegalArgumentException.class, () -> NumberType.BIG_INTEGER.parseNumber("F", null));
		assertEquals(BigInteger.valueOf(15), NumberType.BIG_INTEGER.parseNumber("0xF", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.BIG_INTEGER.parseNumber("10l", Radix.DECIMAL));
		assertEquals(BigInteger.valueOf(Long.MAX_VALUE), assertDoesNotThrow(() -> NumberType.BIG_INTEGER.parseNumber("0x7FFFFFFFFFFFFFFF", Radix.HEXADECIMAL)));
		assertEquals(BigInteger.TEN, NumberType.BIG_INTEGER.parseNumber("10", Radix.DECIMAL));
		assertEquals(BigInteger.valueOf(Long.MAX_VALUE), NumberType.BIG_INTEGER.parseNumber("9223372036854775807", Radix.DECIMAL));
		assertEquals(BigInteger.valueOf(Long.MIN_VALUE), NumberType.BIG_INTEGER.parseNumber("-9223372036854775808", Radix.DECIMAL));
		assertEquals(BigInteger.valueOf(Long.MAX_VALUE), NumberType.BIG_INTEGER.parseNumber("7FFFFFFFFFFFFFFF", Radix.HEXADECIMAL));
		assertEquals(BigInteger.valueOf(Long.MIN_VALUE), NumberType.BIG_INTEGER.parseNumber("-8000000000000000", Radix.HEXADECIMAL));
		
		assertEquals(0.0f, assertDoesNotThrow(() -> (float) NumberType.FLOAT.parseNumber(null, Radix.DECIMAL)));
		assertEquals(0.0f, assertDoesNotThrow(() -> (float) NumberType.FLOAT.parseNumber("0", Radix.DECIMAL)));
		assertThrows(IllegalArgumentException.class, () -> NumberType.FLOAT.parseNumber("F", null));
		assertEquals(15.0f, (float) NumberType.FLOAT.parseNumber("0xFp0", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.FLOAT.parseNumber("10d", Radix.DECIMAL));
		assertEquals(10.0f, assertDoesNotThrow(() -> (float) NumberType.FLOAT.parseNumber("10f", Radix.DECIMAL)));
		assertEquals(Float.MAX_VALUE, assertDoesNotThrow(() -> (float) NumberType.FLOAT.parseNumber(Float.toHexString(Float.MAX_VALUE), Radix.HEXADECIMAL)));
		assertEquals(10.0F, (float) NumberType.FLOAT.parseNumber("10", Radix.DECIMAL));
		assertEquals(Float.MAX_VALUE, (float) NumberType.FLOAT.parseNumber(Float.toString(Float.MAX_VALUE), Radix.DECIMAL));
		assertEquals(Float.MIN_VALUE, (float) NumberType.FLOAT.parseNumber(Float.toString(Float.MIN_VALUE), Radix.DECIMAL));
		assertEquals(Float.MAX_VALUE, (float) NumberType.FLOAT.parseNumber(Float.toHexString(Float.MAX_VALUE), Radix.HEXADECIMAL));
		assertEquals(Float.MIN_VALUE, (float) NumberType.FLOAT.parseNumber(Float.toHexString(Float.MIN_VALUE), Radix.HEXADECIMAL));
		
		assertEquals(0.0d, assertDoesNotThrow(() -> (double) NumberType.DOUBLE.parseNumber(null, Radix.DECIMAL)));
		assertEquals(0.0d, assertDoesNotThrow(() -> (double) NumberType.DOUBLE.parseNumber("0", Radix.DECIMAL)));
		assertThrows(IllegalArgumentException.class, () -> NumberType.DOUBLE.parseNumber("F", null));
		assertEquals(15.0d, (double) NumberType.DOUBLE.parseNumber("0xFp0", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.DOUBLE.parseNumber("10f", Radix.DECIMAL));
		assertEquals(10.0d, assertDoesNotThrow(() -> (double) NumberType.DOUBLE.parseNumber("10d", Radix.DECIMAL)));
		assertEquals(Double.MAX_VALUE, assertDoesNotThrow(() -> (double) NumberType.DOUBLE.parseNumber(Double.toHexString(Double.MAX_VALUE), Radix.HEXADECIMAL)));
		assertEquals(10.0D, (double) NumberType.DOUBLE.parseNumber("10", Radix.DECIMAL));
		assertEquals(Double.MAX_VALUE, (double) NumberType.DOUBLE.parseNumber(Double.toString(Double.MAX_VALUE), Radix.DECIMAL));
		assertEquals(Double.MIN_VALUE, (double) NumberType.DOUBLE.parseNumber(Double.toString(Double.MIN_VALUE), Radix.DECIMAL));
		assertEquals(Double.MAX_VALUE, (double) NumberType.DOUBLE.parseNumber(Double.toHexString(Double.MAX_VALUE), Radix.HEXADECIMAL));
		assertEquals(Double.MIN_VALUE, (double) NumberType.DOUBLE.parseNumber(Double.toHexString(Double.MIN_VALUE), Radix.HEXADECIMAL));
		
		
		assertEquals(BigDecimal.ZERO, assertDoesNotThrow(() -> NumberType.BIG_DECIMAL.parseNumber(null, Radix.DECIMAL)));
		assertEquals(BigDecimal.ZERO, assertDoesNotThrow(() -> NumberType.BIG_DECIMAL.parseNumber("0", Radix.DECIMAL)));
		assertThrows(IllegalArgumentException.class, () -> NumberType.BIG_DECIMAL.parseNumber("F", null));
		assertEquals(BigDecimal.valueOf(15), NumberType.BIG_DECIMAL.parseNumber("0xFp0", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.BIG_DECIMAL.parseNumber("10d", Radix.DECIMAL));
		assertEquals(Mth.parseHexToBigDecimal(Double.toHexString(Double.MAX_VALUE)), assertDoesNotThrow(() -> NumberType.BIG_DECIMAL.parseNumber(Double.toHexString(Double.MAX_VALUE), Radix.HEXADECIMAL)));
		assertEquals(BigDecimal.TEN, NumberType.BIG_DECIMAL.parseNumber("10", Radix.DECIMAL));
		assertEquals(BigDecimal.valueOf(Double.MAX_VALUE), NumberType.BIG_DECIMAL.parseNumber(Double.toString(Double.MAX_VALUE), Radix.DECIMAL));
		assertEquals(BigDecimal.valueOf(Double.MIN_VALUE), NumberType.BIG_DECIMAL.parseNumber(Double.toString(Double.MIN_VALUE), Radix.DECIMAL));
		assertEquals(Mth.parseHexToBigDecimal(Double.toHexString(Double.MAX_VALUE)), NumberType.BIG_DECIMAL.parseNumber(Double.toHexString(Double.MAX_VALUE), Radix.HEXADECIMAL));
		assertEquals(Mth.parseHexToBigDecimal(Double.toHexString(Double.MIN_VALUE)), NumberType.BIG_DECIMAL.parseNumber(Double.toHexString(Double.MIN_VALUE), Radix.HEXADECIMAL));
	}
	
	@Test
	void parseNumberStrict() {
		assertThrows(NullPointerException.class, () -> NumberType.BYTE.parseNumberStrict(null, Radix.DECIMAL));
		assertThrows(NullPointerException.class, () -> NumberType.BYTE.parseNumberStrict("", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.BYTE.parseNumberStrict("", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.BYTE.parseNumberStrict("10b", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.BYTE.parseNumberStrict("0x7F", Radix.HEXADECIMAL));
		assertEquals((byte) 10, (byte) NumberType.BYTE.parseNumberStrict("10", Radix.DECIMAL));
		assertEquals(Byte.MAX_VALUE, (byte) NumberType.BYTE.parseNumberStrict("127", Radix.DECIMAL));
		assertEquals(Byte.MIN_VALUE, (byte) NumberType.BYTE.parseNumberStrict("-128", Radix.DECIMAL));
		assertEquals(Byte.MAX_VALUE, (byte) NumberType.BYTE.parseNumberStrict("7F", Radix.HEXADECIMAL));
		assertEquals(Byte.MIN_VALUE, (byte) NumberType.BYTE.parseNumberStrict("-80", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.SHORT.parseNumberStrict(null, Radix.DECIMAL));
		assertThrows(NullPointerException.class, () -> NumberType.SHORT.parseNumberStrict("", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.SHORT.parseNumberStrict("", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.SHORT.parseNumberStrict("10s", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.SHORT.parseNumberStrict("0x7FFF", Radix.HEXADECIMAL));
		assertEquals((short) 10, (short) NumberType.SHORT.parseNumberStrict("10", Radix.DECIMAL));
		assertEquals(Short.MAX_VALUE, (short) NumberType.SHORT.parseNumberStrict("32767", Radix.DECIMAL));
		assertEquals(Short.MIN_VALUE, (short) NumberType.SHORT.parseNumberStrict("-32768", Radix.DECIMAL));
		assertEquals(Short.MAX_VALUE, (short) NumberType.SHORT.parseNumberStrict("7FFF", Radix.HEXADECIMAL));
		assertEquals(Short.MIN_VALUE, (short) NumberType.SHORT.parseNumberStrict("-8000", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.INTEGER.parseNumberStrict(null, Radix.DECIMAL));
		assertThrows(NullPointerException.class, () -> NumberType.INTEGER.parseNumberStrict("", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.INTEGER.parseNumberStrict("", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.INTEGER.parseNumberStrict("10i", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.INTEGER.parseNumberStrict("0x7FFFFFFF", Radix.HEXADECIMAL));
		assertEquals(10, (int) NumberType.INTEGER.parseNumberStrict("10", Radix.DECIMAL));
		assertEquals(Integer.MAX_VALUE, (int) NumberType.INTEGER.parseNumberStrict("2147483647", Radix.DECIMAL));
		assertEquals(Integer.MIN_VALUE, (int) NumberType.INTEGER.parseNumberStrict("-2147483648", Radix.DECIMAL));
		assertEquals(Integer.MAX_VALUE, (int) NumberType.INTEGER.parseNumberStrict("7FFFFFFF", Radix.HEXADECIMAL));
		assertEquals(Integer.MIN_VALUE, (int) NumberType.INTEGER.parseNumberStrict("-80000000", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.LONG.parseNumberStrict(null, Radix.DECIMAL));
		assertThrows(NullPointerException.class, () -> NumberType.LONG.parseNumberStrict("", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.LONG.parseNumberStrict("", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.LONG.parseNumberStrict("10l", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.LONG.parseNumberStrict("0x7FFFFFFFFFFFFFFF", Radix.HEXADECIMAL));
		assertEquals(10L, (long) NumberType.LONG.parseNumberStrict("10", Radix.DECIMAL));
		assertEquals(Long.MAX_VALUE, (long) NumberType.LONG.parseNumberStrict("9223372036854775807", Radix.DECIMAL));
		assertEquals(Long.MIN_VALUE, (long) NumberType.LONG.parseNumberStrict("-9223372036854775808", Radix.DECIMAL));
		assertEquals(Long.MAX_VALUE, (long) NumberType.LONG.parseNumberStrict("7FFFFFFFFFFFFFFF", Radix.HEXADECIMAL));
		assertEquals(Long.MIN_VALUE, (long) NumberType.LONG.parseNumberStrict("-8000000000000000", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.BIG_INTEGER.parseNumberStrict(null, Radix.DECIMAL));
		assertThrows(NullPointerException.class, () -> NumberType.BIG_INTEGER.parseNumberStrict("", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.BIG_INTEGER.parseNumberStrict("", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.BIG_INTEGER.parseNumberStrict("10l", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.BIG_INTEGER.parseNumberStrict("0x7FFFFFFFFFFFFFFF", Radix.HEXADECIMAL));
		assertEquals(BigInteger.TEN, NumberType.BIG_INTEGER.parseNumberStrict("10", Radix.DECIMAL));
		assertEquals(BigInteger.valueOf(Long.MAX_VALUE), NumberType.BIG_INTEGER.parseNumberStrict("9223372036854775807", Radix.DECIMAL));
		assertEquals(BigInteger.valueOf(Long.MIN_VALUE), NumberType.BIG_INTEGER.parseNumberStrict("-9223372036854775808", Radix.DECIMAL));
		assertEquals(BigInteger.valueOf(Long.MAX_VALUE), NumberType.BIG_INTEGER.parseNumberStrict("7FFFFFFFFFFFFFFF", Radix.HEXADECIMAL));
		assertEquals(BigInteger.valueOf(Long.MIN_VALUE), NumberType.BIG_INTEGER.parseNumberStrict("-8000000000000000", Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.FLOAT.parseNumberStrict(null, Radix.DECIMAL));
		assertThrows(NullPointerException.class, () -> NumberType.FLOAT.parseNumberStrict("", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.FLOAT.parseNumberStrict("", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.FLOAT.parseNumberStrict("0.000002e-125", Radix.HEXADECIMAL));
		assertEquals(10.0F, (float) NumberType.FLOAT.parseNumberStrict("10", Radix.DECIMAL));
		assertEquals(Float.MAX_VALUE, (float) NumberType.FLOAT.parseNumberStrict(Float.toString(Float.MAX_VALUE), Radix.DECIMAL));
		assertEquals(Float.MIN_VALUE, (float) NumberType.FLOAT.parseNumberStrict(Float.toString(Float.MIN_VALUE), Radix.DECIMAL));
		assertEquals(Float.MAX_VALUE, (float) NumberType.FLOAT.parseNumberStrict(Float.toHexString(Float.MAX_VALUE), Radix.HEXADECIMAL));
		assertEquals(Float.MIN_VALUE, (float) NumberType.FLOAT.parseNumberStrict(Float.toHexString(Float.MIN_VALUE), Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.DOUBLE.parseNumberStrict(null, Radix.DECIMAL));
		assertThrows(NullPointerException.class, () -> NumberType.DOUBLE.parseNumberStrict("", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.DOUBLE.parseNumberStrict("", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.DOUBLE.parseNumberStrict("0.0000000000001e-1022", Radix.HEXADECIMAL));
		assertEquals(10.0D, (double) NumberType.DOUBLE.parseNumberStrict("10", Radix.DECIMAL));
		assertEquals(Double.MAX_VALUE, (double) NumberType.DOUBLE.parseNumberStrict(Double.toString(Double.MAX_VALUE), Radix.DECIMAL));
		assertEquals(Double.MIN_VALUE, (double) NumberType.DOUBLE.parseNumberStrict(Double.toString(Double.MIN_VALUE), Radix.DECIMAL));
		assertEquals(Double.MAX_VALUE, (double) NumberType.DOUBLE.parseNumberStrict(Double.toHexString(Double.MAX_VALUE), Radix.HEXADECIMAL));
		assertEquals(Double.MIN_VALUE, (double) NumberType.DOUBLE.parseNumberStrict(Double.toHexString(Double.MIN_VALUE), Radix.HEXADECIMAL));
		
		assertThrows(NullPointerException.class, () -> NumberType.BIG_DECIMAL.parseNumberStrict(null, Radix.DECIMAL));
		assertThrows(NullPointerException.class, () -> NumberType.BIG_DECIMAL.parseNumberStrict("", null));
		assertThrows(IllegalArgumentException.class, () -> NumberType.BIG_DECIMAL.parseNumberStrict("", Radix.DECIMAL));
		assertThrows(NumberFormatException.class, () -> NumberType.BIG_DECIMAL.parseNumberStrict("1.Fe+999999999", Radix.HEXADECIMAL));
		assertEquals(BigDecimal.TEN, NumberType.BIG_DECIMAL.parseNumberStrict("10", Radix.DECIMAL));
		assertEquals(BigDecimal.valueOf(Double.MAX_VALUE), NumberType.BIG_DECIMAL.parseNumberStrict(Double.toString(Double.MAX_VALUE), Radix.DECIMAL));
		assertEquals(BigDecimal.valueOf(Double.MIN_VALUE), NumberType.BIG_DECIMAL.parseNumberStrict(Double.toString(Double.MIN_VALUE), Radix.DECIMAL));
		assertEquals(Mth.parseHexToBigDecimal(Double.toHexString(Double.MAX_VALUE)), NumberType.BIG_DECIMAL.parseNumberStrict(Double.toHexString(Double.MAX_VALUE), Radix.HEXADECIMAL));
		assertEquals(Mth.parseHexToBigDecimal(Double.toHexString(Double.MIN_VALUE)), NumberType.BIG_DECIMAL.parseNumberStrict(Double.toHexString(Double.MIN_VALUE), Radix.HEXADECIMAL));
	}
}
