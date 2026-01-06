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
 * Constraint interface for collection types with a measurable size.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for constraining the size of collections.<br>
 *     It provides methods to set minimum, maximum, exact, and range-based size constraints,
 *     as well as convenience methods for empty, non-empty, singleton, and pair validation.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface SizeConstraint<T, C> extends BaseConstraint<T, C> {
	
	/**
	 * Applies a minimum size constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have a size greater than or equal to the specified minimum.<br>
	 *     Any attempt to encode or decode a value with fewer elements will result in an error.
	 * </p>
	 *
	 * @param minSize The minimum size (inclusive)
	 * @return A new type with the applied minimum size constraint
	 * @throws IllegalArgumentException If the minimum size is negative
	 * @see #maxSize(int)
	 * @see #sizeBetween(int, int)
	 */
	@NonNull C minSize(int minSize);
	
	/**
	 * Applies a maximum size constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have a size less than or equal to the specified maximum.<br>
	 *     Any attempt to encode or decode a value with more elements will result in an error.
	 * </p>
	 *
	 * @param maxSize The maximum size (inclusive)
	 * @return A new type with the applied maximum size constraint
	 * @throws IllegalArgumentException If the maximum size is negative
	 * @see #minSize(int)
	 * @see #sizeBetween(int, int)
	 */
	@NonNull C maxSize(int maxSize);
	
	/**
	 * Applies an exact size constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have exactly the specified size.<br>
	 *     Any attempt to encode or decode a value with a different number of elements will result in an error.
	 * </p>
	 *
	 * @param exactSize The exact size required
	 * @return A new type with the applied exact size constraint
	 * @throws IllegalArgumentException If the exact size is negative
	 * @see #sizeBetween(int, int)
	 */
	@NonNull C exactSize(int exactSize);
	
	/**
	 * Applies a size range constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have a size within the specified range (inclusive).<br>
	 *     Any attempt to encode or decode a value outside this range will result in an error.
	 * </p>
	 *
	 * @param minSize The minimum size (inclusive)
	 * @param maxSize The maximum size (inclusive)
	 * @return A new type with the applied size range constraint
	 * @throws IllegalArgumentException If the minimum or maximum size is negative, or if the maximum size is less than the minimum size
	 * @see #minSize(int)
	 * @see #maxSize(int)
	 */
	@NonNull C sizeBetween(int minSize, int maxSize);
	
	/**
	 * Applies an empty size constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are empty (size of 0).<br>
	 *     This is a convenience method equivalent to {@code exactSize(0)}.
	 * </p>
	 *
	 * @return A new type with the applied empty size constraint
	 * @see #exactSize(int)
	 * @see #notEmpty()
	 */
	default @NonNull C empty() {
		return this.exactSize(0);
	}
	
	/**
	 * Applies a non-empty size constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values contain at least one element (size &gt;= 1).<br>
	 *     This is a convenience method equivalent to {@code minSize(1)}.
	 * </p>
	 *
	 * @return A new type with the applied non-empty size constraint
	 * @see #minSize(int)
	 * @see #empty()
	 */
	default @NonNull C notEmpty() {
		return this.minSize(1);
	}
	
	/**
	 * Applies a singleton size constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values contain exactly one element.<br>
	 *     This is a convenience method equivalent to {@code exactSize(1)}.
	 * </p>
	 *
	 * @return A new type with the applied singleton size constraint
	 * @see #exactSize(int)
	 * @see #pair()
	 */
	default @NonNull C singleton() {
		return this.exactSize(1);
	}
	
	/**
	 * Applies a pair size constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values contain exactly two elements.<br>
	 *     This is a convenience method equivalent to {@code exactSize(2)}.
	 * </p>
	 *
	 * @return A new type with the applied pair size constraint
	 * @see #exactSize(int)
	 * @see #singleton()
	 */
	default @NonNull C pair() {
		return this.exactSize(2);
	}
}
