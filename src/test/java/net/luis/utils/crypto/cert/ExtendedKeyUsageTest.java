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

package net.luis.utils.crypto.cert;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ExtendedKeyUsage}.<br>
 *
 * @author Luis-St
 */
class ExtendedKeyUsageTest {
	
	@Test
	void constantsDeclared() {
		List<ExtendedKeyUsage> expected = List.of(
			ExtendedKeyUsage.SERVER_AUTH, ExtendedKeyUsage.CLIENT_AUTH, ExtendedKeyUsage.CODE_SIGNING, ExtendedKeyUsage.EMAIL_PROTECTION,
			ExtendedKeyUsage.TIME_STAMPING, ExtendedKeyUsage.OCSP_SIGNING, ExtendedKeyUsage.IPSEC_IKE
		);
		assertEquals(expected, List.of(ExtendedKeyUsage.values()));
		for (ExtendedKeyUsage usage : ExtendedKeyUsage.values()) {
			assertNotNull(usage.oid(), usage.name());
		}
	}
	
	@Test
	void byOidWithNull() {
		assertEquals("Oid must not be null", assertThrows(NullPointerException.class, () -> ExtendedKeyUsage.byOid(null)).getMessage());
	}
	
	@Test
	void byOidWithKnownOid() {
		assertEquals(Optional.of(ExtendedKeyUsage.SERVER_AUTH), ExtendedKeyUsage.byOid("1.3.6.1.5.5.7.3.1"));
	}
	
	@Test
	void byOidWithUnknownOid() {
		assertTrue(ExtendedKeyUsage.byOid("1.3.6.1.5.5.7.3.99").isEmpty());
		assertTrue(ExtendedKeyUsage.byOid("2.5.29.37.0").isEmpty());
	}
	
	@Test
	void byOidWithEmptyString() {
		assertTrue(ExtendedKeyUsage.byOid("").isEmpty());
	}
	
	@Test
	void oidOfEveryUsage() {
		assertEquals("1.3.6.1.5.5.7.3.1", ExtendedKeyUsage.SERVER_AUTH.oid());
		assertEquals("1.3.6.1.5.5.7.3.2", ExtendedKeyUsage.CLIENT_AUTH.oid());
		assertEquals("1.3.6.1.5.5.7.3.3", ExtendedKeyUsage.CODE_SIGNING.oid());
		assertEquals("1.3.6.1.5.5.7.3.4", ExtendedKeyUsage.EMAIL_PROTECTION.oid());
		assertEquals("1.3.6.1.5.5.7.3.8", ExtendedKeyUsage.TIME_STAMPING.oid());
		assertEquals("1.3.6.1.5.5.7.3.9", ExtendedKeyUsage.OCSP_SIGNING.oid());
		assertEquals("1.3.6.1.5.5.7.3.17", ExtendedKeyUsage.IPSEC_IKE.oid());
	}
	
	@Test
	void byOidWithLastUsage() {
		assertEquals(Optional.of(ExtendedKeyUsage.IPSEC_IKE), ExtendedKeyUsage.byOid("1.3.6.1.5.5.7.3.17"));
	}
	
	@Test
	void byOidRoundTrip() {
		for (ExtendedKeyUsage usage : ExtendedKeyUsage.values()) {
			assertEquals(Optional.of(usage), ExtendedKeyUsage.byOid(usage.oid()), usage.name());
		}
	}
	
	@Test
	void byOidIsExact() {
		assertTrue(ExtendedKeyUsage.byOid("1.3.6.1.5.5.7.3").isEmpty());
		assertTrue(ExtendedKeyUsage.byOid("1.3.6.1.5.5.7.3.1 ").isEmpty());
		assertTrue(ExtendedKeyUsage.byOid(".1.3.6.1.5.5.7.3.1").isEmpty());
		assertTrue(ExtendedKeyUsage.byOid("1.3.6.1.5.5.7.3.10").isEmpty());
		assertEquals(Optional.of(ExtendedKeyUsage.SERVER_AUTH), ExtendedKeyUsage.byOid("1.3.6.1.5.5.7.3.1"));
		assertEquals(Optional.of(ExtendedKeyUsage.IPSEC_IKE), ExtendedKeyUsage.byOid("1.3.6.1.5.5.7.3.17"));
	}
	
	@Test
	void oidsAreUnique() {
		Set<String> oids = new HashSet<>();
		for (ExtendedKeyUsage usage : ExtendedKeyUsage.values()) {
			assertTrue(oids.add(usage.oid()), usage.name());
			assertTrue(usage.oid().startsWith("1.3.6.1.5.5.7.3."), usage.name());
		}
		assertEquals(7, oids.size());
	}
}
