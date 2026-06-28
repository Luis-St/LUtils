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

package net.luis.utils.io.database.function.window;

import net.luis.utils.io.database.function.window.frame.*;
import org.jspecify.annotations.NonNull;

/**
 * Represents the frame of a sql window clause restricting the rows considered within a partition.<br>
 * A frame is defined by a start and an end bound and is interpreted according to its frame mode.<br>
 *
 * @author Luis-St
 */
public interface SqlWindowFrame {
	
	/**
	 * Creates a window frame using the {@code ROWS} frame mode.<br>
	 *
	 * @param start The start bound of the frame
	 * @param end The end bound of the frame
	 * @return A new rows window frame
	 * @throws NullPointerException If the start or end bound is null
	 */
	static @NonNull SqlWindowFrame rows(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end) {
		return new RowsWindowFrame(start, end);
	}
	
	/**
	 * Creates a window frame using the {@code RANGE} frame mode.<br>
	 *
	 * @param start The start bound of the frame
	 * @param end The end bound of the frame
	 * @return A new range window frame
	 * @throws NullPointerException If the start or end bound is null
	 */
	static @NonNull SqlWindowFrame range(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end) {
		return new RangeWindowFrame(start, end);
	}
	
	/**
	 * Creates a window frame using the {@code GROUPS} frame mode.<br>
	 *
	 * @param start The start bound of the frame
	 * @param end The end bound of the frame
	 * @return A new groups window frame
	 * @throws NullPointerException If the start or end bound is null
	 */
	static @NonNull SqlWindowFrame groups(@NonNull SqlFrameBound start, @NonNull SqlFrameBound end) {
		return new GroupsWindowFrame(start, end);
	}
	
	/**
	 * Returns the start bound of this frame.<br>
	 * @return The start bound
	 */
	@NonNull SqlFrameBound start();
	
	/**
	 * Returns the end bound of this frame.<br>
	 * @return The end bound
	 */
	@NonNull SqlFrameBound end();
}
