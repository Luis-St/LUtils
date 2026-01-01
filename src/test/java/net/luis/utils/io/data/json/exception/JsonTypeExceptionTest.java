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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link JsonTypeException}.<br>
 *
 * @author Luis-St
 */
class JsonTypeExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		JsonTypeException exception = new JsonTypeException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected json object but found json array";
		JsonTypeException exception = new JsonTypeException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		JsonTypeException nullMessageException = new JsonTypeException((String) null);
		assertNull(nullMessageException.getMessage());
		
		JsonTypeException emptyMessageException = new JsonTypeException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Type conversion failed";
		ClassCastException cause = new ClassCastException("Cannot cast String to Integer");
		JsonTypeException exception = new JsonTypeException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		JsonTypeException nullMessageException = new JsonTypeException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		JsonTypeException nullCauseException = new JsonTypeException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		JsonTypeException bothNullException = new JsonTypeException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		ClassCastException cause = new ClassCastException("Type mismatch error");
		JsonTypeException exception = new JsonTypeException(cause);
		
		assertTrue(exception.getMessage().contains("ClassCastException"));
		assertTrue(exception.getMessage().contains("Type mismatch error"));
		assertSame(cause, exception.getCause());
		
		JsonTypeException nullCauseException = new JsonTypeException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NumberFormatException numberCause = new NumberFormatException("Not a valid number");
		JsonTypeException numberException = new JsonTypeException("Number conversion error", numberCause);
		assertSame(numberCause, numberException.getCause());
		
		IllegalArgumentException argCause = new IllegalArgumentException("Invalid type argument");
		JsonTypeException argException = new JsonTypeException("Type argument error", argCause);
		assertSame(argCause, argException.getCause());
		
		IOException ioCause = new IOException("Stream read error during type check");
		JsonTypeException ioException = new JsonTypeException(ioCause);
		assertSame(ioCause, ioException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		JsonTypeException exception = new JsonTypeException();
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		JsonTypeException thrownException = assertThrows(JsonTypeException.class, () -> {
			throw new JsonTypeException("Type mismatch");
		});
		assertEquals("Type mismatch", thrownException.getMessage());
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new JsonTypeException("Another type error");
		});
		assertInstanceOf(JsonTypeException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new JsonTypeException("Yet another type error");
		});
		assertInstanceOf(JsonTypeException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Expected a json primitive, but found: json object";
		JsonTypeException exception = new JsonTypeException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		ClassCastException originalCause = new ClassCastException("String cannot be cast to Number");
		JsonTypeException exception = new JsonTypeException("Type casting failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new JsonTypeException("Test type error");
		} catch (JsonTypeException e) {
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
		
		JsonTypeException topException = new JsonTypeException("Top level type error", intermediateCause);
		
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
		JsonTypeException mainException = new JsonTypeException("Main type error");
		JsonTypeException suppressedException1 = new JsonTypeException("Suppressed type error 1");
		JsonTypeException suppressedException2 = new JsonTypeException("Suppressed type error 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldTypeErrorScenarios() {
		JsonTypeException objectExpectedException = new JsonTypeException("Expected a json object, but found: json array");
		assertTrue(objectExpectedException.getMessage().contains("Expected a json object"));
		assertTrue(objectExpectedException.getMessage().contains("json array"));
		
		JsonTypeException primitiveExpectedException = new JsonTypeException("Expected a json primitive, but found: json null");
		assertTrue(primitiveExpectedException.getMessage().contains("Expected a json primitive"));
		assertTrue(primitiveExpectedException.getMessage().contains("json null"));
		
		JsonTypeException arrayExpectedException = new JsonTypeException("Expected a json array, but found: json primitive");
		assertTrue(arrayExpectedException.getMessage().contains("Expected a json array"));
		assertTrue(arrayExpectedException.getMessage().contains("json primitive"));
		
		NumberFormatException numberError = new NumberFormatException("For input string: \"not_a_number\"");
		JsonTypeException numberConversionException = new JsonTypeException("Failed to convert string to number", numberError);
		assertTrue(numberConversionException.getMessage().contains("Failed to convert"));
		assertSame(numberError, numberConversionException.getCause());
		
		JsonTypeException booleanConversionException = new JsonTypeException("Cannot convert 'maybe' to boolean value");
		assertTrue(booleanConversionException.getMessage().contains("Cannot convert"));
		assertTrue(booleanConversionException.getMessage().contains("boolean"));
	}
	
	@Test
	void messageFormatsForCommonTypes() {
		String[] commonMessages = {
			"Expected a json object, but found: json array",
			"Expected a json array, but found: json primitive",
			"Expected a json primitive, but found: json null",
			"Expected a json null, but found: json object",
			"Cannot convert json array to string",
			"Cannot convert json object to number",
			"Invalid boolean value: expected true or false",
			"Type mismatch: expected Number but got String"
		};
		
		for (String message : commonMessages) {
			JsonTypeException exception = new JsonTypeException(message);
			assertEquals(message, exception.getMessage());
			assertNotNull(exception.getMessage());
			assertFalse(exception.getMessage().isEmpty());
		}
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new JsonTypeException());
		assertDoesNotThrow(() -> new JsonTypeException((String) null));
		assertDoesNotThrow(() -> new JsonTypeException((Throwable) null));
		assertDoesNotThrow(() -> new JsonTypeException(null, null));
		
		JsonTypeException nullMessageException = new JsonTypeException((String) null);
		assertNull(nullMessageException.getMessage());
		
		JsonTypeException nullCauseException = new JsonTypeException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		JsonTypeException bothNullException = new JsonTypeException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void exceptionUsageInTypeCasting() {
		assertThrows(JsonTypeException.class, () -> {
			throw new JsonTypeException("Cannot convert string 'hello' to integer");
		});
		
		assertThrows(JsonTypeException.class, () -> {
			throw new JsonTypeException("Expected a json array, but found: json object");
		});
		
		assertThrows(JsonTypeException.class, () -> {
			throw new JsonTypeException("Cannot interpret number as boolean value");
		});
	}
}
