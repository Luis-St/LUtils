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
		XmlElement nullValue = XmlTypeProvider.INSTANCE.createNull();
		assertTrue(nullValue.isSelfClosing());
		assertEquals("null:generated", nullValue.getName());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertTrue(XmlTypeProvider.INSTANCE.createBoolean(true).isXmlValue());
		assertEquals("boolean:generated", XmlTypeProvider.INSTANCE.createBoolean(true).getName());
		assertTrue(XmlTypeProvider.INSTANCE.createBoolean(true).getAsXmlValue().getAsBoolean());
		
		assertTrue(XmlTypeProvider.INSTANCE.createByte((byte) 42).isXmlValue());
		assertEquals("byte:generated", XmlTypeProvider.INSTANCE.createByte((byte) 42).getName());
		assertEquals(42, XmlTypeProvider.INSTANCE.createByte((byte) 42).getAsXmlValue().getAsByte());
		
		assertTrue(XmlTypeProvider.INSTANCE.createShort((short) 42).isXmlValue());
		assertEquals("short:generated", XmlTypeProvider.INSTANCE.createShort((short) 42).getName());
		assertEquals(42, XmlTypeProvider.INSTANCE.createShort((short) 42).getAsXmlValue().getAsShort());
		
		assertTrue(XmlTypeProvider.INSTANCE.createInteger(42).isXmlValue());
		assertEquals("integer:generated", XmlTypeProvider.INSTANCE.createInteger(42).getName());
		assertEquals(42, XmlTypeProvider.INSTANCE.createInteger(42).getAsXmlValue().getAsInteger());
		
		assertTrue(XmlTypeProvider.INSTANCE.createLong(42L).isXmlValue());
		assertEquals("long:generated", XmlTypeProvider.INSTANCE.createLong(42L).getName());
		assertEquals(42L, XmlTypeProvider.INSTANCE.createLong(42L).getAsXmlValue().getAsLong());
		
		assertTrue(XmlTypeProvider.INSTANCE.createFloat(42.5f).isXmlValue());
		assertEquals("float:generated", XmlTypeProvider.INSTANCE.createFloat(42.5f).getName());
		assertEquals(42.5f, XmlTypeProvider.INSTANCE.createFloat(42.5f).getAsXmlValue().getAsFloat());
		
		assertTrue(XmlTypeProvider.INSTANCE.createDouble(42.5).isXmlValue());
		assertEquals("double:generated", XmlTypeProvider.INSTANCE.createDouble(42.5).getName());
		assertEquals(42.5, XmlTypeProvider.INSTANCE.createDouble(42.5).getAsXmlValue().getAsDouble());
		
		assertTrue(XmlTypeProvider.INSTANCE.createString("test").isXmlValue());
		assertEquals("string:generated", XmlTypeProvider.INSTANCE.createString("test").getName());
		assertEquals("test", XmlTypeProvider.INSTANCE.createString("test").getAsXmlValue().getAsString());
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.createString(null));
	}
	
	@Test
	void createCollectionTypes() {
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.createList(null));
		
		XmlElement emptyList = XmlTypeProvider.INSTANCE.createList(List.of());
		assertTrue(emptyList.isXmlContainer());
		assertEquals("list:generated", emptyList.getName());
		assertTrue(emptyList.getAsXmlContainer().isEmpty());
		
		XmlElement testElement = new XmlValue("test", 42);
		XmlElement list = XmlTypeProvider.INSTANCE.createList(List.of(testElement));
		assertTrue(list.isXmlContainer());
		assertEquals("list:generated", list.getName());
		assertEquals(1, list.getAsXmlContainer().size());
		assertEquals("element:generated", list.getAsXmlContainer().get(0).getName());
		
		XmlElement emptyMap = XmlTypeProvider.INSTANCE.createMap();
		assertTrue(emptyMap.isXmlContainer());
		assertEquals("map:generated", emptyMap.getName());
		
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.createMap(null));
		
		XmlElement map = XmlTypeProvider.INSTANCE.createMap(Map.of("test", testElement));
		assertTrue(map.isXmlContainer());
		assertEquals("map:generated", map.getName());
		assertEquals(1, map.getAsXmlContainer().size());
		assertEquals("test", map.getAsXmlContainer().get(0).getName());
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.isEmpty(null));
		assertFalse(XmlTypeProvider.INSTANCE.isEmpty(new XmlValue("test", 42)));
		assertFalse(XmlTypeProvider.INSTANCE.isEmpty(new XmlContainer("test")));
		
		XmlElement selfClosing = new XmlElement("empty:generated");
		assertTrue(XmlTypeProvider.INSTANCE.isEmpty(selfClosing));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.isNull(null));
		
		XmlElement xmlNull = XmlTypeProvider.INSTANCE.createNull();
		assertTrue(XmlTypeProvider.INSTANCE.isNull(xmlNull));
		
		assertFalse(XmlTypeProvider.INSTANCE.isNull(new XmlElement("empty:generated")));
		assertFalse(XmlTypeProvider.INSTANCE.isNull(new XmlValue("test", 42)));
		assertFalse(XmlTypeProvider.INSTANCE.isNull(new XmlContainer("test")));
		assertFalse(XmlTypeProvider.INSTANCE.isNull(XmlTypeProvider.INSTANCE.createBoolean(true)));
		assertFalse(XmlTypeProvider.INSTANCE.isNull(XmlTypeProvider.INSTANCE.createString("test")));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getBoolean(null));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getByte(null));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getShort(null));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getInteger(null));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getLong(null));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getFloat(null));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getDouble(null));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getString(null));
		
		XmlContainer wrongType = new XmlContainer("test");
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getBoolean(wrongType));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getByte(wrongType));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getShort(wrongType));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getInteger(wrongType));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getLong(wrongType));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getFloat(wrongType));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getDouble(wrongType));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getString(wrongType));
		
		XmlValue invalidValue = new XmlValue("test", "not-a-number");
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getBoolean(invalidValue));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getByte(invalidValue));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getShort(invalidValue));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getInteger(invalidValue));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getLong(invalidValue));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getFloat(invalidValue));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getDouble(invalidValue));
		
		assertTrue(XmlTypeProvider.INSTANCE.getBoolean(new XmlValue("boolean:generated", true)));
		assertEquals((byte) 42, XmlTypeProvider.INSTANCE.getByte(new XmlValue("byte:generated", (byte) 42)));
		assertEquals((short) 42, XmlTypeProvider.INSTANCE.getShort(new XmlValue("short:generated", (short) 42)));
		assertEquals(42, XmlTypeProvider.INSTANCE.getInteger(new XmlValue("integer:generated", 42)));
		assertEquals(42L, XmlTypeProvider.INSTANCE.getLong(new XmlValue("long:generated", 42L)));
		assertEquals(42.5f, XmlTypeProvider.INSTANCE.getFloat(new XmlValue("float:generated", 42.5f)));
		assertEquals(42.5, XmlTypeProvider.INSTANCE.getDouble(new XmlValue("double:generated", 42.5)));
		assertEquals("test", XmlTypeProvider.INSTANCE.getString(new XmlValue("string:generated", "test")));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getList(null));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getMap(null));
		
		XmlValue wrongType = new XmlValue("test", 42);
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getList(wrongType));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getMap(wrongType));
		
		XmlContainer emptyContainer = new XmlContainer("test");
		assertTrue(XmlTypeProvider.INSTANCE.getList(emptyContainer).isEmpty());
		assertTrue(XmlTypeProvider.INSTANCE.getMap(emptyContainer).isEmpty());
		
		XmlContainer arrayContainer = new XmlContainer("list:generated");
		arrayContainer.add(new XmlValue("element:generated", 1));
		arrayContainer.add(new XmlValue("element:generated", 2));
		List<XmlElement> listResult = XmlTypeProvider.INSTANCE.getList(arrayContainer);
		assertEquals(2, listResult.size());
		
		XmlContainer undefinedListContainer = new XmlContainer("list:generated");
		undefinedListContainer.add(new XmlValue("test", 42));
		List<XmlElement> undefinedListResult = XmlTypeProvider.INSTANCE.getList(undefinedListContainer);
		assertEquals(1, undefinedListResult.size());
		
		XmlContainer objectListContainer = new XmlContainer("list:generated");
		objectListContainer.add(new XmlContainer("element1"));
		objectListContainer.add(new XmlContainer("element2"));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getList(objectListContainer));
		
		XmlContainer objectContainer = new XmlContainer("map:generated");
		objectContainer.add(new XmlValue("key1", 1));
		objectContainer.add(new XmlValue("key2", 2));
		Map<String, XmlElement> mapResult = XmlTypeProvider.INSTANCE.getMap(objectContainer);
		assertEquals(2, mapResult.size());
		assertTrue(mapResult.containsKey("key1"));
		assertTrue(mapResult.containsKey("key2"));
		
		XmlContainer undefinedMapContainer = new XmlContainer("map:generated");
		undefinedMapContainer.add(new XmlValue("test", 42));
		Map<String, XmlElement> undefinedMapResult = XmlTypeProvider.INSTANCE.getMap(undefinedMapContainer);
		assertEquals(1, undefinedMapResult.size());
		assertTrue(undefinedMapResult.containsKey("test"));
		
		XmlContainer arrayMapContainer = new XmlContainer("map:generated");
		arrayMapContainer.add(new XmlValue("test", 1));
		arrayMapContainer.add(new XmlValue("test", 2));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.getMap(arrayMapContainer));
	}
	
	@Test
	void mapOperations() {
		XmlContainer container = new XmlContainer("test");
		XmlElement testValue = new XmlValue("test", 42);
		
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.has(null, "key"));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.has(container, null));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.get(null, "key"));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.get(container, null));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.set(null, "key", testValue));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.set(container, null, testValue));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.set(container, "key", null));
		
		XmlValue wrongType = new XmlValue("test", 42);
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.has(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.get(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.set(wrongType, "key", testValue));
		
		XmlContainer arrayContainer = new XmlContainer("test");
		arrayContainer.add(new XmlValue("test", 1));
		arrayContainer.add(new XmlValue("test", 2));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.get(arrayContainer, "key"));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.set(arrayContainer, "key", testValue));
		
		assertFalse(XmlTypeProvider.INSTANCE.has(container, "key"));
		
		XmlTypeProvider.INSTANCE.set(container, "key", testValue);
		assertTrue(XmlTypeProvider.INSTANCE.has(container, "key"));
		assertNotNull(XmlTypeProvider.INSTANCE.get(container, "key"));
		assertEquals(testValue.getAsXmlValue().getAsInteger(), XmlTypeProvider.INSTANCE.get(container, "key").getAsXmlValue().getAsInteger());
	}
	
	@Test
	void mergeOperations() {
		XmlElement emptyElement = new XmlElement("empty:generated");
		XmlElement nullElement = new XmlElement("null:generated");
		XmlValue value1 = new XmlValue("test", 1);
		XmlValue value2 = new XmlValue("test", 2);
		
		assertEquals(value1, XmlTypeProvider.INSTANCE.merge(null, value1));
		assertEquals(value1, XmlTypeProvider.INSTANCE.merge(value1, null));
		
		assertSame(value1, XmlTypeProvider.INSTANCE.merge(emptyElement, value1));
		
		assertSame(nullElement, XmlTypeProvider.INSTANCE.merge(emptyElement, nullElement));
		
		assertSame(value1, XmlTypeProvider.INSTANCE.merge(value1, nullElement));
		assertSame(value1, XmlTypeProvider.INSTANCE.merge(nullElement, value1));
		
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.merge(value1, value2));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.merge(value1, new XmlContainer("test")));
		
		XmlContainer array1 = new XmlContainer("test");
		array1.add(new XmlValue("element:generated", 1));
		array1.add(new XmlValue("element:generated", 2));
		
		XmlContainer array2 = new XmlContainer("test");
		array2.add(new XmlValue("element:generated", 3));
		array2.add(new XmlValue("element:generated", 4));
		
		assertEquals(array1, XmlTypeProvider.INSTANCE.merge(array1, nullElement));
		assertEquals(array1, XmlTypeProvider.INSTANCE.merge(nullElement, array1));
		
		XmlElement mergedArray = XmlTypeProvider.INSTANCE.merge(array1, array2);
		assertTrue(mergedArray.isXmlContainer());
		assertEquals(4, mergedArray.getAsXmlContainer().size());
		
		XmlContainer object1 = new XmlContainer("test");
		object1.add(new XmlValue("key1", 1));
		
		XmlContainer object2 = new XmlContainer("test");
		object2.add(new XmlValue("key2", 2));
		
		assertEquals(object1, XmlTypeProvider.INSTANCE.merge(object1, nullElement));
		assertEquals(object1, XmlTypeProvider.INSTANCE.merge(nullElement, object1));
		
		XmlElement mergedObject = XmlTypeProvider.INSTANCE.merge(object1, object2);
		assertTrue(mergedObject.isXmlContainer());
		assertEquals(2, mergedObject.getAsXmlContainer().size());
		
		XmlContainer undefined1 = new XmlContainer("test");
		undefined1.add(new XmlValue("test", 1));
		
		XmlContainer undefined2 = new XmlContainer("test");
		undefined2.add(new XmlValue("test", 2));
		
		assertDoesNotThrow(() -> XmlTypeProvider.INSTANCE.merge(undefined1, undefined2));
		
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.merge(array1, object1));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.merge(object1, array1));
		assertThrows(TypeProviderException.class, () -> XmlTypeProvider.INSTANCE.merge(value1, array1));
	}
	
	@Test
	void nameEscapingForNumericKeys() {
		XmlContainer container = new XmlContainer("test");
		XmlElement value = new XmlValue("test", 42);
		
		assertDoesNotThrow(() -> XmlTypeProvider.INSTANCE.set(container, "123", value));
		assertTrue(XmlTypeProvider.INSTANCE.has(container, "123"));
		assertNotNull(XmlTypeProvider.INSTANCE.get(container, "123"));
		
		assertDoesNotThrow(() -> XmlTypeProvider.INSTANCE.set(container, "abc", value));
		assertTrue(XmlTypeProvider.INSTANCE.has(container, "abc"));
		assertNotNull(XmlTypeProvider.INSTANCE.get(container, "abc"));
	}
}
