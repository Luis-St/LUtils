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
	void createNullReturnsNull() {
		assertNull(JavaTypeProvider.INSTANCE.createNull(RuntimeException::new));
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(true, JavaTypeProvider.INSTANCE.createBoolean(true, RuntimeException::new));
		assertEquals((byte) 42, JavaTypeProvider.INSTANCE.createByte((byte) 42, RuntimeException::new));
		assertEquals((short) 42, JavaTypeProvider.INSTANCE.createShort((short) 42, RuntimeException::new));
		assertEquals(42, JavaTypeProvider.INSTANCE.createInteger(42, RuntimeException::new));
		assertEquals(42L, JavaTypeProvider.INSTANCE.createLong(42L, RuntimeException::new));
		assertEquals(42.5f, JavaTypeProvider.INSTANCE.createFloat(42.5f, RuntimeException::new));
		assertEquals(42.5, JavaTypeProvider.INSTANCE.createDouble(42.5, RuntimeException::new));
		assertEquals("test", JavaTypeProvider.INSTANCE.createString("test", RuntimeException::new));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.createString(null, RuntimeException::new));
	}
	
	@Test
	void createCollectionTypes() {
		Object element1 = "a";
		Object element2 = "b";
		
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.createList(null, RuntimeException::new));
		
		List<?> emptyList = (List<?>) JavaTypeProvider.INSTANCE.createList(List.of(), RuntimeException::new);
		assertTrue(emptyList.isEmpty());
		
		List<?> listWithElements = (List<?>) JavaTypeProvider.INSTANCE.createList(List.of(element1, element2), RuntimeException::new);
		assertEquals(2, listWithElements.size());
		assertEquals(element1, listWithElements.get(0));
		assertEquals(element2, listWithElements.get(1));
		
		Map<?, ?> emptyMap = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap(RuntimeException::new);
		assertTrue(emptyMap.isEmpty());
		
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.createMap(null, RuntimeException::new));
		
		Map<?, ?> emptyMapFromValues = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap(Map.of(), RuntimeException::new);
		assertTrue(emptyMapFromValues.isEmpty());
		
		Map<?, ?> mapWithElements = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2), RuntimeException::new);
		assertEquals(2, mapWithElements.size());
		assertEquals(element1, mapWithElements.get("key1"));
		assertEquals(element2, mapWithElements.get("key2"));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.isEmpty(null, RuntimeException::new));
		assertFalse(JavaTypeProvider.INSTANCE.isEmpty(List.of(), RuntimeException::new));
		assertFalse(JavaTypeProvider.INSTANCE.isEmpty(1, RuntimeException::new));
		assertFalse(JavaTypeProvider.INSTANCE.isEmpty(Map.of(), RuntimeException::new));
		assertFalse(JavaTypeProvider.INSTANCE.isEmpty("test", RuntimeException::new));
		assertFalse(JavaTypeProvider.INSTANCE.isEmpty(new Object(), RuntimeException::new));
	}
	
	@Test
	void isNullValidation() {
		assertTrue(JavaTypeProvider.INSTANCE.isNull(null, RuntimeException::new));
		
		assertFalse(JavaTypeProvider.INSTANCE.isNull(new Object(), RuntimeException::new));
		assertFalse(JavaTypeProvider.INSTANCE.isNull(List.of(), RuntimeException::new));
		assertFalse(JavaTypeProvider.INSTANCE.isNull(Map.of(), RuntimeException::new));
		assertFalse(JavaTypeProvider.INSTANCE.isNull(1, RuntimeException::new));
		assertFalse(JavaTypeProvider.INSTANCE.isNull(true, RuntimeException::new));
		assertFalse(JavaTypeProvider.INSTANCE.isNull("test", RuntimeException::new));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getBoolean(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getByte(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getShort(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getInteger(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getLong(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getFloat(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getDouble(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getString(null, RuntimeException::new));
		
		List<?> wrongType = List.of();
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getBoolean(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getByte(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getShort(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getInteger(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getLong(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getFloat(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getDouble(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getString(wrongType, RuntimeException::new));
		
		String invalidValue = "not-a-number";
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getBoolean(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getByte(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getShort(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getInteger(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getLong(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getFloat(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getDouble(invalidValue, RuntimeException::new));
		
		assertTrue(JavaTypeProvider.INSTANCE.getBoolean(true, RuntimeException::new));
		assertEquals((byte) 42, JavaTypeProvider.INSTANCE.getByte((byte) 42, RuntimeException::new));
		assertEquals((short) 42, JavaTypeProvider.INSTANCE.getShort((short) 42, RuntimeException::new));
		assertEquals(42, JavaTypeProvider.INSTANCE.getInteger(42, RuntimeException::new));
		assertEquals(42L, JavaTypeProvider.INSTANCE.getLong(42L, RuntimeException::new));
		assertEquals(42.5f, JavaTypeProvider.INSTANCE.getFloat(42.5f, RuntimeException::new));
		assertEquals(42.5, JavaTypeProvider.INSTANCE.getDouble(42.5, RuntimeException::new));
		assertEquals("test", JavaTypeProvider.INSTANCE.getString("test", RuntimeException::new));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getList(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getMap(null, RuntimeException::new));
		
		Integer wrongType = 1;
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getList(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.getMap(wrongType, RuntimeException::new));
		
		List<Object> emptyList = List.of();
		assertTrue(JavaTypeProvider.INSTANCE.getList(emptyList, RuntimeException::new).isEmpty());
		
		List<Object> listWithElements = List.of("a", "b");
		List<Object> listResult = JavaTypeProvider.INSTANCE.getList(listWithElements, RuntimeException::new);
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.get(0));
		assertEquals("b", listResult.get(1));
		
		Map<String, Object> emptyMap = Map.of();
		assertTrue(JavaTypeProvider.INSTANCE.getMap(emptyMap, RuntimeException::new).isEmpty());
		
		Map<String, Object> mapWithElements = Map.of("key", "value");
		Map<String, Object> mapResult = JavaTypeProvider.INSTANCE.getMap(mapWithElements, RuntimeException::new);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void mapOperations() {
		Map<String, Object> map = (Map<String, Object>) JavaTypeProvider.INSTANCE.createMap(RuntimeException::new);
		Object testValue = "test";
		
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.has(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.has(map, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.get(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.get(map, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.set(null, "key", testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.set(map, null, testValue, RuntimeException::new));
		
		JavaTypeProvider.INSTANCE.set(map, "key", null, RuntimeException::new);
		
		List<?> wrongType = List.of();
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.has(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.get(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.set(wrongType, "key", testValue, RuntimeException::new));
		
		assertTrue(JavaTypeProvider.INSTANCE.has(map, "key", RuntimeException::new));
		assertNull(JavaTypeProvider.INSTANCE.get(map, "key", RuntimeException::new));
		
		JavaTypeProvider.INSTANCE.set(map, "key", testValue, RuntimeException::new);
		assertTrue(JavaTypeProvider.INSTANCE.has(map, "key", RuntimeException::new));
		assertEquals(testValue, JavaTypeProvider.INSTANCE.get(map, "key", RuntimeException::new));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void mergeOperations() {
		Object emptyObject = new Object();
		Object nullObject = JavaTypeProvider.INSTANCE.createNull(RuntimeException::new);
		Integer primitive = 1;
		List<Object> list1 = List.of("a");
		List<Object> list2 = List.of("b");
		Map<String, Object> map1 = Map.of("key1", "value1");
		Map<String, Object> map2 = Map.of("key2", "value2");
		
		assertEquals(primitive, JavaTypeProvider.INSTANCE.merge(emptyObject, primitive, RuntimeException::new));
		assertEquals(list1, JavaTypeProvider.INSTANCE.merge(emptyObject, list1, RuntimeException::new));
		assertEquals(map1, JavaTypeProvider.INSTANCE.merge(emptyObject, map1, RuntimeException::new));
		
		assertEquals(emptyObject, JavaTypeProvider.INSTANCE.merge(emptyObject, nullObject, RuntimeException::new));
		assertEquals(emptyObject, JavaTypeProvider.INSTANCE.merge(nullObject, emptyObject, RuntimeException::new));
		
		assertEquals(primitive, JavaTypeProvider.INSTANCE.merge(primitive, nullObject, RuntimeException::new));
		assertEquals(primitive, JavaTypeProvider.INSTANCE.merge(nullObject, primitive, RuntimeException::new));
		
		assertEquals(list1, JavaTypeProvider.INSTANCE.merge(list1, nullObject, RuntimeException::new));
		assertEquals(list1, JavaTypeProvider.INSTANCE.merge(nullObject, list1, RuntimeException::new));
		
		assertEquals(map1, JavaTypeProvider.INSTANCE.merge(map1, nullObject, RuntimeException::new));
		assertEquals(map1, JavaTypeProvider.INSTANCE.merge(nullObject, map1, RuntimeException::new));
		
		List<Object> mutableList1 = (List<Object>) JavaTypeProvider.INSTANCE.createList(Lists.newArrayList("a"), RuntimeException::new);
		Object mergedListResult = JavaTypeProvider.INSTANCE.merge(mutableList1, list2, RuntimeException::new);
		List<Object> mergedList = (List<Object>) mergedListResult;
		assertEquals(2, mergedList.size());
		assertEquals("a", mergedList.get(0));
		assertEquals("b", mergedList.get(1));
		
		Map<String, Object> mutableMap1 = (Map<String, Object>) JavaTypeProvider.INSTANCE.createMap(Maps.newHashMap(Map.of("key1", "value1")), RuntimeException::new);
		Object mergedMapResult = JavaTypeProvider.INSTANCE.merge(mutableMap1, map2, RuntimeException::new);
		Map<String, Object> mergedMap = (Map<String, Object>) mergedMapResult;
		assertEquals(2, mergedMap.size());
		assertEquals("value1", mergedMap.get("key1"));
		assertEquals("value2", mergedMap.get("key2"));
		
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.merge(primitive, list1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.merge(primitive, map1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.merge(list1, primitive, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.merge(list1, map1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.merge(map1, primitive, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JavaTypeProvider.INSTANCE.merge(map1, list1, RuntimeException::new));
	}
}
