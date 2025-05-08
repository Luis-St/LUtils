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
 * Token implementation for a group of tokens.<br>
 * A token group is a sequence of tokens that have been grouped together and match the token definition.<br>
 * <p>
 *     The token group must contain at least two tokens.<br>
 *     The tokens must be continuous; this means that no token can be removed in the middle of the group.<br>
 *     The group is continuous if the distance between the start position of the first token and the end position of the last token<br>
 *     is equal to the length of the group value minus one (inclusive).<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param tokens The list of tokens in the group
 * @param definition The token definition
 */
public record TokenGroup(
	@NotNull List<Token> tokens,
	@NotNull TokenDefinition definition
) implements Token {
	
	/**
	 * Constructs a new token group for a list of tokens.<br>
	 * @param tokens The list of tokens in the group
	 * @param definition The token definition
	 * @throws NullPointerException If the list of tokens, the token definition or any of the tokens are null
	 * @throws IllegalArgumentException If the list of tokens is empty, contains a single element,
	 * does not match the token definition or the start and end positions do not match the length of the token group
	 */
	public TokenGroup {
		Objects.requireNonNull(tokens, "Token list must not be null");
		Objects.requireNonNull(definition, "Token definition must not be null");
		for (Token token : tokens) {
			Objects.requireNonNull(token, "Token list must not be contain a null element");
		}
		if (tokens.isEmpty()) {
			throw new IllegalArgumentException("Token list must not be empty");
		}
		if (tokens.size() == 1) {
			throw new IllegalArgumentException("Token list must not contain a single element");
		}
		String value = tokens.stream().map(Token::value).collect(Collectors.joining());
		if (!definition.matches(value)) {
			throw new IllegalArgumentException("Tokens " + tokens + " of group does not match the defined token definition " + definition);
		}
		tokens = List.copyOf(tokens);
	}
	
	/**
	 * Returns the value of the token group.<br>
	 * The value is the concatenation of all token values in the group.<br>
	 * @return The concatenated values
	 */
	@Override
	public @NotNull String value() {
		return this.tokens.stream().map(Token::value).collect(Collectors.joining());
	}
	
	/**
	 * Returns the start position of the token group.<br>
	 * The start position is the start position of the first token in the group.<br>
	 * @return The start position
	 */
	@Override
	public @NotNull TokenPosition startPosition() {
		return this.tokens.getFirst().startPosition();
	}
	
	/**
	 * Returns the end position of the token group.<br>
	 * The end position is the end position of the last token in the group.<br>
	 * @return The end position
	 */
	@Override
	public @NotNull TokenPosition endPosition() {
		return this.tokens.getLast().endPosition();
	}
}
