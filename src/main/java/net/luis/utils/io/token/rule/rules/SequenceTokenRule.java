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
 *
 * @author Luis-St
 *
 */

public record SequenceTokenRule(
	@NotNull List<TokenRule> tokenRules
) implements TokenRule {
	
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
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		int currentIndex = startIndex;
		List<Token> matchedTokens = new ArrayList<>();
		for (TokenRule tokenRule : this.tokenRules) {
			TokenRuleMatch match = tokenRule.match(tokens, currentIndex);
			if (match == null) {
				return null;
			}
			
			matchedTokens.addAll(match.matchedTokens());
			currentIndex = match.endIndex();
		}
		return new TokenRuleMatch(startIndex, currentIndex, matchedTokens, this);
	}
}
