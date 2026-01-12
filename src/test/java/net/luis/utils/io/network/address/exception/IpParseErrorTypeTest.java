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

package net.luis.utils.io.network.address.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link IpParseErrorType}.<br>
 *
 * @author Luis-St
 */
class IpParseErrorTypeTest {
	
	@Test
	void valuesCount() {
		assertEquals(7, IpParseErrorType.values().length);
	}
	
	@Test
	void valueOf() {
		assertEquals(IpParseErrorType.INVALID_FORMAT, IpParseErrorType.valueOf("INVALID_FORMAT"));
		assertEquals(IpParseErrorType.INVALID_OCTET_VALUE, IpParseErrorType.valueOf("INVALID_OCTET_VALUE"));
		assertEquals(IpParseErrorType.INVALID_HEXTET_VALUE, IpParseErrorType.valueOf("INVALID_HEXTET_VALUE"));
		assertEquals(IpParseErrorType.INVALID_PREFIX_LENGTH, IpParseErrorType.valueOf("INVALID_PREFIX_LENGTH"));
		assertEquals(IpParseErrorType.INVALID_ZONE_ID, IpParseErrorType.valueOf("INVALID_ZONE_ID"));
		assertEquals(IpParseErrorType.EMPTY_INPUT, IpParseErrorType.valueOf("EMPTY_INPUT"));
		assertEquals(IpParseErrorType.UNKNOWN, IpParseErrorType.valueOf("UNKNOWN"));
	}
	
	@Test
	void valueOfInvalidThrows() {
		assertThrows(IllegalArgumentException.class, () -> IpParseErrorType.valueOf("INVALID"));
	}
}
