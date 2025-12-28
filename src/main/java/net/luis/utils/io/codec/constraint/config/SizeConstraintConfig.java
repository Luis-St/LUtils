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

import net.luis.utils.io.codec.constraint.SizeConstraint;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.OptionalInt;

/**
 * Configuration class for size constraints on collections, maps, and other sized types.<br>
 * <p>
 *     This record holds the minimum and maximum size constraints that can be applied to sized values.<br>
 *     Both constraints are optional and can be used independently or together to define size boundaries.
 * </p>
 * <p>
 *     Size constraints are validated using the {@link #matches(int)} method, which checks whether
 *     a given size satisfies the configured minimum and maximum requirements.<br>
 *     Constraints are immutable once created, but new configurations can be derived using the
 *     {@link #withMinSize(int)} and {@link #withMaxSize(int)} methods.
 * </p>
 *
 * @see SizeConstraint
 *
 * @author Luis-St
 *
 * @param minSize The optional minimum size constraint (empty if no minimum is set)
 * @param maxSize The optional maximum size constraint (empty if no maximum is set)
 */
public record SizeConstraintConfig(
	@NonNull OptionalInt minSize,
	@NonNull OptionalInt maxSize
) {

	/**
	 * A predefined unconstrained configuration with no minimum or maximum size limits.<br>
	 * <p>
	 *     This constant represents a configuration where all sizes are valid.<br>
	 *     It can be used as a starting point to build constrained configurations using
	 *     {@link #withMinSize(int)} and {@link #withMaxSize(int)}.
	 * </p>
	 *
	 * @see #withMinSize(int)
	 * @see #withMaxSize(int)
	 */
	public static final SizeConstraintConfig UNCONSTRAINED = new SizeConstraintConfig(OptionalInt.empty(), OptionalInt.empty());
	
	/**
	 * Constructs a new size constraint configuration with the specified minimum and maximum sizes.<br>
	 *
	 * @param minSize The optional minimum size constraint (empty if no minimum is set)
	 * @param maxSize The optional maximum size constraint (empty if no maximum is set)
	 * @throws NullPointerException If either the minimum or maximum size is null
	 * @throws IllegalArgumentException If the minimum size is negative when present,
	 *                                  if the maximum size is negative when present,
	 *                                  or if the maximum size is less than the minimum size when both are present
	 */
	public SizeConstraintConfig {
		Objects.requireNonNull(minSize, "Minimum size must not be null");
		Objects.requireNonNull(maxSize, "Maximum size must not be null");
		
		if (minSize.isPresent() && minSize.getAsInt() < 0) {
			throw new IllegalArgumentException("Minimum size must not be negative when present");
		}
		if (maxSize.isPresent() && maxSize.getAsInt() < 0) {
			throw new IllegalArgumentException("Maximum size must not be negative when present");
		}
		if (minSize.isPresent() && maxSize.isPresent() && maxSize.getAsInt() < minSize.getAsInt()) {
			throw new IllegalArgumentException("Maximum size must not be less than minimum size when both are present");
		}
	}

	/**
	 * Creates a new size constraint configuration with the specified minimum size.<br>
	 * <p>
	 *     The maximum size from the current configuration is preserved.<br>
	 *     This method is useful for incrementally building constraint configurations.
	 * </p>
	 *
	 * @param minSize The minimum size to set (must be non-negative)
	 * @return A new size constraint configuration with the specified minimum size
	 * @throws IllegalArgumentException If the minimum size is negative, or if it exceeds the current maximum size
	 * @see #withMaxSize(int)
	 * @see #withSize(int, int)
	 */
	public @NonNull SizeConstraintConfig withMinSize(int minSize) {
		return new SizeConstraintConfig(OptionalInt.of(minSize), this.maxSize);
	}

	/**
	 * Creates a new size constraint configuration with the specified maximum size.<br>
	 * <p>
	 *     The minimum size from the current configuration is preserved.<br>
	 *     This method is useful for incrementally building constraint configurations.
	 * </p>
	 *
	 * @param maxSize The maximum size to set (must be non-negative)
	 * @return A new size constraint configuration with the specified maximum size
	 * @throws IllegalArgumentException If the maximum size is negative, or if it is less than the current minimum size
	 * @see #withMinSize(int)
	 * @see #withSize(int, int)
	 */
	public @NonNull SizeConstraintConfig withMaxSize(int maxSize) {
		return new SizeConstraintConfig(this.minSize, OptionalInt.of(maxSize));
	}
	
	/**
	 * Creates a new size constraint configuration with the specified minimum and maximum sizes.<br>
	 * <p>
	 *     This method allows setting both size constraints simultaneously, replacing any existing constraints.<br>
	 *     It is useful for defining a complete size range in one step.
	 * </p>
	 *
	 * @param minSize The minimum size to set (must be non-negative)
	 * @param maxSize The maximum size to set (must be non-negative)
	 * @return A new size constraint configuration with the specified minimum and maximum sizes
	 * @throws IllegalArgumentException If the minimum size or maximum size is negative, or if the maximum size is less than the minimum size
	 * @see #withMinSize(int)
	 * @see #withMaxSize(int)
	 */
	public @NonNull SizeConstraintConfig withSize(int minSize, int maxSize) {
		return new SizeConstraintConfig(OptionalInt.of(minSize), OptionalInt.of(maxSize));
	}
	
	/**
	 * Validates whether the given size matches the constraints defined in this configuration.<br>
	 * <p>
	 *     This method checks if the provided size satisfies both the minimum and maximum size requirements.<br>
	 *     If the size is outside the allowed range, an error result is returned with a descriptive message
	 *     indicating which constraint was violated.
	 * </p>
	 * <p>
	 *     If no constraints are set (unconstrained), this method always returns a success result.
	 * </p>
	 *
	 * @param size The size to validate
	 * @return A success result if the size meets the constraints, or an error result with a descriptive message indicating the violation
	 * @see Result
	 */
	public @NonNull Result<Void> matches(int size) {
		if (this.minSize.isPresent() && size < this.minSize.getAsInt()) {
			return Result.error("Violated minimum size constraint: " + size + " < " + this.minSize.getAsInt());
		}
		if (this.maxSize.isPresent() && size > this.maxSize.getAsInt()) {
			return Result.error("Violated maximum size constraint: " + size + " > " + this.maxSize.getAsInt());
		}
		return Result.success();
	}
	
	@Override
	public @NonNull String toString() {
		if (this == UNCONSTRAINED || (this.minSize.isEmpty() && this.maxSize.isEmpty())) {
			return "SizeConstraintConfig[unconstrained]";
		}
		
		if (this.minSize.isPresent() && this.maxSize.isPresent()) {
			if (this.minSize.getAsInt() == this.maxSize.getAsInt()) {
				return "SizeConstraintConfig[size=" + this.minSize.getAsInt() + "]";
			}
			return "SizeConstraintConfig[minSize=" + this.minSize.getAsInt() + ",maxSize=" + this.maxSize.getAsInt() + "]";
		} else if (this.minSize.isPresent()) {
			return "SizeConstraintConfig[minSize=" + this.minSize.getAsInt() + "]";
		} else {
			return "SizeConstraintConfig[maxSize=" + this.maxSize.getAsInt() + "]";
		}
	}
}
