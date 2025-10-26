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
import net.luis.utils.util.result.Result;
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
		assertNull(JavaTypeProvider.INSTANCE.createNull().resultOrThrow());
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
		Result<Object> result = JavaTypeProvider.INSTANCE.createString(null);
		assertTrue(result.isError());
		assertTrue(result.errorOrThrow().startsWith("Value 'null'"));
	}
	
	@Test
	void createCollectionTypes() {
		Object element1 = "a";
		Object element2 = "b";
		
		Result<Object> nullList = JavaTypeProvider.INSTANCE.createList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().startsWith("Value 'null'"));
		
		List<?> emptyList = (List<?>) JavaTypeProvider.INSTANCE.createList(List.of()).resultOrThrow();
		assertTrue(emptyList.isEmpty());
		
		List<?> listWithElements = (List<?>) JavaTypeProvider.INSTANCE.createList(List.of(element1, element2)).resultOrThrow();
		assertEquals(2, listWithElements.size());
		assertEquals(element1, listWithElements.get(0));
		assertEquals(element2, listWithElements.get(1));
		
		Map<?, ?> emptyMap = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap().resultOrThrow();
		assertTrue(emptyMap.isEmpty());
		
		Result<Object> nullMap = JavaTypeProvider.INSTANCE.createMap((Map<String, Object>) null);
		assertTrue(nullMap.isError());
		assertTrue(nullMap.errorOrThrow().startsWith("Value 'null'"));
		
		Map<?, ?> emptyMapFromValues = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap(Map.of()).resultOrThrow();
		assertTrue(emptyMapFromValues.isEmpty());
		
		Map<?, ?> mapWithElements = (Map<?, ?>) JavaTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2)).resultOrThrow();
		assertEquals(2, mapWithElements.size());
		assertEquals(element1, mapWithElements.get("key1"));
		assertEquals(element2, mapWithElements.get("key2"));
	}
	
	@Test
	void getEmptyValidation() {
		Result<Object> nullEmpty = JavaTypeProvider.INSTANCE.getEmpty(null);
		assertTrue(nullEmpty.isError());
		assertTrue(nullEmpty.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(JavaTypeProvider.INSTANCE.getEmpty(List.of()).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getEmpty(1).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getEmpty(Map.of()).isError());
		assertTrue(JavaTypeProvider.INSTANCE.getEmpty("test").isError());
		
		Object emptyObject = new Object();
		assertEquals(emptyObject, JavaTypeProvider.INSTANCE.getEmpty(emptyObject).resultOrThrow());
	}
	
	@Test
	void isNullValidation() {
		assertTrue(JavaTypeProvider.INSTANCE.isNull(null).resultOrThrow());
		
		assertFalse(JavaTypeProvider.INSTANCE.isNull(new Object()).resultOrThrow());
		assertFalse(JavaTypeProvider.INSTANCE.isNull(List.of()).resultOrThrow());
		assertFalse(JavaTypeProvider.INSTANCE.isNull(Map.of()).resultOrThrow());
		assertFalse(JavaTypeProvider.INSTANCE.isNull(1).resultOrThrow());
		assertFalse(JavaTypeProvider.INSTANCE.isNull(true).resultOrThrow());
		assertFalse(JavaTypeProvider.INSTANCE.isNull("test").resultOrThrow());
	}
	
	@Test
	void getPrimitiveTypes() {
		Result<Boolean> nullBoolean = JavaTypeProvider.INSTANCE.getBoolean(null);
		assertTrue(nullBoolean.isError());
		assertTrue(nullBoolean.errorOrThrow().startsWith("Value 'null'"));
		Result<Byte> nullByte = JavaTypeProvider.INSTANCE.getByte(null);
		assertTrue(nullByte.isError());
		assertTrue(nullByte.errorOrThrow().startsWith("Value 'null'"));
		Result<Short> nullShort = JavaTypeProvider.INSTANCE.getShort(null);
		assertTrue(nullShort.isError());
		assertTrue(nullShort.errorOrThrow().startsWith("Value 'null'"));
		Result<Integer> nullInteger = JavaTypeProvider.INSTANCE.getInteger(null);
		assertTrue(nullInteger.isError());
		assertTrue(nullInteger.errorOrThrow().startsWith("Value 'null'"));
		Result<Long> nullLong = JavaTypeProvider.INSTANCE.getLong(null);
		assertTrue(nullLong.isError());
		assertTrue(nullLong.errorOrThrow().startsWith("Value 'null'"));
		Result<Float> nullFloat = JavaTypeProvider.INSTANCE.getFloat(null);
		assertTrue(nullFloat.isError());
		assertTrue(nullFloat.errorOrThrow().startsWith("Value 'null'"));
		Result<Double> nullDouble = JavaTypeProvider.INSTANCE.getDouble(null);
		assertTrue(nullDouble.isError());
		assertTrue(nullDouble.errorOrThrow().startsWith("Value 'null'"));
		Result<String> nullString = JavaTypeProvider.INSTANCE.getString(null);
		assertTrue(nullString.isError());
		assertTrue(nullString.errorOrThrow().startsWith("Value 'null'"));
		
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
		Result<List<Object>> nullList = JavaTypeProvider.INSTANCE.getList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().startsWith("Value 'null'"));
		Result<Map<String, Object>> nullMap = JavaTypeProvider.INSTANCE.getMap(null);
		assertTrue(nullMap.isError());
		assertTrue(nullMap.errorOrThrow().startsWith("Value 'null'"));
		
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
		
		Result<Boolean> nullHas = JavaTypeProvider.INSTANCE.has(null, "key");
		assertTrue(nullHas.isError());
		assertTrue(nullHas.errorOrThrow().startsWith("Value 'null'"));
		Result<Boolean> hasNullKey = JavaTypeProvider.INSTANCE.has(map, null);
		assertTrue(hasNullKey.isError());
		assertTrue(hasNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<Object> nullGet = JavaTypeProvider.INSTANCE.get(null, "key");
		assertTrue(nullGet.isError());
		assertTrue(nullGet.errorOrThrow().startsWith("Value 'null'"));
		Result<Object> getNullKey = JavaTypeProvider.INSTANCE.get(map, null);
		assertTrue(getNullKey.isError());
		assertTrue(getNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<Object> nullSet = JavaTypeProvider.INSTANCE.set(null, "key", testValue);
		assertTrue(nullSet.isError());
		assertTrue(nullSet.errorOrThrow().startsWith("Value 'null'"));
		Result<Object> setNullKey = JavaTypeProvider.INSTANCE.set(map, null, testValue);
		assertTrue(setNullKey.isError());
		assertTrue(setNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<Object> nullValueSet = JavaTypeProvider.INSTANCE.set(map, "key", (Object) null);
		assertTrue(nullValueSet.isSuccess());
		assertNull(nullValueSet.resultOrThrow());
		
		List<?> wrongType = List.of();
		assertTrue(JavaTypeProvider.INSTANCE.has(wrongType, "key").isError());
		assertTrue(JavaTypeProvider.INSTANCE.get(wrongType, "key").isError());
		assertTrue(JavaTypeProvider.INSTANCE.set(wrongType, "key", testValue).isError());
		
		assertTrue(JavaTypeProvider.INSTANCE.has(map, "key").resultOrThrow());
		assertNull(JavaTypeProvider.INSTANCE.get(map, "key").resultOrThrow());
		
		assertNull(JavaTypeProvider.INSTANCE.set(map, "key", testValue).resultOrThrow());
		assertTrue(JavaTypeProvider.INSTANCE.has(map, "key").resultOrThrow());
		assertEquals(testValue, JavaTypeProvider.INSTANCE.get(map, "key").resultOrThrow());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	void mergeOperations() {
		Object emptyObject = new Object();
		Object nullObject = JavaTypeProvider.INSTANCE.createNull().resultOrThrow();
		Integer primitive = 1;
		List<Object> list1 = List.of("a");
		List<Object> list2 = List.of("b");
		Map<String, Object> map1 = Map.of("key1", "value1");
		Map<String, Object> map2 = Map.of("key2", "value2");
		
		assertEquals(primitive, JavaTypeProvider.INSTANCE.merge(emptyObject, primitive).resultOrThrow());
		assertEquals(list1, JavaTypeProvider.INSTANCE.merge(emptyObject, list1).resultOrThrow());
		assertEquals(map1, JavaTypeProvider.INSTANCE.merge(emptyObject, map1).resultOrThrow());
		
		assertEquals(emptyObject, JavaTypeProvider.INSTANCE.merge(emptyObject, nullObject).resultOrThrow());
		assertEquals(emptyObject, JavaTypeProvider.INSTANCE.merge(nullObject, emptyObject).resultOrThrow());
		
		assertEquals(primitive, JavaTypeProvider.INSTANCE.merge(primitive, nullObject).resultOrThrow());
		assertEquals(primitive, JavaTypeProvider.INSTANCE.merge(nullObject, primitive).resultOrThrow());
		
		assertEquals(list1, JavaTypeProvider.INSTANCE.merge(list1, nullObject).resultOrThrow());
		assertEquals(list1, JavaTypeProvider.INSTANCE.merge(nullObject, list1).resultOrThrow());
		
		assertEquals(map1, JavaTypeProvider.INSTANCE.merge(map1, nullObject).resultOrThrow());
		assertEquals(map1, JavaTypeProvider.INSTANCE.merge(nullObject, map1).resultOrThrow());
		
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
