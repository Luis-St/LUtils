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

package net.luis.utils.io.data.toml.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TomlTypeException}.<br>
 *
 * @author Luis-St
 */
class TomlTypeExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		TomlTypeException exception = new TomlTypeException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected toml value but found toml table";
		TomlTypeException exception = new TomlTypeException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		TomlTypeException nullMessageException = new TomlTypeException((String) null);
		assertNull(nullMessageException.getMessage());
		
		TomlTypeException emptyMessageException = new TomlTypeException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Type conversion failed";
		ClassCastException cause = new ClassCastException("Cannot cast String to Integer");
		TomlTypeException exception = new TomlTypeException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		TomlTypeException nullMessageException = new TomlTypeException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		TomlTypeException nullCauseException = new TomlTypeException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		TomlTypeException bothNullException = new TomlTypeException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		ClassCastException cause = new ClassCastException("Type mismatch error");
		TomlTypeException exception = new TomlTypeException(cause);
		
		assertTrue(exception.getMessage().contains("ClassCastException"));
		assertTrue(exception.getMessage().contains("Type mismatch error"));
		assertSame(cause, exception.getCause());
		
		TomlTypeException nullCauseException = new TomlTypeException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NumberFormatException numberCause = new NumberFormatException("Not a valid number");
		TomlTypeException numberException = new TomlTypeException("Number conversion error", numberCause);
		assertSame(numberCause, numberException.getCause());
		
		IllegalArgumentException argCause = new IllegalArgumentException("Invalid type argument");
		TomlTypeException argException = new TomlTypeException("Type argument error", argCause);
		assertSame(argCause, argException.getCause());
		
		IOException ioCause = new IOException("Stream read error during type check");
		TomlTypeException ioException = new TomlTypeException(ioCause);
		assertSame(ioCause, ioException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		TomlTypeException exception = new TomlTypeException();
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		TomlTypeException thrownException = assertThrows(TomlTypeException.class, () -> {
			throw new TomlTypeException("Type mismatch");
		});
		assertEquals("Type mismatch", thrownException.getMessage());
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new TomlTypeException("Another type error");
		});
		assertInstanceOf(TomlTypeException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new TomlTypeException("Yet another type error");
		});
		assertInstanceOf(TomlTypeException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Expected a toml value, but found: toml table";
		TomlTypeException exception = new TomlTypeException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		ClassCastException originalCause = new ClassCastException("String cannot be cast to Number");
		TomlTypeException exception = new TomlTypeException("Type casting failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new TomlTypeException("Test type error");
		} catch (TomlTypeException e) {
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
		
		TomlTypeException topException = new TomlTypeException("Top level type error", intermediateCause);
		
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
		TomlTypeException mainException = new TomlTypeException("Main type error");
		TomlTypeException suppressedException1 = new TomlTypeException("Suppressed type error 1");
		TomlTypeException suppressedException2 = new TomlTypeException("Suppressed type error 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldTypeErrorScenarios() {
		TomlTypeException valueExpectedException = new TomlTypeException("Expected a toml value, but found: toml table");
		assertTrue(valueExpectedException.getMessage().contains("Expected a toml value"));
		assertTrue(valueExpectedException.getMessage().contains("toml table"));
		
		TomlTypeException tableExpectedException = new TomlTypeException("Expected a toml table, but found: toml value");
		assertTrue(tableExpectedException.getMessage().contains("Expected a toml table"));
		assertTrue(tableExpectedException.getMessage().contains("toml value"));
		
		TomlTypeException arrayExpectedException = new TomlTypeException("Expected a toml array, but found: toml null");
		assertTrue(arrayExpectedException.getMessage().contains("Expected a toml array"));
		assertTrue(arrayExpectedException.getMessage().contains("toml null"));
		
		TomlTypeException dateTimeException = new TomlTypeException("Expected LocalDateTime, but found: String");
		assertTrue(dateTimeException.getMessage().contains("Expected LocalDateTime"));
		assertTrue(dateTimeException.getMessage().contains("String"));
		
		NumberFormatException numberError = new NumberFormatException("For input string: \"not_a_number\"");
		TomlTypeException numberConversionException = new TomlTypeException("Failed to convert string to number", numberError);
		assertTrue(numberConversionException.getMessage().contains("Failed to convert"));
		assertSame(numberError, numberConversionException.getCause());
		
		TomlTypeException booleanConversionException = new TomlTypeException("Cannot convert 'maybe' to boolean value");
		assertTrue(booleanConversionException.getMessage().contains("Cannot convert"));
		assertTrue(booleanConversionException.getMessage().contains("boolean"));
	}
	
	@Test
	void messageFormatsForCommonTypes() {
		String[] commonMessages = {
			"Expected a toml value, but found: toml table",
			"Expected a toml table, but found: toml value",
			"Expected a toml array, but found: toml null",
			"Expected a toml null, but found: toml value",
			"Cannot convert toml table to string",
			"Cannot convert toml value to number",
			"Invalid boolean value: expected true or false",
			"Type mismatch: expected Number but got String",
			"Expected LocalDate, but found: OffsetDateTime",
			"Cannot convert toml array to toml table"
		};
		
		for (String message : commonMessages) {
			TomlTypeException exception = new TomlTypeException(message);
			assertEquals(message, exception.getMessage());
			assertNotNull(exception.getMessage());
			assertFalse(exception.getMessage().isEmpty());
		}
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new TomlTypeException());
		assertDoesNotThrow(() -> new TomlTypeException((String) null));
		assertDoesNotThrow(() -> new TomlTypeException((Throwable) null));
		assertDoesNotThrow(() -> new TomlTypeException(null, null));
		
		TomlTypeException nullMessageException = new TomlTypeException((String) null);
		assertNull(nullMessageException.getMessage());
		
		TomlTypeException nullCauseException = new TomlTypeException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		TomlTypeException bothNullException = new TomlTypeException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void exceptionUsageInTypeCasting() {
		assertThrows(TomlTypeException.class, () -> {
			throw new TomlTypeException("Cannot convert string 'hello' to integer");
		});
		
		assertThrows(TomlTypeException.class, () -> {
			throw new TomlTypeException("Expected a toml table, but found: toml value");
		});
		
		assertThrows(TomlTypeException.class, () -> {
			throw new TomlTypeException("Cannot interpret number as boolean value");
		});
	}
}
