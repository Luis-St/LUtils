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

package net.luis.utils.io.data.yaml;

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Helper class for yaml related operations.<br>
 *
 * @author Luis-St
 */
final class YamlHelper {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private YamlHelper() {}
	
	/**
	 * Checks if the given name is a valid anchor name.<br>
	 * Valid anchor names contain only alphanumeric characters, underscores, and hyphens.<br>
	 *
	 * @param name The name to check
	 * @return True if the name is valid, false otherwise
	 * @throws NullPointerException If the name is null
	 */
	static boolean isValidAnchorName(@NonNull String name) {
		Objects.requireNonNull(name, "Name must not be null");
		
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != '_' && c != '-') {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Checks if the given character is a special character in yaml.<br>
	 * Special characters are: {@code # & * ! | > ' " % @ ` { [ - ?}<br>
	 *
	 * @param c The character to check
	 * @return True if the character is a special character, false otherwise
	 */
	static boolean isYamlSpecialCharacter(char c) {
		return c == '#' || c == '&' || c == '*' || c == '!' || c == '|' ||
			c == '>' || c == '\'' || c == '"' || c == '%' || c == '@' ||
			c == '`' || c == '{' || c == '[' || c == '-' || c == '?';
	}
	
	/**
	 * Escapes special characters in a string for YAML double-quoted output.<br>
	 *
	 * @param string The string to escape
	 * @return The escaped string
	 * @throws NullPointerException If the string is null
	 */
	static @NonNull String escapeString(@NonNull String string) {
		Objects.requireNonNull(string, "String must not be null");
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
				case '"' -> builder.append("\\\"");
				case '\\' -> builder.append("\\\\");
				case '\n' -> builder.append("\\n");
				case '\r' -> builder.append("\\r");
				case '\t' -> builder.append("\\t");
				default -> builder.append(c);
			}
		}
		return builder.toString();
	}
}
