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

import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-St
 *
 */

public class TokenRules {
	
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
}
