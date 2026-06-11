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
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAlterColumnOperation}.<br>
 *
 * @author Luis-St
 */
class SqlAlterColumnOperationTest {
	
	@Test
	void constructWithColumnAndAlterations() {
		SqlColumn<?, ?> column = SqlTestFixtures.integerColumn();
		List<SqlColumnAlteration> alterations = List.of(new SqlSetNullableAlteration(true), new SqlDropDefaultAlteration());
		SqlAlterColumnOperation operation = new SqlAlterColumnOperation(column, alterations);
		assertSame(column, operation.column());
		assertEquals(alterations, operation.alterations());
		assertInstanceOf(SqlMigrationOperation.class, operation);
	}
	
	@Test
	void constructWithEmptyAlterations() {
		SqlAlterColumnOperation operation = assertDoesNotThrow(() -> new SqlAlterColumnOperation(SqlTestFixtures.integerColumn(), List.of()));
		assertTrue(operation.alterations().isEmpty());
	}
	
	@Test
	void constructWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlAlterColumnOperation(null, List.of()));
	}
	
	@Test
	void constructWithNullAlterations() {
		assertThrows(NullPointerException.class, () -> new SqlAlterColumnOperation(SqlTestFixtures.integerColumn(), null));
	}
}
