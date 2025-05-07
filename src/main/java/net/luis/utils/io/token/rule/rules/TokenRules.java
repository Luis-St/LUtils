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
 *
 * @author Luis-St
 *
 */

public final class TokenRules {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private TokenRules() {}
	
	//region Factory methods
	public static @NotNull TokenRule alwaysMatch() {
		return AlwaysMatchTokenRule.INSTANCE;
	}
	
	public static @NotNull TokenRule pattern(@Language("RegExp") @NotNull String pattern) {
		return new PatternTokenRule(Objects.requireNonNull(pattern, "Regex must not be null"));
	}
	
	public static @NotNull TokenRule pattern(@NotNull Pattern pattern) {
		return new PatternTokenRule(Objects.requireNonNull(pattern, "Pattern must not be null"));
	}
	
	public static @NotNull TokenRule optional(@NotNull TokenRule tokenRule) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		return new OptionalTokenRule(tokenRule);
	}
	
	public static @NotNull TokenRule repeatAtLeast(@NotNull TokenRule tokenRule, int min) {
		return repeatBetween(Objects.requireNonNull(tokenRule, "Token rule must not be null"), min, Integer.MAX_VALUE);
	}
	
	public static @NotNull TokenRule repeatExactly(@NotNull TokenRule tokenRule, int occurrences) {
		return repeatBetween(Objects.requireNonNull(tokenRule, "Token rule must not be null"), occurrences, occurrences);
	}
	
	public static @NotNull TokenRule repeatAtMost(@NotNull TokenRule tokenRule, int max) {
		return repeatBetween(Objects.requireNonNull(tokenRule, "Token rule must not be null"), 0, max);
	}
	
	public static @NotNull TokenRule repeatInfinitely(@NotNull TokenRule tokenRule) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		return repeatBetween(tokenRule, 0, Integer.MAX_VALUE);
	}
	
	public static @NotNull TokenRule repeatBetween(@NotNull TokenRule tokenRule, int min, int max) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		if (min < 0) {
			throw new IllegalArgumentException("Min occurrences must not be negative");
		}
		if (max < min) {
			throw new IllegalArgumentException("Max occurrences must not be less than min occurrences");
		}
		return new RepeatedTokenRule(tokenRule, min, max);
	}
	
	public static @NotNull TokenRule sequence(TokenRule @NotNull ... tokenRules) {
		return sequence(List.of(Objects.requireNonNull(tokenRules, "Token rule array must not be null")));
	}
	
	public static @NotNull TokenRule sequence(@NotNull List<TokenRule> tokenRules) {
		return new SequenceTokenRule(Objects.requireNonNull(tokenRules, "Token rule list must not be null"));
	}
	
	public static @NotNull TokenRule any(TokenRule @NotNull ... tokenRules) {
		return any(Set.of(Objects.requireNonNull(tokenRules, "Token rule array must not be null")));
	}
	
	public static @NotNull TokenRule any(@NotNull Set<TokenRule> tokenRules) {
		return new AnyOfTokenRule(Objects.requireNonNull(tokenRules, "Token rule list must not be null"));
	}
	
	public static @NotNull TokenRule boundary(@NotNull TokenRule startTokenRule, @NotNull TokenRule endTokenRule) {
		Objects.requireNonNull(startTokenRule, "Start rule must not be null");
		Objects.requireNonNull(endTokenRule, "End rule must not be null");
		return new BoundaryTokenRule(startTokenRule, endTokenRule);
	}
	
	public static @NotNull TokenRule boundary(@NotNull TokenRule startTokenRule, @NotNull TokenRule betweenTokenRule, @NotNull TokenRule endTokenRule) {
		Objects.requireNonNull(startTokenRule, "Start rule must not be null");
		Objects.requireNonNull(betweenTokenRule, "Between token rule must not be null");
		Objects.requireNonNull(endTokenRule, "End rule must not be null");
		return new BoundaryTokenRule(startTokenRule, betweenTokenRule, endTokenRule);
	}
	
	public static @NotNull TokenRule end() {
		return EndTokenRule.INSTANCE;
	}
	//endregion
	
	public static @NotNull String toRegex(@NotNull TokenRule tokenRule) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		String regex = toBaseRegex(tokenRule);
		if (isSurroundedByBrackets(regex)) {
			return regex.substring(1, regex.length() - 1);
		}
		return regex;
	}
	
	private static @NotNull String toBaseRegex(@NotNull TokenRule tokenRule) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		switch (tokenRule) {
			case AlwaysMatchTokenRule ignored -> {
				return ".";
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
	
	private static @NotNull String toBaseRegex(@NotNull TokenDefinition definition) {
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
	
	private static boolean isSurroundedByBrackets(@NotNull String regex) {
		Objects.requireNonNull(regex, "Regex must not be null");
		return regex.charAt(0) == '(' && isLastChar(regex, ')');
	}
	
	private static boolean endsWithSpecialChar(@NotNull String regex) {
		Objects.requireNonNull(regex, "Regex must not be null");
		return isLastChar(regex, '?') || isLastChar(regex, '*') || isLastChar(regex, '+') || isLastChar(regex, '}');
	}
	
	private static boolean isLastChar(@NotNull String regex, char c) {
		Objects.requireNonNull(regex, "Regex must not be null");
		return regex.charAt(regex.length() - 1) == c;
	}
}
