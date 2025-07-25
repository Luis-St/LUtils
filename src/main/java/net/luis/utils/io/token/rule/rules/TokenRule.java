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

import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * A functional interface representing a rule for matching tokens in a list.<br>
 * It defines a method to match tokens starting from a given index and returns a {@link TokenRuleMatch} if successful.<br>
 *
 * @see TokenDefinition
 * @see AlwaysMatchTokenRule
 * @see AnyOfTokenRule
 * @see BoundaryTokenRule
 * @see EndTokenRule
 * @see OptionalTokenRule
 * @see PatternTokenRule
 * @see RepeatedTokenRule
 * @see SequenceTokenRule
 * @see TokenRuleMatch
 * @see TokenRules
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface TokenRule {
	
	/**
	 * Checks if the given tokens match this rule starting from the specified index.<br>
	 * If the match is successful, a {@link TokenRuleMatch} is returned, otherwise null.<br>
	 *
	 * @param tokens The list of tokens to match against
	 * @param startIndex The index to start matching from
	 * @return A token rule match if successful, otherwise null
	 * @throws NullPointerException If the token list is null
	 * @apiNote For the most rules, the start index must be less than the size of the token list.<br>
	 * An exception is the {@link EndTokenRule}, which can match at the end of the list.<br>
	 */
	@Nullable TokenRuleMatch match(@NotNull List<Token> tokens, int startIndex);
	
	/**
	 * Makes this token rule optional by wrapping it in an {@link OptionalTokenRule}.<br>
	 *
	 * @return A new token rule that is optional
	 * @see TokenRules#optional(TokenRule)
	 * @see OptionalTokenRule
	 */
	default @NotNull TokenRule optional() {
		return TokenRules.optional(this);
	}
	
	/**
	 * Makes this token rule at least repeatable by wrapping it in a {@link RepeatedTokenRule}.<br>
	 * To make the new rule match, this rule must match at least the specified number of times.<br>
	 *
	 * @param min The minimum number of times this rule must match
	 * @return A new at least repeatable token rule
	 * @throws IllegalArgumentException If min is less than 0
	 * @see TokenRules#repeatAtLeast(TokenRule, int)
	 * @see RepeatedTokenRule
	 */
	default @NotNull TokenRule repeatAtLeast(int min) {
		return TokenRules.repeatAtLeast(this, min);
	}
	
	/**
	 * Makes this token rule exactly repeatable by wrapping it in a {@link RepeatedTokenRule}.<br>
	 * To make the new rule match, this rule must match exactly the specified number of times.<br>
	 *
	 * @param repeats The number of times this rule must match
	 * @return A new exactly repeatable token rule
	 * @throws IllegalArgumentException If repeats is less than 0
	 * @see TokenRules#repeatExactly(TokenRule, int)
	 * @see RepeatedTokenRule
	 */
	default @NotNull TokenRule repeatExactly(int repeats) {
		return TokenRules.repeatExactly(this, repeats);
	}
	
	/**
	 * Makes this token rule at most repeatable by wrapping it in a {@link RepeatedTokenRule}.<br>
	 * To make the new rule match, this rule must match at most the specified number of times.<br>
	 *
	 * @param max The maximum number of times this rule can match
	 * @return A new at most repeatable token rule
	 * @throws IllegalArgumentException If max is less than 0
	 * @see TokenRules#repeatAtMost(TokenRule, int)
	 * @see RepeatedTokenRule
	 */
	default @NotNull TokenRule repeatAtMost(int max) {
		return TokenRules.repeatAtMost(this, max);
	}
	
	/**
	 * Makes this token rule infinitely repeatable by wrapping it in a {@link RepeatedTokenRule}.<br>
	 * To make the new rule match, this rule must match at least 0 and at most {@link Integer#MAX_VALUE} times.<br>
	 *
	 * @return A new infinitely repeatable token rule
	 * @see TokenRules#repeatInfinitely(TokenRule)
	 * @see RepeatedTokenRule
	 */
	default @NotNull TokenRule repeatInfinitely() {
		return TokenRules.repeatInfinitely(this);
	}
	
	/**
	 * Makes this token rule between repeatable by wrapping it in a {@link RepeatedTokenRule}.<br>
	 * To make the new rule match, this rule must match at least min and at most max times.<br>
	 *
	 * @param min The minimum number of times this rule must match
	 * @param max The maximum number of times this rule can match
	 * @return A new between repeatable token rule
	 * @throws IllegalArgumentException If min is less than 0 or max is less than min
	 * @see TokenRules#repeatBetween(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	default @NotNull TokenRule repeatBetween(int min, int max) {
		return TokenRules.repeatBetween(this, min, max);
	}
}
