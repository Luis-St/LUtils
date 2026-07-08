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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MailMessageBuilder}.<br>
 *
 * @author Luis-St
 */
class MailMessageBuilderTest {
	
	private static final Mailbox ALICE = Mailbox.of(MailAddress.of("alice", "example.com"));
	private static final Mailbox BOB = Mailbox.of(MailAddress.of("bob", "example.com"));
	private static final Mailbox CAROL = Mailbox.of(MailAddress.of("carol", "example.com"));
	private static final Mailbox DAVE = Mailbox.of(MailAddress.of("dave", "example.com"));
	private static final TextContent CONTENT = TextContent.of("body");
	private static final MailAttachment ATTACH = MailAttachment.of("a.txt", new byte[] { 1, 2, 3 });
	
	private static MailMessageBuilder valid() {
		return new MailMessageBuilder().from(ALICE).subject("Subject").content(CONTENT).to(BOB);
	}
	
	@Test
	void constructEmptyBuilder() {
		assertNotNull(new MailMessageBuilder());
	}
	
	@Test
	void constructViaFactory() {
		assertNotNull(MailMessage.builder());
	}
	
	@Test
	void fromWithNull() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.from(null));
	}
	
	@Test
	void subjectWithNull() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.subject(null));
	}
	
	@Test
	void contentWithNull() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.content(null));
	}
	
	@Test
	void dateWithNull() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.date(null));
	}
	
	@Test
	void messageIdWithNull() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.messageId(null));
	}
	
	@Test
	void recipientWithNull() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.recipient(null));
	}
	
	@Test
	void toWithNull() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.to(null));
	}
	
	@Test
	void ccWithNull() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.cc(null));
	}
	
	@Test
	void bccWithNull() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.bcc(null));
	}
	
	@Test
	void headerWithNullHeader() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.header(null));
	}
	
	@Test
	void headerWithNullName() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.header(null, "v"));
	}
	
	@Test
	void headerWithNullValue() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.header("X-Test", null));
	}
	
	@Test
	void headerWithEmptyName() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(IllegalArgumentException.class, () -> builder.header("", "v"));
	}
	
	@Test
	void headerWithValueLineBreak() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(IllegalArgumentException.class, () -> builder.header("X-Test", "a\r\nb"));
	}
	
	@Test
	void attachWithNull() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertThrows(NullPointerException.class, () -> builder.attach(null));
	}
	
	@Test
	void buildWithoutFrom() {
		MailMessageBuilder builder = new MailMessageBuilder().subject("Subject").content(CONTENT).to(BOB);
		assertThrows(NullPointerException.class, builder::build);
	}
	
	@Test
	void buildWithoutSubject() {
		MailMessageBuilder builder = new MailMessageBuilder().from(ALICE).content(CONTENT).to(BOB);
		assertThrows(NullPointerException.class, builder::build);
	}
	
	@Test
	void buildWithoutContent() {
		MailMessageBuilder builder = new MailMessageBuilder().from(ALICE).subject("Subject").to(BOB);
		assertThrows(NullPointerException.class, builder::build);
	}
	
	@Test
	void buildWithoutRecipients() {
		MailMessageBuilder builder = new MailMessageBuilder().from(ALICE).subject("Subject").content(CONTENT);
		assertThrows(IllegalArgumentException.class, builder::build);
	}
	
	@Test
	void fromReturnsSameBuilder() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertSame(builder, builder.from(ALICE));
	}
	
	@Test
	void replyToWithMailbox() {
		MailMessageBuilder builder = valid();
		
		assertSame(builder, builder.replyTo(CAROL));
		assertEquals(CAROL, builder.build().replyTo());
	}
	
	@Test
	void replyToWithNull() {
		MailMessageBuilder builder = valid();
		
		assertSame(builder, builder.replyTo(null));
		MailMessage message = assertDoesNotThrow(builder::build);
		assertNull(message.replyTo());
	}
	
	@Test
	void subjectStored() {
		MailMessageBuilder builder = valid();
		
		assertSame(builder, builder.subject("Custom"));
		assertEquals("Custom", builder.build().subject());
	}
	
	@Test
	void contentStoredWithoutAttachment() {
		MailMessageBuilder builder = new MailMessageBuilder().from(ALICE).subject("Subject").to(BOB);
		
		assertSame(builder, builder.content(CONTENT));
		assertSame(CONTENT, builder.build().content());
	}
	
	@Test
	void dateStoredBranch() {
		MailMessage message = valid().date(Instant.EPOCH).build();
		
		assertEquals(Instant.EPOCH, message.date());
	}
	
	@Test
	void dateDefaultedBranch() {
		Instant before = Instant.now();
		MailMessage message = valid().build();
		Instant after = Instant.now();
		
		assertNotNull(message.date());
		assertFalse(message.date().isBefore(before));
		assertFalse(message.date().isAfter(after));
	}
	
	@Test
	void messageIdStoredBranch() {
		MailMessage message = valid().messageId("<custom@id>").build();
		
		assertEquals("<custom@id>", message.messageId());
	}
	
	@Test
	void messageIdGeneratedBranch() {
		MailMessage message = valid().build();
		String messageId = message.messageId();
		
		assertNotNull(messageId);
		assertTrue(messageId.startsWith("<"));
		assertTrue(messageId.contains("@example.com>"));
		assertTrue(messageId.endsWith(">"));
	}
	
	@Test
	void recipientAppendsToEmptyCollection() {
		MailMessageBuilder builder = new MailMessageBuilder().from(ALICE).subject("Subject").content(CONTENT);
		MailRecipient recipient = MailRecipient.to(BOB);
		
		assertSame(builder, builder.recipient(recipient));
		MailMessage message = builder.build();
		assertEquals(1, message.recipients().size());
		assertTrue(message.recipients().contains(recipient));
	}
	
	@Test
	void recipientAppendsToExistingCollection() {
		MailMessage message = new MailMessageBuilder().from(ALICE).subject("Subject").content(CONTENT)
			.recipient(MailRecipient.to(BOB)).recipient(MailRecipient.cc(CAROL)).build();
		
		assertEquals(2, message.recipients().size());
		assertEquals(MailRecipient.to(BOB), message.recipients().get(0));
		assertEquals(MailRecipient.cc(CAROL), message.recipients().get(1));
	}
	
	@Test
	void toAddsToRecipient() {
		MailMessage message = new MailMessageBuilder().from(ALICE).subject("Subject").content(CONTENT).to(BOB).build();
		
		assertEquals(1, message.recipients().size());
		assertEquals(MailRecipientType.TO, message.recipients().getFirst().type());
		assertEquals(BOB, message.recipients().getFirst().mailbox());
	}
	
	@Test
	void ccAddsCcRecipient() {
		MailMessage message = new MailMessageBuilder().from(ALICE).subject("Subject").content(CONTENT).cc(CAROL).build();
		
		assertEquals(MailRecipientType.CC, message.recipients().getFirst().type());
		assertEquals(CAROL, message.recipients().getFirst().mailbox());
	}
	
	@Test
	void bccAddsBccRecipient() {
		MailMessage message = new MailMessageBuilder().from(ALICE).subject("Subject").content(CONTENT).bcc(DAVE).build();
		
		assertEquals(MailRecipientType.BCC, message.recipients().getFirst().type());
		assertEquals(DAVE, message.recipients().getFirst().mailbox());
	}
	
	@Test
	void headerAppendsToEmptyCollection() {
		MailMessageBuilder builder = valid();
		MailHeader header = new MailHeader("X-A", "1");
		
		assertSame(builder, builder.header(header));
		MailMessage message = builder.build();
		assertEquals(1, message.headers().size());
		assertTrue(message.headers().contains(header));
	}
	
	@Test
	void headerAppendsToExistingCollection() {
		MailMessage message = valid().header(new MailHeader("X-A", "1")).header(new MailHeader("X-B", "2")).build();
		
		assertEquals(2, message.headers().size());
		assertEquals(new MailHeader("X-A", "1"), message.headers().get(0));
		assertEquals(new MailHeader("X-B", "2"), message.headers().get(1));
	}
	
	@Test
	void headerFromNameValue() {
		MailMessage message = valid().header("X-A", "1").build();
		
		assertTrue(message.headers().contains(new MailHeader("X-A", "1")));
	}
	
	@Test
	void attachWrapsContentBranch() {
		MailMessage message = valid().attach(ATTACH).build();
		
		assertInstanceOf(MultipartContent.class, message.content());
		assertTrue(message.content().contentType().startsWith("multipart/mixed"));
		assertNotSame(CONTENT, message.content());
	}
	
	@Test
	void attachAppendsToEmptyCollection() {
		MailMessage message = valid().attach(ATTACH).build();
		
		assertInstanceOf(MultipartContent.class, message.content());
		MultipartContent multipart = (MultipartContent) message.content();
		assertEquals(2, multipart.parts().size());
	}
	
	@Test
	void attachMultipleAppendsToExistingCollection() {
		MailMessage message = valid().attach(ATTACH).attach(MailAttachment.of("b.txt", new byte[] { 4, 5 })).build();
		
		assertInstanceOf(MultipartContent.class, message.content());
		MultipartContent multipart = (MultipartContent) message.content();
		assertEquals(3, multipart.parts().size());
	}
	
	@Test
	void attachReturnsSameBuilder() {
		MailMessageBuilder builder = new MailMessageBuilder();
		assertSame(builder, builder.attach(ATTACH));
	}
	
	@Test
	void buildMinimalMessage() {
		MailMessage message = valid().build();
		
		assertNotNull(message);
		assertEquals(ALICE, message.from());
		assertEquals("Subject", message.subject());
		assertSame(CONTENT, message.content());
		assertEquals(1, message.recipients().size());
		assertNotNull(message.date());
		assertNotNull(message.messageId());
	}
	
	@Test
	void buildWithReplyTo() {
		MailMessage message = valid().replyTo(CAROL).build();
		
		assertEquals(CAROL, message.replyTo());
	}
	
	@Test
	void buildWithExplicitDateAndId() {
		MailMessage message = valid().date(Instant.EPOCH).messageId("<x@y>").build();
		
		assertEquals(Instant.EPOCH, message.date());
		assertEquals("<x@y>", message.messageId());
	}
	
	@Test
	void buildWithSingleHeader() {
		MailMessage message = valid().header(new MailHeader("X-A", "1")).build();
		
		assertEquals(1, message.headers().size());
	}
	
	@Test
	void buildWithToCcBcc() {
		MailMessage message = new MailMessageBuilder().from(ALICE).subject("Subject").content(CONTENT)
			.to(BOB).cc(CAROL).bcc(DAVE).build();
		
		assertEquals(3, message.recipients().size());
		assertEquals(MailRecipientType.TO, message.recipients().get(0).type());
		assertEquals(MailRecipientType.CC, message.recipients().get(1).type());
		assertEquals(MailRecipientType.BCC, message.recipients().get(2).type());
	}
	
	@Test
	void buildWithSingleAttachment() {
		MailMessage message = valid().attach(ATTACH).build();
		
		assertInstanceOf(MultipartContent.class, message.content());
	}
	
	@Test
	void messageIdUsesFromDomain() {
		MailMessage message = new MailMessageBuilder().from(Mailbox.parse("bob@sub.example.org")).subject("Subject").content(CONTENT).to(BOB).build();
		
		assertTrue(message.messageId().contains("@sub.example.org>"));
	}
	
	@Test
	void buildFullMessage() {
		MailMessage message = new MailMessageBuilder()
			.from(ALICE).replyTo(CAROL).subject("Subject").content(CONTENT)
			.date(Instant.EPOCH).messageId("<x@y>")
			.to(BOB).cc(CAROL).bcc(DAVE)
			.header(new MailHeader("X-A", "1")).header(new MailHeader("X-B", "2"))
			.attach(ATTACH).attach(MailAttachment.of("b.txt", new byte[] { 4, 5 }))
			.build();
		
		assertEquals(ALICE, message.from());
		assertEquals(CAROL, message.replyTo());
		assertEquals(Instant.EPOCH, message.date());
		assertEquals("<x@y>", message.messageId());
		assertEquals(3, message.recipients().size());
		assertEquals(2, message.headers().size());
		assertInstanceOf(MultipartContent.class, message.content());
	}
	
	@Test
	void methodChainingConsistency() {
		MailMessageBuilder builder = new MailMessageBuilder();
		
		assertSame(builder, builder.from(ALICE));
		assertSame(builder, builder.subject("Subject"));
		assertSame(builder, builder.content(CONTENT));
		assertSame(builder, builder.to(BOB));
		assertSame(builder, builder.cc(CAROL));
		assertSame(builder, builder.header(new MailHeader("X-A", "1")));
		assertSame(builder, builder.attach(ATTACH));
		assertDoesNotThrow(builder::build);
	}
	
	@Test
	void builderReuseAfterBuild() {
		MailMessageBuilder builder = valid();
		
		MailMessage first = builder.build();
		assertEquals(1, first.recipients().size());
		
		builder.cc(CAROL);
		MailMessage second = builder.build();
		assertEquals(2, second.recipients().size());
		assertEquals(1, first.recipients().size());
	}
	
	@Test
	void builderReuseAccumulatesAttachments() {
		MailMessageBuilder builder = valid();
		
		MailMessage first = builder.build();
		assertSame(CONTENT, first.content());
		
		builder.attach(ATTACH);
		MailMessage second = builder.build();
		assertInstanceOf(MultipartContent.class, second.content());
	}
	
	@Test
	void multipleRecipientsPreserveOrder() {
		MailMessage message = new MailMessageBuilder().from(ALICE).subject("Subject").content(CONTENT)
			.recipient(MailRecipient.to(BOB)).to(CAROL).cc(DAVE).bcc(ALICE).build();
		
		assertEquals(4, message.recipients().size());
		assertEquals(MailRecipientType.TO, message.recipients().get(0).type());
		assertEquals(MailRecipientType.TO, message.recipients().get(1).type());
		assertEquals(MailRecipientType.CC, message.recipients().get(2).type());
		assertEquals(MailRecipientType.BCC, message.recipients().get(3).type());
	}
	
	@Test
	void multipleToRecipients() {
		MailMessage message = new MailMessageBuilder().from(ALICE).subject("Subject").content(CONTENT)
			.to(BOB).to(CAROL).to(DAVE).build();
		
		assertEquals(3, message.recipients().size());
		assertTrue(message.recipients().stream().allMatch(recipient -> recipient.type() == MailRecipientType.TO));
	}
	
	@Test
	void builderReuseAccumulatesHeaders() {
		MailMessageBuilder builder = valid();
		
		builder.header(new MailHeader("X-A", "1"));
		MailMessage first = builder.build();
		assertEquals(1, first.headers().size());
		
		builder.header(new MailHeader("X-B", "2"));
		MailMessage second = builder.build();
		assertEquals(1, first.headers().size());
		assertEquals(2, second.headers().size());
	}
}
