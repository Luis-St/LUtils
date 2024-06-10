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

package net.luis.utils.io.reader;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A utility class to read strings with scopes.<br>
 * A scope is defined by two characters, an opening and a closing character.<br>
 *
 * @author Luis-St
 */
public class ScopedStringReader extends StringReader {
	
	/**
	 * Constant string scope for parentheses.<br>
	 */
	public static final StringScope PARENTHESES = new StringScope('(', ')');
	/**
	 * Constant string scope for curly brackets.<br>
	 */
	public static final StringScope CURLY_BRACKETS = new StringScope('{', '}');
	/**
	 * Constant string scope for square brackets.<br>
	 */
	public static final StringScope SQUARE_BRACKETS = new StringScope('[', ']');
	
	/**
	 * Constructs a new scoped string reader with the given string.<br>
	 * @param string The string to read from
	 * @throws NullPointerException If the string is null
	 */
	public ScopedStringReader(@NotNull String string) {
		super(string);
	}
	
	/**
	 * Reads a string with the given scope.<br>
	 * <p>
	 *     The scope is defined by an opening and a closing character.<br>
	 *     The method will read until the opened scope is closed.<br>
	 *     If there are nested scopes, the method will read until all scopes are closed.<br>
	 * </p>
	 * <p>
	 *     The opening and closing character are included in the returned string.<br>
	 *     If there are no more characters to read, an empty string is returned.<br>
	 * </p>
	 * @param scope The scope to use
	 * @return The read string
	 * @throws NullPointerException If the scope is null
	 * @throws IllegalArgumentException If the given scope has the same opening and closing character or the next character is not the opening character
	 * @throws IllegalStateException If the scope is invalid
	 */
	public @NotNull String readScope(@NotNull StringScope scope) {
		Objects.requireNonNull(scope, "Scope must not be null");
		if (!this.canRead()) {
			return "";
		}
		if (scope.open == scope.close) {
			throw new IllegalArgumentException("Opening and closing character must not be the same");
		}
		char next = this.peek();
		if (next != scope.open) {
			throw new IllegalArgumentException("Expected '" + scope.open + "' but got '" + next + "'");
		}
		StringBuilder builder = new StringBuilder();
		int depth = 0;
		boolean escaped = false;
		boolean inSingleQuotes = false;
		boolean inDoubleQuotes = false;
		while (this.canRead()) {
			char c = this.read();
			if (c == '\\' && !escaped) {
				builder.append(c);
				escaped = true;
				continue;
			}
			if (c == '\'' && !escaped) {
				inSingleQuotes = !inSingleQuotes;
			} else if (c == '\"' && !escaped) {
				inDoubleQuotes = !inDoubleQuotes;
			}
			if (!inSingleQuotes && !inDoubleQuotes && !escaped) {
				if (c == scope.open) {
					depth++;
				} else if (c == scope.close) {
					depth--;
					if (depth == 0) {
						builder.append(c);
						break;
					}
				}
			}
			builder.append(c);
			escaped = false;
		}
		if (depth > 0) {
			throw new IllegalStateException("Invalid scope, " + depth + " scopes are not closed, expected '" + scope.close + "'");
		} else if (depth < 0) {
			throw new IllegalStateException("Invalid scope, " + -depth + " scopes are not opened, expected '" + scope.open + "'");
		}
		return builder.toString();
	}
	
	/**
	 * A record to define a string scope with an opening and a closing character.<br>
	 *
	 * @author Luis-St
	 *
	 * @param open The opening character of the scope
	 * @param close The closing character of the scope
	 */
	public static record StringScope(char open, char close) {}
}
