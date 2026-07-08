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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MailMessageSerializer}.<br>
 *
 * @author Luis-St
 */
class MailMessageSerializerTest {
	
	private static final String CRLF = "\r\n";
	private static final String DATE = "Date: Thu, 01 Jan 1970 00:00:00 +0000" + CRLF;
	private static final Mailbox ALICE = Mailbox.of(MailAddress.of("alice", "example.com"));
	private static final Mailbox BOB = Mailbox.of(MailAddress.of("bob", "example.com"));
	private static final Mailbox CAROL = Mailbox.of(MailAddress.of("carol", "example.com"));
	
	private static String encodedWord(String text) {
		return "=?UTF-8?B?" + Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8)) + "?=";
	}
	
	@Test
	void constructorIsPrivateAndClassNotInstantiable() throws Exception {
		Constructor<MailMessageSerializer> constructor = MailMessageSerializer.class.getDeclaredConstructor();
		
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		assertDoesNotThrow(() -> {constructor.newInstance();});
	}
	
	@Test
	void serializeWithNullMessage() {
		assertThrows(NullPointerException.class, () -> MailMessageSerializer.serialize(null));
	}
	
	@Test
	void encodeHeaderTextWithNull() {
		assertThrows(NullPointerException.class, () -> MailMessageSerializer.encodeHeaderText(null));
	}
	
	@Test
	void encodeDisplayNameWithNull() {
		assertThrows(NullPointerException.class, () -> MailMessageSerializer.encodeDisplayName(null));
	}
	
	@Test
	void serializeWithReplyTo() {
		MailMessage message = new MailMessage(ALICE, BOB, List.of(MailRecipient.to(CAROL)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("Reply-To: bob@example.com" + CRLF));
	}
	
	@Test
	void serializeWithoutReplyTo() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertFalse(MailMessageSerializer.serialize(message).contains("Reply-To:"));
	}
	
	@Test
	void serializeWithToRecipients() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("To: bob@example.com" + CRLF));
	}
	
	@Test
	void serializeWithMultipleToRecipients() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(Mailbox.parse("a@x.com")), MailRecipient.to(Mailbox.parse("b@x.com")));
		MailMessage message = new MailMessage(ALICE, null, recipients, "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("To: a@x.com, b@x.com" + CRLF));
	}
	
	@Test
	void serializeWithCcRecipients() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB), MailRecipient.cc(CAROL));
		MailMessage message = new MailMessage(ALICE, null, recipients, "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("Cc: carol@example.com" + CRLF));
	}
	
	@Test
	void serializeWithoutCcRecipients() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertFalse(MailMessageSerializer.serialize(message).contains("Cc:"));
	}
	
	@Test
	void serializeWithMultipleCcRecipients() {
		List<MailRecipient> recipients = List.of(MailRecipient.cc(Mailbox.parse("c1@x.com")), MailRecipient.cc(Mailbox.parse("c2@x.com")));
		MailMessage message = new MailMessage(ALICE, null, recipients, "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("Cc: c1@x.com, c2@x.com" + CRLF));
	}
	
	@Test
	void serializeOmitsBccRecipients() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB), MailRecipient.bcc(Mailbox.parse("secret@x.com")));
		MailMessage message = new MailMessage(ALICE, null, recipients, "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("To: bob@example.com" + CRLF));
		assertFalse(output.contains("secret@x.com"));
	}
	
	@Test
	void serializeWithMessageIdAddsBrackets() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, "abc@host", List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("Message-ID: <abc@host>" + CRLF));
	}
	
	@Test
	void serializeWithBracketedMessageIdUnchanged() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, "<abc@host>", List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("Message-ID: <abc@host>" + CRLF));
	}
	
	@Test
	void serializeWithPartiallyBracketedMessageId() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, "<id@host", List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("Message-ID: <<id@host>" + CRLF));
	}
	
	@Test
	void serializeWithoutMessageId() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertFalse(MailMessageSerializer.serialize(message).contains("Message-ID:"));
	}
	
	@Test
	void serializeWritesMimeVersionHeader() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("MIME-Version: 1.0" + CRLF));
	}
	
	@Test
	void serializeWithCustomHeaders() {
		List<MailHeader> headers = List.of(new MailHeader("X-Priority", "1"), new MailHeader("X-Mailer", "LUtils"));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, headers);
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("X-Priority: 1" + CRLF));
		assertTrue(output.contains("X-Mailer: LUtils" + CRLF));
	}
	
	@Test
	void serializeWithoutCustomHeaders() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("MIME-Version: 1.0" + CRLF + "Content-Type: text/plain"));
	}
	
	@Test
	void serializeWithAsciiSubject() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("Subject: Hello" + CRLF));
	}
	
	@Test
	void serializeWithNonAsciiSubject() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Grüße", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("Subject: " + encodedWord("Grüße") + CRLF));
	}
	
	@Test
	void serializeTextContent() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("Content-Type: text/plain; charset=\"UTF-8\"" + CRLF));
		assertTrue(output.contains("Content-Transfer-Encoding: quoted-printable" + CRLF));
		assertTrue(output.contains(CRLF + CRLF + "Hi" + CRLF));
	}
	
	@Test
	void serializeTextContentWithCustomCharset() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("x", StandardCharsets.ISO_8859_1), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("charset=\"ISO-8859-1\""));
	}
	
	@Test
	void serializeHtmlContent() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", HtmlContent.of("<b>Hi</b>"), Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("Content-Type: text/html; charset=\"UTF-8\"" + CRLF));
		assertTrue(output.contains("Content-Transfer-Encoding: quoted-printable" + CRLF));
		assertTrue(output.contains(CRLF + CRLF + "<b>Hi</b>" + CRLF));
	}
	
	@Test
	void serializeHtmlContentWithCustomCharset() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", HtmlContent.of("x", StandardCharsets.ISO_8859_1), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains("charset=\"ISO-8859-1\""));
	}
	
	@Test
	void serializeAttachmentContent() {
		byte[] data = { 1, 2, 3 };
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", MailAttachment.of("a.bin", data), Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("Content-Type: application/octet-stream; name=\"a.bin\"" + CRLF));
		assertTrue(output.contains("Content-Transfer-Encoding: base64" + CRLF));
		assertTrue(output.contains("Content-Disposition: attachment; filename=\"a.bin\"" + CRLF));
		assertTrue(output.contains(CRLF + CRLF + Base64.getMimeEncoder().encodeToString(data) + CRLF));
	}
	
	@Test
	void serializeMultipartWithMultipleParts() {
		MailContent content = MultipartContent.mixed(TextContent.of("t"), MailAttachment.of("a.bin", new byte[] { 1, 2, 3 }));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", content, Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("Content-Type: multipart/mixed; boundary=\"=_Part_0\"" + CRLF));
		assertEquals(2, output.split("--=_Part_0" + CRLF, -1).length - 1);
		assertTrue(output.contains("--=_Part_0--" + CRLF));
	}
	
	@Test
	void serializeMultipartWithSinglePart() {
		MailContent content = MultipartContent.alternative(TextContent.of("t"));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", content, Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("Content-Type: multipart/alternative; boundary=\"=_Part_0\"" + CRLF));
		assertTrue(output.contains("--=_Part_0" + CRLF));
		assertTrue(output.contains("--=_Part_0--" + CRLF));
	}
	
	@Test
	void serializeNestedMultipartUsesUniqueBoundaries() {
		MailContent inner = MultipartContent.alternative(TextContent.of("t"), HtmlContent.of("<b>h</b>"));
		MailContent content = MultipartContent.mixed(inner, MailAttachment.of("a.bin", new byte[] { 1, 2, 3 }));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", content, Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("boundary=\"=_Part_0\""));
		assertTrue(output.contains("boundary=\"=_Part_1\""));
		assertTrue(output.contains("--=_Part_1--" + CRLF));
		assertTrue(output.contains("--=_Part_0--" + CRLF));
	}
	
	@Test
	void quotedPrintablePassesPrintableAscii() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hello!"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains(CRLF + CRLF + "Hello!" + CRLF));
	}
	
	@Test
	void quotedPrintableEncodesEqualsSign() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("a=b"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains(CRLF + CRLF + "a=3Db" + CRLF));
	}
	
	@Test
	void quotedPrintableEncodesHighBytes() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("ä"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains(CRLF + CRLF + "=C3=A4" + CRLF));
	}
	
	@Test
	void quotedPrintableEncodesControlChar() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("a\fb"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains(CRLF + CRLF + "a=0Cb" + CRLF));
	}
	
	@Test
	void quotedPrintableKeepsMidLineSpace() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("a b"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains(CRLF + CRLF + "a b" + CRLF));
	}
	
	@Test
	void quotedPrintableEncodesTrailingSpace() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("ab "), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains(CRLF + CRLF + "ab=20" + CRLF));
	}
	
	@Test
	void quotedPrintableEncodesTrailingTab() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("ab\t"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains(CRLF + CRLF + "ab=09" + CRLF));
	}
	
	@Test
	void quotedPrintableEncodesSpaceBeforeNewline() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("a \nb"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains(CRLF + CRLF + "a=20" + CRLF + "b" + CRLF));
	}
	
	@Test
	void quotedPrintableEncodesSpaceBeforeCarriageReturn() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("a \r\nb"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains(CRLF + CRLF + "a=20" + CRLF + "b" + CRLF));
	}
	
	@Test
	void quotedPrintableConvertsNewlineToCrlf() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("a\nb"), Instant.EPOCH, null, List.of());
		
		assertTrue(MailMessageSerializer.serialize(message).contains(CRLF + CRLF + "a" + CRLF + "b" + CRLF));
	}
	
	@Test
	void quotedPrintableSkipsCarriageReturn() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("a\r\nb"), Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains(CRLF + CRLF + "a" + CRLF + "b" + CRLF));
		assertFalse(output.contains("=0D"));
	}
	
	@Test
	void quotedPrintableSoftWrapsLongLine() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("a".repeat(120)), Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("=" + CRLF));
		for (String line : output.split(CRLF, -1)) {
			assertTrue(line.length() <= 76);
		}
	}
	
	@Test
	void encodeHeaderTextReturnsAsciiUnchanged() {
		assertEquals("Plain Subject", MailMessageSerializer.encodeHeaderText("Plain Subject"));
	}
	
	@Test
	void encodeHeaderTextEncodesNonAscii() {
		String result = MailMessageSerializer.encodeHeaderText("Grüße");
		
		assertEquals(encodedWord("Grüße"), result);
		assertTrue(result.startsWith("=?UTF-8?B?"));
		assertTrue(result.endsWith("?="));
	}
	
	@Test
	void encodeDisplayNameReturnsPlainAscii() {
		assertEquals("John Doe", MailMessageSerializer.encodeDisplayName("John Doe"));
	}
	
	@Test
	void encodeDisplayNameQuotesSpecials() {
		assertEquals("\"Doe, John\"", MailMessageSerializer.encodeDisplayName("Doe, John"));
	}
	
	@Test
	void encodeDisplayNameEscapesQuoteAndBackslash() {
		assertEquals("\"a\\\"b\\\\c\"", MailMessageSerializer.encodeDisplayName("a\"b\\c"));
	}
	
	@Test
	void encodeDisplayNameEncodesNonAscii() {
		assertEquals(encodedWord("Müller"), MailMessageSerializer.encodeDisplayName("Müller"));
	}
	
	@Test
	void serializeSimpleTextMessage() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi Bob!"), Instant.EPOCH, null, List.of());
		String expected =
			"From: alice@example.com" + CRLF +
				"To: bob@example.com" + CRLF +
				"Subject: Hello" + CRLF +
				DATE +
				"MIME-Version: 1.0" + CRLF +
				"Content-Type: text/plain; charset=\"UTF-8\"" + CRLF +
				"Content-Transfer-Encoding: quoted-printable" + CRLF +
				CRLF +
				"Hi Bob!" + CRLF;
		
		assertEquals(expected, MailMessageSerializer.serialize(message));
	}
	
	@Test
	void serializeSimpleHtmlMessage() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", HtmlContent.of("<p>Hi</p>"), Instant.EPOCH, null, List.of());
		String expected =
			"From: alice@example.com" + CRLF +
				"To: bob@example.com" + CRLF +
				"Subject: Hello" + CRLF +
				DATE +
				"MIME-Version: 1.0" + CRLF +
				"Content-Type: text/html; charset=\"UTF-8\"" + CRLF +
				"Content-Transfer-Encoding: quoted-printable" + CRLF +
				CRLF +
				"<p>Hi</p>" + CRLF;
		
		assertEquals(expected, MailMessageSerializer.serialize(message));
	}
	
	@Test
	void serializeMessageWithToAndCc() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB), MailRecipient.cc(CAROL));
		MailMessage message = new MailMessage(ALICE, null, recipients, "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		String expected =
			"From: alice@example.com" + CRLF +
				"To: bob@example.com" + CRLF +
				"Cc: carol@example.com" + CRLF +
				"Subject: Hello" + CRLF +
				DATE +
				"MIME-Version: 1.0" + CRLF +
				"Content-Type: text/plain; charset=\"UTF-8\"" + CRLF +
				"Content-Transfer-Encoding: quoted-printable" + CRLF +
				CRLF +
				"Hi" + CRLF;
		
		assertEquals(expected, MailMessageSerializer.serialize(message));
	}
	
	@Test
	void serializeMessageWithReplyToAndMessageId() {
		Mailbox replyTo = Mailbox.parse("reply@example.com");
		MailMessage message = new MailMessage(ALICE, replyTo, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, "id@host", List.of());
		String expected =
			"From: alice@example.com" + CRLF +
				"Reply-To: reply@example.com" + CRLF +
				"To: bob@example.com" + CRLF +
				"Subject: Hello" + CRLF +
				DATE +
				"Message-ID: <id@host>" + CRLF +
				"MIME-Version: 1.0" + CRLF +
				"Content-Type: text/plain; charset=\"UTF-8\"" + CRLF +
				"Content-Transfer-Encoding: quoted-printable" + CRLF +
				CRLF +
				"Hi" + CRLF;
		
		assertEquals(expected, MailMessageSerializer.serialize(message));
	}
	
	@Test
	void serializeMessageWithSingleAttachment() {
		byte[] data = { 1, 2, 3 };
		MailContent content = MultipartContent.mixed(TextContent.of("body"), MailAttachment.of("f.txt", "text/plain", data));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", content, Instant.EPOCH, null, List.of());
		String expected =
			"From: alice@example.com" + CRLF +
				"To: bob@example.com" + CRLF +
				"Subject: Hello" + CRLF +
				DATE +
				"MIME-Version: 1.0" + CRLF +
				"Content-Type: multipart/mixed; boundary=\"=_Part_0\"" + CRLF +
				CRLF +
				"--=_Part_0" + CRLF +
				"Content-Type: text/plain; charset=\"UTF-8\"" + CRLF +
				"Content-Transfer-Encoding: quoted-printable" + CRLF +
				CRLF +
				"body" + CRLF +
				"--=_Part_0" + CRLF +
				"Content-Type: text/plain; name=\"f.txt\"" + CRLF +
				"Content-Transfer-Encoding: base64" + CRLF +
				"Content-Disposition: attachment; filename=\"f.txt\"" + CRLF +
				CRLF +
				Base64.getMimeEncoder().encodeToString(data) + CRLF +
				"--=_Part_0--" + CRLF;
		
		assertEquals(expected, MailMessageSerializer.serialize(message));
	}
	
	@Test
	void serializeMessageWithCustomHeader() {
		List<MailHeader> headers = List.of(new MailHeader("X-Priority", "1"));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, headers);
		String expected =
			"From: alice@example.com" + CRLF +
				"To: bob@example.com" + CRLF +
				"Subject: Hello" + CRLF +
				DATE +
				"MIME-Version: 1.0" + CRLF +
				"X-Priority: 1" + CRLF +
				"Content-Type: text/plain; charset=\"UTF-8\"" + CRLF +
				"Content-Transfer-Encoding: quoted-printable" + CRLF +
				CRLF +
				"Hi" + CRLF;
		
		assertEquals(expected, MailMessageSerializer.serialize(message));
	}
	
	@Test
	void serializeMultipartMixedWithAlternativeAndAttachment() {
		byte[] data = { 1, 2, 3 };
		MailContent inner = MultipartContent.alternative(TextContent.of("plain"), HtmlContent.of("<b>rich</b>"));
		MailContent content = MultipartContent.mixed(inner, MailAttachment.of("r.pdf", "application/pdf", data));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", content, Instant.EPOCH, null, List.of());
		String expected =
			"From: alice@example.com" + CRLF +
				"To: bob@example.com" + CRLF +
				"Subject: Hello" + CRLF +
				DATE +
				"MIME-Version: 1.0" + CRLF +
				"Content-Type: multipart/mixed; boundary=\"=_Part_0\"" + CRLF +
				CRLF +
				"--=_Part_0" + CRLF +
				"Content-Type: multipart/alternative; boundary=\"=_Part_1\"" + CRLF +
				CRLF +
				"--=_Part_1" + CRLF +
				"Content-Type: text/plain; charset=\"UTF-8\"" + CRLF +
				"Content-Transfer-Encoding: quoted-printable" + CRLF +
				CRLF +
				"plain" + CRLF +
				"--=_Part_1" + CRLF +
				"Content-Type: text/html; charset=\"UTF-8\"" + CRLF +
				"Content-Transfer-Encoding: quoted-printable" + CRLF +
				CRLF +
				"<b>rich</b>" + CRLF +
				"--=_Part_1--" + CRLF +
				"--=_Part_0" + CRLF +
				"Content-Type: application/pdf; name=\"r.pdf\"" + CRLF +
				"Content-Transfer-Encoding: base64" + CRLF +
				"Content-Disposition: attachment; filename=\"r.pdf\"" + CRLF +
				CRLF +
				Base64.getMimeEncoder().encodeToString(data) + CRLF +
				"--=_Part_0--" + CRLF;
		
		assertEquals(expected, MailMessageSerializer.serialize(message));
	}
	
	@Test
	void serializeMessageWithAllRecipientTypes() {
		List<MailRecipient> recipients = List.of(
			MailRecipient.to(Mailbox.parse("t1@x.com")), MailRecipient.to(Mailbox.parse("t2@x.com")),
			MailRecipient.cc(Mailbox.parse("c1@x.com")), MailRecipient.bcc(Mailbox.parse("secret@x.com"))
		);
		MailMessage message = new MailMessage(ALICE, null, recipients, "Hello", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("To: t1@x.com, t2@x.com" + CRLF));
		assertTrue(output.contains("Cc: c1@x.com" + CRLF));
		assertFalse(output.contains("secret@x.com"));
	}
	
	@Test
	void serializeMessageWithUnicodeSubjectAndDisplayName() {
		Mailbox from = Mailbox.of("Grüße Team", MailAddress.parse("team@x.com"));
		MailMessage message = new MailMessage(from, null, List.of(MailRecipient.to(BOB)), "Café ☕", TextContent.of("Hi"), Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("From: " + encodedWord("Grüße Team") + " <team@x.com>" + CRLF));
		assertTrue(output.contains("Subject: " + encodedWord("Café ☕") + CRLF));
	}
	
	@Test
	void serializeMessageWithUnicodeTextBodyQuotedPrintable() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Grüße €"), Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("=C3=BC"));
		assertTrue(output.contains("=E2=82=AC"));
	}
	
	@Test
	void serializeMultipartAlternativeExact() {
		MailContent content = MultipartContent.alternative(TextContent.of("plain"), HtmlContent.of("<b>rich</b>"));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", content, Instant.EPOCH, null, List.of());
		String expected =
			"From: alice@example.com" + CRLF +
				"To: bob@example.com" + CRLF +
				"Subject: Hello" + CRLF +
				DATE +
				"MIME-Version: 1.0" + CRLF +
				"Content-Type: multipart/alternative; boundary=\"=_Part_0\"" + CRLF +
				CRLF +
				"--=_Part_0" + CRLF +
				"Content-Type: text/plain; charset=\"UTF-8\"" + CRLF +
				"Content-Transfer-Encoding: quoted-printable" + CRLF +
				CRLF +
				"plain" + CRLF +
				"--=_Part_0" + CRLF +
				"Content-Type: text/html; charset=\"UTF-8\"" + CRLF +
				"Content-Transfer-Encoding: quoted-printable" + CRLF +
				CRLF +
				"<b>rich</b>" + CRLF +
				"--=_Part_0--" + CRLF;
		
		assertEquals(expected, MailMessageSerializer.serialize(message));
	}
	
	@Test
	void serializeLongTextBodyTriggersSoftWrap() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("a".repeat(200)), Instant.EPOCH, null, List.of());
		String output = MailMessageSerializer.serialize(message);
		
		assertTrue(output.contains("=" + CRLF));
		for (String line : output.split(CRLF, -1)) {
			assertTrue(line.length() <= 76);
		}
	}
	
	@Test
	void serializeMessageWithMultipleCustomHeadersPreservesOrder() {
		List<MailHeader> headers = List.of(new MailHeader("X-A", "1"), new MailHeader("X-B", "2"), new MailHeader("X-C", "3"));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", TextContent.of("Hi"), Instant.EPOCH, null, headers);
		
		assertTrue(MailMessageSerializer.serialize(message).contains("MIME-Version: 1.0" + CRLF + "X-A: 1" + CRLF + "X-B: 2" + CRLF + "X-C: 3" + CRLF));
	}
}
