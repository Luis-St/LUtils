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

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link MacAlgorithm}.<br>
 *
 * @author Luis-St
 */
class MacAlgorithmTest {
	
	private static final byte[] RFC4231_KEY = new byte[20];
	private static final byte[] RFC4231_DATA = "Hi There".getBytes(StandardCharsets.UTF_8);
	private static final List<Vector> VECTORS = List.of(
		new Vector(MacAlgorithm.HMAC_SHA_256, "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7"),
		new Vector(MacAlgorithm.HMAC_SHA_384, "afd03944d84895626b0825f4ab46907f15f9dadbe4101ec682aa034c7cebc59cfaea9ea9076ede7f4af152e8b2fa9cb6"),
		new Vector(MacAlgorithm.HMAC_SHA_512, "87aa7cdea5ef619d4ff0b4241a1d6cb02379f4e2ce4ec2787ad0b30545e17cdedaa833b7d6b8a702038b274eaea3f4e4be9d914eeb61f1702e696c203a126854"),
		new Vector(MacAlgorithm.HMAC_SHA3_256, "ba85192310dffa96e2a3a40e69774351140bb7185e1202cdcc917589f95e16bb"),
		new Vector(MacAlgorithm.HMAC_SHA3_384, "68d2dcf7fd4ddd0a2240c8a437305f61fb7334cfb5d0226e1bc27dc10a2e723a20d370b47743130e26ac7e3d532886bd"),
		new Vector(MacAlgorithm.HMAC_SHA3_512, "eb3fbd4b2eaab8f5c504bd3a41465aacec15770a7cabac531e482f860b5ec7ba47ccb2c6f2afce8f88d22b6dc61380f23a668fd3888bb80537c0a0b86407689e")
	);
	
	private static byte[] key(@NonNull MacAlgorithm algorithm, int length) {
		byte[] raw = new byte[length];
		Arrays.fill(raw, (byte) 0x2a);
		return raw;
	}
	
	private static byte[] tag(@NonNull MacAlgorithm algorithm, byte[] rawKey, byte[] data) {
		Mac mac = algorithm.mac();
		assertDoesNotThrow(() -> mac.init(new SecretKeySpec(rawKey, algorithm.jcaName())));
		return mac.doFinal(data);
	}
	
	@Test
	void macUninitialisedThrowsOnUse() {
		assertThrows(IllegalStateException.class, () -> MacAlgorithm.HMAC_SHA_256.mac().doFinal(new byte[1]));
	}
	
	@Test
	void macResolvesForEveryConstant() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			Mac mac = assertDoesNotThrow(algorithm::mac);
			assertNotNull(mac);
			assertEquals(algorithm.jcaName(), mac.getAlgorithm());
		}
	}
	
	@Test
	void macReturnsIndependentInstances() {
		Mac first = MacAlgorithm.HMAC_SHA_256.mac();
		Mac second = MacAlgorithm.HMAC_SHA_256.mac();
		assertNotSame(first, second);
		
		assertDoesNotThrow(() -> first.init(new SecretKeySpec(RFC4231_KEY, "HmacSHA256")));
		assertThrows(IllegalStateException.class, () -> second.doFinal(new byte[1]));
	}
	
	@Test
	void macWithEmptyKey() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			Mac mac = algorithm.mac();
			assertThrows(IllegalArgumentException.class, () -> mac.init(new SecretKeySpec(new byte[0], algorithm.jcaName())));
		}
	}
	
	@Test
	void jcaNameMatchesForEveryConstant() {
		assertEquals("HmacSHA256", MacAlgorithm.HMAC_SHA_256.jcaName());
		assertEquals("HmacSHA384", MacAlgorithm.HMAC_SHA_384.jcaName());
		assertEquals("HmacSHA512", MacAlgorithm.HMAC_SHA_512.jcaName());
		assertEquals("HmacSHA3-256", MacAlgorithm.HMAC_SHA3_256.jcaName());
		assertEquals("HmacSHA3-384", MacAlgorithm.HMAC_SHA3_384.jcaName());
		assertEquals("HmacSHA3-512", MacAlgorithm.HMAC_SHA3_512.jcaName());
		
		Set<String> names = new HashSet<>();
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			assertFalse(algorithm.jcaName().isBlank());
			assertTrue(names.add(algorithm.jcaName()));
		}
	}
	
	@Test
	void tagLengthMatchesActualMacLength() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			Mac mac = algorithm.mac();
			assertDoesNotThrow(() -> mac.init(new SecretKeySpec(key(algorithm, algorithm.recommendedKeyLength()), algorithm.jcaName())));
			assertEquals(algorithm.tagLength(), mac.getMacLength());
			assertEquals(algorithm.tagLength(), mac.doFinal(new byte[0]).length);
		}
	}
	
	@Test
	void recommendedKeyLengthEqualsTagLength() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			assertEquals(algorithm.tagLength(), algorithm.recommendedKeyLength());
			assertTrue(algorithm.tagLength() > 0);
			assertTrue(algorithm.recommendedKeyLength() > 0);
		}
	}
	
	@Test
	void tagLengthValuesPerFamily() {
		assertEquals(32, MacAlgorithm.HMAC_SHA_256.tagLength());
		assertEquals(32, MacAlgorithm.HMAC_SHA3_256.tagLength());
		assertEquals(48, MacAlgorithm.HMAC_SHA_384.tagLength());
		assertEquals(48, MacAlgorithm.HMAC_SHA3_384.tagLength());
		assertEquals(64, MacAlgorithm.HMAC_SHA_512.tagLength());
		assertEquals(64, MacAlgorithm.HMAC_SHA3_512.tagLength());
	}
	
	@Test
	void macKnownAnswerVectors() {
		assertEquals(MacAlgorithm.values().length, VECTORS.size());
		HexFormat hex = HexFormat.of();
		for (Vector vector : VECTORS) {
			assertEquals(vector.tag(), hex.formatHex(tag(vector.algorithm(), RFC4231_KEY, RFC4231_DATA)));
		}
	}
	
	@Test
	void macAcceptsKeysOfAnyLength() {
		for (MacAlgorithm algorithm : MacAlgorithm.values()) {
			byte[] shortTag = tag(algorithm, key(algorithm, 1), RFC4231_DATA);
			byte[] normalTag = tag(algorithm, key(algorithm, algorithm.recommendedKeyLength()), RFC4231_DATA);
			byte[] longTag = tag(algorithm, key(algorithm, 200), RFC4231_DATA);
			assertEquals(algorithm.tagLength(), shortTag.length);
			assertEquals(algorithm.tagLength(), normalTag.length);
			assertEquals(algorithm.tagLength(), longTag.length);
			assertFalse(Arrays.equals(shortTag, normalTag));
			assertFalse(Arrays.equals(normalTag, longTag));
		}
	}
	
	@Test
	void macDistinctPerAlgorithm() {
		byte[] sha2 = tag(MacAlgorithm.HMAC_SHA_256, RFC4231_KEY, RFC4231_DATA);
		byte[] sha3 = tag(MacAlgorithm.HMAC_SHA3_256, RFC4231_KEY, RFC4231_DATA);
		assertEquals(sha2.length, sha3.length);
		assertFalse(Arrays.equals(sha2, sha3));
	}
	
	@Test
	void macReuseAfterDoFinal() {
		Mac mac = MacAlgorithm.HMAC_SHA_256.mac();
		assertDoesNotThrow(() -> mac.init(new SecretKeySpec(RFC4231_KEY, "HmacSHA256")));
		byte[] first = mac.doFinal(RFC4231_DATA);
		byte[] second = mac.doFinal(RFC4231_DATA);
		assertArrayEquals(first, second);
		
		mac.update(RFC4231_DATA, 0, 3);
		mac.update(RFC4231_DATA, 3, RFC4231_DATA.length - 3);
		assertArrayEquals(first, mac.doFinal());
	}
	
	static {
		Arrays.fill(RFC4231_KEY, (byte) 0x0b);
	}
	
	private record Vector(@NonNull MacAlgorithm algorithm, @NonNull String tag) {}
}
