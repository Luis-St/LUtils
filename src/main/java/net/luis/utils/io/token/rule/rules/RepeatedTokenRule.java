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

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public record RepeatedTokenRule(
	@NotNull TokenRule tokenRule,
	int minOccurrences,
	int maxOccurrences
) implements TokenRule {
	
	public RepeatedTokenRule(@NotNull TokenRule tokenRule, int occurrences) {
		this(tokenRule, occurrences, occurrences);
	}
	
	public RepeatedTokenRule {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		if (minOccurrences < 0) {
			throw new IllegalArgumentException("Min occurrences must not be negative");
		}
		if (maxOccurrences < minOccurrences) {
			throw new IllegalArgumentException("Max occurrences must not be less than min occurrences");
		}
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		int occurrences = 0;
		int currentIndex = startIndex;
		List<Token> matchedTokens = Lists.newArrayList();
		while (currentIndex < tokens.size() && occurrences < this.maxOccurrences) {
			TokenRuleMatch match = this.tokenRule.match(tokens, currentIndex);
			if (match == null) {
				break;
			}
			
			matchedTokens.addAll(match.matchedTokens());
			currentIndex = match.endIndex();
			occurrences++;
		}
		
		if (this.minOccurrences <= occurrences && occurrences <= this.maxOccurrences) {
			return new TokenRuleMatch(startIndex, currentIndex, matchedTokens, this);
		}
		return null;
	}
}
