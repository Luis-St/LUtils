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

package net.luis.utils.io.token.parser;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.actions.GroupingTokenAction;
import net.luis.utils.io.token.actions.TokenAction;
import net.luis.utils.io.token.actions.core.GroupingMode;
import net.luis.utils.io.token.context.TokenRuleContext;
import net.luis.utils.io.token.rules.*;
import net.luis.utils.io.token.rules.combinators.AnyOfTokenRule;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Builder class for constructing {@link Parser} instances.<br>
 * <p>
 *     This builder provides methods to define named parser rules that produce labeled abstract syntax tree nodes.<br>
 *     Rules are maintained in a {@link TokenRuleContext} to support rule references and are processed in the order they are defined.
 * </p>
 *
 * @author Luis-St
 */
public class ParserBuilder {
	
	/**
	 * The context that holds all predefined rules for reference.
	 */
	private final TokenRuleContext context = TokenRuleContext.empty();
	/**
	 * The list of parser rules defined in this builder.
	 */
	private final List<ParserRule> rules = Lists.newArrayList();
	
	/**
	 * Package-private constructor for creating a new parser builder instance.<br>
	 * Use {@link Parser#builder(java.util.function.Consumer)} to create a parser.<br>
	 */
	ParserBuilder() {}
	
	/**
	 * Defines a new named rule in the parser context.<br>
	 * The rule can be referenced by other rules using its name.<br>
	 * <p>
	 *     The rule is not added to the list of emitting parser rules and will not produce an abstract syntax tree node by itself.<br>
	 *     This allows defining reusable or recursive rules that can be referenced multiple times.
	 * </p>
	 *
	 * @param name The name of the rule
	 * @param rule The token rule to define
	 * @throws NullPointerException If the name or rule is null
	 * @throws IllegalArgumentException If the name is empty or already defined
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
	 * Adds a new named parser rule that produces a labeled abstract syntax tree node.<br>
	 * The rule is paired with a {@link GroupingTokenAction} that wraps the matched tokens into a {@link net.luis.utils.io.token.tokens.TokenGroup} labeled with the rule name.<br>
	 *
	 * @param name The name of the rule, used as the label of the produced node
	 * @param rule The token rule to apply
	 * @throws NullPointerException If the name or rule is null
	 * @throws IllegalArgumentException If the name is empty
	 * @see #rule(String, TokenRule, TokenAction)
	 */
	public void rule(@NonNull String name, @NonNull TokenRule rule) {
		Objects.requireNonNull(name, "Rule name must not be null");
		Objects.requireNonNull(rule, "Rule must not be null");
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Rule name must not be empty");
		}
		
		this.rule(name, rule, new GroupingTokenAction(name, GroupingMode.MATCHED));
	}
	
	/**
	 * Adds a new named parser rule with the specified action.<br>
	 * <p>
	 *     If the action is a {@link GroupingTokenAction}, the rule is automatically wrapped if its structure requires it,<br>
	 *     so that grouping actions are applied correctly without requiring the user to manually wrap rules.<br>
	 *     The wrapping logic is equivalent to:
	 * </p>
	 * <pre>{@code
	 * TokenRules.any(
	 *     rule,
	 *     rule.group()
	 * );
	 * }</pre>
	 *
	 * @param name The name of the rule
	 * @param rule The token rule to apply
	 * @param action The action to perform on matched tokens
	 * @throws NullPointerException If the name, rule or action is null
	 * @throws IllegalArgumentException If the name is empty
	 */
	public void rule(@NonNull String name, @NonNull TokenRule rule, @NonNull TokenAction action) {
		Objects.requireNonNull(name, "Rule name must not be null");
		Objects.requireNonNull(rule, "Rule must not be null");
		Objects.requireNonNull(action, "Action must not be null");
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Rule name must not be empty");
		}
		
		this.rules.add(new ParserRule(name, this.wrapRule(rule, action), action));
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
	 * Builds and returns a new {@link Parser} instance with all defined rules.<br>
	 *
	 * @return A new parser containing all defined rules and their context
	 */
	public @NonNull Parser build() {
		return new Parser(this.context, this.rules);
	}
}
