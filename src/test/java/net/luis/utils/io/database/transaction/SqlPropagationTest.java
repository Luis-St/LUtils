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

package net.luis.utils.io.database.transaction;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlPropagation}.<br>
 *
 * @author Luis-St
 */
class SqlPropagationTest {
	
	@Test
	void valueOfUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlPropagation.valueOf("UNKNOWN"));
	}
	
	@Test
	void valuesContainsAllConstants() {
		SqlPropagation[] values = SqlPropagation.values();
		assertEquals(7, values.length);
		assertEquals(SqlPropagation.REQUIRED, values[0]);
		assertEquals(SqlPropagation.REQUIRES_NEW, values[1]);
		assertEquals(SqlPropagation.NESTED, values[2]);
		assertEquals(SqlPropagation.SUPPORTS, values[3]);
		assertEquals(SqlPropagation.NOT_SUPPORTED, values[4]);
		assertEquals(SqlPropagation.MANDATORY, values[5]);
		assertEquals(SqlPropagation.NEVER, values[6]);
	}
	
	@Test
	void valueOfReturnsConstant() {
		assertSame(SqlPropagation.NESTED, SqlPropagation.valueOf("NESTED"));
		assertSame(SqlPropagation.NEVER, SqlPropagation.valueOf("NEVER"));
	}
}
