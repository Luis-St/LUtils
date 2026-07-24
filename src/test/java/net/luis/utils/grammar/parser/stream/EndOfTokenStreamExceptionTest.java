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

package net.luis.utils.grammar.parser.stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EndOfTokenStreamException}.<br>
 *
 * @author Luis-St
 */
class EndOfTokenStreamExceptionTest {
	
	@Test
	void constructWithNoArguments() {
		EndOfTokenStreamException exception = new EndOfTokenStreamException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessage() {
		EndOfTokenStreamException exception = new EndOfTokenStreamException("end of stream");
		
		assertEquals("end of stream", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullMessage() {
		assertDoesNotThrow(() -> new EndOfTokenStreamException((String) null));
		
		EndOfTokenStreamException exception = new EndOfTokenStreamException((String) null);
		assertNull(exception.getMessage());
	}
	
	@Test
	void constructWithMessageAndCause() {
		Throwable cause = new IllegalStateException("root");
		EndOfTokenStreamException exception = new EndOfTokenStreamException("end of stream", cause);
		
		assertEquals("end of stream", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithNullMessageAndNullCause() {
		assertDoesNotThrow(() -> new EndOfTokenStreamException(null, null));
		
		EndOfTokenStreamException exception = new EndOfTokenStreamException(null, null);
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullMessageAndCause() {
		Throwable cause = new IllegalStateException("root");
		EndOfTokenStreamException exception = new EndOfTokenStreamException(null, cause);
		
		assertNull(exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithCause() {
		Throwable cause = new IllegalStateException("root");
		EndOfTokenStreamException exception = new EndOfTokenStreamException(cause);
		
		assertSame(cause, exception.getCause());
		assertEquals(cause.toString(), exception.getMessage());
	}
	
	@Test
	void constructWithNullCause() {
		assertDoesNotThrow(() -> new EndOfTokenStreamException((Throwable) null));
		
		EndOfTokenStreamException exception = new EndOfTokenStreamException((Throwable) null);
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageAndCauseIndependentPropagation() {
		EndOfTokenStreamException exception = new EndOfTokenStreamException("only message", null);
		
		assertEquals("only message", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithEmptyStringMessage() {
		EndOfTokenStreamException exception = new EndOfTokenStreamException("");
		
		assertEquals("", exception.getMessage());
	}
}
