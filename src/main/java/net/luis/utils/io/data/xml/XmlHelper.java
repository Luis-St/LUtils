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
 *
 * @author Luis-St
 *
 */

class XmlHelper {
	
	private static final Pattern XML_ELEMENT_NAME_PATTERN = Pattern.compile("^[a-z_-][a-z0-9_-]+(:[a-z0-9_-]+)?$", Pattern.CASE_INSENSITIVE);
	private static final Pattern XML_ATTRIBUTE_KEY_PATTERN = Pattern.compile("^[a-z0-9_-]+(:[a-z0-9_-]+)?$", Pattern.CASE_INSENSITIVE);
	
	private static void validateBase(@NotNull String str, @NotNull String message) {
		Objects.requireNonNull(str, message + " must not be null");
		if (str.isEmpty()) {
			throw new IllegalArgumentException(message + " must not be empty");
		}
		if (str.isBlank()) {
			throw new IllegalArgumentException(message + " must not be blank");
		}
	}
	
	static @NotNull String validateElementName(@NotNull String name) {
		validateBase(name, "Xml element name");
		if (!XML_ELEMENT_NAME_PATTERN.matcher(name).matches()) {
			throw new IllegalArgumentException("Xml element name must match the pattern '" + XML_ELEMENT_NAME_PATTERN.pattern() + "', but was: '" + name + "'");
		}
		return name;
	}
	
	static @NotNull String validateAttributeKey(@NotNull String key) {
		validateBase(key, "Xml attribute key");
		if (!XML_ATTRIBUTE_KEY_PATTERN.matcher(key).matches()) {
			throw new IllegalArgumentException("Xml attribute key must match the pattern '" + XML_ATTRIBUTE_KEY_PATTERN.pattern() + "', but was: '" + key + "'");
		}
		return key;
	}
	
	static @NotNull String escapeXml(@NotNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		return value.replace("\"", "&quot;").replace("'", "&apos;").replace("<", "&lt;").replace(">", "&gt;").replace("&", "&amp;");
	}
	
	static @NotNull String unescapeXml(@NotNull String value) {
		Objects.requireNonNull(value, "Value must not be null");
		return value.replace("&quot;", "\"").replace("&apos;", "'").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
	}
}
