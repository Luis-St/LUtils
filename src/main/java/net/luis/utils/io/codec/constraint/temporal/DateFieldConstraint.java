/*
 * LUtils
 * Copyright (C) 2025 Luis Staudt
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

package net.luis.utils.io.codec.constraint.temporal;

import net.luis.utils.io.codec.constraint.config.temporal.core.FieldConstraintConfig;
import net.luis.utils.io.codec.constraint.core.ComparableConstraintBuilder;
import net.luis.utils.io.codec.constraint.core.provider.DateFieldConstraintConfigProvider;
import org.jspecify.annotations.NonNull;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A constraint interface for date field validation.<br>
 * <p>
 *     This interface provides methods to apply constraints to individual date components
 *     such as day of week, day of month, month, and year of temporal types.<br>
 *     Constraints for numeric fields (day of month, year) are defined using the
 *     {@link ComparableConstraintBuilder} which allows chaining of multiple constraint operations.
 * </p>
 * <p>
 *     Applies to: LocalDate, LocalDateTime, ZonedDateTime, OffsetDateTime.
 * </p>
 * <p>
 *     Example usage:
 * </p>
 * <pre>{@code
 * // Only weekdays
 * codec.dayOfWeekIn(Set.of(
 *     DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
 *     DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
 * ))
 *
 * // Summer months only
 * codec.monthIn(Set.of(Month.JUNE, Month.JULY, Month.AUGUST))
 *
 * // First two weeks of the month
 * codec.dayOfMonth(c -> c.betweenOrEqual(1, 14))
 * }</pre>
 *
 * @see ComparableConstraintBuilder
 * @see TimeFieldConstraint
 *
 * @author Luis-St
 *
 * @param <C> The codec type
 * @param <V> The constraint configuration type
 */
@FunctionalInterface
public interface DateFieldConstraint<C, V extends DateFieldConstraintConfigProvider<V>> {
	
	/**
	 * Applies a date field constraint to the codec.<br>
	 * <p>
	 *     This method must be implemented by codecs to handle the application of date field constraints.
	 * </p>
	 *
	 * @param configModifier A function that modifies the constraint configuration
	 * @return A new codec with the applied constraint
	 * @throws NullPointerException If the constraint config modifier is null
	 */
	@NonNull C applyDateFieldConstraint(@NonNull UnaryOperator<V> configModifier);
	
	/**
	 * Applies a day of week constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values fall on the specified day of week.<br>
	 *     For example, {@code dayOfWeek(DayOfWeek.MONDAY)} ensures the date is always a Monday.
	 * </p>
	 *
	 * @param day The required day of week
	 * @return A new codec with the applied day of week constraint
	 * @throws NullPointerException If the day is null
	 * @see #dayOfWeekIn(Set)
	 */
	default @NonNull C dayOfWeek(@NonNull DayOfWeek day) {
		return this.applyDateFieldConstraint(config -> config.withDayOfWeek(Set.of(day)));
	}

	/**
	 * Applies a day of week set constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values fall on one of the specified days of week.<br>
	 *     For example, {@code dayOfWeekIn(Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY))} ensures the date is a weekday.
	 * </p>
	 *
	 * @param daysOfWeek The set of allowed days of week
	 * @return A new codec with the applied day of week constraint
	 * @throws NullPointerException If daysOfWeek is null or contains null
	 * @throws IllegalArgumentException If daysOfWeek is empty
	 * @see #dayOfWeek(DayOfWeek)
	 */
	default @NonNull C dayOfWeekIn(@NonNull Set<DayOfWeek> daysOfWeek) {
		return this.applyDateFieldConstraint(config -> config.withDayOfWeek(daysOfWeek));
	}

	/**
	 * Applies a constraint to the day of month field of the temporal value.<br>
	 * <p>
	 *     The day of month field represents the day within the month in the range 1-31.<br>
	 *     The constraint is defined using a builder function that configures the allowed day values.
	 * </p>
	 *
	 * @param builderFunction A function that configures the day of month constraint using a builder
	 * @return A new codec with the applied day of month constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #month(Month)
	 */
	default @NonNull C dayOfMonth(@NonNull Function<ComparableConstraintBuilder, ComparableConstraintBuilder> builderFunction) {
		Objects.requireNonNull(builderFunction, "Builder function must not be null");
		
		FieldConstraintConfig fieldConfig = builderFunction.apply(new ComparableConstraintBuilder()).build();
		return this.applyDateFieldConstraint(config -> config.withDayOfMonth(fieldConfig));
	}

	/**
	 * Applies a month constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values fall in the specified month.<br>
	 *     For example, {@code month(Month.DECEMBER)} ensures the date is always in December.
	 * </p>
	 *
	 * @param month The required month
	 * @return A new codec with the applied month constraint
	 * @throws NullPointerException If month is null
	 * @see #monthIn(Set)
	 */
	default @NonNull C month(@NonNull Month month) {
		return this.applyDateFieldConstraint(config -> config.withMonth(Set.of(month)));
	}

	/**
	 * Applies a month set constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values fall in one of the specified months.<br>
	 *     For example, {@code monthIn(Set.of(JUNE, JULY, AUGUST))} ensures the date is in summer.
	 * </p>
	 *
	 * @param months The set of allowed months
	 * @return A new codec with the applied month constraint
	 * @throws NullPointerException If months is null or contains null
	 * @throws IllegalArgumentException If months is empty
	 * @see #month(Month)
	 */
	default @NonNull C monthIn(@NonNull Set<Month> months) {
		return this.applyDateFieldConstraint(config -> config.withMonth(months));
	}

	/**
	 * Applies a constraint to the year field of the temporal value.<br>
	 * <p>
	 *     The constraint is defined using a builder function that configures the allowed year values.
	 * </p>
	 *
	 * @param builderFunction A function that configures the year constraint using a builder
	 * @return A new codec with the applied year constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #dayOfMonth(Function)
	 */
	default @NonNull C year(@NonNull Function<ComparableConstraintBuilder, ComparableConstraintBuilder> builderFunction) {
		Objects.requireNonNull(builderFunction, "Builder function must not be null");
		
		FieldConstraintConfig fieldConfig = builderFunction.apply(new ComparableConstraintBuilder()).build();
		return this.applyDateFieldConstraint(config -> config.withYear(fieldConfig));
	}
}
