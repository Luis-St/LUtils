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

package net.luis.utils.io.database.exception.database;

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlQueryExecutionException}.<br>
 *
 * @author Luis-St
 */
class SqlQueryExecutionExceptionTest {
	
	@Test
	void constructWithNullCauseThrows() {
		assertThrows(NullPointerException.class, () -> new SqlQueryExecutionException("message", null));
		assertThrows(NullPointerException.class, () -> new SqlQueryExecutionException("message", null, "SELECT 1"));
	}
	
	@Test
	void constructWithNullMessage() {
		assertDoesNotThrow(() -> new SqlQueryExecutionException(null, new SQLException("cause")));
	}
	
	@Test
	void constructStoresMessageAndCause() {
		SQLException cause = new SQLException("reason", "HY000", 1);
		SqlQueryExecutionException exception = new SqlQueryExecutionException("error", cause);
		assertEquals("error", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithoutSqlHasEmptySql() {
		SqlQueryExecutionException exception = new SqlQueryExecutionException("error", new SQLException("reason"));
		assertTrue(exception.sql().isEmpty());
	}
	
	@Test
	void constructStoresSql() {
		SqlQueryExecutionException exception = new SqlQueryExecutionException("error", new SQLException("reason"), "SELECT * FROM users");
		assertEquals(Optional.of("SELECT * FROM users"), exception.sql());
	}
	
	@Test
	void constructWithNullSqlHasEmptySql() {
		SqlQueryExecutionException exception = new SqlQueryExecutionException("error", new SQLException("reason"), null);
		assertTrue(exception.sql().isEmpty());
	}
	
	@Test
	void extendsSqlDatabaseException() {
		assertInstanceOf(SqlDatabaseException.class, new SqlQueryExecutionException("error", new SQLException("reason")));
	}
}
