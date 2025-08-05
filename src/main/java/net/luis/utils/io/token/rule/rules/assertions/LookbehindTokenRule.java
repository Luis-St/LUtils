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

package net.luis.utils.io.token.rule.rules.assertions;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
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
 * @see LookMatchMode
 *
 * @author Luis-St
 *
 * @param tokenRule The token rule to match behind
 * @param mode The mode of lookahead matching, either positive or negative
 */
public record LookbehindTokenRule(
	@NotNull TokenRule tokenRule,
	@NotNull LookMatchMode mode
) implements TokenRule {
	
	/**
	 * Constructs a new lookbehind token rule with the given token rule and positive flag.<br>
	 *
	 * @param tokenRule The token rule to match behind
	 * @param mode The mode of lookbehind matching, either positive or negative
	 * @throws NullPointerException If the token rule is null
	 */
	public LookbehindTokenRule {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		Objects.requireNonNull(mode, "Look match mode must not be null");
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size() || startIndex < 0) {
			return null;
		}
		
		if (startIndex == 0) {
			return this.mode.shouldMatch(true) ? null : TokenRuleMatch.empty(startIndex, this);
		}
		
		List<Token> reversedTokens = Lists.reverse(tokens);
		int reversedLookbehindIndex = tokens.size() - startIndex;
		if (reversedLookbehindIndex >= reversedTokens.size()) {
			return this.mode.shouldMatch(true) ? null : TokenRuleMatch.empty(startIndex, this);
		}
		
		TokenRuleMatch match = this.tokenRule.match(reversedTokens, reversedLookbehindIndex);
		if (this.mode.shouldMatch(match != null)) {
			return TokenRuleMatch.empty(startIndex, this);
		}
		return null;
	}
}
