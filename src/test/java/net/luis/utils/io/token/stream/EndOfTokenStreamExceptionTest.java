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

package net.luis.utils.io.token.stream;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EndOfTokenStreamException}.<br>
 *
 * @author Luis-St
 */
class EndOfTokenStreamExceptionTest {
	
	@Test
	void constructorWithNoParameters() {
		EndOfTokenStreamException exception = new EndOfTokenStreamException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Test message";
		EndOfTokenStreamException exception = new EndOfTokenStreamException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithNullMessage() {
		EndOfTokenStreamException exception = new EndOfTokenStreamException((String) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithMessageAndCause() {
		String message = "Test message";
		Throwable cause = new RuntimeException("Cause");
		EndOfTokenStreamException exception = new EndOfTokenStreamException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullMessageAndCause() {
		Throwable cause = new RuntimeException("Cause");
		EndOfTokenStreamException exception = new EndOfTokenStreamException(null, cause);
		
		assertNull(exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithMessageAndNullCause() {
		String message = "Test message";
		EndOfTokenStreamException exception = new EndOfTokenStreamException(message, null);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructorWithCause() {
		Throwable cause = new RuntimeException("Cause");
		EndOfTokenStreamException exception = new EndOfTokenStreamException(cause);
		
		assertEquals(cause.toString(), exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void constructorWithNullCause() {
		EndOfTokenStreamException exception = new EndOfTokenStreamException((Throwable) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
}
