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

package net.luis.utils.io.codec.constraint.config.temporal;

import net.luis.utils.io.codec.constraint.config.temporal.core.*;
import net.luis.utils.io.codec.constraint.core.provider.*;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.*;

/**
 * Configuration class for OffsetDateTime constraints.<br>
 * <p>
 *     This class holds constraint configuration for {@link OffsetDateTime} values, including
 *     temporal comparison constraints, time field constraints, date field constraints,
 *     and duration-based span constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param config The base temporal constraint configuration
 * @param spanConfig The span constraint configuration (withinLast and withinNext)
 * @param timeFieldConfig The time field constraint configuration (hour, minute, second, millisecond)
 * @param dateFieldConfig The date field constraint configuration (daysOfWeek, dayOfMonth, months, year)
 */
public record OffsetDateTimeConstraintConfig(
	@NonNull TemporalConstraintConfig<OffsetDateTime> config,
	@NonNull SpanConstraintConfig spanConfig,
	@NonNull TimeFieldConstraintConfig timeFieldConfig,
	@NonNull DateFieldConstraintConfig dateFieldConfig
) implements TemporalConstraintConfigProvider<OffsetDateTime, OffsetDateTimeConstraintConfig>,
	TimeFieldConstraintConfigProvider<OffsetDateTimeConstraintConfig>,
	DateFieldConstraintConfigProvider<OffsetDateTimeConstraintConfig> {

	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all {@link OffsetDateTime} values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final OffsetDateTimeConstraintConfig UNCONSTRAINED = new OffsetDateTimeConstraintConfig(
		TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED, DateFieldConstraintConfig.UNCONSTRAINED
	);

	/**
	 * Constructs a new OffsetDateTime constraint configuration with the specified constraints.<br>
	 *
	 * @param config The base temporal constraint configuration
	 * @param spanConfig The span constraint configuration (withinLast and withinNext)
	 * @param timeFieldConfig The time field constraint configuration (hour, minute, second, millisecond)
	 * @param dateFieldConfig The date field constraint configuration (daysOfWeek, dayOfMonth, months, year)
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If constraints are invalid
	 */
	public OffsetDateTimeConstraintConfig {
		Objects.requireNonNull(config, "Config must not be null");
		Objects.requireNonNull(spanConfig, "Span config must not be null");
		Objects.requireNonNull(timeFieldConfig, "Time field config must not be null");
		Objects.requireNonNull(dateFieldConfig, "Date field config must not be null");
	}

	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.config.isUnconstrained() && this.spanConfig.isUnconstrained() && this.timeFieldConfig.isUnconstrained() && this.dateFieldConfig.isUnconstrained());
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withEquals(@NonNull OffsetDateTime value, boolean negated) {
		return new OffsetDateTimeConstraintConfig(this.config.withEquals(value, negated), this.spanConfig, this.timeFieldConfig, this.dateFieldConfig);
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withMin(@NonNull OffsetDateTime min, boolean inclusive) {
		return new OffsetDateTimeConstraintConfig(this.config.withMin(min, inclusive), this.spanConfig, this.timeFieldConfig, this.dateFieldConfig);
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withMax(@NonNull OffsetDateTime max, boolean inclusive) {
		return new OffsetDateTimeConstraintConfig(this.config.withMax(max, inclusive), this.spanConfig, this.timeFieldConfig, this.dateFieldConfig);
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withRange(@NonNull OffsetDateTime min, @NonNull OffsetDateTime max, boolean inclusive) {
		return new OffsetDateTimeConstraintConfig(this.config.withRange(min, max, inclusive), this.spanConfig, this.timeFieldConfig, this.dateFieldConfig);
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new OffsetDateTimeConstraintConfig(this.config, this.spanConfig.withWithinLast(duration), this.timeFieldConfig, this.dateFieldConfig);
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new OffsetDateTimeConstraintConfig(this.config, this.spanConfig.withWithinNext(duration), this.timeFieldConfig, this.dateFieldConfig);
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withHour(@NonNull FieldConstraintConfig hourConfig) {
		return new OffsetDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withHour(hourConfig), this.dateFieldConfig);
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withMinute(@NonNull FieldConstraintConfig minuteConfig) {
		return new OffsetDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withMinute(minuteConfig), this.dateFieldConfig);
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withSecond(@NonNull FieldConstraintConfig secondConfig) {
		return new OffsetDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withSecond(secondConfig), this.dateFieldConfig);
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withMillisecond(@NonNull FieldConstraintConfig millisecondConfig) {
		return new OffsetDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withMillisecond(millisecondConfig), this.dateFieldConfig);
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withDayOfWeek(@NonNull Set<DayOfWeek> daysOfWeek) {
		return new OffsetDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig, this.dateFieldConfig.withDayOfWeek(daysOfWeek));
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withDayOfMonth(@NonNull FieldConstraintConfig monthConfig) {
		return new OffsetDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig, this.dateFieldConfig.withDayOfMonth(monthConfig));
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withMonth(@NonNull Set<Month> months) {
		return new OffsetDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig, this.dateFieldConfig.withMonth(months));
	}

	@Override
	public @NonNull OffsetDateTimeConstraintConfig withYear(@NonNull FieldConstraintConfig yearConfig) {
		return new OffsetDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig, this.dateFieldConfig.withYear(yearConfig));
	}

	/**
	 * Validates the constraints against the given value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull OffsetDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}

		Result<Void> baseResult = this.config.matches(value);
		if (baseResult.isError()) {
			return baseResult;
		}

		Result<Void> spanResult = this.spanConfig.matches(value);
		if (spanResult.isError()) {
			return spanResult;
		}

		Result<Void> timeFieldResult = this.timeFieldConfig.matches(value);
		if (timeFieldResult.isError()) {
			return timeFieldResult;
		}

		Result<Void> dateFieldResult = this.dateFieldConfig.matches(value);
		if (dateFieldResult.isError()) {
			return dateFieldResult;
		}

		return Result.success();
	}

	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "OffsetDateTimeConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.config.appendConstraints(constraints);
		this.spanConfig.appendConstraints(constraints);
		this.timeFieldConfig.appendConstraints(constraints);
		this.dateFieldConfig.appendConstraints(constraints);
		return "OffsetDateTimeConstraintConfig[" + String.join(", ", constraints) + "]";
	}
}
