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

package net.luis.utils.io.database.migration;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.table.SqlColumn;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationIndexBuilder}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationIndexBuilderTest {
	
	@Test
	void constructWithName() {
		SqlIndex index = new SqlMigrationIndexBuilder("idx").columns(SqlTestFixtures.integerColumn()).build();
		assertEquals("idx", index.name());
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationIndexBuilder(null));
	}
	
	@Test
	void columnsWithNullArray() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationIndexBuilder("idx").columns((SqlColumn<?, ?>[]) null));
	}
	
	@Test
	void columnsWithEmptyArray() {
		assertThrows(IllegalArgumentException.class, () -> new SqlMigrationIndexBuilder("idx").columns());
	}
	
	@Test
	void methodWithNull() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationIndexBuilder("idx").method(null));
	}
	
	@Test
	void whereWithNullCondition() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationIndexBuilder("idx").where(null));
	}
	
	@Test
	void buildWithoutColumns() {
		SqlMigrationIndexBuilder builder = new SqlMigrationIndexBuilder("idx");
		assertThrows(IllegalStateException.class, builder::build);
	}
	
	@Test
	void buildWithSingleColumn() {
		SqlIndex index = new SqlMigrationIndexBuilder("idx").columns(SqlTestFixtures.integerColumn()).build();
		assertEquals(1, index.columns().size());
		assertFalse(index.unique());
		assertEquals(SqlIndexMethod.BTREE, index.method());
		assertNull(index.whereCondition());
	}
	
	@Test
	void buildUniqueIndex() {
		SqlIndex index = new SqlMigrationIndexBuilder("idx").columns(SqlTestFixtures.integerColumn()).unique().build();
		assertTrue(index.unique());
	}
	
	@Test
	void buildWithCustomMethod() {
		SqlIndex index = new SqlMigrationIndexBuilder("idx").columns(SqlTestFixtures.integerColumn()).method(SqlIndexMethod.HASH).build();
		assertEquals(SqlIndexMethod.HASH, index.method());
	}
	
	@Test
	void buildWithWhereCondition() {
		SqlCondition condition = SqlTestFixtures.alwaysCondition();
		SqlIndex index = new SqlMigrationIndexBuilder("idx").columns(SqlTestFixtures.integerColumn()).where(condition).build();
		assertSame(condition, index.whereCondition());
	}
	
	@Test
	void columnsWithMultipleColumns() {
		SqlColumn<?, ?> first = SqlTestFixtures.integerColumn();
		SqlColumn<?, ?> second = SqlTestFixtures.stringColumn();
		SqlIndex index = new SqlMigrationIndexBuilder("idx").columns(first, second).build();
		assertEquals(2, index.columns().size());
		assertSame(first, index.columns().get(0));
		assertSame(second, index.columns().get(1));
	}
	
	@Test
	void settersReturnSameInstance() {
		SqlMigrationIndexBuilder builder = new SqlMigrationIndexBuilder("idx");
		assertSame(builder, builder.columns(SqlTestFixtures.integerColumn()));
		assertSame(builder, builder.unique());
		assertSame(builder, builder.method(SqlIndexMethod.HASH));
		assertSame(builder, builder.where(SqlTestFixtures.alwaysCondition()));
	}
}
