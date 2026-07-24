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

package net.luis.utils.grammar.lexer.rule.combinators;

import net.luis.utils.grammar.lexer.CharRuleMatch;
import net.luis.utils.grammar.lexer.rule.CharRule;
import net.luis.utils.grammar.lexer.rule.CharRules;
import net.luis.utils.grammar.lexer.stream.CharStream;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharAnyOfRule}.<br>
 *
 * @author Luis-St
 */
class CharAnyOfRuleTest {
	
	@Test
	void constructWithValidRules() {
		CharRule literalA = CharRules.literal('a');
		CharRule literalB = CharRules.literal('b');
		
		CharAnyOfRule rule = new CharAnyOfRule(List.of(literalA, literalB));
		
		assertTrue(rule.charRules().contains(literalA));
		assertTrue(rule.charRules().contains(literalB));
	}
	
	@Test
	void constructWithNullListThrows() {
		assertThrows(NullPointerException.class, () -> new CharAnyOfRule(null));
	}
	
	@Test
	void constructWithNullElementInListThrows() {
		assertThrows(NullPointerException.class, () -> new CharAnyOfRule(Arrays.asList(CharRules.literal('a'), null)));
	}
	
	@Test
	void constructListIsDefensivelyCopied() {
		List<CharRule> list = new ArrayList<>();
		list.add(CharRules.literal('a'));
		list.add(CharRules.literal('b'));
		
		CharAnyOfRule rule = new CharAnyOfRule(list);
		list.add(CharRules.literal('c'));
		
		assertEquals(2, rule.charRules().size());
	}
	
	@Test
	void constructWithEmptyListThrows() {
		assertThrows(IllegalArgumentException.class, () -> new CharAnyOfRule(List.of()));
	}
	
	@Test
	void constructWithSingleRuleThrows() {
		assertThrows(IllegalArgumentException.class, () -> new CharAnyOfRule(List.of(CharRules.literal('a'))));
	}
	
	@Test
	void matchWithNullStreamThrows() {
		CharAnyOfRule rule = new CharAnyOfRule(List.of(CharRules.literal('a'), CharRules.literal('b')));
		
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void notWithNonNegatableMemberRuleThrows() {
		CharRule nonNegatable = stream -> null;
		CharAnyOfRule rule = new CharAnyOfRule(List.of(nonNegatable, CharRules.literal('a')));
		
		assertThrows(UnsupportedOperationException.class, rule::not);
	}
	
	@Test
	void constructWithExactlyTwoRulesSucceeds() {
		assertDoesNotThrow(() -> new CharAnyOfRule(List.of(CharRules.literal('a'), CharRules.literal('b'))));
	}
	
	@Test
	void matchReturnsFirstMatchingRuleAndStopsIterating() {
		CharAnyOfRule rule = new CharAnyOfRule(List.of(CharRules.literal('a'), CharRules.anyOf('a', 'b')));
		CharStream stream = CharStream.createMutable("a");
		
		CharRuleMatch match = rule.match(stream);
		
		assertNotNull(match);
		assertEquals("a", match.matched());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchSkipsNonMatchingRulesAndTriesNext() {
		CharAnyOfRule rule = new CharAnyOfRule(List.of(CharRules.literal('x'), CharRules.literal('a')));
		CharStream stream = CharStream.createMutable("a");
		
		CharRuleMatch match = rule.match(stream);
		
		assertNotNull(match);
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchReturnsNullWhenNoRuleMatches() {
		CharAnyOfRule rule = new CharAnyOfRule(List.of(CharRules.literal('x'), CharRules.literal('y')));
		CharStream stream = CharStream.createMutable("a");
		
		CharRuleMatch match = rule.match(stream);
		
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchWithThreeAlternativesSelectsCorrectOne() {
		CharAnyOfRule rule = new CharAnyOfRule(List.of(CharRules.literal('a'), CharRules.literal('b'), CharRules.literal('c')));
		CharStream stream = CharStream.createMutable("c");
		
		CharRuleMatch match = rule.match(stream);
		
		assertNotNull(match);
		assertEquals("c", match.matched());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notNegatesEachMemberRuleIntoSequenceRule() {
		CharAnyOfRule rule = new CharAnyOfRule(List.of(CharRules.range('a', 'c'), CharRules.range('x', 'z')));
		
		CharRule negated = rule.not();
		
		assertInstanceOf(CharSequenceRule.class, negated);
		assertNull(negated.match(CharStream.createMutable("b")));
		assertNull(negated.match(CharStream.createMutable("y")));
		assertNull(negated.match(CharStream.createMutable("m")));
		assertNotNull(negated.match(CharStream.createMutable("mn")));
	}
	
	@Test
	void matchWithNestedAnyOfRulesComposesCorrectly() {
		CharAnyOfRule rule = new CharAnyOfRule(List.of(CharRules.digit(), CharRules.any(CharRules.literal('+'), CharRules.literal('-'))));
		CharStream stream = CharStream.createMutable("-5");
		
		CharRuleMatch first = rule.match(stream);
		assertNotNull(first);
		assertEquals("-", first.matched());
		assertEquals(1, stream.getCurrentIndex());
		
		CharRuleMatch second = rule.match(stream);
		assertNotNull(second);
		assertEquals("5", second.matched());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchDoesNotConsumeOuterStreamOnPartialAttemptsBeforeSuccess() {
		CharAnyOfRule rule = new CharAnyOfRule(List.of(CharRules.literal('x'), CharRules.literal('y'), CharRules.sequence(CharRules.literal('a'), CharRules.literal('b'))));
		CharStream stream = CharStream.createMutable("ab");
		
		CharRuleMatch match = rule.match(stream);
		
		assertNotNull(match);
		assertEquals(2, match.endIndex() - match.startIndex());
		assertEquals(2, stream.getCurrentIndex());
	}
	//endregion
}
