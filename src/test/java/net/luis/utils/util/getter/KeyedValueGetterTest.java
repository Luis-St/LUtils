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

package net.luis.utils.util.getter;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KeyedValueGetter}.<br>
 *
 * @author Luis-St
 */
class KeyedValueGetterTest {
	
	@Test
	void getAsStringReturnsValue() {
		KeyedValueGetter getter = createGetter("key", "value");
		assertEquals("value", getter.getAsString("key"));
	}
	
	@Test
	void getAsBooleanTrue() {
		KeyedValueGetter getter = createGetter("bool", "true");
		assertTrue(getter.getAsBoolean("bool"));
	}
	
	@Test
	void getAsBooleanFalse() {
		KeyedValueGetter getter = createGetter("bool", "false");
		assertFalse(getter.getAsBoolean("bool"));
	}
	
	@Test
	void getAsBooleanCaseInsensitive() {
		KeyedValueGetter upper = createGetter("bool", "TRUE");
		KeyedValueGetter lower = createGetter("bool", "false");
		KeyedValueGetter mixed = createGetter("bool", "TrUe");
		
		assertTrue(upper.getAsBoolean("bool"));
		assertFalse(lower.getAsBoolean("bool"));
		assertTrue(mixed.getAsBoolean("bool"));
	}
	
	@Test
	void getAsBooleanInvalidThrows() {
		KeyedValueGetter getter = createGetter("bool", "notABoolean");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsBoolean("bool"));
	}
	
	@Test
	void getAsNumberInteger() {
		KeyedValueGetter getter = createGetter("num", "42");
		Number result = getter.getAsNumber("num");
		assertEquals(42, result.intValue());
	}
	
	@Test
	void getAsNumberDecimal() {
		KeyedValueGetter getter = createGetter("num", "3.14");
		Number result = getter.getAsNumber("num");
		assertEquals(3.14, result.doubleValue(), 0.001);
	}
	
	@Test
	void getAsNumberNegative() {
		KeyedValueGetter getter = createGetter("num", "-100");
		Number result = getter.getAsNumber("num");
		assertEquals(-100, result.intValue());
	}
	
	@Test
	void getAsNumberInvalidThrows() {
		KeyedValueGetter getter = createGetter("num", "notANumber");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsNumber("num"));
	}
	
	@Test
	void getAsByteValid() {
		KeyedValueGetter getter = createGetter("byte", "127");
		assertEquals((byte) 127, getter.getAsByte("byte"));
	}
	
	@Test
	void getAsByteNegative() {
		KeyedValueGetter getter = createGetter("byte", "-128");
		assertEquals((byte) -128, getter.getAsByte("byte"));
	}
	
	@Test
	void getAsByteInvalidThrows() {
		KeyedValueGetter getter = createGetter("byte", "notAByte");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsByte("byte"));
	}
	
	@Test
	void getAsShortValid() {
		KeyedValueGetter getter = createGetter("short", "32767");
		assertEquals((short) 32767, getter.getAsShort("short"));
	}
	
	@Test
	void getAsShortNegative() {
		KeyedValueGetter getter = createGetter("short", "-32768");
		assertEquals((short) -32768, getter.getAsShort("short"));
	}
	
	@Test
	void getAsShortInvalidThrows() {
		KeyedValueGetter getter = createGetter("short", "notAShort");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsShort("short"));
	}
	
	@Test
	void getAsIntegerValid() {
		KeyedValueGetter getter = createGetter("int", "2147483647");
		assertEquals(2147483647, getter.getAsInteger("int"));
	}
	
	@Test
	void getAsIntegerNegative() {
		KeyedValueGetter getter = createGetter("int", "-2147483648");
		assertEquals(-2147483648, getter.getAsInteger("int"));
	}
	
	@Test
	void getAsIntegerInvalidThrows() {
		KeyedValueGetter getter = createGetter("int", "notAnInt");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsInteger("int"));
	}
	
	@Test
	void getAsLongValid() {
		KeyedValueGetter getter = createGetter("long", "9223372036854775807");
		assertEquals(9223372036854775807L, getter.getAsLong("long"));
	}
	
	@Test
	void getAsLongNegative() {
		KeyedValueGetter getter = createGetter("long", "-9223372036854775808");
		assertEquals(-9223372036854775808L, getter.getAsLong("long"));
	}
	
	@Test
	void getAsLongInvalidThrows() {
		KeyedValueGetter getter = createGetter("long", "notALong");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsLong("long"));
	}
	
	@Test
	void getAsFloatValid() {
		KeyedValueGetter getter = createGetter("float", "3.14");
		assertEquals(3.14f, getter.getAsFloat("float"), 0.001f);
	}
	
	@Test
	void getAsFloatNegative() {
		KeyedValueGetter getter = createGetter("float", "-2.5");
		assertEquals(-2.5f, getter.getAsFloat("float"), 0.001f);
	}
	
	@Test
	void getAsFloatInvalidThrows() {
		KeyedValueGetter getter = createGetter("float", "notAFloat");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsFloat("float"));
	}
	
	@Test
	void getAsDoubleValid() {
		KeyedValueGetter getter = createGetter("double", "3.141592653589793");
		assertEquals(3.141592653589793, getter.getAsDouble("double"), 0.0000001);
	}
	
	@Test
	void getAsDoubleNegative() {
		KeyedValueGetter getter = createGetter("double", "-2.718281828");
		assertEquals(-2.718281828, getter.getAsDouble("double"), 0.0000001);
	}
	
	@Test
	void getAsDoubleInvalidThrows() {
		KeyedValueGetter getter = createGetter("double", "notADouble");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsDouble("double"));
	}
	
	@Test
	void getAsWithParserNullThrows() {
		KeyedValueGetter getter = createGetter("key", "value");
		assertThrows(NullPointerException.class, () -> getter.getAs("key", null));
	}
	
	@Test
	void getAsWithParserValid() {
		KeyedValueGetter getter = createGetter("num", "42");
		Integer result = getter.getAs("num", Integer::parseInt);
		assertEquals(42, result);
	}
	
	@Test
	void getAsWithParserCustomTransformation() {
		KeyedValueGetter getter = createGetter("text", "hello");
		String result = getter.getAs("text", String::toUpperCase);
		assertEquals("HELLO", result);
	}
	
	@Test
	void getAsWithParserException() {
		KeyedValueGetter getter = createGetter("num", "notANumber");
		assertThrows(IllegalArgumentException.class, () -> getter.getAs("num", Integer::parseInt));
	}
	
	@Test
	void getAsWithMultipleKeys() {
		MapKeyedValueGetter getter = new MapKeyedValueGetter();
		getter.put("name", "John");
		getter.put("age", "30");
		getter.put("active", "true");
		
		assertEquals("John", getter.getAsString("name"));
		assertEquals(30, getter.getAsInteger("age"));
		assertTrue(getter.getAsBoolean("active"));
	}
	
	private static @NonNull KeyedValueGetter createGetter(@NonNull String key, @NonNull String value) {
		MapKeyedValueGetter getter = new MapKeyedValueGetter();
		getter.put(key, value);
		return getter;
	}
	
	private static class MapKeyedValueGetter implements KeyedValueGetter {
		
		private final Map<String, String> values = new HashMap<>();
		
		void put(@NonNull String key, @NonNull String value) {
			this.values.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
		}
		
		@Override
		public @NonNull String getAsString(@NonNull String key) {
			Objects.requireNonNull(key, "Key must not be null");
			String value = this.values.get(key);
			if (value == null) {
				throw new IllegalArgumentException("No value found for key: " + key);
			}
			return value;
		}
	}
}
