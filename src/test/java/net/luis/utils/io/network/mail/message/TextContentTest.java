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

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TextContent}.<br>
 *
 * @author Luis-St
 */
class TextContentTest {
	
	@Test
	void constructWithTextAndCharset() {
		TextContent content = new TextContent("Hello", StandardCharsets.ISO_8859_1);
		assertNotNull(content);
		assertEquals("Hello", content.text());
		assertEquals(StandardCharsets.ISO_8859_1, content.charset());
	}
	
	@Test
	void constructWithNullText() {
		assertThrows(NullPointerException.class, () -> new TextContent(null, StandardCharsets.UTF_8));
	}
	
	@Test
	void constructWithNullCharset() {
		assertThrows(NullPointerException.class, () -> new TextContent("Hi", null));
	}
	
	@Test
	void ofTextUsesUtf8() {
		TextContent content = TextContent.of("Hello");
		assertEquals("Hello", content.text());
		assertEquals(StandardCharsets.UTF_8, content.charset());
	}
	
	@Test
	void ofTextAndCharset() {
		TextContent content = TextContent.of("Bonjour", StandardCharsets.ISO_8859_1);
		assertEquals("Bonjour", content.text());
		assertEquals(StandardCharsets.ISO_8859_1, content.charset());
	}
	
	@Test
	void ofNullTextThrows() {
		assertThrows(NullPointerException.class, () -> TextContent.of(null));
	}
	
	@Test
	void ofNullTextWithCharsetThrows() {
		assertThrows(NullPointerException.class, () -> TextContent.of(null, StandardCharsets.UTF_8));
	}
	
	@Test
	void ofNullCharsetThrows() {
		assertThrows(NullPointerException.class, () -> TextContent.of("Hi", null));
	}
	
	@Test
	void contentTypeIsTextPlain() {
		TextContent content = TextContent.of("x");
		assertEquals("text/plain", content.contentType());
	}
	
	@Test
	void contentTypeIndependentOfTextAndCharset() {
		TextContent utf8 = TextContent.of("a");
		TextContent latin = TextContent.of("b", StandardCharsets.ISO_8859_1);
		assertEquals("text/plain", utf8.contentType());
		assertEquals("text/plain", latin.contentType());
	}
	
	@Test
	void textAccessorReturnsComponent() {
		TextContent content = TextContent.of("payload");
		assertEquals("payload", content.text());
	}
	
	@Test
	void charsetAccessorReturnsComponent() {
		TextContent content = TextContent.of("x", StandardCharsets.US_ASCII);
		assertEquals(StandardCharsets.US_ASCII, content.charset());
	}
	
	@Test
	void defaultCharsetPathYieldsUtf8() {
		TextContent defaultCharset = TextContent.of("x");
		TextContent explicitCharset = TextContent.of("x", StandardCharsets.UTF_16);
		assertEquals(StandardCharsets.UTF_8, defaultCharset.charset());
		assertEquals(StandardCharsets.UTF_16, explicitCharset.charset());
	}
	
	@Test
	void constructWithEmptyText() {
		TextContent content = TextContent.of("");
		assertEquals("", content.text());
		assertEquals(StandardCharsets.UTF_8, content.charset());
		assertEquals("text/plain", content.contentType());
	}
	
	@Test
	void equalContentsAreEqual() {
		TextContent first = TextContent.of("same", StandardCharsets.UTF_8);
		TextContent second = TextContent.of("same", StandardCharsets.UTF_8);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void differentTextNotEqual() {
		assertNotEquals(TextContent.of("a"), TextContent.of("b"));
	}
	
	@Test
	void differentCharsetNotEqual() {
		assertNotEquals(TextContent.of("a", StandardCharsets.UTF_8), TextContent.of("a", StandardCharsets.ISO_8859_1));
	}
	
	@Test
	void toStringContainsTextAndCharset() {
		String string = TextContent.of("Hi", StandardCharsets.UTF_8).toString();
		assertTrue(string.contains("Hi"));
		assertTrue(string.contains("TextContent"));
		assertTrue(string.contains("UTF-8"));
	}
	
	@Test
	void notEqualToNullOrDifferentType() {
		TextContent content = TextContent.of("x");
		assertNotEquals(content, null);
		assertNotEquals(content, "x");
	}
	
	@Test
	void constructWithUnicodeText() {
		TextContent content = TextContent.of("Grüße 世界 😀");
		assertEquals("Grüße 世界 😀", content.text());
		assertEquals(StandardCharsets.UTF_8, content.charset());
	}
	
	@Test
	void unicodeRoundTripThroughStoredCharset() {
		TextContent content = TextContent.of("Grüße", StandardCharsets.UTF_8);
		byte[] bytes = content.text().getBytes(content.charset());
		assertEquals(content.text(), new String(bytes, content.charset()));
	}
	
	@Test
	void latin1CharsetRoundTrip() {
		TextContent content = TextContent.of("Café", StandardCharsets.ISO_8859_1);
		byte[] bytes = content.text().getBytes(content.charset());
		assertEquals("Café", new String(bytes, content.charset()));
		assertEquals(StandardCharsets.ISO_8859_1, content.charset());
	}
	
	@Test
	void multiLineTextPreserved() {
		TextContent content = TextContent.of("line1\r\nline2\n");
		assertEquals("line1\r\nline2\n", content.text());
	}
}
