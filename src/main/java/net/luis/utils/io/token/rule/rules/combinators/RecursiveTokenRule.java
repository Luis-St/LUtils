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

package net.luis.utils.io.token.rule.rules.combinators;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

/**
 * A token rule that supports true recursive matching patterns.<br>
 * This rule matches a configurable opening rule, followed by content that can include recursive instances,<br>
 * and finally, a configurable closing rule.<br>
 * <p>
 *     The key feature of this implementation is that the content rule can reference the recursive rule itself,<br>
 *     allowing for true recursive grammar definitions. This enables matching of nested structures such as:
 * </p>
 *
 * <ul>
 *     <li>Nested parentheses: {@code (), (content), (()), (a(b)c)}</li>
 *     <li>Nested brackets: {@code [], [content], [[]], [a[b]c]}</li>
 *     <li>Mathematical expressions: {@code (5), (5+3), ((2*3)+4)}</li>
 *     <li>JSON-like structures: {@code {}, {key:value}, {{nested}}}</li>
 * </ul>
 * <p>
 *     The recursive rule works by:
 * </p>
 *
 * <ol>
 *     <li>Matching the opening rule at the current position</li>
 *     <li>Applying the content rule (which may include recursive references)</li>
 *     <li>Matching the closing rule to complete the structure</li>
 * </ol>
 *
 * @author Luis-St
 */
public class RecursiveTokenRule implements TokenRule {
	
	/**
	 * The rule that must match at the beginning of the recursive structure.<br>
	 */
	private final TokenRule openingRule;
	/**
	 * The rule that defines the content between opening and closing rules.<br>
	 * This rule may include references to the recursive rule itself.<br>
	 */
	private final TokenRule contentRule;
	/**
	 * The rule that must match at the end of the recursive structure.<br>
	 */
	private final TokenRule closingRule;
	
	/**
	 * Constructs a new recursive token rule with the specified opening, content, and closing rules.<br>
	 * This is a convenience constructor for simple recursive rules where the content is just a choice between<br>
	 * base content and recursive instances.<br>
	 * <p>
	 *     The content rule is automatically enhanced to include recursive references by creating a rule that<br>
	 *     can match either the provided base content or recursive instances of this rule, repeated infinitely.
	 * </p>
	 *
	 * @param openingRule The rule that must match at the beginning of the recursive structure
	 * @param contentRule The rule for matching non-recursive content between opening and closing
	 * @param closingRule The rule that must match at the end of the recursive structure
	 * @throws NullPointerException If any of the rules is null
	 */
	public RecursiveTokenRule(@NotNull TokenRule openingRule, @NotNull TokenRule contentRule, @NotNull TokenRule closingRule) {
		this(openingRule, closingRule, (Function<TokenRule, TokenRule>) self -> contentRule);
	}
	
	/**
	 * Constructs a new recursive token rule with the specified opening, closing rules and content rule factory.<br>
	 * The content rule factory receives the recursive rule itself as a parameter, allowing it to include<br>
	 * recursive references in the content definition.<br>
	 * <p>
	 *     This constructor provides full control over how the recursive structure is defined. The content rule factory<br>
	 *     function is called with the recursive rule instance, enabling complex recursive patterns to be constructed.
	 * </p>
	 *
	 * @param openingRule The rule that must match at the beginning of the recursive structure
	 * @param closingRule The rule that must match at the end of the recursive structure
	 * @param contentRuleFactory A function that takes the recursive rule and returns the content rule
	 * @throws NullPointerException If any of the parameters is null
	 * @throws NullPointerException If the content rule factory returns null
	 */
	public RecursiveTokenRule(@NotNull TokenRule openingRule, @NotNull TokenRule closingRule, @NotNull Function<TokenRule, TokenRule> contentRuleFactory) {
		this.openingRule = Objects.requireNonNull(openingRule, "Opening rule must not be null");
		this.closingRule = Objects.requireNonNull(closingRule, "Closing rule must not be null");
		Objects.requireNonNull(contentRuleFactory, "Content rule factory must not be null");
		
		this.contentRule = Objects.requireNonNull(contentRuleFactory.apply(this), "Content rule must not be null");
	}
	
	/**
	 * Returns the opening rule used by this recursive token rule.<br>
	 * The opening rule defines what tokens mark the beginning of a recursive structure.<br>
	 *
	 * @return The opening rule
	 */
	public @NotNull TokenRule openingRule() {
		return this.openingRule;
	}
	
	/**
	 * Returns the content rule used by this recursive token rule.<br>
	 * The content rule defines what can appear between the opening and closing rules.<br>
	 *
	 * @return The content rule
	 * @apiNote This content rule may include references to this recursive rule itself
	 */
	public @NotNull TokenRule contentRule() {
		return this.contentRule;
	}
	
	/**
	 * Returns the closing rule used by this recursive token rule.<br>
	 * The closing rule defines what tokens mark the end of a recursive structure.<br>
	 *
	 * @return The closing rule
	 */
	public @NotNull TokenRule closingRule() {
		return this.closingRule;
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream) {
		Objects.requireNonNull(stream, "Token stream cannot be null");
		TokenStream workingStream = stream.copyWithCurrentIndex();
		
		TokenRuleMatch openingMatch = this.openingRule.match(workingStream);
		if (openingMatch == null) {
			return null;
		}
		
		TokenRuleMatch contentMatch = this.contentRule.match(workingStream);
		
		TokenRuleMatch closingMatch = this.closingRule.match(workingStream);
		if (closingMatch == null) {
			return null;
		}
		
		int endIndex = workingStream.getCurrentIndex();
		int totalLength = endIndex - openingMatch.startIndex();
		for (int i = 0; i < totalLength; i++) {
			stream.consumeToken();
		}
		return this.mergeMatches(stream, openingMatch.startIndex(), totalLength);
	}
	
	/**
	 * Merges individual token rule matches into a single combined match result.<br>
	 * This method extracts the matched tokens from the original stream and creates a new<br>
	 * {@link TokenRuleMatch} that represents the entire recursive structure.<br>
	 *
	 * @param originalStream The original stream at the position where matching started
	 * @param startIndex The starting index of the overall match
	 * @param totalLength The total number of tokens consumed by all individual matches
	 * @return A combined token rule match representing the entire recursive structure
	 * @throws NullPointerException If the original stream is null
	 */
	private @NotNull TokenRuleMatch mergeMatches(@NotNull TokenStream originalStream, int startIndex, int totalLength) {
		List<Token> matchedTokens = Lists.newArrayList();
		TokenStream extractStream = originalStream.copy(startIndex);
		
		for (int i = 0; i < totalLength && extractStream.hasToken(); i++) {
			matchedTokens.add(extractStream.readToken());
		}
		
		int endIndex = startIndex + totalLength;
		return new TokenRuleMatch(startIndex, endIndex, matchedTokens, this);
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RecursiveTokenRule that)) return false;
		
		if (!this.openingRule.equals(that.openingRule)) return false;
		if (!this.contentRule.equals(that.contentRule)) return false;
		return this.closingRule.equals(that.closingRule);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.openingRule, this.contentRule, this.closingRule);
	}
	
	@Override
	public String toString() {
		return "RecursiveTokenRule[openingRule=" + this.openingRule + ",contentRule=" + this.contentRule + ",closingRule=" + this.closingRule + "]";
	}
	//endregion
}
