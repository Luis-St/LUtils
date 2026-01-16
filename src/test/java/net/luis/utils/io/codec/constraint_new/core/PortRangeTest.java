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
 * Test class for {@link PortRange}.<br>
 *
 * @author Luis-St
 */
class PortRangeTest {
	
	@Test
	void valuesContainsAllConstants() {
		PortRange[] values = PortRange.values();
		assertEquals(3, values.length);
		assertEquals(PortRange.SYSTEM, values[0]);
		assertEquals(PortRange.REGISTERED, values[1]);
		assertEquals(PortRange.DYNAMIC, values[2]);
	}
	
	@Test
	void fromPortForSystemRange() {
		assertEquals(PortRange.SYSTEM, PortRange.fromPort(0));
		assertEquals(PortRange.SYSTEM, PortRange.fromPort(80));
		assertEquals(PortRange.SYSTEM, PortRange.fromPort(443));
		assertEquals(PortRange.SYSTEM, PortRange.fromPort(512));
		assertEquals(PortRange.SYSTEM, PortRange.fromPort(1023));
	}
	
	@Test
	void fromPortForSystemRangeBoundary() {
		assertEquals(PortRange.SYSTEM, PortRange.fromPort(0));
		assertEquals(PortRange.SYSTEM, PortRange.fromPort(1023));
	}
	
	@Test
	void fromPortForRegisteredRange() {
		assertEquals(PortRange.REGISTERED, PortRange.fromPort(1024));
		assertEquals(PortRange.REGISTERED, PortRange.fromPort(3306));
		assertEquals(PortRange.REGISTERED, PortRange.fromPort(8080));
		assertEquals(PortRange.REGISTERED, PortRange.fromPort(27017));
		assertEquals(PortRange.REGISTERED, PortRange.fromPort(49151));
	}
	
	@Test
	void fromPortForRegisteredRangeBoundary() {
		assertEquals(PortRange.REGISTERED, PortRange.fromPort(1024));
		assertEquals(PortRange.REGISTERED, PortRange.fromPort(49151));
	}
	
	@Test
	void fromPortForDynamicRange() {
		assertEquals(PortRange.DYNAMIC, PortRange.fromPort(49152));
		assertEquals(PortRange.DYNAMIC, PortRange.fromPort(50000));
		assertEquals(PortRange.DYNAMIC, PortRange.fromPort(55000));
		assertEquals(PortRange.DYNAMIC, PortRange.fromPort(60000));
		assertEquals(PortRange.DYNAMIC, PortRange.fromPort(65535));
	}
	
	@Test
	void fromPortForDynamicRangeBoundary() {
		assertEquals(PortRange.DYNAMIC, PortRange.fromPort(49152));
		assertEquals(PortRange.DYNAMIC, PortRange.fromPort(65535));
	}
	
	@Test
	void fromPortWithNegativePort() {
		assertThrows(IllegalArgumentException.class, () -> PortRange.fromPort(-1));
		assertThrows(IllegalArgumentException.class, () -> PortRange.fromPort(-100));
		assertThrows(IllegalArgumentException.class, () -> PortRange.fromPort(Integer.MIN_VALUE));
	}
	
	@Test
	void fromPortWithPortTooHigh() {
		assertThrows(IllegalArgumentException.class, () -> PortRange.fromPort(65536));
		assertThrows(IllegalArgumentException.class, () -> PortRange.fromPort(70000));
		assertThrows(IllegalArgumentException.class, () -> PortRange.fromPort(Integer.MAX_VALUE));
	}
}
