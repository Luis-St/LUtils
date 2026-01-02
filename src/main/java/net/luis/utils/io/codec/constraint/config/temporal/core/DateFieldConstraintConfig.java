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

package net.luis.utils.io.codec.constraint.config.temporal.core;

import net.luis.utils.io.codec.constraint.config.temporal.provider.DateFieldProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.*;

/**
 * Configuration class for date field constraints (daysOfWeek, dayOfMonth, months, year).<br>
 * This class holds date field constraint fields that are common to temporal types with date components.<br>
 *
 * @author Luis-St
 *
 * @param daysOfWeek The allowed days of week (empty if unconstrained)
 * @param dayOfMonth The day of month constraint (empty if unconstrained)
 * @param months The allowed months (empty if unconstrained)
 * @param year The year constraint (empty if unconstrained)
 */
@SuppressWarnings("OptionalContainsCollection")
public record DateFieldConstraintConfig(
	@NonNull Optional<Set<DayOfWeek>> daysOfWeek,
	@NonNull Optional<FieldConstraintConfig> dayOfMonth,
	@NonNull Optional<Set<Month>> months,
	@NonNull Optional<FieldConstraintConfig> year
) {
	
	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all date field values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final DateFieldConstraintConfig UNCONSTRAINED = new DateFieldConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new date field constraint configuration with the specified constraints.<br>
	 *
	 * @param daysOfWeek The allowed days of week (empty if unconstrained)
	 * @param dayOfMonth The day of month constraint (empty if unconstrained)
	 * @param months The allowed months (empty if unconstrained)
	 * @param year The year constraint (empty if unconstrained)
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If constraints are invalid
	 */
	public DateFieldConstraintConfig {
		Objects.requireNonNull(daysOfWeek, "Days of week constraint must not be null");
		Objects.requireNonNull(dayOfMonth, "Day of month constraint must not be null");
		Objects.requireNonNull(months, "Months constraint must not be null");
		Objects.requireNonNull(year, "Year constraint must not be null");
		
		if (daysOfWeek.isPresent() && daysOfWeek.get().isEmpty()) {
			throw new IllegalArgumentException("Days of week constraint must not be empty when present");
		}
		
		if (months.isPresent() && months.get().isEmpty()) {
			throw new IllegalArgumentException("Months constraint must not be empty when present");
		}
	}
	
	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.daysOfWeek.isEmpty() && (this.dayOfMonth.isEmpty() || this.dayOfMonth.get().isUnconstrained()) && this.months.isEmpty() && (this.year.isEmpty() || this.year.get().isUnconstrained()));
	}
	
	/**
	 * Creates a new configuration with a day of week constraint.<br>
	 *
	 * @param daysOfWeek The set of allowed days of the week
	 * @return A new configuration with the day of week constraint applied
	 */
	public @NonNull DateFieldConstraintConfig withDayOfWeek(@NonNull Set<DayOfWeek> daysOfWeek) {
		return new DateFieldConstraintConfig(Optional.of(Set.copyOf(daysOfWeek)), this.dayOfMonth, this.months, this.year);
	}
	
	/**
	 * Creates a new configuration with a day of month field constraint.<br>
	 *
	 * @param monthConfig The day of month field constraint configuration
	 * @return A new configuration with the day of month constraint applied
	 */
	public @NonNull DateFieldConstraintConfig withDayOfMonth(@NonNull FieldConstraintConfig monthConfig) {
		return new DateFieldConstraintConfig(this.daysOfWeek, Optional.of(monthConfig), this.months, this.year);
	}
	
	/**
	 * Creates a new configuration with a month constraint.<br>
	 *
	 * @param months The set of allowed months
	 * @return A new configuration with the month constraint applied
	 */
	public @NonNull DateFieldConstraintConfig withMonth(@NonNull Set<Month> months) {
		return new DateFieldConstraintConfig(this.daysOfWeek, this.dayOfMonth, Optional.of(Set.copyOf(months)), this.year);
	}
	
	/**
	 * Creates a new configuration with a year field constraint.<br>
	 *
	 * @param yearConfig The year field constraint configuration
	 * @return A new configuration with the year constraint applied
	 */
	public @NonNull DateFieldConstraintConfig withYear(@NonNull FieldConstraintConfig yearConfig) {
		return new DateFieldConstraintConfig(this.daysOfWeek, this.dayOfMonth, this.months, Optional.of(yearConfig));
	}
	
	/**
	 * Validates the date field constraints using the provided field provider.<br>
	 *
	 * @param provider The provider that supplies date field values
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the provider is null
	 */
	private @NonNull Result<Void> matches(@NonNull DateFieldProvider provider) {
		Objects.requireNonNull(provider, "Provider must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}
		
		if (this.daysOfWeek.isPresent()) {
			if (!this.daysOfWeek.get().contains(provider.dayOfWeek().get())) {
				return Result.error("Violated day of week constraint: value day of week (" + provider.dayOfWeek().get() + ") is not in allowed days (" + this.daysOfWeek.get() + ")");
			}
		}
		
		if (this.dayOfMonth.isPresent()) {
			Result<Void> dayOfMonthResult = this.dayOfMonth.get().matches("dayOfMonth", provider.dayOfMonth().getAsInt());
			if (dayOfMonthResult.isError()) {
				return dayOfMonthResult;
			}
		}
		
		if (this.months.isPresent()) {
			if (!this.months.get().contains(provider.month().get())) {
				return Result.error("Violated month constraint: value month (" + provider.month().get() + ") is not in allowed months (" + this.months.get() + ")");
			}
		}
		
		if (this.year.isPresent()) {
			Result<Void> yearResult = this.year.get().matches("year", provider.year().getAsInt());
			if (yearResult.isError()) {
				return yearResult;
			}
		}
		
		return Result.success();
	}
	
	/**
	 * Validates the date field constraints against the given {@link LocalDate} value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull LocalDate value) {
		Objects.requireNonNull(value, "Value must not be null");
		return this.matches(DateFieldProvider.create(value::getDayOfWeek, value::getDayOfMonth, value::getMonth, value::getYear));
	}
	
	/**
	 * Validates the date field constraints against the given {@link LocalDateTime} value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull LocalDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		return this.matches(DateFieldProvider.create(value::getDayOfWeek, value::getDayOfMonth, value::getMonth, value::getYear));
	}
	
	/**
	 * Validates the date field constraints against the given {@link LocalDateTime} value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull OffsetDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		return this.matches(DateFieldProvider.create(value::getDayOfWeek, value::getDayOfMonth, value::getMonth, value::getYear));
	}
	
	/**
	 * Validates the date field constraints against the given {@link ZonedDateTime} value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull ZonedDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		return this.matches(DateFieldProvider.create(value::getDayOfWeek, value::getDayOfMonth, value::getMonth, value::getYear));
	}
	
	/**
	 * Appends the constraint description to the provided list.<br>
	 *
	 * @param constraints The list to append constraint descriptions to
	 */
	public void appendConstraints(@NonNull List<String> constraints) {
		if (this.isUnconstrained()) {
			return;
		}
		
		this.daysOfWeek.ifPresent(days -> constraints.add("daysOfWeek=" + days));
		this.dayOfMonth.ifPresent(dom -> dom.appendConstraints("dayOfMonth", constraints));
		this.months.ifPresent(m -> constraints.add("months=" + m));
		this.year.ifPresent(y -> y.appendConstraints("year", constraints));
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "DateFieldConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.appendConstraints(constraints);
		return "DateFieldConstraintConfig[" + String.join(",", constraints) + "]";
	}
}
