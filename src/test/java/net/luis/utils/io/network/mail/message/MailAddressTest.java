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
 * Test class for {@link MailAddress}.<br>
 *
 * @author Luis-St
 */
class MailAddressTest {
	
	@Test
	void constructWithValidParts() {
		MailAddress address = new MailAddress("john.doe", "example.com");
		assertEquals("john.doe", address.localPart());
		assertEquals("example.com", address.domain());
	}
	
	@Test
	void constructWithNullLocalPart() {
		assertThrows(NullPointerException.class, () -> new MailAddress(null, "example.com"));
	}
	
	@Test
	void constructWithNullDomain() {
		assertThrows(NullPointerException.class, () -> new MailAddress("john", null));
	}
	
	@Test
	void ofCreatesValidAddress() {
		MailAddress address = MailAddress.of("john", "example.com");
		assertEquals(new MailAddress("john", "example.com"), address);
		assertEquals("john", address.localPart());
		assertEquals("example.com", address.domain());
	}
	
	@Test
	void ofWithNullLocalPart() {
		assertThrows(NullPointerException.class, () -> MailAddress.of(null, "example.com"));
	}
	
	@Test
	void ofWithNullDomain() {
		assertThrows(NullPointerException.class, () -> MailAddress.of("john", null));
	}
	
	@Test
	void parseValidAddress() {
		MailAddress address = MailAddress.parse("john.doe@example.com");
		assertEquals("john.doe", address.localPart());
		assertEquals("example.com", address.domain());
	}
	
	@Test
	void parseWithNullAddress() {
		assertThrows(NullPointerException.class, () -> MailAddress.parse(null));
	}
	
	@Test
	void constructWithEmptyLocalPart() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("", "example.com"));
	}
	
	@Test
	void constructWithEmptyDomain() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("john", ""));
	}
	
	@Test
	void constructWithIllegalCharInLocalPart() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("jo hn", "example.com"));
	}
	
	@Test
	void constructWithIllegalCharInDomain() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("john", "exa mple.com"));
	}
	
	@Test
	void constructWithAtSignInLocalPart() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("jo@hn", "example.com"));
	}
	
	@Test
	void constructWithAtSignInDomain() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("john", "exa@mple.com"));
	}
	
	@Test
	void ofWithEmptyLocalPart() {
		assertThrows(IllegalArgumentException.class, () -> MailAddress.of("", "example.com"));
	}
	
	@Test
	void ofWithIllegalCharacter() {
		assertThrows(IllegalArgumentException.class, () -> MailAddress.of("jo hn", "example.com"));
	}
	
	@Test
	void parseWithoutAtSign() {
		assertThrows(IllegalArgumentException.class, () -> MailAddress.parse("johnexample.com"));
	}
	
	@Test
	void parseWithMultipleAtSigns() {
		assertThrows(IllegalArgumentException.class, () -> MailAddress.parse("john@doe@example.com"));
	}
	
	@Test
	void parseWithEmptyString() {
		assertThrows(IllegalArgumentException.class, () -> MailAddress.parse(""));
	}
	
	@Test
	void parseWithEmptyLocalPart() {
		assertThrows(IllegalArgumentException.class, () -> MailAddress.parse("@example.com"));
	}
	
	@Test
	void parseWithEmptyDomain() {
		assertThrows(IllegalArgumentException.class, () -> MailAddress.parse("john@"));
	}
	
	@Test
	void parseWithBlankAddress() {
		assertThrows(IllegalArgumentException.class, () -> MailAddress.parse(" @ "));
	}
	
	@Test
	void validatePartEmptyBranchTrue() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new MailAddress("", "example.com"));
		assertTrue(exception.getMessage().contains("must not be empty"));
	}
	
	@Test
	void validatePartEmptyBranchFalse() {
		MailAddress address = new MailAddress("a", "b");
		assertEquals("a", address.localPart());
		assertEquals("b", address.domain());
	}
	
	@Test
	void validatePartRejectsSpace() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("a b", "example.com"));
	}
	
	@Test
	void validatePartRejectsControlChar() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("a\tb", "example.com"));
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("a\nb", "example.com"));
	}
	
	@Test
	void validatePartRejectsNullChar() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("a\0b", "example.com"));
	}
	
	@Test
	void validatePartRejectsDelChar() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("ab", "example.com"));
	}
	
	@Test
	void validatePartRejectsAtSign() {
		assertThrows(IllegalArgumentException.class, () -> new MailAddress("a@b", "example.com"));
	}
	
	@Test
	void validatePartAcceptsPrintableAscii() {
		MailAddress address = new MailAddress("a!#$%&'*+-/=?^_`{|}~.b", "example.com");
		assertEquals("a!#$%&'*+-/=?^_`{|}~.b", address.localPart());
	}
	
	@Test
	void validatePartBoundaryAcceptsExclamation() {
		MailAddress address = new MailAddress("!", "example.com");
		assertEquals("!", address.localPart());
	}
	
	@Test
	void parseAtLessThanZeroTrue() {
		assertThrows(IllegalArgumentException.class, () -> MailAddress.parse("noatsign"));
	}
	
	@Test
	void parseSecondOperandTrue() {
		assertThrows(IllegalArgumentException.class, () -> MailAddress.parse("a@b@c"));
	}
	
	@Test
	void parseBothOperandsFalse() {
		MailAddress address = MailAddress.parse("a@b");
		assertEquals("a", address.localPart());
		assertEquals("b", address.domain());
	}
	
	@Test
	void parseSingleCharParts() {
		MailAddress address = MailAddress.parse("a@b");
		assertEquals("a", address.localPart());
		assertEquals("b", address.domain());
	}
	
	@Test
	void accessorsReturnComponents() {
		MailAddress address = MailAddress.of("john", "example.com");
		assertEquals("john", address.localPart());
		assertEquals("example.com", address.domain());
	}
	
	@Test
	void toStringJoinsWithAt() {
		assertEquals("john@example.com", MailAddress.of("john", "example.com").toString());
	}
	
	@Test
	void toStringMatchesParseInput() {
		assertEquals("john.doe@example.com", MailAddress.parse("john.doe@example.com").toString());
	}
	
	@Test
	void equalsSameComponents() {
		assertEquals(MailAddress.of("john", "example.com"), MailAddress.of("john", "example.com"));
	}
	
	@Test
	void equalsDifferentLocalPart() {
		assertNotEquals(MailAddress.of("a", "x.com"), MailAddress.of("b", "x.com"));
	}
	
	@Test
	void equalsDifferentDomain() {
		assertNotEquals(MailAddress.of("a", "x.com"), MailAddress.of("a", "y.com"));
	}
	
	@Test
	void hashCodeConsistentWithEquals() {
		assertEquals(MailAddress.of("john", "example.com").hashCode(), MailAddress.of("john", "example.com").hashCode());
	}
	
	@Test
	void equalsWithNullReturnsFalse() {
		assertNotEquals(null, MailAddress.of("john", "example.com"));
	}
	
	@Test
	void equalsWithDifferentTypeReturnsFalse() {
		assertNotEquals("john@example.com", MailAddress.of("john", "example.com"));
	}
	
	@Test
	void parseSubaddressingPlusTag() {
		MailAddress address = MailAddress.parse("john.doe+newsletter@example.com");
		assertEquals("john.doe+newsletter", address.localPart());
		assertEquals("example.com", address.domain());
	}
	
	@Test
	void constructWithDottedLocalAndSubdomain() {
		MailAddress address = MailAddress.of("first.last", "mail.corp.example.co.uk");
		assertEquals("first.last", address.localPart());
		assertEquals("mail.corp.example.co.uk", address.domain());
		assertEquals("first.last@mail.corp.example.co.uk", address.toString());
	}
	
	@Test
	void parseLocalPartWithSpecialSymbols() {
		MailAddress address = MailAddress.of("user!#$%&'*+-/=?^_`{|}~", "example.com");
		assertEquals("user!#$%&'*+-/=?^_`{|}~", address.localPart());
	}
	
	@Test
	void constructRoundTripThroughParseAndToString() {
		MailAddress address = MailAddress.of("john+tag", "sub.example.com");
		assertEquals(address, MailAddress.parse(address.toString()));
	}
	
	@Test
	void parseWithLeadingDotLocalPart() {
		MailAddress address = MailAddress.parse(".john@example.com");
		assertEquals(".john", address.localPart());
		assertEquals("example.com", address.domain());
	}
	
	@Test
	void parseInternationalizedDomainAscii() {
		MailAddress address = MailAddress.of("john", "exämple.com");
		assertEquals("exämple.com", address.domain());
	}
}
