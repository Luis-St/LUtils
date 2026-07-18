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

package net.luis.utils.grammar.parser.rule.matchers;

import net.luis.utils.grammar.parser.TokenRuleMatch;
import net.luis.utils.grammar.parser.context.TokenRuleContext;
import net.luis.utils.grammar.parser.rule.TokenRule;
import net.luis.utils.grammar.parser.stream.TokenStream;
import net.luis.utils.grammar.token.SimpleToken;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ValueTokenRule}.<br>
 *
 * @author Luis-St
 */
class ValueTokenRuleTest {
	
	@Test
	void constructWithCharValue() {
		ValueTokenRule rule = new ValueTokenRule('a', false);
		
		assertEquals("a", rule.value());
		assertFalse(rule.ignoreCase());
	}
	
	@Test
	void constructWithStringValue() {
		ValueTokenRule rule = new ValueTokenRule("abc", true);
		
		assertEquals("abc", rule.value());
		assertTrue(rule.ignoreCase());
	}
	
	@Test
	void constructWithNullStringValue() {
		assertThrows(NullPointerException.class, () -> new ValueTokenRule(null, false));
	}
	
	@Test
	void constructWithEmptyStringValue() {
		assertThrows(IllegalArgumentException.class, () -> new ValueTokenRule("", false));
	}
	
	@Test
	void matchWithNullToken() {
		ValueTokenRule rule = new ValueTokenRule("abc", false);
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchIgnoreCaseTrueWithDifferentCaseReturnsTrue() {
		ValueTokenRule rule = new ValueTokenRule("abc", true);
		assertTrue(rule.match(SimpleToken.createUnpositioned("ABC")));
	}
	
	@Test
	void matchIgnoreCaseTrueWithDifferentValueReturnsFalse() {
		ValueTokenRule rule = new ValueTokenRule("abc", true);
		assertFalse(rule.match(SimpleToken.createUnpositioned("xyz")));
	}
	
	@Test
	void matchIgnoreCaseFalseWithExactValueReturnsTrue() {
		ValueTokenRule rule = new ValueTokenRule("abc", false);
		assertTrue(rule.match(SimpleToken.createUnpositioned("abc")));
	}
	
	@Test
	void matchIgnoreCaseFalseWithDifferentCaseReturnsFalse() {
		ValueTokenRule rule = new ValueTokenRule("abc", false);
		assertFalse(rule.match(SimpleToken.createUnpositioned("ABC")));
	}
	
	@Test
	void matchSingleCharacterValue() {
		ValueTokenRule rule = new ValueTokenRule('x', false);
		assertTrue(rule.match(SimpleToken.createUnpositioned("x")));
	}
	
	@Test
	void matchWithDifferentLengthValueReturnsFalse() {
		ValueTokenRule rule = new ValueTokenRule("abc", false);
		assertFalse(rule.match(SimpleToken.createUnpositioned("ab")));
	}
	
	@Test
	void matchViaTokenStreamConsumesMatchingToken() {
		ValueTokenRule rule = new ValueTokenRule("abc", false);
		TokenStream stream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("abc")));
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notNegatesValueMatchResult() {
		ValueTokenRule rule = new ValueTokenRule("abc", false);
		TokenRule negated = rule.not();
		
		TokenStream nonMatchingStream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("xyz")));
		TokenStream matchingStream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("abc")));
		
		assertNotNull(negated.match(nonMatchingStream, TokenRuleContext.empty()));
		assertNull(negated.match(matchingStream, TokenRuleContext.empty()));
	}
}
