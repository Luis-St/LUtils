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
 * A token rule that matches a sequence of token rules.<br>
 * This rule is useful for creating complex matching logic by combining multiple rules.<br>
 * It will match the token rules in the order they are provided, otherwise it will return null.<br>
 *
 * @author Luis-St
 *
 * @param tokenRules The list of token rules to match against
 */
public record SequenceTokenRule(
	@NonNull List<TokenRule> tokenRules
) implements TokenRule {
	
	/**
	 * Constructs a new sequence token rule with the given token rules.<br>
	 *
	 * @param tokenRules The list of token rules to match against
	 * @throws NullPointerException If the token rule list or any of its elements are null
	 * @throws IllegalArgumentException If the token rule list is empty or contains less than two rules
	 */
	public SequenceTokenRule {
		Objects.requireNonNull(tokenRules, "Token rule list must not be null");
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
		
		int startIndex = stream.getCurrentIndex();
		TokenStream workingStream = stream.copyWithOffset(0);
		List<Token> matchedTokens = new ArrayList<>();
		for (TokenRule tokenRule : this.tokenRules) {
			
			TokenRuleMatch match = tokenRule.match(workingStream, ctx);
			if (match == null) {
				return null;
			}
			
			matchedTokens.addAll(match.matchedTokens());
		}
		
		stream.advanceTo(workingStream);
		return new TokenRuleMatch(startIndex, stream.getCurrentIndex(), matchedTokens, this);
	}
	
	@Override
	public @NonNull TokenRule not() {
		return new AnyOfTokenRule(this.tokenRules.stream().map(TokenRule::not).toList()); // Negation using De Morgan's laws
	}
}
