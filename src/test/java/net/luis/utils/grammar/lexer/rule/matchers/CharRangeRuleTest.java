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

package net.luis.utils.grammar.lexer.rule.matchers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharRangeRule}.<br>
 *
 * @author Luis-St
 */
class CharRangeRuleTest {
	
	@Test
	void constructValidRange() {
		CharRangeRule rule = new CharRangeRule('a', 'z');
		assertEquals('a', rule.start());
		assertEquals('z', rule.end());
	}
	
	@Test
	void constructRangeWithEqualStartAndEnd() {
		CharRangeRule rule = new CharRangeRule('a', 'a');
		assertEquals('a', rule.start());
		assertEquals('a', rule.end());
	}
	
	@Test
	void constructWithEndLessThanStart() {
		assertThrows(IllegalArgumentException.class, () -> new CharRangeRule('z', 'a'));
	}
	
	@Test
	void matchCharBelowRangeReturnsFalse() {
		assertFalse(new CharRangeRule('b', 'y').match('a'));
	}
	
	@Test
	void matchCharAboveRangeReturnsFalse() {
		assertFalse(new CharRangeRule('b', 'y').match('z'));
	}
	
	@Test
	void matchCharWithinRangeReturnsTrue() {
		assertTrue(new CharRangeRule('a', 'z').match('m'));
	}
	
	@Test
	void matchCharAtStartBoundary() {
		assertTrue(new CharRangeRule('a', 'z').match('a'));
	}
	
	@Test
	void matchCharAtEndBoundary() {
		assertTrue(new CharRangeRule('a', 'z').match('z'));
	}
	
	@Test
	void matchDigitRange() {
		assertTrue(new CharRangeRule('0', '9').match('5'));
	}
	
	@Test
	void matchSingleCharRangeOnlyMatchesThatChar() {
		CharRangeRule rule = new CharRangeRule('m', 'm');
		assertTrue(rule.match('m'));
		assertFalse(rule.match('l'));
		assertFalse(rule.match('n'));
	}
}
