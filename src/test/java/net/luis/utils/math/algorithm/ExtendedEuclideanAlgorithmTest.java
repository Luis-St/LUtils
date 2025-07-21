/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.math.algorithm;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ExtendedEuclideanAlgorithm}.<br>
 *
 * @author Luis-St
 */
class ExtendedEuclideanAlgorithmTest {
	
	@Test
	void constructorIntFailsWithZeroValue() {
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(0, 10));
	}
	
	@Test
	void constructorIntFailsWithZeroDivisor() {
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(10, 0));
	}
	
	@Test
	void constructorIntFailsWithBothZero() {
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(0, 0));
	}
	
	@Test
	void constructorIntWorksWithValidInputs() {
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(78457223, 13));
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(1, 1));
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(-10, 5));
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(10, 3));
	}
	
	@Test
	void constructorLongFailsWithZeroValue() {
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(0L, 10L));
	}
	
	@Test
	void constructorLongFailsWithZeroDivisor() {
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(10L, 0L));
	}
	
	@Test
	void constructorLongWorksWithValidInputs() {
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(784572235658445215L, 13L));
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(Long.MAX_VALUE, 1));
	}
	
	@Test
	void constructorBigIntegerFailsWithZeroValue() {
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(BigInteger.ZERO, BigInteger.TEN));
	}
	
	@Test
	void constructorBigIntegerFailsWithZeroDivisor() {
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(BigInteger.TEN, BigInteger.ZERO));
	}
	
	@Test
	void constructorBigIntegerWorksWithValidInputs() {
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(BigInteger.valueOf(784572235658445215L), BigInteger.valueOf(13L)));
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(BigInteger.ONE, BigInteger.ONE));
	}
	
	@Test
	void gcdIntFailsWithZeroInputs() {
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(0, 10));
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(10, 0));
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(0, 0));
	}
	
	@Test
	void gcdIntCalculatesCorrectly() {
		assertEquals(2, ExtendedEuclideanAlgorithm.gcd(20, 3));
		assertEquals(1, ExtendedEuclideanAlgorithm.gcd(19, 2));
		assertEquals(15, ExtendedEuclideanAlgorithm.gcd(15, 25));
		assertEquals(4, ExtendedEuclideanAlgorithm.gcd(17, 13));
		assertEquals(12, ExtendedEuclideanAlgorithm.gcd(48, 18));
	}
	
	@Test
	void gcdIntHandlesNegativeNumbers() {
		assertEquals(1, ExtendedEuclideanAlgorithm.gcd(-20, 3));
		assertThrows(ArithmeticException.class, () -> ExtendedEuclideanAlgorithm.gcd(20, -3));
		assertThrows(ArithmeticException.class, () -> ExtendedEuclideanAlgorithm.gcd(-20, -2));
	}
	
	@Test
	void gcdLongFailsWithZeroInputs() {
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(0L, 10L));
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(10L, 0L));
	}
	
	@Test
	void gcdLongCalculatesCorrectly() {
		assertEquals(2L, ExtendedEuclideanAlgorithm.gcd(20L, 3L));
		assertEquals(1L, ExtendedEuclideanAlgorithm.gcd(19L, 2L));
		assertEquals(1L, ExtendedEuclideanAlgorithm.gcd(Long.MAX_VALUE, Long.MAX_VALUE - 1));
	}
	
	@Test
	void gcdBigIntegerFailsWithZeroInputs() {
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(BigInteger.ZERO, BigInteger.TEN));
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(BigInteger.TEN, BigInteger.ZERO));
	}
	
	@Test
	void gcdBigIntegerCalculatesCorrectly() {
		assertEquals(BigInteger.TWO, ExtendedEuclideanAlgorithm.gcd(BigInteger.valueOf(20L), BigInteger.TWO));
		assertEquals(BigInteger.ONE, ExtendedEuclideanAlgorithm.gcd(BigInteger.valueOf(19L), BigInteger.TWO));
		
		BigInteger large1 = new BigInteger("123456789012345678901234567890");
		BigInteger large2 = new BigInteger("987654321098765432109876543210");
		assertDoesNotThrow(() -> ExtendedEuclideanAlgorithm.gcd(large1, large2));
	}
	
	@Test
	void isCompleteInitiallyFalse() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertFalse(eea.isComplete());
	}
	
	@Test
	void isCompleteTrueAfterExecution() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		eea.executeUntilComplete();
		assertTrue(eea.isComplete());
	}
	
	@Test
	void isCompleteHandlesSimpleCases() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(5, 2);
		assertFalse(eea.isComplete());
		eea.execute();
		eea.execute();
		assertTrue(eea.isComplete());
	}
	
	@Test
	void getInitialValueReturnsCorrectValue() {
		ExtendedEuclideanAlgorithm eea1 = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(784572235658445215L), eea1.getInitialValue());
		
		ExtendedEuclideanAlgorithm eea2 = new ExtendedEuclideanAlgorithm(100, 25);
		assertEquals(BigInteger.valueOf(100), eea2.getInitialValue());
	}
	
	@Test
	void getInitialDivisorReturnsCorrectValue() {
		ExtendedEuclideanAlgorithm eea1 = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(13), eea1.getInitialDivisor());
		
		ExtendedEuclideanAlgorithm eea2 = new ExtendedEuclideanAlgorithm(100, 25);
		assertEquals(BigInteger.valueOf(25), eea2.getInitialDivisor());
	}
	
	@Test
	void getValueInitiallyEqualsInitialValue() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(784572235658445215L), eea.getValue());
	}
	
	@Test
	void getDivisorInitiallyEqualsInitialDivisor() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(13), eea.getDivisor());
	}
	
	@Test
	void getRemainderCalculatesCorrectly() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(784572235658445215L % 13), eea.getRemainder());
		
		ExtendedEuclideanAlgorithm eea2 = new ExtendedEuclideanAlgorithm(17, 5);
		assertEquals(BigInteger.valueOf(2), eea2.getRemainder());
	}
	
	@Test
	void getQuotientCalculatesCorrectly() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(784572235658445215L / 13), eea.getQuotient());
		
		ExtendedEuclideanAlgorithm eea2 = new ExtendedEuclideanAlgorithm(17, 5);
		assertEquals(BigInteger.valueOf(3), eea2.getQuotient());
	}
	
	@Test
	void getStepInitiallyZero() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(0, eea.getStep());
	}
	
	@Test
	void getStepIncrementsWithExecution() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(0, eea.getStep());
		eea.execute();
		assertEquals(1, eea.getStep());
		eea.execute();
		assertEquals(2, eea.getStep());
		eea.execute();
		assertEquals(3, eea.getStep());
	}
	
	@Test
	void getFirstCoefficientInitiallyZero() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.ZERO, eea.getFirstCoefficient());
	}
	
	@Test
	void getFirstCoefficientChangesWithExecution() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.ZERO, eea.getFirstCoefficient());
		eea.execute();
		assertEquals(BigInteger.ONE, eea.getFirstCoefficient());
		eea.executeUntilComplete();
		assertEquals(BigInteger.valueOf(2), eea.getFirstCoefficient());
	}
	
	@Test
	void getSecondCoefficientInitiallyOne() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.ONE, eea.getSecondCoefficient());
	}
	
	@Test
	void getSecondCoefficientChangesWithExecution() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.ONE, eea.getSecondCoefficient());
		eea.execute();
		assertEquals(BigInteger.valueOf(-60351710435265016L), eea.getSecondCoefficient());
		eea.executeUntilComplete();
		assertEquals(BigInteger.valueOf(-120703420870530033L), eea.getSecondCoefficient());
	}
	
	@Test
	void executeUntilCompleteWorksCorrectly() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(0, eea.getStep());
		assertFalse(eea.isComplete());
		eea.executeUntilComplete();
		assertEquals(3, eea.getStep());
		assertTrue(eea.isComplete());
	}
	
	@Test
	void executeUntilCompleteHandlesAlreadyComplete() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(10, 5);
		eea.executeUntilComplete();
		int finalStep = eea.getStep();
		assertTrue(eea.isComplete());
		
		eea.executeUntilComplete();
		assertEquals(finalStep, eea.getStep());
		assertTrue(eea.isComplete());
	}
	
	@Test
	void executeSingleStepWorks() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(0, eea.getStep());
		assertFalse(eea.isComplete());
		eea.execute();
		assertEquals(1, eea.getStep());
		assertFalse(eea.isComplete());
	}
	
	@Test
	void executeMultipleStepsWorks() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(0, eea.getStep());
		eea.execute(2);
		assertEquals(2, eea.getStep());
		assertFalse(eea.isComplete());
		eea.execute(1);
		assertEquals(3, eea.getStep());
		assertTrue(eea.isComplete());
	}
	
	@Test
	void executeHandlesZeroSteps() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(100, 25);
		int initialStep = eea.getStep();
		eea.execute(0);
		assertEquals(initialStep, eea.getStep());
	}
	
	@Test
	void executeHandlesNegativeSteps() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(100, 25);
		int initialStep = eea.getStep();
		eea.execute(-5);
		assertEquals(initialStep, eea.getStep());
	}
	
	@Test
	void executeDoesNothingWhenComplete() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(10, 5);
		eea.executeUntilComplete();
		int finalStep = eea.getStep();
		assertTrue(eea.isComplete());
		
		eea.execute();
		assertEquals(finalStep, eea.getStep());
		eea.execute(5);
		assertEquals(finalStep, eea.getStep());
	}
	
	@Test
	void algorithmProducesCorrectGcd() {
		ExtendedEuclideanAlgorithm eea1 = new ExtendedEuclideanAlgorithm(48, 18);
		eea1.executeUntilComplete();
		assertEquals(BigInteger.valueOf(6), eea1.getDivisor());
		
		ExtendedEuclideanAlgorithm eea2 = new ExtendedEuclideanAlgorithm(17, 13);
		eea2.executeUntilComplete();
		assertEquals(BigInteger.ONE, eea2.getDivisor());
		
		ExtendedEuclideanAlgorithm eea3 = new ExtendedEuclideanAlgorithm(100, 13);
		eea3.executeUntilComplete();
		assertEquals(BigInteger.valueOf(1), eea3.getDivisor());
	}
	
	@Test
	void equalsReturnsTrueForSameObjects() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(100, 13);
		assertEquals(eea, eea);
	}
	
	@Test
	void equalsReturnsTrueForIdenticalAlgorithms() {
		ExtendedEuclideanAlgorithm eea1 = new ExtendedEuclideanAlgorithm(100, 13);
		ExtendedEuclideanAlgorithm eea2 = new ExtendedEuclideanAlgorithm(100, 13);
		assertEquals(eea1, eea2);
		assertEquals(eea1.hashCode(), eea2.hashCode());
	}
	
	@Test
	void equalsReturnsFalseForDifferentInputs() {
		ExtendedEuclideanAlgorithm eea1 = new ExtendedEuclideanAlgorithm(100, 13);
		ExtendedEuclideanAlgorithm eea2 = new ExtendedEuclideanAlgorithm(100, 11);
		ExtendedEuclideanAlgorithm eea3 = new ExtendedEuclideanAlgorithm(200, 13);
		
		assertNotEquals(eea1, eea2);
		assertNotEquals(eea1, eea3);
		assertNotEquals(eea2, eea3);
	}
	
	@Test
	void equalsReturnsFalseForNull() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(100, 13);
		assertNotEquals(eea, null);
	}
	
	@Test
	void equalsReturnsFalseForDifferentClass() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(100, 13);
		assertNotEquals(eea, "not an algorithm");
	}
	
	@Test
	void toStringContainsRelevantInformation() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(100, 13);
		String str = eea.toString();
		assertTrue(str.contains("100"));
		assertTrue(str.contains("13"));
		assertTrue(str.contains("EEA"));
	}
	
	@Test
	void toStringChangesWithExecution() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(100, 13);
		String initialStr = eea.toString();
		eea.execute();
		String afterStr = eea.toString();
		assertNotEquals(initialStr, afterStr);
	}
}
