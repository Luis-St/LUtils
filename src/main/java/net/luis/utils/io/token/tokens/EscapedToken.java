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

import java.util.Objects;

/**
 * Special token implementation for escaped tokens.<br>
 * An escaped token is a token that starts with a backslash and has a length of 2.<br>
 *
 * @author Luis-St
 *
 * @param definition The token definition
 * @param value The string value of the token
 * @param startPosition The start position of the token
 * @param endPosition The end position of the token
 */
public record EscapedToken(
	@NotNull TokenDefinition definition,
	@NotNull String value,
	@NotNull TokenPosition startPosition,
	@NotNull TokenPosition endPosition
) implements Token {
	
	/**
	 * Constructs a new escaped token for a string value.<br>
	 *
	 * @param definition The token definition
	 * @param value The string value of the token
	 * @param startPosition The start position of the token
	 * @param endPosition The end position of the token
	 * @throws NullPointerException If any of the parameters are null
	 * @throws IllegalArgumentException If the token value does not have a length of 2, does not start with a backslash or does not match the token definition.
	 * If the start and end positions are positioned, the distance between them must be equal to the length of the token value minus 1 (inclusive).
	 */
	public EscapedToken {
		Objects.requireNonNull(definition, "Token definition must not be null");
		Objects.requireNonNull(value, "Token value must not be null");
		Objects.requireNonNull(startPosition, "Token start position must not be null");
		Objects.requireNonNull(endPosition, "Token end position must not be null");
		if (value.length() != 2 || value.charAt(0) != '\\') {
			throw new IllegalArgumentException("Escaped token value '" + value + "' must be of length 2 and start with '\\'");
		}
		if (!definition.matches(value)) {
			throw new IllegalArgumentException("Token value '" + value + "' does not match the token definition '" + definition + "'");
		}
		if (startPosition.isPositioned() && endPosition.isPositioned()) {
			if (endPosition.character() - startPosition.character() != value.length() - 1) { // Use -1 to make it inclusive
				throw new IllegalArgumentException("Positions of token '" + value + "' do not match the length of the token");
			}
		}
	}
	
	/**
	 * Creates an unpositioned escaped token for the given token definition and value.<br>
	 *
	 * @param definition The token definition
	 * @param value The string value of the token
	 * @return The unpositioned escaped token
	 * @throws NullPointerException If the token definition or the token value is null
	 * @throws IllegalArgumentException If the token value does not have a length of 2, does not start with a backslash or does not match the token definition
	 */
	public static @NotNull EscapedToken createUnpositioned(@NotNull TokenDefinition definition, @NotNull String value) {
		return new EscapedToken(definition, value, TokenPosition.UNPOSITIONED, TokenPosition.UNPOSITIONED);
	}
	
	@Override
	public @NotNull String toString() {
		return "EscapedToken[definition=" + this.definition + ",value=" + this.value.replace("\t", "\\t").replace("\n", "\\n") + ",startPosition=" + this.startPosition + ",endPosition=" + this.endPosition + "]";
	}
}
