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

package net.luis.utils.io.token.grammar;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.actions.GroupingTokenAction;
import net.luis.utils.io.token.actions.TokenAction;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.*;
import net.luis.utils.io.token.rules.combinators.AnyOfTokenRule;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Builder class for constructing {@link Grammar} instances.<br>
 * <p>
 *     This builder provides methods to define named grammar rules with associated actions.<br>
 *     Rules are maintained in a {@link TokenRuleContext} to support rule references and are processed in the order they are defined.
 * </p>
 *
 * @author Luis-St
 */
public class GrammarBuilder {
	
	/**
	 * The context that holds all predefined rules for reference.<br>
	 */
	private final TokenRuleContext context = TokenRuleContext.empty();
	/**
	 * The list of grammar rules defined in this builder.<br>
	 */
	private final List<GrammarRule> rules = Lists.newArrayList();
	
	/**
	 * Package-private constructor for creating a new grammar builder instance.<br>
	 * Use {@link Grammar#builder(java.util.function.Consumer)} to create a grammar.<br>
	 */
	GrammarBuilder() {}
	
	/**
	 * Defines a new named rule in the grammar context.<br>
	 * The rule can be referenced by other rules using its name.<br>
	 * <p>
	 *     The rule is not added to the list of grammar rules of this builder and will not be processed unless added separately using {@link #addRule(TokenRule)} or {@link #addRule(TokenRule, TokenAction)}.<br>
	 *     This allows defining reusable rules that can be referenced multiple times without being created multiple times.
	 * </p>
	 *
	 * @param name The name of the rule
	 * @param rule The token rule to define
	 * @throws NullPointerException If name or rule is null
	 * @throws IllegalArgumentException If name is empty or already defined
	 * @see TokenRuleContext#defineRule(String, TokenRule)
	 */
	public void defineRule(@NonNull String name, @NonNull TokenRule rule) {
		Objects.requireNonNull(name, "Rule name must not be null");
		Objects.requireNonNull(rule, "Rule must not be null");
		
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Rule name must not be empty");
		}
		if (this.context.getRuleReference(name) != null) {
			throw new IllegalArgumentException("Rule with name '" + name + "' is already defined");
		}
		
		this.context.defineRule(name, rule);
	}
	
	/**
	 * Adds a new rule with the identity action to the grammar.<br>
	 * The rule is added to the list of grammar rules of this builder but not defined in the context for references.<br>
	 *
	 * @param rule The token rule to apply
	 * @throws NullPointerException If the rule is null
	 * @see #addRule(TokenRule, TokenAction)
	 */
	public void addRule(@NonNull TokenRule rule) {
		this.addRule(rule, TokenAction.identity());
	}
	
	/**
	 * Adds a new rule with the identity action to the grammar.<br>
	 * The rule is added to the list of grammar rules of this builder but not defined in the context for references.<br>
	 *
	 * @param rule The token rule to apply
	 * @param wrap Whether to automatically wrap the rule if needed for grouping actions
	 * @throws NullPointerException If the rule is null
	 * @see #addRule(TokenRule, TokenAction, boolean)
	 */
	public void addRule(@NonNull TokenRule rule, boolean wrap) {
		this.addRule(rule, TokenAction.identity(), wrap);
	}
	
	/**
	 * Adds a new rule with the specified action to the grammar.<br>
	 * The rule is added to the list of grammar rules of this builder but not defined in the context for references.<br>
	 * <p>
	 *     The rule is automatically wrapped if the action is a {@link GroupingTokenAction} and the rule structure requires it.<br>
	 *     This ensures that grouping actions are applied correctly without requiring the user to manually wrap rules.<br>
	 *     The wrapping logic is equivalent to:
	 * </p>
	 * <pre>{@code
	 * TokenRules.any(
	 *     rule,
	 *     rule.group()
	 * );
	 * }</pre>
	 *
	 * @param rule The token rule to apply
	 * @param action The action to perform on matched tokens
	 * @throws NullPointerException If name or rule is null
	 * @throws IllegalArgumentException If name is empty or already defined
	 */
	public void addRule(@NonNull TokenRule rule, @NonNull TokenAction action) {
		this.addRule(rule, action, true);
	}
	
	/**
	 * Adds a new rule with the specified action to the grammar.<br>
	 * The rule is added to the list of grammar rules of this builder but not defined in the context for references.<br>
	 * <p>
	 *     If wrap is true, the rule is automatically wrapped if the action is a {@link GroupingTokenAction} and the rule structure requires it.<br>
	 *     This ensures that grouping actions are applied correctly without requiring the user to manually wrap rules.<br>
	 *     The wrapping logic is equivalent to:
	 * </p>
	 * <pre>{@code
	 * TokenRules.any(
	 *     rule,
	 *     rule.group()
	 * );
	 * }</pre>
	 *
	 * @param rule The token rule to apply
	 * @param action The action to perform on matched tokens
	 * @param wrap Whether to automatically wrap the rule if needed for grouping actions
	 * @throws NullPointerException If name or rule is null
	 * @throws IllegalArgumentException If name is empty or already defined
	 */
	public void addRule(@NonNull TokenRule rule, @NonNull TokenAction action, boolean wrap) {
		Objects.requireNonNull(rule, "Rule must not be null");
		Objects.requireNonNull(action, "Action must not be null");
		
		this.rules.add(new GrammarRule(wrap ? this.wrapRule(rule, action) : rule, action));
	}
	
	/**
	 * Wraps the given rule if necessary based on the action type.<br>
	 * <p>
	 *     If the action is a {@link GroupingTokenAction}, this method checks if the rule needs to be wrapped with a grouping variant.<br>
	 *     Rules that are already properly structured for grouping are returned unchanged.
	 * </p>
	 *
	 * @param rule The token rule to potentially wrap
	 * @param action The action associated with the rule
	 * @return The wrapped or original rule
	 */
	private @NonNull TokenRule wrapRule(@NonNull TokenRule rule, @NonNull TokenAction action) {
		if (action instanceof GroupingTokenAction) {
			if (!(rule instanceof AnyOfTokenRule(List<TokenRule> tokenRules))) {
				return TokenRules.any(
					rule,
					rule.group()
				);
			}
			
			if (tokenRules.size() != 2) {
				return TokenRules.any(
					rule,
					rule.group()
				);
			}
			
			TokenRule first = tokenRules.get(0);
			TokenRule second = tokenRules.get(1);
			if (first instanceof TokenGroupRule == second instanceof TokenGroupRule) {
				return TokenRules.any(
					rule,
					rule.group()
				);
			}
			return rule;
		}
		return rule;
	}
	
	/**
	 * Builds and returns a new {@link Grammar} instance with all defined rules.<br>
	 * @return A new Grammar containing all defined rules and their context
	 */
	public @NonNull Grammar build() {
		return new Grammar(this.context, this.rules);
	}
}
