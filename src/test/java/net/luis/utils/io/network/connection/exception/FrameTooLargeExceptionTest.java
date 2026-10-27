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

package net.luis.utils.io.network.connection.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link FrameTooLargeException}.<br>
 *
 * @author Luis-St
 */
class FrameTooLargeExceptionTest {
	
	@Test
	void constructWithFrameLengthAndMaxBytes() {
		FrameTooLargeException exception = new FrameTooLargeException(20, 10);
		
		assertEquals(20, exception.frameLength());
		assertEquals(10, exception.maxBytes());
	}
	
	@Test
	void constructWithZeroFrameLengthAndMaxBytes() {
		FrameTooLargeException exception = new FrameTooLargeException(0, 0);
		
		assertEquals(0, exception.frameLength());
		assertEquals(0, exception.maxBytes());
	}
	
	@Test
	void messageContainsFrameLengthAndMaxBytes() {
		FrameTooLargeException exception = new FrameTooLargeException(1024, 512);
		
		assertNotNull(exception.getMessage());
		assertTrue(exception.getMessage().contains("1024"));
		assertTrue(exception.getMessage().contains("512"));
	}
	
	@Test
	void constructHasNoCause() {
		FrameTooLargeException exception = new FrameTooLargeException(20, 10);
		
		assertNull(exception.getCause());
	}
	
	@Test
	void extendsIOException() {
		FrameTooLargeException exception = new FrameTooLargeException(20, 10);
		
		assertInstanceOf(IOException.class, exception);
	}
}
