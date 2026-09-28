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

import net.luis.utils.crypto.algorithm.*;
import net.luis.utils.crypto.exception.*;
import net.luis.utils.crypto.key.*;
import net.luis.utils.crypto.util.CryptoBytes;
import net.luis.utils.crypto.util.CryptoRandom;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.KEM;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.security.*;
import java.security.interfaces.XECPublicKey;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link Kems}.<br>
 *
 * @author Luis-St
 */
class KemsTest {
	
	private static final NativeKemAlgorithm FAKE = new NativeKemAlgorithm("fake", "NoSuchKem", "NoSuchKeyType", 1, 1, 1);
	
	private static KeyPair x25519;
	private static KeyPair x448;
	private static KeyPair mlKem768;
	private static KeyPair hybrid;
	private static KeyPair ed25519;
	
	@BeforeAll
	static void setUp() throws Exception {
		Providers.installBouncyCastle();
		x25519 = Kems.generateKeyPair(KemAlgorithm.X25519);
		x448 = Kems.generateKeyPair(KemAlgorithm.X448);
		mlKem768 = Kems.generateKeyPair(KemAlgorithm.ML_KEM_768);
		hybrid = Kems.generateKeyPair(KemAlgorithm.X25519_ML_KEM_768);
		ed25519 = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
	}
	
	private static byte[] materialOf(Secret secret) {
		try (secret) {
			return secret.material().clone();
		}
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = Kems.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(Kems.class.getModifiers()));
		
		Constructor<Kems> constructor = Kems.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void constructEncapsulation() {
		Kems.Encapsulation encapsulation = new Kems.Encapsulation(new byte[] { 1, 2 }, Secret.copyOf(new byte[32]));
		assertArrayEquals(new byte[] { 1, 2 }, encapsulation.encapsulation());
		assertEquals(32, encapsulation.sharedSecret().length());
	}
	
	@Test
	void constructEncapsulationWithNullEncapsulation() {
		assertEquals("Encapsulation must not be null", assertThrows(NullPointerException.class, () -> new Kems.Encapsulation(null, Secret.copyOf(new byte[32]))).getMessage());
	}
	
	@Test
	void constructEncapsulationWithNullSharedSecret() {
		assertEquals("Shared secret must not be null", assertThrows(NullPointerException.class, () -> new Kems.Encapsulation(new byte[1], null)).getMessage());
	}
	
	@Test
	void constructEncapsulationWithBothNull() {
		assertEquals("Encapsulation must not be null", assertThrows(NullPointerException.class, () -> new Kems.Encapsulation(null, null)).getMessage());
	}
	
	@Test
	void generateKeyPairWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Kems.generateKeyPair(null)).getMessage());
	}
	
	@Test
	void generateKeyPairWithUnservedAlgorithm() {
		UnsupportedAlgorithmException exception = assertThrows(UnsupportedAlgorithmException.class, () -> Kems.generateKeyPair(FAKE));
		assertTrue(exception.getMessage().contains("NoSuchKeyType"));
		assertInstanceOf(NoSuchAlgorithmException.class, exception.getCause());
	}
	
	@Test
	void encapsulateWithNullAlgorithm() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Kems.encapsulate(null, x25519.getPublic())).getMessage());
	}
	
	@Test
	void encapsulateWithNullRecipient() {
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> Kems.encapsulate(KemAlgorithm.X25519, null)).getMessage());
	}
	
	@Test
	void encapsulateWithBothNull() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> Kems.encapsulate(null, null)).getMessage());
	}
	
	@Test
	void encapsulateHybridWithNonHybridKey() {
		assertThrows(ClassCastException.class, () -> Kems.encapsulate(KemAlgorithm.X25519_ML_KEM_768, x25519.getPublic()));
	}
	
	@Test
	void encapsulateNativeWithWrongKeyType() {
		CryptoException exception = assertThrows(CryptoException.class, () -> Kems.encapsulate(KemAlgorithm.ML_KEM_768, x25519.getPublic()));
		assertTrue(exception.getMessage().contains("Encapsulation failed for ML-KEM-768"));
	}
	
	@Test
	void encapsulateDhWithWrongKeyType() {
		CryptoException exception = assertThrows(CryptoException.class, () -> Kems.encapsulate(KemAlgorithm.X25519, ed25519.getPublic()));
		assertEquals("Encapsulation failed for X25519", exception.getMessage());
		assertFalse(ClassCastException.class.isInstance(exception));
	}
	
	@Test
	void encapsulateDhWithMismatchedCurve() {
		try (Kems.Encapsulation sent = assertDoesNotThrow(() -> Kems.encapsulate(KemAlgorithm.X25519, x448.getPublic()))) {
			assertEquals(KemAlgorithm.X448.encapsulationLength(), sent.encapsulation().length);
			assertNotEquals(KemAlgorithm.X25519.encapsulationLength(), sent.encapsulation().length);
			assertThrows(MalformedDataException.class, () -> Kems.decapsulate(KemAlgorithm.X25519, x448.getPrivate(), sent.encapsulation()));
		}
	}
	
	@Test
	void decapsulateWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> Kems.decapsulate(null, x25519.getPrivate(), new byte[32]));
	}
	
	@Test
	void decapsulateWithNullRecipient() {
		assertEquals("Recipient must not be null", assertThrows(NullPointerException.class, () -> Kems.decapsulate(KemAlgorithm.X25519, null, new byte[32])).getMessage());
	}
	
	@Test
	void decapsulateWithNullEncapsulation() {
		assertEquals("Encapsulation must not be null", assertThrows(NullPointerException.class, () -> Kems.decapsulate(KemAlgorithm.X25519, x25519.getPrivate(), null)).getMessage());
	}
	
	@Test
	void decapsulateWithWrongEncapsulationLength() {
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.X25519, KemAlgorithm.ML_KEM_768, KemAlgorithm.X25519_ML_KEM_768)) {
			PrivateKey recipient = switch (algorithm.name()) {
				case "X25519" -> x25519.getPrivate();
				case "ML-KEM-768" -> mlKem768.getPrivate();
				default -> hybrid.getPrivate();
			};
			for (int length : new int[] { algorithm.encapsulationLength() - 1, algorithm.encapsulationLength() + 1, 0, 100000 }) {
				MalformedDataException exception = assertThrows(MalformedDataException.class, () -> Kems.decapsulate(algorithm, recipient, new byte[length]));
				assertTrue(exception.getMessage().contains(String.valueOf(algorithm.encapsulationLength())));
				assertTrue(exception.getMessage().contains(algorithm.name()));
				assertTrue(exception.getMessage().contains(String.valueOf(length)));
			}
		}
	}
	
	@Test
	void decapsulateHybridWithNonHybridKey() {
		byte[] encapsulation = new byte[KemAlgorithm.X25519_ML_KEM_768.encapsulationLength()];
		assertThrows(ClassCastException.class, () -> Kems.decapsulate(KemAlgorithm.X25519_ML_KEM_768, x25519.getPrivate(), encapsulation));
	}
	
	@Test
	void decapsulateNativeWithWrongKey() {
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.ML_KEM_768, mlKem768.getPublic())) {
			AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Kems.decapsulate(KemAlgorithm.ML_KEM_768, ed25519.getPrivate(), sent.encapsulation()));
			assertTrue(exception.getMessage().contains("Decapsulation failed for ML-KEM-768"));
			assertInstanceOf(CryptoException.class, exception);
		}
	}
	
	@Test
	void decapsulateNativeWithGarbageEncapsulation() {
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.ML_KEM_768, mlKem768.getPublic())) {
			byte[] garbage = CryptoRandom.bytes(KemAlgorithm.ML_KEM_768.encapsulationLength());
			byte[] expected = sent.sharedSecret().material();
			try {
				assertFalse(CryptoBytes.equalsConstantTime(expected, materialOf(Kems.decapsulate(KemAlgorithm.ML_KEM_768, mlKem768.getPrivate(), garbage))));
			} catch (AuthenticationException e) {
				assertTrue(e.getMessage().contains("ML-KEM-768"));
			}
		}
	}
	
	@Test
	void decapsulateDhWithGarbageEncapsulation() {
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic())) {
			byte[] garbage = CryptoRandom.bytes(KemAlgorithm.X25519.encapsulationLength());
			byte[] expected = sent.sharedSecret().material();
			try {
				assertFalse(CryptoBytes.equalsConstantTime(expected, materialOf(Kems.decapsulate(KemAlgorithm.X25519, x25519.getPrivate(), garbage))));
			} catch (AuthenticationException e) {
				assertTrue(e.getMessage().contains("X25519"));
			}
		}
	}
	
	@Test
	void decapsulateDhWithWrongPrivateKey() {
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic())) {
			AuthenticationException exception = assertThrows(AuthenticationException.class, () -> Kems.decapsulate(KemAlgorithm.X25519, ed25519.getPrivate(), sent.encapsulation()));
			assertEquals("Decapsulation failed for X25519", exception.getMessage());
			assertInstanceOf(CryptoException.class, exception);
		}
	}
	
	@Test
	void generateKeyPairForNativeKem() {
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.ML_KEM_512, KemAlgorithm.ML_KEM_768, KemAlgorithm.ML_KEM_1024)) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = Kems.generateKeyPair(algorithm);
			assertNotNull(pair.getPublic());
			assertEquals("ML-DSA".equals(pair.getPublic().getAlgorithm()) ? "ML-DSA" : "ML-KEM", pair.getPublic().getAlgorithm());
		}
	}
	
	@Test
	void generateKeyPairForDhKem() {
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.X25519, KemAlgorithm.X448)) {
			KeyPair pair = Kems.generateKeyPair(algorithm);
			assertInstanceOf(XECPublicKey.class, pair.getPublic());
			assertNotNull(pair.getPrivate());
		}
	}
	
	@Test
	void generateKeyPairForHybridKem() {
		KeyPair pair = Kems.generateKeyPair(KemAlgorithm.X25519_ML_KEM_768);
		assertInstanceOf(HybridPublicKey.class, pair.getPublic());
		assertInstanceOf(HybridPrivateKey.class, pair.getPrivate());
	}
	
	@Test
	void encapsulateAndDecapsulateNativeKem() {
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.ML_KEM_768, mlKem768.getPublic())) {
			assertEquals(KemAlgorithm.ML_KEM_768.encapsulationLength(), sent.encapsulation().length);
			assertArrayEquals(sent.sharedSecret().material(), materialOf(Kems.decapsulate(KemAlgorithm.ML_KEM_768, mlKem768.getPrivate(), sent.encapsulation())));
		}
	}
	
	@Test
	void encapsulateAndDecapsulateDhKem() {
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.X25519, KemAlgorithm.X448)) {
			KeyPair pair = KemAlgorithm.X25519.equals(algorithm) ? x25519 : x448;
			try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, pair.getPublic())) {
				assertEquals(algorithm.encapsulationLength(), sent.encapsulation().length);
				assertEquals(algorithm.sharedSecretLength(), sent.sharedSecret().length());
				assertArrayEquals(sent.sharedSecret().material(), materialOf(Kems.decapsulate(algorithm, pair.getPrivate(), sent.encapsulation())));
			}
		}
		assertEquals(64, KemAlgorithm.X448.sharedSecretLength());
	}
	
	@Test
	void dhAndNativeArmsShareOneImplementation() {
		CryptoException dh = assertThrows(CryptoException.class, () -> Kems.encapsulate(KemAlgorithm.X25519, ed25519.getPublic()));
		CryptoException native0 = assertThrows(CryptoException.class, () -> Kems.encapsulate(KemAlgorithm.ML_KEM_768, ed25519.getPublic()));
		
		assertEquals("Encapsulation failed for X25519", dh.getMessage());
		assertEquals("Encapsulation failed for ML-KEM-768", native0.getMessage());
		assertEquals(dh.getClass(), native0.getClass());
	}
	
	@Test
	void encapsulateAndDecapsulateHybridKem() {
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.X25519_ML_KEM_768, KemAlgorithm.X448_ML_KEM_1024)) {
			HybridKemAlgorithm hybridAlgorithm = assertInstanceOf(HybridKemAlgorithm.class, algorithm);
			KeyPair pair = Kems.generateKeyPair(algorithm);
			try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, pair.getPublic())) {
				assertEquals(hybridAlgorithm.classical().encapsulationLength() + hybridAlgorithm.postQuantum().encapsulationLength(), sent.encapsulation().length);
				assertArrayEquals(sent.sharedSecret().material(), materialOf(Kems.decapsulate(algorithm, pair.getPrivate(), sent.encapsulation())));
			}
		}
	}
	
	@Test
	void decapsulateWithCorrectEncapsulationLength() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = Kems.generateKeyPair(algorithm);
			try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, pair.getPublic())) {
				assertEquals(algorithm.encapsulationLength(), sent.encapsulation().length);
				assertDoesNotThrow(() -> materialOf(Kems.decapsulate(algorithm, pair.getPrivate(), sent.encapsulation())));
			}
		}
	}
	
	@Test
	void encapsulationCloseWipesSecret() {
		Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic());
		byte[] encapsulation = sent.encapsulation().clone();
		
		sent.close();
		assertThrows(IllegalStateException.class, () -> sent.sharedSecret().material());
		assertEquals(32, sent.sharedSecret().length());
		assertArrayEquals(encapsulation, sent.encapsulation());
	}
	
	@Test
	void encapsulationLengthPerMechanism() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = Kems.generateKeyPair(algorithm);
			try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, pair.getPublic())) {
				assertEquals(algorithm.encapsulationLength(), sent.encapsulation().length, algorithm.name());
			}
		}
	}
	
	@Test
	void sharedSecretLengthPerMechanism() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = Kems.generateKeyPair(algorithm);
			try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, pair.getPublic())) {
				assertEquals(algorithm.sharedSecretLength(), sent.sharedSecret().length(), algorithm.name());
			}
		}
		assertEquals(32, KemAlgorithm.X25519.sharedSecretLength());
		assertEquals(64, KemAlgorithm.X448.sharedSecretLength());
	}
	
	@Test
	void encapsulateProducesFreshSecretsPerCall() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = Kems.generateKeyPair(algorithm);
			try (Kems.Encapsulation first = Kems.encapsulate(algorithm, pair.getPublic()); Kems.Encapsulation second = Kems.encapsulate(algorithm, pair.getPublic())) {
				assertFalse(Arrays.equals(first.encapsulation(), second.encapsulation()), algorithm.name());
				assertFalse(Arrays.equals(first.sharedSecret().material(), second.sharedSecret().material()), algorithm.name());
			}
		}
	}
	
	@Test
	void generateKeyPairProducesDistinctPairs() {
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.X25519, KemAlgorithm.ML_KEM_768)) {
			assertFalse(Arrays.equals(Kems.generateKeyPair(algorithm).getPublic().getEncoded(), Kems.generateKeyPair(algorithm).getPublic().getEncoded()));
		}
	}
	
	@Test
	void encapsulationIsNotWipedOnClose() {
		Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic());
		byte[] before = sent.encapsulation().clone();
		
		sent.close();
		assertArrayEquals(before, sent.encapsulation());
		assertFalse(Arrays.equals(new byte[before.length], sent.encapsulation()));
	}
	
	@Test
	void encapsulationCloseIsIdempotent() {
		Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic());
		assertDoesNotThrow(() -> {
			sent.close();
			sent.close();
		});
	}
	
	@Test
	void encapsulationInTryWithResources() {
		byte[] captured;
		Kems.Encapsulation escaped;
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic())) {
			escaped = sent;
			captured = sent.encapsulation();
			assertNotNull(sent.sharedSecret().material());
		}
		
		assertThrows(IllegalStateException.class, () -> escaped.sharedSecret().material());
		assertArrayEquals(captured, escaped.encapsulation());
	}
	
	@Test
	void hybridEncapsulationConcatenatesComponents() {
		HybridKemAlgorithm algorithm = assertInstanceOf(HybridKemAlgorithm.class, KemAlgorithm.X25519_ML_KEM_768);
		HybridPrivateKey recipient = assertInstanceOf(HybridPrivateKey.class, hybrid.getPrivate());
		
		try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, hybrid.getPublic())) {
			byte[] classicalPart = CryptoBytes.slice(sent.encapsulation(), 0, algorithm.classical().encapsulationLength());
			assertEquals(32, classicalPart.length);
			assertDoesNotThrow(() -> materialOf(Kems.decapsulate(algorithm.classical(), recipient.classical(), classicalPart)));
		}
	}
	
	@Test
	void roundTripForEveryMechanism() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = Kems.generateKeyPair(algorithm);
			try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, pair.getPublic())) {
				assertArrayEquals(sent.sharedSecret().material(), materialOf(Kems.decapsulate(algorithm, pair.getPrivate(), sent.encapsulation())), algorithm.name());
			}
		}
	}
	
	@Test
	void decapsulateWithWrongKeyGivesDifferentSecret() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair first = Kems.generateKeyPair(algorithm);
			KeyPair second = Kems.generateKeyPair(algorithm);
			
			try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, first.getPublic())) {
				byte[] expected = sent.sharedSecret().material().clone();
				try {
					assertFalse(CryptoBytes.equalsConstantTime(expected, materialOf(Kems.decapsulate(algorithm, second.getPrivate(), sent.encapsulation()))), algorithm.name());
				} catch (CryptoException e) {
					assertInstanceOf(AuthenticationException.class, e);
				}
			}
		}
	}
	
	@Test
	void hybridSecretDependsOnBothComponents() {
		HybridKemAlgorithm algorithm = assertInstanceOf(HybridKemAlgorithm.class, KemAlgorithm.X25519_ML_KEM_768);
		int split = algorithm.classical().encapsulationLength();
		
		try (Kems.Encapsulation first = Kems.encapsulate(algorithm, hybrid.getPublic()); Kems.Encapsulation second = Kems.encapsulate(algorithm, hybrid.getPublic())) {
			byte[] expected = materialOf(Kems.decapsulate(algorithm, hybrid.getPrivate(), first.encapsulation()));
			byte[] swappedClassical = CryptoBytes.concat(CryptoBytes.slice(second.encapsulation(), 0, split), CryptoBytes.slice(first.encapsulation(), split, first.encapsulation().length - split));
			byte[] swappedPostQuantum = CryptoBytes.concat(CryptoBytes.slice(first.encapsulation(), 0, split), CryptoBytes.slice(second.encapsulation(), split, second.encapsulation().length - split));
			
			assertFalse(CryptoBytes.equalsConstantTime(expected, materialOf(Kems.decapsulate(algorithm, hybrid.getPrivate(), swappedClassical))));
			assertFalse(CryptoBytes.equalsConstantTime(expected, materialOf(Kems.decapsulate(algorithm, hybrid.getPrivate(), swappedPostQuantum))));
		}
	}
	
	@Test
	void hybridCombinerBindsTheMechanismName() {
		Set<String> secrets = new HashSet<>();
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.X25519_ML_KEM_768, KemAlgorithm.X448_ML_KEM_1024)) {
			KeyPair pair = Kems.generateKeyPair(algorithm);
			for (int i = 0; i < 5; i++) {
				try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, pair.getPublic())) {
					secrets.add(HexFormat.of().formatHex(sent.sharedSecret().material()));
				}
			}
		}
		assertEquals(10, secrets.size());
	}
	
	@Test
	void hybridSecretDiffersFromEitherComponentSecret() {
		HybridKemAlgorithm algorithm = assertInstanceOf(HybridKemAlgorithm.class, KemAlgorithm.X25519_ML_KEM_768);
		HybridPrivateKey recipient = assertInstanceOf(HybridPrivateKey.class, hybrid.getPrivate());
		int split = algorithm.classical().encapsulationLength();
		
		try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, hybrid.getPublic())) {
			byte[] combined = sent.sharedSecret().material();
			byte[] classical = materialOf(Kems.decapsulate(algorithm.classical(), recipient.classical(), CryptoBytes.slice(sent.encapsulation(), 0, split)));
			byte[] postQuantum = materialOf(Kems.decapsulate(algorithm.postQuantum(), recipient.postQuantum(), CryptoBytes.slice(sent.encapsulation(), split, sent.encapsulation().length - split)));
			
			assertFalse(Arrays.equals(combined, classical));
			assertFalse(Arrays.equals(combined, postQuantum));
			assertFalse(Arrays.equals(combined, CryptoBytes.xor(classical, postQuantum)));
			assertFalse(Arrays.equals(combined, Arrays.copyOf(CryptoBytes.concat(postQuantum, classical), combined.length)));
		}
	}
	
	@Test
	void dhKemMatchesTheProviderDirectly() throws Exception {
		for (KemAlgorithm algorithm : List.of(KemAlgorithm.X25519, KemAlgorithm.X448)) {
			KeyPair pair = KemAlgorithm.X25519.equals(algorithm) ? x25519 : x448;
			try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, pair.getPublic())) {
				byte[] direct = KEM.getInstance("DHKEM").newDecapsulator(pair.getPrivate()).decapsulate(sent.encapsulation()).getEncoded();
				assertArrayEquals(sent.sharedSecret().material(), direct, algorithm.name());
			}
		}
	}
	
	@Test
	void dhKemEncapsulationIsTheRawUCoordinate() {
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic())) {
			assertEquals(32, sent.encapsulation().length);
			assertEquals(KemAlgorithm.X25519.publicKeyLength(), sent.encapsulation().length);
			assertNotEquals(44, sent.encapsulation().length);
		}
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X448, x448.getPublic())) {
			assertEquals(56, sent.encapsulation().length);
			assertNotEquals(68, sent.encapsulation().length);
		}
	}
	
	@Test
	void dhKemBindsTheRecipientKey() {
		KeyPair other = Kems.generateKeyPair(KemAlgorithm.X25519);
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic())) {
			byte[] expected = sent.sharedSecret().material().clone();
			try {
				assertFalse(CryptoBytes.equalsConstantTime(expected, materialOf(Kems.decapsulate(KemAlgorithm.X25519, other.getPrivate(), sent.encapsulation()))));
			} catch (AuthenticationException e) {
				assertTrue(e.getMessage().contains("X25519"));
			}
		}
	}
	
	@Test
	void decapsulateDhWorksFromStoredPrivateKeyAlone() {
		byte[] encoded = x25519.getPrivate().getEncoded();
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic())) {
			PrivateKey restored = CryptoKeys.privateKey(KemAlgorithm.X25519, encoded);
			assertArrayEquals(sent.sharedSecret().material(), materialOf(Kems.decapsulate(KemAlgorithm.X25519, restored, sent.encapsulation())));
		}
	}
	
	@Test
	void encapsulationRecordEqualsIsIdentityBased() {
		byte[] bytes = { 1, 2 };
		Kems.Encapsulation first = new Kems.Encapsulation(bytes, Secret.copyOf(new byte[32]));
		
		assertNotEquals(new Kems.Encapsulation(bytes.clone(), Secret.copyOf(new byte[32])), first);
		assertEquals(first, first);
	}
	
	@Test
	void encapsulateDoesNotMutateRecipientKey() {
		byte[] before = x25519.getPublic().getEncoded();
		try (Kems.Encapsulation ignored = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic())) {
			assertArrayEquals(before, x25519.getPublic().getEncoded());
		}
	}
	
	@Test
	void decapsulateDoesNotMutateEncapsulation() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = Kems.generateKeyPair(algorithm);
			try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, pair.getPublic())) {
				byte[] before = sent.encapsulation().clone();
				materialOf(Kems.decapsulate(algorithm, pair.getPrivate(), sent.encapsulation()));
				assertArrayEquals(before, sent.encapsulation(), algorithm.name());
			}
		}
	}
	
	@Test
	void secretsAreWipedOnClose() {
		Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519, x25519.getPublic());
		byte[] live = sent.sharedSecret().material();
		
		assertFalse(Arrays.equals(new byte[live.length], live));
		sent.close();
		assertArrayEquals(new byte[32], live);
	}
	
	@Test
	void keyPairFromKemsIsUsableWithCryptoKeys() {
		PublicKey restoredPublic = CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, hybrid.getPublic().getEncoded());
		PrivateKey restoredPrivate = CryptoKeys.privateKey(KemAlgorithm.X25519_ML_KEM_768, hybrid.getPrivate().getEncoded());
		
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519_ML_KEM_768, restoredPublic)) {
			assertArrayEquals(sent.sharedSecret().material(), materialOf(Kems.decapsulate(KemAlgorithm.X25519_ML_KEM_768, restoredPrivate, sent.encapsulation())));
			assertArrayEquals(sent.sharedSecret().material(), materialOf(Kems.decapsulate(KemAlgorithm.X25519_ML_KEM_768, hybrid.getPrivate(), sent.encapsulation())));
		}
	}
	
	@Test
	void hybridDecapsulateSplitsAtTheRightOffset() {
		assertEquals(32, KemAlgorithm.X25519.encapsulationLength());
		assertEquals(56, KemAlgorithm.X448.encapsulationLength());
		
		HybridKemAlgorithm algorithm = assertInstanceOf(HybridKemAlgorithm.class, KemAlgorithm.X25519_ML_KEM_768);
		try (Kems.Encapsulation sent = Kems.encapsulate(algorithm, hybrid.getPublic())) {
			byte[] expected = materialOf(Kems.decapsulate(algorithm, hybrid.getPrivate(), sent.encapsulation()));
			for (int index : new int[] { 31, 32 }) {
				byte[] corrupted = sent.encapsulation().clone();
				corrupted[index] ^= 1;
				try {
					assertFalse(CryptoBytes.equalsConstantTime(expected, materialOf(Kems.decapsulate(algorithm, hybrid.getPrivate(), corrupted))), "index " + index);
				} catch (AuthenticationException ignored) {
					assertTrue(true);
				}
			}
		}
	}
}
