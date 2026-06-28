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
 * Test class for {@link SqlDropIndexOperation}.<br>
 *
 * @author Luis-St
 */
class SqlDropIndexOperationTest {
	
	@Test
	void constructWithTableAndIndex() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		SqlDropIndexOperation operation = new SqlDropIndexOperation(table, "idx_name");
		assertSame(table, operation.table());
		assertEquals("idx_name", operation.index());
		assertInstanceOf(SqlMigrationOperation.class, operation);
	}
	
	@Test
	void constructWithNullTable() {
		SqlDropIndexOperation operation = assertDoesNotThrow(() -> new SqlDropIndexOperation(null, "idx_name"));
		assertNull(operation.table());
		assertEquals("idx_name", operation.index());
	}
	
	@Test
	void constructWithNullIndex() {
		assertThrows(NullPointerException.class, () -> new SqlDropIndexOperation(SqlTestFixtures.sampleTable(), null));
	}
}
