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

package net.luis.utils.io.data.toml;

import net.luis.utils.io.data.toml.exception.TomlArrayIndexOutOfBoundsException;
import net.luis.utils.io.data.toml.exception.TomlTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlArray}.<br>
 *
 * @author Luis-St
 */
class TomlArrayTest {
	
	private static final TomlConfig CUSTOM_CONFIG = new TomlConfig(
		true, true, "  ",
		false, 3, true, 5, false, 80, false,
		TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
	);
	
	@Test
	void constructorWithEmptyList() {
		TomlArray array = new TomlArray();
		assertTrue(array.isEmpty());
		assertEquals(0, array.size());
	}
	
	@Test
	void constructorWithElementsList() {
		List<TomlElement> elements = List.of(TomlNull.INSTANCE, new TomlValue(42), new TomlValue("test"));
		TomlArray array = new TomlArray(elements);
		
		assertEquals(3, array.size());
		assertEquals(TomlNull.INSTANCE, array.get(0));
		assertEquals(new TomlValue(42), array.get(1));
		assertEquals(new TomlValue("test"), array.get(2));
	}
	
	@Test
	void constructorWithNullList() {
		assertThrows(NullPointerException.class, () -> new TomlArray(null));
	}
	
	@Test
	void tomlElementTypeChecks() {
		TomlArray array = new TomlArray();
		
		assertFalse(array.isTomlNull());
		assertFalse(array.isTomlValue());
		assertTrue(array.isTomlArray());
		assertFalse(array.isTomlTable());
	}
	
	@Test
	void tomlElementConversions() {
		TomlArray array = new TomlArray();
		
		assertThrows(TomlTypeException.class, array::getAsTomlTable);
		assertSame(array, array.getAsTomlArray());
		assertThrows(TomlTypeException.class, array::getAsTomlValue);
	}
	
	@Test
	void arrayOfTablesFlag() {
		TomlArray array = new TomlArray();
		assertFalse(array.isArrayOfTables());
		
		array.setArrayOfTables(true);
		assertTrue(array.isArrayOfTables());
		
		array.setArrayOfTables(false);
		assertFalse(array.isArrayOfTables());
	}
	
	@Test
	void sizeOperations() {
		TomlArray array = new TomlArray();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
		
		array.add(TomlNull.INSTANCE);
		assertEquals(1, array.size());
		assertFalse(array.isEmpty());
		
		array.add(new TomlValue(10));
		assertEquals(2, array.size());
		
		array.remove(0);
		assertEquals(1, array.size());
		
		array.clear();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
	}
	
	@Test
	void containsOperations() {
		TomlArray array = new TomlArray();
		TomlElement element = new TomlValue("test");
		
		assertFalse(array.contains(element));
		assertFalse(array.contains(null));
		
		array.add(element);
		assertTrue(array.contains(element));
		assertFalse(array.contains(new TomlValue("different")));
		
		array.add((TomlElement) null);
		assertTrue(array.contains(TomlNull.INSTANCE));
	}
	
	@Test
	void iteratorAndCollectionViews() {
		TomlArray array = new TomlArray();
		array.add(new TomlValue(1));
		array.add(new TomlValue(2));
		array.add(new TomlValue(3));
		
		int count = 0;
		for (TomlElement element : array) {
			count++;
			assertInstanceOf(TomlValue.class, element);
		}
		assertEquals(3, count);
		
		assertEquals(3, array.elements().size());
		assertTrue(array.elements().contains(new TomlValue(1)));
		
		List<TomlElement> elements = array.getElements();
		assertEquals(3, elements.size());
		assertEquals(new TomlValue(1), elements.get(0));
		assertEquals(new TomlValue(2), elements.get(1));
		assertEquals(new TomlValue(3), elements.get(2));
		
		assertThrows(UnsupportedOperationException.class, () -> elements.add(new TomlValue(4)));
	}
	
	@Test
	void setOperations() {
		TomlArray array = new TomlArray();
		
		assertThrows(TomlArrayIndexOutOfBoundsException.class, () -> array.set(-1, TomlNull.INSTANCE));
		assertThrows(TomlArrayIndexOutOfBoundsException.class, () -> array.set(0, TomlNull.INSTANCE));
		
		array.add(new TomlValue(1));
		
		TomlElement old = array.set(0, new TomlValue(2));
		assertEquals(new TomlValue(1), old);
		assertEquals(new TomlValue(2), array.get(0));
		
		array.set(0, (TomlElement) null);
		assertEquals(TomlNull.INSTANCE, array.get(0));
		
		array.set(0, "string");
		assertEquals(new TomlValue("string"), array.get(0));
		
		array.set(0, true);
		assertEquals(new TomlValue(true), array.get(0));
		
		array.set(0, (Number) null);
		assertEquals(TomlNull.INSTANCE, array.get(0));
		
		array.set(0, 42);
		assertEquals(new TomlValue(42), array.get(0));
	}
	
	@Test
	void addOperations() {
		TomlArray array = new TomlArray();
		
		array.add((TomlElement) null);
		assertEquals(TomlNull.INSTANCE, array.get(0));
		
		array.add(new TomlValue(42));
		assertEquals(new TomlValue(42), array.get(1));
		
		array.add("string");
		assertEquals(new TomlValue("string"), array.get(2));
		
		array.add((String) null);
		assertEquals(TomlNull.INSTANCE, array.get(3));
		
		array.add(true);
		assertEquals(new TomlValue(true), array.get(4));
		
		array.add((Number) null);
		assertEquals(TomlNull.INSTANCE, array.get(5));
		
		array.add(123);
		assertEquals(new TomlValue(123), array.get(6));
	}
	
	@Test
	void addDateTimeOperations() {
		TomlArray array = new TomlArray();
		
		LocalDate date = LocalDate.of(2025, 1, 15);
		LocalTime time = LocalTime.of(14, 30, 45);
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 45);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 1, 15, 14, 30, 45, 0, ZoneOffset.UTC);
		
		array.add(date);
		array.add(time);
		array.add(dateTime);
		array.add(offsetDateTime);
		array.add((LocalDate) null);
		array.add((LocalTime) null);
		array.add((LocalDateTime) null);
		array.add((OffsetDateTime) null);
		
		assertEquals(new TomlValue(date), array.get(0));
		assertEquals(new TomlValue(time), array.get(1));
		assertEquals(new TomlValue(dateTime), array.get(2));
		assertEquals(new TomlValue(offsetDateTime), array.get(3));
		assertEquals(TomlNull.INSTANCE, array.get(4));
		assertEquals(TomlNull.INSTANCE, array.get(5));
		assertEquals(TomlNull.INSTANCE, array.get(6));
		assertEquals(TomlNull.INSTANCE, array.get(7));
	}
	
	@Test
	void addAllOperations() {
		TomlArray array = new TomlArray();
		
		TomlArray other = new TomlArray();
		other.add(new TomlValue(1));
		other.add(new TomlValue(2));
		
		assertThrows(NullPointerException.class, () -> array.addAll((TomlArray) null));
		
		array.addAll(other);
		assertEquals(2, array.size());
		assertEquals(new TomlValue(1), array.get(0));
		assertEquals(new TomlValue(2), array.get(1));
		
		assertThrows(NullPointerException.class, () -> array.addAll((TomlElement[]) null));
		
		array.addAll(new TomlValue(3), new TomlValue(4));
		assertEquals(4, array.size());
		assertEquals(new TomlValue(3), array.get(2));
		assertEquals(new TomlValue(4), array.get(3));
		
		assertThrows(NullPointerException.class, () -> array.addAll((List<TomlElement>) null));
		
		array.addAll(Arrays.asList(new TomlValue(5), new TomlValue(6)));
		assertEquals(6, array.size());
		assertEquals(new TomlValue(5), array.get(4));
		assertEquals(new TomlValue(6), array.get(5));
	}
	
	@Test
	void removeOperations() {
		TomlArray array = new TomlArray();
		
		assertThrows(TomlArrayIndexOutOfBoundsException.class, () -> array.remove(-1));
		assertThrows(TomlArrayIndexOutOfBoundsException.class, () -> array.remove(0));
		
		array.add(new TomlValue(1));
		array.add(new TomlValue(2));
		array.add(new TomlValue(3));
		
		TomlElement removed = array.remove(1);
		assertEquals(new TomlValue(2), removed);
		assertEquals(2, array.size());
		assertEquals(new TomlValue(1), array.get(0));
		assertEquals(new TomlValue(3), array.get(1));
		
		assertTrue(array.remove(new TomlValue(1)));
		assertFalse(array.remove(new TomlValue(999)));
		assertFalse(array.remove(null));
		
		array.add(new TomlValue(10));
		array.add(new TomlValue(20));
		assertEquals(3, array.size());
		
		array.clear();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
	}
	
	@Test
	void getOperationsWithBoundsChecking() {
		TomlArray array = new TomlArray();
		
		assertThrows(TomlArrayIndexOutOfBoundsException.class, () -> array.get(-1));
		assertThrows(TomlArrayIndexOutOfBoundsException.class, () -> array.get(0));
		
		array.add(new TomlTable());
		array.add(new TomlArray());
		array.add(new TomlValue("test"));
		array.add(new TomlValue(42));
		array.add(new TomlValue(true));
		array.add(new TomlValue(3.14));
		
		assertEquals(new TomlTable(), array.get(0));
		assertEquals(new TomlArray(), array.get(1));
		assertEquals(new TomlValue("test"), array.get(2));
	}
	
	@Test
	void getAsSpecificTypes() {
		TomlArray array = new TomlArray();
		array.add(new TomlTable());
		array.add(new TomlArray());
		array.add(new TomlValue("test"));
		array.add(new TomlValue(42));
		array.add(new TomlValue(true));
		array.add(new TomlValue(3.14));
		array.add(TomlNull.INSTANCE);
		
		assertEquals(new TomlTable(), array.getAsTomlTable(0));
		assertEquals(new TomlArray(), array.getAsTomlArray(1));
		assertEquals(new TomlValue("test"), array.getAsTomlValue(2));
		
		assertEquals("test", array.getAsString(2));
		assertEquals(42, array.getAsNumber(3));
		assertEquals(42, array.getAsInteger(3));
		assertEquals(42L, array.getAsLong(3));
		assertEquals(3.14, array.getAsDouble(5));
		assertTrue(array.getAsBoolean(4));
		
		assertThrows(TomlTypeException.class, () -> array.getAsTomlTable(2));
		assertThrows(TomlTypeException.class, () -> array.getAsTomlArray(2));
		assertThrows(TomlTypeException.class, () -> array.getAsTomlValue(0));
		assertThrows(TomlTypeException.class, () -> array.getAsString(6));
		assertThrows(TomlTypeException.class, () -> array.getAsNumber(6));
		
		assertThrows(TomlArrayIndexOutOfBoundsException.class, () -> array.getAsTomlTable(-1));
		assertThrows(TomlArrayIndexOutOfBoundsException.class, () -> array.getAsString(100));
	}
	
	@Test
	void getAsDateTimeTypes() {
		TomlArray array = new TomlArray();
		
		LocalDate date = LocalDate.of(2025, 1, 15);
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 45);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 1, 15, 14, 30, 45, 0, ZoneOffset.UTC);
		
		array.add(date);
		array.add(dateTime);
		array.add(offsetDateTime);
		
		assertEquals(date, array.getAsLocalDate(0));
		assertEquals(dateTime, array.getAsLocalDateTime(1));
		assertEquals(offsetDateTime, array.getAsOffsetDateTime(2));
		
		assertThrows(TomlTypeException.class, () -> array.getAsLocalDate(1));
		assertThrows(TomlTypeException.class, () -> array.getAsLocalDateTime(0));
	}
	
	@Test
	void equalsAndHashCode() {
		TomlArray array1 = new TomlArray();
		TomlArray array2 = new TomlArray();
		TomlArray array3 = new TomlArray();
		
		assertEquals(array1, array2);
		assertEquals(array1.hashCode(), array2.hashCode());
		
		array1.add(new TomlValue(42));
		array1.add(new TomlValue("test"));
		
		array2.add(new TomlValue(42));
		array2.add(new TomlValue("test"));
		
		assertEquals(array1, array2);
		assertEquals(array1.hashCode(), array2.hashCode());
		
		array3.add(new TomlValue(43));
		array3.add(new TomlValue("test"));
		
		assertNotEquals(array1, array3);
		assertNotEquals(array1, null);
		assertNotEquals(array1, "not an array");
		
		assertEquals(array1, array1);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		TomlArray array = new TomlArray();
		assertEquals("[]", array.toString());
		
		array.add(new TomlValue(42));
		assertEquals("[42]", array.toString());
		
		for (int i = 1; i < 10; i++) {
			array.add(new TomlValue(i));
		}
		assertEquals("[42, 1, 2, 3, 4, 5, 6, 7, 8, 9]", array.toString());
	}
	
	@Test
	void toStringWithCustomConfig() {
		TomlArray array = new TomlArray();
		
		assertThrows(NullPointerException.class, () -> array.toString(null));
		
		assertEquals("[]", array.toString(CUSTOM_CONFIG));
		
		for (int i = 0; i < 5; i++) {
			array.add(new TomlValue(i));
		}
		assertEquals("[0, 1, 2, 3, 4]", array.toString(CUSTOM_CONFIG));
		
		array.add(new TomlValue(5));
		String result = array.toString(CUSTOM_CONFIG);
		assertTrue(result.contains(System.lineSeparator()));
		assertTrue(result.contains("  "));
		assertTrue(result.startsWith("["));
		assertTrue(result.endsWith("]"));
	}
	
	@Test
	void toStringWithNestedStructures() {
		TomlArray array = new TomlArray();
		TomlTable nestedTable = new TomlTable();
		nestedTable.add("key", new TomlValue("value"));
		
		TomlArray nestedArray = new TomlArray();
		nestedArray.add(new TomlValue(1));
		nestedArray.add(new TomlValue(2));
		
		array.add(nestedTable);
		array.add(nestedArray);
		
		String result = array.toString();
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
		assertTrue(result.contains("[1, 2]"));
	}
}
