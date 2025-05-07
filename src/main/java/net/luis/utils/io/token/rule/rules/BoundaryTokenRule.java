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
	@NotNull TokenRule betweenTokenRule,
	@NotNull TokenRule endTokenRule
) implements TokenRule {
	
	private static final Set<Class<? extends TokenRule>> INVALID_BETWEEN_RULES = Set.of(
		BoundaryTokenRule.class,
		RepeatedTokenRule.class,
		SequenceTokenRule.class,
		EndTokenRule.class
	);
	
	public BoundaryTokenRule(@NotNull TokenRule startTokenRule, @NotNull TokenRule endTokenRule) {
		this(Objects.requireNonNull(startTokenRule, "Start rule must not be null"), TokenRules.alwaysMatch(), Objects.requireNonNull(endTokenRule, "End rule must not be null"));
	}
	
	public BoundaryTokenRule {
		Objects.requireNonNull(startTokenRule, "Start rule must not be null");
		Objects.requireNonNull(betweenTokenRule, "Between token rule must not be null");
		if (betweenTokenRule instanceof AnyOfTokenRule(Set<TokenRule> tokenRules)) {
			validateTokenRules(tokenRules);
		}
		if (betweenTokenRule instanceof OptionalTokenRule(TokenRule tokenRule)) {
			validateTokenRules(Set.of(tokenRule));
		}
		Objects.requireNonNull(endTokenRule, "End rule must not be null");
	}
	
	private static void validateTokenRules(@NotNull Set<TokenRule> tokenRules) {
		for (TokenRule tokenRule : tokenRules) {
			if (INVALID_BETWEEN_RULES.contains(tokenRule.getClass())) {
				throw new IllegalArgumentException("Between token rule must not contain a token rule of type " + tokenRule.getClass().getSimpleName());
			}
			if (tokenRule instanceof AnyOfTokenRule(Set<TokenRule> rules)) {
				validateTokenRules(rules);
			}
			if (tokenRule instanceof OptionalTokenRule(TokenRule rule)) {
				validateTokenRules(Set.of(rule));
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
		List<Token> matchedTokens = Lists.newArrayList(startMatch.matchedTokens());
		while (currentIndex < tokens.size()) {
			TokenRuleMatch endMatch = this.endTokenRule.match(tokens, currentIndex);
			if (endMatch != null) {
				matchedTokens.addAll(endMatch.matchedTokens());
				return new TokenRuleMatch(startIndex, endMatch.endIndex(), matchedTokens, this);
			}
			
			TokenRuleMatch betweenMatch = this.betweenTokenRule.match(tokens, currentIndex);
			if (betweenMatch == null) {
				return null;
			}
			if (currentIndex - betweenMatch.endIndex() > 1) {
				throw new IllegalStateException("Between token rule must not match more than one token");
			}
			if (currentIndex < tokens.size()) {
				matchedTokens.addAll(betweenMatch.matchedTokens());
				currentIndex = betweenMatch.endIndex();
			}
		}
		
		return null;
	}
}
