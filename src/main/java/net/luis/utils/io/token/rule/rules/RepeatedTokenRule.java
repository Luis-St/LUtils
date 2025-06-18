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
 * A token rule that matches a token rule a number of times.<br>
 * This rule is useful for creating complex matching logic by repeating a rule.<br>
 * It will match the token rule a number of times between the min and max occurrences.<br>
 * If the min and max occurrences are the same, it will match exactly that number of times.<br>
 *
 * @apiNote This class does not allow the creation of a optional token rule by setting the min and max occurrences to 0.<br>
 * This is because the implementation of {@link #match(List, int)} will return null if there is no match.<br>
 * Use {@link OptionalTokenRule} for that purpose.<br>
 *
 * @author Luis-St
 *
 * @param tokenRule The token rule to match
 * @param minOccurrences The minimum number of occurrences
 * @param maxOccurrences The maximum number of occurrences
 */
public record RepeatedTokenRule(
	@NotNull TokenRule tokenRule,
	int minOccurrences,
	int maxOccurrences
) implements TokenRule {
	
	/**
	 * Constructs a new repeated token rule with the given token rule and exact number of occurrences.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param occurrences The exact number of occurrences
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the number of occurrences is lower than 0
	 */
	public RepeatedTokenRule(@NotNull TokenRule tokenRule, int occurrences) {
		this(tokenRule, occurrences, occurrences);
	}
	
	/**
	 * Constructs a new repeated token rule with the given token rule and min and max number of occurrences.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param minOccurrences The minimum number of occurrences
	 * @param maxOccurrences The maximum number of occurrences
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the min or max occurrences are lower than 0, or if the max occurrences are lower than the min occurrences, or if both are 0
	 */
	public RepeatedTokenRule {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		if (minOccurrences < 0) {
			throw new IllegalArgumentException("Min occurrences must not be negative");
		}
		if (maxOccurrences < minOccurrences) {
			throw new IllegalArgumentException("Max occurrences must not be less than min occurrences");
		}
		if (maxOccurrences == 0) {
			throw new IllegalArgumentException("Min and max occurrences must not be 0, this rule will never match");
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
		while (currentIndex < tokens.size() && occurrences <= this.maxOccurrences) {
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
