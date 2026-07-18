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
import net.luis.utils.grammar.token.Token;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link LengthTokenRule}.<br>
 *
 * @author Luis-St
 */
class LengthTokenRuleTest {
	
	@Test
	void constructWithValidMinAndMaxLength() {
		LengthTokenRule rule = new LengthTokenRule(2, 5);
		
		assertEquals(2, rule.minLength());
		assertEquals(5, rule.maxLength());
	}
	
	@Test
	void constructWithEqualMinAndMaxLength() {
		LengthTokenRule rule = new LengthTokenRule(3, 3);
		
		assertEquals(3, rule.minLength());
		assertEquals(3, rule.maxLength());
	}
	
	@Test
	void constructWithNegativeMinLengthThrows() {
		assertThrows(IllegalArgumentException.class, () -> new LengthTokenRule(-1, 5));
	}
	
	@Test
	void constructWithNegativeMaxLengthThrows() {
		assertThrows(IllegalArgumentException.class, () -> new LengthTokenRule(0, -1));
	}
	
	@Test
	void constructWithMaxLengthLessThanMinLengthThrows() {
		assertThrows(IllegalArgumentException.class, () -> new LengthTokenRule(5, 2));
	}
	
	@Test
	void matchWithNullTokenThrows() {
		LengthTokenRule rule = new LengthTokenRule(0, 5);
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void matchReturnsTrueWhenLengthWithinRange() {
		LengthTokenRule rule = new LengthTokenRule(2, 5);
		
		assertTrue(rule.match(SimpleToken.createUnpositioned("abc")));
	}
	
	@Test
	void matchReturnsFalseWhenLengthBelowMinimum() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		
		assertFalse(rule.match(SimpleToken.createUnpositioned("ab")));
	}
	
	@Test
	void matchReturnsFalseWhenLengthAboveMaximum() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		
		assertFalse(rule.match(SimpleToken.createUnpositioned("abcdef")));
	}
	
	@Test
	void matchReturnsTrueAtExactMinimumBoundary() {
		LengthTokenRule rule = new LengthTokenRule(3, 5);
		
		assertTrue(rule.match(SimpleToken.createUnpositioned("abc")));
	}
	
	@Test
	void matchReturnsTrueAtExactMaximumBoundary() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		
		assertTrue(rule.match(SimpleToken.createUnpositioned("abc")));
	}
	
	@Test
	void matchReturnsTrueForEmptyTokenValueWhenMinimumIsZero() {
		LengthTokenRule rule = new LengthTokenRule(0, 5);
		
		assertTrue(rule.match(SimpleToken.createUnpositioned("")));
	}
	
	@Test
	void matchReturnsFalseForEmptyTokenValueWhenMinimumIsPositive() {
		LengthTokenRule rule = new LengthTokenRule(1, 5);
		
		assertFalse(rule.match(SimpleToken.createUnpositioned("")));
	}
	
	@Test
	void matchWithSingleAllowedLength() {
		LengthTokenRule rule = new LengthTokenRule(4, 4);
		
		assertTrue(rule.match(SimpleToken.createUnpositioned("abcd")));
	}
	
	@Test
	void matchViaTokenStreamConsumesMatchingToken() {
		Token token = SimpleToken.createUnpositioned("abc");
		TokenStream stream = TokenStream.createMutable(List.of(token));
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		
		TokenRuleMatch match = rule.match(stream, TokenRuleContext.empty());
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notNegatesLengthMatchResult() {
		LengthTokenRule rule = new LengthTokenRule(1, 3);
		TokenRule negated = rule.not();
		TokenRuleContext ctx = TokenRuleContext.empty();
		
		TokenStream tooLongStream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("abcdef")));
		assertNotNull(negated.match(tooLongStream, ctx));
		
		TokenStream matchingStream = TokenStream.createMutable(List.of(SimpleToken.createUnpositioned("abc")));
		assertNull(negated.match(matchingStream, ctx));
	}
}
