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

package net.luis.utils.io.data.property;

import net.luis.utils.io.data.config.ReadOnly;
import net.luis.utils.io.data.config.WriteOnly;
import net.luis.utils.io.data.property.exception.PropertySyntaxException;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Configuration for reading and writing properties.<br>
 *
 * @author Luis-St
 *
 * @param separator The separator between key and value (default '=')
 * @param alignment The number of spaces around the separator (write-only, default 1)
 * @param commentCharacters The characters that indicate a comment line (read-only, default '#')
 * @param keyPattern The pattern that a key must match
 * @param valuePattern The pattern that a value must match
 * @param advancedParsing Whether to use advanced parsing for compacted/variable keys (read-only)
 * @param charset The charset to use for reading and writing
 * @param prettyPrint Whether to format output with proper indentation (write-only)
 * @param indent The indentation string to use for nested structures (write-only)
 * @param arrayOpenChar The character to open an inline array (default '[')
 * @param arrayCloseChar The character to close an inline array (default ']')
 * @param arraySeparator The character to separate array elements (default ',')
 * @param allowMultiLineArrays Whether to allow multi-line array syntax like key[] = value (read-only)
 * @param parseTypedValues Whether to automatically parse string values to boolean/number types (read-only)
 * @param nullStyle How to represent null values in output (write-only)
 * @param enableWriteCompaction Whether to compact common key prefixes when writing (write-only)
 * @param minCompactionGroupSize Minimum number of keys with same prefix to enable compaction (write-only)
 * @param variableTypeSeparator The character separating variable type from key in ${type:key} (default ':')
 * @param defaultValueMarker The string marking a default value in variables like ${type:key:-default} (default ":-")
 * @param customVariables Custom variable map for variable resolution (read-only)
 */
public record PropertyConfig(
	char separator,
	@WriteOnly int alignment,
	@ReadOnly @NonNull Set<Character> commentCharacters,
	@NonNull Pattern keyPattern,
	@NonNull Pattern valuePattern,
	@ReadOnly boolean advancedParsing,
	@NonNull Charset charset,
	@WriteOnly boolean prettyPrint,
	@WriteOnly @NonNull String indent,
	char arrayOpenChar,
	char arrayCloseChar,
	char arraySeparator,
	@ReadOnly boolean allowMultiLineArrays,
	@ReadOnly boolean parseTypedValues,
	@NonNull NullStyle nullStyle,
	@WriteOnly boolean enableWriteCompaction,
	@WriteOnly int minCompactionGroupSize,
	char variableTypeSeparator,
	@NonNull String defaultValueMarker,
	@ReadOnly @Nullable Map<String, String> customVariables
) {
	
	/**
	 * The default property configuration.<br>
	 * <ul>
	 *     <li>Separator: '='</li>
	 *     <li>Alignment: 1</li>
	 *     <li>Comment characters: '#'</li>
	 *     <li>Key pattern: "^[a-zA-Z0-9._${}:\\[\\]|-]+$" (supports advanced keys)</li>
	 *     <li>Value pattern: ".*"</li>
	 *     <li>Advanced parsing: false</li>
	 *     <li>Charset: UTF-8</li>
	 *     <li>Pretty print: false</li>
	 *     <li>Indent: ""</li>
	 *     <li>Array chars: '[', ']', ','</li>
	 *     <li>Allow multi-line arrays: true</li>
	 *     <li>Parse typed values: false</li>
	 *     <li>Null style: EMPTY</li>
	 *     <li>Write compaction: false</li>
	 *     <li>Min compaction group size: 2</li>
	 *     <li>Variable type separator: ':'</li>
	 *     <li>Default value marker: ":-"</li>
	 *     <li>Custom variables: null</li>
	 * </ul>
	 */
	public static final PropertyConfig DEFAULT = new PropertyConfig(
		'=', 1, Set.of('#'),
		Pattern.compile("^[a-zA-Z0-9._${}:\\[\\]|-]+$"), Pattern.compile(".*"),
		false, StandardCharsets.UTF_8,
		false, "",
		'[', ']', ',',
		true, false,
		NullStyle.EMPTY,
		false, 2,
		':', ":-",
		null
	);
	/**
	 * Configuration with advanced parsing enabled.<br>
	 * Enables compacted keys, variable resolution, and typed value parsing.<br>
	 */
	public static final PropertyConfig ADVANCED = new PropertyConfig(
		'=', 1, Set.of('#'),
		Pattern.compile("^[a-zA-Z0-9._${}:\\[\\]|-]+$"), Pattern.compile(".*"),
		true, StandardCharsets.UTF_8,
		true, "\t",
		'[', ']', ',',
		true, true,
		NullStyle.EMPTY,
		true, 2,
		':', ":-",
		null
	);
	
	/**
	 * Constructs a new property configuration.<br>
	 *
	 * @throws NullPointerException If any of the required parameters is null
	 * @throws IllegalArgumentException If the separator is invalid or conflicts with other characters
	 */
	public PropertyConfig {
		Objects.requireNonNull(commentCharacters, "Comment characters must not be null");
		Objects.requireNonNull(keyPattern, "Key pattern must not be null");
		Objects.requireNonNull(valuePattern, "Value pattern must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		Objects.requireNonNull(indent, "Indent must not be null");
		Objects.requireNonNull(nullStyle, "Null style must not be null");
		Objects.requireNonNull(defaultValueMarker, "Default value marker must not be null");
		
		if (separator == '\0' || Character.isWhitespace(separator)) {
			throw new IllegalArgumentException("Separator must not be a whitespace character");
		}
		if (commentCharacters.contains(separator)) {
			throw new IllegalArgumentException("Separator must not be a comment character");
		}
		if (alignment < 0) {
			throw new IllegalArgumentException("Alignment must not be negative");
		}
		if (minCompactionGroupSize < 2) {
			throw new IllegalArgumentException("Minimum compaction group size must be at least 2");
		}
		if (arrayOpenChar == arrayCloseChar) {
			throw new IllegalArgumentException("Array open and close characters must be different");
		}
		if (arraySeparator == arrayOpenChar || arraySeparator == arrayCloseChar) {
			throw new IllegalArgumentException("Array separator must not be the same as array open or close character");
		}
		if (defaultValueMarker.isEmpty()) {
			throw new IllegalArgumentException("Default value marker must not be empty");
		}
	}
	
	/**
	 * Checks whether the given key matches the key pattern.<br>
	 *
	 * @param key The key to check
	 * @throws NullPointerException If the key is null
	 * @throws PropertySyntaxException If the key is blank or does not match the key pattern
	 */
	public void ensureKeyMatches(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (key.isBlank()) {
			throw new PropertySyntaxException("Property key must not be empty");
		}
		if (!this.keyPattern.matcher(key).matches()) {
			throw new PropertySyntaxException("Property key '" + key + "' does not match the pattern '" + this.keyPattern.pattern() + "' defined in property config");
		}
	}
	
	/**
	 * Checks whether the given value matches the value pattern.<br>
	 *
	 * @param value The value to check
	 * @throws NullPointerException If the value is null
	 * @throws PropertySyntaxException If the value does not match the value pattern
	 */
	public void ensureValueMatches(@NonNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (!this.valuePattern.matcher(value).matches()) {
			throw new PropertySyntaxException("Property value '" + value + "' does not match the pattern '" + this.valuePattern.pattern() + "' defined in property config");
		}
	}
	
	/**
	 * Returns a copy of the custom variables map, or an empty map if none were provided.<br>
	 *
	 * @return An unmodifiable copy of the custom variables
	 */
	public @NonNull Map<String, String> getCustomVariables() {
		if (this.customVariables == null) {
			return Map.of();
		}
		return Collections.unmodifiableMap(this.customVariables);
	}
	
	/**
	 * Represents how null values should be serialized.<br>
	 */
	public enum NullStyle {
		/**
		 * Null is represented as an empty string.<br>
		 */
		EMPTY,
		/**
		 * Null is represented as the literal string "null".<br>
		 */
		NULL_STRING,
		/**
		 * Null is represented as a tilde "~" (YAML style).<br>
		 */
		TILDE
	}
}
