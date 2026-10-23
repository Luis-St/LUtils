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

import org.jspecify.annotations.NonNull;

/**
 * Represents a position in a string.<br>
 * The line and column are one-based, the index is zero-based.<br>
 *
 * @author Luis-St
 *
 * @param index The zero-based character index
 * @param line The one-based line number
 * @param column The one-based column number in the line
 */
public record StringPosition(int index, int line, int column) {
	
	/**
	 * Constructs a new string position.<br>
	 *
	 * @param index The zero-based character index
	 * @param line The one-based line number
	 * @param column The one-based column number in the line
	 * @throws IllegalArgumentException If the index is negative or the line or column is less than one
	 */
	public StringPosition {
		if (0 > index) {
			throw new IllegalArgumentException("Index must not be negative");
		}
		if (1 > line) {
			throw new IllegalArgumentException("Line must be greater than zero");
		}
		if (1 > column) {
			throw new IllegalArgumentException("Column must be greater than zero");
		}
	}
	
	//region Object overrides
	@Override
	public @NonNull String toString() {
		return "line " + this.line + ", column " + this.column;
	}
	//endregion
}
