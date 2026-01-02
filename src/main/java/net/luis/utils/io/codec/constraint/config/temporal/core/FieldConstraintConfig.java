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

import net.luis.utils.util.Pair;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * Configuration class for field constraints on temporal components.<br>
 * <p>
 *     This class holds constraint configuration for individual temporal fields
 *     such as hour, minute, second, millisecond, day of month, and year.<br>
 *     Constraints are applied as numeric comparisons on the field values.
 * </p>
 *
 * @author Luis-St
 *
 * @param min The minimum value constraint (empty if unconstrained)
 * @param max The maximum value constraint (empty if unconstrained)
 * @param equals The exact value constraint (empty if unconstrained)
 */
public record FieldConstraintConfig(
	@NonNull Optional<Pair<Integer, /*Inclusive*/ Boolean>> min,
	@NonNull Optional<Pair<Integer, /*Inclusive*/ Boolean>> max,
	@NonNull Optional<Pair<Integer, /*Negated*/ Boolean>> equals
) {
	
	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all field values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final FieldConstraintConfig UNCONSTRAINED = new FieldConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new field constraint configuration with the specified minimum, maximum, and equals constraints.<br>
	 *
	 * @param min The minimum value constraint (empty if unconstrained)
	 * @param max The maximum value constraint (empty if unconstrained)
	 * @param equals The exact value constraint (empty if unconstrained)
	 * @throws NullPointerException If min, max, or equals is null
	 * @throws IllegalArgumentException If min is greater than max
	 */
	public FieldConstraintConfig {
		Objects.requireNonNull(min, "Min constraint must not be null");
		Objects.requireNonNull(max, "Max constraint must not be null");
		Objects.requireNonNull(equals, "Equals constraint must not be null");
		
		if (min.isPresent() && max.isPresent()) {
			Pair<Integer, Boolean> minPair = min.get();
			Pair<Integer, Boolean> maxPair = max.get();
			
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
	
	/**
	 * Creates a new configuration with a minimum value constraint.<br>
	 *
	 * @param min The minimum value
	 * @param inclusive True for inclusive (>=), false for exclusive (>)
	 * @return A new configuration with the minimum constraint applied
	 */
	public @NonNull FieldConstraintConfig withMin(int min, boolean inclusive) {
		return new FieldConstraintConfig(Optional.of(Pair.of(min, inclusive)), this.max, this.equals);
	}
	
	/**
	 * Creates a new configuration with a maximum value constraint.<br>
	 *
	 * @param max The maximum value
	 * @param inclusive True for inclusive (<=), false for exclusive (<)
	 * @return A new configuration with the maximum constraint applied
	 */
	public @NonNull FieldConstraintConfig withMax(int max, boolean inclusive) {
		return new FieldConstraintConfig(this.min, Optional.of(Pair.of(max, inclusive)), this.equals);
	}
	
	/**
	 * Creates a new configuration with a range constraint.<br>
	 *
	 * @param min The minimum value
	 * @param max The maximum value
	 * @param inclusive True for inclusive bounds, false for exclusive
	 * @return A new configuration with the range constraint applied
	 */
	public @NonNull FieldConstraintConfig withRange(int min, int max, boolean inclusive) {
		return new FieldConstraintConfig(Optional.of(Pair.of(min, inclusive)), Optional.of(Pair.of(max, inclusive)), this.equals);
	}
	
	/**
	 * Creates a new configuration with an equality constraint.<br>
	 *
	 * @param equals The value to match or exclude
	 * @param negated True to exclude (!=), false to require (==)
	 * @return A new configuration with the equality constraint applied
	 */
	public @NonNull FieldConstraintConfig withEquals(int equals, boolean negated) {
		return new FieldConstraintConfig(this.min, this.max, Optional.of(Pair.of(equals, negated)));
	}
	
	/**
	 * Validates the constraints against the given field value.<br>
	 *
	 * @param fieldName The name of the field being validated (for error messages)
	 * @param value The field value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 */
	public @NonNull Result<Void> matches(@NonNull String fieldName, int value) {
		Objects.requireNonNull(fieldName, "Field name must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}
		
		if (this.equals.isPresent()) {
			Pair<Integer, Boolean> pair = this.equals.get();
			if (pair.getSecond()) {
				if (value != pair.getFirst()) {
					return Result.success();
				}
				return Result.error("Violated equals constraint for " + fieldName + ": value (" + value + ") is equal to expected (" + pair.getFirst() + "), but it should not be");
			} else {
				if (value == pair.getFirst()) {
					return Result.success();
				}
				return Result.error("Violated equals constraint for " + fieldName + ": value (" + value + ") is not equal to expected (" + pair.getFirst() + "), but it should be");
			}
		}
		
		if (this.min.isPresent()) {
			Pair<Integer, Boolean> pair = this.min.get();
			int comparison = Integer.compare(value, pair.getFirst());
			if (pair.getSecond()) {
				if (comparison < 0) {
					return Result.error("Violated minimum constraint for " + fieldName + ": value (" + value + ") is less than min (" + pair.getFirst() + "), but it should be at least min");
				}
			} else {
				if (comparison <= 0) {
					return Result.error("Violated minimum constraint (exclusive) for " + fieldName + ": value (" + value + ") is less than or equal to min (" + pair.getFirst() + "), but it should be greater than min");
				}
			}
		}
		
		if (this.max.isPresent()) {
			Pair<Integer, Boolean> pair = this.max.get();
			int comparison = Integer.compare(value, pair.getFirst());
			if (pair.getSecond()) {
				if (comparison > 0) {
					return Result.error("Violated maximum constraint for " + fieldName + ": value (" + value + ") is greater than max (" + pair.getFirst() + "), but it should be at most max");
				}
			} else {
				if (comparison >= 0) {
					return Result.error("Violated maximum constraint (exclusive) for " + fieldName + ": value (" + value + ") is greater than or equal to max (" + pair.getFirst() + "), but it should be less than max");
				}
			}
		}
		
		return Result.success();
	}
	
	/**
	 * Appends the constraint description to the provided list.<br>
	 *
	 * @param fieldName The name of the field
	 * @param constraints The list to append constraint descriptions to
	 */
	public void appendConstraints(@NonNull String fieldName, @NonNull List<String> constraints) {
		if (this.isUnconstrained()) {
			return;
		}
		
		this.min.ifPresent(pair -> constraints.add(fieldName + ".min=" + pair.getFirst() + (pair.getSecond() ? " (inclusive)" : " (exclusive)")));
		this.max.ifPresent(pair -> constraints.add(fieldName + ".max=" + pair.getFirst() + (pair.getSecond() ? " (inclusive)" : " (exclusive)")));
		this.equals.ifPresent(pair -> constraints.add(fieldName + ".equals=" + pair.getFirst() + (pair.getSecond() ? " (negated)" : "")));
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "FieldConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.min.ifPresent(pair -> constraints.add("min=" + pair.getFirst() + (pair.getSecond() ? " (inclusive)" : " (exclusive)")));
		this.max.ifPresent(pair -> constraints.add("max=" + pair.getFirst() + (pair.getSecond() ? " (inclusive)" : " (exclusive)")));
		this.equals.ifPresent(pair -> constraints.add("equals=" + pair.getFirst() + (pair.getSecond() ? " (negated)" : "")));
		return "FieldConstraintConfig[" + String.join(", ", constraints) + "]";
	}
}
