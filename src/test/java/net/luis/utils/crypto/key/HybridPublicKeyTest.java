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

import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HybridPublicKey}.<br>
 *
 * @author Luis-St
 */
class HybridPublicKeyTest {
	
	private static KeyPair ed25519;
	private static KeyPair mlDsa65;
	private static KeyPair mlDsa87;
	private static KeyPair ed448;
	private static KeyPair x25519;
	
	@BeforeAll
	static void setUp() throws Exception {
		ed25519 = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
		ed448 = KeyPairGenerator.getInstance("Ed448").generateKeyPair();
		x25519 = KeyPairGenerator.getInstance("X25519").generateKeyPair();
		mlDsa65 = KeyPairGenerator.getInstance("ML-DSA-65").generateKeyPair();
		mlDsa87 = KeyPairGenerator.getInstance("ML-DSA-87").generateKeyPair();
	}
	
	@Test
	void constructHybridPublicKey() {
		HybridPublicKey key = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		assertSame(ed25519.getPublic(), key.classical());
		assertSame(mlDsa65.getPublic(), key.postQuantum());
	}
	
	@Test
	void constructWithNullClassical() {
		assertThrows(NullPointerException.class, () -> new HybridPublicKey(null, mlDsa65.getPublic()));
	}
	
	@Test
	void constructWithNullPostQuantum() {
		assertThrows(NullPointerException.class, () -> new HybridPublicKey(ed25519.getPublic(), null));
	}
	
	@Test
	void constructWithBothComponentsNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> new HybridPublicKey(null, null));
		assertEquals("Classical component must not be null", exception.getMessage());
	}
	
	@Test
	void getEncodedWithComponentWithoutEncoding() {
		HybridPublicKey classicalMissing = new HybridPublicKey(new TestPublicKey("HSM", null), mlDsa65.getPublic());
		assertThrows(NullPointerException.class, classicalMissing::getEncoded);
		
		HybridPublicKey postQuantumMissing = new HybridPublicKey(ed25519.getPublic(), new TestPublicKey("HSM", null));
		assertThrows(NullPointerException.class, postQuantumMissing::getEncoded);
	}
	
	@Test
	void getFormatIsRawHybrid() {
		assertEquals("RAW-HYBRID", new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic()).getFormat());
		assertEquals("RAW-HYBRID", new HybridPublicKey(new TestPublicKey("a", new byte[0]), new TestPublicKey("b", new byte[0])).getFormat());
	}
	
	@Test
	void getAlgorithmJoinsComponentAlgorithms() {
		HybridPublicKey key = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		assertEquals("Hybrid(" + ed25519.getPublic().getAlgorithm() + "+" + mlDsa65.getPublic().getAlgorithm() + ")", key.getAlgorithm());
		assertEquals("Hybrid(EdDSA+ML-DSA)", key.getAlgorithm());
	}
	
	@Test
	void getAlgorithmReflectsComponentsGenerically() {
		HybridPublicKey key = new HybridPublicKey(new TestPublicKey("a", new byte[0]), new TestPublicKey("b", new byte[0]));
		assertEquals("Hybrid(a+b)", key.getAlgorithm());
	}
	
	@Test
	void getEncodedLayout() {
		byte[] first = ed25519.getPublic().getEncoded();
		byte[] second = mlDsa65.getPublic().getEncoded();
		byte[] encoded = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic()).getEncoded();
		
		assertEquals(8 + first.length + second.length, encoded.length);
		assertArrayEquals(first, Arrays.copyOfRange(encoded, 4, 4 + first.length));
		assertArrayEquals(second, Arrays.copyOfRange(encoded, 8 + first.length, encoded.length));
	}
	
	@Test
	void getEncodedLengthsAreBigEndian() {
		byte[] first = ed25519.getPublic().getEncoded();
		byte[] second = mlDsa65.getPublic().getEncoded();
		byte[] encoded = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic()).getEncoded();
		
		assertEquals(first.length, ByteBuffer.wrap(encoded, 0, 4).getInt());
		assertEquals(second.length, ByteBuffer.wrap(encoded, 4 + first.length, 4).getInt());
	}
	
	@Test
	void getEncodedReturnsFreshArrays() {
		HybridPublicKey key = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		byte[] first = key.getEncoded();
		byte[] second = key.getEncoded();
		
		assertNotSame(first, second);
		assertArrayEquals(first, second);
		first[0] ^= 1;
		assertArrayEquals(second, key.getEncoded());
	}
	
	@Test
	void classicalAndPostQuantumAccessorsAreNotSwapped() {
		HybridPublicKey key = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		byte[] first = ed25519.getPublic().getEncoded();
		
		assertSame(ed25519.getPublic(), key.classical());
		assertSame(mlDsa65.getPublic(), key.postQuantum());
		assertArrayEquals(first, Arrays.copyOfRange(key.getEncoded(), 4, 4 + first.length));
	}
	
	@Test
	void isPublicKey() {
		HybridPublicKey key = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		assertInstanceOf(PublicKey.class, key);
		assertInstanceOf(Key.class, key);
	}
	
	@Test
	void getEncodedIsDecodableBackToComponents() throws Exception {
		HybridPublicKey key = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		byte[] encoded = key.getEncoded();
		
		ByteBuffer buffer = ByteBuffer.wrap(encoded);
		byte[] first = new byte[buffer.getInt()];
		buffer.get(first);
		byte[] second = new byte[buffer.getInt()];
		buffer.get(second);
		
		PublicKey classical = KeyFactory.getInstance("EdDSA").generatePublic(new X509EncodedKeySpec(first));
		PublicKey postQuantum = KeyFactory.getInstance("ML-DSA").generatePublic(new X509EncodedKeySpec(second));
		assertEquals(ed25519.getPublic(), classical);
		assertEquals(mlDsa65.getPublic(), postQuantum);
		assertEquals(key, new HybridPublicKey(classical, postQuantum));
	}
	
	@Test
	void getEncodedDistinguishesSwappedComponents() {
		HybridPublicKey first = new HybridPublicKey(ed25519.getPublic(), x25519.getPublic());
		HybridPublicKey second = new HybridPublicKey(x25519.getPublic(), ed25519.getPublic());
		
		assertFalse(Arrays.equals(first.getEncoded(), second.getEncoded()));
		assertNotEquals(KeyId.of(first), KeyId.of(second));
	}
	
	@Test
	void keyIdIdentifiesThePairAsAWhole() {
		HybridPublicKey key = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		KeyId id = KeyId.of(key);
		
		assertEquals(id, KeyId.of(key));
		assertNotEquals(id, KeyId.of(new HybridPublicKey(ed448.getPublic(), mlDsa65.getPublic())));
		assertNotEquals(id, KeyId.of(new HybridPublicKey(ed25519.getPublic(), mlDsa87.getPublic())));
		assertNotEquals(id, KeyId.of(ed25519.getPublic()));
	}
	
	@Test
	void equalsAndHashCodeOverComponents() throws Exception {
		HybridPublicKey first = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		HybridPublicKey second = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new HybridPublicKey(mlDsa65.getPublic(), ed25519.getPublic()));
		
		PublicKey rebuilt = KeyFactory.getInstance("EdDSA").generatePublic(new X509EncodedKeySpec(ed25519.getPublic().getEncoded()));
		assertNotSame(ed25519.getPublic(), rebuilt);
		assertEquals(first, new HybridPublicKey(rebuilt, mlDsa65.getPublic()));
	}
	
	@Test
	void toStringContainsComponents() {
		HybridPublicKey key = new HybridPublicKey(new TestPublicKey("a", new byte[0]), new TestPublicKey("b", new byte[0]));
		assertTrue(key.toString().startsWith("HybridPublicKey["));
		assertTrue(key.toString().contains("classical="));
		assertTrue(key.toString().contains("postQuantum="));
	}
	
	@Test
	void getEncodedWithEmptyComponentEncoding() {
		HybridPublicKey key = new HybridPublicKey(new TestPublicKey("a", new byte[0]), new TestPublicKey("b", new byte[0]));
		byte[] encoded = key.getEncoded();
		
		assertEquals(8, encoded.length);
		assertArrayEquals(new byte[8], encoded);
	}
	
	@Test
	void getEncodedWithLargeComponents() {
		byte[] classical = ed448.getPublic().getEncoded();
		byte[] postQuantum = mlDsa87.getPublic().getEncoded();
		byte[] encoded = new HybridPublicKey(ed448.getPublic(), mlDsa87.getPublic()).getEncoded();
		
		assertEquals(69, classical.length);
		assertEquals(2614, postQuantum.length);
		assertEquals(8 + 69 + 2614, encoded.length);
		assertEquals(69, ByteBuffer.wrap(encoded, 0, 4).getInt());
		assertEquals(2614, ByteBuffer.wrap(encoded, 4 + 69, 4).getInt());
	}
	
	@Test
	void matchesHybridPrivateKeyLayout() {
		HybridPublicKey publicKey = new HybridPublicKey(ed25519.getPublic(), mlDsa65.getPublic());
		HybridPrivateKey privateKey = new HybridPrivateKey(ed25519.getPrivate(), mlDsa65.getPrivate());
		
		assertEquals(8 + ed25519.getPublic().getEncoded().length + mlDsa65.getPublic().getEncoded().length, publicKey.getEncoded().length);
		assertEquals(8 + ed25519.getPrivate().getEncoded().length + mlDsa65.getPrivate().getEncoded().length, privateKey.getEncoded().length);
		assertEquals(ed25519.getPrivate().getEncoded().length, ByteBuffer.wrap(privateKey.getEncoded(), 0, 4).getInt());
		assertEquals(publicKey.getFormat(), privateKey.getFormat());
		assertEquals(publicKey.getAlgorithm(), privateKey.getAlgorithm());
	}
	
	private record TestPublicKey(String algorithm, byte @Nullable [] encoded) implements PublicKey {
		
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
	}
}
