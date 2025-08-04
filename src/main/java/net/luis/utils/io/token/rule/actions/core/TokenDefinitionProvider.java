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

package net.luis.utils.io.token.rule.actions.core;

import net.luis.utils.io.token.definition.TokenDefinition;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Token definition provider that provides appropriate token definitions for given values.<br>
 * This is useful for scenarios where new tokens are created with values that may not match<br>
 * the original token's definition, such as in split operations.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface TokenDefinitionProvider {
	
	/**
	 * Creates a token definition provider that always returns the same definition.<br>
	 * This is useful when all split parts should use the same definition.<br>
	 *
	 * @param definition The definition to always return
	 * @return A provider that always returns the given definition
	 * @throws NullPointerException If the definition is null
	 */
	static @NotNull TokenDefinitionProvider constant(@NotNull TokenDefinition definition) {
		Objects.requireNonNull(definition, "Definition must not be null");
		return value -> definition;
	}
	
	/**
	 * Creates a token definition provider that returns a definition accepting any value.<br>
	 * This is the default behavior when no specific provider is needed.<br>
	 *
	 * @return A provider that accepts any string value
	 */
	static @NotNull TokenDefinitionProvider acceptAll() {
		return value -> word -> true;
	}
	
	/**
	 * Creates a token definition provider based on simple pattern matching.<br>
	 * This provider checks if the value matches common patterns and returns appropriate definitions.<br>
	 * Supported patterns include:
	 * <ul>
	 *     <li>Numeric values (e.g., "123")</li>
	 *     <li>Alphabetic values (e.g., "abc")</li>
	 *     <li>Whitespace (e.g., " ")</li>
	 *     <li>Word characters (e.g., "word")</li>
	 * </ul>
	 * If the value does not match any of these patterns, it returns a definition that accepts any string.<br>
	 *
	 * @return A provider that returns definitions based on value patterns
	 */
	static @NotNull TokenDefinitionProvider patternBased() {
		return value -> {
			if (value.matches("\\d+")) {
				return word -> word.matches("\\d+");
			} else if (value.matches("[a-zA-Z]+")) {
				return word -> word.matches("[a-zA-Z]+");
			} else if (value.matches("\\s+")) {
				return word -> word.matches("\\s+");
			} else if (value.matches("\\w+")) {
				return word -> word.matches("\\w+");
			} else {
				return word -> true;
			}
		};
	}
	
	/**
	 * Provides a token definition for the given value.<br>
	 * The provider should return a definition that accepts the given value.<br>
	 *
	 * @param value The value for which to provide a definition
	 * @return A token definition that accepts the given value
	 * @throws NullPointerException If the value is null
	 * @apiNote The returned definition should match the given value
	 */
	@NotNull TokenDefinition provide(@NotNull String value);
}
