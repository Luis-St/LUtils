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

package net.luis.utils.io.codec.constraint.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpVersion}.<br>
 *
 * @author Luis-St
 */
class IpVersionTest {
	
	@Test
	void valuesContainsAllConstants() {
		IpVersion[] values = IpVersion.values();
		assertEquals(2, values.length);
		assertEquals(IpVersion.IPV4, values[0]);
		assertEquals(IpVersion.IPV6, values[1]);
	}
	
	@Test
	void valueOfReturnsCorrectConstant() {
		assertEquals(IpVersion.IPV4, IpVersion.valueOf("IPV4"));
		assertEquals(IpVersion.IPV6, IpVersion.valueOf("IPV6"));
	}
}
