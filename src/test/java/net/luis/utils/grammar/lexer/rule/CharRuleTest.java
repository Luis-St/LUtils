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

package net.luis.utils.grammar.lexer.rule;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharRule}.<br>
 *
 * @author Luis-St
 */
class CharRuleTest {
	
	@Test
	void notOnDefaultImplementationThrowsUnsupportedOperationException() {
		CharRule rule = stream -> null;
		assertThrows(UnsupportedOperationException.class, rule::not);
	}
	
	@Test
	void atLeastWithNegativeMinThrowsIllegalArgumentException() {
		CharRule rule = stream -> null;
		assertThrows(IllegalArgumentException.class, () -> rule.atLeast(-1));
	}
	
	@Test
	void exactlyWithNegativeRepeatsThrowsIllegalArgumentException() {
		CharRule rule = stream -> null;
		assertThrows(IllegalArgumentException.class, () -> rule.exactly(-1));
	}
	
	@Test
	void exactlyWithZeroRepeatsThrowsIllegalArgumentException() {
		CharRule rule = stream -> null;
		assertThrows(IllegalArgumentException.class, () -> rule.exactly(0));
	}
	
	@Test
	void atMostWithNegativeMaxThrowsIllegalArgumentException() {
		CharRule rule = stream -> null;
		assertThrows(IllegalArgumentException.class, () -> rule.atMost(-1));
	}
	
	@Test
	void atMostWithZeroMaxThrowsIllegalArgumentException() {
		CharRule rule = stream -> null;
		assertThrows(IllegalArgumentException.class, () -> rule.atMost(0));
	}
	
	@Test
	void betweenWithNegativeMinThrowsIllegalArgumentException() {
		CharRule rule = stream -> null;
		assertThrows(IllegalArgumentException.class, () -> rule.between(-1, 5));
	}
	
	@Test
	void betweenWithMaxLessThanMinThrowsIllegalArgumentException() {
		CharRule rule = stream -> null;
		assertThrows(IllegalArgumentException.class, () -> rule.between(5, 2));
	}
	
	@Test
	void betweenWithBothZeroThrowsIllegalArgumentException() {
		CharRule rule = stream -> null;
		assertThrows(IllegalArgumentException.class, () -> rule.between(0, 0));
	}
	
	@Test
	void atLeastWithZeroMinDoesNotThrow() {
		CharRule rule = stream -> null;
		assertDoesNotThrow(() -> rule.atLeast(0));
		assertNotNull(rule.atLeast(0));
	}
	
	@Test
	void betweenWithMinEqualsMaxDoesNotThrow() {
		CharRule rule = stream -> null;
		assertDoesNotThrow(() -> rule.between(3, 3));
		assertNotNull(rule.between(3, 3));
	}
	
	@Test
	void betweenWithMaxGreaterThanMinDoesNotThrow() {
		CharRule rule = stream -> null;
		assertDoesNotThrow(() -> rule.between(1, 5));
		assertNotNull(rule.between(1, 5));
	}
}
