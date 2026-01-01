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
 * Test class for {@link IniSyntaxException}.<br>
 *
 * @author Luis-St
 */
class IniSyntaxExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		IniSyntaxException exception = new IniSyntaxException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Invalid INI syntax";
		IniSyntaxException exception = new IniSyntaxException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		IniSyntaxException nullMessageException = new IniSyntaxException((String) null);
		assertNull(nullMessageException.getMessage());
		
		IniSyntaxException emptyMessageException = new IniSyntaxException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "INI parsing failed";
		IOException cause = new IOException("Stream error");
		IniSyntaxException exception = new IniSyntaxException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		IniSyntaxException nullMessageException = new IniSyntaxException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		IniSyntaxException nullCauseException = new IniSyntaxException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		IniSyntaxException bothNullException = new IniSyntaxException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		IOException cause = new IOException("Underlying stream error");
		IniSyntaxException exception = new IniSyntaxException(cause);
		
		assertTrue(exception.getMessage().contains("IOException"));
		assertTrue(exception.getMessage().contains("Underlying stream error"));
		assertSame(cause, exception.getCause());
		
		IniSyntaxException nullCauseException = new IniSyntaxException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NumberFormatException numberCause = new NumberFormatException("Invalid number format");
		IniSyntaxException numberException = new IniSyntaxException("Number parsing error", numberCause);
		assertSame(numberCause, numberException.getCause());
		
		IllegalArgumentException argCause = new IllegalArgumentException("Invalid argument");
		IniSyntaxException argException = new IniSyntaxException("Argument error", argCause);
		assertSame(argCause, argException.getCause());
		
		RuntimeException runtimeCause = new RuntimeException("Runtime error");
		IniSyntaxException runtimeException = new IniSyntaxException(runtimeCause);
		assertSame(runtimeCause, runtimeException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		IniSyntaxException exception = new IniSyntaxException();
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		IniSyntaxException thrownException = assertThrows(IniSyntaxException.class, () -> {
			throw new IniSyntaxException("Test exception");
		});
		assertEquals("Test exception", thrownException.getMessage());
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new IniSyntaxException("Another test");
		});
		assertInstanceOf(IniSyntaxException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new IniSyntaxException("Yet another test");
		});
		assertInstanceOf(IniSyntaxException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Unexpected character ']' at line 15";
		IniSyntaxException exception = new IniSyntaxException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		IOException originalCause = new IOException("File not found");
		IniSyntaxException exception = new IniSyntaxException("INI read error", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new IniSyntaxException("Test syntax error");
		} catch (IniSyntaxException e) {
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
		
		IniSyntaxException topException = new IniSyntaxException("Top level error", intermediateCause);
		
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
		IniSyntaxException mainException = new IniSyntaxException("Main error");
		IniSyntaxException suppressedException1 = new IniSyntaxException("Suppressed 1");
		IniSyntaxException suppressedException2 = new IniSyntaxException("Suppressed 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldScenarios() {
		IniSyntaxException eofException = new IniSyntaxException("Unexpected end of INI input at line 42");
		assertTrue(eofException.getMessage().contains("Unexpected end"));
		assertTrue(eofException.getMessage().contains("line 42"));
		
		IniSyntaxException invalidCharException = new IniSyntaxException("Invalid character '\\' at line 15");
		assertTrue(invalidCharException.getMessage().contains("Invalid character"));
		assertTrue(invalidCharException.getMessage().contains("line 15"));
		
		IniSyntaxException missingSectionException = new IniSyntaxException("Missing closing bracket ']' for section header");
		assertTrue(missingSectionException.getMessage().contains("Missing closing bracket"));
		
		IniSyntaxException invalidKeyException = new IniSyntaxException("Invalid key format: key cannot start with '='");
		assertTrue(invalidKeyException.getMessage().contains("Invalid key format"));
		
		IOException ioError = new IOException("Connection reset");
		IniSyntaxException ioBasedException = new IniSyntaxException("Failed to read INI from stream", ioError);
		assertTrue(ioBasedException.getMessage().contains("Failed to read INI"));
		assertSame(ioError, ioBasedException.getCause());
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new IniSyntaxException());
		assertDoesNotThrow(() -> new IniSyntaxException((String) null));
		assertDoesNotThrow(() -> new IniSyntaxException((Throwable) null));
		assertDoesNotThrow(() -> new IniSyntaxException(null, null));
		
		IniSyntaxException nullMessageException = new IniSyntaxException((String) null);
		assertNull(nullMessageException.getMessage());
		
		IniSyntaxException nullCauseException = new IniSyntaxException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		IniSyntaxException bothNullException = new IniSyntaxException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
}
