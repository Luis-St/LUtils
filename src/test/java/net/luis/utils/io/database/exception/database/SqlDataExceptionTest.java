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
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDataException}.<br>
 *
 * @author Luis-St
 */
class SqlDataExceptionTest {
	
	@Test
	void constructWithNullCauseThrows() {
		assertThrows(NullPointerException.class, () -> new SqlDataException("message", null));
		assertThrows(NullPointerException.class, () -> new SqlDataException("message", null, 1, "column"));
	}
	
	@Test
	void constructWithNullMessage() {
		assertDoesNotThrow(() -> new SqlDataException(null, new SQLException("cause")));
	}
	
	@Test
	void constructStoresMessageAndCause() {
		SQLException cause = new SQLException("reason", "22003", 1264);
		SqlDataException exception = new SqlDataException("error", cause);
		assertEquals("error", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals(Optional.of("22003"), exception.sqlState());
	}
	
	@Test
	void constructWithoutColumnHasEmptyDiagnostics() {
		SqlDataException exception = new SqlDataException("error", new SQLException("reason"));
		assertTrue(exception.columnIndex().isEmpty());
		assertTrue(exception.columnName().isEmpty());
	}
	
	@Test
	void constructStoresColumnIndexAndName() {
		SqlDataException exception = new SqlDataException("error", new SQLException("reason"), 3, "amount");
		assertEquals(OptionalInt.of(3), exception.columnIndex());
		assertEquals(Optional.of("amount"), exception.columnName());
	}
	
	@Test
	void constructWithUnknownColumnIndexHasEmptyIndex() {
		SqlDataException exception = new SqlDataException("error", new SQLException("reason"), -1, "amount");
		assertTrue(exception.columnIndex().isEmpty());
		assertEquals(Optional.of("amount"), exception.columnName());
	}
	
	@Test
	void constructWithNullColumnNameHasEmptyName() {
		SqlDataException exception = new SqlDataException("error", new SQLException("reason"), 3, null);
		assertEquals(OptionalInt.of(3), exception.columnIndex());
		assertTrue(exception.columnName().isEmpty());
	}
	
	@Test
	void extendsSqlDatabaseException() {
		assertInstanceOf(SqlDatabaseException.class, new SqlDataException("error", new SQLException("reason")));
	}
}
