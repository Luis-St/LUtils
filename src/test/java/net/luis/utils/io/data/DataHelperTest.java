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

package net.luis.utils.io.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DataHelper}.<br>
 *
 * @author Luis-St
 */
class DataHelperTest {
	
	@Test
	void tryParseWithNull() {
		assertThrows(NullPointerException.class, () -> DataHelper.tryParse(null));
	}
	
	@Test
	void tryParseWithBooleans() {
		assertEquals(Boolean.TRUE, DataHelper.tryParse("true"));
		assertEquals(Boolean.FALSE, DataHelper.tryParse("false"));
		
		assertEquals(Boolean.TRUE, DataHelper.tryParse("TRUE"));
		assertEquals(Boolean.FALSE, DataHelper.tryParse("FALSE"));
		
		assertEquals(Boolean.TRUE, DataHelper.tryParse("True"));
		assertEquals(Boolean.FALSE, DataHelper.tryParse("False"));
		
		assertEquals(Boolean.TRUE, DataHelper.tryParse("tRuE"));
		assertEquals(Boolean.FALSE, DataHelper.tryParse("fAlSe"));
	}
	
	@Test
	void tryParseWithNumbers() {
		assertInstanceOf(Number.class, DataHelper.tryParse("123"));
		assertInstanceOf(Number.class, DataHelper.tryParse("-456"));
		assertInstanceOf(Number.class, DataHelper.tryParse("0"));
		assertInstanceOf(Number.class, DataHelper.tryParse("1.5"));
		assertInstanceOf(Number.class, DataHelper.tryParse("-3.14"));
		assertInstanceOf(Number.class, DataHelper.tryParse("1.0e10"));
	}
	
	@Test
	void tryParseWithUnparsableStrings() {
		assertEquals("abc", DataHelper.tryParse("abc"));
		assertEquals("hello world", DataHelper.tryParse("hello world"));
		assertEquals("", DataHelper.tryParse(""));
		assertEquals(" ", DataHelper.tryParse(" "));
		assertEquals("123abc", DataHelper.tryParse("123abc"));
		assertEquals("truee", DataHelper.tryParse("truee"));
		assertEquals("falsee", DataHelper.tryParse("falsee"));
		assertEquals(" true", DataHelper.tryParse(" true"));
		assertEquals("false ", DataHelper.tryParse("false "));
	}
	
	@Test
	void tryParseNumberWithNull() {
		assertThrows(NullPointerException.class, () -> DataHelper.tryParseNumber(null));
	}
	
	@Test
	void tryParseNumberWithIntegers() {
		Object result = DataHelper.tryParseNumber("123");
		assertInstanceOf(Number.class, result);
		assertEquals(123, ((Number) result).intValue());
		
		result = DataHelper.tryParseNumber("-456");
		assertInstanceOf(Number.class, result);
		assertEquals(-456, ((Number) result).intValue());
		
		result = DataHelper.tryParseNumber("0");
		assertInstanceOf(Number.class, result);
		assertEquals(0, ((Number) result).intValue());
		
		result = DataHelper.tryParseNumber("+789");
		assertInstanceOf(Number.class, result);
		assertEquals(789, ((Number) result).intValue());
	}
	
	@Test
	void tryParseNumberWithDecimals() {
		Object result = DataHelper.tryParseNumber("1.5");
		assertInstanceOf(Number.class, result);
		assertEquals(1.5, ((Number) result).doubleValue(), 0.0001);
		
		result = DataHelper.tryParseNumber("-3.14");
		assertInstanceOf(Number.class, result);
		assertEquals(-3.14, ((Number) result).doubleValue(), 0.0001);
		
		result = DataHelper.tryParseNumber("0.0");
		assertInstanceOf(Number.class, result);
		assertEquals(0.0, ((Number) result).doubleValue(), 0.0001);
		
		result = DataHelper.tryParseNumber(".5");
		assertInstanceOf(Number.class, result);
		assertEquals(0.5, ((Number) result).doubleValue(), 0.0001);
	}
	
	@Test
	void tryParseNumberWithScientificNotation() {
		Object result = DataHelper.tryParseNumber("1.0e10");
		assertInstanceOf(Number.class, result);
		assertEquals(1e10, ((Number) result).doubleValue(), 1e5);
		
		result = DataHelper.tryParseNumber("1.0E10");
		assertInstanceOf(Number.class, result);
		assertEquals(1e10, ((Number) result).doubleValue(), 1e5);
		
		result = DataHelper.tryParseNumber("1.5E-3");
		assertInstanceOf(Number.class, result);
		assertEquals(0.0015, ((Number) result).doubleValue(), 0.00001);
		
		result = DataHelper.tryParseNumber("2.5e+4");
		assertInstanceOf(Number.class, result);
		assertEquals(25000.0, ((Number) result).doubleValue(), 0.1);
	}
	
	@Test
	void tryParseNumberWithTrailingCharacters() {
		assertEquals("123abc", DataHelper.tryParseNumber("123abc"));
		assertEquals("456def", DataHelper.tryParseNumber("456def"));
		assertEquals("1.5xyz", DataHelper.tryParseNumber("1.5xyz"));
		assertEquals("123#", DataHelper.tryParseNumber("123#"));
	}
	
	@Test
	void tryParseNumberWithUnparsableStrings() {
		assertEquals("abc", DataHelper.tryParseNumber("abc"));
		assertEquals("", DataHelper.tryParseNumber(""));
		assertEquals("hello world", DataHelper.tryParseNumber("hello world"));
		assertEquals("true", DataHelper.tryParseNumber("true"));
		assertEquals("false", DataHelper.tryParseNumber("false"));
		assertEquals("NaN", DataHelper.tryParseNumber("NaN"));
		assertEquals("Infinity", DataHelper.tryParseNumber("Infinity"));
	}
	
	@Test
	void tryParseNumberWithWhitespace() {
		Object result = DataHelper.tryParseNumber("123   ");
		assertInstanceOf(Number.class, result);
		assertEquals(123, ((Number) result).intValue());
		
		result = DataHelper.tryParseNumber("456\t");
		assertInstanceOf(Number.class, result);
		assertEquals(456, ((Number) result).intValue());
		
		result = DataHelper.tryParseNumber("789\n");
		assertInstanceOf(Number.class, result);
		assertEquals(789, ((Number) result).intValue());
		
		assertEquals(" 123", DataHelper.tryParseNumber(" 123"));
	}
}
