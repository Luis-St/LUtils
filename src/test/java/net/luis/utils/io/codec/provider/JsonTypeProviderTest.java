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
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.createNull(RuntimeException::new));
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new JsonPrimitive(true), JsonTypeProvider.INSTANCE.createBoolean(true, RuntimeException::new));
		assertEquals(new JsonPrimitive((byte) 42), JsonTypeProvider.INSTANCE.createByte((byte) 42, RuntimeException::new));
		assertEquals(new JsonPrimitive((short) 42), JsonTypeProvider.INSTANCE.createShort((short) 42, RuntimeException::new));
		assertEquals(new JsonPrimitive(42), JsonTypeProvider.INSTANCE.createInteger(42, RuntimeException::new));
		assertEquals(new JsonPrimitive(42L), JsonTypeProvider.INSTANCE.createLong(42L, RuntimeException::new));
		assertEquals(new JsonPrimitive(42.5f), JsonTypeProvider.INSTANCE.createFloat(42.5f, RuntimeException::new));
		assertEquals(new JsonPrimitive(42.5), JsonTypeProvider.INSTANCE.createDouble(42.5, RuntimeException::new));
		assertEquals(new JsonPrimitive("test"), JsonTypeProvider.INSTANCE.createString("test", RuntimeException::new));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.createString(null, RuntimeException::new));
	}
	
	@Test
	void createCollectionTypes() {
		JsonElement element1 = new JsonPrimitive("a");
		JsonElement element2 = new JsonPrimitive("b");
		
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.createList(null, RuntimeException::new));
		
		JsonArray emptyArray = new JsonArray();
		assertEquals(emptyArray, JsonTypeProvider.INSTANCE.createList(List.of(), RuntimeException::new));
		
		JsonArray arrayWithElements = new JsonArray(List.of(element1, element2));
		assertEquals(arrayWithElements, JsonTypeProvider.INSTANCE.createList(List.of(element1, element2), RuntimeException::new));
		
		JsonObject emptyObject = new JsonObject();
		assertEquals(emptyObject, JsonTypeProvider.INSTANCE.createMap(RuntimeException::new));
		assertEquals(emptyObject, JsonTypeProvider.INSTANCE.createMap(Map.of(), RuntimeException::new));
		
		JsonObject objectWithElements = new JsonObject(Map.of("key1", element1, "key2", element2));
		assertEquals(objectWithElements, JsonTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2), RuntimeException::new));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.isEmpty(null, RuntimeException::new));
		assertFalse(JsonTypeProvider.INSTANCE.isEmpty(new JsonArray(), RuntimeException::new));
		assertFalse(JsonTypeProvider.INSTANCE.isEmpty(new JsonPrimitive(1), RuntimeException::new));
		assertFalse(JsonTypeProvider.INSTANCE.isEmpty(new JsonObject(), RuntimeException::new));
		assertFalse(JsonTypeProvider.INSTANCE.isEmpty(JsonNull.INSTANCE, RuntimeException::new));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.isNull(null, RuntimeException::new));
		
		assertTrue(JsonTypeProvider.INSTANCE.isNull(JsonNull.INSTANCE, RuntimeException::new));
		
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonArray(), RuntimeException::new));
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonObject(), RuntimeException::new));
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonPrimitive(1), RuntimeException::new));
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonPrimitive(true), RuntimeException::new));
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonPrimitive("test"), RuntimeException::new));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getBoolean(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getByte(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getShort(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getInteger(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getLong(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getFloat(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getDouble(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getString(null, RuntimeException::new));
		
		JsonArray wrongType = new JsonArray();
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getBoolean(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getByte(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getShort(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getInteger(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getLong(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getFloat(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getDouble(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getString(wrongType, RuntimeException::new));
		
		JsonPrimitive invalidValue = new JsonPrimitive("not-a-number");
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getBoolean(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getByte(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getShort(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getInteger(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getLong(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getFloat(invalidValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getDouble(invalidValue, RuntimeException::new));
		
		assertTrue(JsonTypeProvider.INSTANCE.getBoolean(new JsonPrimitive(true), RuntimeException::new));
		assertEquals((byte) 42, JsonTypeProvider.INSTANCE.getByte(new JsonPrimitive((byte) 42), RuntimeException::new));
		assertEquals((short) 42, JsonTypeProvider.INSTANCE.getShort(new JsonPrimitive((short) 42), RuntimeException::new));
		assertEquals(42, JsonTypeProvider.INSTANCE.getInteger(new JsonPrimitive(42), RuntimeException::new));
		assertEquals(42L, JsonTypeProvider.INSTANCE.getLong(new JsonPrimitive(42L), RuntimeException::new));
		assertEquals(42.5f, JsonTypeProvider.INSTANCE.getFloat(new JsonPrimitive(42.5f), RuntimeException::new));
		assertEquals(42.5, JsonTypeProvider.INSTANCE.getDouble(new JsonPrimitive(42.5), RuntimeException::new));
		assertEquals("test", JsonTypeProvider.INSTANCE.getString(new JsonPrimitive("test"), RuntimeException::new));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getList(null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getMap(null, RuntimeException::new));
		
		JsonPrimitive wrongType = new JsonPrimitive(1);
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getList(wrongType, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.getMap(wrongType, RuntimeException::new));
		
		JsonArray emptyArray = new JsonArray();
		assertTrue(JsonTypeProvider.INSTANCE.getList(emptyArray, RuntimeException::new).isEmpty());
		
		JsonArray arrayWithElements = new JsonArray(List.of(new JsonPrimitive("a"), new JsonPrimitive("b")));
		List<JsonElement> listResult = JsonTypeProvider.INSTANCE.getList(arrayWithElements, RuntimeException::new);
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsJsonPrimitive().getAsString());
		
		JsonObject emptyObject = new JsonObject();
		assertTrue(JsonTypeProvider.INSTANCE.getMap(emptyObject, RuntimeException::new).isEmpty());
		
		JsonObject objectWithElements = new JsonObject(Map.of("key", new JsonPrimitive("value")));
		Map<String, JsonElement> mapResult = JsonTypeProvider.INSTANCE.getMap(objectWithElements, RuntimeException::new);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsJsonPrimitive().getAsString());
	}
	
	@Test
	void mapOperations() {
		JsonObject jsonObject = new JsonObject();
		JsonElement testValue = new JsonPrimitive("test");
		
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.has(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.has(jsonObject, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.get(null, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.get(jsonObject, null, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.set(null, "key", testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.set(jsonObject, null, testValue, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.set(jsonObject, "key", null, RuntimeException::new));
		
		JsonArray wrongType = new JsonArray();
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.has(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.get(wrongType, "key", RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.set(wrongType, "key", testValue, RuntimeException::new));
		
		assertFalse(JsonTypeProvider.INSTANCE.has(jsonObject, "key", RuntimeException::new));
		
		JsonTypeProvider.INSTANCE.set(jsonObject, "key", testValue, RuntimeException::new);
		assertTrue(JsonTypeProvider.INSTANCE.has(jsonObject, "key", RuntimeException::new));
		assertEquals(testValue, JsonTypeProvider.INSTANCE.get(jsonObject, "key", RuntimeException::new));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.merge(null, JsonNull.INSTANCE, RuntimeException::new));
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, null, RuntimeException::new));
		
		JsonElement primitive = new JsonPrimitive(1);
		JsonArray array1 = new JsonArray(List.of(new JsonPrimitive("a")));
		JsonArray array2 = new JsonArray(List.of(new JsonPrimitive("b")));
		JsonObject object1 = new JsonObject(Map.of("key1", new JsonPrimitive("value1")));
		JsonObject object2 = new JsonObject(Map.of("key2", new JsonPrimitive("value2")));
		
		assertEquals(primitive, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), primitive, RuntimeException::new));
		assertEquals(array1, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), array1, RuntimeException::new));
		assertEquals(object1, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), object1, RuntimeException::new));
		
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), JsonNull.INSTANCE, RuntimeException::new));
		
		assertEquals(primitive, JsonTypeProvider.INSTANCE.merge(primitive, JsonNull.INSTANCE, RuntimeException::new));
		assertEquals(primitive, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, primitive, RuntimeException::new));
		
		assertEquals(array1, JsonTypeProvider.INSTANCE.merge(array1, JsonNull.INSTANCE, RuntimeException::new));
		assertEquals(array1, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, array1, RuntimeException::new));
		
		assertEquals(object1, JsonTypeProvider.INSTANCE.merge(object1, JsonNull.INSTANCE, RuntimeException::new));
		assertEquals(object1, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, object1, RuntimeException::new));
		
		JsonArray mergedArray = JsonTypeProvider.INSTANCE.merge(array1, array2, RuntimeException::new).getAsJsonArray();
		assertEquals(2, mergedArray.size());
		assertEquals("a", mergedArray.get(0).getAsJsonPrimitive().getAsString());
		assertEquals("b", mergedArray.get(1).getAsJsonPrimitive().getAsString());
		
		JsonObject mergedObject = JsonTypeProvider.INSTANCE.merge(object1, object2, RuntimeException::new).getAsJsonObject();
		assertEquals(2, mergedObject.size());
		assertEquals("value1", mergedObject.get("key1").getAsJsonPrimitive().getAsString());
		assertEquals("value2", mergedObject.get("key2").getAsJsonPrimitive().getAsString());
		
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.merge(primitive, array1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.merge(primitive, object1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.merge(array1, primitive, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.merge(array1, object1, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.merge(object1, primitive, RuntimeException::new));
		assertThrows(RuntimeException.class, () -> JsonTypeProvider.INSTANCE.merge(object1, array1, RuntimeException::new));
	}
}
