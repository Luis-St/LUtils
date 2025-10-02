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

package net.luis.utils.io.token.grammar;

import com.google.common.collect.Maps;
import net.luis.utils.io.token.actions.GroupingTokenAction;
import net.luis.utils.io.token.actions.TokenAction;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.*;
import net.luis.utils.io.token.rules.combinators.AnyOfTokenRule;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
	
	private final TokenRuleContext context = TokenRuleContext.empty();
	private final LinkedHashMap<String, GrammarRule> rules = Maps.newLinkedHashMap();
	
	/**
	 * Package-private constructor for creating a new grammar builder instance.<br>
	 * Use {@link Grammar#builder(java.util.function.Consumer)} to create a grammar.<br>
	 */
	GrammarBuilder() {}
	
	/**
	 * Defines a new grammar rule with the specified name and token rule.<br>
	 * This is a convenience method that uses the identity action, which returns the input tokens unchanged.<br>
	 *
	 * @param name The unique name for this rule
	 * @param rule The token rule to apply
	 * @throws NullPointerException If name or rule is null
	 * @throws IllegalArgumentException If name is empty or already defined
	 */
	public void define(@NotNull String name, @NotNull TokenRule rule) {
		this.define(name, rule, TokenAction.identity());
	}
	
	/**
	 * Defines a new grammar rule with the specified name, token rule, and action.
	 * <p>
	 *     The rule is automatically wrapped if the action is a {@link GroupingTokenAction} and the rule structure requires it.<br>
	 *     The rule is registered in the context to allow references from other rules.
	 * </p>
	 *
	 * @param name The unique name for this rule
	 * @param rule The token rule to apply
	 * @param action The action to perform on matched tokens
	 * @throws NullPointerException If name or rule is null
	 * @throws IllegalArgumentException If name is empty or already defined
	 */
	public void define(@NotNull String name, @NotNull TokenRule rule, @NotNull TokenAction action) {
		Objects.requireNonNull(name, "Rule name must not be null");
		Objects.requireNonNull(rule, "Rule must not be null");
		
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Rule name must not be empty");
		}
		if (this.context.getRuleReference(name) != null) {
			throw new IllegalArgumentException("Rule with name '" + name + "' is already defined");
		}
		
		rule = this.wrapRule(rule, action);
		this.rules.put(name, new GrammarRule(rule, action));
		this.context.defineRule(name, rule);
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
	private @NotNull TokenRule wrapRule(@NotNull TokenRule rule, @NotNull TokenAction action) {
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
	public @NotNull Grammar build() {
		return new Grammar(this.context, this.rules);
	}
}
