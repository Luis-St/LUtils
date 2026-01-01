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

import net.luis.utils.io.data.toml.exception.NoSuchTomlElementException;
import net.luis.utils.io.data.toml.exception.TomlTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlTable}.<br>
 *
 * @author Luis-St
 */
class TomlTableTest {
	
	private static final TomlConfig CUSTOM_CONFIG = new TomlConfig(
		true, true, "  ",
		false, 3, true, 5, false, 80, false,
		TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
	);
	
	private static final TomlConfig INLINE_CONFIG = new TomlConfig(
		true, true, "  ",
		true, 10, true, 10, false, 80, false,
		TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
	);
	
	@Test
	void constructorWithEmptyMap() {
		TomlTable table = new TomlTable();
		assertTrue(table.isEmpty());
		assertEquals(0, table.size());
	}
	
	@Test
	void constructorWithElementsMap() {
		Map<String, TomlElement> elements = new LinkedHashMap<>();
		elements.put("key1", new TomlValue("value1"));
		elements.put("key2", new TomlValue(42));
		elements.put("key3", TomlNull.INSTANCE);
		
		TomlTable table = new TomlTable(elements);
		
		assertEquals(3, table.size());
		assertEquals(new TomlValue("value1"), table.get("key1"));
		assertEquals(new TomlValue(42), table.get("key2"));
		assertEquals(TomlNull.INSTANCE, table.get("key3"));
	}
	
	@Test
	void constructorWithNullMap() {
		assertThrows(NullPointerException.class, () -> new TomlTable(null));
	}
	
	@Test
	void tomlElementTypeChecks() {
		TomlTable table = new TomlTable();
		
		assertFalse(table.isTomlNull());
		assertFalse(table.isTomlValue());
		assertFalse(table.isTomlArray());
		assertTrue(table.isTomlTable());
	}
	
	@Test
	void tomlElementConversions() {
		TomlTable table = new TomlTable();
		
		assertSame(table, table.getAsTomlTable());
		assertThrows(TomlTypeException.class, table::getAsTomlArray);
		assertThrows(TomlTypeException.class, table::getAsTomlValue);
	}
	
	@Test
	void inlineFlag() {
		TomlTable table = new TomlTable();
		assertFalse(table.isInline());
		
		table.setInline(true);
		assertTrue(table.isInline());
		
		table.setInline(false);
		assertFalse(table.isInline());
	}
	
	@Test
	void sizeAndEmptyOperations() {
		TomlTable table = new TomlTable();
		assertEquals(0, table.size());
		assertTrue(table.isEmpty());
		
		table.add("key1", new TomlValue("value1"));
		assertEquals(1, table.size());
		assertFalse(table.isEmpty());
		
		table.add("key2", new TomlValue(42));
		assertEquals(2, table.size());
		
		table.remove("key1");
		assertEquals(1, table.size());
		
		table.clear();
		assertEquals(0, table.size());
		assertTrue(table.isEmpty());
	}
	
	@Test
	void containsOperations() {
		TomlTable table = new TomlTable();
		TomlElement value = new TomlValue("test");
		
		assertFalse(table.containsKey("key"));
		assertFalse(table.containsKey(null));
		assertFalse(table.containsValue(value));
		assertFalse(table.containsValue(null));
		
		table.add("key", value);
		assertTrue(table.containsKey("key"));
		assertTrue(table.containsValue(value));
		assertFalse(table.containsKey("other"));
		assertFalse(table.containsValue(new TomlValue("different")));
		
		table.add("nullKey", (String) null);
		assertTrue(table.containsKey("nullKey"));
		assertTrue(table.containsValue(TomlNull.INSTANCE));
	}
	
	@Test
	void keySetAndValuesOperations() {
		TomlTable table = new TomlTable();
		
		assertEquals(Set.of(), table.keySet());
		assertTrue(table.elements().isEmpty());
		assertTrue(table.entrySet().isEmpty());
		
		table.add("key1", new TomlValue("value1"));
		table.add("key2", new TomlValue(42));
		table.add("key3", TomlNull.INSTANCE);
		
		Set<String> keys = table.keySet();
		assertEquals(Set.of("key1", "key2", "key3"), keys);
		
		assertEquals(3, table.elements().size());
		assertTrue(table.elements().contains(new TomlValue("value1")));
		assertTrue(table.elements().contains(new TomlValue(42)));
		assertTrue(table.elements().contains(TomlNull.INSTANCE));
		
		assertEquals(3, table.entrySet().size());
	}
	
	@Test
	void iteratorOperations() {
		TomlTable table = new TomlTable();
		table.add("key1", new TomlValue("value1"));
		table.add("key2", new TomlValue(42));
		
		Iterator<Map.Entry<String, TomlElement>> iterator = table.iterator();
		assertTrue(iterator.hasNext());
		
		int count = 0;
		while (iterator.hasNext()) {
			Map.Entry<String, TomlElement> entry = iterator.next();
			assertNotNull(entry.getKey());
			assertNotNull(entry.getValue());
			count++;
		}
		assertEquals(2, count);
	}
	
	@Test
	void forEachOperation() {
		TomlTable table = new TomlTable();
		
		assertThrows(NullPointerException.class, () -> table.forEach((BiConsumer<? super String, ? super TomlElement>) null));
		
		table.forEach((key, value) -> fail("Should not be called for empty table"));
		
		table.add("key1", new TomlValue("value1"));
		table.add("key2", new TomlValue(42));
		
		AtomicInteger callCount = new AtomicInteger(0);
		table.forEach((key, value) -> {
			callCount.incrementAndGet();
			assertTrue(Set.of("key1", "key2").contains(key));
			assertNotNull(value);
		});
		
		assertEquals(2, callCount.get());
	}
	
	@Test
	void addOperationsWithTomlElement() {
		TomlTable table = new TomlTable();
		
		assertThrows(NullPointerException.class, () -> table.add(null, new TomlValue("value")));
		
		TomlElement previous = table.add("key1", (TomlElement) null);
		assertNull(previous);
		assertEquals(TomlNull.INSTANCE, table.get("key1"));
		
		previous = table.add("key1", new TomlValue("newValue"));
		assertEquals(TomlNull.INSTANCE, previous);
		assertEquals(new TomlValue("newValue"), table.get("key1"));
	}
	
	@Test
	void addOperationsWithPrimitiveTypes() {
		TomlTable table = new TomlTable();
		
		table.add("stringKey", "testString");
		assertEquals(new TomlValue("testString"), table.get("stringKey"));
		
		table.add("nullStringKey", (String) null);
		assertEquals(TomlNull.INSTANCE, table.get("nullStringKey"));
		
		table.add("booleanKey", true);
		assertEquals(new TomlValue(true), table.get("booleanKey"));
		
		table.add("numberKey", Integer.valueOf(42));
		assertEquals(new TomlValue(42), table.get("numberKey"));
		
		table.add("nullNumberKey", (Number) null);
		assertEquals(TomlNull.INSTANCE, table.get("nullNumberKey"));
	}
	
	@Test
	void addOperationsWithDateTimeTypes() {
		TomlTable table = new TomlTable();
		
		LocalDate date = LocalDate.of(2025, 1, 15);
		LocalTime time = LocalTime.of(14, 30, 45);
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 45);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 1, 15, 14, 30, 45, 0, ZoneOffset.UTC);
		
		table.add("dateKey", date);
		table.add("timeKey", time);
		table.add("dateTimeKey", dateTime);
		table.add("offsetDateTimeKey", offsetDateTime);
		table.add("nullDateKey", (LocalDate) null);
		table.add("nullTimeKey", (LocalTime) null);
		table.add("nullDateTimeKey", (LocalDateTime) null);
		table.add("nullOffsetDateTimeKey", (OffsetDateTime) null);
		
		assertEquals(new TomlValue(date), table.get("dateKey"));
		assertEquals(new TomlValue(time), table.get("timeKey"));
		assertEquals(new TomlValue(dateTime), table.get("dateTimeKey"));
		assertEquals(new TomlValue(offsetDateTime), table.get("offsetDateTimeKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullDateKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullTimeKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullDateTimeKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullOffsetDateTimeKey"));
	}
	
	@Test
	void addAllOperations() {
		TomlTable table = new TomlTable();
		
		TomlTable other = new TomlTable();
		other.add("key1", new TomlValue("value1"));
		other.add("key2", new TomlValue(42));
		
		assertThrows(NullPointerException.class, () -> table.addAll((TomlTable) null));
		
		table.addAll(other);
		assertEquals(2, table.size());
		assertEquals(new TomlValue("value1"), table.get("key1"));
		assertEquals(new TomlValue(42), table.get("key2"));
		
		Map<String, TomlElement> map = new HashMap<>();
		map.put("key3", new TomlValue("value3"));
		map.put("key4", TomlNull.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> table.addAll((Map<String, TomlElement>) null));
		
		table.addAll(map);
		assertEquals(4, table.size());
		assertEquals(new TomlValue("value3"), table.get("key3"));
		assertEquals(TomlNull.INSTANCE, table.get("key4"));
	}
	
	@Test
	void dottedKeyOperations() {
		TomlTable table = new TomlTable();
		
		assertThrows(NullPointerException.class, () -> table.addDotted(null, new TomlValue("value")));
		assertThrows(NullPointerException.class, () -> table.getDotted(null));
		assertThrows(NullPointerException.class, () -> table.containsDotted(null));
		
		table.addDotted("simple", new TomlValue("simpleValue"));
		assertEquals(new TomlValue("simpleValue"), table.getDotted("simple"));
		assertTrue(table.containsDotted("simple"));
		
		table.addDotted("a.b.c", new TomlValue("nestedValue"));
		assertEquals(new TomlValue("nestedValue"), table.getDotted("a.b.c"));
		assertTrue(table.containsDotted("a.b.c"));
		assertTrue(table.containsDotted("a.b"));
		assertTrue(table.containsDotted("a"));
		
		assertInstanceOf(TomlTable.class, table.get("a"));
		TomlTable aTable = (TomlTable) table.get("a");
		assertInstanceOf(TomlTable.class, aTable.get("b"));
		
		assertNull(table.getDotted("nonexistent"));
		assertNull(table.getDotted("a.nonexistent"));
		assertNull(table.getDotted("a.b.nonexistent"));
		assertFalse(table.containsDotted("nonexistent"));
		
		table.add("value", new TomlValue("not a table"));
		assertNull(table.getDotted("value.nested"));
		assertFalse(table.containsDotted("value.nested"));
	}
	
	@Test
	void removeOperations() {
		TomlTable table = new TomlTable();
		
		assertNull(table.remove("nonexistent"));
		assertNull(table.remove(null));
		
		table.add("key1", new TomlValue("value1"));
		table.add("key2", new TomlValue(42));
		table.add("key3", TomlNull.INSTANCE);
		
		TomlElement removed = table.remove("key1");
		assertEquals(new TomlValue("value1"), removed);
		assertEquals(2, table.size());
		assertFalse(table.containsKey("key1"));
		
		assertNull(table.remove("nonexistent"));
		assertEquals(2, table.size());
		
		table.clear();
		assertEquals(0, table.size());
		assertTrue(table.isEmpty());
	}
	
	@Test
	void replaceOperations() {
		TomlTable table = new TomlTable();
		
		assertThrows(NullPointerException.class, () -> table.replace(null, new TomlValue("value")));
		
		TomlElement result = table.replace("key", new TomlValue("value"));
		assertNull(result);
		assertTrue(table.isEmpty());
		
		table.add("key1", new TomlValue("oldValue"));
		
		result = table.replace("key1", new TomlValue("newValue"));
		assertEquals(new TomlValue("oldValue"), result);
		assertEquals(new TomlValue("newValue"), table.get("key1"));
		
		result = table.replace("key1", null);
		assertEquals(new TomlValue("newValue"), result);
		assertEquals(TomlNull.INSTANCE, table.get("key1"));
	}
	
	@Test
	void replaceWithOldValueOperations() {
		TomlTable table = new TomlTable();
		
		assertThrows(NullPointerException.class, () -> table.replace(null, new TomlValue("old"), new TomlValue("new")));
		assertThrows(NullPointerException.class, () -> table.replace("key", null, new TomlValue("new")));
		
		table.add("key1", new TomlValue("oldValue"));
		
		assertFalse(table.replace("key1", new TomlValue("wrongOld"), new TomlValue("newValue")));
		assertEquals(new TomlValue("oldValue"), table.get("key1"));
		
		assertTrue(table.replace("key1", new TomlValue("oldValue"), new TomlValue("newValue")));
		assertEquals(new TomlValue("newValue"), table.get("key1"));
		
		assertTrue(table.replace("key1", new TomlValue("newValue"), null));
		assertEquals(TomlNull.INSTANCE, table.get("key1"));
	}
	
	@Test
	void getOperations() {
		TomlTable table = new TomlTable();
		
		assertThrows(NullPointerException.class, () -> table.get(null));
		assertNull(table.get("nonexistent"));
		
		table.add("stringKey", new TomlValue("stringValue"));
		table.add("numberKey", new TomlValue(42));
		table.add("booleanKey", new TomlValue(true));
		table.add("nullKey", TomlNull.INSTANCE);
		
		assertEquals(new TomlValue("stringValue"), table.get("stringKey"));
		assertEquals(new TomlValue(42), table.get("numberKey"));
		assertEquals(new TomlValue(true), table.get("booleanKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullKey"));
	}
	
	@Test
	void getTomlValueOperations() {
		TomlTable table = new TomlTable();
		
		assertThrows(NoSuchTomlElementException.class, () -> table.getTomlValue("nonexistent"));
		
		table.add("valueKey", new TomlValue("test"));
		table.add("nullKey", TomlNull.INSTANCE);
		table.add("tableKey", new TomlTable());
		
		assertEquals(new TomlValue("test"), table.getTomlValue("valueKey"));
		assertThrows(TomlTypeException.class, () -> table.getTomlValue("nullKey"));
		assertThrows(TomlTypeException.class, () -> table.getTomlValue("tableKey"));
	}
	
	@Test
	void getTomlArrayOperations() {
		TomlTable table = new TomlTable();
		
		assertThrows(NoSuchTomlElementException.class, () -> table.getTomlArray("nonexistent"));
		
		table.add("arrayKey", new TomlArray());
		table.add("valueKey", new TomlValue("test"));
		
		assertEquals(new TomlArray(), table.getTomlArray("arrayKey"));
		assertThrows(TomlTypeException.class, () -> table.getTomlArray("valueKey"));
	}
	
	@Test
	void getTomlTableOperations() {
		TomlTable table = new TomlTable();
		
		assertThrows(NoSuchTomlElementException.class, () -> table.getTomlTable("nonexistent"));
		
		table.add("tableKey", new TomlTable());
		table.add("valueKey", new TomlValue("test"));
		
		assertEquals(new TomlTable(), table.getTomlTable("tableKey"));
		assertThrows(TomlTypeException.class, () -> table.getTomlTable("valueKey"));
	}
	
	@Test
	void getAsSpecificTypesSuccess() {
		TomlTable table = new TomlTable();
		table.add("stringKey", new TomlValue("stringValue"));
		table.add("numberKey", new TomlValue(42));
		table.add("booleanKey", new TomlValue(true));
		table.add("doubleKey", new TomlValue(3.14));
		
		assertEquals("stringValue", table.getAsString("stringKey"));
		assertEquals(42, table.getAsNumber("numberKey"));
		assertEquals(42, table.getAsInteger("numberKey"));
		assertEquals(42L, table.getAsLong("numberKey"));
		assertEquals(42.0f, table.getAsFloat("numberKey"));
		assertEquals(42.0, table.getAsDouble("numberKey"));
		assertEquals((byte) 42, table.getAsByte("numberKey"));
		assertEquals((short) 42, table.getAsShort("numberKey"));
		assertTrue(table.getAsBoolean("booleanKey"));
		assertEquals(3.14, table.getAsDouble("doubleKey"));
	}
	
	@Test
	void getAsSpecificTypesExceptions() {
		TomlTable table = new TomlTable();
		table.add("stringKey", new TomlValue("stringValue"));
		table.add("nullKey", TomlNull.INSTANCE);
		
		assertThrows(NoSuchTomlElementException.class, () -> table.getAsString("nonexistent"));
		assertThrows(NoSuchTomlElementException.class, () -> table.getAsBoolean("nonexistent"));
		assertThrows(NoSuchTomlElementException.class, () -> table.getAsNumber("nonexistent"));
		
		assertThrows(TomlTypeException.class, () -> table.getAsString("nullKey"));
		assertThrows(TomlTypeException.class, () -> table.getAsBoolean("nullKey"));
		assertThrows(TomlTypeException.class, () -> table.getAsNumber("nullKey"));
		
		assertThrows(TomlTypeException.class, () -> table.getAsBoolean("stringKey"));
		assertThrows(TomlTypeException.class, () -> table.getAsNumber("stringKey"));
	}
	
	@Test
	void getAsDateTimeTypes() {
		TomlTable table = new TomlTable();
		
		LocalDate date = LocalDate.of(2025, 1, 15);
		LocalTime time = LocalTime.of(14, 30, 45);
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 45);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 1, 15, 14, 30, 45, 0, ZoneOffset.UTC);
		
		table.add("dateKey", date);
		table.add("timeKey", time);
		table.add("dateTimeKey", dateTime);
		table.add("offsetDateTimeKey", offsetDateTime);
		
		assertEquals(date, table.getAsLocalDate("dateKey"));
		assertEquals(time, table.getAsLocalTime("timeKey"));
		assertEquals(dateTime, table.getAsLocalDateTime("dateTimeKey"));
		assertEquals(offsetDateTime, table.getAsOffsetDateTime("offsetDateTimeKey"));
		
		assertThrows(TomlTypeException.class, () -> table.getAsLocalDate("dateTimeKey"));
		assertThrows(TomlTypeException.class, () -> table.getAsLocalDateTime("dateKey"));
		assertThrows(NoSuchTomlElementException.class, () -> table.getAsLocalDate("nonexistent"));
	}
	
	@Test
	void equalsAndHashCode() {
		TomlTable table1 = new TomlTable();
		TomlTable table2 = new TomlTable();
		TomlTable table3 = new TomlTable();
		
		assertEquals(table1, table2);
		assertEquals(table1.hashCode(), table2.hashCode());
		
		table1.add("key1", new TomlValue("value1"));
		table1.add("key2", new TomlValue(42));
		
		table2.add("key1", new TomlValue("value1"));
		table2.add("key2", new TomlValue(42));
		
		assertEquals(table1, table2);
		assertEquals(table1.hashCode(), table2.hashCode());
		
		table3.add("key1", new TomlValue("different"));
		table3.add("key2", new TomlValue(42));
		
		assertNotEquals(table1, table3);
		assertNotEquals(table1, null);
		assertNotEquals(table1, "not a table");
		
		assertEquals(table1, table1);
		
		TomlTable table4 = new TomlTable();
		table4.add("key1", new TomlValue("value1"));
		assertNotEquals(table1, table4);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		TomlTable table = new TomlTable();
		assertEquals("{}", table.toString());
		
		table.add("key", new TomlValue("value"));
		String result = table.toString();
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void toStringWithCustomConfig() {
		TomlTable table = new TomlTable();
		
		assertThrows(NullPointerException.class, () -> table.toString(null));
		
		assertEquals("{}", table.toString(CUSTOM_CONFIG));
		
		table.add("key1", new TomlValue("value1"));
		table.add("key2", new TomlValue(42));
		
		String result = table.toString(CUSTOM_CONFIG);
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("value1"));
		assertTrue(result.contains("key2"));
		assertTrue(result.contains("42"));
	}
	
	@Test
	void toStringWithInlineConfig() {
		TomlTable table = new TomlTable();
		table.add("key1", new TomlValue("value1"));
		table.add("key2", new TomlValue(42));
		
		String result = table.toString(INLINE_CONFIG);
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("key2"));
	}
	
	@Test
	void toStringWithInlineFlag() {
		TomlTable table = new TomlTable();
		table.add("key1", new TomlValue("value1"));
		table.add("key2", new TomlValue(42));
		
		table.setInline(true);
		String result = table.toString(CUSTOM_CONFIG);
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
	}
	
	@Test
	void toStringWithNestedStructures() {
		TomlTable table = new TomlTable();
		TomlTable nestedTable = new TomlTable();
		nestedTable.add("nestedKey", new TomlValue("nestedValue"));
		nestedTable.setInline(true);
		
		TomlArray nestedArray = new TomlArray();
		nestedArray.add(new TomlValue(1));
		nestedArray.add(new TomlValue(2));
		
		table.add("table", nestedTable);
		table.add("array", nestedArray);
		
		String result = table.toString();
		assertTrue(result.contains("nestedKey"));
		assertTrue(result.contains("nestedValue"));
	}
	
	@Test
	void toSectionString() {
		TomlTable table = new TomlTable();
		table.add("key1", new TomlValue("value1"));
		table.add("key2", new TomlValue(42));
		
		String result = table.toSectionString("section", CUSTOM_CONFIG);
		assertTrue(result.contains("[section]"));
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("value1"));
		assertTrue(result.contains("key2"));
		assertTrue(result.contains("42"));
	}
	
	@Test
	void toSectionStringWithNestedTables() {
		TomlTable table = new TomlTable();
		table.add("key1", new TomlValue("value1"));
		
		TomlTable nestedTable = new TomlTable();
		nestedTable.add("nestedKey", new TomlValue("nestedValue"));
		table.add("nested", nestedTable);
		
		String result = table.toSectionString("main", CUSTOM_CONFIG);
		assertTrue(result.contains("[main]"));
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("[main.nested]"));
		assertTrue(result.contains("nestedKey"));
	}
	
	@Test
	void toSectionStringValidation() {
		TomlTable table = new TomlTable();
		
		assertThrows(NullPointerException.class, () -> table.toSectionString(null, CUSTOM_CONFIG));
		assertThrows(NullPointerException.class, () -> table.toSectionString("section", null));
	}
	
	@Test
	void preservesInsertionOrder() {
		TomlTable table = new TomlTable();
		
		table.add("third", new TomlValue(3));
		table.add("first", new TomlValue(1));
		table.add("second", new TomlValue(2));
		
		String[] expectedOrder = { "third", "first", "second" };
		String[] actualOrder = table.keySet().toArray(new String[0]);
		assertArrayEquals(expectedOrder, actualOrder);
		
		String result = table.toString();
		int thirdIndex = result.indexOf("third");
		int firstIndex = result.indexOf("first");
		int secondIndex = result.indexOf("second");
		
		assertTrue(thirdIndex < firstIndex);
		assertTrue(firstIndex < secondIndex);
	}
	
	@Test
	void keysRequiringQuoting() {
		TomlTable table = new TomlTable();
		table.add("simple-key", new TomlValue("value1"));
		table.add("key_with_underscore", new TomlValue("value2"));
		table.add("key with spaces", new TomlValue("value3"));
		table.add("key.with.dots", new TomlValue("value4"));
		table.add("key\"with\"quotes", new TomlValue("value5"));
		
		assertTrue(table.containsKey("simple-key"));
		assertTrue(table.containsKey("key with spaces"));
		assertTrue(table.containsKey("key.with.dots"));
		assertTrue(table.containsKey("key\"with\"quotes"));
		
		String result = table.toString();
		assertTrue(result.contains("simple-key"));
	}
}
