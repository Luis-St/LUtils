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

import net.luis.utils.grammar.lexer.CharRuleMatch;
import net.luis.utils.grammar.lexer.stream.CharStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link NegatableCharRule}.<br>
 *
 * @author Luis-St
 */
class NegatableCharRuleTest {
	
	@Test
	void matchStreamWithNullStreamThrows() {
		NegatableCharRule rule = c -> c == 'a';
		assertThrows(NullPointerException.class, () -> rule.match(null));
	}
	
	@Test
	void notMatchStreamWithNullStreamThrows() {
		NegatableCharRule rule = c -> c == 'a';
		CharRule negated = rule.not();
		assertThrows(NullPointerException.class, () -> negated.match(null));
	}
	
	@Test
	void matchStreamAtEndOfStreamReturnsNull() {
		NegatableCharRule rule = c -> true;
		CharStream stream = CharStream.createMutable("");
		assertNull(rule.match(stream));
	}
	
	@Test
	void matchStreamWithMatchingCharacterConsumesIt() {
		NegatableCharRule rule = c -> c == 'a';
		CharStream stream = CharStream.createMutable("a");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals("a", match.matched());
		assertEquals(0, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchStreamWithNonMatchingCharacterReturnsNullWithoutConsuming() {
		NegatableCharRule rule = c -> c == 'a';
		CharStream stream = CharStream.createMutable("b");
		assertNull(rule.match(stream));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void notMatchStreamAtEndOfStreamReturnsNull() {
		NegatableCharRule rule = c -> c == 'a';
		CharRule negated = rule.not();
		CharStream stream = CharStream.createMutable("");
		assertNull(negated.match(stream));
	}
	
	@Test
	void notMatchStreamWithOriginalNonMatchConsumesCharacter() {
		NegatableCharRule rule = c -> c == 'a';
		CharRule negated = rule.not();
		CharStream stream = CharStream.createMutable("b");
		CharRuleMatch match = negated.match(stream);
		assertNotNull(match);
		assertEquals("b", match.matched());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void notMatchStreamWithOriginalMatchReturnsNullWithoutConsuming() {
		NegatableCharRule rule = c -> c == 'a';
		CharRule negated = rule.not();
		CharStream stream = CharStream.createMutable("a");
		assertNull(negated.match(stream));
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void notOfNotReturnsOriginalRuleInstance() {
		NegatableCharRule rule = c -> c == 'x';
		CharRule doubleNegated = rule.not().not();
		assertSame(rule, doubleNegated);
	}
	
	@Test
	void matchStreamSequentialCallsAdvanceIndexEachTime() {
		NegatableCharRule rule = c -> c == 'a';
		CharStream stream = CharStream.createMutable("aab");
		
		CharRuleMatch first = rule.match(stream);
		assertNotNull(first);
		assertEquals("a", first.matched());
		assertEquals(1, stream.getCurrentIndex());
		
		CharRuleMatch second = rule.match(stream);
		assertNotNull(second);
		assertEquals("a", second.matched());
		assertEquals(2, stream.getCurrentIndex());
		
		assertNull(rule.match(stream));
		assertEquals(2, stream.getCurrentIndex());
	}
}
