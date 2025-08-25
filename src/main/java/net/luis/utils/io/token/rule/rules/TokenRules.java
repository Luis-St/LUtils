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
import net.luis.utils.io.token.rule.rules.assertions.*;
import net.luis.utils.io.token.rule.rules.assertions.anchors.*;
import net.luis.utils.io.token.rule.rules.combinators.*;
import net.luis.utils.io.token.rule.rules.matchers.LengthTokenRule;
import net.luis.utils.io.token.rule.rules.matchers.PatternTokenRule;
import net.luis.utils.io.token.rule.rules.quantifiers.OptionalTokenRule;
import net.luis.utils.io.token.rule.rules.quantifiers.RepeatedTokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A utility class for creating token rules.<br>
 *
 * @author Luis-St
 */
public final class TokenRules {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private TokenRules() {}
	
	/**
	 * Provides a token rule that always matches.<br>
	 *
	 * @return The token rule
	 * @apiNote This is the preferred way to access the {@link AlwaysMatchTokenRule#INSTANCE} instance
	 * @see AlwaysMatchTokenRule
	 */
	public static @NotNull TokenRule alwaysMatch() {
		return AlwaysMatchTokenRule.INSTANCE;
	}
	
	/**
	 * Creates a pattern token rule with the given pattern in string format.<br>
	 *
	 * @param pattern The pattern to match
	 * @return The created token rule
	 * @throws NullPointerException If the pattern is null
	 * @see #pattern(Pattern)
	 * @see Pattern
	 */
	public static @NotNull TokenRule pattern(@Language("RegExp") @NotNull String pattern) {
		return new PatternTokenRule(pattern);
	}
	
	/**
	 * Creates a pattern token rule with the given pattern.<br>
	 *
	 * @param pattern The pattern to match
	 * @return The created token rule
	 * @throws NullPointerException If the pattern is null
	 * @see Pattern
	 */
	public static @NotNull TokenRule pattern(@NotNull Pattern pattern) {
		return new PatternTokenRule(pattern);
	}
	
	/**
	 * Creates an optional token rule with the given token rule.<br>
	 *
	 * @param tokenRule The token rule to match optionally
	 * @return The created token rule that matches the given token rule optionally
	 * @throws NullPointerException If the token rule is null
	 * @see OptionalTokenRule
	 */
	public static @NotNull TokenRule optional(@NotNull TokenRule tokenRule) {
		return new OptionalTokenRule(tokenRule);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and the minimum number of occurrences.<br>
	 * This method is equivalent to {@link #repeatBetween(TokenRule, int, int)} with the maximum number of occurrences set to {@link Integer#MAX_VALUE}.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param min The minimum number of occurrences
	 * @return The created token rule that matches the given token rule at least the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the minimum number of occurrences is less than 0
	 * @see #repeatBetween(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule repeatAtLeast(@NotNull TokenRule tokenRule, int min) {
		return repeatBetween(tokenRule, min, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and the exact number of occurrences.<br>
	 * This method is equivalent to {@link #repeatBetween(TokenRule, int, int)} with the minimum and maximum number of occurrences set to the same value.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param repeats The exact number of occurrences
	 * @return The created token rule that matches the given token rule exactly the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the number of occurrences is lower than 0
	 * @see #repeatBetween(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule repeatExactly(@NotNull TokenRule tokenRule, int repeats) {
		return repeatBetween(tokenRule, repeats, repeats);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and the maximum number of occurrences.<br>
	 * This method is equivalent to {@link #repeatBetween(TokenRule, int, int)} with the minimum number of occurrences set to 0.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param max The maximum number of occurrences
	 * @return The created token rule that matches the given token rule at most the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the maximum number of occurrences is lower than 0
	 * @see #repeatBetween(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule repeatAtMost(@NotNull TokenRule tokenRule, int max) {
		return repeatBetween(tokenRule, 0, max);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and no limit on the number of occurrences.<br>
	 * This method is equivalent to {@link #repeatBetween(TokenRule, int, int)} with the minimum number of occurrences set to 0 and the maximum number of occurrences set to {@link Integer#MAX_VALUE}.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @return The created token rule that matches the given token rule any number of times
	 * @throws NullPointerException If the token rule is null
	 * @see #repeatBetween(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule repeatInfinitely(@NotNull TokenRule tokenRule) {
		return repeatBetween(tokenRule, 0, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and the minimum and maximum number of occurrences.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param min The minimum number of occurrences
	 * @param max The maximum number of occurrences
	 * @return The created token rule that matches the given token rule between the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the minimum or maximum number of occurrences is lower than 0, or if the maximum number of occurrences is lower than the minimum number of occurrences, or if both are 0
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule repeatBetween(@NotNull TokenRule tokenRule, int min, int max) {
		return new RepeatedTokenRule(tokenRule, min, max);
	}
	
	/**
	 * Creates a token rule that matches a sequence of token rules.<br>
	 *
	 * @param tokenRules The token rules to match in sequence
	 * @return The created token rule
	 * @throws NullPointerException If the token rule array or any of its elements are null
	 * @throws IllegalArgumentException If the token rule array is empty
	 * @see SequenceTokenRule
	 */
	public static @NotNull TokenRule sequence(TokenRule @NotNull ... tokenRules) {
		return sequence(List.of(Objects.requireNonNull(tokenRules, "Token rule array must not be null")));
	}
	
	/**
	 * Creates a token rule that matches a sequence of token rules.<br>
	 *
	 * @param tokenRules The token rules to match in sequence
	 * @return The created token rule
	 * @throws NullPointerException If the token rule list or any of its elements are null
	 * @throws IllegalArgumentException If the token rule list is empty
	 * @see SequenceTokenRule
	 */
	public static @NotNull TokenRule sequence(@NotNull List<TokenRule> tokenRules) {
		return new SequenceTokenRule(tokenRules);
	}
	
	/**
	 * Creates a token rule that matches any of the given token rules.<br>
	 *
	 * @param tokenRules The token rules to match any of
	 * @return The created token rule
	 * @throws NullPointerException If the token rule array or any of its elements are null
	 * @throws IllegalArgumentException If the token rule array is empty
	 * @see AnyOfTokenRule
	 */
	public static @NotNull TokenRule any(TokenRule @NotNull ... tokenRules) {
		return any(Set.of(Objects.requireNonNull(tokenRules, "Token rule array must not be null")));
	}
	
	/**
	 * Creates a token rule that matches any of the given token rules.<br>
	 *
	 * @param tokenRules The token rules to match any of
	 * @return The created token rule
	 * @throws NullPointerException If the token rule set or any of its elements are null
	 * @throws IllegalArgumentException If the token rule set is empty
	 * @see AnyOfTokenRule
	 */
	public static @NotNull TokenRule any(@NotNull Set<TokenRule> tokenRules) {
		return new AnyOfTokenRule(tokenRules);
	}
	
	/**
	 * Creates a token rule that matches a boundary between the given start and end token rules.<br>
	 *
	 * @param startTokenRule The token rule that marks the start of the sequence
	 * @param endTokenRule The token rule that marks the end of the sequence
	 * @return The created token rule
	 * @throws NullPointerException If the start or end token rule is null
	 * @throws IllegalArgumentException If the start token rule is invalid
	 * @see BoundaryTokenRule
	 */
	public static @NotNull TokenRule boundary(@NotNull TokenRule startTokenRule, @NotNull TokenRule endTokenRule) {
		return new BoundaryTokenRule(startTokenRule, endTokenRule);
	}
	
	/**
	 * Creates a token rule that matches a boundary between the given start, between, and end token rules.<br>
	 *
	 * @param startTokenRule The token rule that marks the start of the sequence
	 * @param betweenTokenRule The token rule that matches the content between the start and end token rules
	 * @param endTokenRule The token rule that marks the end of the sequence
	 * @return The created token rule
	 * @throws NullPointerException If the start, between, or end token rule is null
	 * @throws IllegalArgumentException If the start or between token rule is invalid
	 * @see BoundaryTokenRule
	 */
	public static @NotNull TokenRule boundary(@NotNull TokenRule startTokenRule, @NotNull TokenRule betweenTokenRule, @NotNull TokenRule endTokenRule) {
		return new BoundaryTokenRule(startTokenRule, betweenTokenRule, endTokenRule);
	}
	
	/**
	 * Creates a token rule that matches the end of the document (entire token list).<br>
	 *
	 * @return The token rule
	 * @see EndTokenRule
	 */
	public static @NotNull TokenRule endDocument() {
		return new EndTokenRule(AnchorType.DOCUMENT);
	}
	
	/**
	 * Creates a token rule that matches the end of a line.<br>
	 *
	 * @return The token rule
	 * @see EndTokenRule
	 */
	public static @NotNull TokenRule endLine() {
		return new EndTokenRule(AnchorType.LINE);
	}
	
	/**
	 * Creates a positive lookahead token rule that checks if the given rule matches ahead without consuming tokens.<br>
	 *
	 * @param tokenRule The token rule to match ahead
	 * @return The created lookahead token rule
	 * @throws NullPointerException If the token rule is null
	 * @see LookaheadTokenRule
	 */
	public static @NotNull TokenRule lookahead(@NotNull TokenRule tokenRule) {
		return new LookaheadTokenRule(tokenRule, LookMatchMode.POSITIVE);
	}
	
	/**
	 * Creates a negative lookahead token rule that checks if the given rule does NOT match ahead without consuming tokens.<br>
	 *
	 * @param tokenRule The token rule to check ahead
	 * @return The created negative lookahead token rule
	 * @throws NullPointerException If the token rule is null
	 * @see LookaheadTokenRule
	 */
	public static @NotNull TokenRule negativeLookahead(@NotNull TokenRule tokenRule) {
		return new LookaheadTokenRule(tokenRule, LookMatchMode.NEGATIVE);
	}
	
	/**
	 * Creates a positive lookbehind token rule that checks if the given rule matches behind the current position.<br>
	 *
	 * @param tokenRule The token rule to match behind
	 * @return The created lookbehind token rule
	 * @throws NullPointerException If the token rule is null
	 * @see LookbehindTokenRule
	 */
	public static @NotNull TokenRule lookbehind(@NotNull TokenRule tokenRule) {
		return new LookbehindTokenRule(tokenRule, LookMatchMode.POSITIVE);
	}
	
	/**
	 * Creates a negative lookbehind token rule that checks if the given rule does NOT match behind the current position.<br>
	 *
	 * @param tokenRule The token rule to check behind
	 * @return The created negative lookbehind token rule
	 * @throws NullPointerException If the token rule is null
	 * @see LookbehindTokenRule
	 */
	public static @NotNull TokenRule negativeLookbehind(@NotNull TokenRule tokenRule) {
		return new LookbehindTokenRule(tokenRule, LookMatchMode.NEGATIVE);
	}
	
	/**
	 * Creates a token rule that matches the start of the document (entire token list).<br>
	 *
	 * @return The token rule
	 * @see StartTokenRule
	 */
	public static @NotNull TokenRule startDocument() {
		return new StartTokenRule(AnchorType.DOCUMENT);
	}
	
	/**
	 * Creates a token rule that matches the start of a line.<br>
	 *
	 * @return The token rule
	 * @see StartTokenRule
	 */
	public static @NotNull TokenRule startLine() {
		return new StartTokenRule(AnchorType.LINE);
	}
	
	/**
	 * Creates a token rule that matches tokens with at least the specified length.<br>
	 *
	 * @param minLength The minimum length of the token value (inclusive)
	 * @return The created length token rule
	 * @throws IllegalArgumentException If minLength is negative
	 * @see #lengthBetween(int, int)
	 * @see LengthTokenRule
	 */
	public static @NotNull TokenRule minLength(int minLength) {
		return lengthBetween(minLength, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a token rule that matches tokens with exactly the specified length.<br>
	 *
	 * @param exactLength The exact length the token value must have
	 * @return The created length token rule
	 * @throws IllegalArgumentException If exactLength is negative
	 * @see #lengthBetween(int, int)
	 * @see LengthTokenRule
	 */
	public static @NotNull TokenRule exactLength(int exactLength) {
		return lengthBetween(exactLength, exactLength);
	}
	
	/**
	 * Creates a token rule that matches tokens with at most the specified length.<br>
	 *
	 * @param maxLength The maximum length of the token value (inclusive)
	 * @return The created length token rule
	 * @throws IllegalArgumentException If maxLength is negative
	 * @see #lengthBetween(int, int)
	 * @see LengthTokenRule
	 */
	public static @NotNull TokenRule maxLength(int maxLength) {
		return lengthBetween(0, maxLength);
	}
	
	/**
	 * Creates a token rule that matches tokens based on their length constraints.<br>
	 *
	 * @param minLength The minimum length of the token value (inclusive)
	 * @param maxLength The maximum length of the token value (inclusive)
	 * @return The created length token rule
	 * @throws IllegalArgumentException If minLength is negative, maxLength is negative, or maxLength is less than minLength
	 * @see LengthTokenRule
	 */
	public static @NotNull TokenRule lengthBetween(int minLength, int maxLength) {
		return new LengthTokenRule(minLength, maxLength);
	}
	
	/**
	 * Creates a token group rule that applies the given token rule to the tokens within a group.<br>
	 * This rule matches if the token at the current position is a {@link net.luis.utils.io.token.tokens.TokenGroup}
	 * and the inner rule matches when applied to the tokens contained within that group.<br>
	 *
	 * @param tokenRule The inner token rule to apply to the tokens within the group
	 * @return The created token group rule
	 * @throws NullPointerException If the token rule is null
	 * @see TokenGroupRule
	 */
	public static @NotNull TokenRule group(@NotNull TokenRule tokenRule) {
		return new TokenGroupRule(tokenRule);
	}
	
	/**
	 * Creates a separated list token rule that matches elements separated by a delimiter.<br>
	 * This is equivalent to: {@code sequence(elementRule, sequence(separatorRule, elementRule).repeatInfinitely())}<br>
	 * <p>
	 *     This pattern matches one or more elements separated by the specified separator,
	 *     which is common in many grammar rules like parameter lists, array elements, etc.
	 * </p>
	 *
	 * @param elementRule The token rule for matching list elements
	 * @param separatorRule The token rule for matching separators between elements
	 * @return The created separated list token rule
	 * @throws NullPointerException If the element rule or separator rule is null
	 * @see #separatedList(TokenRule, String)
	 * @see #sequence(TokenRule...)
	 * @see #repeatInfinitely(TokenRule)
	 */
	public static @NotNull TokenRule separatedList(@NotNull TokenRule elementRule, @NotNull TokenRule separatorRule) {
		Objects.requireNonNull(elementRule, "Element rule must not be null");
		Objects.requireNonNull(separatorRule, "Separator rule must not be null");
		
		return sequence(
			elementRule,
			sequence(
				separatorRule, elementRule
			).repeatInfinitely()
		);
	}
	
	/**
	 * Creates a separated list token rule that matches elements separated by a literal string delimiter.<br>
	 * This is a convenience method equivalent to {@link #separatedList(TokenRule, TokenRule)}
	 * with a string token definition for the separator.<br>
	 *
	 * @param elementRule The token rule for matching list elements
	 * @param separator The literal string separator (will be converted to a token definition)
	 * @return The created separated list token rule
	 * @throws NullPointerException If the element rule or separator string is null
	 * @see #separatedList(TokenRule, TokenRule)
	 * @see TokenDefinition#of(String, boolean)
	 */
	public static @NotNull TokenRule separatedList(@NotNull TokenRule elementRule, @NotNull String separator) {
		Objects.requireNonNull(elementRule, "Element rule must not be null");
		Objects.requireNonNull(separator, "Separator string must not be null");
		
		return separatedList(elementRule, TokenDefinition.of(separator, false));
	}
	
	/**
	 * Creates a recursive token rule using the specified rule factory function.<br>
	 * The factory function receives the recursive rule itself as a parameter, enabling
	 * complex recursive grammar definitions.<br>
	 *
	 * @param ruleFactory A function that takes the recursive rule and returns the complete rule definition
	 * @return The created recursive token rule
	 * @throws NullPointerException If the rule factory is null or if the factory returns null
	 * @see RecursiveTokenRule
	 */
	public static @NotNull TokenRule recursive(@NotNull Function<TokenRule, TokenRule> ruleFactory) {
		return new RecursiveTokenRule(ruleFactory);
	}
	
	/**
	 * Creates a recursive token rule for simple opening-content-closing patterns.<br>
	 * This is a convenience method for backward compatibility with simple boundary patterns.<br>
	 *
	 * @param openingRule The rule that must match at the beginning
	 * @param contentRule The rule for content between opening and closing (may reference recursion)
	 * @param closingRule The rule that must match at the end
	 * @return The created recursive token rule
	 * @throws NullPointerException If the opening, content, or closing rule is null
	 * @see #recursive(Function)
	 * @see RecursiveTokenRule
	 */
	public static @NotNull TokenRule recursive(@NotNull TokenRule openingRule, @NotNull TokenRule contentRule, @NotNull TokenRule closingRule) {
		return new RecursiveTokenRule(openingRule, contentRule, closingRule);
	}
	
	/**
	 * Creates a recursive token rule for opening-closing patterns with flexible content.<br>
	 * The content rule factory receives the recursive rule itself as a parameter.<br>
	 *
	 * @param openingRule The rule that must match at the beginning
	 * @param closingRule The rule that must match at the end
	 * @param contentRuleFactory A function that takes the recursive rule and returns the content rule
	 * @return The created recursive token rule
	 * @throws NullPointerException If the opening, closing rule, or content rule factory is null
	 * @see #recursive(Function)
	 * @see RecursiveTokenRule
	 */
	public static @NotNull TokenRule recursive(@NotNull TokenRule openingRule, @NotNull TokenRule closingRule, @NotNull Function<TokenRule, TokenRule> contentRuleFactory) {
		return new RecursiveTokenRule(openingRule, closingRule, contentRuleFactory);
	}
	
	/**
	 * Creates a custom token rule that matches tokens based on a provided condition.<br>
	 * The condition is a predicate that takes a {@link Token} and returns true if the token matches the rule.<br>
	 * This allows for flexible and dynamic token matching based on custom logic.<br>
	 *
	 * @param condition The condition to match tokens against
	 * @return The created custom token rule
	 * @throws NullPointerException If the condition is null
	 * @see CustomTokeRule
	 */
	public static @NotNull TokenRule custom(@NotNull Predicate<Token> condition) {
		return new CustomTokeRule(condition);
	}
	
	/**
	 * Provides a token rule that never matches.<br>
	 *
	 * @return The token rule
	 * @apiNote This is the preferred way to access the {@link NeverMatchTokenRule#INSTANCE} instance
	 * @see NeverMatchTokenRule
	 */
	public static @NotNull TokenRule neverMatch() {
		return NeverMatchTokenRule.INSTANCE;
	}
}
