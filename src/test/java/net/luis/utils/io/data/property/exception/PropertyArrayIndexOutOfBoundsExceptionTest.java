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

package net.luis.utils.io.data.property.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyArrayIndexOutOfBoundsException}.<br>
 *
 * @author Luis-St
 */
class PropertyArrayIndexOutOfBoundsExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		PropertyArrayIndexOutOfBoundsException exception = new PropertyArrayIndexOutOfBoundsException();
		assertNull(exception.getMessage());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Custom index error message";
		PropertyArrayIndexOutOfBoundsException exception = new PropertyArrayIndexOutOfBoundsException(message);
		assertEquals(message, exception.getMessage());
		
		PropertyArrayIndexOutOfBoundsException nullMessageException = new PropertyArrayIndexOutOfBoundsException(null);
		assertNull(nullMessageException.getMessage());
		
		PropertyArrayIndexOutOfBoundsException emptyMessageException = new PropertyArrayIndexOutOfBoundsException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithIndex() {
		PropertyArrayIndexOutOfBoundsException exception = new PropertyArrayIndexOutOfBoundsException(5);
		assertTrue(exception.getMessage().contains("5"));
		assertTrue(exception.getMessage().contains("out of bounds"));
		
		PropertyArrayIndexOutOfBoundsException negativeException = new PropertyArrayIndexOutOfBoundsException(-1);
		assertTrue(negativeException.getMessage().contains("-1"));
		assertTrue(negativeException.getMessage().contains("out of bounds"));
		
		PropertyArrayIndexOutOfBoundsException zeroException = new PropertyArrayIndexOutOfBoundsException(0);
		assertTrue(zeroException.getMessage().contains("0"));
		assertTrue(zeroException.getMessage().contains("out of bounds"));
	}
	
	@Test
	void constructorWithIndexAndSize() {
		PropertyArrayIndexOutOfBoundsException exception = new PropertyArrayIndexOutOfBoundsException(5, 3);
		assertTrue(exception.getMessage().contains("5"));
		assertTrue(exception.getMessage().contains("3"));
		assertTrue(exception.getMessage().contains("out of bounds"));
		
		PropertyArrayIndexOutOfBoundsException negativeIndexException = new PropertyArrayIndexOutOfBoundsException(-1, 10);
		assertTrue(negativeIndexException.getMessage().contains("-1"));
		assertTrue(negativeIndexException.getMessage().contains("10"));
		assertTrue(negativeIndexException.getMessage().contains("out of bounds"));
		
		PropertyArrayIndexOutOfBoundsException zeroBothException = new PropertyArrayIndexOutOfBoundsException(0, 0);
		assertTrue(zeroBothException.getMessage().contains("0"));
		assertTrue(zeroBothException.getMessage().contains("out of bounds"));
		
		PropertyArrayIndexOutOfBoundsException largeIndexException = new PropertyArrayIndexOutOfBoundsException(1000, 500);
		assertTrue(largeIndexException.getMessage().contains("1000"));
		assertTrue(largeIndexException.getMessage().contains("500"));
	}
	
	@Test
	void exceptionInheritance() {
		PropertyArrayIndexOutOfBoundsException exception = new PropertyArrayIndexOutOfBoundsException();
		
		assertInstanceOf(ArrayIndexOutOfBoundsException.class, exception);
		assertInstanceOf(IndexOutOfBoundsException.class, exception);
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		PropertyArrayIndexOutOfBoundsException thrownException = assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> {
			throw new PropertyArrayIndexOutOfBoundsException(10, 5);
		});
		assertTrue(thrownException.getMessage().contains("10"));
		assertTrue(thrownException.getMessage().contains("5"));
		
		ArrayIndexOutOfBoundsException caughtAsParent = assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
			throw new PropertyArrayIndexOutOfBoundsException(3);
		});
		assertInstanceOf(PropertyArrayIndexOutOfBoundsException.class, caughtAsParent);
		
		IndexOutOfBoundsException caughtAsIndexOOB = assertThrows(IndexOutOfBoundsException.class, () -> {
			throw new PropertyArrayIndexOutOfBoundsException(-1);
		});
		assertInstanceOf(PropertyArrayIndexOutOfBoundsException.class, caughtAsIndexOOB);
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new PropertyArrayIndexOutOfBoundsException(0, 0);
		});
		assertInstanceOf(PropertyArrayIndexOutOfBoundsException.class, caughtAsRuntime);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Index 5 is out of bounds for property array of size 3";
		PropertyArrayIndexOutOfBoundsException exception = new PropertyArrayIndexOutOfBoundsException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new PropertyArrayIndexOutOfBoundsException(10, 5);
		} catch (PropertyArrayIndexOutOfBoundsException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			
			StackTraceElement topElement = e.getStackTrace()[0];
			assertEquals("stackTracePreservation", topElement.getMethodName());
		}
	}
	
	@Test
	void suppressedExceptions() {
		PropertyArrayIndexOutOfBoundsException mainException = new PropertyArrayIndexOutOfBoundsException(10, 5);
		PropertyArrayIndexOutOfBoundsException suppressedException1 = new PropertyArrayIndexOutOfBoundsException(20, 10);
		PropertyArrayIndexOutOfBoundsException suppressedException2 = new PropertyArrayIndexOutOfBoundsException(30, 15);
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldScenarios() {
		PropertyArrayIndexOutOfBoundsException accessBeyondEnd = new PropertyArrayIndexOutOfBoundsException(10, 5);
		assertTrue(accessBeyondEnd.getMessage().contains("10"));
		assertTrue(accessBeyondEnd.getMessage().contains("5"));
		
		PropertyArrayIndexOutOfBoundsException negativeAccess = new PropertyArrayIndexOutOfBoundsException(-1, 100);
		assertTrue(negativeAccess.getMessage().contains("-1"));
		
		PropertyArrayIndexOutOfBoundsException emptyArrayAccess = new PropertyArrayIndexOutOfBoundsException(0, 0);
		assertTrue(emptyArrayAccess.getMessage().contains("0"));
		
		PropertyArrayIndexOutOfBoundsException firstElementEmptyArray = new PropertyArrayIndexOutOfBoundsException(0);
		assertTrue(firstElementEmptyArray.getMessage().contains("0"));
	}
	
	@Test
	void boundaryValues() {
		PropertyArrayIndexOutOfBoundsException maxIntIndex = new PropertyArrayIndexOutOfBoundsException(Integer.MAX_VALUE);
		assertTrue(maxIntIndex.getMessage().contains(String.valueOf(Integer.MAX_VALUE)));
		
		PropertyArrayIndexOutOfBoundsException minIntIndex = new PropertyArrayIndexOutOfBoundsException(Integer.MIN_VALUE);
		assertTrue(minIntIndex.getMessage().contains(String.valueOf(Integer.MIN_VALUE)));
		
		PropertyArrayIndexOutOfBoundsException maxBothParams = new PropertyArrayIndexOutOfBoundsException(Integer.MAX_VALUE, Integer.MAX_VALUE);
		assertTrue(maxBothParams.getMessage().contains(String.valueOf(Integer.MAX_VALUE)));
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new PropertyArrayIndexOutOfBoundsException());
		assertDoesNotThrow(() -> new PropertyArrayIndexOutOfBoundsException(null));
		assertDoesNotThrow(() -> new PropertyArrayIndexOutOfBoundsException(0));
		assertDoesNotThrow(() -> new PropertyArrayIndexOutOfBoundsException(0, 0));
		
		PropertyArrayIndexOutOfBoundsException nullMessageException = new PropertyArrayIndexOutOfBoundsException(null);
		assertNull(nullMessageException.getMessage());
	}
	
	@Test
	void compatibilityWithArrayIndexOutOfBoundsException() {
		PropertyArrayIndexOutOfBoundsException propertyException = new PropertyArrayIndexOutOfBoundsException(5, 3);
		
		ArrayIndexOutOfBoundsException arrayException = propertyException;
		assertTrue(arrayException.getMessage().contains("5"));
		
		assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
			throw new PropertyArrayIndexOutOfBoundsException(10);
		});
		
		try {
			throw new PropertyArrayIndexOutOfBoundsException(7, 4);
		} catch (ArrayIndexOutOfBoundsException e) {
			assertInstanceOf(PropertyArrayIndexOutOfBoundsException.class, e);
			assertTrue(e.getMessage().contains("7"));
			assertTrue(e.getMessage().contains("4"));
		}
	}
	
	@Test
	void exceptionUsageInArrayAccess() {
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> {
			throw new PropertyArrayIndexOutOfBoundsException(5, 3);
		});
		
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> {
			throw new PropertyArrayIndexOutOfBoundsException(-1);
		});
		
		assertThrows(PropertyArrayIndexOutOfBoundsException.class, () -> {
			throw new PropertyArrayIndexOutOfBoundsException("Custom: Index 10 is out of bounds for array with 5 elements");
		});
	}
	
	@Test
	void messageFormatConsistency() {
		PropertyArrayIndexOutOfBoundsException exception1 = new PropertyArrayIndexOutOfBoundsException(5);
		PropertyArrayIndexOutOfBoundsException exception2 = new PropertyArrayIndexOutOfBoundsException(5, 3);
		
		assertTrue(exception1.getMessage().toLowerCase().contains("out of bounds"));
		assertTrue(exception2.getMessage().toLowerCase().contains("out of bounds"));
		
		assertTrue(exception1.getMessage().toLowerCase().contains("property array"));
		assertTrue(exception2.getMessage().toLowerCase().contains("property array"));
	}
}
