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

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.data.xml.exception.NoSuchXmlAttributeException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlAttributes}.<br>
 *
 * @author Luis-St
 */
class XmlAttributesTest {
	
	@Test
	void constructorWithNullMap() {
		assertThrows(NullPointerException.class, () -> new XmlAttributes(null));
	}
	
	@Test
	void constructorWithValidMap() {
		Map<String, XmlAttribute> map = Map.of(
			"attr1", new XmlAttribute("attr1", "value1"),
			"attr2", new XmlAttribute("attr2", "value2")
		);
		XmlAttributes attributes = new XmlAttributes(map);
		assertEquals(2, attributes.size());
		assertTrue(attributes.containsName("attr1"));
		assertTrue(attributes.containsName("attr2"));
	}
	
	@Test
	void constructorEmpty() {
		XmlAttributes attributes = new XmlAttributes();
		assertEquals(0, attributes.size());
		assertTrue(attributes.isEmpty());
	}
	
	@Test
	void size() {
		XmlAttributes attributes = new XmlAttributes();
		assertEquals(0, attributes.size());
		
		attributes.add("name", "value");
		assertEquals(1, attributes.size());
		
		attributes.add("other", "value");
		assertEquals(2, attributes.size());
		
		attributes.remove("name");
		assertEquals(1, attributes.size());
	}
	
	@Test
	void isEmpty() {
		XmlAttributes attributes = new XmlAttributes();
		assertTrue(attributes.isEmpty());
		
		attributes.add("name", "value");
		assertFalse(attributes.isEmpty());
		
		attributes.clear();
		assertTrue(attributes.isEmpty());
	}
	
	@Test
	void containsName() {
		XmlAttributes attributes = new XmlAttributes();
		assertFalse(attributes.containsName("name"));
		assertFalse(attributes.containsName(null));
		
		attributes.add("name", "value");
		assertTrue(attributes.containsName("name"));
		assertFalse(attributes.containsName("other"));
		assertFalse(attributes.containsName(null));
	}
	
	@Test
	void containsValue() {
		XmlAttributes attributes = new XmlAttributes();
		XmlAttribute attribute = new XmlAttribute("name", "value");
		XmlAttribute otherAttribute = new XmlAttribute("other", "value");
		
		assertFalse(attributes.containsValue(attribute));
		assertFalse(attributes.containsValue(null));
		
		attributes.add(attribute);
		assertTrue(attributes.containsValue(attribute));
		assertFalse(attributes.containsValue(otherAttribute));
		assertFalse(attributes.containsValue(null));
	}
	
	@Test
	void nameSet() {
		XmlAttributes attributes = new XmlAttributes();
		assertEquals(Set.of(), attributes.nameSet());
		
		attributes.add("name", "value");
		assertEquals(Set.of("name"), attributes.nameSet());
		
		attributes.add("other", "value");
		assertEquals(Set.of("name", "other"), attributes.nameSet());
		
		Set<String> nameSet = attributes.nameSet();
		assertThrows(UnsupportedOperationException.class, () -> nameSet.add("test"));
	}
	
	@Test
	void attributes() {
		XmlAttributes attributes = new XmlAttributes();
		assertTrue(attributes.attributes().isEmpty());
		
		XmlAttribute attribute1 = new XmlAttribute("name", "value");
		attributes.add(attribute1);
		assertEquals(1, attributes.attributes().size());
		assertTrue(attributes.attributes().contains(attribute1));
		
		XmlAttribute attribute2 = new XmlAttribute("other", "value");
		attributes.add(attribute2);
		assertEquals(2, attributes.attributes().size());
		assertTrue(attributes.attributes().contains(attribute1));
		assertTrue(attributes.attributes().contains(attribute2));
	}
	
	@Test
	void addXmlAttribute() {
		XmlAttributes attributes = new XmlAttributes();
		XmlAttribute attribute = new XmlAttribute("name", "value");
		
		assertThrows(NullPointerException.class, () -> attributes.add(null));
		
		assertNull(attributes.add(attribute));
		assertEquals(1, attributes.size());
		
		XmlAttribute newAttribute = new XmlAttribute("name", "new_value");
		XmlAttribute oldAttribute = attributes.add(newAttribute);
		assertEquals(attribute, oldAttribute);
		assertEquals(1, attributes.size());
		assertEquals("new_value", attributes.getAsString("name"));
	}
	
	@Test
	void addStringValue() {
		XmlAttributes attributes = new XmlAttributes();
		
		assertThrows(NullPointerException.class, () -> attributes.add(null, "value"));
		
		assertNull(attributes.add("name", "value"));
		assertEquals(1, attributes.size());
		assertEquals("value", attributes.getAsString("name"));
		
		assertNull(attributes.add("nullValue", (String) null));
		assertEquals("null", attributes.getAsString("nullValue"));
	}
	
	@Test
	void addBooleanValue() {
		XmlAttributes attributes = new XmlAttributes();
		
		assertThrows(NullPointerException.class, () -> attributes.add(null, true));
		
		assertNull(attributes.add("trueValue", true));
		assertNull(attributes.add("falseValue", false));
		assertEquals(2, attributes.size());
		assertTrue(attributes.getAsBoolean("trueValue"));
		assertFalse(attributes.getAsBoolean("falseValue"));
	}
	
	@Test
	void addNumberValue() {
		XmlAttributes attributes = new XmlAttributes();
		
		assertThrows(NullPointerException.class, () -> attributes.add(null, (Number) 0));
		
		assertNull(attributes.add("intValue", 42));
		assertNull(attributes.add("doubleValue", 3.14));
		assertNull(attributes.add("nullValue", (Number) null));
		assertEquals(3, attributes.size());
		assertEquals(42L, attributes.getAsNumber("intValue"));
		assertEquals(3.14, attributes.getAsNumber("doubleValue"));
		assertEquals("null", attributes.getAsString("nullValue"));
	}
	
	@Test
	void addPrimitiveNumbers() {
		XmlAttributes attributes = new XmlAttributes();
		
		assertThrows(NullPointerException.class, () -> attributes.add(null, (byte) 0));
		assertThrows(NullPointerException.class, () -> attributes.add(null, (short) 0));
		assertThrows(NullPointerException.class, () -> attributes.add(null, 0));
		assertThrows(NullPointerException.class, () -> attributes.add(null, 0L));
		assertThrows(NullPointerException.class, () -> attributes.add(null, 0.0F));
		assertThrows(NullPointerException.class, () -> attributes.add(null, 0.0));
		
		assertNull(attributes.add("byteValue", (byte) 127));
		assertNull(attributes.add("shortValue", (short) 1000));
		assertNull(attributes.add("intValue", 100000));
		assertNull(attributes.add("longValue", 1000000L));
		assertNull(attributes.add("floatValue", 3.14F));
		assertNull(attributes.add("doubleValue", 2.718));
		
		assertEquals(6, attributes.size());
		assertEquals((byte) 127, attributes.getAsByte("byteValue"));
		assertEquals((short) 1000, attributes.getAsShort("shortValue"));
		assertEquals(100000, attributes.getAsInteger("intValue"));
		assertEquals(1000000L, attributes.getAsLong("longValue"));
		assertEquals(3.14F, attributes.getAsFloat("floatValue"));
		assertEquals(2.718, attributes.getAsDouble("doubleValue"));
	}
	
	@Test
	void removeByName() {
		XmlAttributes attributes = new XmlAttributes();
		XmlAttribute attribute = new XmlAttribute("name", "value");
		
		assertNull(attributes.remove("nonexistent"));
		assertNull(attributes.remove((String) null));
		
		attributes.add(attribute);
		assertEquals(attribute, attributes.remove("name"));
		assertEquals(0, attributes.size());
	}
	
	@Test
	void removeByAttribute() {
		XmlAttributes attributes = new XmlAttributes();
		XmlAttribute attribute = new XmlAttribute("name", "value");
		XmlAttribute otherAttribute = new XmlAttribute("other", "value");
		
		assertThrows(NullPointerException.class, () -> attributes.remove((XmlAttribute) null));
		
		assertNull(attributes.remove(attribute));
		
		attributes.add(attribute);
		assertEquals(attribute, attributes.remove(attribute));
		assertEquals(0, attributes.size());
		
		attributes.add(attribute);
		assertNull(attributes.remove(otherAttribute));
		assertEquals(1, attributes.size());
	}
	
	@Test
	void clear() {
		XmlAttributes attributes = new XmlAttributes();
		attributes.add("name1", "value1");
		attributes.add("name2", "value2");
		assertEquals(2, attributes.size());
		
		attributes.clear();
		assertEquals(0, attributes.size());
		assertTrue(attributes.isEmpty());
	}
	
	@Test
	void replaceAttribute() {
		XmlAttributes attributes = new XmlAttributes();
		XmlAttribute oldAttribute = new XmlAttribute("name", "old_value");
		XmlAttribute newAttribute = new XmlAttribute("name", "new_value");
		
		assertThrows(NullPointerException.class, () -> attributes.replace(null, newAttribute));
		assertThrows(NullPointerException.class, () -> attributes.replace("name", null));
		
		assertNull(attributes.replace("name", newAttribute));
		
		attributes.add(oldAttribute);
		assertEquals(oldAttribute, attributes.replace("name", newAttribute));
		assertEquals("new_value", attributes.getAsString("name"));
	}
	
	@Test
	void replaceConditional() {
		XmlAttributes attributes = new XmlAttributes();
		XmlAttribute oldAttribute = new XmlAttribute("name", "old_value");
		XmlAttribute newAttribute = new XmlAttribute("name", "new_value");
		XmlAttribute wrongAttribute = new XmlAttribute("name", "wrong_value");
		
		assertThrows(NullPointerException.class, () -> attributes.replace(null, null, newAttribute));
		assertThrows(NullPointerException.class, () -> attributes.replace("name", null, null));
		
		assertFalse(attributes.replace("name", oldAttribute, newAttribute));
		
		attributes.add(oldAttribute);
		assertFalse(attributes.replace("name", wrongAttribute, newAttribute));
		assertTrue(attributes.replace("name", oldAttribute, newAttribute));
		assertEquals("new_value", attributes.getAsString("name"));
		
		assertFalse(attributes.replace("name", null, newAttribute));
	}
	
	@Test
	void get() {
		XmlAttributes attributes = new XmlAttributes();
		XmlAttribute attribute = new XmlAttribute("name", "value");
		
		assertThrows(NullPointerException.class, () -> attributes.get(null));
		assertNull(attributes.get("name"));
		
		attributes.add(attribute);
		assertEquals(attribute, attributes.get("name"));
		assertNull(attributes.get("nonexistent"));
	}
	
	@Test
	void getAsString() {
		XmlAttributes attributes = new XmlAttributes();
		
		assertThrows(NullPointerException.class, () -> attributes.getAsString(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsString("name"));
		
		attributes.add("name", "value");
		assertEquals("value", attributes.getAsString("name"));
		
		attributes.add("escaped", "<test>");
		assertEquals("<test>", attributes.getAsString("escaped"));
	}
	
	@Test
	void getAsBoolean() {
		XmlAttributes attributes = new XmlAttributes();
		
		assertThrows(NullPointerException.class, () -> attributes.getAsBoolean(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsBoolean("name"));
		
		attributes.add("trueValue", true);
		attributes.add("falseValue", false);
		assertTrue(attributes.getAsBoolean("trueValue"));
		assertFalse(attributes.getAsBoolean("falseValue"));
	}
	
	@Test
	void getAsNumber() {
		XmlAttributes attributes = new XmlAttributes();
		
		assertThrows(NullPointerException.class, () -> attributes.getAsNumber(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsNumber("name"));
		
		attributes.add("intValue", 42);
		attributes.add("doubleValue", 3.14);
		assertEquals(42L, attributes.getAsNumber("intValue"));
		assertEquals(3.14, attributes.getAsNumber("doubleValue"));
	}
	
	@Test
	void getAsPrimitiveNumbers() {
		XmlAttributes attributes = new XmlAttributes();
		attributes.add("byteValue", (byte) 127);
		attributes.add("shortValue", (short) 1000);
		attributes.add("intValue", 100000);
		attributes.add("longValue", 1000000L);
		attributes.add("floatValue", 3.14F);
		attributes.add("doubleValue", 2.718);
		
		assertThrows(NullPointerException.class, () -> attributes.getAsByte(null));
		assertThrows(NullPointerException.class, () -> attributes.getAsShort(null));
		assertThrows(NullPointerException.class, () -> attributes.getAsInteger(null));
		assertThrows(NullPointerException.class, () -> attributes.getAsLong(null));
		assertThrows(NullPointerException.class, () -> attributes.getAsFloat(null));
		assertThrows(NullPointerException.class, () -> attributes.getAsDouble(null));
		
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsByte("nonexistent"));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsShort("nonexistent"));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsInteger("nonexistent"));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsLong("nonexistent"));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsFloat("nonexistent"));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAsDouble("nonexistent"));
		
		assertEquals((byte) 127, attributes.getAsByte("byteValue"));
		assertEquals((short) 1000, attributes.getAsShort("shortValue"));
		assertEquals(100000, attributes.getAsInteger("intValue"));
		assertEquals(1000000L, attributes.getAsLong("longValue"));
		assertEquals(3.14F, attributes.getAsFloat("floatValue"));
		assertEquals(2.718, attributes.getAsDouble("doubleValue"));
	}
	
	@Test
	void getAsWithParser() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = value ->
			new ScopedStringReader(String.valueOf(value)).readList(StringReader::readBoolean);
		
		XmlAttributes attributes = new XmlAttributes();
		
		assertThrows(NullPointerException.class, () -> attributes.getAs(null, parser));
		assertThrows(NullPointerException.class, () -> attributes.getAs("name", null));
		assertThrows(NoSuchXmlAttributeException.class, () -> attributes.getAs("name", parser));
		
		attributes.add("empty", "[]");
		assertIterableEquals(List.of(), attributes.getAs("empty", parser));
		
		attributes.add("list", "[true, false]");
		assertIterableEquals(List.of(true, false), attributes.getAs("list", parser));
	}
	
	@Test
	void equalsAndHashCode() {
		XmlAttributes attributes1 = new XmlAttributes();
		XmlAttributes attributes2 = new XmlAttributes();
		XmlAttributes attributes3 = new XmlAttributes();
		
		assertEquals(attributes1, attributes2);
		assertEquals(attributes1.hashCode(), attributes2.hashCode());
		
		attributes1.add("name", "value");
		assertNotEquals(attributes1, attributes2);
		
		attributes2.add("name", "value");
		assertEquals(attributes1, attributes2);
		assertEquals(attributes1.hashCode(), attributes2.hashCode());
		
		attributes3.add("other", "value");
		assertNotEquals(attributes1, attributes3);
		
		assertNotEquals(attributes1, null);
		assertNotEquals(attributes1, "string");
	}
	
	@Test
	void toStringWithDefaultConfig() {
		XmlAttributes attributes = new XmlAttributes();
		assertEquals("", attributes.toString());
		
		attributes.add("name", "value");
		assertEquals("name=\"value\"", attributes.toString());
		
		attributes.add("test", 0.0);
		assertEquals("name=\"value\" test=\"0.0\"", attributes.toString());
	}
	
	@Test
	void toStringWithCustomConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		XmlAttributes attributes = new XmlAttributes();
		
		assertEquals("", attributes.toString(customConfig));
		
		attributes.add("name", "value");
		assertEquals("name=\"value\"", attributes.toString(customConfig));
		
		attributes.add("test", 0.0);
		assertEquals("name=\"value\" test=\"0.0\"", attributes.toString(customConfig));
	}
	
	@Test
	void toStringWithNullConfig() {
		XmlAttributes attributes = new XmlAttributes();
		assertDoesNotThrow(() -> attributes.toString(null));
	}
}
