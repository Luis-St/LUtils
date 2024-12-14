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

import com.google.common.collect.Lists;
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
	
	private static final XmlConfig CUSTOM_CONFIG = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new XmlElements((List<XmlElement>) null));
		assertThrows(NullPointerException.class, () -> new XmlElements((Map<String, XmlElement>) null));
	}
	
	@Test
	void isUndefined() {
		XmlElements elements = new XmlElements();
		assertTrue(elements.isUndefined());
		elements.add(new XmlElement("test"));
		assertTrue(elements.isUndefined());
		elements.add(new XmlElement("test"));
		assertFalse(elements.isUndefined());
	}
	
	@Test
	void isArray() {
		XmlElements elements = new XmlElements();
		assertFalse(elements.isArray());
		elements.add(new XmlElement("test"));
		assertFalse(elements.isArray());
		elements.add(new XmlElement("test"));
		assertTrue(elements.isArray());
		elements.remove(0);
		assertFalse(elements.isArray());
	}
	
	@Test
	void isObject() {
		XmlElements elements = new XmlElements();
		assertFalse(elements.isObject());
		elements.add(new XmlElement("test1"));
		assertFalse(elements.isObject());
		elements.add(new XmlElement("test2"));
		assertTrue(elements.isObject());
		elements.remove("test2");
		assertFalse(elements.isObject());
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
		elements.add(new XmlElement("test2"));
		assertEquals(2, elements.size());
	}
	
	@Test
	void isEmpty() {
		XmlElements elements = new XmlElements();
		assertTrue(elements.isEmpty());
		elements.add(new XmlElement("test1"));
		assertFalse(elements.isEmpty());
		elements.remove(0);
		assertTrue(elements.isEmpty());
	}
	
	@Test
	void containsName() {
		XmlElements elements = new XmlElements();
		assertFalse(elements.containsName("test"));
		elements.add(new XmlElement("test"));
		assertTrue(elements.containsName("test"));
	}
	
	@Test
	void containsElement() {
		XmlElements elements = new XmlElements();
		XmlElement element = new XmlElement("test");
		assertFalse(elements.containsElement(element));
		elements.add(element);
		assertTrue(elements.containsElement(element));
	}
	
	@Test
	void nameSet() {
		XmlElements elements = new XmlElements();
		assertEquals(Set.of(), elements.nameSet());
		elements.add(new XmlElement("test1"));
		assertEquals(Set.of("test1"), elements.nameSet());
		elements.add(new XmlElement("test2"));
		assertEquals(Set.of("test1", "test2"), elements.nameSet());
		elements.remove("test2");
		elements.add(new XmlElement("test1"));
		assertEquals(Set.of("test1"), elements.nameSet());
	}
	
	@Test
	void elements() {
		XmlElements elements = new XmlElements();
		assertIterableEquals(Collections.emptyList(), elements.elements());
		XmlElement element1 = new XmlElement("test1");
		elements.add(element1);
		XmlElement element2 = new XmlElement("test2");
		elements.add(element2);
		assertIterableEquals(Lists.newArrayList(element1, element2), elements.elements());
		elements.remove(element2);
		XmlElement element3 = new XmlElement("test1");
		elements.add(element3);
		assertIterableEquals(Lists.newArrayList(element1, element3), elements.elements());
	}
	
	@Test
	void add() {
		XmlElements elements = new XmlElements();
		assertThrows(NullPointerException.class, () -> elements.add(null));
		elements.add(new XmlElement("test1"));
		assertEquals(1, elements.size());
		elements.add(new XmlElement("test2"));
		assertEquals(2, elements.size());
		assertThrows(XmlTypeException.class, () -> elements.add(new XmlElement("test1")));
		elements.remove("test1");
		elements.add(new XmlElement("test2"));
		assertEquals(2, elements.size());
	}
	
	@Test
	void addContainer() {
		XmlElements elements = new XmlElements();
		assertThrows(NullPointerException.class, () -> elements.addContainer(null));
		elements.addContainer(new XmlContainer("test1"));
		assertEquals(1, elements.size());
		elements.addContainer(new XmlContainer("test2"));
		assertEquals(2, elements.size());
		assertThrows(XmlTypeException.class, () -> elements.addContainer(new XmlContainer("test1")));
		elements.remove("test1");
		elements.addContainer(new XmlContainer("test2"));
		assertEquals(2, elements.size());
	}
	
	@Test
	void addValue() {
		XmlElements elements = new XmlElements();
		assertThrows(NullPointerException.class, () -> elements.addValue(null));
		elements.addValue(new XmlValue("test1", true));
		assertEquals(1, elements.size());
		elements.addValue(new XmlValue("test2", 0));
		assertEquals(2, elements.size());
		assertThrows(XmlTypeException.class, () -> elements.addValue(new XmlValue("test1", false)));
		elements.remove("test1");
		elements.addValue(new XmlValue("test2", 0.0));
		assertEquals(2, elements.size());
	}
	
	@Test
	void remove() {
		XmlElements elements = new XmlElements();
		assertThrows(NullPointerException.class, () -> elements.remove((XmlElement) null));
		XmlElement element1 = new XmlElement("test1");
		assertFalse(elements.remove(element1));
		elements.add(element1);
		XmlElement element2 = new XmlElement("test2");
		elements.add(element2);
		assertEquals(2, elements.size());
		assertTrue(elements.remove(element1));
		assertTrue(elements.remove(element2));
		assertEquals(0, elements.size());
		
		elements.add(element1);
		elements.add(element2);
		assertThrows(XmlTypeException.class, () -> elements.remove(0));
		assertTrue(elements.remove("test2"));
		XmlElement element3 = new XmlElement("test1");
		elements.add(element3);
		assertThrows(XmlTypeException.class, () -> elements.remove("test1"));
		assertTrue(elements.remove(1));
		assertEquals(1, elements.size());
	}
	
	@Test
	void clear() {
		XmlElements elements = new XmlElements();
		elements.add(new XmlElement("test1"));
		assertEquals(1, elements.size());
		elements.clear();
		assertEquals(0, elements.size());
	}
	
	@Test
	void get() {
		XmlElements elements = new XmlElements();
		assertNull(elements.get("test"));
		XmlElement element1 = new XmlElement("test1");
		elements.add(element1);
		assertEquals(element1, elements.get("test1"));
		assertEquals(element1, elements.get(0));
		
		elements.add(new XmlElement("test2"));
		assertNotNull(elements.get("test2"));
		assertThrows(XmlTypeException.class, () -> elements.get(0));
		
		elements.remove("test2");
		elements.add(new XmlElement("test1"));
		assertNotNull(elements.get(1));
		assertThrows(XmlTypeException.class, () -> elements.get("test1"));
	}
	
	@Test
	void getAsContainer() {
		XmlElements elements = new XmlElements();
		assertThrows(NoSuchXmlElementException.class, () -> elements.getAsContainer("test"));
		XmlContainer element1 = new XmlContainer("test1");
		elements.add(element1);
		assertEquals(element1, elements.getAsContainer("test1"));
		elements.add(new XmlElement("test2"));
		assertThrows(XmlTypeException.class, () -> elements.getAsContainer("test2"));
		
		assertThrows(XmlTypeException.class, () -> elements.getAsContainer(1));
		elements.remove("test2");
		elements.add(new XmlContainer("test1"));
		assertEquals(element1, elements.getAsContainer(0));
	}
	
	@Test
	void getAsValue() {
		XmlElements elements = new XmlElements();
		assertThrows(NoSuchXmlElementException.class, () -> elements.getAsValue("test"));
		XmlValue element1 = new XmlValue("test1", true);
		elements.add(element1);
		assertEquals(element1, elements.getAsValue("test1"));
		elements.add(new XmlElement("test2"));
		assertThrows(XmlTypeException.class, () -> elements.getAsValue("test2"));
		
		assertThrows(XmlTypeException.class, () -> elements.getAsValue(1));
		elements.remove("test2");
		elements.add(new XmlValue("test1", 0));
		assertEquals(element1, elements.getAsValue(0));
	}
	
	@Test
	void getAsArray() {
		XmlElements elements = new XmlElements();
		XmlElement element1 = new XmlElement("test1");
		elements.add(element1);
		elements.add(new XmlElement("test2"));
		assertThrows(XmlTypeException.class, elements::getAsArray);
		elements.remove("test2");
		XmlElement element2 = new XmlElement("test1");
		elements.add(element2);
		assertIterableEquals(List.of(element1, element2), elements.getAsArray());
	}
	
	@Test
	void getAsObject() {
		XmlElements elements = new XmlElements();
		XmlElement element1 = new XmlElement("test1");
		elements.add(element1);
		elements.add(new XmlElement("test1"));
		assertThrows(XmlTypeException.class, elements::getAsObject);
		elements.remove(1);
		XmlElement element2 = new XmlElement("test2");
		elements.add(element2);
		assertEquals(Map.of("test1", element1, "test2", element2), elements.getAsObject());
	}
	
	@Test
	void toStringDefaultConfig() {
		XmlElements elements = new XmlElements();
		assertThrows(NullPointerException.class, () -> elements.toString(null));
		assertEquals("", elements.toString(XmlConfig.DEFAULT));
		elements.add(new XmlElement("test1"));
		elements.add(new XmlElement("test2"));
		assertEquals("<test1/>" + System.lineSeparator() + "<test2/>", elements.toString(XmlConfig.DEFAULT));
		elements.remove("test2");
		elements.add(new XmlElement("test1"));
		assertEquals("<test1/>" + System.lineSeparator() + "<test1/>", elements.toString(XmlConfig.DEFAULT));
	}
	
	@Test
	void toStringCustomConfig() {
		XmlElements elements = new XmlElements();
		assertThrows(NullPointerException.class, () -> elements.toString(null));
		assertEquals("", elements.toString(XmlConfig.DEFAULT));
		elements.add(new XmlElement("test1"));
		elements.add(new XmlElement("test2"));
		assertEquals("<test1/><test2/>", elements.toString(CUSTOM_CONFIG));
		elements.remove("test2");
		elements.add(new XmlElement("test1"));
		assertEquals("<test1/><test1/>", elements.toString(CUSTOM_CONFIG));
	}
}
