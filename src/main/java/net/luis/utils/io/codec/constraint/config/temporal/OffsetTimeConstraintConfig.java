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

package net.luis.utils.io.codec.constraint.config.temporal;

import net.luis.utils.io.codec.constraint.config.temporal.core.*;
import net.luis.utils.io.codec.constraint.core.provider.TemporalConstraintConfigProvider;
import net.luis.utils.io.codec.constraint.core.provider.TimeFieldConstraintConfigProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.OffsetTime;
import java.util.*;

/**
 * Configuration class for OffsetTime constraints.<br>
 * <p>
 *     This class holds constraint configuration for {@link OffsetTime} values, including
 *     temporal comparison constraints, time field constraints, and duration-based span constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param config The base temporal constraint configuration
 * @param spanConfig The span constraint configuration (withinLast, withinNext)
 * @param timeFieldConfig The time field constraint configuration (hour, minute, second, millisecond)
 */
public record OffsetTimeConstraintConfig(
	@NonNull TemporalConstraintConfig<OffsetTime> config,
	@NonNull SpanConstraintConfig spanConfig,
	@NonNull TimeFieldConstraintConfig timeFieldConfig
) implements TemporalConstraintConfigProvider<OffsetTime, OffsetTimeConstraintConfig>, TimeFieldConstraintConfigProvider<OffsetTimeConstraintConfig> {
	
	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all {@link OffsetTime} values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final OffsetTimeConstraintConfig UNCONSTRAINED = new OffsetTimeConstraintConfig(
		TemporalConstraintConfig.unconstrained(), SpanConstraintConfig.UNCONSTRAINED, TimeFieldConstraintConfig.UNCONSTRAINED
	);
	
	/**
	 * Constructs a new OffsetTime constraint configuration with the specified constraints.<br>
	 *
	 * @param config The base temporal constraint configuration
	 * @param spanConfig The span constraint configuration (withinLast, withinNext)
	 * @param timeFieldConfig The time field constraint configuration (hour, minute, second, millisecond)
	 * @throws NullPointerException If any parameter is null
	 */
	public OffsetTimeConstraintConfig {
		Objects.requireNonNull(config, "Config must not be null");
		Objects.requireNonNull(spanConfig, "Span config must not be null");
		Objects.requireNonNull(timeFieldConfig, "Time field config must not be null");
	}
	
	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.config.isUnconstrained() && this.spanConfig.isUnconstrained() && this.timeFieldConfig.isUnconstrained());
	}
	
	@Override
	public @NonNull OffsetTimeConstraintConfig withEquals(@NonNull OffsetTime value, boolean negated) {
		return new OffsetTimeConstraintConfig(this.config.withEquals(value, negated), this.spanConfig, this.timeFieldConfig);
	}
	
	@Override
	public @NonNull OffsetTimeConstraintConfig withMin(@NonNull OffsetTime min, boolean inclusive) {
		return new OffsetTimeConstraintConfig(this.config.withMin(min, inclusive), this.spanConfig, this.timeFieldConfig);
	}
	
	@Override
	public @NonNull OffsetTimeConstraintConfig withMax(@NonNull OffsetTime max, boolean inclusive) {
		return new OffsetTimeConstraintConfig(this.config.withMax(max, inclusive), this.spanConfig, this.timeFieldConfig);
	}
	
	@Override
	public @NonNull OffsetTimeConstraintConfig withRange(@NonNull OffsetTime min, @NonNull OffsetTime max, boolean inclusive) {
		return new OffsetTimeConstraintConfig(this.config.withRange(min, max, inclusive), this.spanConfig, this.timeFieldConfig);
	}
	
	@Override
	public @NonNull OffsetTimeConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new OffsetTimeConstraintConfig(this.config, this.spanConfig.withWithinLast(duration), this.timeFieldConfig);
	}
	
	@Override
	public @NonNull OffsetTimeConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new OffsetTimeConstraintConfig(this.config, this.spanConfig.withWithinNext(duration), this.timeFieldConfig);
	}
	
	@Override
	public @NonNull OffsetTimeConstraintConfig withHour(@NonNull FieldConstraintConfig hourConfig) {
		return new OffsetTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withHour(hourConfig));
	}
	
	@Override
	public @NonNull OffsetTimeConstraintConfig withMinute(@NonNull FieldConstraintConfig minuteConfig) {
		return new OffsetTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withMinute(minuteConfig));
	}
	
	@Override
	public @NonNull OffsetTimeConstraintConfig withSecond(@NonNull FieldConstraintConfig secondConfig) {
		return new OffsetTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withSecond(secondConfig));
	}
	
	@Override
	public @NonNull OffsetTimeConstraintConfig withMillisecond(@NonNull FieldConstraintConfig millisecondConfig) {
		return new OffsetTimeConstraintConfig(this.config, this.spanConfig, this.timeFieldConfig.withMillisecond(millisecondConfig));
	}
	
	/**
	 * Validates the constraints against the given value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull OffsetTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}
		
		Result<Void> baseResult = this.config.matches(value);
		if (baseResult.isError()) {
			return baseResult;
		}
		
		Result<Void> spanResult = this.spanConfig.matches(value, OffsetTime::now);
		if (spanResult.isError()) {
			return spanResult;
		}
		
		Result<Void> timeFieldResult = this.timeFieldConfig.matches(value);
		if (timeFieldResult.isError()) {
			return timeFieldResult;
		}
		
		return Result.success();
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "OffsetTimeConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.config.appendConstraints(constraints);
		this.spanConfig.appendConstraints(constraints);
		this.timeFieldConfig.appendConstraints(constraints);
		return "OffsetTimeConstraintConfig[" + String.join(", ", constraints) + "]";
	}
}
