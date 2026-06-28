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

package net.luis.utils.io.database.query.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlJoinType}.<br>
 *
 * @author Luis-St
 */
class SqlJoinTypeTest {
	
	@Test
	void valuesContainsAllConstants() {
		SqlJoinType[] values = SqlJoinType.values();
		assertEquals(5, values.length);
		assertArrayEquals(new SqlJoinType[] { SqlJoinType.INNER, SqlJoinType.LEFT, SqlJoinType.RIGHT, SqlJoinType.FULL, SqlJoinType.CROSS }, values);
	}
	
	@Test
	void valueOfResolvesConstants() {
		assertEquals(SqlJoinType.INNER, SqlJoinType.valueOf("INNER"));
		assertEquals(SqlJoinType.LEFT, SqlJoinType.valueOf("LEFT"));
		assertEquals(SqlJoinType.RIGHT, SqlJoinType.valueOf("RIGHT"));
		assertEquals(SqlJoinType.FULL, SqlJoinType.valueOf("FULL"));
		assertEquals(SqlJoinType.CROSS, SqlJoinType.valueOf("CROSS"));
	}
}
