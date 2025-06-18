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
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A token rule that matches a sequence of tokens with a start, between, and end token rule.<br>
 * This rule is useful for matching patterns that have a specific structure, such as a start and end delimiter with optional content in between.<br>
 * The start and end token rules are required, while the between token rule can match zero or infinite tokens.<br>
 * The start and between token rule must not be or contain an {@link EndTokenRule} or any other token rule class that is added to {@link #INVALID_RULES}.<br>
 *
 * @author Luis-St
 *
 * @param startTokenRule The token rule that marks the start of the sequence
 * @param betweenTokenRule The token rule that matches the content between the start and end token rules
 * @param endTokenRule The token rule that marks the end of the sequence
 */
public record BoundaryTokenRule(
	@NotNull TokenRule startTokenRule,
	@NotNull TokenRule betweenTokenRule,
	@NotNull TokenRule endTokenRule
) implements TokenRule {
	
	/**
	 * A set of token rules that are not allowed to be used as start or between token rule.<br>
	 * This includes {@link EndTokenRule} and any other token rule class added to this set.<br>
	 * @apiNote The rules are checked recursively, so if a rule contains another rule in this set, it will also be invalid.<br>
	 */
	public static final Set<Class<? extends TokenRule>> INVALID_RULES = Set.of(
		EndTokenRule.class
	);
	
	/**
	 * Constructs a new boundary token rule with the given start, and end token rule.<br>
	 * The between token rule is set to {@link TokenRules#alwaysMatch()} by default.<br>
	 *
	 * @param startTokenRule The token rule that marks the start of the sequence
	 * @param endTokenRule The token rule that marks the end of the sequence
	 * @throws NullPointerException If the start or end token rule is null
	 * @throws IllegalArgumentException If the start token rule is invalid
	 */
	public BoundaryTokenRule(@NotNull TokenRule startTokenRule, @NotNull TokenRule endTokenRule) {
		this(Objects.requireNonNull(startTokenRule, "Start rule must not be null"), TokenRules.alwaysMatch(), Objects.requireNonNull(endTokenRule, "End rule must not be null"));
	}
	
	/**
	 * Constructs a new boundary token rule with the given start, between, and end token rule.<br>
	 *
	 * @param startTokenRule The token rule that marks the start of the sequence
	 * @param betweenTokenRule The token rule that matches the content between the start and end token rules
	 * @param endTokenRule The token rule that marks the end of the sequence
	 * @throws NullPointerException If the start, between, or end token rule is null
	 * @throws IllegalArgumentException If the start or between token rule is invalid
	 * @see #INVALID_RULES
	 */
	public BoundaryTokenRule {
		Objects.requireNonNull(startTokenRule, "Start token rule must not be null");
		Objects.requireNonNull(betweenTokenRule, "Between token rule must not be null");
		Objects.requireNonNull(endTokenRule, "End token rule must not be null");
		
		validateTokenRules("Start token rule", List.of(startTokenRule));
		validateTokenRules("Between token rule", List.of(betweenTokenRule));
	}
	
	/**
	 * Validates the given token rules to ensure they do not contain any invalid between token rules.<br>
	 * This method is called recursively to check all nested token rules.<br>
	 *
	 * @param type The type of the token rule to validate (e.g. "Start", "Between", "End")
	 * @param tokenRules The collection of token rules to validate
	 * @throws IllegalArgumentException If any of the token rules are invalid
	 */
	private static void validateTokenRules(@NotNull String type, @NotNull Collection<TokenRule> tokenRules) {
		for (TokenRule tokenRule : tokenRules) {
			if (INVALID_RULES.contains(tokenRule.getClass())) {
				throw new IllegalArgumentException(type + " must not contain a token rule of type " + tokenRule.getClass().getSimpleName());
			}
			
			Collection<TokenRule> nestedRules = switch (tokenRule) {
				case AnyOfTokenRule(var rules) -> rules;
				case BoundaryTokenRule(var start, var between, var end) -> Set.of(start, between, end);
				case OptionalTokenRule(var nested) -> Set.of(nested);
				case RepeatedTokenRule(var nested, var min, var max) -> Set.of(nested);
				case SequenceTokenRule(var rules) -> rules;
				default -> Collections.emptySet();
			};
			
			if (!nestedRules.isEmpty()) {
				validateTokenRules(type, nestedRules);
			}
		}
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		TokenRuleMatch startMatch = this.startTokenRule.match(tokens, startIndex);
		if (startMatch == null) {
			return null;
		}
		
		int currentIndex = startMatch.endIndex();
		List<Token> matchedTokens = Lists.newArrayList(startMatch.matchedTokens());
		while (currentIndex < tokens.size()) {
			TokenRuleMatch endMatch = this.endTokenRule.match(tokens, currentIndex);
			if (endMatch != null) {
				matchedTokens.addAll(endMatch.matchedTokens());
				return new TokenRuleMatch(startIndex, endMatch.endIndex(), matchedTokens, this);
			}
			
			TokenRuleMatch betweenMatch = this.betweenTokenRule.match(tokens, currentIndex);
			if (betweenMatch == null) {
				return null;
			}
			if (currentIndex - betweenMatch.endIndex() > 1) {
				throw new IllegalStateException("Between token rule must not match more than one token");
			}
			if (currentIndex < tokens.size()) {
				matchedTokens.addAll(betweenMatch.matchedTokens());
				currentIndex = betweenMatch.endIndex();
			}
		}
		
		return null;
	}
}
