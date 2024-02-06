package net.luis.utils.collection;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link WeightCollection}.<br>
 *
 * @author Luis-St
 */
class WeightCollectionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new WeightCollection<>());
		assertDoesNotThrow(() -> new WeightCollection<>(new Random()));
		assertThrows(NullPointerException.class, () -> new WeightCollection<>(null));
	}
	
	@Test
	void add() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		collection.add(100, 10);
		collection.add(100, 10);
		assertEquals(2, collection.getSize());
		assertEquals(200, collection.getTotal());
		collection.add(100, 10);
		assertEquals(3, collection.getSize());
		assertEquals(300, collection.getTotal());
		assertThrows(IllegalArgumentException.class, () -> collection.add(0, 10));
		assertThrows(NullPointerException.class, () -> collection.add(100, null));
	}
	
	@Test
	void remove() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		assertDoesNotThrow(() -> collection.remove(10));
		assertFalse(collection.remove(10));
		collection.add(100, 10);
		assertTrue(collection.remove(10));
	}
	
	@Test
	void contains() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		assertFalse(collection.contains(null));
		assertFalse(collection.contains(10));
		collection.add(100, 10);
		assertTrue(collection.contains(10));
	}
	
	@Test
	void clear() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		assertDoesNotThrow(collection::clear);
		collection.add(100, 10);
		collection.add(100, 10);
		assertEquals(2, collection.getSize());
		assertDoesNotThrow(collection::clear);
		assertEquals(0, collection.getSize());
	}
	
	@Test
	void next() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		assertThrows(IllegalStateException.class, collection::next);
		collection.add(100, 10);
		assertEquals(10, collection.next());
	}
	
	@Test
	void isEmpty() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		assertTrue(collection.isEmpty());
		collection.add(100, 10);
		assertFalse(collection.isEmpty());
	}
	
	@Test
	void getSize() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		assertEquals(0, collection.getSize());
		collection.add(100, 10);
		assertEquals(1, collection.getSize());
	}
	
	@Test
	void getTotal() {
		WeightCollection<Integer> collection = new WeightCollection<>();
		assertEquals(0, collection.getTotal());
		collection.add(100, 10);
		assertEquals(100, collection.getTotal());
	}
}