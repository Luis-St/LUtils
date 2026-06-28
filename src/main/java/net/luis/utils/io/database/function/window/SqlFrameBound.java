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

import net.luis.utils.io.database.function.window.frame.bound.*;
import org.jspecify.annotations.NonNull;

/**
 * Represents a single bound of a sql window frame.<br>
 * A bound can be unbounded, the current row or a fixed offset preceding or following the current row.<br>
 *
 * @author Luis-St
 */
public interface SqlFrameBound {
	
	/**
	 * Creates a frame bound representing {@code UNBOUNDED PRECEDING}.<br>
	 * @return A new unbounded preceding frame bound
	 */
	static @NonNull SqlFrameBound unboundedPreceding() {
		return new UnboundedPrecedingFrameBound();
	}
	
	/**
	 * Creates a frame bound representing the given number of rows preceding the current row.<br>
	 *
	 * @param offset The number of rows preceding the current row
	 * @return A new preceding frame bound
	 * @throws IllegalArgumentException If the offset is negative
	 */
	static @NonNull SqlFrameBound preceding(int offset) {
		return new PrecedingFrameBound(offset);
	}
	
	/**
	 * Creates a frame bound representing {@code CURRENT ROW}.<br>
	 * @return A new current row frame bound
	 */
	static @NonNull SqlFrameBound currentRow() {
		return new CurrentRowFrameBound();
	}
	
	/**
	 * Creates a frame bound representing the given number of rows following the current row.<br>
	 *
	 * @param offset The number of rows following the current row
	 * @return A new following frame bound
	 * @throws IllegalArgumentException If the offset is negative
	 */
	static @NonNull SqlFrameBound following(int offset) {
		return new FollowingFrameBound(offset);
	}
	
	/**
	 * Creates a frame bound representing {@code UNBOUNDED FOLLOWING}.<br>
	 * @return A new unbounded following frame bound
	 */
	static @NonNull SqlFrameBound unboundedFollowing() {
		return new UnboundedFollowingFrameBound();
	}
}
