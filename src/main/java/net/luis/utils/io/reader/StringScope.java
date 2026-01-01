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

package net.luis.utils.io.reader;

import java.util.HashMap;
import java.util.Map;

/**
 * A record to define a string scope with an opening and a closing character.<br>
 *
 * @author Luis-St
 *
 * @param open The opening character of the scope
 * @param close The closing character of the scope
 */
public record StringScope(char open, char close) {
	
	/**
	 * A map that stores all created scopes.<br>
	 * The key is the opening character and the value is the closing character.<br>
	 */
	public static final Map<Character, Character> SCOPE_REGISTRY = new HashMap<>();
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
	 * Constant string scope for angle brackets.<br>
	 */
	public static final StringScope ANGLE_BRACKETS = new StringScope('<', '>');
	
	/**
	 * Constructs a new string scope with the given opening and closing character.<br>
	 *
	 * @param open The opening character
	 * @param close The closing character
	 * @throws IllegalArgumentException If the opening and closing character are the same
	 */
	public StringScope {
		if (open == close) {
			throw new IllegalArgumentException("Opening and closing character must not be the same");
		}
		if (open == '\0' || close == '\0') {
			throw new IllegalArgumentException("Opening and closing character must not be the null character");
		}
		SCOPE_REGISTRY.put(open, close);
	}
}
