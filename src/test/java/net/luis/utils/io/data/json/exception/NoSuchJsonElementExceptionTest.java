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
		
		// Test with null message
		NoSuchJsonElementException nullMessageException = new NoSuchJsonElementException((String) null);
		assertNull(nullMessageException.getMessage());
		
		// Test with empty message
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
		
		// Test with null message and valid cause
		NoSuchJsonElementException nullMessageException = new NoSuchJsonElementException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		// Test with valid message and null cause
		NoSuchJsonElementException nullCauseException = new NoSuchJsonElementException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		// Test with both null
		NoSuchJsonElementException bothNullException = new NoSuchJsonElementException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		IllegalArgumentException cause = new IllegalArgumentException("Invalid key provided");
		NoSuchJsonElementException exception = new NoSuchJsonElementException(cause);
		
		// Message should be derived from the cause
		assertTrue(exception.getMessage().contains("IllegalArgumentException"));
		assertTrue(exception.getMessage().contains("Invalid key provided"));
		assertSame(cause, exception.getCause());
		
		// Test with null cause
		NoSuchJsonElementException nullCauseException = new NoSuchJsonElementException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		// Test with different exception types as causes
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
		
		// Should be an instance of NoSuchElementException
		assertInstanceOf(NoSuchElementException.class, exception);
		
		// Should also be an instance of RuntimeException
		assertInstanceOf(RuntimeException.class, exception);
		
		// Should also be an instance of Exception
		assertInstanceOf(Exception.class, exception);
		
		// Should also be an instance of Throwable
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		// Test that the exception can be thrown and caught as itself
		NoSuchJsonElementException thrownException = assertThrows(NoSuchJsonElementException.class, () -> {
			throw new NoSuchJsonElementException("Element not found");
		});
		assertEquals("Element not found", thrownException.getMessage());
		
		// Test that it can be caught as its parent type
		NoSuchElementException caughtAsParent = assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchJsonElementException("Another element not found");
		});
		assertInstanceOf(NoSuchJsonElementException.class, caughtAsParent);
		
		// Test that it can be caught as RuntimeException
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new NoSuchJsonElementException("Yet another element not found");
		});
		assertInstanceOf(NoSuchJsonElementException.class, caughtAsRuntime);
		
		// Test that it can be caught as Exception
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
		
		// Message should remain the same through multiple calls
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		NullPointerException originalCause = new NullPointerException("Key is null");
		NoSuchJsonElementException exception = new NoSuchJsonElementException("Element lookup failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		// Cause should remain the same through multiple calls
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
			
			// The top of the stack trace should reference this test method
			StackTraceElement topElement = e.getStackTrace()[0];
			assertEquals("stackTracePreservation", topElement.getMethodName());
		}
	}
	
	@Test
	void causeChaining() {
		// Test that cause chaining works correctly
		IllegalArgumentException rootCause = new IllegalArgumentException("Invalid argument");
		NullPointerException intermediateCause = new NullPointerException("Null pointer");
		intermediateCause.initCause(rootCause);
		
		NoSuchJsonElementException topException = new NoSuchJsonElementException("Element search failed", intermediateCause);
		
		assertSame(intermediateCause, topException.getCause());
		assertSame(rootCause, topException.getCause().getCause());
		
		// Test the full chain
		Throwable current = topException;
		int chainLength = 0;
		while (current != null) {
			chainLength++;
			current = current.getCause();
		}
		assertEquals(3, chainLength); // topException -> intermediateCause -> rootCause
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
		// Test scenarios that might occur in actual JSON element lookup
		
		// Scenario 1: Missing key in object
		NoSuchJsonElementException missingKeyException = new NoSuchJsonElementException("Expected json object for key 'username', but found none");
		assertTrue(missingKeyException.getMessage().contains("Expected json object"));
		assertTrue(missingKeyException.getMessage().contains("key 'username'"));
		assertTrue(missingKeyException.getMessage().contains("found none"));
		
		// Scenario 2: Missing array element
		NoSuchJsonElementException missingArrayException = new NoSuchJsonElementException("Expected json array for key 'items', but found none");
		assertTrue(missingArrayException.getMessage().contains("Expected json array"));
		assertTrue(missingArrayException.getMessage().contains("key 'items'"));
		
		// Scenario 3: Missing primitive value
		NoSuchJsonElementException missingPrimitiveException = new NoSuchJsonElementException("Expected json primitive for key 'count', but found none");
		assertTrue(missingPrimitiveException.getMessage().contains("Expected json primitive"));
		assertTrue(missingPrimitiveException.getMessage().contains("key 'count'"));
		
		// Scenario 4: Element exists but wrong type requested
		NoSuchJsonElementException wrongTypeException = new NoSuchJsonElementException("Expected boolean value for key 'active', but element is not a boolean");
		assertTrue(wrongTypeException.getMessage().contains("Expected boolean value"));
		assertTrue(wrongTypeException.getMessage().contains("key 'active'"));
		assertTrue(wrongTypeException.getMessage().contains("not a boolean"));
		
		// Scenario 5: Nested key not found
		NoSuchJsonElementException nestedKeyException = new NoSuchJsonElementException("Expected element at path 'user.profile.email', but found none");
		assertTrue(nestedKeyException.getMessage().contains("path 'user.profile.email'"));
	}
	
	@Test
	void messageFormatsForDifferentElementTypes() {
		// Test common message formats used for different JSON element types
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
		// All constructors should handle null inputs gracefully without throwing NPE
		assertDoesNotThrow(() -> new NoSuchJsonElementException());
		assertDoesNotThrow(() -> new NoSuchJsonElementException((String) null));
		assertDoesNotThrow(() -> new NoSuchJsonElementException((Throwable) null));
		assertDoesNotThrow(() -> new NoSuchJsonElementException(null, null));
		
		// Verify the null values are preserved
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
		// Simulate how this exception would be used in actual JSON element access scenarios
		
		// Example: trying to get a non-existent key from an object
		assertThrows(NoSuchJsonElementException.class, () -> {
			// This would be thrown when calling getAsString("nonexistent") on a JsonObject
			throw new NoSuchJsonElementException("Expected string for key 'nonexistent', but found none");
		});
		
		// Example: trying to get an element at an invalid array index
		assertThrows(NoSuchJsonElementException.class, () -> {
			// This would be thrown when accessing an out-of-bounds array index
			throw new NoSuchJsonElementException("Array index 10 is out of bounds for array of size 5");
		});
		
		// Example: trying to get a specific type that doesn't exist
		assertThrows(NoSuchJsonElementException.class, () -> {
			// This would be thrown when calling getAsJsonObject() on a key that has a primitive value
			throw new NoSuchJsonElementException("Expected json object for key 'count', but found json primitive");
		});
	}
	
	@Test
	void compatibilityWithNoSuchElementException() {
		// Verify that NoSuchJsonElementException can be used wherever NoSuchElementException is expected
		NoSuchJsonElementException jsonException = new NoSuchJsonElementException("JSON element not found");
		
		// Should be usable as NoSuchElementException
		NoSuchElementException genericException = jsonException;
		assertEquals("JSON element not found", genericException.getMessage());
		
		// Should work in contexts expecting NoSuchElementException
		assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchJsonElementException("Test");
		});
		
		// Should maintain its specific type when caught as the parent type
		try {
			throw new NoSuchJsonElementException("Specific test");
		} catch (NoSuchElementException e) {
			assertInstanceOf(NoSuchJsonElementException.class, e);
			assertEquals("Specific test", e.getMessage());
		}
	}
}