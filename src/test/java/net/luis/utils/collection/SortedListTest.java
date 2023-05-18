package net.luis.utils.collection;

import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Luis-St
 *
 */

class SortedListTest {
	
	@Test
	void setComparator() {
		SortedList<String> sortedList = new SortedList<>("A", "B");
		assertEquals("A", sortedList.get(0));
		assertEquals("B", sortedList.get(1));
		sortedList.setComparator(Comparator.reverseOrder());
		assertEquals("B", sortedList.get(0));
		assertEquals("A", sortedList.get(1));
	}
	
	@Test
	void set() {
		SortedList<String> sortedList = new SortedList<>();
		sortedList.add("A");
		sortedList.add("B");
		sortedList.set(0, "C");
		assertEquals(2, sortedList.size());
		assertEquals("B", sortedList.get(0));
		assertEquals("C", sortedList.get(1));
	}
	
	@Test
	void add() {
		SortedList<String> sortedList = new SortedList<>();
		sortedList.add("B");
		sortedList.add("A");
		assertEquals(2, sortedList.size());
	}
	
	@Test
	void remove() {
		SortedList<String> sortedList = new SortedList<>();
		sortedList.add("A");
		sortedList.add("B");
		sortedList.remove("A");
		assertEquals(1, sortedList.size());
		assertEquals("B", sortedList.get(0));
	}
	
	@Test
	void get() {
		SortedList<String> sortedList = new SortedList<>();
		sortedList.add("B");
		sortedList.add("A");
		assertEquals("A", sortedList.get(0));
		assertEquals("B", sortedList.get(1));
	}
	
	@Test
	void size() {
		SortedList<String> sortedList = new SortedList<>();
		sortedList.add("A");
		assertEquals(1, sortedList.size());
		sortedList.add("B");
		assertEquals(2, sortedList.size());
		sortedList.add("C");
		assertEquals(3, sortedList.size());
	}
	
	@Test
	void sort() {
		SortedList<String> sortedList = new SortedList<>();
		sortedList.add("B");
		sortedList.add("A");
		assertEquals("A", sortedList.get(0));
		assertEquals("B", sortedList.get(1));
		sortedList.sort(Comparator.reverseOrder());
		assertEquals("A", sortedList.get(0));
		assertEquals("B", sortedList.get(1));
	}
}