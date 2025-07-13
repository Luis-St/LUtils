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

package net.luis.utils.io.data.xml;

import net.luis.utils.io.data.config.WriteOnly;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Configuration for reading and writing xml elements.<br>
 *
 * @author Luis-St
 *
 * @param strict Whether to use strict xml parsing when reading
 * @param prettyPrint Whether to pretty print the xml (write-only)
 * @param indent The string to use for indentation (write-only)
 * @param allowAttributes Whether to allow attributes in xml elements
 * @param simplifyValues Whether to simplify xml values (write-only)
 * @param charset The charset to use for reading and writing
 */
public record XmlConfig(
	boolean strict,
	@WriteOnly boolean prettyPrint,
	@WriteOnly("prettyPrint") @NotNull String indent,
	boolean allowAttributes,
	@WriteOnly boolean simplifyValues,
	@NotNull Charset charset
) {
	
	/**
	 * The default xml configuration.<br>
	 * Strict: true<br>
	 * Pretty print: true<br>
	 * Indent: "\t"<br>
	 * Allow attributes: true<br>
	 * Simplify values: true<br>
	 * Charset: UTF-8<br>
	 */
	public static final XmlConfig DEFAULT = new XmlConfig(true, true, "\t", true, true, StandardCharsets.UTF_8);
	
	/**
	 * Constructs a new xml configuration.<br>
	 *
	 * @param strict Whether to use strict xml parsing when reading
	 * @param prettyPrint Whether to pretty print the xml (write-only)
	 * @param indent The string to use for indentation (write-only)
	 * @param allowAttributes Whether to allow attributes in xml elements
	 * @param simplifyValues Whether to simplify xml values (write-only)
	 * @param charset The charset to use for reading and writing
	 * @throws NullPointerException If the indent or charset is null
	 */
	public XmlConfig {
		Objects.requireNonNull(indent, "Indent must not be null");
		Objects.requireNonNull(charset, "Charset must not be null");
	}
}
