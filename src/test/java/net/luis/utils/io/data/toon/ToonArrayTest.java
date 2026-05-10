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

import net.luis.utils.io.data.toon.exception.ToonArrayIndexOutOfBoundsException;
import net.luis.utils.io.data.toon.exception.ToonTypeException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonArray}.<br>
 *
 * @author Luis-St
 */
class ToonArrayTest {
	
	@Test
	void constructEmpty() {
		ToonArray array = new ToonArray();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
	}
	
	@Test
	void constructWithList() {
		List<ToonElement> list = List.of(new ToonValue("a"), new ToonValue("b"), new ToonValue("c"));
		ToonArray array = new ToonArray(list);
		assertEquals(3, array.size());
		assertFalse(array.isEmpty());
	}
	
	@Test
	void constructWithNullList() {
		assertThrows(NullPointerException.class, () -> new ToonArray(null));
	}
	
	@Test
	void addAndGet() {
		ToonArray array = new ToonArray();
		ToonValue value = new ToonValue("hello");
		array.add(value);
		
		assertEquals(1, array.size());
		assertEquals(value, array.get(0));
		
		array.add((ToonElement) null);
		assertEquals(ToonNull.INSTANCE, array.get(1));
	}
	
	@Test
	void addString() {
		ToonArray array = new ToonArray();
		array.add("hello");
		assertEquals(new ToonValue("hello"), array.get(0));
		
		array.add((String) null);
		assertEquals(ToonNull.INSTANCE, array.get(1));
	}
	
	@Test
	void addBoolean() {
		ToonArray array = new ToonArray();
		array.add(true);
		array.add(false);
		assertEquals(new ToonValue(true), array.get(0));
		assertEquals(new ToonValue(false), array.get(1));
	}
	
	@Test
	void addNumber() {
		ToonArray array = new ToonArray();
		array.add(42);
		array.add(3.14);
		assertEquals(new ToonValue(42), array.get(0));
		assertEquals(new ToonValue(3.14), array.get(1));
		
		array.add((Number) null);
		assertEquals(ToonNull.INSTANCE, array.get(2));
	}
	
	@Test
	void addAll() {
		ToonArray source = new ToonArray();
		source.add("a");
		source.add("b");
		
		ToonArray target = new ToonArray();
		target.add("c");
		target.addAll(source);
		assertEquals(3, target.size());
		
		ToonArray varArgs = new ToonArray();
		varArgs.addAll(new ToonValue("x"), new ToonValue("y"));
		assertEquals(2, varArgs.size());
		assertEquals(new ToonValue("x"), varArgs.get(0));
		assertEquals(new ToonValue("y"), varArgs.get(1));
		
		ToonArray fromList = new ToonArray();
		List<ToonElement> list = List.of(new ToonValue("m"), new ToonValue("n"));
		fromList.addAll(list);
		assertEquals(2, fromList.size());
		assertEquals(new ToonValue("m"), fromList.get(0));
	}
	
	@Test
	void setElement() {
		ToonArray array = new ToonArray();
		array.add("original");
		array.add("second");
		
		ToonElement previous = array.set(0, new ToonValue("replaced"));
		assertEquals(new ToonValue("original"), previous);
		assertEquals(new ToonValue("replaced"), array.get(0));
		
		previous = array.set(1, "stringReplaced");
		assertEquals(new ToonValue("second"), previous);
		
		array.set(0, true);
		assertEquals(new ToonValue(true), array.get(0));
		
		array.set(0, 99);
		assertEquals(new ToonValue(99), array.get(0));
		
		array.set(0, (ToonElement) null);
		assertEquals(ToonNull.INSTANCE, array.get(0));
	}
	
	@Test
	void containsAndSize() {
		ToonArray array = new ToonArray();
		ToonValue value = new ToonValue("test");
		array.add(value);
		array.add("other");
		
		assertTrue(array.contains(value));
		assertTrue(array.contains(new ToonValue("other")));
		assertFalse(array.contains(new ToonValue("missing")));
		assertEquals(2, array.size());
		assertFalse(array.isEmpty());
	}
	
	@Test
	void getTyped() {
		ToonArray array = new ToonArray();
		array.add(new ToonValue("hello"));
		array.add(new ToonArray());
		ToonObject obj = new ToonObject();
		obj.add("key", "val");
		array.add(obj);
		
		assertNotNull(array.getAsToonValue(0));
		assertNotNull(array.getAsToonArray(1));
		assertNotNull(array.getAsToonObject(2));
		
		assertThrows(ToonTypeException.class, () -> array.getAsToonValue(1));
		assertThrows(ToonTypeException.class, () -> array.getAsToonArray(0));
		assertThrows(ToonTypeException.class, () -> array.getAsToonObject(0));
	}
	
	@Test
	void getAsTyped() {
		ToonArray array = new ToonArray();
		array.add("hello");
		array.add(true);
		array.add(42);
		array.add(100L);
		array.add(3.14);
		
		assertEquals("hello", array.getAsString(0));
		assertTrue(array.getAsBoolean(1));
		assertEquals(42, array.getAsNumber(2).intValue());
		assertEquals(42, array.getAsInteger(2));
		assertEquals(100L, array.getAsLong(3));
		assertEquals(3.14, array.getAsDouble(4), 0.001);
	}
	
	@Test
	void removeByIndex() {
		ToonArray array = new ToonArray();
		array.add("first");
		array.add("second");
		array.add("third");
		
		ToonElement removed = array.remove(1);
		assertEquals(new ToonValue("second"), removed);
		assertEquals(2, array.size());
		assertEquals(new ToonValue("third"), array.get(1));
		
		assertThrows(ToonArrayIndexOutOfBoundsException.class, () -> array.remove(10));
		assertThrows(ToonArrayIndexOutOfBoundsException.class, () -> array.remove(-1));
	}
	
	@Test
	void removeByElement() {
		ToonArray array = new ToonArray();
		ToonValue value = new ToonValue("target");
		array.add(value);
		array.add("other");
		
		assertTrue(array.remove(value));
		assertEquals(1, array.size());
		assertFalse(array.remove(new ToonValue("nonexistent")));
	}
	
	@Test
	void clear() {
		ToonArray array = new ToonArray();
		array.add("a");
		array.add("b");
		array.add("c");
		
		assertEquals(3, array.size());
		array.clear();
		assertEquals(0, array.size());
		assertTrue(array.isEmpty());
	}
	
	@Test
	void indexOutOfBounds() {
		ToonArray array = new ToonArray();
		array.add("only");
		
		assertThrows(ToonArrayIndexOutOfBoundsException.class, () -> array.get(-1));
		assertThrows(ToonArrayIndexOutOfBoundsException.class, () -> array.get(1));
		
		ToonArray empty = new ToonArray();
		assertThrows(ToonArrayIndexOutOfBoundsException.class, () -> empty.get(0));
	}
	
	@Test
	void toStringWithConfig() {
		ToonArray array = new ToonArray();
		array.add("a");
		array.add("b");
		array.add("c");
		
		String result = array.toString(ToonConfig.DEFAULT);
		assertNotNull(result);
		assertFalse(result.isEmpty());
		assertTrue(result.contains("a"));
		assertTrue(result.contains("b"));
		assertTrue(result.contains("c"));
	}
	
	@Test
	void equalsAndHashCode() {
		ToonArray a = new ToonArray();
		a.add("x");
		a.add("y");
		
		ToonArray b = new ToonArray();
		b.add("x");
		b.add("y");
		
		ToonArray c = new ToonArray();
		c.add("x");
		c.add("z");
		
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
		assertNotEquals(a, c);
		assertNotEquals(a, null);
		assertNotEquals(a, "string");
		assertEquals(a, a);
	}
}
