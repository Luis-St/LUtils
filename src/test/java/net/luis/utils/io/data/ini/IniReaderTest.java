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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.ini.exception.IniSyntaxException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IniReader}.<br>
 *
 * @author Luis-St
 */
class IniReaderTest {
	
	private static final IniConfig STRICT_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, true,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
	);
	
	private static final IniConfig NON_STRICT_CONFIG = new IniConfig(
		false, true, "\t", Set.of(';', '#'), '=', 1,
		true, true, true,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
	);
	
	private static final IniConfig PARSE_TYPES_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, true,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
	);
	
	@Test
	void constructorWithString() {
		assertThrows(NullPointerException.class, () -> new IniReader((String) null));
		assertThrows(NullPointerException.class, () -> new IniReader("content", null));
		
		assertDoesNotThrow(() -> new IniReader(""));
		assertDoesNotThrow(() -> new IniReader("key = value"));
		assertDoesNotThrow(() -> new IniReader("key = value", STRICT_CONFIG));
	}
	
	@Test
	void constructorWithInputProvider() {
		assertThrows(NullPointerException.class, () -> new IniReader((InputProvider) null));
		assertThrows(NullPointerException.class, () -> new IniReader(new InputProvider(new ByteArrayInputStream(new byte[0])), null));
		
		InputProvider provider = new InputProvider(new ByteArrayInputStream("key = value".getBytes(StandardCharsets.UTF_8)));
		assertDoesNotThrow(() -> new IniReader(provider));
		
		InputProvider provider2 = new InputProvider(new ByteArrayInputStream("key = value".getBytes(StandardCharsets.UTF_8)));
		assertDoesNotThrow(() -> new IniReader(provider2, STRICT_CONFIG));
	}
	
	@Test
	void readIniEmptyInput() throws IOException {
		try (IniReader reader = new IniReader("")) {
			IniDocument document = reader.readIni();
			assertTrue(document.isEmpty());
			assertFalse(document.hasGlobalProperties());
			assertFalse(document.hasSections());
		}
	}
	
	@Test
	void readIniSingleGlobalProperty() throws IOException {
		try (IniReader reader = new IniReader("key = value")) {
			IniDocument document = reader.readIni();
			assertTrue(document.hasGlobalProperties());
			assertEquals(1, document.globalSize());
			assertEquals("value", document.getGlobalAsString("key"));
		}
	}
	
	@Test
	void readIniMultipleGlobalProperties() throws IOException {
		String ini = """
			key1 = value1
			key2 = value2
			key3 = value3
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertEquals(3, document.globalSize());
			assertEquals("value1", document.getGlobalAsString("key1"));
			assertEquals("value2", document.getGlobalAsString("key2"));
			assertEquals("value3", document.getGlobalAsString("key3"));
		}
	}
	
	@Test
	void readIniSingleSection() throws IOException {
		String ini = """
			[section]
			key = value
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertFalse(document.hasGlobalProperties());
			assertTrue(document.hasSections());
			assertEquals(1, document.sectionCount());
			
			IniSection section = document.requireSection("section");
			assertEquals(1, section.size());
			assertEquals("value", section.getAsString("key"));
		}
	}
	
	@Test
	void readIniMultipleSections() throws IOException {
		String ini = """
			[section1]
			key1 = value1
			
			[section2]
			key2 = value2
			
			[section3]
			key3 = value3
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertEquals(3, document.sectionCount());
			assertEquals("value1", document.requireSection("section1").getAsString("key1"));
			assertEquals("value2", document.requireSection("section2").getAsString("key2"));
			assertEquals("value3", document.requireSection("section3").getAsString("key3"));
		}
	}
	
	@Test
	void readIniGlobalsAndSections() throws IOException {
		String ini = """
			globalKey = globalValue
			
			[section]
			sectionKey = sectionValue
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertTrue(document.hasGlobalProperties());
			assertTrue(document.hasSections());
			assertEquals("globalValue", document.getGlobalAsString("globalKey"));
			assertEquals("sectionValue", document.requireSection("section").getAsString("sectionKey"));
		}
	}
	
	@Test
	void readIniCommentsIgnored() throws IOException {
		String ini = """
			; This is a comment
			# This is also a comment
			key1 = value1
			; Another comment
			[section]
			# Section comment
			key2 = value2
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertEquals(1, document.globalSize());
			assertEquals(1, document.sectionCount());
			assertEquals("value1", document.getGlobalAsString("key1"));
			assertEquals("value2", document.requireSection("section").getAsString("key2"));
		}
	}
	
	@Test
	void readIniEmptyLinesIgnored() throws IOException {
		String ini = """
			
			key1 = value1
			
			
			[section]
			
			key2 = value2
			
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertEquals(1, document.globalSize());
			assertEquals(1, document.sectionCount());
		}
	}
	
	@Test
	void readIniQuotedStringValues() throws IOException {
		String ini = """
			key1 = "quoted value"
			key2 = 'single quoted'
			key3 = "value with spaces"
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertEquals("quoted value", document.getGlobalAsString("key1"));
			assertEquals("single quoted", document.getGlobalAsString("key2"));
			assertEquals("value with spaces", document.getGlobalAsString("key3"));
		}
	}
	
	@Test
	void readIniEscapeSequences() throws IOException {
		String ini = """
			key1 = "line1\\nline2"
			key2 = "tab\\there"
			key3 = "quote\\"inside"
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertEquals("line1\nline2", document.getGlobalAsString("key1"));
			assertEquals("tab\there", document.getGlobalAsString("key2"));
			assertEquals("quote\"inside", document.getGlobalAsString("key3"));
		}
	}
	
	@Test
	void readIniEmptyValue() throws IOException {
		String ini = "key =";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertEquals(IniNull.INSTANCE, document.getGlobal("key"));
		}
	}
	
	@Test
	void readIniTypedValuesBoolean() throws IOException {
		String ini = """
			trueKey = true
			falseKey = false
			TrueKey = True
			FalseKey = FALSE
			""";
		
		try (IniReader reader = new IniReader(ini, PARSE_TYPES_CONFIG)) {
			IniDocument document = reader.readIni();
			assertTrue(document.getGlobalAsBoolean("trueKey"));
			assertFalse(document.getGlobalAsBoolean("falseKey"));
			assertTrue(document.getGlobalAsBoolean("TrueKey"));
			assertFalse(document.getGlobalAsBoolean("FalseKey"));
		}
	}
	
	@Test
	void readIniTypedValuesNumbers() throws IOException {
		String ini = """
			intKey = 42
			negativeKey = -17
			floatKey = 3.14
			negativeFloat = -2.5
			""";
		
		try (IniReader reader = new IniReader(ini, PARSE_TYPES_CONFIG)) {
			IniDocument document = reader.readIni();
			assertEquals(42, document.getGlobalAsInteger("intKey"));
			assertEquals(-17, document.getGlobalAsInteger("negativeKey"));
			assertEquals(3.14, document.getGlobalAsDouble("floatKey"));
			assertEquals(-2.5, document.getGlobalAsDouble("negativeFloat"));
		}
	}
	
	@Test
	void readIniInlineComments() throws IOException {
		String ini = """
			key1 = value1 ; this is a comment
			key2 = value2 # another comment
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertEquals("value1", document.getGlobalAsString("key1"));
			assertEquals("value2", document.getGlobalAsString("key2"));
		}
	}
	
	@Test
	void readIniInlineCommentInQuotedStringPreserved() throws IOException {
		String ini = """
			key = "value ; not a comment"
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertEquals("value ; not a comment", document.getGlobalAsString("key"));
		}
	}
	
	@Test
	void readIniStrictModeInvalidSectionName() {
		String ini = "[invalid section name]";
		
		try (IniReader reader = new IniReader(ini, STRICT_CONFIG)) {
			assertThrows(IniSyntaxException.class, reader::readIni);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readIniStrictModeInvalidKeyName() {
		String ini = "invalid key = value";
		
		try (IniReader reader = new IniReader(ini, STRICT_CONFIG)) {
			assertThrows(IniSyntaxException.class, reader::readIni);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readIniStrictModeDuplicateSection() {
		String ini = """
			[section]
			key1 = value1
			
			[section]
			key2 = value2
			""";
		
		try (IniReader reader = new IniReader(ini, STRICT_CONFIG)) {
			assertThrows(IniSyntaxException.class, reader::readIni);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readIniStrictModeDuplicateKey() {
		String ini = """
			key = value1
			key = value2
			""";
		
		try (IniReader reader = new IniReader(ini, STRICT_CONFIG)) {
			assertThrows(IniSyntaxException.class, reader::readIni);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readIniNonStrictModeDuplicateSectionAllowed() throws IOException {
		String ini = """
			[section]
			key1 = value1
			
			[section]
			key2 = value2
			""";
		
		try (IniReader reader = new IniReader(ini, NON_STRICT_CONFIG)) {
			IniDocument document = reader.readIni();
			IniSection section = document.requireSection("section");
			assertEquals(2, section.size());
			assertTrue(section.containsKey("key1"));
			assertTrue(section.containsKey("key2"));
		}
	}
	
	@Test
	void readIniNonStrictModeDuplicateKeyAllowed() throws IOException {
		String ini = """
			key = value1
			key = value2
			""";
		
		try (IniReader reader = new IniReader(ini, NON_STRICT_CONFIG)) {
			IniDocument document = reader.readIni();
			assertEquals("value2", document.getGlobalAsString("key"));
		}
	}
	
	@Test
	void readIniInvalidSyntaxMissingClosingBracket() {
		String ini = "[section";
		
		try (IniReader reader = new IniReader(ini, STRICT_CONFIG)) {
			assertThrows(IniSyntaxException.class, reader::readIni);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readIniInvalidSyntaxEmptySectionName() {
		String ini = "[]";
		
		try (IniReader reader = new IniReader(ini, STRICT_CONFIG)) {
			assertThrows(IniSyntaxException.class, reader::readIni);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readIniInvalidSyntaxEmptyKey() {
		String ini = " = value";
		
		try (IniReader reader = new IniReader(ini, STRICT_CONFIG)) {
			assertThrows(IniSyntaxException.class, reader::readIni);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readIniNonStrictModeMissingSeparator() throws IOException {
		String ini = "keyWithoutValue";
		
		try (IniReader reader = new IniReader(ini, NON_STRICT_CONFIG)) {
			IniDocument document = reader.readIni();
			assertEquals(IniNull.INSTANCE, document.getGlobal("keyWithoutValue"));
		}
	}
	
	@Test
	void readIniStrictModeMissingSeparator() {
		String ini = "keyWithoutValue";
		
		try (IniReader reader = new IniReader(ini, STRICT_CONFIG)) {
			assertThrows(IniSyntaxException.class, reader::readIni);
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void readIniWithInputProvider() throws IOException {
		String ini = """
			[section]
			key = value
			""";
		
		InputProvider provider = new InputProvider(new ByteArrayInputStream(ini.getBytes(StandardCharsets.UTF_8)));
		
		try (IniReader reader = new IniReader(provider)) {
			IniDocument document = reader.readIni();
			assertEquals("value", document.requireSection("section").getAsString("key"));
		}
	}
	
	@Test
	void readIniWhitespaceTrimming() throws IOException {
		String ini = """
			  key1  =  value1
			[  section  ]
			  key2  =  value2
			""";
		
		try (IniReader reader = new IniReader(ini)) {
			IniDocument document = reader.readIni();
			assertEquals("value1", document.getGlobalAsString("key1"));
			assertTrue(document.containsSection("section"));
			assertEquals("value2", document.requireSection("section").getAsString("key2"));
		}
	}
	
	@Test
	void closeResource() throws IOException {
		IniReader reader = new IniReader("key = value");
		reader.readIni();
		assertDoesNotThrow(reader::close);
	}
	
	@Test
	void readIniComplexDocument() throws IOException {
		String ini = """
			; Application Configuration
			appName = MyApplication
			version = 1
			debug = true
			
			[database]
			host = localhost
			port = 5432
			name = mydb
			
			; Logging configuration
			[logging]
			level = INFO
			file = /var/log/app.log
			console = true
			
			[features]
			feature1 = true
			feature2 = false
			maxRetries = 3
			""";
		
		try (IniReader reader = new IniReader(ini, PARSE_TYPES_CONFIG)) {
			IniDocument document = reader.readIni();
			
			assertEquals(3, document.globalSize());
			assertEquals("MyApplication", document.getGlobalAsString("appName"));
			assertEquals(1, document.getGlobalAsInteger("version"));
			assertTrue(document.getGlobalAsBoolean("debug"));
			
			assertEquals(3, document.sectionCount());
			
			IniSection database = document.requireSection("database");
			assertEquals("localhost", database.getAsString("host"));
			assertEquals(5432, database.getAsInteger("port"));
			
			IniSection logging = document.requireSection("logging");
			assertEquals("INFO", logging.getAsString("level"));
			assertTrue(logging.getAsBoolean("console"));
			
			IniSection features = document.requireSection("features");
			assertTrue(features.getAsBoolean("feature1"));
			assertFalse(features.getAsBoolean("feature2"));
			assertEquals(3, features.getAsInteger("maxRetries"));
		}
	}
}
