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
 * Test class for {@link NoSuchXmlAttributeException}.<br>
 *
 * @author Luis-St
 */
class NoSuchXmlAttributeExceptionTest {
	
	@Test
	void defaultConstructor() {
		NoSuchXmlAttributeException exception = new NoSuchXmlAttributeException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertInstanceOf(NoSuchElementException.class, exception);
	}
	
	@Test
	void messageConstructor() {
		String message = "Attribute not found";
		NoSuchXmlAttributeException exception = new NoSuchXmlAttributeException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void messageConstructorWithNull() {
		NoSuchXmlAttributeException exception = new NoSuchXmlAttributeException((String) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void messageAndCauseConstructor() {
		String message = "Attribute not found";
		Throwable cause = new RuntimeException("Root cause");
		NoSuchXmlAttributeException exception = new NoSuchXmlAttributeException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void messageAndCauseConstructorWithNulls() {
		NoSuchXmlAttributeException exception = new NoSuchXmlAttributeException(null, null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void causeConstructor() {
		Throwable cause = new RuntimeException("Root cause");
		NoSuchXmlAttributeException exception = new NoSuchXmlAttributeException(cause);
		
		assertEquals(cause.toString(), exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void causeConstructorWithNull() {
		NoSuchXmlAttributeException exception = new NoSuchXmlAttributeException((Throwable) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void inheritanceHierarchy() {
		NoSuchXmlAttributeException exception = new NoSuchXmlAttributeException();
		
		assertInstanceOf(NoSuchElementException.class, exception);
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
}