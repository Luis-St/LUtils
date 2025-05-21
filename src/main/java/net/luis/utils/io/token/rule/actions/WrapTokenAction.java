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

import com.google.common.collect.Lists;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Token action that wraps the tokens with a prefix and suffix token.<br>
 * The prefix and suffix tokens are added to the beginning and end of the matched tokens, respectively.<br>
 *
 * @author Luis-St
 *
 * @param prefixToken The prefix token
 * @param suffixToken The suffix token
 */
public record WrapTokenAction(
	@NotNull Token prefixToken,
	@NotNull Token suffixToken
) implements TokenAction {
	
	/**
	 * Constructs a new wrap token action with the given prefix and suffix tokens.<br>
	 * @param prefixToken The prefix token
	 * @param suffixToken The suffix token
	 * @throws NullPointerException If the prefix or suffix token is null
	 */
	public WrapTokenAction {
		Objects.requireNonNull(prefixToken, "Prefix token must not be null");
		Objects.requireNonNull(suffixToken, "Suffix token must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match) {
		Objects.requireNonNull(match, "Token rule match must not be null");
		
		List<Token> tokens = match.matchedTokens();
		if (tokens.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<Token> result = Lists.newArrayListWithExpectedSize(tokens.size() + 2);
		result.add(this.prefixToken);
		result.addAll(tokens);
		result.add(this.suffixToken);
		return List.copyOf(result);
	}
}
