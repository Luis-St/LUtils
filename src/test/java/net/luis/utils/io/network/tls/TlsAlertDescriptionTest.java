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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsAlertDescription}.<br>
 *
 * @author Luis-St
 */
class TlsAlertDescriptionTest {
	
	@Test
	void constantsHaveExpectedCodes() {
		assertEquals(0, TlsAlertDescription.CLOSE_NOTIFY.code());
		assertEquals(10, TlsAlertDescription.UNEXPECTED_MESSAGE.code());
		assertEquals(20, TlsAlertDescription.BAD_RECORD_MAC.code());
		assertEquals(22, TlsAlertDescription.RECORD_OVERFLOW.code());
		assertEquals(50, TlsAlertDescription.DECODE_ERROR.code());
		assertEquals(80, TlsAlertDescription.INTERNAL_ERROR.code());
		assertEquals(90, TlsAlertDescription.USER_CANCELED.code());
		assertEquals(120, TlsAlertDescription.NO_APPLICATION_PROTOCOL.code());
	}
	
	@Test
	void constantsHaveUniqueCodes() {
		Set<Integer> codes = new HashSet<>();
		for (TlsAlertDescription description : TlsAlertDescription.values()) {
			codes.add(description.code());
		}
		assertEquals(27, TlsAlertDescription.values().length);
		assertEquals(TlsAlertDescription.values().length, codes.size());
	}
	
	@Test
	void byCodeWithKnownCode() {
		assertEquals(Optional.of(TlsAlertDescription.BAD_RECORD_MAC), TlsAlertDescription.byCode(20));
	}
	
	@Test
	void byCodeWithUnknownCode() {
		assertTrue(TlsAlertDescription.byCode(1).isEmpty());
		assertTrue(TlsAlertDescription.byCode(21).isEmpty());
	}
	
	@Test
	void byCodeWithFirstAndLastConstant() {
		assertEquals(Optional.of(TlsAlertDescription.CLOSE_NOTIFY), TlsAlertDescription.byCode(0));
		assertEquals(Optional.of(TlsAlertDescription.NO_APPLICATION_PROTOCOL), TlsAlertDescription.byCode(120));
	}
	
	@Test
	void byCodeWithNegativeCode() {
		assertTrue(TlsAlertDescription.byCode(-1).isEmpty());
	}
	
	@Test
	void byCodeWithUnassignedGapCodes() {
		assertTrue(TlsAlertDescription.byCode(30).isEmpty());
		assertTrue(TlsAlertDescription.byCode(41).isEmpty());
		assertTrue(TlsAlertDescription.byCode(111).isEmpty());
		assertTrue(TlsAlertDescription.byCode(114).isEmpty());
	}
	
	@Test
	void byCodeRoundTripForEveryConstant() {
		for (TlsAlertDescription description : TlsAlertDescription.values()) {
			assertEquals(Optional.of(description), TlsAlertDescription.byCode(description.code()));
		}
	}
	
	@Test
	void byCodeWithOutOfByteRangeCode() {
		assertTrue(TlsAlertDescription.byCode(256).isEmpty());
		assertTrue(TlsAlertDescription.byCode(20 + 256).isEmpty());
	}
}
