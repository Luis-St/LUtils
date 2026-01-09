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
import net.luis.utils.io.codec.constraint_new.config.NumericFieldConstraintConfig;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.time.Period;
import java.util.*;

/**
 * Configuration record for Period type constraints.<br>
 * <p>
 *     This record stores the constraint values for Period codecs.<br>
 *     It includes base constraints, comparable constraints, signed constraints, and field constraints
 *     for day, month, and year components.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the Period and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of Periods and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The Period equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The Period set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param min The minimum Period constraint as a pair of (value, inclusive)
 * @param max The maximum Period constraint as a pair of (value, inclusive)
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
) {
	
	/**
	 * An unconstrained Period configuration with no constraints applied.<br>
	 */
	public static final PeriodConstraintConfig UNCONSTRAINED = new PeriodConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact Period that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withEqualTo(@NonNull Period value) {
		return new PeriodConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The Period that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withNotEqualTo(@NonNull Period value) {
		return new PeriodConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of Periods that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withIn(@NonNull Collection<Period> values) {
		return new PeriodConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of Periods that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withNotIn(@NonNull Collection<Period> values) {
		return new PeriodConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold Period (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withGreaterThan(@NonNull Period value) {
		return new PeriodConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Period (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withGreaterThanOrEqual(@NonNull Period value) {
		return new PeriodConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold Period (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withLessThan(@NonNull Period value) {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Period (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withLessThanOrEqual(@NonNull Period value) {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum Period (exclusive)
	 * @param max The maximum Period (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withBetween(@NonNull Period min, @NonNull Period max) {
		return new PeriodConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(min), false)), Optional.of(Pair.of(Objects.requireNonNull(max), false)), this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum Period (inclusive)
	 * @param max The maximum Period (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withBetweenOrEqual(@NonNull Period min, @NonNull Period max) {
		return new PeriodConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(min), true)), Optional.of(Pair.of(Objects.requireNonNull(max), true)), this.positive, this.negative, this.zero, this.day, this.month, this.year, this.custom);
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
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, Optional.of(Objects.requireNonNull(dayConfig)), this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified month constraint.<br>
	 *
	 * @param monthConfig The numeric field constraint config for month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withMonth(@NonNull NumericFieldConstraintConfig monthConfig) {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.day, Optional.of(Objects.requireNonNull(monthConfig)), this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified year constraint.<br>
	 *
	 * @param yearConfig The numeric field constraint config for year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withYear(@NonNull NumericFieldConstraintConfig yearConfig) {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, Optional.of(Objects.requireNonNull(yearConfig)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull PeriodConstraintConfig withCustom(@NonNull Constraint<Period> constraint) {
		return new PeriodConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.day, this.month, this.year, Optional.of(Objects.requireNonNull(constraint)));
	}
}
