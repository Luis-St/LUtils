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
 * Test class for {@link TomlSyntaxException}.<br>
 *
 * @author Luis-St
 */
class TomlSyntaxExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		TomlSyntaxException exception = new TomlSyntaxException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Invalid TOML syntax";
		TomlSyntaxException exception = new TomlSyntaxException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		TomlSyntaxException nullMessageException = new TomlSyntaxException((String) null);
		assertNull(nullMessageException.getMessage());
		
		TomlSyntaxException emptyMessageException = new TomlSyntaxException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "TOML parsing failed";
		IOException cause = new IOException("Stream error");
		TomlSyntaxException exception = new TomlSyntaxException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		TomlSyntaxException nullMessageException = new TomlSyntaxException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		TomlSyntaxException nullCauseException = new TomlSyntaxException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		TomlSyntaxException bothNullException = new TomlSyntaxException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		IOException cause = new IOException("Underlying stream error");
		TomlSyntaxException exception = new TomlSyntaxException(cause);
		
		assertTrue(exception.getMessage().contains("IOException"));
		assertTrue(exception.getMessage().contains("Underlying stream error"));
		assertSame(cause, exception.getCause());
		
		TomlSyntaxException nullCauseException = new TomlSyntaxException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NumberFormatException numberCause = new NumberFormatException("Invalid number format");
		TomlSyntaxException numberException = new TomlSyntaxException("Number parsing error", numberCause);
		assertSame(numberCause, numberException.getCause());
		
		IllegalArgumentException argCause = new IllegalArgumentException("Invalid argument");
		TomlSyntaxException argException = new TomlSyntaxException("Argument error", argCause);
		assertSame(argCause, argException.getCause());
		
		RuntimeException runtimeCause = new RuntimeException("Runtime error");
		TomlSyntaxException runtimeException = new TomlSyntaxException(runtimeCause);
		assertSame(runtimeCause, runtimeException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		TomlSyntaxException exception = new TomlSyntaxException();
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		TomlSyntaxException thrownException = assertThrows(TomlSyntaxException.class, () -> {
			throw new TomlSyntaxException("Test exception");
		});
		assertEquals("Test exception", thrownException.getMessage());
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new TomlSyntaxException("Another test");
		});
		assertInstanceOf(TomlSyntaxException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new TomlSyntaxException("Yet another test");
		});
		assertInstanceOf(TomlSyntaxException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Unexpected character ']' at line 15";
		TomlSyntaxException exception = new TomlSyntaxException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		IOException originalCause = new IOException("File not found");
		TomlSyntaxException exception = new TomlSyntaxException("TOML read error", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new TomlSyntaxException("Test syntax error");
		} catch (TomlSyntaxException e) {
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
		
		TomlSyntaxException topException = new TomlSyntaxException("Top level error", intermediateCause);
		
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
		TomlSyntaxException mainException = new TomlSyntaxException("Main error");
		TomlSyntaxException suppressedException1 = new TomlSyntaxException("Suppressed 1");
		TomlSyntaxException suppressedException2 = new TomlSyntaxException("Suppressed 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldScenarios() {
		TomlSyntaxException eofException = new TomlSyntaxException("Unexpected end of TOML input at line 42");
		assertTrue(eofException.getMessage().contains("Unexpected end"));
		assertTrue(eofException.getMessage().contains("line 42"));
		
		TomlSyntaxException invalidCharException = new TomlSyntaxException("Invalid character '\\' at line 15");
		assertTrue(invalidCharException.getMessage().contains("Invalid character"));
		assertTrue(invalidCharException.getMessage().contains("line 15"));
		
		TomlSyntaxException missingBracketException = new TomlSyntaxException("Missing closing bracket ']' for table header");
		assertTrue(missingBracketException.getMessage().contains("Missing closing bracket"));
		
		TomlSyntaxException invalidKeyException = new TomlSyntaxException("Invalid bare key: cannot contain '='");
		assertTrue(invalidKeyException.getMessage().contains("Invalid bare key"));
		
		TomlSyntaxException invalidDateException = new TomlSyntaxException("Invalid datetime format: expected RFC 3339");
		assertTrue(invalidDateException.getMessage().contains("Invalid datetime"));
		assertTrue(invalidDateException.getMessage().contains("RFC 3339"));
		
		IOException ioError = new IOException("Connection reset");
		TomlSyntaxException ioBasedException = new TomlSyntaxException("Failed to read TOML from stream", ioError);
		assertTrue(ioBasedException.getMessage().contains("Failed to read TOML"));
		assertSame(ioError, ioBasedException.getCause());
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new TomlSyntaxException());
		assertDoesNotThrow(() -> new TomlSyntaxException((String) null));
		assertDoesNotThrow(() -> new TomlSyntaxException((Throwable) null));
		assertDoesNotThrow(() -> new TomlSyntaxException(null, null));
		
		TomlSyntaxException nullMessageException = new TomlSyntaxException((String) null);
		assertNull(nullMessageException.getMessage());
		
		TomlSyntaxException nullCauseException = new TomlSyntaxException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		TomlSyntaxException bothNullException = new TomlSyntaxException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
}
