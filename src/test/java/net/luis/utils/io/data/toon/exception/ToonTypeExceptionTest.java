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

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ToonTypeException}.<br>
 *
 * @author Luis-St
 */
class ToonTypeExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		ToonTypeException exception = new ToonTypeException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected toon value but found toon table";
		ToonTypeException exception = new ToonTypeException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		ToonTypeException nullMessageException = new ToonTypeException((String) null);
		assertNull(nullMessageException.getMessage());
		
		ToonTypeException emptyMessageException = new ToonTypeException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Type conversion failed";
		ClassCastException cause = new ClassCastException("Cannot cast String to Integer");
		ToonTypeException exception = new ToonTypeException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		ToonTypeException nullMessageException = new ToonTypeException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		ToonTypeException nullCauseException = new ToonTypeException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		ToonTypeException bothNullException = new ToonTypeException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		ClassCastException cause = new ClassCastException("Type mismatch error");
		ToonTypeException exception = new ToonTypeException(cause);
		
		assertTrue(exception.getMessage().contains("ClassCastException"));
		assertTrue(exception.getMessage().contains("Type mismatch error"));
		assertSame(cause, exception.getCause());
		
		ToonTypeException nullCauseException = new ToonTypeException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NumberFormatException numberCause = new NumberFormatException("Not a valid number");
		ToonTypeException numberException = new ToonTypeException("Number conversion error", numberCause);
		assertSame(numberCause, numberException.getCause());
		
		IllegalArgumentException argCause = new IllegalArgumentException("Invalid type argument");
		ToonTypeException argException = new ToonTypeException("Type argument error", argCause);
		assertSame(argCause, argException.getCause());
		
		IOException ioCause = new IOException("Stream read error during type check");
		ToonTypeException ioException = new ToonTypeException(ioCause);
		assertSame(ioCause, ioException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		ToonTypeException exception = new ToonTypeException();
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		ToonTypeException thrownException = assertThrows(ToonTypeException.class, () -> {
			throw new ToonTypeException("Type mismatch");
		});
		assertEquals("Type mismatch", thrownException.getMessage());
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new ToonTypeException("Another type error");
		});
		assertInstanceOf(ToonTypeException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new ToonTypeException("Yet another type error");
		});
		assertInstanceOf(ToonTypeException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Expected a toon value, but found: toon table";
		ToonTypeException exception = new ToonTypeException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		ClassCastException originalCause = new ClassCastException("String cannot be cast to Number");
		ToonTypeException exception = new ToonTypeException("Type casting failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new ToonTypeException("Test type error");
		} catch (ToonTypeException e) {
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
		
		ToonTypeException topException = new ToonTypeException("Top level type error", intermediateCause);
		
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
		ToonTypeException mainException = new ToonTypeException("Main type error");
		ToonTypeException suppressedException1 = new ToonTypeException("Suppressed type error 1");
		ToonTypeException suppressedException2 = new ToonTypeException("Suppressed type error 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldTypeErrorScenarios() {
		ToonTypeException valueExpectedException = new ToonTypeException("Expected a toon value, but found: toon table");
		assertTrue(valueExpectedException.getMessage().contains("Expected a toon value"));
		assertTrue(valueExpectedException.getMessage().contains("toon table"));
		
		ToonTypeException tableExpectedException = new ToonTypeException("Expected a toon table, but found: toon value");
		assertTrue(tableExpectedException.getMessage().contains("Expected a toon table"));
		assertTrue(tableExpectedException.getMessage().contains("toon value"));
		
		ToonTypeException arrayExpectedException = new ToonTypeException("Expected a toon array, but found: toon null");
		assertTrue(arrayExpectedException.getMessage().contains("Expected a toon array"));
		assertTrue(arrayExpectedException.getMessage().contains("toon null"));
		
		ToonTypeException dateTimeException = new ToonTypeException("Expected LocalDateTime, but found: String");
		assertTrue(dateTimeException.getMessage().contains("Expected LocalDateTime"));
		assertTrue(dateTimeException.getMessage().contains("String"));
		
		NumberFormatException numberError = new NumberFormatException("For input string: \"not_a_number\"");
		ToonTypeException numberConversionException = new ToonTypeException("Failed to convert string to number", numberError);
		assertTrue(numberConversionException.getMessage().contains("Failed to convert"));
		assertSame(numberError, numberConversionException.getCause());
		
		ToonTypeException booleanConversionException = new ToonTypeException("Cannot convert 'maybe' to boolean value");
		assertTrue(booleanConversionException.getMessage().contains("Cannot convert"));
		assertTrue(booleanConversionException.getMessage().contains("boolean"));
	}
	
	@Test
	void messageFormatsForCommonTypes() {
		String[] commonMessages = {
			"Expected a toon value, but found: toon table",
			"Expected a toon table, but found: toon value",
			"Expected a toon array, but found: toon null",
			"Expected a toon null, but found: toon value",
			"Cannot convert toon table to string",
			"Cannot convert toon value to number",
			"Invalid boolean value: expected true or false",
			"Type mismatch: expected Number but got String",
			"Expected LocalDate, but found: OffsetDateTime",
			"Cannot convert toon array to toon table"
		};
		
		for (String message : commonMessages) {
			ToonTypeException exception = new ToonTypeException(message);
			assertEquals(message, exception.getMessage());
			assertNotNull(exception.getMessage());
			assertFalse(exception.getMessage().isEmpty());
		}
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new ToonTypeException());
		assertDoesNotThrow(() -> new ToonTypeException((String) null));
		assertDoesNotThrow(() -> new ToonTypeException((Throwable) null));
		assertDoesNotThrow(() -> new ToonTypeException(null, null));
		
		ToonTypeException nullMessageException = new ToonTypeException((String) null);
		assertNull(nullMessageException.getMessage());
		
		ToonTypeException nullCauseException = new ToonTypeException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		ToonTypeException bothNullException = new ToonTypeException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void exceptionUsageInTypeCasting() {
		assertThrows(ToonTypeException.class, () -> {
			throw new ToonTypeException("Cannot convert string 'hello' to integer");
		});
		
		assertThrows(ToonTypeException.class, () -> {
			throw new ToonTypeException("Expected a toon table, but found: toon value");
		});
		
		assertThrows(ToonTypeException.class, () -> {
			throw new ToonTypeException("Cannot interpret number as boolean value");
		});
	}
}
