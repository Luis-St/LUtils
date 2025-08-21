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

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * A custom token rule that matches a token based on a user-defined condition.<br>
 * This rule is useful for scenarios where the matching logic cannot be expressed with predefined rules.<br>
 * The condition is a predicate that takes a {@link Token} and returns a boolean indicating whether the token matches the rule.<br>
 *
 * @author Luis-St
 *
 * @param condition The condition to match the token against
 */
public record CustomTokeRule(
	@NotNull Predicate<Token> condition
) implements TokenRule {
	
	/**
	 * Creates a new custom token rule with the specified condition.<br>
	 *
	 * @param condition The condition to match the token against
	 */
	public CustomTokeRule {
		Objects.requireNonNull(condition, "Condition must not be null");
	}
	
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		if (!stream.hasToken()) {
			return null;
		}
		
		int startIndex = stream.getCurrentIndex();
		Token currentToken = stream.getCurrentToken();
		if (this.condition.test(currentToken)) {
			return new TokenRuleMatch(startIndex, stream.consumeToken(), Collections.singletonList(currentToken), this);
		}
		return null;
	}
}
