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
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;

/**
 * Token action that transforms the tokens using a transformer.<br>
 * The transformer is applied to the tokens, and the result is returned as an immutable list of tokens.<br>
 *
 * @see TokenTransformer
 *
 * @author Luis-St
 *
 * @param transformer The transformer to apply to tokens
 */
public record TransformTokenAction(
	@NotNull TokenTransformer transformer
) implements TokenAction {
	
	/**
	 * Constructs a new transform token action with the given transformer.<br>
	 * @param transformer The transformer to apply to tokens
	 * @throws NullPointerException If the transformer is null
	 */
	public TransformTokenAction {
		Objects.requireNonNull(transformer, "Transformer must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match) {
		Objects.requireNonNull(match, "Token rule match must not be null");
		return List.copyOf(this.transformer.transform(match.matchedTokens()));
	}
}
