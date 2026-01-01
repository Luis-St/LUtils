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

import net.luis.utils.io.data.json.*;
import net.luis.utils.util.result.Result;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonTypeProvider}.<br>
 *
 * @author Luis-St
 */
class JsonTypeProviderTest {
	
	@Test
	void emptyReturnsJsonElement() {
		JsonElement element = JsonTypeProvider.INSTANCE.empty();
		assertFalse(element.isJsonNull());
		assertFalse(element.isJsonPrimitive());
		assertFalse(element.isJsonArray());
		assertFalse(element.isJsonObject());
	}
	
	@Test
	void createNullReturnsJsonNull() {
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.createNull().resultOrThrow());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new JsonPrimitive(true), JsonTypeProvider.INSTANCE.createBoolean(true).resultOrThrow());
		assertEquals(new JsonPrimitive((byte) 42), JsonTypeProvider.INSTANCE.createByte((byte) 42).resultOrThrow());
		assertEquals(new JsonPrimitive((short) 42), JsonTypeProvider.INSTANCE.createShort((short) 42).resultOrThrow());
		assertEquals(new JsonPrimitive(42), JsonTypeProvider.INSTANCE.createInteger(42).resultOrThrow());
		assertEquals(new JsonPrimitive(42L), JsonTypeProvider.INSTANCE.createLong(42L).resultOrThrow());
		assertEquals(new JsonPrimitive(42.5f), JsonTypeProvider.INSTANCE.createFloat(42.5f).resultOrThrow());
		assertEquals(new JsonPrimitive(42.5), JsonTypeProvider.INSTANCE.createDouble(42.5).resultOrThrow());
		assertEquals(new JsonPrimitive("test"), JsonTypeProvider.INSTANCE.createString("test").resultOrThrow());
	}
	
	@Test
	void createStringWithNullThrowsException() {
		Result<JsonElement> res = JsonTypeProvider.INSTANCE.createString(null);
		assertTrue(res.isError());
		assertTrue(res.errorOrThrow().startsWith("Value 'null'"));
	}
	
	@Test
	void createCollectionTypes() {
		JsonElement element1 = new JsonPrimitive("a");
		JsonElement element2 = new JsonPrimitive("b");
		
		Result<JsonElement> nullList = JsonTypeProvider.INSTANCE.createList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().startsWith("Value 'null'"));
		
		JsonArray emptyArray = new JsonArray();
		assertEquals(emptyArray, JsonTypeProvider.INSTANCE.createList(List.of()).resultOrThrow());
		
		JsonArray arrayWithElements = new JsonArray(List.of(element1, element2));
		assertEquals(arrayWithElements, JsonTypeProvider.INSTANCE.createList(List.of(element1, element2)).resultOrThrow());
		
		JsonObject emptyObject = new JsonObject();
		assertEquals(emptyObject, JsonTypeProvider.INSTANCE.createMap().resultOrThrow());
		assertEquals(emptyObject, JsonTypeProvider.INSTANCE.createMap(Map.of()).resultOrThrow());
		
		JsonObject objectWithElements = new JsonObject(Map.of("key1", element1, "key2", element2));
		assertEquals(objectWithElements, JsonTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2)).resultOrThrow());
	}
	
	@Test
	void getEmptyValidation() {
		Result<JsonElement> nullEmpty = JsonTypeProvider.INSTANCE.getEmpty(null);
		assertTrue(nullEmpty.isError());
		assertTrue(nullEmpty.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(JsonTypeProvider.INSTANCE.getEmpty(new JsonArray()).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getEmpty(new JsonPrimitive(1)).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getEmpty(new JsonObject()).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getEmpty(JsonNull.INSTANCE).isError());
	}
	
	@Test
	void isNullValidation() {
		Result<Boolean> nullIsNull = JsonTypeProvider.INSTANCE.isNull(null);
		assertTrue(nullIsNull.isError());
		assertTrue(nullIsNull.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(JsonTypeProvider.INSTANCE.isNull(JsonNull.INSTANCE).resultOrThrow());
		
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonArray()).resultOrThrow());
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonObject()).resultOrThrow());
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonPrimitive(1)).resultOrThrow());
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonPrimitive(true)).resultOrThrow());
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonPrimitive("test")).resultOrThrow());
	}
	
	@Test
	void getPrimitiveTypes() {
		Result<Boolean> nullBoolean = JsonTypeProvider.INSTANCE.getBoolean(null);
		assertTrue(nullBoolean.isError());
		assertTrue(nullBoolean.errorOrThrow().startsWith("Value 'null'"));
		Result<Byte> nullByte = JsonTypeProvider.INSTANCE.getByte(null);
		assertTrue(nullByte.isError());
		assertTrue(nullByte.errorOrThrow().startsWith("Value 'null'"));
		Result<Short> nullShort = JsonTypeProvider.INSTANCE.getShort(null);
		assertTrue(nullShort.isError());
		assertTrue(nullShort.errorOrThrow().startsWith("Value 'null'"));
		Result<Integer> nullInteger = JsonTypeProvider.INSTANCE.getInteger(null);
		assertTrue(nullInteger.isError());
		assertTrue(nullInteger.errorOrThrow().startsWith("Value 'null'"));
		Result<Long> nullLong = JsonTypeProvider.INSTANCE.getLong(null);
		assertTrue(nullLong.isError());
		assertTrue(nullLong.errorOrThrow().startsWith("Value 'null'"));
		Result<Float> nullFloat = JsonTypeProvider.INSTANCE.getFloat(null);
		assertTrue(nullFloat.isError());
		assertTrue(nullFloat.errorOrThrow().startsWith("Value 'null'"));
		Result<Double> nullDouble = JsonTypeProvider.INSTANCE.getDouble(null);
		assertTrue(nullDouble.isError());
		assertTrue(nullDouble.errorOrThrow().startsWith("Value 'null'"));
		Result<String> nullString = JsonTypeProvider.INSTANCE.getString(null);
		assertTrue(nullString.isError());
		assertTrue(nullString.errorOrThrow().startsWith("Value 'null'"));
		
		JsonArray wrongType = new JsonArray();
		assertTrue(JsonTypeProvider.INSTANCE.getBoolean(wrongType).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getByte(wrongType).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getShort(wrongType).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getInteger(wrongType).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getLong(wrongType).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getFloat(wrongType).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getDouble(wrongType).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getString(wrongType).isError());
		
		JsonPrimitive invalidValue = new JsonPrimitive("not-a-number");
		assertTrue(JsonTypeProvider.INSTANCE.getBoolean(invalidValue).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getByte(invalidValue).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getShort(invalidValue).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getInteger(invalidValue).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getLong(invalidValue).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getFloat(invalidValue).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getDouble(invalidValue).isError());
		
		assertTrue(JsonTypeProvider.INSTANCE.getBoolean(new JsonPrimitive(true)).resultOrThrow());
		assertEquals((byte) 42, JsonTypeProvider.INSTANCE.getByte(new JsonPrimitive((byte) 42)).resultOrThrow());
		assertEquals((short) 42, JsonTypeProvider.INSTANCE.getShort(new JsonPrimitive((short) 42)).resultOrThrow());
		assertEquals(42, JsonTypeProvider.INSTANCE.getInteger(new JsonPrimitive(42)).resultOrThrow());
		assertEquals(42L, JsonTypeProvider.INSTANCE.getLong(new JsonPrimitive(42L)).resultOrThrow());
		assertEquals(42.5f, JsonTypeProvider.INSTANCE.getFloat(new JsonPrimitive(42.5f)).resultOrThrow());
		assertEquals(42.5, JsonTypeProvider.INSTANCE.getDouble(new JsonPrimitive(42.5)).resultOrThrow());
		assertEquals("test", JsonTypeProvider.INSTANCE.getString(new JsonPrimitive("test")).resultOrThrow());
	}
	
	@Test
	void getCollectionTypes() {
		Result<List<JsonElement>> nullList = JsonTypeProvider.INSTANCE.getList(null);
		assertTrue(nullList.isError());
		assertTrue(nullList.errorOrThrow().startsWith("Value 'null'"));
		Result<Map<String, JsonElement>> nullMap = JsonTypeProvider.INSTANCE.getMap(null);
		assertTrue(nullMap.isError());
		assertTrue(nullMap.errorOrThrow().startsWith("Value 'null'"));
		
		JsonPrimitive wrongType = new JsonPrimitive(1);
		assertTrue(JsonTypeProvider.INSTANCE.getList(wrongType).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getMap(wrongType).isError());
		
		JsonArray emptyArray = new JsonArray();
		assertTrue(JsonTypeProvider.INSTANCE.getList(emptyArray).resultOrThrow().isEmpty());
		
		JsonArray arrayWithElements = new JsonArray(List.of(new JsonPrimitive("a"), new JsonPrimitive("b")));
		List<JsonElement> listResult = JsonTypeProvider.INSTANCE.getList(arrayWithElements).resultOrThrow();
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsJsonPrimitive().getAsString());
		
		JsonObject emptyObject = new JsonObject();
		assertTrue(JsonTypeProvider.INSTANCE.getMap(emptyObject).resultOrThrow().isEmpty());
		
		JsonObject objectWithElements = new JsonObject(Map.of("key", new JsonPrimitive("value")));
		Map<String, JsonElement> mapResult = JsonTypeProvider.INSTANCE.getMap(objectWithElements).resultOrThrow();
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsJsonPrimitive().getAsString());
	}
	
	@Test
	void mapOperations() {
		JsonObject jsonObject = new JsonObject();
		JsonElement testValue = new JsonPrimitive("test");
		
		Result<Boolean> nullHas = JsonTypeProvider.INSTANCE.has(null, "key");
		assertTrue(nullHas.isError());
		assertTrue(nullHas.errorOrThrow().startsWith("Value 'null'"));
		Result<Boolean> hasNullKey = JsonTypeProvider.INSTANCE.has(jsonObject, null);
		assertTrue(hasNullKey.isError());
		assertTrue(hasNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<JsonElement> nullGet = JsonTypeProvider.INSTANCE.get(null, "key");
		assertTrue(nullGet.isError());
		assertTrue(nullGet.errorOrThrow().startsWith("Value 'null'"));
		Result<JsonElement> getNullKey = JsonTypeProvider.INSTANCE.get(jsonObject, null);
		assertTrue(getNullKey.isError());
		assertTrue(getNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<JsonElement> nullSet = JsonTypeProvider.INSTANCE.set(null, "key", testValue);
		assertTrue(nullSet.isError());
		assertTrue(nullSet.errorOrThrow().startsWith("Value 'null'"));
		Result<JsonElement> setNullKey = JsonTypeProvider.INSTANCE.set(jsonObject, null, testValue);
		assertTrue(setNullKey.isError());
		assertTrue(setNullKey.errorOrThrow().startsWith("Value 'null'"));
		Result<JsonElement> nullValueSet = JsonTypeProvider.INSTANCE.set(jsonObject, "key", (JsonElement) null);
		assertTrue(nullValueSet.isError());
		assertTrue(nullValueSet.errorOrThrow().startsWith("Value 'null'"));
		
		JsonArray wrongType = new JsonArray();
		assertTrue(JsonTypeProvider.INSTANCE.has(wrongType, "key").isError());
		assertTrue(JsonTypeProvider.INSTANCE.get(wrongType, "key").isError());
		assertTrue(JsonTypeProvider.INSTANCE.set(wrongType, "key", testValue).isError());
		
		assertFalse(JsonTypeProvider.INSTANCE.has(jsonObject, "key").resultOrThrow());
		assertNull(JsonTypeProvider.INSTANCE.get(jsonObject, "key").resultOrThrow());
		
		assertNull(JsonTypeProvider.INSTANCE.set(jsonObject, "key", testValue).resultOrThrow());
		assertTrue(JsonTypeProvider.INSTANCE.has(jsonObject, "key").resultOrThrow());
		assertEquals(testValue, JsonTypeProvider.INSTANCE.get(jsonObject, "key").resultOrThrow());
	}
	
	@Test
	void mapOperationsWithResults() {
		JsonObject jsonObject = new JsonObject();
		JsonElement testValue = new JsonPrimitive("test");
		
		Result<JsonElement> nullType = JsonTypeProvider.INSTANCE.set(null, "key", Result.success(testValue));
		assertTrue(nullType.isError());
		assertTrue(nullType.errorOrThrow().startsWith("Type 'null'"));
		Result<JsonElement> nullKey = JsonTypeProvider.INSTANCE.set(jsonObject, null, Result.success(testValue));
		assertTrue(nullKey.isError());
		assertTrue(nullKey.errorOrThrow().startsWith("Key 'null'"));
		Result<JsonElement> nullValue = JsonTypeProvider.INSTANCE.set(jsonObject, "key", (Result<JsonElement>) null);
		assertTrue(nullValue.isError());
		assertTrue(nullValue.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(JsonTypeProvider.INSTANCE.set(jsonObject, "key", Result.error("error")).isError());
		assertTrue(JsonTypeProvider.INSTANCE.set(jsonObject, "key", Result.success(testValue)).isSuccess());
		assertEquals(testValue, jsonObject.get("key"));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.merge(null, JsonNull.INSTANCE).resultOrThrow());
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, (JsonElement) null).resultOrThrow());
		
		JsonElement primitive = new JsonPrimitive(1);
		JsonArray array1 = new JsonArray(List.of(new JsonPrimitive("a")));
		JsonArray array2 = new JsonArray(List.of(new JsonPrimitive("b")));
		JsonObject object1 = new JsonObject(Map.of("key1", new JsonPrimitive("value1")));
		JsonObject object2 = new JsonObject(Map.of("key2", new JsonPrimitive("value2")));
		
		assertEquals(primitive, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), primitive).resultOrThrow());
		assertEquals(array1, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), array1).resultOrThrow());
		assertEquals(object1, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), object1).resultOrThrow());
		
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), JsonNull.INSTANCE).resultOrThrow());
		
		assertEquals(primitive, JsonTypeProvider.INSTANCE.merge(primitive, JsonNull.INSTANCE).resultOrThrow());
		assertEquals(primitive, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, primitive).resultOrThrow());
		
		assertEquals(array1, JsonTypeProvider.INSTANCE.merge(array1, JsonNull.INSTANCE).resultOrThrow());
		assertEquals(array1, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, array1).resultOrThrow());
		
		assertEquals(object1, JsonTypeProvider.INSTANCE.merge(object1, JsonNull.INSTANCE).resultOrThrow());
		assertEquals(object1, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, object1).resultOrThrow());
		
		JsonArray mergedArray = JsonTypeProvider.INSTANCE.merge(array1, array2).resultOrThrow().getAsJsonArray();
		assertEquals(2, mergedArray.size());
		assertEquals("a", mergedArray.get(0).getAsJsonPrimitive().getAsString());
		assertEquals("b", mergedArray.get(1).getAsJsonPrimitive().getAsString());
		
		JsonObject mergedObject = JsonTypeProvider.INSTANCE.merge(object1, object2).resultOrThrow().getAsJsonObject();
		assertEquals(2, mergedObject.size());
		assertEquals("value1", mergedObject.get("key1").getAsJsonPrimitive().getAsString());
		assertEquals("value2", mergedObject.get("key2").getAsJsonPrimitive().getAsString());
		
		assertTrue(JsonTypeProvider.INSTANCE.merge(primitive, array1).isError());
		assertTrue(JsonTypeProvider.INSTANCE.merge(primitive, object1).isError());
		assertTrue(JsonTypeProvider.INSTANCE.merge(array1, primitive).isError());
		assertTrue(JsonTypeProvider.INSTANCE.merge(array1, object1).isError());
		assertTrue(JsonTypeProvider.INSTANCE.merge(object1, primitive).isError());
		assertTrue(JsonTypeProvider.INSTANCE.merge(object1, array1).isError());
	}
	
	@Test
	void mergeOperationsWithResults() {
		JsonObject jsonObject = new JsonObject();
		JsonElement testValue = new JsonPrimitive("test");
		
		Result<JsonElement> nullCurrent = JsonTypeProvider.INSTANCE.merge(null, Result.success(testValue));
		assertTrue(nullCurrent.isError());
		assertTrue(nullCurrent.errorOrThrow().startsWith("Current value 'null'"));
		Result<JsonElement> nullValue = JsonTypeProvider.INSTANCE.merge(jsonObject, (Result<JsonElement>) null);
		assertTrue(nullValue.isError());
		assertTrue(nullValue.errorOrThrow().startsWith("Value 'null'"));
		
		assertTrue(JsonTypeProvider.INSTANCE.merge(jsonObject, Result.error("error")).isError());
		assertEquals(testValue, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), Result.success(testValue)).resultOrThrow());
	}
}
