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
 * Test class for {@link YamlTypeException}.<br>
 *
 * @author Luis-St
 */
class YamlTypeExceptionTest {
	
	@Test
	void constructorNoArgs() {
		YamlTypeException exception = new YamlTypeException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected YamlMapping but got YamlScalar";
		YamlTypeException exception = new YamlTypeException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithNullMessage() {
		YamlTypeException exception = new YamlTypeException((String) null);
		assertNull(exception.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Type mismatch";
		Throwable cause = new ClassCastException("Cannot cast");
		YamlTypeException exception = new YamlTypeException(message, cause);
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullMessageAndCause() {
		Throwable cause = new RuntimeException("Root cause");
		YamlTypeException exception = new YamlTypeException(null, cause);
		assertNull(exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithMessageAndNullCause() {
		String message = "Type error";
		YamlTypeException exception = new YamlTypeException(message, null);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithCause() {
		Throwable cause = new ClassCastException("Invalid cast");
		YamlTypeException exception = new YamlTypeException(cause);
		assertEquals(cause.toString(), exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullCause() {
		YamlTypeException exception = new YamlTypeException((Throwable) null);
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void isRuntimeException() {
		YamlTypeException exception = new YamlTypeException();
		assertInstanceOf(RuntimeException.class, exception);
	}
	
	@Test
	void canBeThrown() {
		assertThrows(YamlTypeException.class, () -> {
			throw new YamlTypeException("Test exception");
		});
	}
	
	@Test
	void canBeCaught() {
		try {
			throw new YamlTypeException("Caught exception");
		} catch (YamlTypeException e) {
			assertEquals("Caught exception", e.getMessage());
		}
	}
	
	@Test
	void messageContainsTypeInfo() {
		String expectedType = "YamlMapping";
		String actualType = "YamlScalar";
		String message = "Expected " + expectedType + " but got " + actualType;
		
		YamlTypeException exception = new YamlTypeException(message);
		
		assertTrue(exception.getMessage().contains(expectedType));
		assertTrue(exception.getMessage().contains(actualType));
	}
	
	@Test
	void chainedExceptionPreservesCause() {
		ClassCastException originalCause = new ClassCastException("Cast failed");
		YamlTypeException exception = new YamlTypeException("Type conversion failed", originalCause);
		
		assertNotNull(exception.getCause());
		assertInstanceOf(ClassCastException.class, exception.getCause());
		assertEquals("Cast failed", exception.getCause().getMessage());
	}
}
