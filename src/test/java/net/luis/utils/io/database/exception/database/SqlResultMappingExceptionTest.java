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
 * Test class for {@link SqlResultMappingException}.<br>
 *
 * @author Luis-St
 */
class SqlResultMappingExceptionTest {
	
	@Test
	void constructWithNullSqlCauseThrows() {
		assertThrows(NullPointerException.class, () -> new SqlResultMappingException("message", null));
		assertThrows(NullPointerException.class, () -> new SqlResultMappingException("message", null, String.class, "column"));
	}
	
	@Test
	void constructWithNullThrowableCauseThrows() {
		assertThrows(NullPointerException.class, () -> new SqlResultMappingException("message", null, String.class));
	}
	
	@Test
	void constructWithNullMessage() {
		assertDoesNotThrow(() -> new SqlResultMappingException(null, new SQLException("cause")));
	}
	
	@Test
	void constructFromSqlExceptionStoresMessageAndCause() {
		SQLException cause = new SQLException("reason", "22018", 1);
		SqlResultMappingException exception = new SqlResultMappingException("error", cause);
		assertEquals("error", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals(Optional.of("22018"), exception.sqlState());
	}
	
	@Test
	void constructFromSqlExceptionWithoutDetailsHasEmptyDiagnostics() {
		SqlResultMappingException exception = new SqlResultMappingException("error", new SQLException("reason"));
		assertTrue(exception.targetType().isEmpty());
		assertTrue(exception.column().isEmpty());
	}
	
	@Test
	void constructFromSqlExceptionStoresTargetTypeAndColumn() {
		SqlResultMappingException exception = new SqlResultMappingException("error", new SQLException("reason"), Integer.class, "age");
		assertEquals(Optional.of(Integer.class), exception.targetType());
		assertEquals(Optional.of("age"), exception.column());
	}
	
	@Test
	void constructFromSqlExceptionWithNullDetailsHasEmptyDiagnostics() {
		SqlResultMappingException exception = new SqlResultMappingException("error", new SQLException("reason"), null, null);
		assertTrue(exception.targetType().isEmpty());
		assertTrue(exception.column().isEmpty());
	}
	
	@Test
	void constructFromThrowableStoresCauseAndTargetType() {
		Throwable cause = new IllegalStateException("reflective failure");
		SqlResultMappingException exception = new SqlResultMappingException("error", cause, Double.class);
		assertEquals("error", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals(Optional.of(Double.class), exception.targetType());
		assertTrue(exception.column().isEmpty());
	}
	
	@Test
	void constructFromThrowableHasNoJdbcDiagnostics() {
		SqlResultMappingException exception = new SqlResultMappingException("error", new IllegalStateException("boom"), Double.class);
		assertTrue(exception.sqlState().isEmpty());
		assertTrue(exception.vendorErrorCode().isEmpty());
	}
	
	@Test
	void constructFromThrowableWithNullTargetTypeHasEmptyTargetType() {
		SqlResultMappingException exception = new SqlResultMappingException("error", new IllegalStateException("boom"), null);
		assertTrue(exception.targetType().isEmpty());
	}
	
	@Test
	void extendsSqlDatabaseException() {
		assertInstanceOf(SqlDatabaseException.class, new SqlResultMappingException("error", new SQLException("reason")));
	}
}
