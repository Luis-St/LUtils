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

import net.luis.utils.io.data.toon.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonTypeProvider}.<br>
 *
 * @author Luis-St
 */
class ToonTypeProviderTest {
	
	@Test
	void emptyReturnsToonElement() {
		ToonElement element = ToonTypeProvider.INSTANCE.empty();
		assertFalse(element.isToonNull());
		assertFalse(element.isToonValue());
		assertFalse(element.isToonArray());
		assertFalse(element.isToonObject());
	}
	
	@Test
	void createNullReturnsToonNull() {
		assertEquals(ToonNull.INSTANCE, ToonTypeProvider.INSTANCE.createNull(RuntimeException::new));
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new ToonValue(true), ToonTypeProvider.INSTANCE.createBoolean(true, RuntimeException::new));
		assertEquals(new ToonValue((byte) 42), ToonTypeProvider.INSTANCE.createByte((byte) 42, RuntimeException::new));
		assertEquals(new ToonValue((short) 42), ToonTypeProvider.INSTANCE.createShort((short) 42, RuntimeException::new));
		assertEquals(new ToonValue(42), ToonTypeProvider.INSTANCE.createInteger(42, RuntimeException::new));
		assertEquals(new ToonValue(42L), ToonTypeProvider.INSTANCE.createLong(42L, RuntimeException::new));
		assertEquals(new ToonValue(42.5f), ToonTypeProvider.INSTANCE.createFloat(42.5f, RuntimeException::new));
		assertEquals(new ToonValue(42.5), ToonTypeProvider.INSTANCE.createDouble(42.5, RuntimeException::new));
		assertEquals(new ToonValue("test"), ToonTypeProvider.INSTANCE.createString("test", RuntimeException::new));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.createString(null, RuntimeException::new));
	}
	
	@Test
	void createCollectionTypes() {
		ToonElement element1 = new ToonValue("a");
		ToonElement element2 = new ToonValue("b");
		
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.createList(null, RuntimeException::new));
		
		ToonArray emptyArray = new ToonArray();
		assertEquals(emptyArray, ToonTypeProvider.INSTANCE.createList(List.of(), RuntimeException::new));
		
		ToonArray arrayWithElements = new ToonArray(List.of(element1, element2));
		assertEquals(arrayWithElements, ToonTypeProvider.INSTANCE.createList(List.of(element1, element2), RuntimeException::new));
		
		ToonObject emptyObject = new ToonObject();
		assertEquals(emptyObject, ToonTypeProvider.INSTANCE.createMap(RuntimeException::new));
		assertEquals(emptyObject, ToonTypeProvider.INSTANCE.createMap(Map.of(), RuntimeException::new));
		
		ToonObject objectWithElements = new ToonObject(Map.of("key1", element1, "key2", element2));
		assertEquals(objectWithElements, ToonTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2), RuntimeException::new));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.isEmpty(null, RuntimeException::new));
		assertFalse(ToonTypeProvider.INSTANCE.isEmpty(new ToonArray(), RuntimeException::new));
		assertFalse(ToonTypeProvider.INSTANCE.isEmpty(new ToonValue(1), RuntimeException::new));
		assertFalse(ToonTypeProvider.INSTANCE.isEmpty(new ToonObject(), RuntimeException::new));
		assertFalse(ToonTypeProvider.INSTANCE.isEmpty(ToonNull.INSTANCE, RuntimeException::new));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.isNull(null, RuntimeException::new));
		
		assertTrue(ToonTypeProvider.INSTANCE.isNull(ToonNull.INSTANCE, RuntimeException::new));
		
		assertFalse(ToonTypeProvider.INSTANCE.isNull(new ToonArray(), RuntimeException::new));
		assertFalse(ToonTypeProvider.INSTANCE.isNull(new ToonObject(), RuntimeException::new));
		assertFalse(ToonTypeProvider.INSTANCE.isNull(new ToonValue(1), RuntimeException::new));
		assertFalse(ToonTypeProvider.INSTANCE.isNull(new ToonValue(true), RuntimeException::new));
		assertFalse(ToonTypeProvider.INSTANCE.isNull(new ToonValue("test"), RuntimeException::new));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getBoolean(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getByte(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getShort(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getInteger(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getLong(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getFloat(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getDouble(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getString(null, RuntimeException::new));
		
		ToonArray wrongType = new ToonArray();
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getBoolean(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getByte(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getShort(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getInteger(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getLong(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getFloat(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getDouble(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getString(wrongType, RuntimeException::new));
		
		ToonValue invalidValue = new ToonValue("not-a-number");
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getBoolean(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getByte(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getShort(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getInteger(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getLong(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getFloat(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getDouble(invalidValue, RuntimeException::new));
		
		assertTrue(ToonTypeProvider.INSTANCE.getBoolean(new ToonValue(true), RuntimeException::new));
		assertEquals((byte) 42, ToonTypeProvider.INSTANCE.getByte(new ToonValue((byte) 42), RuntimeException::new));
		assertEquals((short) 42, ToonTypeProvider.INSTANCE.getShort(new ToonValue((short) 42), RuntimeException::new));
		assertEquals(42, ToonTypeProvider.INSTANCE.getInteger(new ToonValue(42), RuntimeException::new));
		assertEquals(42L, ToonTypeProvider.INSTANCE.getLong(new ToonValue(42L), RuntimeException::new));
		assertEquals(42.5f, ToonTypeProvider.INSTANCE.getFloat(new ToonValue(42.5f), RuntimeException::new));
		assertEquals(42.5, ToonTypeProvider.INSTANCE.getDouble(new ToonValue(42.5), RuntimeException::new));
		assertEquals("test", ToonTypeProvider.INSTANCE.getString(new ToonValue("test"), RuntimeException::new));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getList(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getMap(null, RuntimeException::new));
		
		ToonValue wrongType = new ToonValue(1);
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getList(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.getMap(wrongType, RuntimeException::new));
		
		ToonArray emptyArray = new ToonArray();
		assertTrue(ToonTypeProvider.INSTANCE.getList(emptyArray, RuntimeException::new).isEmpty());
		
		ToonArray arrayWithElements = new ToonArray(List.of(new ToonValue("a"), new ToonValue("b")));
		List<ToonElement> listResult = ToonTypeProvider.INSTANCE.getList(arrayWithElements, RuntimeException::new);
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsToonValue().getAsString());
		
		ToonObject emptyObject = new ToonObject();
		assertTrue(ToonTypeProvider.INSTANCE.getMap(emptyObject, RuntimeException::new).isEmpty());
		
		ToonObject objectWithElements = new ToonObject(Map.of("key", new ToonValue("value")));
		Map<String, ToonElement> mapResult = ToonTypeProvider.INSTANCE.getMap(objectWithElements, RuntimeException::new);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsToonValue().getAsString());
	}
	
	@Test
	void mapOperations() {
		ToonObject toonObject = new ToonObject();
		ToonElement testValue = new ToonValue("test");
		
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.has(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.has(toonObject, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.get(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.get(toonObject, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.set(null, "key", testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.set(toonObject, null, testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.set(toonObject, "key", null, RuntimeException::new));
		
		ToonArray wrongType = new ToonArray();
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.has(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.get(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.set(wrongType, "key", testValue, RuntimeException::new));
		
		assertFalse(ToonTypeProvider.INSTANCE.has(toonObject, "key", RuntimeException::new));
		
		ToonTypeProvider.INSTANCE.set(toonObject, "key", testValue, RuntimeException::new);
		assertTrue(ToonTypeProvider.INSTANCE.has(toonObject, "key", RuntimeException::new));
		assertEquals(testValue, ToonTypeProvider.INSTANCE.get(toonObject, "key", RuntimeException::new));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(ToonNull.INSTANCE, ToonTypeProvider.INSTANCE.merge(null, ToonNull.INSTANCE, RuntimeException::new));
		assertEquals(ToonNull.INSTANCE, ToonTypeProvider.INSTANCE.merge(ToonNull.INSTANCE, null, RuntimeException::new));
		
		ToonElement primitive = new ToonValue(1);
		ToonArray array1 = new ToonArray(List.of(new ToonValue("a")));
		ToonArray array2 = new ToonArray(List.of(new ToonValue("b")));
		ToonObject object1 = new ToonObject(Map.of("key1", new ToonValue("value1")));
		ToonObject object2 = new ToonObject(Map.of("key2", new ToonValue("value2")));
		
		assertEquals(primitive, ToonTypeProvider.INSTANCE.merge(ToonTypeProvider.INSTANCE.empty(), primitive, RuntimeException::new));
		assertEquals(array1, ToonTypeProvider.INSTANCE.merge(ToonTypeProvider.INSTANCE.empty(), array1, RuntimeException::new));
		assertEquals(object1, ToonTypeProvider.INSTANCE.merge(ToonTypeProvider.INSTANCE.empty(), object1, RuntimeException::new));
		
		assertEquals(ToonNull.INSTANCE, ToonTypeProvider.INSTANCE.merge(ToonTypeProvider.INSTANCE.empty(), ToonNull.INSTANCE, RuntimeException::new));
		
		assertEquals(primitive, ToonTypeProvider.INSTANCE.merge(primitive, ToonNull.INSTANCE, RuntimeException::new));
		assertEquals(primitive, ToonTypeProvider.INSTANCE.merge(ToonNull.INSTANCE, primitive, RuntimeException::new));
		
		assertEquals(array1, ToonTypeProvider.INSTANCE.merge(array1, ToonNull.INSTANCE, RuntimeException::new));
		assertEquals(array1, ToonTypeProvider.INSTANCE.merge(ToonNull.INSTANCE, array1, RuntimeException::new));
		
		assertEquals(object1, ToonTypeProvider.INSTANCE.merge(object1, ToonNull.INSTANCE, RuntimeException::new));
		assertEquals(object1, ToonTypeProvider.INSTANCE.merge(ToonNull.INSTANCE, object1, RuntimeException::new));
		
		ToonArray mergedArray = ToonTypeProvider.INSTANCE.merge(array1, array2, RuntimeException::new).getAsToonArray();
		assertEquals(2, mergedArray.size());
		assertEquals("a", mergedArray.get(0).getAsToonValue().getAsString());
		assertEquals("b", mergedArray.get(1).getAsToonValue().getAsString());
		
		ToonObject mergedObject = ToonTypeProvider.INSTANCE.merge(object1, object2, RuntimeException::new).getAsToonObject();
		assertEquals(2, mergedObject.size());
		assertEquals("value1", mergedObject.get("key1").getAsToonValue().getAsString());
		assertEquals("value2", mergedObject.get("key2").getAsToonValue().getAsString());
		
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.merge(primitive, array1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.merge(primitive, object1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.merge(array1, primitive, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.merge(array1, object1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.merge(object1, primitive, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> ToonTypeProvider.INSTANCE.merge(object1, array1, RuntimeException::new));
	}
}
