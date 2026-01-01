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

package net.luis.utils.io.data.ini;

import net.luis.utils.io.data.config.ReadOnly;
import net.luis.utils.io.data.config.WriteOnly;
import net.luis.utils.io.data.ini.exception.IniSyntaxException;
import org.jspecify.annotations.NonNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Configuration for reading and writing ini files.<br>
 *
 * @author Luis-St
 *
 * @param strict Whether to use strict parsing mode (read-only)
 * @param prettyPrint Whether to format output with proper indentation (write-only)
 * @param indent The indentation string to use for keys within sections (write-only)
 * @param commentCharacters The characters that indicate a comment line (read-only, default ';' and '#')
 * @param separator The separator between key and value (default '=')
 * @param alignment The number of spaces around the separator (write-only, default 1)
 * @param allowDuplicateKeys Whether to allow duplicate keys within a section (read-only)
 * @param allowDuplicateSections Whether to allow sections with the same name (read-only)
 * @param parseTypedValues Whether to automatically parse string values to boolean/number types (read-only)
 * @param keyPattern The pattern that a key must match
 * @param sectionPattern The pattern that a section name must match
 * @param nullStyle How to represent null values in output (write-only)
 * @param charset The charset to use for reading and writing
 */
public record IniConfig(
	@ReadOnly boolean strict,
	@WriteOnly boolean prettyPrint,
	@WriteOnly @NonNull String indent,
	@ReadOnly @NonNull Set<Character> commentCharacters,
	char separator,
	@WriteOnly int alignment,
	@ReadOnly boolean allowDuplicateKeys,
	@ReadOnly boolean allowDuplicateSections,
	@ReadOnly boolean parseTypedValues,
	@NonNull Pattern keyPattern,
	@NonNull Pattern sectionPattern,
	@WriteOnly @NonNull NullStyle nullStyle,
	@NonNull Charset charset
) {
	
	/**
	 * The default ini configuration.<br>
	 * <ul>
	 *     <li>Strict: true</li>
	 *     <li>Pretty print: true</li>
	 *     <li>Indent: ""</li>
	 *     <li>Comment characters: ';', '#'</li>
	 *     <li>Separator: '='</li>
	 *     <li>Alignment: 1</li>
	 *     <li>Allow duplicate keys: false</li>
	 *     <li>Allow duplicate sections: false</li>
	 *     <li>Parse typed values: false</li>
	 *     <li>Key pattern: "^[a-zA-Z0-9._-]+$"</li>
	 *     <li>Section pattern: "^[a-zA-Z0-9._-]+$"</li>
	 *     <li>Null style: EMPTY</li>
	 *     <li>Charset: UTF-8</li>
	 * </ul>
	 */
	public static final IniConfig DEFAULT = new IniConfig(
		true,
		true,
		"",
		Set.of(';', '#'),
		'=',
		1,
		false,
		false,
		false,
		Pattern.compile("^[a-zA-Z0-9._-]+$"),
		Pattern.compile("^[a-zA-Z0-9._-]+$"),
		NullStyle.EMPTY,
		StandardCharsets.UTF_8
	);
	
	/**
	 * Constructs a new ini configuration.<br>
	 *
	 * @throws NullPointerException If any of the required parameters is null
	 * @throws IllegalArgumentException If the separator is invalid or conflicts with other characters
	 */
	public IniConfig {
		Objects.requireNonNull(indent, "Indent must not be null");
		Objects.requireNonNull(commentCharacters, "Comment characters must not be null");
		Objects.requireNonNull(keyPattern, "Key pattern must not be null");
		Objects.requireNonNull(sectionPattern, "Section pattern must not be null");
		Objects.requireNonNull(nullStyle, "Null style must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		
		if (separator == '\0' || Character.isWhitespace(separator)) {
			throw new IllegalArgumentException("Separator must not be a whitespace character");
		}
		if (commentCharacters.contains(separator)) {
			throw new IllegalArgumentException("Separator must not be a comment character");
		}
		if (alignment < 0) {
			throw new IllegalArgumentException("Alignment must not be negative");
		}
	}
	
	/**
	 * Checks whether the given key matches the key pattern.<br>
	 *
	 * @param key The key to check
	 * @throws NullPointerException If the key is null
	 * @throws IniSyntaxException If the key is blank or does not match the key pattern
	 */
	public void ensureKeyMatches(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (key.isBlank()) {
			throw new IniSyntaxException("Ini key must not be empty");
		}
		if (!this.keyPattern.matcher(key).matches()) {
			throw new IniSyntaxException("Ini key '" + key + "' does not match the pattern '" + this.keyPattern.pattern() + "' defined in ini config");
		}
	}
	
	/**
	 * Checks whether the given section name matches the section pattern.<br>
	 *
	 * @param sectionName The section name to check
	 * @throws NullPointerException If the section name is null
	 * @throws IniSyntaxException If the section name is blank or does not match the section pattern
	 */
	public void ensureSectionMatches(@NonNull String sectionName) {
		Objects.requireNonNull(sectionName, "Section name must not be null");
		if (sectionName.isBlank()) {
			throw new IniSyntaxException("Ini section name must not be empty");
		}
		if (!this.sectionPattern.matcher(sectionName).matches()) {
			throw new IniSyntaxException("Ini section name '" + sectionName + "' does not match the pattern '" + this.sectionPattern.pattern() + "' defined in ini config");
		}
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
		 * Null values are skipped and not written.<br>
		 */
		SKIP
	}
}
