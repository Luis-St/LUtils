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

package net.luis.utils.io.codec.constraint.config.temporal.local;

import net.luis.utils.io.codec.constraint.config.ConstraintConfig;
import net.luis.utils.io.codec.constraint.config.EnumConstraintConfig;
import net.luis.utils.io.codec.constraint.config.matcher.ConstraintMatchers;
import net.luis.utils.io.codec.constraint.config.numeric.NumericConstraintConfig;
import net.luis.utils.io.codec.constraint.core.Constraint;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.time.chrono.ChronoLocalDateTime;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * Configuration record for local date time type constraints.<br>
 * <p>
 *     This record stores the constraint values for local date time codecs.<br>
 *     It includes base constraints, temporal comparable constraints, temporal span constraints,
 *     and both date and time field constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The exact local date time that should be matched (Boolean: false=equalTo, true=notEqualTo)
 * @param in The set of LocalDateTimes that are allowed or excluded (Boolean: false=in, true=notIn)
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
 * @param custom A custom constraint implementation
 */
public record LocalDateTimeConstraintConfig(
	@NonNull Optional<Pair<LocalDateTime, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<LocalDateTime>, Boolean>> in,
	@NonNull Optional<Pair<LocalDateTime, Boolean>> after,
	@NonNull Optional<Pair<LocalDateTime, Boolean>> before,
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
	@NonNull Optional<Constraint<LocalDateTime>> custom
) implements ConstraintConfig<LocalDateTime> {
	
	/**
	 * An unconstrained local date time configuration with no constraints applied.<br>
	 */
	public static final LocalDateTimeConstraintConfig UNCONSTRAINED = new LocalDateTimeConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new local date time constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The exact local date time that should be matched (Boolean: false=equalTo, true=notEqualTo)
	 * @param in The set of LocalDateTimes that are allowed or excluded (Boolean: false=in, true=notIn)
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
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the 'in' set is empty when present
	 * @throws IllegalArgumentException If withinLast duration is not positive when present
	 * @throws IllegalArgumentException If withinNext duration is not positive when present
	 */
	public LocalDateTimeConstraintConfig {
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
	 * @param value The exact local date time that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withEqualTo(@NonNull LocalDateTime value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new LocalDateTimeConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The local date time that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withNotEqualTo(@NonNull LocalDateTime value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new LocalDateTimeConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of LocalDateTimes that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withIn(@NonNull Collection<LocalDateTime> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of LocalDateTimes that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withNotIn(@NonNull Collection<LocalDateTime> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold local date time (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withAfter(@NonNull LocalDateTime value) {
		Objects.requireNonNull(value, "Value for 'after' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold local date time (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withAfterOrEqual(@NonNull LocalDateTime value) {
		Objects.requireNonNull(value, "Value for 'after or equal' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold local date time (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withBefore(@NonNull LocalDateTime value) {
		Objects.requireNonNull(value, "Value for 'before' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(value, false)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold local date time (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withBeforeOrEqual(@NonNull LocalDateTime value) {
		Objects.requireNonNull(value, "Value for 'before or equal' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(value, true)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param after The minimum local date time (exclusive)
	 * @param before The maximum local date time (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withBetween(@NonNull LocalDateTime after, @NonNull LocalDateTime before) {
		Objects.requireNonNull(after, "After value for 'between' constraint must not be null");
		Objects.requireNonNull(before, "Before value for 'between' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(after, false)), Optional.of(Pair.of(before, false)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param after The minimum local date time (inclusive)
	 * @param before The maximum local date time (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withBetweenOrEqual(@NonNull LocalDateTime after, @NonNull LocalDateTime before) {
		Objects.requireNonNull(after, "After value for 'between or equal' constraint must not be null");
		Objects.requireNonNull(before, "Before value for 'between or equal' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(after, true)), Optional.of(Pair.of(before, true)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-last constraint.<br>
	 *
	 * @param duration The duration backwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withWithinLast(@NonNull Duration duration) {
		Objects.requireNonNull(duration, "Duration for 'within last' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, Optional.of(duration), this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withWithinNext(@NonNull Duration duration) {
		Objects.requireNonNull(duration, "Duration for 'within next' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, Optional.of(duration), this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of week constraint.<br>
	 *
	 * @param config The enum constraint config for day of week validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withDayOfWeek(@NonNull EnumConstraintConfig<DayOfWeek> config) {
		Objects.requireNonNull(config, "Config for 'day of week' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, Optional.of(config), this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of month constraint.<br>
	 *
	 * @param config The numeric field constraint config for day of month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withDayOfMonth(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'day of month' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, Optional.of(config), this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of year constraint.<br>
	 *
	 * @param config The numeric field constraint config for day of year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withDayOfYear(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'day of year' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, Optional.of(config), this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified week of month constraint.<br>
	 *
	 * @param config The numeric field constraint config for week of month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withWeekOfMonth(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'week of month' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, Optional.of(config), this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified week of year constraint.<br>
	 *
	 * @param config The numeric field constraint config for week of year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withWeekOfYear(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'week of year' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, Optional.of(config), this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified month constraint.<br>
	 *
	 * @param config The enum constraint config for month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withMonth(@NonNull EnumConstraintConfig<Month> config) {
		Objects.requireNonNull(config, "Config for 'month' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, Optional.of(config), this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified year constraint.<br>
	 *
	 * @param config The numeric field constraint config for year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withYear(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'year' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, Optional.of(config), this.hour, this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified hour constraint.<br>
	 *
	 * @param config The numeric field constraint config for hour validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withHour(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'hour' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, Optional.of(config), this.minute, this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified minute constraint.<br>
	 *
	 * @param config The numeric field constraint config for minute validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withMinute(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'minute' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, Optional.of(config), this.second, this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified second constraint.<br>
	 *
	 * @param config The numeric field constraint config for second validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withSecond(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'second' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, Optional.of(config), this.millisecond, this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified millisecond constraint.<br>
	 *
	 * @param config The numeric field constraint config for millisecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withMillisecond(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'millisecond' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, Optional.of(config), this.nanosecond, this.custom);
	}
	
	/**
	 * Creates a new config with the specified nanosecond constraint.<br>
	 *
	 * @param config The numeric field constraint config for nanosecond validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withNanosecond(@NonNull NumericConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'nanosecond' constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, Optional.of(config), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateTimeConstraintConfig withCustom(@NonNull Constraint<LocalDateTime> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new LocalDateTimeConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.hour, this.minute, this.second, this.millisecond, this.nanosecond, Optional.of(constraint));
	}
	//endregion
	
	@Override
	public @NotNull Result<Void> matches(@NonNull LocalDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		
		return ConstraintMatchers.allOf(
			() -> ConstraintMatchers.matchEqualTo(value, this.equalTo),
			() -> ConstraintMatchers.matchIn(value, this.in),
			() -> ConstraintMatchers.matchRange(value, this.after, this.before, ChronoLocalDateTime::compareTo),
			() -> ConstraintMatchers.matchWithinLast(value, this.withinLast, LocalDateTime::now, LocalDateTime::minus, "Local date time"),
			() -> ConstraintMatchers.matchWithinNext(value, this.withinNext, LocalDateTime::now, LocalDateTime::plus, "Local date time"),
			() -> ConstraintMatchers.matchNestedConfig(value.getDayOfWeek(), this.dayOfWeek, "Day of week"),
			() -> ConstraintMatchers.matchNumericField(value.getDayOfMonth(), this.dayOfMonth, "day of month"),
			() -> ConstraintMatchers.matchNumericField(value.getDayOfYear(), this.dayOfYear, "day of year"),
			() -> ConstraintMatchers.matchNumericField(value.get(WeekFields.ISO.weekOfMonth()), this.weekOfMonth, "week of month"),
			() -> ConstraintMatchers.matchNumericField(value.get(WeekFields.ISO.weekOfWeekBasedYear()), this.weekOfYear, "week of year"),
			() -> ConstraintMatchers.matchNestedConfig(value.getMonth(), this.month, "Month"),
			() -> ConstraintMatchers.matchNumericField(value.getYear(), this.year, "year"),
			() -> ConstraintMatchers.matchNumericField(value.getHour(), this.hour, "hour"),
			() -> ConstraintMatchers.matchNumericField(value.getMinute(), this.minute, "minute"),
			() -> ConstraintMatchers.matchNumericField(value.getSecond(), this.second, "second"),
			() -> ConstraintMatchers.matchNumericField(value.getNano() / 1_000_000, this.millisecond, "millisecond"),
			() -> ConstraintMatchers.matchNumericField(value.getNano(), this.nanosecond, "nanosecond"),
			() -> ConstraintMatchers.matchCustom(value, this.custom)
		);
	}
}
