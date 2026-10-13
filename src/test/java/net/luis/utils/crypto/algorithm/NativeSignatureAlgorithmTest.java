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

import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link NativeSignatureAlgorithm}.<br>
 *
 * @author Luis-St
 */
class NativeSignatureAlgorithmTest {
	
	private static final byte[] MESSAGE = "message".getBytes();
	
	//region Setup
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	//endregion
	
	private static KeyPair keyPair(NativeSignatureAlgorithm algorithm) throws GeneralSecurityException {
		KeyPairGenerator generator = KeyPairGenerator.getInstance(algorithm.keyJcaName());
		if (algorithm.keySpec() != null) {
			generator.initialize(algorithm.keySpec());
		}
		return generator.generateKeyPair();
	}
	
	@Test
	void constructNativeSignatureAlgorithm() {
		NativeSignatureAlgorithm algorithm = new NativeSignatureAlgorithm("Ed25519", "Ed25519", "Ed25519", null, 44, false, false);
		assertEquals("Ed25519", algorithm.name());
		assertEquals("Ed25519", algorithm.jcaName());
		assertEquals("Ed25519", algorithm.keyJcaName());
		assertNull(algorithm.curve());
		assertEquals(44, algorithm.publicKeyLength());
		assertNull(algorithm.keySpec());
	}
	
	@Test
	void constructWithCurve() {
		NativeSignatureAlgorithm algorithm = new NativeSignatureAlgorithm("ECDSA-P256-SHA256", "SHA256withECDSA", "EC", "secp256r1", 91, false, false);
		assertEquals("secp256r1", algorithm.curve());
		assertNotNull(algorithm.keySpec());
	}
	
	@Test
	void constructWithNullName() {
		assertThrows(NullPointerException.class, () -> new NativeSignatureAlgorithm(null, "jca", "key", null, 1, false, false));
	}
	
	@Test
	void constructWithNullJcaName() {
		assertThrows(NullPointerException.class, () -> new NativeSignatureAlgorithm("name", null, "key", null, 1, false, false));
	}
	
	@Test
	void constructWithNullKeyJcaName() {
		assertThrows(NullPointerException.class, () -> new NativeSignatureAlgorithm("name", "jca", null, null, 1, false, false));
	}
	
	@Test
	void constructWithNullCurve() {
		NativeSignatureAlgorithm algorithm = assertDoesNotThrow(() -> new NativeSignatureAlgorithm("name", "jca", "key", null, 1, false, false));
		assertNull(algorithm.curve());
	}
	
	@Test
	void constructWithAllNullNames() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new NativeSignatureAlgorithm(null, null, null, null, 1, false, false));
		assertEquals("Name must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithNegativePublicKeyLength() {
		NativeSignatureAlgorithm algorithm = assertDoesNotThrow(() -> new NativeSignatureAlgorithm("x", "y", "z", null, -1, false, false));
		assertEquals(-1, algorithm.publicKeyLength());
	}
	
	@Test
	void keySpecWithUnknownCurveName() {
		NativeSignatureAlgorithm algorithm = new NativeSignatureAlgorithm("x", "SHA256withECDSA", "EC", "not-a-curve", 1, false, false);
		AlgorithmParameterSpec spec = assertDoesNotThrow(algorithm::keySpec);
		assertEquals("not-a-curve", assertInstanceOf(ECGenParameterSpec.class, spec).getName());
		assertThrows(InvalidAlgorithmParameterException.class, () -> KeyPairGenerator.getInstance("EC").initialize(spec));
	}
	
	@Test
	void keySpecWithoutCurve() {
		assertNull(SignatureAlgorithm.ED25519.keySpec());
	}
	
	@Test
	void keySpecWithCurve() {
		AlgorithmParameterSpec spec = SignatureAlgorithm.ECDSA_P256_SHA_256.keySpec();
		assertEquals("secp256r1", assertInstanceOf(ECGenParameterSpec.class, spec).getName());
	}
	
	@Test
	void keySpecNullForEveryNonCurveConstant() {
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (algorithm instanceof NativeSignatureAlgorithm native0 && native0.curve() == null) {
				assertNull(native0.keySpec());
			}
		}
		assertNull(SignatureAlgorithm.ED25519.keySpec());
		assertNull(SignatureAlgorithm.ED448.keySpec());
		assertNull(SignatureAlgorithm.ML_DSA_65.keySpec());
		assertNull(SignatureAlgorithm.SLH_DSA_SHA2_128S.keySpec());
	}
	
	@Test
	void keySpecPresentForEveryCurveConstant() {
		assertEquals("secp256r1", ((ECGenParameterSpec) SignatureAlgorithm.ECDSA_P256_SHA_256.keySpec()).getName());
		assertEquals("secp384r1", ((ECGenParameterSpec) SignatureAlgorithm.ECDSA_P384_SHA_384.keySpec()).getName());
		assertEquals("secp521r1", ((ECGenParameterSpec) SignatureAlgorithm.ECDSA_P521_SHA_512.keySpec()).getName());
	}
	
	@Test
	void isPostQuantumBothValues() {
		assertFalse(SignatureAlgorithm.ED25519.isPostQuantum());
		assertFalse(SignatureAlgorithm.ED448.isPostQuantum());
		assertFalse(SignatureAlgorithm.ECDSA_P256_SHA_256.isPostQuantum());
		assertTrue(SignatureAlgorithm.ML_DSA_65.isPostQuantum());
		assertTrue(SignatureAlgorithm.SLH_DSA_SHA2_128S.isPostQuantum());
		assertTrue(new NativeSignatureAlgorithm("x", "y", "z", null, 1, true, false).isPostQuantum());
		assertFalse(new NativeSignatureAlgorithm("x", "y", "z", null, 1, false, false).isPostQuantum());
	}
	
	@Test
	void requiresBouncyCastleBothValues() {
		assertFalse(SignatureAlgorithm.ED25519.requiresBouncyCastle());
		assertFalse(SignatureAlgorithm.ML_DSA_65.requiresBouncyCastle());
		assertTrue(SignatureAlgorithm.SLH_DSA_SHA2_128S.requiresBouncyCastle());
		assertTrue(new NativeSignatureAlgorithm("x", "y", "z", null, 1, false, true).requiresBouncyCastle());
		assertFalse(new NativeSignatureAlgorithm("x", "y", "z", null, 1, false, false).requiresBouncyCastle());
	}
	
	@Test
	void constructWithEmptyNames() {
		NativeSignatureAlgorithm algorithm = assertDoesNotThrow(() -> new NativeSignatureAlgorithm("", "", "", null, 1, false, false));
		assertEquals("", algorithm.name());
	}
	
	@Test
	void constructWithEmptyCurve() {
		NativeSignatureAlgorithm algorithm = new NativeSignatureAlgorithm("x", "y", "z", "", 1, false, false);
		assertNotNull(algorithm.keySpec());
		assertEquals("", ((ECGenParameterSpec) algorithm.keySpec()).getName());
	}
	
	@Test
	void constructWithZeroPublicKeyLength() {
		assertEquals(0, assertDoesNotThrow(() -> new NativeSignatureAlgorithm("x", "y", "z", null, 0, false, false)).publicKeyLength());
	}
	
	@Test
	void accessorsReturnComponents() {
		NativeSignatureAlgorithm algorithm = new NativeSignatureAlgorithm("name", "jca", "key", "curve", 77, true, false);
		assertEquals("name", algorithm.name());
		assertEquals("jca", algorithm.jcaName());
		assertEquals("key", algorithm.keyJcaName());
		assertEquals("curve", algorithm.curve());
		assertEquals(77, algorithm.publicKeyLength());
		assertTrue(algorithm.isPostQuantum());
		assertFalse(algorithm.requiresBouncyCastle());
	}
	
	@Test
	void isSignatureAlgorithm() {
		assertInstanceOf(SignatureAlgorithm.class, SignatureAlgorithm.ED25519);
	}
	
	@Test
	void keySpecReturnsIndependentSpecs() {
		NativeSignatureAlgorithm algorithm = SignatureAlgorithm.ECDSA_P256_SHA_256;
		assertNotSame(algorithm.keySpec(), algorithm.keySpec());
		assertEquals(((ECGenParameterSpec) algorithm.keySpec()).getName(), ((ECGenParameterSpec) algorithm.keySpec()).getName());
	}
	
	@Test
	void equalsAndHashCodeOverAllComponents() {
		NativeSignatureAlgorithm algorithm = new NativeSignatureAlgorithm("name", "jca", "key", "secp256r1", 77, true, false);
		assertEquals(algorithm, new NativeSignatureAlgorithm("name", "jca", "key", "secp256r1", 77, true, false));
		assertEquals(algorithm.hashCode(), new NativeSignatureAlgorithm("name", "jca", "key", "secp256r1", 77, true, false).hashCode());
		assertNotEquals(algorithm, new NativeSignatureAlgorithm("other", "jca", "key", "secp256r1", 77, true, false));
		assertNotEquals(algorithm, new NativeSignatureAlgorithm("name", "other", "key", "secp256r1", 77, true, false));
		assertNotEquals(algorithm, new NativeSignatureAlgorithm("name", "jca", "other", "secp256r1", 77, true, false));
		assertNotEquals(algorithm, new NativeSignatureAlgorithm("name", "jca", "key", null, 77, true, false));
		assertNotEquals(algorithm, new NativeSignatureAlgorithm("name", "jca", "key", "secp256r1", 99, true, false));
		assertNotEquals(algorithm, new NativeSignatureAlgorithm("name", "jca", "key", "secp256r1", 77, false, false));
		assertNotEquals(algorithm, new NativeSignatureAlgorithm("name", "jca", "key", "secp256r1", 77, true, true));
	}
	
	@Test
	void toStringContainsComponents() {
		String string = new NativeSignatureAlgorithm("name", "jca", "key", null, 77, true, false).toString();
		assertTrue(string.contains("NativeSignatureAlgorithm"));
		assertTrue(string.contains("name"));
		assertTrue(string.contains("jca"));
		assertTrue(string.contains("null"));
	}
	
	@Test
	void jcaNameAndKeyJcaNameDifferForEcdsa() {
		assertEquals("SHA256withECDSA", SignatureAlgorithm.ECDSA_P256_SHA_256.jcaName());
		assertEquals("EC", SignatureAlgorithm.ECDSA_P256_SHA_256.keyJcaName());
		assertEquals("EC", SignatureAlgorithm.ECDSA_P384_SHA_384.keyJcaName());
		assertEquals("EC", SignatureAlgorithm.ECDSA_P521_SHA_512.keyJcaName());
		assertNotEquals(SignatureAlgorithm.ECDSA_P256_SHA_256.jcaName(), SignatureAlgorithm.ECDSA_P384_SHA_384.jcaName());
		assertNotEquals(SignatureAlgorithm.ECDSA_P256_SHA_256.curve(), SignatureAlgorithm.ECDSA_P384_SHA_384.curve());
	}
	
	@Test
	void jcaNamesEqualForNonEcdsaConstants() {
		for (NativeSignatureAlgorithm algorithm : new NativeSignatureAlgorithm[] {
			SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448,
			SignatureAlgorithm.ML_DSA_44, SignatureAlgorithm.ML_DSA_65, SignatureAlgorithm.ML_DSA_87, SignatureAlgorithm.SLH_DSA_SHA2_128S
		}) {
			assertEquals(algorithm.name(), algorithm.jcaName());
			assertEquals(algorithm.name(), algorithm.keyJcaName());
		}
	}
	
	@Test
	void signAndVerifyRoundTripForClassicalConstants() throws Exception {
		for (NativeSignatureAlgorithm algorithm : new NativeSignatureAlgorithm[] {
			SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448,
			SignatureAlgorithm.ECDSA_P256_SHA_256, SignatureAlgorithm.ECDSA_P384_SHA_384, SignatureAlgorithm.ECDSA_P521_SHA_512
		}) {
			KeyPair pair = keyPair(algorithm);
			Signature signer = Signature.getInstance(algorithm.jcaName());
			signer.initSign(pair.getPrivate());
			signer.update(MESSAGE);
			byte[] signature = signer.sign();
			
			Signature verifier = Signature.getInstance(algorithm.jcaName());
			verifier.initVerify(pair.getPublic());
			verifier.update(MESSAGE);
			assertTrue(verifier.verify(signature));
			
			Signature tampered = Signature.getInstance(algorithm.jcaName());
			tampered.initVerify(pair.getPublic());
			tampered.update("tampered".getBytes());
			assertFalse(tampered.verify(signature));
		}
	}
	
	@Test
	void publicKeyLengthMatchesEncodedKeyForClassicalConstants() throws Exception {
		for (NativeSignatureAlgorithm algorithm : new NativeSignatureAlgorithm[] {
			SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448,
			SignatureAlgorithm.ECDSA_P256_SHA_256, SignatureAlgorithm.ECDSA_P384_SHA_384, SignatureAlgorithm.ECDSA_P521_SHA_512
		}) {
			assertEquals(algorithm.publicKeyLength(), keyPair(algorithm).getPublic().getEncoded().length);
		}
	}
	
	@Test
	void signAndVerifyRoundTripForPostQuantumConstants() throws Exception {
		for (NativeSignatureAlgorithm algorithm : new NativeSignatureAlgorithm[] { SignatureAlgorithm.ML_DSA_65, SignatureAlgorithm.SLH_DSA_SHA2_128S }) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = keyPair(algorithm);
			Signature signer = Signature.getInstance(algorithm.jcaName());
			signer.initSign(pair.getPrivate());
			signer.update(MESSAGE);
			byte[] signature = signer.sign();
			
			Signature verifier = Signature.getInstance(algorithm.jcaName());
			verifier.initVerify(pair.getPublic());
			verifier.update(MESSAGE);
			assertTrue(verifier.verify(signature));
			assertEquals(algorithm.publicKeyLength(), pair.getPublic().getEncoded().length);
		}
	}
}
