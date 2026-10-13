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

package net.luis.utils.crypto.cert;

import net.luis.utils.crypto.Providers;
import net.luis.utils.crypto.Signatures;
import net.luis.utils.crypto.algorithm.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ChainValidationResult}.<br>
 *
 * @author Luis-St
 */
class ChainValidationResultTest {
	
	private static X509Certificate anchor;
	
	@BeforeAll
	static void setUp() {
		Providers.installBouncyCastle();
		anchor = Certificates.builder().subject(DistinguishedName.ofCommonName("LUtils Test Anchor")).selfSigned(Signatures.generateKeyPair(SignatureAlgorithm.ED25519))
			.validity(Instant.now(), Duration.ofDays(1)).ca(0).build(SignatureAlgorithm.ED25519);
	}
	
	private static String describe(ChainValidationResult result) {
		return switch (result) {
			case ChainValidationResult.Valid valid -> "valid " + valid.anchor().getSubjectX500Principal().getName();
			case ChainValidationResult.Invalid invalid -> "invalid " + invalid.index() + " " + invalid.reason();
		};
	}
	
	@Test
	void constructValid() {
		ChainValidationResult.Valid valid = new ChainValidationResult.Valid(anchor);
		assertSame(anchor, valid.anchor());
	}
	
	@Test
	void constructValidWithNullAnchor() {
		assertEquals("Anchor must not be null", assertThrows(NullPointerException.class, () -> new ChainValidationResult.Valid(null)).getMessage());
	}
	
	@Test
	void constructInvalid() {
		ChainValidationResult.Invalid invalid = new ChainValidationResult.Invalid("Signature mismatch", 1);
		assertEquals("Signature mismatch", invalid.reason());
		assertEquals(1, invalid.index());
	}
	
	@Test
	void constructInvalidWithNullReason() {
		assertEquals("Reason must not be null", assertThrows(NullPointerException.class, () -> new ChainValidationResult.Invalid(null, 0)).getMessage());
	}
	
	@Test
	void constructInvalidWithIndexBelowMinusOne() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ChainValidationResult.Invalid("reason", -2));
		assertTrue(exception.getMessage().contains("-2"));
		assertThrows(IllegalArgumentException.class, () -> new ChainValidationResult.Invalid("reason", Integer.MIN_VALUE));
	}
	
	@Test
	void constructInvalidWithMinusOne() {
		ChainValidationResult.Invalid invalid = new ChainValidationResult.Invalid("Chain does not end in a trust anchor", -1);
		assertEquals(-1, invalid.index());
	}
	
	@Test
	void constructInvalidWithChainIndex() {
		assertEquals(0, new ChainValidationResult.Invalid("reason", 0).index());
		assertEquals(Integer.MAX_VALUE, new ChainValidationResult.Invalid("reason", Integer.MAX_VALUE).index());
	}
	
	@Test
	void isValidForValid() {
		assertTrue(new ChainValidationResult.Valid(anchor).isValid());
	}
	
	@Test
	void isValidForInvalid() {
		assertFalse(new ChainValidationResult.Invalid("reason", 0).isValid());
		assertFalse(new ChainValidationResult.Invalid("reason", -1).isValid());
	}
	
	@Test
	void constructInvalidWithEmptyReason() {
		ChainValidationResult.Invalid invalid = assertDoesNotThrow(() -> new ChainValidationResult.Invalid("", 0));
		assertEquals("", invalid.reason());
	}
	
	@Test
	void validEquality() {
		ChainValidationResult.Valid first = new ChainValidationResult.Valid(anchor);
		ChainValidationResult.Valid second = new ChainValidationResult.Valid(anchor);
		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
	}
	
	@Test
	void invalidEquality() {
		ChainValidationResult.Invalid first = new ChainValidationResult.Invalid("reason", 2);
		assertEquals(first, new ChainValidationResult.Invalid("reason", 2));
		assertEquals(first.hashCode(), new ChainValidationResult.Invalid("reason", 2).hashCode());
		assertNotEquals(first, new ChainValidationResult.Invalid("reason", 3));
		assertNotEquals(first, new ChainValidationResult.Invalid("other", 2));
	}
	
	@Test
	void patternMatchingOverResults() {
		List<ChainValidationResult> results = List.of(new ChainValidationResult.Valid(anchor), new ChainValidationResult.Invalid("expired", 0), new ChainValidationResult.Invalid("untrusted", -1));
		List<String> described = results.stream().map(ChainValidationResultTest::describe).toList();
		assertEquals("valid CN=LUtils Test Anchor", described.get(0));
		assertEquals("invalid 0 expired", described.get(1));
		assertEquals("invalid -1 untrusted", described.get(2));
		assertEquals(List.of(true, false, false), results.stream().map(ChainValidationResult::isValid).toList());
	}
}
