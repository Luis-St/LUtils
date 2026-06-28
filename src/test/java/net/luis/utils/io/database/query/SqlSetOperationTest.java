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

package net.luis.utils.io.database.query;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlSetOperation}.<br>
 *
 * @author Luis-St
 */
class SqlSetOperationTest {
	
	@Test
	void valuesContainsAllConstants() {
		SqlSetOperation[] values = SqlSetOperation.values();
		assertEquals(4, values.length);
		assertArrayEquals(new SqlSetOperation[] { SqlSetOperation.UNION, SqlSetOperation.UNION_ALL, SqlSetOperation.INTERSECT, SqlSetOperation.EXCEPT }, values);
	}
	
	@Test
	void valueOfResolvesConstants() {
		assertEquals(SqlSetOperation.UNION, SqlSetOperation.valueOf("UNION"));
		assertEquals(SqlSetOperation.UNION_ALL, SqlSetOperation.valueOf("UNION_ALL"));
		assertEquals(SqlSetOperation.INTERSECT, SqlSetOperation.valueOf("INTERSECT"));
		assertEquals(SqlSetOperation.EXCEPT, SqlSetOperation.valueOf("EXCEPT"));
	}
}
