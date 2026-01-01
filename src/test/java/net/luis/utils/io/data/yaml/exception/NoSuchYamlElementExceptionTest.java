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

package net.luis.utils.io.data.yaml.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NoSuchYamlElementException}.<br>
 *
 * @author Luis-St
 */
class NoSuchYamlElementExceptionTest {
	
	@Test
	void constructorNoArgs() {
		NoSuchYamlElementException exception = new NoSuchYamlElementException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Element 'config' not found";
		NoSuchYamlElementException exception = new NoSuchYamlElementException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithNullMessage() {
		NoSuchYamlElementException exception = new NoSuchYamlElementException((String) null);
		assertNull(exception.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Missing required element";
		Throwable cause = new RuntimeException("Root cause");
		NoSuchYamlElementException exception = new NoSuchYamlElementException(message, cause);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullMessageAndCause() {
		Throwable cause = new RuntimeException("Root cause");
		NoSuchYamlElementException exception = new NoSuchYamlElementException(null, cause);
		assertNull(exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithMessageAndNullCause() {
		String message = "Element not found";
		NoSuchYamlElementException exception = new NoSuchYamlElementException(message, null);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithCause() {
		Throwable cause = new IllegalStateException("Invalid state");
		NoSuchYamlElementException exception = new NoSuchYamlElementException(cause);
		assertEquals(cause.toString(), exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullCause() {
		NoSuchYamlElementException exception = new NoSuchYamlElementException((Throwable) null);
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void isRuntimeException() {
		NoSuchYamlElementException exception = new NoSuchYamlElementException();
		assertInstanceOf(RuntimeException.class, exception);
	}
	
	@Test
	void canBeThrown() {
		assertThrows(NoSuchYamlElementException.class, () -> {
			throw new NoSuchYamlElementException("Test exception");
		});
	}
	
	@Test
	void canBeCaught() {
		try {
			throw new NoSuchYamlElementException("Caught exception");
		} catch (NoSuchYamlElementException e) {
			assertEquals("Caught exception", e.getMessage());
		}
	}
	
	@Test
	void messageContainsKeyInfo() {
		String key = "database.connection.host";
		String message = "No such yaml element: '" + key + "'";
		
		NoSuchYamlElementException exception = new NoSuchYamlElementException(message);
		
		assertTrue(exception.getMessage().contains(key));
	}
	
	@Test
	void chainedExceptionPreservesCause() {
		NullPointerException originalCause = new NullPointerException("Null key");
		NoSuchYamlElementException exception = new NoSuchYamlElementException("Element lookup failed", originalCause);
		
		assertNotNull(exception.getCause());
		assertInstanceOf(NullPointerException.class, exception.getCause());
		assertEquals("Null key", exception.getCause().getMessage());
	}
}
