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

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToIntBiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * A utility class for string operations.<br>
 *
 * @author Luis-St
 */
public final class StringUtils {
	
	/**
	 * Regular expression pattern used to remove single-quoted string parts.<br>
	 * The regular expression allows escaped single quotes inside the string.<br>
	 */
	private static final String SINGLE_QUOTE_PATTERN = "(?<!\\\\)('([^\\\\']|\\\\.)*')";
	/**
	 * Regular expression pattern used to remove double-quoted string parts.<br>
	 * The regular expression allows escaped double quotes inside the string.<br>
	 */
	private static final String DOUBLE_QUOTE_PATTERN = "(?<!\\\\)(\"([^\\\\\"]|\\\\.)*\")";
	/**
	 * Constant for system property 'lang.surrounded.reverse.brackets'.<br>
	 * <p>
	 *     This property is used in {@link #isSurroundedBy(String, char, String...)} to determine<br>
	 *     if the brackets should be reversed.
	 * </p>
	 * <p>
	 *     The default value is {@code false}.
	 * </p>
	 */
	private static final String LANG_SURROUNDED_REVERSE_BRACKETS = "lang.surrounded.reverse.brackets";
	/**
	 * Constant for system property 'lang.match.in.quotes'.<br>
	 * <p>
	 *     This property is used in {@link #matchingBalanced(String, String, String)} to determine<br>
	 *     if occurrences of the {@code open} and {@code close} strings should be match inside quotes.
	 * </p>
	 * <p>
	 *     The default value is {@code false}.
	 * </p>
	 */
	private static final String LANG_MATCH_IN_QUOTES = "lang.match.in.quotes";
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private StringUtils() {}
	
	/**
	 * Gets the opposite bracket of the given {@code bracket}.<br>
	 * If the given character is not a bracket, {@code '\0'} will be returned.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * getOppositeBracket('(') -> ')'
	 * getOppositeBracket(']') -> '['
	 * getOppositeBracket('{') -> '}'
	 * getOppositeBracket('<') -> '>'
	 * getOppositeBracket('a') -> '\0'
	 * }</pre>
	 *
	 * @param bracket The bracket to get the opposite of
	 * @return The opposite bracket of the given character or {@code '\0'} if the given character is not a bracket
	 */
	public static char getOppositeBracket(char bracket) {
		return switch (bracket) {
			case '(' -> ')';
			case ')' -> '(';
			case '[' -> ']';
			case ']' -> '[';
			case '{' -> '}';
			case '}' -> '{';
			case '<' -> '>';
			case '>' -> '<';
			default -> '\0';
		};
	}
	
	/**
	 * Reverses the given string including brackets.<br>
	 * If the given string is null or empty, an empty string will be returned.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * reverseIncludeBrackets(null) -> ""
	 * reverseIncludeBrackets("") -> ""
	 * reverseIncludeBrackets("abc") -> "cba"
	 * reverseIncludeBrackets("a(bc)") -> "(cb)a"
	 * reverseIncludeBrackets("a<bc>") -> "<cb>a"
	 * }</pre>
	 *
	 * @param str The string to reverse
	 * @return The reversed string including brackets
	 */
	public static @NotNull String reverseIncludeBrackets(@Nullable String str) {
		if (isEmpty(str)) {
			return "";
		}
		char[] reversed = reverse(str).toCharArray();
		for (int i = 0; i < reversed.length; i++) {
			char opposite = getOppositeBracket(reversed[i]);
			if (opposite != '\0') {
				reversed[i] = opposite;
			}
		}
		return new String(reversed);
	}
	
	/**
	 * Searches for occurrences of the given {@code search} character in the given string.<br>
	 * The index of the found occurrences will be returned in a list.<br>
	 * If the given string is empty, an empty list will be returned.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * indexOfAll(null, *) -> []
	 * indexOfAll("", *) -> []
	 * indexOfAll("abc", 'a') -> [0]
	 * indexOfAll("aba", 'a') -> [0, 2]
	 * indexOfAll("   ", ' ') -> [0, 1, 2]
	 * }</pre>
	 *
	 * @param str The string to search in
	 * @param search The character to search for
	 * @return A list with the indexes of all occurrences in the given character
	 */
	public static @NotNull List<Integer> indexOfAll(@Nullable String str, char search) {
		List<Integer> indexes = Lists.newArrayList();
		if (isEmpty(str)) {
			return indexes;
		}
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == search) {
				indexes.add(i);
			}
		}
		return indexes;
	}
	
	/**
	 * Searches for occurrences of the given {@code search} string in the given string.<br>
	 * The occurred indexes will be returned in a list.<br>
	 * If the given string or the search string is empty, an empty list will be returned.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * indexOfAll(null, *) -> []
	 * indexOfAll("", *) -> []
	 * indexOfAll("abed", "a") -> [0]
	 * indexOfAll("abac", "ab") -> [0]
	 * indexOfAll("abac ba", "ba") -> [1, 5]
	 * }</pre>
	 *
	 * @param str The string to search in
	 * @param search The string to search for
	 * @return A list with the indexes of all occurrences in the given string
	 */
	public static @NotNull List<Integer> indexOfAll(@Nullable String str, @Nullable String search) {
		List<Integer> indexes = Lists.newArrayList();
		if (isEmpty(str) || isEmpty(search)) {
			return indexes;
		}
		for (int i = 0; i < str.length(); i++) {
			if (str.startsWith(search, i)) {
				indexes.add(i);
			}
		}
		return indexes;
	}
	
	//region Description
	
	/*
	 * Summarize methods, only one method for counting differences with different modes
	 */
	
	/**
	 * Does the base difference calculation for the given strings.<br>
	 * <p>
	 *     If both strings are equal, 0 will be returned.<br>
	 *     If one of the strings is empty or null, the length of the other string will be returned.<br>
	 *     In any other case, -1 will be returned.<br>
	 * </p>
	 * @param base The base string
	 * @param compare The string to compare
	 * @return The difference between the given strings
	 * @see #countDifference(String, String)
	 * @see #countDifferenceStripped(String, String)
	 * @see #countDifferenceBackwards(String, String)
	 * @see #countDifferenceBackwardsStripped(String, String)
	 * @see #levenshteinDistance(String, String)
	 */
	private static int baseDifference(@Nullable String base, @Nullable String compare) {
		if (org.apache.commons.lang3.StringUtils.equals(base, compare)) {
			return 0;
		}
		if (isEmpty(base)) {
			return isEmpty(compare) ? 0 : compare.length();
		}
		if (isEmpty(compare)) {
			return base.length();
		}
		return -1;
	}
	
	/**
	 * Counts the difference between the given strings.<br>
	 * <p>
	 *     If both strings are equal, empty or null, 0 will be returned.<br>
	 *     If one of the strings is empty or null, the length of the other string will be returned.<br>
	 * </p>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * countDifference(null, null) -> 0
	 * countDifference("", "") -> 0
	 * countDifference("abc", "abc") -> 0
	 * countDifference("xyz", "abc") -> 3
	 * countDifference("abcde", "abc  ") -> 2
	 * countDifference("abcde", "  cde") -> 2
	 * countDifference("abcde", "cde") -> 5
	 * }</pre>
	 * @param base The base string
	 * @param compare The string to compare
	 * @return The difference between the given strings
	 * @see #countDifferenceStripped(String, String)
	 */
	@SuppressWarnings("ConstantConditions") // Ignore null warnings, because the baseDifference method already checks for null
	public static int countDifference(@Nullable String base, @Nullable String compare) {
		int baseDifference = baseDifference(base, compare);
		if (baseDifference >= 0) {
			return baseDifference;
		}
		int difference = Math.abs(base.length() - compare.length());
		for (int i = 0; i < Math.min(base.length(), compare.length()); i++) {
			if (base.charAt(i) != compare.charAt(i)) {
				difference++;
			}
		}
		return difference;
	}
	
	/**
	 * Counts the difference between the given strings, after stripping them.<br>
	 * <p>
	 *     If both strings are equal, empty or null, 0 will be returned.<br>
	 *     If one of the strings is empty or null, the length of the other string will be returned.<br>
	 * </p>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * countDifferenceStripped(null, null) -> 0
	 * countDifferenceStripped("", "") -> 0
	 * countDifferenceStripped("abc", "abc") -> 0
	 * countDifferenceStripped("xyz", "abc") -> 3
	 * countDifferenceStripped("abcde", "  abc") -> 2
	 * countDifferenceStripped("abcde", "  cde") -> 5
	 * countDifferenceStripped("abcde", "cde") -> 5
	 * }</pre>
	 * @param base The base string
	 * @param compare The string to compare
	 * @return The difference between the given strings
	 * @see #countDifference(String, String)
	 */
	public static int countDifferenceStripped(@Nullable String base, @Nullable String compare) {
		return countDifference(strip(base), strip(compare));
	}
	
	/**
	 * Counts the difference between the given strings, backwards.<br>
	 * <p>
	 *     If both strings are equal, empty or null, 0 will be returned.<br>
	 *     If one of the strings is empty or null, the length of the other string will be returned.<br>
	 * </p>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * countDifferenceBackwards(null, null) -> 0
	 * countDifferenceBackwards("", "") -> 0
	 * countDifferenceBackwards("abc", "abc") -> 0
	 * countDifferenceBackwards("xyz", "abc") -> 3
	 * countDifferenceBackwards("abcde", "  abc") -> 5
	 * countDifferenceBackwards("abcde", "  cde") -> 2
	 * countDifferenceBackwards("abcde", "cde") -> 2
	 * }</pre>
	 * @param base The base string
	 * @param compare The string to compare
	 * @return The difference between the given strings
	 * @see #countDifferenceBackwardsStripped(String, String)
	 */
	@SuppressWarnings("ConstantConditions") // Ignore null warnings, because the baseDifference method already checks for null
	public static int countDifferenceBackwards(@Nullable String base, @Nullable String compare) {
		int baseDifference = baseDifference(base, compare);
		if (baseDifference >= 0) {
			return baseDifference;
		}
		int abs = Math.abs(base.length() - compare.length());
		int difference = abs;
		int length = Math.max(base.length(), compare.length());
		for (int i = length - 1; i >= abs; i--) {
			if (base.charAt(i - (length - base.length())) != compare.charAt(i - (length - compare.length()))) {
				difference++;
			}
		}
		return difference;
	}
	
	/**
	 * Counts the difference between the given strings, backwards, after stripping them.<br>
	 * <p>
	 *     If both strings are equal, empty or null, 0 will be returned.<br>
	 *     If one of the strings is empty or null, the length of the other string will be returned.<br>
	 * </p>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * countDifferenceBackwardsStripped(null, null) -> 0
	 * countDifferenceBackwardsStripped("", "") -> 0
	 * countDifferenceBackwardsStripped("abc", "abc") -> 0
	 * countDifferenceBackwardsStripped("xyz", "abc") -> 3
	 * countDifferenceBackwardsStripped("abcde", "abc  ") -> 5
	 * countDifferenceBackwardsStripped("abcde", "cde  ") -> 2
	 * countDifferenceBackwardsStripped("abcde", "cde") -> 2
	 * }</pre>
	 * @param base The base string
	 * @param compare The string to compare
	 * @return The difference between the given strings
	 * @see #countDifferenceBackwards(String, String)
	 */
	public static int countDifferenceBackwardsStripped(@Nullable String base, @Nullable String compare) {
		return countDifferenceBackwards(strip(base), strip(compare));
	}
	
	/**
	 * Counts the difference between the given strings, aligned.<br>
	 * The difference will be counted in both directions, and the lowest difference will be returned.<br>
	 * <p>
	 *     If both strings are equal, empty or null, 0 will be returned.<br>
	 *     If one of the strings is empty or null, the length of the other string will be returned.<br>
	 * </p>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * countDifferenceAligned(null, null) -> 0
	 * countDifferenceAligned("", "") -> 0
	 * countDifferenceAligned("abc", "abc") -> 0
	 * countDifferenceAligned("xyz", "abc") -> 3
	 * countDifferenceAligned("abcde", "abc  ") -> 2
	 * countDifferenceAligned("abcde", "  cde") -> 2
	 * countDifferenceAligned("abcde", "cde") -> 2
	 * }</pre>
	 * @param base The base string
	 * @param compare The string to compare
	 * @return The difference between the given strings
	 * @see #countDifference(String, String)
	 * @see #countDifferenceBackwards(String, String)
	 * @see #countDifferenceStripped(String, String)
	 * @see #countDifferenceBackwardsStripped(String, String)
	 */
	public static int countDifferenceAligned(@Nullable String base, @Nullable String compare) {
		int def = countDifference(base, compare);
		if (0 >= def) {
			return def;
		}
		List<Integer> differences = Lists.newArrayList(def);
		differences.add(countDifferenceBackwards(base, compare));
		if (containsWhitespace(base) || containsWhitespace(compare)) {
			differences.add(countDifferenceStripped(base, compare));
			differences.add(countDifferenceBackwardsStripped(base, compare));
		}
		return Collections.min(differences);
	}
	//endregion
	
	/**
	 * Calculates the levenshtein distance between the given strings.<br>
	 * <p>
	 *     If both strings are equal, empty or null, 0 will be returned.<br>
	 *     If one of the strings is empty or null, the length of the other string will be returned.<br>
	 * </p>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * levenshteinDistance(null, *) -> 0
	 * levenshteinDistance(*, null) -> 0
	 * levenshteinDistance("", *) -> 0
	 * levenshteinDistance(*, "") -> 0
	 * levenshteinDistance("abc", "") -> 3
	 * levenshteinDistance("", "abc") -> 3
	 * levenshteinDistance("abc", "abc") -> 0
	 * levenshteinDistance("abc", "def") -> 3
	 * levenshteinDistance("test", "text") -> 1
	 * levenshteinDistance("abed", "tset") -> 3
	 * }</pre>
	 *
	 * @param base The base string
	 * @param compare The string to compare
	 * @return The levenshtein distance between the given strings
	 */
	@SuppressWarnings("ConstantConditions") // Ignore null warnings, because the baseDifference method already checks for null
	public static int levenshteinDistance(@Nullable String base, @Nullable String compare) {
		int baseDifference = baseDifference(base, compare);
		if (baseDifference >= 0) {
			return baseDifference;
		}
		
		if (base.length() > compare.length()) {
			String tmp = base;
			base = compare;
			compare = tmp;
		}
		int baseLength = base.length();
		int compareLength = compare.length();
		if (baseLength == 0) {
			return compareLength;
		}
		int baseIndex;
		int[] previousCost = new int[baseLength + 1];
		for (baseIndex = 0; baseIndex <= baseLength; baseIndex++) {
			previousCost[baseIndex] = baseIndex;
		}
		int cost;
		int upper;
		int upperLeft;
		char toCompare;
		for (int compareIndex = 1; compareIndex <= compareLength; compareIndex++) {
			upperLeft = previousCost[0];
			toCompare = compare.charAt(compareIndex - 1);
			previousCost[0] = compareIndex;
			for (baseIndex = 1; baseIndex <= baseLength; baseIndex++) {
				upper = previousCost[baseIndex];
				cost = base.charAt(baseIndex - 1) == toCompare ? 0 : 1;
				previousCost[baseIndex] = Math.min(Math.min(previousCost[baseIndex - 1] + 1, previousCost[baseIndex] + 1), upperLeft + cost);
				upperLeft = upper;
			}
		}
		return previousCost[baseLength];
	}
	
	/**
	 * Finds similar strings to the given base string.<br>
	 * A value will be considered similar if the difference is less or equal to the given {@code allowDifference}.<br>
	 * The similarity will be checked with default counter-function {@link #countDifference(String, String)}.<br>
	 * <p>
	 *     In the following cases, an empty list will be returned:
	 * </p>
	 * <ul>
	 *     <li>The base string is empty</li>
	 *     <li>The allowed difference is less than 0</li>
	 *     <li>The given array of values is null or empty</li>
	 * </ul>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * findSimilar(null, *, *) -> []
	 * findSimilar("", * *) -> []
	 * findSimilar(*, -1, *) -> []
	 * findSimilar(*, *, null) -> []
	 * findSimilar("abc", 1, ["abc", "def", "ghi"]) -> ["abc"]
	 * findSimilar("ab*", 1, ["ab0", "ab1"]) -> ["ab0", "ab1"]
	 * findSimilar("abc", 2 ["bcd", "cde", "def", "efg"]) -> []
	 * }</pre>
	 *
	 * @param base The base string
	 * @param allowDifference The allowed difference between the base string and the values
	 * @param values The array of values to check
	 * @return A list with all similar strings to the base string
	 * @see #findSimilar(String, int, ToIntBiFunction, List)
	 */
	public static @NotNull List<String> findSimilar(@Nullable String base, int allowDifference, String @Nullable ... values) {
		return findSimilar(base, allowDifference, StringUtils::countDifference, Arrays.asList(ArrayUtils.nullToEmpty(values)));
	}
	
	/**
	 * Finds similar strings to the given base string.<br>
	 * A value will be considered similar if the difference is less or equal to the given {@code allowDifference}.<br>
	 * The similarity will be checked with default counter-function {@link #countDifference(String, String)}.<br>
	 * <p>
	 *     In the following cases, an empty list will be returned:
	 * </p>
	 * <ul>
	 *     <li>The base string is empty</li>
	 *     <li>The allowed difference is less than 0</li>
	 *     <li>The given list of values is null or empty</li>
	 * </ul>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * findSimilar(null, *, *) -> []
	 * findSimilar("", * *) -> []
	 * findSimilar(*, -1, *) -> []
	 * findSimilar(*, *, null) -> []
	 * findSimilar("abc", 1, ["abc", "def", "ghi"]) -> ["abc"]
	 * findSimilar("ab*", 1, ["ab0", "ab1"]) -> ["ab0", "ab1"]
	 * findSimilar("abc", 2 ["bcd", "cde", "def", "efg"]) -> []
	 * }</pre>
	 *
	 * @param base The base string
	 * @param allowDifference The allowed difference between the base string and the values
	 * @param values The list of values to check
	 * @return A list with all similar strings to the base string
	 * @see #findSimilar(String, int, ToIntBiFunction, List)
	 */
	public static @NotNull List<String> findSimilar(@Nullable String base, int allowDifference, @Nullable List<String> values) {
		return findSimilar(base, allowDifference, StringUtils::countDifference, values);
	}
	
	/**
	 * Finds similar strings to the given base string.<br>
	 * A value will be considered similar if the difference is less or equal to the given {@code allowDifference}.<br>
	 * The similarity will be checked with the given counter-function.<br>
	 * <p>
	 *     In the following cases, an empty list will be returned:
	 * </p>
	 * <ul>
	 *     <li>The base string is empty</li>
	 *     <li>The allowed difference is less than 0</li>
	 *     <li>The given list of values is null or empty</li>
	 * </ul>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * findSimilar(null, *, *, *) -> []
	 * findSimilar("", *, *, *) -> []
	 * findSimilar(*, -1, *, *) -> []
	 * findSimilar(*, *, null, *) -> []
	 * findSimilar(*, *, *, null) -> []
	 * findSimilar(*, *, *, []) -> []
	 * }</pre>
	 *
	 * @param base The base string
	 * @param allowDifference The allowed difference between the base string and the values
	 * @param counter The counter-function to check the difference
	 * @param values The list of values to check
	 * @return A list with all similar strings to the base string
	 */
	public static @NotNull List<String> findSimilar(@Nullable String base, int allowDifference, @Nullable ToIntBiFunction<String, String> counter, @Nullable List<String> values) {
		List<String> similar = Lists.newArrayList();
		if (isEmpty(base) || allowDifference < 0 || counter == null || values == null || values.isEmpty()) {
			return similar;
		}
		for (String value : values) {
			if (counter.applyAsInt(base, value) <= allowDifference) {
				similar.add(value);
			}
		}
		return similar;
	}
	
	/**
	 * Checks if the given {@code target} character is followed by any string inside the given {@code follows} array.<br>
	 * If the {@code target} character is found in the given string, all occurrences will be checked.<br>
	 * <p>
	 *     If all occurrences of the {@code target} character are followed by any string inside the {@code follows} array,
	 *     true will be returned.<br>
	 *     If the string is empty or the character is not found, false will be returned.
	 * </p>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * isAfterAllOccurrence(null, *, *) -> false
	 * isAfterAllOccurrence("", *, *) -> false
	 * isAfterAllOccurrence(*, *, null) -> false
	 * isAfterAllOccurrence("abcde", 'a', []) -> true
	 * isAfterAllOccurrence("abcde", 'x', []) -> false // No occurrence
	 * isAfterAllOccurrence("abcde", 'a', ["b"]) -> true // First occurrence matches
	 * isAfterAllOccurrence("abcab", 'a', ["b"]) -> true // Both occurrences match
	 * isAfterAllOccurrence("abcae", 'a', ["b"]) -> false // Second occurrence does not match
	 * isAfterAllOccurrence("abcae", 'a', ["b", "e"]) -> true // Both occurrences match
	 * }</pre>
	 *
	 * @param str The string to check
	 * @param target The character to check if it is followed by any string
	 * @param follows The strings to check if any of them {@code follows} the target character
	 * @return True if the string contains the target character, and it is followed by any string inside the {@code follows} array, false otherwise
	 */
	public static boolean isAfterAllOccurrence(@Nullable String str, char target, String @Nullable ... follows) {
		if (str == null && follows == null) {
			return true;
		}
		if (isEmpty(str) || follows == null) {
			return false;
		}
		List<Integer> indexes = indexOfAll(str, target);
		if (indexes.isEmpty()) {
			return false;
		}
		if (follows.length == 0) {
			return true;
		}
		for (int index : indexes) {
			if (index + 1 >= str.length() || !startsWithAny(str.substring(index + 1), follows)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the given {@code target} character is preceded by any string inside the given {@code precedes} array.<br>
	 * If the {@code target} character is found, all occurrences will be checked.<br>
	 * <p>
	 *     If all occurrences of the {@code target} character are preceded by any string inside the {@code precedes} array,
	 *     true will be returned.<br>
	 *     If the string is empty or the character is not found, false will be returned.
	 * </p>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * isBeforeAllOccurrence(null, *, *) -> false
	 * isBeforeAllOccurrence("", *, *) -> false
	 * isBeforeAllOccurrence(*, *, null) -> false
	 * isBeforeAllOccurrence("abcde", 'd', []) -> true
	 * isBeforeAllOccurrence("abcde", 'x', []) -> false // No occurrence
	 * isBeforeAllOccurrence("abcde", 'e', ["d"]) -> true // First occurrence matches
	 * isBeforeAllOccurrence("aecae", 'e', ["a"]) -> true // Both occurrences match
	 * isBeforeAllOccurrence("aecde", 'e', ["a"]) -> false // Second occurrence does not match
	 * isBeforeAllOccurrence("aecde", 'e', ["a", "d"]) -> true // Both occurrences match
	 * }</pre>
	 *
	 * @param str The string to check
	 * @param target The character to check if it is preceded by any string
	 * @param precedes The strings to check if any of them {@code precedes} the target character
	 * @return True if the string contains the target character, and it is preceded by any string inside the {@code precedes} array, false otherwise
	 */
	public static boolean isBeforeAllOccurrence(@Nullable String str, char target, String @Nullable ... precedes) {
		return isAfterAllOccurrence(reverse(str), target, precedes);
	}
	
	/**
	 * Checks if the given {@code target} character is surrounded by any string inside the given {@code surrounded} array.<br>
	 * If the target character is found, all occurrences will be checked.<br>
	 * <p>
	 *     If the {@code target} character is found, and it is surrounded by any string inside the {@code surrounded} array,
	 *     true will be returned.<br>
	 *     If the string is empty or the character is not found, false will be returned.
	 * </p>
	 * <p>
	 *     The strings inside the {@code surrounded} array will be checked in the order they are given.<br>
	 *     Each string will be reversed to check if it's before the {@code target} character.
	 * </p>
	 * <p>
	 *     If the system property 'lang.surrounded.reverse.brackets' is set to true,
	 *     the strings inside the {@code surrounded} array will be reversed including brackets.
	 * </p>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * isSurroundedBy(null, *, *) -> false
	 * isSurroundedBy("", *, *) -> false
	 * isSurroundedBy(*, *, null) -> false
	 * isSurroundedBy("a.*.b", '*', []) -> true
	 * isSurroundedBy("a.*.b", 'x', []) -> false // No occurrence
	 * isSurroundedBy("a.*.b", '*', ["."]) -> true // First occurrence matches
	 * isSurroundedBy("a.*.b.*.c", '*', ["."]) -> true // Both occurrences match
	 * isSurroundedBy("a.*.b.?*?.c", '*', ["."]) -> false // Second occurrence does not match
	 * isSurroundedBy("a.*.b.?*?.c", '*', [".", "?"]) -> true // Both occurrences match
	 * isSurroundedBy("a.*.b.?*?.c", '*', [".", "?."]) -> true // Both occurrences match
	 * // With system property 'lang.surrounded.reverse.brackets' set to true
	 * isSurroundedBy("a.[*].b", '*', ["]."]) -> true
	 * isSurroundedBy("a.[*[.b", '*', ["[."]) -> false
	 * }</pre>
	 *
	 * @param str The string to check
	 * @param target The character to check if it is surrounded by any string
	 * @param surrounded The strings to check if any of them surrounds the target character
	 * @return True if the string contains the target character, and it is surrounded by any string inside the {@code surrounded} array, false otherwise
	 * @see #LANG_SURROUNDED_REVERSE_BRACKETS
	 * @see #reverseIncludeBrackets(String)
	 */
	public static boolean isSurroundedBy(@Nullable String str, char target, String @Nullable ... surrounded) {
		if (str == null && surrounded == null) {
			return true;
		}
		if (isEmpty(str) || surrounded == null) {
			return false;
		}
		if (surrounded.length == 0) {
			return contains(str, target);
		}
		List<Integer> indexes = indexOfAll(str, target);
		if (indexes.isEmpty()) {
			return false;
		}
		boolean reverseBrackets = Boolean.parseBoolean(System.getProperty(LANG_SURROUNDED_REVERSE_BRACKETS, "false"));
		for (int index : indexes) {
			String before = str.substring(0, index);
			String reverse = reverseBrackets ? reverseIncludeBrackets(before) : reverse(before);
			if (!startsWithAny(reverse, surrounded) || !startsWithAny(str.substring(index + 1), surrounded)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Removes quoted parts from the given string.<br>
	 * <p>
	 *     Double quotes will be removed before single quotes.<br>
	 *     If a quote is escaped with a backslash, it will be ignored.
	 * </p>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * removeQuoted(null) -> ""
	 * removeQuoted("") -> ""
	 * removeQuoted("abc") -> "abc"
	 * removeQuoted("a\"b\"c") -> "ac"
	 * removeQuoted("a'b'c") -> "ac"
	 * removeQuoted("a\"b\\\"c\"d") -> "ad"
	 * removeQuoted("a'b\\'c'd") -> "ad"
	 * removeQuoted("a\"b'c\"d'e") -> "ad'e"
	 * }</pre>
	 *
	 * @param str The string to remove the quoted parts from
	 * @return The string without the quoted parts
	 */
	public static @NotNull String removeQuoted(@Nullable String str) {
		if (isEmpty(str)) {
			return "";
		}
		if (str.contains("\"")) {
			str = str.replaceAll(DOUBLE_QUOTE_PATTERN, "");
		}
		if (str.contains("'")) {
			return str.replaceAll(SINGLE_QUOTE_PATTERN, "");
		}
		return str;
	}
	
	/**
	 * Checks if the given string is matching balanced.<br>
	 * This means that the open and close characters are in the correct order and have the same number of occurrences.<br>
	 * <p>
	 *     If the system property 'lang.match.in.quotes' is set to false,<br>
	 *     double and single quotes will be removed from the string before checking.
	 * </p>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * matchingBalanced(null, *, *) -> false
	 * matchingBalanced("", *, *) -> true
	 * matchingBalanced(" ", *, *) -> true
	 * matchingBalanced("(", '(', '(') -> true
	 * matchingBalanced("(", '(', ')') -> false
	 * matchingBalanced("(())", '(', ')') -> true
	 * matchingBalanced("(()", '(', ')') -> false
	 * // With system property 'lang.match.in.quotes' set to true
	 * matchingBalanced("(\"(\")", '(', ')') -> false
	 * matchingBalanced("('(')", '(', ')') -> false
	 * matchingBalanced("((\")\")", '(', ')') -> true
	 * matchingBalanced("((')')", '(', ')') -> true
	 * }</pre>
	 *
	 * @param str The string to check
	 * @param open The open character
	 * @param close The close character
	 * @return True if the string is matching balanced, false otherwise
	 * @see #matchingBalanced(String, String, String)
	 * @see #LANG_MATCH_IN_QUOTES
	 */
	public static boolean matchingBalanced(@Nullable String str, char open, char close) {
		return matchingBalanced(str, String.valueOf(open), String.valueOf(close));
	}
	
	/**
	 * Checks if the given string is matching balanced.<br>
	 * This means that the open and close strings are in the correct order and have the same number of occurrences.<br>
	 * <p>
	 *     If the system property 'lang.match.in.quotes' is set to false,<br>
	 *     double and single quotes will be removed from the string before checking.
	 * </p>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * matchingBalanced(null, *, *) -> true
	 * matchingBalanced(*, null, null) -> true
	 * matchingBalanced(*, null, *) -> false
	 * matchingBalanced(*, *, null) -> false
	 * matchingBalanced("", *, *) -> true
	 * matchingBalanced(" ", *, *) -> true
	 * matchingBalanced("(", "(", "(") -> true
	 * matchingBalanced("(", "(", ")") -> false
	 * matchingBalanced("(())", "(", ")") -> true
	 * matchingBalanced("(()", "(", ")") -> false
	 * // With system property 'lang.match.in.quotes' set to true
	 * matchingBalanced("(\"(\")", "(", ")") -> false
	 * matchingBalanced("('(')", "(", ")") -> false
	 * matchingBalanced("((\")\")", "(", ")") -> true
	 * matchingBalanced("((')')", "(", ")") -> true
	 * }</pre>
	 *
	 * @param str The string to check
	 * @param open The open string
	 * @param close The close string
	 * @return True if the string is matching balanced, false otherwise
	 * @see #LANG_MATCH_IN_QUOTES
	 */
	public static boolean matchingBalanced(@Nullable String str, @Nullable String open, @Nullable String close) {
		if (str == null || (open == null) != (close == null)) {
			return false;
		}
		if (isBlank(str) || ObjectUtils.allNull(open, close) || (Objects.equal(open, close) && str.equals(open))) {
			return true;
		}
		assert open != null;
		// Inverted, keep quotes if system property is set to true
		if (!Boolean.parseBoolean(System.getProperty(LANG_MATCH_IN_QUOTES, "false"))) {
			str = removeQuoted(str);
		}
		Deque<String> stack = new ArrayDeque<>();
		for (int i = 0; i < str.length(); i++) {
			if (str.startsWith(open, i)) {
				stack.push(open);
				i += open.length() - 1;
			} else if (str.startsWith(close, i)) {
				if (stack.isEmpty()) {
					return false;
				}
				stack.pop();
				i += close.length() - 1;
			}
		}
		return stack.isEmpty();
	}
	
	/**
	 * Checks if the given string matches the given pattern.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * matchesPattern(null, null) -> true
	 * matchesPattern(null, *) -> false
	 * matchesPattern(*, null) -> false
	 * matchesPattern(Pattern.compile("a"), "a") -> true
	 * matchesPattern(Pattern.compile("a"), "b") -> false
	 * }</pre>
	 *
	 * @param pattern The pattern to match
	 * @param str The string to check
	 * @return True if the string matches the pattern, false otherwise
	 */
	public static boolean matchesPattern(@Nullable Pattern pattern, @Nullable String str) {
		if (pattern == null && str == null) {
			return true;
		}
		if (pattern == null || str == null) {
			return false;
		}
		return pattern.matcher(str).matches();
	}
	
	/**
	 * Checks if the string contains the given target character.<br>
	 * The target character will be ignored if it is escaped with a backslash.<br>
	 * If the string is empty or null, false will be returned.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * containsNotEscaped(null, *) -> false
	 * containsNotEscaped("", *) -> false
	 * containsNotEscaped("abc", 'a') -> true
	 * containsNotEscaped("aba", 'c') -> false
	 * containsNotEscaped("\\abc", 'a') -> false
	 * containsNotEscaped("\\aba", 'a') -> true
	 * }</pre>
	 *
	 * @param str The string to check
	 * @param target The target character to check
	 * @return True if the string contains the target character not escaped, false otherwise
	 */
	public static boolean containsNotEscaped(@Nullable String str, char target) {
		if (isEmpty(str)) {
			return false;
		}
		boolean escaped = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\\') {
				escaped = !escaped;
			} else if (c == target && !escaped) {
				return true;
			} else {
				escaped = false;
			}
		}
		return false;
	}
	
	/**
	 * Splits the given string by the given target character.<br>
	 * The target character will be ignored if it is escaped with a backslash.<br>
	 * If the string is empty or null, an empty array will be returned.<br>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * splitNotEscaped(null, *) -> []
	 * splitNotEscaped("", *) -> []
	 * splitNotEscaped("abc", 'b') -> ["a", "c"]
	 * splitNotEscaped("a\\bc", 'b') -> ["abc"]
	 * splitNotEscaped("a\\bc", 'c') -> ["a\\b"]
	 * splitNotEscaped("a\\:b:c:d\\:e", ':') -> ["a\\:b", "c", "d\\:e"]
	 * }</pre>
	 *
	 * @param str The string to split
	 * @param target The target character to split by
	 * @return The parts of the string split by the target character
	 */
	public static String @NotNull [] splitNotEscaped(@Nullable String str, char target) {
		if (isEmpty(str)) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		List<String> parts = Lists.newArrayList();
		StringBuilder builder = new StringBuilder();
		boolean escaped = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c == '\\') {
				escaped = !escaped;
				builder.append(c);
			} else if (c == target && !escaped) {
				parts.add(builder.toString());
				builder.setLength(0);
			} else {
				builder.append(c);
				escaped = false;
			}
		}
		String remaining = builder.toString();
		if (!remaining.isEmpty()) {
			parts.add(remaining);
		}
		return parts.toArray(String[]::new);
	}
	
	/**
	 * Extracts all groups from the given string which match the given regex.<br>
	 * <p>
	 *     If the string is null or empty, an empty array will be returned.<br>
	 *     If the regex is null, empty or does not match, an empty array will be returned.
	 * </p>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * extract(null, null) -> []
	 * extract(null, *) -> []
	 * extract(*, null) -> []
	 * extract("", *) -> []
	 * extract(*, "") -> []
	 * extract("abc", "a") -> ["a"]
	 * extract("abc", "abc") -> ["abc"]
	 * extract("abc.[def].ghi", "\\[.*?]") -> ["[def]"]
	 * }</pre>
	 *
	 * @param str The string to extract the groups from
	 * @param regex The regex to match
	 * @return The extracted groups
	 */
	public static String @NotNull [] extract(@Nullable String str, @Language("RegExp") @Nullable String regex) {
		if (isEmpty(str) || isEmpty(regex)) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		return extract(str, Pattern.compile(regex));
	}
	
	/**
	 * Extract all groups from the given string which match the given pattern.<br>
	 * <p>
	 *     If the string is null or empty, an empty array will be returned.<br>
	 *     If the pattern is null or does not match, an empty array will be returned.
	 * </p>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * extract(null, null) -> []
	 * extract(null, *) -> []
	 * extract(*, null) -> []
	 * extract("abc", Pattern.compile("a")) -> ["a"]
	 * extract("abc", Pattern.compile("abc")) -> ["abc"]
	 * extract("abc.[def].ghi", Pattern.compile("\\[.*?]")) -> ["[def]"]
	 * }</pre>
	 *
	 * @param str The string to extract the groups from
	 * @param pattern The pattern to match
	 * @return The extracted groups
	 */
	public static String @NotNull [] extract(@Nullable String str, @Nullable Pattern pattern) {
		if (isEmpty(str) || pattern == null) {
			return ArrayUtils.EMPTY_STRING_ARRAY;
		}
		Matcher matcher = pattern.matcher(str);
		List<String> groups = Lists.newArrayList();
		while (matcher.find()) {
			groups.add(matcher.group());
		}
		return groups.toArray(String[]::new);
	}
	
	/**
	 * Converts the given string to a readable string.<br>
	 * <p>
	 *     The string will be split by the given condition.<br>
	 *     The first character of each part will be capitalized, the rest will be lowercased.<br>
	 *     The parts will be joined by a space.
	 * </p>
	 * <p>
	 *     If the string is null or empty, an empty string will be returned.<br>
	 *     If the condition is null, the string will be returned as is.
	 * </p>
	 * <p>
	 *     Examples:
	 * </p>
	 * <pre>{@code
	 * getReadableString(null, *) -> ""
	 * getReadableString("", *) -> ""
	 * getReadableString("abc", null) -> "abc"
	 * getReadableString("abc", Character::isUpperCase) -> "abc"
	 * getReadableString("abc", Character::isLowerCase) -> "A B C"
	 * getReadableString("abcDEFghi", Character::isUpperCase) -> "abc D E Fghi"
	 * }</pre>
	 *
	 * @param str The string to convert
	 * @param condition The condition to split the string by
	 * @return The readable string
	 */
	public static @NotNull String getReadableString(@Nullable String str, @Nullable Predicate<Character> condition) {
		if (isEmpty(str)) {
			return "";
		}
		if (condition == null) {
			return str;
		}
		StringBuilder name = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (condition.test(c)) {
				name.append(" ");
				name.append(Character.toUpperCase(c));
			} else {
				name.append(Character.toLowerCase(c));
			}
		}
		return name.toString().strip();
	}
}
