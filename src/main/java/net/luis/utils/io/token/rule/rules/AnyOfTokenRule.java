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

import java.util.*;

/**
 * A token rule that matches any of the provided token rules.<br>
 * The first matching rule will be used to create the {@link TokenRuleMatch}.<br>
 * This rule is useful for creating complex matching logic by combining multiple rules.<br>
 *
 * @author Luis-St
 *
 * @param tokenRules The set of token rules to match against
 */
public record AnyOfTokenRule(
	@NotNull Set<TokenRule> tokenRules
) implements TokenRule {
	
	/**
	 * Constructs a new any of token rule with the given token rules.<br>
	 * @param tokenRules The set of token rules to match against
	 * @throws NullPointerException If the token rule list is null
	 */
	public AnyOfTokenRule {
		Objects.requireNonNull(tokenRules, "Token rule list must not be null");
		if (tokenRules.isEmpty()) {
			throw new IllegalArgumentException("Token rule list must not be empty");
		}
		tokenRules = Collections.unmodifiableSequencedSet(new LinkedHashSet<>(tokenRules));
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		for (TokenRule tokenRule : this.tokenRules) {
			TokenRuleMatch match = tokenRule.match(tokens, startIndex);
			if (match != null) {
				return match;
			}
		}
		return null;
	}
}
