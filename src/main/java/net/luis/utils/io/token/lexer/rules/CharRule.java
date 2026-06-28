/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.token.lexer.rules;

import net.luis.utils.io.token.lexer.CharRuleMatch;
import net.luis.utils.io.token.lexer.rules.quantifiers.CharOptionalRule;
import net.luis.utils.io.token.lexer.rules.quantifiers.CharRepeatedRule;
import net.luis.utils.io.token.lexer.stream.CharStream;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * A functional interface representing a rule for matching characters in a stream.<br>
 * It defines a method to match characters starting from the current index of a {@link CharStream} and returns a {@link CharRuleMatch} if successful.<br>
 * <p>
 *     A character rule is the lexer-side analogue of a parser-side token rule.<br>
 *     In contrast to a token rule, a character rule is restricted to <b>regular</b> constructs only:
 *     character classes, sequences, alternations, quantifiers and anchors.<br>
 *     There is intentionally no recursion, no rule references and no capture, which is what keeps the lexer grammar regular.
 * </p>
 *
 * @see CharRules
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface CharRule {
	
	/**
	 * Checks if the given character stream matches this rule.<br>
	 * If the match is successful, a {@link CharRuleMatch} is returned and the stream is advanced past the match, otherwise null is returned.<br>
	 *
	 * @param stream The character stream to match against
	 * @return A character rule match if successful, otherwise null
	 * @throws NullPointerException If the character stream is null
	 */
	@Nullable CharRuleMatch match(@NonNull CharStream stream);
	
	/**
	 * Makes this character rule optional by wrapping it in a {@link CharOptionalRule}.<br>
	 *
	 * @return A new character rule that is optional
	 * @see CharRules#optional(CharRule)
	 * @see CharOptionalRule
	 */
	default @NonNull CharRule optional() {
		return CharRules.optional(this);
	}
	
	/**
	 * Makes this character rule at least repeatable by wrapping it in a {@link CharRepeatedRule}.<br>
	 * To make the new rule match, this rule must match at least the specified number of times.<br>
	 *
	 * @param min The minimum number of times this rule must match
	 * @return A new at least repeatable character rule
	 * @throws IllegalArgumentException If min is less than 0
	 * @see CharRules#atLeast(CharRule, int)
	 * @see CharRepeatedRule
	 */
	default @NonNull CharRule atLeast(int min) {
		return CharRules.atLeast(this, min);
	}
	
	/**
	 * Makes this character rule exactly repeatable by wrapping it in a {@link CharRepeatedRule}.<br>
	 * To make the new rule match, this rule must match exactly the specified number of times.<br>
	 *
	 * @param repeats The number of times this rule must match
	 * @return A new exactly repeatable character rule
	 * @throws IllegalArgumentException If repeats is less than 0
	 * @see CharRules#exactly(CharRule, int)
	 * @see CharRepeatedRule
	 */
	default @NonNull CharRule exactly(int repeats) {
		return CharRules.exactly(this, repeats);
	}
	
	/**
	 * Makes this character rule at most repeatable by wrapping it in a {@link CharRepeatedRule}.<br>
	 * To make the new rule match, this rule must match at most the specified number of times.<br>
	 *
	 * @param max The maximum number of times this rule can match
	 * @return A new at most repeatable character rule
	 * @throws IllegalArgumentException If max is less than 0
	 * @see CharRules#atMost(CharRule, int)
	 * @see CharRepeatedRule
	 */
	default @NonNull CharRule atMost(int max) {
		return CharRules.atMost(this, max);
	}
	
	/**
	 * Makes this character rule infinitely repeatable by wrapping it in a {@link CharRepeatedRule}.<br>
	 * To make the new rule match, this rule must match at least 0 and at most {@link Integer#MAX_VALUE} times.<br>
	 *
	 * @return A new infinitely repeatable character rule
	 * @see CharRules#zeroOrMore(CharRule)
	 * @see CharRepeatedRule
	 */
	default @NonNull CharRule zeroOrMore() {
		return CharRules.zeroOrMore(this);
	}
	
	/**
	 * Makes this character rule at least once repeatable by wrapping it in a {@link CharRepeatedRule}.<br>
	 * To make the new rule match, this rule must match at least 1 and at most {@link Integer#MAX_VALUE} times.<br>
	 *
	 * @return A new at least once repeatable character rule
	 * @see CharRules#oneOrMore(CharRule)
	 * @see CharRepeatedRule
	 */
	default @NonNull CharRule oneOrMore() {
		return CharRules.oneOrMore(this);
	}
	
	/**
	 * Makes this character rule between repeatable by wrapping it in a {@link CharRepeatedRule}.<br>
	 * To make the new rule match, this rule must match at least min and at most max times.<br>
	 *
	 * @param min The minimum number of times this rule must match
	 * @param max The maximum number of times this rule can match
	 * @return A new between repeatable character rule
	 * @throws IllegalArgumentException If min is less than 0 or max is less than min
	 * @see CharRules#between(CharRule, int, int)
	 * @see CharRepeatedRule
	 */
	default @NonNull CharRule between(int min, int max) {
		return CharRules.between(this, min, max);
	}
	
	/**
	 * Negates this character rule.<br>
	 * <p>
	 *     This operation is not supported by default and will throw an {@link UnsupportedOperationException}.<br>
	 *     All rules that are an instance of {@link NegatableCharRule} are guaranteed to support negation.
	 * </p>
	 * <p>
	 *     The {@link UnsupportedOperationException} is thrown to avoid accidental misuse of this method on rules that do not support negation.<br>
	 *     You only need to be careful with anonymous implementations or lambdas of this interface.
	 * </p>
	 *
	 * @return A new negated character rule
	 * @throws UnsupportedOperationException If this character rule does not support negation
	 * @see NegatableCharRule
	 */
	default @NonNull CharRule not() {
		throw new UnsupportedOperationException("This character rule does not support negation");
	}
}
