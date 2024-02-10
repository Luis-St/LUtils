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