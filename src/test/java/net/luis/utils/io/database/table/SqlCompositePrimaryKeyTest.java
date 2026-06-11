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
 * Test class for {@link SqlCompositePrimaryKey}.<br>
 *
 * @author Luis-St
 */
class SqlCompositePrimaryKeyTest {
	
	@Test
	void constructWithTwoColumns() {
		SqlColumn<Object, Integer> first = integerColumn();
		SqlColumn<Object, String> second = stringColumn();
		SqlCompositePrimaryKey<Object> key = new SqlCompositePrimaryKey<>(List.of(first, second));
		assertEquals(2, key.columns().size());
		assertSame(first, key.columns().get(0));
		assertSame(second, key.columns().get(1));
	}
	
	@Test
	void constructWithNullColumns() {
		assertThrows(NullPointerException.class, () -> new SqlCompositePrimaryKey<>(null));
	}
	
	@Test
	void constructWithEmptyColumns() {
		assertThrows(IllegalArgumentException.class, () -> new SqlCompositePrimaryKey<>(List.of()));
	}
	
	@Test
	void constructWithSingleColumn() {
		assertThrows(IllegalArgumentException.class, () -> new SqlCompositePrimaryKey<>(List.of(integerColumn())));
	}
	
	@Test
	void constructWithThreeColumns() {
		SqlColumn<Object, Integer> first = integerColumn();
		SqlColumn<Object, String> second = stringColumn();
		SqlColumn<Object, Integer> third = integerColumn();
		SqlCompositePrimaryKey<Object> key = new SqlCompositePrimaryKey<>(List.of(first, second, third));
		assertEquals(3, key.columns().size());
		assertSame(first, key.columns().get(0));
		assertSame(second, key.columns().get(1));
		assertSame(third, key.columns().get(2));
	}
	
	@Test
	void referenceTargetReturnsColumns() {
		SqlCompositePrimaryKey<Object> key = new SqlCompositePrimaryKey<>(List.of(integerColumn(), stringColumn()));
		assertEquals(key.columns(), key.referenceTarget());
	}
	
	@Test
	void columnsAreUnmodifiable() {
		SqlCompositePrimaryKey<Object> key = new SqlCompositePrimaryKey<>(List.of(integerColumn(), stringColumn()));
		assertThrows(UnsupportedOperationException.class, () -> key.columns().add(integerColumn()));
	}
	
	@Test
	void equalsAndHashCodeForEqualKeys() {
		SqlColumn<Object, Integer> first = integerColumn();
		SqlColumn<Object, String> second = stringColumn();
		SqlCompositePrimaryKey<Object> keyA = new SqlCompositePrimaryKey<>(List.of(first, second));
		SqlCompositePrimaryKey<Object> keyB = new SqlCompositePrimaryKey<>(List.of(first, second));
		assertEquals(keyA, keyB);
		assertEquals(keyA.hashCode(), keyB.hashCode());
		assertNotEquals(keyA, new SqlCompositePrimaryKey<>(List.of(second, first)));
	}
}
