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

package net.luis.utils.io.token.rule.rules.combinators;

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	@NotNull List<TokenRule> tokenRules
) implements TokenRule {
	
	/**
	 * Constructs a new sequence token rule with the given token rules.<br>
	 *
	 * @param tokenRules The list of token rules to match against
	 * @throws NullPointerException If the token rule list or any of its elements are null
	 * @throws IllegalArgumentException If the token rule list is empty
	 */
	public SequenceTokenRule {
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
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream must not be null");
		
		int startIndex = stream.getCurrentIndex();
		/*int currentIndex = startIndex;*/
		List<Token> matchedTokens = new ArrayList<>();
		for (TokenRule tokenRule : this.tokenRules) {
			
			TokenRuleMatch match = tokenRule.match(stream);
			if (match == null) {
				return null;
			}
			
			matchedTokens.addAll(match.matchedTokens());
			/*currentIndex = match.endIndex();*/
		}
		return new TokenRuleMatch(startIndex, stream.getCurrentIndex(), matchedTokens, this);
	}
}
