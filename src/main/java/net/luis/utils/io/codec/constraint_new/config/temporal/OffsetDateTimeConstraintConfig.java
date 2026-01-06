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
import net.luis.utils.io.codec.constraint_new.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint_new.config.NumericFieldConstraintConfig;
import net.luis.utils.util.Pair;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.*;

/**
 * Configuration record for OffsetDateTime type constraints.<br>
 * <p>
 *     This record stores the constraint values for OffsetDateTime codecs.<br>
 *     It includes base constraints, temporal comparable constraints, temporal span constraints,
 *     date and time field constraints, and offset constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The exact OffsetDateTime that should be matched
 * @param notEqualTo The OffsetDateTime that should be excluded
 * @param in The set of OffsetDateTimes that are allowed
 * @param notIn The set of OffsetDateTimes that are not allowed
 * @param after The "after" temporal constraint as a pair of (value, inclusive)
 * @param before The "before" temporal constraint as a pair of (value, inclusive)
 * @param withinLast A Duration specifying how far back from now values must fall
 * @param withinNext A Duration specifying how far ahead from now values must fall
 * @param dayOfWeek A nested config for day of week constraints
 * @param dayOfMonth A nested config for day of month constraints
 * @param dayOfYear A nested config for day of year constraints
 * @param weekOfMonth A nested config for week of month constraints
 * @param weekOfYear A nested config for week of year constraints
 * @param month A nested config for month constraints
 * @param year A nested config for year constraints
 * @param hour A nested config for hour constraints
 * @param minute A nested config for minute constraints
 * @param second A nested config for second constraints
 * @param millisecond A nested config for millisecond constraints
 * @param nanosecond A nested config for nanosecond constraints
 * @param offset A nested config for offset constraints
 * @param custom A custom constraint implementation
 */
public record OffsetDateTimeConstraintConfig(
	@NonNull Optional<OffsetDateTime> equalTo,
	@NonNull Optional<OffsetDateTime> notEqualTo,
	@NonNull Optional<Set<OffsetDateTime>> in,
	@NonNull Optional<Set<OffsetDateTime>> notIn,
	@NonNull Optional<Pair<OffsetDateTime, Boolean>> after,
	@NonNull Optional<Pair<OffsetDateTime, Boolean>> before,
	@NonNull Optional<Duration> withinLast,
	@NonNull Optional<Duration> withinNext,
	@NonNull Optional<EnumConstraintConfig<DayOfWeek>> dayOfWeek,
	@NonNull Optional<NumericFieldConstraintConfig> dayOfMonth,
	@NonNull Optional<NumericFieldConstraintConfig> dayOfYear,
	@NonNull Optional<NumericFieldConstraintConfig> weekOfMonth,
	@NonNull Optional<NumericFieldConstraintConfig> weekOfYear,
	@NonNull Optional<EnumConstraintConfig<Month>> month,
	@NonNull Optional<NumericFieldConstraintConfig> year,
	@NonNull Optional<NumericFieldConstraintConfig> hour,
	@NonNull Optional<NumericFieldConstraintConfig> minute,
	@NonNull Optional<NumericFieldConstraintConfig> second,
	@NonNull Optional<NumericFieldConstraintConfig> millisecond,
	@NonNull Optional<NumericFieldConstraintConfig> nanosecond,
	@NonNull Optional<ZoneOffsetConstraintConfig> offset,
	@NonNull Optional<Constraint<OffsetDateTime>> custom
) {

	/**
	 * An unconstrained OffsetDateTime configuration with no constraints applied.<br>
	 */
	public static final OffsetDateTimeConstraintConfig UNCONSTRAINED = new OffsetDateTimeConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(),
		Optional.empty()
	);

	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact OffsetDateTime that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withEqualTo(@NonNull OffsetDateTime value) {
		return new OffsetDateTimeConstraintConfig(Optional.of(Objects.requireNonNull(value)), this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The OffsetDateTime that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withNotEqualTo(@NonNull OffsetDateTime value) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, Optional.of(Objects.requireNonNull(value)), this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of OffsetDateTimes that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withIn(@NonNull Collection<OffsetDateTime> values) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, Optional.of(Set.copyOf(values)), this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of OffsetDateTimes that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withNotIn(@NonNull Collection<OffsetDateTime> values) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, Optional.of(Set.copyOf(values)), this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold OffsetDateTime (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withAfter(@NonNull OffsetDateTime value) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold OffsetDateTime (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withAfterOrEqual(@NonNull OffsetDateTime value) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold OffsetDateTime (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withBefore(@NonNull OffsetDateTime value) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold OffsetDateTime (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withBeforeOrEqual(@NonNull OffsetDateTime value) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param after The minimum OffsetDateTime (exclusive)
	 * @param before The maximum OffsetDateTime (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withBetween(@NonNull OffsetDateTime after, @NonNull OffsetDateTime before) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(after), false)), Optional.of(Pair.of(Objects.requireNonNull(before), false)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param after The minimum OffsetDateTime (inclusive)
	 * @param before The maximum OffsetDateTime (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withBetweenOrEqual(@NonNull OffsetDateTime after, @NonNull OffsetDateTime before) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, Optional.of(Pair.of(Objects.requireNonNull(after), true)), Optional.of(Pair.of(Objects.requireNonNull(before), true)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified within-last constraint.<br>
	 *
	 * @param duration The duration backwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, Optional.of(Objects.requireNonNull(duration)), this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, Optional.of(Objects.requireNonNull(duration)), this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified day of week constraint.<br>
	 *
	 * @param config The enum constraint config for day of week validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withDayOfWeek(@NonNull EnumConstraintConfig<DayOfWeek> config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, Optional.of(Objects.requireNonNull(config)), this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified day of month constraint.<br>
	 *
	 * @param config The numeric field constraint config for day of month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withDayOfMonth(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, Optional.of(Objects.requireNonNull(config)), this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified day of year constraint.<br>
	 *
	 * @param config The numeric field constraint config for day of year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withDayOfYear(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, Optional.of(Objects.requireNonNull(config)), this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified week of month constraint.<br>
	 *
	 * @param config The numeric field constraint config for week of month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withWeekOfMonth(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, Optional.of(Objects.requireNonNull(config)), this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified week of year constraint.<br>
	 *
	 * @param config The numeric field constraint config for week of year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withWeekOfYear(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, Optional.of(Objects.requireNonNull(config)), this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified month constraint.<br>
	 *
	 * @param config The enum constraint config for month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withMonth(@NonNull EnumConstraintConfig<Month> config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, Optional.of(Objects.requireNonNull(config)), this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified year constraint.<br>
	 *
	 * @param config The numeric field constraint config for year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withYear(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, Optional.of(Objects.requireNonNull(config)), this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified hour constraint.<br>
	 *
	 * @param config The numeric field constraint config for hour validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withHour(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, Optional.of(Objects.requireNonNull(config)), this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified minute constraint.<br>
	 *
	 * @param config The numeric field constraint config for minute validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withMinute(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, Optional.of(Objects.requireNonNull(config)), this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified second constraint.<br>
	 *
	 * @param config The numeric field constraint config for second validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withSecond(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, Optional.of(Objects.requireNonNull(config)), this.millisecond, this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified millisecond constraint.<br>
	 *
	 * @param config The numeric field constraint config for millisecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withMillisecond(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, Optional.of(Objects.requireNonNull(config)), this.nanosecond, this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified nanosecond constraint.<br>
	 *
	 * @param config The numeric field constraint config for nanosecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withNanosecond(@NonNull NumericFieldConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, Optional.of(Objects.requireNonNull(config)), this.offset, this.custom);
	}

	/**
	 * Creates a new config with the specified offset constraint.<br>
	 *
	 * @param config The zone offset constraint config for offset validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withOffset(@NonNull ZoneOffsetConstraintConfig config) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, Optional.of(Objects.requireNonNull(config)), this.custom);
	}

	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withCustom(@NonNull Constraint<OffsetDateTime> constraint) {
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.notEqualTo, this.in, this.notIn, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, Optional.of(Objects.requireNonNull(constraint)));
	}
}
