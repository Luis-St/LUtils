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

package net.luis.utils.io.data.property.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NoSuchPropertyException}.<br>
 *
 * @author Luis-St
 */
class NoSuchPropertyExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		NoSuchPropertyException exception = new NoSuchPropertyException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected property object for key 'name', but found none";
		NoSuchPropertyException exception = new NoSuchPropertyException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		NoSuchPropertyException nullMessageException = new NoSuchPropertyException((String) null);
		assertNull(nullMessageException.getMessage());
		
		NoSuchPropertyException emptyMessageException = new NoSuchPropertyException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Element not found in property structure";
		IOException cause = new IOException("Stream ended unexpectedly");
		NoSuchPropertyException exception = new NoSuchPropertyException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		NoSuchPropertyException nullMessageException = new NoSuchPropertyException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		NoSuchPropertyException nullCauseException = new NoSuchPropertyException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		NoSuchPropertyException bothNullException = new NoSuchPropertyException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		IllegalArgumentException cause = new IllegalArgumentException("Invalid key provided");
		NoSuchPropertyException exception = new NoSuchPropertyException(cause);
		
		assertTrue(exception.getMessage().contains("IllegalArgumentException"));
		assertTrue(exception.getMessage().contains("Invalid key provided"));
		assertSame(cause, exception.getCause());
		
		NoSuchPropertyException nullCauseException = new NoSuchPropertyException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NullPointerException nullCause = new NullPointerException("Key was null");
		NoSuchPropertyException nullException = new NoSuchPropertyException("Null key error", nullCause);
		assertSame(nullCause, nullException.getCause());
		
		IndexOutOfBoundsException indexCause = new IndexOutOfBoundsException("Array index out of bounds");
		NoSuchPropertyException indexException = new NoSuchPropertyException("Index error", indexCause);
		assertSame(indexCause, indexException.getCause());
		
		RuntimeException runtimeCause = new RuntimeException("General runtime error");
		NoSuchPropertyException runtimeException = new NoSuchPropertyException(runtimeCause);
		assertSame(runtimeCause, runtimeException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		NoSuchPropertyException exception = new NoSuchPropertyException();
		
		assertInstanceOf(NoSuchElementException.class, exception);
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		NoSuchPropertyException thrownException = assertThrows(NoSuchPropertyException.class, () -> {
			throw new NoSuchPropertyException("Element not found");
		});
		assertEquals("Element not found", thrownException.getMessage());
		
		NoSuchElementException caughtAsParent = assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchPropertyException("Another element not found");
		});
		assertInstanceOf(NoSuchPropertyException.class, caughtAsParent);
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new NoSuchPropertyException("Yet another element not found");
		});
		assertInstanceOf(NoSuchPropertyException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new NoSuchPropertyException("Final element not found");
		});
		assertInstanceOf(NoSuchPropertyException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Expected property value for key 'age', but found none";
		NoSuchPropertyException exception = new NoSuchPropertyException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		NullPointerException originalCause = new NullPointerException("Key is null");
		NoSuchPropertyException exception = new NoSuchPropertyException("Element lookup failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new NoSuchPropertyException("Test element not found");
		} catch (NoSuchPropertyException e) {
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
		
		NoSuchPropertyException topException = new NoSuchPropertyException("Element search failed", intermediateCause);
		
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
		NoSuchPropertyException mainException = new NoSuchPropertyException("Main element not found");
		NoSuchPropertyException suppressedException1 = new NoSuchPropertyException("Suppressed 1");
		NoSuchPropertyException suppressedException2 = new NoSuchPropertyException("Suppressed 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldElementNotFoundScenarios() {
		NoSuchPropertyException missingKeyException = new NoSuchPropertyException("Expected property object for key 'username', but found none");
		assertTrue(missingKeyException.getMessage().contains("Expected property object"));
		assertTrue(missingKeyException.getMessage().contains("key 'username'"));
		assertTrue(missingKeyException.getMessage().contains("found none"));
		
		NoSuchPropertyException missingArrayException = new NoSuchPropertyException("Expected property array for key 'items', but found none");
		assertTrue(missingArrayException.getMessage().contains("Expected property array"));
		assertTrue(missingArrayException.getMessage().contains("key 'items'"));
		
		NoSuchPropertyException missingValueException = new NoSuchPropertyException("Expected property value for key 'count', but found none");
		assertTrue(missingValueException.getMessage().contains("Expected property value"));
		assertTrue(missingValueException.getMessage().contains("key 'count'"));
		
		NoSuchPropertyException wrongTypeException = new NoSuchPropertyException("Expected boolean value for key 'active', but element is not a boolean");
		assertTrue(wrongTypeException.getMessage().contains("Expected boolean value"));
		assertTrue(wrongTypeException.getMessage().contains("key 'active'"));
		assertTrue(wrongTypeException.getMessage().contains("not a boolean"));
		
		NoSuchPropertyException nestedKeyException = new NoSuchPropertyException("Expected element at path 'user.profile.email', but found none");
		assertTrue(nestedKeyException.getMessage().contains("path 'user.profile.email'"));
	}
	
	@Test
	void messageFormatsForDifferentElementTypes() {
		String[] commonMessages = {
			"Expected property object for key 'user', but found none",
			"Expected property array for key 'items', but found none",
			"Expected property value for key 'name', but found none",
			"Expected boolean value for key 'active', but found none",
			"Expected number value for key 'age', but found none",
			"Expected string value for key 'email', but found none",
			"No element found at index 5 in property array",
			"Required field 'id' is missing from property object",
			"Path 'data.users[0].profile' does not exist"
		};
		
		for (String message : commonMessages) {
			NoSuchPropertyException exception = new NoSuchPropertyException(message);
			assertEquals(message, exception.getMessage());
			assertNotNull(exception.getMessage());
			assertFalse(exception.getMessage().isEmpty());
		}
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new NoSuchPropertyException());
		assertDoesNotThrow(() -> new NoSuchPropertyException((String) null));
		assertDoesNotThrow(() -> new NoSuchPropertyException((Throwable) null));
		assertDoesNotThrow(() -> new NoSuchPropertyException(null, null));
		
		NoSuchPropertyException nullMessageException = new NoSuchPropertyException((String) null);
		assertNull(nullMessageException.getMessage());
		
		NoSuchPropertyException nullCauseException = new NoSuchPropertyException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		NoSuchPropertyException bothNullException = new NoSuchPropertyException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void exceptionUsageInPropertyAccess() {
		assertThrows(NoSuchPropertyException.class, () -> {
			throw new NoSuchPropertyException("Expected string for key 'nonexistent', but found none");
		});
		
		assertThrows(NoSuchPropertyException.class, () -> {
			throw new NoSuchPropertyException("Array index 10 is out of bounds for array of size 5");
		});
		
		assertThrows(NoSuchPropertyException.class, () -> {
			throw new NoSuchPropertyException("Expected property object for key 'count', but found property value");
		});
	}
	
	@Test
	void compatibilityWithNoSuchElementException() {
		NoSuchPropertyException propertyException = new NoSuchPropertyException("Property element not found");
		
		NoSuchElementException genericException = propertyException;
		assertEquals("Property element not found", genericException.getMessage());
		
		assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchPropertyException("Test");
		});
		
		try {
			throw new NoSuchPropertyException("Specific test");
		} catch (NoSuchElementException e) {
			assertInstanceOf(NoSuchPropertyException.class, e);
			assertEquals("Specific test", e.getMessage());
		}
	}
}
