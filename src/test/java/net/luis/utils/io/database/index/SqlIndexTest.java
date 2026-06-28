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

package net.luis.utils.io.database.index;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.table.SqlColumn;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlIndex}.<br>
 *
 * @author Luis-St
 */
class SqlIndexTest {
	
	@Test
	void constructCanonicalIndex() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		SqlIndex index = new SqlIndex("idx_users", columns, false, condition, SqlIndexMethod.BTREE);
		assertEquals("idx_users", index.name());
		assertEquals(columns, index.columns());
		assertFalse(index.unique());
		assertSame(condition, index.whereCondition());
		assertEquals(SqlIndexMethod.BTREE, index.method());
	}
	
	@Test
	void constructConvenienceIndexWithoutCondition() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		SqlIndex index = new SqlIndex("idx_users", columns, true, SqlIndexMethod.HASH);
		assertNull(index.whereCondition());
		assertEquals(columns, index.columns());
		assertTrue(index.unique());
		assertEquals(SqlIndexMethod.HASH, index.method());
	}
	
	@Test
	void constructWithNullName() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		assertThrows(NullPointerException.class, () -> new SqlIndex(null, columns, false, SqlIndexMethod.BTREE));
	}
	
	@Test
	void constructWithNullColumns() {
		assertThrows(NullPointerException.class, () -> new SqlIndex("idx", null, false, SqlIndexMethod.BTREE));
	}
	
	@Test
	void constructWithNullMethod() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		assertThrows(NullPointerException.class, () -> new SqlIndex("idx", columns, false, null));
	}
	
	@Test
	void constructWithBlankName() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		assertThrows(IllegalArgumentException.class, () -> new SqlIndex("   ", columns, false, SqlIndexMethod.BTREE));
	}
	
	@Test
	void constructWithEmptyName() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		assertThrows(IllegalArgumentException.class, () -> new SqlIndex("", columns, false, SqlIndexMethod.BTREE));
	}
	
	@Test
	void constructWithEmptyColumns() {
		assertThrows(IllegalArgumentException.class, () -> new SqlIndex("idx", List.of(), false, SqlIndexMethod.BTREE));
	}
	
	@Test
	void constructWithNullWhereConditionAllowed() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		SqlIndex index = assertDoesNotThrow(() -> new SqlIndex("idx", columns, false, null, SqlIndexMethod.BTREE));
		assertNull(index.whereCondition());
	}
	
	@Test
	void constructWithUniqueTrueAndFalse() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		SqlIndex unique = new SqlIndex("idx", columns, true, SqlIndexMethod.BTREE);
		SqlIndex nonUnique = new SqlIndex("idx", columns, false, SqlIndexMethod.BTREE);
		assertTrue(unique.unique());
		assertFalse(nonUnique.unique());
	}
	
	@Test
	void accessorsReturnSuppliedColumns() {
		List<SqlColumn<?, ?>> columns = List.of(SqlTestFixtures.integerColumn(), SqlTestFixtures.stringColumn());
		SqlIndex index = new SqlIndex("idx", columns, false, SqlIndexMethod.BTREE);
		assertEquals(columns, index.columns());
		assertEquals(2, index.columns().size());
	}
	
	@Test
	void constructWithEachIndexMethod() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		assertEquals(SqlIndexMethod.BTREE, new SqlIndex("idx", columns, false, SqlIndexMethod.BTREE).method());
		assertEquals(SqlIndexMethod.GIN, new SqlIndex("idx", columns, false, SqlIndexMethod.GIN).method());
		assertEquals(SqlIndexMethod.BITMAP, new SqlIndex("idx", columns, false, SqlIndexMethod.BITMAP).method());
	}
	
	@Test
	void equalsAndHashCodeForEqualIndexes() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		SqlIndex a = new SqlIndex("idx", columns, true, condition, SqlIndexMethod.BTREE);
		SqlIndex b = new SqlIndex("idx", columns, true, condition, SqlIndexMethod.BTREE);
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}
	
	@Test
	void notEqualWhenAnyComponentDiffers() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		SqlIndex base = new SqlIndex("idx", columns, true, condition, SqlIndexMethod.BTREE);
		assertNotEquals(base, new SqlIndex("other", columns, true, condition, SqlIndexMethod.BTREE));
		assertNotEquals(base, new SqlIndex("idx", columns, false, condition, SqlIndexMethod.BTREE));
		assertNotEquals(base, new SqlIndex("idx", columns, true, null, SqlIndexMethod.BTREE));
		assertNotEquals(base, new SqlIndex("idx", columns, true, condition, SqlIndexMethod.HASH));
		assertNotEquals(base, new SqlIndex("idx", List.of(SqlTestFixtures.stringColumn()), true, condition, SqlIndexMethod.BTREE));
	}
	
	@Test
	void toStringContainsComponents() {
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		SqlIndex index = new SqlIndex("idx_users", columns, false, SqlIndexMethod.BTREE);
		String string = index.toString();
		assertTrue(string.contains("idx_users"));
		assertTrue(string.contains("BTREE"));
	}
}
