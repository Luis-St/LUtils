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

import net.luis.utils.io.token.rule.TokenRuleContext;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.assertions.LookaheadTokenRule;
import net.luis.utils.io.token.rule.rules.assertions.LookbehindTokenRule;
import net.luis.utils.io.token.rule.rules.quantifiers.OptionalTokenRule;
import net.luis.utils.io.token.rule.rules.quantifiers.RepeatedTokenRule;
import net.luis.utils.io.token.stream.TokenStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A functional interface representing a rule for matching tokens in a list.<br>
 * It defines a method to match tokens starting from a given index and returns a {@link TokenRuleMatch} if successful.<br>
 *
 * @see TokenRules
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface TokenRule {
	
	/**
	 * Checks if the given token stream match this rule.<br>
	 * If the match is successful, a {@link TokenRuleMatch} is returned, otherwise null.<br>
	 *
	 * @param stream The token stream to match against
	 * @param ctx The context of the token rule match
	 * @return A token rule match if successful, otherwise null
	 * @throws NullPointerException If the token stream or context is null
	 * @apiNote For the most rules, the token stream must have remaining tokens to match.<br>
	 * However, some rules can match even if the token stream is at the end.<br>
	 * Therefore, some rules do not include this check or have a modified behavior.
	 */
	@Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx);
	
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
	 * @see TokenRules#atLeast(TokenRule, int)
	 * @see RepeatedTokenRule
	 */
	default @NotNull TokenRule atLeast(int min) {
		return TokenRules.atLeast(this, min);
	}
	
	/**
	 * Makes this token rule exactly repeatable by wrapping it in a {@link RepeatedTokenRule}.<br>
	 * To make the new rule match, this rule must match exactly the specified number of times.<br>
	 *
	 * @param repeats The number of times this rule must match
	 * @return A new exactly repeatable token rule
	 * @throws IllegalArgumentException If repeats is less than 0
	 * @see TokenRules#exactly(TokenRule, int)
	 * @see RepeatedTokenRule
	 */
	default @NotNull TokenRule exactly(int repeats) {
		return TokenRules.exactly(this, repeats);
	}
	
	/**
	 * Makes this token rule at most repeatable by wrapping it in a {@link RepeatedTokenRule}.<br>
	 * To make the new rule match, this rule must match at most the specified number of times.<br>
	 *
	 * @param max The maximum number of times this rule can match
	 * @return A new at most repeatable token rule
	 * @throws IllegalArgumentException If max is less than 0
	 * @see TokenRules#atMost(TokenRule, int)
	 * @see RepeatedTokenRule
	 */
	default @NotNull TokenRule atMost(int max) {
		return TokenRules.atMost(this, max);
	}
	
	/**
	 * Makes this token rule infinitely repeatable by wrapping it in a {@link RepeatedTokenRule}.<br>
	 * To make the new rule match, this rule must match at least 0 and at most {@link Integer#MAX_VALUE} times.<br>
	 *
	 * @return A new infinitely repeatable token rule
	 * @see TokenRules#zeroOrMore(TokenRule)
	 * @see RepeatedTokenRule
	 */
	default @NotNull TokenRule zeroOrMore() {
		return TokenRules.zeroOrMore(this);
	}
	
	/**
	 * Makes this token rule between repeatable by wrapping it in a {@link RepeatedTokenRule}.<br>
	 * To make the new rule match, this rule must match at least min and at most max times.<br>
	 *
	 * @param min The minimum number of times this rule must match
	 * @param max The maximum number of times this rule can match
	 * @return A new between repeatable token rule
	 * @throws IllegalArgumentException If min is less than 0 or max is less than min
	 * @see TokenRules#between(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	default @NotNull TokenRule between(int min, int max) {
		return TokenRules.between(this, min, max);
	}
	
	/**
	 * Negates this token rule.<br>
	 * <p>
	 *     This operation is not supported by default and will throw an {@link UnsupportedOperationException}.<br>
	 * 	   All rules that are an instance of {@link NegatableTokenRule} are guaranteed to support negation.<br>
	 * 	   In addition, rules in the {@link net.luis.utils.io.token.rule.rules} package are also guaranteed to support negation.
	 * </p>
	 * <p>
	 *     The {@link UnsupportedOperationException} is thrown to avoid accidental misuse of this method on rules that do not support negation.<br>
	 *     All default rules support negation when calling this method.<br>
	 *     You only need to be careful with anonymous implementations or lambdas of this interface.
	 * </p>
	 *
	 * @return A new negated token rule
	 * @throws UnsupportedOperationException If this token rule does not support negation
	 * @see NegatableTokenRule
	 */
	default @NotNull TokenRule not() {
		throw new UnsupportedOperationException("This token rule does not support negation");
	}
	
	/**
	 * Creates a token group rule for this token rule by wrapping it in a {@link TokenGroupRule}.<br>
	 * A token group rule applies this rule to the tokens within a {@link net.luis.utils.io.token.tokens.TokenGroup}<br>
	 * rather than to the group itself.<br>
	 *
	 * @return A new token group rule
	 * @see TokenRules#group(TokenRule)
	 * @see TokenGroupRule
	 */
	default @NotNull TokenRule group() {
		return TokenRules.group(this);
	}
	
	/**
	 * Creates a positive lookahead for this token rule by wrapping it in a {@link LookaheadTokenRule}.<br>
	 * A positive lookahead means that the new rule will match if this rule matches at the current position,<br>
	 * but without consuming any tokens from the input.<br>
	 *
	 * @return A new positive lookahead token rule
	 * @see TokenRules#lookahead(TokenRule)
	 * @see LookaheadTokenRule
	 */
	default @NotNull TokenRule lookahead() {
		return TokenRules.lookahead(this);
	}
	
	/**
	 * Creates a negative lookahead for this token rule by wrapping it in a {@link LookaheadTokenRule}.<br>
	 * A negative lookahead means that the new rule will match if this rule does NOT match at the current position,<br>
	 * but without consuming any tokens from the input.<br>
	 *
	 * @return A new negative lookahead token rule
	 * @see TokenRules#negativeLookahead(TokenRule)
	 * @see LookaheadTokenRule
	 */
	default @NotNull TokenRule negativeLookahead() {
		return TokenRules.negativeLookahead(this);
	}
	
	/**
	 * Creates a positive lookbehind for this token rule by wrapping it in a {@link LookbehindTokenRule}.<br>
	 * A positive lookbehind means that the new rule will match if this rule matches behind the current position,<br>
	 * but without consuming any tokens from the input.<br>
	 *
	 * @return A new positive lookbehind token rule
	 * @see TokenRules#lookbehind(TokenRule)
	 * @see LookbehindTokenRule
	 */
	default @NotNull TokenRule lookbehind() {
		return TokenRules.lookbehind(this);
	}
	
	/**
	 * Creates a negative lookbehind for this token rule by wrapping it in a {@link LookbehindTokenRule}.<br>
	 * A negative lookbehind means that the new rule will match if this rule does NOT match behind the current position,<br>
	 * but without consuming any tokens from the input.<br>
	 *
	 * @return A new negative lookbehind token rule
	 * @see TokenRules#negativeLookbehind(TokenRule)
	 * @see LookbehindTokenRule
	 */
	default @NotNull TokenRule negativeLookbehind() {
		return TokenRules.negativeLookbehind(this);
	}
}
