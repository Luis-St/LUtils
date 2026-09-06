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

import net.luis.utils.crypto.Providers;
import org.junit.jupiter.api.Test;

import javax.crypto.KEM;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KemAlgorithm}.<br>
 *
 * @author Luis-St
 */
class KemAlgorithmTest {
	
	@Test
	void byNameWithNullName() {
		assertThrows(NullPointerException.class, () -> KemAlgorithm.byName(null));
	}
	
	@Test
	void valuesIsImmutable() {
		assertThrows(UnsupportedOperationException.class, () -> KemAlgorithm.VALUES.add(KemAlgorithm.X25519));
		assertThrows(UnsupportedOperationException.class, () -> KemAlgorithm.VALUES.remove(0));
		assertThrows(UnsupportedOperationException.class, () -> KemAlgorithm.VALUES.set(0, KemAlgorithm.X25519));
	}
	
	@Test
	void byNameFindsExistingMechanism() {
		Optional<KemAlgorithm> result = KemAlgorithm.byName("ML-KEM-768");
		assertTrue(result.isPresent());
		assertSame(KemAlgorithm.ML_KEM_768, result.orElseThrow());
	}
	
	@Test
	void byNameWithUnknownName() {
		assertTrue(KemAlgorithm.byName("RSA-2048").isEmpty());
	}
	
	@Test
	void byNameWithEmptyName() {
		assertTrue(assertDoesNotThrow(() -> KemAlgorithm.byName("")).isEmpty());
	}
	
	@Test
	void byNameIsCaseSensitive() {
		assertTrue(KemAlgorithm.byName("ml-kem-768").isEmpty());
	}
	
	@Test
	void byNameResolvesEveryConstant() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			Optional<KemAlgorithm> result = KemAlgorithm.byName(algorithm.name());
			assertTrue(result.isPresent());
			assertSame(algorithm, result.orElseThrow());
		}
	}
	
	@Test
	void isPostQuantumBothValues() {
		assertTrue(KemAlgorithm.ML_KEM_512.isPostQuantum());
		assertTrue(KemAlgorithm.ML_KEM_768.isPostQuantum());
		assertTrue(KemAlgorithm.ML_KEM_1024.isPostQuantum());
		assertTrue(KemAlgorithm.X25519_ML_KEM_768.isPostQuantum());
		assertTrue(KemAlgorithm.X448_ML_KEM_1024.isPostQuantum());
		assertFalse(KemAlgorithm.X25519.isPostQuantum());
		assertFalse(KemAlgorithm.X448.isPostQuantum());
	}
	
	@Test
	void sharedSecretLengthIsNotAlwaysThePublicKeyLength() {
		assertEquals(32, KemAlgorithm.X25519.publicKeyLength());
		assertEquals(32, KemAlgorithm.X25519.sharedSecretLength());
		assertEquals(56, KemAlgorithm.X448.publicKeyLength());
		assertEquals(64, KemAlgorithm.X448.sharedSecretLength());
		assertEquals(32, KemAlgorithm.ML_KEM_768.sharedSecretLength());
		assertNotEquals(KemAlgorithm.ML_KEM_768.publicKeyLength(), KemAlgorithm.ML_KEM_768.sharedSecretLength());
	}
	
	@Test
	void valuesContainsEveryConstant() {
		assertEquals(7, KemAlgorithm.VALUES.size());
		for (KemAlgorithm algorithm : new KemAlgorithm[] {
			KemAlgorithm.ML_KEM_512, KemAlgorithm.ML_KEM_768, KemAlgorithm.ML_KEM_1024,
			KemAlgorithm.X25519, KemAlgorithm.X448, KemAlgorithm.X25519_ML_KEM_768, KemAlgorithm.X448_ML_KEM_1024
		}) {
			assertTrue(KemAlgorithm.VALUES.stream().anyMatch(value -> value == algorithm));
		}
	}
	
	@Test
	void valuesOrderIsStable() {
		assertEquals(List.of(KemAlgorithm.ML_KEM_512, KemAlgorithm.ML_KEM_768, KemAlgorithm.ML_KEM_1024,
			KemAlgorithm.X25519, KemAlgorithm.X448, KemAlgorithm.X25519_ML_KEM_768, KemAlgorithm.X448_ML_KEM_1024), KemAlgorithm.VALUES);
	}
	
	@Test
	void nameOfEveryConstant() {
		assertEquals("ML-KEM-512", KemAlgorithm.ML_KEM_512.name());
		assertEquals("ML-KEM-768", KemAlgorithm.ML_KEM_768.name());
		assertEquals("ML-KEM-1024", KemAlgorithm.ML_KEM_1024.name());
		assertEquals("X25519", KemAlgorithm.X25519.name());
		assertEquals("X448", KemAlgorithm.X448.name());
		assertEquals("X25519+ML-KEM-768", KemAlgorithm.X25519_ML_KEM_768.name());
		assertEquals("X448+ML-KEM-1024", KemAlgorithm.X448_ML_KEM_1024.name());
	}
	
	@Test
	void lengthsOfNativeConstants() {
		assertEquals(768, KemAlgorithm.ML_KEM_512.encapsulationLength());
		assertEquals(822, KemAlgorithm.ML_KEM_512.publicKeyLength());
		assertEquals(32, KemAlgorithm.ML_KEM_512.sharedSecretLength());
		assertEquals(1088, KemAlgorithm.ML_KEM_768.encapsulationLength());
		assertEquals(1206, KemAlgorithm.ML_KEM_768.publicKeyLength());
		assertEquals(1568, KemAlgorithm.ML_KEM_1024.encapsulationLength());
		assertEquals(1590, KemAlgorithm.ML_KEM_1024.publicKeyLength());
	}
	
	@Test
	void lengthsOfDhConstants() {
		assertEquals(32, KemAlgorithm.X25519.encapsulationLength());
		assertEquals(32, KemAlgorithm.X25519.publicKeyLength());
		assertEquals(32, KemAlgorithm.X25519.sharedSecretLength());
		assertEquals(56, KemAlgorithm.X448.encapsulationLength());
		assertEquals(56, KemAlgorithm.X448.publicKeyLength());
		assertEquals(64, KemAlgorithm.X448.sharedSecretLength());
	}
	
	@Test
	void namesAreUniqueAcrossValues() {
		Set<String> names = new HashSet<>();
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assertFalse(algorithm.name().isBlank());
			assertTrue(names.add(algorithm.name()));
		}
		assertEquals(7, names.size());
	}
	
	@Test
	void hybridLengthsDeriveFromComponents() {
		assertEquals(KemAlgorithm.X25519.encapsulationLength() + KemAlgorithm.ML_KEM_768.encapsulationLength(), KemAlgorithm.X25519_ML_KEM_768.encapsulationLength());
		assertEquals(1120, KemAlgorithm.X25519_ML_KEM_768.encapsulationLength());
		assertEquals(KemAlgorithm.X25519.publicKeyLength() + KemAlgorithm.ML_KEM_768.publicKeyLength(), KemAlgorithm.X25519_ML_KEM_768.publicKeyLength());
		assertEquals(1238, KemAlgorithm.X25519_ML_KEM_768.publicKeyLength());
		assertEquals(32, KemAlgorithm.X448_ML_KEM_1024.sharedSecretLength());
	}
	
	@Test
	void allLengthsArePositive() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assertTrue(algorithm.encapsulationLength() > 0);
			assertTrue(algorithm.publicKeyLength() > 0);
			assertTrue(algorithm.sharedSecretLength() > 0);
		}
	}
	
	@Test
	void sealedHierarchyPermitsThreeVariants() {
		assertTrue(KemAlgorithm.class.isSealed());
		assertEquals(Set.of(NativeKemAlgorithm.class, DhKemAlgorithm.class, HybridKemAlgorithm.class),
			Set.of(KemAlgorithm.class.getPermittedSubclasses()));
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assertTrue(algorithm instanceof NativeKemAlgorithm || algorithm instanceof DhKemAlgorithm || algorithm instanceof HybridKemAlgorithm);
		}
	}
	
	@Test
	void everyConstantResolvesThroughTheKemService() {
		assertDoesNotThrow(() -> KEM.getInstance(KemAlgorithm.ML_KEM_768.jcaName()));
		assertEquals("ML-KEM", KemAlgorithm.ML_KEM_512.jcaName());
		assertEquals("ML-KEM", KemAlgorithm.ML_KEM_1024.jcaName());
		assertEquals("DHKEM", KemAlgorithm.X25519.jcaName());
		assertEquals("DHKEM", KemAlgorithm.X448.jcaName());
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assertTrue(Providers.supports(algorithm));
		}
	}
	
	@Test
	void hybridConstantsPairClassicalWithPostQuantum() {
		assertSame(KemAlgorithm.X25519, KemAlgorithm.X25519_ML_KEM_768.classical());
		assertSame(KemAlgorithm.ML_KEM_768, KemAlgorithm.X25519_ML_KEM_768.postQuantum());
		assertSame(KemAlgorithm.X448, KemAlgorithm.X448_ML_KEM_1024.classical());
		assertSame(KemAlgorithm.ML_KEM_1024, KemAlgorithm.X448_ML_KEM_1024.postQuantum());
		assertFalse(KemAlgorithm.X25519_ML_KEM_768.classical().isPostQuantum());
		assertTrue(KemAlgorithm.X25519_ML_KEM_768.postQuantum().isPostQuantum());
	}
}
