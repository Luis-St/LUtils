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
import net.luis.utils.grammar.lexer.rule.anchors.EndCharRule;
import net.luis.utils.grammar.lexer.rule.anchors.StartCharRule;
import net.luis.utils.grammar.lexer.rule.matchers.AnyCharRule;
import net.luis.utils.grammar.lexer.stream.CharStream;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link CharRules}.<br>
 *
 * @author Luis-St
 */
class CharRulesTest {
	
	@Test
	void anyOfWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> CharRules.anyOf((char[]) null));
	}
	
	@Test
	void anyOfWithEmptyArrayThrows() {
		assertThrows(IllegalArgumentException.class, CharRules::anyOf);
	}
	
	@Test
	void setWithNullSetThrows() {
		assertThrows(NullPointerException.class, () -> CharRules.set(null));
	}
	
	@Test
	void setWithEmptySetThrows() {
		assertThrows(IllegalArgumentException.class, () -> CharRules.set(Set.of()));
	}
	
	@Test
	void rangeWithEndLessThanStartThrows() {
		assertThrows(IllegalArgumentException.class, () -> CharRules.range('z', 'a'));
	}
	
	@Test
	void sequenceWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> CharRules.sequence((CharRule[]) null));
	}
	
	@Test
	void sequenceWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> CharRules.sequence(CharRules.literal('a'), null));
	}
	
	@Test
	void sequenceWithFewerThanTwoRulesThrows() {
		assertThrows(IllegalArgumentException.class, () -> CharRules.sequence(CharRules.literal('a')));
	}
	
	@Test
	void sequenceWithNoRulesThrows() {
		assertThrows(IllegalArgumentException.class, CharRules::sequence);
	}
	
	@Test
	void anyRuleArrayWithNullArrayThrows() {
		assertThrows(NullPointerException.class, () -> CharRules.any((CharRule[]) null));
	}
	
	@Test
	void anyRuleArrayWithNullElementThrows() {
		assertThrows(NullPointerException.class, () -> CharRules.any(CharRules.literal('a'), null));
	}
	
	@Test
	void anyRuleArrayWithFewerThanTwoRulesThrows() {
		assertThrows(IllegalArgumentException.class, () -> CharRules.any(CharRules.literal('a')));
	}
	
	@Test
	void anyRuleArrayWithNoRulesThrows() {
		assertThrows(IllegalArgumentException.class, () -> CharRules.any(new CharRule[0]));
	}
	
	@Test
	void optionalWithNullRuleThrows() {
		assertThrows(NullPointerException.class, () -> CharRules.optional(null));
	}
	
	@Test
	void atLeastWithNegativeMinThrows() {
		assertThrows(IllegalArgumentException.class, () -> CharRules.atLeast(CharRules.literal('a'), -1));
	}
	
	@Test
	void exactlyWithNegativeRepeatsThrows() {
		assertThrows(IllegalArgumentException.class, () -> CharRules.exactly(CharRules.literal('a'), -1));
	}
	
	@Test
	void atMostWithNegativeMaxThrows() {
		assertThrows(IllegalArgumentException.class, () -> CharRules.atMost(CharRules.literal('a'), -1));
	}
	
	@Test
	void betweenWithMaxLessThanMinThrows() {
		assertThrows(IllegalArgumentException.class, () -> CharRules.between(CharRules.literal('a'), 5, 2));
	}
	
	@Test
	void betweenWithNullRuleThrows() {
		assertThrows(NullPointerException.class, () -> CharRules.between(null, 0, 1));
	}
	
	@Test
	void escapedWithNullRuleThrows() {
		assertThrows(NullPointerException.class, () -> CharRules.escaped(null));
	}
	
	@Test
	void anyOfWithSingleCharacterSucceeds() {
		assertDoesNotThrow(() -> CharRules.anyOf('a'));
	}
	
	@Test
	void setWithNonEmptySetSucceeds() {
		assertDoesNotThrow(() -> CharRules.set(Set.of('a')));
	}
	
	@Test
	void rangeWithStartEqualToEndSucceeds() {
		assertDoesNotThrow(() -> CharRules.range('a', 'a'));
	}
	
	@Test
	void sequenceWithExactlyTwoRulesSucceeds() {
		assertDoesNotThrow(() -> CharRules.sequence(CharRules.literal('a'), CharRules.literal('b')));
	}
	
	@Test
	void anyRuleArrayWithExactlyTwoRulesSucceeds() {
		assertDoesNotThrow(() -> CharRules.any(CharRules.literal('a'), CharRules.literal('b')));
	}
	
	@Test
	void betweenWithMinEqualToMaxSucceeds() {
		assertDoesNotThrow(() -> CharRules.between(CharRules.literal('a'), 3, 3));
	}
	
	@Test
	void betweenWithMinAndMaxBothZeroThrows() {
		assertThrows(IllegalArgumentException.class, () -> CharRules.between(CharRules.literal('a'), 0, 0));
	}
	
	@Test
	void literalCreatesMatchingRule() {
		assertNotNull(CharRules.literal('x').match(CharStream.createMutable("x")));
	}
	
	@Test
	void digitCreatesRuleMatchingDigits() {
		CharRule rule = CharRules.digit();
		assertNotNull(rule.match(CharStream.createMutable("5")));
		assertNull(rule.match(CharStream.createMutable("a")));
	}
	
	@Test
	void letterCreatesRuleMatchingLettersOfEitherCase() {
		CharRule rule = CharRules.letter();
		assertNotNull(rule.match(CharStream.createMutable("a")));
		assertNotNull(rule.match(CharStream.createMutable("Z")));
		assertNull(rule.match(CharStream.createMutable("5")));
	}
	
	@Test
	void letterOrDigitCreatesRuleMatchingBoth() {
		CharRule rule = CharRules.letterOrDigit();
		assertNotNull(rule.match(CharStream.createMutable("a")));
		assertNotNull(rule.match(CharStream.createMutable("5")));
		assertNull(rule.match(CharStream.createMutable("_")));
	}
	
	@Test
	void whitespaceCreatesRuleMatchingWhitespaceCharacters() {
		CharRule rule = CharRules.whitespace();
		assertNotNull(rule.match(CharStream.createMutable(" ")));
		assertNotNull(rule.match(CharStream.createMutable("\t")));
		assertNotNull(rule.match(CharStream.createMutable("\n")));
		assertNotNull(rule.match(CharStream.createMutable("\r")));
		assertNotNull(rule.match(CharStream.createMutable("\f")));
		assertNull(rule.match(CharStream.createMutable("a")));
	}
	
	@Test
	void anyReturnsSingletonInstance() {
		assertSame(AnyCharRule.INSTANCE, CharRules.any());
	}
	
	@Test
	void escapedWithNoArgumentMatchesBackslashFollowedByAnyCharacter() {
		CharRule rule = CharRules.escaped();
		assertNotNull(rule.match(CharStream.createMutable("\\n")));
		assertNull(rule.match(CharStream.createMutable("n")));
	}
	
	@Test
	void escapedWithRuleMatchesBackslashFollowedByRestrictedCharacter() {
		CharRule rule = CharRules.escaped(CharRules.digit());
		assertNotNull(rule.match(CharStream.createMutable("\\5")));
		assertNull(rule.match(CharStream.createMutable("\\a")));
	}
	
	@Test
	void startOfInputReturnsStartCharRuleInputConstant() {
		assertSame(StartCharRule.INPUT, CharRules.startOfInput());
	}
	
	@Test
	void startOfLineReturnsStartCharRuleLineConstant() {
		assertSame(StartCharRule.LINE, CharRules.startOfLine());
	}
	
	@Test
	void endOfInputReturnsEndCharRuleInputConstant() {
		assertSame(EndCharRule.INPUT, CharRules.endOfInput());
	}
	
	@Test
	void endOfLineReturnsEndCharRuleLineConstant() {
		assertSame(EndCharRule.LINE, CharRules.endOfLine());
	}
	
	@Test
	void atLeastCreatesRuleRequiringMinimumOccurrences() {
		CharRule rule = CharRules.atLeast(CharRules.literal('a'), 2);
		assertNull(rule.match(CharStream.createMutable("a")));
		assertNotNull(rule.match(CharStream.createMutable("aa")));
		assertNotNull(rule.match(CharStream.createMutable("aaa")));
	}
	
	@Test
	void exactlyCreatesRuleRequiringExactOccurrences() {
		CharRule rule = CharRules.exactly(CharRules.literal('a'), 3);
		assertNull(rule.match(CharStream.createMutable("aa")));
		CharRuleMatch exactMatch = rule.match(CharStream.createMutable("aaa"));
		assertNotNull(exactMatch);
		assertEquals(3, exactMatch.matched().length());
		CharStream longerStream = CharStream.createMutable("aaaa");
		assertNotNull(rule.match(longerStream));
		assertEquals(3, longerStream.getCurrentIndex());
	}
	
	@Test
	void atMostCreatesRuleAllowingUpToMaxOccurrences() {
		CharRule rule = CharRules.atMost(CharRules.literal('a'), 2);
		assertNotNull(rule.match(CharStream.createMutable("")));
		assertNotNull(rule.match(CharStream.createMutable("aa")));
		CharStream stream = CharStream.createMutable("aaa");
		assertNotNull(rule.match(stream));
		assertEquals(2, stream.getCurrentIndex());
	}
	
	@Test
	void zeroOrMoreCreatesRuleMatchingEmptyInput() {
		CharRule rule = CharRules.zeroOrMore(CharRules.literal('a'));
		CharStream stream = CharStream.createMutable("b");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals("", match.matched());
		assertEquals(0, stream.getCurrentIndex());
	}
	
	@Test
	void oneOrMoreCreatesRuleRequiringAtLeastOneOccurrence() {
		CharRule rule = CharRules.oneOrMore(CharRules.literal('a'));
		assertNull(rule.match(CharStream.createMutable("")));
		assertNull(rule.match(CharStream.createMutable("b")));
		assertNotNull(rule.match(CharStream.createMutable("a")));
		assertNotNull(rule.match(CharStream.createMutable("aaa")));
	}
	
	@Test
	void anyOfPreservesInsertionOrderAndDeduplicatesCharacters() {
		CharRule rule = CharRules.anyOf('a', 'b', 'a', 'c');
		assertNotNull(rule.match(CharStream.createMutable("a")));
		assertNotNull(rule.match(CharStream.createMutable("b")));
		assertNotNull(rule.match(CharStream.createMutable("c")));
		assertNull(rule.match(CharStream.createMutable("d")));
	}
	
	@Test
	void sequenceOfMultipleRulesMatchesConcatenatedPattern() {
		CharRule rule = CharRules.sequence(CharRules.literal('#'), CharRules.digit(), CharRules.letter());
		CharStream stream = CharStream.createMutable("#5x");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals(3, match.matched().length());
		assertEquals(3, stream.getCurrentIndex());
		assertNull(rule.match(CharStream.createMutable("5#x")));
	}
	
	@Test
	void nestedQuantifiersAndAlternationComposeCorrectly() {
		CharRule rule = CharRules.oneOrMore(CharRules.any(CharRules.letterOrDigit(), CharRules.whitespace()));
		CharStream stream = CharStream.createMutable("a1 b2");
		CharRuleMatch match = rule.match(stream);
		assertNotNull(match);
		assertEquals(5, match.matched().length());
		CharStream trailingStream = CharStream.createMutable("a1 b2#");
		assertNotNull(rule.match(trailingStream));
		assertEquals(5, trailingStream.getCurrentIndex());
	}
}
