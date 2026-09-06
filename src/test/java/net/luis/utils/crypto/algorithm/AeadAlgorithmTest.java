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


package net.luis.utils.crypto.algorithm;

import net.luis.utils.crypto.Aeads;
import net.luis.utils.crypto.Providers;
import net.luis.utils.crypto.exception.AuthenticationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.spec.*;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link AeadAlgorithm}.<br>
 *
 * @author Luis-St
 */
class AeadAlgorithmTest {
	
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final byte[] PLAINTEXT = "crypto".getBytes();
	private static final byte[] AAD = "header".getBytes();
	
	//region Setup
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	//endregion
	
	private static byte[] random(int length) {
		byte[] bytes = new byte[length];
		RANDOM.nextBytes(bytes);
		return bytes;
	}
	
	private static SecretKeySpec key(AeadAlgorithm algorithm) {
		return new SecretKeySpec(random(algorithm.keyLength()), algorithm.keyJcaName());
	}
	
	@Test
	void parameterSpecWithNullNonce() {
		assertThrows(NullPointerException.class, () -> AeadAlgorithm.AES_256_GCM.parameterSpec(null));
	}
	
	@Test
	void parameterSpecWithNullNonceForEveryConstant() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			assertThrows(NullPointerException.class, () -> algorithm.parameterSpec(null));
		}
	}
	
	@Test
	void parameterSpecWithEmptyNonce() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			AlgorithmParameterSpec spec = assertDoesNotThrow(() -> algorithm.parameterSpec(new byte[0]));
			assertNotNull(spec);
			if (spec instanceof GCMParameterSpec gcm) {
				assertEquals(0, gcm.getIV().length);
			} else {
				assertEquals(0, assertInstanceOf(IvParameterSpec.class, spec).getIV().length);
			}
		}
	}
	
	@Test
	void randomNonceMessageLimitForUnlimitedModes() {
		assertEquals(Long.MAX_VALUE, AeadAlgorithm.AES_256_GCM_SIV.randomNonceMessageLimit());
		assertEquals(Long.MAX_VALUE, AeadAlgorithm.XCHACHA20_POLY1305.randomNonceMessageLimit());
	}
	
	@Test
	void randomNonceMessageLimitForNonceRespectingModes() {
		assertEquals(1L << 32, AeadAlgorithm.AES_256_GCM.randomNonceMessageLimit());
		assertEquals(1L << 32, AeadAlgorithm.CHACHA20_POLY1305.randomNonceMessageLimit());
		assertEquals(4294967296L, AeadAlgorithm.AES_256_GCM.randomNonceMessageLimit());
	}
	
	@Test
	void randomNonceMessageLimitCoversEveryConstant() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			long limit = algorithm.randomNonceMessageLimit();
			assertTrue(limit == Long.MAX_VALUE || limit == 1L << 32);
			if (algorithm.nonceLength() >= 24) {
				assertEquals(Long.MAX_VALUE, limit);
			}
		}
	}
	
	@Test
	void parameterSpecForChaCha20Modes() {
		byte[] nonce12 = random(12);
		AlgorithmParameterSpec chacha = AeadAlgorithm.CHACHA20_POLY1305.parameterSpec(nonce12);
		assertArrayEquals(nonce12, assertInstanceOf(IvParameterSpec.class, chacha).getIV());
		
		byte[] nonce24 = random(24);
		AlgorithmParameterSpec xchacha = AeadAlgorithm.XCHACHA20_POLY1305.parameterSpec(nonce24);
		assertArrayEquals(nonce24, assertInstanceOf(IvParameterSpec.class, xchacha).getIV());
	}
	
	@Test
	void parameterSpecForGcmModes() {
		byte[] nonce = random(12);
		for (AeadAlgorithm algorithm : new AeadAlgorithm[] { AeadAlgorithm.AES_256_GCM, AeadAlgorithm.AES_256_GCM_SIV }) {
			GCMParameterSpec spec = assertInstanceOf(GCMParameterSpec.class, algorithm.parameterSpec(nonce));
			assertEquals(128, spec.getTLen());
			assertArrayEquals(nonce, spec.getIV());
		}
	}
	
	@Test
	void parameterSpecCoversEveryConstant() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			AlgorithmParameterSpec spec = algorithm.parameterSpec(random(algorithm.nonceLength()));
			assertNotNull(spec);
			assertTrue(spec instanceof IvParameterSpec || spec instanceof GCMParameterSpec);
		}
	}
	
	@Test
	void requiresBouncyCastleBothValues() {
		assertTrue(AeadAlgorithm.AES_256_GCM_SIV.requiresBouncyCastle());
		assertTrue(AeadAlgorithm.XCHACHA20_POLY1305.requiresBouncyCastle());
		assertFalse(AeadAlgorithm.AES_256_GCM.requiresBouncyCastle());
		assertFalse(AeadAlgorithm.CHACHA20_POLY1305.requiresBouncyCastle());
	}
	
	@Test
	void jcaNameMatchesForEveryConstant() {
		assertEquals("AES/GCM/NoPadding", AeadAlgorithm.AES_256_GCM.jcaName());
		assertEquals("AES/GCM-SIV/NoPadding", AeadAlgorithm.AES_256_GCM_SIV.jcaName());
		assertEquals("ChaCha20-Poly1305", AeadAlgorithm.CHACHA20_POLY1305.jcaName());
		assertEquals("XChaCha20-Poly1305", AeadAlgorithm.XCHACHA20_POLY1305.jcaName());
		assertEquals(4, Arrays.stream(AeadAlgorithm.values()).map(AeadAlgorithm::jcaName).distinct().count());
	}
	
	@Test
	void keyJcaNameMatchesForEveryConstant() {
		assertEquals("AES", AeadAlgorithm.AES_256_GCM.keyJcaName());
		assertEquals("AES", AeadAlgorithm.AES_256_GCM_SIV.keyJcaName());
		assertEquals("ChaCha20", AeadAlgorithm.CHACHA20_POLY1305.keyJcaName());
		assertEquals("ChaCha20", AeadAlgorithm.XCHACHA20_POLY1305.keyJcaName());
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			assertDoesNotThrow(() -> new SecretKeySpec(random(algorithm.keyLength()), algorithm.keyJcaName()));
		}
	}
	
	@Test
	void lengthAccessorsForEveryConstant() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			assertEquals(32, algorithm.keyLength());
			assertEquals(16, algorithm.tagLength());
			assertTrue(algorithm.nonceLength() > 0);
		}
		assertEquals(12, AeadAlgorithm.AES_256_GCM.nonceLength());
		assertEquals(12, AeadAlgorithm.AES_256_GCM_SIV.nonceLength());
		assertEquals(12, AeadAlgorithm.CHACHA20_POLY1305.nonceLength());
		assertEquals(24, AeadAlgorithm.XCHACHA20_POLY1305.nonceLength());
	}
	
	@Test
	void nonceLengthDistinguishesXChaCha20() {
		assertEquals(24, AeadAlgorithm.XCHACHA20_POLY1305.nonceLength());
		assertEquals(2 * AeadAlgorithm.CHACHA20_POLY1305.nonceLength(), AeadAlgorithm.XCHACHA20_POLY1305.nonceLength());
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			if (algorithm != AeadAlgorithm.XCHACHA20_POLY1305) {
				assertEquals(12, algorithm.nonceLength());
			}
		}
	}
	
	@Test
	void parameterSpecReturnsIndependentSpecs() {
		byte[] nonce = random(12);
		AlgorithmParameterSpec first = AeadAlgorithm.AES_256_GCM.parameterSpec(nonce);
		AlgorithmParameterSpec second = AeadAlgorithm.AES_256_GCM.parameterSpec(nonce);
		assertNotSame(first, second);
		
		byte[] copy = ((GCMParameterSpec) first).getIV();
		nonce[0] ^= 0x01;
		assertArrayEquals(copy, ((GCMParameterSpec) first).getIV());
	}
	
	@Test
	void parameterSpecTagLengthInBits() {
		for (AeadAlgorithm algorithm : new AeadAlgorithm[] { AeadAlgorithm.AES_256_GCM, AeadAlgorithm.AES_256_GCM_SIV }) {
			GCMParameterSpec spec = (GCMParameterSpec) algorithm.parameterSpec(random(algorithm.nonceLength()));
			assertEquals(algorithm.tagLength() * Byte.SIZE, spec.getTLen());
		}
	}
	
	@Test
	void cipherRoundTripForEveryConstant() throws Exception {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			assumeTrue(Providers.supports(algorithm));
			SecretKeySpec key = key(algorithm);
			byte[] nonce = random(algorithm.nonceLength());
			
			Cipher encrypt = Cipher.getInstance(algorithm.jcaName());
			encrypt.init(Cipher.ENCRYPT_MODE, key, algorithm.parameterSpec(nonce));
			encrypt.updateAAD(AAD);
			byte[] ciphertext = encrypt.doFinal(PLAINTEXT);
			assertEquals(PLAINTEXT.length + algorithm.tagLength(), ciphertext.length);
			
			Cipher decrypt = Cipher.getInstance(algorithm.jcaName());
			decrypt.init(Cipher.DECRYPT_MODE, key, algorithm.parameterSpec(nonce));
			decrypt.updateAAD(AAD);
			assertArrayEquals(PLAINTEXT, decrypt.doFinal(ciphertext));
		}
	}
	
	@Test
	void cipherRejectsTamperedCiphertext() throws Exception {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			assumeTrue(Providers.supports(algorithm));
			SecretKeySpec key = key(algorithm);
			byte[] nonce = random(algorithm.nonceLength());
			
			Cipher encrypt = Cipher.getInstance(algorithm.jcaName());
			encrypt.init(Cipher.ENCRYPT_MODE, key, algorithm.parameterSpec(nonce));
			byte[] ciphertext = encrypt.doFinal(PLAINTEXT);
			ciphertext[0] ^= 0x01;
			
			Cipher decrypt = Cipher.getInstance(algorithm.jcaName());
			decrypt.init(Cipher.DECRYPT_MODE, key, algorithm.parameterSpec(nonce));
			assertThrows(AEADBadTagException.class, () -> decrypt.doFinal(ciphertext));
		}
	}
	
	@Test
	void nonceMisuseResistanceOfGcmSiv() throws Exception {
		for (AeadAlgorithm algorithm : new AeadAlgorithm[] { AeadAlgorithm.AES_256_GCM_SIV, AeadAlgorithm.AES_256_GCM }) {
			assumeTrue(Providers.supports(algorithm));
			SecretKeySpec key = key(algorithm);
			byte[] nonce = random(algorithm.nonceLength());
			
			Cipher first = Cipher.getInstance(algorithm.jcaName());
			first.init(Cipher.ENCRYPT_MODE, key, algorithm.parameterSpec(nonce));
			Cipher second = Cipher.getInstance(algorithm.jcaName());
			second.init(Cipher.ENCRYPT_MODE, key, algorithm.parameterSpec(nonce));
			assertArrayEquals(first.doFinal(PLAINTEXT), second.doFinal(PLAINTEXT));
		}
	}
	
	@Test
	void xchacha20AcceptsBothParameterSpecShapes() throws Exception {
		AeadAlgorithm algorithm = AeadAlgorithm.XCHACHA20_POLY1305;
		assumeTrue(Providers.supports(algorithm));
		SecretKeySpec key = key(algorithm);
		byte[] nonce = random(24);
		
		Cipher withIv = Cipher.getInstance(algorithm.jcaName());
		withIv.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(nonce));
		Cipher withGcm = Cipher.getInstance(algorithm.jcaName());
		assertDoesNotThrow(() -> withGcm.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(algorithm.tagLength() * Byte.SIZE, nonce)));
		assertArrayEquals(withIv.doFinal(PLAINTEXT), withGcm.doFinal(PLAINTEXT));
	}
	
	@Test
	void xchacha20RejectsWrongNonceWidth() {
		AeadAlgorithm algorithm = AeadAlgorithm.XCHACHA20_POLY1305;
		assumeTrue(Providers.supports(algorithm));
		byte[] nonce = random(12);
		AlgorithmParameterSpec spec = assertDoesNotThrow(() -> algorithm.parameterSpec(nonce));
		assertEquals(12, ((IvParameterSpec) spec).getIV().length);
		
		assertThrows(Exception.class, () -> {
			Cipher cipher = Cipher.getInstance(algorithm.jcaName());
			cipher.init(Cipher.ENCRYPT_MODE, key(algorithm), spec);
		});
	}
	
	@Test
	void sequenceNonceWithNegativeSequence() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> AeadAlgorithm.AES_256_GCM.sequenceNonce(-1));
		assertTrue(exception.getMessage().contains("-1"));
	}
	
	@Test
	void sequenceNonceWithMinimumSequence() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			assertThrows(IllegalArgumentException.class, () -> algorithm.sequenceNonce(Long.MIN_VALUE));
		}
	}
	
	@Test
	void sequenceNonceWithZeroSequence() {
		byte[] nonce = AeadAlgorithm.AES_256_GCM.sequenceNonce(0);
		assertEquals(AeadAlgorithm.AES_256_GCM.nonceLength(), nonce.length);
		assertArrayEquals(new byte[12], nonce);
	}
	
	@Test
	void sequenceNonceWithPositiveSequence() {
		byte[] nonce = AeadAlgorithm.AES_256_GCM.sequenceNonce(1);
		assertEquals(12, nonce.length);
		assertEquals(1, nonce[nonce.length - 1]);
		assertArrayEquals(new byte[11], Arrays.copyOf(nonce, 11));
	}
	
	@Test
	void sequenceNonceLengthMatchesEveryAlgorithm() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			assertEquals(algorithm.nonceLength(), algorithm.sequenceNonce(7).length);
		}
		assertEquals(12, AeadAlgorithm.AES_256_GCM.sequenceNonce(7).length);
		assertEquals(12, AeadAlgorithm.AES_256_GCM_SIV.sequenceNonce(7).length);
		assertEquals(12, AeadAlgorithm.CHACHA20_POLY1305.sequenceNonce(7).length);
		assertEquals(24, AeadAlgorithm.XCHACHA20_POLY1305.sequenceNonce(7).length);
	}
	
	@Test
	void sequenceNonceIsBigEndian() {
		byte[] nonce = AeadAlgorithm.AES_256_GCM.sequenceNonce(0x0102030405060708L);
		assertArrayEquals(new byte[4], Arrays.copyOf(nonce, 4));
		assertArrayEquals(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8 }, Arrays.copyOfRange(nonce, 4, 12));
	}
	
	@Test
	void sequenceNonceLeavesPrefixZero() {
		byte[] nonce = AeadAlgorithm.XCHACHA20_POLY1305.sequenceNonce(Long.MAX_VALUE);
		assertEquals(24, nonce.length);
		assertArrayEquals(new byte[16], Arrays.copyOf(nonce, 16));
		assertEquals((byte) 0x7F, nonce[16]);
		for (int i = 17; i < nonce.length; i++) {
			assertEquals((byte) 0xFF, nonce[i]);
		}
	}
	
	@Test
	void sequenceNonceWithMaximumSequence() {
		byte[] nonce = assertDoesNotThrow(() -> AeadAlgorithm.AES_256_GCM.sequenceNonce(Long.MAX_VALUE));
		assertEquals(12, nonce.length);
		assertArrayEquals(new byte[] { 0x7F, -1, -1, -1, -1, -1, -1, -1 }, Arrays.copyOfRange(nonce, 4, 12));
	}
	
	@Test
	void sequenceNonceAboveIntRange() {
		byte[] nonce = AeadAlgorithm.AES_256_GCM.sequenceNonce(1L << 32);
		assertEquals(1, nonce[nonce.length - 5]);
		assertArrayEquals(new byte[4], Arrays.copyOfRange(nonce, nonce.length - 4, nonce.length));
	}
	
	@Test
	void sequenceNonceReturnsFreshArray() {
		byte[] first = AeadAlgorithm.AES_256_GCM.sequenceNonce(9);
		byte[] second = AeadAlgorithm.AES_256_GCM.sequenceNonce(9);
		assertNotSame(first, second);
		
		first[0] = 42;
		assertEquals(0, second[0]);
	}
	
	@Test
	void sequenceNonceIsDeterministic() {
		assertArrayEquals(AeadAlgorithm.CHACHA20_POLY1305.sequenceNonce(4711), AeadAlgorithm.CHACHA20_POLY1305.sequenceNonce(4711));
		assertArrayEquals(AeadAlgorithm.XCHACHA20_POLY1305.sequenceNonce(0), AeadAlgorithm.XCHACHA20_POLY1305.sequenceNonce(0));
	}
	
	@Test
	void sequenceNonceIsDistinctPerSequence() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			Set<ByteBuffer> nonces = new HashSet<>();
			for (long sequence = 0; sequence <= 1000; sequence++) {
				nonces.add(ByteBuffer.wrap(algorithm.sequenceNonce(sequence)));
			}
			assertEquals(1001, nonces.size());
		}
	}
	
	@Test
	void sequenceNonceOrdersLikeTheSequence() {
		long[] sequences = { 0, 1, 255, 256, 65535, 65536 };
		for (int i = 1; i < sequences.length; i++) {
			byte[] lower = AeadAlgorithm.AES_256_GCM.sequenceNonce(sequences[i - 1]);
			byte[] higher = AeadAlgorithm.AES_256_GCM.sequenceNonce(sequences[i]);
			assertTrue(Arrays.compareUnsigned(lower, higher) < 0);
		}
	}
	
	@Test
	void sequenceNonceDiffersPerAlgorithmWidth() {
		byte[] narrow = AeadAlgorithm.AES_256_GCM.sequenceNonce(42);
		byte[] wide = AeadAlgorithm.XCHACHA20_POLY1305.sequenceNonce(42);
		
		assertEquals(12, narrow.length);
		assertEquals(24, wide.length);
		assertFalse(Arrays.equals(narrow, wide));
		assertArrayEquals(Arrays.copyOfRange(narrow, 4, 12), Arrays.copyOfRange(wide, 16, 24));
	}
	
	@Test
	void sequenceNonceRoundTripsThroughAead() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			assumeTrue(Providers.supports(algorithm));
			SecretKeySpec key = key(algorithm);
			byte[] ciphertext = Aeads.encrypt(algorithm, key, algorithm.sequenceNonce(42), PLAINTEXT, null);
			
			assertArrayEquals(PLAINTEXT, Aeads.decrypt(algorithm, key, algorithm.sequenceNonce(42), ciphertext, null));
			assertThrows(AuthenticationException.class, () -> Aeads.decrypt(algorithm, key, algorithm.sequenceNonce(43), ciphertext, null));
		}
	}
	
	@Test
	void sequenceNonceWorksWithParameterSpec() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			assertDoesNotThrow(() -> algorithm.parameterSpec(algorithm.sequenceNonce(5)));
		}
	}
}
