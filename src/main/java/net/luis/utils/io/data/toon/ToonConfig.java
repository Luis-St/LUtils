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

package net.luis.utils.io.data.toon;

import net.luis.utils.io.data.config.ReadOnly;
import net.luis.utils.io.data.config.WriteOnly;
import org.jspecify.annotations.NonNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Configuration for reading and writing toon files.<br>
 *
 * @author Luis-St
 *
 * @param strict Whether to use strict parsing mode (read-only)
 * @param indent The number of spaces per indentation level (write-only)
 * @param delimiter The delimiter to use for separating values (write-only)
 * @param keyFolding The key folding mode (write-only)
 * @param flattenDepth The maximum number of key segments to fold (write-only)
 * @param expandPaths The path expansion mode (read-only)
 * @param charset The charset to use for reading and writing
 */
public record ToonConfig(
	@ReadOnly boolean strict,
	@WriteOnly int indent,
	@WriteOnly @NonNull Delimiter delimiter,
	@WriteOnly @NonNull KeyFolding keyFolding,
	@WriteOnly int flattenDepth,
	@ReadOnly @NonNull PathExpansion expandPaths,
	@NonNull Charset charset
) {
	
	/**
	 * The default toon configuration.<br>
	 * <ul>
	 *     <li>Strict: true</li>
	 *     <li>Indent: 2</li>
	 *     <li>Delimiter: COMMA</li>
	 *     <li>Key folding: OFF</li>
	 *     <li>Flatten depth: Integer.MAX_VALUE</li>
	 *     <li>Expand paths: OFF</li>
	 *     <li>Charset: UTF-8</li>
	 * </ul>
	 */
	public static final ToonConfig DEFAULT = new ToonConfig(
		true,
		2,
		Delimiter.COMMA,
		KeyFolding.OFF,
		Integer.MAX_VALUE,
		PathExpansion.OFF,
		StandardCharsets.UTF_8
	);
	
	/**
	 * Compact toon configuration that uses no indentation.<br>
	 * <ul>
	 *     <li>Strict: true</li>
	 *     <li>Indent: 0</li>
	 *     <li>Delimiter: COMMA</li>
	 *     <li>Key folding: OFF</li>
	 *     <li>Flatten depth: Integer.MAX_VALUE</li>
	 *     <li>Expand paths: OFF</li>
	 *     <li>Charset: UTF-8</li>
	 * </ul>
	 */
	public static final ToonConfig COMPACT = new ToonConfig(
		true,
		0,
		Delimiter.COMMA,
		KeyFolding.OFF,
		Integer.MAX_VALUE,
		PathExpansion.OFF,
		StandardCharsets.UTF_8
	);
	
	/**
	 * Constructs a new toon configuration.<br>
	 *
	 * @throws NullPointerException If any of the required parameters is null
	 * @throws IllegalArgumentException If the indent is negative
	 */
	public ToonConfig {
		Objects.requireNonNull(delimiter, "Delimiter must not be null");
		Objects.requireNonNull(keyFolding, "Key folding must not be null");
		Objects.requireNonNull(expandPaths, "Expand paths must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		
		if (indent < 0) {
			throw new IllegalArgumentException("Indent must not be negative");
		}
	}
	
	/**
	 * Represents the delimiter type for separating values in toon arrays and tabular rows.<br>
	 *
	 * @author Luis-St
	 */
	public enum Delimiter {
		/**
		 * Comma delimiter: ','<br>
		 */
		COMMA(',', ","),
		
		/**
		 * Tab delimiter: '\t'<br>
		 */
		TAB('\t', "\t"),
		
		/**
		 * Pipe delimiter: '|'<br>
		 */
		PIPE('|', "|");
		
		/**
		 * The character used as the delimiter.<br>
		 */
		private final char character;
		
		/**
		 * The symbol string of the delimiter.<br>
		 */
		private final String symbol;
		
		/**
		 * Constructs a new delimiter.<br>
		 * @param character The delimiter character
		 * @param symbol The delimiter symbol
		 */
		Delimiter(char character, @NonNull String symbol) {
			this.character = character;
			this.symbol = symbol;
		}
		
		/**
		 * Returns the character of this delimiter.<br>
		 * @return The delimiter character
		 */
		public char getChar() {
			return this.character;
		}
		
		/**
		 * Returns the symbol string of this delimiter.<br>
		 * @return The delimiter symbol
		 */
		public @NonNull String getSymbol() {
			return this.symbol;
		}
	}
	
	/**
	 * Represents the key folding mode for toon output.<br>
	 * Key folding collapses single-child object chains into dotted key paths.<br>
	 *
	 * @author Luis-St
	 */
	public enum KeyFolding {
		/**
		 * No key folding.<br>
		 */
		OFF,
		
		/**
		 * Safe key folding: only fold keys that match bare key rules.<br>
		 */
		SAFE
	}
	
	/**
	 * Represents the path expansion mode for toon input.<br>
	 * Path expansion splits dotted keys into nested objects during parsing.<br>
	 *
	 * @author Luis-St
	 */
	public enum PathExpansion {
		/**
		 * No path expansion.<br>
		 */
		OFF,
		
		/**
		 * Safe path expansion: only expand keys that contain dots.<br>
		 */
		SAFE
	}
}
