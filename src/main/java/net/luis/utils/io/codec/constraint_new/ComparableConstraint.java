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
 * Constraint interface for comparable types that provides comparison-based validation operations.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for comparing values using greater than,
 *     less than, and range-based constraints.<br>
 *     It is suitable for any type that has a natural ordering and can be compared.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface ComparableConstraint<T, C> extends BaseConstraint<T, C> {
	
	/**
	 * Constrains the value to be greater than the specified value (exclusive).<br>
	 * The constraint passes if {@code value > threshold}.<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return The type with the constraint applied
	 */
	@NonNull C greaterThan(@NonNull T value);
	
	/**
	 * Constrains the value to be greater than or equal to the specified value (inclusive).<br>
	 * The constraint passes if {@code value >= threshold}.<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return The type with the constraint applied
	 */
	@NonNull C greaterThanOrEqual(@NonNull T value);
	
	/**
	 * Constrains the value to be less than the specified value (exclusive).<br>
	 * The constraint passes if {@code value < threshold}.<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return The type with the constraint applied
	 */
	@NonNull C lessThan(@NonNull T value);
	
	/**
	 * Constrains the value to be less than or equal to the specified value (inclusive).<br>
	 * The constraint passes if {@code value <= threshold}.<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return The type with the constraint applied
	 */
	@NonNull C lessThanOrEqual(@NonNull T value);
	
	/**
	 * Constrains the value to be within the specified range (exclusive on both bounds).<br>
	 * The constraint passes if {@code min < value < max}.<br>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return The type with the constraint applied
	 * @throws IllegalArgumentException If {@code min >= max}
	 */
	@NonNull C between(@NonNull T min, @NonNull T max);
	
	/**
	 * Constrains the value to be within the specified range (inclusive on both bounds).<br>
	 * The constraint passes if {@code min <= value <= max}.<br>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return The type with the constraint applied
	 * @throws IllegalArgumentException If {@code min > max}
	 */
	@NonNull C betweenOrEqual(@NonNull T min, @NonNull T max);
}
