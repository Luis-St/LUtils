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
import org.bouncycastle.jcajce.spec.Argon2KeySpec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKeyFactory;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link Argon2PasswordAlgorithm}.<br>
 *
 * @author Luis-St
 */
class Argon2PasswordAlgorithmTest {
	
	//region Setup
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	//endregion
	
	@Test
	void constructArgon2Algorithm() {
		Argon2PasswordAlgorithm algorithm = new Argon2PasswordAlgorithm(65_536, 3, 4);
		assertEquals(65_536, algorithm.memory());
		assertEquals(3, algorithm.iterations());
		assertEquals(4, algorithm.parallelism());
		assertEquals(PasswordAlgorithm.ARGON2ID, algorithm);
	}
	
	@Test
	void constructWithMemoryBelowMinimum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(7, 3, 1));
		assertTrue(exception.getMessage().contains("Memory"));
		assertTrue(exception.getMessage().contains("7"));
	}
	
	@Test
	void constructWithMemoryAboveMaximum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(Argon2PasswordAlgorithm.MAX_MEMORY + 1, 3, 1));
		assertTrue(exception.getMessage().contains("Memory"));
	}
	
	@Test
	void constructWithZeroMemory() {
		assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(0, 3, 1));
	}
	
	@Test
	void constructWithNegativeMemory() {
		assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(-1, 3, 1));
	}
	
	@Test
	void constructWithIterationsBelowMinimum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(65_536, 0, 1));
		assertTrue(exception.getMessage().contains("Iterations"));
	}
	
	@Test
	void constructWithIterationsAboveMaximum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(65_536, 101, 1));
		assertTrue(exception.getMessage().contains("Iterations"));
	}
	
	@Test
	void constructWithParallelismBelowMinimum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(65_536, 3, 0));
		assertTrue(exception.getMessage().contains("Parallelism"));
	}
	
	@Test
	void constructWithParallelismAboveMaximum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(65_536, 3, 17));
		assertTrue(exception.getMessage().contains("Parallelism"));
	}
	
	@Test
	void constructWithMemoryTooSmallForParallelism() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(8, 3, 2));
		assertTrue(exception.getMessage().contains("8"));
		assertTrue(exception.getMessage().contains("2"));
	}
	
	@Test
	void constructWithMultipleInvalidParameters() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(0, 0, 0));
		assertTrue(exception.getMessage().contains("Memory"));
		assertFalse(exception.getMessage().contains("Iterations"));
	}
	
	@Test
	void isWeakerThanWithNullCurrent() {
		assertThrows(NullPointerException.class, () -> PasswordAlgorithm.ARGON2ID.isWeakerThan(null));
	}
	
	@Test
	void parseParametersWithNullParameters() {
		assertThrows(NullPointerException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(null));
	}
	
	@Test
	void parseParametersWithWrongSectionCount() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[0]));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19" }));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "m=65536,t=3,p=4", "x" }));
	}
	
	@Test
	void parseParametersWithUnsupportedVersion() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=16", "m=65536,t=3,p=4" }));
		assertTrue(exception.getMessage().contains("16"));
		assertTrue(exception.getMessage().contains("19"));
	}
	
	@Test
	void parseParametersWithWrongCostCount() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "m=65536,t=3" }));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "m=65536,t=3,p=4,x=1" }));
	}
	
	@Test
	void parseParametersWithWrongParameterNames() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "x=65536,t=3,p=4" }));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "m=65536,x=3,p=4" }));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "m=65536,t=3,x=4" }));
	}
	
	@Test
	void parseParametersWithNonIntegerValue() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "m=abc,t=3,p=4" }));
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}
	
	@Test
	void parseParametersWithOutOfRangeCost() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "m=4,t=3,p=1" }));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "m=65536,t=999,p=1" }));
	}
	
	@Test
	void parseParametersWithNonIntegerVersion() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
			() -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=x", "m=65536,t=3,p=4" }));
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}
	
	@Test
	void constructAtMinimumBounds() {
		Argon2PasswordAlgorithm algorithm = assertDoesNotThrow(() -> new Argon2PasswordAlgorithm(8, 1, 1));
		assertEquals(8, algorithm.memory());
		assertEquals(1, algorithm.iterations());
		assertEquals(1, algorithm.parallelism());
	}
	
	@Test
	void constructAtMaximumBounds() {
		Argon2PasswordAlgorithm algorithm = assertDoesNotThrow(() -> new Argon2PasswordAlgorithm(1_048_576, 100, 16));
		assertEquals(1_048_576, algorithm.memory());
		assertEquals(100, algorithm.iterations());
		assertEquals(16, algorithm.parallelism());
	}
	
	@Test
	void constructAtCrossGuardBoundary() {
		assertDoesNotThrow(() -> new Argon2PasswordAlgorithm(128, 1, 16));
		assertThrows(IllegalArgumentException.class, () -> new Argon2PasswordAlgorithm(127, 1, 16));
	}
	
	@Test
	void isWeakerThanNonArgon2Algorithm() {
		assertTrue(PasswordAlgorithm.ARGON2ID.isWeakerThan(PasswordAlgorithm.SCRYPT));
		assertTrue(PasswordAlgorithm.ARGON2ID.isWeakerThan(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
	}
	
	@Test
	void isWeakerThanWeakerMemory() {
		assertTrue(new Argon2PasswordAlgorithm(32_768, 3, 4).isWeakerThan(PasswordAlgorithm.ARGON2ID));
	}
	
	@Test
	void isWeakerThanWeakerIterations() {
		assertTrue(new Argon2PasswordAlgorithm(65_536, 2, 4).isWeakerThan(PasswordAlgorithm.ARGON2ID));
	}
	
	@Test
	void isWeakerThanWeakerParallelism() {
		assertTrue(new Argon2PasswordAlgorithm(65_536, 3, 2).isWeakerThan(PasswordAlgorithm.ARGON2ID));
	}
	
	@Test
	void isWeakerThanEqualConfiguration() {
		assertFalse(PasswordAlgorithm.ARGON2ID.isWeakerThan(PasswordAlgorithm.ARGON2ID));
	}
	
	@Test
	void isWeakerThanStrongerConfiguration() {
		assertFalse(new Argon2PasswordAlgorithm(131_072, 4, 8).isWeakerThan(PasswordAlgorithm.ARGON2ID));
	}
	
	@Test
	void parseParametersWithValidRecord() {
		assertEquals(PasswordAlgorithm.ARGON2ID, PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "m=65536,t=3,p=4" }));
	}
	
	@Test
	void identifierIsArgon2id() {
		assertEquals("argon2id", PasswordAlgorithm.ARGON2ID.identifier());
		assertEquals("argon2id", new Argon2PasswordAlgorithm(8, 1, 1).identifier());
	}
	
	@Test
	void jcaNameIsArgon2() {
		assertEquals("ARGON2", PasswordAlgorithm.ARGON2ID.jcaName());
	}
	
	@Test
	void requiresBouncyCastleAlwaysTrue() {
		assertTrue(PasswordAlgorithm.ARGON2ID.requiresBouncyCastle());
		assertTrue(new Argon2PasswordAlgorithm(8, 1, 1).requiresBouncyCastle());
	}
	
	@Test
	void encodeParametersOfDefault() {
		assertEquals("v=19$m=65536,t=3,p=4", PasswordAlgorithm.ARGON2ID.encodeParameters());
	}
	
	@Test
	void encodeParametersReflectsCosts() {
		assertEquals("v=19$m=8,t=1,p=1", new Argon2PasswordAlgorithm(8, 1, 1).encodeParameters());
	}
	
	@Test
	void versionConstantIsNineteen() {
		assertEquals(19, Argon2PasswordAlgorithm.VERSION);
		assertTrue(PasswordAlgorithm.ARGON2ID.encodeParameters().startsWith("v=19$"));
	}
	
	@Test
	void boundConstantsAreOrdered() {
		assertTrue(Argon2PasswordAlgorithm.MIN_MEMORY < Argon2PasswordAlgorithm.MAX_MEMORY);
		assertTrue(Argon2PasswordAlgorithm.MIN_ITERATIONS < Argon2PasswordAlgorithm.MAX_ITERATIONS);
		assertTrue(Argon2PasswordAlgorithm.MIN_PARALLELISM < Argon2PasswordAlgorithm.MAX_PARALLELISM);
		assertEquals(1_048_576, Argon2PasswordAlgorithm.MAX_MEMORY);
	}
	
	@Test
	void isPasswordAlgorithm() {
		assertInstanceOf(PasswordAlgorithm.class, PasswordAlgorithm.ARGON2ID);
	}
	
	@Test
	void encodeParametersRoundTrip() {
		for (Argon2PasswordAlgorithm algorithm : new Argon2PasswordAlgorithm[] {
			new Argon2PasswordAlgorithm(8, 1, 1),
			new Argon2PasswordAlgorithm(65_536, 3, 4), new Argon2PasswordAlgorithm(1_048_576, 100, 16)
		}) {
			String[] sections = algorithm.encodeParameters().split("\\$", -1);
			assertEquals(2, sections.length);
			assertEquals(algorithm, algorithm.parseParameters(sections));
		}
	}
	
	@Test
	void equalsAndHashCodeOverAllComponents() {
		Argon2PasswordAlgorithm algorithm = new Argon2PasswordAlgorithm(65_536, 3, 4);
		assertEquals(algorithm, new Argon2PasswordAlgorithm(65_536, 3, 4));
		assertEquals(algorithm.hashCode(), new Argon2PasswordAlgorithm(65_536, 3, 4).hashCode());
		assertNotEquals(algorithm, new Argon2PasswordAlgorithm(32_768, 3, 4));
		assertNotEquals(algorithm, new Argon2PasswordAlgorithm(65_536, 2, 4));
		assertNotEquals(algorithm, new Argon2PasswordAlgorithm(65_536, 3, 2));
	}
	
	@Test
	void toStringContainsComponents() {
		String string = new Argon2PasswordAlgorithm(65_536, 3, 4).toString();
		assertTrue(string.contains("Argon2PasswordAlgorithm"));
		assertTrue(string.contains("65536"));
		assertTrue(string.contains("3"));
		assertTrue(string.contains("4"));
	}
	
	@Test
	void parseParametersIgnoresReceiverCost() {
		PasswordAlgorithm parsed = new Argon2PasswordAlgorithm(8, 1, 1).parseParameters(new String[] { "v=19", "m=65536,t=3,p=4" });
		assertEquals(PasswordAlgorithm.ARGON2ID, parsed);
	}
	
	@Test
	void parseParametersWithEmptyCostSection() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "m=65536,t=3," }));
	}
	
	@Test
	void parseParametersWithReorderedParameters() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.ARGON2ID.parseParameters(new String[] { "v=19", "t=3,m=65536,p=4" }));
	}
	
	@Test
	void isWeakerThanIsNotSymmetric() {
		Argon2PasswordAlgorithm first = new Argon2PasswordAlgorithm(65_536, 3, 8);
		Argon2PasswordAlgorithm second = new Argon2PasswordAlgorithm(131_072, 3, 4);
		assertTrue(first.isWeakerThan(second));
		assertTrue(second.isWeakerThan(first));
	}
	
	@Test
	void deriveWithBouncyCastle() throws Exception {
		assumeTrue(Providers.supports(PasswordAlgorithm.ARGON2ID));
		SecretKeyFactory factory = SecretKeyFactory.getInstance("ARGON2", Security.getProvider("BC"));
		Argon2PasswordAlgorithm algorithm = new Argon2PasswordAlgorithm(8, 1, 1);
		Argon2KeySpec spec = new Argon2KeySpec("password".toCharArray(), new byte[16], algorithm.iterations(), algorithm.memory(), algorithm.parallelism(), 256);
		assertEquals(32, factory.generateSecret(spec).getEncoded().length);
	}
}
