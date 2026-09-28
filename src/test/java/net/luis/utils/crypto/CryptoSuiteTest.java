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
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.exception.UnsupportedAlgorithmException;
import net.luis.utils.crypto.util.CryptoBytes;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CryptoSuite}.<br>
 *
 * @author Luis-St
 */
class CryptoSuiteTest {
	
	private static final NativeKemAlgorithm FAKE_KEM = new NativeKemAlgorithm("fake", "NoSuchKem", "NoSuchKey", 1, 1, 1);
	private static final NativeSignatureAlgorithm FAKE_SIGNATURE = new NativeSignatureAlgorithm("fake", "NoSuchSig", "NoSuchKey", null, 1, false, false);
	
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	
	private static CryptoSuite suite(KemAlgorithm kem, SignatureAlgorithm signature) {
		return new CryptoSuite((short) 99, "test", AeadAlgorithm.AES_256_GCM, kem, signature, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false);
	}
	
	@Test
	void constructCryptoSuite() {
		CryptoSuite suite = new CryptoSuite((short) 99, "test", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false);
		
		assertEquals((short) 99, suite.id());
		assertEquals("test", suite.name());
		assertSame(AeadAlgorithm.AES_256_GCM, suite.aead());
		assertSame(KemAlgorithm.X25519, suite.kem());
		assertSame(SignatureAlgorithm.ED25519, suite.signature());
		assertSame(KdfAlgorithm.HKDF_SHA_256, suite.kdf());
		assertSame(HashAlgorithm.SHA_256, suite.hash());
		assertFalse(suite.isDeprecated());
	}
	
	@Test
	void constructWithNullName() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CryptoSuite((short) 99, null, AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false));
		assertEquals("Name must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullAead() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CryptoSuite((short) 99, "test", null, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false));
		assertEquals("Aead algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullKem() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CryptoSuite((short) 99, "test", AeadAlgorithm.AES_256_GCM, null, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false));
		assertEquals("Kem algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullSignature() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CryptoSuite((short) 99, "test", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, null, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false));
		assertEquals("Signature algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullKdf() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CryptoSuite((short) 99, "test", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, null, HashAlgorithm.SHA_256, false));
		assertEquals("Kdf algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNullHash() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CryptoSuite((short) 99, "test", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, null, false));
		assertEquals("Hash algorithm must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithAllReferencesNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new CryptoSuite((short) 99, null, null, null, null, null, null, false));
		assertEquals("Name must not be null", exception.getMessage());
	}
	
	@Test
	void byIdWithUnknownId() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoSuite.byId((short) 999));
		assertTrue(exception.getMessage().contains("999"));
		assertTrue(exception.getMessage().contains("newer version"));
	}
	
	@Test
	void byIdWithZeroId() {
		assertThrows(MalformedDataException.class, () -> CryptoSuite.byId((short) 0));
	}
	
	@Test
	void byIdWithNegativeId() {
		assertThrows(MalformedDataException.class, () -> CryptoSuite.byId((short) -1));
		assertThrows(MalformedDataException.class, () -> CryptoSuite.byId(Short.MIN_VALUE));
	}
	
	@Test
	void byIdWithMaximumId() {
		assertThrows(MalformedDataException.class, () -> CryptoSuite.byId(Short.MAX_VALUE));
	}
	
	@Test
	void byNameWithNullName() {
		assertThrows(NullPointerException.class, () -> CryptoSuite.byName(null));
	}
	
	@Test
	void valuesIsUnmodifiable() {
		Collection<CryptoSuite> values = CryptoSuite.values();
		
		assertThrows(UnsupportedOperationException.class, () -> values.add(CryptoSuite.HYBRID_V1));
		assertThrows(UnsupportedOperationException.class, () -> values.remove(CryptoSuite.HYBRID_V1));
		assertThrows(UnsupportedOperationException.class, values::clear);
	}
	
	@Test
	void registerIsNotPubliclyReachable() throws Exception {
		Method register = CryptoSuite.class.getDeclaredMethod("register", CryptoSuite.class);
		assertTrue(Modifier.isPrivate(register.getModifiers()));
		assertTrue(Modifier.isStatic(register.getModifiers()));
		
		CryptoSuite duplicate = assertDoesNotThrow(() -> new CryptoSuite((short) 2, "duplicate", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false));
		assertFalse(CryptoSuite.values().contains(duplicate));
		assertSame(CryptoSuite.HYBRID_V1, CryptoSuite.byId((short) 2));
	}
	
	@Test
	void byIdFindsEveryRegisteredSuite() {
		assertSame(CryptoSuite.CLASSICAL_V1, CryptoSuite.byId((short) 1));
		assertSame(CryptoSuite.HYBRID_V1, CryptoSuite.byId((short) 2));
		assertSame(CryptoSuite.POST_QUANTUM_V1, CryptoSuite.byId((short) 3));
	}
	
	@Test
	void byNameFindsExistingSuite() {
		assertSame(CryptoSuite.HYBRID_V1, CryptoSuite.byName("hybrid-v1").orElseThrow());
	}
	
	@Test
	void byNameWithUnknownName() {
		assertTrue(CryptoSuite.byName("no-such-suite").isEmpty());
	}
	
	@Test
	void byNameWithEmptyName() {
		assertTrue(assertDoesNotThrow(() -> CryptoSuite.byName("")).isEmpty());
	}
	
	@Test
	void byNameIsCaseSensitive() {
		assertTrue(CryptoSuite.byName("HYBRID-V1").isEmpty());
	}
	
	@Test
	void byNameResolvesEverySuite() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assertSame(suite, CryptoSuite.byName(suite.name()).orElseThrow());
		}
	}
	
	@Test
	void isDeprecatedBothValues() {
		assertTrue(CryptoSuite.CLASSICAL_V1.isDeprecated());
		assertFalse(CryptoSuite.HYBRID_V1.isDeprecated());
		assertFalse(CryptoSuite.POST_QUANTUM_V1.isDeprecated());
	}
	
	@Test
	void isSupportedWithFullySupportedSuite() {
		assertTrue(CryptoSuite.CLASSICAL_V1.isSupported());
	}
	
	@Test
	void isSupportedWithUnsupportedKem() {
		assertFalse(suite(FAKE_KEM, SignatureAlgorithm.ED25519).isSupported());
	}
	
	@Test
	void isSupportedWithUnsupportedSignature() {
		CryptoSuite unsupported = suite(KemAlgorithm.X25519, FAKE_SIGNATURE);
		
		assertTrue(Providers.supports(unsupported.aead()));
		assertTrue(Providers.supports(unsupported.kem()));
		assertFalse(unsupported.isSupported());
	}
	
	@Test
	void isSupportedMatchesOperandConjunction() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			boolean expected = Providers.supports(suite.aead()) && Providers.supports(suite.kem()) && Providers.supports(suite.signature()) && Providers.supports(suite.kdf()) && Providers.supports(suite.hash());
			assertEquals(expected, suite.isSupported(), suite.name());
		}
	}
	
	@Test
	void currentIsHybridV1() {
		assertSame(CryptoSuite.HYBRID_V1, CryptoSuite.current());
	}
	
	@Test
	void currentIsNotDeprecated() {
		assertFalse(CryptoSuite.current().isDeprecated());
	}
	
	@Test
	void valuesContainsEverySuite() {
		Collection<CryptoSuite> values = CryptoSuite.values();
		
		assertEquals(3, values.size());
		assertTrue(values.contains(CryptoSuite.CLASSICAL_V1));
		assertTrue(values.contains(CryptoSuite.HYBRID_V1));
		assertTrue(values.contains(CryptoSuite.POST_QUANTUM_V1));
	}
	
	@Test
	void valuesIsInRegistrationOrder() {
		assertIterableEquals(List.of(CryptoSuite.CLASSICAL_V1, CryptoSuite.HYBRID_V1, CryptoSuite.POST_QUANTUM_V1), List.copyOf(CryptoSuite.values()));
	}
	
	@Test
	void idOfEverySuite() {
		assertEquals((short) 1, CryptoSuite.CLASSICAL_V1.id());
		assertEquals((short) 2, CryptoSuite.HYBRID_V1.id());
		assertEquals((short) 3, CryptoSuite.POST_QUANTUM_V1.id());
		assertEquals(3, CryptoSuite.values().stream().map(CryptoSuite::id).distinct().count());
	}
	
	@Test
	void nameOfEverySuite() {
		assertEquals("classical-v1", CryptoSuite.CLASSICAL_V1.name());
		assertEquals("hybrid-v1", CryptoSuite.HYBRID_V1.name());
		assertEquals("post-quantum-v1", CryptoSuite.POST_QUANTUM_V1.name());
		assertTrue(CryptoSuite.values().stream().noneMatch(suite -> suite.name().isBlank()));
	}
	
	@Test
	void primitivesOfClassicalV1() {
		CryptoSuite suite = CryptoSuite.CLASSICAL_V1;
		
		assertSame(AeadAlgorithm.AES_256_GCM, suite.aead());
		assertSame(KemAlgorithm.X25519, suite.kem());
		assertSame(SignatureAlgorithm.ED25519, suite.signature());
		assertSame(KdfAlgorithm.HKDF_SHA_256, suite.kdf());
		assertSame(HashAlgorithm.SHA_256, suite.hash());
		assertTrue(suite.deprecated());
	}
	
	@Test
	void primitivesOfHybridV1() {
		CryptoSuite suite = CryptoSuite.HYBRID_V1;
		
		assertSame(AeadAlgorithm.AES_256_GCM, suite.aead());
		assertSame(KemAlgorithm.X25519_ML_KEM_768, suite.kem());
		assertSame(SignatureAlgorithm.ED25519_ML_DSA_65, suite.signature());
		assertSame(KdfAlgorithm.HKDF_SHA_256, suite.kdf());
		assertSame(HashAlgorithm.SHA_256, suite.hash());
		assertFalse(suite.deprecated());
	}
	
	@Test
	void primitivesOfPostQuantumV1() {
		CryptoSuite suite = CryptoSuite.POST_QUANTUM_V1;
		
		assertSame(AeadAlgorithm.AES_256_GCM, suite.aead());
		assertSame(KemAlgorithm.ML_KEM_768, suite.kem());
		assertSame(SignatureAlgorithm.ML_DSA_65, suite.signature());
		assertSame(KdfAlgorithm.HKDF_SHA_256, suite.kdf());
		assertSame(HashAlgorithm.SHA_256, suite.hash());
		assertFalse(suite.deprecated());
	}
	
	@Test
	void isDeprecatedMatchesComponent() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assertEquals(suite.deprecated(), suite.isDeprecated());
		}
		assertTrue(new CryptoSuite((short) 99, "test", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, true).isDeprecated());
		assertFalse(suite(KemAlgorithm.X25519, SignatureAlgorithm.ED25519).isDeprecated());
	}
	
	@Test
	void toStringFormat() {
		assertEquals("hybrid-v1(2)", CryptoSuite.HYBRID_V1.toString());
		assertFalse(CryptoSuite.HYBRID_V1.toString().contains("CryptoSuite["));
	}
	
	@Test
	void toStringForEverySuite() {
		assertEquals("classical-v1(1)", CryptoSuite.CLASSICAL_V1.toString());
		assertEquals("hybrid-v1(2)", CryptoSuite.HYBRID_V1.toString());
		assertEquals("post-quantum-v1(3)", CryptoSuite.POST_QUANTUM_V1.toString());
	}
	
	@Test
	void constructWithEmptyName() {
		CryptoSuite suite = assertDoesNotThrow(() -> new CryptoSuite((short) 99, "", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false));
		assertEquals("(99)", suite.toString());
	}
	
	@Test
	void idsAreUniqueAcrossValues() {
		Set<Short> ids = new HashSet<>();
		CryptoSuite.values().forEach(suite -> ids.add(suite.id()));
		
		assertEquals(3, ids.size());
		assertEquals(CryptoSuite.values().size(), ids.size());
	}
	
	@Test
	void namesAreUniqueAcrossValues() {
		Set<String> names = new HashSet<>();
		CryptoSuite.values().forEach(suite -> names.add(suite.name()));
		
		assertEquals(3, names.size());
		assertTrue(names.stream().noneMatch(String::isBlank));
	}
	
	@Test
	void byIdRoundTripsThroughId() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assertSame(suite, CryptoSuite.byId(suite.id()));
		}
	}
	
	@Test
	void byIdAndByNameAgree() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assertSame(CryptoSuite.byId(suite.id()), CryptoSuite.byName(suite.name()).orElseThrow());
		}
	}
	
	@Test
	void equalsAndHashCodeOverAllComponents() {
		CryptoSuite first = suite(KemAlgorithm.X25519, SignatureAlgorithm.ED25519);
		CryptoSuite second = suite(KemAlgorithm.X25519, SignatureAlgorithm.ED25519);
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new CryptoSuite((short) 98, "test", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false));
		assertNotEquals(first, new CryptoSuite((short) 99, "other", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false));
		assertNotEquals(first, new CryptoSuite((short) 99, "test", AeadAlgorithm.CHACHA20_POLY1305, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, false));
		assertNotEquals(first, suite(KemAlgorithm.X448, SignatureAlgorithm.ED25519));
		assertNotEquals(first, suite(KemAlgorithm.X25519, SignatureAlgorithm.ED448));
		assertNotEquals(first, new CryptoSuite((short) 99, "test", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_512, HashAlgorithm.SHA_256, false));
		assertNotEquals(first, new CryptoSuite((short) 99, "test", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_512, false));
		assertNotEquals(first, new CryptoSuite((short) 99, "test", AeadAlgorithm.AES_256_GCM, KemAlgorithm.X25519, SignatureAlgorithm.ED25519, KdfAlgorithm.HKDF_SHA_256, HashAlgorithm.SHA_256, true));
	}
	
	@Test
	void handBuiltSuiteEqualsRegisteredConstant() {
		CryptoSuite rebuilt = new CryptoSuite(CryptoSuite.HYBRID_V1.id(), CryptoSuite.HYBRID_V1.name(), CryptoSuite.HYBRID_V1.aead(), CryptoSuite.HYBRID_V1.kem(), CryptoSuite.HYBRID_V1.signature(), CryptoSuite.HYBRID_V1.kdf(), CryptoSuite.HYBRID_V1.hash(), CryptoSuite.HYBRID_V1.deprecated());
		
		assertEquals(CryptoSuite.HYBRID_V1, rebuilt);
		assertNotSame(CryptoSuite.HYBRID_V1, rebuilt);
		assertTrue(CryptoSuite.values().contains(rebuilt));
	}
	
	@Test
	void migrationPathIsOrdered() {
		assertFalse(CryptoSuite.CLASSICAL_V1.kem().isPostQuantum());
		assertInstanceOf(HybridKemAlgorithm.class, CryptoSuite.HYBRID_V1.kem());
		assertTrue(CryptoSuite.HYBRID_V1.kem().isPostQuantum());
		assertInstanceOf(NativeKemAlgorithm.class, CryptoSuite.POST_QUANTUM_V1.kem());
		assertTrue(CryptoSuite.POST_QUANTUM_V1.kem().isPostQuantum());
		
		assertFalse(CryptoSuite.CLASSICAL_V1.signature().isPostQuantum());
		assertInstanceOf(HybridSignatureAlgorithm.class, CryptoSuite.HYBRID_V1.signature());
		assertTrue(CryptoSuite.POST_QUANTUM_V1.signature().isPostQuantum());
	}
	
	@Test
	void everySuiteSharesAeadKdfAndHash() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			assertSame(AeadAlgorithm.AES_256_GCM, suite.aead());
			assertSame(KdfAlgorithm.HKDF_SHA_256, suite.kdf());
			assertSame(HashAlgorithm.SHA_256, suite.hash());
		}
	}
	
	@Test
	void currentSuiteIsSupportedOnThisRuntime() {
		assertTrue(CryptoSuite.current().isSupported());
	}
	
	@Test
	void isSupportedAgreesWithProvidersRequire() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			if (suite.isSupported()) {
				assertDoesNotThrow(() -> Providers.require(suite));
			} else {
				assertThrows(UnsupportedAlgorithmException.class, () -> Providers.require(suite));
			}
		}
	}
	
	@Test
	void valuesIsALiveView() {
		Collection<CryptoSuite> first = CryptoSuite.values();
		Collection<CryptoSuite> second = CryptoSuite.values();
		
		assertNotSame(first, second);
		assertIterableEquals(List.copyOf(first), List.copyOf(second));
	}
	
	@Test
	void suiteIdFitsInTwoBytes() {
		for (CryptoSuite suite : CryptoSuite.values()) {
			byte[] encoded = CryptoBytes.of(suite.id());
			assertEquals(2, encoded.length);
			assertEquals(suite.id(), ByteBuffer.wrap(encoded).getShort());
		}
	}
}
