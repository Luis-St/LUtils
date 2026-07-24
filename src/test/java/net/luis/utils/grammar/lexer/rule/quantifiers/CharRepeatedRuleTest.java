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

package net.luis.utils.grammar.lexer.rule.quantifiers;

import net.luis.utils.grammar.lexer.CharRuleMatch;
import net.luis.utils.grammar.lexer.rule.combinators.CharSequenceRule;
import net.luis.utils.grammar.lexer.rule.matchers.LiteralCharRule;
import net.luis.utils.grammar.lexer.stream.CharStream;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharRepeatedRule}.<br>
 *
 * @author Luis-St
 */
class CharRepeatedRuleTest {
	
	@Test
	void constructWithMinAndMaxOccurrences() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 1, 3);
		assertEquals(1, rule.minOccurrences());
		assertEquals(3, rule.maxOccurrences());
	}
	
	@Test
	void constructWithExactOccurrences() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 2);
		assertEquals(2, rule.minOccurrences());
		assertEquals(2, rule.maxOccurrences());
	}
	
	@Test
	void constructWithNullCharRule() {
		assertThrows(NullPointerException.class, () -> new CharRepeatedRule(null, 1, 3));
	}
	
	@Test
	void constructExactWithNullCharRule() {
		assertThrows(NullPointerException.class, () -> new CharRepeatedRule(null, 2));
	}
	
	@Test
	void constructWithNegativeMinOccurrences() {
		assertThrows(IllegalArgumentException.class, () -> new CharRepeatedRule(new LiteralCharRule('a'), -1, 3));
	}
	
	@Test
	void constructWithMaxLessThanMin() {
		assertThrows(IllegalArgumentException.class, () -> new CharRepeatedRule(new LiteralCharRule('a'), 3, 1));
	}
	
	@Test
	void constructWithMinAndMaxBothZero() {
		assertThrows(IllegalArgumentException.class, () -> new CharRepeatedRule(new LiteralCharRule('a'), 0, 0));
	}
	
	@Test
	void constructExactWithNegativeOccurrences() {
		assertThrows(IllegalArgumentException.class, () -> new CharRepeatedRule(new LiteralCharRule('a'), -1));
	}
	
	@Test
	void matchWithNullStream() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 1, 3);
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void constructExactWithZeroOccurrences() {
		assertThrows(IllegalArgumentException.class, () -> new CharRepeatedRule(new LiteralCharRule('a'), 0));
	}
	
	@Test
	void matchLoopNotTakenWhenStreamEmpty() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 0, 3);
		CharRuleMatch match = rule.match(CharStream.createMutable(""));
		assertNotNull(match);
		assertEquals("", match.matched());
	}
	
	@Test
	void matchLoopTakenUntilInnerRuleFails() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 0, 5);
		CharRuleMatch match = rule.match(CharStream.createMutable("aab"));
		assertNotNull(match);
		assertEquals("aa", match.matched());
	}
	
	@Test
	void matchLoopStopsAtMaxOccurrences() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 1, 2);
		CharStream stream = CharStream.createMutable("aaaa");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals("aa", match.matched());
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void matchBreaksOnZeroWidthInnerMatch() {
		CharRepeatedRule rule = new CharRepeatedRule(new CharOptionalRule(new LiteralCharRule('x')), 0, 5);
		CharStream stream = CharStream.createMutable("y");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals("", match.matched());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchPostLoopOccurrencesBelowMinReturnsNull() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 3, 5);
		CharStream stream = CharStream.createMutable("aab");
		assertNull(rule.match(stream));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchPostLoopOccurrencesWithinRangeSucceeds() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 1, 3);
		CharRuleMatch match = rule.match(CharStream.createMutable("aa"));
		assertNotNull(match);
		assertEquals("aa", match.matched());
	}
	
	@Test
	void matchExactOccurrencesRule() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 2);
		CharRuleMatch match = rule.match(CharStream.createMutable("aa"));
		assertNotNull(match);
		assertEquals("aa", match.matched());
	}
	
	@Test
	void matchZeroOrMoreStyleRule() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 0, 10);
		CharRuleMatch match = rule.match(CharStream.createMutable("aaa"));
		assertNotNull(match);
		assertEquals("aaa", match.matched());
	}
	
	@Test
	void matchOneOrMoreStyleRuleFailsOnNoOccurrences() {
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 1, 10);
		assertNull(rule.match(CharStream.createMutable("bbb")));
	}
	
	@Test
	void matchRepeatedSequenceRuleAcrossMultipleGroups() {
		CharRepeatedRule rule = new CharRepeatedRule(new CharSequenceRule(List.of(new LiteralCharRule('a'), new LiteralCharRule('b'))), 1, 2);
		CharStream stream = CharStream.createMutable("ababab");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals("abab", match.matched());
		assertEquals(4, stream.getCurrentIndex());
	}
	
	@Test
	void matchRepeatedRuleAtNonZeroStreamOffset() {
		CharStream stream = CharStream.createMutable("xaaa");
		stream.advanceTo(1);
		CharRepeatedRule rule = new CharRepeatedRule(new LiteralCharRule('a'), 1, 3);
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(4, match.endIndex());
		assertEquals("aaa", match.matched());
	}
}
