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

package net.luis.utils.io.database.migration;

import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlSchemaColumnInfo}.<br>
 *
 * @author Luis-St
 */
class SqlSchemaColumnInfoTest {
	
	@Test
	void constructWithAllFields() {
		SqlSchemaColumnInfo info = new SqlSchemaColumnInfo("users", "id", Types.INTEGER, null, false, true, true, false, 0);
		assertEquals("users", info.tableName());
		assertEquals("id", info.columnName());
		assertEquals(Types.INTEGER, info.jdbcType());
		assertNull(info.parameter());
		assertFalse(info.nullable());
		assertTrue(info.autoIncrement());
		assertTrue(info.primaryKey());
		assertFalse(info.unique());
		assertEquals(0, info.ordinalPosition());
	}
	
	@Test
	void constructWithParameter() {
		SqlParameter parameter = SqlParameter.length(255);
		SqlSchemaColumnInfo info = new SqlSchemaColumnInfo("users", "name", Types.VARCHAR, parameter, true, false, false, false, 1);
		assertSame(parameter, info.parameter());
	}
	
	@Test
	void constructWithNullTableName() {
		assertThrows(NullPointerException.class, () -> new SqlSchemaColumnInfo(null, "id", Types.INTEGER, null, false, false, false, false, 0));
	}
	
	@Test
	void constructWithNullColumnName() {
		assertThrows(NullPointerException.class, () -> new SqlSchemaColumnInfo("users", null, Types.INTEGER, null, false, false, false, false, 0));
	}
	
	@Test
	void booleanFlagsIndependentlySettable() {
		SqlSchemaColumnInfo info = new SqlSchemaColumnInfo("users", "id", Types.INTEGER, null, true, false, false, true, 0);
		assertTrue(info.nullable());
		assertFalse(info.autoIncrement());
		assertFalse(info.primaryKey());
		assertTrue(info.unique());
	}
}
