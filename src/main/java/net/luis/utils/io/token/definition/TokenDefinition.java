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

import net.luis.utils.io.token.rule.rules.NegatableTokenRule;
import net.luis.utils.io.token.rule.rules.TokenRule;
import net.luis.utils.io.token.tokens.Token;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Functional interface for a token definition.<br>
 * A token definition defines a token and provides a method to check if a word matches the token.<br>
 * <p>
 *     For easier usage in rules this interface extends {@link TokenRule}.<br>
 *     This means a token definition can be used as a rule in a {@link TokenRule}.
 * </p>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface TokenDefinition extends NegatableTokenRule {
	
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
	 * Combines multiple token definitions into a single token definition.<br>
	 * The resulting token definition contains the concatenated string of all token definitions.<br>
	 * If only one token definition is provided, it is returned as is.<br>
	 *
	 * @param definitions The token definitions to combine
	 * @return The combined token definition
	 * @throws NullPointerException If the token definition array is null
	 * @throws IllegalArgumentException If the token definition array is empty or contains an unsupported token definition type
	 */
	static @NotNull TokenDefinition combine(TokenDefinition @NotNull ... definitions) {
		Objects.requireNonNull(definitions, "Token definition array must not be null");
		if (definitions.length == 0) {
			throw new IllegalArgumentException("Token definition array must not be empty");
		}
		if (definitions.length == 1) {
			return definitions[0];
		}
		
		StringBuilder combined = new StringBuilder();
		for (TokenDefinition definition : definitions) {
			String value = switch (definition) {
				case StringTokenDefinition(String token, boolean equalsIgnoreCase) -> token;
				case CharTokenDefinition(char token) -> String.valueOf(token);
				case EscapedTokenDefinition(char token) -> "\\" + token;
				default -> throw new IllegalArgumentException("Unsupported token definition type: " + definition.getClass().getName());
			};
			combined.append(value);
		}
		return new StringTokenDefinition(combined.toString(), false);
	}
	
	/**
	 * Checks if the given word matches this token definition.<br>
	 *
	 * @param word The word to check
	 * @return True if the word matches this token definition, false otherwise
	 * @throws NullPointerException If the word is null
	 */
	boolean matches(@NotNull String word);
	
	@Override
	default boolean match(@NotNull Token token) {
		Objects.requireNonNull(token, "Token must not be null");
		
		return this.matches(token.value());
	}
}
