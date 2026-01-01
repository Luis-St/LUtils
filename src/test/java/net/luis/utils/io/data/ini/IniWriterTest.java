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

import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IniWriter}.<br>
 *
 * @author Luis-St
 */
class IniWriterTest {
	
	private static final IniConfig CUSTOM_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.EMPTY, StandardCharsets.UTF_8
	);
	
	private static final IniConfig SKIP_NULL_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.SKIP, StandardCharsets.UTF_8
	);
	
	private static final IniConfig NULL_STRING_CONFIG = new IniConfig(
		true, true, "\t", Set.of(';', '#'), '=', 1,
		false, false, false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile("^[a-zA-Z0-9._-]+$"),
		IniConfig.NullStyle.NULL_STRING, StandardCharsets.UTF_8
	);
	
	@Test
	void constructorValidation() {
		assertThrows(NullPointerException.class, () -> new IniWriter(null));
		assertThrows(NullPointerException.class, () -> new IniWriter(new OutputProvider(new ByteArrayOutputStream()), null));
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		assertDoesNotThrow(() -> new IniWriter(new OutputProvider(output)));
		assertDoesNotThrow(() -> new IniWriter(new OutputProvider(new ByteArrayOutputStream()), CUSTOM_CONFIG));
	}
	
	@Test
	void writeIniEmptyDocument() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(new IniDocument());
		}
		
		assertEquals("", output.toString(StandardCharsets.UTF_8));
	}
	
	@Test
	void writeIniNullValidation() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			assertThrows(NullPointerException.class, () -> writer.writeIni(null));
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void writeIniWithGlobalProperties() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IniDocument document = new IniDocument();
		document.addGlobal("key1", new IniValue("value1"));
		document.addGlobal("key2", new IniValue(42));
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(document);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("value1"));
		assertTrue(result.contains("key2"));
		assertTrue(result.contains("42"));
	}
	
	@Test
	void writeIniWithSection() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IniDocument document = new IniDocument();
		IniSection section = document.createSection("mySection");
		section.add("key", new IniValue("value"));
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(document);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("[mySection]"));
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void writeSectionValidation() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			assertThrows(NullPointerException.class, () -> writer.writeSection(null));
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void writeSection() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IniSection section = new IniSection("testSection");
		section.add("key1", new IniValue("value1"));
		section.add("key2", new IniValue(42));
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeSection(section);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("[testSection]"));
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("value1"));
		assertTrue(result.contains("key2"));
		assertTrue(result.contains("42"));
	}
	
	@Test
	void writePropertyValidation() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			assertThrows(NullPointerException.class, () -> writer.writeProperty(null, new IniValue("value")));
			assertThrows(NullPointerException.class, () -> writer.writeProperty("key", (IniElement) null));
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void writePropertyWithIniElement() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeProperty("stringKey", new IniValue("stringValue"));
			writer.writeProperty("numberKey", new IniValue(42));
			writer.writeProperty("booleanKey", new IniValue(true));
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
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeProperty("key", "value");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("key"));
		assertTrue(result.contains("value"));
	}
	
	@Test
	void writePropertyWithNullString() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output), NULL_STRING_CONFIG)) {
			writer.writeProperty("key", (String) null);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("key"));
		assertTrue(result.contains("null"));
	}
	
	@Test
	void writePropertyWithBoolean() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
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
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeProperty("intKey", 42);
			writer.writeProperty("doubleKey", 3.14);
			writer.writeProperty("nullKey", (Number) null);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("intKey"));
		assertTrue(result.contains("42"));
		assertTrue(result.contains("doubleKey"));
		assertTrue(result.contains("3.14"));
	}
	
	@Test
	void writePropertySkipsNullWhenConfigured() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output), SKIP_NULL_CONFIG)) {
			writer.writeProperty("normalKey", new IniValue("value"));
			writer.writeProperty("nullKey", IniNull.INSTANCE);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("normalKey"));
		assertFalse(result.contains("nullKey"));
	}
	
	@Test
	void writeSectionHeaderValidation() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			assertThrows(NullPointerException.class, () -> writer.writeSectionHeader(null));
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void writeSectionHeader() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeSectionHeader("mySection");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("[mySection]"));
	}
	
	@Test
	void writeCommentValidation() {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			assertThrows(NullPointerException.class, () -> writer.writeComment(null));
		} catch (IOException e) {
			fail("Unexpected IOException");
		}
	}
	
	@Test
	void writeComment() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeComment("This is a comment");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("; This is a comment") || result.contains("# This is a comment"));
	}
	
	@Test
	void writeBlankLine() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeProperty("key1", "value1");
			writer.writeBlankLine();
			writer.writeProperty("key2", "value2");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("key1"));
		assertTrue(result.contains("key2"));
		assertTrue(result.contains(System.lineSeparator() + System.lineSeparator()));
	}
	
	@Test
	void closeResource() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IniWriter writer = new IniWriter(new OutputProvider(output));
		writer.writeProperty("key", "value");
		assertDoesNotThrow(writer::close);
	}
	
	@Test
	void writeIniComplexDocument() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IniDocument document = new IniDocument();
		
		document.addGlobal("appName", new IniValue("MyApplication"));
		document.addGlobal("version", new IniValue(1));
		document.addGlobal("debug", new IniValue(true));
		
		IniSection database = document.createSection("database");
		database.add("host", new IniValue("localhost"));
		database.add("port", new IniValue(5432));
		database.add("name", new IniValue("mydb"));
		
		IniSection logging = document.createSection("logging");
		logging.add("level", new IniValue("INFO"));
		logging.add("file", new IniValue("/var/log/app.log"));
		logging.add("console", new IniValue(true));
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeIni(document);
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
		assertTrue(result.contains("appName"));
		assertTrue(result.contains("MyApplication"));
		assertTrue(result.contains("version"));
		assertTrue(result.contains("1"));
		assertTrue(result.contains("debug"));
		assertTrue(result.contains("true"));
		assertTrue(result.contains("[database]"));
		assertTrue(result.contains("host"));
		assertTrue(result.contains("localhost"));
		assertTrue(result.contains("port"));
		assertTrue(result.contains("5432"));
		assertTrue(result.contains("[logging]"));
		assertTrue(result.contains("level"));
		assertTrue(result.contains("INFO"));
	}
	
	@Test
	void writeMultipleProperties() throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		try (IniWriter writer = new IniWriter(new OutputProvider(output))) {
			writer.writeComment("Global settings");
			writer.writeProperty("key1", "value1");
			writer.writeProperty("key2", 42);
			writer.writeBlankLine();
			writer.writeSectionHeader("section");
			writer.writeProperty("sectionKey", "sectionValue");
		}
		
		String result = output.toString(StandardCharsets.UTF_8);
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
		IniDocument document = new IniDocument();
		document.addGlobal("key", new IniValue("value"));
		
		ByteArrayOutputStream output1 = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output1), CUSTOM_CONFIG)) {
			writer.writeIni(document);
		}
		
		ByteArrayOutputStream output2 = new ByteArrayOutputStream();
		try (IniWriter writer = new IniWriter(new OutputProvider(output2), NULL_STRING_CONFIG)) {
			writer.writeIni(document);
		}
		
		String result1 = output1.toString(StandardCharsets.UTF_8);
		String result2 = output2.toString(StandardCharsets.UTF_8);
		
		assertTrue(result1.contains("key"));
		assertTrue(result2.contains("key"));
	}
}
