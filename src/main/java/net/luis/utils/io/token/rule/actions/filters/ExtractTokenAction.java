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

package net.luis.utils.io.token.rule.actions.filters;

import net.luis.utils.io.token.rule.TokenActionContext;
import net.luis.utils.io.token.rule.TokenRuleMatch;
import net.luis.utils.io.token.rule.actions.TokenAction;
import net.luis.utils.io.token.stream.TokenStream;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Token action that extracts (removes) tokens based on a predicate and processes them with a consumer.<br>
 * Tokens that match the predicate are removed from the result and passed to the consumer for processing.<br>
 * This is similar to {@link SkipTokenAction} but allows for additional processing of the extracted tokens.<br>
 *
 * @author Luis-St
 *
 * @param filter The predicate to identify tokens to extract
 * @param extractor The consumer to process extracted tokens
 */
public record ExtractTokenAction(
	@NotNull Predicate<Token> filter,
	@NotNull Consumer<Token> extractor
) implements TokenAction {
	
	/**
	 * Constructs a new extract token action with the given predicate and consumer.<br>
	 *
	 * @param filter The predicate to identify tokens to extract
	 * @param extractor The consumer to process extracted tokens
	 * @throws NullPointerException If the predicate or consumer is null
	 */
	public ExtractTokenAction {
		Objects.requireNonNull(filter, "Filter predicate must not be null");
		Objects.requireNonNull(extractor, "Extractor consumer must not be null");
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match, @NotNull TokenActionContext ctx) {
		Objects.requireNonNull(match, "Token rule match must not be null");
		Objects.requireNonNull(ctx, "Token action context must not be null");
		
		return match.matchedTokens().stream().filter(token -> {
			if (this.filter.test(token)) {
				this.extractor.accept(token);
				return false;
			}
			
			return true;
		}).toList();
	}
}
