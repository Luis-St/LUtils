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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MailMessage}.<br>
 *
 * @author Luis-St
 */
class MailMessageTest {
	
	private static final Mailbox ALICE = Mailbox.parse("alice@example.com");
	private static final Mailbox BOB = Mailbox.parse("bob@example.com");
	private static final Mailbox CAROL = Mailbox.parse("carol@example.com");
	private static final MailContent CONTENT = TextContent.of("body");
	private static final MailHeader HEADER = new MailHeader("X-Test", "value");
	
	@Test
	void constructWithValidArguments() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		List<MailHeader> headers = List.of(HEADER);
		MailMessage message = new MailMessage(ALICE, CAROL, recipients, "Hello", CONTENT, Instant.EPOCH, "<id@example.com>", headers);
		
		assertEquals(ALICE, message.from());
		assertEquals(CAROL, message.replyTo());
		assertEquals("Hello", message.subject());
		assertEquals(CONTENT, message.content());
		assertEquals(Instant.EPOCH, message.date());
		assertEquals("<id@example.com>", message.messageId());
		assertEquals(recipients, message.recipients());
		assertEquals(headers, message.headers());
	}
	
	@Test
	void constructWithNullReplyTo() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", CONTENT, Instant.EPOCH, "<id@example.com>", List.of());
		
		assertNull(message.replyTo());
	}
	
	@Test
	void constructWithNullMessageId() {
		MailMessage message = new MailMessage(ALICE, CAROL, List.of(MailRecipient.to(BOB)), "Hello", CONTENT, Instant.EPOCH, null, List.of());
		
		assertNull(message.messageId());
	}
	
	@Test
	void constructWithNullFrom() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		assertThrows(NullPointerException.class, () -> new MailMessage(null, CAROL, recipients, "Hello", CONTENT, Instant.EPOCH, null, List.of()));
	}
	
	@Test
	void constructWithNullRecipients() {
		assertThrows(NullPointerException.class, () -> new MailMessage(ALICE, CAROL, null, "Hello", CONTENT, Instant.EPOCH, null, List.of()));
	}
	
	@Test
	void constructWithNullSubject() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		assertThrows(NullPointerException.class, () -> new MailMessage(ALICE, CAROL, recipients, null, CONTENT, Instant.EPOCH, null, List.of()));
	}
	
	@Test
	void constructWithNullContent() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		assertThrows(NullPointerException.class, () -> new MailMessage(ALICE, CAROL, recipients, "Hello", null, Instant.EPOCH, null, List.of()));
	}
	
	@Test
	void constructWithNullDate() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		assertThrows(NullPointerException.class, () -> new MailMessage(ALICE, CAROL, recipients, "Hello", CONTENT, null, null, List.of()));
	}
	
	@Test
	void constructWithNullHeaders() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		assertThrows(NullPointerException.class, () -> new MailMessage(ALICE, CAROL, recipients, "Hello", CONTENT, Instant.EPOCH, null, null));
	}
	
	@Test
	void builderReturnsNewInstance() {
		assertNotNull(MailMessage.builder());
		assertNotSame(MailMessage.builder(), MailMessage.builder());
	}
	
	@Test
	void constructWithEmptyRecipientsThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MailMessage(ALICE, null, List.of(), "Hello", CONTENT, Instant.EPOCH, null, List.of()));
	}
	
	@Test
	void constructWithSubjectContainingCarriageReturnThrows() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		assertThrows(IllegalArgumentException.class, () -> new MailMessage(ALICE, null, recipients, "Hel\rlo", CONTENT, Instant.EPOCH, null, List.of()));
	}
	
	@Test
	void constructWithSubjectContainingLineFeedThrows() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		assertThrows(IllegalArgumentException.class, () -> new MailMessage(ALICE, null, recipients, "Hel\nlo", CONTENT, Instant.EPOCH, null, List.of()));
	}
	
	@Test
	void constructWithSubjectContainingCrlfThrows() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		assertThrows(IllegalArgumentException.class, () -> new MailMessage(ALICE, null, recipients, "Hello\r\nWorld", CONTENT, Instant.EPOCH, null, List.of()));
	}
	
	@Test
	void constructWithSingleRecipientSucceeds() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", CONTENT, Instant.EPOCH, null, List.of());
		
		assertEquals(1, message.recipients().size());
	}
	
	@Test
	void constructWithCleanSubjectSucceeds() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Clean Subject", CONTENT, Instant.EPOCH, null, List.of());
		
		assertEquals("Clean Subject", message.subject());
	}
	
	@Test
	void constructWithEmptySubjectSucceeds() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "", CONTENT, Instant.EPOCH, null, List.of());
		
		assertEquals("", message.subject());
	}
	
	@Test
	void constructWithEmptyHeadersSucceeds() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", CONTENT, Instant.EPOCH, null, List.of());
		
		assertTrue(message.headers().isEmpty());
	}
	
	@Test
	void recipientsListIsDefensivelyCopied() {
		List<MailRecipient> source = new ArrayList<>(List.of(MailRecipient.to(BOB)));
		MailMessage message = new MailMessage(ALICE, null, source, "Hello", CONTENT, Instant.EPOCH, null, List.of());
		
		source.add(MailRecipient.cc(CAROL));
		
		assertEquals(1, message.recipients().size());
	}
	
	@Test
	void headersListIsDefensivelyCopied() {
		List<MailHeader> source = new ArrayList<>(List.of(HEADER));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", CONTENT, Instant.EPOCH, null, source);
		
		source.add(new MailHeader("X-Extra", "1"));
		
		assertEquals(1, message.headers().size());
	}
	
	@Test
	void recipientsAccessorReturnsUnmodifiableList() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", CONTENT, Instant.EPOCH, null, List.of());
		MailRecipient extra = MailRecipient.cc(CAROL);
		
		assertThrows(UnsupportedOperationException.class, () -> message.recipients().add(extra));
	}
	
	@Test
	void headersAccessorReturnsUnmodifiableList() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", CONTENT, Instant.EPOCH, null, List.of());
		MailHeader extra = new MailHeader("X-Extra", "1");
		
		assertThrows(UnsupportedOperationException.class, () -> message.headers().add(extra));
	}
	
	@Test
	void accessorsReturnConstructedValues() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		List<MailHeader> headers = List.of(HEADER);
		MailMessage message = new MailMessage(ALICE, CAROL, recipients, "Hello", CONTENT, Instant.EPOCH, "<id@example.com>", headers);
		
		assertEquals(ALICE, message.from());
		assertEquals(CAROL, message.replyTo());
		assertEquals(recipients, message.recipients());
		assertEquals("Hello", message.subject());
		assertEquals(CONTENT, message.content());
		assertEquals(Instant.EPOCH, message.date());
		assertEquals("<id@example.com>", message.messageId());
		assertEquals(headers, message.headers());
	}
	
	@Test
	void toRfc5322ReturnsNonNullSerialization() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", CONTENT, Instant.EPOCH, null, List.of());
		
		String wire = assertDoesNotThrow(message::toRfc5322);
		assertNotNull(wire);
		assertFalse(wire.isBlank());
	}
	
	@Test
	void equalMessagesAreEqual() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		MailMessage first = new MailMessage(ALICE, null, recipients, "Hello", CONTENT, Instant.EPOCH, "<id@example.com>", List.of());
		MailMessage second = new MailMessage(ALICE, null, recipients, "Hello", CONTENT, Instant.EPOCH, "<id@example.com>", List.of());
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void differentSubjectsAreNotEqual() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB));
		MailMessage first = new MailMessage(ALICE, null, recipients, "Hello", CONTENT, Instant.EPOCH, "<id@example.com>", List.of());
		MailMessage second = new MailMessage(ALICE, null, recipients, "Goodbye", CONTENT, Instant.EPOCH, "<id@example.com>", List.of());
		
		assertNotEquals(first, second);
	}
	
	@Test
	void toStringContainsComponents() {
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", CONTENT, Instant.EPOCH, null, List.of());
		String string = message.toString();
		
		assertNotNull(string);
		assertTrue(string.contains("Hello"));
	}
	
	@Test
	void constructWithMultipleRecipientTypes() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB), MailRecipient.cc(CAROL), MailRecipient.bcc(ALICE));
		MailMessage message = new MailMessage(ALICE, null, recipients, "Hello", CONTENT, Instant.EPOCH, null, List.of());
		
		assertEquals(3, message.recipients().size());
		assertEquals(MailRecipientType.TO, message.recipients().get(0).type());
		assertEquals(MailRecipientType.CC, message.recipients().get(1).type());
		assertEquals(MailRecipientType.BCC, message.recipients().get(2).type());
	}
	
	@Test
	void constructWithMultipleHeaders() {
		List<MailHeader> headers = List.of(new MailHeader("X-A", "1"), new MailHeader("X-B", "2"), new MailHeader("X-C", "3"));
		MailMessage message = new MailMessage(ALICE, null, List.of(MailRecipient.to(BOB)), "Hello", CONTENT, Instant.EPOCH, null, headers);
		
		assertEquals(3, message.headers().size());
		assertEquals(headers, message.headers());
	}
	
	@Test
	void constructFullMessageWithAllOptionalFields() {
		List<MailRecipient> recipients = List.of(MailRecipient.to(BOB), MailRecipient.cc(CAROL));
		List<MailHeader> headers = List.of(new MailHeader("X-A", "1"), new MailHeader("X-B", "2"));
		MailMessage message = new MailMessage(ALICE, CAROL, recipients, "Hello", CONTENT, Instant.EPOCH, "<id@example.com>", headers);
		
		assertEquals(CAROL, message.replyTo());
		assertEquals("<id@example.com>", message.messageId());
		assertFalse(message.toRfc5322().isBlank());
		assertThrows(UnsupportedOperationException.class, () -> message.recipients().add(MailRecipient.to(ALICE)));
		assertThrows(UnsupportedOperationException.class, () -> message.headers().add(HEADER));
	}
	
	@Test
	void defensiveCopyDoesNotShareReferenceWithSource() {
		List<MailRecipient> recipientSource = new ArrayList<>(List.of(MailRecipient.to(BOB), MailRecipient.cc(CAROL)));
		List<MailHeader> headerSource = new ArrayList<>(List.of(new MailHeader("X-A", "1"), new MailHeader("X-B", "2")));
		MailMessage message = new MailMessage(ALICE, null, recipientSource, "Hello", CONTENT, Instant.EPOCH, null, headerSource);
		
		recipientSource.add(MailRecipient.bcc(ALICE));
		headerSource.add(new MailHeader("X-C", "3"));
		
		assertEquals(2, message.recipients().size());
		assertEquals(2, message.headers().size());
	}
}
