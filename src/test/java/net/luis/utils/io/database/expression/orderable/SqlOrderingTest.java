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

package net.luis.utils.io.database.expression.orderable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlOrdering}.<br>
 *
 * @author Luis-St
 */
class SqlOrderingTest {
	
	@Test
	void valuesContainsAllConstants() {
		SqlOrdering[] values = SqlOrdering.values();
		assertEquals(3, values.length);
		assertArrayEquals(new SqlOrdering[] { SqlOrdering.DEFAULT, SqlOrdering.ASCENDING, SqlOrdering.DESCENDING }, values);
	}
	
	@Test
	void valueOfReturnsMatchingConstant() {
		assertSame(SqlOrdering.DEFAULT, SqlOrdering.valueOf("DEFAULT"));
		assertSame(SqlOrdering.DESCENDING, SqlOrdering.valueOf("DESCENDING"));
	}
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlOrdering.valueOf("UNKNOWN"));
	}
	
	@Test
	void nameAndOrdinalAreStable() {
		assertEquals(0, SqlOrdering.DEFAULT.ordinal());
		assertEquals(2, SqlOrdering.DESCENDING.ordinal());
		assertEquals("ASCENDING", SqlOrdering.ASCENDING.name());
	}
}
