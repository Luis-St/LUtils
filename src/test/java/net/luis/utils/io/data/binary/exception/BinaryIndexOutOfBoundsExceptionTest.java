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

package net.luis.utils.io.data.binary.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link BinaryIndexOutOfBoundsException}.<br>
 *
 * @author Luis-St
 */
class BinaryIndexOutOfBoundsExceptionTest {
	
	@Test
	void constructWithoutDetails() {
		BinaryIndexOutOfBoundsException exception = new BinaryIndexOutOfBoundsException();
		
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessage() {
		BinaryIndexOutOfBoundsException exception = new BinaryIndexOutOfBoundsException("custom message");
		
		assertEquals("custom message", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithIndex() {
		BinaryIndexOutOfBoundsException exception = new BinaryIndexOutOfBoundsException(5);
		
		assertEquals("Binary index out of bounds: 5", exception.getMessage());
	}
	
	@Test
	void constructWithIndexAndSize() {
		BinaryIndexOutOfBoundsException exception = new BinaryIndexOutOfBoundsException(7, 3);
		
		assertEquals("Binary index out of bounds: 7 of size 3", exception.getMessage());
	}
	
	@Test
	void constructWithNullMessage() {
		BinaryIndexOutOfBoundsException exception = assertDoesNotThrow(() -> new BinaryIndexOutOfBoundsException(null));
		
		assertNull(exception.getMessage());
	}
	
	@Test
	void constructWithNegativeIndex() {
		BinaryIndexOutOfBoundsException exception = new BinaryIndexOutOfBoundsException(-1);
		
		assertEquals("Binary index out of bounds: -1", exception.getMessage());
	}
	
	@Test
	void constructWithZeroIndexAndZeroSize() {
		BinaryIndexOutOfBoundsException exception = new BinaryIndexOutOfBoundsException(0, 0);
		
		assertEquals("Binary index out of bounds: 0 of size 0", exception.getMessage());
	}
	
	@Test
	void constructWithExtremeIndexValues() {
		assertEquals("Binary index out of bounds: " + Integer.MAX_VALUE, new BinaryIndexOutOfBoundsException(Integer.MAX_VALUE).getMessage());
		assertEquals("Binary index out of bounds: " + Integer.MIN_VALUE, new BinaryIndexOutOfBoundsException(Integer.MIN_VALUE).getMessage());
		assertEquals("Binary index out of bounds: " + Integer.MIN_VALUE + " of size " + Integer.MAX_VALUE, new BinaryIndexOutOfBoundsException(Integer.MIN_VALUE, Integer.MAX_VALUE).getMessage());
	}
	
	@Test
	void exceptionIsIndexOutOfBoundsException() {
		BinaryIndexOutOfBoundsException exception = new BinaryIndexOutOfBoundsException(1);
		
		assertInstanceOf(ArrayIndexOutOfBoundsException.class, exception);
		assertInstanceOf(IndexOutOfBoundsException.class, exception);
		assertInstanceOf(RuntimeException.class, exception);
	}
}
