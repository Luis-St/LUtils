/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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
	void getOppositeBracket() {
		assertEquals(')', StringUtils.getOppositeBracket('('));
		assertEquals('[', StringUtils.getOppositeBracket(']'));
		assertEquals('}', StringUtils.getOppositeBracket('{'));
		assertEquals('>', StringUtils.getOppositeBracket('<'));
		assertEquals('\0', StringUtils.getOppositeBracket('a'));
	}
	
	@Test
	void reverseIncludeBrackets() {
		assertDoesNotThrow(() -> StringUtils.reverseIncludeBrackets(null));
		assertEquals("", StringUtils.reverseIncludeBrackets(null));
		assertEquals("", StringUtils.reverseIncludeBrackets(""));
		assertEquals("cba", StringUtils.reverseIncludeBrackets("abc"));
		assertEquals("(cb)a", StringUtils.reverseIncludeBrackets("a(bc)"));
		assertEquals("<cb>a", StringUtils.reverseIncludeBrackets("a<bc>"));
	}
	
	@Test
	void indexOfAll() {
		assertDoesNotThrow(() -> StringUtils.indexOfAll(null, '\0'));
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll(null, '\0'));
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll("", '\0'));
		assertEquals(Lists.newArrayList(0), StringUtils.indexOfAll("abc", 'a'));
		assertEquals(Lists.newArrayList(0, 2), StringUtils.indexOfAll("aba", 'a'));
		assertEquals(Lists.newArrayList(0, 1, 2), StringUtils.indexOfAll("   ", ' '));
		
		assertDoesNotThrow(() -> StringUtils.indexOfAll(null, "\0"));
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll(null, "\0"));
		assertEquals(Lists.newArrayList(), StringUtils.indexOfAll("", "\0"));
		assertEquals(Lists.newArrayList(0), StringUtils.indexOfAll("abc", "a"));
		assertEquals(Lists.newArrayList(0), StringUtils.indexOfAll("aba", "ab"));
		assertEquals(Lists.newArrayList(0, 1, 2), StringUtils.indexOfAll("   ", " "));
	}
	
	@Test
	void countDifference() {
		assertDoesNotThrow(() -> StringUtils.countDifference(null, null));
		assertDoesNotThrow(() -> StringUtils.countDifference(null, "abc"));
		assertDoesNotThrow(() -> StringUtils.countDifference("abc", null));
		assertEquals(0, StringUtils.countDifference(null, null));
		assertEquals(0, StringUtils.countDifference("", ""));
		assertEquals(3, StringUtils.countDifference(null, "abc"));
		assertEquals(3, StringUtils.countDifference("abc", null));
		assertEquals(3, StringUtils.countDifference("", "abc"));
		assertEquals(3, StringUtils.countDifference("abc", ""));
		assertEquals(0, StringUtils.countDifference("abc", "abc"));
		assertEquals(3, StringUtils.countDifference("xyz", "abc"));
		assertEquals(2, StringUtils.countDifference("abcde", "abc  "));
		assertEquals(2, StringUtils.countDifference("abcde", "  cde"));
		assertEquals(5, StringUtils.countDifference("abcde", "cde"));
	}
	
	@Test
	void countDifferenceStripped() {
		assertDoesNotThrow(() -> StringUtils.countDifferenceStripped(null, null));
		assertDoesNotThrow(() -> StringUtils.countDifferenceStripped(null, "abc"));
		assertDoesNotThrow(() -> StringUtils.countDifferenceStripped("abc", null));
		assertEquals(0, StringUtils.countDifferenceStripped(null, null));
		assertEquals(0, StringUtils.countDifferenceStripped("", ""));
		assertEquals(3, StringUtils.countDifferenceStripped(null, "abc"));
		assertEquals(3, StringUtils.countDifferenceStripped("abc", null));
		assertEquals(3, StringUtils.countDifferenceStripped("", "abc"));
		assertEquals(3, StringUtils.countDifferenceStripped("abc", ""));
		assertEquals(0, StringUtils.countDifferenceStripped("abc", "abc"));
		assertEquals(3, StringUtils.countDifferenceStripped("xyz", "abc"));
		assertEquals(2, StringUtils.countDifferenceStripped("abcde", "  abc"));
		assertEquals(5, StringUtils.countDifferenceStripped("abcde", "  cde"));
		assertEquals(5, StringUtils.countDifferenceStripped("abcde", "cde"));
	}
	
	@Test
	void countDifferenceBackwards() {
		assertDoesNotThrow(() -> StringUtils.countDifferenceBackwards(null, null));
		assertDoesNotThrow(() -> StringUtils.countDifferenceBackwards(null, "abc"));
		assertDoesNotThrow(() -> StringUtils.countDifferenceBackwards("abc", null));
		assertEquals(0, StringUtils.countDifferenceBackwards(null, null));
		assertEquals(0, StringUtils.countDifferenceBackwards("", ""));
		assertEquals(3, StringUtils.countDifferenceBackwards(null, "abc"));
		assertEquals(3, StringUtils.countDifferenceBackwards("abc", null));
		assertEquals(3, StringUtils.countDifferenceBackwards("", "abc"));
		assertEquals(3, StringUtils.countDifferenceBackwards("abc", ""));
		assertEquals(0, StringUtils.countDifferenceBackwards("abc", "abc"));
		assertEquals(3, StringUtils.countDifferenceBackwards("xyz", "abc"));
		assertEquals(5, StringUtils.countDifferenceBackwards("abcde", "  abc"));
		assertEquals(2, StringUtils.countDifferenceBackwards("abcde", "  cde"));
		assertEquals(2, StringUtils.countDifferenceBackwards("abcde", "cde"));
	}
	
	@Test
	void countDifferenceBackwardsStripped() {
		assertDoesNotThrow(() -> StringUtils.countDifferenceBackwardsStripped(null, null));
		assertDoesNotThrow(() -> StringUtils.countDifferenceBackwardsStripped(null, "abc"));
		assertDoesNotThrow(() -> StringUtils.countDifferenceBackwardsStripped("abc", null));
		assertEquals(0, StringUtils.countDifferenceBackwardsStripped(null, null));
		assertEquals(0, StringUtils.countDifferenceBackwardsStripped("", ""));
		assertEquals(3, StringUtils.countDifferenceBackwardsStripped(null, "abc"));
		assertEquals(3, StringUtils.countDifferenceBackwardsStripped("abc", null));
		assertEquals(3, StringUtils.countDifferenceBackwardsStripped("", "abc"));
		assertEquals(3, StringUtils.countDifferenceBackwardsStripped("abc", ""));
		assertEquals(0, StringUtils.countDifferenceBackwardsStripped("abc", "abc"));
		assertEquals(3, StringUtils.countDifferenceBackwardsStripped("xyz", "abc"));
		assertEquals(5, StringUtils.countDifferenceBackwardsStripped("abcde", "abc  "));
		assertEquals(2, StringUtils.countDifferenceBackwardsStripped("abcde", "cde  "));
		assertEquals(2, StringUtils.countDifferenceBackwardsStripped("abcde", "cde"));
	}
	
	@Test
	void countDifferenceAligned() {
		assertDoesNotThrow(() -> StringUtils.countDifferenceAligned(null, null));
		assertDoesNotThrow(() -> StringUtils.countDifferenceAligned(null, "abc"));
		assertDoesNotThrow(() -> StringUtils.countDifferenceAligned("abc", null));
		assertEquals(0, StringUtils.countDifferenceAligned(null, null));
		assertEquals(0, StringUtils.countDifferenceAligned("", ""));
		assertEquals(3, StringUtils.countDifferenceAligned(null, "abc"));
		assertEquals(3, StringUtils.countDifferenceAligned("abc", null));
		assertEquals(3, StringUtils.countDifferenceAligned("", "abc"));
		assertEquals(3, StringUtils.countDifferenceAligned("abc", ""));
		assertEquals(0, StringUtils.countDifferenceAligned("abc", "abc"));
		assertEquals(3, StringUtils.countDifferenceAligned("xyz", "abc"));
		assertEquals(2, StringUtils.countDifferenceAligned("abcde", "abc  "));
		assertEquals(2, StringUtils.countDifferenceAligned("abcde", "  cde"));
		assertEquals(2, StringUtils.countDifferenceAligned("abcde", "cde"));
	}
	
	@Test
	void levenshteinDistance() {
		assertDoesNotThrow(() -> StringUtils.levenshteinDistance(null, null));
		assertDoesNotThrow(() -> StringUtils.levenshteinDistance(null, "abc"));
		assertDoesNotThrow(() -> StringUtils.levenshteinDistance("abc", null));
		assertEquals(0, StringUtils.levenshteinDistance(null, null));
		assertEquals(0, StringUtils.levenshteinDistance("", ""));
		assertEquals(3, StringUtils.levenshteinDistance(null, "abc"));
		assertEquals(3, StringUtils.levenshteinDistance("abc", null));
		assertEquals(3, StringUtils.levenshteinDistance("abc", ""));
		assertEquals(3, StringUtils.levenshteinDistance("", "abc"));
		assertEquals(0, StringUtils.levenshteinDistance("abc", "abc"));
		assertEquals(3, StringUtils.levenshteinDistance("abc", "def"));
		assertEquals(1, StringUtils.levenshteinDistance("test", "text"));
		assertEquals(3, StringUtils.levenshteinDistance("abed", "tset"));
	}
	
	@Test
	void findSimilar() {
		assertDoesNotThrow(() -> StringUtils.findSimilar(null, 0, "abc"));
		assertDoesNotThrow(() -> StringUtils.findSimilar("abc", 0, (String[]) null));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar(null, 0, "abc"));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("", 0, "abc"));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", -1, "abc"));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 0, (String[]) null));
		assertEquals(Lists.newArrayList("abc"), StringUtils.findSimilar("abc", 1, "abc", "def", "ghi"));
		assertEquals(Lists.newArrayList("ab0", "ab1"), StringUtils.findSimilar("ab*", 1, "ab0", "ab1"));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 2, "bcd", "cde", "def", "efg"));
		
		assertDoesNotThrow(() -> StringUtils.findSimilar(null, 0, Lists.newArrayList("abc")));
		assertDoesNotThrow(() -> StringUtils.findSimilar("abc", 0, (List<String>) null));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar(null, 0, Lists.newArrayList("abc")));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("", 0, Lists.newArrayList("abc")));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", -1, Lists.newArrayList("abc")));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 0, (List<String>) null));
		assertEquals(Lists.newArrayList("abc"), StringUtils.findSimilar("abc", 1, Lists.newArrayList("abc", "def", "ghi")));
		assertEquals(Lists.newArrayList("ab0", "ab1"), StringUtils.findSimilar("ab*", 1, Lists.newArrayList("ab0", "ab1")));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 2, Lists.newArrayList("bcd", "cde", "def", "efg")));
		
		assertDoesNotThrow(() -> StringUtils.findSimilar(null, 0, StringUtils::countDifference, Lists.newArrayList("abc")));
		assertDoesNotThrow(() -> StringUtils.findSimilar("abc", 0, null, Lists.newArrayList("abc")));
		assertDoesNotThrow(() -> StringUtils.findSimilar("abc", 0, StringUtils::countDifference, null));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar(null, 0, StringUtils::countDifference, Lists.newArrayList("abc")));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("", 0, StringUtils::countDifference, Lists.newArrayList("abc")));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", -1, StringUtils::countDifference, Lists.newArrayList("abc")));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 0, null, Lists.newArrayList("abc")));
		assertEquals(Lists.newArrayList(), StringUtils.findSimilar("abc", 0, StringUtils::countDifference, null));
	}
	
	@Test
	void isAfterAllOccurrence() {
		assertDoesNotThrow(() -> StringUtils.isAfterAllOccurrence(null, '\0', ArrayUtils.EMPTY_STRING_ARRAY));
		assertDoesNotThrow(() -> StringUtils.isAfterAllOccurrence("abcde", '\0', (String[]) null));
		assertFalse(StringUtils.isAfterAllOccurrence(null, '\0', ArrayUtils.EMPTY_STRING_ARRAY));
		assertFalse(StringUtils.isAfterAllOccurrence("", '\0', ArrayUtils.EMPTY_STRING_ARRAY));
		assertFalse(StringUtils.isAfterAllOccurrence("abcde", '\0', (String[]) null));
		assertTrue(StringUtils.isAfterAllOccurrence("abcde", 'a', ArrayUtils.EMPTY_STRING_ARRAY));
		assertFalse(StringUtils.isAfterAllOccurrence("abcde", 'x', ArrayUtils.EMPTY_STRING_ARRAY));
		assertTrue(StringUtils.isAfterAllOccurrence("abcde", 'a', new String[] { "b" }));
		assertTrue(StringUtils.isAfterAllOccurrence("abcab", 'a', new String[] { "b" }));
		assertFalse(StringUtils.isAfterAllOccurrence("abcae", 'a', new String[] { "b" }));
		assertTrue(StringUtils.isAfterAllOccurrence("abcae", 'a', new String[] { "b", "e" }));
	}
	
	@Test
	void isBeforeAllOccurrence() {
		assertDoesNotThrow(() -> StringUtils.isBeforeAllOccurrence(null, '\0', ArrayUtils.EMPTY_STRING_ARRAY));
		assertDoesNotThrow(() -> StringUtils.isBeforeAllOccurrence("abcde", '\0', (String[]) null));
		assertFalse(StringUtils.isBeforeAllOccurrence(null, '\0', ArrayUtils.EMPTY_STRING_ARRAY));
		assertFalse(StringUtils.isBeforeAllOccurrence("", '\0', ArrayUtils.EMPTY_STRING_ARRAY));
		assertFalse(StringUtils.isBeforeAllOccurrence("abcde", '\0', (String[]) null));
		assertTrue(StringUtils.isBeforeAllOccurrence("abcde", 'd', ArrayUtils.EMPTY_STRING_ARRAY));
		assertFalse(StringUtils.isBeforeAllOccurrence("abcde", 'x', ArrayUtils.EMPTY_STRING_ARRAY));
		assertTrue(StringUtils.isBeforeAllOccurrence("abcde", 'e', new String[] { "d" }));
		assertTrue(StringUtils.isBeforeAllOccurrence("aecae", 'e', new String[] { "a" }));
		assertFalse(StringUtils.isBeforeAllOccurrence("aecde", 'e', new String[] { "a" }));
		assertTrue(StringUtils.isBeforeAllOccurrence("aecde", 'e', new String[] { "a", "d" }));
	}
	
	@Test
	void isSurroundedBy() {
		assertDoesNotThrow(() -> StringUtils.isSurroundedBy(null, '\0', ArrayUtils.EMPTY_STRING_ARRAY));
		assertDoesNotThrow(() -> StringUtils.isSurroundedBy("a.*.b", '\0', (String[]) null));
		assertFalse(StringUtils.isSurroundedBy(null, '\0', ArrayUtils.EMPTY_STRING_ARRAY));
		assertFalse(StringUtils.isSurroundedBy("", '\0', ArrayUtils.EMPTY_STRING_ARRAY));
		assertFalse(StringUtils.isSurroundedBy("a.*.b", '\0', (String[]) null));
		assertTrue(StringUtils.isSurroundedBy("a.*.b", '*', ArrayUtils.EMPTY_STRING_ARRAY));
		assertFalse(StringUtils.isSurroundedBy("a.*.b", 'x', ArrayUtils.EMPTY_STRING_ARRAY));
		assertTrue(StringUtils.isSurroundedBy("a.*.b", '*', new String[] { "." }));
		assertTrue(StringUtils.isSurroundedBy("a.*.b.*.c", '*', new String[] { "." }));
		assertFalse(StringUtils.isSurroundedBy("a.*.b.?*?.c", '*', new String[] { "." }));
		assertTrue(StringUtils.isSurroundedBy("a.*.b.?*?.c", '*', new String[] { ".", "?" }));
		assertTrue(StringUtils.isSurroundedBy("a.*.b.?*?.c", '*', new String[] { ".", "?." }));
		
		System.setProperty("lang.surrounded.reverse.brackets", "true");
		assertTrue(StringUtils.isSurroundedBy("a.[*].b", '*', new String[] { "]." }));
		assertFalse(StringUtils.isSurroundedBy("a.[*[.b", '*', new String[] { "[." }));
		System.setProperty("lang.surrounded.reverse.brackets", "false");
	}
	
	@Test
	void removeQuoted() {
		assertDoesNotThrow(() -> StringUtils.removeQuoted(null));
		assertEquals("", StringUtils.removeQuoted(null));
		assertEquals("", StringUtils.removeQuoted(""));
		assertEquals("abc", StringUtils.removeQuoted("abc"));
		assertEquals("ac", StringUtils.removeQuoted("a\"b\"c"));
		assertEquals("ac", StringUtils.removeQuoted("a'b'c"));
		assertEquals("ad", StringUtils.removeQuoted("a\"b\\\"c\"d"));
		assertEquals("ad", StringUtils.removeQuoted("a'b\\'c'd"));
		assertEquals("ad'e", StringUtils.removeQuoted("a\"b'c\"d'e"));
	}
	
	@Test
	void matchingBalanced() {
		assertDoesNotThrow(() -> StringUtils.matchingBalanced(null, '\0', '\0'));
		assertFalse(StringUtils.matchingBalanced(null, '\0', '\0'));
		assertTrue(StringUtils.matchingBalanced("", '\0', '\0'));
		assertTrue(StringUtils.matchingBalanced(" ", '\0', '\0'));
		assertTrue(StringUtils.matchingBalanced("(", '(', '('));
		assertFalse(StringUtils.matchingBalanced("(", '(', ')'));
		assertTrue(StringUtils.matchingBalanced("(())", '(', ')'));
		assertFalse(StringUtils.matchingBalanced("(()", '(', ')'));
		
		assertDoesNotThrow(() -> StringUtils.matchingBalanced(null, "\0", "\0"));
		assertFalse(StringUtils.matchingBalanced(null, "\0", "\0"));
		assertTrue(StringUtils.matchingBalanced("", "\0", "\0"));
		assertTrue(StringUtils.matchingBalanced(" ", "\0", "\0"));
		assertTrue(StringUtils.matchingBalanced("(", "(", "("));
		assertFalse(StringUtils.matchingBalanced("(", "(", ")"));
		assertTrue(StringUtils.matchingBalanced("(())", "(", ")"));
		assertFalse(StringUtils.matchingBalanced("(()", "(", ")"));
		
		System.setProperty("lang.match.in.quotes", "true");
		assertFalse(StringUtils.matchingBalanced("(\"(\")", '(', ')'));
		assertFalse(StringUtils.matchingBalanced("('(')", '(', ')'));
		assertTrue(StringUtils.matchingBalanced("((\")\")", '(', ')'));
		assertTrue(StringUtils.matchingBalanced("((')')", '(', ')'));
		
		assertFalse(StringUtils.matchingBalanced("(\"(\")", "(", ")"));
		assertFalse(StringUtils.matchingBalanced("('(')", "(", ")"));
		assertTrue(StringUtils.matchingBalanced("((\")\")", "(", ")"));
		assertTrue(StringUtils.matchingBalanced("((')')", "(", ")"));
		System.setProperty("lang.match.in.quotes", "false");
	}
	
	@Test
	void matchesPattern() {
		Pattern pattern = Pattern.compile("a");
		assertDoesNotThrow(() -> StringUtils.matchesPattern(null, null));
		assertDoesNotThrow(() -> StringUtils.matchesPattern(null, "a"));
		assertDoesNotThrow(() -> StringUtils.matchesPattern(pattern, null));
		assertTrue(StringUtils.matchesPattern(null, null));
		assertFalse(StringUtils.matchesPattern(null, "a"));
		assertFalse(StringUtils.matchesPattern(pattern, null));
		assertTrue(StringUtils.matchesPattern(pattern, "a"));
		assertFalse(StringUtils.matchesPattern(pattern, "b"));
	}
}