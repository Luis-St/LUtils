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

import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Helper class for toml related operations.<br>
 *
 * @author Luis-St
 */
final class TomlHelper {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private TomlHelper() {}
	
	/**
	 * Formats a key for toml output, quoting if necessary.<br>
	 *
	 * @param key The key to format
	 * @return The formatted key
	 * @throws NullPointerException If the given key is null
	 */
	static @NonNull String formatKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (isBareKey(key)) {
			return key;
		}
		return "\"" + escapeString(key) + "\"";
	}
	
	/**
	 * Checks if the given key is a valid bare key in toml.<br>
	 *
	 * @param key The key to check
	 * @return True if the key is a valid bare key, false otherwise
	 * @throws NullPointerException If the key is null
	 */
	private static boolean isBareKey(@NonNull String key) {
		Objects.requireNonNull(key, "Key must not be null");
		if (key.isEmpty()) {
			return false;
		}
		
		for (int i = 0; i < key.length(); i++) {
			char c = key.charAt(i);
			if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-')) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Escapes special characters in a string for toml output.<br>
	 *
	 * @param str The string to escape
	 * @return The escaped string
	 * @throws NullPointerException If the given string is null
	 */
	static @NonNull String escapeString(@NonNull String str) {
		Objects.requireNonNull(str, "String must not be null");
		
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			switch (c) {
				case '"' -> result.append("\\\"");
				case '\\' -> result.append("\\\\");
				case '\b' -> result.append("\\b");
				case '\f' -> result.append("\\f");
				case '\n' -> result.append("\\n");
				case '\r' -> result.append("\\r");
				case '\t' -> result.append("\\t");
				default -> {
					if (c < 0x20) {
						result.append(String.format("\\u%04X", (int) c));
					} else {
						result.append(c);
					}
				}
			}
		}
		return result.toString();
	}
}
