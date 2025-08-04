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

import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.actions.transformers.TransformTokenAction;
import net.luis.utils.io.token.rule.actions.transformers.WrapTokenAction;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * Token action that is applied to a token rule match.<br>
 * With a token action, the tokens of the match can be modified, transformed or removed.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface TokenAction {
	
	/**
	 * Creates a token action that does nothing.<br>
	 * This action returns the matched tokens as they are.<br>
	 * The resulting list is an immutable copy of the original list.<br>
	 *
	 * @return The identity token action
	 * @apiNote This method is equivalent to {@code match -> List.copyOf(match.matchedTokens())}
	 */
	static @NotNull TokenAction identity() {
		return match -> List.copyOf(match.matchedTokens());
	}
	
	/**
	 * Applies this token action to the given token rule match.<br>
	 * The action can modify, transform or remove the tokens of the match.<br>
	 *
	 * @param match The token rule match to apply the action to
	 * @return The resulting immutable list of tokens after applying the action
	 * @throws NullPointerException If the match is null
	 */
	@NotNull @Unmodifiable
	List<Token> apply(@NotNull TokenRuleMatch match);
}
