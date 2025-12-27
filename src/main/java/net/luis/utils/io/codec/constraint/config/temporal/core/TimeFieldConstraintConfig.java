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

package net.luis.utils.io.codec.constraint.config.temporal.core;

import net.luis.utils.io.codec.constraint.config.temporal.provider.TimeFieldProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.*;

/**
 * Configuration class for time field constraints (hour, minute, second, millisecond).<br>
 * This class holds time field constraint fields that are common to temporal types with time components.<br>
 *
 * @author Luis-St
 *
 * @param hour The hour constraint (empty if unconstrained)
 * @param minute The minute constraint (empty if unconstrained)
 * @param second The second constraint (empty if unconstrained)
 * @param millisecond The millisecond constraint (empty if unconstrained)
 */
public record TimeFieldConstraintConfig(
	@NonNull Optional<FieldConstraintConfig> hour,
	@NonNull Optional<FieldConstraintConfig> minute,
	@NonNull Optional<FieldConstraintConfig> second,
	@NonNull Optional<FieldConstraintConfig> millisecond
) {

	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all time field values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final TimeFieldConstraintConfig UNCONSTRAINED = new TimeFieldConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);

	/**
	 * Constructs a new time field constraint configuration with the specified constraints.<br>
	 *
	 * @param hour The hour constraint (empty if unconstrained)
	 * @param minute The minute constraint (empty if unconstrained)
	 * @param second The second constraint (empty if unconstrained)
	 * @param millisecond The millisecond constraint (empty if unconstrained)
	 * @throws NullPointerException If any parameter is null
	 */
	public TimeFieldConstraintConfig {
		Objects.requireNonNull(hour, "Hour constraint must not be null");
		Objects.requireNonNull(minute, "Minute constraint must not be null");
		Objects.requireNonNull(second, "Second constraint must not be null");
		Objects.requireNonNull(millisecond, "Millisecond constraint must not be null");
	}

	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || ((this.hour.isEmpty() || this.hour.get().isUnconstrained()) && (this.minute.isEmpty() || this.minute.get().isUnconstrained()) && (this.second.isEmpty() || this.second.get().isUnconstrained()) && (this.millisecond.isEmpty() || this.millisecond.get().isUnconstrained()));
	}

	/**
	 * Creates a new configuration with an hour field constraint.<br>
	 *
	 * @param hourConfig The hour field constraint configuration
	 * @return A new configuration with the hour constraint applied
	 */
	public @NonNull TimeFieldConstraintConfig withHour(@NonNull FieldConstraintConfig hourConfig) {
		return new TimeFieldConstraintConfig(Optional.of(hourConfig), this.minute, this.second, this.millisecond);
	}

	/**
	 * Creates a new configuration with a minute field constraint.<br>
	 *
	 * @param minuteConfig The minute field constraint configuration
	 * @return A new configuration with the minute constraint applied
	 */
	public @NonNull TimeFieldConstraintConfig withMinute(@NonNull FieldConstraintConfig minuteConfig) {
		return new TimeFieldConstraintConfig(this.hour, Optional.of(minuteConfig), this.second, this.millisecond);
	}

	/**
	 * Creates a new configuration with a second field constraint.<br>
	 *
	 * @param secondConfig The second field constraint configuration
	 * @return A new configuration with the second constraint applied
	 */
	public @NonNull TimeFieldConstraintConfig withSecond(@NonNull FieldConstraintConfig secondConfig) {
		return new TimeFieldConstraintConfig(this.hour, this.minute, Optional.of(secondConfig), this.millisecond);
	}

	/**
	 * Creates a new configuration with a millisecond field constraint.<br>
	 *
	 * @param millisecondConfig The millisecond field constraint configuration
	 * @return A new configuration with the millisecond constraint applied
	 */
	public @NonNull TimeFieldConstraintConfig withMillisecond(@NonNull FieldConstraintConfig millisecondConfig) {
		return new TimeFieldConstraintConfig(this.hour, this.minute, this.second, Optional.of(millisecondConfig));
	}

	/**
	 * Validates the time field constraints using the provided field provider.<br>
	 *
	 * @param provider The provider that supplies time field values
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the provider is null
	 */
	private @NonNull Result<Void> matches(@NonNull TimeFieldProvider provider) {
		Objects.requireNonNull(provider, "Provider must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}

		if (this.hour.isPresent()) {
			Result<Void> hourResult = this.hour.get().matches("hour", provider.hour().getAsInt());
			if (hourResult.isError()) return hourResult;
		}

		if (this.minute.isPresent()) {
			Result<Void> minuteResult = this.minute.get().matches("minute", provider.minute().getAsInt());
			if (minuteResult.isError()) return minuteResult;
		}

		if (this.second.isPresent()) {
			Result<Void> secondResult = this.second.get().matches("second", provider.second().getAsInt());
			if (secondResult.isError()) return secondResult;
		}

		if (this.millisecond.isPresent()) {
			Result<Void> millisecondResult = this.millisecond.get().matches("millisecond", provider.millisecond().getAsInt());
			if (millisecondResult.isError()) return millisecondResult;
		}

		return Result.success();
	}

	/**
	 * Validates the time field constraints against the given {@link LocalTime} value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull LocalTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		return this.matches(TimeFieldProvider.create(value::getHour, value::getMinute, value::getSecond, () -> value.getNano() / 1_000_000));
	}

	/**
	 * Validates the time field constraints against the given {@link LocalDateTime} value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull LocalDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		return this.matches(TimeFieldProvider.create(value::getHour, value::getMinute, value::getSecond, () -> value.getNano() / 1_000_000));
	}

	/**
	 * Validates the time field constraints against the given {@link OffsetTime} value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull OffsetTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		return this.matches(TimeFieldProvider.create(value::getHour, value::getMinute, value::getSecond, () -> value.getNano() / 1_000_000));
	}

	/**
	 * Validates the time field constraints against the given {@link OffsetDateTime} value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull OffsetDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		return this.matches(TimeFieldProvider.create(value::getHour, value::getMinute, value::getSecond, () -> value.getNano() / 1_000_000));
	}

	/**
	 * Validates the time field constraints against the given {@link ZonedDateTime} value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull ZonedDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		return this.matches(TimeFieldProvider.create(value::getHour, value::getMinute, value::getSecond, () -> value.getNano() / 1_000_000));
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

		this.hour.ifPresent(h -> h.appendConstraints("hour", constraints));
		this.minute.ifPresent(m -> m.appendConstraints("minute", constraints));
		this.second.ifPresent(s -> s.appendConstraints("second", constraints));
		this.millisecond.ifPresent(ms -> ms.appendConstraints("millisecond", constraints));
	}

	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "TimeFieldConstraintConfig[unconstrained]";
		}

		List<String> constraints = new ArrayList<>();
		this.appendConstraints(constraints);
		return "TimeFieldConstraintConfig[" + String.join(",", constraints) + "]";
	}
}
