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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link EndCharRule}.<br>
 *
 * @author Luis-St
 */
class EndCharRuleTest {
	
	@Test
	void matchInputWithNullStreamThrows() {
		assertThrows(NullPointerException.class, () -> EndCharRule.INPUT.match(null));
	}
	
	@Test
	void matchLineWithNullStreamThrows() {
		assertThrows(NullPointerException.class, () -> EndCharRule.LINE.match(null));
	}
	
	@Test
	void matchInputAtEndOfInputMatches() {
		CharStream stream = CharStream.createImmutable("abc", 3);
		CharRuleMatch match = EndCharRule.INPUT.match(stream);
		assertNotNull(match);
		assertEquals(CharRuleMatch.empty(3), match);
	}
	
	@Test
	void matchInputBeforeEndOfInputDoesNotMatch() {
		CharStream stream = CharStream.createImmutable("abc", 1);
		assertNull(EndCharRule.INPUT.match(stream));
	}
	
	@Test
	void matchLineAtEndOfInputMatches() {
		CharStream stream = CharStream.createImmutable("abc", 3);
		CharRuleMatch match = EndCharRule.LINE.match(stream);
		assertNotNull(match);
		assertEquals(CharRuleMatch.empty(3), match);
	}
	
	@Test
	void matchLineBeforeNewlineCharacterMatches() {
		CharStream stream = CharStream.createImmutable("a\nb", 1);
		CharRuleMatch match = EndCharRule.LINE.match(stream);
		assertNotNull(match);
		assertEquals(CharRuleMatch.empty(1), match);
	}
	
	@Test
	void matchLineMidLineDoesNotMatch() {
		CharStream stream = CharStream.createImmutable("abc", 1);
		assertNull(EndCharRule.LINE.match(stream));
	}
	
	@Test
	void matchInputOnEmptyStreamMatches() {
		CharStream stream = CharStream.createImmutable("");
		CharRuleMatch match = EndCharRule.INPUT.match(stream);
		assertNotNull(match);
		assertEquals(CharRuleMatch.empty(0), match);
	}
	
	@Test
	void matchDoesNotAdvanceOrConsumeStream() {
		CharStream stream = CharStream.createMutable("a", 1);
		CharRuleMatch match = EndCharRule.INPUT.match(stream);
		assertNotNull(match);
		assertEquals(1, match.startIndex());
		assertEquals(1, match.endIndex());
		assertEquals(1, stream.getCurrentIndex());
	}
	
	@Test
	void matchLineAcrossMultipleNewlinesInMultiLineInput() {
		String input = "ab\ncd\n";
		List<LineMatchCase> cases = List.of(
			new LineMatchCase(0, false),
			new LineMatchCase(1, false),
			new LineMatchCase(2, true),
			new LineMatchCase(3, false),
			new LineMatchCase(4, false),
			new LineMatchCase(5, true),
			new LineMatchCase(6, true)
		);
		
		for (LineMatchCase testCase : cases) {
			CharStream stream = CharStream.createImmutable(input, testCase.index());
			CharRuleMatch match = EndCharRule.LINE.match(stream);
			assertEquals(testCase.shouldMatch(), match != null, "index " + testCase.index());
		}
	}
	
	private record LineMatchCase(int index, boolean shouldMatch) {}
}
