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

import net.luis.utils.io.data.xml.exception.NoSuchXmlElementException;
import net.luis.utils.io.data.xml.exception.XmlTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlElements}.<br>
 *
 * @author Luis-St
 */
class XmlElementsTest {
	
	@Test
	void constructorWithNullParameters() {
		assertThrows(NullPointerException.class, () -> new XmlElements((List<XmlElement>) null));
		assertThrows(NullPointerException.class, () -> new XmlElements((Map<String, XmlElement>) null));
	}
	
	@Test
	void constructorWithValidList() {
		List<XmlElement> list = List.of(
			new XmlElement("element1"),
			new XmlElement("element2")
		);
		XmlElements elements = new XmlElements(list);
		assertEquals(2, elements.size());
		assertTrue(elements.isObject());
	}
	
	@Test
	void constructorWithValidMap() {
		Map<String, XmlElement> map = Map.of(
			"element1", new XmlElement("element1"),
			"element2", new XmlElement("element2")
		);
		XmlElements elements = new XmlElements(map);
		assertEquals(2, elements.size());
		assertTrue(elements.isObject());
	}
	
	@Test
	void constructorWithArrayList() {
		List<XmlElement> list = List.of(
			new XmlElement("same"),
			new XmlElement("same")
		);
		XmlElements elements = new XmlElements(list);
		assertEquals(2, elements.size());
		assertTrue(elements.isArray());
	}
	
	@Test
	void constructorEmpty() {
		XmlElements elements = new XmlElements();
		assertEquals(0, elements.size());
		assertTrue(elements.isEmpty());
		assertTrue(elements.isUndefined());
	}
	
	@Test
	void typeDetection() {
		XmlElements elements = new XmlElements();
		
		assertTrue(elements.isUndefined());
		assertFalse(elements.isArray());
		assertFalse(elements.isObject());
		
		elements.add(new XmlElement("test"));
		assertTrue(elements.isUndefined());
		assertFalse(elements.isArray());
		assertFalse(elements.isObject());
		
		elements.add(new XmlElement("test"));
		assertFalse(elements.isUndefined());
		assertTrue(elements.isArray());
		assertFalse(elements.isObject());
		
		elements.clear();
		elements.add(new XmlElement("test1"));
		elements.add(new XmlElement("test2"));
		assertFalse(elements.isUndefined());
		assertFalse(elements.isArray());
		assertTrue(elements.isObject());
	}
	
	@Test
	void size() {
		XmlElements elements = new XmlElements();
		assertEquals(0, elements.size());
		
		elements.add(new XmlElement("test1"));
		assertEquals(1, elements.size());
		
		elements.add(new XmlElement("test1"));
		assertEquals(2, elements.size());
		
		elements.remove(1);
		assertEquals(1, elements.size());
		
		elements.clear();
		elements.add(new XmlElement("test1"));
		elements.add(new XmlElement("test2"));
		assertEquals(2, elements.size());
	}
	
	@Test
	void isEmpty() {
		XmlElements elements = new XmlElements();
		assertTrue(elements.isEmpty());
		
		elements.add(new XmlElement("test1"));
		assertFalse(elements.isEmpty());
		
		elements.clear();
		assertTrue(elements.isEmpty());
	}
	
	@Test
	void containsName() {
		XmlElements elements = new XmlElements();
		assertFalse(elements.containsName("test"));
		assertFalse(elements.containsName(null));
		
		elements.add(new XmlElement("test"));
		assertTrue(elements.containsName("test"));
		assertFalse(elements.containsName("other"));
		assertFalse(elements.containsName(null));
	}
	
	@Test
	void containsElement() {
		XmlElements elements = new XmlElements();
		XmlElement element = new XmlElement("test");
		XmlElement otherElement = new XmlElement("other");
		
		assertFalse(elements.containsElement(element));
		assertFalse(elements.containsElement(null));
		
		elements.add(element);
		assertTrue(elements.containsElement(element));
		assertFalse(elements.containsElement(otherElement));
		assertFalse(elements.containsElement(null));
		
		elements.add(new XmlElement("test"));
		assertTrue(elements.containsElement(element));
		assertFalse(elements.containsElement(otherElement));
	}
	
	@Test
	void nameSet() {
		XmlElements elements = new XmlElements();
		assertEquals(Set.of(), elements.nameSet());
		
		elements.add(new XmlElement("test1"));
		assertEquals(Set.of("test1"), elements.nameSet());
		
		elements.add(new XmlElement("test2"));
		assertEquals(Set.of("test1", "test2"), elements.nameSet());
		
		elements.clear();
		elements.add(new XmlElement("test1"));
		elements.add(new XmlElement("test1"));
		assertEquals(Set.of("test1"), elements.nameSet());
	}
	
	@Test
	void elements() {
		XmlElements elements = new XmlElements();
		assertEquals(Collections.emptyList(), elements.elements());
		
		XmlElement element1 = new XmlElement("test1");
		elements.add(element1);
		assertEquals(List.of(element1), elements.elements());
		
		XmlElement element2 = new XmlElement("test2");
		elements.add(element2);
		assertEquals(List.of(element1, element2), elements.elements());
		
		elements.clear();
		elements.add(element1);
		XmlElement element3 = new XmlElement("test1");
		elements.add(element3);
		assertIterableEquals(List.of(element1, element3), elements.elements());
	}
	
	@Test
	void addElement() {
		XmlElements elements = new XmlElements();
		
		assertThrows(NullPointerException.class, () -> elements.add(null));
		
		elements.add(new XmlElement("test1"));
		assertEquals(1, elements.size());
		
		elements.add(new XmlElement("test2"));
		assertEquals(2, elements.size());
		assertTrue(elements.isObject());
		
		assertThrows(XmlTypeException.class, () -> elements.add(new XmlElement("test1")));
		
		elements.clear();
		elements.add(new XmlElement("test1"));
		elements.add(new XmlElement("test1"));
		assertTrue(elements.isArray());
		
		assertThrows(XmlTypeException.class, () -> elements.add(new XmlElement("test2")));
	}
	
	@Test
	void addContainer() {
		XmlElements elements = new XmlElements();
		
		assertThrows(NullPointerException.class, () -> elements.addContainer(null));
		
		elements.addContainer(new XmlContainer("test1"));
		assertEquals(1, elements.size());
		
		elements.addContainer(new XmlContainer("test2"));
		assertEquals(2, elements.size());
		assertTrue(elements.isObject());
		
		assertThrows(XmlTypeException.class, () -> elements.addContainer(new XmlContainer("test1")));
		
		elements.clear();
		elements.addContainer(new XmlContainer("same"));
		elements.addContainer(new XmlContainer("same"));
		assertTrue(elements.isArray());
		
		assertThrows(XmlTypeException.class, () -> elements.addContainer(new XmlContainer("different")));
	}
	
	@Test
	void addValue() {
		XmlElements elements = new XmlElements();
		
		assertThrows(NullPointerException.class, () -> elements.addValue(null));
		
		elements.addValue(new XmlValue("test1", true));
		assertEquals(1, elements.size());
		
		elements.addValue(new XmlValue("test2", 0));
		assertEquals(2, elements.size());
		assertTrue(elements.isObject());
		
		assertThrows(XmlTypeException.class, () -> elements.addValue(new XmlValue("test1", false)));
		
		elements.clear();
		elements.addValue(new XmlValue("same", "value1"));
		elements.addValue(new XmlValue("same", "value2"));
		assertTrue(elements.isArray());
		
		assertThrows(XmlTypeException.class, () -> elements.addValue(new XmlValue("different", "value")));
	}
	
	@Test
	void addMixedTypes() {
		XmlElements elements = new XmlElements();
		
		elements.add(new XmlElement("element"));
		elements.addContainer(new XmlContainer("container"));
		elements.addValue(new XmlValue("value", "test"));
		
		assertEquals(3, elements.size());
		assertTrue(elements.isObject());
		assertTrue(elements.containsName("element"));
		assertTrue(elements.containsName("container"));
		assertTrue(elements.containsName("value"));
	}
	
	@Test
	void removeElement() {
		XmlElements elements = new XmlElements();
		XmlElement element1 = new XmlElement("test1");
		XmlElement element2 = new XmlElement("test2");
		
		assertThrows(NullPointerException.class, () -> elements.remove((XmlElement) null));
		assertFalse(elements.remove(element1));
		
		elements.add(element1);
		elements.add(element2);
		assertEquals(2, elements.size());
		
		assertTrue(elements.remove(element1));
		assertEquals(1, elements.size());
		assertFalse(elements.containsElement(element1));
		
		assertTrue(elements.remove(element2));
		assertEquals(0, elements.size());
		assertTrue(elements.isEmpty());
	}
	
	@Test
	void removeByName() {
		XmlElements elements = new XmlElements();
		XmlElement element1 = new XmlElement("test1");
		XmlElement element2 = new XmlElement("test2");
		
		assertFalse(elements.remove("nonexistent"));
		
		elements.add(element1);
		elements.add(element2);
		assertTrue(elements.isObject());
		
		assertTrue(elements.remove("test2"));
		assertEquals(1, elements.size());
		assertFalse(elements.containsName("test2"));
		
		elements.add(new XmlElement("test1"));
		assertThrows(XmlTypeException.class, () -> elements.remove("test1"));
	}
	
	@Test
	void removeByIndex() {
		XmlElements elements = new XmlElements();
		XmlElement element1 = new XmlElement("test1");
		XmlElement element2 = new XmlElement("test2");
		
		assertFalse(elements.remove(0));
		
		elements.add(element1);
		elements.add(element2);
		assertTrue(elements.isObject());
		
		assertThrows(XmlTypeException.class, () -> elements.remove(0));
		
		elements.clear();
		elements.add(element1);
		elements.add(new XmlElement("test1"));
		assertTrue(elements.isArray());
		
		assertTrue(elements.remove(1));
		assertEquals(1, elements.size());
		
		assertTrue(elements.remove(0));
		assertEquals(0, elements.size());
		
		assertFalse(elements.remove(0));
	}
	
	@Test
	void clear() {
		XmlElements elements = new XmlElements();
		elements.add(new XmlElement("test1"));
		elements.add(new XmlElement("test2"));
		assertEquals(2, elements.size());
		
		elements.clear();
		assertEquals(0, elements.size());
		assertTrue(elements.isEmpty());
		assertTrue(elements.isUndefined());
	}
	
	@Test
	void getByName() {
		XmlElements elements = new XmlElements();
		XmlElement element1 = new XmlElement("test1");
		
		assertNull(elements.get("test"));
		
		elements.add(element1);
		assertEquals(element1, elements.get("test1"));
		assertNull(elements.get("nonexistent"));
		
		elements.add(new XmlElement("test1"));
		assertThrows(XmlTypeException.class, () -> elements.get("test1"));
	}
	
	@Test
	void getByIndex() {
		XmlElements elements = new XmlElements();
		XmlElement element1 = new XmlElement("test1");
		
		assertNull(elements.get(0));
		
		elements.add(element1);
		assertEquals(element1, elements.get(0));
		assertNull(elements.get(1));
		
		elements.add(new XmlElement("test2"));
		assertThrows(XmlTypeException.class, () -> elements.get(0));
		
		elements.clear();
		elements.add(element1);
		elements.add(new XmlElement("test1"));
		assertEquals(element1, elements.get(0));
		assertNotNull(elements.get(1));
		assertNull(elements.get(2));
		assertNull(elements.get(-1));
	}
	
	@Test
	void getAsContainer() {
		XmlElements elements = new XmlElements();
		XmlContainer container = new XmlContainer("container");
		XmlElement element = new XmlElement("element");
		
		assertThrows(NoSuchXmlElementException.class, () -> elements.getAsContainer("test"));
		assertThrows(NoSuchXmlElementException.class, () -> elements.getAsContainer(0));
		
		elements.add(container);
		assertEquals(container, elements.getAsContainer("container"));
		assertEquals(container, elements.getAsContainer(0));
		
		elements.add(element);
		assertThrows(XmlTypeException.class, () -> elements.getAsContainer("element"));
		
		elements.clear();
		elements.add(container);
		elements.add(new XmlContainer("container"));
		assertEquals(container, elements.getAsContainer(0));
		assertThrows(XmlTypeException.class, () -> elements.getAsContainer("container"));
	}
	
	@Test
	void getAsValue() {
		XmlElements elements = new XmlElements();
		XmlValue value = new XmlValue("value", true);
		XmlElement element = new XmlElement("element");
		
		assertThrows(NoSuchXmlElementException.class, () -> elements.getAsValue("test"));
		assertThrows(NoSuchXmlElementException.class, () -> elements.getAsValue(0));
		
		elements.add(value);
		assertEquals(value, elements.getAsValue("value"));
		assertEquals(value, elements.getAsValue(0));
		
		elements.add(element);
		assertThrows(XmlTypeException.class, () -> elements.getAsValue("element"));
		
		elements.clear();
		elements.add(value);
		elements.add(new XmlValue("value", false));
		assertEquals(value, elements.getAsValue(0));
		assertThrows(XmlTypeException.class, () -> elements.getAsValue("value"));
	}
	
	@Test
	void getAsArray() {
		XmlElements elements = new XmlElements();
		XmlElement element1 = new XmlElement("same");
		XmlElement element2 = new XmlElement("same");
		
		assertThrows(XmlTypeException.class, elements::getAsArray);
		
		elements.add(element1);
		elements.add(new XmlElement("different"));
		assertThrows(XmlTypeException.class, elements::getAsArray);
		
		elements.clear();
		elements.add(element1);
		elements.add(element2);
		List<XmlElement> array = elements.getAsArray();
		assertEquals(List.of(element1, element2), array);
		assertThrows(UnsupportedOperationException.class, () -> array.add(new XmlElement("test")));
	}
	
	@Test
	void getAsObject() {
		XmlElements elements = new XmlElements();
		XmlElement element1 = new XmlElement("element1");
		XmlElement element2 = new XmlElement("element2");
		
		assertThrows(XmlTypeException.class, elements::getAsObject);
		
		elements.add(element1);
		elements.add(new XmlElement("element1"));
		assertThrows(XmlTypeException.class, elements::getAsObject);
		
		elements.clear();
		elements.add(element1);
		elements.add(element2);
		Map<String, XmlElement> object = elements.getAsObject();
		assertEquals(Map.of("element1", element1, "element2", element2), object);
		assertThrows(UnsupportedOperationException.class, () -> object.put("test", new XmlElement("test")));
	}
	
	@Test
	void equalsAndHashCode() {
		XmlElements elements1 = new XmlElements();
		XmlElements elements2 = new XmlElements();
		XmlElements elements3 = new XmlElements();
		
		assertEquals(elements1, elements2);
		assertEquals(elements1.hashCode(), elements2.hashCode());
		
		elements1.add(new XmlElement("test"));
		assertNotEquals(elements1, elements2);
		
		elements2.add(new XmlElement("test"));
		assertEquals(elements1, elements2);
		assertEquals(elements1.hashCode(), elements2.hashCode());
		
		elements3.add(new XmlElement("other"));
		assertNotEquals(elements1, elements3);
		
		assertNotEquals(elements1, null);
		assertNotEquals(elements1, "string");
	}
	
	@Test
	void toStringWithDefaultConfig() {
		XmlElements elements = new XmlElements();
		
		assertThrows(NullPointerException.class, () -> elements.toString(null));
		assertEquals("", elements.toString(XmlConfig.DEFAULT));
		
		elements.add(new XmlElement("test1"));
		assertEquals("<test1/>", elements.toString(XmlConfig.DEFAULT));
		
		elements.add(new XmlElement("test2"));
		String expected = "<test1/>" + System.lineSeparator() + "<test2/>";
		assertEquals(expected, elements.toString(XmlConfig.DEFAULT));
		
		elements.clear();
		elements.add(new XmlElement("same"));
		elements.add(new XmlElement("same"));
		expected = "<same/>" + System.lineSeparator() + "<same/>";
		assertEquals(expected, elements.toString(XmlConfig.DEFAULT));
	}
	
	@Test
	void toStringWithCustomConfig() {
		XmlConfig customConfig = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
		XmlElements elements = new XmlElements();
		
		assertEquals("", elements.toString(customConfig));
		
		elements.add(new XmlElement("test1"));
		assertEquals("<test1/>", elements.toString(customConfig));
		
		elements.add(new XmlElement("test2"));
		assertEquals("<test1/><test2/>", elements.toString(customConfig));
		
		elements.clear();
		elements.add(new XmlElement("same"));
		elements.add(new XmlElement("same"));
		assertEquals("<same/><same/>", elements.toString(customConfig));
	}
	
	@Test
	void toStringWithSingleElement() {
		XmlElements elements = new XmlElements();
		elements.add(new XmlElement("single"));
		
		assertEquals("<single/>", elements.toString(XmlConfig.DEFAULT));
		assertTrue(elements.isUndefined());
	}
	
	@Test
	void toStringWithEmptyElements() {
		XmlElements elements = new XmlElements();
		assertEquals("", elements.toString(XmlConfig.DEFAULT));
	}
}
