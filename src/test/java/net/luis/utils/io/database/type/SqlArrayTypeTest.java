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

package net.luis.utils.io.database.type;

import org.junit.jupiter.api.Test;

import javax.sql.rowset.CachedRowSet;
import java.sql.PreparedStatement;
import java.sql.Types;

import static net.luis.utils.io.database.SqlTestFixtures.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlArrayType}.<br>
 *
 * @author Luis-St
 */
class SqlArrayTypeTest {
	
	@Test
	void constructWithElementType() {
		SqlArrayType<Integer> type = new SqlArrayType<>(SqlTypes.INTEGER);
		assertEquals(SqlTypes.INTEGER, type.elementType());
		assertEquals(Types.ARRAY, type.jdbcType());
	}
	
	@Test
	void constructWithNullElementType() {
		assertThrows(NullPointerException.class, () -> new SqlArrayType<>(null));
	}
	
	@Test
	void constructWithNestedArrayType() {
		assertThrows(IllegalArgumentException.class, () -> new SqlArrayType<>(SqlTypes.INTEGER.array()));
	}
	
	@Test
	void getWithNullAccess() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.ARRAY, null);
		assertThrows(IllegalCallerException.class, () -> type.get(null, rowSet, 1));
	}
	
	@Test
	void getWithNullResultSet() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		assertThrows(NullPointerException.class, () -> type.get(SqlTypeInternalAccess.INSTANCE, null, 1));
	}
	
	@Test
	void getWithColumnIndexBelowOne() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.ARRAY, null);
		assertThrows(IllegalArgumentException.class, () -> type.get(SqlTypeInternalAccess.INSTANCE, rowSet, 0));
	}
	
	@Test
	void setWithNullAccess() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		assertThrows(IllegalCallerException.class, () -> type.set(null, DIALECT, new FakePreparedStatement(), 1, new Integer[] { 1 }));
	}
	
	@Test
	void setWithNullDialect() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		assertThrows(NullPointerException.class, () -> type.set(SqlTypeInternalAccess.INSTANCE, null, new FakePreparedStatement(), 1, new Integer[] { 1 }));
	}
	
	@Test
	void setWithNullPreparedStatement() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		assertThrows(NullPointerException.class, () -> type.set(SqlTypeInternalAccess.INSTANCE, DIALECT, null, 1, new Integer[] { 1 }));
	}
	
	@Test
	void setWithColumnIndexBelowOne() {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		PreparedStatement statement = new FakePreparedStatement();
		assertThrows(IllegalArgumentException.class, () -> type.set(SqlTypeInternalAccess.INSTANCE, DIALECT, statement, 0, new Integer[] { 1 }));
	}
	
	@Test
	void getReturnsNullForNullArrayColumn() throws Exception {
		SqlArrayType<Integer> type = SqlTypes.INTEGER.array();
		CachedRowSet rowSet = SqlRowSets.singleColumn(Types.ARRAY, null);
		assertNull(type.get(SqlTypeInternalAccess.INSTANCE, rowSet, 1));
	}
	
	@Test
	void jdbcTypeIsArray() {
		assertEquals(Types.ARRAY, SqlTypes.INTEGER.array().jdbcType());
	}
	
	@Test
	void javaTypeIsElementArrayClass() {
		assertEquals(Integer[].class, SqlTypes.INTEGER.array().javaType());
	}
	
	@Test
	void equalsSameElementType() {
		assertEquals(SqlTypes.INTEGER.array(), SqlTypes.INTEGER.array());
	}
	
	@Test
	void equalsDifferentElementType() {
		assertNotEquals(SqlTypes.INTEGER.array(), STRING_TYPE.array());
	}
	
	@Test
	void equalsWithNull() {
		assertNotEquals(null, SqlTypes.INTEGER.array());
	}
	
	@Test
	void equalsWithDifferentType() {
		assertNotEquals("string", SqlTypes.INTEGER.array());
	}
	
	@Test
	void elementTypeReturnsConfiguredValue() {
		assertEquals(SqlTypes.INTEGER, SqlTypes.INTEGER.array().elementType());
	}
	
	@Test
	void hashCodeConsistentForEqualTypes() {
		assertEquals(SqlTypes.INTEGER.array().hashCode(), SqlTypes.INTEGER.array().hashCode());
	}
	
	@Test
	void toStringContainsElementType() {
		assertTrue(SqlTypes.INTEGER.array().toString().contains("elementType="));
	}
	
	@Test
	void arrayOfParameterizedElementType() {
		SqlArrayType<String> type = STRING_TYPE.array();
		assertEquals(STRING_TYPE, type.elementType());
		assertEquals(String[].class, type.javaType());
	}
}
