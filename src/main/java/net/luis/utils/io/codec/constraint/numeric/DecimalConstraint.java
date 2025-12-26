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

package net.luis.utils.io.codec.constraint.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.numeric.DecimalConstraintConfig;
import net.luis.utils.io.codec.constraint.core.NumberProvider;
import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * A constraint interface for decimal (floating-point) numeric value validation.<br>
 * <p>
 *     This interface extends {@link NumericConstraint} with additional constraints specific to decimal numbers,
 *     such as scale, precision, and special floating-point value checks (finite, NaN).
 * </p>
 * <p>
 *     All methods inherited from {@link NumericConstraint} and {@link net.luis.utils.io.codec.constraint.ComparableConstraint}
 *     have default implementations and do not need to be overridden.
 * </p>
 *
 * @see NumericConstraint
 *
 * @author Luis-St
 *
 * @param <T> The decimal numeric type being constrained
 * @param <C> The codec type
 */
public interface DecimalConstraint<T extends Number & Comparable<T>, C extends Codec<T>> extends NumericConstraint<T, C, DecimalConstraintConfig<T>> {
	
	@Override
	@NonNull C applyConstraint(@NonNull UnaryOperator<DecimalConstraintConfig<T>> configModifier);

	@Override
	@NonNull NumberProvider<T> provider();

	@Override
	default @NonNull C greaterThan(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMin(value, false)));
	}

	@Override
	default @NonNull C greaterThanOrEqual(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMin(value, true)));
	}

	@Override
	default @NonNull C lessThan(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMax(value, false)));
	}

	@Override
	default @NonNull C lessThanOrEqual(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMax(value, true)));
	}
	
	@Override
	default @NonNull C between(@NonNull T min, @NonNull T max) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withRange(min, max, false)));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull T min, @NonNull T max) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withRange(min, max, true)));
	}

	@Override
	default @NonNull C equalTo(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withEquals(value, false)));
	}

	@Override
	default @NonNull C notEqualTo(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withEquals(value, true)));
	}

	/**
	 * Applies a finite value constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that decimal values are finite (not infinite or NaN).<br>
	 *     This constraint ensures the value is neither positive infinity, negative infinity, nor NaN.
	 * </p>
	 *
	 * @return A new codec with the applied finite constraint
	 * @see #notNaN()
	 */
	default @NonNull C finite() {
		return this.applyConstraint(DecimalConstraintConfig::withFinite);
	}

	/**
	 * Applies a not-NaN constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that decimal values are not NaN (Not a Number).<br>
	 *     This constraint allows infinite values but rejects NaN.
	 * </p>
	 *
	 * @return A new codec with the applied not-NaN constraint
	 * @see #finite()
	 */
	default @NonNull C notNaN() {
		return this.applyConstraint(DecimalConstraintConfig::withNotNaN);
	}

	/**
	 * Applies an integral value constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that decimal values represent whole numbers (no fractional part).<br>
	 *     For example, 5.0 would pass but 5.5 would fail.
	 * </p>
	 *
	 * @return A new codec with the applied integral constraint
	 */
	default @NonNull C integral() {
		return this.applyConstraint(DecimalConstraintConfig::withIntegral);
	}

	/**
	 * Applies a normalized form constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that decimal values are in normalized form.<br>
	 *     A decimal is considered normalized if it is within the range [0.0, 1.0].
	 * </p>
	 *
	 * @return A new codec with the applied normalized constraint
	 */
	default @NonNull C normalized() {
		return this.applyConstraint(DecimalConstraintConfig::withNormalized);
	}
}
