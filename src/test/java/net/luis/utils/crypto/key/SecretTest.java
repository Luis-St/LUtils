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
		assertThrows(NullPointerException.class, () -> secret.toKey(null));
	}
	
	@Test
	void toKeyWithNullAlgorithmAfterClose() {
		Secret secret = Secret.random(32);
		secret.close();
		assertThrows(NullPointerException.class, () -> secret.toKey(null));
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
}
