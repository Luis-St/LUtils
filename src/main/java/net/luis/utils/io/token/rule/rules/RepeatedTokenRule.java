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

public record RepeatedTokenRule(
	@NotNull Rule tokenRule,
	int minOccurrences,
	int maxOccurrences // -1 means unlimited
) implements Rule {
	
	public RepeatedTokenRule(@NotNull Rule tokenRule, int occurrences) {
		this(tokenRule, occurrences, occurrences);
	}
	
	public RepeatedTokenRule {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		if (minOccurrences < 0) {
			throw new IllegalArgumentException("Min occurrences must not be negative");
		}
		if (maxOccurrences != -1 && maxOccurrences < minOccurrences) {
			throw new IllegalArgumentException("Max occurrences must not be less than minOccurrences");
		}
	}
	
	@Override
	public @Nullable Match match(@NotNull List<String> tokens, int startIndex) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		if (startIndex >= tokens.size()) {
			return null;
		}
		
		int currentIndex = startIndex;
		List<String> matchedTokens = new ArrayList<>();
		int occurrences = 0;
		while (currentIndex < tokens.size() && (this.maxOccurrences == -1 || occurrences < this.maxOccurrences)) {
			Match match = this.tokenRule.match(tokens, currentIndex);
			if (match == null) {
				break;
			}
			
			matchedTokens.addAll(match.matchedTokens());
			currentIndex = match.endIndex();
			occurrences++;
		}
		
		if (occurrences >= this.minOccurrences) {
			return new Match(startIndex, currentIndex, matchedTokens, this);
		}
		return null;
	}
}
