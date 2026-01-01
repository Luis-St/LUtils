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

package net.luis.utils.io.data.toml;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlBuilder}.<br>
 *
 * @author Luis-St
 */
class TomlBuilderTest {
	
	private static final TomlConfig CUSTOM_CONFIG = new TomlConfig(
		true, true, "  ",
		false, 3, true, 5, false, 80, false,
		TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
	);
	
	//region Table Builder Tests
	
	private static TomlTable createWorkerTable(String name, int priority) {
		TomlTable table = new TomlTable();
		table.add("name", new TomlValue(name));
		table.add("priority", new TomlValue(priority));
		return table;
	}
	
	@Test
	void createEmptyTable() {
		TomlTable table = TomlBuilder.table().build();
		assertTrue(table.isEmpty());
		assertEquals(0, table.size());
	}
	
	@Test
	void buildSimpleTable() {
		TomlTable table = TomlBuilder.table()
			.add("key1", "value1")
			.add("key2", 42)
			.add("key3", true)
			.build();
		
		assertEquals(3, table.size());
		assertEquals("value1", table.getAsString("key1"));
		assertEquals(42, table.getAsInteger("key2"));
		assertTrue(table.getAsBoolean("key3"));
	}
	
	@Test
	void addAllPrimitiveTypes() {
		TomlTable table = TomlBuilder.table()
			.add("stringKey", "testString")
			.add("booleanKey", true)
			.add("intKey", 42)
			.add("longKey", 100L)
			.add("doubleKey", 3.14)
			.add("nullStringKey", (String) null)
			.add("nullNumberKey", (Number) null)
			.build();
		
		assertEquals(7, table.size());
		assertEquals("testString", table.getAsString("stringKey"));
		assertTrue(table.getAsBoolean("booleanKey"));
		assertEquals(42, table.getAsInteger("intKey"));
		assertEquals(100L, table.getAsLong("longKey"));
		assertEquals(3.14, table.getAsDouble("doubleKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullStringKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullNumberKey"));
	}
	
	@Test
	void addDateTimeTypes() {
		LocalDate date = LocalDate.of(2025, 1, 15);
		LocalTime time = LocalTime.of(14, 30, 45);
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 45);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 1, 15, 14, 30, 45, 0, ZoneOffset.UTC);
		
		TomlTable table = TomlBuilder.table()
			.add("dateKey", date)
			.add("timeKey", time)
			.add("dateTimeKey", dateTime)
			.add("offsetDateTimeKey", offsetDateTime)
			.add("nullDateKey", (LocalDate) null)
			.add("nullTimeKey", (LocalTime) null)
			.add("nullDateTimeKey", (LocalDateTime) null)
			.add("nullOffsetKey", (OffsetDateTime) null)
			.build();
		
		assertEquals(8, table.size());
		assertEquals(date, table.getAsLocalDate("dateKey"));
		assertEquals(time, table.getAsLocalTime("timeKey"));
		assertEquals(dateTime, table.getAsLocalDateTime("dateTimeKey"));
		assertEquals(offsetDateTime, table.getAsOffsetDateTime("offsetDateTimeKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullDateKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullTimeKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullDateTimeKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullOffsetKey"));
	}
	
	@Test
	void addTomlElement() {
		TomlTable table = TomlBuilder.table()
			.add("valueKey", new TomlValue("elementValue"))
			.add("nullKey", (TomlElement) null)
			.add("arrayKey", new TomlArray())
			.add("tableKey", new TomlTable())
			.build();
		
		assertEquals(4, table.size());
		assertEquals(new TomlValue("elementValue"), table.get("valueKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullKey"));
		assertEquals(new TomlArray(), table.get("arrayKey"));
		assertEquals(new TomlTable(), table.get("tableKey"));
	}
	
	@Test
	void conditionalBuilding() {
		TomlTable table = TomlBuilder.table()
			.addIf(true, "included", "yes")
			.addIf(false, "excluded", "no")
			.addIf(true, "boolIncluded", true)
			.addIf(false, "boolExcluded", false)
			.addIf(true, "numIncluded", 42)
			.addIf(false, "numExcluded", 0)
			.build();
		
		assertEquals(3, table.size());
		assertTrue(table.containsKey("included"));
		assertFalse(table.containsKey("excluded"));
		assertTrue(table.containsKey("boolIncluded"));
		assertFalse(table.containsKey("boolExcluded"));
		assertTrue(table.containsKey("numIncluded"));
		assertFalse(table.containsKey("numExcluded"));
	}
	
	@Test
	void nestedTables() {
		TomlTable table = TomlBuilder.table()
			.add("rootKey", "rootValue")
			.startTable("nested")
			.add("nestedKey", "nestedValue")
			.endTable()
			.build();
		
		assertEquals(2, table.size());
		assertEquals("rootValue", table.getAsString("rootKey"));
		
		TomlTable nested = table.getTomlTable("nested");
		assertEquals("nestedValue", nested.getAsString("nestedKey"));
	}
	
	@Test
	void deeplyNestedTables() {
		TomlTable table = TomlBuilder.table()
			.startTable("level1")
			.add("key1", "value1")
			.startTable("level2")
			.add("key2", "value2")
			.startTable("level3")
			.add("key3", "value3")
			.endTable()
			.endTable()
			.endTable()
			.build();
		
		TomlTable level1 = table.getTomlTable("level1");
		assertEquals("value1", level1.getAsString("key1"));
		
		TomlTable level2 = level1.getTomlTable("level2");
		assertEquals("value2", level2.getAsString("key2"));
		
		TomlTable level3 = level2.getTomlTable("level3");
		assertEquals("value3", level3.getAsString("key3"));
	}
	
	@Test
	void inlineTableNested() {
		TomlTable table = TomlBuilder.table()
			.startInlineTable("inline")
			.add("key", "value")
			.endInlineTable()
			.build();
		
		TomlTable inline = table.getTomlTable("inline");
		assertTrue(inline.isInline());
		assertEquals("value", inline.getAsString("key"));
	}
	
	@Test
	void addNullKeyValidation() {
		TomlBuilder builder = TomlBuilder.table();
		assertThrows(NullPointerException.class, () -> builder.add(null, "value"));
		assertThrows(NullPointerException.class, () -> builder.add(null, true));
		assertThrows(NullPointerException.class, () -> builder.add(null, 42));
		assertThrows(NullPointerException.class, () -> builder.add(null, LocalDate.now()));
		assertThrows(NullPointerException.class, () -> builder.add(null, LocalTime.now()));
		assertThrows(NullPointerException.class, () -> builder.add(null, LocalDateTime.now()));
		assertThrows(NullPointerException.class, () -> builder.add(null, OffsetDateTime.now()));
		assertThrows(NullPointerException.class, () -> builder.add(null, (TomlElement) null));
	}
	
	@Test
	void startTableValidation() {
		TomlBuilder builder = TomlBuilder.table();
		assertThrows(NullPointerException.class, () -> builder.startTable(null));
		assertThrows(IllegalArgumentException.class, () -> builder.startTable(""));
		assertThrows(IllegalArgumentException.class, () -> builder.startTable("   "));
	}
	
	@Test
	void startInlineTableValidation() {
		TomlBuilder builder = TomlBuilder.table();
		assertThrows(NullPointerException.class, () -> builder.startInlineTable(null));
		assertThrows(IllegalArgumentException.class, () -> builder.startInlineTable(""));
		assertThrows(IllegalArgumentException.class, () -> builder.startInlineTable("   "));
	}
	
	@Test
	void endTableValidation() {
		TomlBuilder builder = TomlBuilder.table();
		assertThrows(IllegalStateException.class, builder::endTable);
	}
	
	@Test
	void addArrayOfTablesVarargs() {
		TomlTable table1 = new TomlTable();
		table1.add("name", new TomlValue("Server 1"));
		
		TomlTable table2 = new TomlTable();
		table2.add("name", new TomlValue("Server 2"));
		
		TomlTable root = TomlBuilder.table()
			.addArrayOfTables("servers", table1, table2)
			.build();
		
		TomlArray array = root.getTomlArray("servers");
		assertTrue(array.isArrayOfTables());
		assertEquals(2, array.size());
		assertEquals("Server 1", array.getAsTomlTable(0).getAsString("name"));
		assertEquals("Server 2", array.getAsTomlTable(1).getAsString("name"));
	}
	
	@Test
	void addArrayOfTablesList() {
		TomlTable table1 = new TomlTable();
		table1.add("id", new TomlValue(1));
		
		TomlTable table2 = new TomlTable();
		table2.add("id", new TomlValue(2));
		
		List<TomlTable> tables = List.of(table1, table2);
		
		TomlTable root = TomlBuilder.table()
			.addArrayOfTables("items", tables)
			.build();
		
		TomlArray array = root.getTomlArray("items");
		assertTrue(array.isArrayOfTables());
		assertEquals(2, array.size());
	}
	
	@Test
	void addArrayOfTablesValidation() {
		TomlBuilder builder = TomlBuilder.table();
		assertThrows(NullPointerException.class, () -> builder.addArrayOfTables(null, new TomlTable()));
		assertThrows(NullPointerException.class, () -> builder.addArrayOfTables("name", (TomlTable[]) null));
		assertThrows(NullPointerException.class, () -> builder.addArrayOfTables(null, List.of()));
		assertThrows(NullPointerException.class, () -> builder.addArrayOfTables("name", (List<TomlTable>) null));
	}
	
	@Test
	void nestingDepthOperations() {
		TomlBuilder builder = TomlBuilder.table();
		
		assertEquals(0, builder.getNestingDepth());
		assertTrue(builder.isAtRoot());
		assertFalse(builder.isNested());
		
		builder.startTable("level1");
		assertEquals(1, builder.getNestingDepth());
		assertFalse(builder.isAtRoot());
		assertTrue(builder.isNested());
		
		builder.startTable("level2");
		assertEquals(2, builder.getNestingDepth());
		
		builder.endTable();
		assertEquals(1, builder.getNestingDepth());
		
		builder.endTable();
		assertEquals(0, builder.getNestingDepth());
		assertTrue(builder.isAtRoot());
		assertFalse(builder.isNested());
	}
	
	@Test
	void buildAutoClosesNestedTables() {
		TomlTable table = TomlBuilder.table()
			.startTable("nested")
			.add("key", "value")
			// No endTable() call
			.build();
		
		assertEquals(1, table.size());
		assertEquals("value", table.getTomlTable("nested").getAsString("key"));
	}
	
	@Test
	void buildMultipleTimesReturnsConsistentResults() {
		TomlBuilder builder = TomlBuilder.table()
			.add("key", "value");
		
		TomlTable table1 = builder.build();
		TomlTable table2 = builder.build();
		
		assertSame(table1, table2);
		assertEquals(table1.get("key"), table2.get("key"));
	}
	
	@Test
	void toStringWithDefaultConfig() {
		TomlBuilder builder = TomlBuilder.table()
			.add("key", "value")
			.startTable("nested")
			.add("nestedKey", "nestedValue")
			.endTable();
		
		String result = builder.toString();
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void toStringWithCustomConfig() {
		TomlBuilder builder = TomlBuilder.table()
			.add("key", "value");
		
		String result = builder.toString(CUSTOM_CONFIG);
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void toStringNullConfigValidation() {
		TomlBuilder builder = TomlBuilder.table().add("key", "value");
		assertThrows(NullPointerException.class, () -> builder.toString(null));
	}
	
	@Test
	void methodChainingConsistency() {
		TomlBuilder builder = TomlBuilder.table();
		
		assertSame(builder, builder.add("stringKey", "value"));
		assertSame(builder, builder.add("boolKey", true));
		assertSame(builder, builder.add("numKey", 42));
		assertSame(builder, builder.add("dateKey", LocalDate.now()));
		assertSame(builder, builder.add("timeKey", LocalTime.now()));
		assertSame(builder, builder.add("dateTimeKey", LocalDateTime.now()));
		assertSame(builder, builder.add("offsetKey", OffsetDateTime.now()));
		assertSame(builder, builder.add("elementKey", (TomlElement) null));
		assertSame(builder, builder.addIf(true, "condKey", "value"));
		assertSame(builder, builder.addIf(true, "condBoolKey", true));
		assertSame(builder, builder.addIf(true, "condNumKey", 42));
		assertSame(builder, builder.startTable("nested"));
		assertSame(builder, builder.endTable());
		assertSame(builder, builder.startInlineTable("inline"));
		assertSame(builder, builder.endInlineTable());
		assertSame(builder, builder.addArrayOfTables("array", new TomlTable()));
		assertSame(builder, builder.addArrayOfTables("arrayList", List.of()));
	}
	
	//endregion
	
	//region Array Builder Tests
	
	@Test
	void existingNestedTableExtension() {
		TomlTable table = TomlBuilder.table()
			.startTable("nested")
			.add("key1", "value1")
			.endTable()
			.startTable("nested")
			.add("key2", "value2")
			.endTable()
			.build();
		
		TomlTable nested = table.getTomlTable("nested");
		assertEquals(2, nested.size());
		assertTrue(nested.containsKey("key1"));
		assertTrue(nested.containsKey("key2"));
	}
	
	@Test
	void createEmptyArray() {
		TomlArray array = TomlBuilder.array().build();
		assertTrue(array.isEmpty());
		assertEquals(0, array.size());
	}
	
	@Test
	void buildSimpleArray() {
		TomlArray array = TomlBuilder.array()
			.add("string")
			.add(42)
			.add(true)
			.build();
		
		assertEquals(3, array.size());
		assertEquals("string", array.getAsString(0));
		assertEquals(42, array.getAsInteger(1));
		assertTrue(array.getAsBoolean(2));
	}
	
	@Test
	void arrayAddAllPrimitiveTypes() {
		TomlArray array = TomlBuilder.array()
			.add("testString")
			.add(true)
			.add(42)
			.add(3.14)
			.add((String) null)
			.add((Number) null)
			.build();
		
		assertEquals(6, array.size());
		assertEquals("testString", array.getAsString(0));
		assertTrue(array.getAsBoolean(1));
		assertEquals(42, array.getAsInteger(2));
		assertEquals(3.14, array.getAsDouble(3));
		assertEquals(TomlNull.INSTANCE, array.get(4));
		assertEquals(TomlNull.INSTANCE, array.get(5));
	}
	
	@Test
	void arrayAddDateTimeTypes() {
		LocalDate date = LocalDate.of(2025, 1, 15);
		LocalTime time = LocalTime.of(14, 30, 45);
		LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 14, 30, 45);
		OffsetDateTime offsetDateTime = OffsetDateTime.of(2025, 1, 15, 14, 30, 45, 0, ZoneOffset.UTC);
		
		TomlArray array = TomlBuilder.array()
			.add(date)
			.add(time)
			.add(dateTime)
			.add(offsetDateTime)
			.build();
		
		assertEquals(4, array.size());
		assertEquals(date, array.getAsLocalDate(0));
		// Note: LocalTime getter might not exist, using getValue
		assertEquals(new TomlValue(time), array.get(1));
		assertEquals(dateTime, array.getAsLocalDateTime(2));
		assertEquals(offsetDateTime, array.getAsOffsetDateTime(3));
	}
	
	@Test
	void arrayAddTomlElement() {
		TomlArray array = TomlBuilder.array()
			.add(new TomlValue("element"))
			.add((TomlElement) null)
			.add(new TomlArray())
			.add(new TomlTable())
			.build();
		
		assertEquals(4, array.size());
		assertEquals(new TomlValue("element"), array.get(0));
		assertEquals(TomlNull.INSTANCE, array.get(1));
	}
	
	@Test
	void arrayAddAll() {
		TomlArray other = new TomlArray();
		other.add(new TomlValue(1));
		other.add(new TomlValue(2));
		
		TomlArray array = TomlBuilder.array()
			.add(0)
			.addAll(other)
			.add(3)
			.build();
		
		assertEquals(4, array.size());
		assertEquals(0, array.getAsInteger(0));
		assertEquals(1, array.getAsInteger(1));
		assertEquals(2, array.getAsInteger(2));
		assertEquals(3, array.getAsInteger(3));
	}
	
	@Test
	void arrayAddAllNullValidation() {
		TomlBuilder.ArrayBuilder builder = TomlBuilder.array();
		assertThrows(NullPointerException.class, () -> builder.addAll(null));
	}
	
	@Test
	void arrayConditionalAdding() {
		TomlArray array = TomlBuilder.array()
			.addIf(true, "included")
			.addIf(false, "excluded")
			.build();
		
		assertEquals(1, array.size());
		assertEquals("included", array.getAsString(0));
	}
	
	@Test
	void arrayAsArrayOfTables() {
		TomlArray array = TomlBuilder.array()
			.add(new TomlTable())
			.add(new TomlTable())
			.asArrayOfTables()
			.build();
		
		assertTrue(array.isArrayOfTables());
		assertEquals(2, array.size());
	}
	
	//endregion
	
	//region Inline Table Builder Tests
	
	@Test
	void arrayMethodChainingConsistency() {
		TomlBuilder.ArrayBuilder builder = TomlBuilder.array();
		
		assertSame(builder, builder.add("string"));
		assertSame(builder, builder.add(true));
		assertSame(builder, builder.add(42));
		assertSame(builder, builder.add(LocalDate.now()));
		assertSame(builder, builder.add(LocalTime.now()));
		assertSame(builder, builder.add(LocalDateTime.now()));
		assertSame(builder, builder.add(OffsetDateTime.now()));
		assertSame(builder, builder.add((TomlElement) null));
		assertSame(builder, builder.addAll(new TomlArray()));
		assertSame(builder, builder.addIf(true, "conditional"));
		assertSame(builder, builder.asArrayOfTables());
	}
	
	@Test
	void createEmptyInlineTable() {
		TomlTable table = TomlBuilder.inlineTable().build();
		assertTrue(table.isEmpty());
		assertTrue(table.isInline());
	}
	
	@Test
	void buildSimpleInlineTable() {
		TomlTable table = TomlBuilder.inlineTable()
			.add("key1", "value1")
			.add("key2", 42)
			.add("key3", true)
			.build();
		
		assertTrue(table.isInline());
		assertEquals(3, table.size());
		assertEquals("value1", table.getAsString("key1"));
		assertEquals(42, table.getAsInteger("key2"));
		assertTrue(table.getAsBoolean("key3"));
	}
	
	@Test
	void inlineTableAddAllTypes() {
		TomlTable table = TomlBuilder.inlineTable()
			.add("stringKey", "testString")
			.add("booleanKey", true)
			.add("numberKey", 42)
			.add("nullKey", (String) null)
			.add("elementKey", new TomlValue("element"))
			.build();
		
		assertTrue(table.isInline());
		assertEquals(5, table.size());
		assertEquals("testString", table.getAsString("stringKey"));
		assertTrue(table.getAsBoolean("booleanKey"));
		assertEquals(42, table.getAsInteger("numberKey"));
		assertEquals(TomlNull.INSTANCE, table.get("nullKey"));
		assertEquals(new TomlValue("element"), table.get("elementKey"));
	}
	
	@Test
	void inlineTableNullKeyValidation() {
		TomlBuilder.InlineTableBuilder builder = TomlBuilder.inlineTable();
		assertThrows(NullPointerException.class, () -> builder.add(null, "value"));
		assertThrows(NullPointerException.class, () -> builder.add(null, true));
		assertThrows(NullPointerException.class, () -> builder.add(null, 42));
		assertThrows(NullPointerException.class, () -> builder.add(null, (TomlElement) null));
	}
	
	//endregion
	
	@Test
	void inlineTableMethodChainingConsistency() {
		TomlBuilder.InlineTableBuilder builder = TomlBuilder.inlineTable();
		
		assertSame(builder, builder.add("stringKey", "value"));
		assertSame(builder, builder.add("boolKey", true));
		assertSame(builder, builder.add("numKey", 42));
		assertSame(builder, builder.add("elementKey", (TomlElement) null));
	}
	
	@Test
	void complexRealWorldExample() {
		TomlTable config = TomlBuilder.table()
			.add("title", "TOML Application Config")
			.add("version", 1)
			.startTable("database")
			.add("host", "localhost")
			.add("port", 5432)
			.add("name", "mydb")
			.add("enabled", true)
			.add("connectionTimeout", 30)
			.endTable()
			.startTable("server")
			.add("host", "0.0.0.0")
			.add("ports", TomlBuilder.array()
				.add(8080)
				.add(8443)
				.build())
			.startInlineTable("ssl")
			.add("enabled", true)
			.add("cert", "/path/to/cert")
			.endInlineTable()
			.endTable()
			.addArrayOfTables("workers",
				createWorkerTable("worker1", 80),
				createWorkerTable("worker2", 90))
			.startTable("logging")
			.add("level", "INFO")
			.add("file", "/var/log/app.log")
			.addIf(true, "console", true)
			.addIf(false, "syslog", false)
			.endTable()
			.build();
		
		assertEquals("TOML Application Config", config.getAsString("title"));
		assertEquals(1, config.getAsInteger("version"));
		
		TomlTable database = config.getTomlTable("database");
		assertEquals("localhost", database.getAsString("host"));
		assertEquals(5432, database.getAsInteger("port"));
		assertTrue(database.getAsBoolean("enabled"));
		
		TomlTable server = config.getTomlTable("server");
		TomlArray ports = server.getTomlArray("ports");
		assertEquals(2, ports.size());
		assertEquals(8080, ports.getAsInteger(0));
		assertEquals(8443, ports.getAsInteger(1));
		
		TomlTable ssl = server.getTomlTable("ssl");
		assertTrue(ssl.isInline());
		assertTrue(ssl.getAsBoolean("enabled"));
		
		TomlArray workers = config.getTomlArray("workers");
		assertTrue(workers.isArrayOfTables());
		assertEquals(2, workers.size());
		assertEquals("worker1", workers.getAsTomlTable(0).getAsString("name"));
		assertEquals(80, workers.getAsTomlTable(0).getAsInteger("priority"));
		
		TomlTable logging = config.getTomlTable("logging");
		assertEquals("INFO", logging.getAsString("level"));
		assertTrue(logging.containsKey("console"));
		assertFalse(logging.containsKey("syslog"));
	}
}
