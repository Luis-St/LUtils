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

package net.luis.utils.io.codec.constraint_new.temporal;

import net.luis.utils.io.codec.constraint_new.BaseConstraint;
import org.jspecify.annotations.NonNull;

/**
 * Constraint interface for temporal types that provides time-based comparison operations.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for constraining temporal values
 *     based on before/after comparisons and range validation.<br>
 *     It is suitable for date, time, and datetime types that support temporal ordering.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface TemporalComparableConstraint<T, C> extends BaseConstraint<T, C> {
	
	/**
	 * Applies an "after" constraint to the type (exclusive).<br>
	 * <p>
	 *     The returned type will validate that values are strictly after the specified time.<br>
	 *     For example, if the constraint is {@code after(10:00)}, then {@code 10:00:01} passes but {@code 10:00:00} fails.
	 * </p>
	 *
	 * @param value The temporal value that constrained values must be after
	 * @return A new type with the applied after constraint
	 * @throws NullPointerException If the value is null
	 * @see #afterOrEqual(Object)
	 * @see #before(Object)
	 */
	@NonNull C after(@NonNull T value);
	
	/**
	 * Applies an "after or equal" constraint to the type (inclusive).<br>
	 * <p>
	 *     The returned type will validate that values are after or equal to the specified time.<br>
	 *     For example, if the constraint is {@code afterOrEqual(10:00)}, then both {@code 10:00:00} and {@code 10:00:01} pass.
	 * </p>
	 *
	 * @param value The temporal value that constrained values must be after or equal to
	 * @return A new type with the applied after-or-equal constraint
	 * @throws NullPointerException If the value is null
	 * @see #after(Object)
	 * @see #beforeOrEqual(Object)
	 */
	@NonNull C afterOrEqual(@NonNull T value);
	
	/**
	 * Applies a "before" constraint to the type (exclusive).<br>
	 * <p>
	 *     The returned type will validate that values are strictly before the specified time.<br>
	 *     For example, if the constraint is {@code before(10:00)}, then {@code 09:59:59} passes but {@code 10:00:00} fails.
	 * </p>
	 *
	 * @param value The temporal value that constrained values must be before
	 * @return A new type with the applied before constraint
	 * @throws NullPointerException If the value is null
	 * @see #beforeOrEqual(Object)
	 * @see #after(Object)
	 */
	@NonNull C before(@NonNull T value);
	
	/**
	 * Applies a "before or equal" constraint to the type (inclusive).<br>
	 * <p>
	 *     The returned type will validate that values are before or equal to the specified time.<br>
	 *     For example, if the constraint is {@code beforeOrEqual(10:00)}, then both {@code 09:59:59} and {@code 10:00:00} pass.
	 * </p>
	 *
	 * @param value The temporal value that constrained values must be before or equal to
	 * @return A new type with the applied before-or-equal constraint
	 * @throws NullPointerException If the value is null
	 * @see #before(Object)
	 * @see #afterOrEqual(Object)
	 */
	@NonNull C beforeOrEqual(@NonNull T value);
	
	/**
	 * Applies a temporal range constraint to the type (exclusive bounds).<br>
	 * <p>
	 *     The returned type will validate that values are strictly within the specified time range.<br>
	 *     For example, if the constraint is {@code between(09:00, 17:00)}, then {@code 10:00} passes but {@code 09:00} and {@code 17:00} fail.
	 * </p>
	 *
	 * @param min The minimum temporal value (exclusive)
	 * @param max The maximum temporal value (exclusive)
	 * @return A new type with the applied range constraint
	 * @throws NullPointerException If min or max is null
	 * @throws IllegalArgumentException If min is after or equal to max
	 * @see #betweenOrEqual(Object, Object)
	 */
	@NonNull C between(@NonNull T min, @NonNull T max);
	
	/**
	 * Applies a temporal range constraint to the type (inclusive bounds).<br>
	 * <p>
	 *     The returned type will validate that values are within the specified time range, including boundaries.<br>
	 *     For example, if the constraint is {@code betweenOrEqual(09:00, 17:00)}, then {@code 09:00}, {@code 10:00}, and {@code 17:00} all pass.
	 * </p>
	 *
	 * @param min The minimum temporal value (inclusive)
	 * @param max The maximum temporal value (inclusive)
	 * @return A new type with the applied range constraint
	 * @throws NullPointerException If min or max is null
	 * @throws IllegalArgumentException If min is after max
	 * @see #between(Object, Object)
	 */
	@NonNull C betweenOrEqual(@NonNull T min, @NonNull T max);
}
