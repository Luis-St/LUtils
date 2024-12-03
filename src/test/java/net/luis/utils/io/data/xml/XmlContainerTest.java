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
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlContainer}.<br>
 *
 * @author Luis-St
 *
 */
class XmlContainerTest {
	
	private static final XmlConfig CUSTOM_CONFIG = new XmlConfig(false, false, "  ", false, false, StandardCharsets.UTF_8);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new XmlContainer(null));
		assertThrows(NullPointerException.class, () -> new XmlContainer(null, new XmlElements()));
		assertThrows(NullPointerException.class, () -> new XmlContainer(null, new XmlAttributes()));
		assertThrows(NullPointerException.class, () -> new XmlContainer(null, new XmlAttributes(), new XmlElements()));
		assertThrows(NullPointerException.class, () -> new XmlContainer("test", (XmlElements) null));
		assertThrows(NullPointerException.class, () -> new XmlContainer("test", (XmlAttributes) null));
		assertThrows(NullPointerException.class, () -> new XmlContainer("test", (XmlAttributes) null, new XmlElements()));
		assertThrows(NullPointerException.class, () -> new XmlContainer("test", new XmlAttributes(), (XmlElements) null));
	}
	
	@Test
	void getElementType() {
		assertEquals("xml container", new XmlContainer("test").getElementType());
	}
	
	@Test
	void isSelfClosing() {
		assertFalse(new XmlContainer("test").isSelfClosing());
	}
	
	@Test
	void getElements() {
		XmlContainer container = new XmlContainer("test");
		assertEquals(new XmlElements(), container.getElements());
		assertEquals(0, container.getElements().size());
		container.add(new XmlElement("test"));
		assertEquals(1, container.getElements().size());
	}
	
	@Test
	void isUndefinedContainer() {
		XmlContainer container = new XmlContainer("test");
		assertTrue(container.isUndefinedContainer());
		container.add(new XmlElement("test"));
		assertTrue(container.isUndefinedContainer());
		container.add(new XmlElement("test"));
		assertFalse(container.isUndefinedContainer());
	}
	
	@Test
	void isContainerArray() {
		XmlContainer container = new XmlContainer("test");
		assertFalse(container.isContainerArray());
		container.add(new XmlElement("test"));
		assertFalse(container.isContainerArray());
		container.add(new XmlElement("test"));
		assertTrue(container.isContainerArray());
		container.remove(0);
		assertFalse(container.isContainerArray());
	}
	
	@Test
	void isContainerObject() {
		XmlContainer container = new XmlContainer("test");
		assertFalse(container.isContainerObject());
		container.add(new XmlElement("test1"));
		assertFalse(container.isContainerObject());
		container.add(new XmlElement("test2"));
		assertTrue(container.isContainerObject());
		container.remove("test2");
		assertFalse(container.isContainerObject());
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
		container.add(new XmlElement("test2"));
		assertEquals(2, container.size());
	}
	
	@Test
	void isEmpty() {
		XmlContainer container = new XmlContainer("test");
		assertTrue(container.isEmpty());
		container.add(new XmlElement("test1"));
		assertFalse(container.isEmpty());
		container.remove(0);
		assertTrue(container.isEmpty());
	}
	
	@Test
	void containsName() {
		XmlContainer container = new XmlContainer("test");
		assertFalse(container.containsName("test"));
		container.add(new XmlElement("test"));
		assertTrue(container.containsName("test"));
	}
	
	@Test
	void containsElement() {
		XmlContainer container = new XmlContainer("test");
		XmlElement element = new XmlElement("test");
		assertFalse(container.containsElement(element));
		container.add(element);
		assertTrue(container.containsElement(element));
	}
	
	@Test
	void nameSet() {
		XmlContainer container = new XmlContainer("test");
		assertEquals(Set.of(), container.nameSet());
		container.add(new XmlElement("test1"));
		assertEquals(Set.of("test1"), container.nameSet());
		container.add(new XmlElement("test2"));
		assertEquals(Set.of("test1", "test2"), container.nameSet());
		container.remove("test2");
		container.add(new XmlElement("test1"));
		assertEquals(Set.of("test1"), container.nameSet());
	}
	
	@Test
	void elements() {
		XmlContainer container = new XmlContainer("test");
		assertIterableEquals(Collections.emptyList(), container.elements());
		XmlElement element1 = new XmlElement("test1");
		container.add(element1);
		XmlElement element2 = new XmlElement("test2");
		container.add(element2);
		assertIterableEquals(Lists.newArrayList(element1, element2), container.elements());
		container.remove(element2);
		XmlElement element3 = new XmlElement("test1");
		container.add(element3);
		assertIterableEquals(Lists.newArrayList(element1, element3), container.elements());
	}
	
	@Test
	void add() {
		XmlContainer container = new XmlContainer("test");
		assertThrows(NullPointerException.class, () -> container.add((XmlElement) null));
		container.add(new XmlElement("test1"));
		assertEquals(1, container.size());
		container.add(new XmlElement("test2"));
		assertEquals(2, container.size());
		assertThrows(XmlTypeException.class, () -> container.add(new XmlElement("test1")));
		container.remove("test1");
		container.add(new XmlElement("test2"));
		assertEquals(2, container.size());
	}
	
	@Test
	void addContainer() {
		XmlContainer container = new XmlContainer("test");
		assertThrows(NullPointerException.class, () -> container.addContainer(null));
		container.addContainer(new XmlContainer("test1"));
		assertEquals(1, container.size());
		container.addContainer(new XmlContainer("test2"));
		assertEquals(2, container.size());
		assertThrows(XmlTypeException.class, () -> container.addContainer(new XmlContainer("test1")));
		container.remove("test1");
		container.addContainer(new XmlContainer("test2"));
		assertEquals(2, container.size());
	}
	
	@Test
	void addValue() {
		XmlContainer container = new XmlContainer("test");
		assertThrows(NullPointerException.class, () -> container.addValue(null));
		container.addValue(new XmlValue("test1", true));
		assertEquals(1, container.size());
		container.addValue(new XmlValue("test2", 0));
		assertEquals(2, container.size());
		assertThrows(XmlTypeException.class, () -> container.addValue(new XmlValue("test1", false)));
		container.remove("test1");
		container.addValue(new XmlValue("test2", 0.0));
		assertEquals(2, container.size());
	}
	
	@Test
	void remove() {
		XmlContainer container = new XmlContainer("test");
		assertThrows(NullPointerException.class, () -> container.remove((XmlElement) null));
		XmlElement element1 = new XmlElement("test1");
		assertFalse(container.remove(element1));
		container.add(element1);
		XmlElement element2 = new XmlElement("test2");
		container.add(element2);
		assertEquals(2, container.size());
		assertTrue(container.remove(element1));
		assertTrue(container.remove(element2));
		assertEquals(0, container.size());
		
		container.add(element1);
		container.add(element2);
		assertThrows(XmlTypeException.class, () -> container.remove(0));
		assertTrue(container.remove("test2"));
		XmlElement element3 = new XmlElement("test1");
		container.add(element3);
		assertThrows(XmlTypeException.class, () -> container.remove("test1"));
		assertTrue(container.remove(1));
		assertEquals(1, container.size());
	}
	
	@Test
	void clear() {
		XmlContainer container = new XmlContainer("test");
		container.add(new XmlElement("test1"));
		assertEquals(1, container.size());
		container.clear();
		assertEquals(0, container.size());
	}
	
	@Test
	void get() {
		XmlContainer container = new XmlContainer("test");
		assertNull(container.get("test"));
		XmlElement element1 = new XmlElement("test1");
		container.add(element1);
		assertEquals(element1, container.get("test1"));
		assertEquals(element1, container.get(0));
		
		container.add(new XmlElement("test2"));
		assertNotNull(container.get("test2"));
		assertThrows(XmlTypeException.class, () -> container.get(0));
		
		container.remove("test2");
		container.add(new XmlElement("test1"));
		assertNotNull(container.get(1));
		assertThrows(XmlTypeException.class, () -> container.get("test1"));
	}
	
	@Test
	void getAsContainer() {
		XmlContainer container = new XmlContainer("test");
		assertThrows(NoSuchXmlElementException.class, () -> container.getAsContainer("test"));
		XmlContainer element1 = new XmlContainer("test1");
		container.add(element1);
		assertEquals(element1, container.getAsContainer("test1"));
		container.add(new XmlElement("test2"));
		assertThrows(XmlTypeException.class, () -> container.getAsContainer("test2"));
	}
	
	@Test
	void getAsValue() {
		XmlContainer container = new XmlContainer("test");
		assertThrows(NoSuchXmlElementException.class, () -> container.getAsValue("test"));
		XmlValue element1 = new XmlValue("test1", true);
		container.add(element1);
		assertEquals(element1, container.getAsValue("test1"));
		container.add(new XmlElement("test2"));
		assertThrows(XmlTypeException.class, () -> container.getAsValue("test2"));
	}
	
	@Test
	void toStringDefaultConfig() {
		XmlContainer container = new XmlContainer("test");
		assertThrows(NullPointerException.class, () -> container.toString(null));
		assertEquals("<test></test>", container.toString(XmlConfig.DEFAULT));
		
		container.addAttribute(new XmlAttribute("test", "test"));
		assertEquals("<test test=\"test\"></test>", container.toString(XmlConfig.DEFAULT));
		
		container.add(new XmlElement("test1"));
		assertEquals("<test test=\"test\">" + System.lineSeparator() + "\t<test1/>" + System.lineSeparator() + "</test>", container.toString(XmlConfig.DEFAULT));
		
		container.add(new XmlElement("test2"));
		assertEquals("<test test=\"test\">" + System.lineSeparator() + "\t<test1/>" + System.lineSeparator() + "\t<test2/>" + System.lineSeparator() + "</test>", container.toString(XmlConfig.DEFAULT));
		
		container.remove("test2");
		container.add(new XmlElement("test1"));
		assertEquals("<test test=\"test\">" + System.lineSeparator() + "\t<test1/>" + System.lineSeparator() + "\t<test1/>" + System.lineSeparator() + "</test>", container.toString(XmlConfig.DEFAULT));
	}
	
	@Test
	void toStringCustomConfig() {
		XmlContainer container = new XmlContainer("test");
		assertThrows(NullPointerException.class, () -> container.toString(null));
		assertEquals("<test></test>", container.toString(CUSTOM_CONFIG));
		
		container.addAttribute(new XmlAttribute("test", "test"));
		assertThrows(IllegalStateException.class, () -> container.toString(CUSTOM_CONFIG));
		container.getAttributes().clear();
		
		container.add(new XmlElement("test1"));
		assertEquals("<test><test1/></test>", container.toString(CUSTOM_CONFIG));
		
		container.add(new XmlElement("test2"));
		assertEquals("<test><test1/><test2/></test>", container.toString(CUSTOM_CONFIG));
		
		container.remove("test2");
		container.add(new XmlElement("test1"));
		assertEquals("<test><test1/><test1/></test>", container.toString(CUSTOM_CONFIG));
	}
}
