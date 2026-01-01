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

package net.luis.utils.io.data.json.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonArrayIndexOutOfBoundsException}.<br>
 *
 * @author Luis-St
 */
class JsonArrayIndexOutOfBoundsExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		JsonArrayIndexOutOfBoundsException exception = new JsonArrayIndexOutOfBoundsException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Custom error message";
		JsonArrayIndexOutOfBoundsException exception = new JsonArrayIndexOutOfBoundsException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		JsonArrayIndexOutOfBoundsException nullMessageException = new JsonArrayIndexOutOfBoundsException((String) null);
		assertNull(nullMessageException.getMessage());
	}
	
	@Test
	void constructorWithIndex() {
		int index = 5;
		JsonArrayIndexOutOfBoundsException exception = new JsonArrayIndexOutOfBoundsException(index);
		
		String expectedMessage = "Json array index out of bounds: " + index;
		assertEquals(expectedMessage, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithIndexVariousValues() {
		JsonArrayIndexOutOfBoundsException positiveException = new JsonArrayIndexOutOfBoundsException(10);
		assertEquals("Json array index out of bounds: 10", positiveException.getMessage());
		
		JsonArrayIndexOutOfBoundsException negativeException = new JsonArrayIndexOutOfBoundsException(-1);
		assertEquals("Json array index out of bounds: -1", negativeException.getMessage());
		
		JsonArrayIndexOutOfBoundsException zeroException = new JsonArrayIndexOutOfBoundsException(0);
		assertEquals("Json array index out of bounds: 0", zeroException.getMessage());
		
		JsonArrayIndexOutOfBoundsException largeException = new JsonArrayIndexOutOfBoundsException(Integer.MAX_VALUE);
		assertEquals("Json array index out of bounds: " + Integer.MAX_VALUE, largeException.getMessage());
		
		JsonArrayIndexOutOfBoundsException veryNegativeException = new JsonArrayIndexOutOfBoundsException(Integer.MIN_VALUE);
		assertEquals("Json array index out of bounds: " + Integer.MIN_VALUE, veryNegativeException.getMessage());
	}
	
	@Test
	void constructorWithIndexAndSize() {
		int index = 5;
		int size = 3;
		JsonArrayIndexOutOfBoundsException exception = new JsonArrayIndexOutOfBoundsException(index, size);
		
		String expectedMessage = "Json array index out of bounds: " + index + " of size " + size;
		assertEquals(expectedMessage, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithIndexAndSizeVariousValues() {
		JsonArrayIndexOutOfBoundsException normalException = new JsonArrayIndexOutOfBoundsException(3, 2);
		assertEquals("Json array index out of bounds: 3 of size 2", normalException.getMessage());
		
		JsonArrayIndexOutOfBoundsException negativeIndexException = new JsonArrayIndexOutOfBoundsException(-1, 5);
		assertEquals("Json array index out of bounds: -1 of size 5", negativeIndexException.getMessage());
		
		JsonArrayIndexOutOfBoundsException zeroSizeException = new JsonArrayIndexOutOfBoundsException(0, 0);
		assertEquals("Json array index out of bounds: 0 of size 0", zeroSizeException.getMessage());
		
		JsonArrayIndexOutOfBoundsException largeException = new JsonArrayIndexOutOfBoundsException(1000, 999);
		assertEquals("Json array index out of bounds: 1000 of size 999", largeException.getMessage());
		
		JsonArrayIndexOutOfBoundsException equalException = new JsonArrayIndexOutOfBoundsException(5, 5);
		assertEquals("Json array index out of bounds: 5 of size 5", equalException.getMessage());
	}
	
	@Test
	void exceptionInheritance() {
		JsonArrayIndexOutOfBoundsException exception = new JsonArrayIndexOutOfBoundsException();
		
		assertInstanceOf(ArrayIndexOutOfBoundsException.class, exception);
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		assertThrows(JsonArrayIndexOutOfBoundsException.class, () -> {
			throw new JsonArrayIndexOutOfBoundsException(5, 3);
		});
		
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
			throw new JsonArrayIndexOutOfBoundsException(-1);
		});
		
		assertThrows(RuntimeException.class, () -> {
			throw new JsonArrayIndexOutOfBoundsException("Custom message");
		});
	}
	
	@Test
	void messageFormatConsistency() {
		int index = 7;
		int size = 4;
		
		JsonArrayIndexOutOfBoundsException indexOnlyException = new JsonArrayIndexOutOfBoundsException(index);
		JsonArrayIndexOutOfBoundsException indexAndSizeException = new JsonArrayIndexOutOfBoundsException(index, size);
		
		assertTrue(indexOnlyException.getMessage().contains("Json array index out of bounds: " + index));
		assertTrue(indexAndSizeException.getMessage().contains("Json array index out of bounds: " + index));
		assertTrue(indexAndSizeException.getMessage().contains("of size " + size));
	}
	
	@Test
	void messageReadability() {
		JsonArrayIndexOutOfBoundsException exception1 = new JsonArrayIndexOutOfBoundsException(10, 5);
		String message1 = exception1.getMessage();
		
		assertNotNull(message1);
		assertFalse(message1.isEmpty());
		assertTrue(message1.toLowerCase().contains("index"));
		assertTrue(message1.toLowerCase().contains("bounds"));
		assertTrue(message1.contains("10"));
		assertTrue(message1.contains("5"));
		
		JsonArrayIndexOutOfBoundsException exception2 = new JsonArrayIndexOutOfBoundsException(-2);
		String message2 = exception2.getMessage();
		
		assertNotNull(message2);
		assertFalse(message2.isEmpty());
		assertTrue(message2.toLowerCase().contains("index"));
		assertTrue(message2.toLowerCase().contains("bounds"));
		assertTrue(message2.contains("-2"));
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new JsonArrayIndexOutOfBoundsException(5, 3);
		} catch (JsonArrayIndexOutOfBoundsException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			
			StackTraceElement topElement = e.getStackTrace()[0];
			assertEquals("stackTracePreservation", topElement.getMethodName());
		}
	}
}
