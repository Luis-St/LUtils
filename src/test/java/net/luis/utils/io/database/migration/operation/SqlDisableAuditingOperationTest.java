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
import net.luis.utils.io.database.audit.SqlAuditConfig;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDisableAuditingOperation}.<br>
 *
 * @author Luis-St
 */
class SqlDisableAuditingOperationTest {
	
	@Test
	void constructWithTableAndConfig() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		SqlDisableAuditingOperation operation = new SqlDisableAuditingOperation(table, SqlAuditConfig.DEFAULT);
		assertSame(table, operation.table());
		assertSame(SqlAuditConfig.DEFAULT, operation.config());
		assertInstanceOf(SqlMigrationOperation.class, operation);
	}
	
	@Test
	void constructWithNullTable() {
		assertThrows(NullPointerException.class, () -> new SqlDisableAuditingOperation(null, SqlAuditConfig.DEFAULT));
	}
	
	@Test
	void constructWithNullConfig() {
		assertThrows(NullPointerException.class, () -> new SqlDisableAuditingOperation(SqlTestFixtures.sampleTable(), null));
	}
}
