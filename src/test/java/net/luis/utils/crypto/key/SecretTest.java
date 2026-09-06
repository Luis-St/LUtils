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

package net.luis.utils.crypto.key;

import net.luis.utils.crypto.Aeads;
import net.luis.utils.crypto.Macs;
import net.luis.utils.crypto.algorithm.AeadAlgorithm;
import net.luis.utils.crypto.algorithm.MacAlgorithm;
import net.luis.utils.crypto.exception.CryptoException;
import org.junit.jupiter.api.Test;

import javax.crypto.*;
import java.lang.reflect.*;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Secret}.<br>
 *
 * @author Luis-St
 */
class SecretTest {
	
	@Test
	void constructorIsPrivate() {
		Constructor<?>[] constructors = Secret.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Secret.class.getModifiers()));
	}
	
	@Test
	void adoptMaterial() {
		Secret secret = Secret.adopt(new byte[] { 1, 2, 3 });
		assertEquals(3, secret.length());
		assertArrayEquals(new byte[] { 1, 2, 3 }, secret.material());
	}
	
	@Test
	void adoptWithNullMaterial() {
		assertThrows(NullPointerException.class, () -> Secret.adopt(null));
	}
	
	@Test
	void copyOfMaterial() {
		Secret secret = Secret.copyOf(new byte[] { 1, 2, 3 });
		assertEquals(3, secret.length());
		assertArrayEquals(new byte[] { 1, 2, 3 }, secret.material());
	}
	
	@Test
	void copyOfWithNullMaterial() {
		assertThrows(NullPointerException.class, () -> Secret.copyOf(null));
	}
	
	@Test
	void randomMaterial() {
		Secret secret = Secret.random(32);
		assertEquals(32, secret.length());
		assertEquals(32, secret.material().length);
		assertFalse(Arrays.equals(new byte[32], secret.material()));
	}
	
	@Test
	void randomWithNegativeLength() {
		assertThrows(IllegalArgumentException.class, () -> Secret.random(-1));
	}
	
	@Test
	void materialAfterClose() {
		Secret secret = Secret.adopt(new byte[] { 1, 2, 3 });
		secret.close();
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, secret::material);
		assertEquals("Secret has already been closed", exception.getMessage());
	}
	
	@Test
	void toKeyAfterClose() {
		Secret secret = Secret.random(32);
		secret.close();
		assertThrows(IllegalStateException.class, () -> secret.toKey("AES"));
	}
	
	@Test
	void toKeyWithNullAlgorithm() {
		Secret secret = Secret.random(32);
		assertThrows(NullPointerException.class, () -> secret.toKey((String) null));
	}
	
	@Test
	void toKeyWithNullAlgorithmAfterClose() {
		Secret secret = Secret.random(32);
		secret.close();
		assertThrows(NullPointerException.class, () -> secret.toKey((String) null));
	}
	
	@Test
	void toKeyWithEmptyAlgorithm() {
		Secret secret = Secret.random(32);
		SecretKey key = assertDoesNotThrow(() -> secret.toKey(""));
		assertEquals("", key.getAlgorithm());
		assertArrayEquals(secret.material(), key.getEncoded());
	}
	
	@Test
	void materialBeforeClose() {
		Secret secret = Secret.adopt(new byte[] { 1, 2, 3 });
		assertDoesNotThrow(secret::material);
		assertArrayEquals(new byte[] { 1, 2, 3 }, secret.material());
	}
	
	@Test
	void materialReturnsLiveArray() {
		byte[] original = { 1, 2, 3 };
		Secret secret = Secret.adopt(original);
		assertSame(original, secret.material());
		
		secret.material()[0] = 9;
		assertEquals(9, secret.material()[0]);
	}
	
	@Test
	void lengthAfterClose() {
		Secret secret = Secret.adopt(new byte[] { 1, 2, 3 });
		secret.close();
		assertEquals(3, secret.length());
		assertThrows(IllegalStateException.class, secret::material);
	}
	
	@Test
	void closeWipesMaterial() {
		byte[] original = { 1, 2, 3 };
		Secret secret = Secret.adopt(original);
		secret.close();
		assertArrayEquals(new byte[] { 0, 0, 0 }, original);
	}
	
	@Test
	void closeIsIdempotent() {
		Secret secret = Secret.adopt(new byte[] { 1, 2, 3 });
		assertDoesNotThrow(() -> {
			secret.close();
			secret.close();
		});
		assertEquals(3, secret.length());
		assertThrows(IllegalStateException.class, secret::material);
	}
	
	@Test
	void closeOnEmptySecret() {
		Secret secret = Secret.adopt(new byte[0]);
		assertDoesNotThrow(secret::close);
		assertEquals(0, secret.length());
	}
	
	@Test
	void adoptTakesOwnership() {
		byte[] original = { 1, 2, 3 };
		Secret secret = Secret.adopt(original);
		
		original[0] = 9;
		assertEquals(9, secret.material()[0]);
		secret.close();
		assertArrayEquals(new byte[] { 0, 0, 0 }, original);
	}
	
	@Test
	void copyOfDoesNotTakeOwnership() {
		byte[] original = { 1, 2, 3 };
		Secret secret = Secret.copyOf(original);
		assertNotSame(original, secret.material());
		
		original[0] = 9;
		assertArrayEquals(new byte[] { 1, 2, 3 }, secret.material());
		secret.close();
		assertArrayEquals(new byte[] { 9, 2, 3 }, original);
	}
	
	@Test
	void adoptEmptyMaterial() {
		Secret secret = Secret.adopt(new byte[0]);
		assertEquals(0, secret.length());
		assertEquals(0, secret.material().length);
	}
	
	@Test
	void copyOfEmptyMaterial() {
		byte[] original = new byte[0];
		Secret secret = Secret.copyOf(original);
		assertEquals(0, secret.length());
		assertNotSame(original, secret.material());
	}
	
	@Test
	void randomWithZeroLength() {
		Secret secret = assertDoesNotThrow(() -> Secret.random(0));
		assertEquals(0, secret.length());
	}
	
	@Test
	void randomProducesDifferentSecrets() {
		Secret first = Secret.random(32);
		Secret second = Secret.random(32);
		assertFalse(Arrays.equals(first.material(), second.material()));
	}
	
	@Test
	void toKeyWrapsMaterial() {
		Secret secret = Secret.random(32);
		SecretKey key = secret.toKey("AES");
		assertEquals("AES", key.getAlgorithm());
		assertEquals("RAW", key.getFormat());
		assertArrayEquals(secret.material(), key.getEncoded());
	}
	
	@Test
	void toStringDoesNotRevealMaterial() {
		Secret secret = Secret.adopt(new byte[] { 1, 2, 3 });
		assertEquals("Secret[3 bytes]", secret.toString());
		assertFalse(secret.toString().contains("010203"));
		assertFalse(secret.toString().contains("[1, 2, 3]"));
	}
	
	@Test
	void toStringAfterClose() {
		Secret secret = Secret.adopt(new byte[] { 1, 2, 3 });
		secret.close();
		assertEquals("Secret[3 bytes]", secret.toString());
	}
	
	@Test
	void isAutoCloseable() {
		Secret secret = Secret.adopt(new byte[] { 1, 2, 3 });
		assertInstanceOf(AutoCloseable.class, secret);
		assertDoesNotThrow(() -> {
			try (Secret ignored = Secret.random(16)) {
				assertEquals(16, ignored.length());
			}
		});
	}
	
	@Test
	void equalityIsNotImplemented() {
		byte[] source = { 1, 2, 3 };
		Secret first = Secret.copyOf(source);
		Secret second = Secret.copyOf(source);
		
		assertNotEquals(first, second);
		assertEquals(first, first);
		assertThrows(NoSuchMethodException.class, () -> Secret.class.getDeclaredMethod("equals", Object.class));
		assertThrows(NoSuchMethodException.class, () -> Secret.class.getDeclaredMethod("hashCode"));
	}
	
	@Test
	void tryWithResourcesWipesOnNormalExit() {
		byte[] original = { 1, 2, 3 };
		Secret escaped;
		try (Secret secret = Secret.adopt(original)) {
			escaped = secret;
			assertArrayEquals(new byte[] { 1, 2, 3 }, secret.material());
		}
		
		assertArrayEquals(new byte[] { 0, 0, 0 }, original);
		assertThrows(IllegalStateException.class, escaped::material);
	}
	
	@Test
	void tryWithResourcesWipesOnException() {
		byte[] original = { 1, 2, 3 };
		assertThrows(IllegalStateException.class, () -> {
			try (Secret secret = Secret.adopt(original)) {
				assertEquals(3, secret.length());
				throw new IllegalStateException("boom");
			}
		});
		assertArrayEquals(new byte[] { 0, 0, 0 }, original);
	}
	
	@Test
	void toKeySurvivesClose() {
		byte[] original = { 1, 2, 3, 4, 5, 6, 7, 8 };
		Secret secret = Secret.adopt(original.clone());
		SecretKey key = secret.toKey("AES");
		
		secret.close();
		assertArrayEquals(original, key.getEncoded());
		assertFalse(Arrays.equals(new byte[8], key.getEncoded()));
	}
	
	@Test
	void toKeyWithDifferentAlgorithms() {
		Secret secret = Secret.random(32);
		byte[] material = secret.material().clone();
		
		for (String algorithm : new String[] { "AES", "ChaCha20", "HmacSHA256" }) {
			SecretKey key = secret.toKey(algorithm);
			assertEquals(algorithm, key.getAlgorithm());
			assertArrayEquals(material, key.getEncoded());
		}
		assertDoesNotThrow(() -> Cipher.getInstance("AES/ECB/NoPadding").init(Cipher.ENCRYPT_MODE, secret.toKey("AES")));
		assertDoesNotThrow(() -> Mac.getInstance("HmacSHA256").init(secret.toKey("HmacSHA256")));
	}
	
	@Test
	void toKeyReturnsIndependentSpecs() {
		Secret secret = Secret.random(32);
		SecretKey first = secret.toKey("AES");
		SecretKey second = secret.toKey("AES");
		
		assertNotSame(first, second);
		assertArrayEquals(first.getEncoded(), second.getEncoded());
	}
	
	@Test
	void adoptedArrayIsNotCopiedOnMaterial() {
		Secret secret = Secret.adopt(new byte[] { 1, 2, 3 });
		assertSame(secret.material(), secret.material());
	}
	
	@Test
	void largeSecretIsWipedEntirely() {
		byte[] original = new byte[1 << 20];
		Arrays.fill(original, (byte) 0x5A);
		Secret secret = Secret.adopt(original);
		
		secret.close();
		assertArrayEquals(new byte[1 << 20], original);
	}
	
	@Test
	void secretIsNotThreadSafeByDesign() throws Exception {
		Field closed = Secret.class.getDeclaredField("closed");
		assertFalse(Modifier.isVolatile(closed.getModifiers()));
		
		for (Method method : Secret.class.getDeclaredMethods()) {
			assertFalse(Modifier.isSynchronized(method.getModifiers()));
		}
	}
	
	@Test
	void materialSurvivesRepeatedUseBeforeClose() {
		Secret secret = Secret.adopt(new byte[] { 1, 2, 3 });
		
		assertArrayEquals(new byte[] { 1, 2, 3 }, secret.material());
		assertEquals(3, secret.length());
		assertArrayEquals(new byte[] { 1, 2, 3 }, secret.toKey("AES").getEncoded());
		assertEquals(3, secret.length());
		assertArrayEquals(new byte[] { 1, 2, 3 }, secret.material());
	}
	
	@Test
	void toKeyWithNullAeadAlgorithm() {
		Secret secret = Secret.random(32);
		assertThrows(NullPointerException.class, () -> secret.toKey((AeadAlgorithm) null));
	}
	
	@Test
	void toKeyWithNullMacAlgorithm() {
		Secret secret = Secret.random(32);
		assertThrows(NullPointerException.class, () -> secret.toKey((MacAlgorithm) null));
	}
	
	@Test
	void toKeyWithNullAeadAlgorithmAfterClose() {
		Secret secret = Secret.random(32);
		secret.close();
		assertThrows(NullPointerException.class, () -> secret.toKey((AeadAlgorithm) null));
	}
	
	@Test
	void toKeyWithNullMacAlgorithmAfterClose() {
		Secret secret = Secret.random(32);
		secret.close();
		assertThrows(NullPointerException.class, () -> secret.toKey((MacAlgorithm) null));
	}
	
	@Test
	void toKeyWithWrongAeadKeyLength() {
		Secret secret = Secret.random(16);
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> secret.toKey(AeadAlgorithm.AES_256_GCM));
		assertTrue(exception.getMessage().contains("32"));
		assertTrue(exception.getMessage().contains("16"));
	}
	
	@Test
	void toKeyWithEmptySecretForMac() {
		Secret secret = Secret.adopt(new byte[0]);
		assertThrows(CryptoException.class, () -> secret.toKey(MacAlgorithm.HMAC_SHA_256));
	}
	
	@Test
	void toKeyAeadAfterClose() {
		Secret secret = Secret.random(32);
		secret.close();
		assertThrows(IllegalStateException.class, () -> secret.toKey(AeadAlgorithm.AES_256_GCM));
	}
	
	@Test
	void toKeyMacAfterClose() {
		Secret secret = Secret.random(32);
		secret.close();
		assertThrows(IllegalStateException.class, () -> secret.toKey(MacAlgorithm.HMAC_SHA_256));
	}
	
	@Test
	void toKeyWithCorrectAeadKeyLength() {
		Secret secret = Secret.random(32);
		SecretKey key = secret.toKey(AeadAlgorithm.AES_256_GCM);
		
		assertNotNull(key);
		assertEquals("AES", key.getAlgorithm());
		assertEquals(32, key.getEncoded().length);
	}
	
	@Test
	void toKeyWithLongAeadKeyLength() {
		Secret secret = Secret.random(33);
		assertThrows(IllegalArgumentException.class, () -> secret.toKey(AeadAlgorithm.AES_256_GCM));
	}
	
	@Test
	void toKeyWithNonEmptySecretForMac() {
		Secret secret = Secret.random(32);
		SecretKey key = secret.toKey(MacAlgorithm.HMAC_SHA_256);
		
		assertEquals("HmacSHA256", key.getAlgorithm());
		assertEquals(32, key.getEncoded().length);
	}
	
	@Test
	void toKeyAeadWithWrongLengthAfterClose() {
		Secret secret = Secret.random(16);
		secret.close();
		assertThrows(IllegalArgumentException.class, () -> secret.toKey(AeadAlgorithm.AES_256_GCM));
	}
	
	@Test
	void toKeyMacWithEmptySecretAfterClose() {
		Secret secret = Secret.adopt(new byte[0]);
		secret.close();
		assertThrows(CryptoException.class, () -> secret.toKey(MacAlgorithm.HMAC_SHA_256));
	}
	
	@Test
	void toKeyForEveryAeadAlgorithm() {
		for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
			Secret secret = Secret.random(algorithm.keyLength());
			SecretKey key = secret.toKey(algorithm);
			assertEquals(algorithm.keyJcaName(), key.getAlgorithm());
			assertEquals(algorithm.keyLength(), key.getEncoded().length);
		}
	}
	
	@Test
	void toKeyForEveryMacAlgorithm() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			Secret secret = Secret.random(algorithm.recommendedKeyLength());
			SecretKey key = secret.toKey(algorithm);
			assertEquals(algorithm.jcaName(), key.getAlgorithm());
			assertEquals(algorithm.recommendedKeyLength(), key.getEncoded().length);
		}
	}
	
	@Test
	void toKeyMacWithSingleByteSecret() {
		Secret secret = Secret.adopt(new byte[] { 42 });
		SecretKey key = assertDoesNotThrow(() -> secret.toKey(MacAlgorithm.HMAC_SHA_256));
		assertEquals(1, key.getEncoded().length);
	}
	
	@Test
	void toKeyMacBelowRecommendedLength() {
		Secret secret = Secret.random(8);
		SecretKey key = assertDoesNotThrow(() -> secret.toKey(MacAlgorithm.HMAC_SHA_512));
		assertEquals(8, key.getEncoded().length);
		assertEquals("HmacSHA512", key.getAlgorithm());
	}
	
	@Test
	void toKeyAeadCopiesMaterial() {
		Secret secret = Secret.random(32);
		byte[] material = secret.material().clone();
		SecretKey key = secret.toKey(AeadAlgorithm.AES_256_GCM);
		
		assertArrayEquals(material, key.getEncoded());
		secret.close();
		assertArrayEquals(material, key.getEncoded());
	}
	
	@Test
	void toKeyMacCopiesMaterial() {
		Secret secret = Secret.random(32);
		byte[] material = secret.material().clone();
		SecretKey key = secret.toKey(MacAlgorithm.HMAC_SHA_256);
		
		assertArrayEquals(material, key.getEncoded());
		secret.close();
		assertArrayEquals(material, key.getEncoded());
	}
	
	@Test
	void toKeyAeadMatchesStringOverload() {
		Secret secret = Secret.random(32);
		SecretKey typed = secret.toKey(AeadAlgorithm.AES_256_GCM);
		SecretKey named = secret.toKey("AES");
		
		assertEquals(named.getAlgorithm(), typed.getAlgorithm());
		assertArrayEquals(named.getEncoded(), typed.getEncoded());
	}
	
	@Test
	void toKeyMacMatchesStringOverload() {
		Secret secret = Secret.random(32);
		SecretKey typed = secret.toKey(MacAlgorithm.HMAC_SHA_256);
		SecretKey named = secret.toKey(MacAlgorithm.HMAC_SHA_256.jcaName());
		
		assertEquals(named.getAlgorithm(), typed.getAlgorithm());
		assertArrayEquals(named.getEncoded(), typed.getEncoded());
	}
	
	@Test
	void toKeyAeadReturnsIndependentSpecs() {
		Secret secret = Secret.random(32);
		SecretKey first = secret.toKey(AeadAlgorithm.AES_256_GCM);
		SecretKey second = secret.toKey(AeadAlgorithm.AES_256_GCM);
		
		assertNotSame(first, second);
		assertArrayEquals(first.getEncoded(), second.getEncoded());
	}
	
	@Test
	void toKeyAeadEncryptsAndDecrypts() {
		byte[] plaintext = "authenticated".getBytes();
		try (Secret secret = Secret.random(32)) {
			SecretKey key = secret.toKey(AeadAlgorithm.AES_256_GCM);
			byte[] nonce = AeadAlgorithm.AES_256_GCM.sequenceNonce(0);
			byte[] ciphertext = Aeads.encrypt(AeadAlgorithm.AES_256_GCM, key, nonce, plaintext, null);
			
			assertArrayEquals(plaintext, Aeads.decrypt(AeadAlgorithm.AES_256_GCM, key, nonce, ciphertext, null));
		}
	}
	
	@Test
	void toKeyMacAuthenticates() {
		byte[] data = "authenticate me".getBytes();
		try (Secret secret = Secret.random(32); Secret other = Secret.random(32)) {
			SecretKey key = secret.toKey(MacAlgorithm.HMAC_SHA_256);
			byte[] tag = Macs.mac(MacAlgorithm.HMAC_SHA_256, key, data);
			
			assertTrue(Macs.verify(MacAlgorithm.HMAC_SHA_256, key, data, tag));
			assertFalse(Arrays.equals(tag, Macs.mac(MacAlgorithm.HMAC_SHA_256, other.toKey(MacAlgorithm.HMAC_SHA_256), data)));
		}
	}
	
	@Test
	void toKeyRejectsCrossAlgorithmLengths() {
		for (int length : new int[] { 16, 24, 64 }) {
			Secret secret = Secret.random(length);
			for (AeadAlgorithm algorithm : AeadAlgorithm.values()) {
				assertThrows(IllegalArgumentException.class, () -> secret.toKey(algorithm));
			}
		}
	}
}
