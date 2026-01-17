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

import net.luis.utils.io.codec.constraint_new.ApplicableConstraint;
import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.numeric.DecimalConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for floating-point decimal types that provides decimal-specific validation operations.<br>
 * <p>
 *     This interface extends {@link NumericConstraint} with methods specific to floating-point values.<br>
 *     It provides methods for validating finiteness, NaN, integral values, and normalized form.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The numeric type (Float, Double)
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface DecimalConstraint<T extends Number & Comparable<T>, C> extends ApplicableConstraint<DecimalConstraintConfig<T>, C>, NumericConstraint<T, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<DecimalConstraintConfig<T>> configModifier);
	
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
	default @NonNull C finite() {
		return this.apply(DecimalConstraintConfig::withFinite);
	}
	
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
	default @NonNull C notNaN() {
		return this.apply(DecimalConstraintConfig::withNotNaN);
	}
	
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
		return this.apply(DecimalConstraintConfig::withIntegral);
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
		return this.apply(DecimalConstraintConfig::withNormalized);
	}
	
	// BaseConstraint methods
	@Override
	default @NonNull C equalTo(@NonNull T value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull T value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<T> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<T> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<T> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	// ComparableConstraint methods
	@Override
	default @NonNull C greaterThan(@NonNull T value) {
		return this.apply(config -> config.withGreaterThan(value));
	}
	
	@Override
	default @NonNull C greaterThanOrEqual(@NonNull T value) {
		return this.apply(config -> config.withGreaterThanOrEqual(value));
	}
	
	@Override
	default @NonNull C lessThan(@NonNull T value) {
		return this.apply(config -> config.withLessThan(value));
	}
	
	@Override
	default @NonNull C lessThanOrEqual(@NonNull T value) {
		return this.apply(config -> config.withLessThanOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull T min, @NonNull T max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull T min, @NonNull T max) {
		return this.apply(config -> config.withBetweenOrEqual(min, max));
	}
	
	// SignedConstraint methods
	@Override
	default @NonNull C positive() {
		return this.apply(DecimalConstraintConfig::withPositive);
	}
	
	@Override
	default @NonNull C nonPositive() {
		return this.apply(DecimalConstraintConfig::withNonPositive);
	}
	
	@Override
	default @NonNull C negative() {
		return this.apply(DecimalConstraintConfig::withNegative);
	}
	
	@Override
	default @NonNull C nonNegative() {
		return this.apply(DecimalConstraintConfig::withNonNegative);
	}
	
	@Override
	default @NonNull C zero() {
		return this.apply(DecimalConstraintConfig::withZero);
	}
	
	@Override
	default @NonNull C nonZero() {
		return this.apply(DecimalConstraintConfig::withNonZero);
	}
	
	// NumericConstraint methods
	@Override
	default @NonNull C percentage() {
		return this.apply(DecimalConstraintConfig::withPercentage);
	}
}
