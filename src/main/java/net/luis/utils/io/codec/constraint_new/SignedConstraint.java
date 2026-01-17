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
 * Constraint interface for signed numeric types that provides sign-based validation operations.<br>
 * <p>
 *     This interface extends {@link ComparableConstraint} with methods for constraining values based on their sign.<br>
 *     It provides convenience methods for positive, negative, non-negative, non-positive, zero, and non-zero constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface SignedConstraint<T, C> extends ComparableConstraint<T, C> {
	
	/**
	 * Applies a positive value constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are greater than zero.<br>
	 *     This is a convenience method equivalent to {@code greaterThan(zero)}.
	 * </p>
	 *
	 * @return A new type with the applied positive constraint
	 * @see #greaterThan(Object)
	 * @see #negative()
	 */
	@NonNull C positive();
	
	/**
	 * Applies a negative value constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are less than zero.<br>
	 *     This is a convenience method equivalent to {@code lessThan(zero)}.
	 * </p>
	 *
	 * @return A new type with the applied negative constraint
	 * @see #lessThan(Object)
	 * @see #positive()
	 */
	@NonNull C negative();
	
	/**
	 * Applies a non-positive value constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are less than or equal to zero.<br>
	 *     This is a convenience method equivalent to {@code lessThanOrEqual(zero)}.
	 * </p>
	 *
	 * @return A new type with the applied non-positive constraint
	 * @see #lessThanOrEqual(Object)
	 * @see #nonNegative()
	 */
	@NonNull C nonPositive();
	
	/**
	 * Applies a non-negative value constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are greater than or equal to zero.<br>
	 *     This is a convenience method equivalent to {@code greaterThanOrEqual(zero)}.
	 * </p>
	 *
	 * @return A new type with the applied non-negative constraint
	 * @see #greaterThanOrEqual(Object)
	 * @see #nonPositive()
	 */
	@NonNull C nonNegative();
	
	/**
	 * Applies a zero value constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are exactly zero.<br>
	 *     This is a convenience method equivalent to {@code equalTo(zero)}.
	 * </p>
	 *
	 * @return A new type with the applied zero constraint
	 * @see #equalTo(Object)
	 * @see #nonZero()
	 */
	@NonNull C zero();
	
	/**
	 * Applies a non-zero value constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that values are not equal to zero.<br>
	 *     This is a convenience method equivalent to {@code notEqualTo(zero)}.
	 * </p>
	 *
	 * @return A new type with the applied non-zero constraint
	 * @see #notEqualTo(Object)
	 * @see #zero()
	 */
	@NonNull C nonZero();
}
