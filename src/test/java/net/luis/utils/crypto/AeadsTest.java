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

package net.luis.utils.crypto;

import net.luis.utils.crypto.algorithm.AeadAlgorithm;
import net.luis.utils.crypto.exception.*;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Aeads}.<br>
 *
 * @author Luis-St
 */
class AeadsTest {
	
	private static final AeadAlgorithm ALGORITHM = AeadAlgorithm.AES_256_GCM;
	private static final byte[] PLAINTEXT = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	private static final byte[] AAD = "header".getBytes(StandardCharsets.UTF_8);
	
	private static SecretKey key;
	
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
		key = Aeads.generateKey(ALGORITHM);
	}
	
	private static byte[] nonce(AeadAlgorithm algorithm) {
		return CryptoRandom.bytes(algorithm.nonceLength());
	}
	
	private static SecureRandom seeded() throws Exception {
		SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(new byte[] { 1, 2, 3, 4 });
		return random;
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = Aeads.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Aeads.class.getModifiers()));
		
		Constructor<Aeads> constructor = Aeads.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void generateKeyWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Aeads.generateKey(null)).getMessage());
	}
	
	@Test
	void keyWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Aeads.key(null, new byte[32])).getMessage());
	}
	
	@Test
	void keyWithNullRaw() {
		assertEquals("Raw key must not be null", assertThrows(NullPointerException.class, () -> Aeads.key(ALGORITHM, null)).getMessage());
	}
	
	@Test
	void keyWithBothNull() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Aeads.key(null, null)).getMessage());
	}
	
	@Test
	void keyWithShortRaw() {
		for (int length : new int[] { 31, 0 }) {
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Aeads.key(ALGORITHM, new byte[length]));
			assertTrue(exception.getMessage().contains(ALGORITHM.toString()));
			assertTrue(exception.getMessage().contains("32"));
			assertTrue(exception.getMessage().contains(String.valueOf(length)));
		}
	}
	
	@Test
	void keyWithLongRaw() {
		assertThrows(IllegalArgumentException.class, () -> Aeads.key(ALGORITHM, new byte[33]));
		assertThrows(IllegalArgumentException.class, () -> Aeads.key(ALGORITHM, new byte[64]));
	}
	
	@Test
	void encryptWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Aeads.encrypt(null, key, PLAINTEXT));
		assertThrows(NullPointerException.class, () -> Aeads.encrypt(null, key, PLAINTEXT, AAD));
		assertThrows(NullPointerException.class, () -> Aeads.encrypt(CryptoRandom.instance(), null, key, PLAINTEXT, AAD));
		assertThrows(NullPointerException.class, () -> Aeads.encrypt(null, key, nonce(ALGORITHM), PLAINTEXT, AAD));
	}
	
	@Test
	void encryptWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Aeads.encrypt(ALGORITHM, null, PLAINTEXT)).getMessage());
	}
	
	@Test
	void encryptWithNullPlaintext() {
		assertEquals("Plaintext must not be null", assertThrows(NullPointerException.class, () -> Aeads.encrypt(ALGORITHM, key, null)).getMessage());
	}
	
	@Test
	void encryptWithNullNonce() {
		assertEquals("Nonce must not be null", assertThrows(NullPointerException.class, () -> Aeads.encrypt(ALGORITHM, key, null, PLAINTEXT, AAD)).getMessage());
	}
	
	@Test
	void encryptWithNullRandom() {
		assertEquals("Random must not be null", assertThrows(NullPointerException.class, () -> Aeads.encrypt(null, ALGORITHM, key, PLAINTEXT, AAD)).getMessage());
	}
	
	@Test
	void encryptWithNullPlaintextAndNullAlgorithm() {
		assertEquals("Plaintext must not be null", assertThrows(NullPointerException.class, () -> Aeads.encrypt(null, key, new byte[12], null, AAD)).getMessage());
	}
	
	@Test
	void encryptWithWrongNonceLength() {
		SecretKey chachaKey = Aeads.generateKey(AeadAlgorithm.CHACHA20_POLY1305);
		for (int length : new int[] { 8, 16 }) {
			CryptoException exception = assertThrows(CryptoException.class, () -> Aeads.encrypt(AeadAlgorithm.CHACHA20_POLY1305, chachaKey, new byte[length], PLAINTEXT, null));
			assertTrue(exception.getMessage().contains("Encryption failed for"));
			assertTrue(exception.getMessage().contains(AeadAlgorithm.CHACHA20_POLY1305.toString()));
			assertInstanceOf(GeneralSecurityException.class, exception.getCause());
		}
		
		assertDoesNotThrow(() -> Aeads.encrypt(ALGORITHM, key, new byte[8], PLAINTEXT, null));
		assertDoesNotThrow(() -> Aeads.encrypt(ALGORITHM, key, new byte[16], PLAINTEXT, null));
		assertThrows(CryptoException.class, () -> Aeads.encrypt(ALGORITHM, key, new byte[0], PLAINTEXT, null));
	}
	
	@Test
	void encryptWithWrongKeyLength() {
		CryptoException exception = assertThrows(CryptoException.class, () -> Aeads.encrypt(ALGORITHM, new SecretKeySpec(new byte[17], "AES"), nonce(ALGORITHM), PLAINTEXT, null));
		assertInstanceOf(InvalidKeyException.class, exception.getCause());
	}
	
	@Test
	void encryptWithWrongKeyAlgorithmName() {
		SecretKey misnamed = new SecretKeySpec(new byte[32], "ChaCha20");
		byte[] nonce = nonce(ALGORITHM);
		byte[] body = assertDoesNotThrow(() -> Aeads.encrypt(ALGORITHM, misnamed, nonce, PLAINTEXT, null));
		
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(ALGORITHM, misnamed, nonce, body, null));
		assertArrayEquals(body, Aeads.encrypt(ALGORITHM, new SecretKeySpec(new byte[32], "AES"), nonce, PLAINTEXT, null));
	}
	
	@Test
	void decryptWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Aeads.decrypt(null, key, new byte[64]));
		assertThrows(NullPointerException.class, () -> Aeads.decrypt(null, key, new byte[64], AAD));
		assertThrows(NullPointerException.class, () -> Aeads.decrypt(null, key, new byte[12], new byte[32], AAD));
	}
	
	@Test
	void decryptWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Aeads.decrypt(ALGORITHM, null, new byte[12], new byte[32], AAD)).getMessage());
	}
	
	@Test
	void decryptWithNullCiphertext() {
		assertEquals("Ciphertext must not be null", assertThrows(NullPointerException.class, () -> Aeads.decrypt(ALGORITHM, key, null)).getMessage());
		assertEquals("Ciphertext must not be null", assertThrows(NullPointerException.class, () -> Aeads.decrypt(ALGORITHM, key, new byte[12], null, AAD)).getMessage());
	}
	
	@Test
	void decryptWithNullNonce() {
		assertEquals("Nonce must not be null", assertThrows(NullPointerException.class, () -> Aeads.decrypt(ALGORITHM, key, null, new byte[32], AAD)).getMessage());
	}
	
	@Test
	void decryptWithTooShortCiphertext() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			for (int length : new int[] { 0, 1, algorithm.nonceLength(), algorithm.nonceLength() + algorithm.tagLength() - 1 }) {
				MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Aeads.decrypt(algorithm, Aeads.generateKey(algorithm), new byte[length]));
				assertTrue(exception.getMessage().contains(algorithm.toString()));
				assertTrue(exception.getMessage().contains(String.valueOf(length)));
			}
		}
	}
	
	@Test
	void decryptWithTamperedCiphertext() {
		byte[] ciphertext = Aeads.encrypt(ALGORITHM, key, PLAINTEXT);
		for (int index : new int[] { 0, ALGORITHM.nonceLength() + 1, ciphertext.length - 1 }) {
			byte[] tampered = ciphertext.clone();
			tampered[index] ^= 1;
			
			AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Aeads.decrypt(ALGORITHM, key, tampered));
			assertTrue(exception.getMessage().contains("Authentication tag mismatch"));
			assertInstanceOf(CryptoException.class, exception);
		}
	}
	
	@Test
	void decryptWithWrongKey() {
		byte[] ciphertext = Aeads.encrypt(ALGORITHM, key, PLAINTEXT);
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Aeads.decrypt(ALGORITHM, Aeads.generateKey(ALGORITHM), ciphertext));
		assertTrue(exception.getMessage().contains("Authentication tag mismatch"));
	}
	
	@Test
	void decryptWithWrongAssociatedData() {
		byte[] ciphertext = Aeads.encrypt(ALGORITHM, key, PLAINTEXT, "header-a".getBytes(StandardCharsets.UTF_8));
		
		assertThrows(AuthenticationException.class, () -> Aeads.decrypt(ALGORITHM, key, ciphertext, "header-b".getBytes(StandardCharsets.UTF_8)));
		assertThrows(AuthenticationException.class, () -> Aeads.decrypt(ALGORITHM, key, ciphertext, null));
	}
	
	@Test
	void decryptWithWrongNonce() {
		byte[] nonce = nonce(ALGORITHM);
		byte[] body = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, null);
		byte[] other = nonce.clone();
		other[0] ^= 1;
		
		assertThrows(AuthenticationException.class, () -> Aeads.decrypt(ALGORITHM, key, other, body, null));
	}
	
	@Test
	void decryptWithWrongAlgorithm() {
		SecretKey chachaKey = Aeads.generateKey(AeadAlgorithm.CHACHA20_POLY1305);
		byte[] nonce = nonce(AeadAlgorithm.CHACHA20_POLY1305);
		byte[] body = Aeads.encrypt(AeadAlgorithm.CHACHA20_POLY1305, chachaKey, nonce, PLAINTEXT, null);
		
		AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Aeads.decrypt(ALGORITHM, chachaKey, nonce, body, null));
		assertInstanceOf(CryptoException.class, exception);
		assertTrue(exception.getMessage().contains("Authentication tag mismatch"));
	}
	
	@Test
	void tryDecryptWithNullCiphertext() {
		assertEquals("Ciphertext must not be null", assertThrows(NullPointerException.class, () -> Aeads.tryDecrypt(ALGORITHM, key, new byte[12], null, AAD)).getMessage());
	}
	
	@Test
	void tryDecryptWithNullNonce() {
		assertThrows(NullPointerException.class, () -> Aeads.tryDecrypt(ALGORITHM, key, null, new byte[32], AAD));
	}
	
	@Test
	void tryDecryptWithBrokenConfiguration() {
		assertThrows(CryptoException.class, () -> Aeads.tryDecrypt(ALGORITHM, new SecretKeySpec(new byte[17], "AES"), nonce(ALGORITHM), new byte[32], null));
	}
	
	@Test
	void cipherWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Aeads.cipher(null, Cipher.ENCRYPT_MODE, key, new byte[12], null)).getMessage());
	}
	
	@Test
	void cipherWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> Aeads.cipher(ALGORITHM, Cipher.ENCRYPT_MODE, null, new byte[12], null)).getMessage());
	}
	
	@Test
	void cipherWithNullNonce() {
		assertEquals("Nonce must not be null", assertThrows(NullPointerException.class, () -> Aeads.cipher(ALGORITHM, Cipher.ENCRYPT_MODE, key, null, null)).getMessage());
	}
	
	@Test
	void cipherWithAllNull() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Aeads.cipher(null, Cipher.ENCRYPT_MODE, null, null, null)).getMessage());
	}
	
	@Test
	void cipherWithInvalidMode() {
		assertThrows(InvalidParameterException.class, () -> Aeads.cipher(ALGORITHM, 99, key, nonce(ALGORITHM), null));
	}
	
	@Test
	void keyWithCorrectLength() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			byte[] raw = CryptoRandom.bytes(algorithm.keyLength());
			SecretKey wrapped = Aeads.key(algorithm, raw);
			
			assertNotNull(wrapped);
			assertEquals(algorithm.keyJcaName(), wrapped.getAlgorithm());
			assertArrayEquals(raw, wrapped.getEncoded());
		}
	}
	
	@Test
	void decryptWithMinimumValidLength() {
		byte[] ciphertext = Aeads.encrypt(ALGORITHM, key, new byte[0]);
		
		assertEquals(ALGORITHM.nonceLength() + ALGORITHM.tagLength(), ciphertext.length);
		assertEquals(0, assertDoesNotThrow(() -> Aeads.decrypt(ALGORITHM, key, ciphertext)).length);
	}
	
	@Test
	void cipherWithNullAssociatedData() {
		byte[] nonce = nonce(ALGORITHM);
		byte[] withNull = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, null);
		byte[] withEmpty = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, new byte[0]);
		
		assertArrayEquals(withNull, withEmpty);
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(ALGORITHM, key, nonce, withNull, null));
	}
	
	@Test
	void cipherWithEmptyAssociatedData() {
		byte[] nonce = nonce(ALGORITHM);
		assertArrayEquals(Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, null), Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, new byte[0]));
	}
	
	@Test
	void cipherWithNonEmptyAssociatedData() {
		byte[] nonce = nonce(ALGORITHM);
		assertFalse(Arrays.equals(Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, null), Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, AAD)));
	}
	
	@Test
	void encryptAndDecryptWithoutAssociatedData() {
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(ALGORITHM, key, Aeads.encrypt(ALGORITHM, key, PLAINTEXT)));
	}
	
	@Test
	void encryptAndDecryptWithAssociatedData() {
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(ALGORITHM, key, Aeads.encrypt(ALGORITHM, key, PLAINTEXT, AAD), AAD));
	}
	
	@Test
	void tryDecryptWithCorrectKey() {
		byte[] nonce = nonce(ALGORITHM);
		byte[] body = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, AAD);
		Optional<byte[]> result = Aeads.tryDecrypt(ALGORITHM, key, nonce, body, AAD);
		
		assertTrue(result.isPresent());
		assertArrayEquals(PLAINTEXT, result.orElseThrow());
	}
	
	@Test
	void tryDecryptWithWrongKey() {
		byte[] nonce = nonce(ALGORITHM);
		byte[] body = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, null);
		assertTrue(assertDoesNotThrow(() -> Aeads.tryDecrypt(ALGORITHM, Aeads.generateKey(ALGORITHM), nonce, body, null)).isEmpty());
	}
	
	@Test
	void tryDecryptWithTamperedCiphertext() {
		byte[] nonce = nonce(ALGORITHM);
		byte[] body = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, null);
		body[0] ^= 1;
		
		assertTrue(Aeads.tryDecrypt(ALGORITHM, key, nonce, body, null).isEmpty());
	}
	
	@Test
	void tryDecryptWithWrongAssociatedData() {
		byte[] nonce = nonce(ALGORITHM);
		byte[] body = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, AAD);
		assertTrue(Aeads.tryDecrypt(ALGORITHM, key, nonce, body, "other".getBytes(StandardCharsets.UTF_8)).isEmpty());
	}
	
	@Test
	void generateKeyLength() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			SecretKey generated = Aeads.generateKey(algorithm);
			assertEquals(algorithm.keyLength(), generated.getEncoded().length);
			assertEquals(algorithm.keyJcaName(), generated.getAlgorithm());
		}
	}
	
	@Test
	void generateKeyProducesDifferentKeys() {
		assertFalse(Arrays.equals(Aeads.generateKey(ALGORITHM).getEncoded(), Aeads.generateKey(ALGORITHM).getEncoded()));
	}
	
	@Test
	void encryptPrependsNonce() {
		byte[] ciphertext = Aeads.encrypt(ALGORITHM, key, PLAINTEXT);
		byte[] nonce = CryptoBytes.slice(ciphertext, 0, ALGORITHM.nonceLength());
		byte[] body = CryptoBytes.slice(ciphertext, ALGORITHM.nonceLength(), ciphertext.length - ALGORITHM.nonceLength());
		
		assertEquals(ALGORITHM.nonceLength() + PLAINTEXT.length + ALGORITHM.tagLength(), ciphertext.length);
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(ALGORITHM, key, nonce, body, null));
	}
	
	@Test
	void encryptWithEmptyPlaintext() {
		byte[] ciphertext = Aeads.encrypt(ALGORITHM, key, new byte[0]);
		
		assertEquals(ALGORITHM.nonceLength() + ALGORITHM.tagLength(), ciphertext.length);
		assertEquals(0, Aeads.decrypt(ALGORITHM, key, ciphertext).length);
	}
	
	@Test
	void encryptProducesDifferentCiphertextsPerCall() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			SecretKey generated = Aeads.generateKey(algorithm);
			assertFalse(Arrays.equals(Aeads.encrypt(algorithm, generated, PLAINTEXT), Aeads.encrypt(algorithm, generated, PLAINTEXT)), algorithm.name());
		}
	}
	
	@Test
	void encryptWithFixedRandomIsDeterministic() throws Exception {
		assertArrayEquals(Aeads.encrypt(seeded(), ALGORITHM, key, PLAINTEXT, AAD), Aeads.encrypt(seeded(), ALGORITHM, key, PLAINTEXT, AAD));
	}
	
	@Test
	void ciphertextLengthPerAlgorithm() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			SecretKey generated = Aeads.generateKey(algorithm);
			for (int size : new int[] { 0, 1, 1024 }) {
				byte[] plaintext = CryptoRandom.bytes(size);
				assertEquals(size + algorithm.tagLength(), Aeads.encrypt(algorithm, generated, nonce(algorithm), plaintext, null).length);
				assertEquals(algorithm.nonceLength() + size + algorithm.tagLength(), Aeads.encrypt(algorithm, generated, plaintext).length);
			}
		}
	}
	
	@Test
	void decryptRecoversPlaintextForEveryAlgorithm() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			SecretKey generated = Aeads.generateKey(algorithm);
			assertArrayEquals(PLAINTEXT, Aeads.decrypt(algorithm, generated, Aeads.encrypt(algorithm, generated, PLAINTEXT)), algorithm.name());
		}
	}
	
	@Test
	void keyDoesNotAliasRawArray() {
		byte[] raw = CryptoRandom.bytes(32);
		byte[] copy = raw.clone();
		SecretKey wrapped = Aeads.key(ALGORITHM, raw);
		
		Arrays.fill(raw, (byte) 0);
		assertArrayEquals(copy, wrapped.getEncoded());
	}
	
	@Test
	void encryptDoesNotMutateInputs() throws Exception {
		byte[] plaintext = PLAINTEXT.clone();
		byte[] aad = AAD.clone();
		byte[] nonce = nonce(ALGORITHM);
		byte[] nonceCopy = nonce.clone();
		
		Aeads.encrypt(ALGORITHM, key, plaintext);
		Aeads.encrypt(ALGORITHM, key, plaintext, aad);
		Aeads.encrypt(seeded(), ALGORITHM, key, plaintext, aad);
		Aeads.encrypt(ALGORITHM, key, nonce, plaintext, aad);
		
		assertArrayEquals(PLAINTEXT, plaintext);
		assertArrayEquals(AAD, aad);
		assertArrayEquals(nonceCopy, nonce);
	}
	
	@Test
	void decryptDoesNotMutateInputs() {
		byte[] aad = AAD.clone();
		byte[] nonce = nonce(ALGORITHM);
		byte[] nonceCopy = nonce.clone();
		byte[] body = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, aad);
		byte[] bodyCopy = body.clone();
		byte[] attached = CryptoBytes.concat(nonce, body);
		byte[] attachedCopy = attached.clone();
		
		Aeads.decrypt(ALGORITHM, key, nonce, body, aad);
		Aeads.decrypt(ALGORITHM, key, attached, aad);
		assertArrayEquals(bodyCopy, body);
		assertArrayEquals(nonceCopy, nonce);
		assertArrayEquals(attachedCopy, attached);
		assertArrayEquals(AAD, aad);
	}
	
	@Test
	void roundTripForEveryAlgorithmAndPlaintextSize() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			SecretKey generated = Aeads.generateKey(algorithm);
			for (int size : new int[] { 0, 1, 15, 16, 17, 1024, 100000 }) {
				byte[] plaintext = CryptoRandom.bytes(size);
				assertArrayEquals(plaintext, Aeads.decrypt(algorithm, generated, Aeads.encrypt(algorithm, generated, plaintext)));
				assertArrayEquals(plaintext, Aeads.decrypt(algorithm, generated, Aeads.encrypt(algorithm, generated, plaintext, AAD), AAD));
			}
		}
	}
	
	@Test
	void detachedAndAttachedNonceFormsAgree() {
		byte[] nonce = nonce(ALGORITHM);
		byte[] body = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, AAD);
		
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(ALGORITHM, key, CryptoBytes.concat(nonce, body), AAD));
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(ALGORITHM, key, nonce, body, AAD));
	}
	
	@Test
	void tamperingAnyByteFailsAuthentication() {
		byte[] ciphertext = Aeads.encrypt(ALGORITHM, key, new byte[] { 1, 2, 3, 4 });
		for (int index = 0; index < ciphertext.length; index++) {
			byte[] tampered = ciphertext.clone();
			tampered[index] ^= 1;
			
			assertThrows(AuthenticationException.class, () -> Aeads.decrypt(ALGORITHM, key, tampered), "index " + index);
		}
		assertThrows(AuthenticationException.class, () -> Aeads.decrypt(ALGORITHM, key, Arrays.copyOf(ciphertext, ciphertext.length - 1)));
		assertThrows(AuthenticationException.class, () -> Aeads.decrypt(ALGORITHM, key, Arrays.copyOf(ciphertext, ciphertext.length + 1)));
	}
	
	@Test
	void associatedDataIsAuthenticatedButNotEncrypted() {
		byte[] nonce = nonce(ALGORITHM);
		byte[] withAad = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, AAD);
		byte[] withoutAad = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, null);
		
		assertEquals(withoutAad.length, withAad.length);
		assertFalse(HexFormat.of().formatHex(withAad).contains(HexFormat.of().formatHex(AAD)));
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(ALGORITHM, key, nonce, withAad, AAD));
	}
	
	@Test
	void associatedDataOfEveryShape() {
		byte[] nonce = nonce(ALGORITHM);
		assertArrayEquals(Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, null), Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, new byte[0]));
		
		byte[] large = CryptoRandom.bytes(100000);
		byte[] body = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, large);
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(ALGORITHM, key, nonce, body, large));
		
		for (int index : new int[] { 0, large.length - 1 }) {
			byte[] changed = large.clone();
			changed[index] ^= 1;
			assertThrows(AuthenticationException.class, () -> Aeads.decrypt(ALGORITHM, key, nonce, body, changed));
		}
	}
	
	@Test
	void tryDecryptDistinguishesAuthenticationFromConfiguration() {
		byte[] nonce = nonce(ALGORITHM);
		byte[] body = Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, null);
		byte[] tampered = body.clone();
		tampered[0] ^= 1;
		
		assertTrue(Aeads.tryDecrypt(ALGORITHM, Aeads.generateKey(ALGORITHM), nonce, body, null).isEmpty());
		assertTrue(Aeads.tryDecrypt(ALGORITHM, key, nonce, tampered, null).isEmpty());
		assertThrows(CryptoException.class, () -> Aeads.tryDecrypt(ALGORITHM, new SecretKeySpec(new byte[17], "AES"), nonce, body, null));
	}
	
	@Test
	void tryDecryptAcrossManyCandidateKeys() {
		List<SecretKey> keys = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			keys.add(Aeads.generateKey(ALGORITHM));
		}
		
		byte[] nonce = nonce(ALGORITHM);
		byte[] body = Aeads.encrypt(ALGORITHM, keys.get(4), nonce, PLAINTEXT, AAD);
		int matches = 0;
		for (SecretKey candidate : keys) {
			Optional<byte[]> result = assertDoesNotThrow(() -> Aeads.tryDecrypt(ALGORITHM, candidate, nonce, body, AAD));
			if (result.isPresent()) {
				matches++;
				assertArrayEquals(PLAINTEXT, result.orElseThrow());
			}
		}
		assertEquals(1, matches);
	}
	
	@Test
	void gcmSivIsDeterministicUnderAFixedNonce() {
		for (AeadAlgorithm algorithm : new AeadAlgorithm[] { AeadAlgorithm.AES_256_GCM_SIV, AeadAlgorithm.AES_256_GCM }) {
			SecretKey generated = Aeads.generateKey(algorithm);
			byte[] nonce = nonce(algorithm);
			byte[] first = Aeads.encrypt(algorithm, generated, nonce, PLAINTEXT, AAD);
			
			assertArrayEquals(first, Aeads.encrypt(algorithm, generated, nonce, PLAINTEXT, AAD));
			assertArrayEquals(PLAINTEXT, Aeads.decrypt(algorithm, generated, nonce, first, AAD));
		}
	}
	
	@Test
	void keyCommitmentIsNotProvided() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			SecretKey generated = Aeads.generateKey(algorithm);
			assertEquals(algorithm.nonceLength() + PLAINTEXT.length + algorithm.tagLength(), Aeads.encrypt(algorithm, generated, PLAINTEXT).length, algorithm.name());
		}
	}
	
	@Test
	void encryptWithExplicitNonceRejectsReuseSilently() {
		byte[] nonce = nonce(ALGORITHM);
		byte[] first = assertDoesNotThrow(() -> Aeads.encrypt(ALGORITHM, key, nonce, PLAINTEXT, null));
		byte[] second = assertDoesNotThrow(() -> Aeads.encrypt(ALGORITHM, key, nonce, "other message".getBytes(StandardCharsets.UTF_8), null));
		
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(ALGORITHM, key, nonce, first, null));
		assertArrayEquals("other message".getBytes(StandardCharsets.UTF_8), Aeads.decrypt(ALGORITHM, key, nonce, second, null));
	}
	
	@Test
	void knownAnswerVectors() {
		HexFormat hex = HexFormat.of();
		SecretKey aesKey = Aeads.key(AeadAlgorithm.AES_256_GCM, hex.parseHex("b52c505a37d78eda5dd34f20c22540ea1b58963cf8e5bf8ffa85f9f2492505b4"));
		assertEquals("bdc1ac884d332457a1d2664f168c76f0", hex.formatHex(Aeads.encrypt(AeadAlgorithm.AES_256_GCM, aesKey, hex.parseHex("516c33929df5a3284ff463d7"), new byte[0], null)));
		
		SecretKey chachaKey = Aeads.key(AeadAlgorithm.CHACHA20_POLY1305, hex.parseHex("808182838485868788898a8b8c8d8e8f909192939495969798999a9b9c9d9e9f"));
		byte[] plaintext = "Ladies and Gentlemen of the class of '99: If I could offer you only one tip for the future, sunscreen would be it.".getBytes(StandardCharsets.UTF_8);
		byte[] result = Aeads.encrypt(AeadAlgorithm.CHACHA20_POLY1305, chachaKey, hex.parseHex("070000004041424344454647"), plaintext, hex.parseHex("50515253c0c1c2c3c4c5c6c7"));
		assertTrue(hex.formatHex(result).startsWith("d31a8d34648e60db7b86afbc53ef7ec2"));
		assertTrue(hex.formatHex(result).endsWith("1ae10b594f09e26a7e902ecbd0600691"));
	}
	
	@Test
	void cipherReturnsIndependentInstances() throws Exception {
		byte[] nonce = nonce(ALGORITHM);
		Cipher first = Aeads.cipher(ALGORITHM, Cipher.ENCRYPT_MODE, key, nonce, null);
		Cipher second = Aeads.cipher(ALGORITHM, Cipher.ENCRYPT_MODE, key, nonce, null);
		
		assertNotSame(first, second);
		assertEquals(ALGORITHM.jcaName(), first.getAlgorithm());
		assertArrayEquals(first.doFinal(PLAINTEXT), second.doFinal(PLAINTEXT));
	}
	
	@Test
	void largePlaintextRoundTrip() {
		byte[] plaintext = CryptoRandom.bytes(1 << 20);
		byte[] aad = CryptoRandom.bytes(1 << 16);
		
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			SecretKey generated = Aeads.generateKey(algorithm);
			assertArrayEquals(plaintext, Aeads.decrypt(algorithm, generated, Aeads.encrypt(algorithm, generated, plaintext, aad), aad), algorithm.name());
		}
	}
	
	@Test
	void encryptAndDecryptWithWideNonceAlgorithm() {
		AeadAlgorithm algorithm = AeadAlgorithm.XCHACHA20_POLY1305;
		SecretKey generated = Aeads.generateKey(algorithm);
		byte[] plaintext = "crypto".getBytes(StandardCharsets.UTF_8);
		byte[] nonce = nonce(algorithm);
		
		assertArrayEquals(plaintext, Aeads.decrypt(algorithm, generated, Aeads.encrypt(algorithm, generated, plaintext, AAD), AAD));
		assertArrayEquals(plaintext, Aeads.decrypt(algorithm, generated, nonce, Aeads.encrypt(algorithm, generated, nonce, plaintext, AAD), AAD));
		assertDoesNotThrow(() -> Aeads.key(algorithm, new byte[32]));
		assertThrows(IllegalArgumentException.class, () -> Aeads.key(algorithm, new byte[31]));
		assertThrows(IllegalArgumentException.class, () -> Aeads.key(algorithm, new byte[33]));
	}
	
	@Test
	void encryptPrependsWideNonce() {
		AeadAlgorithm algorithm = AeadAlgorithm.XCHACHA20_POLY1305;
		SecretKey generated = Aeads.generateKey(algorithm);
		byte[] ciphertext = Aeads.encrypt(algorithm, generated, PLAINTEXT);
		
		assertEquals(24, algorithm.nonceLength());
		assertEquals(24 + PLAINTEXT.length + 16, ciphertext.length);
		byte[] nonce = CryptoBytes.slice(ciphertext, 0, 24);
		byte[] body = CryptoBytes.slice(ciphertext, 24, ciphertext.length - 24);
		assertArrayEquals(PLAINTEXT, Aeads.decrypt(algorithm, generated, nonce, body, null));
	}
	
	@Test
	void decryptWithMinimumValidLengthForWideNonce() {
		AeadAlgorithm algorithm = AeadAlgorithm.XCHACHA20_POLY1305;
		SecretKey generated = Aeads.generateKey(algorithm);
		byte[] ciphertext = Aeads.encrypt(algorithm, generated, new byte[0]);
		
		assertEquals(40, ciphertext.length);
		assertEquals(0, Aeads.decrypt(algorithm, generated, ciphertext).length);
		for (int length : new int[] { 39, 24, 0 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Aeads.decrypt(algorithm, generated, new byte[length]));
			assertTrue(exception.getMessage().contains(algorithm.toString()));
			assertTrue(exception.getMessage().contains(String.valueOf(length)));
		}
	}
	
	@Test
	void wideNonceAlgorithmUsesTheIvParameterSpecArm() throws Exception {
		AeadAlgorithm algorithm = AeadAlgorithm.XCHACHA20_POLY1305;
		SecretKey generated = Aeads.generateKey(algorithm);
		
		Cipher cipher = assertDoesNotThrow(() -> Aeads.cipher(algorithm, Cipher.ENCRYPT_MODE, generated, new byte[24], AAD));
		assertEquals(PLAINTEXT.length + algorithm.tagLength(), cipher.doFinal(PLAINTEXT).length);
		assertThrows(CryptoException.class, () -> Aeads.encrypt(algorithm, generated, new byte[12], PLAINTEXT, null));
	}
}
