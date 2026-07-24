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

package net.luis.utils.grammar.lexer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharRuleMatch}.<br>
 *
 * @author Luis-St
 */
class CharRuleMatchTest {
	
	@Test
	void constructWithValidValues() {
		CharRuleMatch match = new CharRuleMatch(2, 5, "abc");
		assertEquals(2, match.startIndex());
		assertEquals(5, match.endIndex());
		assertEquals("abc", match.matched());
	}
	
	@Test
	void constructWithNullMatched() {
		assertThrows(NullPointerException.class, () -> new CharRuleMatch(0, 0, null));
	}
	
	@Test
	void constructWithEmptyMatchedString() {
		CharRuleMatch match = new CharRuleMatch(3, 3, "");
		assertEquals(3, match.startIndex());
		assertEquals(3, match.endIndex());
		assertTrue(match.matched().isEmpty());
	}
	
	@Test
	void constructWithStartEqualToEnd() {
		CharRuleMatch match = new CharRuleMatch(4, 4, "");
		assertEquals(match.startIndex(), match.endIndex());
	}
	
	@Test
	void emptyCreatesZeroWidthMatchAtGivenIndex() {
		CharRuleMatch match = CharRuleMatch.empty(7);
		assertEquals(7, match.startIndex());
		assertEquals(7, match.endIndex());
		assertEquals("", match.matched());
	}
	
	@Test
	void emptyAtIndexZero() {
		CharRuleMatch match = CharRuleMatch.empty(0);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
	}
	
	@Test
	void emptyWithNegativeIndex() {
		CharRuleMatch match = CharRuleMatch.empty(-3);
		assertEquals(-3, match.startIndex());
		assertEquals(-3, match.endIndex());
		assertEquals("", match.matched());
	}
}
