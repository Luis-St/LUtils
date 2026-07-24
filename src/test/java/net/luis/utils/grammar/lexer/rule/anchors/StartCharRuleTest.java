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

package net.luis.utils.grammar.lexer.rule.anchors;

import net.luis.utils.grammar.lexer.CharRuleMatch;
import net.luis.utils.grammar.lexer.stream.CharStream;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StartCharRule}.<br>
 *
 * @author Luis-St
 */
class StartCharRuleTest {
	
	@Test
	void matchInputWithNullStreamThrows() {
		assertThrows(NullPointerException.class, () -> StartCharRule.INPUT.match(null));
	}
	
	@Test
	void matchLineWithNullStreamThrows() {
		assertThrows(NullPointerException.class, () -> StartCharRule.LINE.match(null));
	}
	
	@Test
	void matchInputAtStartOfInputMatches() {
		CharStream stream = CharStream.createImmutable("abc", 0);
		CharRuleMatch match = StartCharRule.INPUT.match(stream);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertEquals("", match.matched());
	}
	
	@Test
	void matchInputNotAtStartOfInputDoesNotMatch() {
		CharStream stream = CharStream.createImmutable("abc", 1);
		
		assertNull(StartCharRule.INPUT.match(stream));
	}
	
	@Test
	void matchLineAtStartOfInputMatches() {
		CharStream stream = CharStream.createImmutable("abc", 0);
		CharRuleMatch match = StartCharRule.LINE.match(stream);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
	}
	
	@Test
	void matchLineAfterNewlineCharacterMatches() {
		CharStream stream = CharStream.createImmutable("a\nb", 2);
		CharRuleMatch match = StartCharRule.LINE.match(stream);
		
		assertNotNull(match);
		assertEquals(2, match.startIndex());
		assertEquals(2, match.endIndex());
	}
	
	@Test
	void matchLineMidLineDoesNotMatch() {
		CharStream stream = CharStream.createImmutable("abc", 2);
		
		assertNull(StartCharRule.LINE.match(stream));
	}
	
	@Test
	void matchInputOnEmptyStreamMatches() {
		CharStream stream = CharStream.createImmutable("", 0);
		CharRuleMatch match = StartCharRule.INPUT.match(stream);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
	}
	
	@Test
	void matchDoesNotAdvanceOrConsumeStream() {
		CharStream stream = CharStream.createImmutable("a", 0);
		CharRuleMatch match = StartCharRule.INPUT.match(stream);
		
		assertNotNull(match);
		assertEquals(0, match.startIndex());
		assertEquals(0, match.endIndex());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void matchLineAcrossMultipleNewlinesInMultiLineInput() {
		String input = "ab\ncd\n";
		
		assertNotNull(StartCharRule.LINE.match(CharStream.createImmutable(input, 0)));
		assertNull(StartCharRule.LINE.match(CharStream.createImmutable(input, 1)));
		assertNull(StartCharRule.LINE.match(CharStream.createImmutable(input, 2)));
		assertNotNull(StartCharRule.LINE.match(CharStream.createImmutable(input, 3)));
		assertNull(StartCharRule.LINE.match(CharStream.createImmutable(input, 4)));
		assertNull(StartCharRule.LINE.match(CharStream.createImmutable(input, 5)));
		assertNotNull(StartCharRule.LINE.match(CharStream.createImmutable(input, 6)));
	}
}
