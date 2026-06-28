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

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlUniqueConstraintException}.<br>
 *
 * @author Luis-St
 */
class SqlUniqueConstraintExceptionTest {
	
	@Test
	void constructWithNullCauseThrows() {
		assertThrows(NullPointerException.class, () -> new SqlUniqueConstraintException("message", null));
		assertThrows(NullPointerException.class, () -> new SqlUniqueConstraintException("message", null, "constraint"));
	}
	
	@Test
	void constructWithNullMessage() {
		assertDoesNotThrow(() -> new SqlUniqueConstraintException(null, new SQLException("cause")));
	}
	
	@Test
	void constructStoresMessageAndCause() {
		SQLException cause = new SQLException("reason", "23505", 1);
		SqlUniqueConstraintException exception = new SqlUniqueConstraintException("error", cause);
		assertEquals("error", exception.getMessage());
		assertSame(cause, exception.getCause());
		assertEquals(Optional.of("23505"), exception.sqlState());
	}
	
	@Test
	void constructStoresConstraintName() {
		SqlUniqueConstraintException exception = new SqlUniqueConstraintException("error", new SQLException("reason"), "my_constraint");
		assertEquals(Optional.of("my_constraint"), exception.constraintName());
	}
	
	@Test
	void constructWithoutConstraintNameHasEmptyName() {
		SqlUniqueConstraintException exception = new SqlUniqueConstraintException("error", new SQLException("reason"));
		assertTrue(exception.constraintName().isEmpty());
	}
	
	@Test
	void extendsSqlConstraintViolationException() {
		assertInstanceOf(SqlConstraintViolationException.class, new SqlUniqueConstraintException("error", new SQLException("reason")));
	}
}
