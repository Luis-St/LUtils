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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlColumnDefinition}.<br>
 *
 * @author Luis-St
 */
class SqlColumnDefinitionTest {
	
	@Test
	void constructWithColumnTypeAndOptions() {
		SqlColumn<?, ?> column = SqlTestFixtures.integerColumn();
		SqlColumnDefinition definition = new SqlColumnDefinition(column, SqlTestFixtures.INTEGER_TYPE, SqlColumnOptions.EMPTY);
		assertSame(column, definition.column());
		assertSame(SqlTestFixtures.INTEGER_TYPE, definition.type());
		assertSame(SqlColumnOptions.EMPTY, definition.options());
	}
	
	@Test
	void constructWithNullColumn() {
		assertThrows(NullPointerException.class, () -> new SqlColumnDefinition(null, SqlTestFixtures.INTEGER_TYPE, SqlColumnOptions.EMPTY));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlColumnDefinition(SqlTestFixtures.integerColumn(), null, SqlColumnOptions.EMPTY));
	}
	
	@Test
	void constructWithNullOptions() {
		assertThrows(NullPointerException.class, () -> new SqlColumnDefinition(SqlTestFixtures.integerColumn(), SqlTestFixtures.INTEGER_TYPE, null));
	}
}
