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

package net.luis.utils.io.network.address.ipv6;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Ipv6MulticastScope}.<br>
 *
 * @author Luis-St
 */
class Ipv6MulticastScopeTest {
	
	@Test
	void fromValueReturnsCorrectScope() {
		assertEquals(Ipv6MulticastScope.RESERVED_0, Ipv6MulticastScope.fromValue(0x0));
		assertEquals(Ipv6MulticastScope.INTERFACE_LOCAL, Ipv6MulticastScope.fromValue(0x1));
		assertEquals(Ipv6MulticastScope.LINK_LOCAL, Ipv6MulticastScope.fromValue(0x2));
		assertEquals(Ipv6MulticastScope.REALM_LOCAL, Ipv6MulticastScope.fromValue(0x3));
		assertEquals(Ipv6MulticastScope.ADMIN_LOCAL, Ipv6MulticastScope.fromValue(0x4));
		assertEquals(Ipv6MulticastScope.SITE_LOCAL, Ipv6MulticastScope.fromValue(0x5));
		assertEquals(Ipv6MulticastScope.UNASSIGNED_6, Ipv6MulticastScope.fromValue(0x6));
		assertEquals(Ipv6MulticastScope.UNASSIGNED_7, Ipv6MulticastScope.fromValue(0x7));
		assertEquals(Ipv6MulticastScope.ORGANIZATION_LOCAL, Ipv6MulticastScope.fromValue(0x8));
		assertEquals(Ipv6MulticastScope.UNASSIGNED_9, Ipv6MulticastScope.fromValue(0x9));
		assertEquals(Ipv6MulticastScope.UNASSIGNED_A, Ipv6MulticastScope.fromValue(0xA));
		assertEquals(Ipv6MulticastScope.UNASSIGNED_B, Ipv6MulticastScope.fromValue(0xB));
		assertEquals(Ipv6MulticastScope.UNASSIGNED_C, Ipv6MulticastScope.fromValue(0xC));
		assertEquals(Ipv6MulticastScope.UNASSIGNED_D, Ipv6MulticastScope.fromValue(0xD));
		assertEquals(Ipv6MulticastScope.GLOBAL, Ipv6MulticastScope.fromValue(0xE));
		assertEquals(Ipv6MulticastScope.RESERVED_F, Ipv6MulticastScope.fromValue(0xF));
	}
	
	@Test
	void fromValueOutOfRangeReturnsUnknown() {
		assertEquals(Ipv6MulticastScope.UNKNOWN, Ipv6MulticastScope.fromValue(-2));
		assertEquals(Ipv6MulticastScope.UNKNOWN, Ipv6MulticastScope.fromValue(0x10));
		assertEquals(Ipv6MulticastScope.UNKNOWN, Ipv6MulticastScope.fromValue(100));
	}
	
	@Test
	void fromValueWithUnknownValueReturnsUnknown() {
		assertEquals(Ipv6MulticastScope.UNKNOWN, Ipv6MulticastScope.fromValue(-1));
	}
	
	@Test
	void getValueReturnsCorrectValue() {
		assertEquals(0x0, Ipv6MulticastScope.RESERVED_0.getValue());
		assertEquals(0x1, Ipv6MulticastScope.INTERFACE_LOCAL.getValue());
		assertEquals(0x2, Ipv6MulticastScope.LINK_LOCAL.getValue());
		assertEquals(0x3, Ipv6MulticastScope.REALM_LOCAL.getValue());
		assertEquals(0x4, Ipv6MulticastScope.ADMIN_LOCAL.getValue());
		assertEquals(0x5, Ipv6MulticastScope.SITE_LOCAL.getValue());
		assertEquals(0x6, Ipv6MulticastScope.UNASSIGNED_6.getValue());
		assertEquals(0x7, Ipv6MulticastScope.UNASSIGNED_7.getValue());
		assertEquals(0x8, Ipv6MulticastScope.ORGANIZATION_LOCAL.getValue());
		assertEquals(0x9, Ipv6MulticastScope.UNASSIGNED_9.getValue());
		assertEquals(0xA, Ipv6MulticastScope.UNASSIGNED_A.getValue());
		assertEquals(0xB, Ipv6MulticastScope.UNASSIGNED_B.getValue());
		assertEquals(0xC, Ipv6MulticastScope.UNASSIGNED_C.getValue());
		assertEquals(0xD, Ipv6MulticastScope.UNASSIGNED_D.getValue());
		assertEquals(0xE, Ipv6MulticastScope.GLOBAL.getValue());
		assertEquals(0xF, Ipv6MulticastScope.RESERVED_F.getValue());
		assertEquals(-1, Ipv6MulticastScope.UNKNOWN.getValue());
	}
	
	@Test
	void getDescriptionReturnsNonNull() {
		for (Ipv6MulticastScope scope : Ipv6MulticastScope.values()) {
			assertNotNull(scope.getDescription());
		}
	}
	
	@Test
	void getDescriptionReturnsCorrectDescription() {
		assertEquals("Reserved", Ipv6MulticastScope.RESERVED_0.getDescription());
		assertEquals("Interface-Local scope", Ipv6MulticastScope.INTERFACE_LOCAL.getDescription());
		assertEquals("Link-Local scope", Ipv6MulticastScope.LINK_LOCAL.getDescription());
		assertEquals("Realm-Local scope", Ipv6MulticastScope.REALM_LOCAL.getDescription());
		assertEquals("Admin-Local scope", Ipv6MulticastScope.ADMIN_LOCAL.getDescription());
		assertEquals("Site-Local scope", Ipv6MulticastScope.SITE_LOCAL.getDescription());
		assertEquals("Unassigned", Ipv6MulticastScope.UNASSIGNED_6.getDescription());
		assertEquals("Organization-Local scope", Ipv6MulticastScope.ORGANIZATION_LOCAL.getDescription());
		assertEquals("Global scope", Ipv6MulticastScope.GLOBAL.getDescription());
		assertEquals("Reserved", Ipv6MulticastScope.RESERVED_F.getDescription());
		assertEquals("Unknown scope", Ipv6MulticastScope.UNKNOWN.getDescription());
	}
	
	@Test
	void isAssignedReturnsTrueForAssignedScopes() {
		assertTrue(Ipv6MulticastScope.INTERFACE_LOCAL.isAssigned());
		assertTrue(Ipv6MulticastScope.LINK_LOCAL.isAssigned());
		assertTrue(Ipv6MulticastScope.REALM_LOCAL.isAssigned());
		assertTrue(Ipv6MulticastScope.ADMIN_LOCAL.isAssigned());
		assertTrue(Ipv6MulticastScope.SITE_LOCAL.isAssigned());
		assertTrue(Ipv6MulticastScope.ORGANIZATION_LOCAL.isAssigned());
		assertTrue(Ipv6MulticastScope.GLOBAL.isAssigned());
	}
	
	@Test
	void isAssignedReturnsFalseForReservedScopes() {
		assertFalse(Ipv6MulticastScope.RESERVED_0.isAssigned());
		assertFalse(Ipv6MulticastScope.RESERVED_F.isAssigned());
	}
	
	@Test
	void isAssignedReturnsFalseForUnassignedScopes() {
		assertFalse(Ipv6MulticastScope.UNASSIGNED_6.isAssigned());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_7.isAssigned());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_9.isAssigned());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_A.isAssigned());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_B.isAssigned());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_C.isAssigned());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_D.isAssigned());
	}
	
	@Test
	void isAssignedReturnsFalseForUnknown() {
		assertFalse(Ipv6MulticastScope.UNKNOWN.isAssigned());
	}
	
	@Test
	void isWellKnownReturnsTrueForWellKnownScopes() {
		assertTrue(Ipv6MulticastScope.INTERFACE_LOCAL.isWellKnown());
		assertTrue(Ipv6MulticastScope.LINK_LOCAL.isWellKnown());
		assertTrue(Ipv6MulticastScope.SITE_LOCAL.isWellKnown());
		assertTrue(Ipv6MulticastScope.ORGANIZATION_LOCAL.isWellKnown());
		assertTrue(Ipv6MulticastScope.GLOBAL.isWellKnown());
	}
	
	@Test
	void isWellKnownReturnsFalseForNonWellKnownScopes() {
		assertFalse(Ipv6MulticastScope.RESERVED_0.isWellKnown());
		assertFalse(Ipv6MulticastScope.REALM_LOCAL.isWellKnown());
		assertFalse(Ipv6MulticastScope.ADMIN_LOCAL.isWellKnown());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_6.isWellKnown());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_7.isWellKnown());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_9.isWellKnown());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_A.isWellKnown());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_B.isWellKnown());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_C.isWellKnown());
		assertFalse(Ipv6MulticastScope.UNASSIGNED_D.isWellKnown());
		assertFalse(Ipv6MulticastScope.RESERVED_F.isWellKnown());
		assertFalse(Ipv6MulticastScope.UNKNOWN.isWellKnown());
	}
	
	@Test
	void enumValuesCount() {
		assertEquals(17, Ipv6MulticastScope.values().length);
	}
	
	@Test
	void fromValueAndGetValueRoundTrip() {
		for (int i = 0; i <= 0xF; i++) {
			Ipv6MulticastScope scope = Ipv6MulticastScope.fromValue(i);
			assertEquals(i, scope.getValue());
		}
	}
}
