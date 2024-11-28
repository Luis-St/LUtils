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

package net.luis.utils.io.data.properties;

import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Property}.<br>
 *
 * @author Luis-St
 */
class PropertyTest {
	
	private static final PropertyConfig CUSTOM = new PropertyConfig(':', 0, Set.of('#'), Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile(".*"), false, StandardCharsets.UTF_8);
	
	@Test
	void of() {
		assertThrows(NullPointerException.class, () -> Property.of(null, null));
		assertThrows(NullPointerException.class, () -> Property.of("key", null));
		assertThrows(NullPointerException.class, () -> Property.of(null, "value"));
		assertDoesNotThrow(() -> Property.of("key", "value"));
		assertNotNull(Property.of("key", "value"));
	}
	
	@Test
	void getKey() {
		assertEquals("key0", Property.of("key0", "value").getKey());
		assertEquals("key1", Property.of("key1", "value").getKey());
	}
	
	@Test
	void getRawValue() {
		assertEquals("value0", Property.of("key", "value0").getRawValue());
		assertEquals("value1", Property.of("key", "value1").getRawValue());
	}
	
	@Test
	void getString() {
		assertEquals("value0", Property.of("key", "value0").getString());
		assertEquals("value1", Property.of("key", "value1").getString());
	}
	
	@Test
	void getBoolean() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "a").getBoolean());
		assertTrue(Property.of("key", "true").getBoolean());
		assertFalse(Property.of("key", "false").getBoolean());
		
		assertTrue(Property.of("key", "a").getBoolean(true));
		assertFalse(Property.of("key", "a").getBoolean(false));
	}
	
	@Test
	void getNumber() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "a").getNumber());
		assertEquals(10L, Property.of("key", "10").getNumber());
		assertEquals(10.0, Property.of("key", "10.0").getNumber());
		Property property = Property.of("key", "10f");
		assertInstanceOf(Float.class, property.getNumber());
		assertEquals(10.0f, property.getNumber());
		
		assertEquals(10, Property.of("key", "a").getNumber(10));
		assertEquals(10.0, Property.of("key", "a").getNumber(10.0));
	}
	
	@Test
	void getByte() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0").getByte());
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10f").getByte());
		assertEquals((byte) 10, Property.of("key", "10").getByte());
		assertEquals((byte) 127, Property.of("key", "0x7F").getByte());
		assertEquals((byte) 127, Property.of("key", "0b1111111").getByte());
		
		assertEquals((byte) 10, Property.of("key", "10.0").getByte((byte) 10));
		assertEquals((byte) 10, Property.of("key", "10f").getByte((byte) 10));
	}
	
	@Test
	void getShort() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0").getShort());
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10f").getShort());
		assertEquals((short) 10, Property.of("key", "10").getShort());
		assertEquals((short) 32767, Property.of("key", "0x7FFF").getShort());
		assertEquals((short) 32767, Property.of("key", "0b111111111111111").getShort());
		
		assertEquals((short) 10, Property.of("key", "10.0").getShort((short) 10));
		assertEquals((short) 10, Property.of("key", "10f").getShort((short) 10));
	}
	
	@Test
	void getInteger() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0").getInteger());
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10f").getInteger());
		assertEquals(10, Property.of("key", "10").getInteger());
		assertEquals(2147483647, Property.of("key", "0x7FFFFFFF").getInteger());
		assertEquals(2147483647, Property.of("key", "0b1111111111111111111111111111111").getInteger());
		
		assertEquals(10, Property.of("key", "10.0").getInteger(10));
		assertEquals(10, Property.of("key", "10f").getInteger(10));
	}
	
	@Test
	void getLong() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0").getLong());
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10f").getLong());
		assertEquals(10L, Property.of("key", "10").getLong());
		assertEquals(9223372036854775807L, Property.of("key", "0x7FFFFFFFFFFFFFFF").getLong());
		assertEquals(9223372036854775807L, Property.of("key", "0b111111111111111111111111111111111111111111111111111111111111111").getLong());
		
		assertEquals(10L, Property.of("key", "10.0").getLong(10L));
		assertEquals(10L, Property.of("key", "10f").getLong(10L));
	}
	
	@Test
	void getFloat() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0d").getFloat());
		assertEquals(10.0f, Property.of("key", "10.0").getFloat());
		assertEquals(10.0f, Property.of("key", "10.0f").getFloat());
		
		assertEquals(10.0f, Property.of("key", "10.0d").getFloat(10.0f));
	}
	
	@Test
	void getDouble() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0f").getDouble());
		assertEquals(10.0, Property.of("key", "10.0").getDouble());
		assertEquals(10.0, Property.of("key", "10.0d").getDouble());
		
		assertEquals(10.0, Property.of("key", "10.0f").getDouble(10.0));
	}
	
	@Test
	void get() {
		Property property = Property.of("key", "[10, 20, 30]");
		assertThrows(NullPointerException.class, () -> property.get(null));
		assertThrows(IllegalArgumentException.class, () -> property.get(value -> new ScopedStringReader(value).readSet(StringReader::readInt)));
		assertIterableEquals(List.of(10, 20, 30), property.get(value -> new ScopedStringReader(value).readList(StringReader::readInt)));
		
		assertIterableEquals(List.of(10, 20, 30), property.get(value -> new ScopedStringReader(value).readSet(StringReader::readInt), List.of(10, 20, 30)));
	}
	
	@Test
	void isPartOfGroup() {
		Property property = Property.of("this.is.a.key", "value");
		assertTrue(property.isPartOfGroup("this"));
		assertTrue(property.isPartOfGroup("this.is"));
		assertTrue(property.isPartOfGroup("this.is.a"));
		assertFalse(property.isPartOfGroup("this.is.a.key"));
		assertFalse(property.isPartOfGroup("this.is.another.key"));
	}
	
	@Test
	void toStringDefaultConfig() {
		Property property = Property.of("key", "value");
		assertNotNull(property.toString(PropertyConfig.DEFAULT));
		assertEquals("key = value", property.toString(PropertyConfig.DEFAULT));
	}
	
	@Test
	void toStringCustomConfig() {
		Property property = Property.of("key", "value");
		assertNotNull(property.toString(CUSTOM));
		assertEquals("key:value", property.toString(CUSTOM));
	}
}
