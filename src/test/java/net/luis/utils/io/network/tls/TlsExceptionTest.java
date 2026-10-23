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

package net.luis.utils.io.network.tls;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsException}.<br>
 *
 * @author Luis-St
 */
class TlsExceptionTest {
	
	@Test
	void constructWithoutDetails() {
		TlsException exception = new TlsException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertInstanceOf(IOException.class, exception);
	}
	
	@Test
	void constructWithMessage() {
		TlsException exception = new TlsException("failure");
		assertEquals("failure", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageAndCause() {
		IOException cause = new IOException("io");
		TlsException exception = new TlsException("failure", cause);
		assertEquals("failure", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithCause() {
		IOException cause = new IOException("io");
		TlsException exception = new TlsException(cause);
		assertSame(cause, exception.getCause());
		assertEquals(cause.toString(), exception.getMessage());
	}
	
	@Test
	void constructWithNullMessage() {
		assertDoesNotThrow(() -> new TlsException((String) null));
		assertNull(new TlsException((String) null).getMessage());
	}
	
	@Test
	void constructWithNullMessageAndNullCause() {
		TlsException exception = new TlsException(null, null);
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullCause() {
		TlsException exception = new TlsException((Throwable) null);
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void throwAndCatchAsIOException() {
		IOException exception = assertThrows(IOException.class, () -> {
			throw new TlsException("x");
		});
		assertInstanceOf(TlsException.class, exception);
		assertEquals("x", exception.getMessage());
	}
}
