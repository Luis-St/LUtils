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
 * Test class for {@link SqlResultCountException}.<br>
 *
 * @author Luis-St
 */
class SqlResultCountExceptionTest {
	
	@Test
	void constructor() {
		assertDoesNotThrow(() -> new SqlResultCountException(null, 0, 0));
	}
	
	@Test
	void constructStoresMessage() {
		SqlResultCountException exception = new SqlResultCountException("too many rows", 1, 5);
		assertEquals("too many rows", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructStoresExpectedAndActual() {
		SqlResultCountException exception = new SqlResultCountException("mismatch", 1, 5);
		assertEquals(1, exception.expected());
		assertEquals(5, exception.actual());
	}
	
	@Test
	void constructWithZeroCounts() {
		SqlResultCountException exception = new SqlResultCountException("none", 0, 0);
		assertEquals(0, exception.expected());
		assertEquals(0, exception.actual());
	}
	
	@Test
	void constructWithLargeCounts() {
		SqlResultCountException exception = new SqlResultCountException("huge", Long.MAX_VALUE, Long.MIN_VALUE);
		assertEquals(Long.MAX_VALUE, exception.expected());
		assertEquals(Long.MIN_VALUE, exception.actual());
	}
	
	@Test
	void extendsSqlClientException() {
		assertInstanceOf(SqlClientException.class, new SqlResultCountException("message", 1, 2));
	}
}
