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

import net.luis.utils.io.database.SqlReferentialAction;
import net.luis.utils.io.database.SqlTestFixtures;
import net.luis.utils.io.database.table.SqlColumn;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAddForeignKeyOperation}.<br>
 *
 * @author Luis-St
 */
class SqlAddForeignKeyOperationTest {
	
	@Test
	void constructWithAllFields() {
		SqlTable<Object> table = SqlTable.create(Object.class, "child");
		SqlTable<Object> referenced = SqlTable.create(Object.class, "parent");
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		List<SqlColumn<?, ?>> referencedColumns = SqlTestFixtures.columns();
		SqlAddForeignKeyOperation operation = new SqlAddForeignKeyOperation(table, "fk_name", columns, referenced, referencedColumns, SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION);
		assertSame(table, operation.table());
		assertEquals("fk_name", operation.name());
		assertEquals(columns, operation.columns());
		assertSame(referenced, operation.referencedTable());
		assertEquals(referencedColumns, operation.referencedColumns());
		assertEquals(SqlReferentialAction.CASCADE, operation.onDelete());
		assertEquals(SqlReferentialAction.NO_ACTION, operation.onUpdate());
		assertInstanceOf(SqlMigrationOperation.class, operation);
	}
	
	@Test
	void constructWithEmptyColumnLists() {
		SqlAddForeignKeyOperation operation = assertDoesNotThrow(() -> new SqlAddForeignKeyOperation(SqlTable.create(Object.class, "child"), "fk_name", List.of(), SqlTable.create(Object.class, "parent"), List.of(), SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION));
		assertTrue(operation.columns().isEmpty());
		assertTrue(operation.referencedColumns().isEmpty());
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlAddForeignKeyOperation(null, "fk_name", SqlTestFixtures.columns(), SqlTable.create(Object.class, "parent"), SqlTestFixtures.columns(), SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new SqlAddForeignKeyOperation(SqlTable.create(Object.class, "child"), null, SqlTestFixtures.columns(), SqlTable.create(Object.class, "parent"), SqlTestFixtures.columns(), SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void constructWithNullColumns() {
		assertThrows(NullPointerException.class, () -> new SqlAddForeignKeyOperation(SqlTable.create(Object.class, "child"), "fk_name", null, SqlTable.create(Object.class, "parent"), SqlTestFixtures.columns(), SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void constructWithNullReferencedTable() {
		assertThrows(NullPointerException.class, () -> new SqlAddForeignKeyOperation(SqlTable.create(Object.class, "child"), "fk_name", SqlTestFixtures.columns(), null, SqlTestFixtures.columns(), SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void constructWithNullReferencedColumns() {
		assertThrows(NullPointerException.class, () -> new SqlAddForeignKeyOperation(SqlTable.create(Object.class, "child"), "fk_name", SqlTestFixtures.columns(), SqlTable.create(Object.class, "parent"), null, SqlReferentialAction.CASCADE, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void constructWithNullOnDelete() {
		assertThrows(NullPointerException.class, () -> new SqlAddForeignKeyOperation(SqlTable.create(Object.class, "child"), "fk_name", SqlTestFixtures.columns(), SqlTable.create(Object.class, "parent"), SqlTestFixtures.columns(), null, SqlReferentialAction.NO_ACTION));
	}
	
	@Test
	void constructWithNullOnUpdate() {
		assertThrows(NullPointerException.class, () -> new SqlAddForeignKeyOperation(SqlTable.create(Object.class, "child"), "fk_name", SqlTestFixtures.columns(), SqlTable.create(Object.class, "parent"), SqlTestFixtures.columns(), SqlReferentialAction.CASCADE, null));
	}
}
