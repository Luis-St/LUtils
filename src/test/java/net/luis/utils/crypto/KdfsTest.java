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
import net.luis.utils.crypto.algorithm.KdfAlgorithm;
import net.luis.utils.crypto.key.Secret;
import net.luis.utils.crypto.util.CryptoRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.KDF;
import javax.crypto.SecretKey;
import javax.crypto.spec.HKDFParameterSpec;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Kdfs}.<br>
 *
 * @author Luis-St
 */
class KdfsTest {
	
	private static final KdfAlgorithm ALGORITHM = KdfAlgorithm.HKDF_SHA_256;
	private static final byte[] IKM = "input key material".getBytes(StandardCharsets.UTF_8);
	private static final byte[] SALT = "salt".getBytes(StandardCharsets.UTF_8);
	private static final byte[] INFO = "info".getBytes(StandardCharsets.UTF_8);
	
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	
	private static byte[] materialOf(Secret secret) {
		try (secret) {
			return secret.material().clone();
		}
	}
	
	private static byte[] derived(KdfAlgorithm algorithm, byte[] ikm, byte[] salt, byte[] info, int length) {
		return materialOf(Kdfs.derive(algorithm, ikm, salt, info, length));
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = Kdfs.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Kdfs.class.getModifiers()));
		
		Constructor<Kdfs> constructor = Kdfs.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void extractWithNullAlgorithm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Kdfs.extract(null, SALT, IKM));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void extractWithNullIkm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Kdfs.extract(ALGORITHM, SALT, (byte[]) null));
		assertEquals("Input key material must not be null", exception.getMessage());
	}
	
	@Test
	void extractWithBothNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Kdfs.extract(null, SALT, (byte[]) null));
		assertEquals("Algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void extractWithEmptyIkm() {
		byte[] prk = assertDoesNotThrow(() -> materialOf(Kdfs.extract(ALGORITHM, SALT, new byte[0])));
		assertEquals(ALGORITHM.outputLength(), prk.length);
	}
	
	@Test
	void expandWithNullAlgorithm() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			NullPointerException exception = assertThrows(NullPointerException.class, () -> Kdfs.expand(null, prk, INFO, 32));
			assertEquals("Algorithm must not be null", exception.getMessage());
		}
	}
	
	@Test
	void expandWithNullPrk() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Kdfs.expand(ALGORITHM, null, INFO, 32));
		assertEquals("Pseudo random key must not be null", exception.getMessage());
	}
	
	@Test
	void expandWithZeroLength() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Kdfs.expand(ALGORITHM, prk, INFO, 0));
			assertTrue(exception.getMessage().contains("[1, " + 255 * ALGORITHM.outputLength() + "]"));
		}
	}
	
	@Test
	void expandWithNegativeLength() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			assertThrows(IllegalArgumentException.class, () -> Kdfs.expand(ALGORITHM, prk, INFO, -1));
		}
	}
	
	@Test
	void expandWithLengthAboveMaximum() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			try (Secret prk = Kdfs.extract(algorithm, SALT, IKM)) {
				int maximum = 255 * algorithm.outputLength();
				IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Kdfs.expand(algorithm, prk, INFO, maximum + 1));
				assertTrue(exception.getMessage().contains(String.valueOf(maximum)));
			}
		}
	}
	
	@Test
	void expandWithShortPrk() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			for (int length : new int[] { algorithm.outputLength() - 1, 0 }) {
				try (Secret prk = Secret.adopt(new byte[length])) {
					IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Kdfs.expand(algorithm, prk, INFO, 32));
					assertTrue(exception.getMessage().contains(String.valueOf(algorithm.outputLength())));
					assertTrue(exception.getMessage().contains(algorithm.name()));
				}
			}
		}
	}
	
	@Test
	void expandWithExactlyOneBlockPrk() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			try (Secret prk = Secret.random(algorithm.outputLength())) {
				assertDoesNotThrow(() -> materialOf(Kdfs.expand(algorithm, prk, INFO, 32)));
			}
		}
	}
	
	@Test
	void expandWithLongerThanOneBlockPrk() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			try (Secret prk = Secret.random(2 * algorithm.outputLength())) {
				assertEquals(32, materialOf(Kdfs.expand(algorithm, prk, INFO, 32)).length);
			}
		}
	}
	
	@Test
	void expandGuardOrder() {
		try (Secret prk = Secret.adopt(new byte[1])) {
			IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Kdfs.expand(ALGORITHM, prk, INFO, 0));
			assertTrue(exception.getMessage().contains("output length"));
			assertFalse(exception.getMessage().contains("pseudo random key"));
		}
	}
	
	@Test
	void expandWithClosedPrk() {
		Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM);
		prk.close();
		
		IllegalStateException exception = assertThrows(IllegalStateException.class, () -> Kdfs.expand(ALGORITHM, prk, INFO, 32));
		assertEquals("Secret has already been closed", exception.getMessage());
	}
	
	@Test
	void deriveWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Kdfs.derive(null, IKM, SALT, INFO, 32));
	}
	
	@Test
	void deriveWithNullIkm() {
		assertThrows(NullPointerException.class, () -> Kdfs.derive(ALGORITHM, (byte[]) null, SALT, INFO, 32));
	}
	
	@Test
	void deriveWithInvalidLength() {
		assertThrows(IllegalArgumentException.class, () -> Kdfs.derive(ALGORITHM, IKM, SALT, INFO, 0));
		assertThrows(IllegalArgumentException.class, () -> Kdfs.derive(ALGORITHM, IKM, SALT, INFO, 255 * ALGORITHM.outputLength() + 1));
	}
	
	@Test
	void deriveKeyWithNullTarget() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Kdfs.deriveKey(ALGORITHM, IKM, SALT, INFO, (AeadAlgorithm) null));
		assertEquals("Target must not be null", exception.getMessage());
	}
	
	@Test
	void deriveKeyWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Kdfs.deriveKey(null, IKM, SALT, INFO, AeadAlgorithm.AES_256_GCM));
	}
	
	@Test
	void deriveKeyWithNullIkm() {
		assertThrows(NullPointerException.class, () -> Kdfs.deriveKey(ALGORITHM, (byte[]) null, SALT, INFO, AeadAlgorithm.AES_256_GCM));
	}
	
	@Test
	void deriveKeyWithNullTargetAndNullIkm() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> Kdfs.deriveKey(ALGORITHM, (byte[]) null, SALT, INFO, (AeadAlgorithm) null));
		assertEquals("Target must not be null", exception.getMessage());
	}
	
	@Test
	void extractWithNullSalt() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			byte[] withoutSalt = materialOf(Kdfs.extract(algorithm, null, IKM));
			byte[] withZeroBlock = materialOf(Kdfs.extract(algorithm, new byte[algorithm.outputLength()], IKM));
			assertArrayEquals(withZeroBlock, withoutSalt);
		}
	}
	
	@Test
	void extractWithEmptySalt() {
		byte[] withoutSalt = materialOf(Kdfs.extract(ALGORITHM, null, IKM));
		assertArrayEquals(withoutSalt, materialOf(Kdfs.extract(ALGORITHM, new byte[0], IKM)));
	}
	
	@Test
	void extractWithNonEmptySalt() {
		byte[] salted = materialOf(Kdfs.extract(ALGORITHM, SALT, IKM));
		assertEquals(ALGORITHM.outputLength(), salted.length);
		assertFalse(Arrays.equals(salted, materialOf(Kdfs.extract(ALGORITHM, null, IKM))));
	}
	
	@Test
	void expandWithNullInfo() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			assertArrayEquals(materialOf(Kdfs.expand(ALGORITHM, prk, new byte[0], 32)), materialOf(Kdfs.expand(ALGORITHM, prk, null, 32)));
		}
	}
	
	@Test
	void expandWithEmptyInfo() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			assertArrayEquals(materialOf(Kdfs.expand(ALGORITHM, prk, null, 32)), materialOf(Kdfs.expand(ALGORITHM, prk, new byte[0], 32)));
		}
	}
	
	@Test
	void expandWithNonEmptyInfo() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			assertFalse(Arrays.equals(materialOf(Kdfs.expand(ALGORITHM, prk, null, 32)), materialOf(Kdfs.expand(ALGORITHM, prk, INFO, 32))));
		}
	}
	
	@Test
	void expandWithSingleBlockLength() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			byte[] single = materialOf(Kdfs.expand(ALGORITHM, prk, INFO, ALGORITHM.outputLength()));
			byte[] longer = materialOf(Kdfs.expand(ALGORITHM, prk, INFO, 3 * ALGORITHM.outputLength()));
			
			assertEquals(ALGORITHM.outputLength(), single.length);
			assertArrayEquals(single, Arrays.copyOf(longer, single.length));
		}
	}
	
	@Test
	void expandWithPartialBlockLength() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			byte[] block = materialOf(Kdfs.expand(ALGORITHM, prk, INFO, ALGORITHM.outputLength()));
			for (int length : new int[] { 1, ALGORITHM.outputLength() - 1 }) {
				byte[] partial = materialOf(Kdfs.expand(ALGORITHM, prk, INFO, length));
				assertEquals(length, partial.length);
				assertArrayEquals(Arrays.copyOf(block, length), partial);
			}
		}
	}
	
	@Test
	void expandWithMultiBlockLength() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			byte[] two = materialOf(Kdfs.expand(ALGORITHM, prk, INFO, 2 * ALGORITHM.outputLength()));
			byte[] three = materialOf(Kdfs.expand(ALGORITHM, prk, INFO, 3 * ALGORITHM.outputLength() + 1));
			
			assertEquals(2 * ALGORITHM.outputLength(), two.length);
			assertEquals(3 * ALGORITHM.outputLength() + 1, three.length);
			assertArrayEquals(two, Arrays.copyOf(three, two.length));
		}
	}
	
	@Test
	void expandAtMaximumLength() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			int maximum = 255 * ALGORITHM.outputLength();
			assertEquals(maximum, materialOf(Kdfs.expand(ALGORITHM, prk, INFO, maximum)).length);
		}
	}
	
	@Test
	void expandAtMinimumLength() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			assertEquals(1, materialOf(Kdfs.expand(ALGORITHM, prk, INFO, 1)).length);
		}
	}
	
	@Test
	void extractOutputLength() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertEquals(algorithm.outputLength(), materialOf(Kdfs.extract(algorithm, SALT, IKM)).length);
		}
	}
	
	@Test
	void extractIsDeterministic() {
		assertArrayEquals(materialOf(Kdfs.extract(ALGORITHM, SALT, IKM)), materialOf(Kdfs.extract(ALGORITHM, SALT, IKM)));
	}
	
	@Test
	void extractDiffersForDifferentIkm() {
		byte[] other = IKM.clone();
		other[0] ^= 1;
		assertFalse(Arrays.equals(materialOf(Kdfs.extract(ALGORITHM, SALT, IKM)), materialOf(Kdfs.extract(ALGORITHM, SALT, other))));
	}
	
	@Test
	void extractDiffersForDifferentSalt() {
		assertFalse(Arrays.equals(materialOf(Kdfs.extract(ALGORITHM, SALT, IKM)), materialOf(Kdfs.extract(ALGORITHM, "other".getBytes(StandardCharsets.UTF_8), IKM))));
	}
	
	@Test
	void expandIsDeterministic() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			assertArrayEquals(materialOf(Kdfs.expand(ALGORITHM, prk, INFO, 42)), materialOf(Kdfs.expand(ALGORITHM, prk, INFO, 42)));
		}
	}
	
	@Test
	void deriveMatchesExtractThenExpand() {
		byte[] manual;
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			manual = materialOf(Kdfs.expand(ALGORITHM, prk, INFO, 42));
		}
		assertArrayEquals(manual, derived(ALGORITHM, IKM, SALT, INFO, 42));
	}
	
	@Test
	void deriveIsDeterministic() {
		assertArrayEquals(derived(ALGORITHM, IKM, SALT, INFO, 42), derived(ALGORITHM, IKM, SALT, INFO, 42));
	}
	
	@Test
	void deriveWithNullSaltAndNullInfo() {
		byte[] first = assertDoesNotThrow(() -> derived(ALGORITHM, IKM, null, null, 32));
		assertEquals(32, first.length);
		assertArrayEquals(first, derived(ALGORITHM, IKM, null, null, 32));
	}
	
	@Test
	void deriveKeyLength() {
		for (AeadAlgorithm target : AeadAlgorithm.values()) {
			assertEquals(target.keyLength(), Kdfs.deriveKey(ALGORITHM, IKM, SALT, INFO, target).getEncoded().length);
		}
	}
	
	@Test
	void deriveKeyAlgorithmName() {
		for (AeadAlgorithm target : AeadAlgorithm.values()) {
			assertEquals(target.keyJcaName(), Kdfs.deriveKey(ALGORITHM, IKM, SALT, INFO, target).getAlgorithm());
		}
	}
	
	@Test
	void deriveKeyMatchesDerive() {
		AeadAlgorithm target = AeadAlgorithm.AES_256_GCM;
		assertArrayEquals(derived(ALGORITHM, IKM, SALT, INFO, target.keyLength()), Kdfs.deriveKey(ALGORITHM, IKM, SALT, INFO, target).getEncoded());
	}
	
	@Test
	void deriveKeyIsDeterministic() {
		AeadAlgorithm target = AeadAlgorithm.AES_256_GCM;
		assertArrayEquals(Kdfs.deriveKey(ALGORITHM, IKM, SALT, INFO, target).getEncoded(), Kdfs.deriveKey(ALGORITHM, IKM, SALT, INFO, target).getEncoded());
	}
	
	@Test
	void hkdfKnownAnswerVectors() {
		HexFormat hex = HexFormat.of();
		byte[] ikm = new byte[22];
		Arrays.fill(ikm, (byte) 0x0b);
		
		try (Secret prk = Kdfs.extract(ALGORITHM, hex.parseHex("000102030405060708090a0b0c"), ikm)) {
			assertEquals("077709362c2e32df0ddc3f0dc47bba6390b6c73bb50f9c3122ec844ad7c2b3e5", hex.formatHex(prk.material()));
			assertEquals("3cb25f25faacd57a90434f64d0362f2a2d2d0a90cf1a5a4c5db02d56ecc4c5bf34007208d5b887185865", hex.formatHex(materialOf(Kdfs.expand(ALGORITHM, prk, hex.parseHex("f0f1f2f3f4f5f6f7f8f9"), 42))));
		}
		try (Secret prk = Kdfs.extract(ALGORITHM, new byte[0], ikm)) {
			assertEquals("19ef24a32c717b167f33a91d6f648bdf96596776afdb6377ac434c1c293ccb04", hex.formatHex(prk.material()));
			assertEquals("8da4e775a563c18f715f802a063c5a31b8a11f5c5ee1879ec3454e5f3c738d2d9d201395faa4b61a96c8", hex.formatHex(materialOf(Kdfs.expand(ALGORITHM, prk, new byte[0], 42))));
		}
	}
	
	@Test
	void expandOutputsArePrefixConsistent() {
		int[] lengths = { 1, 16, 32, 33, 64, 100 };
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			byte[] longest = materialOf(Kdfs.expand(ALGORITHM, prk, INFO, 100));
			for (int length : lengths) {
				assertArrayEquals(Arrays.copyOf(longest, length), materialOf(Kdfs.expand(ALGORITHM, prk, INFO, length)));
			}
		}
	}
	
	@Test
	void differentInfoGivesIndependentKeys() {
		byte[] encryption = derived(ALGORITHM, IKM, SALT, "encryption".getBytes(StandardCharsets.UTF_8), 32);
		byte[] authentication = derived(ALGORITHM, IKM, SALT, "authentication".getBytes(StandardCharsets.UTF_8), 32);
		
		assertEquals(32, encryption.length);
		assertEquals(32, authentication.length);
		assertNotEquals(encryption[0], authentication[0]);
	}
	
	@Test
	void differentSaltGivesIndependentKeys() {
		byte[] first = derived(ALGORITHM, IKM, "first".getBytes(StandardCharsets.UTF_8), INFO, 32);
		byte[] second = derived(ALGORITHM, IKM, "second".getBytes(StandardCharsets.UTF_8), INFO, 32);
		
		assertFalse(Arrays.equals(first, second));
		assertNotEquals(first[0], second[0]);
	}
	
	@Test
	void deriveWipesIntermediatePrk() {
		Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM);
		byte[] live = prk.material();
		
		assertFalse(Arrays.equals(new byte[live.length], live));
		prk.close();
		assertArrayEquals(new byte[ALGORITHM.outputLength()], live);
	}
	
	@Test
	void deriveKeySurvivesTheClose() {
		SecretKey key = Kdfs.deriveKey(ALGORITHM, IKM, SALT, INFO, AeadAlgorithm.AES_256_GCM);
		
		assertEquals(32, key.getEncoded().length);
		assertFalse(Arrays.equals(new byte[32], key.getEncoded()));
		assertArrayEquals(derived(ALGORITHM, IKM, SALT, INFO, 32), key.getEncoded());
	}
	
	@Test
	void deriveKeyProducesUsableAeadKey() {
		byte[] message = "round trip".getBytes(StandardCharsets.UTF_8);
		for (AeadAlgorithm target : AeadAlgorithm.values()) {
			SecretKey key = Kdfs.deriveKey(ALGORITHM, IKM, SALT, INFO, target);
			byte[] ciphertext = Aeads.encrypt(target, key, message);
			assertArrayEquals(message, Aeads.decrypt(target, key, ciphertext));
		}
	}
	
	@Test
	void deriveForEveryAlgorithmAndTarget() {
		Set<String> keys = new HashSet<>();
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			for (AeadAlgorithm target : AeadAlgorithm.values()) {
				SecretKey key = Kdfs.deriveKey(algorithm, IKM, SALT, INFO, target);
				assertEquals(target.keyLength(), key.getEncoded().length);
				keys.add(HexFormat.of().formatHex(key.getEncoded()));
			}
		}
		assertEquals(3, keys.size());
	}
	
	@Test
	void extractDoesNotMutateInputs() {
		byte[] ikm = IKM.clone();
		byte[] salt = SALT.clone();
		
		materialOf(Kdfs.extract(ALGORITHM, salt, ikm));
		assertArrayEquals(IKM, ikm);
		assertArrayEquals(SALT, salt);
	}
	
	@Test
	void expandDoesNotMutateInfo() {
		byte[] info = INFO.clone();
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			materialOf(Kdfs.expand(ALGORITHM, prk, info, 32));
		}
		assertArrayEquals(INFO, info);
	}
	
	@Test
	void expandDoesNotCloseThePrk() {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			byte[] first = materialOf(Kdfs.expand(ALGORITHM, prk, INFO, 32));
			
			assertDoesNotThrow(prk::material);
			byte[] second = materialOf(Kdfs.expand(ALGORITHM, prk, "other".getBytes(StandardCharsets.UTF_8), 32));
			assertFalse(Arrays.equals(first, second));
			assertEquals(32, second.length);
		}
	}
	
	@Test
	void extractedSecretIsCallerOwned() {
		Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM);
		
		assertEquals(ALGORITHM.outputLength(), prk.length());
		assertDoesNotThrow(prk::material);
		byte[] live = prk.material();
		prk.close();
		assertArrayEquals(new byte[ALGORITHM.outputLength()], live);
		assertEquals(ALGORITHM.outputLength(), prk.length());
	}
	
	@Test
	void deriveWithLargeIkm() {
		byte[] ikm = CryptoRandom.bytes(100000);
		byte[] first = assertDoesNotThrow(() -> derived(ALGORITHM, ikm, SALT, INFO, 64));
		
		assertEquals(64, first.length);
		assertArrayEquals(first, derived(ALGORITHM, ikm, SALT, INFO, 64));
	}
	
	@Test
	void deriveWithLongSalt() {
		byte[] longSalt = CryptoRandom.bytes(200);
		byte[] withLongSalt = assertDoesNotThrow(() -> derived(ALGORITHM, IKM, longSalt, INFO, 32));
		
		assertEquals(32, withLongSalt.length);
		assertFalse(Arrays.equals(withLongSalt, derived(ALGORITHM, IKM, SALT, INFO, 32)));
	}
	
	@Test
	void everyAlgorithmResolvesThroughTheJdkKdfApi() throws Exception {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertTrue(Providers.supports(algorithm));
			KDF kdf = assertDoesNotThrow(() -> KDF.getInstance(algorithm.jcaName()));
			assertEquals("SunJCE", kdf.getProviderName());
		}
	}
	
	@Test
	void extractMatchesTheProviderDirectly() throws Exception {
		for (byte[] salt : new byte[][] { null, new byte[0], SALT }) {
			HKDFParameterSpec.Builder builder = HKDFParameterSpec.ofExtract().addIKM(IKM);
			if (salt != null && salt.length > 0) {
				builder.addSalt(salt);
			}
			
			byte[] direct = KDF.getInstance(ALGORITHM.jcaName()).deriveData(builder.extractOnly());
			assertArrayEquals(direct, materialOf(Kdfs.extract(ALGORITHM, salt, IKM)));
		}
	}
	
	@Test
	void expandMatchesTheProviderDirectly() throws Exception {
		try (Secret prk = Kdfs.extract(ALGORITHM, SALT, IKM)) {
			for (int length : new int[] { 1, 32, 100 }) {
				byte[] direct = KDF.getInstance(ALGORITHM.jcaName()).deriveData(HKDFParameterSpec.expandOnly(prk.toKey(ALGORITHM.jcaName()), INFO, length));
				assertArrayEquals(direct, materialOf(Kdfs.expand(ALGORITHM, prk, INFO, length)));
			}
		}
	}
	
	@Test
	void deriveKeyForTheWideNonceTarget() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			SecretKey key = Kdfs.deriveKey(algorithm, IKM, SALT, INFO, AeadAlgorithm.XCHACHA20_POLY1305);
			assertEquals(32, key.getEncoded().length);
			assertEquals("ChaCha20", key.getAlgorithm());
		}
	}
}
