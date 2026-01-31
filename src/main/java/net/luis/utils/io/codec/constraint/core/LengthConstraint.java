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

package net.luis.utils.io.codec.constraint.core;

import org.jspecify.annotations.NonNull;

/**
 * Constraint interface for types with a measurable length such as strings or arrays.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for constraining the length of values.<br>
 *     It provides methods to set minimum, maximum, exact, and range-based length constraints,
 *     as well as convenience methods for empty and non-empty validation.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface LengthConstraint<T, C> extends BaseConstraint<T, C> {
	
	/**
	 * Applies a minimum length constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have a length greater than or equal to the specified minimum.<br>
	 *     Any attempt to encode or decode a value with fewer characters/elements will result in an error.
	 * </p>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @return A new type with the applied minimum length constraint
	 * @throws IllegalArgumentException If the minimum length is negative
	 * @see #maxLength(int)
	 * @see #lengthBetween(int, int)
	 */
	@NonNull C minLength(int minLength);
	
	/**
	 * Applies a maximum length constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have a length less than or equal to the specified maximum.<br>
	 *     Any attempt to encode or decode a value with more characters/elements will result in an error.
	 * </p>
	 *
	 * @param maxLength The maximum length (inclusive)
	 * @return A new type with the applied maximum length constraint
	 * @throws IllegalArgumentException If the maximum length is negative
	 * @see #minLength(int)
	 * @see #lengthBetween(int, int)
	 */
	@NonNull C maxLength(int maxLength);
	
	/**
	 * Applies an exact length constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have exactly the specified length.<br>
	 *     Any attempt to encode or decode a value with a different number of characters/elements will result in an error.
	 * </p>
	 *
	 * @param exactLength The exact length required
	 * @return A new type with the applied exact length constraint
	 * @throws IllegalArgumentException If the exact length is negative
	 * @see #lengthBetween(int, int)
	 */
	@NonNull C exactLength(int exactLength);
	
	/**
	 * Applies a length range constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values have a length within the specified range (inclusive).<br>
	 *     Any attempt to encode or decode a value outside this range will result in an error.
	 * </p>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @param maxLength The maximum length (inclusive)
	 * @return A new type with the applied length range constraint
	 * @throws IllegalArgumentException If the minimum or maximum length is negative, or if the maximum length is less than the minimum length
	 * @see #minLength(int)
	 * @see #maxLength(int)
	 */
	@NonNull C lengthBetween(int minLength, int maxLength);
	
	/**
	 * Applies an empty length constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are empty (length of 0).<br>
	 *     This is a convenience method equivalent to {@code exactLength(0)}.
	 * </p>
	 *
	 * @return A new type with the applied empty length constraint
	 * @see #exactLength(int)
	 * @see #notEmpty()
	 */
	default @NonNull C empty() {
		return this.exactLength(0);
	}
	
	/**
	 * Applies a non-empty length constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values contain at least one character/element (length &gt;= 1).<br>
	 *     This is a convenience method equivalent to {@code minLength(1)}.
	 * </p>
	 *
	 * @return A new type with the applied non-empty length constraint
	 * @see #minLength(int)
	 * @see #empty()
	 */
	default @NonNull C notEmpty() {
		return this.minLength(1);
	}
}
