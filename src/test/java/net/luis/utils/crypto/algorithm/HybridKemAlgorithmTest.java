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

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HybridKemAlgorithm}.<br>
 *
 * @author Luis-St
 */
class HybridKemAlgorithmTest {
	
	private static final DhKemAlgorithm SYNTHETIC_CLASSICAL = new DhKemAlgorithm("a", 7, 7);
	private static final NativeKemAlgorithm SYNTHETIC_POST_QUANTUM = new NativeKemAlgorithm("b", "jca", "key", 11, 13, 17);
	
	@Test
	void constructHybridKemAlgorithm() {
		HybridKemAlgorithm hybrid = new HybridKemAlgorithm(KemAlgorithm.X25519, KemAlgorithm.ML_KEM_768);
		assertSame(KemAlgorithm.X25519, hybrid.classical());
		assertSame(KemAlgorithm.ML_KEM_768, hybrid.postQuantum());
		assertTrue(hybrid.isPostQuantum());
	}
	
	@Test
	void constructWithNullClassical() {
		assertThrows(NullPointerException.class, () -> new HybridKemAlgorithm(null, KemAlgorithm.ML_KEM_768));
	}
	
	@Test
	void constructWithNullPostQuantum() {
		assertThrows(NullPointerException.class, () -> new HybridKemAlgorithm(KemAlgorithm.X25519, null));
	}
	
	@Test
	void constructWithBothComponentsNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new HybridKemAlgorithm(null, null));
		assertEquals("Classical component must not be null", exception.getMessage());
	}
	
	@Test
	void isPostQuantumAlwaysTrue() {
		assertTrue(KemAlgorithm.X25519_ML_KEM_768.isPostQuantum());
		assertTrue(KemAlgorithm.X448_ML_KEM_1024.isPostQuantum());
		assertTrue(new HybridKemAlgorithm(SYNTHETIC_CLASSICAL, SYNTHETIC_POST_QUANTUM).isPostQuantum());
		assertFalse(KemAlgorithm.X25519_ML_KEM_768.classical().isPostQuantum());
	}
	
	@Test
	void nameJoinsComponentNames() {
		assertEquals("X25519+ML-KEM-768", KemAlgorithm.X25519_ML_KEM_768.name());
		assertEquals("X448+ML-KEM-1024", KemAlgorithm.X448_ML_KEM_1024.name());
	}
	
	@Test
	void nameUsesPlusSeparator() {
		assertEquals("a+b", new HybridKemAlgorithm(SYNTHETIC_CLASSICAL, SYNTHETIC_POST_QUANTUM).name());
	}
	
	@Test
	void encapsulationLengthSumsComponents() {
		assertEquals(1120, KemAlgorithm.X25519_ML_KEM_768.encapsulationLength());
		assertEquals(1624, KemAlgorithm.X448_ML_KEM_1024.encapsulationLength());
	}
	
	@Test
	void publicKeyLengthSumsComponents() {
		assertEquals(1238, KemAlgorithm.X25519_ML_KEM_768.publicKeyLength());
		assertEquals(1646, KemAlgorithm.X448_ML_KEM_1024.publicKeyLength());
	}
	
	@Test
	void sharedSecretLengthIsFixed() {
		assertEquals(32, KemAlgorithm.X25519_ML_KEM_768.sharedSecretLength());
		assertEquals(32, KemAlgorithm.X448_ML_KEM_1024.sharedSecretLength());
		assertEquals(64, KemAlgorithm.X448.sharedSecretLength());
		assertEquals(32, new HybridKemAlgorithm(KemAlgorithm.X448, KemAlgorithm.ML_KEM_1024).sharedSecretLength());
	}
	
	@Test
	void isKemAlgorithm() {
		assertInstanceOf(KemAlgorithm.class, KemAlgorithm.X25519_ML_KEM_768);
	}
	
	@Test
	void lengthsDeriveFromComponentsGenerically() {
		HybridKemAlgorithm hybrid = new HybridKemAlgorithm(SYNTHETIC_CLASSICAL, SYNTHETIC_POST_QUANTUM);
		assertEquals(18, hybrid.encapsulationLength());
		assertEquals(20, hybrid.publicKeyLength());
		assertEquals(32, hybrid.sharedSecretLength());
	}
	
	@Test
	void equalsAndHashCodeOverComponents() {
		HybridKemAlgorithm hybrid = new HybridKemAlgorithm(KemAlgorithm.X25519, KemAlgorithm.ML_KEM_768);
		assertEquals(KemAlgorithm.X25519_ML_KEM_768, hybrid);
		assertEquals(KemAlgorithm.X25519_ML_KEM_768.hashCode(), hybrid.hashCode());
		assertNotEquals(hybrid, new HybridKemAlgorithm(KemAlgorithm.X448, KemAlgorithm.ML_KEM_768));
		assertNotEquals(hybrid, new HybridKemAlgorithm(KemAlgorithm.X25519, KemAlgorithm.ML_KEM_1024));
	}
	
	@Test
	void toStringContainsComponents() {
		String string = KemAlgorithm.X25519_ML_KEM_768.toString();
		assertTrue(string.contains("HybridKemAlgorithm"));
		assertTrue(string.contains("X25519"));
		assertTrue(string.contains("ML-KEM-768"));
	}
	
	@Test
	void hybridOfMismatchedSecurityLevels() {
		HybridKemAlgorithm hybrid = assertDoesNotThrow(() -> new HybridKemAlgorithm(KemAlgorithm.X448, KemAlgorithm.ML_KEM_512));
		assertEquals("X448+ML-KEM-512", hybrid.name());
		assertEquals(KemAlgorithm.X448.encapsulationLength() + KemAlgorithm.ML_KEM_512.encapsulationLength(), hybrid.encapsulationLength());
		assertEquals(KemAlgorithm.X448.publicKeyLength() + KemAlgorithm.ML_KEM_512.publicKeyLength(), hybrid.publicKeyLength());
	}
	
	@Test
	void nestedHybridIsNotConstructible() {
		Constructor<?>[] constructors = HybridKemAlgorithm.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertArrayEquals(new Class<?>[] { DhKemAlgorithm.class, NativeKemAlgorithm.class }, constructors[0].getParameterTypes());
	}
}
