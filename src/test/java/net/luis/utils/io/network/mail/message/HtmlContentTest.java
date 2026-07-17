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
 * Test class for {@link HtmlContent}.<br>
 *
 * @author Luis-St
 */
class HtmlContentTest {
	
	@Test
	void constructWithHtmlAndCharset() {
		HtmlContent content = new HtmlContent("<h1>Hi</h1>", StandardCharsets.UTF_8);
		assertEquals("<h1>Hi</h1>", content.html());
		assertEquals(StandardCharsets.UTF_8, content.charset());
	}
	
	@Test
	void constructWithNullHtml() {
		assertThrows(NullPointerException.class, () -> new HtmlContent(null, StandardCharsets.UTF_8));
	}
	
	@Test
	void constructWithNullCharset() {
		assertThrows(NullPointerException.class, () -> new HtmlContent("<p>x</p>", null));
	}
	
	@Test
	void ofHtmlWithNullHtml() {
		assertThrows(NullPointerException.class, () -> HtmlContent.of(null));
	}
	
	@Test
	void ofHtmlAndCharsetWithNullHtml() {
		assertThrows(NullPointerException.class, () -> HtmlContent.of(null, StandardCharsets.UTF_8));
	}
	
	@Test
	void ofHtmlAndCharsetWithNullCharset() {
		assertThrows(NullPointerException.class, () -> HtmlContent.of("<p>x</p>", null));
	}
	
	@Test
	void ofHtmlUsesUtf8() {
		HtmlContent content = HtmlContent.of("<b>ok</b>");
		assertEquals(StandardCharsets.UTF_8, content.charset());
		assertEquals("<b>ok</b>", content.html());
	}
	
	@Test
	void ofHtmlAndCharsetStoresGivenCharset() {
		HtmlContent content = HtmlContent.of("<b>ok</b>", StandardCharsets.ISO_8859_1);
		assertEquals(StandardCharsets.ISO_8859_1, content.charset());
		assertEquals("<b>ok</b>", content.html());
	}
	
	@Test
	void contentTypeReturnsTextHtml() {
		assertEquals("text/html", HtmlContent.of("<p>x</p>").contentType());
	}
	
	@Test
	void htmlAccessorReturnsMarkup() {
		HtmlContent content = new HtmlContent("<div>content</div>", StandardCharsets.UTF_8);
		assertEquals("<div>content</div>", content.html());
	}
	
	@Test
	void charsetAccessorReturnsCharset() {
		HtmlContent content = new HtmlContent("<p>x</p>", StandardCharsets.US_ASCII);
		assertEquals(StandardCharsets.US_ASCII, content.charset());
	}
	
	@Test
	void constructWithEmptyHtml() {
		HtmlContent content = new HtmlContent("", StandardCharsets.UTF_8);
		assertEquals("", content.html());
		assertEquals("text/html", content.contentType());
	}
	
	@Test
	void ofEmptyHtml() {
		HtmlContent content = HtmlContent.of("");
		assertEquals("", content.html());
		assertEquals(StandardCharsets.UTF_8, content.charset());
	}
	
	@Test
	void constructWithUnicodeHtml() {
		HtmlContent content = new HtmlContent("<p>héllo 世界 🚀</p>", StandardCharsets.UTF_8);
		assertEquals("<p>héllo 世界 🚀</p>", content.html());
	}
	
	@Test
	void unicodeHtmlRoundTripThroughCharset() {
		HtmlContent content = HtmlContent.of("<p>äöü 世界 🚀</p>");
		byte[] bytes = content.html().getBytes(content.charset());
		assertEquals(content.html(), new String(bytes, content.charset()));
	}
	
	@Test
	void nonUtf8CharsetRoundTripAsciiSafe() {
		HtmlContent content = HtmlContent.of("<h1>Report</h1>", StandardCharsets.ISO_8859_1);
		byte[] bytes = content.html().getBytes(content.charset());
		assertEquals("<h1>Report</h1>", new String(bytes, content.charset()));
		assertEquals(StandardCharsets.ISO_8859_1, content.charset());
	}
	
	@Test
	void equalRecordsAreEqual() {
		HtmlContent first = new HtmlContent("<h1>Hi</h1>", StandardCharsets.UTF_8);
		HtmlContent second = new HtmlContent("<h1>Hi</h1>", StandardCharsets.UTF_8);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void differingRecordsAreNotEqual() {
		HtmlContent base = new HtmlContent("<h1>Hi</h1>", StandardCharsets.UTF_8);
		assertNotEquals(base, new HtmlContent("<h1>Bye</h1>", StandardCharsets.UTF_8));
		assertNotEquals(base, new HtmlContent("<h1>Hi</h1>", StandardCharsets.US_ASCII));
	}
	
	@Test
	void toStringContainsComponents() {
		String string = new HtmlContent("<h1>Hi</h1>", StandardCharsets.UTF_8).toString();
		assertNotNull(string);
		assertTrue(string.contains("<h1>Hi</h1>"));
		assertTrue(string.contains("UTF-8"));
	}
}
