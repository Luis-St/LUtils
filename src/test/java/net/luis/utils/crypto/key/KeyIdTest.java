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

import net.luis.utils.crypto.exception.CryptoException;
import net.luis.utils.crypto.util.CryptoRandom;
import net.luis.utils.util.UUIDs;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.security.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link KeyId}.<br>
 *
 * @author Luis-St
 */
class KeyIdTest {
	
	private static final UUID VERSION_EIGHT = UUID.fromString("00000000-0000-8000-8000-000000000000");
	
	private static PublicKey ed25519;
	private static PublicKey ed448;
	private static PublicKey ecP256;
	private static PublicKey x25519;
	
	@BeforeAll
	static void setUp() throws Exception {
		ed25519 = KeyPairGenerator.getInstance("Ed25519").generateKeyPair().getPublic();
		ed448 = KeyPairGenerator.getInstance("Ed448").generateKeyPair().getPublic();
		x25519 = KeyPairGenerator.getInstance("X25519").generateKeyPair().getPublic();
		
		KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
		generator.initialize(256);
		ecP256 = generator.generateKeyPair().getPublic();
	}
	
	@Test
	void constructKeyId() {
		KeyId id = new KeyId(VERSION_EIGHT);
		assertSame(VERSION_EIGHT, id.value());
	}
	
	@Test
	void constructWithNullValue() {
		assertThrows(NullPointerException.class, () -> new KeyId(null));
	}
	
	@Test
	void ofPublicKeyWithNullKey() {
		assertThrows(NullPointerException.class, () -> KeyId.of((PublicKey) null));
	}
	
	@Test
	void ofEncodedKeyWithNullArray() {
		assertThrows(NullPointerException.class, () -> KeyId.of((byte[]) null));
	}
	
	@Test
	void ofPublicKeyWithoutEncodedForm() {
		CryptoException exception = assertThrows(CryptoException.class, () -> KeyId.of(new UnencodableKey("HSM", null)));
		assertTrue(exception.getMessage().contains("HSM"));
		assertTrue(exception.getMessage().contains("encoded form"));
	}
	
	@Test
	void fromBytesWithNullData() {
		assertThrows(NullPointerException.class, () -> KeyId.fromBytes(null));
	}
	
	@Test
	void fromBytesWithWrongLength() {
		assertTrue(assertThrows(IllegalArgumentException.class, () -> KeyId.fromBytes(new byte[15])).getMessage().contains("15"));
		assertTrue(assertThrows(IllegalArgumentException.class, () -> KeyId.fromBytes(new byte[17])).getMessage().contains("17"));
		assertTrue(assertThrows(IllegalArgumentException.class, () -> KeyId.fromBytes(new byte[0])).getMessage().contains("0"));
	}
	
	@Test
	void ofPublicKeyWithEncodedForm() {
		KeyId id = KeyId.of(ed25519);
		assertNotNull(id);
		assertEquals(KeyId.of(ed25519.getEncoded()), id);
	}
	
	@Test
	void ofEncodedKeyProducesVersionEightUuid() {
		KeyId id = KeyId.of(ed25519.getEncoded());
		assertEquals(8, id.value().version());
		assertEquals(2, id.value().variant());
	}
	
	@Test
	void fromBytesWithSixteenBytes() {
		byte[] data = CryptoRandom.bytes(16);
		KeyId id = assertDoesNotThrow(() -> KeyId.fromBytes(data));
		assertArrayEquals(data, id.toBytes());
	}
	
	@Test
	void ofEncodedKeyIsDeterministic() {
		byte[] encoded = ed25519.getEncoded();
		assertEquals(KeyId.of(encoded), KeyId.of(encoded));
		assertEquals(KeyId.of(encoded), KeyId.of(encoded.clone()));
	}
	
	@Test
	void ofEncodedKeyDiffersForDifferentKeys() {
		assertNotEquals(KeyId.of(ed25519.getEncoded()), KeyId.of(ed448.getEncoded()));
		
		byte[] flipped = ed25519.getEncoded();
		flipped[flipped.length - 1] ^= 1;
		assertNotEquals(KeyId.of(ed25519.getEncoded()), KeyId.of(flipped));
	}
	
	@Test
	void ofEmptyEncodedKey() {
		KeyId id = assertDoesNotThrow(() -> KeyId.of(new byte[0]));
		assertEquals(8, id.value().version());
		assertEquals(id, KeyId.of(new byte[0]));
	}
	
	@Test
	void ofSingleByteEncodedKey() {
		KeyId id = assertDoesNotThrow(() -> KeyId.of(new byte[] { 7 }));
		assertNotEquals(KeyId.of(new byte[0]), id);
	}
	
	@Test
	void toBytesIsSixteenBytes() {
		assertEquals(16, KeyId.of(ed25519).toBytes().length);
		assertEquals(16, new KeyId(VERSION_EIGHT).toBytes().length);
	}
	
	@Test
	void toBytesReturnsFreshArrays() {
		KeyId id = KeyId.of(ed25519);
		byte[] first = id.toBytes();
		byte[] second = id.toBytes();
		
		assertNotSame(first, second);
		assertArrayEquals(first, second);
		first[0] ^= 1;
		assertArrayEquals(second, id.toBytes());
	}
	
	@Test
	void toStringIsBareUuid() {
		KeyId id = KeyId.of(ed25519);
		assertEquals(id.value().toString(), id.toString());
		assertFalse(id.toString().contains("KeyId"));
		assertFalse(id.toString().contains("["));
	}
	
	@Test
	void valueReturnsComponent() {
		assertSame(VERSION_EIGHT, new KeyId(VERSION_EIGHT).value());
	}
	
	@Test
	void constructWithNonVersionEightUuid() {
		UUID version4 = UUID.randomUUID();
		KeyId id = assertDoesNotThrow(() -> new KeyId(version4));
		assertEquals(4, id.value().version());
	}
	
	@Test
	void bytesRoundTrip() {
		List<KeyId> ids = List.of(KeyId.of(ed25519), KeyId.of(ed448), KeyId.of(new byte[0]), new KeyId(UUID.randomUUID()), new KeyId(UUIDs.NIL), new KeyId(UUIDs.MAX));
		for (KeyId id : ids) {
			assertEquals(id, KeyId.fromBytes(id.toBytes()));
		}
	}
	
	@Test
	void ofEncodedKeyMatchesTruncatedSha256() throws Exception {
		byte[] encoded = ed25519.getEncoded();
		byte[] digest = MessageDigest.getInstance("SHA-256").digest(encoded);
		
		ByteBuffer buffer = ByteBuffer.wrap(digest, 0, 16);
		long most = (buffer.getLong() & 0xFFFF_FFFF_FFFF_0FFFL) | (8L << 12);
		long least = (buffer.getLong() & 0x3FFF_FFFF_FFFF_FFFFL) | 0x8000_0000_0000_0000L;
		
		assertEquals(new KeyId(new UUID(most, least)), KeyId.of(encoded));
	}
	
	@Test
	void ofEncodedKeyDoesNotUseMd5OrSha1() throws Exception {
		byte[] encoded = ed25519.getEncoded();
		KeyId id = KeyId.of(encoded);
		
		for (String algorithm : new String[] { "MD5", "SHA-1" }) {
			byte[] digest = MessageDigest.getInstance(algorithm).digest(encoded);
			assertNotEquals(new KeyId(UUIDs.v8(Arrays.copyOf(digest, 16))), id);
		}
		assertNotEquals(new KeyId(UUIDs.v3(UUIDs.NAMESPACE_DNS, encoded)), id);
		assertNotEquals(new KeyId(UUIDs.v5(UUIDs.NAMESPACE_DNS, encoded)), id);
	}
	
	@Test
	void ofPublicKeyForSeveralAlgorithms() {
		Set<KeyId> ids = new HashSet<>();
		for (PublicKey key : List.of(ed25519, ed448, ecP256, x25519)) {
			KeyId id = KeyId.of(key);
			assertEquals(8, id.value().version());
			assertEquals(id, KeyId.of(key));
			assertEquals(KeyId.of(key.getEncoded()), id);
			ids.add(id);
		}
		assertEquals(4, ids.size());
	}
	
	@Test
	void ofHybridPublicKeyIdentifiesThePair() {
		HybridPublicKey hybrid = new HybridPublicKey(ed25519, ed448);
		KeyId id = KeyId.of(hybrid);
		
		assertEquals(id, KeyId.of(hybrid));
		assertNotEquals(id, KeyId.of(new HybridPublicKey(x25519, ed448)));
		assertNotEquals(id, KeyId.of(new HybridPublicKey(ed25519, x25519)));
		assertNotEquals(id, KeyId.of(ed25519));
	}
	
	@Test
	void equalsAndHashCodeOverValue() {
		KeyId first = new KeyId(VERSION_EIGHT);
		KeyId second = new KeyId(UUID.fromString(VERSION_EIGHT.toString()));
		
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertNotEquals(first, new KeyId(UUIDs.NIL));
		assertNotEquals(VERSION_EIGHT, first);
	}
	
	@Test
	void idsAreDistinctAcrossManyKeys() {
		Set<KeyId> ids = new HashSet<>();
		for (int i = 0; i < 1000; i++) {
			ids.add(KeyId.of(CryptoRandom.bytes(32)));
		}
		assertEquals(1000, ids.size());
	}
	
	@Test
	void ofIsIndependentOfInputArrayIdentity() {
		byte[] encoded = ed25519.getEncoded();
		byte[] copy = encoded.clone();
		KeyId id = KeyId.of(encoded);
		
		assertArrayEquals(copy, encoded);
		encoded[0] ^= 1;
		assertEquals(new KeyId(id.value()), id);
		assertEquals(KeyId.of(copy), id);
	}
	
	@Test
	void fromBytesAcceptsArbitraryBytes() {
		byte[] zeros = new byte[16];
		byte[] ones = new byte[16];
		Arrays.fill(ones, (byte) 0xFF);
		
		assertArrayEquals(zeros, KeyId.fromBytes(zeros).toBytes());
		assertArrayEquals(ones, KeyId.fromBytes(ones).toBytes());
		assertEquals(0, KeyId.fromBytes(zeros).value().version());
		assertEquals(15, KeyId.fromBytes(ones).value().version());
	}
	
	@Test
	void fromBytesDoesNotAliasInput() {
		byte[] data = CryptoRandom.bytes(16);
		byte[] copy = data.clone();
		KeyId id = KeyId.fromBytes(data);
		
		data[0] ^= 1;
		assertArrayEquals(copy, id.toBytes());
	}
	
	private record UnencodableKey(String algorithm, byte @Nullable [] encoded) implements PublicKey {
		
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
