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

package net.luis.utils.io.token.rules;

import net.luis.utils.io.token.rules.assertions.LookaheadTokenRule;
import net.luis.utils.io.token.rules.assertions.LookbehindTokenRule;
import net.luis.utils.io.token.rules.assertions.anchors.EndTokenRule;
import net.luis.utils.io.token.rules.assertions.anchors.StartTokenRule;
import net.luis.utils.io.token.rules.combinators.*;
import net.luis.utils.io.token.rules.core.LookMatchMode;
import net.luis.utils.io.token.rules.core.ReferenceType;
import net.luis.utils.io.token.rules.matchers.*;
import net.luis.utils.io.token.rules.quantifiers.OptionalTokenRule;
import net.luis.utils.io.token.rules.quantifiers.RepeatedTokenRule;
import net.luis.utils.io.token.rules.reference.CaptureTokenRule;
import net.luis.utils.io.token.rules.reference.ReferenceTokenRule;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.type.TokenType;
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
	 * Provides a token rule that never matches.<br>
	 *
	 * @return The token rule
	 * @apiNote This is the preferred way to access the {@link NeverMatchTokenRule#INSTANCE} instance
	 * @see NeverMatchTokenRule
	 */
	public static @NotNull TokenRule neverMatch() {
		return NeverMatchTokenRule.INSTANCE;
	}
	
	/**
	 * Creates a value token rule that matches the value of a single token against the given character.<br>
	 *
	 * @param value The character to match against
	 * @param ignoreCase Whether to ignore case when matching or not
	 * @return The created token rule
	 * @throws IllegalArgumentException If the value is empty
	 * @see ValueTokenRule
	 */
	public static @NotNull TokenRule value(char value, boolean ignoreCase) {
		return new ValueTokenRule(value, ignoreCase);
	}
	
	/**
	 * Creates a value token rule that matches the value of a single token against the given string.<br>
	 *
	 * @param value The string to match against
	 * @param ignoreCase Whether to ignore case when matching or not
	 * @return The created token rule
	 * @throws NullPointerException If the value is null
	 * @throws IllegalArgumentException If the value is empty
	 * @see ValueTokenRule
	 */
	public static @NotNull TokenRule value(@NotNull String value, boolean ignoreCase) {
		return new ValueTokenRule(value, ignoreCase);
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
	 * Creates a type token rule that matches tokens containing all the specified token types.<br>
	 *
	 * @param tokenTypes The token types that must all be present on a matching token
	 * @return The created token rule
	 * @throws NullPointerException If the token type array or any of its elements are null
	 * @throws IllegalArgumentException If the token type array is empty
	 * @see TypeTokenRule
	 */
	public static @NotNull TokenRule type(TokenType @NotNull ... tokenTypes) {
		return type(Set.of(Objects.requireNonNull(tokenTypes, "Token type array must not be null")));
	}
	
	/**
	 * Creates a type token rule that matches tokens containing all the specified token types.<br>
	 *
	 * @param tokenTypes The set of token types that must all be present on a matching token
	 * @return The created token rule
	 * @throws NullPointerException If the token type set is null
	 * @throws IllegalArgumentException If the token type set is empty
	 * @see TypeTokenRule
	 */
	public static @NotNull TokenRule type(@NotNull Set<TokenType> tokenTypes) {
		return new TypeTokenRule(tokenTypes);
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
	 * This method is equivalent to {@link #between(TokenRule, int, int)} with the maximum number of occurrences set to {@link Integer#MAX_VALUE}.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param min The minimum number of occurrences
	 * @return The created token rule that matches the given token rule at least the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the minimum number of occurrences is less than 0
	 * @see #between(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule atLeast(@NotNull TokenRule tokenRule, int min) {
		return between(tokenRule, min, Integer.MAX_VALUE);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and the exact number of occurrences.<br>
	 * This method is equivalent to {@link #between(TokenRule, int, int)} with the minimum and maximum number of occurrences set to the same value.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param repeats The exact number of occurrences
	 * @return The created token rule that matches the given token rule exactly the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the number of occurrences is lower than 0
	 * @see #between(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule exactly(@NotNull TokenRule tokenRule, int repeats) {
		return between(tokenRule, repeats, repeats);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and the maximum number of occurrences.<br>
	 * This method is equivalent to {@link #between(TokenRule, int, int)} with the minimum number of occurrences set to 0.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @param max The maximum number of occurrences
	 * @return The created token rule that matches the given token rule at most the given number of times
	 * @throws NullPointerException If the token rule is null
	 * @throws IllegalArgumentException If the maximum number of occurrences is lower than 0
	 * @see #between(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule atMost(@NotNull TokenRule tokenRule, int max) {
		return between(tokenRule, 0, max);
	}
	
	/**
	 * Creates a repeated token rule with the given token rule and no limit on the number of occurrences.<br>
	 * This method is equivalent to {@link #between(TokenRule, int, int)} with the minimum number of occurrences set to 0 and the maximum number of occurrences set to {@link Integer#MAX_VALUE}.<br>
	 *
	 * @param tokenRule The token rule to match
	 * @return The created token rule that matches the given token rule any number of times
	 * @throws NullPointerException If the token rule is null
	 * @see #between(TokenRule, int, int)
	 * @see RepeatedTokenRule
	 */
	public static @NotNull TokenRule zeroOrMore(@NotNull TokenRule tokenRule) {
		return between(tokenRule, 0, Integer.MAX_VALUE);
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
	public static @NotNull TokenRule between(@NotNull TokenRule tokenRule, int min, int max) {
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
		return any(List.of(Objects.requireNonNull(tokenRules, "Token rule array must not be null")));
	}
	
	/**
	 * Creates a token rule that matches any of the given token rules.<br>
	 *
	 * @param tokenRules The token rules to match any of
	 * @return The created token rule
	 * @throws NullPointerException If the token rule list or any of its elements are null
	 * @throws IllegalArgumentException If the token rule list is empty
	 * @see AnyOfTokenRule
	 */
	public static @NotNull TokenRule any(@NotNull List<TokenRule> tokenRules) {
		return new AnyOfTokenRule(tokenRules);
	}
	
	/**
	 * Creates a token rule that matches all the given token rules at the same position.<br>
	 * All rules must match on the same single token for this rule to match.<br>
	 *
	 * @param tokenRules The token rules to match all of
	 * @return The created token rule
	 * @throws NullPointerException If the token rule array or any of its elements are null
	 * @throws IllegalArgumentException If the token rule array is empty or contains less than two rules
	 * @see AllOfTokenRule
	 */
	public static @NotNull TokenRule all(TokenRule @NotNull ... tokenRules) {
		return all(List.of(Objects.requireNonNull(tokenRules, "Token rule array must not be null")));
	}
	
	/**
	 * Creates a token rule that matches all the given token rules at the same position.<br>
	 * All rules must match on the same single token for this rule to match.<br>
	 *
	 * @param tokenRules The token rules to match all of
	 * @return The created token rule
	 * @throws NullPointerException If the token rule list or any of its elements are null
	 * @throws IllegalArgumentException If the token rule list is empty or contains less than two rules
	 * @see AllOfTokenRule
	 */
	public static @NotNull TokenRule all(@NotNull List<TokenRule> tokenRules) {
		return new AllOfTokenRule(tokenRules);
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
	 * Creates a lazily-initialized token rule.<br>
	 * The lazy token rule must be initialized before this rule can successfully match tokens.<br>
	 * This is useful for defining recursive rules, rules that depend on runtime conditions or cyclic dependencies between rules.<br>
	 *
	 * @return The created lazy token rule
	 * @see LazyTokenRule
	 */
	public static @NotNull LazyTokenRule lazy() {
		return new LazyTokenRule();
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
	 * Creates a token rule that matches the start of the document (entire token list).<br>
	 *
	 * @return The token rule
	 * @see StartTokenRule
	 */
	public static @NotNull TokenRule startDocument() {
		return StartTokenRule.DOCUMENT;
	}
	
	/**
	 * Creates a token rule that matches the start of a line.<br>
	 *
	 * @return The token rule
	 * @see StartTokenRule
	 */
	public static @NotNull TokenRule startLine() {
		return StartTokenRule.LINE;
	}
	
	/**
	 * Creates a token rule that matches the end of the document (entire token list).<br>
	 *
	 * @return The token rule
	 * @see EndTokenRule
	 */
	public static @NotNull TokenRule endDocument() {
		return EndTokenRule.DOCUMENT;
	}
	
	/**
	 * Creates a token rule that matches the end of a line.<br>
	 *
	 * @return The token rule
	 * @see EndTokenRule
	 */
	public static @NotNull TokenRule endLine() {
		return EndTokenRule.LINE;
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
	 * Creates a capture token rule that captures the tokens matched by the specified token rule and stores them in the context under a specified key.<br>
	 * The captured tokens can be retrieved later using the key.<br>
	 *
	 * @param key The key under which to store the captured tokens in the context
	 * @param tokenRule The token rule to capture tokens from
	 * @return The created capture token rule
	 * @throws NullPointerException If the token rule or key is null
	 * @throws IllegalArgumentException If the key is empty
	 * @see CaptureTokenRule
	 */
	public static @NotNull TokenRule capture(@NotNull String key, @NotNull TokenRule tokenRule) {
		return new CaptureTokenRule(key, tokenRule);
	}
	
	/**
	 * Creates a reference token rule that dynamically references either another token rule or a list of tokens by its key in the context.<br>
	 * The rule will only be able to match if either a rule or a list of tokens is found in the context using the key.<br>
	 *
	 * @param key The key used to look up the referenced rule or tokens in the context
	 * @return The created reference token rule
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the key is empty
	 * @see ReferenceTokenRule
	 * @see ReferenceType#DYNAMIC
	 * @see #referenceRule(String)
	 * @see #referenceTokens(String)
	 */
	public static @NotNull TokenRule reference(@NotNull String key) {
		return new ReferenceTokenRule(key, ReferenceType.DYNAMIC);
	}
	
	/**
	 * Creates a reference token rule that references another token rule by its key in the context.<br>
	 * The referenced rule is looked up in the context using the key.<br>
	 * The created rule will match according to the referenced rule.<br>
	 *
	 * @param key The key used to look up the referenced rule in the context
	 * @return The created reference token rule
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the key is empty
	 * @see ReferenceTokenRule
	 * @see ReferenceType#RULE
	 */
	public static @NotNull TokenRule referenceRule(@NotNull String key) {
		return new ReferenceTokenRule(key, ReferenceType.RULE);
	}
	
	/**
	 * Creates a reference token rule that references a list of tokens by its key in the context.<br>
	 * The rule will match if the next tokens in the stream match the referenced list of tokens.<br>
	 * The referenced tokens are looked up in the context using the key.<br>
	 *
	 * @param key The key used to look up the referenced tokens in the context
	 * @return The created reference token rule
	 * @throws NullPointerException If the key is null
	 * @throws IllegalArgumentException If the key is empty
	 * @see ReferenceTokenRule
	 * @see ReferenceType#TOKENS
	 */
	public static @NotNull TokenRule referenceTokens(@NotNull String key) {
		return new ReferenceTokenRule(key, ReferenceType.TOKENS);
	}
}
