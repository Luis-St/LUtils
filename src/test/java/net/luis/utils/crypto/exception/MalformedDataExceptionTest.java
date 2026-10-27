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

package net.luis.utils.crypto.exception;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MalformedDataException}.<br>
 *
 * @author Luis-St
 */
class MalformedDataExceptionTest {
	
	@Test
	void constructWithoutDetails() {
		MalformedDataException exception = new MalformedDataException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessage() {
		MalformedDataException exception = new MalformedDataException("failure");
		assertEquals("failure", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageAndCause() {
		IllegalStateException cause = new IllegalStateException("root");
		MalformedDataException exception = new MalformedDataException("failure", cause);
		assertEquals("failure", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithCause() {
		IllegalStateException cause = new IllegalStateException("root");
		MalformedDataException exception = new MalformedDataException(cause);
		assertSame(cause, exception.getCause());
		assertEquals(cause.toString(), exception.getMessage());
	}
	
	@Test
	void constructWithNullMessage() {
		MalformedDataException exception = assertDoesNotThrow(() -> new MalformedDataException((String) null));
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullCause() {
		MalformedDataException fromCause = assertDoesNotThrow(() -> new MalformedDataException((Throwable) null));
		assertNull(fromCause.getCause());
		assertNull(fromCause.getMessage());
		
		MalformedDataException fromBoth = assertDoesNotThrow(() -> new MalformedDataException("failure", null));
		assertNull(fromBoth.getCause());
		assertEquals("failure", fromBoth.getMessage());
	}
	
	@Test
	void constructWithNullMessageAndNullCause() {
		MalformedDataException exception = assertDoesNotThrow(() -> new MalformedDataException(null, null));
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void typeHierarchyConsistency() {
		assertInstanceOf(CryptoException.class, new MalformedDataException());
		assertInstanceOf(RuntimeException.class, new MalformedDataException());
	}
	
	@Test
	void constructWithEmptyMessage() {
		assertEquals("", new MalformedDataException("").getMessage());
	}
	
	@Test
	void causeChainPreservation() {
		IOException root = new IOException("root");
		IllegalArgumentException middle = new IllegalArgumentException("mid", root);
		MalformedDataException exception = new MalformedDataException("outer", middle);
		assertSame(middle, exception.getCause());
		assertSame(root, exception.getCause().getCause());
	}
	
	@Test
	void throwAndCatchAsCryptoException() {
		MalformedDataException thrown = assertThrows(MalformedDataException.class, () -> {
			throw new MalformedDataException("failure");
		});
		assertInstanceOf(CryptoException.class, thrown);
		
		CryptoException caught = assertThrows(CryptoException.class, () -> {
			throw new MalformedDataException("failure");
		});
		assertInstanceOf(MalformedDataException.class, caught);
	}
}
