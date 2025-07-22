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

package net.luis.utils.io.data.xml.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link XmlTypeException}.<br>
 *
 * @author Luis-St
 */
class XmlTypeExceptionTest {
	
	@Test
	void defaultConstructor() {
		XmlTypeException exception = new XmlTypeException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertInstanceOf(RuntimeException.class, exception);
	}
	
	@Test
	void messageConstructor() {
		String message = "Unexpected XML type";
		XmlTypeException exception = new XmlTypeException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void messageConstructorWithNull() {
		XmlTypeException exception = new XmlTypeException((String) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void messageAndCauseConstructor() {
		String message = "Unexpected XML type";
		Throwable cause = new RuntimeException("Root cause");
		XmlTypeException exception = new XmlTypeException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void messageAndCauseConstructorWithNulls() {
		XmlTypeException exception = new XmlTypeException(null, null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void causeConstructor() {
		Throwable cause = new RuntimeException("Root cause");
		XmlTypeException exception = new XmlTypeException(cause);
		
		assertEquals(cause.toString(), exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void causeConstructorWithNull() {
		XmlTypeException exception = new XmlTypeException((Throwable) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void inheritanceHierarchy() {
		XmlTypeException exception = new XmlTypeException();
		
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
}