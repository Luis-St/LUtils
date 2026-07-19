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

package net.luis.utils.grammar.lexer.stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EndOfCharStreamException}.<br>
 *
 * @author Luis-St
 */
class EndOfCharStreamExceptionTest {
	
	@Test
	void constructWithNoArguments() {
		EndOfCharStreamException exception = new EndOfCharStreamException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessage() {
		EndOfCharStreamException exception = new EndOfCharStreamException("end of stream");
		assertEquals("end of stream", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullMessage() {
		EndOfCharStreamException exception = new EndOfCharStreamException((String) null);
		assertNull(exception.getMessage());
	}
	
	@Test
	void constructWithMessageAndCause() {
		Throwable cause = new RuntimeException("cause");
		EndOfCharStreamException exception = new EndOfCharStreamException("end of stream", cause);
		assertEquals("end of stream", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithNullMessageAndCause() {
		Throwable cause = new RuntimeException("cause");
		EndOfCharStreamException exception = new EndOfCharStreamException(null, cause);
		assertNull(exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithMessageAndNullCause() {
		EndOfCharStreamException exception = new EndOfCharStreamException("end of stream", null);
		assertEquals("end of stream", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullMessageAndNullCause() {
		EndOfCharStreamException exception = new EndOfCharStreamException(null, null);
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithCause() {
		Throwable cause = new RuntimeException("cause");
		EndOfCharStreamException exception = new EndOfCharStreamException(cause);
		assertSame(cause, exception.getCause());
		assertEquals(cause.toString(), exception.getMessage());
	}
	
	@Test
	void constructWithNullCause() {
		EndOfCharStreamException exception = new EndOfCharStreamException((Throwable) null);
		assertNull(exception.getCause());
	}
	
	@Test
	void isRuntimeExceptionInstance() {
		EndOfCharStreamException exception = new EndOfCharStreamException("msg");
		assertInstanceOf(RuntimeException.class, exception);
	}
}
