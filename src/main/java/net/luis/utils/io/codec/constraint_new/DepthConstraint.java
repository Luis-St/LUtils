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

package net.luis.utils.io.codec.constraint_new;

import org.jspecify.annotations.NonNull;

/**
 * Constraint interface for types with a measurable depth such as paths or hierarchical structures.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for constraining the depth of values.<br>
 *     It provides methods to set minimum, maximum, exact, and range-based depth constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface DepthConstraint<T, C> extends BaseConstraint<T, C> {
	
	/**
	 * Applies a minimum depth constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have a depth greater than or equal to the specified minimum.<br>
	 *     Any attempt to encode or decode a value with less depth will result in an error.
	 * </p>
	 *
	 * @param minDepth The minimum depth (inclusive)
	 * @return A new type with the applied minimum depth constraint
	 * @throws IllegalArgumentException If the minimum depth is negative
	 * @see #maxDepth(int)
	 * @see #depthBetween(int, int)
	 */
	@NonNull C minDepth(int minDepth);
	
	/**
	 * Applies a maximum depth constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have a depth less than or equal to the specified maximum.<br>
	 *     Any attempt to encode or decode a value with more depth will result in an error.
	 * </p>
	 *
	 * @param maxDepth The maximum depth (inclusive)
	 * @return A new type with the applied maximum depth constraint
	 * @throws IllegalArgumentException If the maximum depth is negative
	 * @see #minDepth(int)
	 * @see #depthBetween(int, int)
	 */
	@NonNull C maxDepth(int maxDepth);
	
	/**
	 * Applies an exact depth constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have exactly the specified depth.<br>
	 *     Any attempt to encode or decode a value with a different depth will result in an error.
	 * </p>
	 *
	 * @param exactDepth The exact depth required
	 * @return A new type with the applied exact depth constraint
	 * @throws IllegalArgumentException If the exact depth is negative
	 * @see #depthBetween(int, int)
	 */
	@NonNull C exactDepth(int exactDepth);
	
	/**
	 * Applies a depth range constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have a depth within the specified range (inclusive).<br>
	 *     Any attempt to encode or decode a value outside this range will result in an error.
	 * </p>
	 *
	 * @param minDepth The minimum depth (inclusive)
	 * @param maxDepth The maximum depth (inclusive)
	 * @return A new type with the applied depth range constraint
	 * @throws IllegalArgumentException If the minimum or maximum depth is negative, or if the maximum depth is less than the minimum depth
	 * @see #minDepth(int)
	 * @see #maxDepth(int)
	 */
	@NonNull C depthBetween(int minDepth, int maxDepth);
}
