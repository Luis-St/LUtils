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

package net.luis.utils.io.database.audit;

import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import net.luis.utils.io.database.type.parameter.SqlParameter;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAuditColumn}.<br>
 *
 * @author Luis-St
 */
class SqlAuditColumnTest {
	
	private static final SqlType<String> USER_TYPE = SqlTypes.STRING.configure(SqlParameter.length(255));
	private static final SqlType<LocalDateTime> TIMESTAMP_TYPE = SqlTypes.LOCAL_DATE_TIME.configure(SqlParameter.fractional(6));
	
	@Test
	void constructValidColumn() {
		SqlAuditColumn column = new SqlAuditColumn("version", SqlTypes.LONG, SqlAuditRole.VERSION, false);
		assertEquals("version", column.name());
		assertEquals(SqlTypes.LONG, column.type());
		assertEquals(SqlAuditRole.VERSION, column.role());
		assertFalse(column.nullable());
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new SqlAuditColumn(null, SqlTypes.LONG, SqlAuditRole.VERSION, false));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new SqlAuditColumn("v", null, SqlAuditRole.VERSION, false));
	}
	
	@Test
	void constructWithNullRole() {
		assertThrows(NullPointerException.class, () -> new SqlAuditColumn("v", SqlTypes.LONG, null, false));
	}
	
	@Test
	void constructWithBlankName() {
		assertThrows(IllegalArgumentException.class, () -> new SqlAuditColumn("   ", SqlTypes.LONG, SqlAuditRole.VERSION, false));
	}
	
	@Test
	void constructWithEmptyName() {
		assertThrows(IllegalArgumentException.class, () -> new SqlAuditColumn("", SqlTypes.LONG, SqlAuditRole.VERSION, false));
	}
	
	@Test
	void constructNonBlankNamePasses() {
		assertDoesNotThrow(() -> new SqlAuditColumn("created_by", USER_TYPE, SqlAuditRole.CREATED_BY, true));
	}
	
	@Test
	void constructNullableColumn() {
		SqlAuditColumn column = new SqlAuditColumn("created_by", USER_TYPE, SqlAuditRole.CREATED_BY, true);
		assertTrue(column.nullable());
	}
	
	@Test
	void constructNonNullableColumn() {
		SqlAuditColumn column = new SqlAuditColumn("version", SqlTypes.LONG, SqlAuditRole.VERSION, false);
		assertFalse(column.nullable());
	}
	
	@Test
	void accessorsReturnConstructorValues() {
		SqlAuditColumn column = new SqlAuditColumn("created_at", TIMESTAMP_TYPE, SqlAuditRole.CREATED_AT, false);
		assertEquals("created_at", column.name());
		assertEquals(TIMESTAMP_TYPE, column.type());
		assertEquals(SqlAuditRole.CREATED_AT, column.role());
		assertFalse(column.nullable());
	}
	
	@Test
	void equalColumnsAreEqual() {
		SqlAuditColumn first = new SqlAuditColumn("version", SqlTypes.LONG, SqlAuditRole.VERSION, false);
		SqlAuditColumn second = new SqlAuditColumn("version", SqlTypes.LONG, SqlAuditRole.VERSION, false);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void columnsWithDifferentRoleAreNotEqual() {
		SqlAuditColumn first = new SqlAuditColumn("version", SqlTypes.LONG, SqlAuditRole.VERSION, false);
		SqlAuditColumn second = new SqlAuditColumn("version", SqlTypes.LONG, SqlAuditRole.CREATED_AT, false);
		assertNotEquals(first, second);
	}
}
