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

import net.luis.utils.io.data.json.exception.JsonArrayIndexOutOfBoundsException;
import net.luis.utils.io.data.json.exception.JsonTypeException;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
	void constructorWithEmptyList() {
		JsonArray array = new JsonArray();
		assertTrue(array.isEmpty());
		assertEquals(0, array.size());
	}
	
	@Test
	void constructorWithElementsList() {
		List<JsonElement> elements = List.of(JsonNull.INSTANCE, new JsonPrimitive(42), new JsonPrimitive("test"));
		JsonArray array = new JsonArray(elements);
		
		assertEquals(3, array.size());
		assertEquals(JsonNull.INSTANCE, array.get(0));
		assertEquals(new JsonPrimitive(42), array.get(1));
		assertEquals(new JsonPrimitive("test"), array.get(2));
	}
	
	@Test
	void constructorWithNullList() {
		assertThrows(NullPointerException.class, () -> new JsonArray(null));
	}
	
	@Test
	void jsonElementTypeChecks() {
		JsonArray array = new JsonArray();
		
		assertFalse(array.isJsonNull());
		assertFalse(array.isJsonObject());
		assertTrue(array.isJsonArray());
		assertFalse(array.isJsonPrimitive());
	}
	
	@Test
	void jsonElementConversions() {
		JsonArray array = new JsonArray();
		
		assertThrows(JsonTypeException.class, array::getAsJsonObject);
		assertSame(array, array.getAsJsonArray());
		assertThrows(JsonTypeException.class, array::getAsJsonPrimitive);
	}
	
	@Test
	void sizeOperations() {
		JsonArray array = new JsonArray();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
		
		array.add(JsonNull.INSTANCE);
		assertEquals(1, array.size());
		assertFalse(array.isEmpty());
		
		array.add(new JsonPrimitive(10));
		assertEquals(2, array.size());
		
		array.remove(0);
		assertEquals(1, array.size());
		
		array.clear();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
	}
	
	@Test
	void containsOperations() {
		JsonArray array = new JsonArray();
		JsonElement element = new JsonPrimitive("test");
		
		assertFalse(array.contains(element));
		assertFalse(array.contains(null));
		
		array.add(element);
		assertTrue(array.contains(element));
		assertFalse(array.contains(new JsonPrimitive("different")));
		
		array.add((JsonElement) null);
		assertTrue(array.contains(JsonNull.INSTANCE));
	}
	
	@Test
	void iteratorAndCollectionViews() {
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		int count = 0;
		for (JsonElement element : array) {
			count++;
			assertTrue(element instanceof JsonPrimitive);
		}
		assertEquals(3, count);
		
		assertEquals(3, array.elements().size());
		assertTrue(array.elements().contains(new JsonPrimitive(1)));
		
		List<JsonElement> elements = array.getElements();
		assertEquals(3, elements.size());
		assertEquals(new JsonPrimitive(1), elements.get(0));
		assertEquals(new JsonPrimitive(2), elements.get(1));
		assertEquals(new JsonPrimitive(3), elements.get(2));
		
		assertThrows(UnsupportedOperationException.class, () -> elements.add(new JsonPrimitive(4)));
	}
	
	@Test
	void setOperations() {
		JsonArray array = new JsonArray();
		
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.set(-1, JsonNull.INSTANCE));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.set(0, JsonNull.INSTANCE));
		
		array.add(new JsonPrimitive(1));
		
		JsonElement old = array.set(0, new JsonPrimitive(2));
		assertEquals(new JsonPrimitive(1), old);
		assertEquals(new JsonPrimitive(2), array.get(0));
		
		array.set(0, (JsonElement) null);
		assertEquals(JsonNull.INSTANCE, array.get(0));
		
		array.set(0, "string");
		assertEquals(new JsonPrimitive("string"), array.get(0));
		
		array.set(0, true);
		assertEquals(new JsonPrimitive(true), array.get(0));
		
		array.set(0, (Number) null);
		assertEquals(JsonNull.INSTANCE, array.get(0));
		
		array.set(0, 42);
		assertEquals(new JsonPrimitive(42), array.get(0));
		
		array.set(0, (byte) 10);
		assertEquals(new JsonPrimitive((byte) 10), array.get(0));
		
		array.set(0, (short) 20);
		assertEquals(new JsonPrimitive((short) 20), array.get(0));
		
		array.set(0, 30L);
		assertEquals(new JsonPrimitive(30L), array.get(0));
		
		array.set(0, 40.5f);
		assertEquals(new JsonPrimitive(40.5f), array.get(0));
		
		array.set(0, 50.7);
		assertEquals(new JsonPrimitive(50.7), array.get(0));
	}
	
	@Test
	void addOperations() {
		JsonArray array = new JsonArray();
		
		array.add((JsonElement) null);
		assertEquals(JsonNull.INSTANCE, array.get(0));
		
		array.add(new JsonPrimitive(42));
		assertEquals(new JsonPrimitive(42), array.get(1));
		
		array.add("string");
		assertEquals(new JsonPrimitive("string"), array.get(2));
		
		array.add((String) null);
		assertEquals(JsonNull.INSTANCE, array.get(3));
		
		array.add(true);
		assertEquals(new JsonPrimitive(true), array.get(4));
		
		array.add((Number) null);
		assertEquals(JsonNull.INSTANCE, array.get(5));
		
		array.add(123);
		assertEquals(new JsonPrimitive(123), array.get(6));
		
		array.add((byte) 1);
		assertEquals(new JsonPrimitive((byte) 1), array.get(7));
		
		array.add((short) 2);
		assertEquals(new JsonPrimitive((short) 2), array.get(8));
		
		array.add(3L);
		assertEquals(new JsonPrimitive(3L), array.get(9));
		
		array.add(4.5f);
		assertEquals(new JsonPrimitive(4.5f), array.get(10));
		
		array.add(5.7);
		assertEquals(new JsonPrimitive(5.7), array.get(11));
	}
	
	@Test
	void addAllOperations() {
		JsonArray array = new JsonArray();
		
		JsonArray other = new JsonArray();
		other.add(new JsonPrimitive(1));
		other.add(new JsonPrimitive(2));
		
		assertThrows(NullPointerException.class, () -> array.addAll((JsonArray) null));
		
		array.addAll(other);
		assertEquals(2, array.size());
		assertEquals(new JsonPrimitive(1), array.get(0));
		assertEquals(new JsonPrimitive(2), array.get(1));
		
		assertThrows(NullPointerException.class, () -> array.addAll((JsonElement[]) null));
		
		array.addAll(new JsonPrimitive(3), new JsonPrimitive(4));
		assertEquals(4, array.size());
		assertEquals(new JsonPrimitive(3), array.get(2));
		assertEquals(new JsonPrimitive(4), array.get(3));
		
		assertThrows(NullPointerException.class, () -> array.addAll((List<JsonElement>) null));
		
		array.addAll(Arrays.asList(new JsonPrimitive(5), new JsonPrimitive(6)));
		assertEquals(6, array.size());
		assertEquals(new JsonPrimitive(5), array.get(4));
		assertEquals(new JsonPrimitive(6), array.get(5));
	}
	
	@Test
	void removeOperations() {
		JsonArray array = new JsonArray();
		
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.remove(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.remove(0));
		
		array.add(new JsonPrimitive(1));
		array.add(new JsonPrimitive(2));
		array.add(new JsonPrimitive(3));
		
		JsonElement removed = array.remove(1);
		assertEquals(new JsonPrimitive(2), removed);
		assertEquals(2, array.size());
		assertEquals(new JsonPrimitive(1), array.get(0));
		assertEquals(new JsonPrimitive(3), array.get(1));
		
		assertTrue(array.remove(new JsonPrimitive(1)));
		assertFalse(array.remove(new JsonPrimitive(999)));
		assertFalse(array.remove(null));
		
		array.add(new JsonPrimitive(10));
		array.add(new JsonPrimitive(20));
		assertEquals(3, array.size());
		
		array.clear();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
	}
	
	@Test
	void getOperationsWithBoundsChecking() {
		JsonArray array = new JsonArray();
		
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.get(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.get(0));
		
		array.add(new JsonObject());
		array.add(new JsonArray());
		array.add(new JsonPrimitive("test"));
		array.add(new JsonPrimitive(42));
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(3.14));
		
		assertEquals(new JsonObject(), array.get(0));
		assertEquals(new JsonArray(), array.get(1));
		assertEquals(new JsonPrimitive("test"), array.get(2));
	}
	
	@Test
	void getAsSpecificTypes() {
		JsonArray array = new JsonArray();
		array.add(new JsonObject());
		array.add(new JsonArray());
		array.add(new JsonPrimitive("test"));
		array.add(new JsonPrimitive(42));
		array.add(new JsonPrimitive(true));
		array.add(new JsonPrimitive(3.14));
		array.add(JsonNull.INSTANCE);
		
		assertEquals(new JsonObject(), array.getAsJsonObject(0));
		assertEquals(new JsonArray(), array.getAsJsonArray(1));
		assertEquals(new JsonPrimitive("test"), array.getAsJsonPrimitive(2));
		
		assertEquals("test", array.getAsString(2));
		assertEquals(42L, array.getAsNumber(3));
		assertEquals(42, array.getAsInteger(3));
		assertEquals(42L, array.getAsLong(3));
		assertEquals(42.0f, array.getAsFloat(3));
		assertEquals(42.0, array.getAsDouble(3));
		assertEquals((byte) 42, array.getAsByte(3));
		assertEquals((short) 42, array.getAsShort(3));
		assertTrue(array.getAsBoolean(4));
		assertEquals(3.14, array.getAsDouble(5));
		
		assertThrows(JsonTypeException.class, () -> array.getAsJsonObject(2));
		assertThrows(JsonTypeException.class, () -> array.getAsJsonArray(2));
		assertThrows(JsonTypeException.class, () -> array.getAsJsonPrimitive(0));
		assertThrows(JsonTypeException.class, () -> array.getAsString(6));
		assertThrows(JsonTypeException.class, () -> array.getAsNumber(6));
		
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsJsonObject(-1));
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> array.getAsString(100));
	}
	
	@Test
	void equalsAndHashCode() {
		JsonArray array1 = new JsonArray();
		JsonArray array2 = new JsonArray();
		JsonArray array3 = new JsonArray();
		
		assertEquals(array1, array2);
		assertEquals(array1.hashCode(), array2.hashCode());
		
		array1.add(new JsonPrimitive(42));
		array1.add(new JsonPrimitive("test"));
		
		array2.add(new JsonPrimitive(42));
		array2.add(new JsonPrimitive("test"));
		
		assertEquals(array1, array2);
		assertEquals(array1.hashCode(), array2.hashCode());
		
		array3.add(new JsonPrimitive(43));
		array3.add(new JsonPrimitive("test"));
		
		assertNotEquals(array1, array3);
		assertNotEquals(array1, null);
		assertNotEquals(array1, "not an array");
		
		assertEquals(array1, array1);
	}
	
	@Test
	void toStringWithDefaultConfig() {
		JsonArray array = new JsonArray();
		assertEquals("[]", array.toString());
		
		array.add(new JsonPrimitive(42));
		assertEquals("[42]", array.toString());
		
		for (int i = 1; i < 10; i++) {
			array.add(new JsonPrimitive(i));
		}
		assertEquals("[42, 1, 2, 3, 4, 5, 6, 7, 8, 9]", array.toString());
		
		array.add(new JsonPrimitive(10));
		String result = array.toString();
		assertTrue(result.contains(System.lineSeparator()));
		assertTrue(result.startsWith("["));
		assertTrue(result.endsWith("]"));
	}
	
	@Test
	void toStringWithCustomConfig() {
		JsonArray array = new JsonArray();
		
		assertThrows(NullPointerException.class, () -> array.toString(null));
		
		assertEquals("[]", array.toString(CUSTOM_CONFIG));
		
		for (int i = 0; i < 5; i++) {
			array.add(new JsonPrimitive(i));
		}
		assertEquals("[0, 1, 2, 3, 4]", array.toString(CUSTOM_CONFIG));
		
		array.add(new JsonPrimitive(5));
		String result = array.toString(CUSTOM_CONFIG);
		assertTrue(result.contains(System.lineSeparator()));
		assertTrue(result.contains("  "));
		assertTrue(result.startsWith("["));
		assertTrue(result.endsWith("]"));
	}
	
	@Test
	void toStringWithNestedStructures() {
		JsonArray array = new JsonArray();
		JsonObject nestedObject = new JsonObject();
		nestedObject.add("key", new JsonPrimitive("value"));
		
		JsonArray nestedArray = new JsonArray();
		nestedArray.add(new JsonPrimitive(1));
		nestedArray.add(new JsonPrimitive(2));
		
		array.add(nestedObject);
		array.add(nestedArray);
		
		String result = array.toString();
		assertTrue(result.contains("\"key\""));
		assertTrue(result.contains("\"value\""));
		assertTrue(result.contains("[1, 2]"));
	}
}
