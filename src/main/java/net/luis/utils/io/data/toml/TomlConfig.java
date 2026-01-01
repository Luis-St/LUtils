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

package net.luis.utils.io.data.toml;

import net.luis.utils.io.data.config.ReadOnly;
import net.luis.utils.io.data.config.WriteOnly;
import org.jspecify.annotations.NonNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Configuration for reading and writing TOML files.<br>
 *
 * @author Luis-St
 *
 * @param strict Whether to use strict TOML 1.0 parsing mode (read-only)
 * @param prettyPrint Whether to format output with proper indentation (write-only)
 * @param indent The indentation string to use for nested structures (write-only)
 * @param useInlineTables Whether to prefer inline tables for simple cases (write-only)
 * @param maxInlineTableSize Maximum number of entries for inline table format (write-only)
 * @param useInlineArrays Whether to prefer inline arrays (write-only)
 * @param maxInlineArraySize Maximum number of elements for inline array format (write-only)
 * @param useMultiLineStrings Whether to use multi-line strings for long text (write-only)
 * @param multiLineStringThreshold Minimum length to trigger multi-line strings (write-only)
 * @param useArrayOfTablesNotation Whether to use [[array.of.tables]] notation (write-only)
 * @param dateTimeStyle How to format date/time values (write-only)
 * @param allowDuplicateKeys Whether to allow duplicate keys (read-only)
 * @param charset The charset to use for reading and writing
 */
public record TomlConfig(
	@ReadOnly boolean strict,
	@WriteOnly boolean prettyPrint,
	@WriteOnly @NonNull String indent,
	@WriteOnly boolean useInlineTables,
	@WriteOnly int maxInlineTableSize,
	@WriteOnly boolean useInlineArrays,
	@WriteOnly int maxInlineArraySize,
	@WriteOnly boolean useMultiLineStrings,
	@WriteOnly int multiLineStringThreshold,
	@WriteOnly boolean useArrayOfTablesNotation,
	@WriteOnly @NonNull DateTimeStyle dateTimeStyle,
	@ReadOnly boolean allowDuplicateKeys,
	@NonNull Charset charset
) {
	
	/**
	 * The default TOML configuration.<br>
	 * <ul>
	 *     <li>Strict: true</li>
	 *     <li>Pretty print: true</li>
	 *     <li>Indent: "  " (2 spaces)</li>
	 *     <li>Use inline tables: false</li>
	 *     <li>Max inline table size: 3</li>
	 *     <li>Use inline arrays: true</li>
	 *     <li>Max inline array size: 10</li>
	 *     <li>Use multi-line strings: false</li>
	 *     <li>Multi-line string threshold: 80</li>
	 *     <li>Use array of tables notation: true</li>
	 *     <li>Date/time style: RFC_3339</li>
	 *     <li>Allow duplicate keys: false</li>
	 *     <li>Charset: UTF-8</li>
	 * </ul>
	 */
	public static final TomlConfig DEFAULT = new TomlConfig(
		true,
		true,
		"  ",
		false,
		3,
		true,
		10,
		false,
		80,
		true,
		DateTimeStyle.RFC_3339,
		false,
		StandardCharsets.UTF_8
	);
	
	/**
	 * Compact TOML configuration that prefers inline styles.<br>
	 */
	public static final TomlConfig COMPACT = new TomlConfig(
		true,
		false,
		"",
		true,
		5,
		true,
		20,
		false,
		120,
		false,
		DateTimeStyle.RFC_3339,
		false,
		StandardCharsets.UTF_8
	);
	
	/**
	 * Constructs a new TOML configuration.<br>
	 *
	 * @throws NullPointerException If any of the required parameters is null
	 * @throws IllegalArgumentException If any size/threshold values are invalid
	 */
	public TomlConfig {
		Objects.requireNonNull(indent, "Indent must not be null");
		Objects.requireNonNull(dateTimeStyle, "Date/time style must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		
		if (maxInlineTableSize < 0) {
			throw new IllegalArgumentException("Max inline table size must not be negative");
		}
		if (maxInlineArraySize < 0) {
			throw new IllegalArgumentException("Max inline array size must not be negative");
		}
		if (multiLineStringThreshold < 0) {
			throw new IllegalArgumentException("Multi-line string threshold must not be negative");
		}
	}
	
	/**
	 * Represents how date/time values should be formatted.<br>
	 */
	public enum DateTimeStyle {
		/**
		 * RFC 3339 format: 2006-01-02T15:04:05-07:00<br>
		 * This is the standard TOML date/time format.<br>
		 */
		RFC_3339,
		
		/**
		 * ISO 8601 format: 2006-01-02T15:04:05<br>
		 * Without timezone offset for local date/times.<br>
		 */
		ISO_8601,
		
		/**
		 * Space-separated format: 2006-01-02 15:04:05<br>
		 * More human-readable but less standard.<br>
		 */
		SPACE_SEPARATED
	}
}
