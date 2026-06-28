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

package net.luis.utils.io.database.migration.operation;

import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlCreateTableOperation}.<br>
 *
 * @author Luis-St
 */
class SqlCreateTableOperationTest {
	
	@Test
	void constructWithTableColumnsAndPrimaryKeys() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		List<SqlColumnDefinition> columns = List.of(new SqlColumnDefinition(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE, SqlColumnOptions.EMPTY));
		List<SqlColumn<?, ?>> primaryKeys = List.of(SqlTestFixtures.integerColumn());
		SqlCreateTableOperation operation = new SqlCreateTableOperation(table, columns, primaryKeys);
		assertSame(table, operation.table());
		assertEquals(columns, operation.columns());
		assertEquals(primaryKeys, operation.primaryKeyColumns());
		assertInstanceOf(SqlMigrationOperation.class, operation);
	}
	
	@Test
	void constructWithEmptyColumnLists() {
		SqlCreateTableOperation operation = assertDoesNotThrow(() -> new SqlCreateTableOperation(SqlTestFixtures.sampleTable(), List.of(), List.of()));
		assertTrue(operation.columns().isEmpty());
		assertTrue(operation.primaryKeyColumns().isEmpty());
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlCreateTableOperation(null, List.of(), List.of()));
	}
	
	@Test
	void constructWithNullColumns() {
		assertThrows(NullPointerException.class, () -> new SqlCreateTableOperation(SqlTestFixtures.sampleTable(), null, List.of()));
	}
	
	@Test
	void constructWithNullPrimaryKeyColumns() {
		assertThrows(NullPointerException.class, () -> new SqlCreateTableOperation(SqlTestFixtures.sampleTable(), List.of(), null));
	}
}
