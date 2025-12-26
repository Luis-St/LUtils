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

package net.luis.utils.io.codec.constraint;

import org.jspecify.annotations.NonNull;

/**
 * A constraint interface providing common comparison operations for any comparable type.<br>
 * This interface defines standard comparison methods that can be reused across different constraint types
 * such as numeric constraints, temporal constraints, character constraints, etc.<br>
 * <br>
 * All comparison operations are based on the natural ordering defined by {@link Comparable}.<br>
 *
 * @author Luis-St
 *
 * @param <T> The comparable type being constrained
 * @param <C> The codec type
 */
public interface ComparableConstraint<T extends Comparable<T>, C> {
	
	/**
	 * Constrains the value to be greater than the specified value (exclusive).<br>
	 * The constraint passes if {@code value > threshold}.<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return The codec with the constraint applied
	 */
	@NonNull C greaterThan(@NonNull T value);
	
	/**
	 * Constrains the value to be greater than or equal to the specified value (inclusive).<br>
	 * The constraint passes if {@code value >= threshold}.<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return The codec with the constraint applied
	 */
	@NonNull C greaterThanOrEqual(@NonNull T value);
	
	/**
	 * Constrains the value to be less than the specified value (exclusive).<br>
	 * The constraint passes if {@code value < threshold}.<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return The codec with the constraint applied
	 */
	@NonNull C lessThan(@NonNull T value);
	
	/**
	 * Constrains the value to be less than or equal to the specified value (inclusive).<br>
	 * The constraint passes if {@code value <= threshold}.<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return The codec with the constraint applied
	 */
	@NonNull C lessThanOrEqual(@NonNull T value);
	
	/**
	 * Constrains the value to be within the specified range (inclusive on both bounds).<br>
	 * The constraint passes if {@code min <= value <= max}.<br>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return The codec with the constraint applied
	 * @throws IllegalArgumentException If {@code min > max}
	 */
	@NonNull C betweenOrEqual(@NonNull T min, @NonNull T max);
	
	/**
	 * Constrains the value to be within the specified range (exclusive on both bounds).<br>
	 * The constraint passes if {@code min < value < max}.<br>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return The codec with the constraint applied
	 * @throws IllegalArgumentException If {@code min >= max}
	 */
	@NonNull C betweenExclusive(@NonNull T min, @NonNull T max);
	
	/**
	 * Constrains the value to be equal to the specified value.<br>
	 * The constraint passes if {@code value.equals(target)}.<br>
	 *
	 * @param value The target value
	 * @return The codec with the constraint applied
	 */
	@NonNull C equalTo(@NonNull T value);
	
	/**
	 * Constrains the value to not be equal to the specified value.<br>
	 * The constraint passes if {@code !value.equals(target)}.<br>
	 *
	 * @param value The value to exclude
	 * @return The codec with the constraint applied
	 */
	@NonNull C notEqualTo(@NonNull T value);
}
