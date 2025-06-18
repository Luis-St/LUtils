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

package net.luis.utils.io.token.rule.actions;

import net.luis.utils.io.token.definition.TokenDefinition;
import net.luis.utils.io.token.rule.TokenRuleEngine;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.tokens.TokenGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Token action that groups the tokens into a single token group.<br>
 * The value of the group must match the definition of this action.<br>
 *
 * @see TokenAction
 * @see TokenGroup
 * @see TokenRuleEngine
 *
 * @author Luis-St
 *
 * @param definition The definition of the token group
 */
public record GroupingTokenAction(
	@NotNull TokenDefinition definition
) implements TokenAction {
	
	/**
	 * Constructs a new grouping token action with the given definition.<br>
	 *
	 * @param definition The definition of the token group
	 * @throws NullPointerException If the definition is null
	 */
	public GroupingTokenAction {
		Objects.requireNonNull(definition, "Definition must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match) {
		List<Token> tokens = match.matchedTokens();
		return Collections.singletonList(new TokenGroup(tokens, this.definition));
	}
}
