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

package net.luis.utils.io.data.xml;

import net.luis.utils.function.throwable.ThrowableFunction;
import net.luis.utils.io.data.xml.exception.NoSuchXmlAttributeException;
import net.luis.utils.io.data.xml.exception.XmlTypeException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlElement}.<br>
 *
 * @author Luis-St
 */
class XmlElementTest {
	
	@Test
	void constructorWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new XmlElement(null));
		assertThrows(NullPointerException.class, () -> new XmlElement(null, new XmlAttributes()));
		assertThrows(NullPointerException.class, () -> new XmlElement("test", null));
	}
	
	@Test
	void constructorWithValidParameters() {
		assertDoesNotThrow(() -> new XmlElement("test"));
		assertDoesNotThrow(() -> new XmlElement("test", new XmlAttributes()));
		assertDoesNotThrow(() -> new XmlElement("valid-name"));
		assertDoesNotThrow(() -> new XmlElement("_underscore"));
		assertDoesNotThrow(() -> new XmlElement("name123"));
		assertDoesNotThrow(() -> new XmlElement("ns:element"));
	}
	
	@Test
	void constructorWithInvalidName() {
		assertThrows(IllegalArgumentException.class, () -> new XmlElement(""));
		assertThrows(IllegalArgumentException.class, () -> new XmlElement(" "));
		assertThrows(IllegalArgumentException.class, () -> new XmlElement("1invalid"));
		assertThrows(IllegalArgumentException.class, () -> new XmlElement(":invalid"));
		assertThrows(IllegalArgumentException.class, () -> new XmlElement("invalid space"));
	}
	
	@Test
	void getElementType() {
		assertEquals("xml self-closing element", new XmlElement("test").getElementType());
	}
	
	@Test
	void isSelfClosing() {
		assertTrue(new XmlElement("test").isSelfClosing());
	}
	
	@Test
	void isXmlContainer() {
		assertFalse(new XmlElement("test").isXmlContainer());
		assertTrue(new XmlContainer("test").isXmlContainer());
	}
	
	@Test
	void isXmlValue() {
		assertFalse(new XmlElement("test").isXmlValue());
		assertTrue(new XmlValue("test", "value").isXmlValue());
	}
	
	@Test
	void getAsXmlContainer() {
		XmlElement element = new XmlElement("test");
		XmlContainer container = new XmlContainer("test");
		
		assertThrows(XmlTypeException.class, element::getAsXmlContainer);
		assertEquals(container, container.getAsXmlContainer());
	}
	
	@Test
	void getAsXmlValue() {
		XmlElement element = new XmlElement("test");
		XmlValue value = new XmlValue("test", "value");
		
		assertThrows(XmlTypeException.class, element::getAsXmlValue);
		assertEquals(value, value.getAsXmlValue());
	}
	
	@Test
	void getName() {
		assertEquals("test", new XmlElement("test").getName());
		assertEquals("element-name", new XmlElement("element-name").getName());
		assertEquals("_underscore", new XmlElement("_underscore").getName());
		assertEquals("ns:element", new XmlElement("ns:element").getName());
	}
	
	@Test
	void getAttributes() {
		XmlElement element = new XmlElement("test");
		assertNotNull(element.getAttributes());
		assertEquals(0, element.getAttributes().size());
		
		element.addAttribute("test", "value");
		assertEquals(1, element.getAttributes().size());
		
		XmlAttributes attributes = new XmlAttributes();
		attributes.add("existing", "value");
		XmlElement elementWithAttributes = new XmlElement("test", attributes);
		assertEquals(1, elementWithAttributes.getAttributes().size());
	}
	
	@Test
	void addAttributeWithNull() {
		XmlElement element = new XmlElement("test");
		
		assertThrows(NullPointerException.class, () -> element.addAttribute(null));
		assertThrows(NullPointerException.class, () -> element.addAttribute(null, "test"));
		assertThrows(NullPointerException.class, () -> element.addAttribute(null, true));
		assertThrows(NullPointerException.class, () -> element.addAttribute(null, (Number) null));
		assertThrows(NullPointerException.class, () -> element.addAttribute(null, (byte) 0));
		assertThrows(NullPointerException.class, () -> element.addAttribute(null, (short) 0));
		assertThrows(NullPointerException.class, () -> element.addAttribute(null, 0));
		assertThrows(NullPointerException.class, () -> element.addAttribute(null, 0L));
		assertThrows(NullPointerException.class, () -> element.addAttribute(null, 0.0F));
		assertThrows(NullPointerException.class, () -> element.addAttribute(null, 0.0));
	}
	
	@Test
	void addAttributeSuccess() {
		XmlElement element = new XmlElement("test");
		
		assertNull(element.addAttribute(new XmlAttribute("attr", "value")));
		assertEquals(1, element.getAttributes().size());
		
		assertNull(element.addAttribute("string", "value"));
		assertNull(element.addAttribute("boolean", true));
		assertNull(element.addAttribute("number", (Number) 42));
		assertNull(element.addAttribute("byte", (byte) 127));
		assertNull(element.addAttribute("short", (short) 1000));
		assertNull(element.addAttribute("int", 100000));
		assertNull(element.addAttribute("long", 1000000L));
		assertNull(element.addAttribute("float", 3.14F));
		assertNull(element.addAttribute("double", 2.718));
		assertEquals(10, element.getAttributes().size());
	}
	
	@Test
	void addAttributeReplacement() {
		XmlElement element = new XmlElement("test");
		XmlAttribute originalAttribute = new XmlAttribute("attr", "original");
		XmlAttribute newAttribute = new XmlAttribute("attr", "new");
		
		element.addAttribute(originalAttribute);
		XmlAttribute replaced = element.addAttribute(newAttribute);
		assertEquals(originalAttribute, replaced);
		assertEquals(1, element.getAttributes().size());
		assertEquals("new", element.getAttributeAsString("attr"));
	}
	
	@Test
	void getAttribute() {
		XmlElement element = new XmlElement("test");
		XmlAttribute attribute = new XmlAttribute("attr", "value");
		
		assertThrows(NullPointerException.class, () -> element.getAttribute(null));
		assertNull(element.getAttribute("nonexistent"));
		
		element.addAttribute(attribute);
		assertEquals(attribute, element.getAttribute("attr"));
		assertNull(element.getAttribute("other"));
	}
	
	@Test
	void getAttributeAsString() {
		XmlElement element = new XmlElement("test");
		
		assertThrows(NullPointerException.class, () -> element.getAttributeAsString(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsString("nonexistent"));
		
		element.addAttribute("attr", "value");
		assertEquals("value", element.getAttributeAsString("attr"));
		
		element.addAttribute("escaped", "<test>");
		assertEquals("<test>", element.getAttributeAsString("escaped"));
	}
	
	@Test
	void getAttributeAsBoolean() {
		XmlElement element = new XmlElement("test");
		
		assertThrows(NullPointerException.class, () -> element.getAttributeAsBoolean(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsBoolean("nonexistent"));
		
		element.addAttribute("true", true);
		element.addAttribute("false", false);
		assertTrue(element.getAttributeAsBoolean("true"));
		assertFalse(element.getAttributeAsBoolean("false"));
	}
	
	@Test
	void getAttributeAsNumber() {
		XmlElement element = new XmlElement("test");
		
		assertThrows(NullPointerException.class, () -> element.getAttributeAsNumber(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsNumber("nonexistent"));
		
		element.addAttribute("int", 42);
		element.addAttribute("double", 3.14);
		assertEquals(42L, element.getAttributeAsNumber("int"));
		assertEquals(3.14, element.getAttributeAsNumber("double"));
	}
	
	@Test
	void getAttributeAsPrimitiveNumbers() {
		XmlElement element = new XmlElement("test");
		element.addAttribute("byte", (byte) 127);
		element.addAttribute("short", (short) 1000);
		element.addAttribute("int", 100000);
		element.addAttribute("long", 1000000L);
		element.addAttribute("float", 3.14F);
		element.addAttribute("double", 2.718);
		
		assertThrows(NullPointerException.class, () -> element.getAttributeAsByte(null));
		assertThrows(NullPointerException.class, () -> element.getAttributeAsShort(null));
		assertThrows(NullPointerException.class, () -> element.getAttributeAsInteger(null));
		assertThrows(NullPointerException.class, () -> element.getAttributeAsLong(null));
		assertThrows(NullPointerException.class, () -> element.getAttributeAsFloat(null));
		assertThrows(NullPointerException.class, () -> element.getAttributeAsDouble(null));
		
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsByte("nonexistent"));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsShort("nonexistent"));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsInteger("nonexistent"));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsLong("nonexistent"));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsFloat("nonexistent"));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsDouble("nonexistent"));
		
		assertEquals((byte) 127, element.getAttributeAsByte("byte"));
		assertEquals((short) 1000, element.getAttributeAsShort("short"));
		assertEquals(100000, element.getAttributeAsInteger("int"));
		assertEquals(1000000L, element.getAttributeAsLong("long"));
		assertEquals(3.14F, element.getAttributeAsFloat("float"));
		assertEquals(2.718, element.getAttributeAsDouble("double"));
	}
	
	@Test
	void getAttributeAs() {
		ThrowableFunction<String, List<Boolean>, Exception> parser = value ->
			new ScopedStringReader(String.valueOf(value)).readList(StringReader::readBoolean);
		
		XmlElement element = new XmlElement("test");
		
		assertThrows(NullPointerException.class, () -> element.getAttributeAs(null, parser));
		assertThrows(NullPointerException.class, () -> element.getAttributeAs("attr", null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAs("nonexistent", parser));
		
		element.addAttribute("list", "[true, false]");
		assertIterableEquals(List.of(true, false), element.getAttributeAs("list", parser));
	}
	
	@Test
	void equalsAndHashCode() {
		XmlElement element1 = new XmlElement("test");
		XmlElement element2 = new XmlElement("test");
		XmlElement element3 = new XmlElement("other");
		
		assertEquals(element1, element2);
		assertEquals(element1.hashCode(), element2.hashCode());
		assertNotEquals(element1, element3);
		
		element1.addAttribute("attr", "value");
		assertNotEquals(element1, element2);
		
		element2.addAttribute("attr", "value");
		assertEquals(element1, element2);
		assertEquals(element1.hashCode(), element2.hashCode());
		
		element2.addAttribute("other", "value");
		assertNotEquals(element1, element2);
		
		assertNotEquals(element1, null);
		assertNotEquals(element1, "string");
		assertNotEquals(element1, new XmlContainer("test"));
		assertNotEquals(element1, new XmlValue("test", "value"));
	}
	
	@Test
	void toBaseStringWithDefaultConfig() {
		XmlElement element = new XmlElement("test");
		
		assertThrows(NullPointerException.class, () -> element.toBaseString(null));
		assertEquals("<test/>", element.toBaseString(XmlConfig.DEFAULT).toString());
		
		element.addAttribute("attr", "value");
		assertEquals("<test attr=\"value\"/>", element.toBaseString(XmlConfig.DEFAULT).toString());
		
		element.addAttribute("other", "test");
		assertEquals("<test attr=\"value\" other=\"test\"/>", element.toBaseString(XmlConfig.DEFAULT).toString());
	}
	
	@Test
	void toBaseStringWithCustomConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		XmlElement element = new XmlElement("test");
		
		assertEquals("<test/>", element.toBaseString(customConfig).toString());
		
		element.addAttribute("attr", "value");
		assertThrows(IllegalStateException.class, () -> element.toBaseString(customConfig));
	}
	
	@Test
	void toStringWithDefaultConfig() {
		XmlElement element = new XmlElement("test");
		
		assertEquals("<test/>", element.toString());
		assertEquals("<test/>", element.toString(XmlConfig.DEFAULT));
		
		element.addAttribute("attr", "value");
		assertEquals("<test attr=\"value\"/>", element.toString(XmlConfig.DEFAULT));
	}
	
	@Test
	void toStringWithCustomConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		XmlElement element = new XmlElement("test");
		
		assertEquals("<test/>", element.toString(customConfig));
		
		element.addAttribute("attr", "value");
		assertThrows(IllegalStateException.class, () -> element.toString(customConfig));
	}
	
	@Test
	void toStringWithSpecialCharacters() {
		XmlElement element = new XmlElement("test");
		element.addAttribute("special", "<>&\"'");
		
		String result = element.toString();
		assertTrue(result.contains("&lt;&gt;&amp;&quot;&apos;"));
		assertFalse(result.contains("<>&\"'"));
	}
}
