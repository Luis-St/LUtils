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

package net.luis.utils.crypto.util;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PemDocument}.<br>
 *
 * @author Luis-St
 */
class PemDocumentTest {
	
	@Test
	void constructDocument() {
		PemDocument document = new PemDocument("PUBLIC KEY", new byte[] { 1, 2, 3 });
		assertEquals("PUBLIC KEY", document.label());
		assertArrayEquals(new byte[] { 1, 2, 3 }, document.content());
	}
	
	@Test
	void constructWithNullLabel() {
		assertThrows(NullPointerException.class, () -> new PemDocument(null, new byte[0]));
	}
	
	@Test
	void constructWithNullContent() {
		assertThrows(NullPointerException.class, () -> new PemDocument("PUBLIC KEY", null));
	}
	
	@Test
	void constructWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new PemDocument(null, null));
		assertEquals("Label must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithValidLabelAndContent() {
		PemDocument document = assertDoesNotThrow(() -> new PemDocument("CERTIFICATE", new byte[] { 42 }));
		assertNotNull(document.label());
		assertNotNull(document.content());
	}
	
	@Test
	void constructWithEmptyLabel() {
		PemDocument document = new PemDocument("", new byte[] { 1 });
		assertEquals("", document.label());
		assertEquals(1, document.content().length);
	}
	
	@Test
	void constructWithEmptyContent() {
		PemDocument document = new PemDocument("PUBLIC KEY", new byte[0]);
		assertEquals("PUBLIC KEY", document.label());
		assertEquals(0, document.content().length);
	}
	
	@Test
	void accessorsReturnConstructorArguments() {
		byte[] der = { 48, 13, 6, 9, 42 };
		PemDocument document = new PemDocument("CERTIFICATE", der);
		assertEquals("CERTIFICATE", document.label());
		assertArrayEquals(der, document.content());
	}
	
	@Test
	void contentReturnsLiveArray() {
		byte[] der = { 1, 2, 3 };
		PemDocument document = new PemDocument("PUBLIC KEY", der);
		assertSame(der, document.content());
		assertSame(document.content(), document.content());
	}
	
	@Test
	void constructWithLongLabel() {
		String label = "X".repeat(200);
		PemDocument document = new PemDocument(label, new byte[] { 1 });
		assertEquals(label, document.label());
		assertEquals(200, document.label().length());
	}
	
	@Test
	void equalityUsesArrayIdentity() {
		byte[] shared = { 1, 2, 3 };
		PemDocument first = new PemDocument("PUBLIC KEY", shared);
		PemDocument second = new PemDocument("PUBLIC KEY", new byte[] { 1, 2, 3 });
		PemDocument third = new PemDocument("PUBLIC KEY", shared);
		
		assertArrayEquals(first.content(), second.content());
		assertNotEquals(first, second);
		assertEquals(first, third);
		assertEquals(first.hashCode(), third.hashCode());
	}
	
	@Test
	void hashCodeConsistency() {
		byte[] shared = { 9, 8, 7 };
		PemDocument document = new PemDocument("CERTIFICATE", shared);
		assertEquals(document.hashCode(), document.hashCode());
		assertEquals(document.hashCode(), new PemDocument("CERTIFICATE", shared).hashCode());
	}
	
	@Test
	void documentWithDifferentLabelsIsNotEqual() {
		byte[] shared = { 1 };
		assertNotEquals(new PemDocument("PUBLIC KEY", shared), new PemDocument("PRIVATE KEY", shared));
	}
	
	@Test
	void toStringContainsLabel() {
		PemDocument document = new PemDocument("PUBLIC KEY", new byte[0]);
		String text = assertDoesNotThrow(document::toString);
		assertTrue(text.contains("PemDocument"));
		assertTrue(text.contains("PUBLIC KEY"));
	}
	
	@Test
	void mutatingContentIsVisibleThroughAccessor() {
		byte[] der = { 1, 2, 3 };
		PemDocument document = new PemDocument("PUBLIC KEY", der);
		der[0] = 42;
		
		assertEquals(42, document.content()[0]);
		assertArrayEquals(new byte[] { 42, 2, 3 }, document.content());
	}
	
	@Test
	void documentIsUsableInCollections() {
		byte[] der = { 1, 2, 3 };
		PemDocument document = new PemDocument("PUBLIC KEY", der);
		
		assertTrue(Objects.equals(document, document));
		assertFalse(Objects.equals(document, new PemDocument("PUBLIC KEY", new byte[] { 1, 2, 3 })));
		assertTrue(Objects.equals(document, new PemDocument("PUBLIC KEY", der)));
	}
}
