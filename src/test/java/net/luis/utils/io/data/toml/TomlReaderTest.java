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

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.toml.exception.TomlSyntaxException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlReader}.<br>
 *
 * @author Luis-St
 */
class TomlReaderTest {
	
	private static final TomlConfig STRICT_CONFIG = new TomlConfig(
		true, true, "\t",
		false, 3, true, 5, false, 80, false,
		TomlConfig.DateTimeStyle.RFC_3339, false, StandardCharsets.UTF_8
	);
	
	private static final TomlConfig NON_STRICT_CONFIG = new TomlConfig(
		false, true, "\t",
		false, 3, true, 5, false, 80, false,
		TomlConfig.DateTimeStyle.RFC_3339, true, StandardCharsets.UTF_8
	);
	
	@Test
	void constructorWithString() {
		assertThrows(NullPointerException.class, () -> new TomlReader((String) null));
		assertThrows(NullPointerException.class, () -> new TomlReader("content", null));
		
		assertDoesNotThrow(() -> new TomlReader(""));
		assertDoesNotThrow(() -> new TomlReader("key = \"value\""));
		assertDoesNotThrow(() -> new TomlReader("key = \"value\"", STRICT_CONFIG));
	}
	
	@Test
	void constructorWithInputProvider() {
		assertThrows(NullPointerException.class, () -> new TomlReader((InputProvider) null));
		assertThrows(NullPointerException.class, () -> new TomlReader(new InputProvider(new ByteArrayInputStream(new byte[0])), null));
		
		InputProvider provider = new InputProvider(new ByteArrayInputStream("key = \"value\"".getBytes(StandardCharsets.UTF_8)));
		assertDoesNotThrow(() -> new TomlReader(provider));
		
		InputProvider provider2 = new InputProvider(new ByteArrayInputStream("key = \"value\"".getBytes(StandardCharsets.UTF_8)));
		assertDoesNotThrow(() -> new TomlReader(provider2, STRICT_CONFIG));
	}
	
	@Test
	void readTomlEmptyInput() throws IOException {
		try (TomlReader reader = new TomlReader("")) {
			TomlTable table = reader.readToml();
			assertTrue(table.isEmpty());
		}
	}
	
	@Test
	void readTomlSingleKeyValue() throws IOException {
		try (TomlReader reader = new TomlReader("key = \"value\"")) {
			TomlTable table = reader.readToml();
			assertEquals(1, table.size());
			assertEquals("value", table.getAsString("key"));
		}
	}
	
	@Test
	void readTomlMultipleKeyValues() throws IOException {
		String toml = """
			key1 = "value1"
			key2 = "value2"
			key3 = "value3"
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(3, table.size());
			assertEquals("value1", table.getAsString("key1"));
			assertEquals("value2", table.getAsString("key2"));
			assertEquals("value3", table.getAsString("key3"));
		}
	}
	
	@Test
	void readTomlBooleanValues() throws IOException {
		String toml = """
			trueKey = true
			falseKey = false
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertTrue(table.getAsBoolean("trueKey"));
			assertFalse(table.getAsBoolean("falseKey"));
		}
	}
	
	@Test
	void readTomlIntegerValues() throws IOException {
		String toml = """
			int = 42
			negative = -17
			zero = 0
			large = 1_000_000
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(42, table.getAsInteger("int"));
			assertEquals(-17, table.getAsInteger("negative"));
			assertEquals(0, table.getAsInteger("zero"));
			assertEquals(1000000, table.getAsInteger("large"));
		}
	}
	
	@Test
	void readTomlHexOctalBinaryIntegers() throws IOException {
		String toml = """
			hex = 0xFF
			octal = 0o755
			binary = 0b1010
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(255, table.getAsInteger("hex"));
			assertEquals(493, table.getAsInteger("octal"));
			assertEquals(10, table.getAsInteger("binary"));
		}
	}
	
	@Test
	void readTomlFloatValues() throws IOException {
		String toml = """
			float = 3.14
			negative = -2.5
			exponent = 1e10
			negativeExponent = 6.02e-23
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(3.14, table.getAsDouble("float"), 0.001);
			assertEquals(-2.5, table.getAsDouble("negative"), 0.001);
			assertEquals(1.0e10, table.getAsDouble("exponent"), 1.0e5);
			assertEquals(6.02e-23, table.getAsDouble("negativeExponent"), 1.0e-28);
		}
	}
	
	@Test
	void readTomlSpecialFloatValues() throws IOException {
		String toml = """
			inf1 = inf
			inf2 = +inf
			negInf = -inf
			nan1 = nan
			nan2 = +nan
			nan3 = -nan
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(Double.POSITIVE_INFINITY, table.getAsDouble("inf1"));
			assertEquals(Double.POSITIVE_INFINITY, table.getAsDouble("inf2"));
			assertEquals(Double.NEGATIVE_INFINITY, table.getAsDouble("negInf"));
			assertTrue(Double.isNaN(table.getAsDouble("nan1")));
			assertTrue(Double.isNaN(table.getAsDouble("nan2")));
			assertTrue(Double.isNaN(table.getAsDouble("nan3")));
		}
	}
	
	@Test
	void readTomlBasicStrings() throws IOException {
		String toml = """
			simple = "hello"
			withSpaces = "hello world"
			withQuotes = "he said \\"hello\\""
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals("hello", table.getAsString("simple"));
			assertEquals("hello world", table.getAsString("withSpaces"));
			assertEquals("he said \"hello\"", table.getAsString("withQuotes"));
		}
	}
	
	@Test
	void readTomlLiteralStrings() throws IOException {
		String toml = """
			simple = 'hello'
			withBackslash = 'C:\\Users\\name'
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals("hello", table.getAsString("simple"));
			assertEquals("C:\\Users\\name", table.getAsString("withBackslash"));
		}
	}
	
	@Test
	void readTomlEscapeSequences() throws IOException {
		String toml = """
			newline = "line1\\nline2"
			tab = "col1\\tcol2"
			backslash = "path\\\\file"
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals("line1\nline2", table.getAsString("newline"));
			assertEquals("col1\tcol2", table.getAsString("tab"));
			assertEquals("path\\file", table.getAsString("backslash"));
		}
	}
	
	@Test
	void readTomlMultiLineBasicString() throws IOException {
		String toml = """
			text = \"""
			Hello
			World\"""
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals("Hello\nWorld", table.getAsString("text"));
		}
	}
	
	@Test
	void readTomlMultiLineLiteralString() throws IOException {
		String toml = """
			text = '''
			Hello
			World'''
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals("Hello\nWorld", table.getAsString("text"));
		}
	}
	
	@Test
	void readTomlLocalDate() throws IOException {
		String toml = "date = 2024-01-15";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(LocalDate.of(2024, 1, 15), table.getAsLocalDate("date"));
		}
	}
	
	@Test
	void readTomlLocalTime() throws IOException {
		String toml = "time = 14:30:00";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(LocalTime.of(14, 30, 0), table.getAsLocalTime("time"));
		}
	}
	
	@Test
	void readTomlLocalDateTime() throws IOException {
		String toml = "datetime = 2024-01-15T14:30:00";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(LocalDateTime.of(2024, 1, 15, 14, 30, 0), table.getAsLocalDateTime("datetime"));
		}
	}
	
	@Test
	void readTomlOffsetDateTime() throws IOException {
		String toml = "datetime = 2024-01-15T14:30:00Z";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(OffsetDateTime.of(2024, 1, 15, 14, 30, 0, 0, ZoneOffset.UTC), table.getAsOffsetDateTime("datetime"));
		}
	}
	
	@Test
	void readTomlSimpleTable() throws IOException {
		String toml = """
			[server]
			host = "localhost"
			port = 8080
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable root = reader.readToml();
			TomlTable server = root.getTomlTable("server");
			assertNotNull(server);
			assertEquals("localhost", server.getAsString("host"));
			assertEquals(8080, server.getAsInteger("port"));
		}
	}
	
	@Test
	void readTomlNestedTable() throws IOException {
		String toml = """
			[server.database]
			host = "localhost"
			port = 5432
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable root = reader.readToml();
			TomlTable server = root.getTomlTable("server");
			TomlTable database = server.getTomlTable("database");
			assertEquals("localhost", database.getAsString("host"));
			assertEquals(5432, database.getAsInteger("port"));
		}
	}
	
	@Test
	void readTomlMultipleTables() throws IOException {
		String toml = """
			[table1]
			key1 = "value1"
			
			[table2]
			key2 = "value2"
			
			[table3]
			key3 = "value3"
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable root = reader.readToml();
			assertEquals(3, root.size());
			assertEquals("value1", root.getTomlTable("table1").getAsString("key1"));
			assertEquals("value2", root.getTomlTable("table2").getAsString("key2"));
			assertEquals("value3", root.getTomlTable("table3").getAsString("key3"));
		}
	}
	
	@Test
	void readTomlInlineTable() throws IOException {
		String toml = "point = { x = 1, y = 2 }";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable root = reader.readToml();
			TomlTable point = root.getTomlTable("point");
			assertTrue(point.isInline());
			assertEquals(1, point.getAsInteger("x"));
			assertEquals(2, point.getAsInteger("y"));
		}
	}
	
	@Test
	void readTomlSimpleArray() throws IOException {
		String toml = "numbers = [1, 2, 3, 4, 5]";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable root = reader.readToml();
			TomlArray numbers = root.getTomlArray("numbers");
			assertEquals(5, numbers.size());
			assertEquals(1, numbers.getAsInteger(0));
			assertEquals(5, numbers.getAsInteger(4));
		}
	}
	
	@Test
	void readTomlMixedArray() throws IOException {
		String toml = "mixed = [\"string\", 42, true, 3.14]";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable root = reader.readToml();
			TomlArray mixed = root.getTomlArray("mixed");
			assertEquals(4, mixed.size());
			assertEquals("string", mixed.getAsString(0));
			assertEquals(42, mixed.getAsInteger(1));
			assertTrue(mixed.getAsBoolean(2));
			assertEquals(3.14, mixed.getAsDouble(3), 0.001);
		}
	}
	
	@Test
	void readTomlNestedArray() throws IOException {
		String toml = "nested = [[1, 2], [3, 4], [5, 6]]";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable root = reader.readToml();
			TomlArray nested = root.getTomlArray("nested");
			assertEquals(3, nested.size());
			assertEquals(2, nested.getAsTomlArray(0).size());
			assertEquals(1, nested.getAsTomlArray(0).getAsInteger(0));
		}
	}
	
	@Test
	void readTomlArrayOfTables() throws IOException {
		String toml = """
			[[products]]
			name = "Product 1"
			price = 10.0
			
			[[products]]
			name = "Product 2"
			price = 20.0
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable root = reader.readToml();
			TomlArray products = root.getTomlArray("products");
			assertTrue(products.isArrayOfTables());
			assertEquals(2, products.size());
			assertEquals("Product 1", products.getAsTomlTable(0).getAsString("name"));
			assertEquals(10.0, products.getAsTomlTable(0).getAsDouble("price"), 0.001);
			assertEquals("Product 2", products.getAsTomlTable(1).getAsString("name"));
		}
	}
	
	@Test
	void readTomlDottedKeys() throws IOException {
		String toml = "physical.color = \"red\"";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable root = reader.readToml();
			TomlTable physical = root.getTomlTable("physical");
			assertEquals("red", physical.getAsString("color"));
		}
	}
	
	@Test
	void readTomlCommentsIgnored() throws IOException {
		String toml = """
			# This is a comment
			key1 = "value1"
			key2 = "value2" # inline comment
			# Another comment
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(2, table.size());
			assertEquals("value1", table.getAsString("key1"));
			assertEquals("value2", table.getAsString("key2"));
		}
	}
	
	@Test
	void readTomlEmptyLinesIgnored() throws IOException {
		String toml = """
			
			key1 = "value1"
			
			
			key2 = "value2"
			
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals(2, table.size());
		}
	}
	
	@Test
	void readTomlInvalidSyntaxMissingValue() {
		String toml = "key = ";
		
		try (TomlReader reader = new TomlReader(toml)) {
			assertThrows(TomlSyntaxException.class, reader::readToml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readTomlInvalidSyntaxMissingEquals() {
		String toml = "key value";
		
		try (TomlReader reader = new TomlReader(toml)) {
			assertThrows(TomlSyntaxException.class, reader::readToml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readTomlInvalidSyntaxUnterminatedString() {
		String toml = "key = \"unterminated";
		
		try (TomlReader reader = new TomlReader(toml)) {
			assertThrows(TomlSyntaxException.class, reader::readToml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readTomlInvalidSyntaxMissingClosingBracket() {
		String toml = "[section";
		
		try (TomlReader reader = new TomlReader(toml)) {
			assertThrows(TomlSyntaxException.class, reader::readToml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readTomlInvalidSyntaxEmptyKey() {
		String toml = " = \"value\"";
		
		try (TomlReader reader = new TomlReader(toml)) {
			assertThrows(TomlSyntaxException.class, reader::readToml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readTomlDuplicateKeyStrict() {
		String toml = """
			key = "value1"
			key = "value2"
			""";
		
		try (TomlReader reader = new TomlReader(toml, STRICT_CONFIG)) {
			assertThrows(TomlSyntaxException.class, reader::readToml);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readTomlDuplicateKeyNonStrict() throws IOException {
		String toml = """
			key = "value1"
			key = "value2"
			""";
		
		try (TomlReader reader = new TomlReader(toml, NON_STRICT_CONFIG)) {
			TomlTable table = reader.readToml();
			assertEquals("value2", table.getAsString("key"));
		}
	}
	
	@Test
	void readTomlWithInputProvider() throws IOException {
		String toml = """
			[server]
			host = "localhost"
			""";
		
		InputProvider provider = new InputProvider(new ByteArrayInputStream(toml.getBytes(StandardCharsets.UTF_8)));
		
		try (TomlReader reader = new TomlReader(provider)) {
			TomlTable table = reader.readToml();
			assertEquals("localhost", table.getTomlTable("server").getAsString("host"));
		}
	}
	
	@Test
	void readTomlWhitespaceTrimming() throws IOException {
		String toml = """
			  key1  =  "value1"
			[  table  ]
			  key2  =  "value2"
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable table = reader.readToml();
			assertEquals("value1", table.getAsString("key1"));
			assertEquals("value2", table.getTomlTable("table").getAsString("key2"));
		}
	}
	
	@Test
	void closeResource() throws IOException {
		TomlReader reader = new TomlReader("key = \"value\"");
		reader.readToml();
		assertDoesNotThrow(reader::close);
	}
	
	@Test
	void readTomlComplexDocument() throws IOException {
		String toml = """
			# Application Configuration
			title = "MyApplication"
			version = "1.0.0"
			debug = true
			
			[database]
			server = "192.168.1.1"
			ports = [8001, 8002, 8003]
			connection_max = 5000
			enabled = true
			
			[servers.alpha]
			ip = "10.0.0.1"
			role = "frontend"
			
			[servers.beta]
			ip = "10.0.0.2"
			role = "backend"
			
			[[clients]]
			name = "Client A"
			enabled = true
			
			[[clients]]
			name = "Client B"
			enabled = false
			""";
		
		try (TomlReader reader = new TomlReader(toml)) {
			TomlTable root = reader.readToml();
			
			assertEquals("MyApplication", root.getAsString("title"));
			assertEquals("1.0.0", root.getAsString("version"));
			assertTrue(root.getAsBoolean("debug"));
			
			TomlTable database = root.getTomlTable("database");
			assertEquals("192.168.1.1", database.getAsString("server"));
			assertEquals(3, database.getTomlArray("ports").size());
			assertEquals(5000, database.getAsInteger("connection_max"));
			
			TomlTable servers = root.getTomlTable("servers");
			assertEquals("10.0.0.1", servers.getTomlTable("alpha").getAsString("ip"));
			assertEquals("10.0.0.2", servers.getTomlTable("beta").getAsString("ip"));
			
			TomlArray clients = root.getTomlArray("clients");
			assertEquals(2, clients.size());
			assertEquals("Client A", clients.getAsTomlTable(0).getAsString("name"));
			assertEquals("Client B", clients.getAsTomlTable(1).getAsString("name"));
		}
	}
}
