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

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharSetRule}.<br>
 *
 * @author Luis-St
 */
class CharSetRuleTest {
	
	@Test
	void constructWithValidCharacterSet() {
		CharSetRule rule = new CharSetRule(Set.of('+', '-', '*'));
		assertTrue(rule.characters().containsAll(Set.of('+', '-', '*')));
	}
	
	@Test
	void constructWithNullSet() {
		assertThrows(NullPointerException.class, () -> new CharSetRule(null));
	}
	
	@Test
	void constructSetIsDefensivelyCopied() {
		Set<Character> mutable = new HashSet<>(Set.of('+', '-'));
		CharSetRule rule = new CharSetRule(mutable);
		mutable.add('*');
		assertEquals(Set.of('+', '-'), rule.characters());
	}
	
	@Test
	void constructWithEmptySet() {
		assertThrows(IllegalArgumentException.class, () -> new CharSetRule(Set.of()));
	}
	
	@Test
	void matchCharInSetReturnsTrue() {
		assertTrue(new CharSetRule(Set.of('+', '-')).match('+'));
	}
	
	@Test
	void matchCharNotInSetReturnsFalse() {
		assertFalse(new CharSetRule(Set.of('+', '-')).match('*'));
	}
	
	@Test
	void matchSingleCharacterSet() {
		assertTrue(new CharSetRule(Set.of('#')).match('#'));
	}
	
	@Test
	void matchLargerOperatorCharacterSet() {
		CharSetRule rule = new CharSetRule(Set.of('+', '-', '*', '/', '%'));
		assertTrue(rule.match('/'));
		assertFalse(rule.match('='));
	}
}
