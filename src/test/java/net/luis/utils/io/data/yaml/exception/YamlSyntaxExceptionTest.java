/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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
 * Test class for {@link YamlSyntaxException}.<br>
 *
 * @author Luis-St
 */
class YamlSyntaxExceptionTest {
	
	@Test
	void constructorNoArgs() {
		YamlSyntaxException exception = new YamlSyntaxException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Invalid yaml syntax";
		YamlSyntaxException exception = new YamlSyntaxException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithNullMessage() {
		YamlSyntaxException exception = new YamlSyntaxException((String) null);
		assertNull(exception.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Syntax error at line 5";
		Throwable cause = new RuntimeException("Root cause");
		YamlSyntaxException exception = new YamlSyntaxException(message, cause);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullMessageAndCause() {
		Throwable cause = new RuntimeException("Root cause");
		YamlSyntaxException exception = new YamlSyntaxException(null, cause);
		assertNull(exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithMessageAndNullCause() {
		String message = "Error message";
		YamlSyntaxException exception = new YamlSyntaxException(message, null);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithCause() {
		Throwable cause = new IllegalArgumentException("Invalid argument");
		YamlSyntaxException exception = new YamlSyntaxException(cause);
		assertEquals(cause.toString(), exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullCause() {
		YamlSyntaxException exception = new YamlSyntaxException((Throwable) null);
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void isRuntimeException() {
		YamlSyntaxException exception = new YamlSyntaxException();
		assertInstanceOf(RuntimeException.class, exception);
	}
	
	@Test
	void canBeThrown() {
		assertThrows(YamlSyntaxException.class, () -> {
			throw new YamlSyntaxException("Test exception");
		});
	}
	
	@Test
	void canBeCaught() {
		try {
			throw new YamlSyntaxException("Caught exception");
		} catch (YamlSyntaxException e) {
			assertEquals("Caught exception", e.getMessage());
		}
	}
	
	@Test
	void chainedExceptionPreservesCause() {
		IOException originalCause = new IOException("IO error");
		YamlSyntaxException exception = new YamlSyntaxException("Failed to parse", originalCause);
		
		assertNotNull(exception.getCause());
		assertInstanceOf(IOException.class, exception.getCause());
		assertEquals("IO error", exception.getCause().getMessage());
	}
	
	private static class IOException extends Exception {
		
		public IOException(String message) {
			super(message);
		}
	}
}
