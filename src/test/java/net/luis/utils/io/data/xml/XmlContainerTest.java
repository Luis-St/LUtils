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

import net.luis.utils.io.data.xml.exception.NoSuchXmlElementException;
import net.luis.utils.io.data.xml.exception.XmlTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlContainer}.<br>
 *
 * @author Luis-St
 */
class XmlContainerTest {
	
	@Test
	void constructorWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new XmlContainer(null));
		assertThrows(NullPointerException.class, () -> new XmlContainer(null, new XmlElements()));
		assertThrows(NullPointerException.class, () -> new XmlContainer(null, new XmlAttributes()));
		assertThrows(NullPointerException.class, () -> new XmlContainer(null, new XmlAttributes(), new XmlElements()));
		assertThrows(NullPointerException.class, () -> new XmlContainer("test", (XmlElements) null));
		assertThrows(NullPointerException.class, () -> new XmlContainer("test", (XmlAttributes) null));
		assertThrows(NullPointerException.class, () -> new XmlContainer("test", null, new XmlElements()));
		assertThrows(NullPointerException.class, () -> new XmlContainer("test", new XmlAttributes(), null));
	}
	
	@Test
	void constructorWithValidParameters() {
		assertDoesNotThrow(() -> new XmlContainer("test"));
		assertDoesNotThrow(() -> new XmlContainer("test", new XmlElements()));
		assertDoesNotThrow(() -> new XmlContainer("test", new XmlAttributes()));
		assertDoesNotThrow(() -> new XmlContainer("test", new XmlAttributes(), new XmlElements()));
	}
	
	@Test
	void constructorWithInvalidName() {
		assertThrows(IllegalArgumentException.class, () -> new XmlContainer(""));
		assertThrows(IllegalArgumentException.class, () -> new XmlContainer(" "));
		assertThrows(IllegalArgumentException.class, () -> new XmlContainer("1invalid"));
	}
	
	@Test
	void getElementType() {
		assertEquals("xml container", new XmlContainer("test").getElementType());
	}
	
	@Test
	void isSelfClosing() {
		assertFalse(new XmlContainer("test").isSelfClosing());
		
		XmlContainer container = new XmlContainer("test");
		container.add(new XmlElement("child"));
		assertFalse(container.isSelfClosing());
	}
	
	@Test
	void getElements() {
		XmlContainer container = new XmlContainer("test");
		assertNotNull(container.getElements());
		assertEquals(0, container.getElements().size());
		
		container.add(new XmlElement("test"));
		assertEquals(1, container.getElements().size());
	}
	
	@Test
	void containerTypeDetection() {
		XmlContainer container = new XmlContainer("test");
		
		assertTrue(container.isUndefinedContainer());
		assertFalse(container.isContainerArray());
		assertFalse(container.isContainerObject());
		
		container.add(new XmlElement("test1"));
		assertTrue(container.isUndefinedContainer());
		assertFalse(container.isContainerArray());
		assertFalse(container.isContainerObject());
		
		container.add(new XmlElement("test1"));
		assertFalse(container.isUndefinedContainer());
		assertTrue(container.isContainerArray());
		assertFalse(container.isContainerObject());
		
		container.clear();
		container.add(new XmlElement("test1"));
		container.add(new XmlElement("test2"));
		assertFalse(container.isUndefinedContainer());
		assertFalse(container.isContainerArray());
		assertTrue(container.isContainerObject());
	}
	
	@Test
	void size() {
		XmlContainer container = new XmlContainer("test");
		assertEquals(0, container.size());
		
		container.add(new XmlElement("test1"));
		assertEquals(1, container.size());
		
		container.add(new XmlElement("test1"));
		assertEquals(2, container.size());
		
		container.remove(1);
		assertEquals(1, container.size());
		
		container.clear();
		container.add(new XmlElement("test1"));
		container.add(new XmlElement("test2"));
		assertEquals(2, container.size());
	}
	
	@Test
	void isEmpty() {
		XmlContainer container = new XmlContainer("test");
		assertTrue(container.isEmpty());
		
		container.add(new XmlElement("test1"));
		assertFalse(container.isEmpty());
		
		container.clear();
		assertTrue(container.isEmpty());
	}
	
	@Test
	void containsName() {
		XmlContainer container = new XmlContainer("test");
		assertFalse(container.containsName("test"));
		assertFalse(container.containsName(null));
		
		container.add(new XmlElement("test"));
		assertTrue(container.containsName("test"));
		assertFalse(container.containsName("other"));
		assertFalse(container.containsName(null));
	}
	
	@Test
	void containsElement() {
		XmlContainer container = new XmlContainer("test");
		XmlElement element = new XmlElement("test");
		XmlElement otherElement = new XmlElement("other");
		
		assertFalse(container.containsElement(element));
		assertFalse(container.containsElement(null));
		
		container.add(element);
		assertTrue(container.containsElement(element));
		assertFalse(container.containsElement(otherElement));
		assertFalse(container.containsElement(null));
	}
	
	@Test
	void nameSet() {
		XmlContainer container = new XmlContainer("test");
		assertEquals(Set.of(), container.nameSet());
		
		container.add(new XmlElement("test1"));
		assertEquals(Set.of("test1"), container.nameSet());
		
		container.add(new XmlElement("test2"));
		assertEquals(Set.of("test1", "test2"), container.nameSet());
		
		container.clear();
		container.add(new XmlElement("test1"));
		container.add(new XmlElement("test1"));
		assertEquals(Set.of("test1"), container.nameSet());
	}
	
	@Test
	void elements() {
		XmlContainer container = new XmlContainer("test");
		assertEquals(Collections.emptyList(), container.elements());
		
		XmlElement element1 = new XmlElement("test1");
		container.add(element1);
		assertEquals(List.of(element1), container.elements());
		
		XmlElement element2 = new XmlElement("test2");
		container.add(element2);
		assertEquals(List.of(element1, element2), container.elements());
		
		container.clear();
		container.add(element1);
		XmlElement element3 = new XmlElement("test1");
		container.add(element3);
		assertIterableEquals(List.of(element1, element3), container.elements());
	}
	
	@Test
	void addElement() {
		XmlContainer container = new XmlContainer("test");
		
		assertThrows(NullPointerException.class, () -> container.add(null));
		
		container.add(new XmlElement("test1"));
		assertEquals(1, container.size());
		
		container.add(new XmlElement("test2"));
		assertEquals(2, container.size());
		assertTrue(container.isContainerObject());
		
		assertThrows(XmlTypeException.class, () -> container.add(new XmlElement("test1")));
		
		container.clear();
		container.add(new XmlElement("test1"));
		container.add(new XmlElement("test1"));
		assertTrue(container.isContainerArray());
		
		assertThrows(XmlTypeException.class, () -> container.add(new XmlElement("test2")));
	}
	
	@Test
	void addContainer() {
		XmlContainer container = new XmlContainer("test");
		
		assertThrows(NullPointerException.class, () -> container.addContainer(null));
		
		container.addContainer(new XmlContainer("test1"));
		assertEquals(1, container.size());
		
		container.addContainer(new XmlContainer("test2"));
		assertEquals(2, container.size());
		assertTrue(container.isContainerObject());
		
		assertThrows(XmlTypeException.class, () -> container.addContainer(new XmlContainer("test1")));
	}
	
	@Test
	void addValue() {
		XmlContainer container = new XmlContainer("test");
		
		assertThrows(NullPointerException.class, () -> container.addValue(null));
		
		container.addValue(new XmlValue("test1", true));
		assertEquals(1, container.size());
		
		container.addValue(new XmlValue("test2", 0));
		assertEquals(2, container.size());
		assertTrue(container.isContainerObject());
		
		assertThrows(XmlTypeException.class, () -> container.addValue(new XmlValue("test1", false)));
	}
	
	@Test
	void removeElement() {
		XmlContainer container = new XmlContainer("test");
		XmlElement element1 = new XmlElement("test1");
		XmlElement element2 = new XmlElement("test2");
		
		assertThrows(NullPointerException.class, () -> container.remove((XmlElement) null));
		assertFalse(container.remove(element1));
		
		container.add(element1);
		container.add(element2);
		assertEquals(2, container.size());
		
		assertTrue(container.remove(element1));
		assertEquals(1, container.size());
		assertFalse(container.containsElement(element1));
		
		assertTrue(container.remove(element2));
		assertEquals(0, container.size());
	}
	
	@Test
	void removeByName() {
		XmlContainer container = new XmlContainer("test");
		XmlElement element1 = new XmlElement("test1");
		XmlElement element2 = new XmlElement("test2");
		
		assertFalse(container.remove("nonexistent"));
		
		container.add(element1);
		container.add(element2);
		assertTrue(container.isContainerObject());
		
		assertTrue(container.remove("test2"));
		assertEquals(1, container.size());
		assertFalse(container.containsName("test2"));
		
		container.add(new XmlElement("test1"));
		assertThrows(XmlTypeException.class, () -> container.remove("test1"));
	}
	
	@Test
	void removeByIndex() {
		XmlContainer container = new XmlContainer("test");
		XmlElement element1 = new XmlElement("test1");
		XmlElement element2 = new XmlElement("test2");
		
		assertFalse(container.remove(0));
		
		container.add(element1);
		container.add(element2);
		assertTrue(container.isContainerObject());
		
		assertThrows(XmlTypeException.class, () -> container.remove(0));
		
		container.clear();
		container.add(element1);
		container.add(new XmlElement("test1"));
		assertTrue(container.isContainerArray());
		
		assertTrue(container.remove(1));
		assertEquals(1, container.size());
		
		assertTrue(container.remove(0));
		assertEquals(0, container.size());
	}
	
	@Test
	void clear() {
		XmlContainer container = new XmlContainer("test");
		container.add(new XmlElement("test1"));
		container.add(new XmlElement("test2"));
		assertEquals(2, container.size());
		
		container.clear();
		assertEquals(0, container.size());
		assertTrue(container.isEmpty());
		assertTrue(container.isUndefinedContainer());
	}
	
	@Test
	void getByName() {
		XmlContainer container = new XmlContainer("test");
		XmlElement element1 = new XmlElement("test1");
		
		assertNull(container.get("test"));
		
		container.add(element1);
		assertEquals(element1, container.get("test1"));
		assertNull(container.get("nonexistent"));
		
		container.add(new XmlElement("test1"));
		assertThrows(XmlTypeException.class, () -> container.get("test1"));
	}
	
	@Test
	void getByIndex() {
		XmlContainer container = new XmlContainer("test");
		XmlElement element1 = new XmlElement("test1");
		
		assertNull(container.get(0));
		
		container.add(element1);
		assertEquals(element1, container.get(0));
		assertNull(container.get(1));
		
		container.add(new XmlElement("test2"));
		assertThrows(XmlTypeException.class, () -> container.get(0));
		
		container.clear();
		container.add(element1);
		container.add(new XmlElement("test1"));
		assertEquals(element1, container.get(0));
		assertNotNull(container.get(1));
		assertNull(container.get(2));
	}
	
	@Test
	void getAsContainer() {
		XmlContainer container = new XmlContainer("test");
		XmlContainer childContainer = new XmlContainer("child");
		XmlElement element = new XmlElement("element");
		
		assertThrows(NoSuchXmlElementException.class, () -> container.getAsContainer("test"));
		assertThrows(NoSuchXmlElementException.class, () -> container.getAsContainer(0));
		
		container.add(childContainer);
		assertEquals(childContainer, container.getAsContainer("child"));
		assertEquals(childContainer, container.getAsContainer(0));
		
		container.add(element);
		assertThrows(XmlTypeException.class, () -> container.getAsContainer("element"));
		
		container.clear();
		container.add(childContainer);
		container.add(new XmlContainer("child"));
		assertEquals(childContainer, container.getAsContainer(0));
		assertThrows(XmlTypeException.class, () -> container.getAsContainer("child"));
	}
	
	@Test
	void getAsValue() {
		XmlContainer container = new XmlContainer("test");
		XmlValue value = new XmlValue("value", true);
		XmlElement element = new XmlElement("element");
		
		assertThrows(NoSuchXmlElementException.class, () -> container.getAsValue("test"));
		assertThrows(NoSuchXmlElementException.class, () -> container.getAsValue(0));
		
		container.add(value);
		assertEquals(value, container.getAsValue("value"));
		assertEquals(value, container.getAsValue(0));
		
		container.add(element);
		assertThrows(XmlTypeException.class, () -> container.getAsValue("element"));
		
		container.clear();
		container.add(value);
		container.add(new XmlValue("value", false));
		assertEquals(value, container.getAsValue(0));
		assertThrows(XmlTypeException.class, () -> container.getAsValue("value"));
	}
	
	@Test
	void equalsAndHashCode() {
		XmlContainer container1 = new XmlContainer("test");
		XmlContainer container2 = new XmlContainer("test");
		XmlContainer container3 = new XmlContainer("other");
		
		assertEquals(container1, container2);
		assertEquals(container1.hashCode(), container2.hashCode());
		assertNotEquals(container1, container3);
		
		container1.add(new XmlElement("child"));
		assertNotEquals(container1, container2);
		
		container2.add(new XmlElement("child"));
		assertEquals(container1, container2);
		assertEquals(container1.hashCode(), container2.hashCode());
		
		container1.addAttribute("attr", "value");
		assertNotEquals(container1, container2);
		
		container2.addAttribute("attr", "value");
		assertEquals(container1, container2);
		assertEquals(container1.hashCode(), container2.hashCode());
		
		assertNotEquals(container1, null);
		assertNotEquals(container1, "string");
	}
	
	@Test
	void toStringWithDefaultConfig() {
		XmlContainer container = new XmlContainer("test");
		
		assertThrows(NullPointerException.class, () -> container.toString(null));
		assertEquals("<test></test>", container.toString(XmlConfig.DEFAULT));
		
		container.addAttribute("attr", "value");
		assertEquals("<test attr=\"value\"></test>", container.toString(XmlConfig.DEFAULT));
		
		container.add(new XmlElement("child"));
		String expected = "<test attr=\"value\">" + System.lineSeparator() +
			"\t<child/>" + System.lineSeparator() +
			"</test>";
		assertEquals(expected, container.toString(XmlConfig.DEFAULT));
		
		container.add(new XmlElement("child2"));
		expected = "<test attr=\"value\">" + System.lineSeparator() +
			"\t<child/>" + System.lineSeparator() +
			"\t<child2/>" + System.lineSeparator() +
			"</test>";
		assertEquals(expected, container.toString(XmlConfig.DEFAULT));
	}
	
	@Test
	void toStringWithCustomConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		XmlContainer container = new XmlContainer("test");
		
		assertEquals("<test></test>", container.toString(customConfig));
		
		container.addAttribute("attr", "value");
		assertThrows(IllegalStateException.class, () -> container.toString(customConfig));
		
		container.getAttributes().clear();
		container.add(new XmlElement("child"));
		assertEquals("<test><child/></test>", container.toString(customConfig));
		
		container.add(new XmlElement("child2"));
		assertEquals("<test><child/><child2/></test>", container.toString(customConfig));
	}
}
