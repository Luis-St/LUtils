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

package net.luis.utils.io.codec.constraint_new.config.numeric;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.*;

/**
 * Configuration record for BigDecimal type constraints.<br>
 * <p>
 *     This record stores the constraint values for BigDecimal codecs.<br>
 *     It includes base constraints, comparable constraints, signed constraints, and BigDecimal-specific constraints
 *     for scale (decimal places) and precision (significant digits).
 * </p>
 * <p>
 *     The min, max, scaleMin, scaleMax, precisionMin, and precisionMax fields use {@link Pair} where the first value
 *     is the bound and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param min The minimum value constraint as a pair of (value, inclusive)
 * @param max The maximum value constraint as a pair of (value, inclusive)
 * @param positive The positive constraint as a Boolean where false means positive (greater than zero) and true means nonPositive (less than or equal to zero)
 * @param negative The negative constraint as a Boolean where false means negative (less than zero) and true means nonNegative (greater than or equal to zero)
 * @param zero The zero constraint as a Boolean where false means zero and true means nonZero
 * @param percentage If present, requires the value to be between 0 and 100 (inclusive)
 * @param integral If present, requires the value to be a whole number (no fractional part)
 * @param normalized If present, requires the value to be within the range [0.0, 1.0]
 * @param scaleMin The minimum scale (decimal places) constraint as a pair of (value, inclusive)
 * @param scaleMax The maximum scale (decimal places) constraint as a pair of (value, inclusive)
 * @param precisionMin The minimum precision (significant digits) constraint as a pair of (value, inclusive)
 * @param precisionMax The maximum precision (significant digits) constraint as a pair of (value, inclusive)
 * @param custom A custom constraint implementation
 */
public record BigDecimalConstraintConfig(
	@NonNull Optional<Pair<BigDecimal, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<BigDecimal>, Boolean>> in,
	@NonNull Optional<Pair<BigDecimal, Boolean>> min,
	@NonNull Optional<Pair<BigDecimal, Boolean>> max,
	@NonNull Optional<Boolean> positive,
	@NonNull Optional<Boolean> negative,
	@NonNull Optional<Boolean> zero,
	@NonNull Optional<Void> percentage,
	@NonNull Optional<Void> integral,
	@NonNull Optional<Void> normalized,
	@NonNull Optional<Pair<Integer, Boolean>> scaleMin,
	@NonNull Optional<Pair<Integer, Boolean>> scaleMax,
	@NonNull Optional<Pair<Integer, Boolean>> precisionMin,
	@NonNull Optional<Pair<Integer, Boolean>> precisionMax,
	@NonNull Optional<Constraint<BigDecimal>> custom
) {
	
	/**
	 * An unconstrained BigDecimal configuration with no constraints applied.<br>
	 */
	public static final BigDecimalConstraintConfig UNCONSTRAINED = new BigDecimalConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withEqualTo(@NonNull BigDecimal value) {
		return new BigDecimalConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNotEqualTo(@NonNull BigDecimal value) {
		return new BigDecimalConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withIn(@NonNull Collection<BigDecimal> values) {
		return new BigDecimalConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNotIn(@NonNull Collection<BigDecimal> values) {
		return new BigDecimalConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withGreaterThan(@NonNull BigDecimal value) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withGreaterThanOrEqual(@NonNull BigDecimal value) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withLessThan(@NonNull BigDecimal value) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withLessThanOrEqual(@NonNull BigDecimal value) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withBetween(@NonNull BigDecimal min, @NonNull BigDecimal max) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(min), false)), Optional.of(Pair.of(Objects.requireNonNull(max), false)), this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withBetweenOrEqual(@NonNull BigDecimal min, @NonNull BigDecimal max) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(min), true)), Optional.of(Pair.of(Objects.requireNonNull(max), true)), this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withPositive() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(false), this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the non-positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNonPositive() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(true), this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNegative() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(false), this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the non-negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNonNegative() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(true), this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withZero() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(false), this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the non-zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNonZero() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(true), this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the percentage constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withPercentage() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, Optional.of(null), this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the integral constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withIntegral() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, Optional.of(null), this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the normalized constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNormalized() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, Optional.of(null), this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exact scale constraint.<br>
	 *
	 * @param scale The exact number of decimal places required
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withScale(int scale) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, Optional.of(Pair.of(scale, true)), Optional.of(Pair.of(scale, true)), this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified minimum scale constraint.<br>
	 *
	 * @param minScale The minimum number of decimal places required
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withMinScale(int minScale) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, Optional.of(Pair.of(minScale, true)), this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified maximum scale constraint.<br>
	 *
	 * @param maxScale The maximum number of decimal places allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withMaxScale(int maxScale) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, Optional.of(Pair.of(maxScale, true)), this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified scale range constraint.<br>
	 *
	 * @param minScale The minimum number of decimal places required
	 * @param maxScale The maximum number of decimal places allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withScaleBetween(int minScale, int maxScale) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, Optional.of(Pair.of(minScale, true)), Optional.of(Pair.of(maxScale, true)), this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exact precision constraint.<br>
	 *
	 * @param precision The exact number of significant digits required
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withPrecision(int precision) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, Optional.of(Pair.of(precision, true)), Optional.of(Pair.of(precision, true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified minimum precision constraint.<br>
	 *
	 * @param minPrecision The minimum number of significant digits required
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withMinPrecision(int minPrecision) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, Optional.of(Pair.of(minPrecision, true)), this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified maximum precision constraint.<br>
	 *
	 * @param maxPrecision The maximum number of significant digits allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withMaxPrecision(int maxPrecision) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, Optional.of(Pair.of(maxPrecision, true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified precision range constraint.<br>
	 *
	 * @param minPrecision The minimum number of significant digits required
	 * @param maxPrecision The maximum number of significant digits allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withPrecisionBetween(int minPrecision, int maxPrecision) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, Optional.of(Pair.of(minPrecision, true)), Optional.of(Pair.of(maxPrecision, true)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withCustom(@NonNull Constraint<BigDecimal> constraint) {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, Optional.of(Objects.requireNonNull(constraint)));
	}
}
