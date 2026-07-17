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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MailRecipientType}.<br>
 *
 * @author Luis-St
 */
class MailRecipientTypeTest {
	
	@Test
	void valueOfUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> MailRecipientType.valueOf("TOO"));
		assertThrows(IllegalArgumentException.class, () -> MailRecipientType.valueOf("to"));
	}
	
	@Test
	void valueOfNullThrows() {
		assertThrows(NullPointerException.class, () -> MailRecipientType.valueOf(null));
	}
	
	@Test
	void constantsExist() {
		assertNotNull(MailRecipientType.TO);
		assertNotNull(MailRecipientType.CC);
		assertNotNull(MailRecipientType.BCC);
	}
	
	@Test
	void valuesContainsAllConstants() {
		MailRecipientType[] values = MailRecipientType.values();
		
		assertEquals(3, values.length);
		List<MailRecipientType> list = Arrays.asList(values);
		assertTrue(list.contains(MailRecipientType.TO));
		assertTrue(list.contains(MailRecipientType.CC));
		assertTrue(list.contains(MailRecipientType.BCC));
	}
	
	@Test
	void valuesDeclarationOrder() {
		MailRecipientType[] values = MailRecipientType.values();
		
		assertSame(MailRecipientType.TO, values[0]);
		assertSame(MailRecipientType.CC, values[1]);
		assertSame(MailRecipientType.BCC, values[2]);
		assertEquals(0, MailRecipientType.TO.ordinal());
		assertEquals(1, MailRecipientType.CC.ordinal());
		assertEquals(2, MailRecipientType.BCC.ordinal());
	}
	
	@Test
	void valueOfReturnsMatchingConstant() {
		assertSame(MailRecipientType.TO, MailRecipientType.valueOf("TO"));
		assertSame(MailRecipientType.CC, MailRecipientType.valueOf("CC"));
		assertSame(MailRecipientType.BCC, MailRecipientType.valueOf("BCC"));
	}
	
	@Test
	void nameMatchesConstant() {
		assertEquals("TO", MailRecipientType.TO.name());
		assertEquals("CC", MailRecipientType.CC.name());
		assertEquals("BCC", MailRecipientType.BCC.name());
	}
}
