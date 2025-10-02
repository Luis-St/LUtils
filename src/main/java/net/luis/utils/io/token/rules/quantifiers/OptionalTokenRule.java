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

package net.luis.utils.io.token.rules.quantifiers;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		
		int startIndex = stream.getCurrentIndex();
		TokenRuleMatch match = this.tokenRule.match(stream, ctx);
		if (match != null) {
			return match;
		}
		
		if (startIndex >= stream.size() || startIndex < 0) { // Check after nested rule processed to allow the rule to match at these edge cases
			return null;
		}
		return TokenRuleMatch.empty(startIndex, this);
	}
	
	@Override
	public @NotNull TokenRule not() {
		return new OptionalTokenRule(this.tokenRule.not());
	}
}
