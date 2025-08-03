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

import java.util.List;
import java.util.Objects;

/**
 * A token rule that performs lookbehind matching by reversing the token list and applying the inner rule.<br>
 * This rule reverses the token list and checks if the inner rule would match at the corresponding position.<br>
 * This approach provides a more consistent and powerful lookbehind implementation.<br>
 *
 * @author Luis-St
 *
 * @param tokenRule The token rule to match behind
 * @param positive If true, match when inner rule matches; if false, match when inner rule doesn't match
 */
public record LookbehindTokenRule(
	@NotNull TokenRule tokenRule,
	boolean positive
) implements TokenRule {
	
	/**
	 * Constructs a new lookbehind token rule with the given token rule and positive flag.<br>
	 *
	 * @param tokenRule The token rule to match behind
	 * @param positive If true, match when inner rule matches; if false, match when inner rule doesn't match
	 * @throws NullPointerException If the token rule is null
	 */
	public LookbehindTokenRule {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size() || startIndex < 0) {
			return null;
		}
		
		// Check if we can look behind (at start, nothing to look behind to)
		if (startIndex == 0) {
			// At start, can only match negative lookbehind
			return this.positive ? null : TokenRuleMatch.empty(startIndex);
		}
		
		// Create reversed token list
		List<Token> reversedTokens = Lists.reverse(tokens);
		
		// Calculate the position in the reversed list where we want to apply the rule
		// If startIndex points to position X, we want to check position X-1 in the original list
		// In the reversed list, original position X-1 becomes: tokens.size() - 1 - (X-1) = tokens.size() - X
		int reversedLookbehindIndex = tokens.size() - startIndex;
		
		// Check bounds for the reversed index
		if (reversedLookbehindIndex >= reversedTokens.size()) {
			// This shouldn't happen given our earlier checks, but safety first
			return this.positive ? null : TokenRuleMatch.empty(startIndex);
		}
		
		// Try to match the rule at the calculated position in the reversed list
		TokenRuleMatch match = this.tokenRule.match(reversedTokens, reversedLookbehindIndex);
		boolean ruleMatches = match != null;
		
		if ((this.positive && ruleMatches) || (!this.positive && !ruleMatches)) {
			// Lookbehind matches but doesn't consume tokens
			return TokenRuleMatch.empty(startIndex);
		}
		return null;
	}
}
