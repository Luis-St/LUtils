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

package net.luis.utils.io.database.migration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlCheckConstraintInfo}.<br>
 *
 * @author Luis-St
 */
class SqlCheckConstraintInfoTest {
	
	@Test
	void constructWithNameAndClause() {
		SqlCheckConstraintInfo info = new SqlCheckConstraintInfo("chk_age", "age > 0");
		assertEquals("chk_age", info.constraintName());
		assertEquals("age > 0", info.checkClause());
	}
	
	@Test
	void constructWithNullConstraintName() {
		assertThrows(NullPointerException.class, () -> new SqlCheckConstraintInfo(null, "age > 0"));
	}
	
	@Test
	void constructWithNullCheckClause() {
		assertThrows(NullPointerException.class, () -> new SqlCheckConstraintInfo("chk_age", null));
	}
	
	@Test
	void equalInstancesAreEqual() {
		SqlCheckConstraintInfo first = new SqlCheckConstraintInfo("chk_age", "age > 0");
		SqlCheckConstraintInfo second = new SqlCheckConstraintInfo("chk_age", "age > 0");
		SqlCheckConstraintInfo different = new SqlCheckConstraintInfo("chk_age", "age >= 0");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, different);
	}
}
