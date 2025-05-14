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

package net.luis.utils.io.token.rule.rules;

import net.luis.utils.io.token.definition.TokenDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link TokenRule}.<br>
 *
 * @author Luis-St
 */
class TokenRuleTest {
	
	private static final TokenDefinition NUMBER_DEFINITION = (word) -> word.matches("\\d+");
	private static final TokenRule TEST_RULE = NUMBER_DEFINITION;
	
	@Test
	void optional() {
		assertInstanceOf(OptionalTokenRule.class , NUMBER_DEFINITION.optional());
	}
	
	@Test
	void repeatAtLeast() {
		assertThrows(IllegalArgumentException.class, () -> TEST_RULE.repeatAtLeast(-1));
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class , TEST_RULE.repeatAtLeast(2));
		
		assertEquals(2, repeatedRule.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeatedRule.maxOccurrences());
	}
	
	@Test
	void repeatExactly() {
		assertThrows(IllegalArgumentException.class, () -> TEST_RULE.repeatExactly(-1));
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class , TEST_RULE.repeatExactly(2));
		
		assertEquals(2, repeatedRule.minOccurrences());
		assertEquals(2, repeatedRule.maxOccurrences());
	}
	
	@Test
	void repeatAtMost() {
		assertThrows(IllegalArgumentException.class, () -> TEST_RULE.repeatAtMost(-1));
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class , TEST_RULE.repeatAtMost(2));
		
		assertEquals(0, repeatedRule.minOccurrences());
		assertEquals(2, repeatedRule.maxOccurrences());
	}
	
	@Test
	void repeatInfinitely() {
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class , TEST_RULE.repeatInfinitely());
		
		assertEquals(0, repeatedRule.minOccurrences());
		assertEquals(Integer.MAX_VALUE, repeatedRule.maxOccurrences());
	}
	
	@Test
	void repeatBetween() {
		assertThrows(IllegalArgumentException.class, () -> TEST_RULE.repeatBetween(-1, 3));
		assertThrows(IllegalArgumentException.class, () -> TEST_RULE.repeatBetween(5, 3));
		
		RepeatedTokenRule repeatedRule = assertInstanceOf(RepeatedTokenRule.class , TEST_RULE.repeatBetween(2, 5));
		assertEquals(2, repeatedRule.minOccurrences());
		assertEquals(5, repeatedRule.maxOccurrences());
	}
}
