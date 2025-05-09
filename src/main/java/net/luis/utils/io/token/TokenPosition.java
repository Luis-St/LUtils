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

package net.luis.utils.io.token;

import net.luis.utils.util.unsafe.StackTraceUtils;

/**
 * Class which represents the position of a token in a file.<br>
 * The line, character in line and character numbers are 0-based.<br>
 * <p>
 *     The unpositioned constant is used to represent a token that is not positioned.<br>
 *     A token is considered unpositioned if it has not been assigned a position in the file.<br>
 *     This is useful for tokens that have been added after the tokenization process.<br>
 * </p>
 *
 * @author Luis-St
 *
 * @param line The line number of the token
 * @param characterInLine The character number in the line of the token
 * @param character The character number of the token in the file
 */
public record TokenPosition(
	int line,
	int characterInLine,
	int character
) {
	
	/**
	 * Constant which represents an unpositioned token.<br>
	 */
	public static final TokenPosition UNPOSITIONED = new TokenPosition(-1, -1, -1);
	
	/**
	 * Constructs a new token position.<br>
	 * @param line The line number of the token
	 * @param characterInLine The character number in the line of the token
	 * @param character The character number of the token in the file
	 * @throws IllegalArgumentException If the line, character in line or character numbers are negative
	 * @apiNote The unpositioned constant uses -1 for all values to indicate that the token is not positioned.
	 * Since the constructor does not accept negative values, there is an exception for callers from within the class.
	 */
	public TokenPosition {
		boolean callWithinClass = StackTraceUtils.getCallingClass() == TokenPosition.class;
		
		if (line < (callWithinClass ? -1 : 0)) {
			throw new IllegalArgumentException("Line number cannot be negative");
		}
		if (characterInLine < (callWithinClass ? -1 : 0)) {
			throw new IllegalArgumentException("Character in line number cannot be negative");
		}
		if (character < (callWithinClass ? -1 : 0)) {
			throw new IllegalArgumentException("Character number cannot be negative");
		}
	}
	
	/**
	 * Checks if the token is positioned.<br>
	 * A token is considered positioned if it has a valid line, character in line and character number (non-negative).<br>
	 * @return True if the token is positioned, false if it is unpositioned
	 */
	public boolean isPositioned() {
		return this != UNPOSITIONED;
	}
}
