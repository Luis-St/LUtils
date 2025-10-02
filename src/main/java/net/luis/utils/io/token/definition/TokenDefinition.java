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

package net.luis.utils.io.token.definition;

import org.jetbrains.annotations.NotNull;

/**
 * Functional interface for a token definition.<br>
 * A token definition defines a token and provides a method to check if a word matches the token.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface TokenDefinition {
	
	/**
	 * Creates a new token definition for a single character.<br>
	 *
	 * @param token The token character
	 * @return The token definition
	 * @see CharTokenDefinition
	 */
	static @NotNull TokenDefinition of(char token) {
		return new CharTokenDefinition(token);
	}
	
	/**
	 * Creates a new token definition for a string.<br>
	 *
	 * @param token The token string
	 * @param equalsIgnoreCase If the token should be compared case-insensitive
	 * @return The token definition
	 * @throws NullPointerException If the token is null
	 * @throws IllegalArgumentException If the token is empty
	 * @see StringTokenDefinition
	 */
	static @NotNull TokenDefinition of(@NotNull String token, boolean equalsIgnoreCase) {
		return new StringTokenDefinition(token, equalsIgnoreCase);
	}
	
	/**
	 * Creates a new escaped token definition for a single character.<br>
	 *
	 * @param token The token character
	 * @return The token definition
	 * @see EscapedTokenDefinition
	 */
	static @NotNull TokenDefinition ofEscaped(char token) {
		return new EscapedTokenDefinition(token);
	}
	
	/**
	 * Checks if the given word matches this token definition.<br>
	 *
	 * @param word The word to check
	 * @return True if the word matches this token definition, false otherwise
	 * @throws NullPointerException If the word is null
	 */
	boolean matches(@NotNull String word);
}
