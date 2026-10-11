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

import net.luis.utils.crypto.Aeads;
import net.luis.utils.crypto.algorithm.AeadAlgorithm;
import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TlsAeadRecordCipher}.<br>
 *
 * @author Luis-St
 */
class TlsAeadRecordCipherTest {
	
	private static final byte[] DATA = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
	private static final AeadAlgorithm[] TLS_ALGORITHMS = { AeadAlgorithm.AES_128_GCM, AeadAlgorithm.AES_256_GCM, AeadAlgorithm.CHACHA20_POLY1305 };
	
	private static @NonNull TlsAeadRecordCipher cipher(@NonNull TlsVersion version, @NonNull AeadAlgorithm algorithm) {
		return new TlsAeadRecordCipher(version, algorithm, new byte[algorithm.keyLength()], new byte[TlsAeadRecordCipher.ivLength(version, algorithm)]);
	}
	
	private static byte @NonNull [] sealTls13(@NonNull AeadAlgorithm algorithm, long sequence, byte @NonNull [] inner) {
		byte[] nonce = CryptoBytes.xor(new byte[algorithm.nonceLength()], algorithm.sequenceNonce(sequence));
		byte[] header = TlsRecord.header(TlsContentType.APPLICATION_DATA, inner.length + algorithm.tagLength());
		return Aeads.encrypt(algorithm, Aeads.key(algorithm, new byte[algorithm.keyLength()]), nonce, inner, header);
	}
	
	@Test
	void constructTls13AesGcm() {
		TlsAeadRecordCipher cipher = new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM, new byte[16], new byte[12]);
		assertEquals(Optional.of(TlsVersion.TLS_1_3), cipher.version());
		assertEquals(16640, cipher.maxBodyLength());
	}
	
	@Test
	void constructTls12AesGcmWithSalt() {
		TlsAeadRecordCipher cipher = assertDoesNotThrow(() -> new TlsAeadRecordCipher(TlsVersion.TLS_1_2, AeadAlgorithm.AES_256_GCM, new byte[32], new byte[4]));
		assertEquals(Optional.of(TlsVersion.TLS_1_2), cipher.version());
		assertEquals(18432, cipher.maxBodyLength());
	}
	
	@Test
	void constructTls12ChaCha() {
		assertDoesNotThrow(() -> new TlsAeadRecordCipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305, new byte[32], new byte[12]));
	}
	
	@Test
	void constructWithNullVersion() {
		assertThrows(NullPointerException.class, () -> new TlsAeadRecordCipher(null, AeadAlgorithm.AES_128_GCM, new byte[16], new byte[12]));
	}
	
	@Test
	void constructWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> new TlsAeadRecordCipher(TlsVersion.TLS_1_3, null, new byte[16], new byte[12]));
	}
	
	@Test
	void constructWithNullKey() {
		assertThrows(NullPointerException.class, () -> new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM, null, new byte[12]));
	}
	
	@Test
	void constructWithNullIv() {
		assertThrows(NullPointerException.class, () -> new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM, new byte[16], null));
	}
	
	@Test
	void constructWithUnsupportedAlgorithm() {
		assertThrows(IllegalArgumentException.class, () -> new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_256_GCM_SIV, new byte[32], new byte[12]));
		assertThrows(IllegalArgumentException.class, () -> new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.XCHACHA20_POLY1305, new byte[32], new byte[24]));
	}
	
	@Test
	void constructWithWrongIvLength() {
		assertThrows(IllegalArgumentException.class, () -> new TlsAeadRecordCipher(TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM, new byte[16], new byte[12]));
		assertThrows(IllegalArgumentException.class, () -> new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM, new byte[16], new byte[4]));
	}
	
	@Test
	void constructWithWrongKeyLength() {
		assertThrows(IllegalArgumentException.class, () -> new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_256_GCM, new byte[16], new byte[12]));
	}
	
	@Test
	void ivLengthWithNullVersion() {
		assertThrows(NullPointerException.class, () -> TlsAeadRecordCipher.ivLength(null, AeadAlgorithm.AES_128_GCM));
	}
	
	@Test
	void ivLengthWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> TlsAeadRecordCipher.ivLength(TlsVersion.TLS_1_3, null));
	}
	
	@Test
	void ivLengthWithUnsupportedAlgorithm() {
		assertThrows(IllegalArgumentException.class, () -> TlsAeadRecordCipher.ivLength(TlsVersion.TLS_1_3, AeadAlgorithm.AES_256_GCM_SIV));
		assertThrows(IllegalArgumentException.class, () -> TlsAeadRecordCipher.ivLength(TlsVersion.TLS_1_2, AeadAlgorithm.XCHACHA20_POLY1305));
	}
	
	@Test
	void encryptWithNullType() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		assertThrows(NullPointerException.class, () -> cipher.encrypt(0, null, DATA));
	}
	
	@Test
	void encryptWithNullFragment() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		assertThrows(NullPointerException.class, () -> cipher.encrypt(0, TlsContentType.HANDSHAKE, null));
	}
	
	@Test
	void decryptWithNullType() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		assertThrows(NullPointerException.class, () -> cipher.decrypt(0, null, new byte[32]));
	}
	
	@Test
	void decryptWithNullBody() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		assertThrows(NullPointerException.class, () -> cipher.decrypt(0, TlsContentType.APPLICATION_DATA, null));
	}
	
	@Test
	void outerContentTypeWithNullType() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		assertThrows(NullPointerException.class, () -> cipher.outerContentType(null));
	}
	
	@Test
	void ivLengthForAesGcmUnderTls12() {
		assertEquals(4, TlsAeadRecordCipher.ivLength(TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM));
		assertEquals(4, TlsAeadRecordCipher.ivLength(TlsVersion.TLS_1_2, AeadAlgorithm.AES_256_GCM));
	}
	
	@Test
	void ivLengthForAesGcmUnderTls13() {
		assertEquals(12, TlsAeadRecordCipher.ivLength(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM));
		assertEquals(12, TlsAeadRecordCipher.ivLength(TlsVersion.TLS_1_3, AeadAlgorithm.AES_256_GCM));
	}
	
	@Test
	void ivLengthForChaCha() {
		assertEquals(12, TlsAeadRecordCipher.ivLength(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305));
		assertEquals(12, TlsAeadRecordCipher.ivLength(TlsVersion.TLS_1_3, AeadAlgorithm.CHACHA20_POLY1305));
	}
	
	@Test
	void encryptTls13AppendsTypeAndTag() {
		byte[] body = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM).encrypt(0, TlsContentType.APPLICATION_DATA, DATA);
		assertEquals(10 + 1 + 16, body.length);
	}
	
	@Test
	void encryptTls12AesGcmPrependsExplicitNonce() {
		long sequence = 0x0102030405060708L;
		byte[] body = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM).encrypt(sequence, TlsContentType.APPLICATION_DATA, DATA);
		assertEquals(8 + 10 + 16, body.length);
		assertArrayEquals(CryptoBytes.of(sequence), Arrays.copyOf(body, 8));
	}
	
	@Test
	void encryptTls12ChaChaHasNoPrefix() {
		byte[] body = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305).encrypt(0, TlsContentType.APPLICATION_DATA, DATA);
		assertEquals(10 + 16, body.length);
	}
	
	@Test
	void decryptBodyShorterThanTag() {
		TlsAlertException tls13 = assertThrows(TlsAlertException.class, () -> cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM).decrypt(0, TlsContentType.APPLICATION_DATA, new byte[15]));
		assertEquals(TlsAlertDescription.BAD_RECORD_MAC, tls13.alert().description());
		assertTrue(tls13.isLocal());
		
		TlsAlertException tls12 = assertThrows(TlsAlertException.class, () -> cipher(TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM).decrypt(0, TlsContentType.APPLICATION_DATA, new byte[23]));
		assertEquals(TlsAlertDescription.BAD_RECORD_MAC, tls12.alert().description());
		assertTrue(tls12.isLocal());
	}
	
	@Test
	void decryptBodyExactlyTagLength() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305);
		TlsAlertException exception = assertThrows(TlsAlertException.class, () -> cipher.decrypt(0, TlsContentType.APPLICATION_DATA, new byte[16]));
		assertEquals(TlsAlertDescription.BAD_RECORD_MAC, exception.alert().description());
		assertTrue(exception.getMessage().contains("failed to authenticate"));
	}
	
	@Test
	void decryptTls12PlaintextAboveLimit() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305);
		TlsAlertException exception = assertThrows(TlsAlertException.class, () -> cipher.decrypt(0, TlsContentType.APPLICATION_DATA, new byte[16384 + 1 + 16]));
		assertEquals(TlsAlertDescription.RECORD_OVERFLOW, exception.alert().description());
	}
	
	@Test
	void decryptTls13PlaintextAboveLimit() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		TlsAlertException exception = assertThrows(TlsAlertException.class, () -> cipher.decrypt(0, TlsContentType.APPLICATION_DATA, new byte[16384 + 2 + 16]));
		assertEquals(TlsAlertDescription.RECORD_OVERFLOW, exception.alert().description());
	}
	
	@Test
	void decryptTls13PlaintextAtLimit() throws TlsAlertException {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		byte[] fragment = new byte[16384];
		Arrays.fill(fragment, (byte) 7);
		
		TlsRecord record = cipher.decrypt(0, TlsContentType.APPLICATION_DATA, cipher.encrypt(0, TlsContentType.APPLICATION_DATA, fragment));
		assertEquals(TlsContentType.APPLICATION_DATA, record.type());
		assertArrayEquals(fragment, record.data());
	}
	
	@Test
	void decryptTls13RecoversInnerType() throws TlsAlertException {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		TlsRecord record = cipher.decrypt(0, TlsContentType.APPLICATION_DATA, cipher.encrypt(0, TlsContentType.HANDSHAKE, DATA));
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(DATA, record.data());
	}
	
	@Test
	void decryptTls12AesGcmUsesExplicitNonce() throws TlsAlertException {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM);
		TlsRecord record = cipher.decrypt(3, TlsContentType.HANDSHAKE, cipher.encrypt(3, TlsContentType.HANDSHAKE, DATA));
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(DATA, record.data());
	}
	
	@Test
	void decryptTls12ChaChaUsesImplicitNonce() throws TlsAlertException {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305);
		TlsRecord record = cipher.decrypt(3, TlsContentType.HANDSHAKE, cipher.encrypt(3, TlsContentType.HANDSHAKE, DATA));
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(DATA, record.data());
	}
	
	@Test
	void decryptTamperedBody() {
		List<TlsAeadRecordCipher> ciphers = List.of(cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM), cipher(TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM), cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305));
		for (TlsAeadRecordCipher cipher : ciphers) {
			TlsContentType outer = cipher.outerContentType(TlsContentType.HANDSHAKE);
			byte[] body = cipher.encrypt(0, TlsContentType.HANDSHAKE, DATA);
			body[body.length - 1] ^= 1;
			
			TlsAlertException exception = assertThrows(TlsAlertException.class, () -> cipher.decrypt(0, outer, body));
			assertEquals(TlsAlertDescription.BAD_RECORD_MAC, exception.alert().description());
		}
	}
	
	@Test
	void decryptTls13StripsZeroPadding() throws TlsAlertException {
		byte[] inner = CryptoBytes.concat(DATA, new byte[] { 22, 0, 0, 0 });
		byte[] body = sealTls13(AeadAlgorithm.AES_128_GCM, 0, inner);
		
		TlsRecord record = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM).decrypt(0, TlsContentType.APPLICATION_DATA, body);
		assertEquals(TlsContentType.HANDSHAKE, record.type());
		assertArrayEquals(DATA, record.data());
	}
	
	@Test
	void decryptTls13WithOnlyZeros() {
		byte[] body = sealTls13(AeadAlgorithm.AES_128_GCM, 0, new byte[4]);
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		
		TlsAlertException exception = assertThrows(TlsAlertException.class, () -> cipher.decrypt(0, TlsContentType.APPLICATION_DATA, body));
		assertEquals(TlsAlertDescription.UNEXPECTED_MESSAGE, exception.alert().description());
	}
	
	@Test
	void decryptTls13WithUnknownInnerType() {
		byte[] body = sealTls13(AeadAlgorithm.AES_128_GCM, 0, CryptoBytes.concat(DATA, new byte[] { 99 }));
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		
		TlsAlertException exception = assertThrows(TlsAlertException.class, () -> cipher.decrypt(0, TlsContentType.APPLICATION_DATA, body));
		assertEquals(TlsAlertDescription.UNEXPECTED_MESSAGE, exception.alert().description());
	}
	
	@Test
	void outerContentTypeTls13HidesType() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		assertEquals(TlsContentType.APPLICATION_DATA, cipher.outerContentType(TlsContentType.HANDSHAKE));
		assertEquals(TlsContentType.APPLICATION_DATA, cipher.outerContentType(TlsContentType.ALERT));
		assertEquals(TlsContentType.APPLICATION_DATA, cipher.outerContentType(TlsContentType.CHANGE_CIPHER_SPEC));
	}
	
	@Test
	void outerContentTypeTls12KeepsType() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM);
		for (TlsContentType type : TlsContentType.values()) {
			assertEquals(type, cipher.outerContentType(type));
		}
	}
	
	@Test
	void encryptEmptyFragment() throws TlsAlertException {
		TlsAeadRecordCipher tls13 = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		byte[] body = tls13.encrypt(0, TlsContentType.APPLICATION_DATA, new byte[0]);
		assertEquals(17, body.length);
		assertEquals(0, tls13.decrypt(0, TlsContentType.APPLICATION_DATA, body).length());
		
		assertEquals(24, cipher(TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM).encrypt(0, TlsContentType.APPLICATION_DATA, new byte[0]).length);
		assertEquals(16, cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305).encrypt(0, TlsContentType.APPLICATION_DATA, new byte[0]).length);
	}
	
	@Test
	void encryptIsDeterministicPerSequence() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.CHACHA20_POLY1305);
		byte[] first = cipher.encrypt(7, TlsContentType.APPLICATION_DATA, DATA);
		byte[] second = cipher.encrypt(7, TlsContentType.APPLICATION_DATA, DATA);
		byte[] other = cipher.encrypt(8, TlsContentType.APPLICATION_DATA, DATA);
		assertArrayEquals(first, second);
		assertFalse(Arrays.equals(first, other));
	}
	
	@Test
	void toStringShowsAlgorithmAndVersion() {
		assertEquals("AES_128_GCM/TLSv1.3", cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM).toString());
	}
	
	@Test
	void maxBodyLengthMatchesVersion() {
		for (TlsVersion version : TlsVersion.values()) {
			assertEquals(version.maxCiphertextLength(), cipher(version, AeadAlgorithm.AES_256_GCM).maxBodyLength());
		}
	}
	
	@Test
	void decryptTls12WithWrongSequence() {
		for (AeadAlgorithm algorithm : new AeadAlgorithm[] { AeadAlgorithm.AES_128_GCM, AeadAlgorithm.CHACHA20_POLY1305 }) {
			TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_2, algorithm);
			byte[] body = cipher.encrypt(0, TlsContentType.APPLICATION_DATA, DATA);
			
			TlsAlertException exception = assertThrows(TlsAlertException.class, () -> cipher.decrypt(1, TlsContentType.APPLICATION_DATA, body));
			assertEquals(TlsAlertDescription.BAD_RECORD_MAC, exception.alert().description());
		}
	}
	
	@Test
	void decryptTls12WithWrongType() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM);
		byte[] body = cipher.encrypt(0, TlsContentType.HANDSHAKE, DATA);
		
		TlsAlertException exception = assertThrows(TlsAlertException.class, () -> cipher.decrypt(0, TlsContentType.APPLICATION_DATA, body));
		assertEquals(TlsAlertDescription.BAD_RECORD_MAC, exception.alert().description());
	}
	
	@Test
	void decryptTls13WithWrongHeaderType() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		byte[] body = cipher.encrypt(0, TlsContentType.HANDSHAKE, DATA);
		
		TlsAlertException exception = assertThrows(TlsAlertException.class, () -> cipher.decrypt(0, TlsContentType.HANDSHAKE, body));
		assertEquals(TlsAlertDescription.BAD_RECORD_MAC, exception.alert().description());
	}
	
	@Test
	void decryptTls13WithWrongSequence() {
		TlsAeadRecordCipher cipher = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		byte[] body = cipher.encrypt(0, TlsContentType.HANDSHAKE, DATA);
		
		TlsAlertException exception = assertThrows(TlsAlertException.class, () -> cipher.decrypt(1, TlsContentType.APPLICATION_DATA, body));
		assertEquals(TlsAlertDescription.BAD_RECORD_MAC, exception.alert().description());
	}
	
	@Test
	void decryptWithDifferentKey() {
		byte[] otherKey = new byte[16];
		Arrays.fill(otherKey, (byte) 1);
		TlsAeadRecordCipher sender = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		TlsAeadRecordCipher receiver = new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM, otherKey, new byte[12]);
		byte[] body = sender.encrypt(0, TlsContentType.APPLICATION_DATA, DATA);
		
		TlsAlertException exception = assertThrows(TlsAlertException.class, () -> receiver.decrypt(0, TlsContentType.APPLICATION_DATA, body));
		assertEquals(TlsAlertDescription.BAD_RECORD_MAC, exception.alert().description());
	}
	
	@Test
	void roundTripForAllTlsCombinations() throws TlsAlertException {
		for (TlsVersion version : TlsVersion.values()) {
			for (AeadAlgorithm algorithm : TLS_ALGORITHMS) {
				TlsAeadRecordCipher cipher = cipher(version, algorithm);
				for (TlsContentType type : TlsContentType.values()) {
					for (long sequence : new long[] { 0, 1, Long.MAX_VALUE - 1 }) {
						TlsRecord record = cipher.decrypt(sequence, cipher.outerContentType(type), cipher.encrypt(sequence, type, DATA));
						assertEquals(type, record.type());
						assertArrayEquals(DATA, record.data());
					}
				}
			}
		}
	}
	
	@Test
	void constructCopiesKeyAndIv() throws TlsAlertException {
		byte[] key = new byte[16];
		byte[] iv = new byte[12];
		TlsAeadRecordCipher first = new TlsAeadRecordCipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM, key, iv);
		Arrays.fill(key, (byte) 1);
		Arrays.fill(iv, (byte) 1);
		
		byte[] body = first.encrypt(0, TlsContentType.APPLICATION_DATA, DATA);
		TlsRecord record = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM).decrypt(0, TlsContentType.APPLICATION_DATA, body);
		assertArrayEquals(DATA, record.data());
	}
	
	@Test
	void encryptWithNegativeSequence() {
		TlsAeadRecordCipher tls13 = cipher(TlsVersion.TLS_1_3, AeadAlgorithm.AES_128_GCM);
		TlsAeadRecordCipher tls12ChaCha = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.CHACHA20_POLY1305);
		TlsAeadRecordCipher tls12AesGcm = cipher(TlsVersion.TLS_1_2, AeadAlgorithm.AES_128_GCM);
		assertThrows(IllegalArgumentException.class, () -> tls13.encrypt(-1, TlsContentType.APPLICATION_DATA, DATA));
		assertThrows(IllegalArgumentException.class, () -> tls12ChaCha.encrypt(-1, TlsContentType.APPLICATION_DATA, DATA));
		assertDoesNotThrow(() -> tls12AesGcm.encrypt(-1, TlsContentType.APPLICATION_DATA, DATA));
	}
}
