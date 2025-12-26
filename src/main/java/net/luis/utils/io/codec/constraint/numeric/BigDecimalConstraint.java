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
import net.luis.utils.io.codec.constraint.config.numeric.BigDecimalConstraintConfig;
import net.luis.utils.io.codec.constraint.core.NumberProvider;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.function.UnaryOperator;

/**
 * A constraint interface for BigDecimal-specific validation.<br>
 * <p>
 *     This interface extends {@link DecimalConstraint} with additional constraints specific to BigDecimal,
 *     such as scale and precision validation which are easily accessible for BigDecimal but not for
 *     primitive floating-point types (double, float).
 * </p>
 * <p>
 *     All methods inherited from {@link NumericConstraint}, {@link DecimalConstraint}, and
 *     {@link net.luis.utils.io.codec.constraint.ComparableConstraint} have default implementations
 *     and do not need to be overridden.
 * </p>
 *
 * @see DecimalConstraint
 * @see java.math.BigDecimal
 *
 * @author Luis-St
 *
 * @param <C> The codec type
 */
public interface BigDecimalConstraint<C extends Codec<BigDecimal>> extends NumericConstraint<BigDecimal, C, BigDecimalConstraintConfig> {

	@Override
	@NonNull C applyConstraint(@NonNull UnaryOperator<BigDecimalConstraintConfig> configModifier);
	
	@Override
	@NonNull NumberProvider<BigDecimal> provider();
	
	@Override
	default @NonNull C greaterThan(@NonNull BigDecimal value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMin(value, false)));
	}
	
	@Override
	default @NonNull C greaterThanOrEqual(@NonNull BigDecimal value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMin(value, true)));
	}
	
	@Override
	default @NonNull C lessThan(@NonNull BigDecimal value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMax(value, false)));
	}
	
	@Override
	default @NonNull C lessThanOrEqual(@NonNull BigDecimal value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMax(value, true)));
	}
	
	@Override
	default @NonNull C between(@NonNull BigDecimal min, @NonNull BigDecimal max) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withRange(min, max, false)));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull BigDecimal min, @NonNull BigDecimal max) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withRange(min, max, true)));
	}
	
	@Override
	default @NonNull C equalTo(@NonNull BigDecimal value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withEquals(value, false)));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull BigDecimal value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withEquals(value, true)));
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
		return this.applyConstraint(BigDecimalConstraintConfig::withIntegral);
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
		return this.applyConstraint(BigDecimalConstraintConfig::withNormalized);
	}
	
	/**
	 * Applies an exact scale constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that BigDecimal values have exactly the specified number of digits after the decimal point.
	 * </p>
	 *
	 * @param scale The exact number of decimal places required
	 * @return A new codec with the applied scale constraint
	 * @see #minScale(int)
	 * @see #maxScale(int)
	 */
	default @NonNull C scale(int scale) {
		return this.applyConstraint(config -> config.withScale(scale, scale));
	}

	/**
	 * Applies a minimum scale constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that BigDecimal values have at least the specified number of digits after the decimal point.
	 * </p>
	 *
	 * @param minScale The minimum number of decimal places required
	 * @return A new codec with the applied minimum scale constraint
	 * @see #scale(int)
	 * @see #scaleBetween(int, int)
	 */
	default @NonNull C minScale(int minScale) {
		return this.applyConstraint(config -> config.withMinScale(minScale));
	}

	/**
	 * Applies a maximum scale constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that BigDecimal values have at most the specified number of digits after the decimal point.
	 * </p>
	 *
	 * @param maxScale The maximum number of decimal places allowed
	 * @return A new codec with the applied maximum scale constraint
	 * @see #scale(int)
	 * @see #scaleBetween(int, int)
	 */
	default @NonNull C maxScale(int maxScale) {
		return this.applyConstraint(config -> config.withMaxScale(maxScale));
	}

	/**
	 * Applies a scale range constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that BigDecimal values have a number of decimal places within the specified range (inclusive).
	 * </p>
	 *
	 * @param minScale The minimum number of decimal places required
	 * @param maxScale The maximum number of decimal places allowed
	 * @return A new codec with the applied scale range constraint
	 * @see #minScale(int)
	 * @see #maxScale(int)
	 */
	default @NonNull C scaleBetween(int minScale, int maxScale) {
		return this.applyConstraint(config -> config.withScale(minScale, maxScale));
	}

	/**
	 * Applies an exact precision constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that BigDecimal values have exactly the specified total number of significant digits.
	 * </p>
	 *
	 * @param precision The exact number of significant digits required
	 * @return A new codec with the applied precision constraint
	 * @see #minPrecision(int)
	 * @see #maxPrecision(int)
	 */
	default @NonNull C precision(int precision) {
		return this.applyConstraint(config -> config.withPrecision(precision, precision));
	}

	/**
	 * Applies a minimum precision constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that BigDecimal values have at least the specified number of significant digits.
	 * </p>
	 *
	 * @param minPrecision The minimum number of significant digits required
	 * @return A new codec with the applied minimum precision constraint
	 * @see #precision(int)
	 * @see #precisionBetween(int, int)
	 */
	default @NonNull C minPrecision(int minPrecision) {
		return this.applyConstraint(config -> config.withMinPrecision(minPrecision));
	}

	/**
	 * Applies a maximum precision constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that BigDecimal values have at most the specified number of significant digits.
	 * </p>
	 *
	 * @param maxPrecision The maximum number of significant digits allowed
	 * @return A new codec with the applied maximum precision constraint
	 * @see #precision(int)
	 * @see #precisionBetween(int, int)
	 */
	default @NonNull C maxPrecision(int maxPrecision) {
		return this.applyConstraint(config -> config.withMaxPrecision(maxPrecision));
	}

	/**
	 * Applies a precision range constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that BigDecimal values have a number of significant digits within the specified range (inclusive).
	 * </p>
	 *
	 * @param minPrecision The minimum number of significant digits required
	 * @param maxPrecision The maximum number of significant digits allowed
	 * @return A new codec with the applied precision range constraint
	 * @see #minPrecision(int)
	 * @see #maxPrecision(int)
	 */
	default @NonNull C precisionBetween(int minPrecision, int maxPrecision) {
		return this.applyConstraint(config -> config.withPrecision(minPrecision, maxPrecision));
	}
}
