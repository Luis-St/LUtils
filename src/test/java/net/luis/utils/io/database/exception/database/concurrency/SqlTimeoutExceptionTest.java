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

package net.luis.utils.io.database.exception.database.concurrency;

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlTimeoutException}.<br>
 *
 * @author Luis-St
 */
class SqlTimeoutExceptionTest {
	
	@Test
	void constructWithNullCauseThrows() {
		assertThrows(NullPointerException.class, () -> new SqlTimeoutException("message", null));
	}
	
	@Test
	void constructWithNullMessage() {
		assertDoesNotThrow(() -> new SqlTimeoutException(null, new SQLException("cause")));
	}
	
	@Test
	void constructStoresMessageAndCause() {
		SQLException cause = new SQLException("reason", "57014", 0);
		SqlTimeoutException exception = new SqlTimeoutException("error", cause);
		assertEquals("error", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructLiftsDiagnostics() {
		SQLException cause = new SQLException("reason", "57014", 0);
		SqlTimeoutException exception = new SqlTimeoutException("error", cause);
		assertEquals(Optional.of("57014"), exception.sqlState());
		assertEquals(OptionalInt.of(0), exception.vendorErrorCode());
	}
	
	@Test
	void isTransient() {
		SqlTimeoutException exception = new SqlTimeoutException("error", new SQLException("reason"));
		assertTrue(exception.isTransient());
	}
	
	@Test
	void extendsSqlDatabaseException() {
		assertInstanceOf(SqlDatabaseException.class, new SqlTimeoutException("error", new SQLException("reason")));
	}
}
