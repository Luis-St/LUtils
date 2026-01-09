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
 * Configuration record for integer type constraints.<br>
 * <p>
 *     This record stores the constraint values for integer codecs (byte, short, int, long, BigInteger).<br>
 *     It includes base constraints, comparable constraints, signed constraints, and integer-specific constraints.
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
 * @param even If present, requires the value to be even
 * @param odd If present, requires the value to be odd
 * @param divisibleBy The divisor that the value must be divisible by
 * @param powerOf The base that the value must be a power of
 * @param custom A custom constraint implementation
 */
public record IntegerConstraintConfig<T extends Number & Comparable<T>>(
	@NonNull Optional<Pair<T, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<T>, Boolean>> in,
	@NonNull Optional<Pair<T, Boolean>> min,
	@NonNull Optional<Pair<T, Boolean>> max,
	@NonNull Optional<Boolean> positive,
	@NonNull Optional<Boolean> negative,
	@NonNull Optional<Boolean> zero,
	@NonNull Optional<Void> percentage,
	@NonNull Optional<Void> even,
	@NonNull Optional<Void> odd,
	@NonNull Optional<Long> divisibleBy,
	@NonNull Optional<Integer> powerOf,
	@NonNull Optional<Constraint<T>> custom
) {
	
	/**
	 * Creates an unconstrained integer configuration with no constraints applied.<br>
	 *
	 * @param <T> The numeric type
	 * @return An unconstrained integer constraint config
	 */
	public static <T extends Number & Comparable<T>> @NonNull IntegerConstraintConfig<T> unconstrained() {
		return new IntegerConstraintConfig<>(
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
	public @NonNull IntegerConstraintConfig<T> withEqualTo(@NonNull T value) {
		return new IntegerConstraintConfig<>(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withNotEqualTo(@NonNull T value) {
		return new IntegerConstraintConfig<>(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withIn(@NonNull Collection<T> values) {
		return new IntegerConstraintConfig<>(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withNotIn(@NonNull Collection<T> values) {
		return new IntegerConstraintConfig<>(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withGreaterThan(@NonNull T value) {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withGreaterThanOrEqual(@NonNull T value) {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withLessThan(@NonNull T value) {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withLessThanOrEqual(@NonNull T value) {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withBetween(@NonNull T min, @NonNull T max) {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(min), false)), Optional.of(Pair.of(Objects.requireNonNull(max), false)), this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withBetweenOrEqual(@NonNull T min, @NonNull T max) {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(min), true)), Optional.of(Pair.of(Objects.requireNonNull(max), true)), this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withPositive() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, Optional.of(false), this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the non-positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withNonPositive() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, Optional.of(true), this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withNegative() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(false), this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the non-negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withNonNegative() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(true), this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withZero() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(false), this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the non-zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withNonZero() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(true), this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the percentage constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withPercentage() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, Optional.of(null), this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the even constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withEven() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, Optional.of(null), this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the odd constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withOdd() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, Optional.of(null), this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified divisibility constraint.<br>
	 *
	 * @param divisor The divisor that the value must be divisible by
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withDivisibleBy(long divisor) {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, Optional.of(divisor), this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the power-of-two constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withPowerOfTwo() {
		return this.withPowerOf(2);
	}
	
	/**
	 * Creates a new config with the specified power-of constraint.<br>
	 *
	 * @param base The base that the value must be a power of
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withPowerOf(int base) {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, Optional.of(base), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withCustom(@NonNull Constraint<T> constraint) {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, Optional.of(Objects.requireNonNull(constraint)));
	}
}
