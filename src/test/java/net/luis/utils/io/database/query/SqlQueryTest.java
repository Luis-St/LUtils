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

package net.luis.utils.io.database.query;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlQuery}.<br>
 *
 * @author Luis-St
 */
class SqlQueryTest {
	
	@Test
	void copyAndAddWithNullList() {
		assertThrows(NullPointerException.class, () -> SqlQuery.copyAndAdd(null, "x"));
	}
	
	@Test
	void copyAndAddWithNullElement() {
		assertThrows(NullPointerException.class, () -> SqlQuery.copyAndAdd(List.of(), null));
	}
	
	@Test
	void copyAndAddAllWithNullList() {
		assertThrows(NullPointerException.class, () -> SqlQuery.copyAndAddAll(null, List.of("x")));
	}
	
	@Test
	void copyAndAddAllWithNullElements() {
		assertThrows(NullPointerException.class, () -> SqlQuery.copyAndAddAll(List.of(), null));
	}
	
	@Test
	void copyAndAddToEmptyList() {
		List<String> result = SqlQuery.copyAndAdd(List.of(), "x");
		assertEquals(List.of("x"), result);
	}
	
	@Test
	void copyAndAddToNonEmptyList() {
		assertEquals(List.of("a", "b", "c"), SqlQuery.copyAndAdd(List.of("a", "b"), "c"));
	}
	
	@Test
	void copyAndAddAllWithEmptyCollection() {
		List<String> base = List.of("a");
		List<String> result = SqlQuery.copyAndAddAll(base, List.of());
		assertEquals(List.of("a"), result);
		assertNotSame(base, result);
	}
	
	@Test
	void copyAndAddAllWithMultipleElements() {
		assertEquals(List.of("a", "b", "c"), SqlQuery.copyAndAddAll(List.of("a"), List.of("b", "c")));
	}
	
	@Test
	void copyAndAddReturnsUnmodifiableList() {
		List<String> result = SqlQuery.copyAndAdd(List.of("a"), "b");
		assertThrows(UnsupportedOperationException.class, () -> result.add("y"));
	}
	
	@Test
	void copyAndAddDoesNotMutateInput() {
		List<String> base = new ArrayList<>(List.of("a"));
		SqlQuery.copyAndAdd(base, "b");
		assertEquals(List.of("a"), base);
	}
	
	@Test
	void copyAndAddAllReturnsUnmodifiableList() {
		List<String> result = SqlQuery.copyAndAddAll(List.of("a"), List.of("b"));
		assertThrows(UnsupportedOperationException.class, () -> result.add("y"));
	}
	
	@Test
	void copyAndAddAllDoesNotMutateInputs() {
		List<String> base = new ArrayList<>(List.of("a"));
		List<String> extra = new ArrayList<>(List.of("b", "c"));
		SqlQuery.copyAndAddAll(base, extra);
		assertEquals(List.of("a"), base);
		assertEquals(List.of("b", "c"), extra);
	}
	
	@Test
	void copyAndAddAllAcceptsSubtypeElements() {
		List<Number> base = List.of(1.5);
		List<Integer> integers = List.of(2, 3);
		List<Number> result = SqlQuery.copyAndAddAll(base, integers);
		assertEquals(List.of(1.5, 2, 3), result);
	}
}
