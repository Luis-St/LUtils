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
	
	private static final Random RNG = new Random(0);
	private static final Map<String, Integer> MAP = Maps.newHashMap(Map.of("0", 0, "1", 1, "2", 2, "3", 3, "4", 4));
	private static final List<Integer> LIST = Lists.newArrayList(0, 1, 2, 3, 4);
	private static final Integer[] ARRAY = { 0, 1, 2, 3, 4 };
	private static final Integer[] EMPTY = {};
	
	@Test
	void isEmpty() {
		assertTrue(Utils.isEmpty(Utils.EMPTY_UUID));
		assertTrue(Utils.isEmpty(UUID.fromString("00000000-0000-0000-0000-000000000000")));
		assertFalse(Utils.isEmpty(UUID.randomUUID()));
		assertDoesNotThrow(() -> Utils.isEmpty(null));
	}
	
	@Test
	void make() {
		assertThrows(NullPointerException.class, () -> Utils.make(null, (list) -> {}));
		assertThrows(NullPointerException.class, () -> Utils.make(Lists.newArrayList(), null));
		assertEquals(Lists.newArrayList(0), Utils.make(Lists.newArrayList(), (list) -> list.add(0)));
	}
	
	@Test
	void mapToList() {
		assertDoesNotThrow(() -> Utils.mapToList(null, null));
		assertDoesNotThrow(() -> Utils.mapToList(null, (key, value) -> value));
		assertDoesNotThrow(() -> Utils.mapToList(MAP, null));
		assertEquals(Lists.newArrayList(), Utils.mapToList(null, null));
		assertEquals(Lists.newArrayList(), Utils.mapToList(null, (key, value) -> value));
		assertEquals(Lists.newArrayList(), Utils.mapToList(MAP, null));
		assertEquals(LIST, Utils.mapToList(MAP, (key, value) -> value));
	}
	
	@Test
	void listToMap() {
		assertDoesNotThrow(() -> Utils.listToMap(null, null));
		assertDoesNotThrow(() -> Utils.listToMap(null, (value) -> new SimpleEntry<>(String.valueOf(value), value)));
		assertDoesNotThrow(() -> Utils.listToMap(LIST, null));
		assertEquals(Maps.newHashMap(), Utils.listToMap(null, null));
		assertEquals(Maps.newHashMap(), Utils.listToMap(null, (value) -> new SimpleEntry<>(String.valueOf(value), value)));
		assertEquals(Maps.newHashMap(), Utils.listToMap(Lists.newArrayList(), null));
		assertEquals(MAP, Utils.listToMap(LIST, (value) -> new SimpleEntry<>(String.valueOf(value), value)));
	}
	
	@Test
	void mapList() {
		assertDoesNotThrow(() -> Utils.mapList(null, null));
		assertDoesNotThrow(() -> Utils.mapList(null, String::valueOf));
		assertDoesNotThrow(() -> Utils.mapList(Lists.newArrayList(), null));
		assertEquals(Lists.newArrayList(), Utils.mapList(null, null));
		assertEquals(Lists.newArrayList(), Utils.mapList(null, String::valueOf));
		assertEquals(Lists.newArrayList(), Utils.mapList(Lists.newArrayList(), null));
		assertEquals(Lists.newArrayList("0", "1", "2", "3", "4"), Utils.mapList(LIST, String::valueOf));
		
		assertDoesNotThrow(() -> Utils.mapList(null, null, null));
		assertDoesNotThrow(() -> Utils.mapList(null, String::valueOf, (value) -> value + "!"));
		assertDoesNotThrow(() -> Utils.mapList(Lists.newArrayList(), null, (value) -> value + "!"));
		assertDoesNotThrow(() -> Utils.mapList(Lists.newArrayList(), String::valueOf, null));
		assertEquals(Lists.newArrayList(), Utils.mapList(null, null, null));
		assertEquals(Lists.newArrayList(), Utils.mapList(null, String::valueOf, (value) -> value + "!"));
		assertEquals(Lists.newArrayList(), Utils.mapList(Lists.newArrayList(), null, (value) -> value + "!"));
		assertEquals(Lists.newArrayList(), Utils.mapList(Lists.newArrayList(), String::valueOf, null));
		assertEquals(Lists.newArrayList(0, 2, 4), Utils.mapList(Lists.newArrayList("0", "1", "2"), Integer::parseInt, (value) -> value * 2));
	}
	
	@Test
	void mapKey() {
		assertDoesNotThrow(() -> Utils.mapKey(null, null));
		assertDoesNotThrow(() -> Utils.mapKey(null, Object::toString));
		assertDoesNotThrow(() -> Utils.mapKey(Maps.newHashMap(), null));
		assertEquals(Maps.newHashMap(), Utils.mapKey(null, null));
		assertEquals(Maps.newHashMap(), Utils.mapKey(null, Object::toString));
		assertEquals(Maps.newHashMap(), Utils.mapKey(Maps.newHashMap(), null));
		assertEquals(Map.of(0, 0, 1, 1, 2, 2, 3, 3, 4, 4), Utils.mapKey(MAP, Integer::parseInt));
	}
	
	@Test
	void mapValue() {
		assertDoesNotThrow(() -> Utils.mapValue(null, null));
		assertDoesNotThrow(() -> Utils.mapValue(null, Object::toString));
		assertDoesNotThrow(() -> Utils.mapValue(Maps.newHashMap(), null));
		assertEquals(Maps.newHashMap(), Utils.mapValue(null, null));
		assertEquals(Maps.newHashMap(), Utils.mapValue(null, Object::toString));
		assertEquals(Maps.newHashMap(), Utils.mapValue(Maps.newHashMap(), null));
		assertEquals(Map.of(0, 0, 1, 1, 2, 2), Utils.mapValue(Map.of(0, "0", 1, "1", 2, "2"), Integer::parseInt));
	}
	
	@Test
	void hasDuplicates() {
		//region Setup
		List<Integer> list = Lists.newArrayList(LIST);
		list.add(4);
		Integer[] array = { 0, 1, 2, 3, 4, 4 };
		//endregion
		assertDoesNotThrow(() -> Utils.hasDuplicates((Integer[]) null));
		assertFalse(Utils.hasDuplicates((Integer[]) null));
		assertFalse(Utils.hasDuplicates(EMPTY));
		assertFalse(Utils.hasDuplicates(ARRAY));
		assertTrue(Utils.hasDuplicates(array));
		
		assertDoesNotThrow(() -> Utils.hasDuplicates((List<Integer>) null));
		assertFalse(Utils.hasDuplicates((List<Integer>) null));
		assertFalse(Utils.hasDuplicates(Lists.newArrayList()));
		assertFalse(Utils.hasDuplicates(LIST));
		assertTrue(Utils.hasDuplicates(list));
		
		assertDoesNotThrow(() -> Utils.hasDuplicates(null, (Integer[]) null));
		assertDoesNotThrow(() -> Utils.hasDuplicates(null, EMPTY));
		assertDoesNotThrow(() -> Utils.hasDuplicates(4, (Integer[]) null));
		assertFalse(Utils.hasDuplicates(null, (Integer[]) null));
		assertFalse(Utils.hasDuplicates(null, EMPTY));
		assertFalse(Utils.hasDuplicates(4, (Integer[]) null));
		assertFalse(Utils.hasDuplicates(null, ARRAY));
		assertFalse(Utils.hasDuplicates(4, EMPTY));
		assertFalse(Utils.hasDuplicates(4, ARRAY));
		assertTrue(Utils.hasDuplicates(4, array));
		
		assertDoesNotThrow(() -> Utils.hasDuplicates(null, (List<Integer>) null));
		assertDoesNotThrow(() -> Utils.hasDuplicates(null, Lists.newArrayList()));
		assertDoesNotThrow(() -> Utils.hasDuplicates(4, (List<Integer>) null));
		assertFalse(Utils.hasDuplicates(null, (List<Integer>) null));
		assertFalse(Utils.hasDuplicates(null, Lists.newArrayList()));
		assertFalse(Utils.hasDuplicates(4, (List<Integer>) null));
		assertFalse(Utils.hasDuplicates(4, Lists.newArrayList()));
		assertFalse(Utils.hasDuplicates(4, LIST));
		assertTrue(Utils.hasDuplicates(4, list));
	}
	
	@Test
	void warpNullTo() {
		assertThrows(NullPointerException.class, () -> Utils.warpNullTo("", (String) null));
		assertThrows(NullPointerException.class, () -> Utils.warpNullTo(null, (String) null));
		assertEquals("fallback", Utils.warpNullTo(null, "fallback"));
		assertEquals("value", Utils.warpNullTo("value", "fallback"));
		
		assertThrows(NullPointerException.class, () -> Utils.warpNullTo("", null));
		assertThrows(NullPointerException.class, () -> Utils.warpNullTo(null, null));
		assertEquals(0, Utils.warpNullTo(null, () -> 0));
		assertEquals(1, Utils.warpNullTo(1, () -> 0));
	}
	
	@Test
	void executeIfNotNull() {
		assertThrows(NullPointerException.class, () -> Utils.executeIfNotNull("value", null));
		assertDoesNotThrow(() -> Utils.executeIfNotNull(null, (value) -> {}));
		assertDoesNotThrow(() -> Utils.executeIfNotNull(null, (value) -> {
			throw new NullPointerException();
		}));
		Utils.executeIfNotNull("value", (value) -> assertEquals("value", value));
	}
	
	@Test
	void systemRandom() {
		assertNotNull(Utils.systemRandom());
	}
	
	@Test
	void getRandom() {
		//region Setup
		Integer[] singleArray = { 0 };
		List<Integer> singleList = Lists.newArrayList(0);
		//endregion
		assertThrows(NullPointerException.class, () -> Utils.getRandom(null, (Integer[]) null));
		assertThrows(NullPointerException.class, () -> Utils.getRandom(RNG, (Integer[]) null));
		assertThrows(NullPointerException.class, () -> Utils.getRandom(null, ARRAY));
		assertThrows(IllegalArgumentException.class, () -> Utils.getRandom(RNG, EMPTY));
		assertEquals(0, Utils.getRandom(RNG, singleArray));
		for (int i = 0; i < 1000; i++) {
			int value = assertDoesNotThrow(() -> Utils.getRandom(RNG, ARRAY));
			assertTrue(4 >= value && value >= 0);
		}
		
		assertThrows(NullPointerException.class, () -> Utils.getRandom(null, (List<Integer>) null));
		assertThrows(NullPointerException.class, () -> Utils.getRandom(RNG, (List<Integer>) null));
		assertThrows(NullPointerException.class, () -> Utils.getRandom(null, LIST));
		assertThrows(IllegalArgumentException.class, () -> Utils.getRandom(RNG, Lists.newArrayList()));
		assertEquals(0, Utils.getRandom(RNG, singleList));
		for (int i = 0; i < 1000; i++) {
			int value = assertDoesNotThrow(() -> Utils.getRandom(RNG, LIST));
			assertTrue(4 >= value && value >= 0);
		}
	}
	
	@Test
	void getRandomSafe() {
		//region Setup
		Integer[] singleArray = { 0 };
		List<Integer> singleList = Lists.newArrayList(0);
		//endregion
		assertThrows(NullPointerException.class, () -> Utils.getRandomSafe(null, (Integer[]) null));
		assertDoesNotThrow(() -> Utils.getRandomSafe(RNG, (Integer[]) null));
		assertEquals(Optional.empty(), Utils.getRandomSafe(RNG, EMPTY));
		assertEquals(Optional.of(0), Utils.getRandomSafe(RNG, singleArray));
		for (int i = 0; i < 1000; i++) {
			Optional<Integer> value = assertDoesNotThrow(() -> Utils.getRandomSafe(RNG, ARRAY));
			assertTrue(value.isPresent());
			assertTrue(4 >= value.get() && value.get() >= 0);
		}
		
		assertThrows(NullPointerException.class, () -> Utils.getRandomSafe(null, (List<Integer>) null));
		assertDoesNotThrow(() -> Utils.getRandomSafe(RNG, (List<Integer>) null));
		assertEquals(Optional.empty(), Utils.getRandomSafe(RNG, Lists.newArrayList()));
		assertEquals(Optional.of(0), Utils.getRandomSafe(RNG, singleList));
		for (int i = 0; i < 1000; i++) {
			Optional<Integer> value = assertDoesNotThrow(() -> Utils.getRandomSafe(RNG, LIST));
			assertTrue(value.isPresent());
			assertTrue(4 >= value.get() && value.get() >= 0);
		}
	}
	
	@Test
	void throwSneaky() {
		assertThrows(NullPointerException.class, () -> Utils.throwSneaky(null));
		assertThrows(IllegalMonitorStateException.class, () -> Utils.throwSneaky(new IllegalMonitorStateException()));
	}
}
