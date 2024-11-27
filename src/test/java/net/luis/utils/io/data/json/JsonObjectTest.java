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

package net.luis.utils.io.data.json;

import net.luis.utils.io.data.json.exception.JsonTypeException;
import net.luis.utils.io.data.json.exception.NoSuchJsonElementException;
import net.luis.utils.util.ErrorAction;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonObject}.<br>
 *
 * @author Luis-St
 */
class JsonObjectTest {
	
	private static final JsonConfig CUSTOM_CONFIG = new JsonConfig(true, true, "  ", true, 10, true, 2, StandardCharsets.UTF_8, ErrorAction.THROW);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new JsonObject(null));
		assertDoesNotThrow(() -> new JsonObject(Map.of("key", JsonNull.INSTANCE)));
	}
	
	@Test
	void isJsonNull() {
		assertFalse(new JsonObject().isJsonNull());
	}
	
	@Test
	void isJsonObject() {
		assertTrue(new JsonObject().isJsonObject());
	}
	
	@Test
	void isJsonArray() {
		assertFalse(new JsonObject().isJsonArray());
	}
	
	@Test
	void isJsonPrimitive() {
		assertFalse(new JsonObject().isJsonPrimitive());
	}
	
	@Test
	void getAsJsonObject() {
		assertDoesNotThrow(() -> new JsonObject().getAsJsonObject());
	}
	
	@Test
	void getAsJsonArray() {
		assertThrows(JsonTypeException.class, () -> new JsonObject().getAsJsonArray());
	}
	
	@Test
	void getAsJsonPrimitive() {
		assertThrows(JsonTypeException.class, () -> new JsonObject().getAsJsonPrimitive());
	}
	
	@Test
	void size() {
		JsonObject object = new JsonObject();
		assertEquals(0, object.size());
		object.add("key", JsonNull.INSTANCE);
		assertEquals(1, object.size());
		object.remove("key");
		assertEquals(0, object.size());
	}
	
	@Test
	void isEmpty() {
		JsonObject object = new JsonObject();
		assertTrue(object.isEmpty());
		object.add("key", JsonNull.INSTANCE);
		assertFalse(object.isEmpty());
		object.remove("key");
		assertTrue(object.isEmpty());
	}
	
	@Test
	void containsKey() {
		JsonObject object = new JsonObject();
		assertFalse(object.containsKey("key"));
		object.add("key", JsonNull.INSTANCE);
		assertTrue(object.containsKey("key"));
	}
	
	@Test
	void containsValue() {
		JsonObject object = new JsonObject();
		assertFalse(object.containsValue(JsonNull.INSTANCE));
		object.add("key", JsonNull.INSTANCE);
		assertTrue(object.containsValue(JsonNull.INSTANCE));
	}
	
	@Test
	void keySet() {
		JsonObject object = new JsonObject();
		assertIterableEquals(Set.of(), object.keySet());
		object.add("key", JsonNull.INSTANCE);
		assertIterableEquals(Set.of("key"), object.keySet());
	}
	
	@Test
	void values() {
		JsonObject object = new JsonObject();
		assertIterableEquals(Set.of(), object.values());
		object.add("key", JsonNull.INSTANCE);
		assertIterableEquals(Set.of(JsonNull.INSTANCE), object.values());
	}
	
	@Test
	void entrySet() {
		JsonObject object = new JsonObject();
		assertIterableEquals(Set.of(), object.entrySet());
		object.add("key", JsonNull.INSTANCE);
		assertIterableEquals(Set.of(Map.entry("key", JsonNull.INSTANCE)), object.entrySet());
	}
	
	@Test
	void add() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.add(null, JsonNull.INSTANCE));
		assertDoesNotThrow(() -> object.add("key0", (JsonElement) null));
		object.add("key1", new JsonPrimitive(10));
		assertEquals(JsonNull.INSTANCE, object.get("key0"));
		assertEquals(new JsonPrimitive(10), object.get("key1"));
	}
	
	@Test
	void remove() {
		JsonObject object = new JsonObject();
		assertNull(assertDoesNotThrow(() -> object.remove(null)));
		object.add("key", JsonNull.INSTANCE);
		assertEquals(1, object.size());
		assertEquals(JsonNull.INSTANCE, object.remove("key"));
		assertEquals(0, object.size());
	}
	
	@Test
	void clear() {
		JsonObject object = new JsonObject();
		object.add("key", JsonNull.INSTANCE);
		assertEquals(1, object.size());
		object.clear();
		assertEquals(0, object.size());
	}
	
	@Test
	void replace() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.replace(null, JsonNull.INSTANCE));
		assertDoesNotThrow(() -> object.replace("key", (JsonElement) null));
		object.add("key", JsonNull.INSTANCE);
		assertEquals(JsonNull.INSTANCE, object.get("key"));
		object.replace("key", new JsonPrimitive(10));
		assertEquals(new JsonPrimitive(10), object.get("key"));
		object.replace("key", new JsonPrimitive(10), new JsonPrimitive(20));
		assertEquals(new JsonPrimitive(20), object.get("key"));
	}
	
	@Test
	void get() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.get(null));
		assertNull(object.get("key"));
		object.add("key0", new JsonPrimitive(10));
		object.add("key1", JsonNull.INSTANCE);
		assertEquals(new JsonPrimitive(10), object.get("key0"));
		assertEquals(JsonNull.INSTANCE, object.get("key1"));
	}
	
	@Test
	void getAsJsonObjectKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsJsonObject(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsJsonObject("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsJsonObject("key0"));
		object.add("key1", new JsonObject());
		assertEquals(new JsonObject(), assertDoesNotThrow(() -> object.getAsJsonObject("key1")));
	}
	
	@Test
	void getAsJsonArrayKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsJsonArray(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsJsonArray("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsJsonArray("key0"));
		object.add("key1", new JsonArray());
		assertEquals(new JsonArray(), assertDoesNotThrow(() -> object.getAsJsonArray("key1")));
	}
	
	@Test
	void getJsonPrimitiveKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getJsonPrimitive(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getJsonPrimitive("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getJsonPrimitive("key0"));
		object.add("key1", new JsonPrimitive(10));
		assertEquals(new JsonPrimitive(10), assertDoesNotThrow(() -> object.getJsonPrimitive("key1")));
	}
	
	@Test
	void getAsStringKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsString(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsString("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsString("key0"));
		object.add("key1", new JsonPrimitive("10"));
		assertEquals("10", assertDoesNotThrow(() -> object.getAsString("key1")));
	}
	
	@Test
	void getAsBooleanKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsBoolean(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsBoolean("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsBoolean("key0"));
		object.add("key1", new JsonPrimitive(true));
		assertTrue(assertDoesNotThrow(() -> object.getAsBoolean("key1")));
	}
	
	@Test
	void getAsNumberKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsNumber(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsNumber("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsNumber("key0"));
		object.add("key1", new JsonPrimitive(10));
		assertEquals(10, assertDoesNotThrow(() -> object.getAsNumber("key1")));
	}
	
	@Test
	void getAsByteKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsByte(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsByte("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsByte("key0"));
		object.add("key1", new JsonPrimitive(10));
		assertEquals((byte) 10, assertDoesNotThrow(() -> object.getAsByte("key1")));
	}
	
	@Test
	void getAsShortKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsShort(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsShort("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsShort("key0"));
		object.add("key1", new JsonPrimitive(10));
		assertEquals((short) 10, assertDoesNotThrow(() -> object.getAsShort("key1")));
	}
	
	@Test
	void getAsIntegerKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsInteger(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsInteger("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsInteger("key0"));
		object.add("key1", new JsonPrimitive(10));
		assertEquals(10, assertDoesNotThrow(() -> object.getAsInteger("key1")));
	}
	
	@Test
	void getAsLongKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsLong(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsLong("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsLong("key0"));
		object.add("key1", new JsonPrimitive(10));
		assertEquals(10L, assertDoesNotThrow(() -> object.getAsLong("key1")));
	}
	
	@Test
	void getAsFloatKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsFloat(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsFloat("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsFloat("key0"));
		object.add("key1", new JsonPrimitive(10));
		assertEquals(10.0F, assertDoesNotThrow(() -> object.getAsFloat("key1")));
	}
	
	@Test
	void getAsDoubleKeyed() {
		JsonObject object = new JsonObject();
		assertThrows(NullPointerException.class, () -> object.getAsDouble(null));
		assertThrows(NoSuchJsonElementException.class, () -> object.getAsDouble("key"));
		object.add("key0", JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> object.getAsDouble("key0"));
		object.add("key1", new JsonPrimitive(10));
		assertEquals(10.0, assertDoesNotThrow(() -> object.getAsDouble("key1")));
	}
	
	@Test
	void toStringDefaultConfig() {
		JsonObject object = new JsonObject();
		assertEquals("{}", object.toString());
		
		object.add("key0", JsonNull.INSTANCE);
		assertEquals("{ \"key0\": null }", object.toString());
		
		object.add("key1", new JsonPrimitive(10));
		assertEquals("{%n\t\"key0\": null,%n\t\"key1\": 10%n}".replace("%n", System.lineSeparator()), object.toString());
	}
	
	@Test
	void toStringCustomConfig() {
		JsonObject object = new JsonObject();
		assertEquals("{}", object.toString(CUSTOM_CONFIG));
		
		object.add("key0", JsonNull.INSTANCE);
		object.add("key1", new JsonPrimitive(10));
		
		//region Test setup
		String expected = "{" + System.lineSeparator() +
			"  \"key0\": null," + System.lineSeparator() +
			"  \"key1\": 10," + System.lineSeparator() +
			"  \"key2\": { \"key0\": true, \"key1\": 10 }" + System.lineSeparator() +
			"}";
		//endregion
		
		assertEquals("{ \"key0\": null, \"key1\": 10 }", object.toString(CUSTOM_CONFIG));
		
		JsonObject innerObject = new JsonObject();
		innerObject.add("key0", new JsonPrimitive(true));
		innerObject.add("key1", new JsonPrimitive(10));
		object.add("key2", innerObject);
		assertEquals(expected, object.toString(CUSTOM_CONFIG));
	}
}
