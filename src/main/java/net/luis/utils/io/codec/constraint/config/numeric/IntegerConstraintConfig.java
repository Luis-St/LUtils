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
	@NonNull Optional<Unit> percentage,
	@NonNull Optional<Unit> even,
	@NonNull Optional<Unit> odd,
	@NonNull Optional<Long> divisibleBy,
	@NonNull Optional<Integer> powerOf,
	@NonNull Optional<Constraint<T>> custom
) implements ConstraintConfig<T> {
	
	/**
	 * Constructs a new integer constraint config with the specified parameters.<br>
	 *
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
	 * @throws NullPointerException If any optional field is null
	 * @throws IllegalArgumentException If the 'in' constraint set is empty when present
	 * @throws IllegalArgumentException If min is greater than max when both are present
	 * @throws IllegalArgumentException If min equals max with at least one exclusive bound when both are present
	 * @throws IllegalArgumentException If both 'even' and 'odd' constraints are present
	 * @throws IllegalArgumentException If 'divisibleBy' is not positive when present
	 * @throws IllegalArgumentException If 'powerOf' is not greater than 1 when present
	 */
	public IntegerConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(positive, "Optional for 'positive' constraint must not be null");
		Objects.requireNonNull(negative, "Optional for 'negative' constraint must not be null");
		Objects.requireNonNull(zero, "Optional for 'zero' constraint must not be null");
		Objects.requireNonNull(percentage, "Optional for 'percentage' constraint must not be null");
		Objects.requireNonNull(even, "Optional for 'even' constraint must not be null");
		Objects.requireNonNull(odd, "Optional for 'odd' constraint must not be null");
		Objects.requireNonNull(divisibleBy, "Optional for 'divisible by' constraint must not be null");
		Objects.requireNonNull(powerOf, "Optional for 'power of' constraint must not be null");
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
		
		if (even.isPresent() && odd.isPresent()) {
			throw new IllegalArgumentException("Even and odd are mutually exclusive");
		}
		
		if (divisibleBy.isPresent() && divisibleBy.get() <= 0) {
			throw new IllegalArgumentException("Divisible by must be positive when present, but got " + divisibleBy.get());
		}
		
		if (powerOf.isPresent() && powerOf.get() <= 1) {
			throw new IllegalArgumentException("Power of must be greater than 1 when present, but got " + powerOf.get());
		}
	}
	
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
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact value that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withEqualTo(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new IntegerConstraintConfig<>(Optional.of(Pair.of(value, false)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The value that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withNotEqualTo(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new IntegerConstraintConfig<>(Optional.of(Pair.of(value, true)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of values that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withIn(@NonNull Collection<T> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new IntegerConstraintConfig<>(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of values that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withNotIn(@NonNull Collection<T> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new IntegerConstraintConfig<>(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withGreaterThan(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'greater than' constraint must not be null");
		return new IntegerConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withGreaterThanOrEqual(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'greater than or equal' constraint must not be null");
		return new IntegerConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withLessThan(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'less than' constraint must not be null");
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, false)), this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withLessThanOrEqual(@NonNull T value) {
		Objects.requireNonNull(value, "Value for 'less than or equal' constraint must not be null");
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, true)), this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum value (exclusive)
	 * @param max The maximum value (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withBetween(@NonNull T min, @NonNull T max) {
		Objects.requireNonNull(min, "Min value for 'between' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between' constraint must not be null");
		return new IntegerConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(min, false)), Optional.of(Pair.of(max, false)), this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum value (inclusive)
	 * @param max The maximum value (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withBetweenOrEqual(@NonNull T min, @NonNull T max) {
		Objects.requireNonNull(min, "Min value for 'between or equal' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between or equal' constraint must not be null");
		return new IntegerConstraintConfig<>(this.equalTo, this.in, Optional.of(Pair.of(min, true)), Optional.of(Pair.of(max, true)), this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
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
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, Optional.of(Unit.INSTANCE), this.even, this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the even constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withEven() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, Optional.of(Unit.INSTANCE), this.odd, this.divisibleBy, this.powerOf, this.custom);
	}
	
	/**
	 * Creates a new config with the odd constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull IntegerConstraintConfig<T> withOdd() {
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, Optional.of(Unit.INSTANCE), this.divisibleBy, this.powerOf, this.custom);
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
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new IntegerConstraintConfig<>(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.percentage, this.even, this.odd, this.divisibleBy, this.powerOf, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NonNull Result<Void> matches(@NonNull T value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchRange(value, this.min, this.max),
			() -> ConstraintMatchers.matchSign(value, this.positive, this.negative, this.zero),
			() -> ConstraintMatchers.matchPercentage(value, this.percentage),
			() -> ConstraintMatchers.matchParity(value.longValue(), this.even, this.odd),
			() -> ConstraintMatchers.matchDivisibleBy(value.longValue(), this.divisibleBy),
			() -> ConstraintMatchers.matchPowerOf(value.longValue(), this.powerOf),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
