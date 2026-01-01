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

package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Pair}.<br>
 *
 * @author Luis-St
 */
class PairTest {
	
	@Test
	void ofCreatesValidPair() {
		assertNotNull(Pair.of(1, 2));
		assertNotNull(Pair.of("hello", "world"));
		assertNotNull(Pair.of(true, false));
	}
	
	@Test
	void ofAcceptsNullValues() {
		assertDoesNotThrow(() -> Pair.of(null, null));
		assertDoesNotThrow(() -> Pair.of(1, null));
		assertDoesNotThrow(() -> Pair.of(null, 2));
		
		Pair<Integer, Integer> bothNull = Pair.of(null, null);
		assertNull(bothNull.getFirst());
		assertNull(bothNull.getSecond());
	}
	
	@Test
	void getFirstReturnsFirstValue() {
		assertEquals(1, Pair.of(1, 2).getFirst());
		assertEquals("hello", Pair.of("hello", "world").getFirst());
		assertNull(Pair.of(null, 2).getFirst());
	}
	
	@Test
	void getSecondReturnsSecondValue() {
		assertEquals(2, Pair.of(1, 2).getSecond());
		assertEquals("world", Pair.of("hello", "world").getSecond());
		assertNull(Pair.of(1, null).getSecond());
	}
	
	@Test
	void swapExchangesValues() {
		Pair<Integer, String> original = Pair.of(1, "hello");
		Pair<String, Integer> swapped = original.swap();
		
		assertEquals("hello", swapped.getFirst());
		assertEquals(1, swapped.getSecond());
		assertNotEquals(original, swapped);
	}
	
	@Test
	void swapWithNullValues() {
		Pair<Integer, String> withNull = Pair.of(null, "hello");
		Pair<String, Integer> swapped = withNull.swap();
		
		assertEquals("hello", swapped.getFirst());
		assertNull(swapped.getSecond());
	}
	
	@Test
	void mapFirstTransformsFirstValue() {
		Pair<Integer, String> pair = Pair.of(5, "test");
		Pair<String, String> mapped = pair.mapFirst(Object::toString);
		
		assertEquals("5", mapped.getFirst());
		assertEquals("test", mapped.getSecond());
	}
	
	@Test
	void mapFirstRejectsNullMapper() {
		assertThrows(NullPointerException.class, () -> Pair.of(1, 2).mapFirst(null));
	}
	
	@Test
	void mapFirstHandlesNullValue() {
		Pair<Integer, String> pair = Pair.of(null, "test");
		Pair<String, String> mapped = pair.mapFirst(value -> value == null ? "null" : value.toString());
		
		assertEquals("null", mapped.getFirst());
		assertEquals("test", mapped.getSecond());
	}
	
	@Test
	void mapSecondTransformsSecondValue() {
		Pair<String, Integer> pair = Pair.of("test", 5);
		Pair<String, String> mapped = pair.mapSecond(Object::toString);
		
		assertEquals("test", mapped.getFirst());
		assertEquals("5", mapped.getSecond());
	}
	
	@Test
	void mapSecondRejectsNullMapper() {
		assertThrows(NullPointerException.class, () -> Pair.of(1, 2).mapSecond(null));
	}
	
	@Test
	void mapSecondHandlesNullValue() {
		Pair<String, Integer> pair = Pair.of("test", null);
		Pair<String, String> mapped = pair.mapSecond(value -> value == null ? "null" : value.toString());
		
		assertEquals("test", mapped.getFirst());
		assertEquals("null", mapped.getSecond());
	}
	
	@Test
	void withFirstReplacesFirstValue() {
		Pair<Integer, String> original = Pair.of(1, "test");
		Pair<String, String> modified = original.withFirst("hello");
		
		assertEquals("hello", modified.getFirst());
		assertEquals("test", modified.getSecond());
		assertNotEquals(original, modified);
	}
	
	@Test
	void withFirstAcceptsNull() {
		Pair<Integer, String> original = Pair.of(1, "test");
		Pair<Integer, String> modified = original.withFirst(null);
		
		assertNull(modified.getFirst());
		assertEquals("test", modified.getSecond());
	}
	
	@Test
	void withSecondReplacesSecondValue() {
		Pair<String, Integer> original = Pair.of("test", 1);
		Pair<String, String> modified = original.withSecond("hello");
		
		assertEquals("test", modified.getFirst());
		assertEquals("hello", modified.getSecond());
		assertNotEquals(original, modified);
	}
	
	@Test
	void withSecondAcceptsNull() {
		Pair<String, Integer> original = Pair.of("test", 1);
		Pair<String, Integer> modified = original.withSecond(null);
		
		assertEquals("test", modified.getFirst());
		assertNull(modified.getSecond());
	}
	
	@Test
	void equalsComparesValues() {
		Pair<Integer, String> pair1 = Pair.of(1, "test");
		Pair<Integer, String> pair2 = Pair.of(1, "test");
		Pair<Integer, String> differentFirst = Pair.of(2, "test");
		Pair<Integer, String> differentSecond = Pair.of(1, "other");
		Pair<Integer, String> bothDifferent = Pair.of(2, "other");
		
		assertEquals(pair1, pair2);
		assertNotEquals(pair1, differentFirst);
		assertNotEquals(pair1, differentSecond);
		assertNotEquals(pair1, bothDifferent);
		assertNotEquals(pair1, null);
		assertNotEquals(pair1, "not a pair");
	}
	
	@Test
	void equalsHandlesNullValues() {
		Pair<Integer, String> bothNull = Pair.of(null, null);
		Pair<Integer, String> bothNullToo = Pair.of(null, null);
		Pair<Integer, String> firstNull = Pair.of(null, "test");
		Pair<Integer, String> secondNull = Pair.of(1, null);
		
		assertEquals(bothNull, bothNullToo);
		assertNotEquals(bothNull, firstNull);
		assertNotEquals(bothNull, secondNull);
		assertNotEquals(firstNull, secondNull);
	}
	
	@Test
	void hashCodeIsSameForEqualObjects() {
		Pair<Integer, String> pair1 = Pair.of(1, "test");
		Pair<Integer, String> pair2 = Pair.of(1, "test");
		
		assertEquals(pair1.hashCode(), pair2.hashCode());
	}
	
	@Test
	void toStringFormatsCorrectly() {
		assertEquals("(1, test)", Pair.of(1, "test").toString());
		assertEquals("(null, test)", Pair.of(null, "test").toString());
		assertEquals("(1, null)", Pair.of(1, null).toString());
		assertEquals("(null, null)", Pair.of(null, null).toString());
		assertEquals("(hello, world)", Pair.of("hello", "world").toString());
	}
}
