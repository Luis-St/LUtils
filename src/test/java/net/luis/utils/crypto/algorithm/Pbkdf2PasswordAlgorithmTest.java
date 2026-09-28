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

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Pbkdf2PasswordAlgorithm}.<br>
 *
 * @author Luis-St
 */
class Pbkdf2PasswordAlgorithmTest {
	
	@Test
	void constructPbkdf2Algorithm() {
		Pbkdf2PasswordAlgorithm algorithm = new Pbkdf2PasswordAlgorithm(210_000);
		assertEquals(210_000, algorithm.iterations());
		assertEquals(PasswordAlgorithm.PBKDF2_HMAC_SHA_512, algorithm);
	}
	
	@Test
	void constructWithIterationsBelowMinimum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Pbkdf2PasswordAlgorithm(999));
		assertTrue(exception.getMessage().contains("999"));
		assertTrue(exception.getMessage().contains("1000"));
	}
	
	@Test
	void constructWithIterationsAboveMaximum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Pbkdf2PasswordAlgorithm(10_000_001));
		assertTrue(exception.getMessage().contains("10000001"));
	}
	
	@Test
	void constructWithZeroIterations() {
		assertThrows(IllegalArgumentException.class, () -> new Pbkdf2PasswordAlgorithm(0));
	}
	
	@Test
	void constructWithNegativeIterations() {
		assertThrows(IllegalArgumentException.class, () -> new Pbkdf2PasswordAlgorithm(-1));
	}
	
	@Test
	void constructWithIntegerMaxIterations() {
		assertThrows(IllegalArgumentException.class, () -> new Pbkdf2PasswordAlgorithm(Integer.MAX_VALUE));
	}
	
	@Test
	void isWeakerThanWithNullCurrent() {
		assertThrows(NullPointerException.class, () -> PasswordAlgorithm.PBKDF2_HMAC_SHA_512.isWeakerThan(null));
	}
	
	@Test
	void parseParametersWithNullParameters() {
		assertThrows(NullPointerException.class, () -> PasswordAlgorithm.PBKDF2_HMAC_SHA_512.parseParameters(null));
	}
	
	@Test
	void parseParametersWithWrongSectionCount() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.PBKDF2_HMAC_SHA_512.parseParameters(new String[0]));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.PBKDF2_HMAC_SHA_512.parseParameters(new String[] { "i=210000", "x=1" }));
	}
	
	@Test
	void parseParametersWithWrongParameterName() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> PasswordAlgorithm.PBKDF2_HMAC_SHA_512.parseParameters(new String[] { "t=210000" }));
		assertTrue(exception.getMessage().contains("i"));
	}
	
	@Test
	void parseParametersWithNonIntegerValue() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> PasswordAlgorithm.PBKDF2_HMAC_SHA_512.parseParameters(new String[] { "i=abc" }));
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}
	
	@Test
	void parseParametersWithOutOfRangeIterations() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.PBKDF2_HMAC_SHA_512.parseParameters(new String[] { "i=1" }));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.PBKDF2_HMAC_SHA_512.parseParameters(new String[] { "i=999999999" }));
	}
	
	@Test
	void constructAtMinimumIterations() {
		Pbkdf2PasswordAlgorithm algorithm = assertDoesNotThrow(() -> new Pbkdf2PasswordAlgorithm(Pbkdf2PasswordAlgorithm.MIN_ITERATIONS));
		assertEquals(1000, algorithm.iterations());
	}
	
	@Test
	void constructAtMaximumIterations() {
		Pbkdf2PasswordAlgorithm algorithm = assertDoesNotThrow(() -> new Pbkdf2PasswordAlgorithm(Pbkdf2PasswordAlgorithm.MAX_ITERATIONS));
		assertEquals(10_000_000, algorithm.iterations());
	}
	
	@Test
	void isWeakerThanNonPbkdf2Algorithm() {
		assertTrue(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.isWeakerThan(PasswordAlgorithm.ARGON2ID));
		assertTrue(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.isWeakerThan(PasswordAlgorithm.SCRYPT));
	}
	
	@Test
	void isWeakerThanLowerIterationCount() {
		assertTrue(new Pbkdf2PasswordAlgorithm(100_000).isWeakerThan(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
	}
	
	@Test
	void isWeakerThanEqualIterationCount() {
		assertFalse(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.isWeakerThan(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
	}
	
	@Test
	void isWeakerThanHigherIterationCount() {
		assertFalse(new Pbkdf2PasswordAlgorithm(500_000).isWeakerThan(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
	}
	
	@Test
	void parseParametersWithValidRecord() {
		assertEquals(PasswordAlgorithm.PBKDF2_HMAC_SHA_512, PasswordAlgorithm.PBKDF2_HMAC_SHA_512.parseParameters(new String[] { "i=210000" }));
	}
	
	@Test
	void identifierIsPbkdf2Sha512() {
		assertEquals("pbkdf2-sha512", PasswordAlgorithm.PBKDF2_HMAC_SHA_512.identifier());
		assertEquals("pbkdf2-sha512", new Pbkdf2PasswordAlgorithm(1000).identifier());
	}
	
	@Test
	void jcaNameIsPbkdf2WithHmacSha512() {
		assertEquals("PBKDF2WithHmacSHA512", PasswordAlgorithm.PBKDF2_HMAC_SHA_512.jcaName());
	}
	
	@Test
	void requiresBouncyCastleAlwaysFalse() {
		assertFalse(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.requiresBouncyCastle());
		assertFalse(new Pbkdf2PasswordAlgorithm(1000).requiresBouncyCastle());
		for (PasswordAlgorithm algorithm : PasswordAlgorithm.VALUES) {
			if (algorithm != PasswordAlgorithm.PBKDF2_HMAC_SHA_512) {
				assertTrue(algorithm.requiresBouncyCastle());
			}
		}
	}
	
	@Test
	void encodeParametersOfDefault() {
		assertEquals("i=210000", PasswordAlgorithm.PBKDF2_HMAC_SHA_512.encodeParameters());
	}
	
	@Test
	void encodeParametersAtBounds() {
		assertEquals("i=1000", new Pbkdf2PasswordAlgorithm(1000).encodeParameters());
		assertEquals("i=10000000", new Pbkdf2PasswordAlgorithm(10_000_000).encodeParameters());
	}
	
	@Test
	void encodeParametersDoesNotContainDollarOrComma() {
		String encoded = PasswordAlgorithm.PBKDF2_HMAC_SHA_512.encodeParameters();
		assertFalse(encoded.contains("$"));
		assertFalse(encoded.contains(","));
		assertEquals(1, encoded.split("\\$", -1).length);
	}
	
	@Test
	void boundConstantsAreOrdered() {
		assertTrue(Pbkdf2PasswordAlgorithm.MIN_ITERATIONS < Pbkdf2PasswordAlgorithm.MAX_ITERATIONS);
		assertTrue(Pbkdf2PasswordAlgorithm.MIN_ITERATIONS > 0);
		assertEquals(1000, Pbkdf2PasswordAlgorithm.MIN_ITERATIONS);
		assertEquals(10_000_000, Pbkdf2PasswordAlgorithm.MAX_ITERATIONS);
	}
	
	@Test
	void defaultMatchesOwaspFigure() {
		assertEquals(210_000, PasswordAlgorithm.PBKDF2_HMAC_SHA_512.iterations());
		assertTrue(210_000 >= Pbkdf2PasswordAlgorithm.MIN_ITERATIONS);
		assertTrue(210_000 <= Pbkdf2PasswordAlgorithm.MAX_ITERATIONS);
	}
	
	@Test
	void isPasswordAlgorithm() {
		assertInstanceOf(PasswordAlgorithm.class, PasswordAlgorithm.PBKDF2_HMAC_SHA_512);
	}
	
	@Test
	void encodeParametersRoundTrip() {
		for (int iterations : new int[] { 1000, 210_000, 10_000_000 }) {
			Pbkdf2PasswordAlgorithm algorithm = new Pbkdf2PasswordAlgorithm(iterations);
			String[] sections = algorithm.encodeParameters().split("\\$", -1);
			assertEquals(1, sections.length);
			assertEquals(algorithm, algorithm.parseParameters(sections));
		}
	}
	
	@Test
	void equalsAndHashCodeOverIterations() {
		assertEquals(new Pbkdf2PasswordAlgorithm(1000), new Pbkdf2PasswordAlgorithm(1000));
		assertEquals(new Pbkdf2PasswordAlgorithm(1000).hashCode(), new Pbkdf2PasswordAlgorithm(1000).hashCode());
		assertNotEquals(new Pbkdf2PasswordAlgorithm(1000), new Pbkdf2PasswordAlgorithm(2000));
	}
	
	@Test
	void toStringContainsIterations() {
		String string = new Pbkdf2PasswordAlgorithm(1000).toString();
		assertTrue(string.contains("Pbkdf2PasswordAlgorithm"));
		assertTrue(string.contains("1000"));
	}
	
	@Test
	void parseParametersIgnoresReceiverIterations() {
		PasswordAlgorithm parsed = new Pbkdf2PasswordAlgorithm(1000).parseParameters(new String[] { "i=210000" });
		assertEquals(PasswordAlgorithm.PBKDF2_HMAC_SHA_512, parsed);
	}
	
	@Test
	void parseParametersWithPlusSignedValue() {
		assertEquals(PasswordAlgorithm.PBKDF2_HMAC_SHA_512, PasswordAlgorithm.PBKDF2_HMAC_SHA_512.parseParameters(new String[] { "i=+210000" }));
	}
	
	@Test
	void isWeakerThanOrderingAgainstOtherFamilies() {
		assertTrue(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.isWeakerThan(PasswordAlgorithm.ARGON2ID));
		assertTrue(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.isWeakerThan(PasswordAlgorithm.SCRYPT));
		assertFalse(PasswordAlgorithm.SCRYPT.isWeakerThan(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
	}
	
	@Test
	void deriveWithJdkProvider() throws Exception {
		SecretKeyFactory factory = assertDoesNotThrow(() -> SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512"));
		PBEKeySpec spec = new PBEKeySpec("password".toCharArray(), "salt".getBytes(), 1000, 512);
		assertEquals(64, factory.generateSecret(spec).getEncoded().length);
	}
	
	@Test
	void deriveKnownAnswerVector() throws Exception {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
		PBEKeySpec spec = new PBEKeySpec("password".toCharArray(), "salt".getBytes(), 1000, 512);
		String derived = HexFormat.of().formatHex(factory.generateSecret(spec).getEncoded());
		assertEquals("afe6c5530785b6cc6b1c6453384731bd5ee432ee549fd42fb6695779ad8a1c5bf59de69c48f774efc4007d5298f9033c0241d5ab69305e7b64eceeb8d834cfec", derived);
		
		SecretKeyFactory sha256 = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		String other = HexFormat.of().formatHex(sha256.generateSecret(new PBEKeySpec("password".toCharArray(), "salt".getBytes(), 1000, 512)).getEncoded());
		assertNotEquals(derived, other);
	}
}
