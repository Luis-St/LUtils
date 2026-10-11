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
 * Test class for {@link TlsAlertLevel}.<br>
 *
 * @author Luis-St
 */
class TlsAlertLevelTest {
	
	@Test
	void constantsHaveExpectedCodes() {
		assertEquals(1, TlsAlertLevel.WARNING.code());
		assertEquals(2, TlsAlertLevel.FATAL.code());
		assertEquals(2, TlsAlertLevel.values().length);
	}
	
	@Test
	void byCodeWithKnownCode() {
		assertEquals(Optional.of(TlsAlertLevel.WARNING), TlsAlertLevel.byCode(1));
		assertEquals(Optional.of(TlsAlertLevel.FATAL), TlsAlertLevel.byCode(2));
	}
	
	@Test
	void byCodeWithUnknownCode() {
		assertTrue(TlsAlertLevel.byCode(0).isEmpty());
		assertTrue(TlsAlertLevel.byCode(3).isEmpty());
	}
	
	@Test
	void byCodeWithNegativeCode() {
		assertTrue(TlsAlertLevel.byCode(-1).isEmpty());
	}
	
	@Test
	void byCodeRoundTripForEveryConstant() {
		for (TlsAlertLevel level : TlsAlertLevel.values()) {
			assertEquals(Optional.of(level), TlsAlertLevel.byCode(level.code()));
		}
	}
}
