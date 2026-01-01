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
 * Test class for {@link JsonSyntaxException}.<br>
 *
 * @author Luis-St
 */
class JsonSyntaxExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		JsonSyntaxException exception = new JsonSyntaxException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Invalid JSON syntax";
		JsonSyntaxException exception = new JsonSyntaxException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		JsonSyntaxException nullMessageException = new JsonSyntaxException((String) null);
		assertNull(nullMessageException.getMessage());
		
		JsonSyntaxException emptyMessageException = new JsonSyntaxException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "JSON parsing failed";
		IOException cause = new IOException("Stream error");
		JsonSyntaxException exception = new JsonSyntaxException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		JsonSyntaxException nullMessageException = new JsonSyntaxException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		JsonSyntaxException nullCauseException = new JsonSyntaxException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		JsonSyntaxException bothNullException = new JsonSyntaxException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		IOException cause = new IOException("Underlying stream error");
		JsonSyntaxException exception = new JsonSyntaxException(cause);
		
		assertTrue(exception.getMessage().contains("IOException"));
		assertTrue(exception.getMessage().contains("Underlying stream error"));
		assertSame(cause, exception.getCause());
		
		JsonSyntaxException nullCauseException = new JsonSyntaxException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NumberFormatException numberCause = new NumberFormatException("Invalid number format");
		JsonSyntaxException numberException = new JsonSyntaxException("Number parsing error", numberCause);
		assertSame(numberCause, numberException.getCause());
		
		IllegalArgumentException argCause = new IllegalArgumentException("Invalid argument");
		JsonSyntaxException argException = new JsonSyntaxException("Argument error", argCause);
		assertSame(argCause, argException.getCause());
		
		RuntimeException runtimeCause = new RuntimeException("Runtime error");
		JsonSyntaxException runtimeException = new JsonSyntaxException(runtimeCause);
		assertSame(runtimeCause, runtimeException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		JsonSyntaxException exception = new JsonSyntaxException();
		
		assertInstanceOf(RuntimeException.class, exception);
		
		assertInstanceOf(Exception.class, exception);
		
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		JsonSyntaxException thrownException = assertThrows(JsonSyntaxException.class, () -> {
			throw new JsonSyntaxException("Test exception");
		});
		assertEquals("Test exception", thrownException.getMessage());
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new JsonSyntaxException("Another test");
		});
		assertInstanceOf(JsonSyntaxException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new JsonSyntaxException("Yet another test");
		});
		assertInstanceOf(JsonSyntaxException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Unexpected character '}' at position 15";
		JsonSyntaxException exception = new JsonSyntaxException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		IOException originalCause = new IOException("File not found");
		JsonSyntaxException exception = new JsonSyntaxException("JSON read error", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new JsonSyntaxException("Test syntax error");
		} catch (JsonSyntaxException e) {
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
		
		JsonSyntaxException topException = new JsonSyntaxException("Top level error", intermediateCause);
		
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
		JsonSyntaxException mainException = new JsonSyntaxException("Main error");
		JsonSyntaxException suppressedException1 = new JsonSyntaxException("Suppressed 1");
		JsonSyntaxException suppressedException2 = new JsonSyntaxException("Suppressed 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldScenarios() {
		JsonSyntaxException eofException = new JsonSyntaxException("Unexpected end of JSON input at position 42");
		assertTrue(eofException.getMessage().contains("Unexpected end"));
		assertTrue(eofException.getMessage().contains("position 42"));
		
		JsonSyntaxException invalidCharException = new JsonSyntaxException("Invalid character '\\' at position 15");
		assertTrue(invalidCharException.getMessage().contains("Invalid character"));
		assertTrue(invalidCharException.getMessage().contains("position 15"));
		
		JsonSyntaxException missingBracketException = new JsonSyntaxException("Missing closing bracket ']' for array");
		assertTrue(missingBracketException.getMessage().contains("Missing closing bracket"));
		
		IOException ioError = new IOException("Connection reset");
		JsonSyntaxException ioBasedException = new JsonSyntaxException("Failed to read JSON from stream", ioError);
		assertTrue(ioBasedException.getMessage().contains("Failed to read JSON"));
		assertSame(ioError, ioBasedException.getCause());
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new JsonSyntaxException());
		assertDoesNotThrow(() -> new JsonSyntaxException((String) null));
		assertDoesNotThrow(() -> new JsonSyntaxException((Throwable) null));
		assertDoesNotThrow(() -> new JsonSyntaxException(null, null));
		
		JsonSyntaxException nullMessageException = new JsonSyntaxException((String) null);
		assertNull(nullMessageException.getMessage());
		
		JsonSyntaxException nullCauseException = new JsonSyntaxException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		JsonSyntaxException bothNullException = new JsonSyntaxException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
}
