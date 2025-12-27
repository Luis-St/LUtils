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

import net.luis.utils.io.codec.constraint.core.provider.TemporalConstraintConfigProvider;
import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.*;

/**
 * Configuration class for Year constraints.<br>
 * <p>
 *     This class holds constraint configuration for {@link Year} values, including
 *     temporal comparison constraints only.
 * </p>
 *
 * @author Luis-St
 */
public record YearConstraintConfig(
	@NonNull Optional<Pair<Year, Boolean>> min,
	@NonNull Optional<Pair<Year, Boolean>> max,
	@NonNull Optional<Pair<Year, Boolean>> equals
) implements TemporalConstraintConfigProvider<Year, YearConstraintConfig> {
	
	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all {@link Year} values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final YearConstraintConfig UNCONSTRAINED = new YearConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty()
	);

	/**
	 * Constructs a new Year constraint configuration with the specified minimum, maximum, and equals constraints.<br>
	 *
	 * @param min The minimum value constraint (empty if unconstrained)
	 * @param max The maximum value constraint (empty if unconstrained)
	 * @param equals The exact value constraint (empty if unconstrained)
	 * @throws NullPointerException If min, max, or equals is null
	 * @throws IllegalArgumentException If min is greater than max
	 */
	public YearConstraintConfig {
		Objects.requireNonNull(min, "Min constraint must not be null");
		Objects.requireNonNull(max, "Max constraint must not be null");
		Objects.requireNonNull(equals, "Equals constraint must not be null");

		if (min.isPresent() && max.isPresent()) {
			Pair<Year, Boolean> minPair = min.get();
			Pair<Year, Boolean> maxPair = max.get();

			int comparison = minPair.getFirst().compareTo(maxPair.getFirst());
			if (comparison > 0 || (comparison == 0 && (!minPair.getSecond() || !maxPair.getSecond()))) {
				throw new IllegalArgumentException("Minimum value must not be greater than maximum value: min=" + minPair + ", max=" + maxPair);
			}
		}
	}

	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.min.isEmpty() && this.max.isEmpty() && this.equals.isEmpty());
	}

	@Override
	public @NonNull YearConstraintConfig withEquals(@NonNull Year value, boolean negated) {
		return new YearConstraintConfig(this.min, this.max, Optional.of(Pair.of(value, negated)));
	}

	@Override
	public @NonNull YearConstraintConfig withMin(@NonNull Year min, boolean inclusive) {
		return new YearConstraintConfig(Optional.of(Pair.of(min, inclusive)), this.max, this.equals);
	}

	@Override
	public @NonNull YearConstraintConfig withMax(@NonNull Year max, boolean inclusive) {
		return new YearConstraintConfig(this.min, Optional.of(Pair.of(max, inclusive)), this.equals);
	}

	@Override
	public @NonNull YearConstraintConfig withRange(@NonNull Year min, @NonNull Year max, boolean inclusive) {
		return new YearConstraintConfig(Optional.of(Pair.of(min, inclusive)), Optional.of(Pair.of(max, inclusive)), this.equals);
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
		Objects.requireNonNull(value, "Value must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}

		if (this.equals.isPresent()) {
			Pair<Year, Boolean> pair = this.equals.get();
			if (pair.getSecond()) {
				if (!value.equals(pair.getFirst())) {
					return Result.success();
				}
				return Result.error("Violated equals constraint: value (" + value + ") is equal to expected (" + pair.getFirst() + "), but it should not be");
			} else {
				if (value.equals(pair.getFirst())) {
					return Result.success();
				}
				return Result.error("Violated equals constraint: value (" + value + ") is not equal to expected (" + pair.getFirst() + "), but it should be");
			}
		}

		if (this.min.isPresent()) {
			Pair<Year, Boolean> pair = this.min.get();
			int comparison = value.compareTo(pair.getFirst());
			if (pair.getSecond()) {
				if (comparison < 0) {
					return Result.error("Violated minimum constraint: value (" + value + ") is before min (" + pair.getFirst() + "), but it should be at least min");
				}
			} else {
				if (comparison <= 0) {
					return Result.error("Violated minimum constraint (exclusive): value (" + value + ") is before or equal to min (" + pair.getFirst() + "), but it should be after min");
				}
			}
		}

		if (this.max.isPresent()) {
			Pair<Year, Boolean> pair = this.max.get();
			int comparison = value.compareTo(pair.getFirst());
			if (pair.getSecond()) {
				if (comparison > 0) {
					return Result.error("Violated maximum constraint: value (" + value + ") is after max (" + pair.getFirst() + "), but it should be at most max");
				}
			} else {
				if (comparison >= 0) {
					return Result.error("Violated maximum constraint (exclusive): value (" + value + ") is after or equal to max (" + pair.getFirst() + "), but it should be before max");
				}
			}
		}

		return Result.success();
	}

	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "YearConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.min.ifPresent(pair -> constraints.add("min=" + pair.getFirst()));
		this.max.ifPresent(pair -> constraints.add("max=" + pair.getFirst()));
		this.equals.ifPresent(pair -> constraints.add("equals=" + pair.getFirst()));
		return "YearConstraintConfig[" + String.join(", ", constraints) + "]";
	}
}
