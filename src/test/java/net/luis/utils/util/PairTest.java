package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class PairTest {
	
	@Test
	void of() {
		assertNotNull(Pair.of(1, 2));
	}
	
	@Test
	void getFirst() {
		assertEquals(1, Pair.of(1, 2).getFirst());
		assertNotEquals(2, Pair.of(1, 2).getFirst());
	}
	
	@Test
	void getSecond() {
		assertEquals(2, Pair.of(1, 2).getSecond());
		assertNotEquals(1, Pair.of(1, 2).getSecond());
	}
	
	@Test
	void swap() {
		assertEquals(Pair.of(2, 1), Pair.of(1, 2).swap());
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).swap());
	}
	
	@Test
	void withFirst() {
		assertEquals(Pair.of(3, 2), Pair.of(1, 2).withFirst(3));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).withFirst(3));
	}
	
	@Test
	void withSecond() {
		assertEquals(Pair.of(1, 3), Pair.of(1, 2).withSecond(3));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).withSecond(3));
	}
	
	@Test
	void mapFirst() {
		assertEquals(Pair.of(2, 2), Pair.of(1, 2).mapFirst(i -> i + 1));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).mapFirst(i -> i + 1));
	}
	
	@Test
	void mapSecond() {
		assertEquals(Pair.of(1, 3), Pair.of(1, 2).mapSecond(i -> i + 1));
		assertNotEquals(Pair.of(1, 2), Pair.of(1, 2).mapSecond(i -> i + 1));
	}
}