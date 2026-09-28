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
import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.exception.MalformedDataException;
import net.luis.utils.crypto.key.*;
import net.luis.utils.crypto.util.CryptoBytes;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link CryptoKeys}.<br>
 *
 * @author Luis-St
 */
class CryptoKeysTest {
	
	private static final byte[] DATA = "the quick brown fox".getBytes(StandardCharsets.UTF_8);
	
	private static KeyPair ed25519;
	private static KeyPair x25519;
	private static KeyPair x448;
	private static KeyPair mlKem768;
	private static KeyPair hybridKem;
	private static KeyPair hybridSignature;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		ed25519 = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		x25519 = Kems.generateKeyPair(KemAlgorithm.X25519);
		x448 = Kems.generateKeyPair(KemAlgorithm.X448);
		mlKem768 = Kems.generateKeyPair(KemAlgorithm.ML_KEM_768);
		hybridKem = Kems.generateKeyPair(KemAlgorithm.X25519_ML_KEM_768);
		hybridSignature = Signatures.generateKeyPair(SignatureAlgorithm.ED25519_ML_DSA_65);
	}
	
	private static byte[] withPrefix(int length, byte[] payload) {
		return CryptoBytes.concat(CryptoBytes.of(length), payload);
	}
	
	@Test
	void constructorIsPrivate() throws Exception {
		Constructor<?>[] constructors = CryptoKeys.class.getDeclaredConstructors();
		assertEquals(1, constructors.length);
		assertTrue(Modifier.isPrivate(constructors[0].getModifiers()));
		assertTrue(Modifier.isFinal(CryptoKeys.class.getModifiers()));
		
		Constructor<CryptoKeys> constructor = CryptoKeys.class.getDeclaredConstructor();
		constructor.setAccessible(true);
		assertNotNull(constructor.newInstance());
	}
	
	@Test
	void publicKeyForKemWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> CryptoKeys.publicKey((KemAlgorithm) null, new byte[0]));
	}
	
	@Test
	void publicKeyForKemWithNullEncoded() {
		assertEquals("Encoded key must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519, null)).getMessage());
	}
	
	@Test
	void privateKeyForKemWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> CryptoKeys.privateKey((KemAlgorithm) null, new byte[0]));
	}
	
	@Test
	void privateKeyForKemWithNullEncoded() {
		assertEquals("Encoded key must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.privateKey(KemAlgorithm.X25519, null)).getMessage());
	}
	
	@Test
	void publicKeyForSignatureWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> CryptoKeys.publicKey((SignatureAlgorithm) null, new byte[0]));
	}
	
	@Test
	void publicKeyForSignatureWithNullEncoded() {
		assertEquals("Encoded key must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.publicKey(SignatureAlgorithm.ED25519, null)).getMessage());
	}
	
	@Test
	void privateKeyForSignatureWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> CryptoKeys.privateKey((SignatureAlgorithm) null, new byte[0]));
	}
	
	@Test
	void privateKeyForSignatureWithNullEncoded() {
		assertEquals("Encoded key must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.privateKey(SignatureAlgorithm.ED25519, null)).getMessage());
	}
	
	@Test
	void publicKeyWithBothNull() {
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.publicKey((KemAlgorithm) null, null)).getMessage());
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.privateKey((KemAlgorithm) null, null)).getMessage());
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.publicKey((SignatureAlgorithm) null, null)).getMessage());
		assertEquals("Algorithm must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.privateKey((SignatureAlgorithm) null, null)).getMessage());
	}
	
	@Test
	void idWithNullKey() {
		assertThrows(NullPointerException.class, () -> CryptoKeys.id(null));
	}
	
	@Test
	void fingerprintWithNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.fingerprint(HashAlgorithm.SHA_256, null)).getMessage());
	}
	
	@Test
	void fingerprintWithNullAlgorithm() {
		assertThrows(NullPointerException.class, () -> CryptoKeys.fingerprint(null, ed25519.getPublic()));
	}
	
	@Test
	void fingerprintWithNullAlgorithmAndNullKey() {
		assertEquals("Key must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.fingerprint(null, null)).getMessage());
	}
	
	@Test
	void decodePublicWithNullKeyJcaName() {
		assertEquals("Key JCA name must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.decodePublic(null, new byte[0])).getMessage());
	}
	
	@Test
	void decodePublicWithNullEncoded() {
		assertEquals("Encoded key must not be null", assertThrows(NullPointerException.class, () -> CryptoKeys.decodePublic("Ed25519", null)).getMessage());
	}
	
	@Test
	void decodePrivateWithNullKeyJcaName() {
		assertThrows(NullPointerException.class, () -> CryptoKeys.decodePrivate(null, new byte[0]));
	}
	
	@Test
	void decodePrivateWithNullEncoded() {
		assertThrows(NullPointerException.class, () -> CryptoKeys.decodePrivate("Ed25519", null));
	}
	
	@Test
	void decodePublicWithUnknownKeyJcaName() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.decodePublic("NoSuchKeyType", ed25519.getPublic().getEncoded()));
		
		assertTrue(exception.getMessage().contains("NoSuchKeyType"));
		assertTrue(exception.getMessage().contains("public key"));
		assertInstanceOf(NoSuchAlgorithmException.class, exception.getCause());
	}
	
	@Test
	void decodePublicWithMalformedEncoding() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.decodePublic("Ed25519", new byte[] { 1, 2, 3 }));
		assertInstanceOf(InvalidKeySpecException.class, exception.getCause());
	}
	
	@Test
	void decodePublicWithEmptyEncoding() {
		assertThrows(MalformedDataException.class, () -> CryptoKeys.decodePublic("Ed25519", new byte[0]));
	}
	
	@Test
	void decodePublicWithWrongKeyType() {
		assertThrows(MalformedDataException.class, () -> CryptoKeys.decodePublic("X25519", ed25519.getPublic().getEncoded()));
	}
	
	@Test
	void decodePrivateWithUnknownKeyJcaName() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.decodePrivate("NoSuchKeyType", ed25519.getPrivate().getEncoded()));
		assertTrue(exception.getMessage().contains("private key"));
	}
	
	@Test
	void decodePrivateWithMalformedEncoding() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.decodePrivate("Ed25519", new byte[] { 1, 2, 3 }));
		assertInstanceOf(InvalidKeySpecException.class, exception.getCause());
	}
	
	@Test
	void decodePrivateWithPublicKeyEncoding() {
		assertThrows(MalformedDataException.class, () -> CryptoKeys.decodePrivate("Ed25519", ed25519.getPublic().getEncoded()));
	}
	
	@Test
	void publicKeyForHybridWithEmptyEncoding() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, new byte[0]));
		
		assertEquals("Malformed composite key encoding", exception.getMessage());
		assertInstanceOf(BufferUnderflowException.class, exception.getCause());
	}
	
	@Test
	void publicKeyForHybridWithTruncatedLengthPrefix() {
		for (int length : new int[] { 1, 2, 3 }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, new byte[length]));
			assertInstanceOf(BufferUnderflowException.class, exception.getCause());
		}
	}
	
	@Test
	void publicKeyForHybridWithTruncatedFirstComponent() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, withPrefix(100, new byte[4])));
		
		assertInstanceOf(MalformedDataException.class, exception.getCause());
		assertTrue(exception.getCause().getMessage().contains("100"));
		assertTrue(exception.getCause().getMessage().contains("4"));
	}
	
	@Test
	void publicKeyForHybridWithTruncatedSecondComponent() {
		byte[] encoded = CryptoBytes.concat(CryptoBytes.of(2), new byte[] { 1, 2 }, CryptoBytes.of(100), new byte[4]);
		assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, encoded));
	}
	
	@Test
	void publicKeyForHybridWithNegativeLengthPrefix() {
		for (int length : new int[] { -1, Integer.MIN_VALUE }) {
			MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, withPrefix(length, new byte[8])));
			assertTrue(exception.getCause().getMessage().contains(String.valueOf(length)));
		}
	}
	
	@Test
	void publicKeyForHybridWithHugeLengthPrefix() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, withPrefix(Integer.MAX_VALUE, new byte[8])));
		assertTrue(exception.getCause().getMessage().contains(String.valueOf(Integer.MAX_VALUE)));
	}
	
	@Test
	void publicKeyForHybridWithValidSplitButInvalidComponent() {
		byte[] encoded = CryptoBytes.concat(CryptoBytes.of(3), new byte[] { 1, 2, 3 }, CryptoBytes.of(3), new byte[] { 4, 5, 6 });
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, encoded));
		
		assertTrue(exception.getMessage().contains("X25519"));
		assertNotEquals("Malformed composite key encoding", exception.getMessage());
	}
	
	@Test
	void privateKeyForHybridWithMalformedEncodings() {
		for (byte[] malformed : new byte[][] { new byte[0], new byte[3], withPrefix(-1, new byte[8]), withPrefix(Integer.MAX_VALUE, new byte[8]) }) {
			assertThrows(MalformedDataException.class, () -> CryptoKeys.privateKey(KemAlgorithm.X25519_ML_KEM_768, malformed));
		}
	}
	
	@Test
	void hybridSignatureKeyWithMalformedEncodings() {
		for (byte[] malformed : new byte[][] { new byte[0], new byte[3], withPrefix(-1, new byte[8]), withPrefix(Integer.MAX_VALUE, new byte[8]) }) {
			assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(SignatureAlgorithm.ED25519_ML_DSA_65, malformed));
			assertThrows(MalformedDataException.class, () -> CryptoKeys.privateKey(SignatureAlgorithm.ED25519_ML_DSA_65, malformed));
		}
	}
	
	@Test
	void publicKeyForNativeKem() {
		assumeTrue(Providers.supports(KemAlgorithm.ML_KEM_768));
		assertEquals(mlKem768.getPublic(), CryptoKeys.publicKey(KemAlgorithm.ML_KEM_768, mlKem768.getPublic().getEncoded()));
	}
	
	@Test
	void publicKeyForDhKem() {
		assertEquals(x25519.getPublic(), CryptoKeys.publicKey(KemAlgorithm.X25519, x25519.getPublic().getEncoded()));
		assertEquals(x448.getPublic(), CryptoKeys.publicKey(KemAlgorithm.X448, x448.getPublic().getEncoded()));
	}
	
	@Test
	void publicKeyForHybridKem() {
		HybridPublicKey original = assertInstanceOf(HybridPublicKey.class, hybridKem.getPublic());
		HybridPublicKey decoded = assertInstanceOf(HybridPublicKey.class, CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, original.getEncoded()));
		
		assertEquals(original.classical(), decoded.classical());
		assertEquals(original.postQuantum(), decoded.postQuantum());
	}
	
	@Test
	void privateKeyForNativeKem() {
		assumeTrue(Providers.supports(KemAlgorithm.ML_KEM_768));
		assertEquals(mlKem768.getPrivate(), CryptoKeys.privateKey(KemAlgorithm.ML_KEM_768, mlKem768.getPrivate().getEncoded()));
	}
	
	@Test
	void privateKeyForDhKem() {
		assertEquals(x25519.getPrivate(), CryptoKeys.privateKey(KemAlgorithm.X25519, x25519.getPrivate().getEncoded()));
		assertEquals(x448.getPrivate(), CryptoKeys.privateKey(KemAlgorithm.X448, x448.getPrivate().getEncoded()));
	}
	
	@Test
	void privateKeyForHybridKem() {
		HybridPrivateKey original = assertInstanceOf(HybridPrivateKey.class, hybridKem.getPrivate());
		HybridPrivateKey decoded = assertInstanceOf(HybridPrivateKey.class, CryptoKeys.privateKey(KemAlgorithm.X25519_ML_KEM_768, original.getEncoded()));
		
		assertEquals(original.classical(), decoded.classical());
		assertEquals(original.postQuantum(), decoded.postQuantum());
	}
	
	@Test
	void publicKeyForNativeSignature() {
		for (NativeSignatureAlgorithm algorithm : new NativeSignatureAlgorithm[] { SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448, SignatureAlgorithm.ECDSA_P256_SHA_256, SignatureAlgorithm.ECDSA_P384_SHA_384, SignatureAlgorithm.ECDSA_P521_SHA_512 }) {
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			assertEquals(pair.getPublic(), CryptoKeys.publicKey(algorithm, pair.getPublic().getEncoded()), algorithm.name());
		}
	}
	
	@Test
	void publicKeyForHybridSignature() {
		HybridPublicKey original = assertInstanceOf(HybridPublicKey.class, hybridSignature.getPublic());
		HybridPublicKey decoded = assertInstanceOf(HybridPublicKey.class, CryptoKeys.publicKey(SignatureAlgorithm.ED25519_ML_DSA_65, original.getEncoded()));
		
		assertEquals(original.classical(), decoded.classical());
		assertEquals(original.postQuantum(), decoded.postQuantum());
	}
	
	@Test
	void privateKeyForNativeSignature() {
		for (NativeSignatureAlgorithm algorithm : new NativeSignatureAlgorithm[] { SignatureAlgorithm.ED25519, SignatureAlgorithm.ED448, SignatureAlgorithm.ECDSA_P256_SHA_256 }) {
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			assertEquals(pair.getPrivate(), CryptoKeys.privateKey(algorithm, pair.getPrivate().getEncoded()), algorithm.name());
		}
	}
	
	@Test
	void privateKeyForHybridSignature() {
		HybridPrivateKey decoded = assertInstanceOf(HybridPrivateKey.class, CryptoKeys.privateKey(SignatureAlgorithm.ED25519_ML_DSA_65, hybridSignature.getPrivate().getEncoded()));
		assertNotNull(decoded.classical());
		assertNotNull(decoded.postQuantum());
	}
	
	@Test
	void decodePublicWithValidEncoding() {
		PublicKey decoded = CryptoKeys.decodePublic("Ed25519", ed25519.getPublic().getEncoded());
		assertEquals(ed25519.getPublic(), decoded);
		assertEquals("X.509", decoded.getFormat());
	}
	
	@Test
	void decodePrivateWithValidEncoding() {
		PrivateKey decoded = CryptoKeys.decodePrivate("Ed25519", ed25519.getPrivate().getEncoded());
		assertEquals(ed25519.getPrivate(), decoded);
		assertEquals("PKCS#8", decoded.getFormat());
	}
	
	@Test
	void hybridSplitWithEmptyComponents() {
		MalformedDataException exception = assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, new byte[8]));
		assertNotEquals("Malformed composite key encoding", exception.getMessage());
		assertTrue(exception.getMessage().contains("X25519"));
	}
	
	@Test
	void hybridSplitWithLengthExactlyMatchingRemaining() {
		byte[] encoded = hybridKem.getPublic().getEncoded();
		HybridPublicKey original = assertInstanceOf(HybridPublicKey.class, hybridKem.getPublic());
		
		assertEquals(8 + original.classical().getEncoded().length + original.postQuantum().getEncoded().length, encoded.length);
		assertDoesNotThrow(() -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, encoded));
	}
	
	@Test
	void hybridSplitIgnoresTrailingBytes() {
		byte[] encoded = CryptoBytes.concat(hybridKem.getPublic().getEncoded(), new byte[] { 9, 9, 9, 9, 9 });
		HybridPublicKey decoded = assertInstanceOf(HybridPublicKey.class, assertDoesNotThrow(() -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, encoded)));
		
		assertEquals(((HybridPublicKey) hybridKem.getPublic()).classical(), decoded.classical());
	}
	
	@Test
	void idDelegatesToKeyId() {
		assertEquals(KeyId.of(ed25519.getPublic()), CryptoKeys.id(ed25519.getPublic()));
		assertEquals(CryptoKeys.id(ed25519.getPublic()), CryptoKeys.id(ed25519.getPublic()));
	}
	
	@Test
	void idForHybridKey() {
		HybridPublicKey key = assertInstanceOf(HybridPublicKey.class, hybridKem.getPublic());
		
		assertEquals(CryptoKeys.id(key), CryptoKeys.id(key));
		assertNotEquals(CryptoKeys.id(key.classical()), CryptoKeys.id(key));
	}
	
	@Test
	void idWithoutEncodedForm() {
		CryptoException exception = assertThrows(CryptoException.class, () -> CryptoKeys.id(new UnencodableKey("HSM")));
		assertTrue(exception.getMessage().contains("HSM"));
	}
	
	@Test
	void fingerprintLength() {
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertEquals(algorithm.digestLength(), CryptoKeys.fingerprint(algorithm, ed25519.getPublic()).length, algorithm.jcaName());
		}
	}
	
	@Test
	void fingerprintIsDeterministic() {
		assertArrayEquals(CryptoKeys.fingerprint(HashAlgorithm.SHA_256, ed25519.getPublic()), CryptoKeys.fingerprint(HashAlgorithm.SHA_256, ed25519.getPublic()));
	}
	
	@Test
	void fingerprintDiffersForDifferentKeys() {
		KeyPair other = Signatures.generateKeyPair(SignatureAlgorithm.ED25519);
		assertFalse(Arrays.equals(CryptoKeys.fingerprint(HashAlgorithm.SHA_256, ed25519.getPublic()), CryptoKeys.fingerprint(HashAlgorithm.SHA_256, other.getPublic())));
	}
	
	@Test
	void fingerprintDiffersForDifferentAlgorithms() {
		byte[] sha2 = CryptoKeys.fingerprint(HashAlgorithm.SHA_256, ed25519.getPublic());
		byte[] sha3 = CryptoKeys.fingerprint(HashAlgorithm.SHA3_256, ed25519.getPublic());
		
		assertEquals(sha2.length, sha3.length);
		assertFalse(Arrays.equals(sha2, sha3));
	}
	
	@Test
	void fingerprintMatchesHashOfEncoding() {
		assertArrayEquals(Hashes.hash(HashAlgorithm.SHA_256, ed25519.getPublic().getEncoded()), CryptoKeys.fingerprint(HashAlgorithm.SHA_256, ed25519.getPublic()));
	}
	
	@Test
	void fingerprintIsLongerThanKeyId() {
		byte[] fingerprint = CryptoKeys.fingerprint(HashAlgorithm.SHA_256, ed25519.getPublic());
		byte[] id = CryptoKeys.id(ed25519.getPublic()).toBytes();
		
		assertEquals(32, fingerprint.length);
		assertEquals(16, id.length);
		assertEquals(8, CryptoKeys.id(ed25519.getPublic()).value().version());
		assertFalse(Arrays.equals(Arrays.copyOf(fingerprint, 16), id));
	}
	
	@Test
	void decodePublicReturnsIndependentKeys() {
		byte[] encoded = ed25519.getPublic().getEncoded();
		PublicKey first = CryptoKeys.decodePublic("Ed25519", encoded);
		PublicKey second = CryptoKeys.decodePublic("Ed25519", encoded);
		
		assertEquals(first, second);
		assertNotSame(first, second);
	}
	
	@Test
	void keyPairRoundTripForEveryKemAlgorithm() {
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			assumeTrue(Providers.supports(algorithm));
			KeyPair pair = Kems.generateKeyPair(algorithm);
			
			assertEquals(pair.getPublic(), CryptoKeys.publicKey(algorithm, pair.getPublic().getEncoded()), algorithm.name());
			assertEquals(pair.getPrivate(), CryptoKeys.privateKey(algorithm, pair.getPrivate().getEncoded()), algorithm.name());
		}
	}
	
	@Test
	void keyPairRoundTripForEverySignatureAlgorithm() {
		for (SignatureAlgorithm algorithm : SignatureAlgorithm.VALUES) {
			if (!Providers.supports(algorithm)) {
				continue;
			}
			KeyPair pair = Signatures.generateKeyPair(algorithm);
			assertEquals(pair.getPublic(), CryptoKeys.publicKey(algorithm, pair.getPublic().getEncoded()), algorithm.name());
			assertEquals(pair.getPrivate(), CryptoKeys.privateKey(algorithm, pair.getPrivate().getEncoded()), algorithm.name());
		}
	}
	
	@Test
	void decodedHybridKeyIsUsable() {
		PublicKey publicKey = CryptoKeys.publicKey(SignatureAlgorithm.ED25519_ML_DSA_65, hybridSignature.getPublic().getEncoded());
		PrivateKey privateKey = CryptoKeys.privateKey(SignatureAlgorithm.ED25519_ML_DSA_65, hybridSignature.getPrivate().getEncoded());
		byte[] signature = Signatures.sign(SignatureAlgorithm.ED25519_ML_DSA_65, privateKey, DATA);
		
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, publicKey, DATA, signature));
		assertTrue(Signatures.verify(SignatureAlgorithm.ED25519_ML_DSA_65, hybridSignature.getPublic(), DATA, signature));
	}
	
	@Test
	void decodedHybridKemKeyAgreesOnTheSameSecret() {
		PublicKey publicKey = CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, hybridKem.getPublic().getEncoded());
		PrivateKey privateKey = CryptoKeys.privateKey(KemAlgorithm.X25519_ML_KEM_768, hybridKem.getPrivate().getEncoded());
		
		try (Kems.Encapsulation sent = Kems.encapsulate(KemAlgorithm.X25519_ML_KEM_768, publicKey)) {
			try (Secret received = Kems.decapsulate(KemAlgorithm.X25519_ML_KEM_768, privateKey, sent.encapsulation()); Secret original = Kems.decapsulate(KemAlgorithm.X25519_ML_KEM_768, hybridKem.getPrivate(), sent.encapsulation())) {
				assertArrayEquals(sent.sharedSecret().material(), received.material());
				assertArrayEquals(sent.sharedSecret().material(), original.material());
			}
		}
	}
	
	@Test
	void hybridEncodingMatchesHybridKeyLayout() {
		HybridPublicKey key = assertInstanceOf(HybridPublicKey.class, hybridKem.getPublic());
		byte[] encoded = key.getEncoded();
		byte[] classical = key.classical().getEncoded();
		byte[] postQuantum = key.postQuantum().getEncoded();
		
		assertEquals(classical.length, ByteBuffer.wrap(encoded, 0, 4).getInt());
		assertEquals(8 + classical.length + postQuantum.length, encoded.length);
		assertArrayEquals(classical, Arrays.copyOfRange(encoded, 4, 4 + classical.length));
		assertArrayEquals(postQuantum, Arrays.copyOfRange(encoded, 8 + classical.length, encoded.length));
	}
	
	@Test
	void hybridDecodeRejectsSwappedComponents() {
		assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X448_ML_KEM_1024, hybridKem.getPublic().getEncoded()));
	}
	
	@Test
	void hybridDecodeRejectsNativeEncoding() {
		assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, x25519.getPublic().getEncoded()));
	}
	
	@Test
	void nativeDecodeRejectsHybridEncoding() {
		assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519, hybridKem.getPublic().getEncoded()));
	}
	
	@Test
	void decodeRejectsAllTruncationsOfAHybridEncoding() {
		byte[] encoded = hybridKem.getPublic().getEncoded();
		for (int length : new int[] { 0, 1, 3, 4, 7, 20, encoded.length / 2, encoded.length - 1 }) {
			byte[] prefix = Arrays.copyOf(encoded, length);
			assertThrows(MalformedDataException.class, () -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, prefix), "prefix length " + length);
		}
		assertDoesNotThrow(() -> CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, encoded));
	}
	
	@Test
	void decodeDoesNotMutateInput() {
		byte[] nativeEncoded = ed25519.getPublic().getEncoded();
		byte[] nativeCopy = nativeEncoded.clone();
		byte[] hybridEncoded = hybridKem.getPublic().getEncoded();
		byte[] hybridCopy = hybridEncoded.clone();
		
		CryptoKeys.publicKey(SignatureAlgorithm.ED25519, nativeEncoded);
		CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, hybridEncoded);
		assertArrayEquals(nativeCopy, nativeEncoded);
		assertArrayEquals(hybridCopy, hybridEncoded);
	}
	
	@Test
	void decodeIsDeterministic() {
		byte[] nativeEncoded = ed25519.getPublic().getEncoded();
		byte[] hybridEncoded = hybridKem.getPublic().getEncoded();
		
		assertEquals(CryptoKeys.publicKey(SignatureAlgorithm.ED25519, nativeEncoded), CryptoKeys.publicKey(SignatureAlgorithm.ED25519, nativeEncoded));
		assertEquals(CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, hybridEncoded), CryptoKeys.publicKey(KemAlgorithm.X25519_ML_KEM_768, hybridEncoded));
	}
	
	@Test
	void fingerprintAndIdAgreeAcrossKeyTypes() {
		List<PublicKey> keys = new ArrayList<>();
		for (KemAlgorithm algorithm : KemAlgorithm.VALUES) {
			if (Providers.supports(algorithm)) {
				keys.add(Kems.generateKeyPair(algorithm).getPublic());
			}
		}
		keys.add(ed25519.getPublic());
		keys.add(hybridSignature.getPublic());
		
		Set<KeyId> ids = new HashSet<>();
		Set<String> fingerprints = new HashSet<>();
		for (PublicKey key : keys) {
			ids.add(CryptoKeys.id(key));
			fingerprints.add(HexFormat.of().formatHex(CryptoKeys.fingerprint(HashAlgorithm.SHA_256, key)));
		}
		assertEquals(keys.size(), ids.size());
		assertEquals(keys.size(), fingerprints.size());
	}
	
	private record UnencodableKey(String algorithm) implements PublicKey {
		
		@Override
		public String getAlgorithm() {
			return this.algorithm;
		}
		
		@Override
		public String getFormat() {
			return "RAW";
		}
		
		@Override
		public byte @Nullable [] getEncoded() {
			return null;
		}
	}
}
