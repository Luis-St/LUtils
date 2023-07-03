package net.luis.utils.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class UtilsTest {
	
	@Test
	void isEmpty() {
		assertTrue(Utils.isEmpty(Utils.EMPTY_UUID));
		assertTrue(Utils.isEmpty(UUID.fromString("00000000-0000-0000-0000-000000000000")));
		assertFalse(Utils.isEmpty(UUID.randomUUID()));
	}
	
	@Test
	void make() {
		assertThrows(NullPointerException.class, () -> Utils.make(null, (list) -> {}));
		assertEquals(Utils.make(Lists.newArrayList(), (list) -> list.addAll(Arrays.asList(0, 1, 2, 3, 4))), Lists.newArrayList(0, 1, 2, 3, 4));
	}
	
	@Test
	void mapToList() {
		assertEquals(Utils.mapToList(Maps.newHashMap(Map.of("0", 0, "1", 1, "2", 2)), (key, value) -> value), Lists.newArrayList(0, 1, 2));
	}
	
	@Test
	void listToMap() {
		assertEquals(Utils.listToMap(Lists.newArrayList(0, 1, 2), (value) -> new SimpleEntry<>(String.valueOf(value), value)), Maps.newHashMap(Map.of("0", 0, "1", 1, "2", 2)));
	}
	
	@Test
	void mapList() {
		assertEquals(Utils.mapList(Lists.newArrayList(0, 1, 2), String::valueOf), Lists.newArrayList("0", "1", "2"));
		assertEquals(Utils.mapList(Lists.newArrayList("0", "1", "2"), Integer::parseInt, (value) -> value * 2), Lists.newArrayList(0, 2, 4));
	}
	
	@Test
	void mapKey() {
		assertEquals(Utils.mapKey(Map.of("0", 0, "1", 1, "2", 2), Integer::parseInt), Map.of(0, 0, 1, 1, 2, 2));
	}
	
	@Test
	void mapValue() {
		assertEquals(Utils.mapValue(Map.of(0, "0", 1, "1", 2, "2"), Integer::parseInt), Map.of(0, 0, 1, 1, 2, 2));
	}
	
	@Test
	void mapIf() {
		assertThrows(NullPointerException.class, () -> Utils.mapIf(null, String::isBlank, String::trim));
		assertEquals(Utils.mapIf(" ", String::isBlank, String::trim), "");
	}
	
	@Test
	void mapIfNot() {
		assertThrows(NullPointerException.class, () -> Utils.mapIfNot(null, String::isEmpty, (value) -> "*".repeat(4)));
		assertEquals(Utils.mapIfNot("*".repeat(4), String::isEmpty, (value) -> ""), "");
	}
	
	@Test
	void mapIfNotNull() {
		assertNull(Utils.mapIfNotNull(null, String::trim));
		assertEquals(Utils.mapIfNotNull(" runIfNotNull ", String::trim), "runIfNotNull");
	}
	
	@Test
	void hasDuplicates() {
		assertThrows(NullPointerException.class, () -> Utils.hasDuplicates((Integer[]) null));
		assertThrows(NullPointerException.class, () -> Utils.hasDuplicates((List<Integer>) null));
		assertFalse(Utils.hasDuplicates(new Integer[] {0, 1, 2, 3, 4}));
		assertTrue(Utils.hasDuplicates(new Integer[] {0, 1, 2, 3, 4, 0}));
		assertFalse(Utils.hasDuplicates(Lists.newArrayList(0, 1, 2, 3, 4)));
		assertTrue(Utils.hasDuplicates(Lists.newArrayList(0, 1, 2, 3, 4, 0)));
		
		assertFalse(Utils.hasDuplicates(4, new Integer[] {0, 1, 2, 3, 4}));
		assertTrue(Utils.hasDuplicates(4, new Integer[] {0, 1, 2, 3, 4, 4}));
		assertFalse(Utils.hasDuplicates(4, Lists.newArrayList(0, 1, 2, 3, 4)));
		assertTrue(Utils.hasDuplicates(4, Lists.newArrayList(0, 1, 2, 3, 4, 4)));
	}
	
	@Test
	void warpNullTo() {
		assertThrows(NullPointerException.class, () -> Utils.warpNullTo((String) null, (String) null));
		assertEquals(Utils.warpNullTo(null, "fallback"), "fallback");
		assertEquals(Utils.warpNullTo("value", "fallback"), "value");
		assertThrows(NullPointerException.class, () -> Utils.warpNullTo(null, null));
		assertEquals(Utils.warpNullTo(null, () -> 0), 0);
		assertEquals(Utils.warpNullTo(1, () -> 0), 1);
	}
	
	@Test
	void executeIfNotNull() {
		assertDoesNotThrow(() -> Utils.executeIfNotNull(null, (value) -> {}));
		assertDoesNotThrow(() -> Utils.executeIfNotNull(null, (value) -> {
			throw new NullPointerException();
		}));
		Utils.executeIfNotNull("value", (value) -> assertEquals(value, "value"));
	}
	
	@Test
	void getRandomSafe() {
		assertTrue(Utils.getRandomSafe(new Integer[0], new Random()).isEmpty());
		assertTrue(Utils.getRandomSafe(new Integer[] {0, 1, 2, 3, 4}, new Random()).isPresent());
		assertTrue(Utils.getRandomSafe(Lists.newArrayList(), new Random()).isEmpty());
		assertTrue(Utils.getRandomSafe(Lists.newArrayList(0, 1, 2, 3, 4), new Random()).isPresent());
	}
}