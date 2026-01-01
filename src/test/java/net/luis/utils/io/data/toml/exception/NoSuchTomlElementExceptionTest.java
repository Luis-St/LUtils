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

package net.luis.utils.io.data.toml.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NoSuchTomlElementException}.<br>
 *
 * @author Luis-St
 */
class NoSuchTomlElementExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		NoSuchTomlElementException exception = new NoSuchTomlElementException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected toml value for key 'name', but found none";
		NoSuchTomlElementException exception = new NoSuchTomlElementException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		NoSuchTomlElementException nullMessageException = new NoSuchTomlElementException((String) null);
		assertNull(nullMessageException.getMessage());
		
		NoSuchTomlElementException emptyMessageException = new NoSuchTomlElementException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Element not found in TOML structure";
		IOException cause = new IOException("Stream ended unexpectedly");
		NoSuchTomlElementException exception = new NoSuchTomlElementException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		NoSuchTomlElementException nullMessageException = new NoSuchTomlElementException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		NoSuchTomlElementException nullCauseException = new NoSuchTomlElementException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		NoSuchTomlElementException bothNullException = new NoSuchTomlElementException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		IllegalArgumentException cause = new IllegalArgumentException("Invalid key provided");
		NoSuchTomlElementException exception = new NoSuchTomlElementException(cause);
		
		assertTrue(exception.getMessage().contains("IllegalArgumentException"));
		assertTrue(exception.getMessage().contains("Invalid key provided"));
		assertSame(cause, exception.getCause());
		
		NoSuchTomlElementException nullCauseException = new NoSuchTomlElementException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NullPointerException nullCause = new NullPointerException("Key was null");
		NoSuchTomlElementException nullException = new NoSuchTomlElementException("Null key error", nullCause);
		assertSame(nullCause, nullException.getCause());
		
		IndexOutOfBoundsException indexCause = new IndexOutOfBoundsException("Array index out of bounds");
		NoSuchTomlElementException indexException = new NoSuchTomlElementException("Index error", indexCause);
		assertSame(indexCause, indexException.getCause());
		
		RuntimeException runtimeCause = new RuntimeException("General runtime error");
		NoSuchTomlElementException runtimeException = new NoSuchTomlElementException(runtimeCause);
		assertSame(runtimeCause, runtimeException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		NoSuchTomlElementException exception = new NoSuchTomlElementException();
		
		assertInstanceOf(NoSuchElementException.class, exception);
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		NoSuchTomlElementException thrownException = assertThrows(NoSuchTomlElementException.class, () -> {
			throw new NoSuchTomlElementException("Element not found");
		});
		assertEquals("Element not found", thrownException.getMessage());
		
		NoSuchElementException caughtAsParent = assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchTomlElementException("Another element not found");
		});
		assertInstanceOf(NoSuchTomlElementException.class, caughtAsParent);
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new NoSuchTomlElementException("Yet another element not found");
		});
		assertInstanceOf(NoSuchTomlElementException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new NoSuchTomlElementException("Final element not found");
		});
		assertInstanceOf(NoSuchTomlElementException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Expected toml value for key 'age', but found none";
		NoSuchTomlElementException exception = new NoSuchTomlElementException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		NullPointerException originalCause = new NullPointerException("Key is null");
		NoSuchTomlElementException exception = new NoSuchTomlElementException("Element lookup failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new NoSuchTomlElementException("Test element not found");
		} catch (NoSuchTomlElementException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			
			StackTraceElement topElement = e.getStackTrace()[0];
			assertEquals("stackTracePreservation", topElement.getMethodName());
		}
	}
	
	@Test
	void causeChaining() {
		IllegalArgumentException rootCause = new IllegalArgumentException("Invalid argument");
		NullPointerException intermediateCause = new NullPointerException("Null pointer");
		intermediateCause.initCause(rootCause);
		
		NoSuchTomlElementException topException = new NoSuchTomlElementException("Element search failed", intermediateCause);
		
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
		NoSuchTomlElementException mainException = new NoSuchTomlElementException("Main element not found");
		NoSuchTomlElementException suppressedException1 = new NoSuchTomlElementException("Suppressed 1");
		NoSuchTomlElementException suppressedException2 = new NoSuchTomlElementException("Suppressed 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldElementNotFoundScenarios() {
		NoSuchTomlElementException missingKeyException = new NoSuchTomlElementException("Expected toml value for key 'username', but found none");
		assertTrue(missingKeyException.getMessage().contains("Expected toml value"));
		assertTrue(missingKeyException.getMessage().contains("key 'username'"));
		assertTrue(missingKeyException.getMessage().contains("found none"));
		
		NoSuchTomlElementException missingTableException = new NoSuchTomlElementException("Expected toml table 'database', but found none");
		assertTrue(missingTableException.getMessage().contains("Expected toml table"));
		assertTrue(missingTableException.getMessage().contains("'database'"));
		
		NoSuchTomlElementException missingArrayException = new NoSuchTomlElementException("Expected toml array 'items', but found none");
		assertTrue(missingArrayException.getMessage().contains("Expected toml array"));
		assertTrue(missingArrayException.getMessage().contains("'items'"));
		
		NoSuchTomlElementException wrongTypeException = new NoSuchTomlElementException("Expected boolean value for key 'active', but element is not a boolean");
		assertTrue(wrongTypeException.getMessage().contains("Expected boolean value"));
		assertTrue(wrongTypeException.getMessage().contains("key 'active'"));
		assertTrue(wrongTypeException.getMessage().contains("not a boolean"));
		
		NoSuchTomlElementException dottedKeyException = new NoSuchTomlElementException("Expected element at path 'server.database.port', but found none");
		assertTrue(dottedKeyException.getMessage().contains("path 'server.database.port'"));
	}
	
	@Test
	void messageFormatsForDifferentElementTypes() {
		String[] commonMessages = {
			"Expected toml value for key 'user', but found none",
			"Expected toml table 'database', but found none",
			"Expected toml array 'items', but found none",
			"Expected boolean value for key 'active', but found none",
			"Expected number value for key 'age', but found none",
			"Expected string value for key 'email', but found none",
			"No element found at index 5 in toml array",
			"Required field 'id' is missing from toml table",
			"Path 'data.users[0].profile' does not exist",
			"Expected LocalDateTime for key 'created_at', but found none"
		};
		
		for (String message : commonMessages) {
			NoSuchTomlElementException exception = new NoSuchTomlElementException(message);
			assertEquals(message, exception.getMessage());
			assertNotNull(exception.getMessage());
			assertFalse(exception.getMessage().isEmpty());
		}
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new NoSuchTomlElementException());
		assertDoesNotThrow(() -> new NoSuchTomlElementException((String) null));
		assertDoesNotThrow(() -> new NoSuchTomlElementException((Throwable) null));
		assertDoesNotThrow(() -> new NoSuchTomlElementException(null, null));
		
		NoSuchTomlElementException nullMessageException = new NoSuchTomlElementException((String) null);
		assertNull(nullMessageException.getMessage());
		
		NoSuchTomlElementException nullCauseException = new NoSuchTomlElementException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		NoSuchTomlElementException bothNullException = new NoSuchTomlElementException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void exceptionUsageInTomlAccess() {
		assertThrows(NoSuchTomlElementException.class, () -> {
			throw new NoSuchTomlElementException("Expected string for key 'nonexistent', but found none");
		});
		
		assertThrows(NoSuchTomlElementException.class, () -> {
			throw new NoSuchTomlElementException("Table 'missing_table' does not exist");
		});
		
		assertThrows(NoSuchTomlElementException.class, () -> {
			throw new NoSuchTomlElementException("Expected toml value for key 'count', but found toml table");
		});
	}
	
	@Test
	void compatibilityWithNoSuchElementException() {
		NoSuchTomlElementException tomlException = new NoSuchTomlElementException("TOML element not found");
		
		NoSuchElementException genericException = tomlException;
		assertEquals("TOML element not found", genericException.getMessage());
		
		assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchTomlElementException("Test");
		});
		
		try {
			throw new NoSuchTomlElementException("Specific test");
		} catch (NoSuchElementException e) {
			assertInstanceOf(NoSuchTomlElementException.class, e);
			assertEquals("Specific test", e.getMessage());
		}
	}
}
