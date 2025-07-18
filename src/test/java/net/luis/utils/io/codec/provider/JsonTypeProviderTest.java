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

import net.luis.utils.io.codec.provider.JsonTypeProvider;
import net.luis.utils.io.data.json.*;
import net.luis.utils.util.Result;
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
	void emptyReturnsJsonNull() {
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.empty());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new JsonPrimitive(true), JsonTypeProvider.INSTANCE.createBoolean(true).orThrow());
		assertEquals(new JsonPrimitive((byte) 42), JsonTypeProvider.INSTANCE.createByte((byte) 42).orThrow());
		assertEquals(new JsonPrimitive((short) 42), JsonTypeProvider.INSTANCE.createShort((short) 42).orThrow());
		assertEquals(new JsonPrimitive(42), JsonTypeProvider.INSTANCE.createInteger(42).orThrow());
		assertEquals(new JsonPrimitive(42L), JsonTypeProvider.INSTANCE.createLong(42L).orThrow());
		assertEquals(new JsonPrimitive(42.5f), JsonTypeProvider.INSTANCE.createFloat(42.5f).orThrow());
		assertEquals(new JsonPrimitive(42.5), JsonTypeProvider.INSTANCE.createDouble(42.5).orThrow());
		assertEquals(new JsonPrimitive("test"), JsonTypeProvider.INSTANCE.createString("test").orThrow());
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.createString(null));
	}
	
	@Test
	void createCollectionTypes() {
		JsonElement element1 = new JsonPrimitive("a");
		JsonElement element2 = new JsonPrimitive("b");
		
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.createList(null));
		
		JsonArray emptyArray = new JsonArray();
		assertEquals(emptyArray, JsonTypeProvider.INSTANCE.createList(List.of()).orThrow());
		
		JsonArray arrayWithElements = new JsonArray(List.of(element1, element2));
		assertEquals(arrayWithElements, JsonTypeProvider.INSTANCE.createList(List.of(element1, element2)).orThrow());
		
		JsonObject emptyObject = new JsonObject();
		assertEquals(emptyObject, JsonTypeProvider.INSTANCE.createMap().orThrow());
		assertEquals(emptyObject, JsonTypeProvider.INSTANCE.createMap(Map.of()).orThrow());
		
		JsonObject objectWithElements = new JsonObject(Map.of("key1", element1, "key2", element2));
		assertEquals(objectWithElements, JsonTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2)).orThrow());
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getEmpty(null));
		
		assertTrue(JsonTypeProvider.INSTANCE.getEmpty(new JsonArray()).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getEmpty(new JsonPrimitive(1)).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getEmpty(new JsonObject()).isError());
		
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.getEmpty(JsonNull.INSTANCE).orThrow());
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getBoolean(null));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getByte(null));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getShort(null));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getInteger(null));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getLong(null));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getFloat(null));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getDouble(null));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getString(null));
		
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
		
		assertTrue(JsonTypeProvider.INSTANCE.getBoolean(new JsonPrimitive(true)).orThrow());
		assertEquals((byte) 42, JsonTypeProvider.INSTANCE.getByte(new JsonPrimitive((byte) 42)).orThrow());
		assertEquals((short) 42, JsonTypeProvider.INSTANCE.getShort(new JsonPrimitive((short) 42)).orThrow());
		assertEquals(42, JsonTypeProvider.INSTANCE.getInteger(new JsonPrimitive(42)).orThrow());
		assertEquals(42L, JsonTypeProvider.INSTANCE.getLong(new JsonPrimitive(42L)).orThrow());
		assertEquals(42.5f, JsonTypeProvider.INSTANCE.getFloat(new JsonPrimitive(42.5f)).orThrow());
		assertEquals(42.5, JsonTypeProvider.INSTANCE.getDouble(new JsonPrimitive(42.5)).orThrow());
		assertEquals("test", JsonTypeProvider.INSTANCE.getString(new JsonPrimitive("test")).orThrow());
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getList(null));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getMap(null));
		
		JsonPrimitive wrongType = new JsonPrimitive(1);
		assertTrue(JsonTypeProvider.INSTANCE.getList(wrongType).isError());
		assertTrue(JsonTypeProvider.INSTANCE.getMap(wrongType).isError());
		
		JsonArray emptyArray = new JsonArray();
		assertTrue(JsonTypeProvider.INSTANCE.getList(emptyArray).orThrow().isEmpty());
		
		JsonArray arrayWithElements = new JsonArray(List.of(new JsonPrimitive("a"), new JsonPrimitive("b")));
		List<JsonElement> listResult = JsonTypeProvider.INSTANCE.getList(arrayWithElements).orThrow();
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsJsonPrimitive().getAsString());
		
		JsonObject emptyObject = new JsonObject();
		assertTrue(JsonTypeProvider.INSTANCE.getMap(emptyObject).orThrow().isEmpty());
		
		JsonObject objectWithElements = new JsonObject(Map.of("key", new JsonPrimitive("value")));
		Map<String, JsonElement> mapResult = JsonTypeProvider.INSTANCE.getMap(objectWithElements).orThrow();
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsJsonPrimitive().getAsString());
	}
	
	@Test
	void mapOperations() {
		JsonObject jsonObject = new JsonObject();
		JsonElement testValue = new JsonPrimitive("test");
		
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.has(null, "key"));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.has(jsonObject, null));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.get(null, "key"));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.get(jsonObject, null));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(null, "key", testValue));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(jsonObject, null, testValue));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(jsonObject, "key", (JsonElement) null));
		
		JsonArray wrongType = new JsonArray();
		assertTrue(JsonTypeProvider.INSTANCE.has(wrongType, "key").isError());
		assertTrue(JsonTypeProvider.INSTANCE.get(wrongType, "key").isError());
		assertTrue(JsonTypeProvider.INSTANCE.set(wrongType, "key", testValue).isError());
		
		assertFalse(JsonTypeProvider.INSTANCE.has(jsonObject, "key").orThrow());
		assertNull(JsonTypeProvider.INSTANCE.get(jsonObject, "key").orThrow());
		
		assertNull(JsonTypeProvider.INSTANCE.set(jsonObject, "key", testValue).orThrow());
		assertTrue(JsonTypeProvider.INSTANCE.has(jsonObject, "key").orThrow());
		assertEquals(testValue, JsonTypeProvider.INSTANCE.get(jsonObject, "key").orThrow());
	}
	
	@Test
	void mapOperationsWithResults() {
		JsonObject jsonObject = new JsonObject();
		JsonElement testValue = new JsonPrimitive("test");
		
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(null, "key", Result.success(testValue)));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(jsonObject, null, Result.success(testValue)));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(jsonObject, "key", (Result<JsonElement>) null));
		
		assertTrue(JsonTypeProvider.INSTANCE.set(jsonObject, "key", Result.error("error")).isError());
		assertTrue(JsonTypeProvider.INSTANCE.set(jsonObject, "key", Result.success(testValue)).isSuccess());
		assertEquals(testValue, jsonObject.get("key"));
	}
	
	@Test
	void mergeOperations() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.merge(null, JsonNull.INSTANCE));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, (JsonElement) null));
		
		JsonElement primitive = new JsonPrimitive(1);
		JsonArray array1 = new JsonArray(List.of(new JsonPrimitive("a")));
		JsonArray array2 = new JsonArray(List.of(new JsonPrimitive("b")));
		JsonObject object1 = new JsonObject(Map.of("key1", new JsonPrimitive("value1")));
		JsonObject object2 = new JsonObject(Map.of("key2", new JsonPrimitive("value2")));
		
		assertEquals(primitive, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, primitive).orThrow());
		assertEquals(array1, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, array1).orThrow());
		assertEquals(object1, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, object1).orThrow());
		
		JsonArray mergedArray = JsonTypeProvider.INSTANCE.merge(array1, array2).orThrow().getAsJsonArray();
		assertEquals(2, mergedArray.size());
		assertEquals("a", mergedArray.get(0).getAsJsonPrimitive().getAsString());
		assertEquals("b", mergedArray.get(1).getAsJsonPrimitive().getAsString());
		
		JsonObject mergedObject = JsonTypeProvider.INSTANCE.merge(object1, object2).orThrow().getAsJsonObject();
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
		
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.merge(null, Result.success(testValue)));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.merge(jsonObject, (Result<JsonElement>) null));
		
		assertTrue(JsonTypeProvider.INSTANCE.merge(jsonObject, Result.error("error")).isError());
		assertEquals(testValue, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, Result.success(testValue)).orThrow());
	}
}
