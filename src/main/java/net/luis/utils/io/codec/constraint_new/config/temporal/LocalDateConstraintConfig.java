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
 * Configuration record for LocalDate type constraints.<br>
 * <p>
 *     This record stores the constraint values for LocalDate codecs.<br>
 *     It includes base constraints, temporal comparable constraints, temporal span constraints,
 *     and date field constraints.
 * </p>
 * <p>
 *     The after and before fields use {@link Pair} where the first value is the bound
 *     and the second value indicates whether the bound is inclusive (true) or exclusive (false).
 * </p>
 * <p>
 *     The equalTo field uses {@link Pair} where the first value is the LocalDate and
 *     the second value indicates negation (false=equalTo, true=notEqualTo).
 * </p>
 * <p>
 *     The in field uses {@link Pair} where the first value is the set of LocalDates and
 *     the second value indicates negation (false=in, true=notIn).
 * </p>
 *
 * @author Luis-St
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
	 * An unconstrained LocalDate configuration with no constraints applied.<br>
	 */
	public static final LocalDateConstraintConfig UNCONSTRAINED = new LocalDateConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Creates a new config with the specified equal-to constraint.<br>
	 *
	 * @param value The exact LocalDate that should be matched
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withEqualTo(@NonNull LocalDate value) {
		return new LocalDateConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified not-equal-to constraint.<br>
	 *
	 * @param value The LocalDate that should be excluded
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withNotEqualTo(@NonNull LocalDate value) {
		return new LocalDateConstraintConfig(Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified inclusion constraint.<br>
	 *
	 * @param values The collection of LocalDates that are allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withIn(@NonNull Collection<LocalDate> values) {
		return new LocalDateConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), false)), this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified exclusion constraint.<br>
	 *
	 * @param values The collection of LocalDates that are not allowed
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withNotIn(@NonNull Collection<LocalDate> values) {
		return new LocalDateConstraintConfig(this.equalTo, Optional.of(Pair.of(Set.copyOf(values), true)), this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after constraint (exclusive).<br>
	 *
	 * @param value The threshold LocalDate (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withAfter(@NonNull LocalDate value) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified after-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold LocalDate (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withAfterOrEqual(@NonNull LocalDate value) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before constraint (exclusive).<br>
	 *
	 * @param value The threshold LocalDate (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withBefore(@NonNull LocalDate value) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(Objects.requireNonNull(value), false)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified before-or-equal constraint (inclusive).<br>
	 *
	 * @param value The threshold LocalDate (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withBeforeOrEqual(@NonNull LocalDate value) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, Optional.of(Pair.of(Objects.requireNonNull(value), true)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (exclusive on both bounds).<br>
	 *
	 * @param after The minimum LocalDate (exclusive)
	 * @param before The maximum LocalDate (exclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withBetween(@NonNull LocalDate after, @NonNull LocalDate before) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(after), false)), Optional.of(Pair.of(Objects.requireNonNull(before), false)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified between constraint (inclusive on both bounds).<br>
	 *
	 * @param after The minimum LocalDate (inclusive)
	 * @param before The maximum LocalDate (inclusive)
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withBetweenOrEqual(@NonNull LocalDate after, @NonNull LocalDate before) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, Optional.of(Pair.of(Objects.requireNonNull(after), true)), Optional.of(Pair.of(Objects.requireNonNull(before), true)), this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-last constraint.<br>
	 *
	 * @param duration The duration backwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, Optional.of(Objects.requireNonNull(duration)), this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified within-next constraint.<br>
	 *
	 * @param duration The duration forwards from now
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, Optional.of(Objects.requireNonNull(duration)), this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of week constraint.<br>
	 *
	 * @param config The enum constraint config for day of week validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withDayOfWeek(@NonNull EnumConstraintConfig<DayOfWeek> config) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, Optional.of(Objects.requireNonNull(config)), this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of month constraint.<br>
	 *
	 * @param config The numeric field constraint config for day of month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withDayOfMonth(@NonNull NumericFieldConstraintConfig config) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, Optional.of(Objects.requireNonNull(config)), this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified day of year constraint.<br>
	 *
	 * @param config The numeric field constraint config for day of year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withDayOfYear(@NonNull NumericFieldConstraintConfig config) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, Optional.of(Objects.requireNonNull(config)), this.weekOfMonth, this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified week of month constraint.<br>
	 *
	 * @param config The numeric field constraint config for week of month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withWeekOfMonth(@NonNull NumericFieldConstraintConfig config) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, Optional.of(Objects.requireNonNull(config)), this.weekOfYear, this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified week of year constraint.<br>
	 *
	 * @param config The numeric field constraint config for week of year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withWeekOfYear(@NonNull NumericFieldConstraintConfig config) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, Optional.of(Objects.requireNonNull(config)), this.month, this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified month constraint.<br>
	 *
	 * @param config The enum constraint config for month validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withMonth(@NonNull EnumConstraintConfig<Month> config) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, Optional.of(Objects.requireNonNull(config)), this.year, this.custom);
	}
	
	/**
	 * Creates a new config with the specified year constraint.<br>
	 *
	 * @param config The numeric field constraint config for year validation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withYear(@NonNull NumericFieldConstraintConfig config) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, Optional.of(Objects.requireNonNull(config)), this.custom);
	}
	
	/**
	 * Creates a new config with the specified custom constraint.<br>
	 *
	 * @param constraint The custom constraint implementation
	 * @return A new config with the constraint applied
	 */
	public @NonNull LocalDateConstraintConfig withCustom(@NonNull Constraint<LocalDate> constraint) {
		return new LocalDateConstraintConfig(this.equalTo, this.in, this.after, this.before, this.withinLast, this.withinNext, this.dayOfWeek, this.dayOfMonth, this.dayOfYear, this.weekOfMonth, this.weekOfYear, this.month, this.year, Optional.of(Objects.requireNonNull(constraint)));
	}
}
