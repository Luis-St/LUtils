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

import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.rules.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

/**
 * An enhanced recursive token rule that supports complex grammar patterns with true recursion.<br>
 * Unlike the simple opening-content-closing pattern, this rule allows for sophisticated grammar
 * definitions including alternatives, sequences, and complex recursive structures.<br>
 * <p>
 *     This implementation supports grammar rules such as:
 * </p>
 * <ul>
 *     <li>{@code AnnotationValue ::= PrimitiveValue | Identifier | AnnotationDeclaration | '{' AnnotationValue (',' AnnotationValue)* '}'}</li>
 *     <li>{@code Expression ::= Primary | '(' Expression ')' | Expression '+' Expression}</li>
 *     <li>{@code JsonValue ::= String | Number | Boolean | 'null' | JsonObject | JsonArray}</li>
 * </ul>
 * <p>
 *     The rule is defined using a factory function that receives the recursive rule itself as a parameter,
 *     allowing for complex compositions with other token rules like {@code AnyOfTokenRule},
 *     {@code SequenceTokenRule}, {@code RepeatedTokenRule}, etc.
 * </p>
 * <p>
 *     <strong>Example usage:</strong>
 * </p>
 * <pre>{@code
 * // Define: AnnotationValue ::= PrimitiveValue | Identifier | AnnotationDeclaration | '{' AnnotationValue (',' AnnotationValue)* '}'
 * // assuming that primitiveValueRule, identifierRule, and annotationDeclarationRule are predefined rules
 * RecursiveTokenRule annotationValue = new RecursiveTokenRule(self ->
 *     return TokenRules.any(
 *         primitiveValueRule,
 *         identifierRule,
 *         annotationDeclarationRule,
 *         TokenRules.sequence(
 *             TokenRules.literal("{"),
 *             TokenRules.sequence(
 *                 self,
 *                 TokenRules.sequence(
 *                     TokenRules.pattern(","),
 *                     self
 *                  ).repeatInfinitely()
 *             ),
 *             TokenRules.literal("}")
 *         )
 *     )
 * );
 * }</pre>
 *
 * @author Luis-St
 */
public class RecursiveTokenRule implements TokenRule {
	
	/**
	 * The actual rule that defines the recursive grammar pattern.<br>
	 * This rule is created by applying the rule factory function with this recursive rule as parameter.<br>
	 */
	private final TokenRule tokenRule;
	
	/**
	 * Constructs a new recursive token rule using the specified rule factory function.<br>
	 * The factory function receives the recursive rule itself as a parameter, enabling
	 * complex recursive grammar definitions.<br>
	 * <p>
	 *     This constructor provides maximum flexibility for defining recursive patterns.
	 *     The rule factory function is called with the recursive rule instance, allowing
	 *     the creation of sophisticated grammar structures that can reference themselves.
	 * </p>
	 *
	 * @param ruleFactory A function that takes the recursive rule and returns the complete rule definition
	 * @throws NullPointerException If the rule factory is null or if the factory returns null
	 */
	public RecursiveTokenRule(@NotNull Function<TokenRule, TokenRule> ruleFactory) {
		Objects.requireNonNull(ruleFactory, "Rule factory must not be null");
		this.tokenRule = Objects.requireNonNull(ruleFactory.apply(this), "Rule factory must return a non-null rule");
	}
	
	/**
	 * Convenience constructor for backward compatibility with simple opening-content-closing patterns.<br>
	 * This constructor creates a recursive rule that matches an opening rule, followed by content
	 * that can include recursive instances, and finally a closing rule.<br>
	 * <p>
	 *     This is equivalent to creating a rule factory that returns:
	 *     {@code TokenRules.sequence(openingRule, contentRule, closingRule)}
	 * </p>
	 *
	 * @param openingRule The rule that must match at the beginning
	 * @param contentRule The rule for content between opening and closing (may reference recursion)
	 * @param closingRule The rule that must match at the end
	 * @throws NullPointerException If the opening, content, or closing rule is null
	 */
	public RecursiveTokenRule(@NotNull TokenRule openingRule, @NotNull TokenRule contentRule, @NotNull TokenRule closingRule) {
		this(self -> TokenRules.sequence(
			Objects.requireNonNull(openingRule, "Opening rule must not be null"),
			Objects.requireNonNull(contentRule, "Content rule must not be null"),
			Objects.requireNonNull(closingRule, "Closing rule must not be null")
		));
	}
	
	/**
	 * Convenience constructor for simple opening-closing patterns with flexible content.<br>
	 * The content rule factory receives the recursive rule itself as a parameter.<br>
	 *
	 * @param openingRule The rule that must match at the beginning
	 * @param closingRule The rule that must match at the end
	 * @param contentRuleFactory A function that takes the recursive rule and returns the content rule
	 * @throws NullPointerException If the opening, closing rule, or content rule factory is null
	 */
	public RecursiveTokenRule(@NotNull TokenRule openingRule, @NotNull TokenRule closingRule, @NotNull Function<TokenRule, TokenRule> contentRuleFactory) {
		this(self -> TokenRules.sequence(
			Objects.requireNonNull(openingRule, "Opening rule must not be null"),
			Objects.requireNonNull(contentRuleFactory, "Content rule factory must not be null").apply(self),
			Objects.requireNonNull(closingRule, "Closing rule must not be null")
		));
	}
	
	/**
	 * Returns the actual rule that defines this recursive pattern.<br>
	 * This rule is the result of applying the factory function with this recursive rule as parameter.<br>
	 *
	 * @return The actual rule defining the recursive pattern
	 */
	public @NotNull TokenRule getTokenRule() {
		return this.tokenRule;
	}
	
	@Override
	public @Nullable TokenRuleMatch match(@NotNull TokenStream stream, @NotNull TokenRuleContext ctx) {
		Objects.requireNonNull(stream, "Token stream cannot be null");
		Objects.requireNonNull(ctx, "Token rule context cannot be null");
		return this.tokenRule.match(stream, ctx);
	}
	
	@Override
	public @NotNull TokenRule not() {
		return new RecursiveTokenRule(self -> this.tokenRule.not()) {
			@Override
			public @NotNull TokenRule getTokenRule() {
				return RecursiveTokenRule.this; // Negating the not() method returns the original rule, preventing double negation and nesting of classes
			}
		};
	}
	
	//region Object overrides
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RecursiveTokenRule that)) return false;
		return this.tokenRule.equals(that.tokenRule);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(this.tokenRule);
	}
	
	@Override
	public String toString() {
		return "RecursiveTokenRule[tokenRule=" + this.tokenRule + "]";
	}
	//endregion
}
