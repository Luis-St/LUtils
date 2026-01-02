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

package net.luis.utils.io.codec.constraint.config.numeric;

import net.luis.utils.io.codec.constraint.numeric.DecimalConstraint;
import net.luis.utils.math.NumberType;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Configuration class for decimal (floating-point) numeric constraints.<br>
 * <p>
 *     This record holds constraint fields specific to decimal numeric values (float, double), including
 *     numeric constraints, finite value validation, NaN checks, integral form validation, and normalized form validation.<br>
 *     Unlike BigDecimal constraints, this configuration handles special floating-point values like infinity and NaN.
 * </p>
 * <p>
 *     Constraints are validated using the {@link #matches(NumberType, Number)} method, which checks whether
 *     a given decimal value satisfies all configured requirements.<br>
 *     Constraints are immutable once created, but new configurations can be derived using the
 *     various {@code with*} methods.
 * </p>
 *
 * @see DecimalConstraint
 * @see NumericConstraintConfig
 *
 * @author Luis-St
 *
 * @param numericConfig The optional numeric constraint configuration (empty if unconstrained)
 * @param finite The optional finite constraint (empty if unconstrained, value ignored if present)
 * @param notNaN The optional not-NaN constraint (empty if unconstrained, value ignored if present)
 * @param integral The optional integral constraint (empty if unconstrained, value ignored if present)
 * @param normalized The optional normalized constraint (empty if unconstrained, value ignored if present)
 * @param <T> The decimal numeric type being constrained
 */
public record DecimalConstraintConfig<T extends Number & Comparable<T>>(
	@NonNull Optional<NumericConstraintConfig<T>> numericConfig,
	@NonNull Optional</*Value Ignored*/ Boolean> finite,
	@NonNull Optional</*Value Ignored*/ Boolean> notNaN,
	@NonNull Optional</*Value Ignored*/ Boolean> integral,
	@NonNull Optional</*Value Ignored*/ Boolean> normalized
) {
	
	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all decimal values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations using
	 *     the various {@code with*} methods.
	 * </p>
	 *
	 * @see #unconstrained()
	 */
	private static final DecimalConstraintConfig<? extends Number> UNCONSTRAINED = new DecimalConstraintConfig<>(
		Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()
	);
	
	/**
	 * Constructs a new decimal constraint configuration with the specified constraints.<br>
	 *
	 * @param numericConfig The optional numeric constraint configuration (empty if unconstrained)
	 * @param finite The optional finite constraint (empty if unconstrained, value ignored if present)
	 * @param notNaN The optional not-NaN constraint (empty if unconstrained, value ignored if present)
	 * @param integral The optional integral constraint (empty if unconstrained, value ignored if present)
	 * @param normalized The optional normalized constraint (empty if unconstrained, value ignored if present)
	 * @throws NullPointerException If any parameter is null
	 */
	public DecimalConstraintConfig {
		Objects.requireNonNull(numericConfig, "Numeric configuration must not be null");
		Objects.requireNonNull(finite, "Finite constraint must not be null");
		Objects.requireNonNull(notNaN, "Not-NaN constraint must not be null");
		Objects.requireNonNull(integral, "Integral constraint must not be null");
		Objects.requireNonNull(normalized, "Normalized constraint must not be null");
	}
	
	/**
	 * Returns an unconstrained configuration for the specified type.<br>
	 * <p>
	 *     This method provides a type-safe way to get an unconstrained configuration without raw type warnings.
	 * </p>
	 *
	 * @return An unconstrained decimal constraint configuration
	 * @param <T> The decimal numeric type
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number & Comparable<T>> @NonNull DecimalConstraintConfig<T> unconstrained() {
		return (DecimalConstraintConfig<T>) UNCONSTRAINED;
	}
	
	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.numericConfig.isEmpty() && this.finite.isEmpty() && this.notNaN.isEmpty() && this.integral.isEmpty() && this.normalized.isEmpty());
	}
	
	/**
	 * Creates a new decimal constraint configuration with the modified numeric configuration.<br>
	 * <p>
	 *     This method allows customizing the numeric constraints (min, max, equals) applied to decimal values.<br>
	 *     If no numeric configuration is currently set, a new unconstrained configuration is created and modified.<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param numericConfigModifier A function that modifies the numeric configuration
	 * @return A new decimal constraint configuration with the modified numeric configuration
	 * @throws NullPointerException If the modifier is null
	 * @see NumericConstraintConfig
	 */
	public @NonNull DecimalConstraintConfig<T> withNumericConfig(@NonNull UnaryOperator<NumericConstraintConfig<T>> numericConfigModifier) {
		Objects.requireNonNull(numericConfigModifier, "Numeric configuration modifier must not be null");
		
		return new DecimalConstraintConfig<>(Optional.of(numericConfigModifier.apply(this.numericConfig.orElse(NumericConstraintConfig.unconstrained()))), this.finite, this.notNaN, this.integral, this.normalized);
	}
	
	/**
	 * Creates a new decimal constraint configuration with a finite constraint.<br>
	 * <p>
	 *     The finite constraint ensures that decimal values are finite (not infinite or NaN).<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @return A new decimal constraint configuration with the finite constraint applied
	 * @see #withNotNaN()
	 */
	public @NonNull DecimalConstraintConfig<T> withFinite() {
		return new DecimalConstraintConfig<>(this.numericConfig, Optional.of(true), this.notNaN, this.integral, this.normalized);
	}
	
	/**
	 * Creates a new decimal constraint configuration with a not-NaN constraint.<br>
	 * <p>
	 *     The not-NaN constraint ensures that decimal values are not NaN (Not a Number).<br>
	 *     This constraint allows infinite values but rejects NaN.<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @return A new decimal constraint configuration with the not-NaN constraint applied
	 * @see #withFinite()
	 */
	public @NonNull DecimalConstraintConfig<T> withNotNaN() {
		return new DecimalConstraintConfig<>(this.numericConfig, this.finite, Optional.of(true), this.integral, this.normalized);
	}
	
	/**
	 * Creates a new decimal constraint configuration with an integral constraint.<br>
	 * <p>
	 *     The integral constraint ensures that decimal values represent whole numbers (no fractional part).<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @return A new decimal constraint configuration with the integral constraint applied
	 * @see #withNormalized()
	 */
	public @NonNull DecimalConstraintConfig<T> withIntegral() {
		return new DecimalConstraintConfig<>(this.numericConfig, this.finite, this.notNaN, Optional.of(true), this.normalized);
	}
	
	/**
	 * Creates a new decimal constraint configuration with a normalized constraint.<br>
	 * <p>
	 *     The normalized constraint ensures that decimal values are within the range [0.0, 1.0].<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @return A new decimal constraint configuration with the normalized constraint applied
	 * @see #withIntegral()
	 */
	public @NonNull DecimalConstraintConfig<T> withNormalized() {
		return new DecimalConstraintConfig<>(this.numericConfig, this.finite, this.notNaN, this.integral, Optional.of(true));
	}
	
	/**
	 * Validates whether the given decimal value matches all constraints defined in this configuration.<br>
	 * <p>
	 *     This method checks if the provided value satisfies all configured requirements including
	 *     numeric constraints, finite value, not-NaN, integral form, and normalized form.<br>
	 *     If the value violates any constraint, an error result is returned with a descriptive message
	 *     indicating which constraint was violated.
	 * </p>
	 * <p>
	 *     If no constraints are set (unconstrained), this method always returns a success result.
	 * </p>
	 *
	 * @param type The number type being validated (must be a floating-point type)
	 * @param value The decimal value to validate
	 * @return A success result if the value meets all constraints, or an error result with a descriptive message indicating the violation
	 * @throws NullPointerException If the type or value is null
	 * @throws IllegalArgumentException If the type is not a floating-point type or if it is a big number type
	 * @see Result
	 */
	public @NonNull Result<Void> matches(@NonNull NumberType type, @NonNull T value) {
		Objects.requireNonNull(type, "Number type must not be null");
		Objects.requireNonNull(value, "Value to match must not be null");
		if (!type.isFloatingPoint()) {
			throw new IllegalArgumentException("Number type " + type + " is not a decimal (floating-point) type");
		}
		if (type.getBitSize() == -1) {
			throw new IllegalArgumentException("Big number type " + type + " is not supported for decimal (floating-point) constraints");
		}
		
		if (this.isUnconstrained()) {
			return Result.success();
		}
		
		if (this.numericConfig.isPresent()) {
			Result<Void> numericResult = this.numericConfig.get().matches(value);
			if (numericResult.isError()) {
				return numericResult;
			}
		}
		
		if (type == NumberType.FLOAT) {
			return this.matchesFloat(value.floatValue());
		} else if (type == NumberType.DOUBLE) {
			return this.matchesDouble(value.doubleValue());
		}
		throw new IllegalStateException("Unsupported decimal (floating-point) number type: " + type);
	}
	
	/**
	 * Validates float-specific constraints against the given value.<br>
	 * <p>
	 *     This method checks finite, not-NaN, integral, and normalized constraints for float values.<br>
	 *     It uses float-specific comparison methods to handle special floating-point values correctly.
	 * </p>
	 *
	 * @param value The float value to validate
	 * @return A success result if the value meets all constraints, or an error result with a descriptive message
	 */
	@SuppressWarnings("FloatingPointEquality")
	private @NonNull Result<Void> matchesFloat(float value) {
		if (this.finite.isPresent() && !Float.isFinite(value)) {
			return Result.error("Violated finite constraint: " + value + " is not finite");
		}
		
		if (this.notNaN.isPresent() && Float.isNaN(value)) {
			return Result.error("Violated not-NaN constraint: " + value + " is NaN");
		}
		
		if (this.integral.isPresent() && Math.floor(value) != value) {
			return Result.error("Violated integral constraint: " + value + " has a fractional part");
		}
		
		if (this.normalized.isPresent() && (value > 1.0F || 0.0F > value)) {
			return Result.error("Violated normalized constraint: " + value + " is not normalized, must be in range [0.0, 1.0]");
		}
		return Result.success();
	}
	
	/**
	 * Validates double-specific constraints against the given value.<br>
	 * <p>
	 *     This method checks finite, not-NaN, integral, and normalized constraints for double values.<br>
	 *     It uses double-specific comparison methods to handle special floating-point values correctly.
	 * </p>
	 *
	 * @param value The double value to validate
	 * @return A success result if the value meets all constraints, or an error result with a descriptive message
	 */
	@SuppressWarnings("FloatingPointEquality")
	private @NonNull Result<Void> matchesDouble(double value) {
		if (this.finite.isPresent() && !Double.isFinite(value)) {
			return Result.error("Violated finite constraint: " + value + " is not finite");
		}
		
		if (this.notNaN.isPresent() && Double.isNaN(value)) {
			return Result.error("Violated not-NaN constraint: " + value + " is NaN");
		}
		
		if (this.integral.isPresent() && Math.floor(value) != value) {
			return Result.error("Violated integral constraint: " + value + " has a fractional part");
		}
		
		if (this.normalized.isPresent() && (value > 1.0 || 0.0 > value)) {
			return Result.error("Violated normalized constraint: " + value + " is not normalized, must be in range [0.0, 1.0]");
		}
		return Result.success();
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "DecimalConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.numericConfig.ifPresent(config -> config.appendConstraints(constraints));
		this.finite.ifPresent(_ -> constraints.add("finite"));
		this.notNaN.ifPresent(_ -> constraints.add("notNaN"));
		this.integral.ifPresent(_ -> constraints.add("integral"));
		this.normalized.ifPresent(_ -> constraints.add("normalized"));
		return "DecimalConstraintConfig[" + String.join(",", constraints) + "]";
	}
}
