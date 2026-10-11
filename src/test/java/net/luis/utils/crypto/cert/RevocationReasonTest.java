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

import java.security.cert.CRLReason;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link RevocationReason}.<br>
 *
 * @author Luis-St
 */
class RevocationReasonTest {
	
	@Test
	void valuesHoldTenReasons() {
		RevocationReason[] values = RevocationReason.values();
		assertEquals(10, values.length);
		assertEquals(RevocationReason.UNSPECIFIED, values[0]);
		assertEquals(RevocationReason.AA_COMPROMISE, values[9]);
	}
	
	@Test
	void valueOfWithNullName() {
		assertThrows(NullPointerException.class, () -> RevocationReason.valueOf(null));
	}
	
	@Test
	void valueOfWithUnknownName() {
		assertThrows(IllegalArgumentException.class, () -> RevocationReason.valueOf("UNUSED"));
		assertThrows(IllegalArgumentException.class, () -> RevocationReason.valueOf("key_compromise"));
	}
	
	@Test
	void codeSevenIsUnused() {
		assertTrue(Arrays.stream(RevocationReason.values()).noneMatch(reason -> reason.code() == 7));
		assertEquals(6, RevocationReason.CERTIFICATE_HOLD.code());
		assertEquals(8, RevocationReason.REMOVE_FROM_CRL.code());
	}
	
	@Test
	void codeOfEachReason() {
		assertEquals(0, RevocationReason.UNSPECIFIED.code());
		assertEquals(1, RevocationReason.KEY_COMPROMISE.code());
		assertEquals(2, RevocationReason.CA_COMPROMISE.code());
		assertEquals(3, RevocationReason.AFFILIATION_CHANGED.code());
		assertEquals(4, RevocationReason.SUPERSEDED.code());
		assertEquals(5, RevocationReason.CESSATION_OF_OPERATION.code());
		assertEquals(6, RevocationReason.CERTIFICATE_HOLD.code());
		assertEquals(8, RevocationReason.REMOVE_FROM_CRL.code());
		assertEquals(9, RevocationReason.PRIVILEGE_WITHDRAWN.code());
		assertEquals(10, RevocationReason.AA_COMPROMISE.code());
	}
	
	@Test
	void valueOfKnownName() {
		assertEquals(RevocationReason.KEY_COMPROMISE, RevocationReason.valueOf("KEY_COMPROMISE"));
		assertEquals(RevocationReason.UNSPECIFIED, RevocationReason.valueOf("UNSPECIFIED"));
	}
	
	@Test
	void codesAreUniqueAndAscending() {
		RevocationReason[] values = RevocationReason.values();
		for (int i = 1; i < values.length; i++) {
			assertTrue(values[i - 1].code() < values[i].code(), values[i - 1] + " must have a lower code than " + values[i]);
		}
		assertEquals(values.length, Arrays.stream(values).mapToInt(RevocationReason::code).distinct().count());
	}
	
	@Test
	void codesMatchJdkCrlReason() {
		for (RevocationReason reason : RevocationReason.values()) {
			assertEquals(reason.name(), CRLReason.values()[reason.code()].name());
		}
		assertEquals("UNUSED", CRLReason.values()[7].name());
	}
}
