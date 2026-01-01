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

package net.luis.utils.io.data.xml.exception;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NoSuchXmlElementException}.<br>
 *
 * @author Luis-St
 */
class NoSuchXmlElementExceptionTest {
	
	@Test
	void defaultConstructor() {
		NoSuchXmlElementException exception = new NoSuchXmlElementException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertInstanceOf(NoSuchElementException.class, exception);
	}
	
	@Test
	void messageConstructor() {
		String message = "Element not found";
		NoSuchXmlElementException exception = new NoSuchXmlElementException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void messageConstructorWithNull() {
		NoSuchXmlElementException exception = new NoSuchXmlElementException((String) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void messageAndCauseConstructor() {
		String message = "Element not found";
		Throwable cause = new RuntimeException("Root cause");
		NoSuchXmlElementException exception = new NoSuchXmlElementException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void messageAndCauseConstructorWithNulls() {
		NoSuchXmlElementException exception = new NoSuchXmlElementException(null, null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void causeConstructor() {
		Throwable cause = new RuntimeException("Root cause");
		NoSuchXmlElementException exception = new NoSuchXmlElementException(cause);
		
		assertEquals(cause.toString(), exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void causeConstructorWithNull() {
		NoSuchXmlElementException exception = new NoSuchXmlElementException((Throwable) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void inheritanceHierarchy() {
		NoSuchXmlElementException exception = new NoSuchXmlElementException();
		
		assertInstanceOf(NoSuchElementException.class, exception);
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
}