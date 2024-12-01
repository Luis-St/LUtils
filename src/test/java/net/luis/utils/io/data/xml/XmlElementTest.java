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

package net.luis.utils.io.data.xml;

import net.luis.utils.io.data.xml.exception.NoSuchXmlAttributeException;
import net.luis.utils.io.data.xml.exception.XmlTypeException;
import net.luis.utils.io.reader.ScopedStringReader;
import net.luis.utils.io.reader.StringReader;
import net.luis.utils.util.ValueParser;
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
	
	private static final XmlConfig CUSTOM_CONFIG = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new XmlElement(null));
		assertThrows(NullPointerException.class, () -> new XmlElement(null, new XmlAttributes()));
		assertThrows(NullPointerException.class, () -> new XmlElement("test", null));
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
	}
	
	@Test
	void isXmlValue() {
		assertFalse(new XmlElement("test").isXmlValue());
	}
	
	@Test
	void getAsXmlContainer() {
		assertThrows(XmlTypeException.class, () -> new XmlElement("test").getAsXmlContainer());
	}
	
	@Test
	void getAsXmlValue() {
		assertThrows(XmlTypeException.class, () -> new XmlElement("test").getAsXmlValue());
	}
	
	@Test
	void getName() {
		assertEquals("test", new XmlElement("test").getName());
	}
	
	@Test
	void getAttributes() {
		XmlElement element = new XmlElement("test");
		assertEquals(new XmlAttributes(), element.getAttributes());
		element.addAttribute("test", "test");
		assertEquals(1, element.getAttributes().size());
	}
	
	@Test
	void addAttribute() {
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
		
		assertNull(assertDoesNotThrow(() -> element.addAttribute(new XmlAttribute("test_attribute", 0))));
		assertEquals(1, element.getAttributes().size());
		assertNull(assertDoesNotThrow(() -> element.addAttribute("test_string", (String) null)));
		assertEquals(2, element.getAttributes().size());
		assertNull(assertDoesNotThrow(() -> element.addAttribute("test_boolean", true)));
		assertEquals(3, element.getAttributes().size());
		assertNull(assertDoesNotThrow(() -> element.addAttribute("test_number", (Number) null)));
		assertEquals(4, element.getAttributes().size());
		assertNull(assertDoesNotThrow(() -> element.addAttribute("test_byte", (byte) 0)));
		assertEquals(5, element.getAttributes().size());
		assertNull(assertDoesNotThrow(() -> element.addAttribute("test_short", (short) 0)));
		assertEquals(6, element.getAttributes().size());
		assertNull(assertDoesNotThrow(() -> element.addAttribute("test_integer", 0)));
		assertEquals(7, element.getAttributes().size());
		assertNull(assertDoesNotThrow(() -> element.addAttribute("test_long", 0L)));
		assertEquals(8, element.getAttributes().size());
		assertNull(assertDoesNotThrow(() -> element.addAttribute("test_float", 0.0F)));
		assertEquals(9, element.getAttributes().size());
		assertNull(assertDoesNotThrow(() -> element.addAttribute("test_double", 0.0)));
		assertEquals(10, element.getAttributes().size());
		
		assertNotNull(element.addAttribute("test_attribute", "test"));
	}
	
	@Test
	void getAttribute() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttribute(null));
		assertNull(element.getAttribute("test"));
		element.addAttribute("test", "test");
		assertNotNull(element.getAttribute("test"));
	}
	
	@Test
	void getAttributeAsString() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttributeAsString(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsString("test"));
		element.addAttribute("test", "test");
		assertEquals("test", element.getAttributeAsString("test"));
	}
	
	@Test
	void getAttributeAsBoolean() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttributeAsBoolean(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsBoolean("test"));
		element.addAttribute("test", true);
		assertTrue(element.getAttributeAsBoolean("test"));
	}
	
	@Test
	void getAttributeAsNumber() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttributeAsNumber(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsNumber("test"));
		element.addAttribute("test", 0);
		assertEquals(0L, element.getAttributeAsNumber("test"));
	}
	
	@Test
	void getAttributeAsByte() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttributeAsByte(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsByte("test"));
		element.addAttribute("test", (byte) 0);
		assertEquals((byte) 0, element.getAttributeAsByte("test"));
	}
	
	@Test
	void getAttributeAsShort() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttributeAsShort(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsShort("test"));
		element.addAttribute("test", (short) 0);
		assertEquals((short) 0, element.getAttributeAsShort("test"));
	}
	
	@Test
	void getAttributeAsInteger() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttributeAsInteger(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsInteger("test"));
		element.addAttribute("test", 0);
		assertEquals(0, element.getAttributeAsInteger("test"));
	}
	
	@Test
	void getAttributeAsLong() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttributeAsLong(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsLong("test"));
		element.addAttribute("test", 0L);
		assertEquals(0L, element.getAttributeAsLong("test"));
	}
	
	@Test
	void getAttributeAsFloat() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttributeAsFloat(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsFloat("test"));
		element.addAttribute("test", 0.0F);
		assertEquals(0.0F, element.getAttributeAsFloat("test"));
	}
	
	@Test
	void getAttributeAsDouble() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttributeAsDouble(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAsDouble("test"));
		element.addAttribute("test", 0.0);
		assertEquals(0.0, element.getAttributeAsDouble("test"));
	}
	
	@Test
	void getAttributeAs() {
		ValueParser<String, List<Boolean>> parser = string -> new ScopedStringReader(string).readList(StringReader::readBoolean);
		
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.getAttributeAsDouble(null));
		assertThrows(NoSuchXmlAttributeException.class, () -> element.getAttributeAs("test", parser));
		element.addAttribute("test", "[true, false]");
		assertIterableEquals(List.of(true, false), element.getAttributeAs("test", parser));
	}
	
	@Test
	void toBaseStringDefaultConfig() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.toBaseString(null));
		assertEquals("<test/>", element.toBaseString(XmlConfig.DEFAULT).toString());
		element.addAttribute("test", "test");
		assertEquals("<test test=\"test\"/>", element.toBaseString(XmlConfig.DEFAULT).toString());
	}
	
	@Test
	void toBaseStringCustomConfig() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.toBaseString(null));
		assertEquals("<test/>", element.toBaseString(CUSTOM_CONFIG).toString());
		element.addAttribute("test", "test");
		assertThrows(IllegalStateException.class, () -> element.toBaseString(CUSTOM_CONFIG).toString());
	}
	
	@Test
	void toStringDefaultConfig() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.toBaseString(null));
		assertEquals("<test/>", element.toBaseString(XmlConfig.DEFAULT).toString());
		element.addAttribute("test", "test");
		assertEquals("<test test=\"test\"/>", element.toBaseString(XmlConfig.DEFAULT).toString());
	}
	
	@Test
	void toStringCustomConfig() {
		XmlElement element = new XmlElement("test");
		assertThrows(NullPointerException.class, () -> element.toBaseString(null));
		assertEquals("<test/>", element.toString(CUSTOM_CONFIG));
		element.addAttribute("test", "test");
		assertThrows(IllegalStateException.class, () -> element.toString(CUSTOM_CONFIG));
	}
}
