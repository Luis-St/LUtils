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
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.TokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.Token;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * A token rule that matches all the provided token rules.<br>
 * This rule is useful for creating complex matching logic by combining multiple rules.<br>
 * <p>
 *     The order of the token rules matters, as all rules must match in sequence for this rule to match.<br>
 *     The rule only supports token rules that match a single token.
 * </p>
 *
 * @author Luis-St
 *
 * @param tokenRules The list of token rules to match against
 */
public record AllOfTokenRule(
	@NonNull List<TokenRule> tokenRules
) implements TokenRule {
	
	/**
	 * Constructs a new all of token rule with the given token rules.<br>
	 *
	 * @param tokenRules The list of token rules to match against
	 * @throws NullPointerException If the token rule list is null or contains a null element
	 * @throws IllegalArgumentException If the token rule list is empty or contains less than two rules
	 */
	public AllOfTokenRule {
		Objects.requireNonNull(tokenRules, "Token rules must not be null");
		
		if (tokenRules.isEmpty()) {
			throw new IllegalArgumentException("Token rule list must not be empty");
		}
		if (tokenRules.size() == 1) {
			throw new IllegalArgumentException("At least two token rules are required");
		}
		
		for (TokenRule tokenRule : tokenRules) {
			Objects.requireNonNull(tokenRule, "Token rule list must not contain a null element");
		}
		tokenRules = List.copyOf(tokenRules);
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NonNull TokenStream stream, @NonNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		Objects.requireNonNull(ctx, "Token rule context must not be null");
		
		for (TokenRule rule : this.tokenRules()) {
			TokenRuleMatch match = rule.match(stream.copyWithOffset(0), ctx);
			if (match == null) {
				return null;
			}
			
			if (match.endIndex() - match.startIndex() > 1) {
				throw new IllegalStateException("Token rules that match multiple tokens are not supported by this rule, rule matched from index " + match.startIndex() + " to " + match.endIndex() + ": " + rule);
			}
		}
		
		int startIndex = stream.getCurrentIndex();
		Token token = stream.getCurrentToken();
		return new TokenRuleMatch(startIndex, stream.advance(), Collections.singletonList(token), this);
	}
	
	@Override
	public @NonNull TokenRule not() {
		return new AllOfTokenRule(this.tokenRules().stream().map(TokenRule::not).toList());
	}
}
