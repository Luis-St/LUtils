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

package net.luis.utils.io.data.property;

import net.luis.utils.io.data.OutputProvider;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyWriter}.<br>
 *
 * @author Luis-St
 */
class PropertyWriterTest {
	
	private static final PropertyConfig DEFAULT_CONFIG = PropertyConfig.DEFAULT;
	private static final PropertyConfig ADVANCED_CONFIG = PropertyConfig.ADVANCED;
	
	@Test
	void constructorValidation() {
		OutputProvider provider = new OutputProvider(OutputStream.nullOutputStream());
		
		assertThrows(NullPointerException.class, () -> new PropertyWriter(null));
		assertThrows(NullPointerException.class, () -> new PropertyWriter(null, DEFAULT_CONFIG));
		assertThrows(NullPointerException.class, () -> new PropertyWriter(provider, null));
		
		assertDoesNotThrow(() -> new PropertyWriter(provider));
		assertDoesNotThrow(() -> new PropertyWriter(provider, DEFAULT_CONFIG));
		assertDoesNotThrow(() -> new PropertyWriter(provider, ADVANCED_CONFIG));
	}
	
	@Test
	void writeEmptyPropertyObject() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			writer.write(new PropertyObject());
			assertEquals("", stream.toString());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeSinglePropertyFromObject() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			PropertyObject props = new PropertyObject();
			props.add("key", new PropertyValue("value"));
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("key"));
			assertTrue(result.contains("="));
			assertTrue(result.contains("value"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeMultipleProperties() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			PropertyObject props = new PropertyObject();
			props.add("name", new PropertyValue("John"));
			props.add("age", new PropertyValue(30));
			props.add("active", new PropertyValue(true));
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("name"));
			assertTrue(result.contains("John"));
			assertTrue(result.contains("age"));
			assertTrue(result.contains("30"));
			assertTrue(result.contains("active"));
			assertTrue(result.contains("true"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writePropertyNullEmpty() {
		PropertyConfig emptyNullConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), emptyNullConfig)) {
			PropertyObject props = new PropertyObject();
			props.add("nullKey", PropertyNull.INSTANCE);
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("nullKey"));
			assertTrue(result.contains("="));
			assertTrue(result.endsWith("= ") || result.trim().endsWith("="));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writePropertyNullString() {
		PropertyConfig nullStringConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.NULL_STRING,
			false, 2,
			':', ":-",
			null
		);
		
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), nullStringConfig)) {
			PropertyObject props = new PropertyObject();
			props.add("nullKey", PropertyNull.INSTANCE);
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("nullKey"));
			assertTrue(result.contains("null"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writePropertyNullTilde() {
		PropertyConfig tildeNullConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.TILDE,
			false, 2,
			':', ":-",
			null
		);
		
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), tildeNullConfig)) {
			PropertyObject props = new PropertyObject();
			props.add("nullKey", PropertyNull.INSTANCE);
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("nullKey"));
			assertTrue(result.contains("~"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writePropertyValueTypes() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			PropertyObject props = new PropertyObject();
			props.add("string", new PropertyValue("hello"));
			props.add("integer", new PropertyValue(42));
			props.add("float", new PropertyValue(3.14));
			props.add("boolean", new PropertyValue(true));
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("hello"));
			assertTrue(result.contains("42"));
			assertTrue(result.contains("3.14"));
			assertTrue(result.contains("true"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writePropertyArray() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			PropertyObject props = new PropertyObject();
			PropertyArray array = new PropertyArray();
			array.add(new PropertyValue("red"));
			array.add(new PropertyValue("green"));
			array.add(new PropertyValue("blue"));
			props.add("colors", array);
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("colors"));
			assertTrue(result.contains("["));
			assertTrue(result.contains("]"));
			assertTrue(result.contains("red"));
			assertTrue(result.contains("green"));
			assertTrue(result.contains("blue"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeSinglePropertyString() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			writer.writeSingleProperty("key", "value");
			
			String result = stream.toString();
			assertTrue(result.contains("key"));
			assertTrue(result.contains("="));
			assertTrue(result.contains("value"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeSinglePropertyBoolean() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			writer.writeSingleProperty("enabled", true);
			
			String result = stream.toString();
			assertTrue(result.contains("enabled"));
			assertTrue(result.contains("="));
			assertTrue(result.contains("true"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeSinglePropertyNumber() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			writer.writeSingleProperty("count", 42);
			
			String result = stream.toString();
			assertTrue(result.contains("count"));
			assertTrue(result.contains("="));
			assertTrue(result.contains("42"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeSinglePropertyElement() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			writer.writeSingleProperty("element", new PropertyValue("test"));
			
			String result = stream.toString();
			assertTrue(result.contains("element"));
			assertTrue(result.contains("="));
			assertTrue(result.contains("test"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeSinglePropertyNullValidation() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			assertThrows(NullPointerException.class, () -> writer.writeSingleProperty(null, "value"));
			assertThrows(NullPointerException.class, () -> writer.writeSingleProperty("key", (String) null));
			assertThrows(NullPointerException.class, () -> writer.writeSingleProperty(null, true));
			assertThrows(NullPointerException.class, () -> writer.writeSingleProperty("key", (Number) null));
			assertThrows(NullPointerException.class, () -> writer.writeSingleProperty(null, 42));
			assertThrows(NullPointerException.class, () -> writer.writeSingleProperty(null, new PropertyValue("test")));
			assertThrows(NullPointerException.class, () -> writer.writeSingleProperty("key", (PropertyElement) null));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeMultiLineArray() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			PropertyArray array = new PropertyArray();
			array.add(new PropertyValue("first"));
			array.add(new PropertyValue("second"));
			array.add(new PropertyValue("third"));
			writer.writeMultiLineArray("items", array);
			
			String result = stream.toString();
			assertTrue(result.contains("items[]"));
			assertTrue(result.contains("first"));
			assertTrue(result.contains("second"));
			assertTrue(result.contains("third"));
			
			String[] lines = result.split(System.lineSeparator());
			assertEquals(3, lines.length);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeMultiLineArrayEmpty() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			PropertyArray array = new PropertyArray();
			writer.writeMultiLineArray("items", array);
			
			String result = stream.toString();
			assertEquals("", result);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeMultiLineArrayNullValidation() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			assertThrows(NullPointerException.class, () -> writer.writeMultiLineArray(null, new PropertyArray()));
			assertThrows(NullPointerException.class, () -> writer.writeMultiLineArray("key", null));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeInlineArrayEmpty() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			PropertyObject props = new PropertyObject();
			props.add("items", new PropertyArray());
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("items"));
			assertTrue(result.contains("[]"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeInlineArrayMixed() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			PropertyObject props = new PropertyObject();
			PropertyArray array = new PropertyArray();
			array.add(new PropertyValue(42));
			array.add(new PropertyValue("hello"));
			array.add(new PropertyValue(true));
			array.add(PropertyNull.INSTANCE);
			props.add("mixed", array);
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("mixed"));
			assertTrue(result.contains("["));
			assertTrue(result.contains("]"));
			assertTrue(result.contains("42"));
			assertTrue(result.contains("hello"));
			assertTrue(result.contains("true"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeWithCompactionSimple() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), ADVANCED_CONFIG)) {
			PropertyObject props = new PropertyObject();
			props.add("app.dev.url", new PropertyValue("http://localhost"));
			props.add("app.prod.url", new PropertyValue("http://localhost"));
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("[dev|prod]") || result.contains("[prod|dev]"));
			assertTrue(result.contains("http://localhost"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeWithCompactionDisabled() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), DEFAULT_CONFIG)) {
			PropertyObject props = new PropertyObject();
			props.add("app.dev.url", new PropertyValue("http://localhost"));
			props.add("app.prod.url", new PropertyValue("http://localhost"));
			writer.write(props);
			
			String result = stream.toString();
			assertFalse(result.contains("["));
			assertFalse(result.contains("]"));
			assertTrue(result.contains("app.dev.url"));
			assertTrue(result.contains("app.prod.url"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeWithCompactionDifferentValues() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), ADVANCED_CONFIG)) {
			PropertyObject props = new PropertyObject();
			props.add("app.dev.url", new PropertyValue("http://localhost:8080"));
			props.add("app.prod.url", new PropertyValue("http://production.com"));
			writer.write(props);
			
			String result = stream.toString();
			assertFalse(result.contains("[dev|prod]"));
			assertFalse(result.contains("[prod|dev]"));
			assertTrue(result.contains("app.dev.url"));
			assertTrue(result.contains("app.prod.url"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeWithDifferentSeparators() {
		PropertyConfig colonConfig = new PropertyConfig(
			':', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			'|', "||",
			null
		);
		
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), colonConfig)) {
			PropertyObject props = new PropertyObject();
			props.add("key", new PropertyValue("value"));
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains(":"));
			assertFalse(result.contains(" = "));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeWithDifferentAlignment() {
		PropertyConfig alignmentConfig = new PropertyConfig(
			'=', 3, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), alignmentConfig)) {
			PropertyObject props = new PropertyObject();
			props.add("key", new PropertyValue("value"));
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("   =   "));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeWithDifferentArrayChars() {
		PropertyConfig parenArrayConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'(', ')', ';',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream), parenArrayConfig)) {
			PropertyObject props = new PropertyObject();
			PropertyArray array = new PropertyArray();
			array.add(new PropertyValue("a"));
			array.add(new PropertyValue("b"));
			array.add(new PropertyValue("c"));
			props.add("items", array);
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("("));
			assertTrue(result.contains(")"));
			assertTrue(result.contains(";"));
			assertFalse(result.contains("["));
			assertFalse(result.contains("]"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeWithDifferentCharsets() {
		PropertyConfig utf16Config = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_16,
			false, "",
			'[', ']', ',',
			true, false,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(os), utf16Config)) {
			PropertyObject props = new PropertyObject();
			props.add("message", new PropertyValue("Hello 世界"));
			writer.write(props);
			
			String result = os.toString(StandardCharsets.UTF_16);
			assertTrue(result.contains("Hello 世界"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeNullPropertyObject() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			assertThrows(NullPointerException.class, () -> writer.write(null));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void flushingBehavior() {
		FlushTrackingOutputStream trackingStream = new FlushTrackingOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(trackingStream))) {
			PropertyObject props = new PropertyObject();
			props.add("key", new PropertyValue("value"));
			writer.write(props);
			
			assertTrue(trackingStream.wasFlushCalled());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new PropertyWriter(new OutputProvider(OutputStream.nullOutputStream())).close());
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PropertyWriter writer = new PropertyWriter(new OutputProvider(os));
		assertDoesNotThrow(writer::close);
		assertDoesNotThrow(writer::close);
	}
	
	@Test
	void writeLargePropertyObject() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			PropertyObject props = new PropertyObject();
			for (int i = 0; i < 100; i++) {
				props.add("key" + i, new PropertyValue("value" + i));
			}
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("key0"));
			assertTrue(result.contains("key99"));
			assertTrue(result.contains("value0"));
			assertTrue(result.contains("value99"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeHierarchicalProperties() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			PropertyObject props = new PropertyObject();
			props.add("app.database.host", new PropertyValue("localhost"));
			props.add("app.database.port", new PropertyValue(5432));
			props.add("app.database.name", new PropertyValue("mydb"));
			props.add("app.cache.enabled", new PropertyValue(true));
			writer.write(props);
			
			String result = stream.toString();
			assertTrue(result.contains("app.database.host"));
			assertTrue(result.contains("app.database.port"));
			assertTrue(result.contains("app.database.name"));
			assertTrue(result.contains("app.cache.enabled"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void writeMultipleSingleProperties() {
		StringOutputStream stream = new StringOutputStream();
		try (PropertyWriter writer = new PropertyWriter(new OutputProvider(stream))) {
			writer.writeSingleProperty("first", "value1");
			writer.writeSingleProperty("second", "value2");
			writer.writeSingleProperty("third", "value3");
			
			String result = stream.toString();
			assertTrue(result.contains("first"));
			assertTrue(result.contains("second"));
			assertTrue(result.contains("third"));
			
			String[] lines = result.split(System.lineSeparator());
			assertEquals(3, lines.length);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	private static class StringOutputStream extends OutputStream {
		
		private final StringBuilder builder = new StringBuilder();
		
		@Override
		public void write(int b) {
			if (b == 0) {
				return;
			}
			this.builder.append((char) b);
		}
		
		public void reset() {
			this.builder.setLength(0);
		}
		
		@Override
		public String toString() {
			return this.builder.toString();
		}
	}
	
	private static class FlushTrackingOutputStream extends OutputStream {
		
		private boolean flushCalled;
		
		@Override
		public void write(int b) {
			// Do nothing
		}
		
		@Override
		public void flush() {
			this.flushCalled = true;
		}
		
		public boolean wasFlushCalled() {
			return this.flushCalled;
		}
	}
}
