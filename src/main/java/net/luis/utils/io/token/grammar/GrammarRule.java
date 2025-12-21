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

import net.luis.utils.io.token.actions.TokenAction;
import net.luis.utils.io.token.rules.TokenRule;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a single rule within a grammar, combining a token matching rule with an action to perform on matched tokens.<br>
 * <p>
 *     A grammar rule encapsulates both the pattern matching logic (via {@link TokenRule}) and the transformation or
 *     processing logic (via {@link TokenAction}) that should be applied when the rule matches a sequence of tokens.
 * </p>
 *
 * @author Luis-St
 *
 * @param rule The token rule that defines the matching pattern
 * @param action The action to perform on tokens matched by this rule
 */
public record GrammarRule(
	@NonNull TokenRule rule,
	@NonNull TokenAction action
) {
	
	/**
	 * Constructs a new grammar rule with the specified token rule and action.<br>
	 *
	 * @param rule The token rule that defines the matching pattern
	 * @param action The action to perform on tokens matched by this rule
	 * @throws NullPointerException If either the rule or action is null
	 */
	public GrammarRule {
		Objects.requireNonNull(rule, "Token rule of a grammar rule must not be null");
		Objects.requireNonNull(action, "Token action of a grammar rule must not be null");
	}
}
