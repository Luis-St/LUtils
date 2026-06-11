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
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.table.SqlTable;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlColumnOptions}.<br>
 *
 * @author Luis-St
 */
class SqlColumnOptionsTest {
	
	@Test
	void constructWithAllFields() {
		SqlTable<?> table = SqlTestFixtures.sampleTable();
		SqlCondition check = SqlTestFixtures.alwaysCondition();
		SqlColumnOptions options = new SqlColumnOptions(true, true, true, Optional.of("d"), table, check);
		assertTrue(options.notNull());
		assertTrue(options.unique());
		assertTrue(options.autoIncrement());
		assertEquals(Optional.of("d"), options.defaultValue());
		assertSame(table, options.referencesTable());
		assertSame(check, options.check());
	}
	
	@Test
	void constructWithNullableFieldsNull() {
		SqlColumnOptions options = assertDoesNotThrow(() -> new SqlColumnOptions(false, false, false, Optional.empty(), null, null));
		assertNull(options.referencesTable());
		assertNull(options.check());
	}
	
	@Test
	void constructWithNullDefaultValue() {
		assertThrows(NullPointerException.class, () -> new SqlColumnOptions(false, false, false, null, null, null));
	}
	
	@Test
	void emptyConstantHasDefaults() {
		assertFalse(SqlColumnOptions.EMPTY.notNull());
		assertFalse(SqlColumnOptions.EMPTY.unique());
		assertFalse(SqlColumnOptions.EMPTY.autoIncrement());
		assertTrue(SqlColumnOptions.EMPTY.defaultValue().isEmpty());
		assertNull(SqlColumnOptions.EMPTY.referencesTable());
		assertNull(SqlColumnOptions.EMPTY.check());
	}
	
	@Test
	void flagsIndependentlySettable() {
		SqlColumnOptions options = new SqlColumnOptions(true, false, true, Optional.empty(), null, null);
		assertTrue(options.notNull());
		assertFalse(options.unique());
		assertTrue(options.autoIncrement());
	}
}
