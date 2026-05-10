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

import net.luis.utils.io.data.toon.exception.ToonTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonValue}.<br>
 *
 * @author Luis-St
 */
class ToonValueTest {
	
	@Test
	void constructorWithBoolean() {
		ToonValue trueValue = new ToonValue(true);
		ToonValue falseValue = new ToonValue(false);
		
		assertTrue(trueValue.isToonBoolean());
		assertTrue(falseValue.isToonBoolean());
		assertTrue(trueValue.getAsBoolean());
		assertFalse(falseValue.getAsBoolean());
	}
	
	@Test
	void constructorWithNumber() {
		ToonValue intValue = new ToonValue(42);
		ToonValue doubleValue = new ToonValue(3.14);
		
		assertTrue(intValue.isToonNumber());
		assertTrue(doubleValue.isToonNumber());
		assertFalse(intValue.isToonBoolean());
		assertFalse(intValue.isToonString());
	}
	
	@Test
	void constructorWithCharacter() {
		ToonValue charValue = new ToonValue('a');
		
		assertTrue(charValue.isToonString());
		assertEquals("a", charValue.getAsString());
	}
	
	@Test
	void constructorWithString() {
		ToonValue stringValue = new ToonValue("hello");
		
		assertTrue(stringValue.isToonString());
		assertEquals("hello", stringValue.getAsString());
	}
	
	@Test
	void constructorWithNullNumber() {
		assertThrows(NullPointerException.class, () -> new ToonValue((Number) null));
	}
	
	@Test
	void constructorWithNullString() {
		assertThrows(NullPointerException.class, () -> new ToonValue((String) null));
	}
	
	@Test
	void getAsBooleanValid() {
		assertTrue(new ToonValue(true).getAsBoolean());
		assertFalse(new ToonValue(false).getAsBoolean());
	}
	
	@Test
	void getAsBooleanInvalid() {
		assertThrows(ToonTypeException.class, () -> new ToonValue(42).getAsBoolean());
		assertThrows(ToonTypeException.class, () -> new ToonValue("true").getAsBoolean());
	}
	
	@Test
	void getAsNumberValid() {
		assertEquals(42, new ToonValue(42).getAsNumber());
		assertEquals(3.14, new ToonValue(3.14).getAsNumber());
	}
	
	@Test
	void getAsNumberInvalid() {
		assertThrows(ToonTypeException.class, () -> new ToonValue("hello").getAsNumber());
		assertThrows(ToonTypeException.class, () -> new ToonValue(true).getAsNumber());
	}
	
	@Test
	void getAsByteValid() {
		assertEquals((byte) 42, new ToonValue(42).getAsByte());
		assertEquals((byte) 1, new ToonValue((byte) 1).getAsByte());
	}
	
	@Test
	void getAsShortValid() {
		assertEquals((short) 42, new ToonValue(42).getAsShort());
		assertEquals((short) 100, new ToonValue((short) 100).getAsShort());
	}
	
	@Test
	void getAsIntegerValid() {
		assertEquals(42, new ToonValue(42).getAsInteger());
		assertEquals(100, new ToonValue(100L).getAsInteger());
	}
	
	@Test
	void getAsLongValid() {
		assertEquals(42L, new ToonValue(42).getAsLong());
		assertEquals(100L, new ToonValue(100L).getAsLong());
	}
	
	@Test
	void getAsFloatValid() {
		assertEquals(3.14f, new ToonValue(3.14f).getAsFloat());
		assertEquals(42.0f, new ToonValue(42).getAsFloat());
	}
	
	@Test
	void getAsDoubleValid() {
		assertEquals(3.14, new ToonValue(3.14).getAsDouble());
		assertEquals(42.0, new ToonValue(42).getAsDouble());
	}
	
	@Test
	void getAsStringFromAll() {
		assertEquals("hello", new ToonValue("hello").getAsString());
		assertEquals("42", new ToonValue(42).getAsString());
		assertEquals("true", new ToonValue(true).getAsString());
		assertEquals("false", new ToonValue(false).getAsString());
	}
	
	@Test
	void toStringWithConfig() {
		assertEquals("true", new ToonValue(true).toString(ToonConfig.DEFAULT));
		assertEquals("false", new ToonValue(false).toString(ToonConfig.DEFAULT));
		
		assertEquals("42", new ToonValue(42).toString(ToonConfig.DEFAULT));
		assertEquals("3.14", new ToonValue(3.14).toString(ToonConfig.DEFAULT));
		
		assertEquals("hello", new ToonValue("hello").toString(ToonConfig.DEFAULT));
		assertEquals("\"\"", new ToonValue("").toString(ToonConfig.DEFAULT));
		assertEquals("\"true\"", new ToonValue("true").toString(ToonConfig.DEFAULT));
		
		assertThrows(NullPointerException.class, () -> new ToonValue(true).toString(null));
	}
	
	@Test
	void equalsAndHashCode() {
		ToonValue value1 = new ToonValue("test");
		ToonValue value2 = new ToonValue("test");
		ToonValue value3 = new ToonValue("different");
		ToonValue value4 = new ToonValue(42);
		ToonValue value5 = new ToonValue(42);
		
		assertEquals(value1, value2);
		assertEquals(value1.hashCode(), value2.hashCode());
		assertNotEquals(value1, value3);
		
		assertEquals(value4, value5);
		assertEquals(value4.hashCode(), value5.hashCode());
		
		assertNotEquals(value1, value4);
		assertNotEquals(value1, null);
		assertNotEquals(value1, "not a value");
		
		assertEquals(value1, value1);
	}
}
