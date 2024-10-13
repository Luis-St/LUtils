/*
 * LUtils
 * Copyright (C) 2024 Luis Staudt
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

package net.luis.utils.io.data.properties;

import net.luis.utils.io.data.config.ReadOnly;
import net.luis.utils.io.data.config.WriteOnly;
import net.luis.utils.io.data.properties.exception.IllegalPropertyKeyException;
import net.luis.utils.io.data.properties.exception.IllegalPropertyValueException;
import net.luis.utils.util.ErrorAction;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Configuration for reading and writing properties.<br>
 *
 * @author Luis-St
 *
 * @param separator The separator between key and value
 * @param alignment The number of spaces between key and value (write-only)
 * @param commentCharacters The characters that indicate a comment line (read-only)
 * @param keyPattern The pattern that a key must match
 * @param valuePattern The pattern that a value must match
 * @param advancedParsing Whether to use advanced parsing (read-only)
 * @param charset The charset to use for reading and writing
 * @param errorAction The action to take when an error occurs
 */
public record PropertyConfig(
	char separator,
	@WriteOnly int alignment,
	@ReadOnly @NotNull Set<Character> commentCharacters,
	@NotNull Pattern keyPattern,
	@NotNull Pattern valuePattern,
	@ReadOnly boolean advancedParsing,
	@NotNull Charset charset,
	@NotNull ErrorAction errorAction
) {
	
	/**
	 * The default property configuration.<br>
	 * Separator: '='<br>
	 * Alignment: 1<br>
	 * Comment characters: '#'<br>
	 * Key pattern: "^[a-zA-Z0-9._-]+$"<br>
	 * Value pattern: ".*"<br>
	 * Advanced parsing: false<br>
	 * Charset: UTF-8<br>
	 * Error action: THROW<br>
	 */
	public static final PropertyConfig DEFAULT = new PropertyConfig('=', 1, Set.of('#'), Pattern.compile("^[a-zA-Z0-9._-]+$"), Pattern.compile(".*"), false, StandardCharsets.UTF_8, ErrorAction.THROW);
	
	/**
	 * Constructs a new property configuration.<br>
	 * @param separator The separator between key and value
	 * @param alignment The number of spaces between key and value
	 * @param commentCharacters The characters that indicate a comment line
	 * @param keyPattern The pattern that a key must match
	 * @param valuePattern The pattern that a value must match
	 * @param advancedParsing Whether to use advanced parsing
	 * @param charset The charset to use for reading and writing
	 * @param errorAction The action to take when an error occurs
	 * @throws NullPointerException If any of the parameters is null
	 * @throws IllegalArgumentException If the separator is a whitespace character or a comment character
	 */
	public PropertyConfig {
		Objects.requireNonNull(commentCharacters, "Comment characters must not be null");
		Objects.requireNonNull(keyPattern, "Key pattern must not be null");
		Objects.requireNonNull(valuePattern, "Value pattern must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		Objects.requireNonNull(errorAction, "Error action must not be null");
		
		if (separator == '\0' || Character.isWhitespace(separator)) {
			throw new IllegalArgumentException("Separator must not be a whitespace character");
		}
		if (commentCharacters.contains(separator)) {
			throw new IllegalArgumentException("Separator must not be a comment character");
		}
	}
	
	/**
	 * Checks whether the given key matches the key pattern.<br>
	 * @param key The key to check
	 * @throws NullPointerException If the key is null
	 * @throws IllegalPropertyKeyException If the key is blank or does not match the key pattern
	 */
	public void ensureKeyMatches(@NotNull String key) throws IllegalPropertyKeyException {
		Objects.requireNonNull(key, "Key must not be null");
		if (key.isBlank()) {
			throw new IllegalPropertyKeyException("Property key must not be empty");
		}
		if (!this.keyPattern.matcher(key).matches()) {
			throw new IllegalPropertyKeyException("Property key '" + key + "' does not match the pattern '" + this.keyPattern.pattern() + "' defined in property config");
		}
	}
	
	/**
	 * Checks whether the given value matches the value pattern.<br>
	 * @param value The value to check
	 * @throws NullPointerException If the value is null
	 * @throws IllegalPropertyValueException If the value does not match the value pattern
	 */
	public void ensureValueMatches(@NotNull String value) throws IllegalPropertyValueException {
		Objects.requireNonNull(value, "Value must not be null");
		if (!this.valuePattern.matcher(value).matches()) {
			throw new IllegalPropertyValueException("Property value '" + value + "' does not match the pattern '" + this.valuePattern.pattern() + "' defined in property config");
		}
	}
}
