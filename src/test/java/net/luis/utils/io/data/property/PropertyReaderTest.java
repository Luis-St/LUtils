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

import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.property.exception.PropertySyntaxException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyReader}.<br>
 *
 * @author Luis-St
 */
class PropertyReaderTest {
	
	private static final PropertyConfig DEFAULT_CONFIG = PropertyConfig.DEFAULT;
	private static final PropertyConfig ADVANCED_CONFIG = PropertyConfig.ADVANCED;
	
	private static PropertyReader createReader(String content) {
		return createReader(content, DEFAULT_CONFIG);
	}
	
	private static PropertyReader createReader(String content, PropertyConfig config) {
		ByteArrayInputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
		return new PropertyReader(new InputProvider(stream), config);
	}
	
	@Test
	void constructorWithInputProvider() {
		assertThrows(NullPointerException.class, () -> new PropertyReader(null));
		assertThrows(NullPointerException.class, () -> new PropertyReader(null, DEFAULT_CONFIG));
		assertThrows(NullPointerException.class, () -> new PropertyReader(new InputProvider(InputStream.nullInputStream()), null));
		
		assertDoesNotThrow(() -> new PropertyReader(new InputProvider(InputStream.nullInputStream())));
		assertDoesNotThrow(() -> new PropertyReader(new InputProvider(InputStream.nullInputStream()), DEFAULT_CONFIG));
		assertDoesNotThrow(() -> new PropertyReader(new InputProvider(InputStream.nullInputStream()), ADVANCED_CONFIG));
	}
	
	@Test
	void readPropertiesEmptyInput() {
		try (PropertyReader reader = createReader("")) {
			PropertyObject props = reader.readProperties();
			assertTrue(props.isEmpty());
			assertEquals(0, props.size());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesCommentLines() {
		String content = """
			# This is a comment
			key1 = value1
			# Another comment
			key2 = value2
			""";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(2, props.size());
			assertEquals(new PropertyValue("value1"), props.get("key1"));
			assertEquals(new PropertyValue("value2"), props.get("key2"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesBlankLines() {
		String content = """
			key1 = value1
			
			key2 = value2
			
			
			key3 = value3
			""";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(3, props.size());
			assertEquals(new PropertyValue("value1"), props.get("key1"));
			assertEquals(new PropertyValue("value2"), props.get("key2"));
			assertEquals(new PropertyValue("value3"), props.get("key3"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesSingleProperty() {
		String content = "key = value";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			assertEquals(new PropertyValue("value"), props.get("key"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesMultipleProperties() {
		String content = """
			name = John Doe
			age = 30
			active = true
			city = New York
			""";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(4, props.size());
			assertEquals(new PropertyValue("John Doe"), props.get("name"));
			assertEquals(new PropertyValue("30"), props.get("age"));
			assertEquals(new PropertyValue("true"), props.get("active"));
			assertEquals(new PropertyValue("New York"), props.get("city"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesNullValues() throws Exception {
		// Note: Using no space before = to avoid alignment issue with empty values
		String content = "empty=\nnullValue=null\ntildeValue=~\n";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(3, props.size());
			assertTrue(props.get("empty").isPropertyNull(), "empty should be null");
			assertTrue(props.get("nullValue").isPropertyNull(), "nullValue should be null");
			assertTrue(props.get("tildeValue").isPropertyNull(), "tildeValue should be null");
		}
	}
	
	@Test
	void readPropertiesWithTypedValueParsing() {
		PropertyConfig typedConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, true,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		String content = """
			intValue = 42
			floatValue = 3.14
			boolTrue = true
			boolFalse = false
			stringValue = hello
			""";
		
		try (PropertyReader reader = createReader(content, typedConfig)) {
			PropertyObject props = reader.readProperties();
			assertEquals(5, props.size());
			
			PropertyValue intVal = props.getPropertyValue("intValue");
			assertTrue(intVal.isNumber());
			assertEquals(42, intVal.getAsInteger());
			
			PropertyValue floatVal = props.getPropertyValue("floatValue");
			assertTrue(floatVal.isNumber());
			assertEquals(3.14, floatVal.getAsDouble(), 0.001);
			
			PropertyValue boolTrueVal = props.getPropertyValue("boolTrue");
			assertTrue(boolTrueVal.isBoolean());
			assertTrue(boolTrueVal.getAsBoolean());
			
			PropertyValue boolFalseVal = props.getPropertyValue("boolFalse");
			assertTrue(boolFalseVal.isBoolean());
			assertFalse(boolFalseVal.getAsBoolean());
			
			PropertyValue stringVal = props.getPropertyValue("stringValue");
			assertTrue(stringVal.isString());
			assertEquals("hello", stringVal.getAsString());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesInlineArray() {
		String content = "colors = [red, green, blue]";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			
			PropertyArray array = props.getAsPropertyArray("colors");
			assertEquals(3, array.size());
			assertEquals(new PropertyValue("red"), array.get(0));
			assertEquals(new PropertyValue("green"), array.get(1));
			assertEquals(new PropertyValue("blue"), array.get(2));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesEmptyInlineArray() {
		String content = "items = []";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			
			PropertyArray array = props.getAsPropertyArray("items");
			assertTrue(array.isEmpty());
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesMultiLineArray() {
		String content = """
			items[] = first
			items[] = second
			items[] = third
			""";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			
			PropertyArray array = props.getAsPropertyArray("items");
			assertEquals(3, array.size());
			assertEquals(new PropertyValue("first"), array.get(0));
			assertEquals(new PropertyValue("second"), array.get(1));
			assertEquals(new PropertyValue("third"), array.get(2));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesCompactedKeys() {
		String content = "app.[dev|prod].url = http://localhost";
		
		try (PropertyReader reader = createReader(content, ADVANCED_CONFIG)) {
			PropertyObject props = reader.readProperties();
			assertEquals(2, props.size());
			assertEquals(new PropertyValue("http://localhost"), props.get("app.dev.url"));
			assertEquals(new PropertyValue("http://localhost"), props.get("app.prod.url"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesCompactedKeysMultipleVariants() {
		String content = "database.[host|port|name] = default";
		
		try (PropertyReader reader = createReader(content, ADVANCED_CONFIG)) {
			PropertyObject props = reader.readProperties();
			assertEquals(3, props.size());
			assertEquals(new PropertyValue("default"), props.get("database.host"));
			assertEquals(new PropertyValue("default"), props.get("database.port"));
			assertEquals(new PropertyValue("default"), props.get("database.name"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesVariableKeysProperty() {
		String content = """
			profile = dev
			app.${prop:profile}.url = http://localhost
			""";
		
		try (PropertyReader reader = createReader(content, ADVANCED_CONFIG)) {
			PropertyObject props = reader.readProperties();
			assertEquals(2, props.size());
			assertEquals(new PropertyValue("dev"), props.get("profile"));
			assertEquals(new PropertyValue("http://localhost"), props.get("app.dev.url"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesVariableKeysWithDefault() {
		String content = "app.${prop:missing:-fallback}.url = http://localhost";
		
		try (PropertyReader reader = createReader(content, ADVANCED_CONFIG)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			assertEquals(new PropertyValue("http://localhost"), props.get("app.fallback.url"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesVariableKeysSystem() {
		String originalValue = System.getProperty("user.name");
		if (originalValue != null) {
			String content = "user.${sys:user.name}.profile = active";
			
			try (PropertyReader reader = createReader(content, ADVANCED_CONFIG)) {
				PropertyObject props = reader.readProperties();
				assertEquals(1, props.size());
				assertTrue(props.containsKey("user." + originalValue + ".profile"));
				assertEquals(new PropertyValue("active"), props.get("user." + originalValue + ".profile"));
			} catch (Exception e) {
				fail("Unexpected exception: " + e.getMessage());
			}
		}
	}
	
	@Test
	void readPropertiesVariableKeysCustom() {
		PropertyConfig customVarConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			true, StandardCharsets.UTF_8,
			true, "\t",
			'[', ']', ',',
			true, true,
			PropertyConfig.NullStyle.EMPTY,
			true, 2,
			':', ":-",
			Map.of("CUSTOM_VAR", "custom_value")
		);
		
		String content = "app.${prop:CUSTOM_VAR}.setting = enabled";
		
		try (PropertyReader reader = createReader(content, customVarConfig)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			assertEquals(new PropertyValue("enabled"), props.get("app.custom_value.setting"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesNoSeparator() {
		String content = "key without separator";
		
		try (PropertyReader reader = createReader(content)) {
			assertThrows(PropertySyntaxException.class, reader::readProperties);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesAdvancedKeyNotAllowed() {
		String content = "app.[dev|prod].url = http://localhost";
		
		try (PropertyReader reader = createReader(content, DEFAULT_CONFIG)) {
			assertThrows(PropertySyntaxException.class, reader::readProperties);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesInvalidVariableType() {
		String content = "app.${unknown:key}.setting = value";
		
		try (PropertyReader reader = createReader(content, ADVANCED_CONFIG)) {
			assertThrows(PropertySyntaxException.class, reader::readProperties);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesUnresolvedVariable() {
		String content = "app.${prop:nonexistent}.setting = value";
		
		try (PropertyReader reader = createReader(content, ADVANCED_CONFIG)) {
			assertThrows(PropertySyntaxException.class, reader::readProperties);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesWithAlignment() {
		String content = "key   =   value";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			assertEquals(new PropertyValue("value"), props.get("key"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesMultipleSeparators() {
		String content = "url = http://example.com/path?query=value";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			assertEquals(new PropertyValue("http://example.com/path?query=value"), props.get("url"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesWithColonSeparator() {
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
		
		String content = "key : value";
		
		try (PropertyReader reader = createReader(content, colonConfig)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			assertEquals(new PropertyValue("value"), props.get("key"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesDifferentCommentCharacters() {
		PropertyConfig multiCommentConfig = new PropertyConfig(
			'=', 1, Set.of('#', ';', '!'),
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
		
		String content = """
			# Hash comment
			key1 = value1
			; Semicolon comment
			key2 = value2
			! Exclamation comment
			key3 = value3
			""";
		
		try (PropertyReader reader = createReader(content, multiCommentConfig)) {
			PropertyObject props = reader.readProperties();
			assertEquals(3, props.size());
			assertEquals(new PropertyValue("value1"), props.get("key1"));
			assertEquals(new PropertyValue("value2"), props.get("key2"));
			assertEquals(new PropertyValue("value3"), props.get("key3"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesHierarchicalKeys() {
		String content = """
			app.database.host = localhost
			app.database.port = 5432
			app.database.name = mydb
			app.cache.enabled = true
			app.cache.ttl = 3600
			""";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(5, props.size());
			assertEquals(new PropertyValue("localhost"), props.get("app.database.host"));
			assertEquals(new PropertyValue("5432"), props.get("app.database.port"));
			assertEquals(new PropertyValue("mydb"), props.get("app.database.name"));
			assertEquals(new PropertyValue("true"), props.get("app.cache.enabled"));
			assertEquals(new PropertyValue("3600"), props.get("app.cache.ttl"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesInlineArrayWithTypedValues() {
		PropertyConfig typedConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, true,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		String content = "numbers = [1, 2, 3, 4, 5]";
		
		try (PropertyReader reader = createReader(content, typedConfig)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			
			PropertyArray array = props.getAsPropertyArray("numbers");
			assertEquals(5, array.size());
			assertEquals(1, array.getAsInteger(0));
			assertEquals(2, array.getAsInteger(1));
			assertEquals(3, array.getAsInteger(2));
			assertEquals(4, array.getAsInteger(3));
			assertEquals(5, array.getAsInteger(4));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesInlineArrayMixedTypes() {
		PropertyConfig typedConfig = new PropertyConfig(
			'=', 1, Set.of('#'),
			Pattern.compile(".*"), Pattern.compile(".*"),
			false, StandardCharsets.UTF_8,
			false, "",
			'[', ']', ',',
			true, true,
			PropertyConfig.NullStyle.EMPTY,
			false, 2,
			':', ":-",
			null
		);
		
		String content = "mixed = [42, hello, true, 3.14]";
		
		try (PropertyReader reader = createReader(content, typedConfig)) {
			PropertyObject props = reader.readProperties();
			assertEquals(1, props.size());
			
			PropertyArray array = props.getAsPropertyArray("mixed");
			assertEquals(4, array.size());
			assertEquals(42, array.getAsInteger(0));
			assertEquals("hello", array.getAsString(1));
			assertTrue(array.getAsBoolean(2));
			assertEquals(3.14, array.getAsDouble(3), 0.001);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesWithLeadingWhitespace() {
		String content = """
			  key1 = value1
			    key2 = value2
			\tkey3 = value3
			""";
		
		try (PropertyReader reader = createReader(content)) {
			PropertyObject props = reader.readProperties();
			assertEquals(3, props.size());
			assertEquals(new PropertyValue("value1"), props.get("key1"));
			assertEquals(new PropertyValue("value2"), props.get("key2"));
			assertEquals(new PropertyValue("value3"), props.get("key3"));
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesEmptyCompactedPart() {
		String content = "app.[].url = http://localhost";
		
		try (PropertyReader reader = createReader(content, ADVANCED_CONFIG)) {
			assertThrows(PropertySyntaxException.class, reader::readProperties);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void readPropertiesEmptyVariablePart() {
		String content = "app.${}.url = http://localhost";
		
		try (PropertyReader reader = createReader(content, ADVANCED_CONFIG)) {
			assertThrows(PropertySyntaxException.class, reader::readProperties);
		} catch (Exception e) {
			fail("Unexpected exception: " + e.getMessage());
		}
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> createReader("key = value").close());
		assertDoesNotThrow(() -> createReader("").close());
		
		PropertyReader reader = createReader("test = value");
		assertDoesNotThrow(reader::close);
		assertDoesNotThrow(reader::close);
	}
}
