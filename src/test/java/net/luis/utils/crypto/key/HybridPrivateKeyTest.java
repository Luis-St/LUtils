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

package net.luis.utils.crypto.key;

import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.security.auth.DestroyFailedException;
import javax.security.auth.Destroyable;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HybridPrivateKey}.<br>
 *
 * @author Luis-St
 */
class HybridPrivateKeyTest {
	
	private static final byte[] MESSAGE = "hybrid".getBytes();
	
	private static KeyPair ed25519;
	private static KeyPair mlDsa65;
	private static KeyPair ed448;
	
	@BeforeAll
	static void setUp() throws Exception {
		ed25519 = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
		ed448 = KeyPairGenerator.getInstance("Ed448").generateKeyPair();
		mlDsa65 = KeyPairGenerator.getInstance("ML-DSA-65").generateKeyPair();
	}
	
	private static TestPrivateKey destroyable(String algorithm) {
		return new TestPrivateKey(algorithm, new byte[] { 1, 2, 3 }, null);
	}
	
	private static TestPrivateKey failing(String algorithm, @Nullable String message) {
		return new TestPrivateKey(algorithm, new byte[] { 1, 2, 3 }, message == null ? new DestroyFailedException() : new DestroyFailedException(message));
	}
	
	private static boolean verifies(String algorithm, PrivateKey privateKey, PublicKey publicKey) throws Exception {
		Signature signer = Signature.getInstance(algorithm);
		signer.initSign(privateKey);
		signer.update(MESSAGE);
		byte[] signature = signer.sign();
		
		Signature verifier = Signature.getInstance(algorithm);
		verifier.initVerify(publicKey);
		verifier.update(MESSAGE);
		return verifier.verify(signature);
	}
	
	@Test
	void constructHybridPrivateKey() {
		HybridPrivateKey key = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		assertSame(ed25519.getPrivate(), key.classical());
		assertSame(mlDsa65.getPrivate(), key.postQuantum());
	}
	
	@Test
	void constructWithNullClassical() {
		assertThrows(NullPointerException.class, () -> new HybridPrivateKey(null, mlDsa65.getPrivate()));
	}
	
	@Test
	void constructWithNullPostQuantum() {
		assertThrows(NullPointerException.class, () -> new HybridPrivateKey(ed25519.getPrivate(), null));
	}
	
	@Test
	void constructWithBothComponentsNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new HybridPrivateKey(null, null));
		assertEquals("Classical component must not be null", exception.getMessage());
	}
	
	@Test
	void destroyWithFailingClassicalComponent() {
		HybridPrivateKey key = new HybridPrivateKey(failing("Broken", "nope"), destroyable("Fine"));
		
		DestroyFailedException exception = assertThrows(DestroyFailedException.class, key::destroy);
		assertTrue(exception.getMessage().contains("Could not destroy every component"));
		assertTrue(exception.getMessage().contains("Broken"));
		assertTrue(exception.getMessage().contains("nope"));
	}
	
	@Test
	void destroyWithFailingPostQuantumComponent() {
		HybridPrivateKey key = new HybridPrivateKey(destroyable("Fine"), failing("Broken", "nope"));
		
		DestroyFailedException exception = assertThrows(DestroyFailedException.class, key::destroy);
		assertTrue(exception.getMessage().contains("Broken"));
		assertFalse(exception.getMessage().contains("Fine"));
	}
	
	@Test
	void destroyWithBothComponentsFailing() {
		HybridPrivateKey key = new HybridPrivateKey(failing("First", "a"), failing("Second", "b"));
		
		DestroyFailedException exception = assertThrows(DestroyFailedException.class, key::destroy);
		assertTrue(exception.getMessage().contains("First: a"));
		assertTrue(exception.getMessage().contains("Second: b"));
		assertTrue(exception.getMessage().contains("First: a, Second: b"));
	}
	
	@Test
	void getEncodedWithComponentWithoutEncoding() {
		HybridPrivateKey classicalMissing = new HybridPrivateKey(new TestPrivateKey("HSM", null, null), mlDsa65.getPrivate());
		assertThrows(NullPointerException.class, classicalMissing::getEncoded);
		
		HybridPrivateKey postQuantumMissing = new HybridPrivateKey(ed25519.getPrivate(), new TestPrivateKey("HSM", null, null));
		assertThrows(NullPointerException.class, postQuantumMissing::getEncoded);
	}
	
	@Test
	void destroyBothComponents() {
		TestPrivateKey classical = destroyable("Fine");
		TestPrivateKey postQuantum = destroyable("AlsoFine");
		HybridPrivateKey key = new HybridPrivateKey(classical, postQuantum);
		
		assertDoesNotThrow(key::destroy);
		assertTrue(classical.isDestroyed());
		assertTrue(postQuantum.isDestroyed());
		assertTrue(key.isDestroyed());
	}
	
	@Test
	void destroyContinuesAfterFirstFailure() {
		TestPrivateKey classical = failing("Broken", "nope");
		TestPrivateKey postQuantum = destroyable("Fine");
		HybridPrivateKey key = new HybridPrivateKey(classical, postQuantum);
		
		assertThrows(DestroyFailedException.class, key::destroy);
		assertFalse(classical.isDestroyed());
		assertTrue(postQuantum.isDestroyed());
	}
	
	@Test
	void isDestroyedWithNeitherComponentDestroyed() {
		assertFalse(new HybridPrivateKey(destroyable("a"), destroyable("b")).isDestroyed());
	}
	
	@Test
	void isDestroyedWithOnlyClassicalDestroyed() throws Exception {
		TestPrivateKey classical = destroyable("a");
		classical.destroy();
		assertFalse(new HybridPrivateKey(classical, destroyable("b")).isDestroyed());
	}
	
	@Test
	void isDestroyedWithOnlyPostQuantumDestroyed() throws Exception {
		TestPrivateKey postQuantum = destroyable("b");
		postQuantum.destroy();
		assertFalse(new HybridPrivateKey(destroyable("a"), postQuantum).isDestroyed());
	}
	
	@Test
	void isDestroyedWithBothComponentsDestroyed() throws Exception {
		TestPrivateKey classical = destroyable("a");
		TestPrivateKey postQuantum = destroyable("b");
		classical.destroy();
		postQuantum.destroy();
		assertTrue(new HybridPrivateKey(classical, postQuantum).isDestroyed());
	}
	
	@Test
	void isDestroyedAfterSuccessfulDestroy() {
		HybridPrivateKey key = new HybridPrivateKey(destroyable("a"), destroyable("b"));
		
		assertFalse(key.isDestroyed());
		assertDoesNotThrow(key::destroy);
		assertTrue(key.isDestroyed());
	}
	
	@Test
	void isDestroyedAfterPartialDestroy() {
		HybridPrivateKey key = new HybridPrivateKey(failing("Broken", "nope"), destroyable("Fine"));
		
		assertThrows(DestroyFailedException.class, key::destroy);
		assertFalse(key.isDestroyed());
	}
	
	@Test
	void getFormatIsRawHybrid() {
		assertEquals("RAW-HYBRID", new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate()).getFormat());
		assertEquals("RAW-HYBRID", new HybridPrivateKey(destroyable("a"), destroyable("b")).getFormat());
	}
	
	@Test
	void getAlgorithmJoinsComponentAlgorithms() {
		HybridPrivateKey key = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		assertEquals("Hybrid(" + ed25519.getPrivate().getAlgorithm() + "+" + mlDsa65.getPrivate().getAlgorithm() + ")", key.getAlgorithm());
		assertEquals("Hybrid(EdDSA+ML-DSA)", key.getAlgorithm());
	}
	
	@Test
	void getAlgorithmReflectsComponentsGenerically() {
		assertEquals("Hybrid(a+b)", new HybridPrivateKey(destroyable("a"), destroyable("b")).getAlgorithm());
	}
	
	@Test
	void getEncodedLayout() {
		byte[] first = ed25519.getPrivate().getEncoded();
		byte[] second = mlDsa65.getPrivate().getEncoded();
		byte[] encoded = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate()).getEncoded();
		
		assertEquals(8 + first.length + second.length, encoded.length);
		assertEquals(first.length, ByteBuffer.wrap(encoded, 0, 4).getInt());
		assertArrayEquals(first, Arrays.copyOfRange(encoded, 4, 4 + first.length));
		assertArrayEquals(second, Arrays.copyOfRange(encoded, 8 + first.length, encoded.length));
	}
	
	@Test
	void getEncodedLengthsAreBigEndian() {
		byte[] first = ed25519.getPrivate().getEncoded();
		byte[] second = mlDsa65.getPrivate().getEncoded();
		byte[] encoded = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate()).getEncoded();
		
		assertEquals(first.length, ByteBuffer.wrap(encoded, 0, 4).getInt());
		assertEquals(second.length, ByteBuffer.wrap(encoded, 4 + first.length, 4).getInt());
	}
	
	@Test
	void getEncodedReturnsFreshArrays() {
		HybridPrivateKey key = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		byte[] first = key.getEncoded();
		byte[] second = key.getEncoded();
		
		assertNotSame(first, second);
		assertArrayEquals(first, second);
		first[0] ^= 1;
		assertArrayEquals(second, key.getEncoded());
	}
	
	@Test
	void classicalAndPostQuantumAccessorsAreNotSwapped() {
		HybridPrivateKey key = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		byte[] first = ed25519.getPrivate().getEncoded();
		
		assertSame(ed25519.getPrivate(), key.classical());
		assertSame(mlDsa65.getPrivate(), key.postQuantum());
		assertArrayEquals(first, Arrays.copyOfRange(key.getEncoded(), 4, 4 + first.length));
	}
	
	@Test
	void destroyIsIdempotent() {
		TestPrivateKey classical = destroyable("a");
		TestPrivateKey postQuantum = destroyable("b");
		HybridPrivateKey key = new HybridPrivateKey(classical, postQuantum);
		
		assertDoesNotThrow(key::destroy);
		assertDoesNotThrow(key::destroy);
		assertTrue(key.isDestroyed());
	}
	
	@Test
	void isPrivateKey() {
		HybridPrivateKey key = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		assertInstanceOf(PrivateKey.class, key);
		assertInstanceOf(Destroyable.class, key);
	}
	
	@Test
	void toStringDoesNotRevealMaterial() {
		HybridPrivateKey key = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		
		assertEquals("HybridPrivateKey[EdDSA+ML-DSA]", key.toString());
		assertFalse(key.toString().contains("classical="));
		assertFalse(key.toString().contains("postQuantum="));
		assertFalse(key.toString().contains(java.util.HexFormat.of().formatHex(ed25519.getPrivate().getEncoded())));
		assertFalse(key.toString().contains(java.util.Base64.getEncoder().encodeToString(ed25519.getPrivate().getEncoded())));
	}
	
	@Test
	void toStringReflectsComponentsGenerically() {
		assertEquals("HybridPrivateKey[a+b]", new HybridPrivateKey(destroyable("a"), destroyable("b")).toString());
	}
	
	@Test
	void getEncodedIsDecodableBackToComponents() throws Exception {
		HybridPrivateKey key = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		ByteBuffer buffer = ByteBuffer.wrap(key.getEncoded());
		
		byte[] first = new byte[buffer.getInt()];
		buffer.get(first);
		byte[] second = new byte[buffer.getInt()];
		buffer.get(second);
		
		PrivateKey classical = KeyFactory.getInstance("EdDSA").generatePrivate(new PKCS8EncodedKeySpec(first));
		PrivateKey postQuantum = KeyFactory.getInstance("ML-DSA").generatePrivate(new PKCS8EncodedKeySpec(second));
		assertEquals(ed25519.getPrivate(), classical);
		assertEquals(mlDsa65.getPrivate(), postQuantum);
		assertEquals(key, new HybridPrivateKey(classical, postQuantum));
	}
	
	@Test
	void matchesHybridPublicKeyLayout() {
		HybridPrivateKey privateKey = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		HybridPublicKey publicKey = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		
		assertEquals(8 + ed25519.getPrivate().getEncoded().length + mlDsa65.getPrivate().getEncoded().length, privateKey.getEncoded().length);
		assertEquals(8 + ed25519.getPublic().getEncoded().length + mlDsa65.getPublic().getEncoded().length, publicKey.getEncoded().length);
		assertEquals(ed25519.getPublic().getEncoded().length, ByteBuffer.wrap(publicKey.getEncoded(), 0, 4).getInt());
		assertEquals(privateKey.getFormat(), publicKey.getFormat());
		assertEquals(privateKey.getAlgorithm(), publicKey.getAlgorithm());
	}
	
	@Test
	void getEncodedWithEmptyComponentEncoding() {
		HybridPrivateKey key = new HybridPrivateKey(new TestPrivateKey("a", new byte[0], null), new TestPrivateKey("b", new byte[0], null));
		byte[] encoded = key.getEncoded();
		
		assertEquals(8, encoded.length);
		assertArrayEquals(new byte[8], encoded);
	}
	
	@Test
	void equalsAndHashCodeOverComponents() {
		HybridPrivateKey first = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		HybridPrivateKey second = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new HybridPrivateKey(ed448.getPrivate(), mlDsa65.getPrivate()));
	}
	
	@Test
	void destroyFailureMessageNamesEveryFailingComponent() {
		HybridPrivateKey single = new HybridPrivateKey(failing("Only", "a"), destroyable("Fine"));
		String singleMessage = assertThrows(DestroyFailedException.class, single::destroy).getMessage();
		assertTrue(singleMessage.endsWith("Only: a"));
		assertFalse(singleMessage.contains(", "));
		
		HybridPrivateKey both = new HybridPrivateKey(failing("First", "a"), failing("Second", "b"));
		String bothMessage = assertThrows(DestroyFailedException.class, both::destroy).getMessage();
		assertTrue(bothMessage.endsWith("First: a, Second: b"));
		assertEquals(1, bothMessage.split(", ", -1).length - 1);
	}
	
	@Test
	void destroyWithNullFailureMessage() {
		HybridPrivateKey key = new HybridPrivateKey(failing("Broken", null), destroyable("Fine"));
		
		DestroyFailedException exception = assertThrows(DestroyFailedException.class, key::destroy);
		assertTrue(exception.getMessage().endsWith("Broken: null"));
	}
	
	@Test
	void signAndVerifyWithRebuiltKey() throws Exception {
		HybridPrivateKey key = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		ByteBuffer buffer = ByteBuffer.wrap(key.getEncoded());
		
		byte[] first = new byte[buffer.getInt()];
		buffer.get(first);
		byte[] second = new byte[buffer.getInt()];
		buffer.get(second);
		
		PrivateKey classical = KeyFactory.getInstance("EdDSA").generatePrivate(new PKCS8EncodedKeySpec(first));
		PrivateKey postQuantum = KeyFactory.getInstance("ML-DSA").generatePrivate(new PKCS8EncodedKeySpec(second));
		assertTrue(verifies("Ed25519", classical, ed25519.getPublic()));
		assertTrue(verifies("ML-DSA", postQuantum, mlDsa65.getPublic()));
	}
	
	@Test
	void destroyedKeyCannotSign() throws Exception {
		KeyPair pair = KeyPairGenerator.getInstance("ML-DSA-65").generateKeyPair();
		HybridPrivateKey key = new HybridPrivateKey(destroyable("Fine"), pair.getPrivate());
		assertTrue(verifies("ML-DSA", pair.getPrivate(), pair.getPublic()));
		
		assertDoesNotThrow(key::destroy);
		assertTrue(key.isDestroyed());
		assertTrue(pair.getPrivate().isDestroyed());
		assertFalse(verifies("ML-DSA", pair.getPrivate(), pair.getPublic()));
	}
	
	private static final class TestPrivateKey implements PrivateKey {
		
		private final String algorithm;
		private final byte @Nullable [] encoded;
		private final @Nullable DestroyFailedException failure;
		private boolean destroyed;
		
		private TestPrivateKey(String algorithm, byte @Nullable [] encoded, @Nullable DestroyFailedException failure) {
			this.algorithm = algorithm;
			this.encoded = encoded;
			this.failure = failure;
		}
		
		@Override
		public String getAlgorithm() {
			return this.algorithm;
		}
		
		@Override
		public String getFormat() {
			return "RAW";
		}
		
		@Override
		public byte[] getEncoded() {
			return this.encoded;
		}
		
		@Override
		public void destroy() throws DestroyFailedException {
			if (this.failure != null) {
				throw this.failure;
			}
			this.destroyed = true;
		}
		
		@Override
		public boolean isDestroyed() {
			return this.destroyed;
		}
	}
}
