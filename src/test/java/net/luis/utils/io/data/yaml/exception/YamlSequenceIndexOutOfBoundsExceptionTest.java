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

package net.luis.utils.io.data.yaml.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link YamlSequenceIndexOutOfBoundsException}.<br>
 *
 * @author Luis-St
 */
class YamlSequenceIndexOutOfBoundsExceptionTest {
	
	@Test
	void constructorNoArgs() {
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException();
		assertNull(exception.getMessage());
	}
	
	@Test
	void constructorWithIndex() {
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException(5);
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("5"));
	}
	
	@Test
	void constructorWithNegativeIndex() {
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException(-1);
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("-1"));
	}
	
	@Test
	void constructorWithIndexAndSize() {
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException(10, 5);
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("10"));
		assertTrue(exception.getMessage().contains("5"));
	}
	
	@Test
	void constructorWithZeroIndex() {
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException(0, 0);
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("0"));
	}
	
	@Test
	void constructorWithMessage() {
		String message = "Custom error message";
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException(message);
		assertEquals(message, exception.getMessage());
	}
	
	@Test
	void constructorWithNullMessage() {
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException(null);
		assertNull(exception.getMessage());
	}
	
	@Test
	void isIndexOutOfBoundsException() {
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException();
		assertInstanceOf(IndexOutOfBoundsException.class, exception);
	}
	
	@Test
	void isRuntimeException() {
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException();
		assertInstanceOf(RuntimeException.class, exception);
	}
	
	@Test
	void canBeThrown() {
		assertThrows(YamlSequenceIndexOutOfBoundsException.class, () -> {
			throw new YamlSequenceIndexOutOfBoundsException(5);
		});
	}
	
	@Test
	void canBeCaughtAsIndexOutOfBounds() {
		try {
			throw new YamlSequenceIndexOutOfBoundsException(3);
		} catch (IndexOutOfBoundsException e) {
			assertInstanceOf(YamlSequenceIndexOutOfBoundsException.class, e);
		}
	}
	
	@Test
	void canBeCaught() {
		try {
			throw new YamlSequenceIndexOutOfBoundsException(7, 3);
		} catch (YamlSequenceIndexOutOfBoundsException e) {
			assertTrue(e.getMessage().contains("7"));
			assertTrue(e.getMessage().contains("3"));
		}
	}
	
	@Test
	void messageFormatWithIndex() {
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException(42);
		assertEquals("Yaml sequence index out of bounds: 42", exception.getMessage());
	}
	
	@Test
	void messageFormatWithIndexAndSize() {
		YamlSequenceIndexOutOfBoundsException exception = new YamlSequenceIndexOutOfBoundsException(10, 5);
		assertEquals("Yaml sequence index out of bounds: 10, size: 5", exception.getMessage());
	}
	
	@Test
	void differentIndexValues() {
		assertEquals("Yaml sequence index out of bounds: 0", new YamlSequenceIndexOutOfBoundsException(0).getMessage());
		assertEquals("Yaml sequence index out of bounds: 100", new YamlSequenceIndexOutOfBoundsException(100).getMessage());
		assertEquals("Yaml sequence index out of bounds: -5", new YamlSequenceIndexOutOfBoundsException(-5).getMessage());
	}
	
	@Test
	void differentSizeValues() {
		assertEquals("Yaml sequence index out of bounds: 5, size: 0", new YamlSequenceIndexOutOfBoundsException(5, 0).getMessage());
		assertEquals("Yaml sequence index out of bounds: 5, size: 3", new YamlSequenceIndexOutOfBoundsException(5, 3).getMessage());
		assertEquals("Yaml sequence index out of bounds: 5, size: 100", new YamlSequenceIndexOutOfBoundsException(5, 100).getMessage());
	}
}
