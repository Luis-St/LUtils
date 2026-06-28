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
 * Test class for {@link SqlSavepoint}.<br>
 *
 * @author Luis-St
 */
class SqlSavepointTest {
	
	@Test
	void constructWithName() {
		SqlSavepoint savepoint = new SqlSavepoint("sp1");
		assertEquals("sp1", savepoint.name());
	}
	
	@Test
	void constructWithNullNameIsAccepted() {
		SqlSavepoint savepoint = assertDoesNotThrow(() -> new SqlSavepoint(null));
		assertNull(savepoint.name());
	}
	
	@Test
	void constructWithEmptyName() {
		assertEquals("", new SqlSavepoint("").name());
	}
	
	@Test
	void accessorReturnsName() {
		assertEquals("abc", new SqlSavepoint("abc").name());
	}
	
	@Test
	void equalsAndHashCodeByName() {
		SqlSavepoint first = new SqlSavepoint("sp");
		SqlSavepoint second = new SqlSavepoint("sp");
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new SqlSavepoint("other"));
	}
	
	@Test
	void toStringContainsName() {
		String string = new SqlSavepoint("sp1").toString();
		assertTrue(string.contains("sp1"));
		assertTrue(string.contains("SqlSavepoint"));
	}
}
