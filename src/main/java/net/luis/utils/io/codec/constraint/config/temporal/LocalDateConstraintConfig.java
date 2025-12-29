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
import net.luis.utils.io.codec.constraint.core.provider.DateFieldConstraintConfigProvider;
import net.luis.utils.io.codec.constraint.core.provider.TemporalConstraintConfigProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.*;

/**
 * Configuration class for LocalDate constraints.<br>
 * <p>
 *     This class holds constraint configuration for {@link LocalDate} values, including
 *     temporal comparison constraints, date field constraints, and duration-based span constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param config The base temporal constraint configuration
 * @param spanConfig The span constraint configuration (withinLast and withinNext)
 * @param dateFieldConfig The date field constraint configuration
 */
public record LocalDateConstraintConfig(
	@NonNull TemporalConstraintConfig<LocalDate> config,
	@NonNull SpanConstraintConfig spanConfig,
	@NonNull DateFieldConstraintConfig dateFieldConfig
) implements TemporalConstraintConfigProvider<LocalDate, LocalDateConstraintConfig>, DateFieldConstraintConfigProvider<LocalDateConstraintConfig> {
	
	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all {@link LocalDate} values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final LocalDateConstraintConfig UNCONSTRAINED = new LocalDateConstraintConfig(
		TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, DateFieldConstraintConfig.UNCONSTRAINED
	);
	
	/**
	 * Constructs a new LocalDate constraint configuration with the specified constraints.<br>
	 *
	 * @param config The base temporal constraint configuration
	 * @param spanConfig The span constraint configuration (withinLast and withinNext)
	 * @param dateFieldConfig The date field constraint configuration
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If constraints are invalid
	 */
	public LocalDateConstraintConfig {
		Objects.requireNonNull(config, "Config must not be null");
		Objects.requireNonNull(spanConfig, "Span config must not be null");
		Objects.requireNonNull(dateFieldConfig, "Date field config must not be null");
	}
	
	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.config.isUnconstrained() && this.spanConfig.isUnconstrained() && this.dateFieldConfig.isUnconstrained());
	}
	
	@Override
	public @NonNull LocalDateConstraintConfig withEquals(@NonNull LocalDate value, boolean negated) {
		return new LocalDateConstraintConfig(this.config.withEquals(value, negated), this.spanConfig, this.dateFieldConfig);
	}
	
	@Override
	public @NonNull LocalDateConstraintConfig withMin(@NonNull LocalDate min, boolean inclusive) {
		return new LocalDateConstraintConfig(this.config.withMin(min, inclusive), this.spanConfig, this.dateFieldConfig);
	}
	
	@Override
	public @NonNull LocalDateConstraintConfig withMax(@NonNull LocalDate max, boolean inclusive) {
		return new LocalDateConstraintConfig(this.config.withMax(max, inclusive), this.spanConfig, this.dateFieldConfig);
	}
	
	@Override
	public @NonNull LocalDateConstraintConfig withRange(@NonNull LocalDate min, @NonNull LocalDate max, boolean inclusive) {
		return new LocalDateConstraintConfig(this.config.withRange(min, max, inclusive), this.spanConfig, this.dateFieldConfig);
	}
	
	@Override
	public @NonNull LocalDateConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new LocalDateConstraintConfig(this.config, this.spanConfig.withWithinLast(duration), this.dateFieldConfig);
	}
	
	@Override
	public @NonNull LocalDateConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new LocalDateConstraintConfig(this.config, this.spanConfig.withWithinNext(duration), this.dateFieldConfig);
	}
	
	@Override
	public @NonNull LocalDateConstraintConfig withDayOfWeek(@NonNull Set<DayOfWeek> daysOfWeek) {
		return new LocalDateConstraintConfig(this.config, this.spanConfig, this.dateFieldConfig.withDayOfWeek(daysOfWeek));
	}
	
	@Override
	public @NonNull LocalDateConstraintConfig withDayOfMonth(@NonNull FieldConstraintConfig monthConfig) {
		return new LocalDateConstraintConfig(this.config, this.spanConfig, this.dateFieldConfig.withDayOfMonth(monthConfig));
	}
	
	@Override
	public @NonNull LocalDateConstraintConfig withMonth(@NonNull Set<Month> months) {
		return new LocalDateConstraintConfig(this.config, this.spanConfig, this.dateFieldConfig.withMonth(months));
	}
	
	@Override
	public @NonNull LocalDateConstraintConfig withYear(@NonNull FieldConstraintConfig yearConfig) {
		return new LocalDateConstraintConfig(this.config, this.spanConfig, this.dateFieldConfig.withYear(yearConfig));
	}
	
	/**
	 * Validates the constraints against the given value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull LocalDate value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}
		
		Result<Void> baseResult = this.config.matches(value);
		if (baseResult.isError()) {
			return baseResult;
		}
		
		Result<Void> spanResult = this.spanConfig.matches(value, LocalDate::now);
		if (spanResult.isError()) {
			return spanResult;
		}
		
		return this.dateFieldConfig.matches(value);
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "LocalDateConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.config.appendConstraints(constraints);
		this.spanConfig.appendConstraints(constraints);
		this.dateFieldConfig.appendConstraints(constraints);
		return "LocalDateConstraintConfig[" + String.join(", ", constraints) + "]";
	}
}
