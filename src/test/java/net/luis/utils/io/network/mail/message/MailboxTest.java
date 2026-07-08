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
 * Test class for {@link Mailbox}.<br>
 *
 * @author Luis-St
 */
class MailboxTest {
	
	private static final MailAddress ADDRESS = MailAddress.parse("john@example.com");
	
	@Test
	void constructWithDisplayNameAndAddress() {
		Mailbox mailbox = new Mailbox("John Doe", ADDRESS);
		assertEquals("John Doe", mailbox.displayName());
		assertEquals(ADDRESS, mailbox.address());
	}
	
	@Test
	void constructWithNullDisplayName() {
		Mailbox mailbox = new Mailbox(null, ADDRESS);
		assertNull(mailbox.displayName());
		assertEquals(ADDRESS, mailbox.address());
	}
	
	@Test
	void constructWithNullAddress() {
		assertThrows(NullPointerException.class, () -> new Mailbox("Name", null));
	}
	
	@Test
	void ofAddressCreatesMailboxWithoutDisplayName() {
		Mailbox mailbox = Mailbox.of(ADDRESS);
		assertNull(mailbox.displayName());
		assertEquals(ADDRESS, mailbox.address());
	}
	
	@Test
	void ofAddressWithNullAddress() {
		assertThrows(NullPointerException.class, () -> Mailbox.of(null));
	}
	
	@Test
	void ofDisplayNameAndAddress() {
		Mailbox mailbox = Mailbox.of("John Doe", ADDRESS);
		assertEquals("John Doe", mailbox.displayName());
		assertEquals(ADDRESS, mailbox.address());
	}
	
	@Test
	void ofDisplayNameWithNullDisplayName() {
		assertThrows(NullPointerException.class, () -> Mailbox.of(null, ADDRESS));
	}
	
	@Test
	void ofDisplayNameWithNullAddress() {
		assertThrows(NullPointerException.class, () -> Mailbox.of("Name", null));
	}
	
	@Test
	void parseBareAddress() {
		Mailbox mailbox = Mailbox.parse("john@example.com");
		assertNull(mailbox.displayName());
		assertEquals(MailAddress.parse("john@example.com"), mailbox.address());
	}
	
	@Test
	void parseNameAddr() {
		Mailbox mailbox = Mailbox.parse("John Doe <john@example.com>");
		assertEquals("John Doe", mailbox.displayName());
		assertEquals(MailAddress.parse("john@example.com"), mailbox.address());
	}
	
	@Test
	void parseWithNullMailbox() {
		assertThrows(NullPointerException.class, () -> Mailbox.parse(null));
	}
	
	@Test
	void constructWithDisplayNameContainingCarriageReturn() {
		assertThrows(IllegalArgumentException.class, () -> new Mailbox("John\rDoe", ADDRESS));
	}
	
	@Test
	void constructWithDisplayNameContainingLineFeed() {
		assertThrows(IllegalArgumentException.class, () -> new Mailbox("John\nDoe", ADDRESS));
	}
	
	@Test
	void ofWithDisplayNameContainingLineBreak() {
		assertThrows(IllegalArgumentException.class, () -> Mailbox.of("John\nDoe", ADDRESS));
	}
	
	@Test
	void parseMissingClosingBracket() {
		assertThrows(IllegalArgumentException.class, () -> Mailbox.parse("John <john@example.com"));
	}
	
	@Test
	void parseBareWithInvalidAddress() {
		assertThrows(IllegalArgumentException.class, () -> Mailbox.parse("not-an-address"));
	}
	
	@Test
	void parseNameAddrWithInvalidAddress() {
		assertThrows(IllegalArgumentException.class, () -> Mailbox.parse("John <not-an-address>"));
	}
	
	@Test
	void constructWithNonNullDisplayNameNoLineBreaks() {
		Mailbox mailbox = new Mailbox("Plain Name", ADDRESS);
		assertEquals("Plain Name", mailbox.displayName());
	}
	
	@Test
	void parseEmptyDisplayNameYieldsNull() {
		Mailbox mailbox = Mailbox.parse("<john@example.com>");
		assertNull(mailbox.displayName());
		assertEquals(MailAddress.parse("john@example.com"), mailbox.address());
	}
	
	@Test
	void parseNonEmptyDisplayNameUnquoted() {
		Mailbox mailbox = Mailbox.parse("John Doe <john@example.com>");
		assertEquals("John Doe", mailbox.displayName());
	}
	
	@Test
	void parseQuotedDisplayNameStripsQuotes() {
		Mailbox mailbox = Mailbox.parse("\"Doe, John\" <john@example.com>");
		assertEquals("Doe, John", mailbox.displayName());
	}
	
	@Test
	void parseEmptyQuotedDisplayNameStripsToEmpty() {
		Mailbox mailbox = Mailbox.parse("\"\" <john@example.com>");
		assertEquals("", mailbox.displayName());
		assertEquals(MailAddress.parse("john@example.com"), mailbox.address());
	}
	
	@Test
	void parseDisplayNameNotQuotedReturnedAsIs() {
		Mailbox mailbox = Mailbox.parse("John Doe <john@example.com>");
		assertEquals("John Doe", mailbox.displayName());
	}
	
	@Test
	void parseDisplayNameLeadingQuoteOnly() {
		Mailbox mailbox = Mailbox.parse("\"John <john@example.com>");
		assertEquals("\"John", mailbox.displayName());
	}
	
	@Test
	void parseDisplayNameSingleQuoteChar() {
		Mailbox mailbox = Mailbox.parse("\" <john@example.com>");
		assertEquals("\"", mailbox.displayName());
	}
	
	@Test
	void parseQuotedDisplayNameUnescapesQuoteAndBackslash() {
		Mailbox mailbox = Mailbox.parse("\"Doe \\\"JD\\\" \\\\x\" <john@example.com>");
		assertEquals("Doe \"JD\" \\x", mailbox.displayName());
	}
	
	@Test
	void toStringWithoutDisplayNameRendersBareAddress() {
		Mailbox mailbox = Mailbox.of(MailAddress.parse("john@example.com"));
		assertEquals("john@example.com", mailbox.toString());
	}
	
	@Test
	void toStringWithDisplayNameRendersNameAddr() {
		Mailbox mailbox = Mailbox.of("John Doe", MailAddress.parse("john@example.com"));
		assertEquals("John Doe <john@example.com>", mailbox.toString());
	}
	
	@Test
	void toStringWithSpecialCharsQuotesDisplayName() {
		Mailbox mailbox = Mailbox.of("Doe, John", MailAddress.parse("john@example.com"));
		assertEquals("\"Doe, John\" <john@example.com>", mailbox.toString());
	}
	
	@Test
	void toStringWithNonAsciiEncodesDisplayName() {
		Mailbox mailbox = Mailbox.of("Jörg", MailAddress.parse("john@example.com"));
		String string = mailbox.toString();
		assertTrue(string.startsWith("=?UTF-8?B?"));
		assertTrue(string.endsWith("?= <john@example.com>"));
	}
	
	@Test
	void accessorsReturnComponents() {
		Mailbox mailbox = new Mailbox("John Doe", ADDRESS);
		assertEquals("John Doe", mailbox.displayName());
		assertEquals(ADDRESS, mailbox.address());
	}
	
	@Test
	void equalsAndHashCodeForEqualMailboxes() {
		Mailbox first = new Mailbox("John Doe", MailAddress.parse("john@example.com"));
		Mailbox second = new Mailbox("John Doe", MailAddress.parse("john@example.com"));
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void notEqualForDifferentDisplayName() {
		Mailbox first = new Mailbox("John Doe", ADDRESS);
		Mailbox second = new Mailbox("Jane Doe", ADDRESS);
		assertNotEquals(first, second);
	}
	
	@Test
	void notEqualForDifferentAddress() {
		Mailbox first = new Mailbox("John Doe", MailAddress.parse("john@example.com"));
		Mailbox second = new Mailbox("John Doe", MailAddress.parse("jane@example.com"));
		assertNotEquals(first, second);
	}
	
	@Test
	void toStringPlainAsciiName() {
		Mailbox mailbox = Mailbox.of("Alice", MailAddress.parse("alice@example.com"));
		assertEquals("Alice <alice@example.com>", mailbox.toString());
	}
	
	@Test
	void parseRoundTripBareAddress() {
		Mailbox mailbox = Mailbox.of(MailAddress.parse("john@example.com"));
		assertEquals(mailbox, Mailbox.parse(mailbox.toString()));
	}
	
	@Test
	void parseRoundTripPlainNameAddr() {
		Mailbox mailbox = Mailbox.of("John Doe", MailAddress.parse("john@example.com"));
		assertEquals(mailbox, Mailbox.parse(mailbox.toString()));
	}
	
	@Test
	void parseRoundTripQuotedDisplayName() {
		Mailbox mailbox = Mailbox.of("Doe, John", MailAddress.parse("john@example.com"));
		assertEquals(mailbox, Mailbox.parse(mailbox.toString()));
	}
	
	@Test
	void parseRoundTripEscapedQuotesAndBackslash() {
		Mailbox mailbox = Mailbox.of("Doe \"JD\" \\x", MailAddress.parse("john@example.com"));
		assertEquals(mailbox, Mailbox.parse(mailbox.toString()));
	}
	
	@Test
	void parseTrimsWhitespaceAroundComponents() {
		Mailbox mailbox = Mailbox.parse("  John Doe  <  john@example.com  >  ");
		assertEquals("John Doe", mailbox.displayName());
		assertEquals(MailAddress.parse("john@example.com"), mailbox.address());
	}
	
	@Test
	void toStringWithBackslashInNameEscapesAndQuotes() {
		Mailbox mailbox = Mailbox.of("a\\b", MailAddress.parse("john@example.com"));
		assertEquals("\"a\\\\b\" <john@example.com>", mailbox.toString());
	}
}
