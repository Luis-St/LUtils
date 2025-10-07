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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JavaTypeProvider}.<br>
 *
 * @author Luis-St
 */
class JavaTypeProviderTest {
	
	@Test
	void emptyReturnsPlainObject() {
		Object empty = JavaTypeProvider.INSTANCE.empty();
		assertNotNull(empty);
		assertEquals(Object.class, empty.getClass());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(true, JavaTypeProvider.INSTANCE.createBoolean(true).resultOrThrow());
		assertEquals((byte) 42, JavaTypeProvider.INSTANCE.createByte((byte) 42).resultOrThrow());
		assertEquals((short) 42, JavaTypeProvider.INSTANCE.createShort((short) 42).resultOrThrow());
		assertEquals(42, JavaTypeProvider.INSTANCE.createInteger(42).resultOrThrow());
		assertEquals(42L, JavaTypeProvider.INSTANCE.createLong(42L).resultOrThrow());
		assertEquals(42.5f, JavaTypeProvider.INSTANCE.createFloat(42.5f).resultOrThrow());
		assertEquals(42.5, JavaTypeProvider.INSTANCE.createDouble(42.5).resultOrThrow());
		assertEquals("test", JavaTypeProvider.INSTANCE.createString("test").resultOrThrow());
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.createString(null));
	}
	
	@Test
	void createCollectionTypes() {
		Object element1 = "a";
		Object element2 = "b";
		
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.createList(null));
		
		List<?> emptyList = (List<?>) JavaTypeProvider.INSTANCE.createList(List.of()).resultOrThrow();
		assertTrue(emptyList.isEmpty());
		
		List<?> listWithElements = (List<?>) JavaTypeProvider.INSTANCE.createList(List.of(element1, element2)).resultOrThrow();
		assertEquals(2, listWithElements.size());
		assertEquals(element1, listWithElements.get(0));
		assertEquals(element2, listWithElements.get(1));
		
		Map<?, ?> emptyMap = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap().resultOrThrow();
		assertTrue(emptyMap.isEmpty());
		
		Map<?, ?> emptyMapFromValues = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap(Map.of()).resultOrThrow();
		assertTrue(emptyMapFromValues.isEmpty());
		
		Map<?, ?> mapWithElements = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2)).resultOrThrow();
		assertEquals(2, mapWithElements.size());
		assertEquals(element1, mapWithElements.get("key1"));
		assertEquals(element2, mapWithElements.get("key2"));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getEmpty(null));
		
		assertTrue(JavaTypeProvider.INSTANCE.getEmpty(List.of()).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getEmpty(1).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getEmpty(Map.of()).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getEmpty("test").isError());
		
		Object emptyObject = new Object();
		assertEquals(emptyObject, JavaTypeProvider.INSTANCE.getEmpty(emptyObject).resultOrThrow());
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getBoolean(null));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getByte(null));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getShort(null));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getInteger(null));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getLong(null));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getFloat(null));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getDouble(null));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getString(null));
		
		List<?> wrongType = List.of();
		assertTrue(JavaTypeProvider.INSTANCE.getBoolean(wrongType).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getByte(wrongType).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getShort(wrongType).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getInteger(wrongType).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getLong(wrongType).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getFloat(wrongType).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getDouble(wrongType).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getString(wrongType).isError());
		
		String invalidValue = "not-a-number";
		assertTrue(JavaTypeProvider.INSTANCE.getBoolean(invalidValue).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getByte(invalidValue).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getShort(invalidValue).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getInteger(invalidValue).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getLong(invalidValue).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getFloat(invalidValue).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getDouble(invalidValue).isError());
		
		assertTrue(JavaTypeProvider.INSTANCE.getBoolean(true).resultOrThrow());
		assertEquals((byte) 42, JavaTypeProvider.INSTANCE.getByte((byte) 42).resultOrThrow());
		assertEquals((short) 42, JavaTypeProvider.INSTANCE.getShort((short) 42).resultOrThrow());
		assertEquals(42, JavaTypeProvider.INSTANCE.getInteger(42).resultOrThrow());
		assertEquals(42L, JavaTypeProvider.INSTANCE.getLong(42L).resultOrThrow());
		assertEquals(42.5f, JavaTypeProvider.INSTANCE.getFloat(42.5f).resultOrThrow());
		assertEquals(42.5, JavaTypeProvider.INSTANCE.getDouble(42.5).resultOrThrow());
		assertEquals("test", JavaTypeProvider.INSTANCE.getString("test").resultOrThrow());
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getList(null));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.getMap(null));
		
		Integer wrongType = 1;
		assertTrue(JavaTypeProvider.INSTANCE.getList(wrongType).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getMap(wrongType).isError());
		
		List<Object> emptyList = List.of();
		assertTrue(JavaTypeProvider.INSTANCE.getList(emptyList).resultOrThrow().isEmpty());
		
		List<Object> listWithElements = List.of("a", "b");
		List<Object> listResult = JavaTypeProvider.INSTANCE.getList(listWithElements).resultOrThrow();
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.get(0));
		assertEquals("b", listResult.get(1));
		
		Map<String, Object> emptyMap = Map.of();
		assertTrue(JavaTypeProvider.INSTANCE.getMap(emptyMap).resultOrThrow().isEmpty());
		
		Map<String, Object> mapWithElements = Map.of("key", "value");
		Map<String, Object> mapResult = JavaTypeProvider.INSTANCE.getMap(mapWithElements).resultOrThrow();
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void mapOperations() {
		Map<String, Object> map = (Map<String, Object>) JavaTypeProvider.INSTANCE.createMap().resultOrThrow();
		Object testValue = "test";
		
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.has(null, "key"));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.has(map, null));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.get(null, "key"));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.get(map, null));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.set(null, "key", testValue));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.set(map, null, testValue));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.set(map, "key", (Object) null));
		
		List<?> wrongType = List.of();
		assertTrue(JavaTypeProvider.INSTANCE.has(wrongType, "key").isError());
		assertTrue(JavaTypeProvider.INSTANCE.get(wrongType, "key").isError());
		assertTrue(JavaTypeProvider.INSTANCE.set(wrongType, "key", testValue).isError());
		
		assertFalse(JavaTypeProvider.INSTANCE.has(map, "key").resultOrThrow());
		assertNull(JavaTypeProvider.INSTANCE.get(map, "key").resultOrThrow());
		
		assertNull(JavaTypeProvider.INSTANCE.set(map, "key", testValue).resultOrThrow());
		assertTrue(JavaTypeProvider.INSTANCE.has(map, "key").resultOrThrow());
		assertEquals(testValue, JavaTypeProvider.INSTANCE.get(map, "key").resultOrThrow());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void mergeOperations() {
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.merge(null, new Object()));
		assertThrows(NullPointerException.class, () -> JavaTypeProvider.INSTANCE.merge(new Object(), null));
		
		Object emptyObject = new Object();
		Integer primitive = 1;
		List<Object> list1 = List.of("a");
		List<Object> list2 = List.of("b");
		Map<String, Object> map1 = Map.of("key1", "value1");
		Map<String, Object> map2 = Map.of("key2", "value2");
		
		assertEquals(primitive, JavaTypeProvider.INSTANCE.merge(emptyObject, primitive).resultOrThrow());
		assertEquals(list1, JavaTypeProvider.INSTANCE.merge(emptyObject, list1).resultOrThrow());
		assertEquals(map1, JavaTypeProvider.INSTANCE.merge(emptyObject, map1).resultOrThrow());
		
		List<Object> mutableList1 = (List<Object>) JavaTypeProvider.INSTANCE.createList(Lists.newArrayList("a")).resultOrThrow();
		Object mergedListResult = JavaTypeProvider.INSTANCE.merge(mutableList1, list2).resultOrThrow();
		List<Object> mergedList = (List<Object>) mergedListResult;
		assertEquals(2, mergedList.size());
		assertEquals("a", mergedList.get(0));
		assertEquals("b", mergedList.get(1));
		
		Map<String, Object> mutableMap1 = (Map<String, Object>) JavaTypeProvider.INSTANCE.createMap(Maps.newHashMap(Map.of("key1", "value1"))).resultOrThrow();
		Object mergedMapResult = JavaTypeProvider.INSTANCE.merge(mutableMap1, map2).resultOrThrow();
		Map<String, Object> mergedMap = (Map<String, Object>) mergedMapResult;
		assertEquals(2, mergedMap.size());
		assertEquals("value1", mergedMap.get("key1"));
		assertEquals("value2", mergedMap.get("key2"));
		
		assertTrue(JavaTypeProvider.INSTANCE.merge(primitive, list1).isError());
		assertTrue(JavaTypeProvider.INSTANCE.merge(primitive, map1).isError());
		assertTrue(JavaTypeProvider.INSTANCE.merge(list1, primitive).isError());
		assertTrue(JavaTypeProvider.INSTANCE.merge(list1, map1).isError());
		assertTrue(JavaTypeProvider.INSTANCE.merge(map1, primitive).isError());
		assertTrue(JavaTypeProvider.INSTANCE.merge(map1, list1).isError());
	}
}
