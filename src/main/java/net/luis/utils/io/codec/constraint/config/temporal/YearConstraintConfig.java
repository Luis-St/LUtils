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

import net.luis.utils.io.codec.constraint.config.temporal.core.TemporalConstraintConfig;
import net.luis.utils.io.codec.constraint.core.provider.TemporalConstraintConfigProvider;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.time.Year;
import java.util.*;

/**
 * Configuration class for Year constraints.<br>
 * <p>
 *     This class holds constraint configuration for {@link Year} values, including
 *     temporal comparison constraints only.
 * </p>
 *
 * @author Luis-St
 *
 * @param config The base temporal constraint configuration
 */
public record YearConstraintConfig(
	@NonNull TemporalConstraintConfig<Year> config
) implements TemporalConstraintConfigProvider<Year, YearConstraintConfig> {
	
	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all {@link Year} values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final YearConstraintConfig UNCONSTRAINED = new YearConstraintConfig(
		TemporalConstraintConfig.unconstrained()
	);
	
	/**
	 * Constructs a new Year constraint configuration with the specified base configuration.<br>
	 *
	 * @param config The base temporal constraint configuration
	 * @throws NullPointerException If config is null
	 */
	public YearConstraintConfig {
		Objects.requireNonNull(config, "Config must not be null");
	}
	
	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this.config.isUnconstrained();
	}
	
	@Override
	public @NonNull YearConstraintConfig withEquals(@NonNull Year value, boolean negated) {
		return new YearConstraintConfig(this.config.withEquals(value, negated));
	}
	
	@Override
	public @NonNull YearConstraintConfig withMin(@NonNull Year min, boolean inclusive) {
		return new YearConstraintConfig(this.config.withMin(min, inclusive));
	}
	
	@Override
	public @NonNull YearConstraintConfig withMax(@NonNull Year max, boolean inclusive) {
		return new YearConstraintConfig(this.config.withMax(max, inclusive));
	}
	
	@Override
	public @NonNull YearConstraintConfig withRange(@NonNull Year min, @NonNull Year max, boolean inclusive) {
		return new YearConstraintConfig(this.config.withRange(min, max, inclusive));
	}
	
	@Override
	public @NonNull YearConstraintConfig withWithinLast(@NonNull Duration duration) {
		throw new UnsupportedOperationException("Year does not support withinLast constraint");
	}
	
	@Override
	public @NonNull YearConstraintConfig withWithinNext(@NonNull Duration duration) {
		throw new UnsupportedOperationException("Year does not support withinNext constraint");
	}
	
	/**
	 * Validates the constraints against the given value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull Year value) {
		return this.config.matches(value);
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "YearConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.config.appendConstraints(constraints);
		return "YearConstraintConfig[" + String.join(", ", constraints) + "]";
	}
}
