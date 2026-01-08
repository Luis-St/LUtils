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

import java.time.Duration;
import java.util.*;

/**
 * Configuration record for Duration type constraints.<br>
 * <p>
 *     This record stores the constraint values for Duration codecs.<br>
 *     It includes base constraints, comparable constraints, signed constraints, time field constraints,
 *     and temporal span constraints.
 * </p>
 * <p>
 *     The min and max fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the Duration and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of Durations and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The Duration equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The Duration set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param min The minimum Duration constraint as a pair of (value, inclusive)
 * @param max The maximum Duration constraint as a pair of (value, inclusive)
 * @param positive The positive constraint as a Boolean where false means positive and true means nonPositive
 * @param negative The negative constraint as a Boolean where false means negative and true means nonNegative
 * @param zero The zero constraint as a Boolean where false means zero and true means nonZero
 * @param withinLast A Duration specifying how far back from now values must fall
 * @param withinNext A Duration specifying how far ahead from now values must fall
 * @param hour A nested config for hour component constraints
 * @param minute A nested config for minute component constraints
 * @param second A nested config for second component constraints
 * @param millisecond A nested config for millisecond component constraints
 * @param nanosecond A nested config for nanosecond component constraints
 * @param custom A custom constraint implementation
 */
public record DurationConstraintConfig(
	@NonNull Optional<Pair<Duration, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<Duration>, Boolean>> in,
	@NonNull Optional<Pair<Duration, Boolean>> min,
	@NonNull Optional<Pair<Duration, Boolean>> max,
	@NonNull Optional<Boolean> positive,
	@NonNull Optional<Boolean> negative,
	@NonNull Optional<Boolean> zero,
	@NonNull Optional<Duration> withinLast,
	@NonNull Optional<Duration> withinNext,
	@NonNull Optional<NumericFieldConstraintConfig> hour,
	@NonNull Optional<NumericFieldConstraintConfig> minute,
	@NonNull Optional<NumericFieldConstraintConfig> second,
	@NonNull Optional<NumericFieldConstraintConfig> millisecond,
	@NonNull Optional<NumericFieldConstraintConfig> nanosecond,
	@NonNull Optional<Constraint<Duration>> custom
) {

	/**
	 * An unconstrained Duration configuration with no constraints applied.<br>
	 */
	public static final DurationConstraintConfig UNCONSTRAINED = new DurationConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty()
	);

	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact Duration that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withEqualTo(@NonNull Duration value) {
		return new DurationConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The Duration that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withNotEqualTo(@NonNull Duration value) {
		return new DurationConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of Durations that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withIn(@NonNull Collection<Duration> values) {
		return new DurationConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of Durations that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withNotIn(@NonNull Collection<Duration> values) {
		return new DurationConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold Duration (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withGreaterThan(@NonNull Duration value) {
		return new DurationConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Duration (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withGreaterThanOrEqual(@NonNull Duration value) {
		return new DurationConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold Duration (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withLessThan(@NonNull Duration value) {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Duration (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withLessThanOrEqual(@NonNull Duration value) {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum Duration (exclusive)
	 * @param max The maximum Duration (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withBetween(@NonNull Duration min, @NonNull Duration max) {
		return new DurationConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(min), false)), Optional.of(Pair.of(Objects.requireNonNull(max), false)), this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum Duration (inclusive)
	 * @param max The maximum Duration (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withBetweenOrEqual(@NonNull Duration min, @NonNull Duration max) {
		return new DurationConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(min), true)), Optional.of(Pair.of(Objects.requireNonNull(max), true)), this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withPositive() {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(false), this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the non-positive constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withNonPositive() {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, Optional.of(true), this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withNegative() {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(false), this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the non-negative constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withNonNegative() {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, Optional.of(true), this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withZero() {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(false), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the non-zero constraint enabled.<br>
	 *
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withNonZero() {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, Optional.of(true), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified within-last constraint.<br>
	 *
	 * @param duration The duration backwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, Optional.of(Objects.requireNonNull(duration)), this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, Optional.of(Objects.requireNonNull(duration)), this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified hour constraint.<br>
	 *
	 * @param hourConfig The numeric field constraint config for hour validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withHour(@NonNull NumericFieldConstraintConfig hourConfig) {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, Optional.of(Objects.requireNonNull(hourConfig)), this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified minute constraint.<br>
	 *
	 * @param minuteConfig The numeric field constraint config for minute validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withMinute(@NonNull NumericFieldConstraintConfig minuteConfig) {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, Optional.of(Objects.requireNonNull(minuteConfig)), this.second, this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified second constraint.<br>
	 *
	 * @param secondConfig The numeric field constraint config for second validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withSecond(@NonNull NumericFieldConstraintConfig secondConfig) {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, Optional.of(Objects.requireNonNull(secondConfig)), this.millisecond, this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified millisecond constraint.<br>
	 *
	 * @param millisecondConfig The numeric field constraint config for millisecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withMillisecond(@NonNull NumericFieldConstraintConfig millisecondConfig) {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, Optional.of(Objects.requireNonNull(millisecondConfig)), this.nanosecond, this.custom);
	}

	/**
	 * Creates a new config with the specified nanosecond constraint.<br>
	 *
	 * @param nanosecondConfig The numeric field constraint config for nanosecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withNanosecond(@NonNull NumericFieldConstraintConfig nanosecondConfig) {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, Optional.of(Objects.requireNonNull(nanosecondConfig)), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withCustom(@NonNull Constraint<Duration> constraint) {
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, Optional.of(Objects.requireNonNull(constraint)));
	}
}
