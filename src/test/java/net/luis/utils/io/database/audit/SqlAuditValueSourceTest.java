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
 * Test class for {@link SqlAuditValueSource}.<br>
 *
 * @author Luis-St
 */
class SqlAuditValueSourceTest {
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlAuditValueSource.valueOf("UNKNOWN"));
	}
	
	@Test
	void valueOfWithNullThrows() {
		assertThrows(NullPointerException.class, () -> SqlAuditValueSource.valueOf(null));
	}
	
	@Test
	void valuesContainsBothSources() {
		SqlAuditValueSource[] values = SqlAuditValueSource.values();
		assertEquals(2, values.length);
		assertEquals(SqlAuditValueSource.ORM_CLOCK, values[0]);
		assertEquals(SqlAuditValueSource.DATABASE, values[1]);
	}
	
	@Test
	void valueOfReturnsMatchingConstant() {
		assertEquals(SqlAuditValueSource.ORM_CLOCK, SqlAuditValueSource.valueOf("ORM_CLOCK"));
		assertEquals(SqlAuditValueSource.DATABASE, SqlAuditValueSource.valueOf("DATABASE"));
	}
	
	@Test
	void nameAndOrdinalAreStable() {
		assertEquals(0, SqlAuditValueSource.ORM_CLOCK.ordinal());
		assertEquals(1, SqlAuditValueSource.DATABASE.ordinal());
		assertEquals("ORM_CLOCK", SqlAuditValueSource.ORM_CLOCK.name());
		assertEquals("DATABASE", SqlAuditValueSource.DATABASE.name());
	}
}
