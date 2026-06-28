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

package net.luis.utils.io.database.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTemporalPart}.<br>
 *
 * @author Luis-St
 */
class SqlTemporalPartTest {
	
	@Test
	void valuesContainsAllConstants() {
		SqlTemporalPart[] expected = {
			SqlTemporalPart.YEAR, SqlTemporalPart.MONTH, SqlTemporalPart.DAY, SqlTemporalPart.HOUR, SqlTemporalPart.MINUTE,
			SqlTemporalPart.SECOND, SqlTemporalPart.QUARTER, SqlTemporalPart.WEEK, SqlTemporalPart.DAY_OF_WEEK, SqlTemporalPart.DAY_OF_YEAR
		};
		assertEquals(10, SqlTemporalPart.values().length);
		assertArrayEquals(expected, SqlTemporalPart.values());
	}
	
	@Test
	void valueOfReturnsMatchingConstant() {
		assertSame(SqlTemporalPart.YEAR, SqlTemporalPart.valueOf("YEAR"));
		assertSame(SqlTemporalPart.DAY_OF_YEAR, SqlTemporalPart.valueOf("DAY_OF_YEAR"));
	}
	
	@Test
	void nameAndOrdinalAreStable() {
		assertEquals(0, SqlTemporalPart.YEAR.ordinal());
		assertEquals(9, SqlTemporalPart.DAY_OF_YEAR.ordinal());
		assertEquals("DAY_OF_WEEK", SqlTemporalPart.DAY_OF_WEEK.name());
	}
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlTemporalPart.valueOf("CENTURY"));
	}
}
