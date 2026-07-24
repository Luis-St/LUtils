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

import net.luis.utils.grammar.lexer.CharRuleMatch;
import net.luis.utils.grammar.lexer.stream.CharStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link AnyCharRule}.<br>
 *
 * @author Luis-St
 */
class AnyCharRuleTest {
	
	@Test
	void constructDefaultInstance() {
		AnyCharRule rule = new AnyCharRule();
		assertNotNull(rule);
		assertEquals(new AnyCharRule(), rule);
	}
	
	@Test
	void matchAnyLetterCharReturnsTrue() {
		assertTrue(AnyCharRule.INSTANCE.match('a'));
	}
	
	@Test
	void matchAnyDigitCharReturnsTrue() {
		assertTrue(AnyCharRule.INSTANCE.match('5'));
	}
	
	@Test
	void matchAnySpecialCharReturnsTrue() {
		assertTrue(AnyCharRule.INSTANCE.match('!'));
	}
	
	@Test
	void matchViaCharStreamConsumesOneCharacter() {
		CharStream stream = CharStream.createMutable("x");
		CharRuleMatch match = AnyCharRule.INSTANCE.match(stream);
		assertNotNull(match);
		assertEquals("x", match.matched());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void instanceConstantIsReusable() {
		assertTrue(AnyCharRule.INSTANCE.match('a'));
		assertTrue(AnyCharRule.INSTANCE.match('b'));
	}
}
