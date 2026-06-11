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

package net.luis.utils.io.database;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlReferentialAction}.<br>
 *
 * @author Luis-St
 */
class SqlReferentialActionTest {
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlReferentialAction.valueOf("UNKNOWN"));
	}
	
	@Test
	void valueOfWithNullThrows() {
		assertThrows(NullPointerException.class, () -> SqlReferentialAction.valueOf(null));
	}
	
	@Test
	void valueOfReturnsMatchingConstant() {
		assertSame(SqlReferentialAction.NO_ACTION, SqlReferentialAction.valueOf("NO_ACTION"));
		assertSame(SqlReferentialAction.RESTRICT, SqlReferentialAction.valueOf("RESTRICT"));
		assertSame(SqlReferentialAction.CASCADE, SqlReferentialAction.valueOf("CASCADE"));
		assertSame(SqlReferentialAction.SET_NULL, SqlReferentialAction.valueOf("SET_NULL"));
		assertSame(SqlReferentialAction.SET_DEFAULT, SqlReferentialAction.valueOf("SET_DEFAULT"));
	}
	
	@Test
	void valuesContainsAllConstants() {
		SqlReferentialAction[] values = SqlReferentialAction.values();
		assertEquals(5, values.length);
		assertArrayEquals(new SqlReferentialAction[] {
			SqlReferentialAction.NO_ACTION,
			SqlReferentialAction.RESTRICT,
			SqlReferentialAction.CASCADE,
			SqlReferentialAction.SET_NULL,
			SqlReferentialAction.SET_DEFAULT
		}, values);
	}
	
	@Test
	void valuesAreInDeclarationOrder() {
		assertEquals(0, SqlReferentialAction.NO_ACTION.ordinal());
		assertEquals(1, SqlReferentialAction.RESTRICT.ordinal());
		assertEquals(2, SqlReferentialAction.CASCADE.ordinal());
		assertEquals(3, SqlReferentialAction.SET_NULL.ordinal());
		assertEquals(4, SqlReferentialAction.SET_DEFAULT.ordinal());
		assertEquals("NO_ACTION", SqlReferentialAction.NO_ACTION.name());
		assertEquals("SET_DEFAULT", SqlReferentialAction.SET_DEFAULT.name());
	}
}
