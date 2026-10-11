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
 * Test class for {@link UnsupportedAlgorithmException}.<br>
 *
 * @author Luis-St
 */
class UnsupportedAlgorithmExceptionTest {
	
	@Test
	void constructWithoutDetails() {
		UnsupportedAlgorithmException exception = new UnsupportedAlgorithmException();
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessage() {
		UnsupportedAlgorithmException exception = new UnsupportedAlgorithmException("failure");
		assertEquals("failure", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithMessageAndCause() {
		IllegalStateException cause = new IllegalStateException("root");
		UnsupportedAlgorithmException exception = new UnsupportedAlgorithmException("failure", cause);
		assertEquals("failure", exception.getMessage());
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void constructWithCause() {
		IllegalStateException cause = new IllegalStateException("root");
		UnsupportedAlgorithmException exception = new UnsupportedAlgorithmException(cause);
		assertSame(cause, exception.getCause());
		assertEquals(cause.toString(), exception.getMessage());
	}
	
	@Test
	void constructWithNullMessage() {
		UnsupportedAlgorithmException exception = assertDoesNotThrow(() -> new UnsupportedAlgorithmException((String) null));
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void constructWithNullCause() {
		UnsupportedAlgorithmException fromCause = assertDoesNotThrow(() -> new UnsupportedAlgorithmException((Throwable) null));
		assertNull(fromCause.getCause());
		assertNull(fromCause.getMessage());
		
		UnsupportedAlgorithmException fromBoth = assertDoesNotThrow(() -> new UnsupportedAlgorithmException("failure", null));
		assertNull(fromBoth.getCause());
		assertEquals("failure", fromBoth.getMessage());
	}
	
	@Test
	void constructWithNullMessageAndNullCause() {
		UnsupportedAlgorithmException exception = assertDoesNotThrow(() -> new UnsupportedAlgorithmException(null, null));
		assertNull(exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void typeHierarchyConsistency() {
		assertInstanceOf(CryptoException.class, new UnsupportedAlgorithmException());
		assertInstanceOf(RuntimeException.class, new UnsupportedAlgorithmException());
	}
	
	@Test
	void constructWithEmptyMessage() {
		assertEquals("", new UnsupportedAlgorithmException("").getMessage());
	}
	
	@Test
	void causeChainPreservation() {
		IOException root = new IOException("root");
		IllegalArgumentException middle = new IllegalArgumentException("mid", root);
		UnsupportedAlgorithmException exception = new UnsupportedAlgorithmException("outer", middle);
		assertSame(middle, exception.getCause());
		assertSame(root, exception.getCause().getCause());
	}
	
	@Test
	void throwAndCatchAsCryptoException() {
		UnsupportedAlgorithmException thrown = assertThrows(UnsupportedAlgorithmException.class, () -> {
			throw new UnsupportedAlgorithmException("failure");
		});
		assertInstanceOf(CryptoException.class, thrown);
		
		CryptoException caught = assertThrows(CryptoException.class, () -> {
			throw new UnsupportedAlgorithmException("failure");
		});
		assertInstanceOf(UnsupportedAlgorithmException.class, caught);
	}
	
	@Test
	void forAlgorithmBuildsNamedMessage() {
		IllegalStateException cause = new IllegalStateException("root");
		UnsupportedAlgorithmException exception = UnsupportedAlgorithmException.forAlgorithm("Ed25519", cause);
		assertEquals("Algorithm 'Ed25519' is not available from any registered provider", exception.getMessage());
		assertTrue(exception.getMessage().contains("Ed25519"));
		assertSame(cause, exception.getCause());
	}
	
	@Test
	void forAlgorithmWithNullCause() {
		UnsupportedAlgorithmException exception = assertDoesNotThrow(() -> UnsupportedAlgorithmException.forAlgorithm("Ed25519", null));
		assertEquals("Algorithm 'Ed25519' is not available from any registered provider", exception.getMessage());
		assertNull(exception.getCause());
	}
	
	@Test
	void forAlgorithmWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> UnsupportedAlgorithmException.forAlgorithm(null, null));
	}
	
	@Test
	void forAlgorithmWithEmptyAlgorithm() {
		assertEquals("Algorithm '' is not available from any registered provider", UnsupportedAlgorithmException.forAlgorithm("", null).getMessage());
	}
}
