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

package net.luis.utils.io.codec.encoder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EncoderException}.<br>
 *
 * @author Luis-St
 */
class EncoderExceptionTest {
	
	@Test
	void constructorWithEncoder() {
		EncoderException exception = new EncoderException((Encoder<?>) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getCodec());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Encoding failed";
		EncoderException exception = new EncoderException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getCodec());
	}
	
	@Test
	void constructorWithNullMessage() {
		EncoderException exception = new EncoderException((String) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getCodec());
	}
	
	@Test
	void constructorWithEmptyMessage() {
		String message = "";
		EncoderException exception = new EncoderException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getCodec());
	}
	
	@Test
	void constructorWithMessageAndEncoder() {
		String message = "Encoding failed";
		EncoderException exception = new EncoderException(message, (Encoder<?>) null);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getCodec());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Encoding failed";
		RuntimeException cause = new RuntimeException("Root cause");
		EncoderException exception = new EncoderException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		assertNull(exception.getCodec());
	}
	
	@Test
	void constructorWithNullMessageAndCause() {
		RuntimeException cause = new RuntimeException("Root cause");
		EncoderException exception = new EncoderException((String) null, cause);
		
		assertNull(exception.getMessage());
		assertSame(cause, exception.getCause());
		assertNull(exception.getCodec());
	}
	
	@Test
	void constructorWithMessageAndNullCause() {
		String message = "Encoding failed";
		EncoderException exception = new EncoderException(message, (Throwable) null);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getCodec());
	}
	
	@Test
	void constructorWithNullMessageAndNullCause() {
		EncoderException exception = new EncoderException((String) null, (Throwable) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getCodec());
	}
	
	@Test
	void constructorWithMessageEncoderAndCause() {
		String message = "Encoding failed";
		RuntimeException cause = new RuntimeException("Root cause");
		EncoderException exception = new EncoderException(message, null, cause);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCodec());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructorWithEncoderAndCause() {
		RuntimeException cause = new RuntimeException("Root cause");
		EncoderException exception = new EncoderException((Encoder<?>) null, cause);
		
		assertNull(exception.getCodec());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void isCheckedException() {
		EncoderException exception = new EncoderException("test");
		
		assertInstanceOf(Exception.class, exception);
		assertFalse(RuntimeException.class.isAssignableFrom(EncoderException.class));
	}
	
	@Test
	void canBeThrown() {
		assertThrows(EncoderException.class, () -> {
			throw new EncoderException("test exception");
		});
	}
	
	@Test
	void preservesStackTrace() {
		try {
			throw new EncoderException("test");
		} catch (EncoderException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			assertEquals("preservesStackTrace", e.getStackTrace()[0].getMethodName());
		}
	}
	
	@Test
	void causedByChain() {
		Exception rootCause = new Exception("Root");
		RuntimeException intermediateCause = new RuntimeException("Intermediate", rootCause);
		EncoderException exception = new EncoderException("Final", intermediateCause);
		
		assertEquals("Final", exception.getMessage());
		assertSame(intermediateCause, exception.getCause());
		assertSame(rootCause, exception.getCause().getCause());
	}
	
	@Test
	void withComplexMessage() {
		String complexMessage = "Failed to encode value '123' as string at field 'name': IllegalStateException";
		EncoderException exception = new EncoderException(complexMessage);
		
		assertEquals(complexMessage, exception.getMessage());
	}
	
	@Test
	void withSpecialCharactersInMessage() {
		String specialMessage = "Encoding failed: 特殊字符 & symbols! @#$%^&*()";
		EncoderException exception = new EncoderException(specialMessage);
		
		assertEquals(specialMessage, exception.getMessage());
	}
}
