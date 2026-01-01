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

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SortedList}.<br>
 *
 * @author Luis-St
 */
class SortedListTest {
	
	@Test
	void constructEmptyList() {
		SortedList<String> list = new SortedList<>();
		
		assertTrue(list.isEmpty());
		assertEquals(0, list.size());
	}
	
	@Test
	void constructWithComparator() {
		SortedList<String> list = new SortedList<>(Comparator.<String>reverseOrder());
		
		assertTrue(list.isEmpty());
		list.add("A");
		list.add("B");
		assertEquals("B", list.get(0));
		assertEquals("A", list.get(1));
	}
	
	@Test
	void constructWithNullComparator() {
		assertDoesNotThrow(() -> new SortedList<>((Comparator<String>) null));
	}
	
	@Test
	void constructWithElements() {
		SortedList<String> list = new SortedList<>("C", "A", "B");
		
		assertEquals(3, list.size());
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("C", list.get(2));
	}
	
	@Test
	void constructWithElementsAndComparator() {
		SortedList<String> list = new SortedList<>(Comparator.reverseOrder(), "A", "B", "C");
		
		assertEquals(3, list.size());
		assertEquals("C", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("A", list.get(2));
	}
	
	@Test
	void constructWithNullElements() {
		assertThrows(NullPointerException.class, () -> new SortedList<>((String[]) null));
		assertThrows(NullPointerException.class, () -> new SortedList<>(null, (String[]) null));
	}
	
	@Test
	void constructFromList() {
		List<String> source = List.of("C", "A", "B");
		SortedList<String> list = new SortedList<>(source);
		
		assertEquals(3, list.size());
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("C", list.get(2));
	}
	
	@Test
	void constructFromListWithComparator() {
		List<String> source = List.of("A", "B", "C");
		SortedList<String> list = new SortedList<>(source, Comparator.reverseOrder());
		
		assertEquals(3, list.size());
		assertEquals("C", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("A", list.get(2));
	}
	
	@Test
	void constructFromNullList() {
		assertThrows(NullPointerException.class, () -> new SortedList<>((List<String>) null));
		assertThrows(NullPointerException.class, () -> new SortedList<>((List<String>) null, null));
	}
	
	@Test
	void setComparatorNaturalOrder() {
		SortedList<String> list = new SortedList<>("C", "A", "B");
		list.setComparator(null);
		
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("C", list.get(2));
	}
	
	@Test
	void setComparatorReverseOrder() {
		SortedList<String> list = new SortedList<>("A", "B", "C");
		list.setComparator(Comparator.reverseOrder());
		
		assertEquals("C", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("A", list.get(2));
	}
	
	@Test
	void setComparatorMultipleTimes() {
		SortedList<String> list = new SortedList<>("B", "A", "C");
		
		list.setComparator(Comparator.reverseOrder());
		assertEquals("C", list.getFirst());
		
		list.setComparator(null);
		assertEquals("A", list.getFirst());
		
		list.setComparator(Comparator.reverseOrder());
		assertEquals("C", list.getFirst());
	}
	
	@Test
	void setElementMaintainsSorting() {
		SortedList<String> list = new SortedList<>("A", "B", "C");
		String old = list.set(1, "D");
		
		assertEquals("B", old);
		assertEquals(3, list.size());
		assertEquals("A", list.get(0));
		assertEquals("C", list.get(1));
		assertEquals("D", list.get(2));
	}
	
	@Test
	void setElementWithReverseComparator() {
		SortedList<String> list = new SortedList<>(Comparator.reverseOrder(), "A", "B", "C");
		list.set(0, "Z");
		
		assertEquals("Z", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("A", list.get(2));
	}
	
	@Test
	void setNullElement() {
		SortedList<String> list = new SortedList<>("A");
		
		assertThrows(NullPointerException.class, () -> list.set(0, null));
	}
	
	@Test
	void addElementMaintainsSorting() {
		SortedList<String> list = new SortedList<>();
		list.add("C");
		list.add("A");
		list.add("B");
		
		assertEquals(3, list.size());
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("C", list.get(2));
	}
	
	@Test
	void addElementAtIndex() {
		SortedList<String> list = new SortedList<>("A", "C");
		list.add(1, "B");
		
		assertEquals(3, list.size());
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("C", list.get(2));
	}
	
	@Test
	void addDuplicateElements() {
		SortedList<String> list = new SortedList<>();
		list.add("A");
		list.add("A");
		list.add("B");
		
		assertEquals(3, list.size());
		assertEquals("A", list.get(0));
		assertEquals("A", list.get(1));
		assertEquals("B", list.get(2));
	}
	
	@Test
	void addNullElement() {
		SortedList<String> list = new SortedList<>();
		
		assertThrows(NullPointerException.class, () -> list.add(null));
		assertThrows(NullPointerException.class, () -> list.addFirst(null));
	}
	
	@Test
	void removeExistingElement() {
		SortedList<String> list = new SortedList<>("A", "B", "C");
		boolean removed = list.remove("B");
		
		assertTrue(removed);
		assertEquals(2, list.size());
		assertEquals("A", list.get(0));
		assertEquals("C", list.get(1));
	}
	
	@Test
	void removeNonExistingElement() {
		SortedList<String> list = new SortedList<>("A", "B", "C");
		boolean removed = list.remove("D");
		
		assertFalse(removed);
		assertEquals(3, list.size());
	}
	
	@Test
	void removeNullElement() {
		SortedList<String> list = new SortedList<>("A", "B", "C");
		boolean removed = list.remove(null);
		
		assertFalse(removed);
		assertEquals(3, list.size());
	}
	
	@Test
	void getElementAtValidIndex() {
		SortedList<String> list = new SortedList<>("C", "A", "B");
		
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("C", list.get(2));
	}
	
	@Test
	void getElementAtInvalidIndex() {
		SortedList<String> list = new SortedList<>("A");
		
		assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
		assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
	}
	
	@Test
	void sizeChangesWithModifications() {
		SortedList<String> list = new SortedList<>();
		assertEquals(0, list.size());
		
		list.add("A");
		assertEquals(1, list.size());
		
		list.add("B");
		assertEquals(2, list.size());
		
		list.remove("A");
		assertEquals(1, list.size());
		
		list.clear();
		assertEquals(0, list.size());
	}
	
	@Test
	void sortIgnoresProvidedComparator() {
		SortedList<String> list = new SortedList<>(Comparator.reverseOrder(), "A", "B", "C");
		
		// Should remain in reverse order despite passing natural order comparator
		list.sort(Comparator.naturalOrder());
		
		assertEquals("C", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("A", list.get(2));
	}
	
	@Test
	void sortWithNullComparator() {
		SortedList<String> list = new SortedList<>("A", "B", "C");
		
		assertDoesNotThrow(() -> list.sort(null));
		
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("C", list.get(2));
	}
}
