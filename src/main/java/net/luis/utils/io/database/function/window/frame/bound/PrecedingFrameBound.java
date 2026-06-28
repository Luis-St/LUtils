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

package net.luis.utils.io.database.function.window.frame.bound;

import net.luis.utils.io.database.function.window.SqlFrameBound;

/**
 * Frame bound representing a fixed number of rows preceding the current row.<br>
 *
 * @author Luis-St
 *
 * @param offset The number of rows preceding the current row
 */
public record PrecedingFrameBound(int offset) implements SqlFrameBound {
	
	/**
	 * Constructs a new preceding frame bound validating the offset.<br>
	 * @throws IllegalArgumentException If the offset is negative
	 */
	public PrecedingFrameBound {
		if (offset < 0) {
			throw new IllegalArgumentException("Sql frame bound offset must not be negative");
		}
	}
}
