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

import java.lang.reflect.Constructor;
import java.security.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link HybridSignatureAlgorithm}.<br>
 *
 * @author Luis-St
 */
class HybridSignatureAlgorithmTest {
	
	private static final NativeSignatureAlgorithm BC_ONLY = new NativeSignatureAlgorithm("a", "a", "a", null, 7, false, true);
	private static final NativeSignatureAlgorithm PLAIN = new NativeSignatureAlgorithm("b", "b", "b", null, 11, false, false);
	
	//region Setup
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	//endregion
	
	private static boolean verifies(Signature signature, byte[] bytes) {
		try {
			return signature.verify(bytes);
		} catch (SignatureException e) {
			return false;
		}
	}
	
	@Test
	void constructHybridSignatureAlgorithm() {
		HybridSignatureAlgorithm hybrid = new HybridSignatureAlgorithm(SignatureAlgorithm.ED25519, SignatureAlgorithm.ML_DSA_65);
		assertSame(SignatureAlgorithm.ED25519, hybrid.classical());
		assertSame(SignatureAlgorithm.ML_DSA_65, hybrid.postQuantum());
		assertTrue(hybrid.isPostQuantum());
	}
	
	@Test
	void constructWithNullClassical() {
		assertThrows(NullPointerException.class, () -> new HybridSignatureAlgorithm(null, SignatureAlgorithm.ML_DSA_65));
	}
	
	@Test
	void constructWithNullPostQuantum() {
		assertThrows(NullPointerException.class, () -> new HybridSignatureAlgorithm(SignatureAlgorithm.ED25519, null));
	}
	
	@Test
	void constructWithBothComponentsNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new HybridSignatureAlgorithm(null, null));
		assertEquals("Classical component must not be null", exception.getMessage());
	}
	
	@Test
	void constructWithSameComponentTwice() {
		HybridSignatureAlgorithm hybrid = assertDoesNotThrow(() -> new HybridSignatureAlgorithm(SignatureAlgorithm.ED25519, SignatureAlgorithm.ED25519));
		assertEquals("Ed25519+Ed25519", hybrid.name());
		assertTrue(hybrid.isPostQuantum());
		assertFalse(hybrid.classical().isPostQuantum());
		assertFalse(hybrid.postQuantum().isPostQuantum());
	}
	
	@Test
	void requiresBouncyCastleFromClassicalComponent() {
		assertTrue(new HybridSignatureAlgorithm(BC_ONLY, PLAIN).requiresBouncyCastle());
	}
	
	@Test
	void requiresBouncyCastleFromPostQuantumComponent() {
		assertTrue(new HybridSignatureAlgorithm(SignatureAlgorithm.ED25519, SignatureAlgorithm.SLH_DSA_SHA2_128S).requiresBouncyCastle());
		assertFalse(SignatureAlgorithm.ED25519.requiresBouncyCastle());
		assertTrue(SignatureAlgorithm.SLH_DSA_SHA2_128S.requiresBouncyCastle());
	}
	
	@Test
	void requiresBouncyCastleFromNeitherComponent() {
		assertFalse(SignatureAlgorithm.ED25519_ML_DSA_65.requiresBouncyCastle());
		assertFalse(new HybridSignatureAlgorithm(PLAIN, PLAIN).requiresBouncyCastle());
	}
	
	@Test
	void requiresBouncyCastleFromBothComponents() {
		assertTrue(new HybridSignatureAlgorithm(BC_ONLY, BC_ONLY).requiresBouncyCastle());
	}
	
	@Test
	void isPostQuantumAlwaysTrue() {
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (algorithm instanceof HybridSignatureAlgorithm hybrid) {
				assertTrue(hybrid.isPostQuantum());
			}
		}
		assertTrue(new HybridSignatureAlgorithm(PLAIN, PLAIN).isPostQuantum());
		assertFalse(SignatureAlgorithm.ED25519_ML_DSA_65.classical().isPostQuantum());
		assertTrue(SignatureAlgorithm.ED25519_ML_DSA_65.isPostQuantum());
	}
	
	@Test
	void nameJoinsComponentNames() {
		assertEquals("Ed25519+ML-DSA-65", SignatureAlgorithm.ED25519_ML_DSA_65.name());
		assertEquals("ECDSA-P256-SHA256+ML-DSA-44", SignatureAlgorithm.ECDSA_P256_ML_DSA_44.name());
	}
	
	@Test
	void nameUsesPlusSeparator() {
		assertEquals("a+b", new HybridSignatureAlgorithm(BC_ONLY, PLAIN).name());
	}
	
	@Test
	void publicKeyLengthSumsComponents() {
		assertEquals(2018, SignatureAlgorithm.ED25519_ML_DSA_65.publicKeyLength());
		assertEquals(1425, SignatureAlgorithm.ECDSA_P256_ML_DSA_44.publicKeyLength());
		assertEquals(2094, SignatureAlgorithm.ECDSA_P384_ML_DSA_65.publicKeyLength());
		assertEquals(2683, SignatureAlgorithm.ED448_ML_DSA_87.publicKeyLength());
		assertEquals(2772, SignatureAlgorithm.ECDSA_P521_ML_DSA_87.publicKeyLength());
	}
	
	@Test
	void classicalAndPostQuantumAccessorsAreNotSwapped() {
		HybridSignatureAlgorithm hybrid = new HybridSignatureAlgorithm(BC_ONLY, PLAIN);
		assertSame(BC_ONLY, hybrid.classical());
		assertSame(PLAIN, hybrid.postQuantum());
		assertTrue(hybrid.name().startsWith(BC_ONLY.name()));
	}
	
	@Test
	void isSignatureAlgorithm() {
		assertInstanceOf(SignatureAlgorithm.class, SignatureAlgorithm.ED25519_ML_DSA_65);
	}
	
	@Test
	void publicKeyLengthDerivesFromComponentsGenerically() {
		assertEquals(18, new HybridSignatureAlgorithm(BC_ONLY, PLAIN).publicKeyLength());
	}
	
	@Test
	void publicKeyLengthWithZeroLengthComponents() {
		NativeSignatureAlgorithm zero = new NativeSignatureAlgorithm("z", "z", "z", null, 0, false, false);
		assertEquals(0, new HybridSignatureAlgorithm(zero, zero).publicKeyLength());
		assertEquals(11, new HybridSignatureAlgorithm(zero, PLAIN).publicKeyLength());
		assertEquals(11, new HybridSignatureAlgorithm(PLAIN, zero).publicKeyLength());
	}
	
	@Test
	void equalsAndHashCodeOverComponents() {
		HybridSignatureAlgorithm hybrid = new HybridSignatureAlgorithm(SignatureAlgorithm.ED25519, SignatureAlgorithm.ML_DSA_65);
		assertEquals(SignatureAlgorithm.ED25519_ML_DSA_65, hybrid);
		assertEquals(SignatureAlgorithm.ED25519_ML_DSA_65.hashCode(), hybrid.hashCode());
		assertNotEquals(new HybridSignatureAlgorithm(BC_ONLY, PLAIN), new HybridSignatureAlgorithm(PLAIN, BC_ONLY));
	}
	
	@Test
	void toStringContainsComponents() {
		String string = SignatureAlgorithm.ED25519_ML_DSA_65.toString();
		assertTrue(string.contains("HybridSignatureAlgorithm"));
		assertTrue(string.contains("Ed25519"));
		assertTrue(string.contains("ML-DSA-65"));
	}
	
	@Test
	void nestedHybridIsNotConstructible() {
		Constructor<?>[] constructors = HybridSignatureAlgorithm.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertArrayEquals(new Class<?>[] { NativeSignatureAlgorithm.class, NativeSignatureAlgorithm.class }, constructors[0].getParameterTypes());
	}
	
	@Test
	void signAndVerifyBothHalvesForRecommendedHybrid() throws Exception {
		HybridSignatureAlgorithm hybrid = SignatureAlgorithm.ED25519_ML_DSA_65;
		assumeTrue(Providers.supports(hybrid));
		byte[] message = "message".getBytes();
		
		for (NativeSignatureAlgorithm component : new NativeSignatureAlgorithm[] { hybrid.classical(), hybrid.postQuantum() }) {
			KeyPair pair = KeyPairGenerator.getInstance(component.keyJcaName()).generateKeyPair();
			Signature signer = Signature.getInstance(component.jcaName());
			signer.initSign(pair.getPrivate());
			signer.update(message);
			byte[] signature = signer.sign();
			
			Signature verifier = Signature.getInstance(component.jcaName());
			verifier.initVerify(pair.getPublic());
			verifier.update(message);
			assertTrue(verifier.verify(signature));
			
			signature[0] ^= 0x01;
			Signature broken = Signature.getInstance(component.jcaName());
			broken.initVerify(pair.getPublic());
			broken.update(message);
			assertFalse(verifies(broken, signature));
		}
	}
}
