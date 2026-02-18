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

package net.luis.utils.io.data.toon;

import net.luis.utils.io.data.toon.exception.NoSuchToonElementException;
import net.luis.utils.io.data.toon.exception.ToonTypeException;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonObject}.<br>
 *
 * @author Luis-St
 */
class ToonObjectTest {
	
	@Test
	void constructEmpty() {
		ToonObject object = new ToonObject();
		assertEquals(0, object.size());
		assertTrue(object.isEmpty());
	}
	
	@Test
	void constructWithMap() {
		LinkedHashMap<String, ToonElement> map = new LinkedHashMap<>();
		map.put("name", new ToonValue("hello"));
		map.put("count", new ToonValue(42));
		
		ToonObject object = new ToonObject(map);
		assertEquals(2, object.size());
		assertTrue(object.containsKey("name"));
		assertTrue(object.containsKey("count"));
		assertEquals(new ToonValue("hello"), object.get("name"));
	}
	
	@Test
	void constructWithNullMap() {
		assertThrows(NullPointerException.class, () -> new ToonObject(null));
	}
	
	@Test
	void addAndGet() {
		ToonObject object = new ToonObject();
		ToonValue value = new ToonValue("world");
		
		assertNull(object.add("key", value));
		assertEquals(value, object.get("key"));
		assertEquals(1, object.size());
		
		ToonElement previous = object.add("key", new ToonValue("updated"));
		assertEquals(value, previous);
		assertEquals(new ToonValue("updated"), object.get("key"));
		
		object.add("nullElement", (ToonElement) null);
		assertEquals(ToonNull.INSTANCE, object.get("nullElement"));
	}
	
	@Test
	void addString() {
		ToonObject object = new ToonObject();
		object.add("greeting", "hello");
		assertEquals(new ToonValue("hello"), object.get("greeting"));
		
		object.add("nullStr", (String) null);
		assertEquals(ToonNull.INSTANCE, object.get("nullStr"));
	}
	
	@Test
	void addBoolean() {
		ToonObject object = new ToonObject();
		object.add("flag", true);
		assertEquals(new ToonValue(true), object.get("flag"));
		
		object.add("other", false);
		assertEquals(new ToonValue(false), object.get("other"));
	}
	
	@Test
	void addNumber() {
		ToonObject object = new ToonObject();
		object.add("count", 42);
		assertEquals(new ToonValue(42), object.get("count"));
		
		object.add("pi", 3.14);
		assertEquals(new ToonValue(3.14), object.get("pi"));
		
		object.add("nullNum", (Number) null);
		assertEquals(ToonNull.INSTANCE, object.get("nullNum"));
	}
	
	@Test
	void addAll() {
		ToonObject source = new ToonObject();
		source.add("a", "alpha");
		source.add("b", "beta");
		
		ToonObject target = new ToonObject();
		target.add("c", "gamma");
		target.addAll(source);
		
		assertEquals(3, target.size());
		assertTrue(target.containsKey("a"));
		assertTrue(target.containsKey("b"));
		assertTrue(target.containsKey("c"));
		
		LinkedHashMap<String, ToonElement> map = new LinkedHashMap<>();
		map.put("d", new ToonValue("delta"));
		map.put("e", new ToonValue("epsilon"));
		target.addAll(map);
		
		assertEquals(5, target.size());
		assertTrue(target.containsKey("d"));
		assertTrue(target.containsKey("e"));
	}
	
	@Test
	void containsKeyAndValue() {
		ToonObject object = new ToonObject();
		ToonValue value = new ToonValue("test");
		object.add("key", value);
		
		assertTrue(object.containsKey("key"));
		assertFalse(object.containsKey("missing"));
		assertTrue(object.containsValue(value));
		assertFalse(object.containsValue(new ToonValue("other")));
	}
	
	@Test
	void getTyped() {
		ToonObject object = new ToonObject();
		object.add("value", new ToonValue("hello"));
		object.add("array", new ToonArray());
		ToonObject nested = new ToonObject();
		nested.add("inner", "data");
		object.add("object", nested);
		
		assertEquals(new ToonValue("hello"), object.getToonValue("value"));
		assertNotNull(object.getToonArray("array"));
		assertNotNull(object.getToonObject("object"));
		
		assertThrows(NoSuchToonElementException.class, () -> object.getToonValue("missing"));
		assertThrows(NoSuchToonElementException.class, () -> object.getToonArray("missing"));
		assertThrows(NoSuchToonElementException.class, () -> object.getToonObject("missing"));
		
		assertThrows(ToonTypeException.class, () -> object.getToonValue("array"));
		assertThrows(ToonTypeException.class, () -> object.getToonArray("value"));
		assertThrows(ToonTypeException.class, () -> object.getToonObject("value"));
	}
	
	@Test
	void getAsTyped() {
		ToonObject object = new ToonObject();
		object.add("str", "hello");
		object.add("bool", true);
		object.add("intVal", 42);
		object.add("longVal", 100L);
		object.add("doubleVal", 3.14);
		object.add("byteVal", (byte) 7);
		object.add("shortVal", (short) 123);
		object.add("floatVal", 2.5f);
		
		assertEquals("hello", object.getAsString("str"));
		assertTrue(object.getAsBoolean("bool"));
		assertEquals(42, object.getAsNumber("intVal").intValue());
		assertEquals((byte) 7, object.getAsByte("byteVal"));
		assertEquals((short) 123, object.getAsShort("shortVal"));
		assertEquals(42, object.getAsInteger("intVal"));
		assertEquals(100L, object.getAsLong("longVal"));
		assertEquals(2.5f, object.getAsFloat("floatVal"));
		assertEquals(3.14, object.getAsDouble("doubleVal"), 0.001);
	}
	
	@Test
	void removeAndClear() {
		ToonObject object = new ToonObject();
		ToonValue value = new ToonValue("hello");
		object.add("key", value);
		object.add("other", "world");
		
		ToonElement removed = object.remove("key");
		assertEquals(value, removed);
		assertEquals(1, object.size());
		assertNull(object.remove("nonexistent"));
		
		object.clear();
		assertEquals(0, object.size());
		assertTrue(object.isEmpty());
	}
	
	@Test
	void replaceElement() {
		ToonObject object = new ToonObject();
		ToonValue original = new ToonValue("original");
		ToonValue replacement = new ToonValue("replacement");
		object.add("key", original);
		
		ToonElement previous = object.replace("key", replacement);
		assertEquals(original, previous);
		assertEquals(replacement, object.get("key"));
		
		assertNull(object.replace("missing", new ToonValue("x")));
		
		assertTrue(object.replace("key", replacement, new ToonValue("final")));
		assertEquals(new ToonValue("final"), object.get("key"));
		
		assertFalse(object.replace("key", replacement, new ToonValue("nope")));
		
		object.replace("key", null);
		assertEquals(ToonNull.INSTANCE, object.get("key"));
	}
	
	@Test
	void forEachWithAction() {
		ToonObject object = new ToonObject();
		object.add("a", "alpha");
		object.add("b", "beta");
		
		List<String> keys = new ArrayList<>();
		object.forEach((key, element) -> keys.add(key));
		assertEquals(2, keys.size());
		assertTrue(keys.contains("a"));
		assertTrue(keys.contains("b"));
		
		assertThrows(NullPointerException.class, () -> object.forEach((java.util.function.BiConsumer<? super String, ? super ToonElement>) null));
	}
	
	@Test
	void keySetAndElements() {
		ToonObject object = new ToonObject();
		object.add("x", "ex");
		object.add("y", "why");
		
		Set<String> keys = object.keySet();
		assertEquals(2, keys.size());
		assertTrue(keys.contains("x"));
		assertTrue(keys.contains("y"));
		
		Collection<ToonElement> elements = object.elements();
		assertEquals(2, elements.size());
		
		Set<Map.Entry<String, ToonElement>> entries = object.entrySet();
		assertEquals(2, entries.size());
	}
	
	@Test
	void iterator() {
		ToonObject object = new ToonObject();
		object.add("first", "one");
		object.add("second", "two");
		
		int count = 0;
		for (Map.Entry<String, ToonElement> entry : object) {
			assertNotNull(entry.getKey());
			assertNotNull(entry.getValue());
			count++;
		}
		assertEquals(2, count);
	}
	
	@Test
	void toStringWithConfig() {
		ToonObject object = new ToonObject();
		object.add("name", "hello");
		object.add("count", 42);
		
		String result = object.toString(ToonConfig.DEFAULT);
		assertTrue(result.contains("name: hello"));
		assertTrue(result.contains("count: 42"));
	}
	
	@Test
	void equalsAndHashCode() {
		ToonObject a = new ToonObject();
		a.add("key", "value");
		
		ToonObject b = new ToonObject();
		b.add("key", "value");
		
		ToonObject c = new ToonObject();
		c.add("key", "other");
		
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, c);
		assertNotEquals(a, null);
		assertNotEquals(a, "string");
		assertEquals(a, a);
	}
}
