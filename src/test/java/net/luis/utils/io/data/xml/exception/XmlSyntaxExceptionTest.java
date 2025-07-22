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
 * Test class for {@link XmlSyntaxException}.<br>
 *
 * @author Luis-St
 */
class XmlSyntaxExceptionTest {
	
	@Test
	void defaultConstructor() {
		XmlSyntaxException exception = new XmlSyntaxException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
		assertInstanceOf(RuntimeException.class, exception);
	}
	
	@Test
	void messageConstructor() {
		String message = "Invalid XML syntax";
		XmlSyntaxException exception = new XmlSyntaxException(message);
		
		assertEquals(message, exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void messageConstructorWithNull() {
		XmlSyntaxException exception = new XmlSyntaxException((String) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void messageAndCauseConstructor() {
		String message = "Invalid XML syntax";
		Throwable cause = new RuntimeException("Root cause");
		XmlSyntaxException exception = new XmlSyntaxException(message, cause);
		
		assertEquals(message, exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void messageAndCauseConstructorWithNulls() {
		XmlSyntaxException exception = new XmlSyntaxException(null, null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void causeConstructor() {
		Throwable cause = new RuntimeException("Root cause");
		XmlSyntaxException exception = new XmlSyntaxException(cause);
		
		assertEquals(cause.toString(), exception.getMessage());
		assertEquals(cause, exception.getCause());
	}
	
	@Test
	void causeConstructorWithNull() {
		XmlSyntaxException exception = new XmlSyntaxException((Throwable) null);
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void inheritanceHierarchy() {
		XmlSyntaxException exception = new XmlSyntaxException();
		
		assertInstanceOf(RuntimeException.class, exception);
		assertInstanceOf(Exception.class, exception);
		assertInstanceOf(Throwable.class, exception);
	}
}