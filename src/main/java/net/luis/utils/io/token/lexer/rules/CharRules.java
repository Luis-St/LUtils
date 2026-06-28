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

package net.luis.utils.io.token.lexer.rules;

import net.luis.utils.io.token.lexer.rules.anchors.EndCharRule;
import net.luis.utils.io.token.lexer.rules.anchors.StartCharRule;
import net.luis.utils.io.token.lexer.rules.combinators.CharAnyOfRule;
import net.luis.utils.io.token.lexer.rules.combinators.CharSequenceRule;
import net.luis.utils.io.token.lexer.rules.matchers.*;
import net.luis.utils.io.token.lexer.rules.quantifiers.CharOptionalRule;
import net.luis.utils.io.token.lexer.rules.quantifiers.CharRepeatedRule;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * A utility class for creating character rules.<br>
 * It mirrors the parser-side token rule factory but is restricted to regular constructs.<br>
 *
 * @author Luis-St
 */
public final class CharRules {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private CharRules() {}
	
	/**
	 * Creates a character rule that matches a single specific character.<br>
	 *
	 * @param character The character to match against
	 * @return The created character rule
	 * @see LiteralCharRule
	 */
	public static @NonNull CharRule literal(char character) {
		return new LiteralCharRule(character);
	}
	
	/**
	 * Creates a character rule that matches a single character that is one of the given characters.<br>
	 *
	 * @param characters The characters to match against
	 * @return The created character rule
	 * @throws NullPointerException If the character array is null
	 * @throws IllegalArgumentException If the character array is empty
	 * @see CharSetRule
	 */
	public static @NonNull CharRule anyOf(char @NonNull ... characters) {
		Objects.requireNonNull(characters, "Character array must not be null");
		Set<Character> set = new LinkedHashSet<>();
		for (char character : characters) {
			set.add(character);
		}
		return new CharSetRule(set);
	}
	
	/**
	 * Creates a character rule that matches a single character that is a member of the given set of characters.<br>
	 *
	 * @param characters The set of characters to match against
	 * @return The created character rule
	 * @throws NullPointerException If the set of characters is null
	 * @throws IllegalArgumentException If the set of characters is empty
	 * @see CharSetRule
	 */
	public static @NonNull CharRule set(@NonNull Set<Character> characters) {
		return new CharSetRule(characters);
	}
	
	/**
	 * Creates a character rule that matches a single character that lies within the given inclusive range.<br>
	 *
	 * @param start The start of the range (inclusive)
	 * @param end The end of the range (inclusive)
	 * @return The created character rule
	 * @throws IllegalArgumentException If the end of the range is less than the start of the range
	 * @see CharRangeRule
	 */
	public static @NonNull CharRule range(char start, char end) {
		return new CharRangeRule(start, end);
	}
	
	/**
	 * Creates a character rule that matches a single digit character from {@code '0'} to {@code '9'}.<br>
	 *
	 * @return The created character rule
	 * @see CharRangeRule
	 */
	public static @NonNull CharRule digit() {
		return range('0', '9');
	}
	
	/**
	 * Creates a character rule that matches a single ascii letter from {@code 'a'} to {@code 'z'} or {@code 'A'} to {@code 'Z'}.<br>
	 *
	 * @return The created character rule
	 * @see CharAnyOfRule
	 */
	public static @NonNull CharRule letter() {
		return any(range('a', 'z'), range('A', 'Z'));
	}
	
	/**
	 * Creates a character rule that matches a single ascii letter or digit.<br>
	 *
	 * @return The created character rule
	 * @see CharAnyOfRule
	 */
	public static @NonNull CharRule letterOrDigit() {
		return any(letter(), digit());
	}
	
	/**
	 * Creates a character rule that matches a single whitespace character.<br>
	 * The whitespace characters are the space, tab, newline, carriage return and form feed characters.<br>
	 *
	 * @return The created character rule
	 * @see CharSetRule
	 */
	public static @NonNull CharRule whitespace() {
		return anyOf(' ', '\t', '\n', '\r', '\f');
	}
	
	/**
	 * Creates a character rule that matches any single character of the lexer's alphabet.<br>
	 *
	 * @return The created character rule
	 * @apiNote This is the preferred way to access the {@link AnyCharRule#INSTANCE} instance
	 * @see AnyCharRule
	 */
	public static @NonNull CharRule any() {
		return AnyCharRule.INSTANCE;
	}
	
	/**
	 * Creates a character rule that matches a sequence (concatenation) of character rules.<br>
	 *
	 * @param charRules The character rules to match in sequence
	 * @return The created character rule
	 * @throws NullPointerException If the character rule array or any of its elements are null
	 * @throws IllegalArgumentException If the character rule array is empty or contains less than two rules
	 * @see CharSequenceRule
	 */
	public static @NonNull CharRule sequence(CharRule @NonNull ... charRules) {
		return new CharSequenceRule(List.of(Objects.requireNonNull(charRules, "Character rule array must not be null")));
	}
	
	/**
	 * Creates a character rule that matches any of the given character rules (ordered alternation).<br>
	 *
	 * @param charRules The character rules to match any of
	 * @return The created character rule
	 * @throws NullPointerException If the character rule array or any of its elements are null
	 * @throws IllegalArgumentException If the character rule array is empty or contains less than two rules
	 * @see CharAnyOfRule
	 */
	public static @NonNull CharRule any(CharRule @NonNull ... charRules) {
		return new CharAnyOfRule(List.of(Objects.requireNonNull(charRules, "Character rule array must not be null")));
	}
	
	/**
	 * Creates an optional character rule with the given character rule.<br>
	 *
	 * @param charRule The character rule to match optionally
	 * @return The created character rule
	 * @throws NullPointerException If the character rule is null
	 * @see CharOptionalRule
	 */
	public static @NonNull CharRule optional(@NonNull CharRule charRule) {
		return new CharOptionalRule(charRule);
	}
	
	/**
	 * Creates a repeated character rule that matches the given character rule at least the given number of times.<br>
	 *
	 * @param charRule The character rule to match
	 * @param min The minimum number of occurrences
	 * @return The created character rule
	 * @throws NullPointerException If the character rule is null
	 * @throws IllegalArgumentException If the minimum number of occurrences is less than 0
	 * @see CharRepeatedRule
	 */
	public static @NonNull CharRule atLeast(@NonNull CharRule charRule, int min) {
		return between(charRule, min, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a repeated character rule that matches the given character rule exactly the given number of times.<br>
	 *
	 * @param charRule The character rule to match
	 * @param repeats The exact number of occurrences
	 * @return The created character rule
	 * @throws NullPointerException If the character rule is null
	 * @throws IllegalArgumentException If the number of occurrences is lower than 0
	 * @see CharRepeatedRule
	 */
	public static @NonNull CharRule exactly(@NonNull CharRule charRule, int repeats) {
		return between(charRule, repeats, repeats);
	}
	
	/**
	 * Creates a repeated character rule that matches the given character rule at most the given number of times.<br>
	 *
	 * @param charRule The character rule to match
	 * @param max The maximum number of occurrences
	 * @return The created character rule
	 * @throws NullPointerException If the character rule is null
	 * @throws IllegalArgumentException If the maximum number of occurrences is lower than 0
	 * @see CharRepeatedRule
	 */
	public static @NonNull CharRule atMost(@NonNull CharRule charRule, int max) {
		return between(charRule, 0, max);
	}
	
	/**
	 * Creates a repeated character rule that matches the given character rule zero or more times.<br>
	 *
	 * @param charRule The character rule to match
	 * @return The created character rule
	 * @throws NullPointerException If the character rule is null
	 * @see CharRepeatedRule
	 */
	public static @NonNull CharRule zeroOrMore(@NonNull CharRule charRule) {
		return between(charRule, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a repeated character rule that matches the given character rule one or more times.<br>
	 *
	 * @param charRule The character rule to match
	 * @return The created character rule
	 * @throws NullPointerException If the character rule is null
	 * @see CharRepeatedRule
	 */
	public static @NonNull CharRule oneOrMore(@NonNull CharRule charRule) {
		return between(charRule, 1, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a repeated character rule that matches the given character rule between the given number of times.<br>
	 *
	 * @param charRule The character rule to match
	 * @param min The minimum number of occurrences
	 * @param max The maximum number of occurrences
	 * @return The created character rule
	 * @throws NullPointerException If the character rule is null
	 * @throws IllegalArgumentException If the minimum or maximum number of occurrences is lower than 0, or if the maximum is lower than the minimum, or if both are 0
	 * @see CharRepeatedRule
	 */
	public static @NonNull CharRule between(@NonNull CharRule charRule, int min, int max) {
		return new CharRepeatedRule(charRule, min, max);
	}
	
	/**
	 * Creates a character rule that matches an escape sequence consisting of a backslash followed by any single character.<br>
	 * When used in a lexer rule, the produced token is an {@link net.luis.utils.io.token.tokens.EscapedToken}.<br>
	 *
	 * @return The created character rule
	 * @see #escaped(CharRule)
	 */
	public static @NonNull CharRule escaped() {
		return escaped(any());
	}
	
	/**
	 * Creates a character rule that matches an escape sequence consisting of a backslash followed by a character matching the given rule.<br>
	 * When used in a lexer rule, the produced token is an {@link net.luis.utils.io.token.tokens.EscapedToken}.<br>
	 *
	 * @param charRule The character rule that matches the escaped character
	 * @return The created character rule
	 * @throws NullPointerException If the character rule is null
	 * @see CharSequenceRule
	 */
	public static @NonNull CharRule escaped(@NonNull CharRule charRule) {
		Objects.requireNonNull(charRule, "Character rule must not be null");
		return sequence(literal('\\'), charRule);
	}
	
	/**
	 * Creates a zero-width character rule that matches the start of the input.<br>
	 *
	 * @return The character rule
	 * @see StartCharRule
	 */
	public static @NonNull CharRule startOfInput() {
		return StartCharRule.INPUT;
	}
	
	/**
	 * Creates a zero-width character rule that matches the start of a line.<br>
	 *
	 * @return The character rule
	 * @see StartCharRule
	 */
	public static @NonNull CharRule startOfLine() {
		return StartCharRule.LINE;
	}
	
	/**
	 * Creates a zero-width character rule that matches the end of the input.<br>
	 *
	 * @return The character rule
	 * @see EndCharRule
	 */
	public static @NonNull CharRule endOfInput() {
		return EndCharRule.INPUT;
	}
	
	/**
	 * Creates a zero-width character rule that matches the end of a line.<br>
	 *
	 * @return The character rule
	 * @see EndCharRule
	 */
	public static @NonNull CharRule endOfLine() {
		return EndCharRule.LINE;
	}
}
