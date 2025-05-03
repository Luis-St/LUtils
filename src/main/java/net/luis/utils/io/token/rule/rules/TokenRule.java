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

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

/**
 *
 * @author Luis-St
 *
 */

@FunctionalInterface
public interface TokenRule {
	
	//region Factory methods
	static @NotNull TokenRule alwaysMatch() {
		return AlwaysMatchTokenRule.INSTANCE;
	}
	
	static @NotNull TokenRule single(@NotNull TokenDefinition definition) {
		return new SingleTokenRule(Objects.requireNonNull(definition, "Definition must not be null"));
	}
	
	static @NotNull TokenRule pattern(@Language("RegExp") @NotNull String pattern) {
		return new PatternTokenRule(Objects.requireNonNull(pattern, "Regex must not be null"));
	}
	
	static @NotNull TokenRule pattern(@NotNull Pattern pattern) {
		return new PatternTokenRule(Objects.requireNonNull(pattern, "Pattern must not be null"));
	}
	
	static @NotNull TokenRule atLeast(@NotNull TokenRule tokenRule, int min) {
		return between(Objects.requireNonNull(tokenRule, "Token rule must not be null"), min, Integer.MAX_VALUE);
	}
	
	static @NotNull TokenRule exactly(@NotNull TokenRule tokenRule, int occurrences) {
		return between(Objects.requireNonNull(tokenRule, "Token rule must not be null"), occurrences, occurrences);
	}
	
	static @NotNull TokenRule atMost(@NotNull TokenRule tokenRule, int max) {
		return between(Objects.requireNonNull(tokenRule, "Token rule must not be null"), 0, max);
	}
	
	static @NotNull TokenRule between(@NotNull TokenRule tokenRule, int min, int max) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		if (min < 0) {
			throw new IllegalArgumentException("Min occurrences must not be negative");
		}
		if (max < min) {
			throw new IllegalArgumentException("Max occurrences must not be less than min occurrences");
		}
		return new RepeatedTokenRule(tokenRule, min, max);
	}
	
	static @NotNull TokenRule sequence(TokenRule @NotNull ... tokenRules) {
		return sequence(List.of(Objects.requireNonNull(tokenRules, "Token rule array must not be null")));
	}
	
	static @NotNull TokenRule sequence(@NotNull List<TokenRule> tokenRules) {
		return new SequenceTokenRule(Objects.requireNonNull(tokenRules, "Token rule list must not be null"));
	}
	
	static @NotNull TokenRule any(TokenRule @NotNull ... tokenRules) {
		return any(Set.of(Objects.requireNonNull(tokenRules, "Token rule array must not be null")));
	}
	
	static @NotNull TokenRule any(@NotNull Set<TokenRule> tokenRules) {
		return new AnyOfTokenRule(Objects.requireNonNull(tokenRules, "Token rule list must not be null"));
	}
	
	static @NotNull TokenRule boundary(@NotNull TokenRule startTokenRule, @NotNull TokenRule endTokenRule) {
		Objects.requireNonNull(startTokenRule, "Start rule must not be null");
		Objects.requireNonNull(endTokenRule, "End rule must not be null");
		return new BoundaryTokenRule(startTokenRule, endTokenRule);
	}
	
	static @NotNull TokenRule boundary(@NotNull TokenRule startTokenRule, @NotNull TokenRule betweenTokenRule, @NotNull TokenRule endTokenRule) {
		Objects.requireNonNull(startTokenRule, "Start rule must not be null");
		Objects.requireNonNull(betweenTokenRule, "Between token rule must not be null");
		Objects.requireNonNull(endTokenRule, "End rule must not be null");
		return new BoundaryTokenRule(startTokenRule, betweenTokenRule, endTokenRule);
	}
	
	static @NotNull TokenRule end() {
		return EndTokenRule.INSTANCE;
	}
	//endregion
	
	@Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex);
	
	//region Rule creation
	default @NotNull TokenRule atLeast(int min) {
		return atLeast(this, min);
	}
	
	default @NotNull TokenRule exactly(int occurrences) {
		return exactly(this, occurrences);
	}
	
	default @NotNull TokenRule atMost(int max) {
		return atMost(this, max);
	}
	
	default @NotNull TokenRule between(int min, int max) {
		return TokenRule.between(this, min, max);
	}
	
	default @NotNull TokenRule followedBy(@NotNull TokenRule tokenRule) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		return TokenRule.sequence(this, tokenRule);
	}
	
	default @NotNull TokenRule followedBy(TokenRule @NotNull ... tokenRules) {
		Objects.requireNonNull(tokenRules, "Token rule array must not be null");
		List<TokenRule> rules = Lists.newArrayList(tokenRules);
		rules.addFirst(this);
		return TokenRule.sequence(rules);
	}
	
	default @NotNull TokenRule or(@NotNull TokenRule tokenRule) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		return TokenRule.any(this, tokenRule);
	}
	
	default @NotNull TokenRule or(TokenRule @NotNull ... tokenRules) {
		Objects.requireNonNull(tokenRules, "Token rule array must not be null");
		Set<TokenRule> rules = Sets.newHashSet(tokenRules);
		rules.add(this);
		return TokenRule.any(rules);
	}
	
	default @NotNull TokenRule asStart(@NotNull TokenRule endTokenRule) {
		Objects.requireNonNull(endTokenRule, "End token rule must not be null");
		return TokenRule.boundary(this, endTokenRule);
	}
	
	default @NotNull TokenRule asStart(@NotNull TokenRule betweenTokeRule, @NotNull TokenRule endTokenRule) {
		Objects.requireNonNull(betweenTokeRule, "Between token rule must not be null");
		Objects.requireNonNull(endTokenRule, "End token rule must not be null");
		return TokenRule.boundary(this, betweenTokeRule, endTokenRule);
	}
	
	default @NotNull TokenRule asEnd(@NotNull TokenRule startTokenRule) {
		Objects.requireNonNull(startTokenRule, "Start token rule must not be null");
		return TokenRule.boundary(startTokenRule, this);
	}
	
	default @NotNull TokenRule asEnd(@NotNull TokenRule startTokenRule, @NotNull TokenRule betweenTokeRule) {
		Objects.requireNonNull(startTokenRule, "Start token rule must not be null");
		Objects.requireNonNull(betweenTokeRule, "Between token rule must not be null");
		return TokenRule.boundary(startTokenRule, betweenTokeRule, this);
	}
	//endregion
}
