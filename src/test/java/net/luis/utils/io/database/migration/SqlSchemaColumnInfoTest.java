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
	
	@Test
	void constructWithTypeIdentifier() {
		SqlSchemaColumnInfo info = new SqlSchemaColumnInfo("table", "column", Types.CHAR, SqlParameter.length(36), false, false, true, false, 0, "uuid");
		assertEquals("uuid", info.typeIdentifier());
		assertEquals("table", info.tableName());
		assertEquals("column", info.columnName());
		assertEquals(Types.CHAR, info.jdbcType());
		assertEquals(0, info.ordinalPosition());
	}
	
	@Test
	void constructWithNullTypeIdentifier() {
		SqlSchemaColumnInfo info = assertDoesNotThrow(() -> new SqlSchemaColumnInfo("table", "column", Types.INTEGER, null, true, false, false, false, 1, null));
		assertNull(info.typeIdentifier());
	}
	
	@Test
	void constructWithoutTypeIdentifier() {
		SqlSchemaColumnInfo info = new SqlSchemaColumnInfo("table", "column", Types.INTEGER, null, true, false, false, false, 2);
		assertNull(info.typeIdentifier());
		assertEquals("table", info.tableName());
		assertEquals(2, info.ordinalPosition());
	}
	
	@Test
	void constructWithNullTableNameAndTypeIdentifier() {
		assertThrows(NullPointerException.class, () -> new SqlSchemaColumnInfo(null, "column", Types.INTEGER, null, true, false, false, false, 0, "uuid"));
	}
	
	@Test
	void constructWithNullColumnNameAndTypeIdentifier() {
		assertThrows(NullPointerException.class, () -> new SqlSchemaColumnInfo("table", null, Types.INTEGER, null, true, false, false, false, 0, "uuid"));
	}
	
	@Test
	void constructWithNullTableNameInShortConstructor() {
		assertThrows(NullPointerException.class, () -> new SqlSchemaColumnInfo(null, "column", Types.INTEGER, null, true, false, false, false, 0));
	}
	
	@Test
	void constructWithNullColumnNameInShortConstructor() {
		assertThrows(NullPointerException.class, () -> new SqlSchemaColumnInfo("table", null, Types.INTEGER, null, true, false, false, false, 0));
	}
	
	@Test
	void typeIdentifierIndependentOfParameter() {
		SqlSchemaColumnInfo identified = new SqlSchemaColumnInfo("table", "column", Types.CHAR, SqlParameter.length(36), true, false, false, false, 0, "uuid");
		SqlSchemaColumnInfo plain = new SqlSchemaColumnInfo("table", "column", Types.CHAR, SqlParameter.length(36), true, false, false, false, 0, null);
		assertEquals(SqlParameter.length(36), identified.parameter());
		assertEquals(identified.parameter(), plain.parameter());
		assertEquals("uuid", identified.typeIdentifier());
		assertNull(plain.typeIdentifier());
	}
	
	@Test
	void constructWithBlankTypeIdentifier() {
		SqlSchemaColumnInfo info = assertDoesNotThrow(() -> new SqlSchemaColumnInfo("table", "column", Types.CHAR, null, true, false, false, false, 0, ""));
		assertEquals("", info.typeIdentifier());
	}
	
	@Test
	void equalsDistinguishesTypeIdentifier() {
		SqlSchemaColumnInfo identified = new SqlSchemaColumnInfo("table", "column", Types.CHAR, null, true, false, false, false, 0, "uuid");
		SqlSchemaColumnInfo plain = new SqlSchemaColumnInfo("table", "column", Types.CHAR, null, true, false, false, false, 0, null);
		SqlSchemaColumnInfo other = new SqlSchemaColumnInfo("table", "column", Types.CHAR, null, true, false, false, false, 0, "uuid");
		assertNotEquals(identified, plain);
		assertEquals(identified, other);
		assertEquals(identified.hashCode(), other.hashCode());
	}
	
	@Test
	void equalsBetweenShortAndCanonicalConstructor() {
		SqlSchemaColumnInfo shortForm = new SqlSchemaColumnInfo("table", "column", Types.INTEGER, null, true, false, false, false, 0);
		SqlSchemaColumnInfo canonical = new SqlSchemaColumnInfo("table", "column", Types.INTEGER, null, true, false, false, false, 0, null);
		assertEquals(shortForm, canonical);
		assertEquals(shortForm.hashCode(), canonical.hashCode());
	}
	
	@Test
	void toStringContainsTypeIdentifier() {
		String string = new SqlSchemaColumnInfo("table", "column", Types.CHAR, null, true, false, false, false, 0, "uuid").toString();
		assertTrue(string.contains("typeIdentifier=uuid"));
	}
}
