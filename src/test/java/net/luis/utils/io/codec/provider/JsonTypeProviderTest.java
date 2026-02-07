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
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.createNull());
	}
	
	@Test
	void createPrimitiveTypes() {
		assertEquals(new JsonPrimitive(true), JsonTypeProvider.INSTANCE.createBoolean(true));
		assertEquals(new JsonPrimitive((byte) 42), JsonTypeProvider.INSTANCE.createByte((byte) 42));
		assertEquals(new JsonPrimitive((short) 42), JsonTypeProvider.INSTANCE.createShort((short) 42));
		assertEquals(new JsonPrimitive(42), JsonTypeProvider.INSTANCE.createInteger(42));
		assertEquals(new JsonPrimitive(42L), JsonTypeProvider.INSTANCE.createLong(42L));
		assertEquals(new JsonPrimitive(42.5f), JsonTypeProvider.INSTANCE.createFloat(42.5f));
		assertEquals(new JsonPrimitive(42.5), JsonTypeProvider.INSTANCE.createDouble(42.5));
		assertEquals(new JsonPrimitive("test"), JsonTypeProvider.INSTANCE.createString("test"));
	}
	
	@Test
	void createStringWithNullThrowsException() {
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.createString(null));
	}
	
	@Test
	void createCollectionTypes() {
		JsonElement element1 = new JsonPrimitive("a");
		JsonElement element2 = new JsonPrimitive("b");
		
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.createList(null));
		
		JsonArray emptyArray = new JsonArray();
		assertEquals(emptyArray, JsonTypeProvider.INSTANCE.createList(List.of()));
		
		JsonArray arrayWithElements = new JsonArray(List.of(element1, element2));
		assertEquals(arrayWithElements, JsonTypeProvider.INSTANCE.createList(List.of(element1, element2)));
		
		JsonObject emptyObject = new JsonObject();
		assertEquals(emptyObject, JsonTypeProvider.INSTANCE.createMap());
		assertEquals(emptyObject, JsonTypeProvider.INSTANCE.createMap(Map.of()));
		
		JsonObject objectWithElements = new JsonObject(Map.of("key1", element1, "key2", element2));
		assertEquals(objectWithElements, JsonTypeProvider.INSTANCE.createMap(Map.of("key1", element1, "key2", element2)));
	}
	
	@Test
	void getEmptyValidation() {
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.isEmpty(null));
		assertFalse(JsonTypeProvider.INSTANCE.isEmpty(new JsonArray()));
		assertFalse(JsonTypeProvider.INSTANCE.isEmpty(new JsonPrimitive(1)));
		assertFalse(JsonTypeProvider.INSTANCE.isEmpty(new JsonObject()));
		assertFalse(JsonTypeProvider.INSTANCE.isEmpty(JsonNull.INSTANCE));
	}
	
	@Test
	void isNullValidation() {
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.isNull(null));
		
		assertTrue(JsonTypeProvider.INSTANCE.isNull(JsonNull.INSTANCE));
		
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonArray()));
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonObject()));
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonPrimitive(1)));
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonPrimitive(true)));
		assertFalse(JsonTypeProvider.INSTANCE.isNull(new JsonPrimitive("test")));
	}
	
	@Test
	void getPrimitiveTypes() {
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getBoolean(null));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getByte(null));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getShort(null));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getInteger(null));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getLong(null));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getFloat(null));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getDouble(null));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getString(null));
		
		JsonArray wrongType = new JsonArray();
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getBoolean(wrongType));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getByte(wrongType));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getShort(wrongType));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getInteger(wrongType));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getLong(wrongType));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getFloat(wrongType));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getDouble(wrongType));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getString(wrongType));
		
		JsonPrimitive invalidValue = new JsonPrimitive("not-a-number");
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getBoolean(invalidValue));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getByte(invalidValue));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getShort(invalidValue));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getInteger(invalidValue));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getLong(invalidValue));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getFloat(invalidValue));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getDouble(invalidValue));
		
		assertTrue(JsonTypeProvider.INSTANCE.getBoolean(new JsonPrimitive(true)));
		assertEquals((byte) 42, JsonTypeProvider.INSTANCE.getByte(new JsonPrimitive((byte) 42)));
		assertEquals((short) 42, JsonTypeProvider.INSTANCE.getShort(new JsonPrimitive((short) 42)));
		assertEquals(42, JsonTypeProvider.INSTANCE.getInteger(new JsonPrimitive(42)));
		assertEquals(42L, JsonTypeProvider.INSTANCE.getLong(new JsonPrimitive(42L)));
		assertEquals(42.5f, JsonTypeProvider.INSTANCE.getFloat(new JsonPrimitive(42.5f)));
		assertEquals(42.5, JsonTypeProvider.INSTANCE.getDouble(new JsonPrimitive(42.5)));
		assertEquals("test", JsonTypeProvider.INSTANCE.getString(new JsonPrimitive("test")));
	}
	
	@Test
	void getCollectionTypes() {
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getList(null));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getMap(null));
		
		JsonPrimitive wrongType = new JsonPrimitive(1);
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getList(wrongType));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.getMap(wrongType));
		
		JsonArray emptyArray = new JsonArray();
		assertTrue(JsonTypeProvider.INSTANCE.getList(emptyArray).isEmpty());
		
		JsonArray arrayWithElements = new JsonArray(List.of(new JsonPrimitive("a"), new JsonPrimitive("b")));
		List<JsonElement> listResult = JsonTypeProvider.INSTANCE.getList(arrayWithElements);
		assertEquals(2, listResult.size());
		assertEquals("a", listResult.getFirst().getAsJsonPrimitive().getAsString());
		
		JsonObject emptyObject = new JsonObject();
		assertTrue(JsonTypeProvider.INSTANCE.getMap(emptyObject).isEmpty());
		
		JsonObject objectWithElements = new JsonObject(Map.of("key", new JsonPrimitive("value")));
		Map<String, JsonElement> mapResult = JsonTypeProvider.INSTANCE.getMap(objectWithElements);
		assertEquals(1, mapResult.size());
		assertEquals("value", mapResult.get("key").getAsJsonPrimitive().getAsString());
	}
	
	@Test
	void mapOperations() {
		JsonObject jsonObject = new JsonObject();
		JsonElement testValue = new JsonPrimitive("test");
		
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.has(null, "key"));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.has(jsonObject, null));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.get(null, "key"));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.get(jsonObject, null));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.set(null, "key", testValue));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.set(jsonObject, null, testValue));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.set(jsonObject, "key", null));
		
		JsonArray wrongType = new JsonArray();
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.has(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.get(wrongType, "key"));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.set(wrongType, "key", testValue));
		
		assertFalse(JsonTypeProvider.INSTANCE.has(jsonObject, "key"));
		
		JsonTypeProvider.INSTANCE.set(jsonObject, "key", testValue);
		assertTrue(JsonTypeProvider.INSTANCE.has(jsonObject, "key"));
		assertEquals(testValue, JsonTypeProvider.INSTANCE.get(jsonObject, "key"));
	}
	
	@Test
	void mergeOperations() {
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.merge(null, JsonNull.INSTANCE));
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, null));
		
		JsonElement primitive = new JsonPrimitive(1);
		JsonArray array1 = new JsonArray(List.of(new JsonPrimitive("a")));
		JsonArray array2 = new JsonArray(List.of(new JsonPrimitive("b")));
		JsonObject object1 = new JsonObject(Map.of("key1", new JsonPrimitive("value1")));
		JsonObject object2 = new JsonObject(Map.of("key2", new JsonPrimitive("value2")));
		
		assertEquals(primitive, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), primitive));
		assertEquals(array1, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), array1));
		assertEquals(object1, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), object1));
		
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.merge(JsonTypeProvider.INSTANCE.empty(), JsonNull.INSTANCE));
		
		assertEquals(primitive, JsonTypeProvider.INSTANCE.merge(primitive, JsonNull.INSTANCE));
		assertEquals(primitive, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, primitive));
		
		assertEquals(array1, JsonTypeProvider.INSTANCE.merge(array1, JsonNull.INSTANCE));
		assertEquals(array1, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, array1));
		
		assertEquals(object1, JsonTypeProvider.INSTANCE.merge(object1, JsonNull.INSTANCE));
		assertEquals(object1, JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, object1));
		
		JsonArray mergedArray = JsonTypeProvider.INSTANCE.merge(array1, array2).getAsJsonArray();
		assertEquals(2, mergedArray.size());
		assertEquals("a", mergedArray.get(0).getAsJsonPrimitive().getAsString());
		assertEquals("b", mergedArray.get(1).getAsJsonPrimitive().getAsString());
		
		JsonObject mergedObject = JsonTypeProvider.INSTANCE.merge(object1, object2).getAsJsonObject();
		assertEquals(2, mergedObject.size());
		assertEquals("value1", mergedObject.get("key1").getAsJsonPrimitive().getAsString());
		assertEquals("value2", mergedObject.get("key2").getAsJsonPrimitive().getAsString());
		
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.merge(primitive, array1));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.merge(primitive, object1));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.merge(array1, primitive));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.merge(array1, object1));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.merge(object1, primitive));
		assertThrows(TypeProviderException.class, () -> JsonTypeProvider.INSTANCE.merge(object1, array1));
	}
}
