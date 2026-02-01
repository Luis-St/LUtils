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

package net.luis.utils.io.codec.constraint.config.temporal.offset;

import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.validator.ConstraintValidators;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.config.temporal.zoned.ZoneOffsetConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * Configuration record for offset date time  type constraints.<br>
 * <p>
 *     This record stores the constraint values for offset date time  codecs.<br>
 *     It includes base constraints, temporal comparable constraints, temporal span constraints,
 *     date and time field constraints, and offset constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The exact offset date time  that should be matched (Boolean: false=equalTo, true=notEqualTo)
 * @param in The set of OffsetDateTimes that are allowed or excluded (Boolean: false=in, true=notIn)
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
	@NonNull Optional<Pair<OffsetDateTime, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<OffsetDateTime>, Boolean>> in,
	@NonNull Optional<Pair<OffsetDateTime, Boolean>> after,
	@NonNull Optional<Pair<OffsetDateTime, Boolean>> before,
	@NonNull Optional<Duration> withinLast,
	@NonNull Optional<Duration> withinNext,
	@NonNull Optional<EnumConstraintConfig<DayOfWeek>> dayOfWeek,
	@NonNull Optional<NumericConstraintConfig> dayOfMonth,
	@NonNull Optional<NumericConstraintConfig> dayOfYear,
	@NonNull Optional<NumericConstraintConfig> weekOfMonth,
	@NonNull Optional<NumericConstraintConfig> weekOfYear,
	@NonNull Optional<EnumConstraintConfig<Month>> month,
	@NonNull Optional<NumericConstraintConfig> year,
	@NonNull Optional<NumericConstraintConfig> hour,
	@NonNull Optional<NumericConstraintConfig> minute,
	@NonNull Optional<NumericConstraintConfig> second,
	@NonNull Optional<NumericConstraintConfig> millisecond,
	@NonNull Optional<NumericConstraintConfig> nanosecond,
	@NonNull Optional<ZoneOffsetConstraintConfig> offset,
	@NonNull Optional<Constraint<OffsetDateTime>> custom
) implements ConstraintConfig<OffsetDateTime> {
	
	/**
	 * An unconstrained offset date time  configuration with no constraints applied.<br>
	 */
	public static final OffsetDateTimeConstraintConfig UNCONSTRAINED = new OffsetDateTimeConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new offset date time constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The exact offset date time  that should be matched (Boolean: false=equalTo, true=notEqualTo)
	 * @param in The set of OffsetDateTimes that are allowed or excluded (Boolean: false=in, true=notIn)
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
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the 'in' set is empty when present
	 * @throws IllegalArgumentException If withinLast duration is not positive when present
	 * @throws IllegalArgumentException If withinNext duration is not positive when present
	 */
	public OffsetDateTimeConstraintConfig {
		Objects.requireNonNull(equalTo, "Optional for 'equal to' constraint must not be null");
		Objects.requireNonNull(in, "Optional for 'in' constraint must not be null");
		Objects.requireNonNull(after, "Optional for 'after' constraint must not be null");
		Objects.requireNonNull(before, "Optional for 'before' constraint must not be null");
		Objects.requireNonNull(withinLast, "Optional for 'within last' constraint must not be null");
		Objects.requireNonNull(withinNext, "Optional for 'within next' constraint must not be null");
		Objects.requireNonNull(dayOfWeek, "Optional for 'day of week' constraint must not be null");
		Objects.requireNonNull(dayOfMonth, "Optional for 'day of month' constraint must not be null");
		Objects.requireNonNull(dayOfYear, "Optional for 'day of year' constraint must not be null");
		Objects.requireNonNull(weekOfMonth, "Optional for 'week of month' constraint must not be null");
		Objects.requireNonNull(weekOfYear, "Optional for 'week of year' constraint must not be null");
		Objects.requireNonNull(month, "Optional for 'month' constraint must not be null");
		Objects.requireNonNull(year, "Optional for 'year' constraint must not be null");
		Objects.requireNonNull(hour, "Optional for 'hour' constraint must not be null");
		Objects.requireNonNull(minute, "Optional for 'minute' constraint must not be null");
		Objects.requireNonNull(second, "Optional for 'second' constraint must not be null");
		Objects.requireNonNull(millisecond, "Optional for 'millisecond' constraint must not be null");
		Objects.requireNonNull(nanosecond, "Optional for 'nanosecond' constraint must not be null");
		Objects.requireNonNull(offset, "Optional for 'offset' constraint must not be null");
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
	
	//region With methods
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact offset date time  that should be matched
	 * @throws NullPointerException If the value is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withEqualTo(@NonNull OffsetDateTime value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The offset date time  that should be excluded
	 * @throws NullPointerException If the value is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withNotEqualTo(@NonNull OffsetDateTime value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of OffsetDateTimes that are allowed
	 * @throws NullPointerException If the values is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withIn(@NonNull Collection<OffsetDateTime> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of OffsetDateTimes that are not allowed
	 * @throws NullPointerException If the values is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withNotIn(@NonNull Collection<OffsetDateTime> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold offset date time  (exclusive)
	 * @throws NullPointerException If the value is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withAfter(@NonNull OffsetDateTime value) {
		Objects.requireNonNull(value, "Value for 'after' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold offset date time  (inclusive)
	 * @throws NullPointerException If the value is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withAfterOrEqual(@NonNull OffsetDateTime value) {
		Objects.requireNonNull(value, "Value for 'after or equal' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold offset date time  (exclusive)
	 * @throws NullPointerException If the value is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withBefore(@NonNull OffsetDateTime value) {
		Objects.requireNonNull(value, "Value for 'before' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(value, false)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold offset date time  (inclusive)
	 * @throws NullPointerException If the value is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withBeforeOrEqual(@NonNull OffsetDateTime value) {
		Objects.requireNonNull(value, "Value for 'before or equal' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(value, true)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param after The minimum offset date time  (exclusive)
	 * @param before The maximum offset date time  (exclusive)
	 * @throws NullPointerException If the after or before is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withBetween(@NonNull OffsetDateTime after, @NonNull OffsetDateTime before) {
		Objects.requireNonNull(after, "After value for 'between' constraint must not be null");
		Objects.requireNonNull(before, "Before value for 'between' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(after, false)), Optional.of(Pair.of(before, false)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param after The minimum offset date time  (inclusive)
	 * @param before The maximum offset date time  (inclusive)
	 * @throws NullPointerException If the after or before is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withBetweenOrEqual(@NonNull OffsetDateTime after, @NonNull OffsetDateTime before) {
		Objects.requireNonNull(after, "After value for 'between or equal' constraint must not be null");
		Objects.requireNonNull(before, "Before value for 'between or equal' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(after, true)), Optional.of(Pair.of(before, true)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-last constraint.<br>
	 *
	 * @param duration The duration backwards from now
	 * @throws NullPointerException If the duration is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withWithinLast(@NonNull Duration duration) {
		Objects.requireNonNull(duration, "Duration for 'within last' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, Optional.of(duration), this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @throws NullPointerException If the duration is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withWithinNext(@NonNull Duration duration) {
		Objects.requireNonNull(duration, "Duration for 'within next' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, Optional.of(duration), this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of week constraint.<br>
	 *
	 * @param config The enum constraint config for day of week validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withDayOfWeek(@NonNull EnumConstraintConfig<DayOfWeek> config) {
		Objects.requireNonNull(config, "Config for 'day of week' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, Optional.of(config), this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of month constraint.<br>
	 *
	 * @param config The numeric field constraint config for day of month validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withDayOfMonth(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'day of month' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, Optional.of(config), this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of year constraint.<br>
	 *
	 * @param config The numeric field constraint config for day of year validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withDayOfYear(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'day of year' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, Optional.of(config), this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified week of month constraint.<br>
	 *
	 * @param config The numeric field constraint config for week of month validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withWeekOfMonth(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'week of month' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, Optional.of(config), this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified week of year constraint.<br>
	 *
	 * @param config The numeric field constraint config for week of year validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withWeekOfYear(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'week of year' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, Optional.of(config), this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified month constraint.<br>
	 *
	 * @param config The enum constraint config for month validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withMonth(@NonNull EnumConstraintConfig<Month> config) {
		Objects.requireNonNull(config, "Config for 'month' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, Optional.of(config), this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified year constraint.<br>
	 *
	 * @param config The numeric field constraint config for year validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withYear(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'year' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, Optional.of(config), this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified hour constraint.<br>
	 *
	 * @param config The numeric field constraint config for hour validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withHour(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'hour' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, Optional.of(config), this.minute, this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified minute constraint.<br>
	 *
	 * @param config The numeric field constraint config for minute validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withMinute(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'minute' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, Optional.of(config), this.second, this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified second constraint.<br>
	 *
	 * @param config The numeric field constraint config for second validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withSecond(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'second' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, Optional.of(config), this.millisecond, this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified millisecond constraint.<br>
	 *
	 * @param config The numeric field constraint config for millisecond validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withMillisecond(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'millisecond' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, Optional.of(config), this.nanosecond, this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified nanosecond constraint.<br>
	 *
	 * @param config The numeric field constraint config for nanosecond validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withNanosecond(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'nanosecond' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, Optional.of(config), this.offset, this.custom);
	}
	
	/**
	 * Creates a new config with the specified offset constraint.<br>
	 *
	 * @param config The zone offset constraint config for offset validation
	 * @throws NullPointerException If the config is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withOffset(@NonNull ZoneOffsetConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'offset' constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, Optional.of(config), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @throws NullPointerException If the constraint is null
	 * @return A new config with the constraint applied
	 */
	public @NonNull OffsetDateTimeConstraintConfig withCustom(@NonNull Constraint<OffsetDateTime> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new OffsetDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.offset, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public void validate(@NonNull OffsetDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		ConstraintValidators.validateAll(
			() -> ConstraintValidators.validateEqualTo(value, this.equalTo),
			() -> ConstraintValidators.validateIn(value, this.in),
			() -> ConstraintValidators.validateRange(value, this.after, this.before),
			() -> ConstraintValidators.validateWithinLast(value, this.withinLast, OffsetDateTime::now, OffsetDateTime::minus, "Offset date time"),
			() -> ConstraintValidators.validateWithinNext(value, this.withinNext, OffsetDateTime::now, OffsetDateTime::plus, "Offset date time"),
			() -> ConstraintValidators.validateNestedConfig(value.getDayOfWeek(), this.dayOfWeek, "Day of week"),
			() -> ConstraintValidators.validateNumericField(value.getDayOfMonth(), this.dayOfMonth, "day of month"),
			() -> ConstraintValidators.validateNumericField(value.getDayOfYear(), this.dayOfYear, "day of year"),
			() -> ConstraintValidators.validateNumericField(value.get(WeekFields.ISO.weekOfMonth()), this.weekOfMonth, "week of month"),
			() -> ConstraintValidators.validateNumericField(value.get(WeekFields.ISO.weekOfWeekBasedYear()), this.weekOfYear, "week of year"),
			() -> ConstraintValidators.validateNestedConfig(value.getMonth(), this.month, "Month"),
			() -> ConstraintValidators.validateNumericField(value.getYear(), this.year, "year"),
			() -> ConstraintValidators.validateNumericField(value.getHour(), this.hour, "hour"),
			() -> ConstraintValidators.validateNumericField(value.getMinute(), this.minute, "minute"),
			() -> ConstraintValidators.validateNumericField(value.getSecond(), this.second, "second"),
			() -> ConstraintValidators.validateNumericField(value.getNano() / 1_000_000, this.millisecond, "millisecond"),
			() -> ConstraintValidators.validateNumericField(value.getNano(), this.nanosecond, "nanosecond"),
			() -> ConstraintValidators.validateNestedConfig(value.getOffset(), this.offset, "Offset"),
			() -> ConstraintValidators.validateCustom(value, this.custom)
		);
	}
}
