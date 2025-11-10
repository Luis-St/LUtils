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
	void emptyReturnsSelfClosingElement() {
		XmlElement empty = XmlTypeProvider.INSTANCE.empty();
		assertTrue(empty.isSelfClosing());
		assertEquals("empty:generated", empty.getName());
	}
	
	@Test
	void createNullReturnsNullValue() {
		XmlElement nullValue = XmlTypeProvider.INSTANCE.createNull().resultOrThrow();
		assertTrue(nullValue.isSelfClosing());
		assertEquals("null:generated", nullValue.getName());
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
		Result<XmlElement> res = XmlTypeProvider.INSTANCE.createString(null);
		assertTrue(res.isError());
		assertTrue(res.errorOrThrow().startsWith("Value 'null'"));
	}
	
	@Test
	void createCollectionTypes() {
		Result<XmlElement> nullList = XmlTypeProvider.INSTANCE.createList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().startsWith("Value 'null'"));
		
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
		
		Result<XmlElement> nullMap = XmlTypeProvider.INSTANCE.createMap((Map<String, XmlElement>) null);
		assertTrue(nullMap.isError());
		assertTrue(nullMap.errorOrThrow().startsWith("Value 'null'"));
		
		XmlElement map = XmlTypeProvider.INSTANCE.createMap(Map.of("test", testElement)).resultOrThrow();
		assertTrue(map.isXmlContainer());
		assertEquals("map:generated", map.getName());
		assertEquals(1, map.getAsXmlContainer().size());
		assertEquals("test", map.getAsXmlContainer().get(0).getName());
	}
	
	@Test
	void getEmptyValidation() {
		Result<XmlElement> nullEmpty = XmlTypeProvider.INSTANCE.getEmpty(null);
		assertTrue(nullEmpty.isError());
		assertTrue(nullEmpty.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(XmlTypeProvider.INSTANCE.getEmpty(new XmlValue("test", 42)).isError());
		assertTrue(XmlTypeProvider.INSTANCE.getEmpty(new XmlContainer("test")).isError());
		
		XmlElement selfClosing = new XmlElement("empty:generated");
		assertEquals(selfClosing, XmlTypeProvider.INSTANCE.getEmpty(selfClosing).resultOrThrow());
	}
	
	@Test
	void isNullValidation() {
		Result<Boolean> nullIsNull = XmlTypeProvider.INSTANCE.isNull(null);
		assertTrue(nullIsNull.isError());
		assertTrue(nullIsNull.errorOrThrow().startsWith("Value 'null'"));
		
		XmlElement xmlNull = XmlTypeProvider.INSTANCE.createNull().resultOrThrow();
		assertTrue(XmlTypeProvider.INSTANCE.isNull(xmlNull).resultOrThrow());
		
		assertFalse(XmlTypeProvider.INSTANCE.isNull(new XmlElement("empty:generated")).resultOrThrow());
		assertFalse(XmlTypeProvider.INSTANCE.isNull(new XmlValue("test", 42)).resultOrThrow());
		assertFalse(XmlTypeProvider.INSTANCE.isNull(new XmlContainer("test")).resultOrThrow());
		assertFalse(XmlTypeProvider.INSTANCE.isNull(XmlTypeProvider.INSTANCE.createBoolean(true).resultOrThrow()).resultOrThrow());
		assertFalse(XmlTypeProvider.INSTANCE.isNull(XmlTypeProvider.INSTANCE.createString("test").resultOrThrow()).resultOrThrow());
	}
	
	@Test
	void getPrimitiveTypes() {
		Result<Boolean> nullBoolean = XmlTypeProvider.INSTANCE.getBoolean(null);
		assertTrue(nullBoolean.isError());
		assertTrue(nullBoolean.errorOrThrow().startsWith("Value 'null'"));
		Result<Byte> nullByte = XmlTypeProvider.INSTANCE.getByte(null);
		assertTrue(nullByte.isError());
		assertTrue(nullByte.errorOrThrow().startsWith("Value 'null'"));
		Result<Short> nullShort = XmlTypeProvider.INSTANCE.getShort(null);
		assertTrue(nullShort.isError());
		assertTrue(nullShort.errorOrThrow().startsWith("Value 'null'"));
		Result<Integer> nullInteger = XmlTypeProvider.INSTANCE.getInteger(null);
		assertTrue(nullInteger.isError());
		assertTrue(nullInteger.errorOrThrow().startsWith("Value 'null'"));
		Result<Long> nullLong = XmlTypeProvider.INSTANCE.getLong(null);
		assertTrue(nullLong.isError());
		assertTrue(nullLong.errorOrThrow().startsWith("Value 'null'"));
		Result<Float> nullFloat = XmlTypeProvider.INSTANCE.getFloat(null);
		assertTrue(nullFloat.isError());
		assertTrue(nullFloat.errorOrThrow().startsWith("Value 'null'"));
		Result<Double> nullDouble = XmlTypeProvider.INSTANCE.getDouble(null);
		assertTrue(nullDouble.isError());
		assertTrue(nullDouble.errorOrThrow().startsWith("Value 'null'"));
		Result<String> nullString = XmlTypeProvider.INSTANCE.getString(null);
		assertTrue(nullString.isError());
		assertTrue(nullString.errorOrThrow().startsWith("Value 'null'"));
		
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
		Result<List<XmlElement>> nullList = XmlTypeProvider.INSTANCE.getList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().startsWith("Value 'null'"));
		Result<Map<String, XmlElement>> nullMap = XmlTypeProvider.INSTANCE.getMap(null);
		assertTrue(nullMap.isError());
		assertTrue(nullMap.errorOrThrow().startsWith("Value 'null'"));
		
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
		
		Result<Boolean> nullHas = XmlTypeProvider.INSTANCE.has(null, "key");
		assertTrue(nullHas.isError());
		assertTrue(nullHas.errorOrThrow().startsWith("Value 'null'"));
		Result<Boolean> hasNullKey = XmlTypeProvider.INSTANCE.has(container, null);
		assertTrue(hasNullKey.isError());
		assertTrue(hasNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<XmlElement> nullGet = XmlTypeProvider.INSTANCE.get(null, "key");
		assertTrue(nullGet.isError());
		assertTrue(nullGet.errorOrThrow().startsWith("Value 'null'"));
		Result<XmlElement> getNullKey = XmlTypeProvider.INSTANCE.get(container, null);
		assertTrue(getNullKey.isError());
		assertTrue(getNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<XmlElement> nullSet = XmlTypeProvider.INSTANCE.set(null, "key", testValue);
		assertTrue(nullSet.isError());
		assertTrue(nullSet.errorOrThrow().startsWith("Value 'null'"));
		Result<XmlElement> setNullKey = XmlTypeProvider.INSTANCE.set(container, null, testValue);
		assertTrue(setNullKey.isError());
		assertTrue(setNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<XmlElement> setNullValue = XmlTypeProvider.INSTANCE.set(container, "key", (XmlElement) null);
		assertTrue(setNullValue.isError());
		assertTrue(setNullValue.errorOrThrow().startsWith("Value 'null'"));
		
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
		
		Result<XmlElement> nullType = XmlTypeProvider.INSTANCE.set(null, "key", Result.success(testValue));
		assertTrue(nullType.isError());
		assertTrue(nullType.errorOrThrow().startsWith("Type 'null'"));
		Result<XmlElement> nullKey = XmlTypeProvider.INSTANCE.set(container, null, Result.success(testValue));
		assertTrue(nullKey.isError());
		assertTrue(nullKey.errorOrThrow().startsWith("Key 'null'"));
		Result<XmlElement> nullValue = XmlTypeProvider.INSTANCE.set(container, "key", (Result<XmlElement>) null);
		assertTrue(nullValue.isError());
		assertTrue(nullValue.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(XmlTypeProvider.INSTANCE.set(container, "key", Result.error("error")).isError());
		assertTrue(XmlTypeProvider.INSTANCE.set(container, "key", Result.success(testValue)).isSuccess());
	}
	
	@Test
	void mergeOperations() {
		XmlElement emptyElement = new XmlElement("empty:generated");
		XmlElement nullElement = new XmlElement("null:generated");
		XmlValue value1 = new XmlValue("test", 1);
		XmlValue value2 = new XmlValue("test", 2);
		
		assertEquals(value1, XmlTypeProvider.INSTANCE.merge(null, value1).resultOrThrow());
		assertEquals(value1, XmlTypeProvider.INSTANCE.merge(value1, (XmlElement) null).resultOrThrow());
		
		assertSame(value1, XmlTypeProvider.INSTANCE.merge(emptyElement, value1).resultOrThrow());
		
		assertSame(nullElement, XmlTypeProvider.INSTANCE.merge(emptyElement, nullElement).resultOrThrow());
		
		assertSame(value1, XmlTypeProvider.INSTANCE.merge(value1, nullElement).resultOrThrow());
		assertSame(value1, XmlTypeProvider.INSTANCE.merge(nullElement, value1).resultOrThrow());
		
		assertTrue(XmlTypeProvider.INSTANCE.merge(value1, value2).isError());
		assertTrue(XmlTypeProvider.INSTANCE.merge(value1, new XmlContainer("test")).isError());
		
		XmlContainer array1 = new XmlContainer("test");
		array1.add(new XmlValue("element:generated", 1));
		array1.add(new XmlValue("element:generated", 2));
		
		XmlContainer array2 = new XmlContainer("test");
		array2.add(new XmlValue("element:generated", 3));
		array2.add(new XmlValue("element:generated", 4));
		
		assertEquals(array1, XmlTypeProvider.INSTANCE.merge(array1, nullElement).resultOrThrow());
		assertEquals(array1, XmlTypeProvider.INSTANCE.merge(nullElement, array1).resultOrThrow());
		
		XmlElement mergedArray = XmlTypeProvider.INSTANCE.merge(array1, array2).resultOrThrow();
		assertTrue(mergedArray.isXmlContainer());
		assertEquals(4, mergedArray.getAsXmlContainer().size());
		
		XmlContainer object1 = new XmlContainer("test");
		object1.add(new XmlValue("key1", 1));
		
		XmlContainer object2 = new XmlContainer("test");
		object2.add(new XmlValue("key2", 2));
		
		assertEquals(object1, XmlTypeProvider.INSTANCE.merge(object1, nullElement).resultOrThrow());
		assertEquals(object1, XmlTypeProvider.INSTANCE.merge(nullElement, object1).resultOrThrow());
		
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
		
		Result<XmlElement> nullCurrent = XmlTypeProvider.INSTANCE.merge(null, Result.success(testValue));
		assertTrue(nullCurrent.isError());
		assertTrue(nullCurrent.errorOrThrow().startsWith("Current value 'null'"));
		Result<XmlElement> nullValue = XmlTypeProvider.INSTANCE.merge(emptyElement, (Result<XmlElement>) null);
		assertTrue(nullValue.isError());
		assertTrue(nullValue.errorOrThrow().startsWith("Value 'null'"));
		
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
