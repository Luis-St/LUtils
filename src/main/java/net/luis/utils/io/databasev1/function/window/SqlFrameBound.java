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
 * Interface representing a single frame boundary point in a SQL window frame specification.<br>
 *
 * @author Luis-St
 */
@FunctionalInterface
public interface SqlFrameBound extends SqlRenderable {
	
	/**
	 * Creates a frame bound representing all rows before the current partition.<br>
	 * Generates SQL: {@code UNBOUNDED PRECEDING}.<br>
	 *
	 * @return The unbounded preceding frame bound
	 */
	static @NonNull SqlFrameBound unboundedPreceding() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a frame bound representing a fixed number of rows before the current row.<br>
	 * Generates SQL: {@code offset PRECEDING}.<br>
	 *
	 * @param offset The number of rows before the current row
	 * @return The preceding frame bound
	 */
	static @NonNull SqlFrameBound preceding(int offset) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a frame bound representing the current row.<br>
	 * Generates SQL: {@code CURRENT ROW}.<br>
	 *
	 * @return The current row frame bound
	 */
	static @NonNull SqlFrameBound currentRow() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a frame bound representing a fixed number of rows after the current row.<br>
	 * Generates SQL: {@code offset FOLLOWING}.<br>
	 *
	 * @param offset The number of rows after the current row
	 * @return The following frame bound
	 */
	static @NonNull SqlFrameBound following(int offset) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Creates a frame bound representing all rows after the current partition.<br>
	 * Generates SQL: {@code UNBOUNDED FOLLOWING}.<br>
	 *
	 * @return The unbounded following frame bound
	 */
	static @NonNull SqlFrameBound unboundedFollowing() {
		throw new UnsupportedOperationException();
	}
}
