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

package net.luis.utils.io.data.xml;

import net.luis.utils.io.data.xml.exception.NoSuchXmlAttributeException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import net.luis.utils.util.ValueParser;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlAttributes}.<br>
 *
 * @author Luis-St
 */
class XmlAttributesTest {
	
	private static final XmlConfig CUSTOM_CONFIG = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new XmlAttributes(null));
	}
	
	@Test
	void size() {
		XmlAttributes attributes = new XmlAttributes();
		assertEquals(0, attributes.size());
		attributes.add("name", "value");
		assertEquals(1, attributes.size());
	}
	
	@Test
	void isEmpty() {
		XmlAttributes attributes = new XmlAttributes();
		assertTrue(attributes.isEmpty());
		attributes.add("name", "value");
		assertFalse(attributes.isEmpty());
	}
	
	@Test
	void containsName() {
		XmlAttributes attributes = new XmlAttributes();
		assertFalse(attributes.containsName("name"));
		attributes.add("name", "value");
		assertTrue(attributes.containsName("name"));
	}
	
	@Test
	void containsValue() {
		XmlAttributes attributes = new XmlAttributes();
		XmlAttribute attribute = new XmlAttribute("name", "value");
		assertFalse(attributes.containsValue(attribute));
		attributes.add(attribute);
		assertTrue(attributes.containsValue(attribute));
	}
	
	@Test
	void nameSet() {
		XmlAttributes attributes = new XmlAttributes();
		assertEquals(Set.of(), attributes.nameSet());
		attributes.add("name", "value");
		assertEquals(Set.of("name"), attributes.nameSet());
	}
	
	@Test
	void attributes() {
		XmlAttributes attributes = new XmlAttributes();
		assertIterableEquals(Set.of(), attributes.attributes());
		XmlAttribute attribute = new XmlAttribute("name", "value");
		attributes.add(attribute);
		assertIterableEquals(Set.of(attribute), attributes.attributes());
	}
	
	@Test
	void add() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.add(null));
		assertThrows(NullPointerException.class, () -> attributes.add(null, ""));
		assertThrows(NullPointerException.class, () -> attributes.add(null, true));
		assertThrows(NullPointerException.class, () -> attributes.add(null, (Number) 0));
		assertThrows(NullPointerException.class, () -> attributes.add(null, (byte) 0));
		assertThrows(NullPointerException.class, () -> attributes.add(null, (short) 0));
		assertThrows(NullPointerException.class, () -> attributes.add(null, 0));
		assertThrows(NullPointerException.class, () -> attributes.add(null, 0L));
		assertThrows(NullPointerException.class, () -> attributes.add(null, 0.0F));
		assertThrows(NullPointerException.class, () -> attributes.add(null, 0.0D));
		
		XmlAttribute attribute1 = new XmlAttribute("name", "value");
		assertNull(attributes.add(attribute1));
		assertNull(attributes.add("name_string", "value"));
		assertNull(attributes.add("name_boolean", true));
		assertNull(attributes.add("name_number", (Number) 0));
		assertNull(attributes.add("name_byte", (byte) 0));
		assertNull(attributes.add("name_short", (short) 0));
		assertNull(attributes.add("name_integer", 0));
		assertNull(attributes.add("name_long", 0L));
		assertNull(attributes.add("name_float", 0.0F));
		assertNull(attributes.add("name_double", 0.0D));
		
		XmlAttribute attribute2 = attributes.add(new XmlAttribute("name", "value"));
		assertNotNull(attribute2);
		assertEquals(attribute1, attribute2);
	}
	
	@Test
	void remove() {
		XmlAttributes attributes = new XmlAttributes();
		assertDoesNotThrow(() -> attributes.remove((String) null));
		assertThrows(NullPointerException.class, () -> attributes.remove((XmlAttribute) null));
		assertNull(attributes.remove("name"));
		XmlAttribute attribute = new XmlAttribute("name", "value");
		attributes.add(attribute);
		assertEquals(1, attributes.size());
		assertEquals(attribute, attributes.remove("name"));
		assertEquals(0, attributes.size());
	}
	
	@Test
	void clear() {
		XmlAttributes attributes = new XmlAttributes();
		attributes.add("name", "value");
		assertEquals(1, attributes.size());
		attributes.clear();
		assertEquals(0, attributes.size());
	}
	
	@Test
	void replace() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.replace(null, new XmlAttribute("name", "value")));
		assertThrows(NullPointerException.class, () -> attributes.replace("name", null));
		XmlAttribute attribute1 = new XmlAttribute("name", "value");
		attributes.add(attribute1);
		assertEquals(1, attributes.size());
		XmlAttribute attribute2 = new XmlAttribute("name", "new_value");
		assertEquals(attribute1, attributes.replace("name", attribute2));
		assertEquals(1, attributes.size());
		assertEquals("new_value", attributes.getAsString("name"));
		
		XmlAttribute attribute3 = new XmlAttribute("name", "new_new_value");
		assertThrows(NullPointerException.class, () -> attributes.replace(null, null, attribute3));
		assertThrows(NullPointerException.class, () -> attributes.replace("name", null, null));
		assertFalse(assertDoesNotThrow(() -> attributes.replace("name", null, attribute3)));
		assertFalse(attributes.replace("name", attribute1, attribute3));
		assertTrue(attributes.replace("name", attribute2, attribute3));
	}
	
	@Test
	void get() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.get(null));
		assertNull(attributes.get("name"));
		XmlAttribute attribute = new XmlAttribute("name", "value");
		attributes.add(attribute);
		assertEquals(attribute, attributes.get("name"));
	}
	
	@Test
	void getAsString() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.getAsString(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsString("name"));
		attributes.add(new XmlAttribute("name", "value"));
		assertEquals("value", attributes.getAsString("name"));
	}
	
	@Test
	void getAsBoolean() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.getAsBoolean(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsBoolean("name"));
		attributes.add(new XmlAttribute("name", true));
		assertTrue(attributes.getAsBoolean("name"));
	}
	
	@Test
	void getAsNumber() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.getAsNumber(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsNumber("name"));
		attributes.add(new XmlAttribute("name", 0));
		assertEquals(0L, attributes.getAsNumber("name"));
	}
	
	@Test
	void getAsByte() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.getAsByte(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsByte("name"));
		attributes.add(new XmlAttribute("name", (byte) 0));
		assertEquals((byte) 0, attributes.getAsByte("name"));
	}
	
	@Test
	void getAsShort() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.getAsShort(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsShort("name"));
		attributes.add(new XmlAttribute("name", (short) 0));
		assertEquals((short) 0, attributes.getAsShort("name"));
	}
	
	@Test
	void getAsInteger() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.getAsInteger(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsInteger("name"));
		attributes.add(new XmlAttribute("name", 0));
		assertEquals(0, attributes.getAsInteger("name"));
	}
	
	@Test
	void getAsLong() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.getAsLong(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsLong("name"));
		attributes.add(new XmlAttribute("name", 0L));
		assertEquals(0L, attributes.getAsLong("name"));
	}
	
	@Test
	void getAsFloat() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.getAsFloat(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsFloat("name"));
		attributes.add(new XmlAttribute("name", 0.0F));
		assertEquals(0.0F, attributes.getAsFloat("name"));
	}
	
	@Test
	void getAsDouble() {
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.getAsDouble(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsDouble("name"));
		attributes.add(new XmlAttribute("name", 0.0D));
		assertEquals(0.0D, attributes.getAsDouble("name"));
	}
	
	@Test
	void getAs() {
		ValueParser<String, List<Boolean>> parser = value -> new ScopedStringReader(String.valueOf(value)).readList(StringReader::readBoolean);
		
		XmlAttributes attributes = new XmlAttributes();
		assertThrows(NullPointerException.class, () -> attributes.getAs(null, parser));
		assertThrows(NullPointerException.class, () -> attributes.getAs("name", null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAs("name", parser));
		attributes.add(new XmlAttribute("name", "[]"));
		assertIterableEquals(List.of(), attributes.getAs("name", parser));
		attributes.add(new XmlAttribute("name", "[true, false]"));
		assertIterableEquals(List.of(true, false), attributes.getAs("name", parser));
	}
	
	@Test
	void toStringDefaultConfig() {
		XmlAttributes attributes = new XmlAttributes();
		assertDoesNotThrow(() -> attributes.toString(null));
		assertEquals("", attributes.toString(XmlConfig.DEFAULT));
		attributes.add("name", "value");
		assertEquals("name=\"value\"", attributes.toString(XmlConfig.DEFAULT));
		attributes.add("test", 0.0);
		assertEquals("name=\"value\" test=\"0.0\"", attributes.toString(XmlConfig.DEFAULT));
	}
	
	@Test
	void toStringCustomConfig() {
		XmlAttributes attributes = new XmlAttributes();
		assertDoesNotThrow(() -> attributes.toString(null));
		assertEquals("", attributes.toString(CUSTOM_CONFIG));
		attributes.add("name", "value");
		assertEquals("name=\"value\"", attributes.toString(CUSTOM_CONFIG));
		attributes.add("test", 0.0);
		assertEquals("name=\"value\" test=\"0.0\"", attributes.toString(CUSTOM_CONFIG));
	}
}
