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

package net.luis.utils.io.database.exception;

import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlDatabaseException}.<br>
 *
 * @author Luis-St
 */
class SqlDatabaseExceptionTest {
	
	@Test
	void constructorWithNullCauseThrows() {
		assertThrows(NullPointerException.class, () -> new SqlDatabaseException("message", null));
		assertThrows(NullPointerException.class, () -> new SqlDatabaseException("message", null, true));
	}
	
	@Test
	void constructorWithNullMessage() {
		assertDoesNotThrow(() -> new SqlDatabaseException(null, new SQLException("cause")));
		assertDoesNotThrow(() -> new SqlDatabaseException(null, null, -1));
	}
	
	@Test
	void constructFromSqlExceptionLiftsDiagnostics() {
		SQLException cause = new SQLException("reason", "23505", 1062);
		SqlDatabaseException exception = new SqlDatabaseException("error", cause);
		assertEquals("error", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals(Optional.of("23505"), exception.sqlState());
		assertEquals(OptionalInt.of(1062), exception.vendorErrorCode());
	}
	
	@Test
	void constructFromSqlExceptionWithoutSqlState() {
		SQLException cause = new SQLException("reason");
		SqlDatabaseException exception = new SqlDatabaseException("error", cause);
		assertTrue(exception.sqlState().isEmpty());
		assertTrue(exception.sqlStateClass().isEmpty());
		// A plain SQLException reports a vendor error code of 0, which is distinct from the unknown sentinel (-1)
		assertEquals(OptionalInt.of(0), exception.vendorErrorCode());
	}
	
	@Test
	void vendorErrorCodeEmptyForUnknownSentinel() {
		SqlDatabaseException exception = new SqlDatabaseException("error", null, -1);
		assertTrue(exception.vendorErrorCode().isEmpty());
	}
	
	@Test
	void constructFromSqlExceptionNotTransientByDefault() {
		SqlDatabaseException exception = new SqlDatabaseException("error", new SQLException("reason"));
		assertFalse(exception.isTransient());
	}
	
	@Test
	void constructFromTransientSqlExceptionIsTransient() {
		SqlDatabaseException exception = new SqlDatabaseException("error", new SQLTransientException("reason"));
		assertTrue(exception.isTransient());
	}
	
	@Test
	void constructFromRecoverableSqlExceptionIsTransient() {
		SqlDatabaseException exception = new SqlDatabaseException("error", new SQLRecoverableException("reason"));
		assertTrue(exception.isTransient());
	}
	
	@Test
	void constructWithExplicitTransientFlag() {
		SqlDatabaseException transientException = new SqlDatabaseException("error", new SQLException("reason"), true);
		assertTrue(transientException.isTransient());
		
		SqlDatabaseException nonTransientException = new SqlDatabaseException("error", new SQLException("reason"), false);
		assertFalse(nonTransientException.isTransient());
	}
	
	@Test
	void constructChainsNextExceptionsAsSuppressed() {
		SQLException cause = new SQLException("first", "23505", 1);
		SQLException next = new SQLException("second", "23502", 2);
		cause.setNextException(next);
		
		SqlDatabaseException exception = new SqlDatabaseException("error", cause);
		Throwable[] suppressed = exception.getSuppressed();
		assertEquals(1, suppressed.length);
		assertSame(next, suppressed[0]);
	}
	
	@Test
	void constructHandBuiltWithoutCause() {
		SqlDatabaseException exception = new SqlDatabaseException("error", "08006", 500);
		assertEquals("error", exception.getMessage());
		assertNull(exception.getCause());
		assertEquals(Optional.of("08006"), exception.sqlState());
		assertEquals(OptionalInt.of(500), exception.vendorErrorCode());
		assertFalse(exception.isTransient());
	}
	
	@Test
	void constructHandBuiltWithUnknownDiagnostics() {
		SqlDatabaseException exception = new SqlDatabaseException("error", null, -1);
		assertTrue(exception.sqlState().isEmpty());
		assertTrue(exception.vendorErrorCode().isEmpty());
		assertTrue(exception.sqlStateClass().isEmpty());
	}
	
	@Test
	void sqlStateClassResolvesKnownClass() {
		SqlDatabaseException exception = new SqlDatabaseException("error", "23505", 1);
		assertEquals(Optional.of(SqlStateClass.INTEGRITY_CONSTRAINT), exception.sqlStateClass());
	}
	
	@Test
	void sqlStateClassEmptyForUnknownClass() {
		SqlDatabaseException exception = new SqlDatabaseException("error", "ZZ999", 1);
		assertTrue(exception.sqlStateClass().isEmpty());
	}
	
	@Test
	void extendsSqlException() {
		assertInstanceOf(SqlException.class, new SqlDatabaseException("error", new SQLException("reason")));
	}
}
