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

import java.sql.Connection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlIsolationLevel}.<br>
 *
 * @author Luis-St
 */
class SqlIsolationLevelTest {
	
	@Test
	void valueOfUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> SqlIsolationLevel.valueOf("UNKNOWN"));
	}
	
	@Test
	void jdbcLevelReturnsSerializable() {
		assertEquals(Connection.TRANSACTION_SERIALIZABLE, SqlIsolationLevel.SERIALIZABLE.jdbcLevel());
	}
	
	@Test
	void jdbcLevelReturnsRepeatableRead() {
		assertEquals(Connection.TRANSACTION_REPEATABLE_READ, SqlIsolationLevel.REPEATABLE_READ.jdbcLevel());
	}
	
	@Test
	void jdbcLevelReturnsReadCommitted() {
		assertEquals(Connection.TRANSACTION_READ_COMMITTED, SqlIsolationLevel.READ_COMMITTED.jdbcLevel());
	}
	
	@Test
	void jdbcLevelReturnsReadUncommitted() {
		assertEquals(Connection.TRANSACTION_READ_UNCOMMITTED, SqlIsolationLevel.READ_UNCOMMITTED.jdbcLevel());
	}
	
	@Test
	void valuesContainsAllConstants() {
		SqlIsolationLevel[] values = SqlIsolationLevel.values();
		assertEquals(4, values.length);
		assertEquals(SqlIsolationLevel.SERIALIZABLE, values[0]);
		assertEquals(SqlIsolationLevel.REPEATABLE_READ, values[1]);
		assertEquals(SqlIsolationLevel.READ_COMMITTED, values[2]);
		assertEquals(SqlIsolationLevel.READ_UNCOMMITTED, values[3]);
	}
	
	@Test
	void valueOfReturnsConstant() {
		assertSame(SqlIsolationLevel.READ_COMMITTED, SqlIsolationLevel.valueOf("READ_COMMITTED"));
	}
	
	@Test
	void jdbcLevelsAreAllDistinct() {
		Set<Integer> levels = new HashSet<>();
		for (SqlIsolationLevel level : SqlIsolationLevel.values()) {
			levels.add(level.jdbcLevel());
		}
		assertEquals(4, levels.size());
	}
}
