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

package net.luis.utils.io.network.mail.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MailAttachment}.<br>
 *
 * @author Luis-St
 */
class MailAttachmentTest {
	
	@Test
	void constructValidAttachment() {
		MailAttachment attachment = new MailAttachment("file.txt", "text/plain", new byte[] { 1, 2, 3 });
		assertEquals("file.txt", attachment.fileName());
		assertEquals("text/plain", attachment.contentType());
		assertArrayEquals(new byte[] { 1, 2, 3 }, attachment.data());
	}
	
	@Test
	void constructWithNullFileName() {
		assertThrows(NullPointerException.class, () -> new MailAttachment(null, "text/plain", new byte[0]));
	}
	
	@Test
	void constructWithNullContentType() {
		assertThrows(NullPointerException.class, () -> new MailAttachment("f.txt", null, new byte[0]));
	}
	
	@Test
	void constructWithNullData() {
		assertThrows(NullPointerException.class, () -> new MailAttachment("f.txt", "text/plain", null));
	}
	
	@Test
	void ofFileNameAndDataUsesDefaultContentType() {
		MailAttachment attachment = MailAttachment.of("f.bin", new byte[] { 9 });
		assertEquals("f.bin", attachment.fileName());
		assertEquals(MailAttachment.DEFAULT_CONTENT_TYPE, attachment.contentType());
		assertArrayEquals(new byte[] { 9 }, attachment.data());
	}
	
	@Test
	void ofFileNameContentTypeAndData() {
		MailAttachment attachment = MailAttachment.of("f.pdf", "application/pdf", new byte[] { 1 });
		assertEquals("f.pdf", attachment.fileName());
		assertEquals("application/pdf", attachment.contentType());
		assertArrayEquals(new byte[] { 1 }, attachment.data());
	}
	
	@Test
	void ofFileNameAndDataWithNullFileName() {
		assertThrows(NullPointerException.class, () -> MailAttachment.of(null, new byte[0]));
	}
	
	@Test
	void ofFileNameAndDataWithNullData() {
		assertThrows(NullPointerException.class, () -> MailAttachment.of("f", null));
	}
	
	@Test
	void ofFullWithNullFileName() {
		assertThrows(NullPointerException.class, () -> MailAttachment.of(null, "text/plain", new byte[0]));
	}
	
	@Test
	void ofFullWithNullContentType() {
		assertThrows(NullPointerException.class, () -> MailAttachment.of("f", null, new byte[0]));
	}
	
	@Test
	void ofFullWithNullData() {
		assertThrows(NullPointerException.class, () -> MailAttachment.of("f", "text/plain", null));
	}
	
	@Test
	void constructWithEmptyFileName() {
		assertThrows(IllegalArgumentException.class, () -> new MailAttachment("", "text/plain", new byte[0]));
	}
	
	@Test
	void constructWithEmptyContentType() {
		assertThrows(IllegalArgumentException.class, () -> new MailAttachment("f.txt", "", new byte[0]));
	}
	
	@Test
	void constructWithCarriageReturnInFileName() {
		assertThrows(IllegalArgumentException.class, () -> new MailAttachment("a\rb.txt", "text/plain", new byte[0]));
	}
	
	@Test
	void constructWithNewlineInFileName() {
		assertThrows(IllegalArgumentException.class, () -> new MailAttachment("a\nb.txt", "text/plain", new byte[0]));
	}
	
	@Test
	void constructWithDoubleQuoteInFileName() {
		assertThrows(IllegalArgumentException.class, () -> new MailAttachment("a\"b.txt", "text/plain", new byte[0]));
	}
	
	@Test
	void ofWithEmptyFileName() {
		assertThrows(IllegalArgumentException.class, () -> MailAttachment.of("", new byte[0]));
	}
	
	@Test
	void ofWithIllegalCharInFileName() {
		assertThrows(IllegalArgumentException.class, () -> MailAttachment.of("a\"b", "text/plain", new byte[0]));
	}
	
	@Test
	void constructWithNonEmptyFileNameSucceeds() {
		MailAttachment attachment = assertDoesNotThrow(() -> new MailAttachment("f.txt", "text/plain", new byte[0]));
		assertFalse(attachment.fileName().isEmpty());
	}
	
	@Test
	void constructWithNonEmptyContentTypeSucceeds() {
		MailAttachment attachment = assertDoesNotThrow(() -> new MailAttachment("f.txt", "text/plain", new byte[0]));
		assertEquals("text/plain", attachment.contentType());
	}
	
	@Test
	void constructWithCleanFileNameSucceeds() {
		MailAttachment attachment = assertDoesNotThrow(() -> new MailAttachment("report.pdf", "application/pdf", new byte[0]));
		assertEquals("report.pdf", attachment.fileName());
	}
	
	@Test
	void equalsSameInstance() {
		MailAttachment attachment = new MailAttachment("f.txt", "text/plain", new byte[] { 1, 2 });
		assertEquals(attachment, attachment);
	}
	
	@Test
	void equalsEqualComponents() {
		MailAttachment a = new MailAttachment("f.txt", "text/plain", new byte[] { 1, 2 });
		MailAttachment b = new MailAttachment("f.txt", "text/plain", new byte[] { 1, 2 });
		assertEquals(a, b);
	}
	
	@Test
	void equalsWithNonAttachment() {
		MailAttachment attachment = new MailAttachment("f.txt", "text/plain", new byte[] { 1, 2 });
		assertNotEquals(attachment, "string");
	}
	
	@Test
	void equalsWithNull() {
		MailAttachment attachment = new MailAttachment("f.txt", "text/plain", new byte[] { 1, 2 });
		assertNotEquals(attachment, null);
	}
	
	@Test
	void equalsWithDifferentFileName() {
		MailAttachment a = new MailAttachment("a.txt", "text/plain", new byte[] { 1, 2 });
		MailAttachment b = new MailAttachment("b.txt", "text/plain", new byte[] { 1, 2 });
		assertNotEquals(a, b);
	}
	
	@Test
	void equalsWithDifferentContentType() {
		MailAttachment a = new MailAttachment("f.txt", "text/plain", new byte[] { 1, 2 });
		MailAttachment b = new MailAttachment("f.txt", "application/pdf", new byte[] { 1, 2 });
		assertNotEquals(a, b);
	}
	
	@Test
	void equalsWithDifferentData() {
		MailAttachment a = new MailAttachment("f.txt", "text/plain", new byte[] { 1, 2 });
		MailAttachment b = new MailAttachment("f.txt", "text/plain", new byte[] { 1, 3 });
		assertNotEquals(a, b);
	}
	
	@Test
	void defaultContentTypeConstantValue() {
		assertEquals("application/octet-stream", MailAttachment.DEFAULT_CONTENT_TYPE);
	}
	
	@Test
	void accessorsReturnComponents() {
		MailAttachment attachment = new MailAttachment("f.txt", "text/plain", new byte[] { 10, 20, 30 });
		assertEquals("f.txt", attachment.fileName());
		assertEquals("text/plain", attachment.contentType());
		assertArrayEquals(new byte[] { 10, 20, 30 }, attachment.data());
	}
	
	@Test
	void hashCodeConsistentWithEquals() {
		MailAttachment a = new MailAttachment("f.txt", "text/plain", new byte[] { 1, 2 });
		MailAttachment b = new MailAttachment("f.txt", "text/plain", new byte[] { 1, 2 });
		assertEquals(a.hashCode(), b.hashCode());
	}
	
	@Test
	void hashCodeDiffersForDifferentData() {
		MailAttachment a = new MailAttachment("f.txt", "text/plain", new byte[] { 1 });
		MailAttachment b = new MailAttachment("f.txt", "text/plain", new byte[] { 2 });
		assertNotEquals(a.hashCode(), b.hashCode());
	}
	
	@Test
	void toStringShowsByteLength() {
		MailAttachment attachment = new MailAttachment("a.txt", "text/plain", new byte[] { 1, 2, 3 });
		assertEquals("Attachment[fileName=a.txt, contentType=text/plain, data=3 bytes]", attachment.toString());
	}
	
	@Test
	void constructWithEmptyData() {
		MailAttachment attachment = new MailAttachment("a.txt", "text/plain", new byte[0]);
		assertEquals(0, attachment.data().length);
		assertEquals("Attachment[fileName=a.txt, contentType=text/plain, data=0 bytes]", attachment.toString());
	}
	
	@Test
	void dataStoredByReferenceNotCopied() {
		byte[] src = { 1, 2, 3 };
		MailAttachment attachment = new MailAttachment("f", "text/plain", src);
		src[0] = 99;
		assertEquals(99, attachment.data()[0]);
	}
	
	@Test
	void accessorReturnsInternalReference() {
		byte[] src = { 1, 2 };
		MailAttachment attachment = new MailAttachment("f", "text/plain", src);
		assertSame(src, attachment.data());
	}
	
	@Test
	void largeDataAttachment() {
		MailAttachment attachment = new MailAttachment("big.bin", "application/octet-stream", new byte[1_000_000]);
		assertEquals(1_000_000, attachment.data().length);
		assertTrue(attachment.toString().contains("1000000 bytes"));
	}
	
	@Test
	void equalsHashCodeContractRoundTrip() {
		MailAttachment a = MailAttachment.of("f.bin", new byte[] { 5, 6 });
		MailAttachment b = new MailAttachment("f.bin", MailAttachment.DEFAULT_CONTENT_TYPE, new byte[] { 5, 6 });
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}
}
