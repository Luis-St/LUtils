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

package net.luis.utils.io.token.actions.filters;

import net.luis.utils.io.token.TokenRuleMatch;
import net.luis.utils.io.token.actions.TokenAction;
import net.luis.utils.io.token.context.TokenActionContext;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Token action that skips (removes) tokens based on a predicate.<br>
 * Tokens that match the predicate are removed from the result.<br>
 * This is the inverse of {@link FilterTokenAction}.<br>
 *
 * @author Luis-St
 *
 * @param filter The predicate to identify tokens to skip
 */
public record SkipTokenAction(
	@NonNull Predicate<Token> filter
) implements TokenAction {
	
	/**
	 * Constructs a new skip token action with the given predicate.<br>
	 *
	 * @param filter The predicate to identify tokens to skip
	 * @throws NullPointerException If the predicate is null
	 */
	public SkipTokenAction {
		Objects.requireNonNull(filter, "Filter predicate must not be null");
	}
	
	@Override
	public @NonNull @Unmodifiable List<Token> apply(@NonNull TokenRuleMatch match, @NonNull TokenActionContext ctx) {
		Objects.requireNonNull(match, "Token rule match must not be null");
		Objects.requireNonNull(ctx, "Token action context must not be null");
		
		return match.matchedTokens().stream().filter(this.filter.negate()).toList();
	}
}
