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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NoSuchIniElementException}.<br>
 *
 * @author Luis-St
 */
class NoSuchIniElementExceptionTest {
	
	@Test
	void constructorWithNoArguments() {
		NoSuchIniElementException exception = new NoSuchIniElementException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Expected ini value for key 'name', but found none";
		NoSuchIniElementException exception = new NoSuchIniElementException(message);
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		
		NoSuchIniElementException nullMessageException = new NoSuchIniElementException((String) null);
		assertNull(nullMessageException.getMessage());
		
		NoSuchIniElementException emptyMessageException = new NoSuchIniElementException("");
		assertEquals("", emptyMessageException.getMessage());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Element not found in INI structure";
		IOException cause = new IOException("Stream ended unexpectedly");
		NoSuchIniElementException exception = new NoSuchIniElementException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		
		NoSuchIniElementException nullMessageException = new NoSuchIniElementException(null, cause);
		assertNull(nullMessageException.getMessage());
		assertSame(cause, nullMessageException.getCause());
		
		NoSuchIniElementException nullCauseException = new NoSuchIniElementException(message, null);
		assertEquals(message, nullCauseException.getMessage());
		assertNull(nullCauseException.getCause());
		
		NoSuchIniElementException bothNullException = new NoSuchIniElementException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void constructorWithCause() {
		IllegalArgumentException cause = new IllegalArgumentException("Invalid key provided");
		NoSuchIniElementException exception = new NoSuchIniElementException(cause);
		
		assertTrue(exception.getMessage().contains("IllegalArgumentException"));
		assertTrue(exception.getMessage().contains("Invalid key provided"));
		assertSame(cause, exception.getCause());
		
		NoSuchIniElementException nullCauseException = new NoSuchIniElementException((Throwable) null);
		assertNull(nullCauseException.getCause());
	}
	
	@Test
	void constructorWithDifferentCauseTypes() {
		NullPointerException nullCause = new NullPointerException("Key was null");
		NoSuchIniElementException nullException = new NoSuchIniElementException("Null key error", nullCause);
		assertSame(nullCause, nullException.getCause());
		
		IndexOutOfBoundsException indexCause = new IndexOutOfBoundsException("Index out of bounds");
		NoSuchIniElementException indexException = new NoSuchIniElementException("Index error", indexCause);
		assertSame(indexCause, indexException.getCause());
		
		RuntimeException runtimeCause = new RuntimeException("General runtime error");
		NoSuchIniElementException runtimeException = new NoSuchIniElementException(runtimeCause);
		assertSame(runtimeCause, runtimeException.getCause());
	}
	
	@Test
	void exceptionInheritance() {
		NoSuchIniElementException exception = new NoSuchIniElementException();
		
		assertInstanceOf(NoSuchElementException.class, exception);
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
	
	@Test
	void exceptionCanBeThrownAndCaught() {
		NoSuchIniElementException thrownException = assertThrows(NoSuchIniElementException.class, () -> {
			throw new NoSuchIniElementException("Element not found");
		});
		assertEquals("Element not found", thrownException.getMessage());
		
		NoSuchElementException caughtAsParent = assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchIniElementException("Another element not found");
		});
		assertInstanceOf(NoSuchIniElementException.class, caughtAsParent);
		
		RuntimeException caughtAsRuntime = assertThrows(RuntimeException.class, () -> {
			throw new NoSuchIniElementException("Yet another element not found");
		});
		assertInstanceOf(NoSuchIniElementException.class, caughtAsRuntime);
		
		Exception caughtAsException = assertThrows(Exception.class, () -> {
			throw new NoSuchIniElementException("Final element not found");
		});
		assertInstanceOf(NoSuchIniElementException.class, caughtAsException);
	}
	
	@Test
	void messagePreservation() {
		String originalMessage = "Expected ini value for key 'age', but found none";
		NoSuchIniElementException exception = new NoSuchIniElementException(originalMessage);
		
		assertEquals(originalMessage, exception.getMessage());
		
		assertEquals(originalMessage, exception.getMessage());
		assertEquals(originalMessage, exception.getMessage());
	}
	
	@Test
	void causePreservation() {
		NullPointerException originalCause = new NullPointerException("Key is null");
		NoSuchIniElementException exception = new NoSuchIniElementException("Element lookup failed", originalCause);
		
		assertSame(originalCause, exception.getCause());
		
		assertSame(originalCause, exception.getCause());
		assertSame(originalCause, exception.getCause());
	}
	
	@Test
	void stackTracePreservation() {
		try {
			throw new NoSuchIniElementException("Test element not found");
		} catch (NoSuchIniElementException e) {
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
		
		NoSuchIniElementException topException = new NoSuchIniElementException("Element search failed", intermediateCause);
		
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
		NoSuchIniElementException mainException = new NoSuchIniElementException("Main element not found");
		NoSuchIniElementException suppressedException1 = new NoSuchIniElementException("Suppressed 1");
		NoSuchIniElementException suppressedException2 = new NoSuchIniElementException("Suppressed 2");
		
		mainException.addSuppressed(suppressedException1);
		mainException.addSuppressed(suppressedException2);
		
		Throwable[] suppressed = mainException.getSuppressed();
		assertEquals(2, suppressed.length);
		assertSame(suppressedException1, suppressed[0]);
		assertSame(suppressedException2, suppressed[1]);
	}
	
	@Test
	void realWorldElementNotFoundScenarios() {
		NoSuchIniElementException missingKeyException = new NoSuchIniElementException("Expected ini value for key 'username', but found none");
		assertTrue(missingKeyException.getMessage().contains("Expected ini value"));
		assertTrue(missingKeyException.getMessage().contains("key 'username'"));
		assertTrue(missingKeyException.getMessage().contains("found none"));
		
		NoSuchIniElementException missingSectionException = new NoSuchIniElementException("Expected ini section 'database', but found none");
		assertTrue(missingSectionException.getMessage().contains("Expected ini section"));
		assertTrue(missingSectionException.getMessage().contains("'database'"));
		
		NoSuchIniElementException missingGlobalException = new NoSuchIniElementException("Expected global property 'version', but found none");
		assertTrue(missingGlobalException.getMessage().contains("Expected global property"));
		assertTrue(missingGlobalException.getMessage().contains("'version'"));
		
		NoSuchIniElementException wrongTypeException = new NoSuchIniElementException("Expected boolean value for key 'active', but element is not a boolean");
		assertTrue(wrongTypeException.getMessage().contains("Expected boolean value"));
		assertTrue(wrongTypeException.getMessage().contains("key 'active'"));
		assertTrue(wrongTypeException.getMessage().contains("not a boolean"));
		
		NoSuchIniElementException nestedKeyException = new NoSuchIniElementException("Expected element in section 'server' for key 'port', but found none");
		assertTrue(nestedKeyException.getMessage().contains("section 'server'"));
		assertTrue(nestedKeyException.getMessage().contains("key 'port'"));
	}
	
	@Test
	void messageFormatsForDifferentElementTypes() {
		String[] commonMessages = {
			"Expected ini value for key 'user', but found none",
			"Expected ini section 'database', but found none",
			"Expected boolean value for key 'active', but found none",
			"Expected number value for key 'age', but found none",
			"Expected string value for key 'email', but found none",
			"Required field 'id' is missing from ini section",
			"Section 'settings' does not exist in document",
			"Global property 'version' not found"
		};
		
		for (String message : commonMessages) {
			NoSuchIniElementException exception = new NoSuchIniElementException(message);
			assertEquals(message, exception.getMessage());
			assertNotNull(exception.getMessage());
			assertFalse(exception.getMessage().isEmpty());
		}
	}
	
	@Test
	void nullToleranceInConstructors() {
		assertDoesNotThrow(() -> new NoSuchIniElementException());
		assertDoesNotThrow(() -> new NoSuchIniElementException((String) null));
		assertDoesNotThrow(() -> new NoSuchIniElementException((Throwable) null));
		assertDoesNotThrow(() -> new NoSuchIniElementException(null, null));
		
		NoSuchIniElementException nullMessageException = new NoSuchIniElementException((String) null);
		assertNull(nullMessageException.getMessage());
		
		NoSuchIniElementException nullCauseException = new NoSuchIniElementException((Throwable) null);
		assertNull(nullCauseException.getCause());
		
		NoSuchIniElementException bothNullException = new NoSuchIniElementException(null, null);
		assertNull(bothNullException.getMessage());
		assertNull(bothNullException.getCause());
	}
	
	@Test
	void exceptionUsageInIniAccess() {
		assertThrows(NoSuchIniElementException.class, () -> {
			throw new NoSuchIniElementException("Expected string for key 'nonexistent', but found none");
		});
		
		assertThrows(NoSuchIniElementException.class, () -> {
			throw new NoSuchIniElementException("Section 'missing_section' does not exist");
		});
		
		assertThrows(NoSuchIniElementException.class, () -> {
			throw new NoSuchIniElementException("Expected ini value for key 'count', but found ini section");
		});
	}
	
	@Test
	void compatibilityWithNoSuchElementException() {
		NoSuchIniElementException iniException = new NoSuchIniElementException("INI element not found");
		
		NoSuchElementException genericException = iniException;
		assertEquals("INI element not found", genericException.getMessage());
		
		assertThrows(NoSuchElementException.class, () -> {
			throw new NoSuchIniElementException("Test");
		});
		
		try {
			throw new NoSuchIniElementException("Specific test");
		} catch (NoSuchElementException e) {
			assertInstanceOf(NoSuchIniElementException.class, e);
			assertEquals("Specific test", e.getMessage());
		}
	}
}
