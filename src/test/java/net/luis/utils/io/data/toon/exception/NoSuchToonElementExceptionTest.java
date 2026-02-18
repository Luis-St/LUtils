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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NoSuchToonElementException}.<br>
 *
 * @author Luis-St
 */
class NoSuchToonElementExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		NoSuchToonElementException exception = new NoSuchToonElementException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected toon value for key 'name', but found none";
		NoSuchToonElementException exception = new NoSuchToonElementException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		NoSuchToonElementException nullMessageException = new NoSuchToonElementException((String) null);
		assertNull(nullMessageException.getMessage());
		
		NoSuchToonElementException emptyMessageException = new NoSuchToonElementException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Element not found in TOON structure";
		IOException cause = new IOException("Stream ended unexpectedly");
		NoSuchToonElementException exception = new NoSuchToonElementException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		NoSuchToonElementException nullMessageException = new NoSuchToonElementException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		NoSuchToonElementException nullCauseException = new NoSuchToonElementException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		NoSuchToonElementException bothNullException = new NoSuchToonElementException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		IllegalArgumentException cause = new IllegalArgumentException("Invalid key provided");
		NoSuchToonElementException exception = new NoSuchToonElementException(cause);
		
		assertTrue(exception.getMessage().contains("IllegalArgumentException"));
		assertTrue(exception.getMessage().contains("Invalid key provided"));
		assertSame(cause, exception.getCause());
		
		NoSuchToonElementException nullCauseException = new NoSuchToonElementException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NullPointerException nullCause = new NullPointerException("Key was null");
		NoSuchToonElementException nullException = new NoSuchToonElementException("Null key error", nullCause);
		assertSame(nullCause, nullException.getCause());
		
		IndexOutOfBoundsException indexCause = new IndexOutOfBoundsException("Array index out of bounds");
		NoSuchToonElementException indexException = new NoSuchToonElementException("Index error", indexCause);
		assertSame(indexCause, indexException.getCause());
		
		RuntimeException runtimeCause = new RuntimeException("General runtime error");
		NoSuchToonElementException runtimeException = new NoSuchToonElementException(runtimeCause);
		assertSame(runtimeCause, runtimeException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		NoSuchToonElementException exception = new NoSuchToonElementException();
		
		assertInstanceOf(NoSuchElementException.class, exception);
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		NoSuchToonElementException thrownException = assertThrows(NoSuchToonElementException.class, () -> {
			throw new NoSuchToonElementException("Element not found");
		});
		assertEquals("Element not found", thrownException.getMessage());
		
		NoSuchElementException caughtAsParent = assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchToonElementException("Another element not found");
		});
		assertInstanceOf(NoSuchToonElementException.class, caughtAsParent);
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new NoSuchToonElementException("Yet another element not found");
		});
		assertInstanceOf(NoSuchToonElementException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new NoSuchToonElementException("Final element not found");
		});
		assertInstanceOf(NoSuchToonElementException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Expected toon value for key 'age', but found none";
		NoSuchToonElementException exception = new NoSuchToonElementException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		NullPointerException originalCause = new NullPointerException("Key is null");
		NoSuchToonElementException exception = new NoSuchToonElementException("Element lookup failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new NoSuchToonElementException("Test element not found");
		} catch (NoSuchToonElementException e) {
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
		
		NoSuchToonElementException topException = new NoSuchToonElementException("Element search failed", intermediateCause);
		
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
		NoSuchToonElementException mainException = new NoSuchToonElementException("Main element not found");
		NoSuchToonElementException suppressedException1 = new NoSuchToonElementException("Suppressed 1");
		NoSuchToonElementException suppressedException2 = new NoSuchToonElementException("Suppressed 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldElementNotFoundScenarios() {
		NoSuchToonElementException missingKeyException = new NoSuchToonElementException("Expected toon value for key 'username', but found none");
		assertTrue(missingKeyException.getMessage().contains("Expected toon value"));
		assertTrue(missingKeyException.getMessage().contains("key 'username'"));
		assertTrue(missingKeyException.getMessage().contains("found none"));
		
		NoSuchToonElementException missingTableException = new NoSuchToonElementException("Expected toon table 'database', but found none");
		assertTrue(missingTableException.getMessage().contains("Expected toon table"));
		assertTrue(missingTableException.getMessage().contains("'database'"));
		
		NoSuchToonElementException missingArrayException = new NoSuchToonElementException("Expected toon array 'items', but found none");
		assertTrue(missingArrayException.getMessage().contains("Expected toon array"));
		assertTrue(missingArrayException.getMessage().contains("'items'"));
		
		NoSuchToonElementException wrongTypeException = new NoSuchToonElementException("Expected boolean value for key 'active', but element is not a boolean");
		assertTrue(wrongTypeException.getMessage().contains("Expected boolean value"));
		assertTrue(wrongTypeException.getMessage().contains("key 'active'"));
		assertTrue(wrongTypeException.getMessage().contains("not a boolean"));
		
		NoSuchToonElementException dottedKeyException = new NoSuchToonElementException("Expected element at path 'server.database.port', but found none");
		assertTrue(dottedKeyException.getMessage().contains("path 'server.database.port'"));
	}
	
	@Test
	void messageFormatsForDifferentElementTypes() {
		String[] commonMessages = {
			"Expected toon value for key 'user', but found none",
			"Expected toon table 'database', but found none",
			"Expected toon array 'items', but found none",
			"Expected boolean value for key 'active', but found none",
			"Expected number value for key 'age', but found none",
			"Expected string value for key 'email', but found none",
			"No element found at index 5 in toon array",
			"Required field 'id' is missing from toon table",
			"Path 'data.users[0].profile' does not exist",
			"Expected LocalDateTime for key 'created_at', but found none"
		};
		
		for (String message : commonMessages) {
			NoSuchToonElementException exception = new NoSuchToonElementException(message);
			assertEquals(message, exception.getMessage());
			assertNotNull(exception.getMessage());
			assertFalse(exception.getMessage().isEmpty());
		}
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new NoSuchToonElementException());
		assertDoesNotThrow(() -> new NoSuchToonElementException((String) null));
		assertDoesNotThrow(() -> new NoSuchToonElementException((Throwable) null));
		assertDoesNotThrow(() -> new NoSuchToonElementException(null, null));
		
		NoSuchToonElementException nullMessageException = new NoSuchToonElementException((String) null);
		assertNull(nullMessageException.getMessage());
		
		NoSuchToonElementException nullCauseException = new NoSuchToonElementException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		NoSuchToonElementException bothNullException = new NoSuchToonElementException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void exceptionUsageInToonAccess() {
		assertThrows(NoSuchToonElementException.class, () -> {
			throw new NoSuchToonElementException("Expected string for key 'nonexistent', but found none");
		});
		
		assertThrows(NoSuchToonElementException.class, () -> {
			throw new NoSuchToonElementException("Table 'missing_table' does not exist");
		});
		
		assertThrows(NoSuchToonElementException.class, () -> {
			throw new NoSuchToonElementException("Expected toon value for key 'count', but found toon table");
		});
	}
	
	@Test
	void compatibilityWithNoSuchElementException() {
		NoSuchToonElementException toonException = new NoSuchToonElementException("TOON element not found");
		
		NoSuchElementException genericException = toonException;
		assertEquals("TOON element not found", genericException.getMessage());
		
		assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchToonElementException("Test");
		});
		
		try {
			throw new NoSuchToonElementException("Specific test");
		} catch (NoSuchElementException e) {
			assertInstanceOf(NoSuchToonElementException.class, e);
			assertEquals("Specific test", e.getMessage());
		}
	}
}
