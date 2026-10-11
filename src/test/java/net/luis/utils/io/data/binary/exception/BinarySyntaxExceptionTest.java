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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinarySyntaxException}.<br>
 *
 * @author Luis-St
 */
class BinarySyntaxExceptionTest {
	
	@Test
	void constructWithoutDetails() {
		BinarySyntaxException exception = new BinarySyntaxException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessage() {
		BinarySyntaxException exception = new BinarySyntaxException("invalid data");
		
		assertEquals("invalid data", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageAndCause() {
		IOException cause = new IOException("io");
		BinarySyntaxException exception = new BinarySyntaxException("invalid data", cause);
		
		assertEquals("invalid data", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithCause() {
		IOException cause = new IOException("io");
		BinarySyntaxException exception = new BinarySyntaxException(cause);
		
		assertSame(cause, exception.getCause());
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("io"));
	}
	
	@Test
	void constructWithNullMessage() {
		BinarySyntaxException exception = assertDoesNotThrow(() -> new BinarySyntaxException((String) null));
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullCause() {
		BinarySyntaxException fromCause = assertDoesNotThrow(() -> new BinarySyntaxException((Throwable) null));
		assertNull(fromCause.getCause());
		
		BinarySyntaxException fromMessageAndCause = assertDoesNotThrow(() -> new BinarySyntaxException("message", null));
		assertEquals("message", fromMessageAndCause.getMessage());
		assertNull(fromMessageAndCause.getCause());
	}
	
	@Test
	void exceptionIsUnchecked() {
		assertInstanceOf(RuntimeException.class, new BinarySyntaxException());
	}
	
	@Test
	void throwAndCatchAsRuntimeException() {
		BinarySyntaxException exception = assertThrows(BinarySyntaxException.class, () -> {
			throw new BinarySyntaxException("bad");
		});
		assertEquals("bad", exception.getMessage());
		
		assertThrows(RuntimeException.class, () -> {
			throw new BinarySyntaxException("bad");
		});
	}
}
