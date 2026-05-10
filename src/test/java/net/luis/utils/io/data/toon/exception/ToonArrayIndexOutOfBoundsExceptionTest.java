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

package net.luis.utils.io.data.toon.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonArrayIndexOutOfBoundsException}.<br>
 *
 * @author Luis-St
 */
class ToonArrayIndexOutOfBoundsExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		ToonArrayIndexOutOfBoundsException exception = new ToonArrayIndexOutOfBoundsException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Custom error message";
		ToonArrayIndexOutOfBoundsException exception = new ToonArrayIndexOutOfBoundsException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		ToonArrayIndexOutOfBoundsException nullMessageException = new ToonArrayIndexOutOfBoundsException(null);
		assertNull(nullMessageException.getMessage());
	}
	
	@Test
	void constructorWithIndex() {
		int index = 5;
		ToonArrayIndexOutOfBoundsException exception = new ToonArrayIndexOutOfBoundsException(index);
		
		String expectedMessage = "Index out of range: " + index;
		assertEquals(expectedMessage, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithIndexVariousValues() {
		ToonArrayIndexOutOfBoundsException positiveException = new ToonArrayIndexOutOfBoundsException(10);
		assertEquals("Index out of range: 10", positiveException.getMessage());
		
		ToonArrayIndexOutOfBoundsException negativeException = new ToonArrayIndexOutOfBoundsException(-1);
		assertEquals("Index out of range: -1", negativeException.getMessage());
		
		ToonArrayIndexOutOfBoundsException zeroException = new ToonArrayIndexOutOfBoundsException(0);
		assertEquals("Index out of range: 0", zeroException.getMessage());
		
		ToonArrayIndexOutOfBoundsException largeException = new ToonArrayIndexOutOfBoundsException(Integer.MAX_VALUE);
		assertEquals("Index out of range: " + Integer.MAX_VALUE, largeException.getMessage());
		
		ToonArrayIndexOutOfBoundsException veryNegativeException = new ToonArrayIndexOutOfBoundsException(Integer.MIN_VALUE);
		assertEquals("Index out of range: " + Integer.MIN_VALUE, veryNegativeException.getMessage());
	}
	
	@Test
	void exceptionInheritance() {
		ToonArrayIndexOutOfBoundsException exception = new ToonArrayIndexOutOfBoundsException();
		
		assertInstanceOf(IndexOutOfBoundsException.class, exception);
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		assertThrows(ToonArrayIndexOutOfBoundsException.class, () -> {
			throw new ToonArrayIndexOutOfBoundsException(5);
		});
		
		assertThrows(IndexOutOfBoundsException.class, () -> {
			throw new ToonArrayIndexOutOfBoundsException(-1);
		});
		
		assertThrows(RuntimeException.class, () -> {
			throw new ToonArrayIndexOutOfBoundsException("Custom message");
		});
	}
	
	@Test
	void messageFormatConsistency() {
		int index = 7;
		
		ToonArrayIndexOutOfBoundsException indexOnlyException = new ToonArrayIndexOutOfBoundsException(index);
		
		assertTrue(indexOnlyException.getMessage().contains("Index out of range: " + index));
	}
	
	@Test
	void messageReadability() {
		ToonArrayIndexOutOfBoundsException exception1 = new ToonArrayIndexOutOfBoundsException(10);
		String message1 = exception1.getMessage();
		
		assertNotNull(message1);
		assertFalse(message1.isEmpty());
		assertTrue(message1.toLowerCase().contains("index"));
		assertTrue(message1.contains("10"));
		
		ToonArrayIndexOutOfBoundsException exception2 = new ToonArrayIndexOutOfBoundsException(-2);
		String message2 = exception2.getMessage();
		
		assertNotNull(message2);
		assertFalse(message2.isEmpty());
		assertTrue(message2.toLowerCase().contains("index"));
		assertTrue(message2.contains("-2"));
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new ToonArrayIndexOutOfBoundsException(5);
		} catch (ToonArrayIndexOutOfBoundsException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			
			StackTraceElement topElement = e.getStackTrace()[0];
			assertEquals("stackTracePreservation", topElement.getMethodName());
		}
	}
	
	@Test
	void compatibilityWithIndexOutOfBoundsException() {
		ToonArrayIndexOutOfBoundsException toonException = new ToonArrayIndexOutOfBoundsException(3);
		
		IndexOutOfBoundsException genericException = toonException;
		assertTrue(genericException.getMessage().contains("3"));
		
		assertThrows(IndexOutOfBoundsException.class, () -> {
			throw new ToonArrayIndexOutOfBoundsException(10);
		});
		
		try {
			throw new ToonArrayIndexOutOfBoundsException(7);
		} catch (IndexOutOfBoundsException e) {
			assertInstanceOf(ToonArrayIndexOutOfBoundsException.class, e);
			assertTrue(e.getMessage().contains("7"));
		}
	}
	
	@Test
	void realWorldArrayAccessScenarios() {
		ToonArrayIndexOutOfBoundsException emptyArrayException = new ToonArrayIndexOutOfBoundsException("Array is empty, cannot access index 0");
		assertTrue(emptyArrayException.getMessage().contains("Array is empty"));
		assertTrue(emptyArrayException.getMessage().contains("index 0"));
		
		ToonArrayIndexOutOfBoundsException negativeIndexException = new ToonArrayIndexOutOfBoundsException(-1);
		assertTrue(negativeIndexException.getMessage().contains("-1"));
		
		ToonArrayIndexOutOfBoundsException largeSizeException = new ToonArrayIndexOutOfBoundsException("Index 100 is out of bounds for array of size 50");
		assertTrue(largeSizeException.getMessage().contains("100"));
		assertTrue(largeSizeException.getMessage().contains("50"));
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new ToonArrayIndexOutOfBoundsException());
		assertDoesNotThrow(() -> new ToonArrayIndexOutOfBoundsException(null));
		assertDoesNotThrow(() -> new ToonArrayIndexOutOfBoundsException(0));
		
		ToonArrayIndexOutOfBoundsException nullMessageException = new ToonArrayIndexOutOfBoundsException(null);
		assertNull(nullMessageException.getMessage());
	}
}
