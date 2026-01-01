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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NoSuchJsonElementException}.<br>
 *
 * @author Luis-St
 */
class NoSuchJsonElementExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		NoSuchJsonElementException exception = new NoSuchJsonElementException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected json object for key 'name', but found none";
		NoSuchJsonElementException exception = new NoSuchJsonElementException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		NoSuchJsonElementException nullMessageException = new NoSuchJsonElementException((String) null);
		assertNull(nullMessageException.getMessage());
		
		NoSuchJsonElementException emptyMessageException = new NoSuchJsonElementException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Element not found in JSON structure";
		IOException cause = new IOException("Stream ended unexpectedly");
		NoSuchJsonElementException exception = new NoSuchJsonElementException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		NoSuchJsonElementException nullMessageException = new NoSuchJsonElementException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		NoSuchJsonElementException nullCauseException = new NoSuchJsonElementException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		NoSuchJsonElementException bothNullException = new NoSuchJsonElementException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		IllegalArgumentException cause = new IllegalArgumentException("Invalid key provided");
		NoSuchJsonElementException exception = new NoSuchJsonElementException(cause);
		
		assertTrue(exception.getMessage().contains("IllegalArgumentException"));
		assertTrue(exception.getMessage().contains("Invalid key provided"));
		assertSame(cause, exception.getCause());
		
		NoSuchJsonElementException nullCauseException = new NoSuchJsonElementException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NullPointerException nullCause = new NullPointerException("Key was null");
		NoSuchJsonElementException nullException = new NoSuchJsonElementException("Null key error", nullCause);
		assertSame(nullCause, nullException.getCause());
		
		IndexOutOfBoundsException indexCause = new IndexOutOfBoundsException("Array index out of bounds");
		NoSuchJsonElementException indexException = new NoSuchJsonElementException("Index error", indexCause);
		assertSame(indexCause, indexException.getCause());
		
		RuntimeException runtimeCause = new RuntimeException("General runtime error");
		NoSuchJsonElementException runtimeException = new NoSuchJsonElementException(runtimeCause);
		assertSame(runtimeCause, runtimeException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		NoSuchJsonElementException exception = new NoSuchJsonElementException();
		
		assertInstanceOf(NoSuchElementException.class, exception);
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		NoSuchJsonElementException thrownException = assertThrows(NoSuchJsonElementException.class, () -> {
			throw new NoSuchJsonElementException("Element not found");
		});
		assertEquals("Element not found", thrownException.getMessage());
		
		NoSuchElementException caughtAsParent = assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchJsonElementException("Another element not found");
		});
		assertInstanceOf(NoSuchJsonElementException.class, caughtAsParent);
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new NoSuchJsonElementException("Yet another element not found");
		});
		assertInstanceOf(NoSuchJsonElementException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new NoSuchJsonElementException("Final element not found");
		});
		assertInstanceOf(NoSuchJsonElementException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Expected json primitive for key 'age', but found none";
		NoSuchJsonElementException exception = new NoSuchJsonElementException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		NullPointerException originalCause = new NullPointerException("Key is null");
		NoSuchJsonElementException exception = new NoSuchJsonElementException("Element lookup failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new NoSuchJsonElementException("Test element not found");
		} catch (NoSuchJsonElementException e) {
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
		
		NoSuchJsonElementException topException = new NoSuchJsonElementException("Element search failed", intermediateCause);
		
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
		NoSuchJsonElementException mainException = new NoSuchJsonElementException("Main element not found");
		NoSuchJsonElementException suppressedException1 = new NoSuchJsonElementException("Suppressed 1");
		NoSuchJsonElementException suppressedException2 = new NoSuchJsonElementException("Suppressed 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldElementNotFoundScenarios() {
		NoSuchJsonElementException missingKeyException = new NoSuchJsonElementException("Expected json object for key 'username', but found none");
		assertTrue(missingKeyException.getMessage().contains("Expected json object"));
		assertTrue(missingKeyException.getMessage().contains("key 'username'"));
		assertTrue(missingKeyException.getMessage().contains("found none"));
		
		NoSuchJsonElementException missingArrayException = new NoSuchJsonElementException("Expected json array for key 'items', but found none");
		assertTrue(missingArrayException.getMessage().contains("Expected json array"));
		assertTrue(missingArrayException.getMessage().contains("key 'items'"));
		
		NoSuchJsonElementException missingPrimitiveException = new NoSuchJsonElementException("Expected json primitive for key 'count', but found none");
		assertTrue(missingPrimitiveException.getMessage().contains("Expected json primitive"));
		assertTrue(missingPrimitiveException.getMessage().contains("key 'count'"));
		
		NoSuchJsonElementException wrongTypeException = new NoSuchJsonElementException("Expected boolean value for key 'active', but element is not a boolean");
		assertTrue(wrongTypeException.getMessage().contains("Expected boolean value"));
		assertTrue(wrongTypeException.getMessage().contains("key 'active'"));
		assertTrue(wrongTypeException.getMessage().contains("not a boolean"));
		
		NoSuchJsonElementException nestedKeyException = new NoSuchJsonElementException("Expected element at path 'user.profile.email', but found none");
		assertTrue(nestedKeyException.getMessage().contains("path 'user.profile.email'"));
	}
	
	@Test
	void messageFormatsForDifferentElementTypes() {
		String[] commonMessages = {
			"Expected json object for key 'user', but found none",
			"Expected json array for key 'items', but found none",
			"Expected json primitive for key 'name', but found none",
			"Expected boolean value for key 'active', but found none",
			"Expected number value for key 'age', but found none",
			"Expected string value for key 'email', but found none",
			"No element found at index 5 in json array",
			"Required field 'id' is missing from json object",
			"Path 'data.users[0].profile' does not exist"
		};
		
		for (String message : commonMessages) {
			NoSuchJsonElementException exception = new NoSuchJsonElementException(message);
			assertEquals(message, exception.getMessage());
			assertNotNull(exception.getMessage());
			assertFalse(exception.getMessage().isEmpty());
		}
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new NoSuchJsonElementException());
		assertDoesNotThrow(() -> new NoSuchJsonElementException((String) null));
		assertDoesNotThrow(() -> new NoSuchJsonElementException((Throwable) null));
		assertDoesNotThrow(() -> new NoSuchJsonElementException(null, null));
		
		NoSuchJsonElementException nullMessageException = new NoSuchJsonElementException((String) null);
		assertNull(nullMessageException.getMessage());
		
		NoSuchJsonElementException nullCauseException = new NoSuchJsonElementException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		NoSuchJsonElementException bothNullException = new NoSuchJsonElementException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void exceptionUsageInJsonAccess() {
		assertThrows(NoSuchJsonElementException.class, () -> {
			throw new NoSuchJsonElementException("Expected string for key 'nonexistent', but found none");
		});
		
		assertThrows(NoSuchJsonElementException.class, () -> {
			throw new NoSuchJsonElementException("Array index 10 is out of bounds for array of size 5");
		});
		
		assertThrows(NoSuchJsonElementException.class, () -> {
			throw new NoSuchJsonElementException("Expected json object for key 'count', but found json primitive");
		});
	}
	
	@Test
	void compatibilityWithNoSuchElementException() {
		NoSuchJsonElementException jsonException = new NoSuchJsonElementException("JSON element not found");
		
		NoSuchElementException genericException = jsonException;
		assertEquals("JSON element not found", genericException.getMessage());
		
		assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchJsonElementException("Test");
		});
		
		try {
			throw new NoSuchJsonElementException("Specific test");
		} catch (NoSuchElementException e) {
			assertInstanceOf(NoSuchJsonElementException.class, e);
			assertEquals("Specific test", e.getMessage());
		}
	}
}
