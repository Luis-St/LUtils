/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

import net.luis.utils.annotation.type.MockObject;
import net.luis.utils.io.data.InputProvider;
import net.luis.utils.io.data.property.exception.PropertySyntaxException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
	private static final PropertyConfig ADVANCED_DEFAULT_CONFIG = new PropertyConfig('=', 1, Set.of('#'), Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile(".*"), true, StandardCharsets.UTF_8);
	private static final PropertyConfig CUSTOM_CONFIG = new PropertyConfig(':', 0, Set.of(';'), Pattern.compile("^[a-z._]+$"), Pattern.compile("^[ a-zA-Z0-9._-]*$"), true, StandardCharsets.UTF_8);
	
	private static @NotNull PropertyReader createReader(@NotNull String content) {
		return new PropertyReader(new InputProvider(new StringInputStream(content)));
	}
	
	private static @NotNull PropertyReader createReader(@NotNull String content, @NotNull PropertyConfig config) {
		return new PropertyReader(new InputProvider(new StringInputStream(content)), config);
	}
	
	@Test
	void constructor() {
		InputProvider provider = new InputProvider(InputStream.nullInputStream());
		
		assertThrows(NullPointerException.class, () -> new PropertyReader(null));
		assertDoesNotThrow(() -> new PropertyReader(provider));
		
		assertThrows(NullPointerException.class, () -> new PropertyReader(null, PropertyConfig.DEFAULT));
		assertThrows(NullPointerException.class, () -> new PropertyReader(provider, null));
		assertDoesNotThrow(() -> new PropertyReader(provider, PropertyConfig.DEFAULT));
	}
	
	@Test
	void readSimplePropertiesDefaultConfig() {
		PropertyReader reader;
		Properties properties;
		
		reader = createReader("key1 = value1" + System.lineSeparator() + "key2 =value2" + System.lineSeparator() + "key3=" + System.lineSeparator() + "key4= value4");
		properties = assertDoesNotThrow(reader::readProperties);
		assertEquals(4, properties.size());
		assertNotNull(properties.getProperty("key1"));
		assertEquals("value1", properties.getProperty("key1").getString());
		assertNotNull(properties.getProperty("key2"));
		assertEquals("alue2", properties.getProperty("key2").getString());
		assertNotNull(properties.getProperty("key3"));
		assertEquals("", properties.getProperty("key3").getString());
		assertNotNull(properties.getProperty("key4"));
		assertEquals(" value4", properties.getProperty("key4").getString());
		
		// Commented line
		reader = createReader("#key1 = value1" + System.lineSeparator());
		properties = assertDoesNotThrow(reader::readProperties);
		assertEquals(0, properties.size());
		
		// No separator
		reader = createReader("key_a : value1");
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Illegal key
		reader = createReader("key 1 = value1" + System.lineSeparator());
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Compacted key -> Not allowed
		reader = createReader("key.[1|2] = value1" + System.lineSeparator());
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Variable key -> Not allowed
		reader = createReader("key.${?key1} = value1" + System.lineSeparator());
		assertThrows(PropertySyntaxException.class, reader::readProperties);
	}
	
	@Test
	void readSimplePropertiesCustomConfig() {
		PropertyReader reader;
		Properties properties;
		
		reader = createReader("key_a : value1" + System.lineSeparator() + "key_b :value2" + System.lineSeparator() + "key_c:" + System.lineSeparator() + "key_d: value4", CUSTOM_CONFIG);
		properties = assertDoesNotThrow(reader::readProperties);
		assertEquals(4, properties.size());
		assertNotNull(properties.getProperty("key_a"));
		assertEquals("value1", properties.getProperty("key_a").getString());
		assertNotNull(properties.getProperty("key_b"));
		assertEquals("alue2", properties.getProperty("key_b").getString());
		assertNotNull(properties.getProperty("key_c"));
		assertEquals("", properties.getProperty("key_c").getString());
		assertNotNull(properties.getProperty("key_d"));
		assertEquals(" value4", properties.getProperty("key_d").getString());
		
		// Commented line
		reader = createReader(";key_a : value1", CUSTOM_CONFIG);
		properties = assertDoesNotThrow(reader::readProperties);
		assertEquals(0, properties.size());
		
		// No separator
		reader = createReader("key_a = value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Illegal key
		reader = createReader("key1 : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Compacted key -> Not allowed
		reader = createReader("key.[a|b] = value1" + System.lineSeparator());
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Variable key -> Not allowed
		reader = createReader("key.${?key_a} = value1" + System.lineSeparator());
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Illegal value
		reader = createReader("key_a : $value", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
	}
	
	@Test
	void readCompactedPropertiesAdvancedDefaultConfig() {
		PropertyReader reader;
		Properties properties;
		
		reader = createReader("key.[1] = value1" + System.lineSeparator() + "key.[2|3] =value2" + System.lineSeparator() + "key.[4].[5]=" + System.lineSeparator() + "key.[6|7].[8|9]= value4", ADVANCED_DEFAULT_CONFIG);
		properties = assertDoesNotThrow(reader::readProperties);
		assertEquals(8, properties.size());
		assertNotNull(properties.getProperty("key.1"));
		assertEquals("value1", properties.getProperty("key.1").getString());
		assertNotNull(properties.getProperty("key.2"));
		assertEquals("alue2", properties.getProperty("key.2").getString());
		assertNotNull(properties.getProperty("key.3"));
		assertEquals("alue2", properties.getProperty("key.3").getString());
		assertNotNull(properties.getProperty("key.4.5"));
		assertEquals("", properties.getProperty("key.4.5").getString());
		assertNotNull(properties.getProperty("key.6.8"));
		assertEquals(" value4", properties.getProperty("key.6.8").getString());
		assertNotNull(properties.getProperty("key.6.9"));
		assertEquals(" value4", properties.getProperty("key.6.9").getString());
		assertNotNull(properties.getProperty("key.7.8"));
		assertEquals(" value4", properties.getProperty("key.7.8").getString());
		assertNotNull(properties.getProperty("key.7.9"));
		assertEquals(" value4", properties.getProperty("key.7.9").getString());
		
		// Empty key part
		reader = createReader("key..test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Blank key part
		reader = createReader("key. .test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Illegal key end
		reader = createReader("key.test. = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Empty compacted key part
		reader = createReader("key.[].test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Blank compacted key part
		reader = createReader("key.[ ].test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
	}
	
	@Test
	void readCompactedPropertiesCustomConfig() {
		PropertyReader reader;
		Properties properties;
		
		reader = createReader("key.[a] : value1" + System.lineSeparator() + "key.[b|c] :value2" + System.lineSeparator() + "key.[d].[e]:" + System.lineSeparator() + "key.[f|g].[h|i]: value4", CUSTOM_CONFIG);
		properties = assertDoesNotThrow(reader::readProperties);
		assertEquals(8, properties.size());
		assertNotNull(properties.getProperty("key.a"));
		assertEquals("value1", properties.getProperty("key.a").getString());
		assertNotNull(properties.getProperty("key.b"));
		assertEquals("alue2", properties.getProperty("key.b").getString());
		assertNotNull(properties.getProperty("key.c"));
		assertEquals("alue2", properties.getProperty("key.c").getString());
		assertNotNull(properties.getProperty("key.d.e"));
		assertEquals("", properties.getProperty("key.d.e").getString());
		assertNotNull(properties.getProperty("key.f.h"));
		assertEquals(" value4", properties.getProperty("key.f.h").getString());
		assertNotNull(properties.getProperty("key.f.i"));
		assertEquals(" value4", properties.getProperty("key.f.i").getString());
		assertNotNull(properties.getProperty("key.g.h"));
		assertEquals(" value4", properties.getProperty("key.g.h").getString());
		assertNotNull(properties.getProperty("key.g.i"));
		assertEquals(" value4", properties.getProperty("key.g.i").getString());
		
		// Empty key part
		reader = createReader("key..test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Blank key part
		reader = createReader("key. .test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Illegal key end
		reader = createReader("key.test. : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Empty compacted key part
		reader = createReader("key.[].test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Blank compacted key part
		reader = createReader("key.[ ].test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
	}
	
	@Test
	void readVariablePropertiesAdvancedDefaultConfig() {
		System.setProperty("sys.default", "2");
		PropertyReader reader;
		Properties properties;
		
		reader = createReader("key = 1" + System.lineSeparator() + "key.${?key} =value2" + System.lineSeparator() + "key.${sys?sys.default}=" + System.lineSeparator() + "key.${env?env.default?}.${property?key.test?4}= value4", ADVANCED_DEFAULT_CONFIG);
		properties = assertDoesNotThrow(reader::readProperties);
		assertEquals(4, properties.size());
		assertNotNull(properties.getProperty("key"));
		assertEquals("1", properties.getProperty("key").getString());
		assertNotNull(properties.getProperty("key.1"));
		assertEquals("alue2", properties.getProperty("key.1").getString());
		assertNotNull(properties.getProperty("key.2"));
		assertEquals("", properties.getProperty("key.2").getString());
		assertNotNull(properties.getProperty("key.3.4"));
		assertEquals(" value4", properties.getProperty("key.3.4").getString());
		
		// Empty key part
		reader = createReader("key..test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Blank key part
		reader = createReader("key. .test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Illegal key end
		reader = createReader("key.test. = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Empty variable key part
		reader = createReader("key.${}.test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Blank variable key part
		reader = createReader("key.${ }.test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// No target type specified
		reader = createReader("key.${key.test}.test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Too many arguments specified
		reader = createReader("key.${custom?key.test?10?11}.test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Nested compacted key part
		reader = createReader("key.${env?key[1|2]?10?11}.test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Nested variable key part
		reader = createReader("key.${env?key.${sys?test}?10?11}.test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Unknown target type
		reader = createReader("key.${custom?key.test}.test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Key not found -> No default value
		reader = createReader("key.${sys?key.test}.test = value1", ADVANCED_DEFAULT_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
	}
	
	@Test
	void readVariablePropertiesCustomConfig() {
		System.setProperty("sys.custom", "b");
		PropertyReader reader;
		Properties properties;
		
		reader = createReader("key : a" + System.lineSeparator() + "key.${?key} :value2" + System.lineSeparator() + "key.${sys?sys.custom}:" + System.lineSeparator() + "key.${env?env.custom?}.${property?key.test?d}: value4", CUSTOM_CONFIG);
		properties = assertDoesNotThrow(reader::readProperties);
		assertEquals(4, properties.size());
		assertNotNull(properties.getProperty("key"));
		assertEquals("a", properties.getProperty("key").getString());
		assertNotNull(properties.getProperty("key.a"));
		assertEquals("alue2", properties.getProperty("key.a").getString());
		assertNotNull(properties.getProperty("key.b"));
		assertEquals("", properties.getProperty("key.b").getString());
		assertNotNull(properties.getProperty("key.c.d"));
		assertEquals(" value4", properties.getProperty("key.c.d").getString());
		
		// Empty key part
		reader = createReader("key..test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Blank key part
		reader = createReader("key. .test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Illegal key end
		reader = createReader("key.test. : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Empty variable key part
		reader = createReader("key.${}.test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Blank variable key part
		reader = createReader("key.${ }.test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// No target type specified
		reader = createReader("key.${key.test}.test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Too many arguments specified
		reader = createReader("key.${custom?key.test?10?11}.test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Nested compacted key part
		reader = createReader("key.${env?key[1|2]?10?11}.test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Nested variable key part
		reader = createReader("key.${env?key.${sys?test}?10?11}.test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Unknown target type
		reader = createReader("key.${custom?key.test}.test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
		
		// Key not found -> No default value
		reader = createReader("key.${sys?key.test}.test : value1", CUSTOM_CONFIG);
		assertThrows(PropertySyntaxException.class, reader::readProperties);
	}
	
	@Test
	void close() {
		assertDoesNotThrow(() -> new PropertyReader(new InputProvider(InputStream.nullInputStream())).close());
	}
	
	//region Internal classes
	@MockObject(InputStream.class)
	private static class StringInputStream extends InputStream {
		
		private final String string;
		private int index;
		
		private StringInputStream(@NotNull String string) {
			this.string = Objects.requireNonNull(string, "String must not be null");
		}
		
		@Override
		public int read() throws IOException {
			return this.index < this.string.length() ? this.string.charAt(this.index++) : -1;
		}
		
		public void reset() {
			this.index = 0;
		}
	}
	//endregion
}
