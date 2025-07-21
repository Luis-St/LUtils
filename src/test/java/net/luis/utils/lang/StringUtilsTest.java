/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.lang;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link StringUtils}.<br>
 *
 * @author Luis-St
 */
@SuppressWarnings("RedundantArrayCreation")
class StringUtilsTest {
	
	@Test
	void getOppositeBracketReturnsCorrectBrackets() {
		assertEquals(')', StringUtils.getOppositeBracket('('));
		assertEquals('(', StringUtils.getOppositeBracket(')'));
		assertEquals(']', StringUtils.getOppositeBracket('['));
		assertEquals('[', StringUtils.getOppositeBracket(']'));
		assertEquals('}', StringUtils.getOppositeBracket('{'));
		assertEquals('{', StringUtils.getOppositeBracket('}'));
		assertEquals('>', StringUtils.getOppositeBracket('<'));
		assertEquals('<', StringUtils.getOppositeBracket('>'));
	}
	
	@Test
	void getOppositeBracketReturnsNullCharacterForNonBrackets() {
		assertEquals('\0', StringUtils.getOppositeBracket('a'));
		assertEquals('\0', StringUtils.getOppositeBracket('Z'));
		assertEquals('\0', StringUtils.getOppositeBracket('1'));
		assertEquals('\0', StringUtils.getOppositeBracket(' '));
		assertEquals('\0', StringUtils.getOppositeBracket('\n'));
		assertEquals('\0', StringUtils.getOppositeBracket('\0'));
	}
	
	@Test
	void reverseIncludeBracketsHandlesNullAndEmpty() {
		assertEquals("", StringUtils.reverseIncludeBrackets(null));
		assertEquals("", StringUtils.reverseIncludeBrackets(""));
	}
	
	@Test
	void reverseIncludeBracketsReversesSimpleStrings() {
		assertEquals("cba", StringUtils.reverseIncludeBrackets("abc"));
		assertEquals("321", StringUtils.reverseIncludeBrackets("123"));
		assertEquals("a", StringUtils.reverseIncludeBrackets("a"));
	}
	
	@Test
	void reverseIncludeBracketsReversesBrackets() {
		assertEquals("(cb)a", StringUtils.reverseIncludeBrackets("a(bc)"));
		assertEquals("<cb>a", StringUtils.reverseIncludeBrackets("a<bc>"));
		assertEquals("[fed]cba", StringUtils.reverseIncludeBrackets("abc[def]"));
		assertEquals("{ghi}fed[cba]", StringUtils.reverseIncludeBrackets("[abc]def{ihg}"));
	}
	
	@Test
	void indexOfAllCharHandlesNullAndEmpty() {
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll(null, 'a'));
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll("", 'a'));
	}
	
	@Test
	void indexOfAllCharFindsOccurrences() {
		assertEquals(Lists.newArrayList(0), StringUtils.indexOfAll("abc", 'a'));
		assertEquals(Lists.newArrayList(0, 2), StringUtils.indexOfAll("aba", 'a'));
		assertEquals(Lists.newArrayList(0, 1, 2), StringUtils.indexOfAll("   ", ' '));
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll("abc", 'z'));
	}
	
	@Test
	void indexOfAllCharHandlesSpecialCharacters() {
		assertEquals(Lists.newArrayList(2, 5), StringUtils.indexOfAll("ab\nab\n", '\n'));
		assertEquals(Lists.newArrayList(0, 3), StringUtils.indexOfAll("\t12\t", '\t'));
	}
	
	@Test
	void indexOfAllStringHandlesNullAndEmpty() {
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll(null, "ab"));
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll("", "ab"));
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll("abc", null));
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll("abc", ""));
	}
	
	@Test
	void indexOfAllStringFindsOccurrences() {
		assertEquals(Lists.newArrayList(0), StringUtils.indexOfAll("abc", "a"));
		assertEquals(Lists.newArrayList(0), StringUtils.indexOfAll("aba", "ab"));
		assertEquals(Lists.newArrayList(1, 5), StringUtils.indexOfAll("abac ba", "ba"));
		assertEquals(Lists.newArrayList(0, 2), StringUtils.indexOfAll("abab", "ab"));
	}
	
	@Test
	void countDifferenceHandlesNullAndEmpty() {
		assertEquals(0, StringUtils.countDifference(null, null));
		assertEquals(0, StringUtils.countDifference("", ""));
		assertEquals(3, StringUtils.countDifference(null, "abc"));
		assertEquals(3, StringUtils.countDifference("abc", null));
		assertEquals(3, StringUtils.countDifference("", "abc"));
		assertEquals(3, StringUtils.countDifference("abc", ""));
	}
	
	@Test
	void countDifferenceCalculatesCorrectly() {
		assertEquals(0, StringUtils.countDifference("abc", "abc"));
		assertEquals(3, StringUtils.countDifference("xyz", "abc"));
		assertEquals(2, StringUtils.countDifference("abcde", "abc  "));
		assertEquals(2, StringUtils.countDifference("abcde", "  cde"));
		assertEquals(5, StringUtils.countDifference("abcde", "cde"));
		assertEquals(1, StringUtils.countDifference("abc", "abd"));
	}
	
	@Test
	void countDifferenceStrippedHandlesWhitespace() {
		assertEquals(0, StringUtils.countDifferenceStripped(null, null));
		assertEquals(0, StringUtils.countDifferenceStripped("", ""));
		assertEquals(3, StringUtils.countDifferenceStripped(null, "abc"));
		assertEquals(3, StringUtils.countDifferenceStripped("abc", null));
		assertEquals(2, StringUtils.countDifferenceStripped("abcde", "  abc"));
		assertEquals(5, StringUtils.countDifferenceStripped("abcde", "  cde"));
		assertEquals(0, StringUtils.countDifferenceStripped(" abc ", "abc"));
	}
	
	@Test
	void countDifferenceBackwardsCalculatesFromEnd() {
		assertEquals(0, StringUtils.countDifferenceBackwards(null, null));
		assertEquals(0, StringUtils.countDifferenceBackwards("", ""));
		assertEquals(3, StringUtils.countDifferenceBackwards(null, "abc"));
		assertEquals(3, StringUtils.countDifferenceBackwards("abc", null));
		assertEquals(5, StringUtils.countDifferenceBackwards("abcde", "  abc"));
		assertEquals(2, StringUtils.countDifferenceBackwards("abcde", "  cde"));
		assertEquals(2, StringUtils.countDifferenceBackwards("abcde", "cde"));
	}
	
	@Test
	void countDifferenceBackwardsStrippedCombinesBackwardsAndStripping() {
		assertEquals(0, StringUtils.countDifferenceBackwardsStripped(null, null));
		assertEquals(0, StringUtils.countDifferenceBackwardsStripped("", ""));
		assertEquals(5, StringUtils.countDifferenceBackwardsStripped("abcde", "abc  "));
		assertEquals(2, StringUtils.countDifferenceBackwardsStripped("abcde", "cde  "));
		assertEquals(2, StringUtils.countDifferenceBackwardsStripped("abcde", "cde"));
	}
	
	@Test
	void countDifferenceAlignedSelectsBestAlignment() {
		assertEquals(0, StringUtils.countDifferenceAligned(null, null));
		assertEquals(0, StringUtils.countDifferenceAligned("", ""));
		assertEquals(3, StringUtils.countDifferenceAligned(null, "abc"));
		assertEquals(3, StringUtils.countDifferenceAligned("abc", null));
		assertEquals(2, StringUtils.countDifferenceAligned("abcde", "abc  "));
		assertEquals(2, StringUtils.countDifferenceAligned("abcde", "  cde"));
		assertEquals(2, StringUtils.countDifferenceAligned("abcde", "cde"));
	}
	
	@Test
	void levenshteinDistanceHandlesNullAndEmpty() {
		assertEquals(0, StringUtils.levenshteinDistance(null, null));
		assertEquals(0, StringUtils.levenshteinDistance("", ""));
		assertEquals(3, StringUtils.levenshteinDistance(null, "abc"));
		assertEquals(3, StringUtils.levenshteinDistance("abc", null));
		assertEquals(3, StringUtils.levenshteinDistance("abc", ""));
		assertEquals(3, StringUtils.levenshteinDistance("", "abc"));
	}
	
	@Test
	void levenshteinDistanceCalculatesCorrectly() {
		assertEquals(0, StringUtils.levenshteinDistance("abc", "abc"));
		assertEquals(3, StringUtils.levenshteinDistance("abc", "def"));
		assertEquals(1, StringUtils.levenshteinDistance("test", "text"));
		assertEquals(3, StringUtils.levenshteinDistance("abed", "tset"));
		assertEquals(1, StringUtils.levenshteinDistance("cat", "bat"));
		assertEquals(3, StringUtils.levenshteinDistance("kitten", "sitting"));
	}
	
	@Test
	void findSimilarHandlesInvalidInputs() {
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar(null, 0, "abc"));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("", 0, "abc"));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", -1, "abc"));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 0, (String[]) null));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 0, (List<String>) null));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 0, StringUtils::countDifference, null));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar(null, 0, null, Lists.newArrayList("abc")));
	}
	
	@Test
	void findSimilarFindsMatches() {
		assertEquals(Lists.newArrayList("abc"), StringUtils.findSimilar("abc", 1, "abc", "def", "ghi"));
		assertEquals(Lists.newArrayList("ab0", "ab1"), StringUtils.findSimilar("ab*", 1, "ab0", "ab1"));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 2, "bcd", "cde", "def", "efg"));
		assertEquals(Lists.newArrayList("abc", "abd"), StringUtils.findSimilar("abc", 1, "abc", "abd", "xyz"));
	}
	
	@Test
	void findSimilarWithListWorks() {
		assertEquals(Lists.newArrayList("abc"), StringUtils.findSimilar("abc", 1, Lists.newArrayList("abc", "def", "ghi")));
		assertEquals(Lists.newArrayList("ab0", "ab1"), StringUtils.findSimilar("ab*", 1, Lists.newArrayList("ab0", "ab1")));
	}
	
	@Test
	void findSimilarWithCustomCounterWorks() {
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 0, StringUtils::levenshteinDistance, Lists.newArrayList("def", "ghi")));
		assertEquals(Lists.newArrayList("abc"), StringUtils.findSimilar("abc", 1, StringUtils::levenshteinDistance, Lists.newArrayList("abc", "def")));
	}
	
	@Test
	void isAfterAllOccurrenceHandlesInvalidInputs() {
		assertFalse(StringUtils.isAfterAllOccurrence(null, 'a', "b"));
		assertFalse(StringUtils.isAfterAllOccurrence("", 'a', "b"));
		assertFalse(StringUtils.isAfterAllOccurrence("abc", 'a', (String[]) null));
	}
	
	@Test
	void isAfterAllOccurrenceChecksFollowing() {
		assertTrue(StringUtils.isAfterAllOccurrence("abcde", 'a', new String[] {}));
		assertFalse(StringUtils.isAfterAllOccurrence("abcde", 'x', new String[] {}));
		assertTrue(StringUtils.isAfterAllOccurrence("abcde", 'a', new String[] { "b" }));
		assertTrue(StringUtils.isAfterAllOccurrence("abcab", 'a', new String[] { "b" }));
		assertFalse(StringUtils.isAfterAllOccurrence("abcae", 'a', new String[] { "b" }));
		assertTrue(StringUtils.isAfterAllOccurrence("abcae", 'a', new String[] { "b", "e" }));
	}
	
	@Test
	void isBeforeAllOccurrenceHandlesInvalidInputs() {
		assertFalse(StringUtils.isBeforeAllOccurrence(null, 'a', "b"));
		assertFalse(StringUtils.isBeforeAllOccurrence("", 'a', "b"));
		assertFalse(StringUtils.isBeforeAllOccurrence("abc", 'a', (String[]) null));
	}
	
	@Test
	void isBeforeAllOccurrenceChecksPreceding() {
		assertTrue(StringUtils.isBeforeAllOccurrence("abcde", 'd', new String[] {}));
		assertFalse(StringUtils.isBeforeAllOccurrence("abcde", 'x', new String[] {}));
		assertTrue(StringUtils.isBeforeAllOccurrence("abcde", 'e', new String[] { "d" }));
		assertTrue(StringUtils.isBeforeAllOccurrence("aecae", 'e', new String[] { "a" }));
		assertFalse(StringUtils.isBeforeAllOccurrence("aecde", 'e', new String[] { "a" }));
		assertTrue(StringUtils.isBeforeAllOccurrence("aecde", 'e', new String[] { "a", "d" }));
	}
	
	@Test
	void isSurroundedByHandlesInvalidInputs() {
		assertFalse(StringUtils.isSurroundedBy(null, 'a', "b"));
		assertFalse(StringUtils.isSurroundedBy("", 'a', "b"));
		assertFalse(StringUtils.isSurroundedBy("abc", 'a', (String[]) null));
	}
	
	@Test
	void isSurroundedByChecksSurrounding() {
		assertTrue(StringUtils.isSurroundedBy("a.*.b", '*', new String[] {}));
		assertFalse(StringUtils.isSurroundedBy("a.*.b", 'x', new String[] {}));
		assertTrue(StringUtils.isSurroundedBy("a.*.b", '*', new String[] { "." }));
		assertTrue(StringUtils.isSurroundedBy("a.*.b.*.c", '*', new String[] { "." }));
		assertFalse(StringUtils.isSurroundedBy("a.*.b.?*?.c", '*', new String[] { "." }));
		assertTrue(StringUtils.isSurroundedBy("a.*.b.?*?.c", '*', new String[] { ".", "?" }));
		assertTrue(StringUtils.isSurroundedBy("a.*.b.?*?.c", '*', new String[] { ".", "?." }));
	}
	
	@Test
	void isSurroundedByWithReverseBrackets() {
		System.setProperty("lang.surrounded.reverse.brackets", "true");
		try {
			assertTrue(StringUtils.isSurroundedBy("a.[*].b", '*', new String[] { "]." }));
			assertFalse(StringUtils.isSurroundedBy("a.[*[.b", '*', new String[] { "." }));
		} finally {
			System.setProperty("lang.surrounded.reverse.brackets", "false");
		}
	}
	
	@Test
	void removeQuotedHandlesNullAndEmpty() {
		assertEquals("", StringUtils.removeQuoted(null));
		assertEquals("", StringUtils.removeQuoted(""));
		assertEquals("abc", StringUtils.removeQuoted("abc"));
	}
	
	@Test
	void removeQuotedRemovesQuotes() {
		assertEquals("ac", StringUtils.removeQuoted("a\"b\"c"));
		assertEquals("ac", StringUtils.removeQuoted("a'b'c"));
		assertEquals("ad", StringUtils.removeQuoted("a\"b\\\"c\"d"));
		assertEquals("ad", StringUtils.removeQuoted("a'b\\'c'd"));
		assertEquals("ad'e", StringUtils.removeQuoted("a\"b'c\"d'e"));
	}
	
	@Test
	void removeQuotedHandlesEscapedQuotes() {
		assertEquals("a\\\"b\\\"c", StringUtils.removeQuoted("a\\\"b\\\"c"));
		assertEquals("a\\'b\\'c", StringUtils.removeQuoted("a\\'b\\'c"));
		assertEquals("ac", StringUtils.removeQuoted("a\"\"c"));
	}
	
	@Test
	void matchingBalancedCharHandlesInvalidInputs() {
		assertFalse(StringUtils.matchingBalanced(null, '(', ')'));
		assertTrue(StringUtils.matchingBalanced("", '(', ')'));
		assertTrue(StringUtils.matchingBalanced(" ", '(', ')'));
	}
	
	@Test
	void matchingBalancedCharWorksCorrectly() {
		assertTrue(StringUtils.matchingBalanced("(", '(', '('));
		assertFalse(StringUtils.matchingBalanced("(", '(', ')'));
		assertTrue(StringUtils.matchingBalanced("(())", '(', ')'));
		assertFalse(StringUtils.matchingBalanced("(()", '(', ')'));
		assertTrue(StringUtils.matchingBalanced("((()))", '(', ')'));
		assertFalse(StringUtils.matchingBalanced("((())", '(', ')'));
	}
	
	@Test
	void matchingBalancedStringHandlesInvalidInputs() {
		assertFalse(StringUtils.matchingBalanced(null, "(", ")"));
		assertFalse(StringUtils.matchingBalanced("test", null, ")"));
		assertFalse(StringUtils.matchingBalanced("test", "(", null));
		assertTrue(StringUtils.matchingBalanced("", "(", ")"));
		assertTrue(StringUtils.matchingBalanced("test", null, null));
	}
	
	@Test
	void matchingBalancedStringWorksCorrectly() {
		assertTrue(StringUtils.matchingBalanced("(", "(", "("));
		assertFalse(StringUtils.matchingBalanced("(", "(", ")"));
		assertTrue(StringUtils.matchingBalanced("(())", "(", ")"));
		assertFalse(StringUtils.matchingBalanced("(()", "(", ")"));
		assertTrue(StringUtils.matchingBalanced("beginendbeginend", "begin", "end"));
		assertFalse(StringUtils.matchingBalanced("beginbeginend", "begin", "end"));
	}
	
	@Test
	void matchingBalancedWithQuotes() {
		System.setProperty("lang.match.in.quotes", "true");
		try {
			assertFalse(StringUtils.matchingBalanced("(\"(\")", '(', ')'));
			assertFalse(StringUtils.matchingBalanced("('(')", '(', ')'));
			assertTrue(StringUtils.matchingBalanced("((\")\")", '(', ')'));
			assertTrue(StringUtils.matchingBalanced("((')')", '(', ')'));
		} finally {
			System.setProperty("lang.match.in.quotes", "false");
		}
	}
	
	@Test
	void matchesPatternHandlesNulls() {
		assertTrue(StringUtils.matchesPattern(null, null));
		assertFalse(StringUtils.matchesPattern(null, "a"));
		assertFalse(StringUtils.matchesPattern(Pattern.compile("a"), null));
	}
	
	@Test
	void matchesPatternWorksCorrectly() {
		Pattern pattern = Pattern.compile("a+");
		assertTrue(StringUtils.matchesPattern(pattern, "a"));
		assertTrue(StringUtils.matchesPattern(pattern, "aa"));
		assertFalse(StringUtils.matchesPattern(pattern, "b"));
		assertFalse(StringUtils.matchesPattern(pattern, "ab"));
	}
	
	@Test
	void containsNotEscapedHandlesNullAndEmpty() {
		assertFalse(StringUtils.containsNotEscaped(null, 'a'));
		assertFalse(StringUtils.containsNotEscaped("", 'a'));
	}
	
	@Test
	void containsNotEscapedFindsUnescapedChars() {
		assertTrue(StringUtils.containsNotEscaped("abc", 'a'));
		assertFalse(StringUtils.containsNotEscaped("aba", 'c'));
		assertFalse(StringUtils.containsNotEscaped("\\a", 'a'));
		assertTrue(StringUtils.containsNotEscaped("\\ab", 'b'));
		assertTrue(StringUtils.containsNotEscaped("\\\\a", 'a'));
	}
	
	@Test
	void splitNotEscapedHandlesNullAndEmpty() {
		assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, StringUtils.splitNotEscaped(null, 'a'));
		assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, StringUtils.splitNotEscaped("", 'a'));
	}
	
	@Test
	void splitNotEscapedSplitsCorrectly() {
		assertArrayEquals(new String[] { "a", "c" }, StringUtils.splitNotEscaped("abc", 'b'));
		assertArrayEquals(new String[] { "a\\bc" }, StringUtils.splitNotEscaped("a\\bc", 'b'));
		assertArrayEquals(new String[] { "a\\b" }, StringUtils.splitNotEscaped("a\\bc", 'c'));
		assertArrayEquals(new String[] { "a\\:b", "c", "d\\:e" }, StringUtils.splitNotEscaped("a\\:b:c:d\\:e", ':'));
	}
	
	@Test
	void splitNotEscapedHandlesEscapeSequences() {
		assertArrayEquals(new String[] { "a\\\\", "c" }, StringUtils.splitNotEscaped("a\\\\:c", ':'));
		assertArrayEquals(new String[] { "a", "", "c" }, StringUtils.splitNotEscaped("a::c", ':'));
		assertArrayEquals(new String[] { "a\\:" }, StringUtils.splitNotEscaped("a\\:", ':'));
	}
	
	@Test
	void extractStringHandlesNullAndEmpty() {
		assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, StringUtils.extract(null, (String) null));
		assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, StringUtils.extract(null, "abc"));
		assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, StringUtils.extract("abc", (String) null));
		assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, StringUtils.extract("", "abc"));
		assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, StringUtils.extract("abc", ""));
	}
	
	@Test
	void extractStringFindsMatches() {
		assertArrayEquals(new String[] { "a" }, StringUtils.extract("abc", "a"));
		assertArrayEquals(new String[] { "abc" }, StringUtils.extract("abc", "abc"));
		assertArrayEquals(new String[] { "[def]" }, StringUtils.extract("abc.[def].ghi", "\\[.*?]"));
		assertArrayEquals(new String[] { "123", "456" }, StringUtils.extract("abc123def456ghi", "\\d+"));
	}
	
	@Test
	void extractPatternHandlesNulls() {
		assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, StringUtils.extract(null, (Pattern) null));
		assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, StringUtils.extract(null, Pattern.compile("a")));
		assertArrayEquals(ArrayUtils.EMPTY_STRING_ARRAY, StringUtils.extract("abc", (Pattern) null));
	}
	
	@Test
	void extractPatternFindsMatches() {
		assertArrayEquals(new String[] { "a" }, StringUtils.extract("abc", Pattern.compile("a")));
		assertArrayEquals(new String[] { "abc" }, StringUtils.extract("abc", Pattern.compile("abc")));
		assertArrayEquals(new String[] { "[def]" }, StringUtils.extract("abc.[def].ghi", Pattern.compile("\\[.*?]")));
		assertArrayEquals(new String[] { "123", "456" }, StringUtils.extract("abc123def456ghi", Pattern.compile("\\d+")));
	}
	
	@Test
	void getReadableStringHandlesNullAndEmpty() {
		assertEquals("", StringUtils.getReadableString(null, Character::isUpperCase));
		assertEquals("abc", StringUtils.getReadableString("abc", null));
		assertEquals("", StringUtils.getReadableString("", Character::isUpperCase));
	}
	
	@Test
	void getReadableStringConvertsCorrectly() {
		assertEquals("abc", StringUtils.getReadableString("abc", Character::isUpperCase));
		assertEquals("A B C", StringUtils.getReadableString("abc", Character::isLowerCase));
		assertEquals("abc D E Fghi", StringUtils.getReadableString("abcDEFghi", Character::isUpperCase));
		assertEquals("Test Class", StringUtils.getReadableString("TestClass", Character::isUpperCase));
		assertEquals("my _constant", StringUtils.getReadableString("MY_CONSTANT", c -> c == '_'));
	}
	
	@Test
	void getReadableStringHandlesSpecialCases() {
		assertEquals("H T M L Parser", StringUtils.getReadableString("HTMLParser", Character::isUpperCase));
		assertEquals("X M L", StringUtils.getReadableString("XML", Character::isUpperCase));
		assertEquals("a 1b 2c 3", StringUtils.getReadableString("A1B2C3", Character::isDigit));
	}
}
