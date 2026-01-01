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

package net.luis.utils.io.data.toml.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlArrayIndexOutOfBoundsException}.<br>
 *
 * @author Luis-St
 */
class TomlArrayIndexOutOfBoundsExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		TomlArrayIndexOutOfBoundsException exception = new TomlArrayIndexOutOfBoundsException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Custom error message";
		TomlArrayIndexOutOfBoundsException exception = new TomlArrayIndexOutOfBoundsException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		TomlArrayIndexOutOfBoundsException nullMessageException = new TomlArrayIndexOutOfBoundsException(null);
		assertNull(nullMessageException.getMessage());
	}
	
	@Test
	void constructorWithIndex() {
		int index = 5;
		TomlArrayIndexOutOfBoundsException exception = new TomlArrayIndexOutOfBoundsException(index);
		
		String expectedMessage = "Index out of range: " + index;
		assertEquals(expectedMessage, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithIndexVariousValues() {
		TomlArrayIndexOutOfBoundsException positiveException = new TomlArrayIndexOutOfBoundsException(10);
		assertEquals("Index out of range: 10", positiveException.getMessage());
		
		TomlArrayIndexOutOfBoundsException negativeException = new TomlArrayIndexOutOfBoundsException(-1);
		assertEquals("Index out of range: -1", negativeException.getMessage());
		
		TomlArrayIndexOutOfBoundsException zeroException = new TomlArrayIndexOutOfBoundsException(0);
		assertEquals("Index out of range: 0", zeroException.getMessage());
		
		TomlArrayIndexOutOfBoundsException largeException = new TomlArrayIndexOutOfBoundsException(Integer.MAX_VALUE);
		assertEquals("Index out of range: " + Integer.MAX_VALUE, largeException.getMessage());
		
		TomlArrayIndexOutOfBoundsException veryNegativeException = new TomlArrayIndexOutOfBoundsException(Integer.MIN_VALUE);
		assertEquals("Index out of range: " + Integer.MIN_VALUE, veryNegativeException.getMessage());
	}
	
	@Test
	void exceptionInheritance() {
		TomlArrayIndexOutOfBoundsException exception = new TomlArrayIndexOutOfBoundsException();
		
		assertInstanceOf(IndexOutOfBoundsException.class, exception);
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		assertThrows(TomlArrayIndexOutOfBoundsException.class, () -> {
			throw new TomlArrayIndexOutOfBoundsException(5);
		});
		
		assertThrows(IndexOutOfBoundsException.class, () -> {
			throw new TomlArrayIndexOutOfBoundsException(-1);
		});
		
		assertThrows(RuntimeException.class, () -> {
			throw new TomlArrayIndexOutOfBoundsException("Custom message");
		});
	}
	
	@Test
	void messageFormatConsistency() {
		int index = 7;
		
		TomlArrayIndexOutOfBoundsException indexOnlyException = new TomlArrayIndexOutOfBoundsException(index);
		
		assertTrue(indexOnlyException.getMessage().contains("Index out of range: " + index));
	}
	
	@Test
	void messageReadability() {
		TomlArrayIndexOutOfBoundsException exception1 = new TomlArrayIndexOutOfBoundsException(10);
		String message1 = exception1.getMessage();
		
		assertNotNull(message1);
		assertFalse(message1.isEmpty());
		assertTrue(message1.toLowerCase().contains("index"));
		assertTrue(message1.contains("10"));
		
		TomlArrayIndexOutOfBoundsException exception2 = new TomlArrayIndexOutOfBoundsException(-2);
		String message2 = exception2.getMessage();
		
		assertNotNull(message2);
		assertFalse(message2.isEmpty());
		assertTrue(message2.toLowerCase().contains("index"));
		assertTrue(message2.contains("-2"));
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new TomlArrayIndexOutOfBoundsException(5);
		} catch (TomlArrayIndexOutOfBoundsException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			
			StackTraceElement topElement = e.getStackTrace()[0];
			assertEquals("stackTracePreservation", topElement.getMethodName());
		}
	}
	
	@Test
	void compatibilityWithIndexOutOfBoundsException() {
		TomlArrayIndexOutOfBoundsException tomlException = new TomlArrayIndexOutOfBoundsException(3);
		
		IndexOutOfBoundsException genericException = tomlException;
		assertTrue(genericException.getMessage().contains("3"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> {
			throw new TomlArrayIndexOutOfBoundsException(10);
		});
		
		try {
			throw new TomlArrayIndexOutOfBoundsException(7);
		} catch (IndexOutOfBoundsException e) {
			assertInstanceOf(TomlArrayIndexOutOfBoundsException.class, e);
			assertTrue(e.getMessage().contains("7"));
		}
	}
	
	@Test
	void realWorldArrayAccessScenarios() {
		TomlArrayIndexOutOfBoundsException emptyArrayException = new TomlArrayIndexOutOfBoundsException("Array is empty, cannot access index 0");
		assertTrue(emptyArrayException.getMessage().contains("Array is empty"));
		assertTrue(emptyArrayException.getMessage().contains("index 0"));
		
		TomlArrayIndexOutOfBoundsException negativeIndexException = new TomlArrayIndexOutOfBoundsException(-1);
		assertTrue(negativeIndexException.getMessage().contains("-1"));
		
		TomlArrayIndexOutOfBoundsException largeSizeException = new TomlArrayIndexOutOfBoundsException("Index 100 is out of bounds for array of size 50");
		assertTrue(largeSizeException.getMessage().contains("100"));
		assertTrue(largeSizeException.getMessage().contains("50"));
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new TomlArrayIndexOutOfBoundsException());
		assertDoesNotThrow(() -> new TomlArrayIndexOutOfBoundsException(null));
		assertDoesNotThrow(() -> new TomlArrayIndexOutOfBoundsException(0));
		
		TomlArrayIndexOutOfBoundsException nullMessageException = new TomlArrayIndexOutOfBoundsException(null);
		assertNull(nullMessageException.getMessage());
	}
}
