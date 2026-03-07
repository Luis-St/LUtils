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

package net.luis.utils.io.databasev1.function.window;

import net.luis.utils.io.databasev1.SqlRenderable;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing a complete SQL window frame clause.<br>
 * A frame clause specifies the subset of rows within a partition to consider.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface SqlWindowFrame extends SqlRenderable {
	
	/**
	 * Creates a row-based frame clause.<br>
	 * Generates SQL: {@code ROWS BETWEEN start AND end}.<br>
	 *
	 * @param start The start bound of the frame
	 * @param end The end bound of the frame
	 * @return The rows frame
	 */
	static @NonNull SqlWindowFrame rows(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a range-based frame clause.<br>
	 * Generates SQL: {@code RANGE BETWEEN start AND end}.<br>
	 *
	 * @param start The start bound of the frame
	 * @param end The end bound of the frame
	 * @return The range frame
	 */
	static @NonNull SqlWindowFrame range(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a groups-based frame clause.<br>
	 * Generates SQL: {@code GROUPS BETWEEN start AND end}.<br>
	 *
	 * @param start The start bound of the frame
	 * @param end The end bound of the frame
	 * @return The groups frame
	 */
	static @NonNull SqlWindowFrame groups(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end) {
		throw new UnsupportedOperationException();
	}
}
