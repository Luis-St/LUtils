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

import net.luis.utils.io.token.rule.actions.TokenAction;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class RuleEngine {
	
	private final List<RuleAction> ruleActions;
	
	public RuleEngine() {
		this.ruleActions = new ArrayList<>();
	}
	
	public void addRuleAction(@NotNull Rule rule, @NotNull TokenAction action) {
		Objects.requireNonNull(rule, "Rule must not be null");
		Objects.requireNonNull(action, "Action must not be null");
		this.ruleActions.add(new RuleAction(rule, action));
	}
	
	public @NotNull List<String> process(@NotNull List<String> tokens) {
		Objects.requireNonNull(tokens, "Tokens must not be null");
		
		List<String> result = new ArrayList<>(tokens);
		int index = 0;
		while (index < result.size()) {
			boolean matchFound = false;
			
			for (RuleAction ruleAction : this.ruleActions) {
				Match match = ruleAction.rule().match(result, index);
				if (match != null) {
					// Apply the action
					List<String> processed = ruleAction.action().apply(match);
					
					// Replace the matched tokens with the processed tokens
					result.subList(match.startIndex(), match.endIndex()).clear();
					result.addAll(match.startIndex(), processed);
					
					// Adjust index based on change in size
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
	
	private record RuleAction(@NotNull Rule rule, @NotNull TokenAction action) {
		
		private RuleAction {
			Objects.requireNonNull(rule, "Rule must not be null");
			Objects.requireNonNull(action, "Action must not be null");
		}
	}
}
