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

package net.luis.utils.io.codec.constraint.merged.numeric;

import net.luis.utils.io.codec.constraint.core.NumericConstraint;
import net.luis.utils.io.codec.constraint.core.ApplicableConstraint;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.numeric.BigDecimalConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.function.UnaryOperator;

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
@FunctionalInterface
public interface BigDecimalConstraint<C> extends ApplicableConstraint<BigDecimalConstraintConfig, C>, NumericConstraint<BigDecimal, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<BigDecimalConstraintConfig> configModifier);
	
	/**
	 * Applies an integral value constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that decimal values represent whole numbers (no fractional part).<br>
	 *     For example, 5.0 would pass but 5.5 would fail.
	 * </p>
	 *
	 * @return A new type with the applied integral constraint
	 */
	default @NonNull C integral() {
		return this.apply(BigDecimalConstraintConfig::withIntegral);
	}
	
	/**
	 * Applies a normalized form constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that decimal values are in normalized form.<br>
	 *     A decimal is considered normalized if it is within the range [0.0, 1.0].
	 * </p>
	 *
	 * @return A new type with the applied normalized constraint
	 */
	default @NonNull C normalized() {
		return this.apply(BigDecimalConstraintConfig::withNormalized);
	}
	
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
	default @NonNull C scale(int scale) {
		return this.apply(config -> config.withScale(scale));
	}
	
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
	default @NonNull C minScale(int minScale) {
		return this.apply(config -> config.withMinScale(minScale));
	}
	
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
	default @NonNull C maxScale(int maxScale) {
		return this.apply(config -> config.withMaxScale(maxScale));
	}
	
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
	default @NonNull C scaleBetween(int minScale, int maxScale) {
		return this.apply(config -> config.withScaleBetween(minScale, maxScale));
	}
	
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
	default @NonNull C precision(int precision) {
		return this.apply(config -> config.withPrecision(precision));
	}
	
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
	default @NonNull C minPrecision(int minPrecision) {
		return this.apply(config -> config.withMinPrecision(minPrecision));
	}
	
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
	default @NonNull C maxPrecision(int maxPrecision) {
		return this.apply(config -> config.withMaxPrecision(maxPrecision));
	}
	
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
	default @NonNull C precisionBetween(int minPrecision, int maxPrecision) {
		return this.apply(config -> config.withPrecisionBetween(minPrecision, maxPrecision));
	}
	
	// BaseConstraint methods
	@Override
	default @NonNull C equalTo(@NonNull BigDecimal value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull BigDecimal value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<BigDecimal> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<BigDecimal> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<BigDecimal> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	// ComparableConstraint methods
	@Override
	default @NonNull C greaterThan(@NonNull BigDecimal value) {
		return this.apply(config -> config.withGreaterThan(value));
	}
	
	@Override
	default @NonNull C greaterThanOrEqual(@NonNull BigDecimal value) {
		return this.apply(config -> config.withGreaterThanOrEqual(value));
	}
	
	@Override
	default @NonNull C lessThan(@NonNull BigDecimal value) {
		return this.apply(config -> config.withLessThan(value));
	}
	
	@Override
	default @NonNull C lessThanOrEqual(@NonNull BigDecimal value) {
		return this.apply(config -> config.withLessThanOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull BigDecimal min, @NonNull BigDecimal max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull BigDecimal min, @NonNull BigDecimal max) {
		return this.apply(config -> config.withBetweenOrEqual(min, max));
	}
	
	// SignedConstraint methods
	@Override
	default @NonNull C positive() {
		return this.apply(BigDecimalConstraintConfig::withPositive);
	}
	
	@Override
	default @NonNull C nonPositive() {
		return this.apply(BigDecimalConstraintConfig::withNonPositive);
	}
	
	@Override
	default @NonNull C negative() {
		return this.apply(BigDecimalConstraintConfig::withNegative);
	}
	
	@Override
	default @NonNull C nonNegative() {
		return this.apply(BigDecimalConstraintConfig::withNonNegative);
	}
	
	@Override
	default @NonNull C zero() {
		return this.apply(BigDecimalConstraintConfig::withZero);
	}
	
	@Override
	default @NonNull C nonZero() {
		return this.apply(BigDecimalConstraintConfig::withNonZero);
	}
	
	// NumericConstraint methods
	@Override
	default @NonNull C percentage() {
		return this.apply(BigDecimalConstraintConfig::withPercentage);
	}
}
