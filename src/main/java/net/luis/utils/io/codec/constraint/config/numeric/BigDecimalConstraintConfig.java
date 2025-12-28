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

package net.luis.utils.io.codec.constraint.config.numeric;

import net.luis.utils.io.codec.constraint.numeric.BigDecimalConstraint;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Configuration class for big decimal specific constraints.<br>
 * <p>
 *     This record holds constraint fields specific to big decimal values, including numeric constraints,
 *     integral and normalized form validation, as well as scale and precision constraints.<br>
 *     Scale constraints control the number of digits after the decimal point, while precision constraints
 *     control the total number of significant digits.
 * </p>
 * <p>
 *     Constraints are validated using the {@link #matches(BigDecimal)} method, which checks whether
 *     a given big decimal value satisfies all configured requirements.<br>
 *     Constraints are immutable once created, but new configurations can be derived using the
 *     various {@code with*} methods.
 * </p>
 *
 * @see BigDecimalConstraint
 * @see NumericConstraintConfig
 *
 * @author Luis-St
 *
 * @param numericConfig The optional numeric constraint configuration (empty if unconstrained)
 * @param integral The optional integral constraint (empty if unconstrained, value ignored if present)
 * @param normalized The optional normalized constraint (empty if unconstrained, value ignored if present)
 * @param minScale The optional minimum scale constraint (empty if unconstrained)
 * @param maxScale The optional maximum scale constraint (empty if unconstrained)
 * @param minPrecision The optional minimum precision constraint (empty if unconstrained)
 * @param maxPrecision The optional maximum precision constraint (empty if unconstrained)
 */
public record BigDecimalConstraintConfig(
	@NonNull Optional<NumericConstraintConfig<BigDecimal>> numericConfig,
	@NonNull Optional</*Value Ignored*/ Boolean> integral,
	@NonNull Optional</*Value Ignored*/ Boolean> normalized,
	@NonNull OptionalInt minScale,
	@NonNull OptionalInt maxScale,
	@NonNull OptionalInt minPrecision,
	@NonNull OptionalInt maxPrecision
) {
	
	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all big decimal values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations using
	 *     the various {@code with*} methods.
	 * </p>
	 */
	public static final BigDecimalConstraintConfig UNCONSTRAINED = new BigDecimalConstraintConfig(
		Optional.empty(), Optional.empty(), Optional.empty(),
		OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty(), OptionalInt.empty()
	);
	
	/**
	 * Constructs a new big decimal constraint configuration with the specified constraints.<br>
	 *
	 * @param numericConfig The optional numeric constraint configuration (empty if unconstrained)
	 * @param integral The optional integral constraint (empty if unconstrained, value ignored if present)
	 * @param normalized The optional normalized constraint (empty if unconstrained, value ignored if present)
	 * @param minScale The optional minimum scale constraint (empty if unconstrained)
	 * @param maxScale The optional maximum scale constraint (empty if unconstrained)
	 * @param minPrecision The optional minimum precision constraint (empty if unconstrained)
	 * @param maxPrecision The optional maximum precision constraint (empty if unconstrained)
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If the minimum scale is greater than the maximum scale when both are present,
	 *                                  if the minimum precision is negative when present,
	 *                                  if the maximum precision is negative when present,
	 *                                  or if the minimum precision is greater than the maximum precision when both are present
	 */
	public BigDecimalConstraintConfig {
		Objects.requireNonNull(numericConfig, "Numeric configuration must not be null");
		Objects.requireNonNull(integral, "Integral constraint must not be null");
		Objects.requireNonNull(normalized, "Normalized constraint must not be null");
		Objects.requireNonNull(minScale, "Min scale must not be null");
		Objects.requireNonNull(maxScale, "Max scale must not be null");
		Objects.requireNonNull(minPrecision, "Min precision must not be null");
		Objects.requireNonNull(maxPrecision, "Max precision must not be null");
		
		if (minScale.isPresent() && maxScale.isPresent()) {
			if (minScale.getAsInt() > maxScale.getAsInt()) {
				throw new IllegalArgumentException("Min scale must be less than or equal to max scale when both are present");
			}
		}
		
		if (minPrecision.isPresent() && 0 > minPrecision.getAsInt()) {
			throw new IllegalArgumentException("Min precision must be non-negative when present");
		}
		if (maxPrecision.isPresent() && 0 > maxPrecision.getAsInt()) {
			throw new IllegalArgumentException("Max precision must be non-negative when present");
		}
		if (minPrecision.isPresent() && maxPrecision.isPresent()) {
			if (minPrecision.getAsInt() > maxPrecision.getAsInt()) {
				throw new IllegalArgumentException("Min precision must be less than or equal to max precision when both are present");
			}
		}
	}
	
	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.numericConfig.isEmpty() && this.integral.isEmpty() && this.normalized.isEmpty() && this.minScale.isEmpty() && this.maxScale.isEmpty() && this.minPrecision.isEmpty() && this.maxPrecision.isEmpty());
	}
	
	/**
	 * Creates a new big decimal constraint configuration with the modified numeric configuration.<br>
	 * <p>
	 *     This method allows customizing the numeric constraints (min, max, equals) applied to big decimal values.<br>
	 *     If no numeric configuration is currently set, a new unconstrained configuration is created and modified.<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param numericConfigModifier A function that modifies the numeric configuration
	 * @return A new big decimal constraint configuration with the modified numeric configuration
	 * @throws NullPointerException If the modifier is null
	 * @see NumericConstraintConfig
	 */
	public @NonNull BigDecimalConstraintConfig withNumericConfig(@NonNull UnaryOperator<NumericConstraintConfig<BigDecimal>> numericConfigModifier) {
		Objects.requireNonNull(numericConfigModifier, "Numeric configuration modifier must not be null");
		
		return new BigDecimalConstraintConfig(
			Optional.of(numericConfigModifier.apply(this.numericConfig.orElse(NumericConstraintConfig.unconstrained()))), this.integral, this.normalized, this.minScale, this.maxScale, this.minPrecision, this.maxPrecision
		);
	}
	
	/**
	 * Creates a new big decimal constraint configuration with an integral constraint.<br>
	 * <p>
	 *     The integral constraint ensures that big decimal values represent whole numbers (no fractional part).<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @return A new big decimal constraint configuration with the integral constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withIntegral() {
		return new BigDecimalConstraintConfig(this.numericConfig, Optional.of(true), this.normalized, this.minScale, this.maxScale, this.minPrecision, this.maxPrecision);
	}
	
	/**
	 * Creates a new big decimal constraint configuration with a normalized constraint.<br>
	 * <p>
	 *     The normalized constraint ensures that big decimal values are within the range [0.0, 1.0].<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @return A new big decimal constraint configuration with the normalized constraint applied
	 */
	public @NonNull BigDecimalConstraintConfig withNormalized() {
		return new BigDecimalConstraintConfig(this.numericConfig, this.integral, Optional.of(true), this.minScale, this.maxScale, this.minPrecision, this.maxPrecision);
	}
	
	/**
	 * Creates a new big decimal constraint configuration with the specified minimum scale.<br>
	 * <p>
	 *     The minimum scale constraint ensures that big decimal values have at least the specified number of digits after the decimal point.<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param minScale The minimum scale (number of decimal places)
	 * @return A new big decimal constraint configuration with the specified minimum scale
	 * @see #withMaxScale(int)
	 * @see #withScale(int, int)
	 */
	public @NonNull BigDecimalConstraintConfig withMinScale(int minScale) {
		return new BigDecimalConstraintConfig(this.numericConfig, this.integral, this.normalized, OptionalInt.of(minScale), this.maxScale, this.minPrecision, this.maxPrecision);
	}
	
	/**
	 * Creates a new big decimal constraint configuration with the specified maximum scale.<br>
	 * <p>
	 *     The maximum scale constraint ensures that big decimal values have at most the specified number of digits after the decimal point.<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param maxScale The maximum scale (number of decimal places)
	 * @return A new big decimal constraint configuration with the specified maximum scale
	 * @see #withMinScale(int)
	 * @see #withScale(int, int)
	 */
	public @NonNull BigDecimalConstraintConfig withMaxScale(int maxScale) {
		return new BigDecimalConstraintConfig(this.numericConfig, this.integral, this.normalized, this.minScale, OptionalInt.of(maxScale), this.minPrecision, this.maxPrecision);
	}
	
	/**
	 * Creates a new big decimal constraint configuration with the specified minimum and maximum scale.<br>
	 * <p>
	 *     The scale constraints ensure that big decimal values have a number of decimal places within the specified range (inclusive).<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param minScale The minimum scale (number of decimal places)
	 * @param maxScale The maximum scale (number of decimal places)
	 * @return A new big decimal constraint configuration with the specified scale range
	 * @throws IllegalArgumentException If the minimum scale is greater than the maximum scale
	 * @see #withMinScale(int)
	 * @see #withMaxScale(int)
	 */
	public @NonNull BigDecimalConstraintConfig withScale(int minScale, int maxScale) {
		return new BigDecimalConstraintConfig(this.numericConfig, this.integral, this.normalized, OptionalInt.of(minScale), OptionalInt.of(maxScale), this.minPrecision, this.maxPrecision);
	}
	
	/**
	 * Creates a new big decimal constraint configuration with the specified minimum precision.<br>
	 * <p>
	 *     The minimum precision constraint ensures that big decimal values have at least the specified number of significant digits.<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param minPrecision The minimum precision (number of significant digits)
	 * @return A new big decimal constraint configuration with the specified minimum precision
	 * @throws IllegalArgumentException If the minimum precision is negative
	 * @see #withMaxPrecision(int)
	 * @see #withPrecision(int, int)
	 */
	public @NonNull BigDecimalConstraintConfig withMinPrecision(int minPrecision) {
		return new BigDecimalConstraintConfig(this.numericConfig, this.integral, this.normalized, this.minScale, this.maxScale, OptionalInt.of(minPrecision), this.maxPrecision);
	}
	
	/**
	 * Creates a new big decimal constraint configuration with the specified maximum precision.<br>
	 * <p>
	 *     The maximum precision constraint ensures that big decimal values have at most the specified number of significant digits.<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param maxPrecision The maximum precision (number of significant digits)
	 * @return A new big decimal constraint configuration with the specified maximum precision
	 * @throws IllegalArgumentException If the maximum precision is negative
	 * @see #withMinPrecision(int)
	 * @see #withPrecision(int, int)
	 */
	public @NonNull BigDecimalConstraintConfig withMaxPrecision(int maxPrecision) {
		return new BigDecimalConstraintConfig(this.numericConfig, this.integral, this.normalized, this.minScale, this.maxScale, this.minPrecision, OptionalInt.of(maxPrecision));
	}
	
	/**
	 * Creates a new big decimal constraint configuration with the specified minimum and maximum precision.<br>
	 * <p>
	 *     The precision constraints ensure that big decimal values have a number of significant digits within the specified range (inclusive).<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param minPrecision The minimum precision (number of significant digits)
	 * @param maxPrecision The maximum precision (number of significant digits)
	 * @return A new big decimal constraint configuration with the specified precision range
	 * @throws IllegalArgumentException If the minimum precision is negative, if the maximum precision is negative,
	 *                                  or if the minimum precision is greater than the maximum precision
	 * @see #withMinPrecision(int)
	 * @see #withMaxPrecision(int)
	 */
	public @NonNull BigDecimalConstraintConfig withPrecision(int minPrecision, int maxPrecision) {
		return new BigDecimalConstraintConfig(this.numericConfig, this.integral, this.normalized, this.minScale, this.maxScale, OptionalInt.of(minPrecision), OptionalInt.of(maxPrecision));
	}
	
	/**
	 * Validates whether the given big decimal value matches all constraints defined in this configuration.<br>
	 * <p>
	 *     This method checks if the provided value satisfies all configured requirements including
	 *     numeric constraints, integral form, normalized form, scale, and precision.<br>
	 *     If the value violates any constraint, an error result is returned with a descriptive message
	 *     indicating which constraint was violated.
	 * </p>
	 * <p>
	 *     If no constraints are set (unconstrained), this method always returns a success result.
	 * </p>
	 *
	 * @param value The big decimal value to validate
	 * @return A success result if the value meets all constraints, or an error result with a descriptive message indicating the violation
	 * @throws NullPointerException If the value is null
	 * @see Result
	 */
	public @NonNull Result<Void> matches(@NonNull BigDecimal value) {
		Objects.requireNonNull(value, "Value to match must not be null");
		if (this.isUnconstrained()) {
			return Result.success();
		}
		
		if (this.numericConfig.isPresent()) {
			Result<Void> numericResult = this.numericConfig.get().matches(value);
			if (numericResult.isError()) {
				return numericResult;
			}
		}
		
		if (this.integral.isPresent() && value.stripTrailingZeros().scale() > 0) {
			return Result.error("Violated integral constraint: " + value + " has a fractional part");
		}
		
		if (this.normalized.isPresent() && (value.compareTo(BigDecimal.ZERO) < 0 || value.compareTo(BigDecimal.ONE) > 0)) {
			return Result.error("Violated normalized constraint: " + value + " is not normalized, must be in range [0.0, 1.0]");
		}
		
		int scale = value.scale();
		if (this.minScale.isPresent() && this.maxScale.isPresent()) {
			if (this.minScale.getAsInt() == this.maxScale.getAsInt()) {
				if (scale != this.minScale.getAsInt()) {
					return Result.error("Violated scale constraint: " + value + " has scale " + scale + ", expected " + this.minScale.getAsInt());
				}
			} else {
				if (scale < this.minScale.getAsInt() || scale > this.maxScale.getAsInt()) {
					return Result.error("Violated scale range constraint: " + value + " has scale " + scale + ", expected range [" + this.minScale.getAsInt() + ", " + this.maxScale.getAsInt() + "]");
				}
			}
		} else {
			if (this.minScale.isPresent() && scale < this.minScale.getAsInt()) {
				return Result.error("Violated minimum scale constraint: " + value + " has scale " + scale + ", minimum required is " + this.minScale.getAsInt());
			}
			if (this.maxScale.isPresent() && scale > this.maxScale.getAsInt()) {
				return Result.error("Violated maximum scale constraint: " + value + " has scale " + scale + ", maximum allowed is " + this.maxScale.getAsInt());
			}
		}
		
		int precision = value.precision();
		if (this.minPrecision.isPresent() && this.maxPrecision.isPresent()) {
			if (this.minPrecision.getAsInt() == this.maxPrecision.getAsInt()) {
				if (precision != this.minPrecision.getAsInt()) {
					return Result.error("Violated precision constraint: " + value + " has precision " + precision + ", expected " + this.minPrecision.getAsInt());
				}
			} else {
				if (precision < this.minPrecision.getAsInt() || precision > this.maxPrecision.getAsInt()) {
					return Result.error("Violated precision range constraint: " + value + " has precision " + precision + ", expected range [" + this.minPrecision.getAsInt() + ", " + this.maxPrecision.getAsInt() + "]");
				}
			}
		} else {
			if (this.minPrecision.isPresent() && precision < this.minPrecision.getAsInt()) {
				return Result.error("Violated minimum precision constraint: " + value + " has precision " + precision + ", minimum required is " + this.minPrecision.getAsInt());
			}
			if (this.maxPrecision.isPresent() && precision > this.maxPrecision.getAsInt()) {
				return Result.error("Violated maximum precision constraint: " + value + " has precision " + precision + ", maximum allowed is " + this.maxPrecision.getAsInt());
			}
		}
		
		return Result.success();
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "BigDecimalConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.numericConfig.ifPresent(config -> config.appendConstraints(constraints));
		this.integral.ifPresent(_ -> constraints.add("integral"));
		this.normalized.ifPresent(_ -> constraints.add("normalized"));
		this.minScale.ifPresent(scale -> constraints.add("minScale=" + scale));
		this.maxScale.ifPresent(scale -> constraints.add("maxScale=" + scale));
		this.minPrecision.ifPresent(precision -> constraints.add("minPrecision=" + precision));
		this.maxPrecision.ifPresent(precision -> constraints.add("maxPrecision=" + precision));
		return "BigDecimalConstraintConfig[" + String.join(",", constraints) + "]";
	}
}
