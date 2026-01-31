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
 * Test class for {@link IndexedValueGetter}.<br>
 *
 * @author Luis-St
 */
class IndexedValueGetterTest {
	
	@Test
	void getAsStringReturnsValue() {
		IndexedValueGetter getter = createGetter("value");
		assertEquals("value", getter.getAsString(0));
	}
	
	@Test
	void getAsStringOutOfBoundsThrows() {
		IndexedValueGetter getter = createGetter("value");
		assertThrows(IndexOutOfBoundsException.class, () -> getter.getAsString(1));
		assertThrows(IndexOutOfBoundsException.class, () -> getter.getAsString(-1));
	}
	
	@Test
	void getAsBooleanTrue() {
		IndexedValueGetter getter = createGetter("true");
		assertTrue(getter.getAsBoolean(0));
	}
	
	@Test
	void getAsBooleanFalse() {
		IndexedValueGetter getter = createGetter("false");
		assertFalse(getter.getAsBoolean(0));
	}
	
	@Test
	void getAsBooleanCaseInsensitive() {
		ListIndexedValueGetter getter = new ListIndexedValueGetter();
		getter.add("TRUE");
		getter.add("false");
		getter.add("TrUe");
		
		assertTrue(getter.getAsBoolean(0));
		assertFalse(getter.getAsBoolean(1));
		assertTrue(getter.getAsBoolean(2));
	}
	
	@Test
	void getAsBooleanInvalidThrows() {
		IndexedValueGetter getter = createGetter("notABoolean");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsBoolean(0));
	}
	
	@Test
	void getAsNumberInteger() {
		IndexedValueGetter getter = createGetter("42");
		Number result = getter.getAsNumber(0);
		assertEquals(42, result.intValue());
	}
	
	@Test
	void getAsNumberDecimal() {
		IndexedValueGetter getter = createGetter("3.14");
		Number result = getter.getAsNumber(0);
		assertEquals(3.14, result.doubleValue(), 0.001);
	}
	
	@Test
	void getAsNumberNegative() {
		IndexedValueGetter getter = createGetter("-100");
		Number result = getter.getAsNumber(0);
		assertEquals(-100, result.intValue());
	}
	
	@Test
	void getAsNumberInvalidThrows() {
		IndexedValueGetter getter = createGetter("notANumber");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsNumber(0));
	}
	
	@Test
	void getAsByteValid() {
		IndexedValueGetter getter = createGetter("127");
		assertEquals((byte) 127, getter.getAsByte(0));
	}
	
	@Test
	void getAsByteNegative() {
		IndexedValueGetter getter = createGetter("-128");
		assertEquals((byte) -128, getter.getAsByte(0));
	}
	
	@Test
	void getAsByteInvalidThrows() {
		IndexedValueGetter getter = createGetter("notAByte");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsByte(0));
	}
	
	@Test
	void getAsShortValid() {
		IndexedValueGetter getter = createGetter("32767");
		assertEquals((short) 32767, getter.getAsShort(0));
	}
	
	@Test
	void getAsShortNegative() {
		IndexedValueGetter getter = createGetter("-32768");
		assertEquals((short) -32768, getter.getAsShort(0));
	}
	
	@Test
	void getAsShortInvalidThrows() {
		IndexedValueGetter getter = createGetter("notAShort");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsShort(0));
	}
	
	@Test
	void getAsIntegerValid() {
		IndexedValueGetter getter = createGetter("2147483647");
		assertEquals(2147483647, getter.getAsInteger(0));
	}
	
	@Test
	void getAsIntegerNegative() {
		IndexedValueGetter getter = createGetter("-2147483648");
		assertEquals(-2147483648, getter.getAsInteger(0));
	}
	
	@Test
	void getAsIntegerInvalidThrows() {
		IndexedValueGetter getter = createGetter("notAnInt");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsInteger(0));
	}
	
	@Test
	void getAsLongValid() {
		IndexedValueGetter getter = createGetter("9223372036854775807");
		assertEquals(9223372036854775807L, getter.getAsLong(0));
	}
	
	@Test
	void getAsLongNegative() {
		IndexedValueGetter getter = createGetter("-9223372036854775808");
		assertEquals(-9223372036854775808L, getter.getAsLong(0));
	}
	
	@Test
	void getAsLongInvalidThrows() {
		IndexedValueGetter getter = createGetter("notALong");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsLong(0));
	}
	
	@Test
	void getAsFloatValid() {
		IndexedValueGetter getter = createGetter("3.14");
		assertEquals(3.14f, getter.getAsFloat(0), 0.001f);
	}
	
	@Test
	void getAsFloatNegative() {
		IndexedValueGetter getter = createGetter("-2.5");
		assertEquals(-2.5f, getter.getAsFloat(0), 0.001f);
	}
	
	@Test
	void getAsFloatInvalidThrows() {
		IndexedValueGetter getter = createGetter("notAFloat");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsFloat(0));
	}
	
	@Test
	void getAsDoubleValid() {
		IndexedValueGetter getter = createGetter("3.141592653589793");
		assertEquals(3.141592653589793, getter.getAsDouble(0), 0.0000001);
	}
	
	@Test
	void getAsDoubleNegative() {
		IndexedValueGetter getter = createGetter("-2.718281828");
		assertEquals(-2.718281828, getter.getAsDouble(0), 0.0000001);
	}
	
	@Test
	void getAsDoubleInvalidThrows() {
		IndexedValueGetter getter = createGetter("notADouble");
		assertThrows(IllegalArgumentException.class, () -> getter.getAsDouble(0));
	}
	
	@Test
	void getAsWithParserNullThrows() {
		IndexedValueGetter getter = createGetter("value");
		assertThrows(NullPointerException.class, () -> getter.getAs(0, null));
	}
	
	@Test
	void getAsWithParserValid() {
		IndexedValueGetter getter = createGetter("42");
		Integer result = getter.getAs(0, Integer::parseInt);
		assertEquals(42, result);
	}
	
	@Test
	void getAsWithParserCustomTransformation() {
		IndexedValueGetter getter = createGetter("hello");
		String result = getter.getAs(0, String::toUpperCase);
		assertEquals("HELLO", result);
	}
	
	@Test
	void getAsWithParserException() {
		IndexedValueGetter getter = createGetter("notANumber");
		assertThrows(IllegalArgumentException.class, () -> getter.getAs(0, Integer::parseInt));
	}
	
	@Test
	void getAsWithMultipleIndices() {
		ListIndexedValueGetter getter = new ListIndexedValueGetter();
		getter.add("John");
		getter.add("30");
		getter.add("true");
		
		assertEquals("John", getter.getAsString(0));
		assertEquals(30, getter.getAsInteger(1));
		assertTrue(getter.getAsBoolean(2));
	}
	
	@Test
	void accessMultipleIndicesInSequence() {
		ListIndexedValueGetter getter = new ListIndexedValueGetter();
		getter.add("10");
		getter.add("20");
		getter.add("30");
		
		assertEquals(10, getter.getAsInteger(0));
		assertEquals(20, getter.getAsInteger(1));
		assertEquals(30, getter.getAsInteger(2));
	}
	
	private static @NonNull IndexedValueGetter createGetter(@NonNull String value) {
		ListIndexedValueGetter getter = new ListIndexedValueGetter();
		getter.add(value);
		return getter;
	}
	
	private static class ListIndexedValueGetter implements IndexedValueGetter {
		
		private final List<String> values = new ArrayList<>();
		
		void add(@NonNull String value) {
			this.values.add(Objects.requireNonNull(value));
		}
		
		@Override
		public @NonNull String getAsString(int index) {
			if (index < 0 || index >= this.values.size()) {
				throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + this.values.size());
			}
			return this.values.get(index);
		}
	}
}
