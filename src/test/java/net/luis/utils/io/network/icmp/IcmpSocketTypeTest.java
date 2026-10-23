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

package net.luis.utils.io.network.icmp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IcmpSocketType}.<br>
 *
 * @author Luis-St
 */
class IcmpSocketTypeTest {
	
	@Test
	void valueOfWithUnknownName() {
		assertThrows(IllegalArgumentException.class, () -> IcmpSocketType.valueOf("STREAM"));
	}
	
	@Test
	void valueOfWithNullName() {
		assertThrows(NullPointerException.class, () -> IcmpSocketType.valueOf(null));
	}
	
	@Test
	void valuesContainsDatagramAndRaw() {
		IcmpSocketType[] values = IcmpSocketType.values();
		assertEquals(2, values.length);
		assertEquals(IcmpSocketType.DATAGRAM, values[0]);
		assertEquals(IcmpSocketType.RAW, values[1]);
	}
	
	@Test
	void valueOfResolvesEveryConstant() {
		assertEquals(IcmpSocketType.DATAGRAM, IcmpSocketType.valueOf("DATAGRAM"));
		assertEquals(IcmpSocketType.RAW, IcmpSocketType.valueOf("RAW"));
	}
}
