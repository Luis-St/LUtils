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

package net.luis.utils.io.codec.constraint_new.numeric;

import org.jspecify.annotations.NonNull;

/**
 * Constraint interface for integer types that provides integer-specific validation operations.<br>
 * <p>
 *     This interface extends {@link NumericConstraint} with methods specific to integer values.<br>
 *     It provides methods for validating divisibility, parity (even/odd), and power-of-base constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface IntegerConstraint<T, C> extends NumericConstraint<T, C> {
	
	/**
	 * Applies an even number constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that integer values are even (divisible by 2).
	 * </p>
	 *
	 * @return A new type with the applied even constraint
	 * @see #odd()
	 * @see #divisibleBy(long)
	 */
	@NonNull C even();
	
	/**
	 * Applies an odd number constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that integer values are odd (not divisible by 2).
	 * </p>
	 *
	 * @return A new type with the applied odd constraint
	 * @see #even()
	 */
	@NonNull C odd();
	
	/**
	 * Applies a divisibility constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that integer values are divisible by the specified divisor.
	 * </p>
	 *
	 * @param divisor The divisor that values must be divisible by
	 * @return A new type with the applied divisibility constraint
	 * @throws IllegalArgumentException If the divisor is zero
	 * @see #even()
	 */
	@NonNull C divisibleBy(long divisor);
	
	/**
	 * Applies a power-of-two constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that integer values are powers of 2 (1, 2, 4, 8, 16, ...).<br>
	 *     This is a convenience method equivalent to {@code powerOf(2)}.
	 * </p>
	 *
	 * @return A new type with the applied power-of-two constraint
	 * @see #powerOf(int)
	 */
	default @NonNull C powerOfTwo() {
		return this.powerOf(2);
	}
	
	/**
	 * Applies a power-of-base constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that integer values are powers of the specified base.<br>
	 *     For example, {@code powerOf(3)} would accept 1, 3, 9, 27, 81, etc.
	 * </p>
	 *
	 * @param base The base that values must be a power of
	 * @return A new type with the applied power-of-base constraint
	 * @throws IllegalArgumentException If the base is less than 2
	 * @see #powerOfTwo()
	 */
	@NonNull C powerOf(int base);
}
