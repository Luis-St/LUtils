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

package net.luis.utils.io.data.yaml;

import net.luis.utils.io.data.config.ReadOnly;
import net.luis.utils.io.data.config.WriteOnly;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Configuration for reading and writing yaml elements.<br>
 *
 * @author Luis-St
 *
 * @param strict Whether to use strict yaml parsing when reading (read-only)
 * @param prettyPrint Whether to pretty print the yaml (write-only)
 * @param indent The string to use for indentation (write-only)
 * @param useBlockStyle Whether to prefer block style over flow style for collections (write-only)
 * @param useDocumentMarkers Whether to include document markers (--- and ...) (write-only)
 * @param nullStyle The style to use for null values (write-only)
 * @param resolveAnchors Whether to resolve anchors and aliases when reading (read-only)
 * @param allowDuplicateKeys Whether to allow duplicate keys in mappings when reading (read-only)
 * @param charset The charset to use for reading and writing
 */
public record YamlConfig(
	@ReadOnly boolean strict,
	@WriteOnly boolean prettyPrint,
	@WriteOnly("prettyPrint") @NotNull String indent,
	@WriteOnly("prettyPrint") boolean useBlockStyle,
	@WriteOnly boolean useDocumentMarkers,
	@WriteOnly @NotNull NullStyle nullStyle,
	@ReadOnly boolean resolveAnchors,
	@ReadOnly boolean allowDuplicateKeys,
	@NotNull Charset charset
) {
	
	/**
	 * The default yaml configuration.<br>
	 * Strict: true<br>
	 * Pretty print: true<br>
	 * Indent: "  " (2 spaces)<br>
	 * Use block style: true<br>
	 * Use document markers: false<br>
	 * Null style: NULL<br>
	 * Resolve anchors: true<br>
	 * Allow duplicate keys: false<br>
	 * Charset: UTF-8<br>
	 */
	public static final YamlConfig DEFAULT = new YamlConfig(true, true, "  ", true, false, NullStyle.NULL, true, false, StandardCharsets.UTF_8);
	/**
	 * Configuration that preserves anchor/alias structure for inspection.<br>
	 * Same as default but with resolveAnchors set to false.<br>
	 */
	public static final YamlConfig PRESERVE_ANCHORS = new YamlConfig(true, true, "  ", true, false, NullStyle.NULL, false, false, StandardCharsets.UTF_8);
	
	/**
	 * Constructs a new yaml configuration.<br>
	 *
	 * @param strict Whether to use strict yaml parsing when reading (read-only)
	 * @param prettyPrint Whether to pretty print the yaml (write-only)
	 * @param indent The string to use for indentation (write-only)
	 * @param useBlockStyle Whether to prefer block style over flow style for collections (write-only)
	 * @param useDocumentMarkers Whether to include document markers (--- and ...) (write-only)
	 * @param nullStyle The style to use for null values (write-only)
	 * @param resolveAnchors Whether to resolve anchors and aliases when reading (read-only)
	 * @param allowDuplicateKeys Whether to allow duplicate keys in mappings when reading (read-only)
	 * @param charset The charset to use for reading and writing
	 * @throws NullPointerException If the indent, null style, or charset is null
	 */
	public YamlConfig {
		Objects.requireNonNull(indent, "Indent must not be null");
		Objects.requireNonNull(nullStyle, "Null style must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
	}
	
	/**
	 * The style to use for null values when writing yaml.<br>
	 */
	public enum NullStyle {
		/**
		 * Outputs null values as "null".<br>
		 */
		NULL,
		/**
		 * Outputs null values as "~".<br>
		 */
		TILDE,
		/**
		 * Outputs null values as empty string.<br>
		 */
		EMPTY
	}
}
