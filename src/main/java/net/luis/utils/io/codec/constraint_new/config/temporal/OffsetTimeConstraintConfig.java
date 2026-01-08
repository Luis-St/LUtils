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
import java.time.OffsetTime;
import java.util.*;

/**
 * Configuration record for OffsetTime type constraints.<br>
 * <p>
 *     This record stores the constraint values for OffsetTime codecs.<br>
 *     It includes base constraints, temporal comparable constraints, temporal span constraints,
 *     time field constraints, and offset constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The exact OffsetTime that should be matched (Boolean: false=equalTo, true=notEqualTo)
 * @param in The set of OffsetTimes that are allowed or excluded (Boolean: false=in, true=notIn)
 * @param after The "after" temporal constraint as a pair of (value, inclusive)
 * @param before The "before" temporal constraint as a pair of (value, inclusive)
 * @param withinLast A Duration specifying how far back from now values must fall
 * @param withinNext A Duration specifying how far ahead from now values must fall
 * @param hour A nested config for hour constraints
 * @param minute A nested config for minute constraints
 * @param second A nested config for second constraints
 * @param millisecond A nested config for millisecond constraints
 * @param nanosecond A nested config for nanosecond constraints
 * @param offset A nested config for offset constraints
 * @param custom A custom constraint implementation
 */
public record OffsetTimeConstraintConfig(
	@NonNull Optional<Pair<OffsetTime, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<OffsetTime>, Boolean>> in,
	@NonNull Optional<Pair<OffsetTime, Boolean>> after,
	@NonNull Optional<Pair<OffsetTime, Boolean>> before,
	@NonNull Optional<Duration> withinLast,
	@NonNull Optional<Duration> withinNext,
	@NonNull Optional<NumericFieldConstraintConfig> hour,
	@NonNull Optional<NumericFieldConstraintConfig> minute,
	@NonNull Optional<NumericFieldConstraintConfig> second,
	@NonNull Optional<NumericFieldConstraintConfig> millisecond,
	@NonNull Optional<NumericFieldConstraintConfig> nanosecond,
	@NonNull Optional<ZoneOffsetConstraintConfig> offset,
	@NonNull Optional<Constraint<OffsetTime>> custom
) {

	/**
	 * An unconstrained OffsetTime configuration with no constraints applied.<br>
	 */
	public static final OffsetTimeConstraintConfig UNCONSTRAINED = new OffsetTimeConstraintConfig(
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(),
		Optional.empty()
	);

	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact OffsetTime that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withEqualTo(@NonNull OffsetTime value) {
		return new OffsetTimeConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The OffsetTime that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withNotEqualTo(@NonNull OffsetTime value) {
		return new OffsetTimeConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of OffsetTimes that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withIn(@NonNull Collection<OffsetTime> values) {
		return new OffsetTimeConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of OffsetTimes that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withNotIn(@NonNull Collection<OffsetTime> values) {
		return new OffsetTimeConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold OffsetTime (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withAfter(@NonNull OffsetTime value) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold OffsetTime (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withAfterOrEqual(@NonNull OffsetTime value) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold OffsetTime (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withBefore(@NonNull OffsetTime value) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold OffsetTime (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withBeforeOrEqual(@NonNull OffsetTime value) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param after The minimum OffsetTime (exclusive)
	 * @param before The maximum OffsetTime (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withBetween(@NonNull OffsetTime after, @NonNull OffsetTime before) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(after), false)), Optional.of(Pair.of(Objects.requireNonNull(before), false)), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param after The minimum OffsetTime (inclusive)
	 * @param before The maximum OffsetTime (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withBetweenOrEqual(@NonNull OffsetTime after, @NonNull OffsetTime before) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(after), true)), Optional.of(Pair.of(Objects.requireNonNull(before), true)), this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified within-last constraint.<br>
	 *
	 * @param duration The duration backwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, Optional.of(Objects.requireNonNull(duration)), this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, Optional.of(Objects.requireNonNull(duration)), this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified hour constraint.<br>
	 *
	 * @param config The numeric field constraint config for hour validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withHour(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, Optional.of(Objects.requireNonNull(config)), this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified minute constraint.<br>
	 *
	 * @param config The numeric field constraint config for minute validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withMinute(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, Optional.of(Objects.requireNonNull(config)), this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified second constraint.<br>
	 *
	 * @param config The numeric field constraint config for second validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withSecond(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, Optional.of(Objects.requireNonNull(config)), this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified millisecond constraint.<br>
	 *
	 * @param config The numeric field constraint config for millisecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withMillisecond(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, Optional.of(Objects.requireNonNull(config)), this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified nanosecond constraint.<br>
	 *
	 * @param config The numeric field constraint config for nanosecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withNanosecond(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, Optional.of(Objects.requireNonNull(config)), this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified offset constraint.<br>
	 *
	 * @param config The zone offset constraint config for offset validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withOffset(@NonNull ZoneOffsetConstraintConfig config) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, Optional.of(Objects.requireNonNull(config)), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetTimeConstraintConfig withCustom(@NonNull Constraint<OffsetTime> constraint) {
		return new OffsetTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, Optional.of(Objects.requireNonNull(constraint)));
	}
}
