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
import net.luis.utils.io.database.migration.operation.SqlColumnDefinition;
import net.luis.utils.io.database.migration.operation.SqlColumnOptions;
import net.luis.utils.io.database.table.SqlColumn;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationTableBuilder}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationTableBuilderTest {
	
	@Test
	void constructEmpty() {
		SqlMigrationTableBuilder builder = new SqlMigrationTableBuilder();
		assertTrue(builder.getColumns().isEmpty());
		assertTrue(builder.getPrimaryKeyColumns().isEmpty());
	}
	
	@Test
	void columnWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationTableBuilder().column(null, SqlTestFixtures.INTEGER_TYPE));
	}
	
	@Test
	void columnWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationTableBuilder().column(SqlTestFixtures.integerColumn(), null));
	}
	
	@Test
	void columnWithOptionsNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationTableBuilder().column(null, SqlTestFixtures.INTEGER_TYPE, builder -> {}));
	}
	
	@Test
	void columnWithOptionsNullType() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationTableBuilder().column(SqlTestFixtures.integerColumn(), null, builder -> {}));
	}
	
	@Test
	void columnWithNullOptionsConsumer() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationTableBuilder().column(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE, null));
	}
	
	@Test
	void primaryKeyWithNullArray() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationTableBuilder().primaryKey((SqlColumn<?, ?>[]) null));
	}
	
	@Test
	void primaryKeyWithEmptyArray() {
		assertThrows(IllegalArgumentException.class, () -> new SqlMigrationTableBuilder().primaryKey());
	}
	
	@Test
	void addColumnWithDefaultOptions() {
		SqlColumn<?, ?> column = SqlTestFixtures.integerColumn();
		List<SqlColumnDefinition> columns = new SqlMigrationTableBuilder().column(column, SqlTestFixtures.INTEGER_TYPE).getColumns();
		assertEquals(1, columns.size());
		assertSame(column, columns.getFirst().column());
		assertSame(SqlTestFixtures.INTEGER_TYPE, columns.getFirst().type());
		assertSame(SqlColumnOptions.EMPTY, columns.getFirst().options());
	}
	
	@Test
	void addColumnWithOptionsConsumer() {
		List<SqlColumnDefinition> columns = new SqlMigrationTableBuilder().column(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE, builder -> builder.notNull().unique()).getColumns();
		assertEquals(1, columns.size());
		assertTrue(columns.getFirst().options().notNull());
		assertTrue(columns.getFirst().options().unique());
	}
	
	@Test
	void primaryKeySetsColumns() {
		SqlColumn<?, ?> column = SqlTestFixtures.integerColumn();
		List<SqlColumn<?, ?>> primaryKeys = new SqlMigrationTableBuilder().primaryKey(column).getPrimaryKeyColumns();
		assertEquals(1, primaryKeys.size());
		assertSame(column, primaryKeys.getFirst());
	}
	
	@Test
	void primaryKeyClearsPreviousColumns() {
		SqlColumn<?, ?> first = SqlTestFixtures.integerColumn();
		SqlColumn<?, ?> second = SqlTestFixtures.stringColumn();
		List<SqlColumn<?, ?>> primaryKeys = new SqlMigrationTableBuilder().primaryKey(first).primaryKey(second).getPrimaryKeyColumns();
		assertEquals(1, primaryKeys.size());
		assertSame(second, primaryKeys.getFirst());
	}
	
	@Test
	void buildersReturnSameInstance() {
		SqlMigrationTableBuilder builder = new SqlMigrationTableBuilder();
		assertSame(builder, builder.column(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE));
		assertSame(builder, builder.column(SqlTestFixtures.stringColumn(), SqlTestFixtures.STRING_TYPE, options -> {}));
		assertSame(builder, builder.primaryKey(SqlTestFixtures.integerColumn()));
	}
	
	@Test
	void multipleColumnsPreserveInsertionOrder() {
		SqlColumn<?, ?> first = SqlTestFixtures.integerColumn();
		SqlColumn<?, ?> second = SqlTestFixtures.stringColumn();
		SqlColumn<?, ?> third = SqlTestFixtures.integerColumn();
		List<SqlColumnDefinition> columns = new SqlMigrationTableBuilder()
			.column(first, SqlTestFixtures.INTEGER_TYPE)
			.column(second, SqlTestFixtures.STRING_TYPE)
			.column(third, SqlTestFixtures.INTEGER_TYPE)
			.getColumns();
		assertEquals(3, columns.size());
		assertSame(first, columns.get(0).column());
		assertSame(second, columns.get(1).column());
		assertSame(third, columns.get(2).column());
	}
	
	@Test
	void getColumnsReturnsImmutableCopy() {
		SqlMigrationTableBuilder builder = new SqlMigrationTableBuilder();
		builder.column(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE);
		List<SqlColumnDefinition> first = builder.getColumns();
		assertThrows(UnsupportedOperationException.class, () -> first.add(first.getFirst()));
		builder.column(SqlTestFixtures.stringColumn(), SqlTestFixtures.STRING_TYPE);
		assertEquals(1, first.size());
		assertEquals(2, builder.getColumns().size());
	}
}
