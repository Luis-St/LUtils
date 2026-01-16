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

package net.luis.utils.io.codec.constraint_new.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpAddressType}.<br>
 *
 * @author Luis-St
 */
class IpAddressTypeTest {
	
	@Test
	void valuesContainsAllConstants() {
		IpAddressType[] values = IpAddressType.values();
		assertEquals(8, values.length);
		assertEquals(IpAddressType.PUBLIC, values[0]);
		assertEquals(IpAddressType.PRIVATE, values[1]);
		assertEquals(IpAddressType.LOOPBACK, values[2]);
		assertEquals(IpAddressType.LINK_LOCAL, values[3]);
		assertEquals(IpAddressType.MULTICAST, values[4]);
		assertEquals(IpAddressType.BROADCAST, values[5]);
		assertEquals(IpAddressType.DOCUMENTATION, values[6]);
		assertEquals(IpAddressType.UNSPECIFIED, values[7]);
	}
	
	@Test
	void valueOfReturnsCorrectConstant() {
		assertEquals(IpAddressType.PUBLIC, IpAddressType.valueOf("PUBLIC"));
		assertEquals(IpAddressType.PRIVATE, IpAddressType.valueOf("PRIVATE"));
		assertEquals(IpAddressType.LOOPBACK, IpAddressType.valueOf("LOOPBACK"));
		assertEquals(IpAddressType.LINK_LOCAL, IpAddressType.valueOf("LINK_LOCAL"));
		assertEquals(IpAddressType.MULTICAST, IpAddressType.valueOf("MULTICAST"));
		assertEquals(IpAddressType.BROADCAST, IpAddressType.valueOf("BROADCAST"));
		assertEquals(IpAddressType.DOCUMENTATION, IpAddressType.valueOf("DOCUMENTATION"));
		assertEquals(IpAddressType.UNSPECIFIED, IpAddressType.valueOf("UNSPECIFIED"));
	}
	
	@Test
	void ordinalValues() {
		assertEquals(0, IpAddressType.PUBLIC.ordinal());
		assertEquals(1, IpAddressType.PRIVATE.ordinal());
		assertEquals(2, IpAddressType.LOOPBACK.ordinal());
		assertEquals(3, IpAddressType.LINK_LOCAL.ordinal());
		assertEquals(4, IpAddressType.MULTICAST.ordinal());
		assertEquals(5, IpAddressType.BROADCAST.ordinal());
		assertEquals(6, IpAddressType.DOCUMENTATION.ordinal());
		assertEquals(7, IpAddressType.UNSPECIFIED.ordinal());
	}
}
