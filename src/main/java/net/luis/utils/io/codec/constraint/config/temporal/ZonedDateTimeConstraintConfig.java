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
 * Configuration class for ZonedDateTime constraints.<br>
 * <p>
 *     This class holds constraint configuration for {@link ZonedDateTime} values, including
 *     temporal comparison constraints, time field constraints, date field constraints,
 *     and duration-based span constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param config The base temporal constraint configuration
 * @param spanConfig The span constraint configuration (withinLast, withinNext)
 * @param timeFieldConfig The time field constraint configuration (hour, minute, second, millisecond)
 * @param dateFieldConfig The date field constraint configuration (daysOfWeek, dayOfMonth, months, year)
 */
public record ZonedDateTimeConstraintConfig(
	@NonNull TemporalConstraintConfig<ZonedDateTime> config,
	@NonNull SpanConstraintConfig spanConfig,
	@NonNull TimeFieldConstraintConfig timeFieldConfig,
	@NonNull DateFieldConstraintConfig dateFieldConfig
) implements TemporalConstraintConfigProvider<ZonedDateTime, ZonedDateTimeConstraintConfig>, TimeFieldConstraintConfigProvider<ZonedDateTimeConstraintConfig>, DateFieldConstraintConfigProvider<ZonedDateTimeConstraintConfig> {
	
	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all {@link ZonedDateTime} values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final ZonedDateTimeConstraintConfig UNCONSTRAINED = new ZonedDateTimeConstraintConfig(
		TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED, DateFieldConstraintConfig.UNCONSTRAINED
	);
	
	/**
	 * Constructs a new ZonedDateTime constraint configuration with the specified constraints.<br>
	 *
	 * @param config The base temporal constraint configuration
	 * @param spanConfig The span constraint configuration (withinLast, withinNext)
	 * @param timeFieldConfig The time field constraint configuration (hour, minute, second, millisecond)
	 * @param dateFieldConfig The date field constraint configuration (daysOfWeek, dayOfMonth, months, year)
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If constraints are invalid
	 */
	public ZonedDateTimeConstraintConfig {
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
	public @NonNull ZonedDateTimeConstraintConfig withEquals(@NonNull ZonedDateTime value, boolean negated) {
		return new ZonedDateTimeConstraintConfig(this.config.withEquals(value, negated), this.spanConfig, this.timeFieldConfig, this.dateFieldConfig);
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withMin(@NonNull ZonedDateTime min, boolean inclusive) {
		return new ZonedDateTimeConstraintConfig(this.config.withMin(min, inclusive), this.spanConfig, this.timeFieldConfig, this.dateFieldConfig);
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withMax(@NonNull ZonedDateTime max, boolean inclusive) {
		return new ZonedDateTimeConstraintConfig(this.config.withMax(max, inclusive), this.spanConfig, this.timeFieldConfig, this.dateFieldConfig);
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withRange(@NonNull ZonedDateTime min, @NonNull ZonedDateTime max, boolean inclusive) {
		return new ZonedDateTimeConstraintConfig(this.config.withRange(min, max, inclusive), this.spanConfig, this.timeFieldConfig, this.dateFieldConfig);
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new ZonedDateTimeConstraintConfig(this.config, this.spanConfig.withWithinLast(duration), this.timeFieldConfig, this.dateFieldConfig);
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new ZonedDateTimeConstraintConfig(this.config, this.spanConfig.withWithinNext(duration), this.timeFieldConfig, this.dateFieldConfig);
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withHour(@NonNull FieldConstraintConfig hourConfig) {
		return new ZonedDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withHour(hourConfig), this.dateFieldConfig);
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withMinute(@NonNull FieldConstraintConfig minuteConfig) {
		return new ZonedDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withMinute(minuteConfig), this.dateFieldConfig);
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withSecond(@NonNull FieldConstraintConfig secondConfig) {
		return new ZonedDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withSecond(secondConfig), this.dateFieldConfig);
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withMillisecond(@NonNull FieldConstraintConfig millisecondConfig) {
		return new ZonedDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withMillisecond(millisecondConfig), this.dateFieldConfig);
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withDayOfWeek(@NonNull Set<DayOfWeek> daysOfWeek) {
		return new ZonedDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig, this.dateFieldConfig.withDayOfWeek(daysOfWeek));
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withDayOfMonth(@NonNull FieldConstraintConfig monthConfig) {
		return new ZonedDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig, this.dateFieldConfig.withDayOfMonth(monthConfig));
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withMonth(@NonNull Set<Month> months) {
		return new ZonedDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig, this.dateFieldConfig.withMonth(months));
	}
	
	@Override
	public @NonNull ZonedDateTimeConstraintConfig withYear(@NonNull FieldConstraintConfig yearConfig) {
		return new ZonedDateTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig, this.dateFieldConfig.withYear(yearConfig));
	}
	
	/**
	 * Validates the constraints against the given value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull ZonedDateTime value) {
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
		
		return this.dateFieldConfig.matches(value);
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "ZonedDateTimeConstraintConfig[unconstrained]";
		}
		List<String> constraints = new ArrayList<>();
		this.config.appendConstraints(constraints);
		this.spanConfig.appendConstraints(constraints);
		this.timeFieldConfig.appendConstraints(constraints);
		this.dateFieldConfig.appendConstraints(constraints);
		return "ZonedDateTimeConstraintConfig[" + String.join(", ", constraints) + "]";
	}
}
