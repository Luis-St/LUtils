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
 * Constraint interface for floating-point decimal types that provides decimal-specific validation operations.<br>
 * <p>
 *     This interface extends {@link NumericConstraint} with methods specific to floating-point values.<br>
 *     It provides methods for validating finiteness, NaN, integral values, and normalized form.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface DecimalConstraint<T, C> extends NumericConstraint<T, C> {
	
	/**
	 * Applies a finite value constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that decimal values are finite (not infinite or NaN).<br>
	 *     This constraint ensures the value is neither positive infinity, negative infinity, nor NaN.
	 * </p>
	 *
	 * @return A new type with the applied finite constraint
	 * @see #notNaN()
	 */
	@NonNull C finite();
	
	/**
	 * Applies a not-NaN constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that decimal values are not NaN (Not a Number).<br>
	 *     This constraint allows infinite values but rejects NaN.
	 * </p>
	 *
	 * @return A new type with the applied not-NaN constraint
	 * @see #finite()
	 */
	@NonNull C notNaN();
	
	/**
	 * Applies an integral value constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that decimal values represent whole numbers (no fractional part).<br>
	 *     For example, 5.0 would pass but 5.5 would fail.
	 * </p>
	 *
	 * @return A new type with the applied integral constraint
	 */
	@NonNull C integral();
	
	/**
	 * Applies a normalized form constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that decimal values are in normalized form.<br>
	 *     A decimal is considered normalized if it is within the range [0.0, 1.0].
	 * </p>
	 *
	 * @return A new type with the applied normalized constraint
	 */
	@NonNull C normalized();
}
