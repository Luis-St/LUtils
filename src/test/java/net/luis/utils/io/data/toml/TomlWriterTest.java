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

import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlWriter}.<br>
 *
 * @author Luis-St
 */
class TomlWriterTest {
	
	private static final TomlConfig CUSTOM_CONFIG = new TomlConfig(
		true, true, "\t",
		false, 3, true, 5, false, 80, false,
		TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
	);
	
	private static final TomlConfig NO_PRETTY_CONFIG = new TomlConfig(
		true, false, "\t",
		false, 3, true, 5, false, 80, false,
		TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
	);
	
	@Test
	void constructorValidation() {
		assertThrows(NullPointerException.class, () -> new TomlWriter(null));
		assertThrows(NullPointerException.class, () -> new TomlWriter(new OutputProvider(new ByteArrayOutputStream()), null));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assertDoesNotThrow(() -> new TomlWriter(new OutputProvider(output)));
		assertDoesNotThrow(() -> new TomlWriter(new OutputProvider(new ByteArrayOutputStream()), CUSTOM_CONFIG));
	}
	
	@Test
	void writeTomlEmptyTable() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(new TomlTable());
		}
		
		assertEquals("", output.toString(StandardCharsets.UTF_8));
	}
	
	@Test
	void writeTomlNullValidation() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			assertThrows(NullPointerException.class, () -> writer.writeToml(null));
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void writeTomlWithKeyValues() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TomlTable table = new TomlTable();
		table.add("key1", new TomlValue("value1"));
		table.add("key2", new TomlValue(42));
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(table);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("value1"));
		assertTrue(result.contains("key2"));
		assertTrue(result.contains("42"));
	}
	
	@Test
	void writeTomlWithTable() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TomlTable root = new TomlTable();
		TomlTable section = new TomlTable();
		section.add("key", new TomlValue("value"));
		root.add("myTable", section);
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(root);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("[myTable]"));
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void writePropertyValidation() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			assertThrows(NullPointerException.class, () -> writer.writeProperty(null, new TomlValue("value")));
			assertThrows(NullPointerException.class, () -> writer.writeProperty("key", (TomlElement) null));
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void writePropertyWithTomlElement() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeProperty("stringKey", new TomlValue("stringValue"));
			writer.writeProperty("numberKey", new TomlValue(42));
			writer.writeProperty("booleanKey", new TomlValue(true));
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("stringKey"));
		assertTrue(result.contains("stringValue"));
		assertTrue(result.contains("numberKey"));
		assertTrue(result.contains("42"));
		assertTrue(result.contains("booleanKey"));
		assertTrue(result.contains("true"));
	}
	
	@Test
	void writePropertyWithString() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeProperty("key", "value");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void writePropertyWithNullString() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeProperty("key", (String) null);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("key"));
		// TOML null is represented as empty string
		assertTrue(result.contains("key = "));
	}
	
	@Test
	void writePropertyWithBoolean() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeProperty("trueKey", true);
			writer.writeProperty("falseKey", false);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("trueKey"));
		assertTrue(result.contains("true"));
		assertTrue(result.contains("falseKey"));
		assertTrue(result.contains("false"));
	}
	
	@Test
	void writePropertyWithNumber() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeProperty("intKey", 42);
			writer.writeProperty("doubleKey", 3.14);
			writer.writeProperty("nullKey", (Number) null);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("intKey"));
		assertTrue(result.contains("42"));
		assertTrue(result.contains("doubleKey"));
		assertTrue(result.contains("3.14"));
		assertTrue(result.contains("nullKey"));
		assertTrue(result.contains("null"));
	}
	
	@Test
	void writePropertyWithLocalDate() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeProperty("date", LocalDate.of(2024, 1, 15));
			writer.writeProperty("nullDate", (LocalDate) null);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("date"));
		assertTrue(result.contains("2024-01-15"));
		assertTrue(result.contains("nullDate"));
	}
	
	@Test
	void writePropertyWithLocalTime() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeProperty("time", LocalTime.of(14, 30, 45));
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("time"));
		assertTrue(result.contains("14:30:45"));
	}
	
	@Test
	void writePropertyWithLocalDateTime() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeProperty("datetime", LocalDateTime.of(2024, 1, 15, 14, 30, 45));
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("datetime"));
		assertTrue(result.contains("2024-01-15"));
		assertTrue(result.contains("14:30:45"));
	}
	
	@Test
	void writePropertyWithOffsetDateTime() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeProperty("datetime", OffsetDateTime.of(2024, 1, 15, 14, 30, 0, 0, ZoneOffset.UTC));
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("datetime"));
		assertTrue(result.contains("2024-01-15"));
	}
	
	@Test
	void writeTableHeaderValidation() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			assertThrows(NullPointerException.class, () -> writer.writeTableHeader(null));
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void writeTableHeader() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output), NO_PRETTY_CONFIG)) {
			writer.writeTableHeader("myTable");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("[myTable]"));
	}
	
	@Test
	void writeArrayOfTablesHeaderValidation() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			assertThrows(NullPointerException.class, () -> writer.writeArrayOfTablesHeader(null));
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void writeArrayOfTablesHeader() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output), NO_PRETTY_CONFIG)) {
			writer.writeArrayOfTablesHeader("products");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("[[products]]"));
	}
	
	@Test
	void writeCommentValidation() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			assertThrows(NullPointerException.class, () -> writer.writeComment(null));
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void writeComment() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeComment("This is a comment");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("# This is a comment"));
	}
	
	@Test
	void writeBlankLine() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeProperty("key1", "value1");
			writer.writeBlankLine();
			writer.writeProperty("key2", "value2");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("key2"));
	}
	
	@Test
	void closeResource() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TomlWriter writer = new TomlWriter(new OutputProvider(output));
		writer.writeProperty("key", "value");
		assertDoesNotThrow(writer::close);
	}
	
	@Test
	void writeTomlWithInlineTable() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TomlTable root = new TomlTable();
		TomlTable inline = new TomlTable();
		inline.setInline(true);
		inline.add("x", new TomlValue(1));
		inline.add("y", new TomlValue(2));
		root.add("point", inline);
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(root);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("point"));
		assertTrue(result.contains("{"));
		assertTrue(result.contains("}"));
	}
	
	@Test
	void writeTomlWithArray() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TomlTable root = new TomlTable();
		TomlArray array = new TomlArray();
		array.add(new TomlValue(1));
		array.add(new TomlValue(2));
		array.add(new TomlValue(3));
		root.add("numbers", array);
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(root);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("numbers"));
		assertTrue(result.contains("["));
		assertTrue(result.contains("]"));
		assertTrue(result.contains("1"));
		assertTrue(result.contains("2"));
		assertTrue(result.contains("3"));
	}
	
	@Test
	void writeTomlWithArrayOfTables() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TomlTable root = new TomlTable();
		TomlArray products = new TomlArray();
		products.setArrayOfTables(true);
		
		TomlTable product1 = new TomlTable();
		product1.add("name", new TomlValue("Product 1"));
		product1.add("price", new TomlValue(10.0));
		products.add(product1);
		
		TomlTable product2 = new TomlTable();
		product2.add("name", new TomlValue("Product 2"));
		product2.add("price", new TomlValue(20.0));
		products.add(product2);
		
		root.add("products", products);
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(root);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("[[products]]"));
		assertTrue(result.contains("Product 1"));
		assertTrue(result.contains("Product 2"));
	}
	
	@Test
	void writeTomlComplexDocument() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TomlTable root = new TomlTable();
		
		root.add("appName", new TomlValue("MyApplication"));
		root.add("version", new TomlValue(1));
		root.add("debug", new TomlValue(true));
		
		TomlTable database = new TomlTable();
		database.add("host", new TomlValue("localhost"));
		database.add("port", new TomlValue(5432));
		database.add("name", new TomlValue("mydb"));
		root.add("database", database);
		
		TomlTable logging = new TomlTable();
		logging.add("level", new TomlValue("INFO"));
		logging.add("file", new TomlValue("/var/log/app.log"));
		logging.add("console", new TomlValue(true));
		root.add("logging", logging);
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(root);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("appName"));
		assertTrue(result.contains("MyApplication"));
		assertTrue(result.contains("version"));
		assertTrue(result.contains("debug"));
		assertTrue(result.contains("[database]"));
		assertTrue(result.contains("host"));
		assertTrue(result.contains("localhost"));
		assertTrue(result.contains("[logging]"));
		assertTrue(result.contains("level"));
		assertTrue(result.contains("INFO"));
	}
	
	@Test
	void writeMultipleProperties() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output), NO_PRETTY_CONFIG)) {
			writer.writeComment("Configuration file");
			writer.writeProperty("key1", "value1");
			writer.writeProperty("key2", 42);
			writer.writeBlankLine();
			writer.writeTableHeader("section");
			writer.writeProperty("sectionKey", "sectionValue");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("# Configuration file"));
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("value1"));
		assertTrue(result.contains("key2"));
		assertTrue(result.contains("42"));
		assertTrue(result.contains("[section]"));
		assertTrue(result.contains("sectionKey"));
		assertTrue(result.contains("sectionValue"));
	}
	
	@Test
	void writeWithDifferentConfigs() throws IOException {
		TomlTable table = new TomlTable();
		table.add("key", new TomlValue("value"));
		
		ByteArrayOutputStream output1 = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output1), CUSTOM_CONFIG)) {
			writer.writeToml(table);
		}
		
		ByteArrayOutputStream output2 = new ByteArrayOutputStream();
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output2), NO_PRETTY_CONFIG)) {
			writer.writeToml(table);
		}
		
		String result1 = output1.toString(StandardCharsets.UTF_8);
		String result2 = output2.toString(StandardCharsets.UTF_8);
		
		assertTrue(result1.contains("key"));
		assertTrue(result2.contains("key"));
	}
	
	@Test
	void writeNestedTables() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TomlTable root = new TomlTable();
		TomlTable server = new TomlTable();
		TomlTable database = new TomlTable();
		database.add("host", new TomlValue("localhost"));
		database.add("port", new TomlValue(5432));
		server.add("database", database);
		root.add("server", server);
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(root);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("[server.database]") || result.contains("server") && result.contains("database"));
		assertTrue(result.contains("localhost"));
		assertTrue(result.contains("5432"));
	}
	
	@Test
	void writeQuotedKey() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		TomlTable table = new TomlTable();
		table.add("key with spaces", new TomlValue("value"));
		
		try (TomlWriter writer = new TomlWriter(new OutputProvider(output))) {
			writer.writeToml(table);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("\"key with spaces\""));
		assertTrue(result.contains("value"));
	}
}
