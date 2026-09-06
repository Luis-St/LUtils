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

/**
 * Test class for {@link DhKemAlgorithm}.<br>
 *
 * @author Luis-St
 */
class DhKemAlgorithmTest {
	
	@Test
	void constructDhKemAlgorithm() {
		DhKemAlgorithm algorithm = new DhKemAlgorithm("X25519", 32, 32);
		assertEquals("X25519", algorithm.name());
		assertEquals(32, algorithm.publicKeyLength());
		assertEquals(32, algorithm.sharedSecretLength());
		assertEquals(32, algorithm.encapsulationLength());
		assertEquals("DHKEM", algorithm.jcaName());
		assertFalse(algorithm.isPostQuantum());
	}
	
	@Test
	void constructWithNullName() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new DhKemAlgorithm(null, 32, 32));
		assertEquals("Name must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNegativeLengths() {
		DhKemAlgorithm algorithm = assertDoesNotThrow(() -> new DhKemAlgorithm("x", -1, -1));
		assertEquals(-1, algorithm.publicKeyLength());
		assertEquals(-1, algorithm.sharedSecretLength());
		assertEquals(-1, algorithm.encapsulationLength());
	}
	
	@Test
	void constructWithZeroLengths() {
		DhKemAlgorithm algorithm = assertDoesNotThrow(() -> new DhKemAlgorithm("x", 0, 0));
		assertEquals(0, algorithm.publicKeyLength());
		assertEquals(0, algorithm.sharedSecretLength());
	}
	
	@Test
	void isPostQuantumAlwaysFalse() {
		assertFalse(KemAlgorithm.X25519.isPostQuantum());
		assertFalse(KemAlgorithm.X448.isPostQuantum());
		assertFalse(new DhKemAlgorithm("x", 1, 1).isPostQuantum());
	}
	
	@Test
	void accessorsForEveryConstant() {
		for (DhKemAlgorithm algorithm : new DhKemAlgorithm[] { KemAlgorithm.X25519, KemAlgorithm.X448 }) {
			assertFalse(algorithm.name().isBlank());
			assertFalse(algorithm.jcaName().isBlank());
			assertFalse(algorithm.keyJcaName().isBlank());
			assertTrue(algorithm.publicKeyLength() > 0);
			assertTrue(algorithm.sharedSecretLength() > 0);
			assertEquals(algorithm.publicKeyLength(), algorithm.encapsulationLength());
		}
	}
	
	@Test
	void jcaNameIsTheSharedDhkemName() {
		assertEquals("DHKEM", KemAlgorithm.X25519.jcaName());
		assertEquals("DHKEM", KemAlgorithm.X448.jcaName());
		assertEquals("DHKEM", new DhKemAlgorithm("anything", 1, 1).jcaName());
		assertNotEquals(KemAlgorithm.X25519.name(), KemAlgorithm.X25519.jcaName());
		assertNotEquals(KemAlgorithm.X448.name(), KemAlgorithm.X448.jcaName());
	}
	
	@Test
	void keyJcaNameEqualsName() {
		assertEquals(KemAlgorithm.X25519.name(), KemAlgorithm.X25519.keyJcaName());
		assertEquals(KemAlgorithm.X448.name(), KemAlgorithm.X448.keyJcaName());
		assertEquals("custom", new DhKemAlgorithm("custom", 1, 1).keyJcaName());
	}
	
	@Test
	void encapsulationLengthEqualsPublicKeyLength() {
		DhKemAlgorithm algorithm = new DhKemAlgorithm("x", 56, 40);
		assertEquals(56, algorithm.encapsulationLength());
		assertNotEquals(40, algorithm.encapsulationLength());
	}
	
	@Test
	void lengthsOfConstants() {
		assertEquals(32, KemAlgorithm.X25519.publicKeyLength());
		assertEquals(32, KemAlgorithm.X25519.sharedSecretLength());
		assertEquals(32, KemAlgorithm.X25519.encapsulationLength());
		assertEquals(56, KemAlgorithm.X448.publicKeyLength());
		assertEquals(64, KemAlgorithm.X448.sharedSecretLength());
		assertEquals(56, KemAlgorithm.X448.encapsulationLength());
	}
	
	@Test
	void x448SharedSecretIsWiderThanItsPublicKey() {
		assertEquals(64, KemAlgorithm.X448.sharedSecretLength());
		assertEquals(56, KemAlgorithm.X448.publicKeyLength());
		assertTrue(KemAlgorithm.X448.sharedSecretLength() > KemAlgorithm.X448.publicKeyLength());
		assertEquals(KemAlgorithm.X25519.publicKeyLength(), KemAlgorithm.X25519.sharedSecretLength());
	}
	
	@Test
	void constructWithEmptyName() {
		DhKemAlgorithm algorithm = assertDoesNotThrow(() -> new DhKemAlgorithm("", 1, 1));
		assertEquals("", algorithm.name());
		assertEquals("", algorithm.keyJcaName());
		assertEquals("DHKEM", algorithm.jcaName());
	}
	
	@Test
	void equalsAndHashCodeOverAllComponents() {
		DhKemAlgorithm algorithm = new DhKemAlgorithm("name", 11, 22);
		assertEquals(algorithm, new DhKemAlgorithm("name", 11, 22));
		assertEquals(algorithm.hashCode(), new DhKemAlgorithm("name", 11, 22).hashCode());
		assertNotEquals(algorithm, new DhKemAlgorithm("other", 11, 22));
		assertNotEquals(algorithm, new DhKemAlgorithm("name", 99, 22));
		assertNotEquals(algorithm, new DhKemAlgorithm("name", 11, 99));
	}
	
	@Test
	void toStringContainsComponents() {
		String string = new DhKemAlgorithm("name", 11, 22).toString();
		assertTrue(string.contains("DhKemAlgorithm"));
		assertTrue(string.contains("name"));
		assertTrue(string.contains("11"));
		assertTrue(string.contains("22"));
	}
	
	@Test
	void isKemAlgorithm() {
		assertInstanceOf(KemAlgorithm.class, KemAlgorithm.X25519);
	}
	
	@Test
	void jcaNamesResolveAgainstProviders() {
		for (DhKemAlgorithm algorithm : new DhKemAlgorithm[] { KemAlgorithm.X25519, KemAlgorithm.X448 }) {
			assertDoesNotThrow(() -> KeyPairGenerator.getInstance(algorithm.keyJcaName()));
			assertDoesNotThrow(() -> KEM.getInstance(algorithm.jcaName()));
			assertTrue(Providers.supports(algorithm));
		}
	}
	
	@Test
	void publicKeyLengthMatchesTheEncapsulation() throws Exception {
		for (DhKemAlgorithm algorithm : new DhKemAlgorithm[] { KemAlgorithm.X25519, KemAlgorithm.X448 }) {
			KeyPair pair = KeyPairGenerator.getInstance(algorithm.keyJcaName()).generateKeyPair();
			KEM.Encapsulator encapsulator = KEM.getInstance(algorithm.jcaName()).newEncapsulator(pair.getPublic());
			assertEquals(algorithm.publicKeyLength(), encapsulator.encapsulationSize());
			assertEquals(algorithm.publicKeyLength(), encapsulator.encapsulate().encapsulation().length);
		}
	}
	
	@Test
	void sharedSecretLengthMatchesTheProvider() throws Exception {
		for (DhKemAlgorithm algorithm : new DhKemAlgorithm[] { KemAlgorithm.X25519, KemAlgorithm.X448 }) {
			KeyPair pair = KeyPairGenerator.getInstance(algorithm.keyJcaName()).generateKeyPair();
			KEM.Encapsulator encapsulator = KEM.getInstance(algorithm.jcaName()).newEncapsulator(pair.getPublic());
			assertEquals(algorithm.sharedSecretLength(), encapsulator.secretSize());
			assertEquals(algorithm.sharedSecretLength(), encapsulator.encapsulate().key().getEncoded().length);
		}
	}
	
	@Test
	void bothConstantsRoundTripThroughDhkem() throws Exception {
		for (DhKemAlgorithm algorithm : new DhKemAlgorithm[] { KemAlgorithm.X25519, KemAlgorithm.X448 }) {
			KeyPair pair = KeyPairGenerator.getInstance(algorithm.keyJcaName()).generateKeyPair();
			KEM kem = KEM.getInstance(algorithm.jcaName());
			KEM.Encapsulated encapsulated = kem.newEncapsulator(pair.getPublic()).encapsulate();
			byte[] recovered = kem.newDecapsulator(pair.getPrivate()).decapsulate(encapsulated.encapsulation()).getEncoded();
			assertArrayEquals(encapsulated.key().getEncoded(), recovered);
		}
	}
}
