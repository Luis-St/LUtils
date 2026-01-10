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

package net.luis.utils.io.codec.constraint_new.config.temporal;

import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.*;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.Period;
import java.util.*;

/**
 * Configuration record for period type constraints.<br>
 * <p>
 *     This record stores the constraint values for period codecs.<br>
 *     It includes base constraints, comparable constraints, signed constraints, and field constraints
 *     for day, month, and year components.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the period and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of Periods and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The period equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The period set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param min The minimum period constraint as a pair of (value, inclusive)
 * @param max The maximum period constraint as a pair of (value, inclusive)
 * @param positive The positive constraint as a Boolean where false means positive and true means nonPositive
 * @param negative The negative constraint as a Boolean where false means negative and true means nonNegative
 * @param zero The zero constraint as a Boolean where false means zero and true means nonZero
 * @param day A nested config for day component constraints
 * @param month A nested config for month component constraints
 * @param year A nested config for year component constraints
 * @param custom A custom constraint implementation
 */
public record PeriodConstraintConfig(
	@NonNull Optional<Pair<Period, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Period>, Boolean>> in,
	@NonNull Optional<Pair<Period, Boolean>> min,
	@NonNull Optional<Pair<Period, Boolean>> max,
	@NonNull Optional<Boolean> positive,
	@NonNull Optional<Boolean> negative,
	@NonNull Optional<Boolean> zero,
	@NonNull Optional<NumericFieldConstraintConfig> day,
	@NonNull Optional<NumericFieldConstraintConfig> month,
	@NonNull Optional<NumericFieldConstraintConfig> year,
	@NonNull Optional<Constraint<Period>> custom
) implements ConstraintConfig<Period> {
	
	/**
	 * An unconstrained period configuration with no constraints applied.<br>
	 */
	public static final PeriodConstraintConfig UNCONSTRAINED = new PeriodConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new period constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The period equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The period set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param min The minimum period constraint as a pair of (value, inclusive)
	 * @param max The maximum period constraint as a pair of (value, inclusive)
	 * @param positive The positive constraint as a Boolean where false means positive and true means nonPositive
	 * @param negative The negative constraint as a Boolean where false means negative and true means nonNegative
	 * @param zero The zero constraint as a Boolean where false means zero and true means nonZero
	 * @param day A nested config for day component constraints
	 * @param month A nested config for month component constraints
	 * @param year A nested config for year component constraints
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the 'in' set is empty when present
	 * @throws IllegalArgumentException If positive and negative constraints are both present
	 */
	public PeriodConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(positive, "Optional for 'positive' constraint must not be null");
		Objects.requireNonNull(negative, "Optional for 'negative' constraint must not be null");
		Objects.requireNonNull(zero, "Optional for 'zero' constraint must not be null");
		Objects.requireNonNull(day, "Optional for 'day' constraint must not be null");
		Objects.requireNonNull(month, "Optional for 'month' constraint must not be null");
		Objects.requireNonNull(year, "Optional for 'year' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In set must not be empty when present");
		}
		
		if (positive.isPresent() && negative.isPresent()) {
			throw new IllegalArgumentException("Positive and negative constraints are mutually exclusive");
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact period that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withEqualTo(@NonNull Period value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new PeriodConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The period that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withNotEqualTo(@NonNull Period value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new PeriodConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of Periods that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withIn(@NonNull Collection<Period> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of Periods that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withNotIn(@NonNull Collection<Period> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold period (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withGreaterThan(@NonNull Period value) {
		Objects.requireNonNull(value, "Value for 'greater than' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold period (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withGreaterThanOrEqual(@NonNull Period value) {
		Objects.requireNonNull(value, "Value for 'greater than or equal' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold period (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withLessThan(@NonNull Period value) {
		Objects.requireNonNull(value, "Value for 'less than' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, false)), this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold period (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withLessThanOrEqual(@NonNull Period value) {
		Objects.requireNonNull(value, "Value for 'less than or equal' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, true)), this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum period (exclusive)
	 * @param max The maximum period (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withBetween(@NonNull Period min, @NonNull Period max) {
		Objects.requireNonNull(min, "Min value for 'between' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, false)), Optional.of(Pair.of(max, false)), this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum period (inclusive)
	 * @param max The maximum period (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withBetweenOrEqual(@NonNull Period min, @NonNull Period max) {
		Objects.requireNonNull(min, "Min value for 'between or equal' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between or equal' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, true)), Optional.of(Pair.of(max, true)), this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withPositive() {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(false), this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the non-positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withNonPositive() {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(true), this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withNegative() {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(false), this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the non-negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withNonNegative() {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(true), this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withZero() {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(false), this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the non-zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withNonZero() {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(true), this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day constraint.<br>
	 *
	 * @param dayConfig The numeric field constraint config for day validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withDay(@NonNull NumericFieldConstraintConfig dayConfig) {
		Objects.requireNonNull(dayConfig, "Config for 'day' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, Optional.of(dayConfig), this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified month constraint.<br>
	 *
	 * @param monthConfig The numeric field constraint config for month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withMonth(@NonNull NumericFieldConstraintConfig monthConfig) {
		Objects.requireNonNull(monthConfig, "Config for 'month' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.day, Optional.of(monthConfig), this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified year constraint.<br>
	 *
	 * @param yearConfig The numeric field constraint config for year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withYear(@NonNull NumericFieldConstraintConfig yearConfig) {
		Objects.requireNonNull(yearConfig, "Config for 'year' constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, Optional.of(yearConfig), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withCustom(@NonNull Constraint<Period> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull Period value) {
		Objects.requireNonNull(value, "Value must not be null");

		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> TemporalMatchers.matchPeriodRange(value, this.min, this.max),
			() -> TemporalMatchers.matchPeriodSign(value, this.positive, this.negative, this.zero),
			() -> ConstraintMatchers.matchNestedConfig(value.getDays(), this.day, "Day"),
			() -> ConstraintMatchers.matchNestedConfig(value.getMonths(), this.month, "Month"),
			() -> ConstraintMatchers.matchNestedConfig(value.getYears(), this.year, "Year"),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
