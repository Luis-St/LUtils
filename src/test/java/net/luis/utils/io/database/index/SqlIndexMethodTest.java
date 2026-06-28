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

package net.luis.utils.io.database.index;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlIndexMethod}.<br>
 *
 * @author Luis-St
 */
class SqlIndexMethodTest {
	
	@Test
	void valuesContainsAllConstants() {
		SqlIndexMethod[] expected = {
			SqlIndexMethod.BTREE, SqlIndexMethod.HASH, SqlIndexMethod.GIN, SqlIndexMethod.GIST, SqlIndexMethod.BRIN,
			SqlIndexMethod.SPGIST, SqlIndexMethod.CLUSTERED, SqlIndexMethod.NONCLUSTERED, SqlIndexMethod.COLUMNSTORE, SqlIndexMethod.BITMAP
		};
		assertEquals(10, SqlIndexMethod.values().length);
		assertArrayEquals(expected, SqlIndexMethod.values());
	}
	
	@Test
	void valueOfReturnsMatchingConstant() {
		assertSame(SqlIndexMethod.BTREE, SqlIndexMethod.valueOf("BTREE"));
		assertSame(SqlIndexMethod.BITMAP, SqlIndexMethod.valueOf("BITMAP"));
	}
	
	@Test
	void nameAndOrdinalAreStable() {
		assertEquals(0, SqlIndexMethod.BTREE.ordinal());
		assertEquals(9, SqlIndexMethod.BITMAP.ordinal());
		assertEquals("GIN", SqlIndexMethod.GIN.name());
	}
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlIndexMethod.valueOf("UNKNOWN"));
	}
}
