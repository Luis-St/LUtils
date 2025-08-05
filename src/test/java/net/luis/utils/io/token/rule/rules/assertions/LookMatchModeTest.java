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

package net.luis.utils.io.token.rule.rules.assertions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LookMatchMode}.<br>
 *
 * @author Luis-St
 */
class LookMatchModeTest {
	
	@Test
	void positiveShouldMatchWithTrueRuleMatch() {
		boolean result = LookMatchMode.POSITIVE.shouldMatch(true);
		
		assertTrue(result);
	}
	
	@Test
	void positiveShouldMatchWithFalseRuleMatch() {
		boolean result = LookMatchMode.POSITIVE.shouldMatch(false);
		
		assertFalse(result);
	}
	
	@Test
	void negativeShouldMatchWithTrueRuleMatch() {
		boolean result = LookMatchMode.NEGATIVE.shouldMatch(true);
		
		assertFalse(result);
	}
	
	@Test
	void negativeShouldMatchWithFalseRuleMatch() {
		boolean result = LookMatchMode.NEGATIVE.shouldMatch(false);
		
		assertTrue(result);
	}
	
	@Test
	void shouldMatchLogicConsistency() {
		assertTrue(LookMatchMode.POSITIVE.shouldMatch(true) != LookMatchMode.NEGATIVE.shouldMatch(true));
		assertTrue(LookMatchMode.POSITIVE.shouldMatch(false) != LookMatchMode.NEGATIVE.shouldMatch(false));
	}
	
	@Test
	void shouldMatchTruthTable() {
		assertTrue(LookMatchMode.POSITIVE.shouldMatch(true));
		assertFalse(LookMatchMode.POSITIVE.shouldMatch(false));
		
		assertFalse(LookMatchMode.NEGATIVE.shouldMatch(true));
		assertTrue(LookMatchMode.NEGATIVE.shouldMatch(false));
	}
	
	@Test
	void shouldMatchMultipleCalls() {
		assertTrue(LookMatchMode.POSITIVE.shouldMatch(true));
		assertTrue(LookMatchMode.POSITIVE.shouldMatch(true));
		assertTrue(LookMatchMode.POSITIVE.shouldMatch(true));
		
		assertFalse(LookMatchMode.NEGATIVE.shouldMatch(true));
		assertFalse(LookMatchMode.NEGATIVE.shouldMatch(true));
		assertFalse(LookMatchMode.NEGATIVE.shouldMatch(true));
	}
	
	@Test
	void shouldMatchBehaviorDifference() {
		boolean positiveWithTrue = LookMatchMode.POSITIVE.shouldMatch(true);
		boolean negativeWithTrue = LookMatchMode.NEGATIVE.shouldMatch(true);
		assertNotEquals(positiveWithTrue, negativeWithTrue);
		
		boolean positiveWithFalse = LookMatchMode.POSITIVE.shouldMatch(false);
		boolean negativeWithFalse = LookMatchMode.NEGATIVE.shouldMatch(false);
		assertNotEquals(positiveWithFalse, negativeWithFalse);
	}
	
	@Test
	void shouldMatchSymmetryTest() {
		for (LookMatchMode mode : LookMatchMode.values()) {
			for (boolean ruleMatch : new boolean[] { true, false }) {
				boolean result1 = mode.shouldMatch(ruleMatch);
				boolean result2 = mode.shouldMatch(ruleMatch);
				assertEquals(result1, result2);
			}
		}
	}
}
