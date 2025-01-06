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
import net.luis.utils.util.Result;
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
	void useRoot() {
		assertNotNull(XmlTypeProvider.INSTANCE.useRoot());
	}
	
	@Test
	void empty() {
		XmlElement element = XmlTypeProvider.INSTANCE.empty();
		assertTrue(element.isSelfClosing());
		assertEquals("empty:generated", element.getName());
	}
	
	@Test
	void createBoolean() {
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.createBoolean(true);
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlValue());
		assertEquals("boolean:generated", element.getName());
		assertTrue(element.getAsXmlValue().getAsBoolean());
	}
	
	@Test
	void createByte() {
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.createByte((byte) 42);
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlValue());
		assertEquals("byte:generated", element.getName());
		assertEquals(42, element.getAsXmlValue().getAsByte());
	}
	
	@Test
	void createShort() {
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.createShort((short) 42);
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlValue());
		assertEquals("short:generated", element.getName());
		assertEquals(42, element.getAsXmlValue().getAsShort());
	}
	
	@Test
	void createInteger() {
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.createInteger(42);
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlValue());
		assertEquals("integer:generated", element.getName());
		assertEquals(42, element.getAsXmlValue().getAsInteger());
	}
	
	@Test
	void createLong() {
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.createLong(42L);
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlValue());
		assertEquals("long:generated", element.getName());
		assertEquals(42L, element.getAsXmlValue().getAsLong());
	}
	
	@Test
	void createFloat() {
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.createFloat(42.0f);
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlValue());
		assertEquals("float:generated", element.getName());
		assertEquals(42.0f, element.getAsXmlValue().getAsFloat());
	}
	
	@Test
	void createDouble() {
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.createDouble(42.0);
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlValue());
		assertEquals("double:generated", element.getName());
		assertEquals(42.0, element.getAsXmlValue().getAsDouble());
	}
	
	@Test
	void createString() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.createString(null));
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.createString("42");
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlValue());
		assertEquals("string:generated", element.getName());
		assertEquals("42", element.getAsXmlValue().getAsString());
	}
	
	@Test
	void createList() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.createList(null));
		
		Result<XmlElement> emptyResult = XmlTypeProvider.INSTANCE.createList(List.of());
		assertTrue(emptyResult.isSuccess());
		XmlElement emptyElement = emptyResult.orThrow();
		assertTrue(emptyElement.isXmlContainer());
		assertEquals("list:generated", emptyElement.getName());
		assertTrue(emptyElement.getAsXmlContainer().isEmpty());
		
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.createList(List.of(new XmlValue("test", 42)));
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlContainer());
		assertEquals("list:generated", element.getName());
		assertEquals(1, element.getAsXmlContainer().size());
		assertEquals("element:generated", element.getAsXmlContainer().get(0).getName());
	}
	
	@Test
	void createMap() {
		Result<XmlElement> emptyResult = XmlTypeProvider.INSTANCE.createMap();
		assertTrue(emptyResult.isSuccess());
		XmlElement emptyElement = emptyResult.orThrow();
		assertTrue(emptyElement.isXmlContainer());
		assertEquals("map:generated", emptyElement.getName());
		
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.createMap(Map.of("test", new XmlValue("test", 42)));
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlContainer());
		assertEquals("map:generated", element.getName());
		assertEquals(1, element.getAsXmlContainer().size());
		assertEquals("test", element.getAsXmlContainer().get(0).getName());
	}
	
	@Test
	void createMapUseRoot() {
		XmlTypeProvider typeProvider = XmlTypeProvider.INSTANCE.useRoot();
		Result<XmlElement> emptyResult = typeProvider.createMap();
		assertTrue(emptyResult.isSuccess());
		XmlElement emptyElement = emptyResult.orThrow();
		assertTrue(emptyElement.isXmlContainer());
		assertEquals("root:generated", emptyElement.getName());
		
		Result<XmlElement> result = typeProvider.createMap(Map.of("test", new XmlValue("test", 42)));
		assertTrue(result.isSuccess());
		XmlElement element = result.orThrow();
		assertTrue(element.isXmlContainer());
		assertEquals("root:generated", element.getName());
		assertEquals(1, element.getAsXmlContainer().size());
		assertEquals("test", element.getAsXmlContainer().get(0).getName());
	}
	
	@Test
	void getEmpty() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getEmpty(null));
		
		Result<XmlElement> notSelfClosingResult = XmlTypeProvider.INSTANCE.getEmpty(new XmlValue("empty:generated", 42));
		assertTrue(notSelfClosingResult.isError());
		
		Result<XmlElement> selfClosingResult = XmlTypeProvider.INSTANCE.getEmpty(new XmlElement("empty:generated"));
		assertTrue(selfClosingResult.isSuccess());
	}
	
	@Test
	void getBoolean() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getBoolean(null));
		
		Result<Boolean> invalidTypeResult = XmlTypeProvider.INSTANCE.getBoolean(new XmlContainer("test"));
		assertTrue(invalidTypeResult.isError());
		
		Result<Boolean> notBooleanResult = XmlTypeProvider.INSTANCE.getBoolean(new XmlValue("boolean:generated", "test"));
		assertTrue(notBooleanResult.isError());
		
		Result<Boolean> result = XmlTypeProvider.INSTANCE.getBoolean(new XmlValue("boolean:generated", true));
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow());
	}
	
	@Test
	void getByte() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getByte(null));
		
		Result<Byte> invalidTypeResult = XmlTypeProvider.INSTANCE.getByte(new XmlContainer("test"));
		assertTrue(invalidTypeResult.isError());
		
		Result<Byte> notByteResult = XmlTypeProvider.INSTANCE.getByte(new XmlValue("byte:generated", "test"));
		assertTrue(notByteResult.isError());
		
		Result<Byte> result = XmlTypeProvider.INSTANCE.getByte(new XmlValue("byte:generated", (byte) 42));
		assertTrue(result.isSuccess());
		assertEquals((byte) 42, result.orThrow());
	}
	
	@Test
	void getShort() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getShort(null));
		
		Result<Short> invalidTypeResult = XmlTypeProvider.INSTANCE.getShort(new XmlContainer("test"));
		assertTrue(invalidTypeResult.isError());
		
		Result<Short> notShortResult = XmlTypeProvider.INSTANCE.getShort(new XmlValue("short:generated", "test"));
		assertTrue(notShortResult.isError());
		
		Result<Short> result = XmlTypeProvider.INSTANCE.getShort(new XmlValue("short:generated", (short) 42));
		assertTrue(result.isSuccess());
		assertEquals((short) 42, result.orThrow());
	}
	
	@Test
	void getInteger() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getInteger(null));
		
		Result<Integer> invalidTypeResult = XmlTypeProvider.INSTANCE.getInteger(new XmlContainer("test"));
		assertTrue(invalidTypeResult.isError());
		
		Result<Integer> notIntegerResult = XmlTypeProvider.INSTANCE.getInteger(new XmlValue("integer:generated", "test"));
		assertTrue(notIntegerResult.isError());
		
		Result<Integer> result = XmlTypeProvider.INSTANCE.getInteger(new XmlValue("integer:generated", 42));
		assertTrue(result.isSuccess());
		assertEquals(42, result.orThrow());
	}
	
	@Test
	void getLong() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getLong(null));
		
		Result<Long> invalidTypeResult = XmlTypeProvider.INSTANCE.getLong(new XmlContainer("test"));
		assertTrue(invalidTypeResult.isError());
		
		Result<Long> notLongResult = XmlTypeProvider.INSTANCE.getLong(new XmlValue("long:generated", "test"));
		assertTrue(notLongResult.isError());
		
		Result<Long> result = XmlTypeProvider.INSTANCE.getLong(new XmlValue("long:generated", 42L));
		assertTrue(result.isSuccess());
		assertEquals(42L, result.orThrow());
	}
	
	@Test
	void getFloat() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getFloat(null));
		
		Result<Float> invalidTypeResult = XmlTypeProvider.INSTANCE.getFloat(new XmlContainer("test"));
		assertTrue(invalidTypeResult.isError());
		
		Result<Float> notFloatResult = XmlTypeProvider.INSTANCE.getFloat(new XmlValue("float:generated", "test"));
		assertTrue(notFloatResult.isError());
		
		Result<Float> result = XmlTypeProvider.INSTANCE.getFloat(new XmlValue("float:generated", 42.0f));
		assertTrue(result.isSuccess());
		assertEquals(42.0f, result.orThrow());
	}
	
	@Test
	void getDouble() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getDouble(null));
		
		Result<Double> invalidTypeResult = XmlTypeProvider.INSTANCE.getDouble(new XmlContainer("test"));
		assertTrue(invalidTypeResult.isError());
		
		Result<Double> notDoubleResult = XmlTypeProvider.INSTANCE.getDouble(new XmlValue("double:generated", "test"));
		assertTrue(notDoubleResult.isError());
		
		Result<Double> result = XmlTypeProvider.INSTANCE.getDouble(new XmlValue("double:generated", 42.0));
		assertTrue(result.isSuccess());
		assertEquals(42.0, result.orThrow());
	}
	
	@Test
	void getString() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getString(null));
		
		Result<String> invalidTypeResult = XmlTypeProvider.INSTANCE.getString(new XmlContainer("test"));
		assertTrue(invalidTypeResult.isError());
		
		Result<String> result = XmlTypeProvider.INSTANCE.getString(new XmlValue("string:generated", "42"));
		assertTrue(result.isSuccess());
		assertEquals("42", result.orThrow());
	}
	
	@Test
	void getList() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getList(null));
		
		Result<List<XmlElement>> invalidTypeResult = XmlTypeProvider.INSTANCE.getList(new XmlValue("test", 42));
		assertTrue(invalidTypeResult.isError());
		
		Result<List<XmlElement>> emptyResult = XmlTypeProvider.INSTANCE.getList(new XmlContainer("list:generated"));
		assertTrue(emptyResult.isSuccess());
		assertTrue(emptyResult.orThrow().isEmpty());
		
		XmlContainer arrayContainer = new XmlContainer("list:generated");
		arrayContainer.add(new XmlValue("element:generated", 42));
		arrayContainer.add(new XmlValue("element:generated", 42));
		Result<List<XmlElement>> arrayResult = XmlTypeProvider.INSTANCE.getList(arrayContainer);
		assertTrue(arrayResult.isSuccess());
		assertEquals(2, arrayResult.orThrow().size());
		
		XmlContainer undefinedContainer = new XmlContainer("list:generated");
		undefinedContainer.add(new XmlValue("test", 42));
		Result<List<XmlElement>> undefinedResult = XmlTypeProvider.INSTANCE.getList(undefinedContainer);
		assertTrue(undefinedResult.isSuccess());
		assertEquals(1, undefinedResult.orThrow().size());
		
		XmlContainer objectContainer = new XmlContainer("list:generated");
		objectContainer.add(new XmlContainer("element1:generated"));
		objectContainer.add(new XmlContainer("element2:generated"));
		Result<List<XmlElement>> objectResult = XmlTypeProvider.INSTANCE.getList(objectContainer);
		assertTrue(objectResult.isError());
	}
	
	@Test
	void getMap() {
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.getMap(null));
		
		Result<Map<String, XmlElement>> invalidTypeResult = XmlTypeProvider.INSTANCE.getMap(new XmlValue("test", 42));
		assertTrue(invalidTypeResult.isError());
		
		Result<Map<String, XmlElement>> emptyResult = XmlTypeProvider.INSTANCE.getMap(new XmlContainer("map:generated"));
		assertTrue(emptyResult.isSuccess());
		assertTrue(emptyResult.orThrow().isEmpty());
		
		XmlContainer objectContainer = new XmlContainer("map:generated");
		objectContainer.add(new XmlValue("test1", 42));
		objectContainer.add(new XmlValue("test2", 42));
		Result<Map<String, XmlElement>> objectResult = XmlTypeProvider.INSTANCE.getMap(objectContainer);
		assertTrue(objectResult.isSuccess());
		assertEquals(2, objectResult.orThrow().size());
		
		XmlContainer undefinedContainer = new XmlContainer("map:generated");
		undefinedContainer.add(new XmlValue("test", 42));
		Result<Map<String, XmlElement>> undefinedResult = XmlTypeProvider.INSTANCE.getMap(undefinedContainer);
		assertTrue(undefinedResult.isSuccess());
		assertEquals(1, undefinedResult.orThrow().size());
		
		XmlContainer arrayContainer = new XmlContainer("map:generated");
		arrayContainer.add(new XmlValue("test", 42));
		arrayContainer.add(new XmlValue("test", 42));
		Result<Map<String, XmlElement>> arrayResult = XmlTypeProvider.INSTANCE.getMap(arrayContainer);
		assertTrue(arrayResult.isError());
	}
	
	@Test
	void has() {
		XmlContainer map = new XmlContainer("test");
		map.add(new XmlValue("test", 42));
		
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.has(null, "test"));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.has(map, null));
		
		Result<Boolean> invalidTypeResult = XmlTypeProvider.INSTANCE.has(new XmlValue("test", 42), "test");
		assertTrue(invalidTypeResult.isError());
		
		XmlContainer arrayContainer = new XmlContainer("test");
		arrayContainer.add(new XmlValue("test", 42));
		arrayContainer.add(new XmlValue("test", 42));
		Result<Boolean> arrayResult = XmlTypeProvider.INSTANCE.has(arrayContainer, "test");
		assertTrue(arrayResult.isSuccess());
		assertTrue(arrayResult.orThrow());
		
		Result<Boolean> notFoundResult = XmlTypeProvider.INSTANCE.has(map, "notFound");
		assertTrue(notFoundResult.isSuccess());
		assertFalse(notFoundResult.orThrow());
		
		Result<Boolean> foundResult = XmlTypeProvider.INSTANCE.has(map, "test");
		assertTrue(foundResult.isSuccess());
		assertTrue(foundResult.orThrow());
	}
	
	@Test
	void get() {
		XmlContainer map = new XmlContainer("test");
		map.add(new XmlValue("test", 42));
		
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.get(null, "test"));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.get(map, null));
		
		Result<XmlElement> invalidTypeResult = XmlTypeProvider.INSTANCE.get(new XmlValue("test", 42), "test");
		assertTrue(invalidTypeResult.isError());
		
		XmlContainer arrayContainer = new XmlContainer("test");
		arrayContainer.add(new XmlValue("test", 42));
		arrayContainer.add(new XmlValue("test", 42));
		Result<XmlElement> arrayResult = XmlTypeProvider.INSTANCE.get(arrayContainer, "test");
		assertTrue(arrayResult.isError());
		
		Result<XmlElement> notFoundResult = XmlTypeProvider.INSTANCE.get(map, "notFound");
		assertTrue(notFoundResult.isSuccess());
		assertNull(notFoundResult.orThrow());
		
		Result<XmlElement> foundResult = XmlTypeProvider.INSTANCE.get(map, "test");
		assertTrue(foundResult.isSuccess());
		assertEquals(new XmlValue("test", 42), foundResult.orThrow());
	}
	
	@Test
	void set() {
		XmlContainer map = new XmlContainer("test");
		
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(null, "test", new XmlValue("test", 42)));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(map, null, new XmlValue("test", 42)));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(map, "test", (XmlElement) null));
		
		Result<XmlElement> invalidTypeResult = XmlTypeProvider.INSTANCE.set(new XmlValue("test", 42), "test", new XmlValue("test", 42));
		assertTrue(invalidTypeResult.isError());
		
		XmlContainer arrayContainer = new XmlContainer("test");
		arrayContainer.add(new XmlValue("test", 42));
		arrayContainer.add(new XmlValue("test", 42));
		Result<XmlElement> arrayResult = XmlTypeProvider.INSTANCE.set(arrayContainer, "test", new XmlValue("test", 42));
		assertTrue(arrayResult.isError());
		
		Result<XmlElement> result = XmlTypeProvider.INSTANCE.set(map, "test1", new XmlValue("test", 42));
		assertTrue(result.isSuccess());
		assertSame(map, result.orThrow());
		assertEquals(new XmlValue("test1", 42), map.get(0));
		
		
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(null, "test2", Result.success(new XmlValue("test", 42))));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(map, null, Result.success(new XmlValue("test", 42))));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.set(map, "test2", (Result<XmlElement>) null));
		
		Result<XmlElement> errorResult = XmlTypeProvider.INSTANCE.set(map, "test2", Result.error("test"));
		assertTrue(errorResult.isError());
		
		Result<XmlElement> successResult = XmlTypeProvider.INSTANCE.set(map, "test2", Result.success(new XmlValue("test", 42)));
		assertTrue(successResult.isSuccess());
		assertSame(map, result.orThrow());
		assertEquals(new XmlValue("test2", 42), map.get("test2"));
	}
	
	@Test
	void merge() {
		XmlElement emptyElement = new XmlElement("empty:generated");
		
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.merge(null, new XmlValue("test", 42)));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.merge(new XmlValue("test", 42), (XmlElement) null));
		
		// Empty base
		XmlValue value = new XmlValue("test", 42);
		Result<XmlElement> emptyValueResult = XmlTypeProvider.INSTANCE.merge(emptyElement, value);
		assertTrue(emptyValueResult.isSuccess());
		assertSame(value, emptyValueResult.orThrow());
		
		XmlContainer arrayContainer = new XmlContainer("test");
		arrayContainer.add(new XmlValue("test", 42));
		arrayContainer.add(new XmlValue("test", 42));
		Result<XmlElement> emptyArrayResult = XmlTypeProvider.INSTANCE.merge(emptyElement, arrayContainer);
		assertTrue(emptyArrayResult.isSuccess());
		assertSame(arrayContainer, emptyArrayResult.orThrow());
		
		XmlContainer objectContainer = new XmlContainer("test");
		objectContainer.add(new XmlValue("test1", 42));
		Result<XmlElement> emptyObjectResult = XmlTypeProvider.INSTANCE.merge(emptyElement, objectContainer);
		assertTrue(emptyObjectResult.isSuccess());
		assertSame(objectContainer, emptyObjectResult.orThrow());
		
		// Value base
		XmlValue baseValue = new XmlValue("test", 42);
		Result<XmlElement> valueValueResult = XmlTypeProvider.INSTANCE.merge(baseValue, value);
		assertTrue(valueValueResult.isError());
		
		Result<XmlElement> valueArrayResult = XmlTypeProvider.INSTANCE.merge(baseValue, arrayContainer);
		assertTrue(valueArrayResult.isError());
		
		Result<XmlElement> valueObjectResult = XmlTypeProvider.INSTANCE.merge(baseValue, objectContainer);
		assertTrue(valueObjectResult.isError());
		
		// Array base
		Result<XmlElement> arrayValueResult = XmlTypeProvider.INSTANCE.merge(arrayContainer, value);
		assertTrue(arrayValueResult.isError());
		
		XmlContainer arrayContainer2 = new XmlContainer("test");
		arrayContainer2.add(new XmlValue("test", 42));
		arrayContainer2.add(new XmlValue("test", 42));
		Result<XmlElement> arrayArrayResult = XmlTypeProvider.INSTANCE.merge(arrayContainer, arrayContainer2);
		assertTrue(arrayArrayResult.isSuccess());
		assertEquals(4, arrayArrayResult.orThrow().getAsXmlContainer().size());
		
		Result<XmlElement> arrayObjectResult = XmlTypeProvider.INSTANCE.merge(arrayContainer, objectContainer);
		assertTrue(arrayObjectResult.isError());
		
		// Object base
		Result<XmlElement> objectValueResult = XmlTypeProvider.INSTANCE.merge(objectContainer, value);
		assertTrue(objectValueResult.isError());
		
		Result<XmlElement> objectArrayResult = XmlTypeProvider.INSTANCE.merge(objectContainer, arrayContainer);
		assertTrue(objectArrayResult.isError());
		
		XmlContainer objectContainer2 = new XmlContainer("test");
		objectContainer2.add(new XmlValue("test2", 42));
		Result<XmlElement> objectObjectResult = XmlTypeProvider.INSTANCE.merge(objectContainer, objectContainer2);
		assertTrue(objectObjectResult.isSuccess());
		assertEquals(2, objectObjectResult.orThrow().getAsXmlContainer().size());
		
		
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.merge(null, Result.success(value)));
		assertThrows(NullPointerException.class, () -> XmlTypeProvider.INSTANCE.merge(new XmlElement("test"), (Result<XmlElement>) null));
		
		Result<XmlElement> errorResult = XmlTypeProvider.INSTANCE.merge(new XmlElement("test"), Result.error("test"));
		assertTrue(errorResult.isError());
		
		Result<XmlElement> successResult = XmlTypeProvider.INSTANCE.merge(new XmlElement("test"), Result.success(value));
		assertTrue(successResult.isSuccess());
		assertSame(value, successResult.orThrow());
	}
}
