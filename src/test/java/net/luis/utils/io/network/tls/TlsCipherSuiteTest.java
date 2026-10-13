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

import net.luis.utils.crypto.algorithm.AeadAlgorithm;
import net.luis.utils.crypto.algorithm.HashAlgorithm;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsCipherSuite}.<br>
 *
 * @author Luis-St
 */
class TlsCipherSuiteTest {
	
	@Test
	void constantsHaveExpectedParameters() {
		TlsCipherSuite tls13 = TlsCipherSuite.TLS_AES_128_GCM_SHA256;
		assertEquals(0x1301, tls13.id());
		assertEquals(TlsVersion.TLS_1_3, tls13.version());
		assertEquals(AeadAlgorithm.AES_128_GCM, tls13.aead());
		assertEquals(HashAlgorithm.SHA_256, tls13.hash());
		
		TlsCipherSuite tls12 = TlsCipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256;
		assertEquals(0xCCA8, tls12.id());
		assertEquals(TlsVersion.TLS_1_2, tls12.version());
		assertEquals(AeadAlgorithm.CHACHA20_POLY1305, tls12.aead());
		assertEquals(HashAlgorithm.SHA_256, tls12.hash());
	}
	
	@Test
	void constantsHaveUniqueIds() {
		Set<Integer> ids = new HashSet<>();
		for (TlsCipherSuite suite : TlsCipherSuite.values()) {
			ids.add(suite.id());
		}
		assertEquals(9, TlsCipherSuite.values().length);
		assertEquals(TlsCipherSuite.values().length, ids.size());
	}
	
	@Test
	void createCipherWithNullKey() {
		assertThrows(NullPointerException.class, () -> TlsCipherSuite.TLS_AES_128_GCM_SHA256.createCipher(null, new byte[12]));
	}
	
	@Test
	void createCipherWithNullIv() {
		assertThrows(NullPointerException.class, () -> TlsCipherSuite.TLS_AES_128_GCM_SHA256.createCipher(new byte[16], null));
	}
	
	@Test
	void createCipherWithWrongKeyLength() {
		assertThrows(IllegalArgumentException.class, () -> TlsCipherSuite.TLS_AES_256_GCM_SHA384.createCipher(new byte[16], new byte[12]));
	}
	
	@Test
	void createCipherWithWrongIvLength() {
		assertThrows(IllegalArgumentException.class, () -> TlsCipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256.createCipher(new byte[16], new byte[12]));
	}
	
	@Test
	void byIdWithKnownId() {
		assertEquals(Optional.of(TlsCipherSuite.TLS_AES_256_GCM_SHA384), TlsCipherSuite.byId(0x1302));
	}
	
	@Test
	void byIdWithUnknownId() {
		assertTrue(TlsCipherSuite.byId(0x0000).isEmpty());
		assertTrue(TlsCipherSuite.byId(0x1304).isEmpty());
		assertTrue(TlsCipherSuite.byId(0xC027).isEmpty());
	}
	
	@Test
	void ivLengthForTls12AesGcmSuites() {
		assertEquals(4, TlsCipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256.ivLength());
		assertEquals(4, TlsCipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384.ivLength());
		assertEquals(4, TlsCipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256.ivLength());
		assertEquals(4, TlsCipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384.ivLength());
	}
	
	@Test
	void ivLengthForTls13Suites() {
		assertEquals(12, TlsCipherSuite.TLS_AES_128_GCM_SHA256.ivLength());
		assertEquals(12, TlsCipherSuite.TLS_AES_256_GCM_SHA384.ivLength());
		assertEquals(12, TlsCipherSuite.TLS_CHACHA20_POLY1305_SHA256.ivLength());
	}
	
	@Test
	void ivLengthForTls12ChaChaSuites() {
		assertEquals(12, TlsCipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256.ivLength());
		assertEquals(12, TlsCipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256.ivLength());
	}
	
	@Test
	void keyLengthMatchesAead() {
		assertEquals(16, TlsCipherSuite.TLS_AES_128_GCM_SHA256.keyLength());
		assertEquals(32, TlsCipherSuite.TLS_AES_256_GCM_SHA384.keyLength());
		assertEquals(32, TlsCipherSuite.TLS_CHACHA20_POLY1305_SHA256.keyLength());
	}
	
	@Test
	void createCipherReturnsCipherForSuiteVersion() {
		TlsRecordCipher cipher = TlsCipherSuite.TLS_AES_128_GCM_SHA256.createCipher(new byte[16], new byte[12]);
		assertInstanceOf(TlsAeadRecordCipher.class, cipher);
		assertEquals(Optional.of(TlsVersion.TLS_1_3), cipher.version());
		assertEquals(16640, cipher.maxBodyLength());
	}
	
	@Test
	void hashMatchesSuiteName() {
		for (TlsCipherSuite suite : TlsCipherSuite.values()) {
			HashAlgorithm expected = suite.name().endsWith("SHA384") ? HashAlgorithm.SHA_384 : HashAlgorithm.SHA_256;
			assertEquals(expected, suite.hash());
		}
	}
	
	@Test
	void versionMatchesSuiteNamePrefix() {
		for (TlsCipherSuite suite : TlsCipherSuite.values()) {
			TlsVersion expected = suite.name().startsWith("TLS_ECDHE_") ? TlsVersion.TLS_1_2 : TlsVersion.TLS_1_3;
			assertEquals(expected, suite.version());
		}
	}
	
	@Test
	void createCipherForEverySuiteWithDerivedLengths() {
		for (TlsCipherSuite suite : TlsCipherSuite.values()) {
			TlsRecordCipher cipher = assertDoesNotThrow(() -> suite.createCipher(new byte[suite.keyLength()], new byte[suite.ivLength()]));
			assertEquals(Optional.of(suite.version()), cipher.version());
		}
	}
	
	@Test
	void createdCipherRoundTrip() throws TlsAlertException {
		TlsRecordCipher cipher = TlsCipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384.createCipher(new byte[32], new byte[4]);
		byte[] fragment = "data".getBytes(StandardCharsets.UTF_8);
		TlsRecord record = cipher.decrypt(0, TlsContentType.HANDSHAKE, cipher.encrypt(0, TlsContentType.HANDSHAKE, fragment));
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(fragment, record.data());
	}
	
	@Test
	void byIdRoundTripForEverySuite() {
		for (TlsCipherSuite suite : TlsCipherSuite.values()) {
			assertEquals(Optional.of(suite), TlsCipherSuite.byId(suite.id()));
		}
	}
	
	@Test
	void createCipherCopiesKeyAndIv() throws TlsAlertException {
		TlsCipherSuite suite = TlsCipherSuite.TLS_CHACHA20_POLY1305_SHA256;
		byte[] key = new byte[32];
		byte[] iv = new byte[12];
		TlsRecordCipher first = suite.createCipher(key, iv);
		Arrays.fill(key, (byte) 1);
		Arrays.fill(iv, (byte) 1);
		
		byte[] body = first.encrypt(0, TlsContentType.APPLICATION_DATA, new byte[] { 1, 2, 3 });
		TlsRecordCipher second = suite.createCipher(new byte[32], new byte[12]);
		TlsRecord record = second.decrypt(0, TlsContentType.APPLICATION_DATA, body);
		assertArrayEquals(new byte[] { 1, 2, 3 }, record.data());
	}
}
