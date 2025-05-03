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

package net.luis.utils.io.token.tokens;

import net.luis.utils.io.token.TokenPosition;
import net.luis.utils.io.token.definition.TokenDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 *
 * @author Luis-St
 *
 */

public record TokenGroup(
	@NotNull List<Token> tokens,
	@NotNull TokenDefinition definition
) implements Token {
	
	public TokenGroup {
		Objects.requireNonNull(tokens, "Token list must not be null");
		Objects.requireNonNull(definition, "Token definition must not be null");
		if (tokens.isEmpty()) {
			throw new IllegalArgumentException("Token list must not be empty");
		}
		if (tokens.size() == 1) {
			throw new IllegalArgumentException("Token list must not contain a single element");
		}
		for (Token token : tokens) {
			Objects.requireNonNull(token, "Token list must not be contain a null element");
		}
		tokens = List.copyOf(tokens);
	}
	
	@Override
	public @NotNull String value() {
		return this.tokens.stream().map(Token::value).collect(Collectors.joining());
	}
	
	@Override
	public @NotNull TokenPosition startPosition() {
		return this.tokens.getFirst().startPosition();
	}
	
	@Override
	public @NotNull TokenPosition endPosition() {
		return this.tokens.getLast().endPosition();
	}
}
