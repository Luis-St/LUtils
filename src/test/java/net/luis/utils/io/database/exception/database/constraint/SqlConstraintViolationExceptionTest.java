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

package net.luis.utils.io.database.exception.database.constraint;

import net.luis.utils.io.database.exception.SqlDatabaseException;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlConstraintViolationException}.<br>
 *
 * @author Luis-St
 */
class SqlConstraintViolationExceptionTest {
	
	@Test
	void constructWithNullCauseThrows() {
		assertThrows(NullPointerException.class, () -> new SqlConstraintViolationException("message", null));
		assertThrows(NullPointerException.class, () -> new SqlConstraintViolationException("message", null, "constraint"));
	}
	
	@Test
	void constructWithNullMessage() {
		assertDoesNotThrow(() -> new SqlConstraintViolationException(null, new SQLException("cause")));
	}
	
	@Test
	void constructStoresMessageAndCause() {
		SQLException cause = new SQLException("reason", "23000", 1);
		SqlConstraintViolationException exception = new SqlConstraintViolationException("error", cause);
		assertEquals("error", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithoutConstraintNameHasEmptyName() {
		SqlConstraintViolationException exception = new SqlConstraintViolationException("error", new SQLException("reason"));
		assertTrue(exception.constraintName().isEmpty());
	}
	
	@Test
	void constructStoresConstraintName() {
		SqlConstraintViolationException exception = new SqlConstraintViolationException("error", new SQLException("reason"), "uq_users_email");
		assertEquals(Optional.of("uq_users_email"), exception.constraintName());
	}
	
	@Test
	void constructWithNullConstraintNameHasEmptyName() {
		SqlConstraintViolationException exception = new SqlConstraintViolationException("error", new SQLException("reason"), null);
		assertTrue(exception.constraintName().isEmpty());
	}
	
	@Test
	void extendsSqlDatabaseException() {
		assertInstanceOf(SqlDatabaseException.class, new SqlConstraintViolationException("error", new SQLException("reason")));
	}
}
