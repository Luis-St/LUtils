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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyPairGenerator;
import java.security.Signature;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link SignatureAlgorithm}.<br>
 *
 * @author Luis-St
 */
class SignatureAlgorithmTest {
	
	private static final List<NativeSignatureAlgorithm> COMPOSITES = List.of(
		SignatureAlgorithm.COMPOSITE_ML_DSA_44_ECDSA_P256, SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519,
		SignatureAlgorithm.COMPOSITE_ML_DSA_65_ECDSA_P384, SignatureAlgorithm.COMPOSITE_ML_DSA_87_ED448,
		SignatureAlgorithm.COMPOSITE_ML_DSA_87_ECDSA_P521);
	private static final List<HybridSignatureAlgorithm> HYBRIDS = List.of(
		SignatureAlgorithm.ED25519_ML_DSA_65, SignatureAlgorithm.ECDSA_P256_ML_DSA_44, SignatureAlgorithm.ECDSA_P384_ML_DSA_65,
		SignatureAlgorithm.ED448_ML_DSA_87, SignatureAlgorithm.ECDSA_P521_ML_DSA_87, SignatureAlgorithm.ED25519_SLH_DSA_SHA2_128S);
	
	//region Setup
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	//endregion
	
	@Test
	void byNameWithNullName() {
		assertThrows(NullPointerException.class, () -> SignatureAlgorithm.byName(null));
	}
	
	@Test
	void valuesIsImmutable() {
		assertThrows(UnsupportedOperationException.class, () -> SignatureAlgorithm.VALUES.add(SignatureAlgorithm.ED25519));
		assertThrows(UnsupportedOperationException.class, () -> SignatureAlgorithm.VALUES.remove(0));
		assertThrows(UnsupportedOperationException.class, () -> SignatureAlgorithm.VALUES.set(0, SignatureAlgorithm.ED25519));
	}
	
	@Test
	void byNameFindsExistingScheme() {
		assertSame(SignatureAlgorithm.ML_DSA_65, SignatureAlgorithm.byName("ML-DSA-65").orElseThrow());
	}
	
	@Test
	void byNameWithUnknownName() {
		assertTrue(SignatureAlgorithm.byName("RSA-PSS").isEmpty());
	}
	
	@Test
	void byNameWithEmptyName() {
		assertTrue(assertDoesNotThrow(() -> SignatureAlgorithm.byName("")).isEmpty());
	}
	
	@Test
	void byNameIsCaseSensitive() {
		assertTrue(SignatureAlgorithm.byName("ed25519").isEmpty());
	}
	
	@Test
	void byNameResolvesEveryConstant() {
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			assertSame(algorithm, SignatureAlgorithm.byName(algorithm.name()).orElseThrow());
		}
	}
	
	@Test
	void byNameFindsHybridScheme() {
		assertSame(SignatureAlgorithm.ED25519_ML_DSA_65, SignatureAlgorithm.byName("Ed25519+ML-DSA-65").orElseThrow());
	}
	
	@Test
	void byNameFindsCompositeScheme() {
		assertSame(SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519, SignatureAlgorithm.byName("MLDSA65-Ed25519-SHA512").orElseThrow());
	}
	
	@Test
	void byNameDistinguishesHybridFromComposite() {
		SignatureAlgorithm hybrid = SignatureAlgorithm.byName("Ed25519+ML-DSA-65").orElseThrow();
		SignatureAlgorithm composite = SignatureAlgorithm.byName("MLDSA65-Ed25519-SHA512").orElseThrow();
		assertNotSame(hybrid, composite);
		assertInstanceOf(HybridSignatureAlgorithm.class, hybrid);
		assertInstanceOf(NativeSignatureAlgorithm.class, composite);
	}
	
	@Test
	void isPostQuantumBothValues() {
		for (SignatureAlgorithm algorithm : new SignatureAlgorithm[] {
			SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448,
			SignatureAlgorithm.ECDSA_P256_SHA_256, SignatureAlgorithm.ECDSA_P384_SHA_384, SignatureAlgorithm.ECDSA_P521_SHA_512
		}) {
			assertFalse(algorithm.isPostQuantum());
		}
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (!algorithm.name().startsWith("Ed") && !algorithm.name().startsWith("ECDSA")) {
				assertTrue(algorithm.isPostQuantum());
			}
		}
		HYBRIDS.forEach(hybrid -> assertTrue(hybrid.isPostQuantum()));
		COMPOSITES.forEach(composite -> assertTrue(composite.isPostQuantum()));
	}
	
	@Test
	void requiresBouncyCastleBothValues() {
		assertFalse(SignatureAlgorithm.ED25519.requiresBouncyCastle());
		assertFalse(SignatureAlgorithm.ML_DSA_44.requiresBouncyCastle());
		assertFalse(SignatureAlgorithm.ML_DSA_65.requiresBouncyCastle());
		assertFalse(SignatureAlgorithm.ML_DSA_87.requiresBouncyCastle());
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (algorithm.name().startsWith("SLH-DSA")) {
				assertTrue(algorithm.requiresBouncyCastle());
			}
		}
		COMPOSITES.forEach(composite -> assertTrue(composite.requiresBouncyCastle()));
		assertFalse(SignatureAlgorithm.ED25519_ML_DSA_65.requiresBouncyCastle());
		assertTrue(SignatureAlgorithm.ED25519_SLH_DSA_SHA2_128S.requiresBouncyCastle());
	}
	
	@Test
	void requiresBouncyCastleTrueThroughHybridComponent() {
		HybridSignatureAlgorithm hybrid = SignatureAlgorithm.ED25519_SLH_DSA_SHA2_128S;
		assertTrue(hybrid.requiresBouncyCastle());
		assertFalse(hybrid.classical().requiresBouncyCastle());
		assertTrue(hybrid.postQuantum().requiresBouncyCastle());
	}
	
	@Test
	void valuesContainsEveryConstant() {
		assertEquals(31, SignatureAlgorithm.VALUES.size());
		assertTrue(SignatureAlgorithm.VALUES.contains(SignatureAlgorithm.ED25519));
		COMPOSITES.forEach(composite -> assertTrue(SignatureAlgorithm.VALUES.contains(composite)));
		HYBRIDS.forEach(hybrid -> assertTrue(SignatureAlgorithm.VALUES.contains(hybrid)));
	}
	
	@Test
	void valuesOrderIsStable() {
		assertSame(SignatureAlgorithm.ED25519, SignatureAlgorithm.VALUES.get(0));
		assertSame(SignatureAlgorithm.ECDSA_P521_SHA_512, SignatureAlgorithm.VALUES.get(4));
		assertSame(SignatureAlgorithm.ML_DSA_44, SignatureAlgorithm.VALUES.get(5));
		assertSame(SignatureAlgorithm.SLH_DSA_SHA2_128S, SignatureAlgorithm.VALUES.get(8));
		assertSame(SignatureAlgorithm.SLH_DSA_SHA2_128F, SignatureAlgorithm.VALUES.get(14));
		assertSame(SignatureAlgorithm.ED25519_ML_DSA_65, SignatureAlgorithm.VALUES.get(20));
		assertSame(SignatureAlgorithm.ECDSA_P521_ML_DSA_87, SignatureAlgorithm.VALUES.get(24));
		assertSame(SignatureAlgorithm.ED25519_SLH_DSA_SHA2_128S, SignatureAlgorithm.VALUES.get(25));
		assertSame(SignatureAlgorithm.COMPOSITE_ML_DSA_44_ECDSA_P256, SignatureAlgorithm.VALUES.get(26));
		assertSame(SignatureAlgorithm.COMPOSITE_ML_DSA_87_ECDSA_P521, SignatureAlgorithm.VALUES.get(30));
	}
	
	@Test
	void nameOfClassicalConstants() {
		assertEquals("Ed25519", SignatureAlgorithm.ED25519.name());
		assertEquals("Ed448", SignatureAlgorithm.ED448.name());
		assertEquals("ECDSA-P256-SHA256", SignatureAlgorithm.ECDSA_P256_SHA_256.name());
		assertEquals("ECDSA-P384-SHA384", SignatureAlgorithm.ECDSA_P384_SHA_384.name());
		assertEquals("ECDSA-P521-SHA512", SignatureAlgorithm.ECDSA_P521_SHA_512.name());
	}
	
	@Test
	void nameOfPostQuantumConstants() {
		assertEquals("ML-DSA-44", SignatureAlgorithm.ML_DSA_44.name());
		assertEquals("ML-DSA-65", SignatureAlgorithm.ML_DSA_65.name());
		assertEquals("ML-DSA-87", SignatureAlgorithm.ML_DSA_87.name());
		assertEquals("SLH-DSA-SHA2-128S", SignatureAlgorithm.SLH_DSA_SHA2_128S.name());
		assertEquals("SLH-DSA-SHAKE-256F", SignatureAlgorithm.SLH_DSA_SHAKE_256F.name());
	}
	
	@Test
	void nameOfHybridConstants() {
		assertEquals("Ed25519+ML-DSA-65", SignatureAlgorithm.ED25519_ML_DSA_65.name());
		assertEquals("ECDSA-P256-SHA256+ML-DSA-44", SignatureAlgorithm.ECDSA_P256_ML_DSA_44.name());
		assertEquals("ECDSA-P384-SHA384+ML-DSA-65", SignatureAlgorithm.ECDSA_P384_ML_DSA_65.name());
		assertEquals("Ed448+ML-DSA-87", SignatureAlgorithm.ED448_ML_DSA_87.name());
		assertEquals("ECDSA-P521-SHA512+ML-DSA-87", SignatureAlgorithm.ECDSA_P521_ML_DSA_87.name());
		assertEquals("Ed25519+SLH-DSA-SHA2-128S", SignatureAlgorithm.ED25519_SLH_DSA_SHA2_128S.name());
	}
	
	@Test
	void nameOfCompositeConstants() {
		assertEquals("MLDSA44-ECDSA-P256-SHA256", SignatureAlgorithm.COMPOSITE_ML_DSA_44_ECDSA_P256.name());
		assertEquals("MLDSA65-Ed25519-SHA512", SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519.name());
		assertEquals("MLDSA65-ECDSA-P384-SHA512", SignatureAlgorithm.COMPOSITE_ML_DSA_65_ECDSA_P384.name());
		assertEquals("MLDSA87-Ed448-SHAKE256", SignatureAlgorithm.COMPOSITE_ML_DSA_87_ED448.name());
		assertEquals("MLDSA87-ECDSA-P521-SHA512", SignatureAlgorithm.COMPOSITE_ML_DSA_87_ECDSA_P521.name());
		for (NativeSignatureAlgorithm composite : COMPOSITES) {
			assertEquals(composite.name(), composite.jcaName());
			assertEquals(composite.name(), composite.keyJcaName());
			assertFalse(composite.name().contains("+"));
		}
	}
	
	@Test
	void publicKeyLengthOfNativeConstants() {
		assertEquals(44, SignatureAlgorithm.ED25519.publicKeyLength());
		assertEquals(69, SignatureAlgorithm.ED448.publicKeyLength());
		assertEquals(91, SignatureAlgorithm.ECDSA_P256_SHA_256.publicKeyLength());
		assertEquals(120, SignatureAlgorithm.ECDSA_P384_SHA_384.publicKeyLength());
		assertEquals(158, SignatureAlgorithm.ECDSA_P521_SHA_512.publicKeyLength());
		assertEquals(1334, SignatureAlgorithm.ML_DSA_44.publicKeyLength());
		assertEquals(1974, SignatureAlgorithm.ML_DSA_65.publicKeyLength());
		assertEquals(2614, SignatureAlgorithm.ML_DSA_87.publicKeyLength());
		assertEquals(50, SignatureAlgorithm.SLH_DSA_SHA2_128S.publicKeyLength());
		assertEquals(66, SignatureAlgorithm.SLH_DSA_SHA2_192S.publicKeyLength());
		assertEquals(82, SignatureAlgorithm.SLH_DSA_SHA2_256S.publicKeyLength());
	}
	
	@Test
	void publicKeyLengthOfCompositeConstants() {
		assertEquals(1398, SignatureAlgorithm.COMPOSITE_ML_DSA_44_ECDSA_P256.publicKeyLength());
		assertEquals(2005, SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519.publicKeyLength());
		assertEquals(2070, SignatureAlgorithm.COMPOSITE_ML_DSA_65_ECDSA_P384.publicKeyLength());
		assertEquals(2670, SignatureAlgorithm.COMPOSITE_ML_DSA_87_ED448.publicKeyLength());
		assertEquals(2746, SignatureAlgorithm.COMPOSITE_ML_DSA_87_ECDSA_P521.publicKeyLength());
	}
	
	@Test
	void allPublicKeyLengthsArePositive() {
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			assertTrue(algorithm.publicKeyLength() > 0);
		}
	}
	
	@Test
	void namesAreUniqueAcrossValues() {
		Set<String> names = new HashSet<>();
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			assertFalse(algorithm.name().isBlank());
			assertTrue(names.add(algorithm.name()));
		}
		assertEquals(31, names.size());
	}
	
	@Test
	void hybridPublicKeyLengthsDeriveFromComponents() {
		assertEquals(2018, SignatureAlgorithm.ED25519_ML_DSA_65.publicKeyLength());
		assertEquals(1425, SignatureAlgorithm.ECDSA_P256_ML_DSA_44.publicKeyLength());
		assertEquals(2683, SignatureAlgorithm.ED448_ML_DSA_87.publicKeyLength());
		assertEquals(2772, SignatureAlgorithm.ECDSA_P521_ML_DSA_87.publicKeyLength());
		for (HybridSignatureAlgorithm hybrid : HYBRIDS) {
			assertEquals(hybrid.classical().publicKeyLength() + hybrid.postQuantum().publicKeyLength(), hybrid.publicKeyLength());
		}
	}
	
	@Test
	void hybridConstantsPairClassicalWithPostQuantum() {
		for (HybridSignatureAlgorithm hybrid : HYBRIDS) {
			assertFalse(hybrid.classical().isPostQuantum());
			assertTrue(hybrid.postQuantum().isPostQuantum());
			assertTrue(hybrid.isPostQuantum());
		}
	}
	
	@Test
	void hybridFamilyCoversBothPostQuantumFamilies() {
		assertTrue(HYBRIDS.stream().anyMatch(hybrid -> hybrid.postQuantum().name().startsWith("ML-DSA")));
		assertTrue(HYBRIDS.stream().anyMatch(hybrid -> hybrid.postQuantum().name().startsWith("SLH-DSA")));
	}
	
	@Test
	void slhDsaHybridPublicKeyLength() {
		assertEquals(94, SignatureAlgorithm.ED25519_SLH_DSA_SHA2_128S.publicKeyLength());
		assertEquals(44 + 50, SignatureAlgorithm.ED25519_SLH_DSA_SHA2_128S.publicKeyLength());
	}
	
	@Test
	void everyMlDsaHybridHasACompositeCounterpart() {
		assertEquals(SignatureAlgorithm.ED25519_ML_DSA_65.postQuantum().name(), "ML-DSA-65");
		assertTrue(SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519.name().contains("MLDSA65"));
		assertTrue(SignatureAlgorithm.COMPOSITE_ML_DSA_65_ED25519.name().contains("Ed25519"));
		assertTrue(SignatureAlgorithm.COMPOSITE_ML_DSA_44_ECDSA_P256.name().contains("MLDSA44"));
		assertTrue(SignatureAlgorithm.COMPOSITE_ML_DSA_44_ECDSA_P256.name().contains("ECDSA-P256"));
		assertTrue(SignatureAlgorithm.COMPOSITE_ML_DSA_87_ED448.name().contains("Ed448"));
		assertEquals(5, COMPOSITES.size());
	}
	
	@Test
	void compositesAreNativeNotHybrid() {
		for (SignatureAlgorithm composite : COMPOSITES) {
			NativeSignatureAlgorithm native0 = assertInstanceOf(NativeSignatureAlgorithm.class, composite);
			assertFalse(composite instanceof HybridSignatureAlgorithm);
			assertNull(native0.keySpec());
			assertEquals(native0.jcaName(), native0.keyJcaName());
		}
	}
	
	@Test
	void slhDsaSmallAndFastSetsShareKeyLengths() {
		assertEquals(SignatureAlgorithm.SLH_DSA_SHA2_128S.publicKeyLength(), SignatureAlgorithm.SLH_DSA_SHA2_128F.publicKeyLength());
		assertEquals(SignatureAlgorithm.SLH_DSA_SHA2_192S.publicKeyLength(), SignatureAlgorithm.SLH_DSA_SHA2_192F.publicKeyLength());
		assertEquals(SignatureAlgorithm.SLH_DSA_SHA2_256S.publicKeyLength(), SignatureAlgorithm.SLH_DSA_SHA2_256F.publicKeyLength());
		assertEquals(SignatureAlgorithm.SLH_DSA_SHAKE_128S.publicKeyLength(), SignatureAlgorithm.SLH_DSA_SHAKE_128F.publicKeyLength());
		assertEquals(50, SignatureAlgorithm.SLH_DSA_SHA2_128F.publicKeyLength());
	}
	
	@Test
	void sealedHierarchyPermitsTwoVariants() {
		assertTrue(SignatureAlgorithm.class.isSealed());
		assertEquals(Set.of(NativeSignatureAlgorithm.class, HybridSignatureAlgorithm.class),
			Set.of(SignatureAlgorithm.class.getPermittedSubclasses()));
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			assertTrue(algorithm instanceof NativeSignatureAlgorithm || algorithm instanceof HybridSignatureAlgorithm);
		}
	}
	
	@Test
	void deliberatelyAbsentSchemesAreNotResolvable() {
		assertTrue(SignatureAlgorithm.byName("RSA").isEmpty());
		assertTrue(SignatureAlgorithm.byName("DSA").isEmpty());
		assertTrue(SignatureAlgorithm.byName("SHA256withRSA").isEmpty());
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			assertFalse(algorithm.name().contains("RSA"));
			assertNotEquals("DSA", algorithm.name());
		}
	}
	
	@Test
	void compositeNamesResolveAgainstBouncyCastle() {
		for (NativeSignatureAlgorithm composite : COMPOSITES) {
			assumeTrue(Providers.supports(composite));
			assertDoesNotThrow(() -> Signature.getInstance(composite.jcaName()));
			assertDoesNotThrow(() -> KeyPairGenerator.getInstance(composite.keyJcaName()));
		}
	}
}
