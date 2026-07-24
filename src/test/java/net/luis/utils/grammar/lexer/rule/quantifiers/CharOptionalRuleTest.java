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
import net.luis.utils.grammar.lexer.rule.CharRule;
import net.luis.utils.grammar.lexer.rule.combinators.CharSequenceRule;
import net.luis.utils.grammar.lexer.rule.matchers.LiteralCharRule;
import net.luis.utils.grammar.lexer.stream.CharStream;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharOptionalRule}.<br>
 *
 * @author Luis-St
 */
class CharOptionalRuleTest {
	
	@Test
	void constructWithValidCharRule() {
		LiteralCharRule inner = new LiteralCharRule('a');
		CharOptionalRule rule = new CharOptionalRule(inner);
		assertEquals(inner, rule.charRule());
	}
	
	@Test
	void constructWithNullCharRule() {
		assertThrows(NullPointerException.class, () -> new CharOptionalRule(null));
	}
	
	@Test
	void matchWithNullStream() {
		CharOptionalRule rule = new CharOptionalRule(new LiteralCharRule('a'));
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void notThrowsWhenInnerRuleDoesNotSupportNegation() {
		CharRule plain = stream -> null;
		CharOptionalRule rule = new CharOptionalRule(plain);
		assertThrows(UnsupportedOperationException.class, rule::not);
	}
	
	@Test
	void matchInnerRuleSucceeds() {
		CharOptionalRule rule = new CharOptionalRule(new LiteralCharRule('a'));
		CharStream stream = CharStream.createMutable("a");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals("a", match.matched());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchInnerRuleFailsReturnsEmptyMatch() {
		CharOptionalRule rule = new CharOptionalRule(new LiteralCharRule('a'));
		CharStream stream = CharStream.createMutable("b");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertEquals("", match.matched());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchOptionalAtNonZeroOffset() {
		CharStream stream = CharStream.createMutable("xa");
		stream.advanceTo(1);
		CharOptionalRule rule = new CharOptionalRule(new LiteralCharRule('a'));
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals("a", match.matched());
		assertEquals(1, match.startIndex());
		assertEquals(2, match.endIndex());
	}
	
	@Test
	void matchOptionalAtEndOfStream() {
		CharOptionalRule rule = new CharOptionalRule(new LiteralCharRule('a'));
		CharRuleMatch match = rule.match(CharStream.createMutable(""));
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
	}
	
	@Test
	void matchOptionalAtNonZeroOffsetInnerRuleFails() {
		CharStream stream = CharStream.createMutable("xb");
		stream.advanceTo(1);
		CharOptionalRule rule = new CharOptionalRule(new LiteralCharRule('a'));
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals("", match.matched());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notWrapsNegatedInnerRule() {
		CharRule negated = new CharOptionalRule(new LiteralCharRule('a')).not();
		
		CharStream nonMatching = CharStream.createMutable("b");
		CharRuleMatch match = negated.match(nonMatching);
		assertNotNull(match);
		assertEquals("b", match.matched());
		assertEquals(1, nonMatching.getCurrentIndex());
		
		CharStream matching = CharStream.createMutable("a");
		CharRuleMatch empty = negated.match(matching);
		assertNotNull(empty);
		assertEquals(0, empty.startIndex());
		assertEquals(0, empty.endIndex());
		assertEquals("", empty.matched());
	}
	
	@Test
	void matchOptionalWrappingSequenceRule() {
		CharOptionalRule rule = new CharOptionalRule(new CharSequenceRule(List.of(new LiteralCharRule('a'), new LiteralCharRule('b'))));
		
		CharStream matching = CharStream.createMutable("ab");
		CharRuleMatch match = rule.match(matching);
		assertNotNull(match);
		assertEquals("ab", match.matched());
		assertEquals(2, matching.getCurrentIndex());
		
		CharStream nonMatching = CharStream.createMutable("xy");
		CharRuleMatch empty = rule.match(nonMatching);
		assertNotNull(empty);
		assertEquals(0, empty.startIndex());
		assertEquals(0, empty.endIndex());
		assertEquals(0, nonMatching.getCurrentIndex());
	}
}
