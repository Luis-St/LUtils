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
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link NativeKemAlgorithm}.<br>
 *
 * @author Luis-St
 */
class NativeKemAlgorithmTest {
	
	@Test
	void constructNativeKemAlgorithm() {
		NativeKemAlgorithm algorithm = new NativeKemAlgorithm("ML-KEM-768", "ML-KEM", "ML-KEM-768", 1088, 1206, 32);
		assertEquals("ML-KEM-768", algorithm.name());
		assertEquals("ML-KEM", algorithm.jcaName());
		assertEquals("ML-KEM-768", algorithm.keyJcaName());
		assertEquals(1088, algorithm.encapsulationLength());
		assertEquals(1206, algorithm.publicKeyLength());
		assertEquals(32, algorithm.sharedSecretLength());
		assertTrue(algorithm.isPostQuantum());
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new NativeKemAlgorithm(null, "ML-KEM", "ML-KEM-768", 1, 1, 1));
	}
	
	@Test
	void constructWithNullJcaName() {
		assertThrows(NullPointerException.class, () -> new NativeKemAlgorithm("ML-KEM-768", null, "ML-KEM-768", 1, 1, 1));
	}
	
	@Test
	void constructWithNullKeyJcaName() {
		assertThrows(NullPointerException.class, () -> new NativeKemAlgorithm("ML-KEM-768", "ML-KEM", null, 1, 1, 1));
	}
	
	@Test
	void constructWithAllNullNames() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new NativeKemAlgorithm(null, null, null, 1, 1, 1));
		assertEquals("Name must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNegativeLengths() {
		NativeKemAlgorithm algorithm = assertDoesNotThrow(() -> new NativeKemAlgorithm("x", "y", "z", -1, -1, -1));
		assertEquals(-1, algorithm.encapsulationLength());
		assertEquals(-1, algorithm.publicKeyLength());
		assertEquals(-1, algorithm.sharedSecretLength());
	}
	
	@Test
	void isPostQuantumAlwaysTrue() {
		assertTrue(KemAlgorithm.ML_KEM_512.isPostQuantum());
		assertTrue(KemAlgorithm.ML_KEM_768.isPostQuantum());
		assertTrue(KemAlgorithm.ML_KEM_1024.isPostQuantum());
		assertTrue(new NativeKemAlgorithm("x", "y", "z", 1, 1, 1).isPostQuantum());
	}
	
	@Test
	void constructWithEmptyNames() {
		NativeKemAlgorithm algorithm = assertDoesNotThrow(() -> new NativeKemAlgorithm("", "", "", 1, 1, 1));
		assertEquals("", algorithm.name());
		assertEquals("", algorithm.jcaName());
		assertEquals("", algorithm.keyJcaName());
	}
	
	@Test
	void constructWithZeroLengths() {
		NativeKemAlgorithm algorithm = assertDoesNotThrow(() -> new NativeKemAlgorithm("x", "y", "z", 0, 0, 0));
		assertEquals(0, algorithm.encapsulationLength());
		assertEquals(0, algorithm.publicKeyLength());
		assertEquals(0, algorithm.sharedSecretLength());
	}
	
	@Test
	void accessorsReturnComponents() {
		NativeKemAlgorithm algorithm = new NativeKemAlgorithm("name", "jca", "key", 11, 22, 33);
		assertEquals("name", algorithm.name());
		assertEquals("jca", algorithm.jcaName());
		assertEquals("key", algorithm.keyJcaName());
		assertEquals(11, algorithm.encapsulationLength());
		assertEquals(22, algorithm.publicKeyLength());
		assertEquals(33, algorithm.sharedSecretLength());
	}
	
	@Test
	void isKemAlgorithm() {
		assertInstanceOf(KemAlgorithm.class, KemAlgorithm.ML_KEM_768);
	}
	
	@Test
	void equalsAndHashCodeOverAllComponents() {
		NativeKemAlgorithm algorithm = new NativeKemAlgorithm("name", "jca", "key", 11, 22, 33);
		assertEquals(algorithm, new NativeKemAlgorithm("name", "jca", "key", 11, 22, 33));
		assertEquals(algorithm.hashCode(), new NativeKemAlgorithm("name", "jca", "key", 11, 22, 33).hashCode());
		
		assertNotEquals(algorithm, new NativeKemAlgorithm("other", "jca", "key", 11, 22, 33));
		assertNotEquals(algorithm, new NativeKemAlgorithm("name", "other", "key", 11, 22, 33));
		assertNotEquals(algorithm, new NativeKemAlgorithm("name", "jca", "other", 11, 22, 33));
		assertNotEquals(algorithm, new NativeKemAlgorithm("name", "jca", "key", 99, 22, 33));
		assertNotEquals(algorithm, new NativeKemAlgorithm("name", "jca", "key", 11, 99, 33));
		assertNotEquals(algorithm, new NativeKemAlgorithm("name", "jca", "key", 11, 22, 99));
	}
	
	@Test
	void toStringContainsComponents() {
		String string = new NativeKemAlgorithm("name", "jca", "key", 11, 22, 33).toString();
		assertTrue(string.contains("NativeKemAlgorithm"));
		assertTrue(string.contains("name"));
		assertTrue(string.contains("jca"));
		assertTrue(string.contains("key"));
		assertTrue(string.contains("11"));
		assertTrue(string.contains("22"));
		assertTrue(string.contains("33"));
	}
	
	@Test
	void jcaNameAndKeyJcaNameDifferForSizedFamilies() {
		assertEquals("ML-KEM", KemAlgorithm.ML_KEM_768.jcaName());
		assertEquals("ML-KEM-768", KemAlgorithm.ML_KEM_768.keyJcaName());
		assertNotEquals(KemAlgorithm.ML_KEM_768.jcaName(), KemAlgorithm.ML_KEM_768.keyJcaName());
		
		assertEquals(KemAlgorithm.ML_KEM_512.jcaName(), KemAlgorithm.ML_KEM_1024.jcaName());
		assertNotEquals(KemAlgorithm.ML_KEM_512.keyJcaName(), KemAlgorithm.ML_KEM_1024.keyJcaName());
	}
	
	@Test
	void jcaNamesResolveAgainstProviders() {
		for (NativeKemAlgorithm algorithm : new NativeKemAlgorithm[] { KemAlgorithm.ML_KEM_512, KemAlgorithm.ML_KEM_768, KemAlgorithm.ML_KEM_1024 }) {
			assumeTrue(Providers.supports(algorithm));
			assertDoesNotThrow(() -> KeyPairGenerator.getInstance(algorithm.keyJcaName()));
			assertDoesNotThrow(() -> KEM.getInstance(algorithm.jcaName()));
		}
	}
	
	@Test
	void encapsulationLengthMatchesGeneratedEncapsulation() throws Exception {
		for (NativeKemAlgorithm algorithm : new NativeKemAlgorithm[] { KemAlgorithm.ML_KEM_512, KemAlgorithm.ML_KEM_768, KemAlgorithm.ML_KEM_1024 }) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = KeyPairGenerator.getInstance(algorithm.keyJcaName()).generateKeyPair();
			KEM.Encapsulated encapsulated = KEM.getInstance(algorithm.jcaName()).newEncapsulator(pair.getPublic()).encapsulate();
			assertEquals(algorithm.encapsulationLength(), encapsulated.encapsulation().length);
			assertEquals(algorithm.sharedSecretLength(), encapsulated.key().getEncoded().length);
		}
	}
}
