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
import java.time.LocalTime;
import java.util.*;

/**
 * Configuration record for LocalTime type constraints.<br>
 * <p>
 *     This record stores the constraint values for LocalTime codecs.<br>
 *     It includes base constraints, temporal comparable constraints, temporal span constraints,
 *     and time field constraints.
 * </p>
 * <p>
 *     The after and before fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the LocalTime and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of LocalTimes and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The LocalTime equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The LocalTime set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
 * @param after The "after" temporal constraint as a pair of (value, inclusive)
 * @param before The "before" temporal constraint as a pair of (value, inclusive)
 * @param withinLast A Duration specifying how far back from now values must fall
 * @param withinNext A Duration specifying how far ahead from now values must fall
 * @param hour A nested config for hour constraints
 * @param minute A nested config for minute constraints
 * @param second A nested config for second constraints
 * @param millisecond A nested config for millisecond constraints
 * @param nanosecond A nested config for nanosecond constraints
 * @param custom A custom constraint implementation
 *
 * @throws NullPointerException If any of the optional fields is null
 * @throws IllegalArgumentException If the 'in' set is empty when present
 * @throws IllegalArgumentException If withinLast duration is not positive when present
 * @throws IllegalArgumentException If withinNext duration is not positive when present
 */
public record LocalTimeConstraintConfig(
	@NonNull Optional<Pair<LocalTime, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<LocalTime>, Boolean>> in,
	@NonNull Optional<Pair<LocalTime, Boolean>> after,
	@NonNull Optional<Pair<LocalTime, Boolean>> before,
	@NonNull Optional<Duration> withinLast,
	@NonNull Optional<Duration> withinNext,
	@NonNull Optional<NumericFieldConstraintConfig> hour,
	@NonNull Optional<NumericFieldConstraintConfig> minute,
	@NonNull Optional<NumericFieldConstraintConfig> second,
	@NonNull Optional<NumericFieldConstraintConfig> millisecond,
	@NonNull Optional<NumericFieldConstraintConfig> nanosecond,
	@NonNull Optional<Constraint<LocalTime>> custom
) {
	
	/**
	 * An unconstrained LocalTime configuration with no constraints applied.<br>
	 */
	public static final LocalTimeConstraintConfig UNCONSTRAINED = new LocalTimeConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Canonical constructor that validates all constraint fields.<br>
	 *
	 * @param equalTo The LocalTime equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The LocalTime set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
	 * @param after The "after" temporal constraint as a pair of (value, inclusive)
	 * @param before The "before" temporal constraint as a pair of (value, inclusive)
	 * @param withinLast A Duration specifying how far back from now values must fall
	 * @param withinNext A Duration specifying how far ahead from now values must fall
	 * @param hour A nested config for hour constraints
	 * @param minute A nested config for minute constraints
	 * @param second A nested config for second constraints
	 * @param millisecond A nested config for millisecond constraints
	 * @param nanosecond A nested config for nanosecond constraints
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the 'in' set is empty when present
	 * @throws IllegalArgumentException If withinLast duration is not positive when present
	 * @throws IllegalArgumentException If withinNext duration is not positive when present
	 */
	public LocalTimeConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(after, "Optional for 'after' constraint must not be null");
		Objects.requireNonNull(before, "Optional for 'before' constraint must not be null");
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
		
		if (withinLast.isPresent() && (withinLast.get().isNegative() || withinLast.get().isZero())) {
			throw new IllegalArgumentException("Within last duration must be positive when present, but got " + withinLast.get());
		}
		
		if (withinNext.isPresent() && (withinNext.get().isNegative() || withinNext.get().isZero())) {
			throw new IllegalArgumentException("Within next duration must be positive when present, but got " + withinNext.get());
		}
	}
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact LocalTime that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withEqualTo(@NonNull LocalTime value) {
		return new LocalTimeConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The LocalTime that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withNotEqualTo(@NonNull LocalTime value) {
		return new LocalTimeConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of LocalTimes that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withIn(@NonNull Collection<LocalTime> values) {
		return new LocalTimeConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of LocalTimes that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withNotIn(@NonNull Collection<LocalTime> values) {
		return new LocalTimeConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold LocalTime (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withAfter(@NonNull LocalTime value) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold LocalTime (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withAfterOrEqual(@NonNull LocalTime value) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold LocalTime (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withBefore(@NonNull LocalTime value) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold LocalTime (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withBeforeOrEqual(@NonNull LocalTime value) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param after The minimum LocalTime (exclusive)
	 * @param before The maximum LocalTime (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withBetween(@NonNull LocalTime after, @NonNull LocalTime before) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(after), false)), Optional.of(Pair.of(Objects.requireNonNull(before), false)), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param after The minimum LocalTime (inclusive)
	 * @param before The maximum LocalTime (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withBetweenOrEqual(@NonNull LocalTime after, @NonNull LocalTime before) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(after), true)), Optional.of(Pair.of(Objects.requireNonNull(before), true)), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-last constraint.<br>
	 *
	 * @param duration The duration backwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, Optional.of(Objects.requireNonNull(duration)), this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, Optional.of(Objects.requireNonNull(duration)), this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified hour constraint.<br>
	 *
	 * @param config The numeric field constraint config for hour validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withHour(@NonNull NumericFieldConstraintConfig config) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, Optional.of(Objects.requireNonNull(config)), this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified minute constraint.<br>
	 *
	 * @param config The numeric field constraint config for minute validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withMinute(@NonNull NumericFieldConstraintConfig config) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, Optional.of(Objects.requireNonNull(config)), this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified second constraint.<br>
	 *
	 * @param config The numeric field constraint config for second validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withSecond(@NonNull NumericFieldConstraintConfig config) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, Optional.of(Objects.requireNonNull(config)), this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified millisecond constraint.<br>
	 *
	 * @param config The numeric field constraint config for millisecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withMillisecond(@NonNull NumericFieldConstraintConfig config) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, Optional.of(Objects.requireNonNull(config)), this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified nanosecond constraint.<br>
	 *
	 * @param config The numeric field constraint config for nanosecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withNanosecond(@NonNull NumericFieldConstraintConfig config) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, Optional.of(Objects.requireNonNull(config)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalTimeConstraintConfig withCustom(@NonNull Constraint<LocalTime> constraint) {
		return new LocalTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, Optional.of(Objects.requireNonNull(constraint)));
	}
}
