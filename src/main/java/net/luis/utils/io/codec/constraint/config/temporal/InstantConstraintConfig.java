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
import java.time.Instant;
import java.util.*;

/**
 * Configuration class for Instant constraints.<br>
 * <p>
 *     This class holds constraint configuration for {@link Instant} values, including
 *     temporal comparison constraints only (no span or field constraints).
 * </p>
 *
 * @author Luis-St
 *
 * @param config The base temporal constraint configuration
 */
public record InstantConstraintConfig(
	@NonNull TemporalConstraintConfig<Instant> config
) implements TemporalConstraintConfigProvider<Instant, InstantConstraintConfig> {
	
	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all {@link Instant} values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final InstantConstraintConfig UNCONSTRAINED = new InstantConstraintConfig(
		TemporalConstraintConfig.unconstrained()
	);
	
	/**
	 * Constructs a new Instant constraint configuration with the specified base configuration.<br>
	 *
	 * @param config The base temporal constraint configuration
	 * @throws NullPointerException If config is null
	 */
	public InstantConstraintConfig {
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
	public @NonNull InstantConstraintConfig withEquals(@NonNull Instant value, boolean negated) {
		return new InstantConstraintConfig(this.config.withEquals(value, negated));
	}
	
	@Override
	public @NonNull InstantConstraintConfig withMin(@NonNull Instant min, boolean inclusive) {
		return new InstantConstraintConfig(this.config.withMin(min, inclusive));
	}
	
	@Override
	public @NonNull InstantConstraintConfig withMax(@NonNull Instant max, boolean inclusive) {
		return new InstantConstraintConfig(this.config.withMax(max, inclusive));
	}
	
	@Override
	public @NonNull InstantConstraintConfig withRange(@NonNull Instant min, @NonNull Instant max, boolean inclusive) {
		return new InstantConstraintConfig(this.config.withRange(min, max, inclusive));
	}
	
	@Override
	public @NonNull InstantConstraintConfig withWithinLast(@NonNull Duration duration) {
		throw new UnsupportedOperationException("Instant does not support withinLast constraint");
	}
	
	@Override
	public @NonNull InstantConstraintConfig withWithinNext(@NonNull Duration duration) {
		throw new UnsupportedOperationException("Instant does not support withinNext constraint");
	}
	
	/**
	 * Validates the constraints against the given value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull Instant value) {
		return this.config.matches(value);
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "InstantConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.config.appendConstraints(constraints);
		return "InstantConstraintConfig[" + String.join(", ", constraints) + "]";
	}
}
