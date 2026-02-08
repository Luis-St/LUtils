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

package net.luis.utils.io.codec.decoder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link DecoderException}.<br>
 *
 * @author Luis-St
 */
class DecoderExceptionTest {
	
	@Test
	void constructorWithDecoder() {
		DecoderException exception = new DecoderException((Decoder<?>) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getDecoder());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Decoding failed";
		DecoderException exception = new DecoderException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getDecoder());
	}
	
	@Test
	void constructorWithNullMessage() {
		DecoderException exception = new DecoderException((String) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getDecoder());
	}
	
	@Test
	void constructorWithEmptyMessage() {
		String message = "";
		DecoderException exception = new DecoderException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getDecoder());
	}
	
	@Test
	void constructorWithMessageAndDecoder() {
		String message = "Decoding failed";
		DecoderException exception = new DecoderException(message, (Decoder<?>) null);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getDecoder());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Decoding failed";
		RuntimeException cause = new RuntimeException("Root cause");
		DecoderException exception = new DecoderException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertSame(cause, exception.getCause());
		assertNull(exception.getDecoder());
	}
	
	@Test
	void constructorWithNullMessageAndCause() {
		RuntimeException cause = new RuntimeException("Root cause");
		DecoderException exception = new DecoderException((String) null, cause);
		
		assertNull(exception.getMessage());
		assertSame(cause, exception.getCause());
		assertNull(exception.getDecoder());
	}
	
	@Test
	void constructorWithMessageAndNullCause() {
		String message = "Decoding failed";
		DecoderException exception = new DecoderException(message, (Throwable) null);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getDecoder());
	}
	
	@Test
	void constructorWithNullMessageAndNullCause() {
		DecoderException exception = new DecoderException((String) null, (Throwable) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertNull(exception.getDecoder());
	}
	
	@Test
	void constructorWithMessageDecoderAndCause() {
		String message = "Decoding failed";
		RuntimeException cause = new RuntimeException("Root cause");
		DecoderException exception = new DecoderException(message, null, cause);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getDecoder());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructorWithDecoderAndCause() {
		RuntimeException cause = new RuntimeException("Root cause");
		DecoderException exception = new DecoderException((Decoder<?>) null, cause);
		
		assertNull(exception.getDecoder());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void isCheckedException() {
		DecoderException exception = new DecoderException("test");
		
		assertInstanceOf(Exception.class, exception);
		assertFalse(RuntimeException.class.isAssignableFrom(DecoderException.class));
	}
	
	@Test
	void canBeThrown() {
		assertThrows(DecoderException.class, () -> {
			throw new DecoderException("test exception");
		});
	}
	
	@Test
	void preservesStackTrace() {
		try {
			throw new DecoderException("test");
		} catch (DecoderException e) {
			assertNotNull(e.getStackTrace());
			assertTrue(e.getStackTrace().length > 0);
			assertEquals("preservesStackTrace", e.getStackTrace()[0].getMethodName());
		}
	}
	
	@Test
	void causedByChain() {
		Exception rootCause = new Exception("Root");
		RuntimeException intermediateCause = new RuntimeException("Intermediate", rootCause);
		DecoderException exception = new DecoderException("Final", intermediateCause);
		
		assertEquals("Final", exception.getMessage());
		assertSame(intermediateCause, exception.getCause());
		assertSame(rootCause, exception.getCause().getCause());
	}
	
	@Test
	void withComplexMessage() {
		String complexMessage = "Failed to decode value 'abc' as integer at position 5: NumberFormatException";
		DecoderException exception = new DecoderException(complexMessage);
		
		assertEquals(complexMessage, exception.getMessage());
	}
	
	@Test
	void withSpecialCharactersInMessage() {
		String specialMessage = "Decoding failed: 特殊字符 & symbols! @#$%^&*()";
		DecoderException exception = new DecoderException(specialMessage);
		
		assertEquals(specialMessage, exception.getMessage());
	}
}
