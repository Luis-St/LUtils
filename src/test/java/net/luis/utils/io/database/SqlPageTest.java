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

package net.luis.utils.io.database;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlPage}.<br>
 *
 * @author Luis-St
 */
class SqlPageTest {
	
	@Test
	void constructValidPage() {
		SqlPage<String> page = new SqlPage<>(List.of("a", "b"), 0, 10, true, false);
		assertEquals(List.of("a", "b"), page.content());
		assertEquals(0, page.page());
		assertEquals(10, page.pageSize());
		assertTrue(page.hasNext());
		assertFalse(page.hasPrevious());
	}
	
	@Test
	void constructWithNullContentThrows() {
		assertThrows(NullPointerException.class, () -> new SqlPage<>(null, 0, 10, false, false));
	}
	
	@Test
	void constructWithNegativePageThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SqlPage<>(List.of("a"), -1, 10, false, false));
	}
	
	@Test
	void constructWithZeroPageSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SqlPage<>(List.of("a"), 0, 0, false, false));
	}
	
	@Test
	void constructWithNegativePageSizeThrows() {
		assertThrows(IllegalArgumentException.class, () -> new SqlPage<>(List.of("a"), 0, -1, false, false));
	}
	
	@Test
	void constructWithZeroPageBoundary() {
		SqlPage<String> page = new SqlPage<>(List.of("a"), 0, 10, false, false);
		assertEquals(0, page.page());
	}
	
	@Test
	void constructWithPageSizeOneBoundary() {
		SqlPage<String> page = new SqlPage<>(List.of("a"), 0, 1, false, false);
		assertEquals(1, page.pageSize());
	}
	
	@Test
	void constructWithEmptyContent() {
		SqlPage<String> page = new SqlPage<>(List.of(), 0, 10, false, false);
		assertTrue(page.content().isEmpty());
	}
	
	@Test
	void accessorsReturnStoredValues() {
		SqlPage<Integer> page = new SqlPage<>(List.of(1, 2, 3), 2, 25, false, true);
		assertEquals(List.of(1, 2, 3), page.content());
		assertEquals(2, page.page());
		assertEquals(25, page.pageSize());
		assertFalse(page.hasNext());
		assertTrue(page.hasPrevious());
	}
	
	@Test
	void equalPagesAreEqual() {
		SqlPage<String> first = new SqlPage<>(List.of("a", "b"), 1, 10, true, true);
		SqlPage<String> second = new SqlPage<>(List.of("a", "b"), 1, 10, true, true);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void contentIsDefensivelyCopied() {
		List<String> source = new ArrayList<>(List.of("a", "b"));
		SqlPage<String> page = new SqlPage<>(source, 0, 10, false, false);
		source.add("c");
		assertEquals(List.of("a", "b"), page.content());
	}
	
	@Test
	void contentIsUnmodifiable() {
		SqlPage<String> page = new SqlPage<>(new ArrayList<>(List.of("a")), 0, 10, false, false);
		assertThrows(UnsupportedOperationException.class, () -> page.content().add("b"));
	}
}
