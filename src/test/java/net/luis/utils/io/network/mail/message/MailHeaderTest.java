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
 * Test class for {@link MailHeader}.<br>
 *
 * @author Luis-St
 */
class MailHeaderTest {
	
	@Test
	void constructWithValidNameAndValue() {
		MailHeader header = assertDoesNotThrow(() -> new MailHeader("X-Priority", "1"));
		assertEquals("X-Priority", header.name());
		assertEquals("1", header.value());
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new MailHeader(null, "1"));
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new MailHeader("X-Priority", null));
	}
	
	@Test
	void constructWithEmptyNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MailHeader("", "1"));
	}
	
	@Test
	void constructWithNameContainingColonThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MailHeader("X:Y", "1"));
	}
	
	@Test
	void constructWithNameContainingSpaceThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MailHeader("X Y", "1"));
	}
	
	@Test
	void constructWithNameContainingControlCharThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MailHeader("X\tY", "1"));
	}
	
	@Test
	void constructWithNameContainingNonAsciiThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MailHeader("Xé", "1"));
	}
	
	@Test
	void constructWithNameContainingDelCharThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MailHeader("XY", "1"));
	}
	
	@Test
	void constructWithValueContainingCarriageReturnThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MailHeader("X-A", "a\rb"));
	}
	
	@Test
	void constructWithValueContainingLineFeedThrows() {
		assertThrows(IllegalArgumentException.class, () -> new MailHeader("X-A", "a\nb"));
	}
	
	@Test
	void constructWithValidNameRunsLoopWithoutThrow() {
		MailHeader header = assertDoesNotThrow(() -> new MailHeader("!~AZ09-_", "v"));
		assertEquals("!~AZ09-_", header.name());
	}
	
	@Test
	void constructWithBoundaryLegalNameChars() {
		assertDoesNotThrow(() -> new MailHeader("!~", "v"));
	}
	
	@Test
	void constructWithValueWithoutLineBreaks() {
		MailHeader header = assertDoesNotThrow(() -> new MailHeader("X-A", "plain value 123"));
		assertEquals("plain value 123", header.value());
	}
	
	@Test
	void constructWithEmptyValueSucceeds() {
		MailHeader header = assertDoesNotThrow(() -> new MailHeader("X", ""));
		assertEquals("", header.value());
	}
	
	@Test
	void constructWithSingleCharValidName() {
		MailHeader header = assertDoesNotThrow(() -> new MailHeader("A", "v"));
		assertEquals("A", header.name());
	}
	
	@Test
	void nameAccessorReturnsName() {
		MailHeader header = new MailHeader("Subject", "Hello");
		assertEquals("Subject", header.name());
	}
	
	@Test
	void valueAccessorReturnsValue() {
		MailHeader header = new MailHeader("Subject", "Hello");
		assertEquals("Hello", header.value());
	}
	
	@Test
	void equalsSameComponents() {
		assertEquals(new MailHeader("A", "b"), new MailHeader("A", "b"));
	}
	
	@Test
	void equalsDifferentName() {
		assertNotEquals(new MailHeader("A", "b"), new MailHeader("C", "b"));
	}
	
	@Test
	void equalsDifferentValue() {
		assertNotEquals(new MailHeader("A", "b"), new MailHeader("A", "c"));
	}
	
	@Test
	void hashCodeConsistentWithEquals() {
		assertEquals(new MailHeader("A", "b").hashCode(), new MailHeader("A", "b").hashCode());
	}
	
	@Test
	void toStringContainsNameAndValue() {
		String string = new MailHeader("X-A", "1").toString();
		assertTrue(string.contains("X-A"));
		assertTrue(string.contains("1"));
	}
	
	@Test
	void constructWithTabAndTildeBoundaryValue() {
		MailHeader header = assertDoesNotThrow(() -> new MailHeader("X-A", "a\tb c"));
		assertEquals("a\tb c", header.value());
	}
	
	@Test
	void constructWithLongPrintableAsciiName() {
		StringBuilder builder = new StringBuilder();
		for (char c = 0x21; c <= 0x7E; c++) {
			if (c != ':') {
				builder.append(c);
			}
		}
		String name = builder.toString();
		MailHeader header = assertDoesNotThrow(() -> new MailHeader(name, "v"));
		assertEquals(name, header.name());
	}
	
	@Test
	void constructWithValueContainingColonAndSpecials() {
		MailHeader header = assertDoesNotThrow(() -> new MailHeader("X-A", "text: with; specials é"));
		assertEquals("text: with; specials é", header.value());
	}
}
