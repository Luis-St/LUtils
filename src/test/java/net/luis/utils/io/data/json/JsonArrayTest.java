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

import net.luis.utils.io.data.json.exception.JsonArrayIndexOutOfBoundsException;
import net.luis.utils.io.data.json.exception.JsonTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonArray}.<br>
 *
 * @author Luis-St
 */
class JsonArrayTest {
	
	private static final JsonConfig CUSTOM_CONFIG = new JsonConfig(true, true, "  ", true, 5, true, 1, StandardCharsets.UTF_8);
	
	@Test
	void constructor() {
		assertThrows(NullPointerException.class, () -> new JsonArray(null));
		assertDoesNotThrow(() -> new JsonArray(List.of()));
		assertDoesNotThrow(() -> new JsonArray(List.of(JsonNull.INSTANCE)));
	}
	
	@Test
	void isJsonNull() {
		assertFalse(new JsonArray().isJsonNull());
	}
	
	@Test
	void isJsonObject() {
		assertFalse(new JsonArray().isJsonObject());
	}
	
	@Test
	void isJsonArray() {
		assertTrue(new JsonArray().isJsonArray());
	}
	
	@Test
	void isJsonPrimitive() {
		assertFalse(new JsonArray().isJsonPrimitive());
	}
	
	@Test
	void getAsJsonObject() {
		assertThrows(JsonTypeException.class, () -> new JsonArray().getAsJsonObject());
	}
	
	@Test
	void getAsJsonArray() {
		assertDoesNotThrow(() -> new JsonArray().getAsJsonArray());
	}
	
	@Test
	void getAsJsonPrimitive() {
		assertThrows(JsonTypeException.class, () -> new JsonArray().getAsJsonPrimitive());
	}
	
	@Test
	void size() {
		JsonArray array = new JsonArray();
		assertEquals(0, array.size());
		array.add(new JsonPrimitive(10));
		assertEquals(1, array.size());
		array.remove(0);
		assertEquals(0, array.size());
	}
	
	@Test
	void isEmpty() {
		JsonArray array = new JsonArray();
		assertTrue(array.isEmpty());
		array.add(JsonNull.INSTANCE);
		assertFalse(array.isEmpty());
		array.remove(0);
		assertTrue(array.isEmpty());
	}
	
	@Test
	void contains() {
		JsonArray array = new JsonArray();
		assertFalse(array.contains(JsonNull.INSTANCE));
		array.add(JsonNull.INSTANCE);
		assertTrue(array.contains(JsonNull.INSTANCE));
		array.remove(JsonNull.INSTANCE);
		assertFalse(array.contains(JsonNull.INSTANCE));
	}
	
	@Test
	void iterator() {
		assertEquals(JsonNull.INSTANCE, new JsonArray(List.of(JsonNull.INSTANCE)).iterator().next());
	}
	
	@Test
	void elements() {
		JsonArray array = new JsonArray();
		assertIterableEquals(List.of(), array.elements());
		array.add(JsonNull.INSTANCE);
		array.add(new JsonPrimitive(10));
		assertIterableEquals(List.of(JsonNull.INSTANCE, new JsonPrimitive(10)), array.elements());
	}
	
	@Test
	void getElements() {
		JsonArray array = new JsonArray();
		assertIterableEquals(List.of(), array.getElements());
		array.add(JsonNull.INSTANCE);
		array.add(new JsonPrimitive(10));
		assertIterableEquals(List.of(JsonNull.INSTANCE, new JsonPrimitive(10)), array.getElements());
	}
	
	@Test
	void set() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.set(0, JsonNull.INSTANCE));
		array.add(JsonNull.INSTANCE);
		assertEquals(JsonNull.INSTANCE, array.set(0, new JsonPrimitive(10)));
		assertEquals(new JsonPrimitive(10), array.get(0));
		assertEquals(new JsonPrimitive(10), assertDoesNotThrow(() -> array.set(0, (JsonElement) null)));
		assertEquals(JsonNull.INSTANCE, array.get(0));
	}
	
	@Test
	void add() {
		JsonArray array = new JsonArray();
		assertDoesNotThrow(() -> array.add((JsonElement) null));
		array.add(new JsonPrimitive(10));
		assertEquals(new JsonPrimitive(10), array.get(1));
		array.remove(1);
		assertEquals(JsonNull.INSTANCE, array.get(0));
	}
	
	@Test
	void addAll() {
		JsonArray main = new JsonArray();
		assertThrows(NullPointerException.class, () -> main.addAll((JsonArray) null));
		JsonArray array = new JsonArray();
		array.add(JsonNull.INSTANCE);
		array.add(new JsonPrimitive(10));
		main.addAll(array);
		assertEquals(JsonNull.INSTANCE, main.get(0));
		assertEquals(new JsonPrimitive(10), main.get(1));
		assertEquals(2, main.size());
		
		assertThrows(NullPointerException.class, () -> main.addAll((JsonElement[]) null));
		main.addAll(new JsonPrimitive(20), new JsonPrimitive(30));
		assertEquals(new JsonPrimitive(20), main.get(2));
		assertEquals(new JsonPrimitive(30), main.get(3));
		assertEquals(4, main.size());
		
		assertThrows(NullPointerException.class, () -> main.addAll((List<? extends JsonElement>) null));
		main.addAll(List.of(new JsonPrimitive(40), new JsonPrimitive(50)));
		assertEquals(new JsonPrimitive(40), main.get(4));
		assertEquals(new JsonPrimitive(50), main.get(5));
	}
	
	@Test
	void remove() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.remove(0));
		array.add(JsonNull.INSTANCE);
		assertEquals(JsonNull.INSTANCE, array.remove(0));
		assertTrue(array.isEmpty());
		
		array.add(new JsonPrimitive(10));
		assertFalse(assertDoesNotThrow(() -> array.remove(null)));
		assertFalse(array.isEmpty());
		assertTrue(array.remove(new JsonPrimitive(10)));
		assertTrue(array.isEmpty());
	}
	
	@Test
	void clear() {
		JsonArray array = new JsonArray();
		array.add(JsonNull.INSTANCE);
		array.add(new JsonPrimitive(10));
		array.clear();
		assertTrue(array.isEmpty());
	}
	
	@Test
	void get() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.get(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.get(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.get(-1));
		assertEquals(JsonNull.INSTANCE, array.get(0));
	}
	
	@Test
	void getAsJsonObjectIndexed() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonObject(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonObject(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsJsonObject(0));
		array.set(0, new JsonObject());
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonObject(-1));
		assertEquals(new JsonObject(), assertDoesNotThrow(() -> array.getAsJsonObject(0)));
	}
	
	@Test
	void getAsJsonArrayIndexed() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonArray(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonArray(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsJsonArray(0));
		array.set(0, new JsonArray());
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonArray(-1));
		assertEquals(new JsonArray(), assertDoesNotThrow(() -> array.getAsJsonArray(0)));
	}
	
	@Test
	void getAsJsonPrimitiveIndexed() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsJsonPrimitive(0));
		array.set(0, new JsonPrimitive(10));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertEquals(new JsonPrimitive(10), assertDoesNotThrow(() -> array.getAsJsonPrimitive(0)));
	}
	
	@Test
	void getAsString() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsString(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsString(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsString(0));
		array.set(0, new JsonPrimitive("10"));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertEquals("10", assertDoesNotThrow(() -> array.getAsString(0)));
	}
	
	@Test
	void getAsBoolean() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsBoolean(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsBoolean(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsBoolean(0));
		array.set(0, new JsonPrimitive(true));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertTrue(assertDoesNotThrow(() -> array.getAsBoolean(0)));
	}
	
	@Test
	void getAsNumber() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsNumber(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsNumber(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsNumber(0));
		array.set(0, new JsonPrimitive(10));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertEquals(10, assertDoesNotThrow(() -> array.getAsNumber(0)));
	}
	
	@Test
	void getAsByte() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsByte(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsByte(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsByte(0));
		array.set(0, new JsonPrimitive((byte) 10));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertEquals((byte) 10, assertDoesNotThrow(() -> array.getAsByte(0)));
	}
	
	@Test
	void getAsShort() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsShort(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsShort(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsShort(0));
		array.set(0, new JsonPrimitive((short) 10));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertEquals((short) 10, assertDoesNotThrow(() -> array.getAsShort(0)));
	}
	
	@Test
	void getAsInteger() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsInteger(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsInteger(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsInteger(0));
		array.set(0, new JsonPrimitive(10));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertEquals(10, assertDoesNotThrow(() -> array.getAsInteger(0)));
	}
	
	@Test
	void getAsLong() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsLong(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsLong(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsLong(0));
		array.set(0, new JsonPrimitive(10L));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertEquals(10L, assertDoesNotThrow(() -> array.getAsLong(0)));
	}
	
	@Test
	void getAsFloat() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsFloat(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsFloat(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsFloat(0));
		array.set(0, new JsonPrimitive(10.0f));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertEquals(10.0F, assertDoesNotThrow(() -> array.getAsFloat(0)));
	}
	
	@Test
	void getAsDouble() {
		JsonArray array = new JsonArray();
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsDouble(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsDouble(0));
		array.add(JsonNull.INSTANCE);
		assertThrows(JsonTypeException.class, () -> array.getAsDouble(0));
		array.set(0, new JsonPrimitive(10.0));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonPrimitive(-1));
		assertEquals(10.0, assertDoesNotThrow(() -> array.getAsDouble(0)));
	}
	
	@Test
	void toStringDefaultConfig() {
		JsonArray array = new JsonArray();
		assertEquals("[]", array.toString());
		
		//region Test setup
		for (int i = 0; i < 10; i++) {
			array.add(new JsonPrimitive(i));
		}
		//endregion
		
		assertDoesNotThrow(() -> array.toString());
		assertEquals("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]", array.toString());
	}
	
	@Test
	void toStringCustomConfig() {
		JsonArray array = new JsonArray();
		assertEquals("[]", array.toString(CUSTOM_CONFIG));
		
		//region Test setup
		StringBuilder builder = new StringBuilder("[").append(System.lineSeparator());
		for (int i = 0; i < 5; i++) {
			array.add(new JsonPrimitive(i));
			builder.append("  ").append(i).append(",").append(System.lineSeparator());
		}
		String expected = builder.append("  6").append(System.lineSeparator()).append("]").toString();
		//endregion
		
		assertThrows(NullPointerException.class, () -> array.toString(null));
		assertDoesNotThrow(() -> array.toString());
		assertEquals("[0, 1, 2, 3, 4]", array.toString());
		array.add(new JsonPrimitive(6));
		assertDoesNotThrow(() -> array.toString(CUSTOM_CONFIG));
		assertEquals(expected, array.toString(CUSTOM_CONFIG));
	}
}
