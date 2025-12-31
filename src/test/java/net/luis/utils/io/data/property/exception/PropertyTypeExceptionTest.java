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

package net.luis.utils.io.data.property.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PropertyTypeException}.<br>
 *
 * @author Luis-St
 */
class PropertyTypeExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		PropertyTypeException exception = new PropertyTypeException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected property object but found property array";
		PropertyTypeException exception = new PropertyTypeException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		PropertyTypeException nullMessageException = new PropertyTypeException((String) null);
		assertNull(nullMessageException.getMessage());
		
		PropertyTypeException emptyMessageException = new PropertyTypeException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Type conversion failed";
		ClassCastException cause = new ClassCastException("Cannot cast String to Integer");
		PropertyTypeException exception = new PropertyTypeException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		PropertyTypeException nullMessageException = new PropertyTypeException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		PropertyTypeException nullCauseException = new PropertyTypeException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		PropertyTypeException bothNullException = new PropertyTypeException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		ClassCastException cause = new ClassCastException("Type mismatch error");
		PropertyTypeException exception = new PropertyTypeException(cause);
		
		assertTrue(exception.getMessage().contains("ClassCastException"));
		assertTrue(exception.getMessage().contains("Type mismatch error"));
		assertSame(cause, exception.getCause());
		
		PropertyTypeException nullCauseException = new PropertyTypeException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NumberFormatException numberCause = new NumberFormatException("Not a valid number");
		PropertyTypeException numberException = new PropertyTypeException("Number conversion error", numberCause);
		assertSame(numberCause, numberException.getCause());
		
		IllegalArgumentException argCause = new IllegalArgumentException("Invalid type argument");
		PropertyTypeException argException = new PropertyTypeException("Type argument error", argCause);
		assertSame(argCause, argException.getCause());
		
		IOException ioCause = new IOException("Stream read error during type check");
		PropertyTypeException ioException = new PropertyTypeException(ioCause);
		assertSame(ioCause, ioException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		PropertyTypeException exception = new PropertyTypeException();
		
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		PropertyTypeException thrownException = assertThrows(PropertyTypeException.class, () -> {
			throw new PropertyTypeException("Type mismatch");
		});
		assertEquals("Type mismatch", thrownException.getMessage());
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new PropertyTypeException("Another type error");
		});
		assertInstanceOf(PropertyTypeException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new PropertyTypeException("Yet another type error");
		});
		assertInstanceOf(PropertyTypeException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Expected a property value, but found: property object";
		PropertyTypeException exception = new PropertyTypeException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		ClassCastException originalCause = new ClassCastException("String cannot be cast to Number");
		PropertyTypeException exception = new PropertyTypeException("Type casting failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new PropertyTypeException("Test type error");
		} catch (PropertyTypeException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			
			StackTraceElement topElement = e.getStackTrace()[0];
			assertEquals("stackTracePreservation", topElement.getMethodName());
		}
	}
	
	@Test
	void causeChaining() {
		NumberFormatException rootCause = new NumberFormatException("Invalid number format");
		ClassCastException intermediateCause = new ClassCastException("Cast failed");
		intermediateCause.initCause(rootCause);
		
		PropertyTypeException topException = new PropertyTypeException("Top level type error", intermediateCause);
		
		assertSame(intermediateCause, topException.getCause());
		assertSame(rootCause, topException.getCause().getCause());
		
		Throwable current = topException;
		int chainLength = 0;
		while (current != null) {
			chainLength++;
			current = current.getCause();
		}
		assertEquals(3, chainLength);
	}
	
	@Test
	void suppressedExceptions() {
		PropertyTypeException mainException = new PropertyTypeException("Main type error");
		PropertyTypeException suppressedException1 = new PropertyTypeException("Suppressed type error 1");
		PropertyTypeException suppressedException2 = new PropertyTypeException("Suppressed type error 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldTypeErrorScenarios() {
		PropertyTypeException objectExpectedException = new PropertyTypeException("Expected a property object, but found: property array");
		assertTrue(objectExpectedException.getMessage().contains("Expected a property object"));
		assertTrue(objectExpectedException.getMessage().contains("property array"));
		
		PropertyTypeException valueExpectedException = new PropertyTypeException("Expected a property value, but found: property null");
		assertTrue(valueExpectedException.getMessage().contains("Expected a property value"));
		assertTrue(valueExpectedException.getMessage().contains("property null"));
		
		PropertyTypeException arrayExpectedException = new PropertyTypeException("Expected a property array, but found: property value");
		assertTrue(arrayExpectedException.getMessage().contains("Expected a property array"));
		assertTrue(arrayExpectedException.getMessage().contains("property value"));
		
		NumberFormatException numberError = new NumberFormatException("For input string: \"not_a_number\"");
		PropertyTypeException numberConversionException = new PropertyTypeException("Failed to convert string to number", numberError);
		assertTrue(numberConversionException.getMessage().contains("Failed to convert"));
		assertSame(numberError, numberConversionException.getCause());
		
		PropertyTypeException booleanConversionException = new PropertyTypeException("Cannot convert 'maybe' to boolean value");
		assertTrue(booleanConversionException.getMessage().contains("Cannot convert"));
		assertTrue(booleanConversionException.getMessage().contains("boolean"));
	}
	
	@Test
	void messageFormatsForCommonTypes() {
		String[] commonMessages = {
			"Expected a property object, but found: property array",
			"Expected a property array, but found: property value",
			"Expected a property value, but found: property null",
			"Expected a property null, but found: property object",
			"Cannot convert property array to string",
			"Cannot convert property object to number",
			"Invalid boolean value: expected true or false",
			"Type mismatch: expected Number but got String"
		};
		
		for (String message : commonMessages) {
			PropertyTypeException exception = new PropertyTypeException(message);
			assertEquals(message, exception.getMessage());
			assertNotNull(exception.getMessage());
			assertFalse(exception.getMessage().isEmpty());
		}
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new PropertyTypeException());
		assertDoesNotThrow(() -> new PropertyTypeException((String) null));
		assertDoesNotThrow(() -> new PropertyTypeException((Throwable) null));
		assertDoesNotThrow(() -> new PropertyTypeException(null, null));
		
		PropertyTypeException nullMessageException = new PropertyTypeException((String) null);
		assertNull(nullMessageException.getMessage());
		
		PropertyTypeException nullCauseException = new PropertyTypeException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		PropertyTypeException bothNullException = new PropertyTypeException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void exceptionUsageInTypeCasting() {
		assertThrows(PropertyTypeException.class, () -> {
			throw new PropertyTypeException("Cannot convert string 'hello' to integer");
		});
		
		assertThrows(PropertyTypeException.class, () -> {
			throw new PropertyTypeException("Expected a property array, but found: property object");
		});
		
		assertThrows(PropertyTypeException.class, () -> {
			throw new PropertyTypeException("Cannot interpret number as boolean value");
		});
	}
}
