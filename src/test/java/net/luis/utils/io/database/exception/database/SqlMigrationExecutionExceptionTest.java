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
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlMigrationExecutionException}.<br>
 *
 * @author Luis-St
 */
class SqlMigrationExecutionExceptionTest {
	
	@Test
	void constructWithNullCauseThrows() {
		assertThrows(NullPointerException.class, () -> new SqlMigrationExecutionException("message", null));
		assertThrows(NullPointerException.class, () -> new SqlMigrationExecutionException("message", null, 3));
	}
	
	@Test
	void constructWithNullMessage() {
		assertDoesNotThrow(() -> new SqlMigrationExecutionException(null, new SQLException("cause")));
	}
	
	@Test
	void constructStoresMessageAndCause() {
		SQLException cause = new SQLException("reason", "42601", 1);
		SqlMigrationExecutionException exception = new SqlMigrationExecutionException("error", cause);
		assertEquals("error", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithoutVersionHasEmptyVersion() {
		SqlMigrationExecutionException exception = new SqlMigrationExecutionException("error", new SQLException("reason"));
		assertTrue(exception.version().isEmpty());
	}
	
	@Test
	void constructStoresVersion() {
		SqlMigrationExecutionException exception = new SqlMigrationExecutionException("error", new SQLException("reason"), 7);
		assertEquals(OptionalInt.of(7), exception.version());
	}
	
	@Test
	void constructWithUnknownVersionSentinelHasEmptyVersion() {
		SqlMigrationExecutionException exception = new SqlMigrationExecutionException("error", new SQLException("reason"), -1);
		assertTrue(exception.version().isEmpty());
	}
	
	@Test
	void extendsSqlDatabaseException() {
		assertInstanceOf(SqlDatabaseException.class, new SqlMigrationExecutionException("error", new SQLException("reason")));
	}
}
