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
 * Test class for {@link SqlDropConstraintOperation}.<br>
 *
 * @author Luis-St
 */
class SqlDropConstraintOperationTest {
	
	@Test
	void constructWithTableAndName() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		SqlDropConstraintOperation operation = new SqlDropConstraintOperation(table, "constraint_name");
		assertSame(table, operation.table());
		assertEquals("constraint_name", operation.name());
		assertInstanceOf(SqlMigrationOperation.class, operation);
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlDropConstraintOperation(null, "constraint_name"));
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new SqlDropConstraintOperation(SqlTestFixtures.sampleTable(), null));
	}
}
