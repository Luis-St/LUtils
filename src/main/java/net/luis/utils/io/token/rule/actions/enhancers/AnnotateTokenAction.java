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
import net.luis.utils.io.token.tokens.AnnotatedToken;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

/**
 * Token action that annotates tokens with metadata.<br>
 * Each token is wrapped in an {@link AnnotatedToken} with the provided metadata.<br>
 * If a token is already annotated, the metadata is merged (new metadata takes precedence).<br>
 *
 * @author Luis-St
 *
 * @param metadata The metadata to add to tokens
 */
public record AnnotateTokenAction(
	@NotNull Map<String, Object> metadata
) implements TokenAction {
	
	/**
	 * Constructs a new annotate token action with the given metadata.<br>
	 *
	 * @param metadata The metadata to add to tokens
	 * @throws NullPointerException If the metadata map is null
	 */
	public AnnotateTokenAction {
		Objects.requireNonNull(metadata, "Metadata map must not be null");
		metadata = Map.copyOf(metadata);
	}
	
	@Override
	public @NotNull @Unmodifiable List<Token> apply(@NotNull TokenRuleMatch match, @NotNull TokenActionContext ctx) {
		Objects.requireNonNull(match, "Token rule match must not be null");
		Objects.requireNonNull(ctx, "Token action context must not be null");
		
		List<Token> result = Lists.newArrayListWithExpectedSize(match.matchedTokens().size());
		for (Token token : match.matchedTokens()) {
			if (token instanceof AnnotatedToken existingAnnotated) {
				Map<String, Object> mergedMetadata = new HashMap<>(existingAnnotated.metadata());
				mergedMetadata.putAll(this.metadata);
				result.add(new AnnotatedToken(existingAnnotated.token(), mergedMetadata));
			} else {
				result.add(new AnnotatedToken(token, this.metadata));
			}
		}
		return List.copyOf(result);
	}
}
