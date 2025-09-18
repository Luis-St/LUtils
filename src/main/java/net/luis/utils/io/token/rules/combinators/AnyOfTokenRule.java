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

package net.luis.utils.io.token.rules.combinators;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.stream.TokenStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

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
	@NotNull List<TokenRule> tokenRules
) implements TokenRule {
	
	/**
	 * Constructs a new any of token rule with the given token rules.<br>
	 *
	 * @param tokenRules The list of token rules to match against
	 * @throws NullPointerException If the token rule list is null
	 */
	public AnyOfTokenRule {
		Objects.requireNonNull(tokenRules, "Token rule list must not be null");
		if (tokenRules.isEmpty()) {
			throw new IllegalArgumentException("Token rule list must not be empty");
		}
		for (TokenRule tokenRule : tokenRules) {
			Objects.requireNonNull(tokenRule, "Token rule list must not contain a null element");
		}
		tokenRules = List.copyOf(tokenRules);
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		
		for (TokenRule tokenRule : this.tokenRules) {
			TokenStream workingStream = stream.copyWithOffset(0);
			TokenRuleMatch match = tokenRule.match(workingStream, ctx);
			
			if (match != null) {
				stream.advanceTo(workingStream);
				return match;
			}
		}
		return null;
	}
	
	@Override
	public @NotNull TokenRule not() {
		return new SequenceTokenRule(this.tokenRules.stream().map(TokenRule::not).toList()); // Negation using De Morgan's laws
	}
}
