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

package net.luis.utils.io.data.ini.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IniTypeException}.<br>
 *
 * @author Luis-St
 */
class IniTypeExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		IniTypeException exception = new IniTypeException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected ini value but found ini section";
		IniTypeException exception = new IniTypeException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		IniTypeException nullMessageException = new IniTypeException((String) null);
		assertNull(nullMessageException.getMessage());
		
		IniTypeException emptyMessageException = new IniTypeException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Type conversion failed";
		ClassCastException cause = new ClassCastException("Cannot cast String to Integer");
		IniTypeException exception = new IniTypeException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		IniTypeException nullMessageException = new IniTypeException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		IniTypeException nullCauseException = new IniTypeException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		IniTypeException bothNullException = new IniTypeException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		ClassCastException cause = new ClassCastException("Type mismatch error");
		IniTypeException exception = new IniTypeException(cause);
		
		assertTrue(exception.getMessage().contains("ClassCastException"));
		assertTrue(exception.getMessage().contains("Type mismatch error"));
		assertSame(cause, exception.getCause());
		
		IniTypeException nullCauseException = new IniTypeException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NumberFormatException numberCause = new NumberFormatException("Not a valid number");
		IniTypeException numberException = new IniTypeException("Number conversion error", numberCause);
		assertSame(numberCause, numberException.getCause());
		
		IllegalArgumentException argCause = new IllegalArgumentException("Invalid type argument");
		IniTypeException argException = new IniTypeException("Type argument error", argCause);
		assertSame(argCause, argException.getCause());
		
		IOException ioCause = new IOException("Stream read error during type check");
		IniTypeException ioException = new IniTypeException(ioCause);
		assertSame(ioCause, ioException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		IniTypeException exception = new IniTypeException();
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		IniTypeException thrownException = assertThrows(IniTypeException.class, () -> {
			throw new IniTypeException("Type mismatch");
		});
		assertEquals("Type mismatch", thrownException.getMessage());
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new IniTypeException("Another type error");
		});
		assertInstanceOf(IniTypeException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new IniTypeException("Yet another type error");
		});
		assertInstanceOf(IniTypeException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Expected an ini value, but found: ini section";
		IniTypeException exception = new IniTypeException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		ClassCastException originalCause = new ClassCastException("String cannot be cast to Number");
		IniTypeException exception = new IniTypeException("Type casting failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new IniTypeException("Test type error");
		} catch (IniTypeException e) {
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
		
		IniTypeException topException = new IniTypeException("Top level type error", intermediateCause);
		
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
		IniTypeException mainException = new IniTypeException("Main type error");
		IniTypeException suppressedException1 = new IniTypeException("Suppressed type error 1");
		IniTypeException suppressedException2 = new IniTypeException("Suppressed type error 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldTypeErrorScenarios() {
		IniTypeException valueExpectedException = new IniTypeException("Expected an ini value, but found: ini section");
		assertTrue(valueExpectedException.getMessage().contains("Expected an ini value"));
		assertTrue(valueExpectedException.getMessage().contains("ini section"));
		
		IniTypeException sectionExpectedException = new IniTypeException("Expected an ini section, but found: ini value");
		assertTrue(sectionExpectedException.getMessage().contains("Expected an ini section"));
		assertTrue(sectionExpectedException.getMessage().contains("ini value"));
		
		IniTypeException documentExpectedException = new IniTypeException("Expected an ini document, but found: ini null");
		assertTrue(documentExpectedException.getMessage().contains("Expected an ini document"));
		assertTrue(documentExpectedException.getMessage().contains("ini null"));
		
		NumberFormatException numberError = new NumberFormatException("For input string: \"not_a_number\"");
		IniTypeException numberConversionException = new IniTypeException("Failed to convert string to number", numberError);
		assertTrue(numberConversionException.getMessage().contains("Failed to convert"));
		assertSame(numberError, numberConversionException.getCause());
		
		IniTypeException booleanConversionException = new IniTypeException("Cannot convert 'maybe' to boolean value");
		assertTrue(booleanConversionException.getMessage().contains("Cannot convert"));
		assertTrue(booleanConversionException.getMessage().contains("boolean"));
	}
	
	@Test
	void messageFormatsForCommonTypes() {
		String[] commonMessages = {
			"Expected an ini value, but found: ini section",
			"Expected an ini section, but found: ini value",
			"Expected an ini document, but found: ini null",
			"Expected an ini null, but found: ini value",
			"Cannot convert ini section to string",
			"Cannot convert ini value to number",
			"Invalid boolean value: expected true or false",
			"Type mismatch: expected Number but got String"
		};
		
		for (String message : commonMessages) {
			IniTypeException exception = new IniTypeException(message);
			assertEquals(message, exception.getMessage());
			assertNotNull(exception.getMessage());
			assertFalse(exception.getMessage().isEmpty());
		}
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new IniTypeException());
		assertDoesNotThrow(() -> new IniTypeException((String) null));
		assertDoesNotThrow(() -> new IniTypeException((Throwable) null));
		assertDoesNotThrow(() -> new IniTypeException(null, null));
		
		IniTypeException nullMessageException = new IniTypeException((String) null);
		assertNull(nullMessageException.getMessage());
		
		IniTypeException nullCauseException = new IniTypeException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		IniTypeException bothNullException = new IniTypeException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void exceptionUsageInTypeCasting() {
		assertThrows(IniTypeException.class, () -> {
			throw new IniTypeException("Cannot convert string 'hello' to integer");
		});
		
		assertThrows(IniTypeException.class, () -> {
			throw new IniTypeException("Expected an ini section, but found: ini value");
		});
		
		assertThrows(IniTypeException.class, () -> {
			throw new IniTypeException("Cannot interpret number as boolean value");
		});
	}
}
