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

package net.luis.utils.io.network.mail;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link SmtpSecurity}.<br>
 *
 * @author Luis-St
 */
class SmtpSecurityTest {
	
	@Test
	void valueOfWithUnknownNameThrows() {
		assertThrows(IllegalArgumentException.class, () -> SmtpSecurity.valueOf("SSL"));
	}
	
	@Test
	void valueOfWithNullThrows() {
		assertThrows(NullPointerException.class, () -> SmtpSecurity.valueOf(null));
	}
	
	@Test
	void defaultPortPlaintextReturns25() {
		assertEquals(25, SmtpSecurity.PLAINTEXT.defaultPort());
	}
	
	@Test
	void defaultPortImplicitTlsReturns465() {
		assertEquals(465, SmtpSecurity.IMPLICIT_TLS.defaultPort());
	}
	
	@Test
	void defaultPortStarttlsReturns587() {
		assertEquals(587, SmtpSecurity.STARTTLS.defaultPort());
	}
	
	@Test
	void valuesContainsAllConstants() {
		SmtpSecurity[] values = SmtpSecurity.values();
		
		assertEquals(3, values.length);
		List<SmtpSecurity> list = Arrays.asList(values);
		assertTrue(list.contains(SmtpSecurity.PLAINTEXT));
		assertTrue(list.contains(SmtpSecurity.IMPLICIT_TLS));
		assertTrue(list.contains(SmtpSecurity.STARTTLS));
	}
	
	@Test
	void valueOfReturnsMatchingConstant() {
		assertSame(SmtpSecurity.PLAINTEXT, SmtpSecurity.valueOf("PLAINTEXT"));
		assertSame(SmtpSecurity.IMPLICIT_TLS, SmtpSecurity.valueOf("IMPLICIT_TLS"));
		assertSame(SmtpSecurity.STARTTLS, SmtpSecurity.valueOf("STARTTLS"));
	}
	
	@Test
	void defaultPortsAreUniqueAcrossConstants() {
		Set<Integer> ports = new HashSet<>();
		for (SmtpSecurity security : SmtpSecurity.values()) {
			int port = security.defaultPort();
			assertTrue(port >= 1 && port <= 65535);
			ports.add(port);
		}
		
		assertEquals(3, ports.size());
		assertEquals(Set.of(25, 465, 587), ports);
	}
}
