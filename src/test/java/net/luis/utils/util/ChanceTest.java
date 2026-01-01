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

package net.luis.utils.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Chance}.<br>
 *
 * @author Luis-St
 */
class ChanceTest {
	
	@Test
	void ofWithValidChances() {
		assertNotNull(Chance.of(0.5));
		assertNotNull(Chance.of(1.0));
		assertNotNull(Chance.of(0.0));
	}
	
	@Test
	void ofWithChanceBelowZero() {
		assertEquals(Chance.ZERO, Chance.of(-0.1));
		assertEquals(Chance.ZERO, Chance.of(-100.0));
		assertEquals(Chance.ZERO, Chance.of(Double.NEGATIVE_INFINITY));
	}
	
	@Test
	void ofWithChanceAboveOne() {
		assertEquals(Chance.ONE, Chance.of(1.1));
		assertEquals(Chance.ONE, Chance.of(100.0));
		assertEquals(Chance.ONE, Chance.of(Double.POSITIVE_INFINITY));
	}
	
	@Test
	void ofWithExactBoundaryValues() {
		assertEquals(Chance.ZERO, Chance.of(0.0));
		assertEquals(Chance.ONE, Chance.of(1.0));
	}
	
	@Test
	void parseWithValidPercentageStrings() {
		assertEquals(Chance.ZERO, Chance.parse("0.0%"));
		assertEquals(Chance.of(0.05), Chance.parse("0.05%"));
		assertEquals(Chance.of(0.1), Chance.parse("0.1%"));
		assertEquals(Chance.of(0.5), Chance.parse("0.5%"));
		assertEquals(Chance.ONE, Chance.parse("1.0%"));
	}
	
	@Test
	void parseWithCommaAsDecimalSeparator() {
		assertEquals(Chance.of(0.05), Chance.parse("0,05%"));
		assertEquals(Chance.of(0.1), Chance.parse("0,1%"));
		assertEquals(Chance.of(0.5), Chance.parse("0,5%"));
		assertEquals(Chance.ONE, Chance.parse("1,0%"));
	}
	
	@Test
	void parseWithNullOrEmptyStrings() {
		assertEquals(Chance.ZERO, Chance.parse(null));
		assertEquals(Chance.ZERO, Chance.parse(""));
		assertEquals(Chance.ZERO, Chance.parse("   "));
	}
	
	@Test
	void parseWithInvalidFormats() {
		assertEquals(Chance.ZERO, Chance.parse("0%"));
		assertEquals(Chance.ZERO, Chance.parse("0.0"));
		assertEquals(Chance.ZERO, Chance.parse(".0%"));
		assertEquals(Chance.ZERO, Chance.parse("50%"));
		assertEquals(Chance.ZERO, Chance.parse("invalid"));
		assertEquals(Chance.ZERO, Chance.parse("2.0%"));
	}
	
	@Test
	void setSeed() {
		assertDoesNotThrow(() -> Chance.setSeed(0));
		assertDoesNotThrow(() -> Chance.setSeed(-1));
		assertDoesNotThrow(() -> Chance.setSeed(1));
		assertDoesNotThrow(() -> Chance.setSeed(Long.MAX_VALUE));
		assertDoesNotThrow(() -> Chance.setSeed(Long.MIN_VALUE));
	}
	
	@Test
	void isTrueWithZeroChance() {
		assertFalse(Chance.ZERO.isTrue());
		assertFalse(Chance.of(0.0).isTrue());
		assertFalse(Chance.of(-1.0).isTrue());
	}
	
	@Test
	void isTrueWithFullChance() {
		assertTrue(Chance.ONE.isTrue());
		assertTrue(Chance.of(1.0).isTrue());
		assertTrue(Chance.of(2.0).isTrue());
	}
	
	@Test
	void isTrueWithPartialChance() {
		assertFalse(Chance.of(0.5).isTrue());
		assertFalse(Chance.of(0.9).isTrue());
		assertFalse(Chance.of(0.99).isTrue());
	}
	
	@Test
	void isFalseWithZeroChance() {
		assertTrue(Chance.ZERO.isFalse());
		assertTrue(Chance.of(0.0).isFalse());
		assertTrue(Chance.of(-1.0).isFalse());
	}
	
	@Test
	void isFalseWithFullChance() {
		assertFalse(Chance.ONE.isFalse());
		assertFalse(Chance.of(1.0).isFalse());
		assertFalse(Chance.of(2.0).isFalse());
	}
	
	@Test
	void isFalseWithPartialChance() {
		assertFalse(Chance.of(0.5).isFalse());
		assertFalse(Chance.of(0.1).isFalse());
		assertFalse(Chance.of(0.01).isFalse());
	}
	
	@Test
	void resultWithZeroChance() {
		assertFalse(Chance.ZERO.result());
		assertFalse(Chance.of(0.0).result());
		assertFalse(Chance.of(-1.0).result());
	}
	
	@Test
	void resultWithFullChance() {
		assertTrue(Chance.ONE.result());
		assertTrue(Chance.of(1.0).result());
		assertTrue(Chance.of(2.0).result());
	}
	
	@Test
	void resultWithPartialChanceConsistency() {
		Chance.setSeed(12345);
		Chance halfChance = Chance.of(0.5);
		
		int trueCount = 0;
		int totalIterations = 1000;
		
		for (int i = 0; i < totalIterations; i++) {
			if (halfChance.result()) {
				trueCount++;
			}
		}
		
		double ratio = (double) trueCount / totalIterations;
		assertTrue(ratio > 0.4 && ratio < 0.6, "Expected ratio around 0.5, but got " + ratio);
	}
	
	@Test
	void hashCodeConsistency() {
		Chance chance1 = Chance.of(0.5);
		Chance chance2 = Chance.of(0.5);
		
		assertEquals(chance1.hashCode(), chance2.hashCode());
		assertEquals(Chance.ZERO.hashCode(), Chance.of(0.0).hashCode());
		assertEquals(Chance.ONE.hashCode(), Chance.of(1.0).hashCode());
	}
}
