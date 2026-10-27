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

import net.luis.utils.io.network.connection.ssl.TlsProtocol;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsVersion}.<br>
 *
 * @author Luis-St
 */
class TlsVersionTest {
	
	@Test
	void constantsHaveExpectedCodesAndProtocols() {
		assertEquals(0x0303, TlsVersion.TLS_1_2.code());
		assertEquals(0x0304, TlsVersion.TLS_1_3.code());
		assertEquals(TlsProtocol.TLS_V1_2, TlsVersion.TLS_1_2.protocol());
		assertEquals(TlsProtocol.TLS_V1_3, TlsVersion.TLS_1_3.protocol());
		assertEquals(2, TlsVersion.values().length);
	}
	
	@Test
	void byCodeWithKnownCode() {
		assertEquals(Optional.of(TlsVersion.TLS_1_2), TlsVersion.byCode(0x0303));
		assertEquals(Optional.of(TlsVersion.TLS_1_3), TlsVersion.byCode(0x0304));
	}
	
	@Test
	void byCodeWithUnknownCode() {
		assertTrue(TlsVersion.byCode(0x0302).isEmpty());
		assertTrue(TlsVersion.byCode(0x0305).isEmpty());
	}
	
	@Test
	void maxCiphertextLengthForTls12() {
		assertEquals(18432, TlsVersion.TLS_1_2.maxCiphertextLength());
	}
	
	@Test
	void maxCiphertextLengthForTls13() {
		assertEquals(16640, TlsVersion.TLS_1_3.maxCiphertextLength());
	}
	
	@Test
	void toStringReturnsProtocolName() {
		assertEquals("TLSv1.2", TlsVersion.TLS_1_2.toString());
		assertEquals("TLSv1.3", TlsVersion.TLS_1_3.toString());
	}
	
	@Test
	void byCodeWithSingleByteOfVersion() {
		assertTrue(TlsVersion.byCode(0x03).isEmpty());
		assertTrue(TlsVersion.byCode(0x04).isEmpty());
	}
	
	@Test
	void maxCiphertextLengthExceedsMaxPlaintextLength() {
		for (TlsVersion version : TlsVersion.values()) {
			assertTrue(version.maxCiphertextLength() > TlsRecord.MAX_PLAINTEXT_LENGTH);
		}
	}
	
	@Test
	void byCodeRoundTripForEveryConstant() {
		for (TlsVersion version : TlsVersion.values()) {
			assertEquals(Optional.of(version), TlsVersion.byCode(version.code()));
		}
	}
}
