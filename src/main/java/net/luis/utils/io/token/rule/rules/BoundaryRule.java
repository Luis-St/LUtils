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

import net.luis.utils.io.token.rule.Match;
import net.luis.utils.io.token.rule.Rule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public record BoundaryRule(
	@NotNull Rule startRule,
	@NotNull Rule endRule,
	@NotNull Set<String> specifiedTokens,
	boolean ignoreMode // true = ignore specified tokens, false = only allow specified tokens
) implements Rule {
	
	public BoundaryRule {
		Objects.requireNonNull(startRule, "Start rule must not be null");
		Objects.requireNonNull(endRule, "End rule must not be null");
		specifiedTokens = Set.copyOf(Objects.requireNonNull(specifiedTokens, "Specified tokens must not be null"));
	}
	
	@Override
	public @Nullable Match match(@NotNull List<String> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		// Match the start rule
		Match startMatch = this.startRule.match(tokens, startIndex);
		if (startMatch == null) {
			return null;
		}
		
		int currentIndex = startMatch.endIndex();
		List<String> matchedTokens = new ArrayList<>(startMatch.matchedTokens());
		while (currentIndex < tokens.size()) { // Find the end rule, collecting tokens in between
			Match endMatch = this.endRule.match(tokens, currentIndex);
			if (endMatch != null) {
				matchedTokens.addAll(endMatch.matchedTokens());
				return new Match(startIndex, endMatch.endIndex(), matchedTokens, this);
			}
			
			if (currentIndex < tokens.size()) { // If current token should be included/excluded based on mode
				String currentToken = tokens.get(currentIndex);
				boolean shouldInclude;
				
				if (this.ignoreMode) {
					// In ignore-mode, include token if either:
					// 1. The specified set is empty (include everything)
					// 2. The token is not in the specified set (not ignored)
					shouldInclude = this.specifiedTokens.isEmpty() || !this.specifiedTokens.contains(currentToken);
				} else {
					// In include-mode, include token if either:
					// 1. The specified set is empty (include nothing)
					// 2. The token is in the specified set (explicitly included)
					shouldInclude = !this.specifiedTokens.isEmpty() && this.specifiedTokens.contains(currentToken);
				}
				
				if (shouldInclude) {
					matchedTokens.add(currentToken);
					currentIndex++;
				} else {
					return null;
				}
			}
		}
		
		return null;
	}
}
