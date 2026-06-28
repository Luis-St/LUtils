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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlClientException}.<br>
 *
 * @author Luis-St
 */
class SqlClientExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new SqlClientException((String) null));
		assertDoesNotThrow(() -> new SqlClientException((Throwable) null));
		assertDoesNotThrow(() -> new SqlClientException(null, null));
	}
	
	@Test
	void constructWithMessage() {
		SqlClientException exception = new SqlClientException("error message");
		assertEquals("error message", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageAndCause() {
		Throwable cause = new RuntimeException("cause");
		SqlClientException exception = new SqlClientException("error message", cause);
		assertEquals("error message", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithCause() {
		Throwable cause = new RuntimeException("cause");
		SqlClientException exception = new SqlClientException(cause);
		assertSame(cause, exception.getCause());
		assertEquals(cause.toString(), exception.getMessage());
	}
	
	@Test
	void extendsSqlException() {
		assertInstanceOf(SqlException.class, new SqlClientException("message"));
	}
}
