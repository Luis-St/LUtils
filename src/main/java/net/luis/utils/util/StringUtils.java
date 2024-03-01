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

package net.luis.utils.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.ToIntBiFunction;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * A utility class for string operations.<br>
 *
 * @author Luis-St
 */
public class StringUtils {
	
	/**
	 * Searches for occurrences of the given {@code search} character in the given string.<br>
	 * The index of the found occurrences will be returned in a list.<br>
	 * If the given string is empty, an empty list will be returned.<br>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * indexOfAll(null, *) -> []
	 * indexOfAll("", *) -> []
	 * indexOfAll("abc", 'a') -> [0]
	 * indexOfAll("aba", 'a') -> [0, 2]
	 * indexOfAll("   ", ' ') -> [0, 1, 2]
	 * }</pre>
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
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * indexOfAll(null, *) -> []
	 * indexOfAll("", *) -> []
	 * indexOfAll("abed", "a") -> [0]
	 * indexOfAll("abac", "ab") -> [0]
	 * indexOfAll("abac ba", "ba") -> [1, 4]
	 * }</pre>
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
	
	/**
	 * Calculates the levenshtein distance between the given strings.<br>
	 * <p>
	 *     If both strings are equal, empty or null, 0 will be returned.<br>
	 *     If one of the strings is empty or null, the length of the other string will be returned.<br>
	 * </p>
	 * <p>
	 *     Examples:<br>
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
	 *     In the following cases, an empty list will be returned:<br>
	 * </p>
	 * <ul>
	 *     <li>The base string is empty</li>
	 *     <li>The allowed difference is less than 0</li>
	 *     <li>The given array of values is null or empty</li>
	 * </ul>
	 * <p>
	 *     Examples:<br>
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
	 *     In the following cases, an empty list will be returned:<br>
	 * </p>
	 * <ul>
	 *     <li>The base string is empty</li>
	 *     <li>The allowed difference is less than 0</li>
	 *     <li>The given list of values is null or empty</li>
	 * </ul>
	 * <p>
	 *     Examples:<br>
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
	 *     In the following cases, an empty list will be returned:<br>
	 * </p>
	 * <ul>
	 *     <li>The base string is empty</li>
	 *     <li>The allowed difference is less than 0</li>
	 *     <li>The given list of values is null or empty</li>
	 * </ul>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * findSimilar(null, *, *, *) -> []
	 * findSimilar("", *, *, *) -> []
	 * findSimilar(*, -1, *, *) -> []
	 * findSimilar(*, *, null, *) -> []
	 * findSimilar(*, *, *, null) -> []
	 * findSimilar(*, *, *, []) -> []
	 * }</pre>
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
	 * Checks if the given characters follow the given {@code first} character in the given string.<br>
	 * If the {@code first} character is found, all occurrences will be checked if<br>
	 * they are followed by the given {@code follows} characters.<br>
	 * <p>
	 *     If all occurrences of the {@code first} character are followed by the given {@code follows} characters, true will be returned.<br>
	 *     If the string is empty or the character is not found, false will be returned.<br>
	 * </p>
	 * <p>
	 *     The characters {@code follows} must be in the correct order.<br>
	 *     The character {@code first} will be the first character in the string.<br>
	 * </p>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * isAfter(null, *, *) -> false
	 * isAfter("", *, *) -> false
	 * isAfter("abcde", 'a') -> true
	 * isAfter("abcde", 'a', 'b') -> true // First occurrence matches
	 * isAfter("abcae", 'a', 'b') -> false // Second occurrence does not match
	 * isAfter("abcab", 'a', 'b') -> true // Both occurrences match
	 * }</pre>
	 * @param str The string to check
	 * @param first The first character (in the string)
	 * @param follows The characters to check if they follow the first character
	 * @return True if the string contains the first character, and it is followed by the given characters, false otherwise
	 */
	public static boolean isAfter(@Nullable String str, char first, char @Nullable ... follows) {
		List<Integer> indexes = indexOfAll(str, first);
		if (indexes.isEmpty() || isEmpty(str)) {
			return false;
		}
		String follow = first + String.valueOf(ArrayUtils.nullToEmpty(follows));
		for (int index : indexes) {
			if (!str.startsWith(follow, index)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the given characters precede the given {@code first} character in the given string.<br>
	 * If the {@code first} character is found, all occurrences will be checked if<br>
	 * they are preceded by the given {@code precedes} characters.<br>
	 * <p>
	 *     If all occurrences of the {@code first} character are preceded by the given {@code precedes} characters, true will be returned.<br>
	 *     If the string is empty or the character is not found, false will be returned.<br>
	 * </p>
	 * <p>
	 *     The characters {@code precedes} must be in the correct order.<br>
	 *     The character {@code first} will be the first character in the string.<br>
	 * </p>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * isBefore(null, *, *) -> false
	 * isBefore("", *, *) -> false
	 * isBefore("abcde", 'd') -> true
	 * isBefore("abcde", 'd', 'e') -> true // First occurrence matches
	 * isBefore("abcae", 'd', 'e') -> false // Second occurrence does not match
	 * isBefore("aecae", 'a', 'e') -> true // Both occurrences match
	 * }</pre>
	 * @param str The string to check
	 * @param first The first character (in the string)
	 * @param precedes The characters to check if they precede the first character
	 * @return True if the string contains the first character, and it is preceded by the given characters, false otherwise
	 */
	public static boolean isBefore(@Nullable String str, char first, char @Nullable ... precedes) {
		if (isEmpty(str)) {
			return false;
		}
		String reverse = reverse(str);
		if (precedes == null || precedes.length == 0) {
			return isAfter(reverse, first);
		}
		char[] precede = (first + String.valueOf(precedes)).toCharArray();
		ArrayUtils.reverse(precede);
		return isAfter(reverse, precede[0], ArrayUtils.subarray(precede, 1, precede.length));
	}
	
	/**
	 * Checks if the centered character of the {@code between} characters is in the given string.<br>
	 * If the centered character is found, the characters before and after the centered character will be checked.<br>
	 * <p>
	 *     If there is no centered character, false will be returned.<br>
	 *     If the string is empty or the centered character is not found, false will be returned.<br>
	 * </p>
	 * <p>
	 *     If the characters before and after the centered character are in the correct order,<br>
	 *     around the centered character, true will be returned.<br>
	 *     If there are multiple occurrences of the centered character, all occurrences will be checked.<br>
	 * </p>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * isBetween(null, *) -> false
	 * isBetween("", *) -> false
	 * isBetween(*, null) -> false
	 * isBetween(*, <even length>) -> false
	 * isBetween("ab.*.cd", '*') -> true
	 * isBetween("ab.*.cd", '.', '*', '.') -> true
	 * isBetween("ab.*.cd", '.', '*', '*') -> false
	 * }</pre>
	 * @param str The string to check
	 * @param between The characters to check if they are around the centered character
	 * @return True if the string contains the centered character, and the characters before and after are in the correct order, false otherwise
	 */
	public static boolean isBetween(@Nullable String str, char @Nullable ... between) {
		if (isEmpty(str) || between == null || between.length % 2 == 0) {
			return false;
		}
		List<Integer> indexes = indexOfAll(str, between[between.length / 2]);
		if (indexes.isEmpty()) {
			return false;
		}
		char[] before = ArrayUtils.subarray(between, 0, between.length / 2);
		char[] after = ArrayUtils.subarray(between, between.length / 2 + 1, between.length);
		for (int index : indexes) {
			if (!str.startsWith(String.valueOf(after), index + 1)) {
				return false;
			}
			if (!str.substring(index + 1).startsWith(String.valueOf(before))) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the given string is matching balanced.<br>
	 * This means that the open and close characters are in the correct order and have the same number of occurrences.<br>
	 * <p>
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * matchingBalanced(null, *, *) -> false
	 * matchingBalanced("", *, *) -> true
	 * matchingBalanced(" ", *, *) -> true
	 * matchingBalanced("(", '(', '(') -> true
	 * matchingBalanced("(", '(', ')') -> false
	 * matchingBalanced("(())", '(', ')') -> true
	 * matchingBalanced("(()", '(', ')') -> false
	 * }</pre>
	 * @param str The string to check
	 * @param open The open character
	 * @param close The close character
	 * @return True if the string is matching balanced, false otherwise
	 */
	public static boolean matchingBalanced(@Nullable String str, char open, char close) {
		if (str == null) {
			return false;
		}
		if (isBlank(str) || (str.length() == 1 && open == close)) {
			return true;
		}
		Stack<Character> stack = new Stack<>();
		for (char c : str.toCharArray()) {
			if (c == open) {
				stack.push(c);
			} else if (c == close) {
				if (stack.isEmpty()) {
					return false;
				}
				stack.pop();
			}
		}
		return stack.isEmpty();
	}
	
	/**
	 * Checks if the given string is matching balanced.<br>
	 * This means that the open and close strings are in the correct order and have the same number of occurrences.<br>
	 * <p>
	 *     Examples:<br>
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
	 * }</pre>
	 * @param str The string to check
	 * @param open The open string
	 * @param close The close string
	 * @return True if the string is matching balanced, false otherwise
	 */
	public static boolean matchingBalanced(@Nullable String str, @Nullable String open, @Nullable String close) {
		if (str == null) {
			return false;
		}
		if (open == null && close == null) {
			return true;
		}
		if ((open == null) != (close == null)) {
			return false;
		}
		if (isBlank(str) || (open.equals(close) && str.equals(open))) {
			return true;
		}
		Stack<String> stack = new Stack<>();
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
	 *     Examples:<br>
	 * </p>
	 * <pre>{@code
	 * matchesPattern(null, null) -> true
	 * matchesPattern(null, *) -> false
	 * matchesPattern(*, null) -> false
	 * matchesPattern(Pattern.compile("a"), "a") -> true
	 * matchesPattern(Pattern.compile("a"), "b") -> false
	 * }</pre>
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
}
