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
import net.luis.utils.io.database.index.SqlIndex;
import net.luis.utils.io.database.index.SqlIndexMethod;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlCreateIndexOperation}.<br>
 *
 * @author Luis-St
 */
class SqlCreateIndexOperationTest {
	
	@Test
	void constructWithIndexAndTable() {
		SqlIndex index = new SqlIndex("idx", SqlTestFixtures.columns(), false, SqlIndexMethod.BTREE);
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		SqlCreateIndexOperation operation = new SqlCreateIndexOperation(index, table);
		assertSame(index, operation.index());
		assertSame(table, operation.table());
		assertInstanceOf(SqlMigrationOperation.class, operation);
	}
	
	@Test
	void constructWithNullIndex() {
		assertThrows(NullPointerException.class, () -> new SqlCreateIndexOperation(null, SqlTestFixtures.sampleTable()));
	}
	
	@Test
	void constructWithNullTable() {
		SqlIndex index = new SqlIndex("idx", SqlTestFixtures.columns(), false, SqlIndexMethod.BTREE);
		assertThrows(NullPointerException.class, () -> new SqlCreateIndexOperation(index, null));
	}
}
