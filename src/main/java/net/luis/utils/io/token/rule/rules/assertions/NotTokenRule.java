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

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A token rule that matches when the inner token rule does NOT match.<br>
 * This rule is useful for negation logic in token matching.<br>
 * It will return an empty match if the inner rule doesn't match, otherwise null.<br>
 *
 * @author Luis-St
 *
 * @param tokenRule The token rule to negate
 */
public record NotTokenRule(
	@NotNull TokenRule tokenRule
) implements TokenRule {
	
	/**
	 * Constructs a new not token rule with the given token rule.<br>
	 *
	 * @param tokenRule The token rule to negate
	 * @throws NullPointerException If the token rule is null
	 */
	public NotTokenRule {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		if (!stream.hasToken()) {
			return null;
		}
		
		TokenRuleMatch match = this.tokenRule.match(stream.copyWithCurrentIndex());
		if (match == null) {
			return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
		}
		return null;
	}
}
