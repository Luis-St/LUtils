/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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
	void empty() {
		assertEquals(JsonNull.INSTANCE, JsonTypeProvider.INSTANCE.empty());
	}
	
	@Test
	void createBoolean() {
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.createBoolean(true);
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonPrimitive.class, result.orThrow());
		assertEquals(new JsonPrimitive(true), result.orThrow());
	}
	
	@Test
	void createByte() {
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.createByte((byte) 1);
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonPrimitive.class, result.orThrow());
		assertEquals(new JsonPrimitive((byte) 1), result.orThrow());
	}
	
	@Test
	void createShort() {
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.createShort((short) 1);
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonPrimitive.class, result.orThrow());
		assertEquals(new JsonPrimitive((short) 1), result.orThrow());
	}
	
	@Test
	void createInteger() {
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.createInteger(1);
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonPrimitive.class, result.orThrow());
		assertEquals(new JsonPrimitive(1), result.orThrow());
	}
	
	@Test
	void createLong() {
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.createLong(1L);
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonPrimitive.class, result.orThrow());
		assertEquals(new JsonPrimitive(1L), result.orThrow());
	}
	
	@Test
	void createFloat() {
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.createFloat(1.0F);
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonPrimitive.class, result.orThrow());
		assertEquals(new JsonPrimitive(1.0F), result.orThrow());
	}
	
	@Test
	void createDouble() {
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.createDouble(1.0);
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonPrimitive.class, result.orThrow());
		assertEquals(new JsonPrimitive(1.0), result.orThrow());
	}
	
	@Test
	void createString() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.createString(null));
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.createString("test");
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonPrimitive.class, result.orThrow());
		assertEquals(new JsonPrimitive("test"), result.orThrow());
	}
	
	@Test
	void createList() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.createList(null));
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.createList(List.of(JsonNull.INSTANCE));
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonArray.class, result.orThrow());
		assertEquals(1, result.orThrow().getAsJsonArray().size());
		assertEquals(JsonNull.INSTANCE, result.orThrow().getAsJsonArray().get(0));
	}
	
	@Test
	void createMap() {
		Result<JsonElement> emptyResult = JsonTypeProvider.INSTANCE.createMap();
		assertTrue(emptyResult.isSuccess());
		assertInstanceOf(JsonObject.class, emptyResult.orThrow());
		assertEquals(new JsonObject(), emptyResult.orThrow());
		
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.createMap(Map.of("test", JsonNull.INSTANCE));
		assertTrue(result.isSuccess());
		assertInstanceOf(JsonObject.class, result.orThrow());
		assertEquals(1, result.orThrow().getAsJsonObject().size());
		assertTrue(result.orThrow().getAsJsonObject().containsKey("test"));
		assertEquals(JsonNull.INSTANCE, result.orThrow().getAsJsonObject().get("test"));
	}
	
	@Test
	void getEmpty() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getEmpty(null));
		
		Result<JsonElement> invalidTypeResult = JsonTypeProvider.INSTANCE.getEmpty(new JsonArray());
		assertTrue(invalidTypeResult.isError());
		
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.getEmpty(JsonNull.INSTANCE);
		assertTrue(result.isSuccess());
		assertEquals(JsonNull.INSTANCE, result.orThrow());
	}
	
	@Test
	void getBoolean() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getBoolean(null));
		
		Result<Boolean> invalidTypeResult = JsonTypeProvider.INSTANCE.getBoolean(new JsonArray());
		assertTrue(invalidTypeResult.isError());
		
		Result<Boolean> invalidValueResult = JsonTypeProvider.INSTANCE.getBoolean(new JsonPrimitive("test"));
		assertTrue(invalidValueResult.isError());
		
		Result<Boolean> result = JsonTypeProvider.INSTANCE.getBoolean(new JsonPrimitive(true));
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow());
	}
	
	@Test
	void getByte() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getByte(null));
		
		Result<Byte> invalidTypeResult = JsonTypeProvider.INSTANCE.getByte(new JsonArray());
		assertTrue(invalidTypeResult.isError());
		
		Result<Byte> invalidValueResult = JsonTypeProvider.INSTANCE.getByte(new JsonPrimitive("test"));
		assertTrue(invalidValueResult.isError());
		
		Result<Byte> result = JsonTypeProvider.INSTANCE.getByte(new JsonPrimitive((byte) 1));
		assertTrue(result.isSuccess());
		assertEquals((byte) 1, result.orThrow());
	}
	
	@Test
	void getShort() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getShort(null));
		
		Result<Short> invalidTypeResult = JsonTypeProvider.INSTANCE.getShort(new JsonArray());
		assertTrue(invalidTypeResult.isError());
		
		Result<Short> invalidValueResult = JsonTypeProvider.INSTANCE.getShort(new JsonPrimitive("test"));
		assertTrue(invalidValueResult.isError());
		
		Result<Short> result = JsonTypeProvider.INSTANCE.getShort(new JsonPrimitive((short) 1));
		assertTrue(result.isSuccess());
		assertEquals((short) 1, result.orThrow());
	}
	
	@Test
	void getInteger() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getInteger(null));
		
		Result<Integer> invalidTypeResult = JsonTypeProvider.INSTANCE.getInteger(new JsonArray());
		assertTrue(invalidTypeResult.isError());
		
		Result<Integer> invalidValueResult = JsonTypeProvider.INSTANCE.getInteger(new JsonPrimitive("test"));
		assertTrue(invalidValueResult.isError());
		
		Result<Integer> result = JsonTypeProvider.INSTANCE.getInteger(new JsonPrimitive(1));
		assertTrue(result.isSuccess());
		assertEquals(1, result.orThrow());
	}
	
	@Test
	void getLong() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getLong(null));
		
		Result<Long> invalidTypeResult = JsonTypeProvider.INSTANCE.getLong(new JsonArray());
		assertTrue(invalidTypeResult.isError());
		
		Result<Long> invalidValueResult = JsonTypeProvider.INSTANCE.getLong(new JsonPrimitive("test"));
		assertTrue(invalidValueResult.isError());
		
		Result<Long> result = JsonTypeProvider.INSTANCE.getLong(new JsonPrimitive(1L));
		assertTrue(result.isSuccess());
		assertEquals(1L, result.orThrow());
	}
	
	@Test
	void getFloat() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getFloat(null));
		
		Result<Float> invalidTypeResult = JsonTypeProvider.INSTANCE.getFloat(new JsonArray());
		assertTrue(invalidTypeResult.isError());
		
		Result<Float> invalidValueResult = JsonTypeProvider.INSTANCE.getFloat(new JsonPrimitive("test"));
		assertTrue(invalidValueResult.isError());
		
		Result<Float> result = JsonTypeProvider.INSTANCE.getFloat(new JsonPrimitive(1.0F));
		assertTrue(result.isSuccess());
		assertEquals(1.0F, result.orThrow());
	}
	
	@Test
	void getDouble() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getDouble(null));
		
		Result<Double> invalidTypeResult = JsonTypeProvider.INSTANCE.getDouble(new JsonArray());
		assertTrue(invalidTypeResult.isError());
		
		Result<Double> invalidValueResult = JsonTypeProvider.INSTANCE.getDouble(new JsonPrimitive("test"));
		assertTrue(invalidValueResult.isError());
		
		Result<Double> result = JsonTypeProvider.INSTANCE.getDouble(new JsonPrimitive(1.0));
		assertTrue(result.isSuccess());
		assertEquals(1.0, result.orThrow());
	}
	
	@Test
	void getString() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getString(null));
		
		Result<String> invalidTypeResult = JsonTypeProvider.INSTANCE.getString(new JsonArray());
		assertTrue(invalidTypeResult.isError());
		
		Result<String> result = JsonTypeProvider.INSTANCE.getString(new JsonPrimitive("test"));
		assertTrue(result.isSuccess());
		assertEquals("test", result.orThrow());
	}
	
	@Test
	void getList() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getList(null));
		
		Result<List<JsonElement>> invalidTypeResult = JsonTypeProvider.INSTANCE.getList(new JsonObject());
		assertTrue(invalidTypeResult.isError());
		
		Result<List<JsonElement>> invalidValueResult = JsonTypeProvider.INSTANCE.getList(new JsonPrimitive(1));
		assertTrue(invalidValueResult.isError());
		
		Result<List<JsonElement>> result = JsonTypeProvider.INSTANCE.getList(new JsonArray());
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isEmpty());
	}
	
	@Test
	void getMap() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.getMap(null));
		
		Result<Map<String, JsonElement>> invalidTypeResult = JsonTypeProvider.INSTANCE.getMap(new JsonArray());
		assertTrue(invalidTypeResult.isError());
		
		Result<Map<String, JsonElement>> invalidValueResult = JsonTypeProvider.INSTANCE.getMap(new JsonPrimitive(1));
		assertTrue(invalidValueResult.isError());
		
		Result<Map<String, JsonElement>> result = JsonTypeProvider.INSTANCE.getMap(new JsonObject());
		assertTrue(result.isSuccess());
		assertTrue(result.orThrow().isEmpty());
	}
	
	@Test
	void has() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.has(null, "test"));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.has(new JsonObject(), null));
		
		Result<Boolean> invalidTypeResult = JsonTypeProvider.INSTANCE.has(new JsonArray(), "test");
		assertTrue(invalidTypeResult.isError());
		
		Result<Boolean> falseResult = JsonTypeProvider.INSTANCE.has(new JsonObject(), "test");
		assertTrue(falseResult.isSuccess());
		assertFalse(falseResult.orThrow());
		
		Result<Boolean> trueResult = JsonTypeProvider.INSTANCE.has(new JsonObject(Map.of("test", JsonNull.INSTANCE)), "test");
		assertTrue(trueResult.isSuccess());
		assertTrue(trueResult.orThrow());
	}
	
	@Test
	void get() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.get(null, "test"));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.get(new JsonObject(), null));
		
		Result<JsonElement> invalidTypeResult = JsonTypeProvider.INSTANCE.get(new JsonArray(), "test");
		assertTrue(invalidTypeResult.isError());
		
		Result<JsonElement> nullResult = JsonTypeProvider.INSTANCE.get(new JsonObject(), "test");
		assertTrue(nullResult.isSuccess());
		assertNull(nullResult.orThrow());
		
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.get(new JsonObject(Map.of("test", JsonNull.INSTANCE)), "test");
		assertTrue(result.isSuccess());
		assertEquals(JsonNull.INSTANCE, result.orThrow());
	}
	
	@Test
	void set() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(null, "test1", JsonNull.INSTANCE));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(new JsonObject(), null, JsonNull.INSTANCE));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(new JsonObject(), "test1", (JsonElement) null));
		
		Result<JsonElement> invalidTypeResult = JsonTypeProvider.INSTANCE.set(new JsonArray(), "test1", JsonNull.INSTANCE);
		assertTrue(invalidTypeResult.isError());
		
		JsonObject map = new JsonObject();
		Result<JsonElement> result = JsonTypeProvider.INSTANCE.set(map, "test1", JsonNull.INSTANCE);
		assertTrue(result.isSuccess());
		assertNull(result.orThrow());
		assertEquals(JsonNull.INSTANCE, map.get("test1"));
		
		
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(null, "test2", Result.success(JsonNull.INSTANCE)));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(new JsonObject(), null, Result.success(JsonNull.INSTANCE)));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.set(new JsonObject(), "test2", (Result<JsonElement>) null));
		
		Result<JsonElement> errorResult = JsonTypeProvider.INSTANCE.set(new JsonObject(), "test2", Result.error("test"));
		assertTrue(errorResult.isError());
		
		Result<JsonElement> successResult = JsonTypeProvider.INSTANCE.set(map, "test2", Result.success(JsonNull.INSTANCE));
		assertTrue(successResult.isSuccess());
		assertNull(successResult.orThrow());
		assertEquals(JsonNull.INSTANCE, map.get("test2"));
	}
	
	@Test
	void merge() {
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.merge(null, JsonNull.INSTANCE));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.merge(new JsonObject(), (JsonElement) null));
		
		// Null base
		Result<JsonElement> nullNullResult = JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, JsonNull.INSTANCE);
		assertTrue(nullNullResult.isSuccess());
		assertEquals(JsonNull.INSTANCE, nullNullResult.orThrow());
		
		Result<JsonElement> nullPrimitiveResult = JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, new JsonPrimitive(1));
		assertTrue(nullPrimitiveResult.isSuccess());
		assertEquals(new JsonPrimitive(1), nullPrimitiveResult.orThrow());
		
		Result<JsonElement> nullArrayResult = JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, new JsonArray());
		assertTrue(nullArrayResult.isSuccess());
		assertEquals(new JsonArray(), nullArrayResult.orThrow());
		
		Result<JsonElement> nullObjectResult = JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, new JsonObject());
		assertTrue(nullObjectResult.isSuccess());
		assertEquals(new JsonObject(), nullObjectResult.orThrow());
		
		// Primitive base
		Result<JsonElement> primitiveNullResult = JsonTypeProvider.INSTANCE.merge(new JsonPrimitive(1), JsonNull.INSTANCE);
		assertTrue(primitiveNullResult.isError());
		
		Result<JsonElement> primitivePrimitiveResult = JsonTypeProvider.INSTANCE.merge(new JsonPrimitive(1), new JsonPrimitive(2));
		assertTrue(primitivePrimitiveResult.isError());
		
		Result<JsonElement> primitiveArrayResult = JsonTypeProvider.INSTANCE.merge(new JsonPrimitive(1), new JsonArray());
		assertTrue(primitiveArrayResult.isError());
		
		Result<JsonElement> primitiveObjectResult = JsonTypeProvider.INSTANCE.merge(new JsonPrimitive(1), new JsonObject());
		assertTrue(primitiveObjectResult.isError());
		
		// Array base
		Result<JsonElement> arrayNullResult = JsonTypeProvider.INSTANCE.merge(new JsonArray(), JsonNull.INSTANCE);
		assertTrue(arrayNullResult.isError());
		
		Result<JsonElement> arrayPrimitiveResult = JsonTypeProvider.INSTANCE.merge(new JsonArray(), new JsonPrimitive(1));
		assertTrue(arrayPrimitiveResult.isError());
		
		Result<JsonElement> arrayArrayResult = JsonTypeProvider.INSTANCE.merge(new JsonArray(), new JsonArray());
		assertTrue(arrayArrayResult.isSuccess());
		assertEquals(new JsonArray(), arrayArrayResult.orThrow());
		
		Result<JsonElement> arrayObjectResult = JsonTypeProvider.INSTANCE.merge(new JsonArray(), new JsonObject());
		assertTrue(arrayObjectResult.isError());
		
		// Object base
		Result<JsonElement> objectNullResult = JsonTypeProvider.INSTANCE.merge(new JsonObject(), JsonNull.INSTANCE);
		assertTrue(objectNullResult.isError());
		
		Result<JsonElement> objectPrimitiveResult = JsonTypeProvider.INSTANCE.merge(new JsonObject(), new JsonPrimitive(1));
		assertTrue(objectPrimitiveResult.isError());
		
		Result<JsonElement> objectArrayResult = JsonTypeProvider.INSTANCE.merge(new JsonObject(), new JsonArray());
		assertTrue(objectArrayResult.isError());
		
		Result<JsonElement> objectObjectResult = JsonTypeProvider.INSTANCE.merge(new JsonObject(), new JsonObject());
		assertTrue(objectObjectResult.isSuccess());
		assertEquals(new JsonObject(), objectObjectResult.orThrow());
		
		
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.merge(null, Result.success(JsonNull.INSTANCE)));
		assertThrows(NullPointerException.class, () -> JsonTypeProvider.INSTANCE.merge(new JsonObject(), (Result<JsonElement>) null));
		
		Result<JsonElement> errorResult = JsonTypeProvider.INSTANCE.merge(new JsonObject(), Result.error("test"));
		assertTrue(errorResult.isError());
		
		Result<JsonElement> successResult = JsonTypeProvider.INSTANCE.merge(JsonNull.INSTANCE, Result.success(new JsonPrimitive(1)));
		assertTrue(successResult.isSuccess());
		assertEquals(new JsonPrimitive(1), successResult.orThrow());
	}
}
