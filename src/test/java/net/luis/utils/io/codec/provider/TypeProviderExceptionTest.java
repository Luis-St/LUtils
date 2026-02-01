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

package net.luis.utils.io.codec.provider;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TypeProviderException}.<br>
 *
 * @author Luis-St
 */
class TypeProviderExceptionTest {
	
	@Test
	void defaultConstructor() {
		TypeProviderException exception = new TypeProviderException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Type provider operation failed";
		TypeProviderException exception = new TypeProviderException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithNullMessage() {
		TypeProviderException exception = new TypeProviderException((String) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithEmptyMessage() {
		String message = "";
		TypeProviderException exception = new TypeProviderException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithCause() {
		RuntimeException cause = new RuntimeException("Root cause");
		TypeProviderException exception = new TypeProviderException(cause);
		
		assertEquals("java.lang.RuntimeException: Root cause", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullCause() {
		TypeProviderException exception = new TypeProviderException((Throwable) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Type provider operation failed";
		RuntimeException cause = new RuntimeException("Root cause");
		TypeProviderException exception = new TypeProviderException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullMessageAndCause() {
		RuntimeException cause = new RuntimeException("Root cause");
		TypeProviderException exception = new TypeProviderException(null, cause);
		
		assertNull(exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructorWithMessageAndNullCause() {
		String message = "Type provider operation failed";
		TypeProviderException exception = new TypeProviderException(message, null);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithNullMessageAndNullCause() {
		TypeProviderException exception = new TypeProviderException(null, null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void isRuntimeException() {
		TypeProviderException exception = new TypeProviderException("test");
		
		assertInstanceOf(RuntimeException.class, exception);
	}
	
	@Test
	void canBeThrown() {
		assertThrows(TypeProviderException.class, () -> {
			throw new TypeProviderException("test exception");
		});
	}
	
	@Test
	void preservesStackTrace() {
		try {
			throw new TypeProviderException("test");
		} catch (TypeProviderException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			assertEquals("preservesStackTrace", e.getStackTrace()[0].getMethodName());
		}
	}
	
	@Test
	void causedByChain() {
		Exception rootCause = new Exception("Root");
		RuntimeException intermediateCause = new RuntimeException("Intermediate", rootCause);
		TypeProviderException exception = new TypeProviderException("Final", intermediateCause);
		
		assertEquals("Final", exception.getMessage());
		assertSame(intermediateCause, exception.getCause());
		assertSame(rootCause, exception.getCause().getCause());
	}
	
	@Test
	void withComplexMessage() {
		String complexMessage = "Failed to create integer value from 'abc': NumberFormatException";
		TypeProviderException exception = new TypeProviderException(complexMessage);
		
		assertEquals(complexMessage, exception.getMessage());
	}
	
	@Test
	void withSpecialCharactersInMessage() {
		String specialMessage = "Type provider failed: 特殊字符 & symbols! @#$%^&*()";
		TypeProviderException exception = new TypeProviderException(specialMessage);
		
		assertEquals(specialMessage, exception.getMessage());
	}
}
