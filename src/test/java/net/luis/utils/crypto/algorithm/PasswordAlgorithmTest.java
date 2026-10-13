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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link PasswordAlgorithm}.<br>
 *
 * @author Luis-St
 */
class PasswordAlgorithmTest {
	
	@Test
	void byIdentifierWithNullIdentifier() {
		assertThrows(NullPointerException.class, () -> PasswordAlgorithm.byIdentifier(null));
	}
	
	@Test
	void readIntWithNullParameter() {
		assertThrows(NullPointerException.class, () -> PasswordAlgorithm.readInt(null, "m"));
	}
	
	@Test
	void readIntWithNullName() {
		assertThrows(NullPointerException.class, () -> PasswordAlgorithm.readInt("m=8", null));
	}
	
	@Test
	void readIntWithBothArgumentsNull() {
		NullPointerException exception = assertThrows(NullPointerException.class, () -> PasswordAlgorithm.readInt(null, null));
		assertEquals("Parameter must not be null", exception.getMessage());
	}
	
	@Test
	void readIntWithWrongName() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.readInt("t=3", "m"));
		assertTrue(exception.getMessage().contains("m"));
		assertTrue(exception.getMessage().contains("t=3"));
	}
	
	@Test
	void readIntWithNonIntegerValue() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.readInt("m=abc", "m"));
		assertTrue(exception.getMessage().contains("m"));
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}
	
	@Test
	void readIntWithEmptyValue() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.readInt("m=", "m"));
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}
	
	@Test
	void readIntWithOverflowingValue() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.readInt("m=99999999999", "m"));
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}
	
	@Test
	void valuesIsImmutable() {
		assertThrows(UnsupportedOperationException.class, () -> PasswordAlgorithm.VALUES.add(PasswordAlgorithm.ARGON2ID));
		assertThrows(UnsupportedOperationException.class, () -> PasswordAlgorithm.VALUES.remove(0));
		assertThrows(UnsupportedOperationException.class, () -> PasswordAlgorithm.VALUES.set(0, PasswordAlgorithm.SCRYPT));
	}
	
	@Test
	void byIdentifierFindsExistingFunction() {
		assertSame(PasswordAlgorithm.ARGON2ID, PasswordAlgorithm.byIdentifier("argon2id").orElseThrow());
	}
	
	@Test
	void byIdentifierWithUnknownIdentifier() {
		assertTrue(PasswordAlgorithm.byIdentifier("bcrypt").isEmpty());
	}
	
	@Test
	void byIdentifierWithEmptyIdentifier() {
		assertTrue(assertDoesNotThrow(() -> PasswordAlgorithm.byIdentifier("")).isEmpty());
	}
	
	@Test
	void byIdentifierIsCaseSensitive() {
		assertTrue(PasswordAlgorithm.byIdentifier("ARGON2ID").isEmpty());
	}
	
	@Test
	void byIdentifierResolvesEveryConstant() {
		for (PasswordAlgorithm algorithm : PasswordAlgorithm.VALUES) {
			assertSame(algorithm, PasswordAlgorithm.byIdentifier(algorithm.identifier()).orElseThrow());
		}
	}
	
	@Test
	void readIntWithMatchingName() {
		assertEquals(65_536, PasswordAlgorithm.readInt("m=65536", "m"));
	}
	
	@Test
	void readIntWithNameThatIsAPrefixOfAnother() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.readInt("parallelism=4", "p"));
	}
	
	@Test
	void requiresBouncyCastleBothValues() {
		assertTrue(PasswordAlgorithm.ARGON2ID.requiresBouncyCastle());
		assertTrue(PasswordAlgorithm.SCRYPT.requiresBouncyCastle());
		assertFalse(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.requiresBouncyCastle());
	}
	
	@Test
	void valuesContainsEveryConstant() {
		assertEquals(3, PasswordAlgorithm.VALUES.size());
		assertTrue(PasswordAlgorithm.VALUES.contains(PasswordAlgorithm.ARGON2ID));
		assertTrue(PasswordAlgorithm.VALUES.contains(PasswordAlgorithm.SCRYPT));
		assertTrue(PasswordAlgorithm.VALUES.contains(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
	}
	
	@Test
	void valuesOrderIsStrongestFirst() {
		assertEquals(List.of(PasswordAlgorithm.ARGON2ID, PasswordAlgorithm.SCRYPT, PasswordAlgorithm.PBKDF2_HMAC_SHA_512), PasswordAlgorithm.VALUES);
	}
	
	@Test
	void identifierOfEveryConstant() {
		assertEquals("argon2id", PasswordAlgorithm.ARGON2ID.identifier());
		assertEquals("scrypt", PasswordAlgorithm.SCRYPT.identifier());
		assertEquals("pbkdf2-sha512", PasswordAlgorithm.PBKDF2_HMAC_SHA_512.identifier());
	}
	
	@Test
	void jcaNameOfEveryConstant() {
		assertEquals("ARGON2", PasswordAlgorithm.ARGON2ID.jcaName());
		assertEquals("SCRYPT", PasswordAlgorithm.SCRYPT.jcaName());
		assertEquals("PBKDF2WithHmacSHA512", PasswordAlgorithm.PBKDF2_HMAC_SHA_512.jcaName());
	}
	
	@Test
	void defaultCostsOfEveryConstant() {
		assertEquals(new Argon2PasswordAlgorithm(65_536, 3, 4), PasswordAlgorithm.ARGON2ID);
		assertEquals(new ScryptPasswordAlgorithm(65_536, 8, 1), PasswordAlgorithm.SCRYPT);
		assertEquals(new Pbkdf2PasswordAlgorithm(210_000), PasswordAlgorithm.PBKDF2_HMAC_SHA_512);
	}
	
	@Test
	void readIntWithZeroValue() {
		assertEquals(0, assertDoesNotThrow(() -> PasswordAlgorithm.readInt("i=0", "i")));
	}
	
	@Test
	void readIntWithNegativeValue() {
		assertEquals(-5, PasswordAlgorithm.readInt("t=-5", "t"));
	}
	
	@Test
	void readIntWithBoundaryValues() {
		assertEquals(Integer.MAX_VALUE, PasswordAlgorithm.readInt("i=" + Integer.MAX_VALUE, "i"));
		assertEquals(Integer.MIN_VALUE, PasswordAlgorithm.readInt("i=" + Integer.MIN_VALUE, "i"));
	}
	
	@Test
	void identifiersAreUniqueAcrossValues() {
		Set<String> identifiers = new HashSet<>();
		for (PasswordAlgorithm algorithm : PasswordAlgorithm.VALUES) {
			assertFalse(algorithm.identifier().isBlank());
			assertTrue(identifiers.add(algorithm.identifier()));
		}
		assertEquals(3, identifiers.size());
	}
	
	@Test
	void encodeParametersRoundTripsThroughParseParameters() {
		for (PasswordAlgorithm algorithm : PasswordAlgorithm.VALUES) {
			String[] sections = algorithm.encodeParameters().split("\\$", -1);
			assertEquals(algorithm, algorithm.parseParameters(sections));
		}
		assertEquals(2, PasswordAlgorithm.ARGON2ID.encodeParameters().split("\\$", -1).length);
		assertEquals(1, PasswordAlgorithm.SCRYPT.encodeParameters().split("\\$", -1).length);
	}
	
	@Test
	void readIntWithEmbeddedEqualsInValue() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.readInt("m=8=9", "m"));
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}
	
	@Test
	void readIntWithWhitespace() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.readInt("m= 8", "m"));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.readInt("m=8 ", "m"));
	}
	
	@Test
	void readIntWithPlusSignedValue() {
		assertEquals(8, PasswordAlgorithm.readInt("m=+8", "m"));
	}
	
	@Test
	void sealedHierarchyPermitsThreeVariants() {
		assertTrue(PasswordAlgorithm.class.isSealed());
		assertEquals(Set.of(Argon2PasswordAlgorithm.class, ScryptPasswordAlgorithm.class, Pbkdf2PasswordAlgorithm.class),
			Set.of(PasswordAlgorithm.class.getPermittedSubclasses()));
		for (PasswordAlgorithm algorithm : PasswordAlgorithm.VALUES) {
			assertTrue(algorithm instanceof Argon2PasswordAlgorithm || algorithm instanceof ScryptPasswordAlgorithm || algorithm instanceof Pbkdf2PasswordAlgorithm);
		}
	}
	
	@Test
	void isWeakerThanAcrossFunctionFamilies() {
		assertFalse(PasswordAlgorithm.SCRYPT.isWeakerThan(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
		assertTrue(PasswordAlgorithm.SCRYPT.isWeakerThan(PasswordAlgorithm.ARGON2ID));
		assertTrue(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.isWeakerThan(PasswordAlgorithm.ARGON2ID));
		assertTrue(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.isWeakerThan(PasswordAlgorithm.SCRYPT));
		
		assertFalse(PasswordAlgorithm.ARGON2ID.isWeakerThan(PasswordAlgorithm.ARGON2ID));
		assertFalse(PasswordAlgorithm.SCRYPT.isWeakerThan(PasswordAlgorithm.SCRYPT));
		assertFalse(PasswordAlgorithm.PBKDF2_HMAC_SHA_512.isWeakerThan(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
		
		assertTrue(PasswordAlgorithm.ARGON2ID.isWeakerThan(PasswordAlgorithm.SCRYPT));
		assertTrue(PasswordAlgorithm.ARGON2ID.isWeakerThan(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
	}
}
