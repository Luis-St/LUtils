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
	void constructEmptyCollection() {
		WeightCollection<String> collection = new WeightCollection<>();
		
		assertTrue(collection.isEmpty());
		assertEquals(0, collection.getSize());
		assertEquals(0, collection.getTotal());
	}
	
	@Test
	void constructWithRandom() {
		Random random = new Random(42);
		WeightCollection<String> collection = new WeightCollection<>(random);
		
		assertTrue(collection.isEmpty());
		assertEquals(0, collection.getSize());
		assertEquals(0, collection.getTotal());
	}
	
	@Test
	void constructWithNullRandom() {
		assertThrows(NullPointerException.class, () -> new WeightCollection<>(null));
	}
	
	@Test
	void addSingleElement() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(50, "A");
		
		assertEquals(1, collection.getSize());
		assertEquals(50, collection.getTotal());
		assertFalse(collection.isEmpty());
		assertTrue(collection.contains("A"));
	}
	
	@Test
	void addMultipleElements() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(30, "A");
		collection.add(20, "B");
		collection.add(50, "C");
		
		assertEquals(3, collection.getSize());
		assertEquals(100, collection.getTotal());
		assertTrue(collection.contains("A"));
		assertTrue(collection.contains("B"));
		assertTrue(collection.contains("C"));
	}
	
	@Test
	void addDuplicateElements() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(25, "A");
		collection.add(25, "A");
		
		assertEquals(2, collection.getSize());
		assertEquals(50, collection.getTotal());
		assertTrue(collection.contains("A"));
	}
	
	@Test
	void addElementWithZeroWeight() {
		WeightCollection<String> collection = new WeightCollection<>();
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> collection.add(0, "A"));
		assertTrue(exception.getMessage().contains("greater than 0"));
	}
	
	@Test
	void addElementWithNegativeWeight() {
		WeightCollection<String> collection = new WeightCollection<>();
		
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> collection.add(-5, "A"));
		assertTrue(exception.getMessage().contains("greater than 0"));
	}
	
	@Test
	void addNullElement() {
		WeightCollection<String> collection = new WeightCollection<>();
		
		assertThrows(NullPointerException.class, () -> collection.add(50, null));
	}
	
	@Test
	void removeExistingElement() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(30, "A");
		collection.add(20, "B");
		collection.add(50, "C");
		
		boolean removed = collection.remove("B");
		
		assertTrue(removed);
		assertEquals(2, collection.getSize());
		assertEquals(80, collection.getTotal());
		assertFalse(collection.contains("B"));
		assertTrue(collection.contains("A"));
		assertTrue(collection.contains("C"));
	}
	
	@Test
	void removeNonExistingElement() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(50, "A");
		
		boolean removed = collection.remove("B");
		
		assertFalse(removed);
		assertEquals(1, collection.getSize());
		assertEquals(50, collection.getTotal());
	}
	
	@Test
	void removeNullElement() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(50, "A");
		
		boolean removed = collection.remove(null);
		
		assertFalse(removed);
		assertEquals(1, collection.getSize());
		assertEquals(50, collection.getTotal());
	}
	
	@Test
	void removeFromEmptyCollection() {
		WeightCollection<String> collection = new WeightCollection<>();
		
		boolean removed = collection.remove("A");
		
		assertFalse(removed);
		assertTrue(collection.isEmpty());
	}
	
	@Test
	void removeAllElements() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(30, "A");
		collection.add(20, "B");
		
		collection.remove("A");
		collection.remove("B");
		
		assertTrue(collection.isEmpty());
		assertEquals(0, collection.getSize());
		assertEquals(0, collection.getTotal());
	}
	
	@Test
	void containsExistingElement() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(50, "A");
		
		assertTrue(collection.contains("A"));
	}
	
	@Test
	void containsNonExistingElement() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(50, "A");
		
		assertFalse(collection.contains("B"));
	}
	
	@Test
	void containsNullElement() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(50, "A");
		
		assertFalse(collection.contains(null));
	}
	
	@Test
	void clearEmptyCollection() {
		WeightCollection<String> collection = new WeightCollection<>();
		
		assertDoesNotThrow(collection::clear);
		assertTrue(collection.isEmpty());
	}
	
	@Test
	void clearNonEmptyCollection() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(30, "A");
		collection.add(20, "B");
		
		collection.clear();
		
		assertTrue(collection.isEmpty());
		assertEquals(0, collection.getSize());
		assertEquals(0, collection.getTotal());
		assertFalse(collection.contains("A"));
		assertFalse(collection.contains("B"));
	}
	
	@Test
	void nextFromSingleElement() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(100, "A");
		
		assertEquals("A", collection.next());
	}
	
	@Test
	void nextFromMultipleElements() {
		WeightCollection<String> collection = new WeightCollection<>(new Random(42));
		collection.add(50, "A");
		collection.add(30, "B");
		collection.add(20, "C");
		
		String result = collection.next();
		
		assertTrue("A".equals(result) || "B".equals(result) || "C".equals(result));
	}
	
	@Test
	void nextFromEmptyCollection() {
		WeightCollection<String> collection = new WeightCollection<>();
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, collection::next);
		assertTrue(exception.getMessage().contains("empty"));
	}
	
	@Test
	void nextWithDeterministicRandom() {
		WeightCollection<String> collection = new WeightCollection<>(new Random(0));
		collection.add(50, "A");
		collection.add(50, "B");
		
		String first = collection.next();
		assertNotNull(first);
		assertTrue("A".equals(first) || "B".equals(first));
	}
	
	@Test
	void isEmptyInitially() {
		WeightCollection<String> collection = new WeightCollection<>();
		
		assertTrue(collection.isEmpty());
	}
	
	@Test
	void isEmptyAfterAddingElements() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(50, "A");
		
		assertFalse(collection.isEmpty());
	}
	
	@Test
	void isEmptyAfterClearing() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(50, "A");
		collection.clear();
		
		assertTrue(collection.isEmpty());
	}
	
	@Test
	void sizeTracking() {
		WeightCollection<String> collection = new WeightCollection<>();
		assertEquals(0, collection.getSize());
		
		collection.add(25, "A");
		assertEquals(1, collection.getSize());
		
		collection.add(25, "B");
		assertEquals(2, collection.getSize());
		
		collection.remove("A");
		assertEquals(1, collection.getSize());
		
		collection.clear();
		assertEquals(0, collection.getSize());
	}
	
	@Test
	void totalWeightTracking() {
		WeightCollection<String> collection = new WeightCollection<>();
		assertEquals(0, collection.getTotal());
		
		collection.add(30, "A");
		assertEquals(30, collection.getTotal());
		
		collection.add(20, "B");
		assertEquals(50, collection.getTotal());
		
		collection.remove("A");
		assertEquals(20, collection.getTotal());
		
		collection.clear();
		assertEquals(0, collection.getTotal());
	}
	
	@Test
	void toStringRepresentation() {
		WeightCollection<String> collection = new WeightCollection<>();
		collection.add(50, "A");
		
		String result = collection.toString();
		
		assertNotNull(result);
		assertTrue(result.contains("A"));
	}
	
	@Test
	void equalsForSameObjects() {
		WeightCollection<String> collection1 = new WeightCollection<>(new Random(42));
		WeightCollection<String> collection2 = new WeightCollection<>(new Random(42));
		
		collection1.add(50, "A");
		collection2.add(50, "A");
		
		assertEquals(collection1, collection2);
	}
	
	@Test
	void hashCodeForSameObjects() {
		WeightCollection<String> collection1 = new WeightCollection<>(new Random(42));
		WeightCollection<String> collection2 = new WeightCollection<>(new Random(42));
		
		collection1.add(50, "A");
		collection2.add(50, "A");
		
		if (collection1.equals(collection2)) {
			assertEquals(collection1.hashCode(), collection2.hashCode());
		}
	}
}
