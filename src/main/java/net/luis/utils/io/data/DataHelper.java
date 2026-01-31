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

package net.luis.utils.io.data;

import net.luis.utils.io.reader.StringReader;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Helper class for common data operations.<br>
 *
 * @author Luis-St
 */
public final class DataHelper {
	
	/**
	 * Private constructor to prevent instantiation.<br>
	 * This is a static helper class.<br>
	 */
	private DataHelper() {}
	
	/**
	 * Tries to parse the given string to a boolean or number.<br>
	 *
	 * @param string The string
	 * @return The parsed value or the string if it could not be parsed
	 * @throws NullPointerException If the string is null
	 * @see #tryParseNumber(String)
	 */
	public static @NonNull Object tryParse(@NonNull String string) {
		Objects.requireNonNull(string, "String must not be null");
		if ("true".equalsIgnoreCase(string) || "false".equalsIgnoreCase(string)) {
			return Boolean.parseBoolean(string);
		}
		
		return tryParseNumber(string);
	}
	
	/**
	 * Tries to parse the given string to a number.<br>
	 *
	 * @param string The string
	 * @return The parsed number or the string if it could not be parsed
	 * @throws NullPointerException If the string is null
	 * @see StringReader#readNumber()
	 */
	public static @NonNull Object tryParseNumber(@NonNull String string) {
		Objects.requireNonNull(string, "String must not be null");
		
		StringReader reader = new StringReader(string);
		try {
			Number number = reader.readNumber();
			reader.skipWhitespaces();
			
			if (reader.canRead()) {
				return string;
			}
			return number;
		} catch (Exception _) {
			return string;
		}
	}
}
