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
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.tokens.TokenGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 *
 * @author Luis-St
 *
 */

public class GroupingTokenAction implements TokenAction {
	
	private final TokenDefinition definition;
	
	public GroupingTokenAction(@NotNull TokenDefinition definition) {
		Objects.requireNonNull(definition, "Definition must not be null");
		this.definition = definition;
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match) {
		List<Token> tokens = match.matchedTokens();
		TokenGroup group = new TokenGroup(tokens, this.definition);
		if (!this.definition.matches(group.value())) {
			throw new IllegalStateException("Tokens " + tokens + " of group does not match the defined token definition " + this.definition);
		}
		return Collections.singletonList(group);
	}
}
