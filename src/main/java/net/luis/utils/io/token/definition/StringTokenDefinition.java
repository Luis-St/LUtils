/*
 * LUtils
 * Copyright (C) 2026 Luis Staudt
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

package net.luis.utils.io.token.definition;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Token definition for a string.<br>
 * This token definition matches a string that is equal to the token string.<br>
 *
 * @author Luis-St
 *
 * @param token The token string
 * @param equalsIgnoreCase If the token should be compared case-insensitive
 */
public record StringTokenDefinition(
	@NonNull String token,
	boolean equalsIgnoreCase
) implements TokenDefinition {
	
	/**
	 * Constructs a new token definition for the given string.<br>
	 *
	 * @param token The token string
	 * @param equalsIgnoreCase If the token should be compared case-insensitive
	 * @throws NullPointerException If the token is null
	 * @throws IllegalArgumentException If the token is empty
	 */
	public StringTokenDefinition {
		Objects.requireNonNull(token, "Token must not be null");
		if (token.isEmpty()) {
			throw new IllegalArgumentException("Token must not be empty");
		}
	}
	
	@Override
	public boolean matches(@NonNull String word) {
		Objects.requireNonNull(word, "Word must not be null");
		return this.equalsIgnoreCase ? word.equalsIgnoreCase(this.token) : word.equals(this.token);
	}
	
	@Override
	public @NonNull String toString() {
		return "StringTokenDefinition[token=" + this.token.replace("\t", "\\t").replace("\n", "\\n") + ",equalsIgnoreCase=" + this.equalsIgnoreCase + "]";
	}
}
