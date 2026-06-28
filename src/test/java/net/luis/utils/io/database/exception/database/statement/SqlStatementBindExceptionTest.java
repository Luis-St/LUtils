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

package net.luis.utils.io.database.exception.database.statement;

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlStatementBindException}.<br>
 *
 * @author Luis-St
 */
class SqlStatementBindExceptionTest {
	
	@Test
	void constructWithNullCauseThrows() {
		assertThrows(NullPointerException.class, () -> new SqlStatementBindException("message", null));
		assertThrows(NullPointerException.class, () -> new SqlStatementBindException("message", null, 2));
	}
	
	@Test
	void constructWithNullMessage() {
		assertDoesNotThrow(() -> new SqlStatementBindException(null, new SQLException("cause")));
	}
	
	@Test
	void constructStoresMessageAndCause() {
		SQLException cause = new SQLException("reason", "07006", 1);
		SqlStatementBindException exception = new SqlStatementBindException("error", cause);
		assertEquals("error", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithoutColumnIndexHasEmptyIndex() {
		SqlStatementBindException exception = new SqlStatementBindException("error", new SQLException("reason"));
		assertTrue(exception.columnIndex().isEmpty());
	}
	
	@Test
	void constructStoresColumnIndex() {
		SqlStatementBindException exception = new SqlStatementBindException("error", new SQLException("reason"), 4);
		assertEquals(OptionalInt.of(4), exception.columnIndex());
	}
	
	@Test
	void constructWithUnknownColumnIndexHasEmptyIndex() {
		SqlStatementBindException exception = new SqlStatementBindException("error", new SQLException("reason"), -1);
		assertTrue(exception.columnIndex().isEmpty());
	}
	
	@Test
	void extendsSqlDatabaseException() {
		assertInstanceOf(SqlDatabaseException.class, new SqlStatementBindException("error", new SQLException("reason")));
	}
}
