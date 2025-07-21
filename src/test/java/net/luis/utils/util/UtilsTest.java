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

package net.luis.utils.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.luis.utils.collection.util.SimpleEntry;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Utils}.<br>
 *
 * @author Luis-St
 */
class UtilsTest {
	
	@Test
	void isEmptyIdentifiesEmptyUUID() {
		assertTrue(Utils.isEmpty(Utils.EMPTY_UUID));
		assertTrue(Utils.isEmpty(UUID.fromString("00000000-0000-0000-0000-000000000000")));
		assertFalse(Utils.isEmpty(UUID.randomUUID()));
		assertFalse(Utils.isEmpty(UUID.fromString("550e8400-e29b-41d4-a716-446655440000")));
	}
	
	@Test
	void isEmptyHandlesNull() {
		assertDoesNotThrow(() -> Utils.isEmpty(null));
		assertFalse(Utils.isEmpty(null));
	}
	
	@Test
	void makeAppliesActionAndReturnsObject() {
		List<Integer> list = Utils.make(Lists.newArrayList(), l -> l.add(42));
		assertEquals(Lists.newArrayList(42), list);
		
		Map<String, Integer> map = Utils.make(Maps.newHashMap(), m -> m.put("key", 100));
		assertEquals(Map.of("key", 100), map);
	}
	
	@Test
	void makeRejectsNullParameters() {
		assertThrows(NullPointerException.class, () -> Utils.make(null, list -> {}));
		assertThrows(NullPointerException.class, () -> Utils.make(Lists.newArrayList(), null));
	}
	
	@Test
	void mapToListConvertsMapToList() {
		Map<String, Integer> map = Map.of("0", 0, "1", 1, "2", 2);
		List<Integer> result = Utils.mapToList(map, (key, value) -> value);
		
		assertEquals(3, result.size());
		assertTrue(result.containsAll(List.of(0, 1, 2)));
	}
	
	@Test
	void mapToListHandlesNullInputs() {
		assertEquals(Lists.newArrayList(), Utils.mapToList(null, null));
		assertEquals(Lists.newArrayList(), Utils.mapToList(null, (key, value) -> value));
		assertEquals(Lists.newArrayList(), Utils.mapToList(Map.of("a", 1), null));
		assertEquals(Lists.newArrayList(), Utils.mapToList(Maps.newHashMap(), (key, value) -> value));
	}
	
	@Test
	void mapToListTransformsKeyValuePairs() {
		Map<String, Integer> map = Map.of("hello", 5, "world", 5);
		List<String> result = Utils.mapToList(map, (key, value) -> key + ":" + value);
		
		assertEquals(2, result.size());
		assertTrue(result.contains("hello:5"));
		assertTrue(result.contains("world:5"));
	}
	
	@Test
	void listToMapConvertsListToMap() {
		List<Integer> list = List.of(0, 1, 2, 3, 4);
		Map<String, Integer> result = Utils.listToMap(list, value -> new SimpleEntry<>(String.valueOf(value), value));
		
		assertEquals(Map.of("0", 0, "1", 1, "2", 2, "3", 3, "4", 4), result);
	}
	
	@Test
	void listToMapHandlesNullInputs() {
		assertEquals(Maps.newHashMap(), Utils.listToMap(null, null));
		assertEquals(Maps.newHashMap(), Utils.listToMap(null, value -> new SimpleEntry<>("key", value)));
		assertEquals(Maps.newHashMap(), Utils.listToMap(List.of(1, 2), null));
		assertEquals(Maps.newHashMap(), Utils.listToMap(Lists.newArrayList(), value -> new SimpleEntry<>("key", value)));
	}
	
	@Test
	void mapListTransformsElements() {
		List<Integer> numbers = List.of(1, 2, 3, 4);
		List<String> strings = Utils.mapList(numbers, Object::toString);
		
		assertEquals(List.of("1", "2", "3", "4"), strings);
	}
	
	@Test
	void mapListHandlesNullInputs() {
		assertEquals(Lists.newArrayList(), Utils.mapList(null, null));
		assertEquals(Lists.newArrayList(), Utils.mapList(null, Object::toString));
		assertEquals(Lists.newArrayList(), Utils.mapList(List.of(1, 2), null));
		assertEquals(Lists.newArrayList(), Utils.mapList(Lists.newArrayList(), Object::toString));
	}
	
	@Test
	void mapListWithTwoFunctionsChainTransformations() {
		List<Integer> numbers = List.of(1, 2, 3);
		List<Integer> result = Utils.mapList(numbers, Object::toString, String::length);
		
		assertEquals(List.of(1, 1, 1), result);
	}
	
	@Test
	void mapListWithTwoFunctionsHandlesNullInputs() {
		assertEquals(Lists.newArrayList(), Utils.mapList(null, null, null));
		assertEquals(Lists.newArrayList(), Utils.mapList(null, Object::toString, String::length));
		assertEquals(Lists.newArrayList(), Utils.mapList(List.of(1), null, String::length));
		assertEquals(Lists.newArrayList(), Utils.mapList(List.of(1), Object::toString, null));
	}
	
	@Test
	void mapKeyTransformsMapKeys() {
		Map<String, Integer> map = Map.of("0", 0, "1", 1, "2", 2);
		Map<Integer, Integer> result = Utils.mapKey(map, Integer::parseInt);
		
		assertEquals(Map.of(0, 0, 1, 1, 2, 2), result);
	}
	
	@Test
	void mapKeyHandlesNullInputs() {
		assertEquals(Maps.newHashMap(), Utils.mapKey(null, null));
		assertEquals(Maps.newHashMap(), Utils.mapKey(null, Object::toString));
		assertEquals(Maps.newHashMap(), Utils.mapKey(Map.of("a", 1), null));
		assertEquals(Maps.newHashMap(), Utils.mapKey(Maps.newHashMap(), Object::toString));
	}
	
	@Test
	void mapKeyFiltersNullResults() {
		Map<String, Integer> map = Map.of("valid", 1, "null", 2);
		Map<String, Integer> result = Utils.mapKey(map, key -> "null".equals(key) ? null : key.toUpperCase());
		
		assertEquals(Map.of("VALID", 1), result);
	}
	
	@Test
	void mapValueTransformsMapValues() {
		Map<Integer, String> map = Map.of(0, "0", 1, "1", 2, "2");
		Map<Integer, Integer> result = Utils.mapValue(map, Integer::parseInt);
		
		assertEquals(Map.of(0, 0, 1, 1, 2, 2), result);
	}
	
	@Test
	void mapValueHandlesNullInputs() {
		assertEquals(Maps.newHashMap(), Utils.mapValue(null, null));
		assertEquals(Maps.newHashMap(), Utils.mapValue(null, Object::toString));
		assertEquals(Maps.newHashMap(), Utils.mapValue(Map.of("a", 1), null));
		assertEquals(Maps.newHashMap(), Utils.mapValue(Maps.newHashMap(), Object::toString));
	}
	
	@Test
	void mapValueHandlesNullValues() {
		Map<String, Integer> map = Maps.newHashMap();
		map.put("key", null);
		Map<String, String> result = Utils.mapValue(map, value -> value == null ? "null" : value.toString());
		
		assertEquals(Map.of("key", "null"), result);
	}
	
	@Test
	void hasDuplicatesInArray() {
		assertFalse(Utils.hasDuplicates((Integer[]) null));
		assertFalse(Utils.hasDuplicates(new Integer[] {}));
		assertFalse(Utils.hasDuplicates(new Integer[] { 1, 2, 3, 4 }));
		assertTrue(Utils.hasDuplicates(new Integer[] { 1, 2, 3, 3 }));
		assertTrue(Utils.hasDuplicates(new Integer[] { 1, 1, 2, 3 }));
		assertTrue(Utils.hasDuplicates(new String[] { "a", "b", "a" }));
	}
	
	@Test
	void hasDuplicatesInList() {
		assertFalse(Utils.hasDuplicates((List<Integer>) null));
		assertFalse(Utils.hasDuplicates(Lists.newArrayList()));
		assertFalse(Utils.hasDuplicates(List.of(1, 2, 3, 4)));
		assertTrue(Utils.hasDuplicates(List.of(1, 2, 3, 3)));
		assertTrue(Utils.hasDuplicates(List.of(1, 1, 2, 3)));
		assertTrue(Utils.hasDuplicates(List.of("a", "b", "a")));
	}
	
	@Test
	void hasDuplicatesOfSpecificObjectInArray() {
		assertFalse(Utils.hasDuplicates(1, (Integer[]) null));
		assertFalse(Utils.hasDuplicates(1, new Integer[] {}));
		assertFalse(Utils.hasDuplicates(1, new Integer[] { 2, 3, 4 }));
		assertFalse(Utils.hasDuplicates(1, new Integer[] { 1, 2, 3 }));
		assertTrue(Utils.hasDuplicates(1, new Integer[] { 1, 1, 2 }));
		assertTrue(Utils.hasDuplicates(null, new Integer[] { null, null, 1 }));
	}
	
	@Test
	void hasDuplicatesOfSpecificObjectInList() {
		assertFalse(Utils.hasDuplicates(1, (List<Integer>) null));
		assertFalse(Utils.hasDuplicates(1, Lists.newArrayList()));
		assertFalse(Utils.hasDuplicates(1, List.of(2, 3, 4)));
		assertFalse(Utils.hasDuplicates(1, List.of(1, 2, 3)));
		assertTrue(Utils.hasDuplicates(1, List.of(1, 1, 2)));
		
		List<Integer> listWithNulls = Lists.newArrayList();
		listWithNulls.add(null);
		listWithNulls.add(null);
		listWithNulls.add(1);
		assertTrue(Utils.hasDuplicates(null, listWithNulls));
	}
	
	@Test
	void warpNullToWithValue() {
		assertEquals("fallback", Utils.warpNullTo(null, "fallback"));
		assertEquals("value", Utils.warpNullTo("value", "fallback"));
		assertEquals(42, Utils.warpNullTo(null, 42));
		assertEquals(100, Utils.warpNullTo(100, 42));
	}
	
	@Test
	void warpNullToRejectNullFallback() {
		assertThrows(NullPointerException.class, () -> Utils.warpNullTo("", (String) null));
		assertThrows(NullPointerException.class, () -> Utils.warpNullTo(null, (String) null));
	}
	
	@Test
	void warpNullToWithSupplier() {
		assertEquals("supplied", Utils.warpNullTo(null, () -> "supplied"));
		assertEquals("value", Utils.warpNullTo("value", () -> "supplied"));
		assertEquals(0, Utils.warpNullTo(null, () -> 0));
		assertEquals(1, Utils.warpNullTo(1, () -> 0));
	}
	
	@Test
	void warpNullToRejectsNullSupplier() {
		assertThrows(NullPointerException.class, () -> Utils.warpNullTo("", null));
		assertThrows(NullPointerException.class, () -> Utils.warpNullTo(null, null));
	}
	
	@Test
	void executeIfNotNullRunsActionForNonNull() {
		List<String> results = Lists.newArrayList();
		Utils.executeIfNotNull("test", results::add);
		assertEquals(List.of("test"), results);
		
		Utils.executeIfNotNull(42, value -> results.add(value.toString()));
		assertEquals(List.of("test", "42"), results);
	}
	
	@Test
	void executeIfNotNullIgnoresNull() {
		List<String> results = Lists.newArrayList();
		Utils.executeIfNotNull(null, value -> results.add(value.toString()));
		assertTrue(results.isEmpty());
	}
	
	@Test
	void executeIfNotNullRejectsNullAction() {
		assertThrows(NullPointerException.class, () -> Utils.executeIfNotNull("value", null));
	}
	
	@Test
	void systemRandomCreatesValidGenerator() {
		Random random = Utils.systemRandom();
		assertNotNull(random);
		
		Random another = Utils.systemRandom();
		assertNotNull(another);
		assertNotSame(random, another);
	}
	
	@Test
	void getRandomFromArray() {
		Random rng = new Random(12345);
		Integer[] singleElement = { 42 };
		Integer[] multipleElements = { 1, 2, 3, 4, 5 };
		
		assertEquals(42, Utils.getRandom(rng, singleElement));
		
		for (int i = 0; i < 100; i++) {
			Integer result = Utils.getRandom(rng, multipleElements);
			assertTrue(List.of(multipleElements).contains(result));
		}
	}
	
	@Test
	void getRandomFromArrayRejectsInvalidInputs() {
		Random rng = new Random();
		assertThrows(NullPointerException.class, () -> Utils.getRandom(null, new Integer[] { 1 }));
		assertThrows(NullPointerException.class, () -> Utils.getRandom(rng, (Integer[]) null));
		assertThrows(IllegalArgumentException.class, () -> Utils.getRandom(rng, new Integer[] {}));
	}
	
	@Test
	void getRandomFromList() {
		Random rng = new Random(12345);
		List<Integer> singleElement = List.of(42);
		List<Integer> multipleElements = List.of(1, 2, 3, 4, 5);
		
		assertEquals(42, Utils.getRandom(rng, singleElement));
		
		for (int i = 0; i < 100; i++) {
			Integer result = Utils.getRandom(rng, multipleElements);
			assertTrue(multipleElements.contains(result));
		}
	}
	
	@Test
	void getRandomFromListRejectsInvalidInputs() {
		Random rng = new Random();
		assertThrows(NullPointerException.class, () -> Utils.getRandom(null, List.of(1)));
		assertThrows(NullPointerException.class, () -> Utils.getRandom(rng, (List<Integer>) null));
		assertThrows(IllegalArgumentException.class, () -> Utils.getRandom(rng, Lists.newArrayList()));
	}
	
	@Test
	void getRandomSafeFromArray() {
		Random rng = new Random(12345);
		Integer[] singleElement = { 42 };
		Integer[] multipleElements = { 1, 2, 3, 4, 5 };
		
		assertEquals(Optional.of(42), Utils.getRandomSafe(rng, singleElement));
		assertEquals(Optional.empty(), Utils.getRandomSafe(rng, (Integer[]) null));
		assertEquals(Optional.empty(), Utils.getRandomSafe(rng, new Integer[] {}));
		
		for (int i = 0; i < 100; i++) {
			Optional<Integer> result = Utils.getRandomSafe(rng, multipleElements);
			assertTrue(result.isPresent());
			assertTrue(List.of(multipleElements).contains(result.get()));
		}
	}
	
	@Test
	void getRandomSafeFromArrayRejectsNullRng() {
		assertThrows(NullPointerException.class, () -> Utils.getRandomSafe(null, new Integer[] { 1 }));
	}
	
	@Test
	void getRandomSafeFromList() {
		Random rng = new Random(12345);
		List<Integer> singleElement = List.of(42);
		List<Integer> multipleElements = List.of(1, 2, 3, 4, 5);
		
		assertEquals(Optional.of(42), Utils.getRandomSafe(rng, singleElement));
		assertEquals(Optional.empty(), Utils.getRandomSafe(rng, (List<Integer>) null));
		assertEquals(Optional.empty(), Utils.getRandomSafe(rng, Lists.newArrayList()));
		
		for (int i = 0; i < 100; i++) {
			Optional<Integer> result = Utils.getRandomSafe(rng, multipleElements);
			assertTrue(result.isPresent());
			assertTrue(multipleElements.contains(result.get()));
		}
	}
	
	@Test
	void getRandomSafeFromListRejectsNullRng() {
		assertThrows(NullPointerException.class, () -> Utils.getRandomSafe(null, List.of(1)));
	}
	
	@Test
	void throwSneakyRethrowsException() {
		RuntimeException runtime = new RuntimeException("test");
		RuntimeException thrown = assertThrows(RuntimeException.class, () -> Utils.throwSneaky(runtime));
		assertSame(runtime, thrown);
		
		IllegalArgumentException illegal = new IllegalArgumentException("illegal");
		IllegalArgumentException thrownIllegal = assertThrows(IllegalArgumentException.class, () -> Utils.throwSneaky(illegal));
		assertSame(illegal, thrownIllegal);
	}
	
	@Test
	void throwSneakyRejectsNull() {
		assertThrows(NullPointerException.class, () -> Utils.throwSneaky(null));
	}
	
	@Test
	void emptyUuidConstant() {
		assertEquals("00000000-0000-0000-0000-000000000000", Utils.EMPTY_UUID.toString());
		assertTrue(Utils.isEmpty(Utils.EMPTY_UUID));
	}
}
