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

import com.google.common.collect.Lists;
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

public record BoundaryTokenRule(
	@NotNull TokenRule startTokenRule,
	@NotNull Set<TokenRule> betweenTokenRules,
	@NotNull TokenRule endTokenRule
) implements TokenRule {
	
	private static final Set<Class<? extends TokenRule>> INVALID_BETWEEN_RULES = Set.of(
		BoundaryTokenRule.class,
		RepeatedTokenRule.class,
		SequenceTokenRule.class
	);
	
	public BoundaryTokenRule {
		Objects.requireNonNull(startTokenRule, "Start rule must not be null");
		Objects.requireNonNull(betweenTokenRules, "Between token rule list must not be null");
		validateTokenRules(betweenTokenRules);
		Objects.requireNonNull(endTokenRule, "End rule must not be null");
	}
	
	private static void validateTokenRules(@NotNull Set<TokenRule> tokenRules) {
		for (TokenRule tokenRule : tokenRules) {
			if (INVALID_BETWEEN_RULES.contains(tokenRule.getClass())) {
				throw new IllegalArgumentException("Between token rule list must not be of type " + tokenRule.getClass().getSimpleName());
			}
			if (tokenRule instanceof AnyOfTokenRule(Set<TokenRule> rules)) {
				validateTokenRules(rules);
			}
		}
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		TokenRuleMatch startMatch = this.startTokenRule.match(tokens, startIndex);
		if (startMatch == null) {
			return null;
		}
		
		int currentIndex = startMatch.endIndex();
		AnyOfTokenRule betweenRule = new AnyOfTokenRule(this.betweenTokenRules);
		List<Token> matchedTokens = Lists.newArrayList(startMatch.matchedTokens());
		while (currentIndex < tokens.size()) {
			TokenRuleMatch endMatch = this.endTokenRule.match(tokens, currentIndex);
			if (endMatch != null) {
				matchedTokens.addAll(endMatch.matchedTokens());
				return new TokenRuleMatch(startIndex, endMatch.endIndex(), matchedTokens, this);
			}
			
			if (!this.betweenTokenRules.isEmpty()) {
				TokenRuleMatch betweenMatch = betweenRule.match(tokens, currentIndex);
				if (betweenMatch == null) {
					return null;
				}
				if (currentIndex - betweenMatch.endIndex() > 1) {
					throw new IllegalStateException("Between token rule must not match more than one token");
				}
				matchedTokens.addAll(betweenMatch.matchedTokens());
				currentIndex = betweenMatch.endIndex();
			}
			
			if (currentIndex < tokens.size()) {
				matchedTokens.add(tokens.get(currentIndex));
				currentIndex++;
			}
		}
		
		return null;
	}
}
