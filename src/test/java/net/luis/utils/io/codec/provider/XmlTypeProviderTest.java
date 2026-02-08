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

package net.luis.utils.io.codec.provider;

import net.luis.utils.io.data.xml.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlTypeProvider}.<br>
 *
 * @author Luis-St
 */
class XmlTypeProviderTest {
	
	@Test
	void emptyReturnsSelfClosingElement() {
		XmlElement empty = XmlTypeProvider.INSTANCE.empty();
		assertTrue(empty.isSelfClosing());
		assertEquals("empty:generated", empty.getName());
	}
	
	@Test
	void createNullReturnsNullValue() {
		XmlElement nullValue = XmlTypeProvider.INSTANCE.createNull(RuntimeException::new);
		assertTrue(nullValue.isSelfClosing());
		assertEquals("null:generated", nullValue.getName());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertTrue(XmlTypeProvider.INSTANCE.createBoolean(true, RuntimeException::new).isXmlValue());
		assertEquals("boolean:generated", XmlTypeProvider.INSTANCE.createBoolean(true, RuntimeException::new).getName());
		assertTrue(XmlTypeProvider.INSTANCE.createBoolean(true, RuntimeException::new).getAsXmlValue().getAsBoolean());
		
		assertTrue(XmlTypeProvider.INSTANCE.createByte((byte) 42, RuntimeException::new).isXmlValue());
		assertEquals("byte:generated", XmlTypeProvider.INSTANCE.createByte((byte) 42, RuntimeException::new).getName());
		assertEquals(42, XmlTypeProvider.INSTANCE.createByte((byte) 42, RuntimeException::new).getAsXmlValue().getAsByte());
		
		assertTrue(XmlTypeProvider.INSTANCE.createShort((short) 42, RuntimeException::new).isXmlValue());
		assertEquals("short:generated", XmlTypeProvider.INSTANCE.createShort((short) 42, RuntimeException::new).getName());
		assertEquals(42, XmlTypeProvider.INSTANCE.createShort((short) 42, RuntimeException::new).getAsXmlValue().getAsShort());
		
		assertTrue(XmlTypeProvider.INSTANCE.createInteger(42, RuntimeException::new).isXmlValue());
		assertEquals("integer:generated", XmlTypeProvider.INSTANCE.createInteger(42, RuntimeException::new).getName());
		assertEquals(42, XmlTypeProvider.INSTANCE.createInteger(42, RuntimeException::new).getAsXmlValue().getAsInteger());
		
		assertTrue(XmlTypeProvider.INSTANCE.createLong(42L, RuntimeException::new).isXmlValue());
		assertEquals("long:generated", XmlTypeProvider.INSTANCE.createLong(42L, RuntimeException::new).getName());
		assertEquals(42L, XmlTypeProvider.INSTANCE.createLong(42L, RuntimeException::new).getAsXmlValue().getAsLong());
		
		assertTrue(XmlTypeProvider.INSTANCE.createFloat(42.5f, RuntimeException::new).isXmlValue());
		assertEquals("float:generated", XmlTypeProvider.INSTANCE.createFloat(42.5f, RuntimeException::new).getName());
		assertEquals(42.5f, XmlTypeProvider.INSTANCE.createFloat(42.5f, RuntimeException::new).getAsXmlValue().getAsFloat());
		
		assertTrue(XmlTypeProvider.INSTANCE.createDouble(42.5, RuntimeException::new).isXmlValue());
		assertEquals("double:generated", XmlTypeProvider.INSTANCE.createDouble(42.5, RuntimeException::new).getName());
		assertEquals(42.5, XmlTypeProvider.INSTANCE.createDouble(42.5, RuntimeException::new).getAsXmlValue().getAsDouble());
		
		assertTrue(XmlTypeProvider.INSTANCE.createString("test", RuntimeException::new).isXmlValue());
		assertEquals("string:generated", XmlTypeProvider.INSTANCE.createString("test", RuntimeException::new).getName());
		assertEquals("test", XmlTypeProvider.INSTANCE.createString("test", RuntimeException::new).getAsXmlValue().getAsString());
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.createString(null, RuntimeException::new));
	}
	
	@Test
	void createCollectionTypes() {
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.createList(null, RuntimeException::new));
		
		XmlElement emptyList = XmlTypeProvider.INSTANCE.createList(List.of(), RuntimeException::new);
		assertTrue(emptyList.isXmlContainer());
		assertEquals("list:generated", emptyList.getName());
		assertTrue(emptyList.getAsXmlContainer().isEmpty());
		
		XmlElement testElement = new XmlValue("test", 42);
		XmlElement list = XmlTypeProvider.INSTANCE.createList(List.of(testElement), RuntimeException::new);
		assertTrue(list.isXmlContainer());
		assertEquals("list:generated", list.getName());
		assertEquals(1, list.getAsXmlContainer().size());
		assertEquals("element:generated", list.getAsXmlContainer().get(0).getName());
		
		XmlElement emptyMap = XmlTypeProvider.INSTANCE.createMap(RuntimeException::new);
		assertTrue(emptyMap.isXmlContainer());
		assertEquals("map:generated", emptyMap.getName());
		
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.createMap(null, RuntimeException::new));
		
		XmlElement map = XmlTypeProvider.INSTANCE.createMap(Map.of("test", testElement), RuntimeException::new);
		assertTrue(map.isXmlContainer());
		assertEquals("map:generated", map.getName());
		assertEquals(1, map.getAsXmlContainer().size());
		assertEquals("test", map.getAsXmlContainer().get(0).getName());
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.isEmpty(null, RuntimeException::new));
		assertFalse(XmlTypeProvider.INSTANCE.isEmpty(new XmlValue("test", 42), RuntimeException::new));
		assertFalse(XmlTypeProvider.INSTANCE.isEmpty(new XmlContainer("test"), RuntimeException::new));
		
		XmlElement selfClosing = new XmlElement("empty:generated");
		assertTrue(XmlTypeProvider.INSTANCE.isEmpty(selfClosing, RuntimeException::new));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.isNull(null, RuntimeException::new));
		
		XmlElement xmlNull = XmlTypeProvider.INSTANCE.createNull(RuntimeException::new);
		assertTrue(XmlTypeProvider.INSTANCE.isNull(xmlNull, RuntimeException::new));
		
		assertFalse(XmlTypeProvider.INSTANCE.isNull(new XmlElement("empty:generated"), RuntimeException::new));
		assertFalse(XmlTypeProvider.INSTANCE.isNull(new XmlValue("test", 42), RuntimeException::new));
		assertFalse(XmlTypeProvider.INSTANCE.isNull(new XmlContainer("test"), RuntimeException::new));
		assertFalse(XmlTypeProvider.INSTANCE.isNull(XmlTypeProvider.INSTANCE.createBoolean(true, RuntimeException::new), RuntimeException::new));
		assertFalse(XmlTypeProvider.INSTANCE.isNull(XmlTypeProvider.INSTANCE.createString("test", RuntimeException::new), RuntimeException::new));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getBoolean(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getByte(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getShort(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getInteger(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getLong(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getFloat(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getDouble(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getString(null, RuntimeException::new));
		
		XmlContainer wrongType = new XmlContainer("test");
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getBoolean(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getByte(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getShort(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getInteger(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getLong(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getFloat(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getDouble(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getString(wrongType, RuntimeException::new));
		
		XmlValue invalidValue = new XmlValue("test", "not-a-number");
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getBoolean(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getByte(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getShort(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getInteger(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getLong(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getFloat(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getDouble(invalidValue, RuntimeException::new));
		
		assertTrue(XmlTypeProvider.INSTANCE.getBoolean(new XmlValue("boolean:generated", true), RuntimeException::new));
		assertEquals((byte) 42, XmlTypeProvider.INSTANCE.getByte(new XmlValue("byte:generated", (byte) 42), RuntimeException::new));
		assertEquals((short) 42, XmlTypeProvider.INSTANCE.getShort(new XmlValue("short:generated", (short) 42), RuntimeException::new));
		assertEquals(42, XmlTypeProvider.INSTANCE.getInteger(new XmlValue("integer:generated", 42), RuntimeException::new));
		assertEquals(42L, XmlTypeProvider.INSTANCE.getLong(new XmlValue("long:generated", 42L), RuntimeException::new));
		assertEquals(42.5f, XmlTypeProvider.INSTANCE.getFloat(new XmlValue("float:generated", 42.5f), RuntimeException::new));
		assertEquals(42.5, XmlTypeProvider.INSTANCE.getDouble(new XmlValue("double:generated", 42.5), RuntimeException::new));
		assertEquals("test", XmlTypeProvider.INSTANCE.getString(new XmlValue("string:generated", "test"), RuntimeException::new));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getList(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getMap(null, RuntimeException::new));
		
		XmlValue wrongType = new XmlValue("test", 42);
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getList(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getMap(wrongType, RuntimeException::new));
		
		XmlContainer emptyContainer = new XmlContainer("test");
		assertTrue(XmlTypeProvider.INSTANCE.getList(emptyContainer, RuntimeException::new).isEmpty());
		assertTrue(XmlTypeProvider.INSTANCE.getMap(emptyContainer, RuntimeException::new).isEmpty());
		
		XmlContainer arrayContainer = new XmlContainer("list:generated");
		arrayContainer.add(new XmlValue("element:generated", 1));
		arrayContainer.add(new XmlValue("element:generated", 2));
		List<XmlElement> listResult = XmlTypeProvider.INSTANCE.getList(arrayContainer, RuntimeException::new);
		assertEquals(2, listResult.size());
		
		XmlContainer undefinedListContainer = new XmlContainer("list:generated");
		undefinedListContainer.add(new XmlValue("test", 42));
		List<XmlElement> undefinedListResult = XmlTypeProvider.INSTANCE.getList(undefinedListContainer, RuntimeException::new);
		assertEquals(1, undefinedListResult.size());
		
		XmlContainer objectListContainer = new XmlContainer("list:generated");
		objectListContainer.add(new XmlContainer("element1"));
		objectListContainer.add(new XmlContainer("element2"));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getList(objectListContainer, RuntimeException::new));
		
		XmlContainer objectContainer = new XmlContainer("map:generated");
		objectContainer.add(new XmlValue("key1", 1));
		objectContainer.add(new XmlValue("key2", 2));
		Map<String, XmlElement> mapResult = XmlTypeProvider.INSTANCE.getMap(objectContainer, RuntimeException::new);
		assertEquals(2, mapResult.size());
		assertTrue(mapResult.containsKey("key1"));
		assertTrue(mapResult.containsKey("key2"));
		
		XmlContainer undefinedMapContainer = new XmlContainer("map:generated");
		undefinedMapContainer.add(new XmlValue("test", 42));
		Map<String, XmlElement> undefinedMapResult = XmlTypeProvider.INSTANCE.getMap(undefinedMapContainer, RuntimeException::new);
		assertEquals(1, undefinedMapResult.size());
		assertTrue(undefinedMapResult.containsKey("test"));
		
		XmlContainer arrayMapContainer = new XmlContainer("map:generated");
		arrayMapContainer.add(new XmlValue("test", 1));
		arrayMapContainer.add(new XmlValue("test", 2));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.getMap(arrayMapContainer, RuntimeException::new));
	}
	
	@Test
	void mapOperations() {
		XmlContainer container = new XmlContainer("test");
		XmlElement testValue = new XmlValue("test", 42);
		
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.has(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.has(container, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.get(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.get(container, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.set(null, "key", testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.set(container, null, testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.set(container, "key", null, RuntimeException::new));
		
		XmlValue wrongType = new XmlValue("test", 42);
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.has(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.get(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.set(wrongType, "key", testValue, RuntimeException::new));
		
		XmlContainer arrayContainer = new XmlContainer("test");
		arrayContainer.add(new XmlValue("test", 1));
		arrayContainer.add(new XmlValue("test", 2));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.get(arrayContainer, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.set(arrayContainer, "key", testValue, RuntimeException::new));
		
		assertFalse(XmlTypeProvider.INSTANCE.has(container, "key", RuntimeException::new));
		
		XmlTypeProvider.INSTANCE.set(container, "key", testValue, RuntimeException::new);
		assertTrue(XmlTypeProvider.INSTANCE.has(container, "key", RuntimeException::new));
		assertNotNull(XmlTypeProvider.INSTANCE.get(container, "key", RuntimeException::new));
		assertEquals(testValue.getAsXmlValue().getAsInteger(), XmlTypeProvider.INSTANCE.get(container, "key", RuntimeException::new).getAsXmlValue().getAsInteger());
	}
	
	@Test
	void mergeOperations() {
		XmlElement emptyElement = new XmlElement("empty:generated");
		XmlElement nullElement = new XmlElement("null:generated");
		XmlValue value1 = new XmlValue("test", 1);
		XmlValue value2 = new XmlValue("test", 2);
		
		assertEquals(value1, XmlTypeProvider.INSTANCE.merge(null, value1, RuntimeException::new));
		assertEquals(value1, XmlTypeProvider.INSTANCE.merge(value1, null, RuntimeException::new));
		
		assertSame(value1, XmlTypeProvider.INSTANCE.merge(emptyElement, value1, RuntimeException::new));
		
		assertSame(nullElement, XmlTypeProvider.INSTANCE.merge(emptyElement, nullElement, RuntimeException::new));
		
		assertSame(value1, XmlTypeProvider.INSTANCE.merge(value1, nullElement, RuntimeException::new));
		assertSame(value1, XmlTypeProvider.INSTANCE.merge(nullElement, value1, RuntimeException::new));
		
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.merge(value1, value2, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.merge(value1, new XmlContainer("test"), RuntimeException::new));
		
		XmlContainer array1 = new XmlContainer("test");
		array1.add(new XmlValue("element:generated", 1));
		array1.add(new XmlValue("element:generated", 2));
		
		XmlContainer array2 = new XmlContainer("test");
		array2.add(new XmlValue("element:generated", 3));
		array2.add(new XmlValue("element:generated", 4));
		
		assertEquals(array1, XmlTypeProvider.INSTANCE.merge(array1, nullElement, RuntimeException::new));
		assertEquals(array1, XmlTypeProvider.INSTANCE.merge(nullElement, array1, RuntimeException::new));
		
		XmlElement mergedArray = XmlTypeProvider.INSTANCE.merge(array1, array2, RuntimeException::new);
		assertTrue(mergedArray.isXmlContainer());
		assertEquals(4, mergedArray.getAsXmlContainer().size());
		
		XmlContainer object1 = new XmlContainer("test");
		object1.add(new XmlValue("key1", 1));
		
		XmlContainer object2 = new XmlContainer("test");
		object2.add(new XmlValue("key2", 2));
		
		assertEquals(object1, XmlTypeProvider.INSTANCE.merge(object1, nullElement, RuntimeException::new));
		assertEquals(object1, XmlTypeProvider.INSTANCE.merge(nullElement, object1, RuntimeException::new));
		
		XmlElement mergedObject = XmlTypeProvider.INSTANCE.merge(object1, object2, RuntimeException::new);
		assertTrue(mergedObject.isXmlContainer());
		assertEquals(2, mergedObject.getAsXmlContainer().size());
		
		XmlContainer undefined1 = new XmlContainer("test");
		undefined1.add(new XmlValue("test", 1));
		
		XmlContainer undefined2 = new XmlContainer("test");
		undefined2.add(new XmlValue("test", 2));
		
		assertDoesNotThrow(() -> XmlTypeProvider.INSTANCE.merge(undefined1, undefined2, RuntimeException::new));
		
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.merge(array1, object1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.merge(object1, array1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> XmlTypeProvider.INSTANCE.merge(value1, array1, RuntimeException::new));
	}
	
	@Test
	void nameEscapingForNumericKeys() {
		XmlContainer container = new XmlContainer("test");
		XmlElement value = new XmlValue("test", 42);
		
		assertDoesNotThrow(() -> XmlTypeProvider.INSTANCE.set(container, "123", value, RuntimeException::new));
		assertTrue(XmlTypeProvider.INSTANCE.has(container, "123", RuntimeException::new));
		assertNotNull(XmlTypeProvider.INSTANCE.get(container, "123", RuntimeException::new));
		
		assertDoesNotThrow(() -> XmlTypeProvider.INSTANCE.set(container, "abc", value, RuntimeException::new));
		assertTrue(XmlTypeProvider.INSTANCE.has(container, "abc", RuntimeException::new));
		assertNotNull(XmlTypeProvider.INSTANCE.get(container, "abc", RuntimeException::new));
	}
}
