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

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Base configuration class for temporal constraints.<br>
 * This class holds temporal constraint fields that are common to all temporal types.<br>
 *
 * @author Luis-St
 *
 * @param min The minimum value constraint (empty if unconstrained)
 * @param max The maximum value constraint (empty if unconstrained)
 * @param equals The exact value constraint (empty if unconstrained)
 * @param <T> The temporal type being constrained
 */
public record TemporalConstraintConfig<T extends Comparable<? super T>>(
	@NonNull Optional<Pair<T, /*Inclusive*/ Boolean>> min,
	@NonNull Optional<Pair<T, /*Inclusive*/ Boolean>> max,
	@NonNull Optional<Pair<T, /*Negated*/ Boolean>> equals
) {

	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all temporal values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 *
	 * @see #unconstrained()
	 */
	private static final TemporalConstraintConfig<?> UNCONSTRAINED = new TemporalConstraintConfig<>(
		Optional.empty(), Optional.empty(), Optional.empty()
	);

	/**
	 * Constructs a new temporal constraint configuration with the specified minimum, maximum, and equals constraints.<br>
	 *
	 * @param min The minimum value constraint (empty if unconstrained)
	 * @param max The maximum value constraint (empty if unconstrained)
	 * @param equals The exact value constraint (empty if unconstrained)
	 * @throws NullPointerException If min, max, or equals is null
	 * @throws IllegalArgumentException If min is greater than max
	 */
	public TemporalConstraintConfig {
		Objects.requireNonNull(min, "Min constraint must not be null");
		Objects.requireNonNull(max, "Max constraint must not be null");
		Objects.requireNonNull(equals, "Equals constraint must not be null");

		if (min.isPresent() && max.isPresent()) {
			Pair<T, Boolean> minPair = min.get();
			Pair<T, Boolean> maxPair = max.get();

			int comparison = minPair.getFirst().compareTo(maxPair.getFirst());
			if (comparison > 0 || (comparison == 0 && (!minPair.getSecond() || !maxPair.getSecond()))) {
				throw new IllegalArgumentException("Minimum value must not be greater than maximum value: min=" + minPair + ", max=" + maxPair);
			}
		}
	}

	/**
	 * Returns an unconstrained configuration for the specified type.<br>
	 * This method provides a type-safe way to get an unconstrained configuration without raw type warnings.
	 *
	 * @return An unconstrained temporal constraint configuration
	 * @param <T> The temporal type
	 */
	@SuppressWarnings("unchecked")
	public static @NonNull <T extends Comparable<? super T>> TemporalConstraintConfig<T> unconstrained() {
		return (TemporalConstraintConfig<T>) UNCONSTRAINED;
	}

	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.min.isEmpty() && this.max.isEmpty() && this.equals.isEmpty());
	}

	/**
	 * Creates a new configuration with a minimum value constraint.<br>
	 *
	 * @param min The minimum value
	 * @param inclusive True for inclusive (>=), false for exclusive (>)
	 * @return A new configuration with the minimum constraint applied
	 */
	public @NonNull TemporalConstraintConfig<T> withMin(@NonNull T min, boolean inclusive) {
		return new TemporalConstraintConfig<>(Optional.of(Pair.of(min, inclusive)), this.max, this.equals);
	}

	/**
	 * Creates a new configuration with a maximum value constraint.<br>
	 *
	 * @param max The maximum value
	 * @param inclusive True for inclusive (<=), false for exclusive (<)
	 * @return A new configuration with the maximum constraint applied
	 */
	public @NonNull TemporalConstraintConfig<T> withMax(@NonNull T max, boolean inclusive) {
		return new TemporalConstraintConfig<>(this.min, Optional.of(Pair.of(max, inclusive)), this.equals);
	}

	/**
	 * Creates a new configuration with a range constraint.<br>
	 *
	 * @param min The minimum value
	 * @param max The maximum value
	 * @param inclusive True for inclusive bounds, false for exclusive
	 * @return A new configuration with the range constraint applied
	 */
	public @NonNull TemporalConstraintConfig<T> withRange(@NonNull T min, @NonNull T max, boolean inclusive) {
		return new TemporalConstraintConfig<>(Optional.of(Pair.of(min, inclusive)), Optional.of(Pair.of(max, inclusive)), this.equals);
	}

	/**
	 * Creates a new configuration with an equality constraint.<br>
	 *
	 * @param equals The value to match or exclude
	 * @param negated True to exclude (!=), false to require (==)
	 * @return A new configuration with the equality constraint applied
	 */
	public @NonNull TemporalConstraintConfig<T> withEquals(@NonNull T equals, boolean negated) {
		return new TemporalConstraintConfig<>(this.min, this.max, Optional.of(Pair.of(equals, negated)));
	}

	/**
	 * Validates the constraints against the given value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull T value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}

		if (this.equals.isPresent()) {
			Pair<T, Boolean> pair = this.equals.get();
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
			Pair<T, Boolean> pair = this.min.get();

			int comparison = value.compareTo(pair.getFirst());
			if (pair.getSecond()) {
				if (comparison < 0) {
					return Result.error("Violated minimum constraint: value (" + value + ") is before min (" + this.min.get().getFirst() + "), but it should be at least min");
				}
			} else {
				if (comparison <= 0) {
					return Result.error("Violated minimum constraint (exclusive): value (" + value + ") is before or equal to min (" + this.min.get().getFirst() + "), but it should be after min");
				}
			}
		}

		if (this.max.isPresent()) {
			Pair<T, Boolean> pair = this.max.get();

			int comparison = value.compareTo(pair.getFirst());
			if (pair.getSecond()) {
				if (comparison > 0) {
					return Result.error("Violated maximum constraint: value (" + value + ") is after max (" + this.max.get().getFirst() + "), but it should be at most max");
				}
			} else {
				if (comparison >= 0) {
					return Result.error("Violated maximum constraint (exclusive): value (" + value + ") is after or equal to max (" + this.max.get().getFirst() + "), but it should be before max");
				}
			}
		}

		return Result.success();
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

		this.min.ifPresent(pair -> constraints.add("min=" + pair.getFirst() + (pair.getSecond() ? " (inclusive)" : " (exclusive)")));
		this.max.ifPresent(pair -> constraints.add("max=" + pair.getFirst() + (pair.getSecond() ? " (inclusive)" : " (exclusive)")));
		this.equals.ifPresent(pair -> constraints.add("equals=" + pair.getFirst() + (pair.getSecond() ? " (negated)" : "")));
	}

	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "TemporalConstraintConfig[unconstrained]";
		}

		List<String> constraints = new ArrayList<>();
		this.appendConstraints(constraints);
		return "TemporalConstraintConfig[" + String.join(",", constraints) + "]";
	}
}
