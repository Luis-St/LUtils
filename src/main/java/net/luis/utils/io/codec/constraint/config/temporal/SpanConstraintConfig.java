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

import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.time.*;
import java.util.*;

/**
 * Configuration class for temporal span constraints (withinLast and withinNext).<br>
 * This class holds span constraint fields that are common to temporal types that support duration-based constraints.<br>
 *
 * @author Luis-St
 *
 * @param withinLast The "within last" duration constraint (empty if unconstrained)
 * @param withinNext The "within next" duration constraint (empty if unconstrained)
 */
public record SpanConstraintConfig(
	@NonNull Optional<Duration> withinLast,
	@NonNull Optional<Duration> withinNext
) {

	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all temporal values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations.
	 * </p>
	 */
	public static final SpanConstraintConfig UNCONSTRAINED = new SpanConstraintConfig(
		Optional.empty(), Optional.empty()
	);

	/**
	 * Constructs a new span constraint configuration with the specified constraints.<br>
	 *
	 * @param withinLast The "within last" duration constraint (empty if unconstrained)
	 * @param withinNext The "within next" duration constraint (empty if unconstrained)
	 * @throws NullPointerException If withinLast or withinNext is null
	 */
	public SpanConstraintConfig {
		Objects.requireNonNull(withinLast, "Within last constraint must not be null");
		Objects.requireNonNull(withinNext, "Within next constraint must not be null");
	}

	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.withinLast.isEmpty() && this.withinNext.isEmpty());
	}

	/**
	 * Creates a new configuration with a "within last" duration constraint.<br>
	 *
	 * @param duration The duration specifying how far back in time values are allowed
	 * @return A new configuration with the "within last" constraint applied
	 */
	public @NonNull SpanConstraintConfig withWithinLast(@NonNull Duration duration) {
		return new SpanConstraintConfig(Optional.of(duration), this.withinNext);
	}

	/**
	 * Creates a new configuration with a "within next" duration constraint.<br>
	 *
	 * @param duration The duration specifying how far forward in time values are allowed
	 * @return A new configuration with the "within next" constraint applied
	 */
	public @NonNull SpanConstraintConfig withWithinNext(@NonNull Duration duration) {
		return new SpanConstraintConfig(this.withinLast, Optional.of(duration));
	}

	/**
	 * Validates the span constraints against the given LocalDate value.<br>
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

		if (this.withinLast.isPresent()) {
			LocalDate threshold = LocalDate.now().minus(this.withinLast.get());
			if (value.isBefore(threshold)) {
				return Result.error("Violated within-last constraint: value (" + value + ") is before threshold (" + threshold + "), but it should be within the last " + this.withinLast.get());
			}
		}

		if (this.withinNext.isPresent()) {
			LocalDate threshold = LocalDate.now().plus(this.withinNext.get());
			if (value.isAfter(threshold)) {
				return Result.error("Violated within-next constraint: value (" + value + ") is after threshold (" + threshold + "), but it should be within the next " + this.withinNext.get());
			}
		}

		return Result.success();
	}

	/**
	 * Validates the span constraints against the given LocalTime value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull LocalTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}

		if (this.withinLast.isPresent()) {
			LocalTime threshold = LocalTime.now().minus(this.withinLast.get());
			if (value.isBefore(threshold)) {
				return Result.error("Violated within-last constraint: value (" + value + ") is before threshold (" + threshold + "), but it should be within the last " + this.withinLast.get());
			}
		}

		if (this.withinNext.isPresent()) {
			LocalTime threshold = LocalTime.now().plus(this.withinNext.get());
			if (value.isAfter(threshold)) {
				return Result.error("Violated within-next constraint: value (" + value + ") is after threshold (" + threshold + "), but it should be within the next " + this.withinNext.get());
			}
		}

		return Result.success();
	}

	/**
	 * Validates the span constraints against the given LocalDateTime value.<br>
	 *
	 * @param value The value to validate
	 * @return A success result if the value meets the constraints, or an error result with a descriptive message
	 * @throws NullPointerException If the value is null
	 */
	public @NonNull Result<Void> matches(@NonNull LocalDateTime value) {
		Objects.requireNonNull(value, "Value must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}

		if (this.withinLast.isPresent()) {
			LocalDateTime threshold = LocalDateTime.now().minus(this.withinLast.get());
			if (value.isBefore(threshold)) {
				return Result.error("Violated within-last constraint: value (" + value + ") is before threshold (" + threshold + "), but it should be within the last " + this.withinLast.get());
			}
		}

		if (this.withinNext.isPresent()) {
			LocalDateTime threshold = LocalDateTime.now().plus(this.withinNext.get());
			if (value.isAfter(threshold)) {
				return Result.error("Violated within-next constraint: value (" + value + ") is after threshold (" + threshold + "), but it should be within the next " + this.withinNext.get());
			}
		}

		return Result.success();
	}

	/**
	 * Validates the span constraints against the given OffsetTime value.<br>
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

		if (this.withinLast.isPresent()) {
			OffsetTime threshold = OffsetTime.now().minus(this.withinLast.get());
			if (value.isBefore(threshold)) {
				return Result.error("Violated within-last constraint: value (" + value + ") is before threshold (" + threshold + "), but it should be within the last " + this.withinLast.get());
			}
		}

		if (this.withinNext.isPresent()) {
			OffsetTime threshold = OffsetTime.now().plus(this.withinNext.get());
			if (value.isAfter(threshold)) {
				return Result.error("Violated within-next constraint: value (" + value + ") is after threshold (" + threshold + "), but it should be within the next " + this.withinNext.get());
			}
		}

		return Result.success();
	}

	/**
	 * Validates the span constraints against the given OffsetDateTime value.<br>
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

		if (this.withinLast.isPresent()) {
			OffsetDateTime threshold = OffsetDateTime.now().minus(this.withinLast.get());
			if (value.isBefore(threshold)) {
				return Result.error("Violated within-last constraint: value (" + value + ") is before threshold (" + threshold + "), but it should be within the last " + this.withinLast.get());
			}
		}

		if (this.withinNext.isPresent()) {
			OffsetDateTime threshold = OffsetDateTime.now().plus(this.withinNext.get());
			if (value.isAfter(threshold)) {
				return Result.error("Violated within-next constraint: value (" + value + ") is after threshold (" + threshold + "), but it should be within the next " + this.withinNext.get());
			}
		}

		return Result.success();
	}

	/**
	 * Validates the span constraints against the given ZonedDateTime value.<br>
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

		if (this.withinLast.isPresent()) {
			ZonedDateTime threshold = ZonedDateTime.now().minus(this.withinLast.get());
			if (value.isBefore(threshold)) {
				return Result.error("Violated within-last constraint: value (" + value + ") is before threshold (" + threshold + "), but it should be within the last " + this.withinLast.get());
			}
		}

		if (this.withinNext.isPresent()) {
			ZonedDateTime threshold = ZonedDateTime.now().plus(this.withinNext.get());
			if (value.isAfter(threshold)) {
				return Result.error("Violated within-next constraint: value (" + value + ") is after threshold (" + threshold + "), but it should be within the next " + this.withinNext.get());
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

		this.withinLast.ifPresent(d -> constraints.add("withinLast=" + d));
		this.withinNext.ifPresent(d -> constraints.add("withinNext=" + d));
	}

	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "SpanConstraintConfig[unconstrained]";
		}

		List<String> constraints = new ArrayList<>();
		this.appendConstraints(constraints);
		return "SpanConstraintConfig[" + String.join(",", constraints) + "]";
	}
}
