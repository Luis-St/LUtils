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

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.rule.TokenRuleContext;
import net.luis.utils.io.token.stream.TokenStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		if (!stream.hasMoreTokens()) {
			return null;
		}
		
		TokenRuleMatch match = this.tokenRule.match(stream.createLookbehindStream(), ctx);
		if (this.mode.shouldMatch(match != null)) {
			return TokenRuleMatch.empty(stream.getCurrentIndex(), this);
		}
		return null;
	}
	
	@Override
	public @NotNull TokenRule not() {
		return new LookbehindTokenRule(this.tokenRule, this.mode == LookMatchMode.POSITIVE ? LookMatchMode.NEGATIVE : LookMatchMode.POSITIVE);
	}
}
