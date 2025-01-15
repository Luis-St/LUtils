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

package net.luis.utils.io.data.property;

import net.luis.utils.function.throwable.ThrowableFunction;
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
	void getAsString() {
		assertEquals("value0", Property.of("key", "value0").getAsString());
		assertEquals("value1", Property.of("key", "value1").getAsString());
	}
	
	@Test
	void getAsBoolean() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "a").getAsBoolean());
		assertTrue(Property.of("key", "true").getAsBoolean());
		assertFalse(Property.of("key", "false").getAsBoolean());
		
		assertTrue(Property.of("key", "a").getAsBoolean(true));
		assertFalse(Property.of("key", "a").getAsBoolean(false));
	}
	
	@Test
	void getAsNumber() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "a").getAsNumber());
		assertEquals(10L, Property.of("key", "10").getAsNumber());
		assertEquals(10.0, Property.of("key", "10.0").getAsNumber());
		Property property = Property.of("key", "10f");
		assertEquals(10.0f, assertInstanceOf(Float.class, property.getAsNumber()));
		
		assertEquals(10, Property.of("key", "a").getAsNumber(10));
		assertEquals(10.0, Property.of("key", "a").getAsNumber(10.0));
	}
	
	@Test
	void getAsByte() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0").getAsByte());
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10f").getAsByte());
		assertEquals((byte) 10, Property.of("key", "10").getAsByte());
		assertEquals((byte) 127, Property.of("key", "0x7F").getAsByte());
		assertEquals((byte) 127, Property.of("key", "0b1111111").getAsByte());
		
		assertEquals((byte) 10, Property.of("key", "10.0").getAsByte((byte) 10));
		assertEquals((byte) 10, Property.of("key", "10f").getAsByte((byte) 10));
	}
	
	@Test
	void getAsShort() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0").getAsShort());
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10f").getAsShort());
		assertEquals((short) 10, Property.of("key", "10").getAsShort());
		assertEquals((short) 32767, Property.of("key", "0x7FFF").getAsShort());
		assertEquals((short) 32767, Property.of("key", "0b111111111111111").getAsShort());
		
		assertEquals((short) 10, Property.of("key", "10.0").getAsShort((short) 10));
		assertEquals((short) 10, Property.of("key", "10f").getAsShort((short) 10));
	}
	
	@Test
	void getAsInteger() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0").getAsInteger());
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10f").getAsInteger());
		assertEquals(10, Property.of("key", "10").getAsInteger());
		assertEquals(2147483647, Property.of("key", "0x7FFFFFFF").getAsInteger());
		assertEquals(2147483647, Property.of("key", "0b1111111111111111111111111111111").getAsInteger());
		
		assertEquals(10, Property.of("key", "10.0").getAsInteger(10));
		assertEquals(10, Property.of("key", "10f").getAsInteger(10));
	}
	
	@Test
	void getAsLong() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0").getAsLong());
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10f").getAsLong());
		assertEquals(10L, Property.of("key", "10").getAsLong());
		assertEquals(9223372036854775807L, Property.of("key", "0x7FFFFFFFFFFFFFFF").getAsLong());
		assertEquals(9223372036854775807L, Property.of("key", "0b111111111111111111111111111111111111111111111111111111111111111").getAsLong());
		
		assertEquals(10L, Property.of("key", "10.0").getAsLong(10L));
		assertEquals(10L, Property.of("key", "10f").getAsLong(10L));
	}
	
	@Test
	void getAsFloat() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0d").getAsFloat());
		assertEquals(10.0f, Property.of("key", "10.0").getAsFloat());
		assertEquals(10.0f, Property.of("key", "10.0f").getAsFloat());
		
		assertEquals(10.0f, Property.of("key", "10.0d").getAsFloat(10.0f));
	}
	
	@Test
	void getAsDouble() {
		assertThrows(IllegalArgumentException.class, () -> Property.of("key", "10.0f").getAsDouble());
		assertEquals(10.0, Property.of("key", "10.0").getAsDouble());
		assertEquals(10.0, Property.of("key", "10.0d").getAsDouble());
		
		assertEquals(10.0, Property.of("key", "10.0f").getAsDouble(10.0));
	}
	
	@Test
	void getAs() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = string -> new ScopedStringReader(string).readList(StringReader::readBoolean);
		
		assertThrows(NullPointerException.class, () -> Property.of("test", "[true, false]").getAs(null));
		assertThrows(IllegalArgumentException.class, () -> Property.of("test", "true").getAs(parser));
		assertIterableEquals(List.of(true, false), Property.of("test", "[true, false]").getAs(parser));
		
		assertThrows(NullPointerException.class, () -> Property.of("test", "true").getAs(parser, null));
		assertThrows(NullPointerException.class, () -> Property.of("test", "[true, false]").getAs(parser, null));
		assertIterableEquals(List.of(true, false), Property.of("test", "[true, false]").getAs(parser, List.of()));
		assertIterableEquals(List.of(), Property.of("test", "true").getAs(parser, List.of()));
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
