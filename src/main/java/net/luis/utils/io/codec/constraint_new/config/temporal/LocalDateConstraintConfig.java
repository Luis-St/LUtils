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
 * Configuration record for local date type constraints.<br>
 * <p>
 *     This record stores the constraint values for local date codecs.<br>
 *     It includes base constraints, temporal comparable constraints, temporal span constraints,
 *     and date field constraints.
 * </p>
 * <p>
 *     The after and before fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the local date and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of LocalDates and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 *
 * @author Luis-St
 *
 * @param equalTo The local date equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
 * @param in The local date set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
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
 * @param custom A custom constraint implementation
 */
public record LocalDateConstraintConfig(
	@NonNull Optional<Pair<LocalDate, Boolean>> equalTo,
	@NonNull Optional<Pair<Set<LocalDate>, Boolean>> in,
	@NonNull Optional<Pair<LocalDate, Boolean>> after,
	@NonNull Optional<Pair<LocalDate, Boolean>> before,
	@NonNull Optional<Duration> withinLast,
	@NonNull Optional<Duration> withinNext,
	@NonNull Optional<EnumConstraintConfig<DayOfWeek>> dayOfWeek,
	@NonNull Optional<NumericFieldConstraintConfig> dayOfMonth,
	@NonNull Optional<NumericFieldConstraintConfig> dayOfYear,
	@NonNull Optional<NumericFieldConstraintConfig> weekOfMonth,
	@NonNull Optional<NumericFieldConstraintConfig> weekOfYear,
	@NonNull Optional<EnumConstraintConfig<Month>> month,
	@NonNull Optional<NumericFieldConstraintConfig> year,
	@NonNull Optional<Constraint<LocalDate>> custom
) {
	
	/**
	 * An unconstrained local date configuration with no constraints applied.<br>
	 */
	public static final LocalDateConstraintConfig UNCONSTRAINED = new LocalDateConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new local date constraint config with the specified parameters.<br>
	 *
	 * @param equalTo The LocalDate equality constraint as a pair of (value, negated) where negated=false means equalTo and negated=true means notEqualTo
	 * @param in The LocalDate set constraint as a pair of (values, negated) where negated=false means in and negated=true means notIn
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
	 * @param custom A custom constraint implementation
	 * @throws NullPointerException If any of the optional fields is null
	 * @throws IllegalArgumentException If the 'in' set is empty when present
	 * @throws IllegalArgumentException If withinLast duration is not positive when present
	 * @throws IllegalArgumentException If withinNext duration is not positive when present
	 */
	public LocalDateConstraintConfig {
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
	 * @param value The exact local date that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withEqualTo(@NonNull LocalDate value) {
		Objects.requireNonNull(value, "Value for 'equal to' constraint must not be null");
		return new LocalDateConstraintConfig(Optional.of(Pair.of(value, false)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The local date that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withNotEqualTo(@NonNull LocalDate value) {
		Objects.requireNonNull(value, "Value for 'not equal to' constraint must not be null");
		return new LocalDateConstraintConfig(Optional.of(Pair.of(value, true)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of LocalDates that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withIn(@NonNull Collection<LocalDate> values) {
		Objects.requireNonNull(values, "Values for 'in' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of LocalDates that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withNotIn(@NonNull Collection<LocalDate> values) {
		Objects.requireNonNull(values, "Values for 'not in' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold local date (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withAfter(@NonNull LocalDate value) {
		Objects.requireNonNull(value, "Value for 'after' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, false)), this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold local date (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withAfterOrEqual(@NonNull LocalDate value) {
		Objects.requireNonNull(value, "Value for 'after or equal' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(value, true)), this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold local date (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withBefore(@NonNull LocalDate value) {
		Objects.requireNonNull(value, "Value for 'before' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(value, false)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold local date (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withBeforeOrEqual(@NonNull LocalDate value) {
		Objects.requireNonNull(value, "Value for 'before or equal' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(value, true)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param after The minimum local date (exclusive)
	 * @param before The maximum local date (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withBetween(@NonNull LocalDate after, @NonNull LocalDate before) {
		Objects.requireNonNull(after, "After value for 'between' constraint must not be null");
		Objects.requireNonNull(before, "Before value for 'between' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(after, false)), Optional.of(Pair.of(before, false)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param after The minimum local date (inclusive)
	 * @param before The maximum local date (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withBetweenOrEqual(@NonNull LocalDate after, @NonNull LocalDate before) {
		Objects.requireNonNull(after, "After value for 'between or equal' constraint must not be null");
		Objects.requireNonNull(before, "Before value for 'between or equal' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(after, true)), Optional.of(Pair.of(before, true)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-last constraint.<br>
	 *
	 * @param duration The duration backwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withWithinLast(@NonNull Duration duration) {
		Objects.requireNonNull(duration, "Duration for 'within last' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, Optional.of(duration), this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withWithinNext(@NonNull Duration duration) {
		Objects.requireNonNull(duration, "Duration for 'within next' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, Optional.of(duration), this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of week constraint.<br>
	 *
	 * @param config The enum constraint config for day of week validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withDayOfWeek(@NonNull EnumConstraintConfig<DayOfWeek> config) {
		Objects.requireNonNull(config, "Config for 'day of week' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, Optional.of(config), this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of month constraint.<br>
	 *
	 * @param config The numeric field constraint config for day of month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withDayOfMonth(@NonNull NumericFieldConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'day of month' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, Optional.of(config), this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of year constraint.<br>
	 *
	 * @param config The numeric field constraint config for day of year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withDayOfYear(@NonNull NumericFieldConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'day of year' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, Optional.of(config), this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified week of month constraint.<br>
	 *
	 * @param config The numeric field constraint config for week of month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withWeekOfMonth(@NonNull NumericFieldConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'week of month' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, Optional.of(config), this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified week of year constraint.<br>
	 *
	 * @param config The numeric field constraint config for week of year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withWeekOfYear(@NonNull NumericFieldConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'week of year' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, Optional.of(config), this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified month constraint.<br>
	 *
	 * @param config The enum constraint config for month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withMonth(@NonNull EnumConstraintConfig<Month> config) {
		Objects.requireNonNull(config, "Config for 'month' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, Optional.of(config), this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified year constraint.<br>
	 *
	 * @param config The numeric field constraint config for year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withYear(@NonNull NumericFieldConstraintConfig config) {
		Objects.requireNonNull(config, "Config for 'year' constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, Optional.of(config), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withCustom(@NonNull Constraint<LocalDate> constraint) {
		Objects.requireNonNull(constraint, "Custom constraint must not be null");
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, Optional.of(constraint));
	}
}
