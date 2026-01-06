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

import java.math.BigDecimal;

/**
 * Constraint interface for BigDecimal types that provides precision and scale validation operations.<br>
 * <p>
 *     This interface extends {@link NumericConstraint} with methods specific to {@link BigDecimal} values.<br>
 *     It provides methods for validating integral values, normalized form, scale (decimal places), and precision (significant digits).
 * </p>
 *
 * @author Luis-St
 *
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface BigDecimalConstraint<C> extends NumericConstraint<BigDecimal, C> {
	
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
	
	/**
	 * Applies an exact scale constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that BigDecimal values have exactly the specified number of digits after the decimal point.
	 * </p>
	 *
	 * @param scale The exact number of decimal places required
	 * @return A new type with the applied scale constraint
	 * @see #minScale(int)
	 * @see #maxScale(int)
	 */
	@NonNull C scale(int scale);
	
	/**
	 * Applies a minimum scale constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that BigDecimal values have at least the specified number of digits after the decimal point.
	 * </p>
	 *
	 * @param minScale The minimum number of decimal places required
	 * @return A new type with the applied minimum scale constraint
	 * @see #scale(int)
	 * @see #scaleBetween(int, int)
	 */
	@NonNull C minScale(int minScale);
	
	/**
	 * Applies a maximum scale constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that BigDecimal values have at most the specified number of digits after the decimal point.
	 * </p>
	 *
	 * @param maxScale The maximum number of decimal places allowed
	 * @return A new type with the applied maximum scale constraint
	 * @see #scale(int)
	 * @see #scaleBetween(int, int)
	 */
	@NonNull C maxScale(int maxScale);
	
	/**
	 * Applies a scale range constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that BigDecimal values have a number of decimal places within the specified range (inclusive).
	 * </p>
	 *
	 * @param minScale The minimum number of decimal places required
	 * @param maxScale The maximum number of decimal places allowed
	 * @return A new type with the applied scale range constraint
	 * @see #minScale(int)
	 * @see #maxScale(int)
	 */
	@NonNull C scaleBetween(int minScale, int maxScale);
	
	/**
	 * Applies an exact precision constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that BigDecimal values have exactly the specified total number of significant digits.
	 * </p>
	 *
	 * @param precision The exact number of significant digits required
	 * @return A new type with the applied precision constraint
	 * @see #minPrecision(int)
	 * @see #maxPrecision(int)
	 */
	@NonNull C precision(int precision);
	
	/**
	 * Applies a minimum precision constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that BigDecimal values have at least the specified number of significant digits.
	 * </p>
	 *
	 * @param minPrecision The minimum number of significant digits required
	 * @return A new type with the applied minimum precision constraint
	 * @see #precision(int)
	 * @see #precisionBetween(int, int)
	 */
	@NonNull C minPrecision(int minPrecision);
	
	/**
	 * Applies a maximum precision constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that BigDecimal values have at most the specified number of significant digits.
	 * </p>
	 *
	 * @param maxPrecision The maximum number of significant digits allowed
	 * @return A new type with the applied maximum precision constraint
	 * @see #precision(int)
	 * @see #precisionBetween(int, int)
	 */
	@NonNull C maxPrecision(int maxPrecision);
	
	/**
	 * Applies a precision range constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that BigDecimal values have a number of significant digits within the specified range (inclusive).
	 * </p>
	 *
	 * @param minPrecision The minimum number of significant digits required
	 * @param maxPrecision The maximum number of significant digits allowed
	 * @return A new type with the applied precision range constraint
	 * @see #minPrecision(int)
	 * @see #maxPrecision(int)
	 */
	@NonNull C precisionBetween(int minPrecision, int maxPrecision);
}
