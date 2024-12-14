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

package net.luis.utils.io.data.xml;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Helper class for xml related operations.<br>
 *
 * @author Luis-St
 */
final class XmlHelper {
	
	/**
	 * Pattern for valid xml element names.<br>
	 */
	static final Pattern XML_ELEMENT_NAME_PATTERN = Pattern.compile("^[a-z_-][a-z0-9_-]+(:[a-z0-9_-]+)?$", Pattern.CASE_INSENSITIVE);
	/**
	 * Pattern for valid xml attribute keys.<br>
	 */
	static final Pattern XML_ATTRIBUTE_NAME_PATTERN = Pattern.compile("^[a-z0-9_-]+(:[a-z0-9_-]+)?$", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Private constructor to prevent instantiation of utility class.<br>
	 */
	private XmlHelper() {}
	
	/**
	 * Validates the given string.<br>
	 * The base validation checks if the string is not null, not empty and not blank.<br>
	 * @param str The string to validate
	 * @param message The message to use in the exception
	 * @throws NullPointerException If the string is null
	 * @throws IllegalArgumentException If the string is empty or blank
	 */
	private static void validateBase(@NotNull String str, @NotNull String message) {
		Objects.requireNonNull(str, message + " must not be null");
		if (str.isEmpty()) {
			throw new IllegalArgumentException(message + " must not be empty");
		}
		if (str.isBlank()) {
			throw new IllegalArgumentException(message + " must not be blank");
		}
	}
	
	/**
	 * Validates the given xml element name.<br>
	 * In addition to the base validation, the name must match the pattern {@link #XML_ELEMENT_NAME_PATTERN}.<br>
	 * @param name The name to validate
	 * @return The validated name
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the base validation fails or the name does not match the pattern
	 */
	static @NotNull String validateElementName(@NotNull String name) {
		validateBase(name, "Xml element name");
		if (!XML_ELEMENT_NAME_PATTERN.matcher(name).matches()) {
			throw new IllegalArgumentException("Xml element name must match the pattern '" + XML_ELEMENT_NAME_PATTERN.pattern() + "', but was: '" + name + "'");
		}
		return name;
	}
	
	/**
	 * Validates the given xml attribute name.<br>
	 * In addition to the base validation, the name must match the pattern {@link #XML_ATTRIBUTE_NAME_PATTERN}.<br>
	 * @param name The name to validate
	 * @return The validated name
	 * @throws NullPointerException If the name is null
	 * @throws IllegalArgumentException If the base validation fails or the name does not match the pattern
	 */
	static @NotNull String validateAttributeKey(@NotNull String name) {
		validateBase(name, "Xml attribute name");
		if (!XML_ATTRIBUTE_NAME_PATTERN.matcher(name).matches()) {
			throw new IllegalArgumentException("Xml attribute name must match the pattern '" + XML_ATTRIBUTE_NAME_PATTERN.pattern() + "', but was: '" + name + "'");
		}
		return name;
	}
	
	/**
	 * Escapes the given value for xml.<br>
	 * The following characters are replaced:<br>
	 * <ul>
	 *     <li>{@code & -> &amp;}</li>
	 *     <li>{@code " -> &quot;}</li>
	 *     <li>{@code ' -> &apos;}</li>
	 *     <li>{@code < -> &lt;}</li>
	 *     <li>{@code > -> &gt;}</li>
	 * </ul>
	 * @param value The value to escape
	 * @return The escaped value
	 * @throws NullPointerException If the value is null
	 */
	static @NotNull String escapeXml(@NotNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		return value.replace("&", "&amp;").replace("\"", "&quot;").replace("'", "&apos;").replace("<", "&lt;").replace(">", "&gt;");
	}
	
	/**
	 * Unescapes the given value from xml.<br>
	 * The following characters are replaced:<br>
	 * <ul>
	 *     <li>{@code &amp; -> &}</li>
	 *     <li>{@code &quot; -> "}</li>
	 *     <li>{@code &apos; -> '}</li>
	 *     <li>{@code &lt; -> <}</li>
	 *     <li>{@code &gt; -> >}</li>
	 * </ul>
	 * @param value The value to unescape
	 * @return The unescaped value
	 * @throws NullPointerException If the value is null
	 */
	static @NotNull String unescapeXml(@NotNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		return value.replace("&amp;", "&").replace("&quot;", "\"").replace("&apos;", "'").replace("&lt;", "<").replace("&gt;", ">");
	}
}
