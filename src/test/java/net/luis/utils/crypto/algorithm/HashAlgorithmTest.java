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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link HashAlgorithm}.<br>
 *
 * @author Luis-St
 */
class HashAlgorithmTest {
	
	private static final byte[] ABC = "abc".getBytes(StandardCharsets.UTF_8);
	private static final List<Vector> VECTORS = List.of(
		new Vector(HashAlgorithm.SHA_256, "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"),
		new Vector(HashAlgorithm.SHA_384, "cb00753f45a35e8bb5a03d699ac65007272c32ab0eded1631a8b605a43ff5bed8086072ba1e7cc2358baeca134c825a7", "38b060a751ac96384cd9327eb1b1e36a21fdb71114be07434c0cc7bf63f6e1da274edebfe76f65fbd51ad2f14898b95b"),
		new Vector(HashAlgorithm.SHA_512, "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f", "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"),
		new Vector(HashAlgorithm.SHA_512_256, "53048e2681941ef99b2e29b76b4c7dabe4c2d0c634fc6d46e0e2f13107e7af23", "c672b8d1ef56ed28ab87c3622c5114069bdd3ad7b8f9737498d0c01ecef0967a"),
		new Vector(HashAlgorithm.SHA3_256, "3a985da74fe225b2045c172d6bd390bd855f086e3e9d525b46bfe24511431532", "a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a"),
		new Vector(HashAlgorithm.SHA3_384, "ec01498288516fc926459f58e2c6ad8df9b473cb0fc08c2596da7cf0e49be4b298d88cea927ac7f539f1edf228376d25", "0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004"),
		new Vector(HashAlgorithm.SHA3_512, "b751850b1a57168a5693cd924b6b096e08f621827444f70d884f5d0240d2712e10e116e9192af3c91a7ec57647e3934057340b4cf408d5a56592f8274eec53f0", "a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26"),
		new Vector(HashAlgorithm.SHA_1, "a9993e364706816aba3e25717850c26c9cd0d89d", "da39a3ee5e6b4b0d3255bfef95601890afd80709"),
		new Vector(HashAlgorithm.MD5, "900150983cd24fb0d6963f7d28e17f72", "d41d8cd98f00b204e9800998ecf8427e")
	);
	
	@Test
	void digestResolvesForEveryConstant() {
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			MessageDigest digest = assertDoesNotThrow(algorithm::digest);
			assertNotNull(digest);
			assertEquals(algorithm.jcaName(), digest.getAlgorithm());
		}
	}
	
	@Test
	void digestReturnsIndependentInstances() {
		MessageDigest first = HashAlgorithm.SHA_256.digest();
		MessageDigest second = HashAlgorithm.SHA_256.digest();
		assertNotSame(first, second);
		
		first.update(ABC);
		assertArrayEquals(second.digest(), HashAlgorithm.SHA_256.digest().digest(new byte[0]));
		assertFalse(Arrays.equals(first.digest(), second.digest()));
	}
	
	@Test
	void jcaNameMatchesForEveryConstant() {
		assertEquals("SHA-256", HashAlgorithm.SHA_256.jcaName());
		assertEquals("SHA-384", HashAlgorithm.SHA_384.jcaName());
		assertEquals("SHA-512", HashAlgorithm.SHA_512.jcaName());
		assertEquals("SHA-512/256", HashAlgorithm.SHA_512_256.jcaName());
		assertEquals("SHA3-256", HashAlgorithm.SHA3_256.jcaName());
		assertEquals("SHA3-384", HashAlgorithm.SHA3_384.jcaName());
		assertEquals("SHA3-512", HashAlgorithm.SHA3_512.jcaName());
		assertEquals("SHA-1", HashAlgorithm.SHA_1.jcaName());
		assertEquals("MD5", HashAlgorithm.MD5.jcaName());
		
		Set<String> names = new HashSet<>();
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertFalse(algorithm.jcaName().isBlank());
			assertTrue(names.add(algorithm.jcaName()));
		}
	}
	
	@Test
	void digestLengthMatchesActualDigest() {
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertEquals(algorithm.digestLength(), algorithm.digest().digest(new byte[0]).length);
		}
	}
	
	@Test
	void blockLengthValues() {
		assertEquals(64, HashAlgorithm.SHA_256.blockLength());
		assertEquals(64, HashAlgorithm.SHA_1.blockLength());
		assertEquals(64, HashAlgorithm.MD5.blockLength());
		assertEquals(128, HashAlgorithm.SHA_384.blockLength());
		assertEquals(128, HashAlgorithm.SHA_512.blockLength());
		assertEquals(128, HashAlgorithm.SHA_512_256.blockLength());
		assertEquals(136, HashAlgorithm.SHA3_256.blockLength());
		assertEquals(104, HashAlgorithm.SHA3_384.blockLength());
		assertEquals(72, HashAlgorithm.SHA3_512.blockLength());
	}
	
	@Test
	void digestLengthIsPositiveForEveryConstant() {
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertTrue(algorithm.digestLength() > 0);
			assertTrue(algorithm.blockLength() > 0);
		}
	}
	
	@Test
	void digestKnownAnswerVectors() {
		assertEquals(HashAlgorithm.values().length, VECTORS.size());
		HexFormat hex = HexFormat.of();
		for (Vector vector : VECTORS) {
			assertEquals(vector.abcDigest(), hex.formatHex(vector.algorithm().digest().digest(ABC)));
			assertEquals(vector.emptyDigest(), hex.formatHex(vector.algorithm().digest().digest(new byte[0])));
		}
	}
	
	@Test
	void digestDistinctPerAlgorithm() {
		Set<String> names = new HashSet<>();
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			assertTrue(names.add(algorithm.jcaName()));
		}
		
		byte[] sha256 = HashAlgorithm.SHA_256.digest().digest(ABC);
		byte[] sha512256 = HashAlgorithm.SHA_512_256.digest().digest(ABC);
		byte[] sha3256 = HashAlgorithm.SHA3_256.digest().digest(ABC);
		assertFalse(Arrays.equals(sha256, sha512256));
		assertFalse(Arrays.equals(sha256, sha3256));
		assertFalse(Arrays.equals(sha512256, sha3256));
	}
	
	@Test
	void brokenAlgorithmsDeclaredLast() {
		int count = HashAlgorithm.values().length;
		assertEquals(count - 1, HashAlgorithm.MD5.ordinal());
		assertEquals(count - 2, HashAlgorithm.SHA_1.ordinal());
		for (HashAlgorithm algorithm : HashAlgorithm.values()) {
			if (algorithm != HashAlgorithm.SHA_1 && algorithm != HashAlgorithm.MD5) {
				assertTrue(algorithm.ordinal() < HashAlgorithm.SHA_1.ordinal());
			}
		}
	}
	
	@Test
	void digestStateResetAfterDigestCall() {
		MessageDigest digest = HashAlgorithm.SHA_256.digest();
		byte[] first = digest.digest(ABC);
		byte[] second = digest.digest(ABC);
		assertArrayEquals(first, second);
		assertArrayEquals(first, HashAlgorithm.SHA_256.digest().digest(ABC));
	}
	
	private record Vector(@NonNull HashAlgorithm algorithm, @NonNull String abcDigest, @NonNull String emptyDigest) {}
}
