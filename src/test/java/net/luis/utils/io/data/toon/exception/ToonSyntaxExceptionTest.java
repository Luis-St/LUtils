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
 * Test class for {@link ToonSyntaxException}.<br>
 *
 * @author Luis-St
 */
class ToonSyntaxExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		ToonSyntaxException exception = new ToonSyntaxException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Invalid TOON syntax";
		ToonSyntaxException exception = new ToonSyntaxException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		ToonSyntaxException nullMessageException = new ToonSyntaxException((String) null);
		assertNull(nullMessageException.getMessage());
		
		ToonSyntaxException emptyMessageException = new ToonSyntaxException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "TOON parsing failed";
		IOException cause = new IOException("Stream error");
		ToonSyntaxException exception = new ToonSyntaxException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		ToonSyntaxException nullMessageException = new ToonSyntaxException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		ToonSyntaxException nullCauseException = new ToonSyntaxException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		ToonSyntaxException bothNullException = new ToonSyntaxException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		IOException cause = new IOException("Underlying stream error");
		ToonSyntaxException exception = new ToonSyntaxException(cause);
		
		assertTrue(exception.getMessage().contains("IOException"));
		assertTrue(exception.getMessage().contains("Underlying stream error"));
		assertSame(cause, exception.getCause());
		
		ToonSyntaxException nullCauseException = new ToonSyntaxException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NumberFormatException numberCause = new NumberFormatException("Invalid number format");
		ToonSyntaxException numberException = new ToonSyntaxException("Number parsing error", numberCause);
		assertSame(numberCause, numberException.getCause());
		
		IllegalArgumentException argCause = new IllegalArgumentException("Invalid argument");
		ToonSyntaxException argException = new ToonSyntaxException("Argument error", argCause);
		assertSame(argCause, argException.getCause());
		
		RuntimeException runtimeCause = new RuntimeException("Runtime error");
		ToonSyntaxException runtimeException = new ToonSyntaxException(runtimeCause);
		assertSame(runtimeCause, runtimeException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		ToonSyntaxException exception = new ToonSyntaxException();
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		ToonSyntaxException thrownException = assertThrows(ToonSyntaxException.class, () -> {
			throw new ToonSyntaxException("Test exception");
		});
		assertEquals("Test exception", thrownException.getMessage());
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new ToonSyntaxException("Another test");
		});
		assertInstanceOf(ToonSyntaxException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new ToonSyntaxException("Yet another test");
		});
		assertInstanceOf(ToonSyntaxException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Unexpected character ']' at line 15";
		ToonSyntaxException exception = new ToonSyntaxException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		IOException originalCause = new IOException("File not found");
		ToonSyntaxException exception = new ToonSyntaxException("TOON read error", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new ToonSyntaxException("Test syntax error");
		} catch (ToonSyntaxException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			
			StackTraceElement topElement = e.getStackTrace()[0];
			assertEquals("stackTracePreservation", topElement.getMethodName());
		}
	}
	
	@Test
	void causeChaining() {
		IOException rootCause = new IOException("Root cause");
		NumberFormatException intermediateCause = new NumberFormatException("Intermediate cause");
		intermediateCause.initCause(rootCause);
		
		ToonSyntaxException topException = new ToonSyntaxException("Top level error", intermediateCause);
		
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
		ToonSyntaxException mainException = new ToonSyntaxException("Main error");
		ToonSyntaxException suppressedException1 = new ToonSyntaxException("Suppressed 1");
		ToonSyntaxException suppressedException2 = new ToonSyntaxException("Suppressed 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldScenarios() {
		ToonSyntaxException eofException = new ToonSyntaxException("Unexpected end of TOON input at line 42");
		assertTrue(eofException.getMessage().contains("Unexpected end"));
		assertTrue(eofException.getMessage().contains("line 42"));
		
		ToonSyntaxException invalidCharException = new ToonSyntaxException("Invalid character '\\' at line 15");
		assertTrue(invalidCharException.getMessage().contains("Invalid character"));
		assertTrue(invalidCharException.getMessage().contains("line 15"));
		
		ToonSyntaxException missingBracketException = new ToonSyntaxException("Missing closing bracket ']' for table header");
		assertTrue(missingBracketException.getMessage().contains("Missing closing bracket"));
		
		ToonSyntaxException invalidKeyException = new ToonSyntaxException("Invalid bare key: cannot contain '='");
		assertTrue(invalidKeyException.getMessage().contains("Invalid bare key"));
		
		ToonSyntaxException invalidDateException = new ToonSyntaxException("Invalid datetime format: expected RFC 3339");
		assertTrue(invalidDateException.getMessage().contains("Invalid datetime"));
		assertTrue(invalidDateException.getMessage().contains("RFC 3339"));
		
		IOException ioError = new IOException("Connection reset");
		ToonSyntaxException ioBasedException = new ToonSyntaxException("Failed to read TOON from stream", ioError);
		assertTrue(ioBasedException.getMessage().contains("Failed to read TOON"));
		assertSame(ioError, ioBasedException.getCause());
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new ToonSyntaxException());
		assertDoesNotThrow(() -> new ToonSyntaxException((String) null));
		assertDoesNotThrow(() -> new ToonSyntaxException((Throwable) null));
		assertDoesNotThrow(() -> new ToonSyntaxException(null, null));
		
		ToonSyntaxException nullMessageException = new ToonSyntaxException((String) null);
		assertNull(nullMessageException.getMessage());
		
		ToonSyntaxException nullCauseException = new ToonSyntaxException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		ToonSyntaxException bothNullException = new ToonSyntaxException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
}
