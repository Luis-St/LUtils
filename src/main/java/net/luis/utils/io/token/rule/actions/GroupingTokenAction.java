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

import net.luis.utils.io.token.rule.TokenRuleEngine;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import net.luis.utils.io.token.tokens.TokenGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.List;

/**
 * Token action that groups the tokens into a single token group.<br>
 * This class is implemented as a singleton and can be accessed via {@link #INSTANCE}.<br>
 *
 * @see TokenAction
 * @see TokenGroup
 * @see TokenRuleEngine
 *
 * @author Luis-St
 */
public final class GroupingTokenAction implements TokenAction {
	
	/**
	 * The singleton instance of this class.<br>
	 */
	public static final GroupingTokenAction INSTANCE = new GroupingTokenAction();
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 */
	private GroupingTokenAction() {}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match) {
		List<Token> tokens = match.matchedTokens();
		return Collections.singletonList(new TokenGroup(tokens));
	}
}
