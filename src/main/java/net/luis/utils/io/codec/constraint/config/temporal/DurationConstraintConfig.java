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

package net.luis.utils.io.codec.constraint.config.temporal;

import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.matcher.ConstraintMatchers;
import net.luis.utils.io.codec.constraint.config.matcher.TemporalMatchers;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
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
	@NonNull Optional<NumericConstraintConfig> hour,
	@NonNull Optional<NumericConstraintConfig> minute,
	@NonNull Optional<NumericConstraintConfig> second,
	@NonNull Optional<NumericConstraintConfig> millisecond,
	@NonNull Optional<NumericConstraintConfig> nanosecond,
	@NonNull Optional<Constraint<Duration>> custom
) implements ConstraintConfig<Duration> {
	
	/**
	 * An unconstrained Duration configuration with no constraints applied.<br>
	 */
	public static final DurationConstraintConfig UNCONSTRAINED = new DurationConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new duration constraint config with the specified parameters.<br>
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
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the 'in' set is empty when present
	 * @throws IllegalArgumentException If positive and negative constraints are both present
	 * @throws IllegalArgumentException If withinLast duration is not positive when present
	 * @throws IllegalArgumentException If withinNext duration is not positive when present
	 */
	public DurationConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(min, "Optional for 'min' constraint must not be null");
		Objects.requireNonNull(max, "Optional for 'max' constraint must not be null");
		Objects.requireNonNull(positive, "Optional for 'positive' constraint must not be null");
		Objects.requireNonNull(negative, "Optional for 'negative' constraint must not be null");
		Objects.requireNonNull(zero, "Optional for 'zero' constraint must not be null");
		Objects.requireNonNull(withinLast, "Optional for 'within last' constraint must not be null");
		Objects.requireNonNull(withinNext, "Optional for 'within next' constraint must not be null");
		Objects.requireNonNull(hour, "Optional for 'hour' constraint must not be null");
		Objects.requireNonNull(minute, "Optional for 'minute' constraint must not be null");
		Objects.requireNonNull(second, "Optional for 'second' constraint must not be null");
		Objects.requireNonNull(millisecond, "Optional for 'millisecond' constraint must not be null");
		Objects.requireNonNull(nanosecond, "Optional for 'nanosecond' constraint must not be null");
		Objects.requireNonNull(custom, "Optional for 'custom' constraint must not be null");
		
		if (in.isPresent() && in.get().getFirst().isEmpty()) {
			throw new IllegalArgumentException("In set must not be empty when present");
		}
		
		if (positive.isPresent() && negative.isPresent()) {
			throw new IllegalArgumentException("Positive and negative constraints are mutually exclusive");
		}
		
		if (withinLast.isPresent() && (withinLast.get().isNegative() || withinLast.get().isZero())) {
			throw new IllegalArgumentException("Within last duration must be positive when present, but got " + withinLast.get());
		}
		
		if (withinNext.isPresent() && (withinNext.get().isNegative() || withinNext.get().isZero())) {
			throw new IllegalArgumentException("Within next duration must be positive when present, but got " + withinNext.get());
		}
	}
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact Duration that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withEqualTo(@NonNull Duration value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new DurationConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The Duration that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withNotEqualTo(@NonNull Duration value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new DurationConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of Durations that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withIn(@NonNull Collection<Duration> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of Durations that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withNotIn(@NonNull Collection<Duration> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than constraint (exclusive).<br>
	 *
	 * @param value The threshold Duration (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withGreaterThan(@NonNull Duration value) {
		Objects.requireNonNull(value, "Value for 'greater than' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified greater-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Duration (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withGreaterThanOrEqual(@NonNull Duration value) {
		Objects.requireNonNull(value, "Value for 'greater than or equal' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than constraint (exclusive).<br>
	 *
	 * @param value The threshold Duration (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withLessThan(@NonNull Duration value) {
		Objects.requireNonNull(value, "Value for 'less than' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, false)), this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified less-than-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold Duration (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withLessThanOrEqual(@NonNull Duration value) {
		Objects.requireNonNull(value, "Value for 'less than or equal' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, Optional.of(Pair.of(value, true)), this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param min The minimum Duration (exclusive)
	 * @param max The maximum Duration (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withBetween(@NonNull Duration min, @NonNull Duration max) {
		Objects.requireNonNull(min, "Min value for 'between' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, false)), Optional.of(Pair.of(max, false)), this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param min The minimum Duration (inclusive)
	 * @param max The maximum Duration (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withBetweenOrEqual(@NonNull Duration min, @NonNull Duration max) {
		Objects.requireNonNull(min, "Min value for 'between or equal' constraint must not be null");
		Objects.requireNonNull(max, "Max value for 'between or equal' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(min, true)), Optional.of(Pair.of(max, true)), this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
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
		Objects.requireNonNull(duration, "Duration for 'within last' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, Optional.of(duration), this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withWithinNext(@NonNull Duration duration) {
		Objects.requireNonNull(duration, "Duration for 'within next' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, Optional.of(duration), this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified hour constraint.<br>
	 *
	 * @param hourConfig The numeric field constraint config for hour validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withHour(@NonNull NumericConstraintConfig hourConfig) {
		Objects.requireNonNull(hourConfig, "Config for 'hour' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, Optional.of(hourConfig), this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified minute constraint.<br>
	 *
	 * @param minuteConfig The numeric field constraint config for minute validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withMinute(@NonNull NumericConstraintConfig minuteConfig) {
		Objects.requireNonNull(minuteConfig, "Config for 'minute' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, Optional.of(minuteConfig), this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified second constraint.<br>
	 *
	 * @param secondConfig The numeric field constraint config for second validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withSecond(@NonNull NumericConstraintConfig secondConfig) {
		Objects.requireNonNull(secondConfig, "Config for 'second' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, Optional.of(secondConfig), this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified millisecond constraint.<br>
	 *
	 * @param millisecondConfig The numeric field constraint config for millisecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withMillisecond(@NonNull NumericConstraintConfig millisecondConfig) {
		Objects.requireNonNull(millisecondConfig, "Config for 'millisecond' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, Optional.of(millisecondConfig), this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified nanosecond constraint.<br>
	 *
	 * @param nanosecondConfig The numeric field constraint config for nanosecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withNanosecond(@NonNull NumericConstraintConfig nanosecondConfig) {
		Objects.requireNonNull(nanosecondConfig, "Config for 'nanosecond' constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, Optional.of(nanosecondConfig), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull DurationConstraintConfig withCustom(@NonNull Constraint<Duration> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new DurationConstraintConfig(this.equalTo, this.in, this.min, this.max, this.positive, this.negative, this.zero, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull Duration value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchRange(value, this.min, this.max),
			() -> TemporalMatchers.matchDurationSign(value, this.positive, this.negative, this.zero),
			() -> TemporalMatchers.matchDurationWithinLast(value, this.withinLast),
			() -> TemporalMatchers.matchDurationWithinNext(value, this.withinNext),
			() -> ConstraintMatchers.matchNumericField((int) value.toHours(), this.hour, "hour"),
			() -> ConstraintMatchers.matchNumericField((int) (value.toMinutes() % 60), this.minute, "minute"),
			() -> ConstraintMatchers.matchNumericField((int) (value.getSeconds() % 60), this.second, "second"),
			() -> ConstraintMatchers.matchNumericField((int) (value.toMillis() % 1000), this.millisecond, "millisecond"),
			() -> ConstraintMatchers.matchNumericField(value.getNano(), this.nanosecond, "nanosecond"),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
