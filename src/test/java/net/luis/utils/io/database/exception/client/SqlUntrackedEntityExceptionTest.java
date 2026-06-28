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

package net.luis.utils.io.database.exception.client;

import net.luis.utils.io.database.exception.SqlClientException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SqlUntrackedEntityException}.<br>
 *
 * @author Luis-St
 */
class SqlUntrackedEntityExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new SqlUntrackedEntityException(null));
		assertDoesNotThrow(() -> new SqlUntrackedEntityException(null, null));
	}
	
	@Test
	void constructWithMessage() {
		SqlUntrackedEntityException exception = new SqlUntrackedEntityException("error message");
		assertEquals("error message", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageAndCause() {
		Throwable cause = new RuntimeException("cause");
		SqlUntrackedEntityException exception = new SqlUntrackedEntityException("error message", cause);
		assertEquals("error message", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithNullCause() {
		SqlUntrackedEntityException exception = new SqlUntrackedEntityException("error message", null);
		assertEquals("error message", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void extendsSqlClientException() {
		assertInstanceOf(SqlClientException.class, new SqlUntrackedEntityException("message"));
	}
}
