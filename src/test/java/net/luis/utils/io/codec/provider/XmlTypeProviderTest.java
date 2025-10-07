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

package net.luis.utils.io.codec.provider;

import net.luis.utils.io.data.xml.*;
import net.luis.utils.util.result.Result;
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
	void useRootCreatesNewInstanceWhenNeeded() {
		XmlTypeProvider rootProvider = XmlTypeProvider.INSTANCE.useRoot();
		assertNotNull(rootProvider);
		assertSame(rootProvider, rootProvider.useRoot());
	}
	
	@Test
	void emptyReturnsSelfClosingElement() {
		XmlElement empty = XmlTypeProvider.INSTANCE.empty();
		assertTrue(empty.isSelfClosing());
		assertEquals("empty:generated", empty.getName());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertTrue(XmlTypeProvider.INSTANCE.createBoolean(true).resultOrThrow().isXmlValue());
		assertEquals("boolean:generated", XmlTypeProvider.INSTANCE.createBoolean(true).resultOrThrow().getName());
		assertTrue(XmlTypeProvider.INSTANCE.createBoolean(true).resultOrThrow().getAsXmlValue().getAsBoolean());
		
		assertTrue(XmlTypeProvider.INSTANCE.createByte((byte) 42).resultOrThrow().isXmlValue());
		assertEquals("byte:generated", XmlTypeProvider.INSTANCE.createByte((byte) 42).resultOrThrow().getName());
		assertEquals(42, XmlTypeProvider.INSTANCE.createByte((byte) 42).resultOrThrow().getAsXmlValue().getAsByte());
		
		assertTrue(XmlTypeProvider.INSTANCE.createShort((short) 42).resultOrThrow().isXmlValue());
		assertEquals("short:generated", XmlTypeProvider.INSTANCE.createShort((short) 42).resultOrThrow().getName());
		assertEquals(42, XmlTypeProvider.INSTANCE.createShort((short) 42).resultOrThrow().getAsXmlValue().getAsShort());
		
		assertTrue(XmlTypeProvider.INSTANCE.createInteger(42).resultOrThrow().isXmlValue());
		assertEquals("integer:generated", XmlTypeProvider.INSTANCE.createInteger(42).resultOrThrow().getName());
		assertEquals(42, XmlTypeProvider.INSTANCE.createInteger(42).resultOrThrow().getAsXmlValue().getAsInteger());
		
		assertTrue(XmlTypeProvider.INSTANCE.createLong(42L).resultOrThrow().isXmlValue());
		assertEquals("long:generated", XmlTypeProvider.INSTANCE.createLong(42L).resultOrThrow().getName());
		assertEquals(42L, XmlTypeProvider.INSTANCE.createLong(42L).resultOrThrow().getAsXmlValue().getAsLong());
		
		assertTrue(XmlTypeProvider.INSTANCE.createFloat(42.5f).resultOrThrow().isXmlValue());
		assertEquals("float:generated", XmlTypeProvider.INSTANCE.createFloat(42.5f).resultOrThrow().getName());
		assertEquals(42.5f, XmlTypeProvider.INSTANCE.createFloat(42.5f).resultOrThrow().getAsXmlValue().getAsFloat());
		
		assertTrue(XmlTypeProvider.INSTANCE.createDouble(42.5).resultOrThrow().isXmlValue());
		assertEquals("double:generated", XmlTypeProvider.INSTANCE.createDouble(42.5).resultOrThrow().getName());
		assertEquals(42.5, XmlTypeProvider.INSTANCE.createDouble(42.5).resultOrThrow().getAsXmlValue().getAsDouble());
		
		assertTrue(XmlTypeProvider.INSTANCE.createString("test").resultOrThrow().isXmlValue());
		assertEquals("string:generated", XmlTypeProvider.INSTANCE.createString("test").resultOrThrow().getName());
		assertEquals("test", XmlTypeProvider.INSTANCE.createString("test").resultOrThrow().getAsXmlValue().getAsString());
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.createString(null));
	}
	
	@Test
	void createCollectionTypes() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.createList(null));
		
		XmlElement emptyList = XmlTypeProvider.INSTANCE.createList(List.of()).resultOrThrow();
		assertTrue(emptyList.isXmlContainer());
		assertEquals("list:generated", emptyList.getName());
		assertTrue(emptyList.getAsXmlContainer().isEmpty());
		
		XmlElement testElement = new XmlValue("test", 42);
		XmlElement list = XmlTypeProvider.INSTANCE.createList(List.of(testElement)).resultOrThrow();
		assertTrue(list.isXmlContainer());
		assertEquals("list:generated", list.getName());
		assertEquals(1, list.getAsXmlContainer().size());
		assertEquals("element:generated", list.getAsXmlContainer().get(0).getName());
		
		XmlElement emptyMap = XmlTypeProvider.INSTANCE.createMap().resultOrThrow();
		assertTrue(emptyMap.isXmlContainer());
		assertEquals("map:generated", emptyMap.getName());
		
		XmlElement map = XmlTypeProvider.INSTANCE.createMap(Map.of("test", testElement)).resultOrThrow();
		assertTrue(map.isXmlContainer());
		assertEquals("map:generated", map.getName());
		assertEquals(1, map.getAsXmlContainer().size());
		assertEquals("test", map.getAsXmlContainer().get(0).getName());
	}
	
	@Test
	void createMapWithRootName() {
		XmlTypeProvider rootProvider = XmlTypeProvider.INSTANCE.useRoot();
		
		XmlElement emptyMap = rootProvider.createMap().resultOrThrow();
		assertEquals("root:generated", emptyMap.getName());
		
		XmlElement map = rootProvider.createMap(Map.of("key", new XmlValue("test", 42))).resultOrThrow();
		assertEquals("root:generated", map.getName());
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getEmpty(null));
		
		assertTrue(XmlTypeProvider.INSTANCE.getEmpty(new XmlValue("test", 42)).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getEmpty(new XmlContainer("test")).isError());
		
		XmlElement selfClosing = new XmlElement("empty:generated");
		assertEquals(selfClosing, XmlTypeProvider.INSTANCE.getEmpty(selfClosing).resultOrThrow());
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getBoolean(null));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getByte(null));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getShort(null));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getInteger(null));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getLong(null));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getFloat(null));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getDouble(null));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getString(null));
		
		XmlContainer wrongType = new XmlContainer("test");
		assertTrue(XmlTypeProvider.INSTANCE.getBoolean(wrongType).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getByte(wrongType).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getShort(wrongType).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getInteger(wrongType).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getLong(wrongType).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getFloat(wrongType).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getDouble(wrongType).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getString(wrongType).isError());
		
		XmlValue invalidValue = new XmlValue("test", "not-a-number");
		assertTrue(XmlTypeProvider.INSTANCE.getBoolean(invalidValue).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getByte(invalidValue).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getShort(invalidValue).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getInteger(invalidValue).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getLong(invalidValue).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getFloat(invalidValue).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getDouble(invalidValue).isError());
		
		assertTrue(XmlTypeProvider.INSTANCE.getBoolean(new XmlValue("boolean:generated", true)).resultOrThrow());
		assertEquals((byte) 42, XmlTypeProvider.INSTANCE.getByte(new XmlValue("byte:generated", (byte) 42)).resultOrThrow());
		assertEquals((short) 42, XmlTypeProvider.INSTANCE.getShort(new XmlValue("short:generated", (short) 42)).resultOrThrow());
		assertEquals(42, XmlTypeProvider.INSTANCE.getInteger(new XmlValue("integer:generated", 42)).resultOrThrow());
		assertEquals(42L, XmlTypeProvider.INSTANCE.getLong(new XmlValue("long:generated", 42L)).resultOrThrow());
		assertEquals(42.5f, XmlTypeProvider.INSTANCE.getFloat(new XmlValue("float:generated", 42.5f)).resultOrThrow());
		assertEquals(42.5, XmlTypeProvider.INSTANCE.getDouble(new XmlValue("double:generated", 42.5)).resultOrThrow());
		assertEquals("test", XmlTypeProvider.INSTANCE.getString(new XmlValue("string:generated", "test")).resultOrThrow());
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getList(null));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getMap(null));
		
		XmlValue wrongType = new XmlValue("test", 42);
		assertTrue(XmlTypeProvider.INSTANCE.getList(wrongType).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getMap(wrongType).isError());
		
		XmlContainer emptyContainer = new XmlContainer("test");
		assertTrue(XmlTypeProvider.INSTANCE.getList(emptyContainer).resultOrThrow().isEmpty());
		assertTrue(XmlTypeProvider.INSTANCE.getMap(emptyContainer).resultOrThrow().isEmpty());
		
		XmlContainer arrayContainer = new XmlContainer("list:generated");
		arrayContainer.add(new XmlValue("element:generated", 1));
		arrayContainer.add(new XmlValue("element:generated", 2));
		List<XmlElement> listResult = XmlTypeProvider.INSTANCE.getList(arrayContainer).resultOrThrow();
		assertEquals(2, listResult.size());
		
		XmlContainer undefinedListContainer = new XmlContainer("list:generated");
		undefinedListContainer.add(new XmlValue("test", 42));
		List<XmlElement> undefinedListResult = XmlTypeProvider.INSTANCE.getList(undefinedListContainer).resultOrThrow();
		assertEquals(1, undefinedListResult.size());
		
		XmlContainer objectListContainer = new XmlContainer("list:generated");
		objectListContainer.add(new XmlContainer("element1"));
		objectListContainer.add(new XmlContainer("element2"));
		assertTrue(XmlTypeProvider.INSTANCE.getList(objectListContainer).isError());
		
		XmlContainer objectContainer = new XmlContainer("map:generated");
		objectContainer.add(new XmlValue("key1", 1));
		objectContainer.add(new XmlValue("key2", 2));
		Map<String, XmlElement> mapResult = XmlTypeProvider.INSTANCE.getMap(objectContainer).resultOrThrow();
		assertEquals(2, mapResult.size());
		assertTrue(mapResult.containsKey("key1"));
		assertTrue(mapResult.containsKey("key2"));
		
		XmlContainer undefinedMapContainer = new XmlContainer("map:generated");
		undefinedMapContainer.add(new XmlValue("test", 42));
		Map<String, XmlElement> undefinedMapResult = XmlTypeProvider.INSTANCE.getMap(undefinedMapContainer).resultOrThrow();
		assertEquals(1, undefinedMapResult.size());
		assertTrue(undefinedMapResult.containsKey("test"));
		
		XmlContainer arrayMapContainer = new XmlContainer("map:generated");
		arrayMapContainer.add(new XmlValue("test", 1));
		arrayMapContainer.add(new XmlValue("test", 2));
		assertTrue(XmlTypeProvider.INSTANCE.getMap(arrayMapContainer).isError());
	}
	
	@Test
	void mapOperations() {
		XmlContainer container = new XmlContainer("test");
		XmlElement testValue = new XmlValue("test", 42);
		
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.has(null, "key"));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.has(container, null));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.get(null, "key"));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.get(container, null));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(null, "key", testValue));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(container, null, testValue));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(container, "key", (XmlElement) null));
		
		XmlValue wrongType = new XmlValue("test", 42);
		assertTrue(XmlTypeProvider.INSTANCE.has(wrongType, "key").isError());
		assertTrue(XmlTypeProvider.INSTANCE.get(wrongType, "key").isError());
		assertTrue(XmlTypeProvider.INSTANCE.set(wrongType, "key", testValue).isError());
		
		XmlContainer arrayContainer = new XmlContainer("test");
		arrayContainer.add(new XmlValue("test", 1));
		arrayContainer.add(new XmlValue("test", 2));
		assertTrue(XmlTypeProvider.INSTANCE.get(arrayContainer, "key").isError());
		assertTrue(XmlTypeProvider.INSTANCE.set(arrayContainer, "key", testValue).isError());
		
		assertFalse(XmlTypeProvider.INSTANCE.has(container, "key").resultOrThrow());
		assertNull(XmlTypeProvider.INSTANCE.get(container, "key").resultOrThrow());
		
		assertSame(container, XmlTypeProvider.INSTANCE.set(container, "key", testValue).resultOrThrow());
		assertTrue(XmlTypeProvider.INSTANCE.has(container, "key").resultOrThrow());
		assertNotNull(XmlTypeProvider.INSTANCE.get(container, "key").resultOrThrow());
		assertEquals(testValue.getAsXmlValue().getAsInteger(), XmlTypeProvider.INSTANCE.get(container, "key").resultOrThrow().getAsXmlValue().getAsInteger());
	}
	
	@Test
	void mapOperationsWithResults() {
		XmlContainer container = new XmlContainer("test");
		XmlElement testValue = new XmlValue("test", 42);
		
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(null, "key", Result.success(testValue)));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(container, null, Result.success(testValue)));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(container, "key", (Result<XmlElement>) null));
		
		assertTrue(XmlTypeProvider.INSTANCE.set(container, "key", Result.error("error")).isError());
		assertTrue(XmlTypeProvider.INSTANCE.set(container, "key", Result.success(testValue)).isSuccess());
	}
	
	@Test
	void mergeOperations() {
		XmlElement emptyElement = new XmlElement("empty:generated");
		XmlValue value1 = new XmlValue("test", 1);
		XmlValue value2 = new XmlValue("test", 2);
		
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.merge(null, value1));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.merge(value1, (XmlElement) null));
		
		assertSame(value1, XmlTypeProvider.INSTANCE.merge(emptyElement, value1).resultOrThrow());
		
		assertTrue(XmlTypeProvider.INSTANCE.merge(value1, value2).isError());
		assertTrue(XmlTypeProvider.INSTANCE.merge(value1, new XmlContainer("test")).isError());
		
		XmlContainer array1 = new XmlContainer("test");
		array1.add(new XmlValue("element:generated", 1));
		array1.add(new XmlValue("element:generated", 2));
		
		XmlContainer array2 = new XmlContainer("test");
		array2.add(new XmlValue("element:generated", 3));
		array2.add(new XmlValue("element:generated", 4));
		
		XmlElement mergedArray = XmlTypeProvider.INSTANCE.merge(array1, array2).resultOrThrow();
		assertTrue(mergedArray.isXmlContainer());
		assertEquals(4, mergedArray.getAsXmlContainer().size());
		
		XmlContainer object1 = new XmlContainer("test");
		object1.add(new XmlValue("key1", 1));
		
		XmlContainer object2 = new XmlContainer("test");
		object2.add(new XmlValue("key2", 2));
		
		XmlElement mergedObject = XmlTypeProvider.INSTANCE.merge(object1, object2).resultOrThrow();
		assertTrue(mergedObject.isXmlContainer());
		assertEquals(2, mergedObject.getAsXmlContainer().size());
		
		XmlContainer undefined1 = new XmlContainer("test");
		undefined1.add(new XmlValue("test", 1));
		
		XmlContainer undefined2 = new XmlContainer("test");
		undefined2.add(new XmlValue("test", 2));
		
		assertTrue(XmlTypeProvider.INSTANCE.merge(undefined1, undefined2).isSuccess());
		
		assertTrue(XmlTypeProvider.INSTANCE.merge(array1, object1).isError());
		assertTrue(XmlTypeProvider.INSTANCE.merge(object1, array1).isError());
		assertTrue(XmlTypeProvider.INSTANCE.merge(value1, array1).isError());
	}
	
	@Test
	void mergeOperationsWithResults() {
		XmlElement emptyElement = new XmlElement("empty:generated");
		XmlValue testValue = new XmlValue("test", 42);
		
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.merge(null, Result.success(testValue)));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.merge(emptyElement, (Result<XmlElement>) null));
		
		assertTrue(XmlTypeProvider.INSTANCE.merge(emptyElement, Result.error("error")).isError());
		assertEquals(testValue, XmlTypeProvider.INSTANCE.merge(emptyElement, Result.success(testValue)).resultOrThrow());
	}
	
	@Test
	void nameEscapingForNumericKeys() {
		XmlContainer container = new XmlContainer("test");
		XmlElement value = new XmlValue("test", 42);
		
		assertTrue(XmlTypeProvider.INSTANCE.set(container, "123", value).isSuccess());
		assertTrue(XmlTypeProvider.INSTANCE.has(container, "123").resultOrThrow());
		assertNotNull(XmlTypeProvider.INSTANCE.get(container, "123").resultOrThrow());
		
		assertTrue(XmlTypeProvider.INSTANCE.set(container, "abc", value).isSuccess());
		assertTrue(XmlTypeProvider.INSTANCE.has(container, "abc").resultOrThrow());
		assertNotNull(XmlTypeProvider.INSTANCE.get(container, "abc").resultOrThrow());
	}
}
