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

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * A token rule that matches optionally a single token rule.<br>
 * This rule is useful for creating optional matching logic.<br>
 * It will match the token rule if it is present, otherwise it will return an empty match.<br>
 *
 * @author Luis-St
 *
 * @param tokenRule The token rule to match optionally
 */
public record OptionalTokenRule(
	@NotNull TokenRule tokenRule
) implements TokenRule {
	
	/**
	 * Constructs a new optional token rule with the given token rule.<br>
	 *
	 * @param tokenRule The token rule to match optionally
	 * @throws NullPointerException If the token rule is null
	 */
	public OptionalTokenRule {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size() || startIndex < 0) {
			return null;
		}
		
		TokenRuleMatch match = this.tokenRule.match(tokens, startIndex);
		if (match != null) {
			return match;
		}
		return TokenRuleMatch.empty(startIndex);
	}
}
