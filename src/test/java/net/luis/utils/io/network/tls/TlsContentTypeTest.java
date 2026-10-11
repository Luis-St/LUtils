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

package net.luis.utils.io.network.tls;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsContentType}.<br>
 *
 * @author Luis-St
 */
class TlsContentTypeTest {
	
	@Test
	void constantsHaveExpectedCodes() {
		assertEquals(20, TlsContentType.CHANGE_CIPHER_SPEC.code());
		assertEquals(21, TlsContentType.ALERT.code());
		assertEquals(22, TlsContentType.HANDSHAKE.code());
		assertEquals(23, TlsContentType.APPLICATION_DATA.code());
		assertEquals(4, TlsContentType.values().length);
	}
	
	@Test
	void byCodeWithKnownCode() {
		assertEquals(Optional.of(TlsContentType.HANDSHAKE), TlsContentType.byCode(22));
	}
	
	@Test
	void byCodeWithUnknownCode() {
		assertTrue(TlsContentType.byCode(0).isEmpty());
		assertTrue(TlsContentType.byCode(24).isEmpty());
	}
	
	@Test
	void byCodeResolvesEveryConstant() {
		for (TlsContentType type : TlsContentType.values()) {
			assertEquals(Optional.of(type), TlsContentType.byCode(type.code()));
		}
	}
	
	@Test
	void byCodeWithNegativeCode() {
		assertTrue(TlsContentType.byCode(-1).isEmpty());
	}
	
	@Test
	void byCodeWithOutOfByteRangeCode() {
		assertTrue(TlsContentType.byCode(20 + 256).isEmpty());
		assertTrue(TlsContentType.byCode(Integer.MAX_VALUE).isEmpty());
	}
}
