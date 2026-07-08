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
 * Test class for {@link MailContent}.<br>
 *
 * @author Luis-St
 */
class MailContentTest {
	
	@Test
	void contentTypeForTextContent() {
		MailContent content = TextContent.of("Hello");
		assertEquals("text/plain", content.contentType());
	}
	
	@Test
	void contentTypeForHtmlContent() {
		MailContent content = HtmlContent.of("<h1>Hi</h1>");
		assertEquals("text/html", content.contentType());
	}
	
	@Test
	void contentTypeForMailAttachment() {
		MailContent content = MailAttachment.of("report.pdf", "application/pdf", new byte[] { 1, 2, 3 });
		assertEquals("application/pdf", content.contentType());
	}
	
	@Test
	void contentTypeForMultipartContent() {
		MailContent content = MultipartContent.mixed(TextContent.of("body"), HtmlContent.of("<p>x</p>"));
		assertEquals("multipart/mixed", content.contentType());
	}
	
	@Test
	void permittedImplementationsAreMailContent() {
		assertInstanceOf(MailContent.class, TextContent.of("x"));
		assertInstanceOf(MailContent.class, HtmlContent.of("<p>x</p>"));
		assertInstanceOf(MailContent.class, MailAttachment.of("file.txt", new byte[] { 0 }));
		assertInstanceOf(MailContent.class, MultipartContent.mixed(TextContent.of("x")));
	}
}
