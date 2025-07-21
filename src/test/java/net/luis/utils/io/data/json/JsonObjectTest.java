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

package net.luis.utils.io.data.json;

import net.luis.utils.io.data.json.exception.JsonTypeException;
import net.luis.utils.io.data.json.exception.NoSuchJsonElementException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonObject}.<br>
 *
 * @author Luis-St
 */
class JsonObjectTest {
	
	private static final JsonConfig CUSTOM_CONFIG = new JsonConfig(true, true, "  ", true, 10, true, 2, StandardCharsets.UTF_8);
	
	@Test
	void constructorWithEmptyMap() {
		JsonObject object = new JsonObject();
		assertTrue(object.isEmpty());
		assertEquals(0, object.size());
	}
	
	@Test
	void constructorWithElementsMap() {
		Map<String, JsonElement> elements = Map.of(
			"key1", new JsonPrimitive("value1"),
			"key2", new JsonPrimitive(42),
			"key3", JsonNull.INSTANCE
		);
		
		JsonObject object = new JsonObject(elements);
		assertEquals(3, object.size());
		assertEquals(new JsonPrimitive("value1"), object.get("key1"));
		assertEquals(new JsonPrimitive(42), object.get("key2"));
		assertEquals(JsonNull.INSTANCE, object.get("key3"));
	}
	
	@Test
	void constructorWithNullMap() {
		assertThrows(NullPointerException.class, () -> new JsonObject(null));
	}
	
	@Test
	void jsonElementTypeChecks() {
		JsonObject object = new JsonObject();
		
		assertFalse(object.isJsonNull());
		assertTrue(object.isJsonObject());
		assertFalse(object.isJsonArray());
		assertFalse(object.isJsonPrimitive());
	}
	
	@Test
	void jsonElementConversions() {
		JsonObject object = new JsonObject();
		
		assertSame(object, object.getAsJsonObject());
		assertThrows(JsonTypeException.class, object::getAsJsonArray);
		assertThrows(JsonTypeException.class, object::getAsJsonPrimitive);
	}
	
	@Test
	void sizeAndEmptyOperations() {
		JsonObject object = new JsonObject();
		assertEquals(0, object.size());
		assertTrue(object.isEmpty());
		
		object.add("key1", new JsonPrimitive("value1"));
		assertEquals(1, object.size());
		assertFalse(object.isEmpty());
		
		object.add("key2", new JsonPrimitive(42));
		assertEquals(2, object.size());
		
		object.remove("key1");
		assertEquals(1, object.size());
		
		object.clear();
		assertEquals(0, object.size());
		assertTrue(object.isEmpty());
	}
	
	@Test
	void containsOperations() {
		JsonObject object = new JsonObject();
		JsonElement value = new JsonPrimitive("test");
		
		assertFalse(object.containsKey("key"));
		assertFalse(object.containsKey(null));
		assertFalse(object.containsValue(value));
		assertFalse(object.containsValue(null));
		
		object.add("key", value);
		assertTrue(object.containsKey("key"));
		assertTrue(object.containsValue(value));
		assertFalse(object.containsKey("other"));
		assertFalse(object.containsValue(new JsonPrimitive("different")));
		
		object.add("nullKey", (String) null);
		assertTrue(object.containsKey("nullKey"));
		assertTrue(object.containsValue(JsonNull.INSTANCE));
	}
	
	@Test
	void keySetAndValuesOperations() {
		JsonObject object = new JsonObject();
		
		assertEquals(Set.of(), object.keySet());
		assertTrue(object.elements().isEmpty());
		assertTrue(object.entrySet().isEmpty());
		
		object.add("key1", new JsonPrimitive("value1"));
		object.add("key2", new JsonPrimitive(42));
		object.add("key3", JsonNull.INSTANCE);
		
		Set<String> keys = object.keySet();
		assertEquals(Set.of("key1", "key2", "key3"), keys);
		
		assertEquals(3, object.elements().size());
		assertTrue(object.elements().contains(new JsonPrimitive("value1")));
		assertTrue(object.elements().contains(new JsonPrimitive(42)));
		assertTrue(object.elements().contains(JsonNull.INSTANCE));
		
		assertEquals(3, object.entrySet().size());
		assertTrue(object.entrySet().contains(Map.entry("key1", new JsonPrimitive("value1"))));
	}
	
	@Test
	void forEachOperation() {
		JsonObject object = new JsonObject();
		
		assertThrows(NullPointerException.class, () -> object.forEach(null));
		
		object.forEach((key, value) -> fail("Should not be called for empty object"));
		
		object.add("key1", new JsonPrimitive("value1"));
		object.add("key2", new JsonPrimitive(42));
		
		AtomicInteger callCount = new AtomicInteger(0);
		object.forEach((key, value) -> {
			callCount.incrementAndGet();
			assertTrue(Set.of("key1", "key2").contains(key));
			assertNotNull(value);
		});
		
		assertEquals(2, callCount.get());
	}
	
	@Test
	void addOperationsWithJsonElement() {
		JsonObject object = new JsonObject();
		
		assertThrows(NullPointerException.class, () -> object.add(null, new JsonPrimitive("value")));
		
		JsonElement previous = object.add("key1", (JsonElement) null);
		assertNull(previous);
		assertEquals(JsonNull.INSTANCE, object.get("key1"));
		
		previous = object.add("key1", new JsonPrimitive("newValue"));
		assertEquals(JsonNull.INSTANCE, previous);
		assertEquals(new JsonPrimitive("newValue"), object.get("key1"));
	}
	
	@Test
	void addOperationsWithPrimitiveTypes() {
		JsonObject object = new JsonObject();
		
		object.add("stringKey", "testString");
		assertEquals(new JsonPrimitive("testString"), object.get("stringKey"));
		
		object.add("nullStringKey", (String) null);
		assertEquals(JsonNull.INSTANCE, object.get("nullStringKey"));
		
		object.add("booleanKey", true);
		assertEquals(new JsonPrimitive(true), object.get("booleanKey"));
		
		object.add("numberKey", Integer.valueOf(42));
		assertEquals(new JsonPrimitive(42), object.get("numberKey"));
		
		object.add("nullNumberKey", (Number) null);
		assertEquals(JsonNull.INSTANCE, object.get("nullNumberKey"));
		
		object.add("byteKey", (byte) 1);
		assertEquals(new JsonPrimitive((byte) 1), object.get("byteKey"));
		
		object.add("shortKey", (short) 2);
		assertEquals(new JsonPrimitive((short) 2), object.get("shortKey"));
		
		object.add("intKey", 3);
		assertEquals(new JsonPrimitive(3), object.get("intKey"));
		
		object.add("longKey", 4L);
		assertEquals(new JsonPrimitive(4L), object.get("longKey"));
		
		object.add("floatKey", 5.5f);
		assertEquals(new JsonPrimitive(5.5f), object.get("floatKey"));
		
		object.add("doubleKey", 6.6);
		assertEquals(new JsonPrimitive(6.6), object.get("doubleKey"));
	}
	
	@Test
	void addAllOperations() {
		JsonObject object = new JsonObject();
		
		JsonObject other = new JsonObject();
		other.add("key1", new JsonPrimitive("value1"));
		other.add("key2", new JsonPrimitive(42));
		
		assertThrows(NullPointerException.class, () -> object.addAll((JsonObject) null));
		
		object.addAll(other);
		assertEquals(2, object.size());
		assertEquals(new JsonPrimitive("value1"), object.get("key1"));
		assertEquals(new JsonPrimitive(42), object.get("key2"));
		
		Map<String, JsonElement> map = new HashMap<>();
		map.put("key3", new JsonPrimitive("value3"));
		map.put("key4", JsonNull.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> object.addAll((Map<String, JsonElement>) null));
		
		object.addAll(map);
		assertEquals(4, object.size());
		assertEquals(new JsonPrimitive("value3"), object.get("key3"));
		assertEquals(JsonNull.INSTANCE, object.get("key4"));
	}
	
	@Test
	void removeOperations() {
		JsonObject object = new JsonObject();
		
		assertNull(object.remove("nonexistent"));
		assertNull(object.remove(null));
		
		object.add("key1", new JsonPrimitive("value1"));
		object.add("key2", new JsonPrimitive(42));
		object.add("key3", JsonNull.INSTANCE);
		
		JsonElement removed = object.remove("key1");
		assertEquals(new JsonPrimitive("value1"), removed);
		assertEquals(2, object.size());
		assertFalse(object.containsKey("key1"));
		
		assertNull(object.remove("nonexistent"));
		assertEquals(2, object.size());
		
		object.clear();
		assertEquals(0, object.size());
		assertTrue(object.isEmpty());
	}
	
	@Test
	void replaceOperations() {
		JsonObject object = new JsonObject();
		
		assertThrows(NullPointerException.class, () -> object.replace(null, new JsonPrimitive("value")));
		
		JsonElement result = object.replace("key", new JsonPrimitive("value"));
		assertNull(result);
		assertTrue(object.isEmpty());
		
		object.add("key1", new JsonPrimitive("oldValue"));
		
		result = object.replace("key1", new JsonPrimitive("newValue"));
		assertEquals(new JsonPrimitive("oldValue"), result);
		assertEquals(new JsonPrimitive("newValue"), object.get("key1"));
		
		result = object.replace("key1", (JsonElement) null);
		assertEquals(new JsonPrimitive("newValue"), result);
		assertEquals(JsonNull.INSTANCE, object.get("key1"));
		
		assertThrows(NullPointerException.class, () -> object.replace(null, JsonNull.INSTANCE, new JsonPrimitive("value")));
		assertThrows(NullPointerException.class, () -> object.replace("key1", null, new JsonPrimitive("value")));
		
		boolean replaced = object.replace("key1", JsonNull.INSTANCE, new JsonPrimitive("finalValue"));
		assertTrue(replaced);
		assertEquals(new JsonPrimitive("finalValue"), object.get("key1"));
		
		replaced = object.replace("key1", JsonNull.INSTANCE, new JsonPrimitive("shouldNotReplace"));
		assertFalse(replaced);
		assertEquals(new JsonPrimitive("finalValue"), object.get("key1"));
	}
	
	@Test
	void getOperations() {
		JsonObject object = new JsonObject();
		
		assertThrows(NullPointerException.class, () -> object.get(null));
		assertNull(object.get("nonexistent"));
		
		object.add("stringKey", new JsonPrimitive("stringValue"));
		object.add("numberKey", new JsonPrimitive(42));
		object.add("booleanKey", new JsonPrimitive(true));
		object.add("objectKey", new JsonObject());
		object.add("arrayKey", new JsonArray());
		object.add("nullKey", JsonNull.INSTANCE);
		
		assertEquals(new JsonPrimitive("stringValue"), object.get("stringKey"));
		assertEquals(new JsonPrimitive(42), object.get("numberKey"));
		assertEquals(new JsonObject(), object.get("objectKey"));
		assertEquals(new JsonArray(), object.get("arrayKey"));
		assertEquals(JsonNull.INSTANCE, object.get("nullKey"));
	}
	
	@Test
	void getAsSpecificTypesSuccess() {
		JsonObject object = new JsonObject();
		object.add("objectKey", new JsonObject());
		object.add("arrayKey", new JsonArray());
		object.add("stringKey", new JsonPrimitive("stringValue"));
		object.add("numberKey", new JsonPrimitive(42));
		object.add("booleanKey", new JsonPrimitive(true));
		object.add("doubleKey", new JsonPrimitive(3.14));
		
		assertEquals(new JsonObject(), object.getAsJsonObject("objectKey"));
		assertEquals(new JsonArray(), object.getAsJsonArray("arrayKey"));
		assertEquals(new JsonPrimitive("stringValue"), object.getJsonPrimitive("stringKey"));
		
		assertEquals("stringValue", object.getAsString("stringKey"));
		assertEquals(42L, object.getAsNumber("numberKey"));
		assertEquals(42, object.getAsInteger("numberKey"));
		assertEquals(42L, object.getAsLong("numberKey"));
		assertEquals(42.0f, object.getAsFloat("numberKey"));
		assertEquals(42.0, object.getAsDouble("numberKey"));
		assertEquals((byte) 42, object.getAsByte("numberKey"));
		assertEquals((short) 42, object.getAsShort("numberKey"));
		assertTrue(object.getAsBoolean("booleanKey"));
		assertEquals(3.14, object.getAsDouble("doubleKey"));
	}
	
	@Test
	void getAsSpecificTypesExceptions() {
		JsonObject object = new JsonObject();
		object.add("stringKey", new JsonPrimitive("stringValue"));
		object.add("nullKey", JsonNull.INSTANCE);
		
		assertThrows(NullPointerException.class, () -> object.getAsJsonObject(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsJsonObject("nonexistent"));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsJsonArray("nonexistent"));
		assertThrows(NoSuchJsonElementException.class, () -> object.getJsonPrimitive("nonexistent"));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsString("nonexistent"));
		
		assertThrows(JsonTypeException.class, () -> object.getAsJsonObject("stringKey"));
		assertThrows(JsonTypeException.class, () -> object.getAsJsonArray("stringKey"));
		assertThrows(JsonTypeException.class, () -> object.getJsonPrimitive("nullKey"));
		assertThrows(JsonTypeException.class, () -> object.getAsString("nullKey"));
		assertThrows(JsonTypeException.class, () -> object.getAsBoolean("nullKey"));
		assertThrows(JsonTypeException.class, () -> object.getAsNumber("nullKey"));
	}
	
	@Test
	void equalsAndHashCode() {
		JsonObject object1 = new JsonObject();
		JsonObject object2 = new JsonObject();
		JsonObject object3 = new JsonObject();
		
		assertEquals(object1, object2);
		assertEquals(object1.hashCode(), object2.hashCode());
		
		object1.add("key1", new JsonPrimitive("value1"));
		object1.add("key2", new JsonPrimitive(42));
		
		object2.add("key1", new JsonPrimitive("value1"));
		object2.add("key2", new JsonPrimitive(42));
		
		assertEquals(object1, object2);
		assertEquals(object1.hashCode(), object2.hashCode());
		
		object3.add("key1", new JsonPrimitive("differentValue"));
		object3.add("key2", new JsonPrimitive(42));
		
		assertNotEquals(object1, object3);
		assertNotEquals(object1, null);
		assertNotEquals(object1, "not an object");
		
		assertEquals(object1, object1);
		
		JsonObject object4 = new JsonObject();
		object4.add("key1", new JsonPrimitive("value1"));
		assertNotEquals(object1, object4);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		JsonObject object = new JsonObject();
		assertEquals("{}", object.toString());
		
		object.add("key", new JsonPrimitive("value"));
		assertEquals("{ \"key\": \"value\" }", object.toString());
		
		object.add("key2", new JsonPrimitive(42));
		String result = object.toString();
		assertTrue(result.contains(System.lineSeparator()));
		assertTrue(result.contains("\t"));
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
		assertTrue(result.contains("\"key\""));
		assertTrue(result.contains("\"value\""));
		assertTrue(result.contains("\"key2\""));
		assertTrue(result.contains("42"));
	}
	
	@Test
	void toStringWithCustomConfig() {
		JsonObject object = new JsonObject();
		
		assertThrows(NullPointerException.class, () -> object.toString(null));
		
		assertEquals("{}", object.toString(CUSTOM_CONFIG));
		
		object.add("key1", new JsonPrimitive("value1"));
		object.add("key2", new JsonPrimitive(42));
		assertEquals("{ \"key1\": \"value1\", \"key2\": 42 }", object.toString(CUSTOM_CONFIG));
		
		object.add("key3", new JsonPrimitive(true));
		String result = object.toString(CUSTOM_CONFIG);
		assertTrue(result.contains(System.lineSeparator()));
		assertTrue(result.contains("  "));
		assertTrue(result.startsWith("{"));
		assertTrue(result.endsWith("}"));
	}
	
	@Test
	void toStringWithNestedStructures() {
		JsonObject object = new JsonObject();
		
		JsonObject nestedObject = new JsonObject();
		nestedObject.add("nestedKey", new JsonPrimitive("nestedValue"));
		
		JsonArray nestedArray = new JsonArray();
		nestedArray.add(new JsonPrimitive(1));
		nestedArray.add(new JsonPrimitive(2));
		
		object.add("object", nestedObject);
		object.add("array", nestedArray);
		
		String result = object.toString();
		assertTrue(result.contains("\"nestedKey\""));
		assertTrue(result.contains("\"nestedValue\""));
		assertTrue(result.contains("[1, 2]"));
	}
	
	@Test
	void preservesInsertionOrder() {
		JsonObject object = new JsonObject();
		
		object.add("third", new JsonPrimitive(3));
		object.add("first", new JsonPrimitive(1));
		object.add("second", new JsonPrimitive(2));
		
		String[] expectedOrder = { "third", "first", "second" };
		String[] actualOrder = object.keySet().toArray(new String[0]);
		assertArrayEquals(expectedOrder, actualOrder);
		
		String result = object.toString();
		int thirdIndex = result.indexOf("\"third\"");
		int firstIndex = result.indexOf("\"first\"");
		int secondIndex = result.indexOf("\"second\"");
		
		assertTrue(thirdIndex < firstIndex);
		assertTrue(firstIndex < secondIndex);
	}
}
