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
 * Test class for {@link MailRecipient}.<br>
 *
 * @author Luis-St
 */
class MailRecipientTest {
	
	private static final Mailbox MAILBOX = Mailbox.parse("john@example.com");
	private static final Mailbox OTHER_MAILBOX = Mailbox.parse("jane@example.com");
	
	@Test
	void constructWithMailboxAndType() {
		MailRecipient recipient = new MailRecipient(MAILBOX, MailRecipientType.CC);
		assertNotNull(recipient);
		assertEquals(MAILBOX, recipient.mailbox());
		assertEquals(MailRecipientType.CC, recipient.type());
	}
	
	@Test
	void constructWithNullMailbox() {
		assertThrows(NullPointerException.class, () -> new MailRecipient(null, MailRecipientType.TO));
	}
	
	@Test
	void constructWithNullType() {
		assertThrows(NullPointerException.class, () -> new MailRecipient(MAILBOX, null));
	}
	
	@Test
	void toWithNullMailbox() {
		assertThrows(NullPointerException.class, () -> MailRecipient.to(null));
	}
	
	@Test
	void ccWithNullMailbox() {
		assertThrows(NullPointerException.class, () -> MailRecipient.cc(null));
	}
	
	@Test
	void bccWithNullMailbox() {
		assertThrows(NullPointerException.class, () -> MailRecipient.bcc(null));
	}
	
	@Test
	void toSetsTypeToTo() {
		MailRecipient recipient = MailRecipient.to(MAILBOX);
		assertNotNull(recipient);
		assertEquals(MailRecipientType.TO, recipient.type());
		assertEquals(MAILBOX, recipient.mailbox());
	}
	
	@Test
	void ccSetsTypeToCc() {
		MailRecipient recipient = MailRecipient.cc(MAILBOX);
		assertEquals(MailRecipientType.CC, recipient.type());
		assertEquals(MAILBOX, recipient.mailbox());
	}
	
	@Test
	void bccSetsTypeToBcc() {
		MailRecipient recipient = MailRecipient.bcc(MAILBOX);
		assertEquals(MailRecipientType.BCC, recipient.type());
		assertEquals(MAILBOX, recipient.mailbox());
	}
	
	@Test
	void accessorsReturnComponents() {
		MailRecipient recipient = new MailRecipient(MAILBOX, MailRecipientType.BCC);
		assertEquals(MAILBOX, recipient.mailbox());
		assertEquals(MailRecipientType.BCC, recipient.type());
	}
	
	@Test
	void factoriesProduceDistinctTypesForSameMailbox() {
		MailRecipient to = MailRecipient.to(MAILBOX);
		MailRecipient cc = MailRecipient.cc(MAILBOX);
		MailRecipient bcc = MailRecipient.bcc(MAILBOX);
		assertEquals(MailRecipientType.TO, to.type());
		assertEquals(MailRecipientType.CC, cc.type());
		assertEquals(MailRecipientType.BCC, bcc.type());
		assertNotEquals(to, cc);
		assertNotEquals(cc, bcc);
		assertNotEquals(to, bcc);
	}
	
	@Test
	void equalsAndHashCodeForEqualRecipients() {
		MailRecipient first = MailRecipient.to(MAILBOX);
		MailRecipient second = MailRecipient.to(MAILBOX);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void equalsDiffersByType() {
		assertNotEquals(MailRecipient.to(MAILBOX), MailRecipient.cc(MAILBOX));
	}
	
	@Test
	void equalsDiffersByMailbox() {
		assertNotEquals(MailRecipient.to(MAILBOX), MailRecipient.to(OTHER_MAILBOX));
	}
	
	@Test
	void toStringContainsComponents() {
		String string = MailRecipient.to(MAILBOX).toString();
		assertNotNull(string);
		assertTrue(string.contains("john@example.com"));
		assertTrue(string.contains("TO"));
	}
	
	@Test
	void equalsDiffersFromNullAndOtherType() {
		MailRecipient recipient = MailRecipient.to(MAILBOX);
		assertNotEquals(null, recipient);
		assertNotEquals("not a recipient", recipient);
	}
	
	@Test
	void recipientWithDisplayNameMailbox() {
		Mailbox mailbox = Mailbox.of("John Doe", MailAddress.parse("john@example.com"));
		MailRecipient recipient = MailRecipient.cc(mailbox);
		assertEquals("John Doe", recipient.mailbox().displayName());
		assertEquals(MailRecipientType.CC, recipient.type());
	}
	
	@Test
	void equalityConsistentAcrossFactoryAndConstructor() {
		MailRecipient factory = MailRecipient.bcc(MAILBOX);
		MailRecipient constructed = new MailRecipient(MAILBOX, MailRecipientType.BCC);
		assertEquals(factory, constructed);
		assertEquals(factory.hashCode(), constructed.hashCode());
	}
}
