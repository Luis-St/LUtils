package net.luis.utils.collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class WeightCollectionTest {
	
	@Test
	void add() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		assertDoesNotThrow(() -> {
			collection.add(100, 10);
		});
		assertThrows(IllegalArgumentException.class, () -> {
			collection.add(0, 10);
		});
	}
	
	@Test
	void next() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		assertThrows(RuntimeException.class, collection::next);
		collection.add(100, 10);
		assertEquals(10, collection.next());
	}
	
	@Test
	void isEmpty() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		assertTrue(collection.isEmpty());
	}
}