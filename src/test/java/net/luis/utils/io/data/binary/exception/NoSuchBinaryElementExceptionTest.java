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

package net.luis.utils.io.data.binary.exception;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NoSuchBinaryElementException}.<br>
 *
 * @author Luis-St
 */
class NoSuchBinaryElementExceptionTest {
	
	@Test
	void constructWithoutDetails() {
		NoSuchBinaryElementException exception = new NoSuchBinaryElementException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessage() {
		NoSuchBinaryElementException exception = new NoSuchBinaryElementException("missing");
		
		assertEquals("missing", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageAndCause() {
		IllegalStateException cause = new IllegalStateException("broken");
		NoSuchBinaryElementException exception = new NoSuchBinaryElementException("missing", cause);
		
		assertEquals("missing", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithCause() {
		IllegalStateException cause = new IllegalStateException("broken");
		NoSuchBinaryElementException exception = new NoSuchBinaryElementException(cause);
		
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithCauseDerivesMessage() {
		IllegalStateException cause = new IllegalStateException("broken");
		NoSuchBinaryElementException exception = new NoSuchBinaryElementException(cause);
		
		assertNotNull(exception.getMessage());
		assertEquals(cause.toString(), exception.getMessage());
		assertTrue(exception.getMessage().contains("IllegalStateException"));
		assertTrue(exception.getMessage().contains("broken"));
	}
	
	@Test
	void constructWithNullMessage() {
		NoSuchBinaryElementException exception = assertDoesNotThrow(() -> new NoSuchBinaryElementException((String) null));
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullCause() {
		NoSuchBinaryElementException fromCause = assertDoesNotThrow(() -> new NoSuchBinaryElementException((Throwable) null));
		assertNull(fromCause.getCause());
		
		NoSuchBinaryElementException fromMessageAndCause = assertDoesNotThrow(() -> new NoSuchBinaryElementException("message", null));
		assertEquals("message", fromMessageAndCause.getMessage());
		assertNull(fromMessageAndCause.getCause());
	}
	
	@Test
	void exceptionIsNoSuchElementException() {
		NoSuchBinaryElementException exception = new NoSuchBinaryElementException();
		
		assertInstanceOf(NoSuchElementException.class, exception);
		assertInstanceOf(RuntimeException.class, exception);
	}
	
	@Test
	void throwAndCatchAsNoSuchElementException() {
		assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchBinaryElementException("missing");
		});
	}
}
