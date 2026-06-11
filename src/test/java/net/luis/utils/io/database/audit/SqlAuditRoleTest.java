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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlAuditRole}.<br>
 *
 * @author Luis-St
 */
class SqlAuditRoleTest {
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlAuditRole.valueOf("UNKNOWN"));
	}
	
	@Test
	void valueOfWithNullThrows() {
		assertThrows(NullPointerException.class, () -> SqlAuditRole.valueOf(null));
	}
	
	@Test
	void valuesContainsAllRoles() {
		SqlAuditRole[] values = SqlAuditRole.values();
		assertEquals(5, values.length);
		assertEquals(SqlAuditRole.VERSION, values[0]);
		assertEquals(SqlAuditRole.CREATED_AT, values[1]);
		assertEquals(SqlAuditRole.CREATED_BY, values[2]);
		assertEquals(SqlAuditRole.UPDATED_AT, values[3]);
		assertEquals(SqlAuditRole.UPDATED_BY, values[4]);
	}
	
	@Test
	void valueOfReturnsMatchingConstant() {
		assertEquals(SqlAuditRole.VERSION, SqlAuditRole.valueOf("VERSION"));
		assertEquals(SqlAuditRole.CREATED_AT, SqlAuditRole.valueOf("CREATED_AT"));
		assertEquals(SqlAuditRole.CREATED_BY, SqlAuditRole.valueOf("CREATED_BY"));
		assertEquals(SqlAuditRole.UPDATED_AT, SqlAuditRole.valueOf("UPDATED_AT"));
		assertEquals(SqlAuditRole.UPDATED_BY, SqlAuditRole.valueOf("UPDATED_BY"));
	}
	
	@Test
	void nameAndOrdinalAreStable() {
		assertEquals(0, SqlAuditRole.VERSION.ordinal());
		assertEquals(4, SqlAuditRole.UPDATED_BY.ordinal());
		assertEquals("VERSION", SqlAuditRole.VERSION.name());
		assertEquals("UPDATED_BY", SqlAuditRole.UPDATED_BY.name());
	}
}
