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
 * Test class for {@link SqlAddUniqueConstraintOperation}.<br>
 *
 * @author Luis-St
 */
class SqlAddUniqueConstraintOperationTest {
	
	@Test
	void constructWithTableNameAndColumns() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		List<SqlColumn<?, ?>> columns = SqlTestFixtures.columns();
		SqlAddUniqueConstraintOperation operation = new SqlAddUniqueConstraintOperation(table, "uq_name", columns);
		assertSame(table, operation.table());
		assertEquals("uq_name", operation.name());
		assertEquals(columns, operation.columns());
		assertInstanceOf(SqlMigrationOperation.class, operation);
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlAddUniqueConstraintOperation(null, "uq_name", SqlTestFixtures.columns()));
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new SqlAddUniqueConstraintOperation(SqlTestFixtures.sampleTable(), null, SqlTestFixtures.columns()));
	}
	
	@Test
	void constructWithNullColumns() {
		assertThrows(NullPointerException.class, () -> new SqlAddUniqueConstraintOperation(SqlTestFixtures.sampleTable(), "uq_name", null));
	}
	
	@Test
	void constructWithEmptyColumns() {
		assertThrows(IllegalArgumentException.class, () -> new SqlAddUniqueConstraintOperation(SqlTestFixtures.sampleTable(), "uq_name", List.of()));
	}
}
