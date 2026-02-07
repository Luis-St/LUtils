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
	void emptyReturnsPlainObject() {
		Object empty = JavaTypeProvider.INSTANCE.empty();
		assertNotNull(empty);
		assertEquals(Object.class, empty.getClass());
	}
	
	@Test
	void createNullReturnsNull() {
		assertNull(JavaTypeProvider.INSTANCE.createNull());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(true, JavaTypeProvider.INSTANCE.createBoolean(true));
		assertEquals((byte) 42, JavaTypeProvider.INSTANCE.createByte((byte) 42));
		assertEquals((short) 42, JavaTypeProvider.INSTANCE.createShort((short) 42));
		assertEquals(42, JavaTypeProvider.INSTANCE.createInteger(42));
		assertEquals(42L, JavaTypeProvider.INSTANCE.createLong(42L));
		assertEquals(42.5f, JavaTypeProvider.INSTANCE.createFloat(42.5f));
		assertEquals(42.5, JavaTypeProvider.INSTANCE.createDouble(42.5));
		assertEquals("test", JavaTypeProvider.INSTANCE.createString("test"));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.createString(null));
	}
	
	@Test
	void createCollectionTypes() {
		Object element1 = "a";
		Object element2 = "b";
		
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.createList(null));
		
		List<?> emptyList = (List<?>) JavaTypeProvider.INSTANCE.createList(List.of());
		assertTrue(emptyList.isEmpty());
		
		List<?> listWithElements = (List<?>) JavaTypeProvider.INSTANCE.createList(List.of(element1, element2));
		assertEquals(2, listWithElements.size());
		assertEquals(element1, listWithElements.get(0));
		assertEquals(element2, listWithElements.get(1));
		
		Map<?, ?> emptyMap = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap();
		assertTrue(emptyMap.isEmpty());
		
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.createMap(null));
		
		Map<?, ?> emptyMapFromValues = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap(Map.of());
		assertTrue(emptyMapFromValues.isEmpty());
		
		Map<?, ?> mapWithElements = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2));
		assertEquals(2, mapWithElements.size());
		assertEquals(element1, mapWithElements.get("key1"));
		assertEquals(element2, mapWithElements.get("key2"));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.isEmpty(null));
		assertFalse(JavaTypeProvider.INSTANCE.isEmpty(List.of()));
		assertFalse(JavaTypeProvider.INSTANCE.isEmpty(1));
		assertFalse(JavaTypeProvider.INSTANCE.isEmpty(Map.of()));
		assertFalse(JavaTypeProvider.INSTANCE.isEmpty("test"));
		
		Object emptyObject = new Object();
		assertTrue(JavaTypeProvider.INSTANCE.isEmpty(emptyObject));
	}
	
	@Test
	void isNullValidation() {
		assertTrue(JavaTypeProvider.INSTANCE.isNull(null));
		
		assertFalse(JavaTypeProvider.INSTANCE.isNull(new Object()));
		assertFalse(JavaTypeProvider.INSTANCE.isNull(List.of()));
		assertFalse(JavaTypeProvider.INSTANCE.isNull(Map.of()));
		assertFalse(JavaTypeProvider.INSTANCE.isNull(1));
		assertFalse(JavaTypeProvider.INSTANCE.isNull(true));
		assertFalse(JavaTypeProvider.INSTANCE.isNull("test"));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getBoolean(null));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getByte(null));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getShort(null));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getInteger(null));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getLong(null));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getFloat(null));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getDouble(null));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getString(null));
		
		List<?> wrongType = List.of();
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getBoolean(wrongType));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getByte(wrongType));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getShort(wrongType));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getInteger(wrongType));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getLong(wrongType));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getFloat(wrongType));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getDouble(wrongType));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getString(wrongType));
		
		String invalidValue = "not-a-number";
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getBoolean(invalidValue));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getByte(invalidValue));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getShort(invalidValue));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getInteger(invalidValue));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getLong(invalidValue));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getFloat(invalidValue));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getDouble(invalidValue));
		
		assertTrue(JavaTypeProvider.INSTANCE.getBoolean(true));
		assertEquals((byte) 42, JavaTypeProvider.INSTANCE.getByte((byte) 42));
		assertEquals((short) 42, JavaTypeProvider.INSTANCE.getShort((short) 42));
		assertEquals(42, JavaTypeProvider.INSTANCE.getInteger(42));
		assertEquals(42L, JavaTypeProvider.INSTANCE.getLong(42L));
		assertEquals(42.5f, JavaTypeProvider.INSTANCE.getFloat(42.5f));
		assertEquals(42.5, JavaTypeProvider.INSTANCE.getDouble(42.5));
		assertEquals("test", JavaTypeProvider.INSTANCE.getString("test"));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getList(null));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getMap(null));
		
		Integer wrongType = 1;
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getList(wrongType));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.getMap(wrongType));
		
		List<Object> emptyList = List.of();
		assertTrue(JavaTypeProvider.INSTANCE.getList(emptyList).isEmpty());
		
		List<Object> listWithElements = List.of("a", "b");
		List<Object> listResult = JavaTypeProvider.INSTANCE.getList(listWithElements);
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.get(0));
		assertEquals("b", listResult.get(1));
		
		Map<String, Object> emptyMap = Map.of();
		assertTrue(JavaTypeProvider.INSTANCE.getMap(emptyMap).isEmpty());
		
		Map<String, Object> mapWithElements = Map.of("key", "value");
		Map<String, Object> mapResult = JavaTypeProvider.INSTANCE.getMap(mapWithElements);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void mapOperations() {
		Map<String, Object> map = (Map<String, Object>) JavaTypeProvider.INSTANCE.createMap();
		Object testValue = "test";
		
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.has(null, "key"));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.has(map, null));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.get(null, "key"));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.get(map, null));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.set(null, "key", testValue));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.set(map, null, testValue));
		
		JavaTypeProvider.INSTANCE.set(map, "key", null);
		
		List<?> wrongType = List.of();
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.has(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.get(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.set(wrongType, "key", testValue));
		
		assertTrue(JavaTypeProvider.INSTANCE.has(map, "key"));
		assertNull(JavaTypeProvider.INSTANCE.get(map, "key"));
		
		JavaTypeProvider.INSTANCE.set(map, "key", testValue);
		assertTrue(JavaTypeProvider.INSTANCE.has(map, "key"));
		assertEquals(testValue, JavaTypeProvider.INSTANCE.get(map, "key"));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void mergeOperations() {
		Object emptyObject = new Object();
		Object nullObject = JavaTypeProvider.INSTANCE.createNull();
		Integer primitive = 1;
		List<Object> list1 = List.of("a");
		List<Object> list2 = List.of("b");
		Map<String, Object> map1 = Map.of("key1", "value1");
		Map<String, Object> map2 = Map.of("key2", "value2");
		
		assertEquals(primitive, JavaTypeProvider.INSTANCE.merge(emptyObject, primitive));
		assertEquals(list1, JavaTypeProvider.INSTANCE.merge(emptyObject, list1));
		assertEquals(map1, JavaTypeProvider.INSTANCE.merge(emptyObject, map1));
		
		assertEquals(emptyObject, JavaTypeProvider.INSTANCE.merge(emptyObject, nullObject));
		assertEquals(emptyObject, JavaTypeProvider.INSTANCE.merge(nullObject, emptyObject));
		
		assertEquals(primitive, JavaTypeProvider.INSTANCE.merge(primitive, nullObject));
		assertEquals(primitive, JavaTypeProvider.INSTANCE.merge(nullObject, primitive));
		
		assertEquals(list1, JavaTypeProvider.INSTANCE.merge(list1, nullObject));
		assertEquals(list1, JavaTypeProvider.INSTANCE.merge(nullObject, list1));
		
		assertEquals(map1, JavaTypeProvider.INSTANCE.merge(map1, nullObject));
		assertEquals(map1, JavaTypeProvider.INSTANCE.merge(nullObject, map1));
		
		List<Object> mutableList1 = (List<Object>) JavaTypeProvider.INSTANCE.createList(Lists.newArrayList("a"));
		Object mergedListResult = JavaTypeProvider.INSTANCE.merge(mutableList1, list2);
		List<Object> mergedList = (List<Object>) mergedListResult;
		assertEquals(2, mergedList.size());
		assertEquals("a", mergedList.get(0));
		assertEquals("b", mergedList.get(1));
		
		Map<String, Object> mutableMap1 = (Map<String, Object>) JavaTypeProvider.INSTANCE.createMap(Maps.newHashMap(Map.of("key1", "value1")));
		Object mergedMapResult = JavaTypeProvider.INSTANCE.merge(mutableMap1, map2);
		Map<String, Object> mergedMap = (Map<String, Object>) mergedMapResult;
		assertEquals(2, mergedMap.size());
		assertEquals("value1", mergedMap.get("key1"));
		assertEquals("value2", mergedMap.get("key2"));
		
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.merge(primitive, list1));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.merge(primitive, map1));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.merge(list1, primitive));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.merge(list1, map1));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.merge(map1, primitive));
		assertThrows(TypeProviderException.class, () -> JavaTypeProvider.INSTANCE.merge(map1, list1));
	}
}
