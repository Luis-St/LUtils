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

import net.luis.utils.io.token.actions.TokenAction;
import net.luis.utils.io.token.rules.TokenRule;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a single rule within a parser, combining a named token matching rule with an action to perform on matched tokens.<br>
 * <p>
 *     A parser rule encapsulates the pattern matching logic (via {@link TokenRule}) and the transformation logic (via {@link TokenAction})
 *     that should be applied when the rule matches a sequence of tokens.<br>
 *     The name is the label of the abstract syntax tree node the rule produces.
 * </p>
 *
 * @author Luis-St
 *
 * @param name The name of the rule, used as the label of the produced abstract syntax tree node
 * @param rule The token rule that defines the matching pattern
 * @param action The action to perform on tokens matched by this rule
 */
public record ParserRule(
	@NonNull String name,
	@NonNull TokenRule rule,
	@NonNull TokenAction action
) {
	
	/**
	 * Constructs a new parser rule with the specified name, token rule and action.<br>
	 *
	 * @param name The name of the rule
	 * @param rule The token rule that defines the matching pattern
	 * @param action The action to perform on tokens matched by this rule
	 * @throws NullPointerException If the name, rule or action is null
	 * @throws IllegalArgumentException If the name is empty
	 */
	public ParserRule {
		Objects.requireNonNull(name, "Name of a parser rule must not be null");
		Objects.requireNonNull(rule, "Token rule of a parser rule must not be null");
		Objects.requireNonNull(action, "Token action of a parser rule must not be null");
		
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Name of a parser rule must not be empty");
		}
	}
}
