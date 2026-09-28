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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryTypeException}.<br>
 *
 * @author Luis-St
 */
class BinaryTypeExceptionTest {
	
	@Test
	void constructWithoutDetails() {
		BinaryTypeException exception = new BinaryTypeException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessage() {
		BinaryTypeException exception = new BinaryTypeException("wrong type");
		
		assertEquals("wrong type", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageAndCause() {
		IllegalStateException cause = new IllegalStateException("broken");
		BinaryTypeException exception = new BinaryTypeException("wrong type", cause);
		
		assertEquals("wrong type", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithCause() {
		IllegalStateException cause = new IllegalStateException("broken");
		BinaryTypeException exception = new BinaryTypeException(cause);
		
		assertSame(cause, exception.getCause());
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("broken"));
	}
	
	@Test
	void constructWithNullMessage() {
		BinaryTypeException exception = assertDoesNotThrow(() -> new BinaryTypeException((String) null));
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullCause() {
		BinaryTypeException fromCause = assertDoesNotThrow(() -> new BinaryTypeException((Throwable) null));
		assertNull(fromCause.getCause());
		
		BinaryTypeException fromMessageAndCause = assertDoesNotThrow(() -> new BinaryTypeException("message", null));
		assertEquals("message", fromMessageAndCause.getMessage());
		assertNull(fromMessageAndCause.getCause());
	}
	
	@Test
	void exceptionIsUnchecked() {
		assertInstanceOf(RuntimeException.class, new BinaryTypeException());
	}
	
	@Test
	void throwAndCatchAsRuntimeException() {
		BinaryTypeException exception = assertThrows(BinaryTypeException.class, () -> {
			throw new BinaryTypeException("mismatch");
		});
		assertEquals("mismatch", exception.getMessage());
		
		assertThrows(RuntimeException.class, () -> {
			throw new BinaryTypeException("mismatch");
		});
	}
}
