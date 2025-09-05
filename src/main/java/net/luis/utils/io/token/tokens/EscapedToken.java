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
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Special token implementation for escaped tokens.<br>
 * An escaped token is a token that starts with a backslash and has a length of 2.<br>
 *
 * @author Luis-St
 *
 * @param value The string value of the token
 * @param position The position of the token
 */
public record EscapedToken(
	@NotNull String value,
	@NotNull TokenPosition position
) implements Token {
	
	/**
	 * Constructs a new escaped token for a string value.<br>
	 *
	 * @param value The string value of the token
	 * @param position The position of the token
	 * @throws NullPointerException If the token value or the token position is null
	 * @throws IllegalArgumentException If the token value does not have a length of 2 or does not start with a backslash
	 */
	public EscapedToken {
		Objects.requireNonNull(value, "Token value must not be null");
		Objects.requireNonNull(position, "Token position must not be null");
		if (value.length() != 2 || value.charAt(0) != '\\') {
			throw new IllegalArgumentException("Escaped token value '" + value + "' must be of length 2 and start with '\\'");
		}
	}
	
	/**
	 * Creates an unpositioned escaped token for the given value.<br>
	 *
	 * @param value The string value of the token
	 * @return The unpositioned escaped token
	 * @throws NullPointerException If the token value is null
	 * @throws IllegalArgumentException If the token value does not have a length of 2 or does not start with a backslash
	 */
	public static @NotNull EscapedToken createUnpositioned(@NotNull String value) {
		return new EscapedToken(value, TokenPosition.UNPOSITIONED);
	}
	
	@Override
	public @NotNull String toString() {
		return "EscapedToken[value=" + this.value.replace("\\", "\\\\") + ",position=" + this.position + "]";
	}
}
