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

package net.luis.utils.io.token.actions;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.actions.core.GroupingMode;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.tokens.TokenGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Token action that groups the tokens into a single token group.<br>
 * The grouping behavior depends on the specified {@link GroupingMode}.<br>
 *
 * @author Luis-St
 *
 * @param mode The grouping mode to use
 */
public record GroupingTokenAction(
	@NotNull GroupingMode mode
) implements TokenAction {
	
	/**
	 * Constructs a new grouping token action with the given mode.<br>
	 *
	 * @param mode The grouping mode to use
	 * @throws NullPointerException If the mode is null
	 */
	public GroupingTokenAction {
		Objects.requireNonNull(mode, "Grouping mode must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match, @NotNull TokenActionContext ctx) {
		Objects.requireNonNull(match, "Token rule match must not be null");
		Objects.requireNonNull(ctx, "Token action context must not be null");
		
		List<Token> tokens = switch (this.mode) {
			case MATCHED -> match.matchedTokens();
			case ALL -> {
				List<Token> allTokens = ctx.stream().getAllTokens();
				yield allTokens.subList(match.startIndex(), match.endIndex());
			}
		};
		
		if (1 >= tokens.size()) {
			throw new IllegalArgumentException("Cannot group less than 2 tokens: " + match.matchedTokens() + ", rule=" + match.matchingTokenRule());
		}
		return Collections.singletonList(new TokenGroup(tokens));
	}
}
