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

package net.luis.utils.io.token.rule.actions.enhancers;

import com.google.common.collect.Lists;
import net.luis.utils.io.token.rule.TokenActionContext;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.actions.TokenAction;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.IndexedToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

/**
 * Token action that adds index information to tokens.<br>
 * Each token is wrapped in an {@link IndexedToken} with its position in the matched tokens list.<br>
 * The index starts from 0 for the first token in the match.<br>
 *
 * @author Luis-St
 *
 * @param startIndex The starting index for the first token
 */
public record IndexTokenAction(
	int startIndex
) implements TokenAction {
	
	/**
	 * Creates a new index token action with a starting index of 0.<br>
	 */
	public IndexTokenAction() {
		this(0);
	}
	
	/**
	 * Creates a new index token action with the given starting index.<br>
	 *
	 * @param startIndex The starting index for the first token
	 * @throws IllegalArgumentException If the starting index is negative
	 */
	public IndexTokenAction {
		if (startIndex < 0) {
			throw new IllegalArgumentException("Start index must be non-negative");
		}
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match, @NotNull TokenActionContext ctx) {
		Objects.requireNonNull(match, "Token rule match must not be null");
		Objects.requireNonNull(ctx, "Token action context must not be null");
		
		List<Token> matchedTokens = match.matchedTokens();
		List<Token> result = Lists.newArrayListWithExpectedSize(matchedTokens.size());
		for (int i = 0; i < matchedTokens.size(); i++) {
			Token token = matchedTokens.get(i);
			
			if (token instanceof IndexedToken existingIndexed) {
				result.add(existingIndexed);
			} else {
				result.add(new IndexedToken(token, this.startIndex + i));
			}
		}
		return List.copyOf(result);
	}
}
