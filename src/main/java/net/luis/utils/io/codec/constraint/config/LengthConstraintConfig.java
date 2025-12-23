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

package net.luis.utils.io.codec.constraint.config;

import net.luis.utils.io.codec.constraint.LengthConstraint;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.OptionalInt;

/**
 * Configuration class for length constraints on strings, arrays, and other sequential types.<br>
 * <p>
 *     This record holds the minimum and maximum length constraints that can be applied to sequential values.<br>
 *     Both constraints are optional and can be used independently or together to define length boundaries.
 * </p>
 * <p>
 *     Length constraints are validated using the {@link #matches(int)} method, which checks whether
 *     a given length satisfies the configured minimum and maximum requirements.<br>
 *     Constraints are immutable once created, but new configurations can be derived using the
 *     {@link #withMinLength(int)} and {@link #withMaxLength(int)} methods.
 * </p>
 *
 * @see LengthConstraint
 *
 * @author Luis-St
 *
 * @param minLength The optional minimum length constraint (empty if no minimum is set)
 * @param maxLength The optional maximum length constraint (empty if no maximum is set)
 */
public record LengthConstraintConfig(
	@NonNull OptionalInt minLength,
	@NonNull OptionalInt maxLength
) {

	/**
	 * A predefined unconstrained configuration with no minimum or maximum length limits.<br>
	 * <p>
	 *     This constant represents a configuration where all lengths are valid.<br>
	 *     It can be used as a starting point to build constrained configurations using
	 *     {@link #withMinLength(int)} and {@link #withMaxLength(int)}.
	 * </p>
	 *
	 * @see #withMinLength(int)
	 * @see #withMaxLength(int)
	 */
	public static final LengthConstraintConfig UNCONSTRAINED = new LengthConstraintConfig(OptionalInt.empty(), OptionalInt.empty());

	/**
	 * Constructs a new length constraint configuration with the specified minimum and maximum lengths.<br>
	 *
	 * @param minLength The optional minimum length constraint (empty if no minimum is set)
	 * @param maxLength The optional maximum length constraint (empty if no maximum is set)
	 * @throws NullPointerException If either the minimum or maximum length is null
	 * @throws IllegalArgumentException If the minimum length is negative when present,
	 *                                  if the maximum length is negative when present,
	 *                                  or if the maximum length is less than the minimum length when both are present
	 */
	public LengthConstraintConfig {
		Objects.requireNonNull(minLength, "Minimum length must not be null");
		Objects.requireNonNull(maxLength, "Maximum length must not be null");

		if (minLength.isPresent() && minLength.getAsInt() < 0) {
			throw new IllegalArgumentException("Minimum length must not be negative when present");
		}
		if (maxLength.isPresent() && maxLength.getAsInt() < 0) {
			throw new IllegalArgumentException("Maximum length must not be negative when present");
		}
		if (minLength.isPresent() && maxLength.isPresent() && maxLength.getAsInt() < minLength.getAsInt()) {
			throw new IllegalArgumentException("Maximum length must not be less than minimum length when both are present");
		}
	}

	/**
	 * Creates a new length constraint configuration with the specified minimum length.<br>
	 * <p>
	 *     The maximum length from the current configuration is preserved.<br>
	 *     This method is useful for incrementally building constraint configurations.
	 * </p>
	 *
	 * @param minLength The minimum length to set (must be non-negative)
	 * @return A new length constraint configuration with the specified minimum length
	 * @throws IllegalArgumentException If the minimum length is negative, or if it exceeds the current maximum length
	 * @see #withMaxLength(int)
	 */
	public @NonNull LengthConstraintConfig withMinLength(int minLength) {
		return new LengthConstraintConfig(OptionalInt.of(minLength), this.maxLength);
	}

	/**
	 * Creates a new length constraint configuration with the specified maximum length.<br>
	 * <p>
	 *     The minimum length from the current configuration is preserved.<br>
	 *     This method is useful for incrementally building constraint configurations.
	 * </p>
	 *
	 * @param maxLength The maximum length to set (must be non-negative)
	 * @return A new length constraint configuration with the specified maximum length
	 * @throws IllegalArgumentException If the maximum length is negative, or if it is less than the current minimum length
	 * @see #withMinLength(int)
	 */
	public @NonNull LengthConstraintConfig withMaxLength(int maxLength) {
		return new LengthConstraintConfig(this.minLength, OptionalInt.of(maxLength));
	}

	/**
	 * Validates whether the given length matches the constraints defined in this configuration.<br>
	 * <p>
	 *     This method checks if the provided length satisfies both the minimum and maximum length requirements.<br>
	 *     If the length is outside the allowed range, an error result is returned with a descriptive message
	 *     indicating which constraint was violated.
	 * </p>
	 * <p>
	 *     If no constraints are set (unconstrained), this method always returns a success result.
	 * </p>
	 *
	 * @param length The length to validate
	 * @return A success result if the length meets the constraints, or an error result with a descriptive message indicating the violation
	 * @see Result
	 */
	public @NonNull Result<Void> matches(int length) {
		if (this.minLength.isPresent() && length < this.minLength.getAsInt()) {
			return Result.error("Violated minimum length constraint: " + length + " < " + this.minLength.getAsInt());
		}
		if (this.maxLength.isPresent() && length > this.maxLength.getAsInt()) {
			return Result.error("Violated maximum length constraint: " + length + " > " + this.maxLength.getAsInt());
		}
		return Result.success();
	}

	@Override
	public @NonNull String toString() {
		if (this == UNCONSTRAINED || (this.minLength.isEmpty() && this.maxLength.isEmpty())) {
			return "LengthConstraintConfig[unconstrained]";
		}

		if (this.minLength.isPresent() && this.maxLength.isPresent()) {
			if (this.minLength.getAsInt() == this.maxLength.getAsInt()) {
				return "LengthConstraintConfig[length=" + this.minLength.getAsInt() + "]";
			}
			return "LengthConstraintConfig[minLength=" + this.minLength.getAsInt() + ",maxLength=" + this.maxLength.getAsInt() + "]";
		} else if (this.minLength.isPresent()) {
			return "LengthConstraintConfig[minLength=" + this.minLength.getAsInt() + "]";
		} else {
			return "LengthConstraintConfig[maxLength=" + this.maxLength.getAsInt() + "]";
		}
	}
}
