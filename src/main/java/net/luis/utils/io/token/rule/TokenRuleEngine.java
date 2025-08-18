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

package net.luis.utils.io.token.rule;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.TokenStream;
import net.luis.utils.io.token.rule.actions.TokenAction;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

/**
 * A rule engine for processing tokens based on defined rules.<br>
 * A rule can either validate or transform tokens.<br>
 * The engine applies the rules in the order they were added.<br>
 * If a rule matches, it replaces the matched tokens with the result of the action.<br>
 * If no rule matches, the engine moves to the next token.<br>
 *
 * @author Luis-St
 */
public class TokenRuleEngine {
	
	/**
	 * List of rule actions to be applied to the tokens.<br>
	 */
	private final List<RuleAction> ruleActions = Lists.newArrayList();
	
	/**
	 * Constructs a new token rule engine.<br>
	 * The engine starts with an empty list of rules.<br>
	 */
	public TokenRuleEngine() {}
	
	/**
	 * Adds a validation rule to the engine.<br>
	 * The rule is only used to validate tokens and does not modify them.<br>
	 *
	 * @param tokenRule The rule to be added
	 * @throws NullPointerException If the rule is null
	 */
	public void addRule(@NotNull TokenRule tokenRule) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		this.addRule(tokenRule, TokenAction.identity());
	}
	
	/**
	 * Adds a transformation rule to the engine.<br>
	 * The rule is used to transform tokens and can modify them.<br>
	 * The rule is applied to the tokens and the result is used to replace the matched tokens.<br>
	 *
	 * @param tokenRule The rule to be added
	 * @param action The action to be applied to the matched tokens
	 * @throws NullPointerException If the rule or action is null
	 */
	public void addRule(@NotNull TokenRule tokenRule, @NotNull TokenAction action) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		Objects.requireNonNull(action, "Token action must not be null");
		this.ruleActions.add(new RuleAction(tokenRule, action));
	}
	
	/**
	 * Processes the given list of tokens using the defined rules.<br>
	 * The rules are applied in the order they were added.<br>
	 * If a rule matches, the matched tokens are replaced with the result of the action.<br>
	 * If no rule matches, the engine moves to the next token.<br>
	 *
	 * @param tokens The list of tokens to be processed
	 * @return A new unmodifiable list of tokens after processing
	 * @throws NullPointerException If the list of tokens is null
	 */
	public @NotNull @Unmodifiable List<Token> process(@NotNull List<Token> tokens) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		
		List<Token> result = Lists.newArrayList(tokens);
		
		for (RuleAction ruleAction : this.ruleActions) {
			
			int index = 0;
			while (index < result.size()) {
				boolean matchFound = false;
				
				TokenRuleMatch match = ruleAction.tokenRule().match(new TokenStream(result, index));
				if (match != null && !match.matchedTokens().isEmpty()) {
					List<Token> processed = ruleAction.action().apply(match);
					
					result.subList(match.startIndex(), match.endIndex()).clear();
					result.addAll(match.startIndex(), processed);
					
					index = match.startIndex() + processed.size();
					matchFound = true;
				}
				
				if (!matchFound) {
					index++;
				}
			}
		}
		return List.copyOf(result);
	}
	
	//region Internal
	
	/**
	 * A record that holds a token rule and its associated action.<br>
	 *
	 * @author Luis-St
	 *
	 * @param tokenRule The token rule
	 * @param action The action to be applied to the matched tokens
	 */
	private record RuleAction(@NotNull TokenRule tokenRule, @NotNull TokenAction action) {
		
		/**
		 * Constructs a new rule action with the given token rule and action.<br>
		 *
		 * @param tokenRule The token rule
		 * @param action The action to be applied to the matched tokens
		 * @throws NullPointerException If the rule or action is null
		 */
		private RuleAction {
			Objects.requireNonNull(tokenRule, "Token rule must not be null");
			Objects.requireNonNull(action, "Token action must not be null");
		}
	}
	//endregion
}
