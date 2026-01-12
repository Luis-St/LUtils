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

package net.luis.utils.io.network.address;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AddressType}.<br>
 *
 * @author Luis-St
 */
class AddressTypeTest {
	
	@Test
	void allValuesExist() {
		assertEquals(10, AddressType.values().length);
	}
	
	@Test
	void valueOfWorks() {
		assertEquals(AddressType.UNSPECIFIED, AddressType.valueOf("UNSPECIFIED"));
		assertEquals(AddressType.LOOPBACK, AddressType.valueOf("LOOPBACK"));
		assertEquals(AddressType.PRIVATE, AddressType.valueOf("PRIVATE"));
		assertEquals(AddressType.LINK_LOCAL, AddressType.valueOf("LINK_LOCAL"));
		assertEquals(AddressType.MULTICAST, AddressType.valueOf("MULTICAST"));
		assertEquals(AddressType.BROADCAST, AddressType.valueOf("BROADCAST"));
		assertEquals(AddressType.DOCUMENTATION, AddressType.valueOf("DOCUMENTATION"));
		assertEquals(AddressType.SHARED_ADDRESS_SPACE, AddressType.valueOf("SHARED_ADDRESS_SPACE"));
		assertEquals(AddressType.GLOBAL_UNICAST, AddressType.valueOf("GLOBAL_UNICAST"));
		assertEquals(AddressType.RESERVED, AddressType.valueOf("RESERVED"));
	}
	
	@Test
	void descriptionNotNull() {
		for (AddressType type : AddressType.values()) {
			assertNotNull(type.description());
			assertFalse(type.description().isEmpty());
		}
	}
	
	@Test
	void onlyGlobalUnicastIsRoutable() {
		for (AddressType type : AddressType.values()) {
			if (type == AddressType.GLOBAL_UNICAST) {
				assertTrue(type.isRoutable());
			} else {
				assertFalse(type.isRoutable());
			}
		}
	}
	
	@Test
	void unspecifiedProperties() {
		AddressType type = AddressType.UNSPECIFIED;
		assertFalse(type.isRoutable());
		assertTrue(type.description().contains("0.0.0.0"));
	}
	
	@Test
	void loopbackProperties() {
		AddressType type = AddressType.LOOPBACK;
		assertFalse(type.isRoutable());
		assertTrue(type.description().contains("127.0.0.0"));
	}
	
	@Test
	void privateProperties() {
		AddressType type = AddressType.PRIVATE;
		assertFalse(type.isRoutable());
		assertTrue(type.description().contains("RFC 1918"));
	}
	
	@Test
	void linkLocalProperties() {
		AddressType type = AddressType.LINK_LOCAL;
		assertFalse(type.isRoutable());
		assertTrue(type.description().contains("169.254.0.0"));
	}
	
	@Test
	void multicastProperties() {
		AddressType type = AddressType.MULTICAST;
		assertFalse(type.isRoutable());
		assertTrue(type.description().contains("224.0.0.0"));
	}
	
	@Test
	void broadcastProperties() {
		AddressType type = AddressType.BROADCAST;
		assertFalse(type.isRoutable());
		assertTrue(type.description().contains("255.255.255.255"));
	}
	
	@Test
	void documentationProperties() {
		AddressType type = AddressType.DOCUMENTATION;
		assertFalse(type.isRoutable());
		assertTrue(type.description().contains("192.0.2.0"));
	}
	
	@Test
	void sharedAddressSpaceProperties() {
		AddressType type = AddressType.SHARED_ADDRESS_SPACE;
		assertFalse(type.isRoutable());
		assertTrue(type.description().contains("100.64.0.0"));
	}
	
	@Test
	void globalUnicastProperties() {
		AddressType type = AddressType.GLOBAL_UNICAST;
		assertTrue(type.isRoutable());
		assertTrue(type.description().toLowerCase().contains("global"));
	}
	
	@Test
	void reservedProperties() {
		AddressType type = AddressType.RESERVED;
		assertFalse(type.isRoutable());
		assertTrue(type.description().toLowerCase().contains("reserved"));
	}
}
