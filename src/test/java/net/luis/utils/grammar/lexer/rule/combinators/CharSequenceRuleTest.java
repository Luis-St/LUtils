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
import net.luis.utils.grammar.lexer.rule.matchers.LiteralCharRule;
import net.luis.utils.grammar.lexer.stream.CharStream;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharSequenceRule}.<br>
 *
 * @author Luis-St
 */
class CharSequenceRuleTest {
	
	@Test
	void constructWithValidRules() {
		List<CharRule> rules = List.of(new LiteralCharRule('a'), new LiteralCharRule('b'));
		CharSequenceRule rule = new CharSequenceRule(rules);
		assertEquals(2, rule.charRules().size());
		assertEquals(rules, rule.charRules());
	}
	
	@Test
	void constructWithMoreThanTwoRules() {
		List<CharRule> rules = List.of(new LiteralCharRule('a'), new LiteralCharRule('b'), new LiteralCharRule('c'));
		CharSequenceRule rule = new CharSequenceRule(rules);
		assertEquals(3, rule.charRules().size());
	}
	
	@Test
	void constructWithNullList() {
		assertThrows(NullPointerException.class, () -> new CharSequenceRule(null));
	}
	
	@Test
	void constructWithNullElementInList() {
		List<CharRule> rules = Arrays.asList(new LiteralCharRule('a'), null);
		assertThrows(NullPointerException.class, () -> new CharSequenceRule(rules));
	}
	
	@Test
	void constructListIsDefensivelyCopied() {
		List<CharRule> mutable = new ArrayList<>();
		mutable.add(new LiteralCharRule('a'));
		mutable.add(new LiteralCharRule('b'));
		CharSequenceRule rule = new CharSequenceRule(mutable);
		mutable.add(new LiteralCharRule('c'));
		assertEquals(2, rule.charRules().size());
	}
	
	@Test
	void constructWithEmptyList() {
		assertThrows(IllegalArgumentException.class, () -> new CharSequenceRule(List.of()));
	}
	
	@Test
	void constructWithSingleRule() {
		assertThrows(IllegalArgumentException.class, () -> new CharSequenceRule(List.of(new LiteralCharRule('a'))));
	}
	
	@Test
	void matchWithNullStream() {
		CharSequenceRule rule = new CharSequenceRule(List.of(new LiteralCharRule('a'), new LiteralCharRule('b')));
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void notWithNonNegatableMemberRuleThrows() {
		CharRule nonNegatable = stream -> null;
		CharSequenceRule rule = new CharSequenceRule(List.of(nonNegatable, new LiteralCharRule('a')));
		assertThrows(UnsupportedOperationException.class, rule::not);
	}
	
	@Test
	void matchAllRulesSucceed() {
		CharSequenceRule rule = new CharSequenceRule(List.of(new LiteralCharRule('a'), new LiteralCharRule('b')));
		CharStream stream = CharStream.createMutable("ab");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals("ab", match.matched());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchFirstRuleFails() {
		CharSequenceRule rule = new CharSequenceRule(List.of(new LiteralCharRule('a'), new LiteralCharRule('b')));
		CharStream stream = CharStream.createMutable("xb");
		CharRuleMatch match = rule.match(stream);
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchLaterRuleFails() {
		CharSequenceRule rule = new CharSequenceRule(List.of(new LiteralCharRule('a'), new LiteralCharRule('b'), new LiteralCharRule('c')));
		CharStream stream = CharStream.createMutable("axc");
		CharRuleMatch match = rule.match(stream);
		assertNull(match);
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchSequenceAtNonZeroOffset() {
		CharSequenceRule rule = new CharSequenceRule(List.of(new LiteralCharRule('a'), new LiteralCharRule('b')));
		CharStream stream = CharStream.createMutable("xab", 1);
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(3, match.endIndex());
		assertEquals("ab", match.matched());
	}
	
	@Test
	void matchSequenceOfThreeLiterals() {
		CharSequenceRule rule = new CharSequenceRule(List.of(new LiteralCharRule('a'), new LiteralCharRule('b'), new LiteralCharRule('c')));
		CharStream stream = CharStream.createMutable("abc");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals("abc", match.matched());
	}
	
	@Test
	void notReturnsNegatedSequenceViaDeMorgan() {
		CharSequenceRule rule = new CharSequenceRule(List.of(new LiteralCharRule('a'), new LiteralCharRule('b')));
		CharRule negated = rule.not();
		assertInstanceOf(CharAnyOfRule.class, negated);
		CharRuleMatch diffMatch = negated.match(CharStream.createMutable("xy"));
		assertNotNull(diffMatch);
		CharRuleMatch sameMatch = negated.match(CharStream.createMutable("ab"));
		assertTrue(sameMatch == null || !"ab".equals(sameMatch.matched()));
	}
	
	@Test
	void matchNestedCharSequenceRule() {
		CharSequenceRule inner = new CharSequenceRule(List.of(new LiteralCharRule('a'), new LiteralCharRule('b')));
		CharSequenceRule outer = new CharSequenceRule(List.of(inner, new LiteralCharRule('c')));
		CharStream stream = CharStream.createMutable("abc");
		CharRuleMatch match = outer.match(stream);
		assertNotNull(match);
		assertEquals("abc", match.matched());
		assertEquals(3, stream.getCurrentIndex());
	}
}
