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

package net.luis.utils.io.data.json;

import net.luis.utils.io.data.config.ReadOnly;
import net.luis.utils.io.data.config.WriteOnly;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Configuration for reading and writing json elements.<br>
 *
 * @author Luis-St
 *
 * @param strict Whether to use strict json parsing when reading (read-only)
 * @param prettyPrint Whether to pretty print the json (write-only)
 * @param indent The string to use for indentation (write-only)
 * @param simplifyArrays Whether to simplify json arrays (write-only)
 * @param maxArraySimplificationSize The maximum size of a json array to simplify (write-only)
 * @param simplifyObjects Whether to simplify json objects (write-only)
 * @param maxObjectSimplificationSize The maximum size of a json object to simplify (write-only)
 * @param charset The charset to use for reading and writing
 */
public record JsonConfig(
	@ReadOnly boolean strict,
	@WriteOnly boolean prettyPrint,
	@WriteOnly("prettyPrint") @NotNull String indent,
	@WriteOnly("prettyPrint") boolean simplifyArrays,
	@WriteOnly("simplifyArrays") int maxArraySimplificationSize,
	@WriteOnly("prettyPrint") boolean simplifyObjects,
	@WriteOnly("simplifyObjects") int maxObjectSimplificationSize,
	@NotNull Charset charset
) {
	
	/**
	 * The default json configuration.<br>
	 * Strict: true<br>
	 * Pretty print: true<br>
	 * Indent: "\t"<br>
	 * Simplify arrays: true<br>
	 * Max array simplification size: 10<br>
	 * Simplify objects: true<br>
	 * Max object simplification size: 1<br>
	 * Charset: UTF-8<br>
	 */
	public static final JsonConfig DEFAULT = new JsonConfig(true, true, "\t", true, 10, true, 1, StandardCharsets.UTF_8);
	
	/**
	 * Constructs a new json configuration.<br>
	 *
	 * @param strict Whether to use strict json parsing when reading (read-only)
	 * @param prettyPrint Whether to pretty print the json (write-only)
	 * @param indent The string to use for indentation (write-only)
	 * @param simplifyArrays Whether to simplify json arrays (write-only)
	 * @param maxArraySimplificationSize The maximum size of a json array to simplify (write-only)
	 * @param simplifyObjects Whether to simplify json objects (write-only)
	 * @param maxObjectSimplificationSize The maximum size of a json object to simplify (write-only)
	 * @param charset The charset to use for reading and writing
	 * @throws NullPointerException If the indent or charset is null
	 * @throws IllegalArgumentException If the max array or object simplification size is less than 1 and the corresponding simplification is enabled
	 */
	public JsonConfig {
		Objects.requireNonNull(indent, "Indent must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
		if (simplifyArrays && 1 > maxArraySimplificationSize) {
			throw new IllegalArgumentException("Max array simplification size must be greater than 0 if json array should be simplified");
		}
		if (simplifyObjects && 1 > maxObjectSimplificationSize) {
			throw new IllegalArgumentException("Max object simplification size must be greater than 0 if json objects should be simplified");
		}
	}
}
