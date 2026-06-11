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
 * Test class for {@link SqlNullOrdering}.<br>
 *
 * @author Luis-St
 */
class SqlNullOrderingTest {
	
	@Test
	void valuesContainsAllConstants() {
		SqlNullOrdering[] values = SqlNullOrdering.values();
		assertEquals(3, values.length);
		assertArrayEquals(new SqlNullOrdering[] { SqlNullOrdering.DEFAULT, SqlNullOrdering.NULLS_FIRST, SqlNullOrdering.NULLS_LAST }, values);
	}
	
	@Test
	void valueOfReturnsMatchingConstant() {
		assertSame(SqlNullOrdering.NULLS_FIRST, SqlNullOrdering.valueOf("NULLS_FIRST"));
		assertSame(SqlNullOrdering.NULLS_LAST, SqlNullOrdering.valueOf("NULLS_LAST"));
	}
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlNullOrdering.valueOf("UNKNOWN"));
	}
	
	@Test
	void nameAndOrdinalAreStable() {
		assertEquals(0, SqlNullOrdering.DEFAULT.ordinal());
		assertEquals(2, SqlNullOrdering.NULLS_LAST.ordinal());
		assertEquals("NULLS_FIRST", SqlNullOrdering.NULLS_FIRST.name());
	}
}
