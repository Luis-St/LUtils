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

package net.luis.utils.io.token.rule.rules;

import net.luis.utils.io.token.definition.*;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A utility class for creating token rules.<br>
 *
 * @author Luis-St
 */
public final class TokenRules {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private TokenRules() {}
	
	//region Factory methods
	
	/**
	 * Provides a token rule that always matches.<br>
	 *
	 * @return The token rule
	 * @apiNote This is the preferred way to access the {@link AlwaysMatchTokenRule#INSTANCE} instance
	 * @see AlwaysMatchTokenRule
	 */
	public static @NotNull TokenRule alwaysMatch() {
		return AlwaysMatchTokenRule.INSTANCE;
	}
	
	/**
	 * Creates a pattern token rule with the given pattern in string format.<br>
	 *
	 * @param pattern The pattern to match
	 * @return The created token rule
	 * @throws NullPointerException If the pattern is null
	 * @see #pattern(Pattern)
	 * @see Pattern
	 */
	public static @NotNull TokenRule pattern(@Language("RegExp") @NotNull String pattern) {
		return new PatternTokenRule(pattern);
	}
	
	/**
	 * Creates a pattern token rule with the given pattern.<br>
	 *
	 * @param pattern The pattern to match
	 * @return The created token rule
	 * @throws NullPointerException If the pattern is null
	 * @see Pattern
	 */
	public static @NotNull TokenRule pattern(@NotNull Pattern pattern) {
		return new PatternTokenRule(pattern);
	}
	
	/**
	 * Creates an optional token rule with the given token rule.<br>
	 *
	 * @param tokenRule The token rule to match optionally
	 * @return The created token rule that matches the given token rule optionally
	 * @throws NullPointerException If the token rule is null
	 * @see OptionalTokenRule
	 */
	public static @NotNull TokenRule optional(@NotNull TokenRule tokenRule) {
		return new OptionalTokenRule(tokenRule);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and the minimum number of occurrences.<br>
	 * This method is equivalent to {@link #repeatBetween(TokenRule, int, int)} with the maximum number of occurrences set to {@link Integer#MAX_VALUE}.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param min The minimum number of occurrences
	 * @return The created token rule that matches the given token rule at least the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the minimum number of occurrences is less than 0
	 * @see #repeatBetween(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule repeatAtLeast(@NotNull TokenRule tokenRule, int min) {
		return repeatBetween(tokenRule, min, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and the exact number of occurrences.<br>
	 * This method is equivalent to {@link #repeatBetween(TokenRule, int, int)} with the minimum and maximum number of occurrences set to the same value.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param repeats The exact number of occurrences
	 * @return The created token rule that matches the given token rule exactly the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the number of occurrences is lower than 0
	 * @see #repeatBetween(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule repeatExactly(@NotNull TokenRule tokenRule, int repeats) {
		return repeatBetween(tokenRule, repeats, repeats);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and the maximum number of occurrences.<br>
	 * This method is equivalent to {@link #repeatBetween(TokenRule, int, int)} with the minimum number of occurrences set to 0.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param max The maximum number of occurrences
	 * @return The created token rule that matches the given token rule at most the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the maximum number of occurrences is lower than 0
	 * @see #repeatBetween(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule repeatAtMost(@NotNull TokenRule tokenRule, int max) {
		return repeatBetween(tokenRule, 0, max);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and no limit on the number of occurrences.<br>
	 * This method is equivalent to {@link #repeatBetween(TokenRule, int, int)} with the minimum number of occurrences set to 0 and the maximum number of occurrences set to {@link Integer#MAX_VALUE}.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @return The created token rule that matches the given token rule any number of times
	 * @throws NullPointerException If the token rule is null
	 * @see #repeatBetween(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule repeatInfinitely(@NotNull TokenRule tokenRule) {
		return repeatBetween(tokenRule, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and the minimum and maximum number of occurrences.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param min The minimum number of occurrences
	 * @param max The maximum number of occurrences
	 * @return The created token rule that matches the given token rule between the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the minimum or maximum number of occurrences is lower than 0, or if the maximum number of occurrences is lower than the minimum number of occurrences, or if both are 0
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule repeatBetween(@NotNull TokenRule tokenRule, int min, int max) {
		return new RepeatedTokenRule(tokenRule, min, max);
	}
	
	/**
	 * Creates a token rule that matches a sequence of token rules.<br>
	 *
	 * @param tokenRules The token rules to match in sequence
	 * @return The created token rule
	 * @throws NullPointerException If the token rule array or any of its elements are null
	 * @throws IllegalArgumentException If the token rule array is empty
	 * @see SequenceTokenRule
	 */
	public static @NotNull TokenRule sequence(TokenRule @NotNull ... tokenRules) {
		return sequence(List.of(Objects.requireNonNull(tokenRules, "Token rule array must not be null")));
	}
	
	/**
	 * Creates a token rule that matches a sequence of token rules.<br>
	 *
	 * @param tokenRules The token rules to match in sequence
	 * @return The created token rule
	 * @throws NullPointerException If the token rule list or any of its elements are null
	 * @throws IllegalArgumentException If the token rule list is empty
	 * @see SequenceTokenRule
	 */
	public static @NotNull TokenRule sequence(@NotNull List<TokenRule> tokenRules) {
		return new SequenceTokenRule(tokenRules);
	}
	
	/**
	 * Creates a token rule that matches any of the given token rules.<br>
	 *
	 * @param tokenRules The token rules to match any of
	 * @return The created token rule
	 * @throws NullPointerException If the token rule array or any of its elements are null
	 * @throws IllegalArgumentException If the token rule array is empty
	 * @see AnyOfTokenRule
	 */
	public static @NotNull TokenRule any(TokenRule @NotNull ... tokenRules) {
		return any(Set.of(Objects.requireNonNull(tokenRules, "Token rule array must not be null")));
	}
	
	/**
	 * Creates a token rule that matches any of the given token rules.<br>
	 *
	 * @param tokenRules The token rules to match any of
	 * @return The created token rule
	 * @throws NullPointerException If the token rule set or any of its elements are null
	 * @throws IllegalArgumentException If the token rule set is empty
	 * @see AnyOfTokenRule
	 */
	public static @NotNull TokenRule any(@NotNull Set<TokenRule> tokenRules) {
		return new AnyOfTokenRule(tokenRules);
	}
	
	/**
	 * Creates a token rule that matches a boundary between the given start and end token rules.<br>
	 *
	 * @param startTokenRule The token rule that marks the start of the sequence
	 * @param endTokenRule The token rule that marks the end of the sequence
	 * @return The created token rule
	 * @throws NullPointerException If the start or end token rule is null
	 * @throws IllegalArgumentException If the start token rule is invalid
	 * @see BoundaryTokenRule
	 * @see BoundaryTokenRule#INVALID_RULES
	 */
	public static @NotNull TokenRule boundary(@NotNull TokenRule startTokenRule, @NotNull TokenRule endTokenRule) {
		return new BoundaryTokenRule(startTokenRule, endTokenRule);
	}
	
	/**
	 * Creates a token rule that matches a boundary between the given start, between, and end token rules.<br>
	 *
	 * @param startTokenRule The token rule that marks the start of the sequence
	 * @param betweenTokenRule The token rule that matches the content between the start and end token rules
	 * @param endTokenRule The token rule that marks the end of the sequence
	 * @return The created token rule
	 * @throws NullPointerException If the start, between, or end token rule is null
	 * @throws IllegalArgumentException If the start or between token rule is invalid
	 * @see BoundaryTokenRule
	 * @see BoundaryTokenRule#INVALID_RULES
	 */
	public static @NotNull TokenRule boundary(@NotNull TokenRule startTokenRule, @NotNull TokenRule betweenTokenRule, @NotNull TokenRule endTokenRule) {
		return new BoundaryTokenRule(startTokenRule, betweenTokenRule, endTokenRule);
	}
	
	/**
	 * Provides a token rule that matches the end of the input.<br>
	 *
	 * @return The token rule
	 * @apiNote This is the preferred way to access the {@link EndTokenRule#INSTANCE} instance
	 * @see EndTokenRule
	 */
	public static @NotNull TokenRule end() {
		return EndTokenRule.INSTANCE;
	}
	//endregion
	
	/**
	 * Converts the given token rule to a regex string.<br>
	 * This method will remove the surrounding brackets if they are present.<br>
	 *
	 * @param tokenRule The token rule to convert
	 * @return The regex string
	 * @throws NullPointerException If the token rule is null
	 * @see #toBaseRegex(TokenRule)
	 */
	public static @NotNull String toRegex(@NotNull TokenRule tokenRule) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		String regex = toBaseRegex(tokenRule);
		if (isSurroundedByBrackets(regex)) {
			return regex.substring(1, regex.length() - 1);
		}
		return regex;
	}
	
	/**
	 * Converts the given token rule to a regex string without removing the surrounding brackets.<br>
	 * The rules are converted to regex strings and joined together with the appropriate regex operators.<br>
	 *
	 * @param tokenRule The token rule to convert
	 * @return The regex string
	 * @throws NullPointerException If the token rule is null
	 * @see #toBaseRegex(TokenDefinition)
	 */
	private static @NotNull String toBaseRegex(@NotNull TokenRule tokenRule) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		switch (tokenRule) {
			case AlwaysMatchTokenRule ignored -> {
				return ".*?";
			}
			case EndTokenRule ignored -> {
				return "$";
			}
			case PatternTokenRule(Pattern pattern) -> {
				String regex = pattern.pattern();
				if (isSurroundedByBrackets(regex)) {
					return regex.substring(1, regex.length() - 1);
				}
				return regex;
			}
			case OptionalTokenRule(TokenRule rule) -> {
				String regex = toBaseRegex(rule);
				if (endsWithSpecialChar(regex)) {
					return "(" + regex + ")?";
				}
				return regex + "?";
			}
			case RepeatedTokenRule(TokenRule rule, int minOccurrences, int maxOccurrences) -> {
				String regex = toBaseRegex(rule);
				if (endsWithSpecialChar(regex)) {
					regex = "(" + regex + ")";
				}
				if (maxOccurrences == Integer.MAX_VALUE) {
					if (minOccurrences == 0) {
						return regex + "*";
					} else if (minOccurrences == 1) {
						return regex + "+";
					}
					return regex + "{" + minOccurrences + ",}";
				} else if (minOccurrences == 0 && maxOccurrences == 1) {
					return regex + "?";
				} else if (minOccurrences == maxOccurrences) {
					if (minOccurrences == 1) {
						return regex;
					} else {
						return regex + "{" + minOccurrences + "}";
					}
				} else {
					return regex + "{" + minOccurrences + "," + maxOccurrences + "}";
				}
			}
			case AnyOfTokenRule(Set<TokenRule> tokenRules) -> {
				if (tokenRules.stream().allMatch(CharTokenDefinition.class::isInstance)) {
					return tokenRules.stream().map(CharTokenDefinition.class::cast).map(tr -> {
						char token = tr.token();
						return switch (token) {
							case '[' -> "\\[";
							case ']' -> "\\]";
							case '\\' -> "\\\\";
							default -> String.valueOf(token);
						};
					}).collect(Collectors.joining("", "[", "]"));
				}
				return tokenRules.stream().map(TokenRules::toRegex).collect(Collectors.joining("|", "(", ")"));
			}
			case SequenceTokenRule(List<TokenRule> tokenRules) -> {
				return tokenRules.stream().map(TokenRules::toBaseRegex).collect(Collectors.joining("", "(", ")"));
			}
			case BoundaryTokenRule(TokenRule startTokenRule, TokenRule betweenTokenRule, TokenRule endTokenRule) -> {
				String startRegex = toBaseRegex(startTokenRule);
				String betweenRegex = toBaseRegex(betweenTokenRule);
				String endRegex = toBaseRegex(endTokenRule);
				if (endsWithSpecialChar(startRegex)) {
					startRegex = "(" + startRegex + ")";
				}
				if (!isSurroundedByBrackets(betweenRegex)) {
					betweenRegex = "(" + betweenRegex + ")";
				}
				if (endsWithSpecialChar(endRegex)) {
					endRegex = "(" + endRegex + ")";
				}
				return "(" + startRegex + betweenRegex + endRegex + ")";
			}
			default -> {}
		}
		if (tokenRule instanceof TokenDefinition definition) {
			return toBaseRegex(definition);
		}
		throw new IllegalStateException("Unexpected token rule: " + tokenRule);
	}
	
	/**
	 * Converts the given token definition to a regex string.<br>
	 * A word token definition is converted to a regex that matches any alphanumeric and numeric character.<br>
	 * All special characters are escaped.<br>
	 *
	 * @param definition The token definition to convert
	 * @return The regex string
	 * @throws NullPointerException If the token definition is null
	 */
	private static @NotNull String toBaseRegex(@NotNull TokenDefinition definition) {
		Objects.requireNonNull(definition, "Token definition must not be null");
		if (definition instanceof WordTokenDefinition) {
			return "[a-zA-Z0-9]+";
		}
		
		String regex = switch (definition) {
			case CharTokenDefinition def -> String.valueOf(def.token());
			case StringTokenDefinition def -> def.token();
			case EscapedTokenDefinition def -> "\\" + def.token();
			default -> throw new IllegalStateException("Unexpected token definition: " + definition);
		};
		return regex.replace("\\", "\\\\")
			.replace("$", "\\$").replace("(", "\\(").replace(")", "\\)")
			.replace("{", "\\{").replace("}", "\\}").replace("[", "\\[")
			.replace("]", "\\]").replace("^", "\\^").replace("|", "\\|")
			.replace(".", "\\.").replace("+", "\\+").replace("*", "\\*");
	}
	
	/**
	 * Checks if the given regex is surrounded by brackets.<br>
	 * This method will check if the first character is a bracket and if the last character is a closing bracket.<br>
	 *
	 * @param regex The regex to check
	 * @return True if the regex is surrounded by brackets, false otherwise
	 * @throws NullPointerException If the regex is null
	 */
	private static boolean isSurroundedByBrackets(@NotNull String regex) {
		Objects.requireNonNull(regex, "Regex must not be null");
		return regex.charAt(0) == '(' && isLastChar(regex, ')');
	}
	
	/**
	 * Checks if the given regex ends with a special character.<br>
	 * The following characters are considered special characters: {@code ?, *, +, }<br>
	 *
	 * @param regex The regex to check
	 * @return True if the regex ends with a special character, false otherwise
	 * @throws NullPointerException If the regex is null
	 */
	private static boolean endsWithSpecialChar(@NotNull String regex) {
		Objects.requireNonNull(regex, "Regex must not be null");
		return isLastChar(regex, '?') || isLastChar(regex, '*') || isLastChar(regex, '+') || isLastChar(regex, '}');
	}
	
	/**
	 * Checks if the given regex ends with the given character.<br>
	 *
	 * @param regex The regex to check
	 * @param c The character to check
	 * @return True if the regex ends with the given character, false otherwise
	 * @throws NullPointerException If the regex is null
	 */
	private static boolean isLastChar(@NotNull String regex, char c) {
		Objects.requireNonNull(regex, "Regex must not be null");
		return regex.charAt(regex.length() - 1) == c;
	}
}
