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
	void constructor() {
		assertDoesNotThrow(() -> new SortedList<>());
		assertDoesNotThrow(() -> new SortedList<>((Comparator<?>) null));
		assertDoesNotThrow(() -> new SortedList<>("A", "B"));
		assertThrows(NullPointerException.class, () -> new SortedList<>((String[]) null));
		assertDoesNotThrow(() -> new SortedList<>(null, "A", "B"));
		assertDoesNotThrow(() -> new SortedList<>(Comparator.reverseOrder(), "A", "B"));
		assertThrows(NullPointerException.class, () -> new SortedList<>(null, (String[]) null));
		assertDoesNotThrow(() -> new SortedList<>(List.of()));
		assertThrows(NullPointerException.class, () -> new SortedList<>((List<?>) null));
		assertDoesNotThrow(() -> new SortedList<>(List.of("A", "B"), null));
		assertThrows(NullPointerException.class, () -> new SortedList<>((List<?>) null, null));
	}
	
	@Test
	void setComparator() {
		SortedList<String> list = new SortedList<>("A", "B");
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		list.setComparator(Comparator.reverseOrder());
		assertEquals("B", list.get(0));
		assertEquals("A", list.get(1));
	}
	
	@Test
	void set() {
		SortedList<String> list = new SortedList<>();
		list.add("A");
		list.set(0, "B");
		assertThrows(NullPointerException.class, () -> list.set(2, null));
		assertEquals(1, list.size());
		assertEquals("B", list.get(0));
		list.set(0, "C");
		assertEquals(1, list.size());
		assertEquals("C", list.get(0));
	}
	
	@Test
	void add() {
		SortedList<String> list = new SortedList<>();
		list.add("A");
		list.add("B");
		assertThrows(NullPointerException.class, () -> list.add(null));
		assertEquals(2, list.size());
		list.add("C");
		assertEquals(3, list.size());
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertEquals("C", list.get(2));
	}
	
	@Test
	void remove() {
		SortedList<String> list = new SortedList<>();
		list.add("A");
		list.add("B");
		assertEquals(2, list.size());
		assertEquals("A", list.get(0));
		assertTrue(list.remove("A"));
		assertEquals(1, list.size());
		assertEquals("B", list.get(0));
	}
	
	@Test
	void get() {
		SortedList<String> list = new SortedList<>();
		list.add("A");
		list.add("B");
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertThrows(IndexOutOfBoundsException.class, () -> list.get(2));
	}
	
	@Test
	void size() {
		SortedList<String> list = new SortedList<>();
		assertEquals(0, list.size());
		list.add("A");
		assertEquals(1, list.size());
		list.add("B");
		assertEquals(2, list.size());
		list.add("C");
		assertEquals(3, list.size());
	}
	
	@Test
	void sort() {
		SortedList<String> list = new SortedList<>();
		list.add("A");
		list.add("B");
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		list.sort(Comparator.reverseOrder());
		assertEquals("A", list.get(0));
		assertEquals("B", list.get(1));
		assertDoesNotThrow(() -> list.sort(null));
	}
}