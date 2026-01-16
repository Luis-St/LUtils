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

package net.luis.utils.io.network.address.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpParseException}.<br>
 *
 * @author Luis-St
 */
class IpParseExceptionTest {
	
	@Test
	void constructNoArgs() {
		IpParseException exception = new IpParseException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertEquals(IpParseErrorType.UNKNOWN, exception.errorType());
		assertNull(exception.input());
	}
	
	@Test
	void constructWithMessage() {
		IpParseException exception = new IpParseException("Test message");
		
		assertEquals("Test message", exception.getMessage());
		assertNull(exception.getCause());
		assertEquals(IpParseErrorType.UNKNOWN, exception.errorType());
		assertNull(exception.input());
	}
	
	@Test
	void constructWithMessageAndCause() {
		Throwable cause = new RuntimeException("Cause");
		IpParseException exception = new IpParseException("Test message", cause);
		
		assertEquals("Test message", exception.getMessage());
		assertEquals(cause, exception.getCause());
		assertEquals(IpParseErrorType.UNKNOWN, exception.errorType());
		assertNull(exception.input());
	}
	
	@Test
	void constructWithCause() {
		Throwable cause = new RuntimeException("Cause");
		IpParseException exception = new IpParseException(cause);
		
		assertEquals(cause, exception.getCause());
		assertEquals(IpParseErrorType.UNKNOWN, exception.errorType());
		assertNull(exception.input());
	}
	
	@Test
	void constructWithErrorType() {
		IpParseException exception = new IpParseException(IpParseErrorType.INVALID_FORMAT);
		
		assertNull(exception.getMessage());
		assertEquals(IpParseErrorType.INVALID_FORMAT, exception.errorType());
		assertNull(exception.input());
	}
	
	@Test
	void constructWithNullErrorTypeDefaultsToUnknown() {
		IpParseException exception = new IpParseException((IpParseErrorType) null);
		
		assertEquals(IpParseErrorType.UNKNOWN, exception.errorType());
	}
	
	@Test
	void constructWithErrorTypeAndInput() {
		IpParseException exception = new IpParseException(IpParseErrorType.INVALID_OCTET_VALUE, "256.0.0.1");
		
		assertEquals(IpParseErrorType.INVALID_OCTET_VALUE, exception.errorType());
		assertEquals("256.0.0.1", exception.input());
	}
	
	@Test
	void constructWithMessageAndErrorType() {
		IpParseException exception = new IpParseException("Invalid address", IpParseErrorType.INVALID_FORMAT);
		
		assertEquals("Invalid address", exception.getMessage());
		assertEquals(IpParseErrorType.INVALID_FORMAT, exception.errorType());
		assertNull(exception.input());
	}
	
	@Test
	void constructWithMessageErrorTypeAndInput() {
		IpParseException exception = new IpParseException("Invalid address", IpParseErrorType.INVALID_FORMAT, "bad.input");
		
		assertEquals("Invalid address", exception.getMessage());
		assertEquals(IpParseErrorType.INVALID_FORMAT, exception.errorType());
		assertEquals("bad.input", exception.input());
	}
	
	@Test
	void constructWithMessageCauseAndErrorType() {
		Throwable cause = new RuntimeException("Cause");
		IpParseException exception = new IpParseException("Invalid address", cause, IpParseErrorType.INVALID_HEXTET_VALUE);
		
		assertEquals("Invalid address", exception.getMessage());
		assertEquals(cause, exception.getCause());
		assertEquals(IpParseErrorType.INVALID_HEXTET_VALUE, exception.errorType());
		assertNull(exception.input());
	}
	
	@Test
	void constructFullArgs() {
		Throwable cause = new RuntimeException("Cause");
		IpParseException exception = new IpParseException("Invalid address", cause, IpParseErrorType.INVALID_PREFIX_LENGTH, "/33");
		
		assertEquals("Invalid address", exception.getMessage());
		assertEquals(cause, exception.getCause());
		assertEquals(IpParseErrorType.INVALID_PREFIX_LENGTH, exception.errorType());
		assertEquals("/33", exception.input());
	}
	
	@Test
	void nullErrorTypeDefaultsToUnknownInAllConstructors() {
		IpParseException e1 = new IpParseException(null, "input");
		IpParseException e2 = new IpParseException("msg", (IpParseErrorType) null);
		IpParseException e3 = new IpParseException("msg", null, (String) null);
		IpParseException e4 = new IpParseException("msg", new RuntimeException(), null);
		IpParseException e5 = new IpParseException("msg", new RuntimeException(), null, "input");
		
		assertEquals(IpParseErrorType.UNKNOWN, e1.errorType());
		assertEquals(IpParseErrorType.UNKNOWN, e2.errorType());
		assertEquals(IpParseErrorType.UNKNOWN, e3.errorType());
		assertEquals(IpParseErrorType.UNKNOWN, e4.errorType());
		assertEquals(IpParseErrorType.UNKNOWN, e5.errorType());
	}
	
	@Test
	void extendsIllegalArgumentException() {
		IpParseException exception = new IpParseException();
		assertInstanceOf(IllegalArgumentException.class, exception);
	}
}
