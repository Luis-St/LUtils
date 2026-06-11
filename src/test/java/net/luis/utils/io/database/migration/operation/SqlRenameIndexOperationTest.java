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
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlRenameIndexOperation}.<br>
 *
 * @author Luis-St
 */
class SqlRenameIndexOperationTest {
	
	@Test
	void constructWithTableFromAndTo() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		SqlRenameIndexOperation operation = new SqlRenameIndexOperation(table, "old_idx", "new_idx");
		assertSame(table, operation.table());
		assertEquals("old_idx", operation.from());
		assertEquals("new_idx", operation.to());
		assertInstanceOf(SqlMigrationOperation.class, operation);
	}
	
	@Test
	void constructWithNullTable() {
		SqlRenameIndexOperation operation = assertDoesNotThrow(() -> new SqlRenameIndexOperation(null, "old_idx", "new_idx"));
		assertNull(operation.table());
		assertEquals("old_idx", operation.from());
		assertEquals("new_idx", operation.to());
	}
	
	@Test
	void constructWithNullFrom() {
		assertThrows(NullPointerException.class, () -> new SqlRenameIndexOperation(SqlTestFixtures.sampleTable(), null, "new_idx"));
	}
	
	@Test
	void constructWithNullTo() {
		assertThrows(NullPointerException.class, () -> new SqlRenameIndexOperation(SqlTestFixtures.sampleTable(), "old_idx", null));
	}
}
