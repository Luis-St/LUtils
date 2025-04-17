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

/**
 *
 * @author Luis-St
 *
 */

public record TokenPosition(int line, int character, int characterInLine) {
	
	public TokenPosition {
		if (line < 0) {
			throw new IllegalArgumentException("Line number cannot be negative");
		}
		if (character < 0) {
			throw new IllegalArgumentException("Character number cannot be negative");
		}
		if (characterInLine < 0) {
			throw new IllegalArgumentException("Character in line number cannot be negative");
		}
	}
}
