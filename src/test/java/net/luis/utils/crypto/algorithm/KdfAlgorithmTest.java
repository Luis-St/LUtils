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

import javax.crypto.KDF;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KdfAlgorithm}.<br>
 *
 * @author Luis-St
 */
class KdfAlgorithmTest {
	
	@Test
	void accessorsNonNullForEveryConstant() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertNotNull(algorithm.jcaName());
			assertNotNull(algorithm.mac());
			assertNotNull(algorithm.hash());
			assertFalse(algorithm.jcaName().isBlank());
			assertTrue(algorithm.outputLength() > 0);
		}
	}
	
	@Test
	void jcaNamePerConstant() {
		assertEquals("HKDF-SHA256", KdfAlgorithm.HKDF_SHA_256.jcaName());
		assertEquals("HKDF-SHA384", KdfAlgorithm.HKDF_SHA_384.jcaName());
		assertEquals("HKDF-SHA512", KdfAlgorithm.HKDF_SHA_512.jcaName());
	}
	
	@Test
	void jcaNamesAreDistinct() {
		Set<String> names = new HashSet<>();
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertTrue(names.add(algorithm.jcaName()));
			assertFalse(algorithm.jcaName().isBlank());
		}
		assertEquals(3, names.size());
	}
	
	@Test
	void macPairingPerConstant() {
		assertSame(MacAlgorithm.HMAC_SHA_256, KdfAlgorithm.HKDF_SHA_256.mac());
		assertSame(MacAlgorithm.HMAC_SHA_384, KdfAlgorithm.HKDF_SHA_384.mac());
		assertSame(MacAlgorithm.HMAC_SHA_512, KdfAlgorithm.HKDF_SHA_512.mac());
	}
	
	@Test
	void hashPairingPerConstant() {
		assertSame(HashAlgorithm.SHA_256, KdfAlgorithm.HKDF_SHA_256.hash());
		assertSame(HashAlgorithm.SHA_384, KdfAlgorithm.HKDF_SHA_384.hash());
		assertSame(HashAlgorithm.SHA_512, KdfAlgorithm.HKDF_SHA_512.hash());
	}
	
	@Test
	void outputLengthPerConstant() {
		assertEquals(32, KdfAlgorithm.HKDF_SHA_256.outputLength());
		assertEquals(48, KdfAlgorithm.HKDF_SHA_384.outputLength());
		assertEquals(64, KdfAlgorithm.HKDF_SHA_512.outputLength());
	}
	
	@Test
	void outputLengthDelegatesToHash() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertEquals(algorithm.hash().digestLength(), algorithm.outputLength());
		}
	}
	
	@Test
	void macAndHashConsistency() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertEquals(algorithm.hash().digestLength(), algorithm.mac().tagLength());
			assertEquals(algorithm.outputLength(), algorithm.mac().tagLength());
		}
	}
	
	@Test
	void maximumExpansionLength() {
		assertEquals(8160, 255 * KdfAlgorithm.HKDF_SHA_256.outputLength());
		assertEquals(12240, 255 * KdfAlgorithm.HKDF_SHA_384.outputLength());
		assertEquals(16320, 255 * KdfAlgorithm.HKDF_SHA_512.outputLength());
	}
	
	@Test
	void constantsAreDistinctPairings() {
		Set<MacAlgorithm> macs = EnumSet.noneOf(MacAlgorithm.class);
		Set<HashAlgorithm> hashes = EnumSet.noneOf(HashAlgorithm.class);
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertTrue(macs.add(algorithm.mac()));
			assertTrue(hashes.add(algorithm.hash()));
		}
		assertEquals(3, macs.size());
		assertEquals(3, hashes.size());
	}
	
	@Test
	void jcaNameResolvesToAServedKdf() {
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertDoesNotThrow(() -> KDF.getInstance(algorithm.jcaName()));
			assertTrue(Providers.supports(algorithm));
		}
	}
	
	@Test
	void jcaNameAgreesWithTheDescribedConstruction() {
		assertEquals("SHA-256", KdfAlgorithm.HKDF_SHA_256.hash().jcaName());
		assertEquals("SHA-384", KdfAlgorithm.HKDF_SHA_384.hash().jcaName());
		assertEquals("SHA-512", KdfAlgorithm.HKDF_SHA_512.hash().jcaName());
		for (KdfAlgorithm algorithm : KdfAlgorithm.values()) {
			assertEquals(algorithm.hash().digestLength(), algorithm.outputLength());
		}
	}
}
