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

package net.luis.utils.io.database.table;

import org.junit.jupiter.api.Test;

import java.util.List;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlUniqueConstraint}.<br>
 *
 * @author Luis-St
 */
class SqlUniqueConstraintTest {
	
	@Test
	void constructWithSingleColumn() {
		SqlUniqueConstraint<Object> constraint = new SqlUniqueConstraint<>(List.of(integerColumn()));
		assertEquals(1, constraint.columns().size());
	}
	
	@Test
	void constructWithNullColumns() {
		assertThrows(NullPointerException.class, () -> new SqlUniqueConstraint<>(null));
	}
	
	@Test
	void constructWithEmptyColumns() {
		assertThrows(IllegalArgumentException.class, () -> new SqlUniqueConstraint<>(List.of()));
	}
	
	@Test
	void constructWithMultipleColumns() {
		SqlColumn<Object, Integer> first = integerColumn();
		SqlColumn<Object, String> second = stringColumn();
		SqlUniqueConstraint<Object> constraint = new SqlUniqueConstraint<>(List.of(first, second));
		assertEquals(2, constraint.columns().size());
		assertSame(first, constraint.columns().get(0));
		assertSame(second, constraint.columns().get(1));
	}
	
	@Test
	void columnsAccessorReturnsColumns() {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlUniqueConstraint<Object> constraint = new SqlUniqueConstraint<>(List.of(column));
		assertSame(column, constraint.columns().get(0));
	}
	
	@Test
	void columnsAreUnmodifiable() {
		SqlUniqueConstraint<Object> constraint = new SqlUniqueConstraint<>(List.of(integerColumn()));
		assertThrows(UnsupportedOperationException.class, () -> constraint.columns().add(stringColumn()));
	}
	
	@Test
	void equalsAndHashCodeForEqualValues() {
		SqlColumn<Object, Integer> column = integerColumn();
		SqlUniqueConstraint<Object> first = new SqlUniqueConstraint<>(List.of(column));
		SqlUniqueConstraint<Object> second = new SqlUniqueConstraint<>(List.of(column));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new SqlUniqueConstraint<>(List.of(stringColumn())));
	}
}
