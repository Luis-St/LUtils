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
	void of() {
		assertNotNull(Pair.of(1, 2));
		assertDoesNotThrow(() -> Pair.of(null, null));
		assertDoesNotThrow(() -> Pair.of(1, null));
		assertDoesNotThrow(() -> Pair.of(null, 2));
	}
	
	@Test
	void getFirst() {
		assertNull(Pair.of(null, 2).getFirst());
		assertEquals(1, Pair.of(1, 2).getFirst());
		assertNotEquals(2, Pair.of(1, 2).getFirst());
	}
	
	@Test
	void getSecond() {
		assertNull(Pair.of(1, null).getSecond());
		assertEquals(2, Pair.of(1, 2).getSecond());
		assertNotEquals(1, Pair.of(1, 2).getSecond());
	}
	
	@Test
	void swap() {
		assertEquals(Pair.of(2, 1), Pair.of(1, 2).swap());
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).swap());
	}
	
	@Test
	void mapFirst() {
		assertThrows(NullPointerException.class, () -> Pair.of(1, 2).mapFirst(null));
		assertDoesNotThrow(() -> Pair.of(1, 2).mapFirst(i -> i + 1));
		assertEquals(Pair.of(2, 2), Pair.of(1, 2).mapFirst(i -> i + 1));
		assertEquals(Pair.of(1, 1), Pair.of(null, 1).mapFirst(i -> 1));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).mapFirst(i -> i + 1));
	}
	
	@Test
	void mapSecond() {
		assertThrows(NullPointerException.class, () -> Pair.of(1, 2).mapSecond(null));
		assertDoesNotThrow(() -> Pair.of(1, 2).mapSecond(i -> i + 1));
		assertEquals(Pair.of(1, 3), Pair.of(1, 2).mapSecond(i -> i + 1));
		assertEquals(Pair.of(1, 1), Pair.of(1, null).mapSecond(i -> 1));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).mapSecond(i -> i + 1));
	}
	
	@Test
	void withFirst() {
		assertEquals(Pair.of(3, 2), Pair.of(1, 2).withFirst(3));
		assertEquals(Pair.of(null, 2), Pair.of(1, 2).withFirst(null));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).withFirst(3));
	}
	
	@Test
	void withSecond() {
		assertEquals(Pair.of(1, 3), Pair.of(1, 2).withSecond(3));
		assertEquals(Pair.of(1, null), Pair.of(1, 2).withSecond(null));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).withSecond(3));
	}
}