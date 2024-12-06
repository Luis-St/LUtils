/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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
	void constructor() {
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(0, 10));
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(10, 0));
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(0L, 10L));
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(10L, 0L));
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(BigInteger.ZERO, BigInteger.TEN));
		assertThrows(IllegalArgumentException.class, () -> new ExtendedEuclideanAlgorithm(BigInteger.TEN, BigInteger.ZERO));
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(78457223, 13));
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(784572235658445215L, 13L));
		assertDoesNotThrow(() -> new ExtendedEuclideanAlgorithm(BigInteger.valueOf(784572235658445215L), BigInteger.valueOf(13L)));
	}
	
	@Test
	void gcd() {
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(0, 10));
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(10, 0));
		assertEquals(2, ExtendedEuclideanAlgorithm.gcd(20, 2));
		assertEquals(1, ExtendedEuclideanAlgorithm.gcd(19, 2));
		
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(0L, 10L));
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(10L, 0L));
		assertEquals(2L, ExtendedEuclideanAlgorithm.gcd(20L, 2L));
		assertEquals(1L, ExtendedEuclideanAlgorithm.gcd(19L, 2L));
		
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(BigInteger.ZERO, BigInteger.TEN));
		assertThrows(IllegalArgumentException.class, () -> ExtendedEuclideanAlgorithm.gcd(BigInteger.TEN, BigInteger.ZERO));
		assertEquals(BigInteger.TWO, ExtendedEuclideanAlgorithm.gcd(BigInteger.valueOf(20L), BigInteger.TWO));
		assertEquals(BigInteger.ONE, ExtendedEuclideanAlgorithm.gcd(BigInteger.valueOf(19L), BigInteger.TWO));
	}
	
	@Test
	void isComplete() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertFalse(eea.isComplete());
		eea.executeUntilComplete();
		assertTrue(eea.isComplete());
	}
	
	@Test
	void getInitialValue() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(784572235658445215L), eea.getInitialValue());
	}
	
	@Test
	void getInitialDivisor() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(13), eea.getInitialDivisor());
	}
	
	@Test
	void getValue() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(784572235658445215L), eea.getValue());
	}
	
	@Test
	void getDivisor() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(13), eea.getDivisor());
	}
	
	@Test
	void getRemainder() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(784572235658445215L % 13), eea.getRemainder());
	}
	
	@Test
	void getQuotient() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.valueOf(784572235658445215L / 13), eea.getQuotient());
	}
	
	@Test
	void getStep() {
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
	void getFirstCoefficient() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.ZERO, eea.getFirstCoefficient());
		eea.execute();
		assertEquals(BigInteger.ONE, eea.getFirstCoefficient());
		eea.executeUntilComplete();
		assertEquals(BigInteger.TWO, eea.getFirstCoefficient());
	}
	
	@Test
	void getSecondCoefficient() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(BigInteger.ONE, eea.getSecondCoefficient());
		eea.execute();
		assertEquals(BigInteger.valueOf(-60351710435265016L), eea.getSecondCoefficient());
		eea.executeUntilComplete();
		assertEquals(BigInteger.valueOf(-120703420870530033L), eea.getSecondCoefficient());
	}
	
	@Test
	void executeUntilComplete() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(0, eea.getStep());
		assertFalse(eea.isComplete());
		eea.executeUntilComplete();
		assertEquals(3, eea.getStep());
		assertTrue(eea.isComplete());
	}
	
	@Test
	void execute() {
		ExtendedEuclideanAlgorithm eea = new ExtendedEuclideanAlgorithm(784572235658445215L, 13);
		assertEquals(0, eea.getStep());
		assertFalse(eea.isComplete());
		eea.execute();
		assertEquals(1, eea.getStep());
		assertFalse(eea.isComplete());
		eea.execute(2);
		assertEquals(3, eea.getStep());
		assertTrue(eea.isComplete());
	}
}