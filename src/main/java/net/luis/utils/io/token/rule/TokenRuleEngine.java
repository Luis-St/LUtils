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
import net.luis.utils.io.token.rule.actions.TokenAction;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 *
 * @author Luis-St
 *
 */

public class TokenRuleEngine {
	
	private final List<RuleAction> ruleActions = Lists.newArrayList();
	
	public void addRule(@NotNull TokenRule tokenRule) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		this.addRule(tokenRule, TokenAction.identity());
	}
	
	public void addRule(@NotNull TokenRule tokenRule, @NotNull TokenAction action) {
		Objects.requireNonNull(tokenRule, "Token rule must not be null");
		Objects.requireNonNull(action, "Token action must not be null");
		this.ruleActions.add(new RuleAction(tokenRule, action));
	}
	
	public @NotNull List<Token> process(@NotNull List<Token> tokens) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		
		List<Token> result = Lists.newArrayList(tokens);
		int index = 0;
		while (index < result.size()) {
			boolean matchFound = false;
			
			for (RuleAction ruleAction : this.ruleActions) {
				TokenRuleMatch match = ruleAction.tokenRule().match(result, index);
				if (match != null && !match.matchedTokens().isEmpty()) {
					List<Token> processed = ruleAction.action().apply(match);
					
					result.subList(match.startIndex(), match.endIndex()).clear();
					result.addAll(match.startIndex(), processed);
					
					index = match.startIndex() + processed.size();
					matchFound = true;
					break;
				}
			}
			
			if (!matchFound) {
				index++;
			}
		}
		return result;
	}
	
	private record RuleAction(@NotNull TokenRule tokenRule, @NotNull TokenAction action) {
		
		private RuleAction {
			Objects.requireNonNull(tokenRule, "Token rule must not be null");
			Objects.requireNonNull(action, "Token action must not be null");
		}
	}
}
