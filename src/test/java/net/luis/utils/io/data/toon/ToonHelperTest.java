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

package net.luis.utils.io.data.toon;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonHelper}.<br>
 *
 * @author Luis-St
 */
class ToonHelperTest {
	
	@Test
	void formatKeyBare() {
		assertEquals("hello", ToonHelper.formatKey("hello"));
		assertEquals("my_key", ToonHelper.formatKey("my_key"));
		assertEquals("Key", ToonHelper.formatKey("Key"));
		assertEquals("_private", ToonHelper.formatKey("_private"));
		assertEquals("key.name", ToonHelper.formatKey("key.name"));
	}
	
	@Test
	void formatKeyQuoted() {
		assertEquals("\"\"", ToonHelper.formatKey(""));
		assertEquals("\"has space\"", ToonHelper.formatKey("has space"));
		assertEquals("\"key:val\"", ToonHelper.formatKey("key:val"));
		assertEquals("\"key@name\"", ToonHelper.formatKey("key@name"));
	}
	
	@Test
	void formatKeyWithNull() {
		assertThrows(NullPointerException.class, () -> ToonHelper.formatKey(null));
	}
	
	@Test
	void escapeStringSpecialChars() {
		assertEquals("\\\\", ToonHelper.escapeString("\\"));
		assertEquals("\\\"", ToonHelper.escapeString("\""));
		assertEquals("\\n", ToonHelper.escapeString("\n"));
		assertEquals("\\r", ToonHelper.escapeString("\r"));
		assertEquals("\\t", ToonHelper.escapeString("\t"));
		assertEquals("hello\\nworld", ToonHelper.escapeString("hello\nworld"));
		assertEquals("path\\\\to\\\\file", ToonHelper.escapeString("path\\to\\file"));
	}
	
	@Test
	void escapeStringWithNull() {
		assertThrows(NullPointerException.class, () -> ToonHelper.escapeString(null));
	}
	
	@Test
	void needsQuotingEmpty() {
		assertTrue(ToonHelper.needsQuoting("", ','));
	}
	
	@Test
	void needsQuotingReservedWords() {
		assertTrue(ToonHelper.needsQuoting("true", ','));
		assertTrue(ToonHelper.needsQuoting("false", ','));
		assertTrue(ToonHelper.needsQuoting("null", ','));
	}
	
	@Test
	void needsQuotingNumeric() {
		assertTrue(ToonHelper.needsQuoting("42", ','));
		assertTrue(ToonHelper.needsQuoting("3.14", ','));
	}
	
	@Test
	void needsQuotingSpecialChars() {
		assertTrue(ToonHelper.needsQuoting("a,b", ','));
		assertTrue(ToonHelper.needsQuoting("a|b", '|'));
		assertTrue(ToonHelper.needsQuoting("a:b", ','));
		assertTrue(ToonHelper.needsQuoting("a\"b", ','));
		assertTrue(ToonHelper.needsQuoting("a[b", ','));
		assertTrue(ToonHelper.needsQuoting("a{b", ','));
	}
	
	@Test
	void needsQuotingNormal() {
		assertFalse(ToonHelper.needsQuoting("hello", ','));
		assertFalse(ToonHelper.needsQuoting("world", ','));
	}
	
	@Test
	void needsQuotingWithNull() {
		assertThrows(NullPointerException.class, () -> ToonHelper.needsQuoting(null, ','));
	}
	
	@Test
	void formatNumberCanonical() {
		assertEquals("42", ToonHelper.formatNumber(42));
		assertEquals("100", ToonHelper.formatNumber(100L));
		
		assertEquals("3.14", ToonHelper.formatNumber(3.14));
		assertEquals("1.5", ToonHelper.formatNumber(1.5));
		
		assertEquals("null", ToonHelper.formatNumber(Double.NaN));
		assertEquals("null", ToonHelper.formatNumber(Double.POSITIVE_INFINITY));
		assertEquals("null", ToonHelper.formatNumber(Double.NEGATIVE_INFINITY));
		assertEquals("null", ToonHelper.formatNumber(Float.NaN));
		assertEquals("null", ToonHelper.formatNumber(Float.POSITIVE_INFINITY));
		
		assertEquals("0.0", ToonHelper.formatNumber(-0.0));
	}
	
	@Test
	void formatNumberWithNull() {
		assertThrows(NullPointerException.class, () -> ToonHelper.formatNumber(null));
	}
	
	@Test
	void inferTypeBoolean() {
		ToonElement trueElement = ToonHelper.inferType("true");
		ToonElement falseElement = ToonHelper.inferType("false");
		
		assertTrue(trueElement.isToonBoolean());
		assertEquals(new ToonValue(true), trueElement);
		assertTrue(falseElement.isToonBoolean());
		assertEquals(new ToonValue(false), falseElement);
	}
	
	@Test
	void inferTypeNull() {
		ToonElement nullElement = ToonHelper.inferType("null");
		assertSame(ToonNull.INSTANCE, nullElement);
	}
	
	@Test
	void inferTypeNumber() {
		ToonElement intElement = ToonHelper.inferType("42");
		assertTrue(intElement.isToonNumber());
		assertEquals(42L, intElement.getAsNumber());
		
		ToonElement doubleElement = ToonHelper.inferType("3.14");
		assertTrue(doubleElement.isToonNumber());
		assertEquals(3.14, doubleElement.getAsNumber());
	}
	
	@Test
	void inferTypeString() {
		ToonElement stringElement = ToonHelper.inferType("hello");
		assertTrue(stringElement.isToonString());
		assertEquals(new ToonValue("hello"), stringElement);
	}
	
	@Test
	void inferTypeWithNull() {
		assertThrows(NullPointerException.class, () -> ToonHelper.inferType(null));
	}
	
	@Test
	void canFoldKeyValid() {
		assertTrue(ToonHelper.canFoldKey("hello"));
		assertTrue(ToonHelper.canFoldKey("my_key"));
		assertTrue(ToonHelper.canFoldKey("Test123"));
		assertTrue(ToonHelper.canFoldKey("_private"));
	}
	
	@Test
	void canFoldKeyInvalid() {
		assertFalse(ToonHelper.canFoldKey(""));
		assertFalse(ToonHelper.canFoldKey("123abc"));
		assertFalse(ToonHelper.canFoldKey("has.dot"));
		assertFalse(ToonHelper.canFoldKey("has-dash"));
	}
	
	@Test
	void canFoldKeyWithNull() {
		assertThrows(NullPointerException.class, () -> ToonHelper.canFoldKey(null));
	}
}
