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
 * Test class for {@link CryptoException}.<br>
 *
 * @author Luis-St
 */
class CryptoExceptionTest {
	
	@Test
	void constructWithoutDetails() {
		CryptoException exception = new CryptoException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessage() {
		CryptoException exception = new CryptoException("failure");
		assertEquals("failure", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageAndCause() {
		IllegalStateException cause = new IllegalStateException("root");
		CryptoException exception = new CryptoException("failure", cause);
		assertEquals("failure", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithCause() {
		IllegalStateException cause = new IllegalStateException("root");
		CryptoException exception = new CryptoException(cause);
		assertSame(cause, exception.getCause());
		assertEquals(cause.toString(), exception.getMessage());
	}
	
	@Test
	void constructWithNullMessage() {
		CryptoException exception = assertDoesNotThrow(() -> new CryptoException((String) null));
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullCause() {
		CryptoException fromCause = assertDoesNotThrow(() -> new CryptoException((Throwable) null));
		assertNull(fromCause.getCause());
		assertNull(fromCause.getMessage());
		
		CryptoException fromBoth = assertDoesNotThrow(() -> new CryptoException("failure", null));
		assertNull(fromBoth.getCause());
		assertEquals("failure", fromBoth.getMessage());
	}
	
	@Test
	void constructWithNullMessageAndNullCause() {
		CryptoException exception = assertDoesNotThrow(() -> new CryptoException(null, null));
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void typeHierarchyConsistency() {
		assertInstanceOf(RuntimeException.class, new CryptoException());
		assertInstanceOf(RuntimeException.class, new CryptoException());
	}
	
	@Test
	void constructWithEmptyMessage() {
		assertEquals("", new CryptoException("").getMessage());
	}
	
	@Test
	void causeChainPreservation() {
		IOException root = new IOException("root");
		IllegalArgumentException middle = new IllegalArgumentException("mid", root);
		CryptoException exception = new CryptoException("outer", middle);
		assertSame(middle, exception.getCause());
		assertSame(root, exception.getCause().getCause());
	}
	
	@Test
	void throwAndCatchAsCryptoException() {
		CryptoException thrown = assertThrows(CryptoException.class, () -> {
			throw new CryptoException("failure");
		});
		assertInstanceOf(RuntimeException.class, thrown);
		
		RuntimeException caught = assertThrows(RuntimeException.class, () -> {
			throw new CryptoException("failure");
		});
		assertInstanceOf(CryptoException.class, caught);
	}
}
