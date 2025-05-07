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
 * Token implementation for a simple string token.<br>
 *
 * @author Luis-St
 *
 * @param definition The token definition
 * @param value The string value of the token
 * @param startPosition The start position of the token
 * @param endPosition The end position of the token
 */
public record SimpleToken(
	@NotNull TokenDefinition definition,
	@NotNull String value,
	@NotNull TokenPosition startPosition,
	@NotNull TokenPosition endPosition
) implements Token {
	
	/**
	 * Constructs a new simple token for a string value.<br>
	 * @param definition The token definition
	 * @param value The string value of the token
	 * @param startPosition The start position of the token
	 * @param endPosition The end position of the token
	 * @throws NullPointerException If any of the parameters are null
	 * @throws IllegalArgumentException If the token value does not match the token definition or the start and end positions do not match the length of the token
	 */
	public SimpleToken {
		Objects.requireNonNull(definition, "Token definition must not be null");
		Objects.requireNonNull(value, "Token value must not be null");
		Objects.requireNonNull(startPosition, "Token start position must not be null");
		Objects.requireNonNull(endPosition, "Token end position must not be null");
		if (!definition.matches(value)) {
			throw new IllegalArgumentException("Token value '" + value + "' does not match the token definition '" + definition + "'");
		}
		if (endPosition.character() - startPosition.character() != value.length() - 1) { // Use -1 to make it inclusive
			throw new IllegalArgumentException("Positions of token '" + value + "' do not match the length of the token '" + value.length() + "'");
		}
	}
	
	@Override
	public @NotNull String toString() {
		return "SimpleToken[definition=" + this.definition + ",value=" + this.value.replace("\t", "\\t").replace("\n", "\\n") + ",startPosition=" + this.startPosition + ",endPosition=" + this.endPosition + "]";
	}
}
