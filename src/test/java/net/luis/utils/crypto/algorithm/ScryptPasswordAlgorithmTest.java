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
import org.bouncycastle.jcajce.spec.ScryptKeySpec;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKeyFactory;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

/**
 * Test class for {@link ScryptPasswordAlgorithm}.<br>
 *
 * @author Luis-St
 */
class ScryptPasswordAlgorithmTest {
	
	//region Setup
	@BeforeAll
	static void installProvider() {
		Providers.installBouncyCastle();
	}
	//endregion
	
	@Test
	void constructScryptAlgorithm() {
		ScryptPasswordAlgorithm algorithm = new ScryptPasswordAlgorithm(1 << 16, 8, 1);
		assertEquals(65_536, algorithm.cost());
		assertEquals(8, algorithm.blockSize());
		assertEquals(1, algorithm.parallelism());
		assertEquals(PasswordAlgorithm.SCRYPT, algorithm);
	}
	
	@Test
	void constructWithCostBelowMinimum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(512, 8, 1));
		assertTrue(exception.getMessage().contains("Cost"));
	}
	
	@Test
	void constructWithCostAboveMaximum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(1 << 21, 1, 1));
		assertTrue(exception.getMessage().contains("Cost"));
	}
	
	@Test
	void constructWithZeroCost() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(0, 8, 1));
		assertTrue(exception.getMessage().contains("Cost must be in"));
	}
	
	@Test
	void constructWithNegativeCost() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(-1024, 8, 1));
		assertTrue(exception.getMessage().contains("Cost"));
	}
	
	@Test
	void constructWithNonPowerOfTwoCost() {
		IllegalArgumentException first = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(65_537, 8, 1));
		assertTrue(first.getMessage().contains("power of two"));
		IllegalArgumentException second = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(1025, 1, 1));
		assertTrue(second.getMessage().contains("power of two"));
	}
	
	@Test
	void constructWithBlockSizeBelowMinimum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(65_536, 0, 1));
		assertTrue(exception.getMessage().contains("Block size"));
	}
	
	@Test
	void constructWithBlockSizeAboveMaximum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(1024, 65, 1));
		assertTrue(exception.getMessage().contains("Block size"));
	}
	
	@Test
	void constructWithParallelismBelowMinimum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(65_536, 8, 0));
		assertTrue(exception.getMessage().contains("Parallelism"));
	}
	
	@Test
	void constructWithParallelismAboveMaximum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(65_536, 8, 17));
		assertTrue(exception.getMessage().contains("Parallelism"));
	}
	
	@Test
	void constructExceedingMemoryBound() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(1 << 20, 16, 1));
		assertTrue(exception.getMessage().contains("2147483648"));
		assertTrue(exception.getMessage().contains(String.valueOf(ScryptPasswordAlgorithm.MAX_MEMORY)));
	}
	
	@Test
	void constructWithMultipleInvalidParameters() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(0, 0, 0));
		assertTrue(exception.getMessage().contains("Cost"));
		assertFalse(exception.getMessage().contains("Block size"));
	}
	
	@Test
	void isWeakerThanWithNullCurrent() {
		assertThrows(NullPointerException.class, () -> PasswordAlgorithm.SCRYPT.isWeakerThan(null));
	}
	
	@Test
	void parseParametersWithNullParameters() {
		assertThrows(NullPointerException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(null));
	}
	
	@Test
	void parseParametersWithWrongSectionCount() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[0]));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=16,r=8,p=1", "x" }));
	}
	
	@Test
	void parseParametersWithWrongCostCount() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=16,r=8" }));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=16,r=8,p=1,x=1" }));
	}
	
	@Test
	void parseParametersWithCostExponentBelowMinimum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=0,r=8,p=1" }));
		assertTrue(exception.getMessage().contains("exponent"));
	}
	
	@Test
	void parseParametersWithCostExponentAboveMaximum() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=31,r=8,p=1" }));
		assertTrue(exception.getMessage().contains("exponent"));
	}
	
	@Test
	void parseParametersWithNegativeCostExponent() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=-1,r=8,p=1" }));
	}
	
	@Test
	void parseParametersWithWrongParameterNames() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "x=16,r=8,p=1" }));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=16,x=8,p=1" }));
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=16,r=8,x=1" }));
	}
	
	@Test
	void parseParametersWithNonIntegerValue() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=abc,r=8,p=1" }));
		assertInstanceOf(NumberFormatException.class, exception.getCause());
	}
	
	@Test
	void parseParametersWithInRangeExponentButOutOfRangeCost() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=5,r=8,p=1" }));
		assertTrue(exception.getMessage().contains("Cost"));
	}
	
	@Test
	void parseParametersExceedingMemoryBound() {
		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=20,r=16,p=1" }));
		assertTrue(exception.getMessage().contains("bound"));
	}
	
	@Test
	void constructAtMinimumBounds() {
		ScryptPasswordAlgorithm algorithm = assertDoesNotThrow(() -> new ScryptPasswordAlgorithm(1024, 1, 1));
		assertEquals(131_072L, algorithm.memory());
	}
	
	@Test
	void constructAtMaximumCostWithSmallBlockSize() {
		ScryptPasswordAlgorithm algorithm = assertDoesNotThrow(() -> new ScryptPasswordAlgorithm(ScryptPasswordAlgorithm.MAX_COST, 8, 1));
		assertEquals(ScryptPasswordAlgorithm.MAX_MEMORY, algorithm.memory());
	}
	
	@Test
	void constructAtMemoryBoundary() {
		assertDoesNotThrow(() -> new ScryptPasswordAlgorithm(ScryptPasswordAlgorithm.MAX_COST, 8, 1));
		assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(ScryptPasswordAlgorithm.MAX_COST, 16, 1));
	}
	
	@Test
	void constructAtMaximumBlockSizeAndParallelism() {
		ScryptPasswordAlgorithm algorithm = assertDoesNotThrow(() -> new ScryptPasswordAlgorithm(1024, 64, 16));
		assertEquals(8_388_608L, algorithm.memory());
	}
	
	@Test
	void isWeakerThanArgon2Algorithm() {
		assertTrue(PasswordAlgorithm.SCRYPT.isWeakerThan(PasswordAlgorithm.ARGON2ID));
	}
	
	@Test
	void isWeakerThanPbkdf2Algorithm() {
		assertFalse(PasswordAlgorithm.SCRYPT.isWeakerThan(PasswordAlgorithm.PBKDF2_HMAC_SHA_512));
	}
	
	@Test
	void isWeakerThanWeakerCost() {
		assertTrue(new ScryptPasswordAlgorithm(1 << 15, 8, 1).isWeakerThan(PasswordAlgorithm.SCRYPT));
	}
	
	@Test
	void isWeakerThanWeakerBlockSize() {
		assertTrue(new ScryptPasswordAlgorithm(1 << 16, 4, 1).isWeakerThan(PasswordAlgorithm.SCRYPT));
	}
	
	@Test
	void isWeakerThanWeakerParallelism() {
		assertTrue(new ScryptPasswordAlgorithm(1 << 16, 8, 1).isWeakerThan(new ScryptPasswordAlgorithm(1 << 16, 8, 2)));
	}
	
	@Test
	void isWeakerThanEqualConfiguration() {
		assertFalse(PasswordAlgorithm.SCRYPT.isWeakerThan(PasswordAlgorithm.SCRYPT));
	}
	
	@Test
	void isWeakerThanStrongerConfiguration() {
		assertFalse(new ScryptPasswordAlgorithm(1 << 17, 16, 2).isWeakerThan(PasswordAlgorithm.SCRYPT));
	}
	
	@Test
	void parseParametersWithValidRecord() {
		assertEquals(PasswordAlgorithm.SCRYPT, PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=16,r=8,p=1" }));
	}
	
	@Test
	void identifierIsScrypt() {
		assertEquals("scrypt", PasswordAlgorithm.SCRYPT.identifier());
	}
	
	@Test
	void jcaNameIsScrypt() {
		assertEquals("SCRYPT", PasswordAlgorithm.SCRYPT.jcaName());
	}
	
	@Test
	void requiresBouncyCastleAlwaysTrue() {
		assertTrue(PasswordAlgorithm.SCRYPT.requiresBouncyCastle());
		assertTrue(new ScryptPasswordAlgorithm(1024, 1, 1).requiresBouncyCastle());
	}
	
	@Test
	void encodeParametersOfDefault() {
		assertEquals("ln=16,r=8,p=1", PasswordAlgorithm.SCRYPT.encodeParameters());
	}
	
	@Test
	void encodeParametersUsesCostExponent() {
		assertEquals("ln=10,r=1,p=1", new ScryptPasswordAlgorithm(1024, 1, 1).encodeParameters());
	}
	
	@Test
	void memoryOfDefault() {
		assertEquals(67_108_864L, PasswordAlgorithm.SCRYPT.memory());
		assertEquals(128L * 65_536 * 8, PasswordAlgorithm.SCRYPT.memory());
	}
	
	@Test
	void memoryAtBounds() {
		assertEquals(131_072L, new ScryptPasswordAlgorithm(1024, 1, 1).memory());
		assertEquals(ScryptPasswordAlgorithm.MAX_MEMORY, new ScryptPasswordAlgorithm(ScryptPasswordAlgorithm.MAX_COST, 8, 1).memory());
	}
	
	@Test
	void encodeParametersDoesNotContainDollar() {
		assertFalse(PasswordAlgorithm.SCRYPT.encodeParameters().contains("$"));
		assertEquals(1, PasswordAlgorithm.SCRYPT.encodeParameters().split("\\$", -1).length);
	}
	
	@Test
	void boundConstantsAreOrdered() {
		assertTrue(ScryptPasswordAlgorithm.MIN_COST < ScryptPasswordAlgorithm.MAX_COST);
		assertTrue(ScryptPasswordAlgorithm.MIN_BLOCK_SIZE < ScryptPasswordAlgorithm.MAX_BLOCK_SIZE);
		assertTrue(ScryptPasswordAlgorithm.MIN_PARALLELISM < ScryptPasswordAlgorithm.MAX_PARALLELISM);
		assertEquals(1, Integer.bitCount(ScryptPasswordAlgorithm.MIN_COST));
		assertEquals(1, Integer.bitCount(ScryptPasswordAlgorithm.MAX_COST));
		assertEquals(1_073_741_824L, ScryptPasswordAlgorithm.MAX_MEMORY);
	}
	
	@Test
	void isPasswordAlgorithm() {
		assertInstanceOf(PasswordAlgorithm.class, PasswordAlgorithm.SCRYPT);
	}
	
	@Test
	void encodeParametersRoundTrip() {
		for (ScryptPasswordAlgorithm algorithm : new ScryptPasswordAlgorithm[] {
			new ScryptPasswordAlgorithm(1024, 1, 1),
			new ScryptPasswordAlgorithm(65_536, 8, 1), new ScryptPasswordAlgorithm(ScryptPasswordAlgorithm.MAX_COST, 8, 1),
			new ScryptPasswordAlgorithm(1024, 64, 16)
		}) {
			String[] sections = algorithm.encodeParameters().split("\\$", -1);
			assertEquals(1, sections.length);
			assertEquals(algorithm, algorithm.parseParameters(sections));
		}
	}
	
	@Test
	void memoryIsComputedInLongWithoutOverflow() {
		ScryptPasswordAlgorithm algorithm = new ScryptPasswordAlgorithm(ScryptPasswordAlgorithm.MAX_COST, 8, 1);
		assertEquals(1_073_741_824L, algorithm.memory());
		assertTrue(algorithm.memory() > 0);
		
		assertTrue(128 * ScryptPasswordAlgorithm.MAX_COST * 16 < 0);
		assertEquals(2_147_483_648L, 128L * ScryptPasswordAlgorithm.MAX_COST * 16);
		assertThrows(IllegalArgumentException.class, () -> new ScryptPasswordAlgorithm(ScryptPasswordAlgorithm.MAX_COST, 16, 1));
	}
	
	@Test
	void memoryMatchesConstructorGuardComputation() {
		for (ScryptPasswordAlgorithm algorithm : new ScryptPasswordAlgorithm[] {
			new ScryptPasswordAlgorithm(1024, 1, 1),
			new ScryptPasswordAlgorithm(65_536, 8, 1), new ScryptPasswordAlgorithm(1024, 64, 16)
		}) {
			assertTrue(algorithm.memory() <= ScryptPasswordAlgorithm.MAX_MEMORY);
			assertEquals(128L * algorithm.cost() * algorithm.blockSize(), algorithm.memory());
		}
	}
	
	@Test
	void equalsAndHashCodeOverAllComponents() {
		ScryptPasswordAlgorithm algorithm = new ScryptPasswordAlgorithm(65_536, 8, 1);
		assertEquals(algorithm, new ScryptPasswordAlgorithm(65_536, 8, 1));
		assertEquals(algorithm.hashCode(), new ScryptPasswordAlgorithm(65_536, 8, 1).hashCode());
		assertNotEquals(algorithm, new ScryptPasswordAlgorithm(32_768, 8, 1));
		assertNotEquals(algorithm, new ScryptPasswordAlgorithm(65_536, 4, 1));
		assertNotEquals(algorithm, new ScryptPasswordAlgorithm(65_536, 8, 2));
	}
	
	@Test
	void toStringContainsComponents() {
		String string = new ScryptPasswordAlgorithm(65_536, 8, 1).toString();
		assertTrue(string.contains("ScryptPasswordAlgorithm"));
		assertTrue(string.contains("65536"));
		assertTrue(string.contains("8"));
	}
	
	@Test
	void parseParametersIgnoresReceiverCost() {
		assertEquals(PasswordAlgorithm.SCRYPT, new ScryptPasswordAlgorithm(1024, 1, 1).parseParameters(new String[] { "ln=16,r=8,p=1" }));
	}
	
	@Test
	void parseParametersWithEmptyCostSection() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "ln=16,r=8," }));
	}
	
	@Test
	void parseParametersWithReorderedParameters() {
		assertThrows(IllegalArgumentException.class, () -> PasswordAlgorithm.SCRYPT.parseParameters(new String[] { "r=8,ln=16,p=1" }));
	}
	
	@Test
	void isWeakerThanIsNotSymmetric() {
		ScryptPasswordAlgorithm first = new ScryptPasswordAlgorithm(1 << 16, 4, 2);
		ScryptPasswordAlgorithm second = new ScryptPasswordAlgorithm(1 << 15, 8, 1);
		assertTrue(first.isWeakerThan(second));
		assertTrue(second.isWeakerThan(first));
	}
	
	@Test
	void deriveWithBouncyCastle() throws Exception {
		assumeTrue(Providers.supports(PasswordAlgorithm.SCRYPT));
		SecretKeyFactory factory = SecretKeyFactory.getInstance("SCRYPT", Security.getProvider("BC"));
		ScryptPasswordAlgorithm algorithm = new ScryptPasswordAlgorithm(1024, 1, 1);
		ScryptKeySpec spec = new ScryptKeySpec("password".toCharArray(), new byte[16], algorithm.cost(), algorithm.blockSize(), algorithm.parallelism(), 256);
		assertEquals(32, factory.generateSecret(spec).getEncoded().length);
	}
}
