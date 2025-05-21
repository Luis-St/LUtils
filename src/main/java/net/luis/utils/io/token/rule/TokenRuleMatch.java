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

package net.luis.utils.io.token.rule;

import net.luis.utils.io.token.rule.rules.*;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * A class that represents a match of a token rule.<br>
 * It contains the start and end index of the match, the matched tokens, and the matching token rule.<br>
 *
 * @author Luis-St
 *
 * @param startIndex The start index of the match
 * @param endIndex The end index of the match (exclusive)
 * @param matchedTokens The list of matched tokens
 * @param matchingTokenRule The matching token rule
 */
public record TokenRuleMatch(
	int startIndex,
	int endIndex,
	@NotNull List<Token> matchedTokens,
	@NotNull TokenRule matchingTokenRule
) {
	
	/**
	 * Constructs a new token rule match with the given start and end index, matched tokens, and matching token rule.<br>
	 * @param startIndex The start index of the match
	 * @param endIndex The end index of the match (exclusive)
	 * @param matchedTokens The list of matched tokens
	 * @param matchingTokenRule The matching token rule
	 * @throws NullPointerException If the matched tokens or matching token rule are null
	 */
	public TokenRuleMatch {
		Objects.requireNonNull(matchedTokens, "Matched tokens must not be null");
		Objects.requireNonNull(matchingTokenRule, "Matching rule must not be null");
	}
	
	/**
	 * Creates an empty token rule match with the given index.<br>
	 * An empty token rule match has a start and end index of the given index, an empty list of matched tokens, and a matching token rule that always matches.<br>
	 * This is useful for terminating the token rule matching process without consuming any tokens.<br>
	 * @param index The index of the empty token rule match
	 * @return An empty token rule match
	 * @see OptionalTokenRule
	 */
	public static @NotNull TokenRuleMatch empty(int index) {
		return new TokenRuleMatch(index, index, List.of(), TokenRules.alwaysMatch());
	}
}
