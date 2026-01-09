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

import java.util.*;

/**
 * Configuration record for floating-point decimal type constraints.<br>
 * <p>
 *     This record stores the constraint values for decimal codecs (float, double).<br>
 *     It includes base constraints, comparable constraints, signed constraints, and decimal-specific constraints.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The numeric type this config is for
 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param min The minimum value constraint as a pair of (value, inclusive)
 * @param max The maximum value constraint as a pair of (value, inclusive)
 * @param positive The positive constraint as a Boolean where false means positive (greater than zero) and true means nonPositive (less than or equal to zero)
 * @param negative The negative constraint as a Boolean where false means negative (less than zero) and true means nonNegative (greater than or equal to zero)
 * @param zero The zero constraint as a Boolean where false means zero and true means nonZero
 * @param percentage If present, requires the value to be between 0 and 100 (inclusive)
 * @param finite If present, requires the value to be finite (not infinite or NaN)
 * @param notNaN If present, requires the value to not be NaN
 * @param integral If present, requires the value to be a whole number (no fractional part)
 * @param normalized If present, requires the value to be within the range [0.0, 1.0]
 * @param custom A custom constraint implementation
 */
public record DecimalConstraintConfig<T extends Number & Comparable<T>>(
	@NonNull Optional<Pair<T, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<T>, Boolean>> in,
	@NonNull Optional<Pair<T, Boolean>> min,
	@NonNull Optional<Pair<T, Boolean>> max,
	@NonNull Optional<Boolean> positive,
	@NonNull Optional<Boolean> negative,
	@NonNull Optional<Boolean> zero,
	@NonNull Optional<Void> percentage,
	@NonNull Optional<Void> finite,
	@NonNull Optional<Void> notNaN,
	@NonNull Optional<Void> integral,
	@NonNull Optional<Void> normalized,
	@NonNull Optional<Constraint<T>> custom
) {

	/**
	 * Constructs a new decimal constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param min The minimum value constraint as a pair of (value, inclusive)
	 * @param max The maximum value constraint as a pair of (value, inclusive)
	 * @param positive The positive constraint as a Boolean where false means positive (greater than zero) and true means nonPositive (less than or equal to zero)
	 * @param negative The negative constraint as a Boolean where false means negative (less than zero) and true means nonNegative (greater than or equal to zero)
	 * @param zero The zero constraint as a Boolean where false means zero and true means nonZero
	 * @param percentage If present, requires the value to be between 0 and 100 (inclusive)
	 * @param finite If present, requires the value to be finite (not infinite or NaN)
	 * @param notNaN If present, requires the value to not be NaN
	 * @param integral If present, requires the value to be a whole number (no fractional part)
	 * @param normalized If present, requires the value to be within the range [0.0, 1.0]
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 * @throws IllegalArgumentException If min is greater than max when both are present
	 * @throws IllegalArgumentException If min equals max with at least one exclusive bound when both are present
	 */
	public DecimalConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(positive, "Optional for 'positive' constraint must not be null");
		Objects.requireNonNull(negative, "Optional for 'negative' constraint must not be null");
		Objects.requireNonNull(zero, "Optional for 'zero' constraint must not be null");
		Objects.requireNonNull(percentage, "Optional for 'percentage' constraint must not be null");
		Objects.requireNonNull(finite, "Optional for 'finite' constraint must not be null");
		Objects.requireNonNull(notNaN, "Optional for 'not NaN' constraint must not be null");
		Objects.requireNonNull(integral, "Optional for 'integral' constraint must not be null");
		Objects.requireNonNull(normalized, "Optional for 'normalized' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In constraint set must not be empty when present");
		}
		
		if (min.isPresent() && max.isPresent()) {
			if (min.get().getFirst().compareTo(max.get().getFirst()) > 0) {
				throw new IllegalArgumentException("Min must be less than or equal to max when both are present, but got " + min.get().getFirst() + " > " + max.get().getFirst());
			}
			if (min.get().getFirst().compareTo(max.get().getFirst()) == 0 && (!min.get().getSecond() || !max.get().getSecond())) {
				throw new IllegalArgumentException("Min and max are equal but at least one bound is exclusive when both are present");
			}
		}
	}
	
	/**
	 * Creates an unconstrained decimal configuration with no constraints applied.<br>
	 *
	 * @param <T> The numeric type
	 * @return An unconstrained decimal constraint config
	 */
	public static <T extends Number & Comparable<T>> @NonNull DecimalConstraintConfig<T> unconstrained() {
		return new DecimalConstraintConfig<>(
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
			Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
		);
	}
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withEqualTo(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new DecimalConstraintConfig<>(Optional.of(Pair.of(value, false)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withNotEqualTo(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new DecimalConstraintConfig<>(Optional.of(Pair.of(value, true)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withIn(@NonNull Collection<T> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new DecimalConstraintConfig<>(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withNotIn(@NonNull Collection<T> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new DecimalConstraintConfig<>(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withGreaterThan(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'greater than' constraint must not be null");
		return new DecimalConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.max, this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withGreaterThanOrEqual(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'greater than or equal' constraint must not be null");
		return new DecimalConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.max, this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withLessThan(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'less than' constraint must not be null");
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, false)), this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withLessThanOrEqual(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'less than or equal' constraint must not be null");
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, true)), this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withBetween(@NonNull T min, @NonNull T max) {
		Objects.requireNonNull(min, "Min value for 'between' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between' constraint must not be null");
		return new DecimalConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(min, false)), Optional.of(Pair.of(max, false)), this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withBetweenOrEqual(@NonNull T min, @NonNull T max) {
		Objects.requireNonNull(min, "Min value for 'between or equal' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between or equal' constraint must not be null");
		return new DecimalConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(min, true)), Optional.of(Pair.of(max, true)), this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withPositive() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, Optional.of(false), this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the non-positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withNonPositive() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, Optional.of(true), this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withNegative() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(false), this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the non-negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withNonNegative() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(true), this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withZero() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(false), this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the non-zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withNonZero() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(true), this.percentage, this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the percentage constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withPercentage() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, Optional.of(null), this.finite, this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the finite constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withFinite() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, Optional.of(null), this.notNaN, this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the not-NaN constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withNotNaN() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.finite, Optional.of(null), this.integral, this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the integral constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withIntegral() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, Optional.of(null), this.normalized, this.custom);
	}
	
	/**
	 * Creates a new config with the normalized constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withNormalized() {
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, Optional.of(null), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DecimalConstraintConfig<T> withCustom(@NonNull Constraint<T> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new DecimalConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.finite, this.notNaN, this.integral, this.normalized, Optional.of(constraint));
	}
}
