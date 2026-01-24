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

package net.luis.utils.io.codec.constraint.config.numeric;

import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.matcher.ConstraintMatchers;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.util.Unit;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.*;

/**
 * Configuration record for big decimal type constraints.<br>
 * <p>
 *     This record stores the constraint values for big decimal codecs.<br>
 *     It includes base constraints, comparable constraints, signed constraints, and big decimal specific constraints
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
	@NonNull Optional<Unit> percentage,
	@NonNull Optional<Unit> integral,
	@NonNull Optional<Unit> normalized,
	@NonNull Optional<Pair<Integer, Boolean>> scaleMin,
	@NonNull Optional<Pair<Integer, Boolean>> scaleMax,
	@NonNull Optional<Pair<Integer, Boolean>> precisionMin,
	@NonNull Optional<Pair<Integer, Boolean>> precisionMax,
	@NonNull Optional<Constraint<BigDecimal>> custom
) implements ConstraintConfig<BigDecimal> {
	
	/**
	 * An unconstrained big decimal configuration with no constraints applied.<br>
	 */
	public static final BigDecimalConstraintConfig UNCONSTRAINED = new BigDecimalConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new big decimal constraint config with the specified parameters.<br>
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
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 * @throws IllegalArgumentException If min is greater than max when both are present
	 * @throws IllegalArgumentException If min equals max with at least one exclusive bound when both are present
	 * @throws IllegalArgumentException If the minimum scale is negative when present
	 * @throws IllegalArgumentException If the minimum scale is greater than the maximum scale when both are present
	 * @throws IllegalArgumentException If min and max scale are equal but at least one bound is exclusive when both are present
	 * @throws IllegalArgumentException If the minimum precision is not positive when present
	 * @throws IllegalArgumentException If the minimum precision is greater than the maximum precision when both are present
	 * @throws IllegalArgumentException If min and max precision are equal but at least one bound is exclusive when both are present
	 */
	public BigDecimalConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(positive, "Optional for 'positive' constraint must not be null");
		Objects.requireNonNull(negative, "Optional for 'negative' constraint must not be null");
		Objects.requireNonNull(zero, "Optional for 'zero' constraint must not be null");
		Objects.requireNonNull(percentage, "Optional for 'percentage' constraint must not be null");
		Objects.requireNonNull(integral, "Optional for 'integral' constraint must not be null");
		Objects.requireNonNull(normalized, "Optional for 'normalized' constraint must not be null");
		Objects.requireNonNull(scaleMin, "Optional for 'scale min' constraint must not be null");
		Objects.requireNonNull(scaleMax, "Optional for 'scale max' constraint must not be null");
		Objects.requireNonNull(precisionMin, "Optional for 'precision min' constraint must not be null");
		Objects.requireNonNull(precisionMax, "Optional for 'precision max' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("The 'in' constraint set must not be empty when present");
		}
		
		if (min.isPresent() && max.isPresent() && min.get().getFirst().compareTo(max.get().getFirst()) > 0) {
			throw new IllegalArgumentException("Min must be less than or equal to max when both are present, but got " + min.get().getFirst() + " > " + max.get().getFirst());
		}
		
		if (min.isPresent() && max.isPresent() && min.get().getFirst().compareTo(max.get().getFirst()) == 0 && (!min.get().getSecond() || !max.get().getSecond())) {
			throw new IllegalArgumentException("Min and max are equal but at least one bound is exclusive when both are present");
		}
		
		if (scaleMin.isPresent() && scaleMin.get().getFirst() < 0) {
			throw new IllegalArgumentException("Min scale must be non-negative when present, but got " + scaleMin.get().getFirst());
		}
		
		if (scaleMin.isPresent() && scaleMax.isPresent() && scaleMin.get().getFirst() > scaleMax.get().getFirst()) {
			throw new IllegalArgumentException("Min scale must be less than or equal to max scale when both are present, but got " + scaleMin.get().getFirst() + " > " + scaleMax.get().getFirst());
		}
		
		if (scaleMin.isPresent() && scaleMax.isPresent() && scaleMin.get().getFirst().equals(scaleMax.get().getFirst()) && (!scaleMin.get().getSecond() || !scaleMax.get().getSecond())) {
			throw new IllegalArgumentException("Min and max scale are equal but at least one bound is exclusive when both are present");
		}
		
		if (precisionMin.isPresent() && precisionMin.get().getFirst() < 1) {
			throw new IllegalArgumentException("Min precision must be positive when present, but got " + precisionMin.get().getFirst());
		}
		
		if (precisionMin.isPresent() && precisionMax.isPresent() && precisionMin.get().getFirst() > precisionMax.get().getFirst()) {
			throw new IllegalArgumentException("Min precision must be less than or equal to max precision when both are present, but got " + precisionMin.get().getFirst() + " > " + precisionMax.get().getFirst());
		}
		
		if (precisionMin.isPresent() && precisionMax.isPresent() && precisionMin.get().getFirst().equals(precisionMax.get().getFirst()) && (!precisionMin.get().getSecond() || !precisionMax.get().getSecond())) {
			throw new IllegalArgumentException("Min and max precision are equal but at least one bound is exclusive when both are present");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withEqualTo(@NonNull BigDecimal value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new BigDecimalConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNotEqualTo(@NonNull BigDecimal value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new BigDecimalConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withIn(@NonNull Collection<BigDecimal> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new BigDecimalConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNotIn(@NonNull Collection<BigDecimal> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new BigDecimalConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withGreaterThan(@NonNull BigDecimal value) {
		Objects.requireNonNull(value, "Value for 'greater than' constraint must not be null");
		return new BigDecimalConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withGreaterThanOrEqual(@NonNull BigDecimal value) {
		Objects.requireNonNull(value, "Value for 'greater than or equal' constraint must not be null");
		return new BigDecimalConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withLessThan(@NonNull BigDecimal value) {
		Objects.requireNonNull(value, "Value for 'less than' constraint must not be null");
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, false)), this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withLessThanOrEqual(@NonNull BigDecimal value) {
		Objects.requireNonNull(value, "Value for 'less than or equal' constraint must not be null");
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, true)), this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withBetween(@NonNull BigDecimal min, @NonNull BigDecimal max) {
		Objects.requireNonNull(min, "Min value for 'between' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between' constraint must not be null");
		return new BigDecimalConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, false)), Optional.of(Pair.of(max, false)), this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withBetweenOrEqual(@NonNull BigDecimal min, @NonNull BigDecimal max) {
		Objects.requireNonNull(min, "Min value for 'between or equal' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between or equal' constraint must not be null");
		return new BigDecimalConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, true)), Optional.of(Pair.of(max, true)), this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
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
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, Optional.of(Unit.INSTANCE), this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the integral constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withIntegral() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, Optional.of(Unit.INSTANCE), this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
	}
	
	/**
	 * Creates a new config with the normalized constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNormalized() {
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, Optional.of(Unit.INSTANCE), this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, this.custom);
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
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new BigDecimalConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.integral, this.normalized, this.scaleMin, this.scaleMax, this.precisionMin, this.precisionMax, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NonNull Result<Void> matches(@NonNull BigDecimal value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchRange(value, this.min, this.max),
			() -> ConstraintMatchers.matchSign(value, this.positive, this.negative, this.zero),
			() -> ConstraintMatchers.matchPercentage(value, this.percentage),
			() -> ConstraintMatchers.matchFlag(value, this.integral, v -> v.stripTrailingZeros().scale() <= 0, "Value '" + value + "' must be integral (no fractional part)"),
			() -> ConstraintMatchers.matchFlag(value, this.normalized, v -> v.compareTo(BigDecimal.ZERO) >= 0 && v.compareTo(BigDecimal.ONE) <= 0, "Value '" + value + "' must be normalized (between 0.0 and 1.0)"),
			() -> ConstraintMatchers.matchRange(value.scale(), this.scaleMin, this.scaleMax),
			() -> ConstraintMatchers.matchRange(value.precision(), this.precisionMin, this.precisionMax),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
