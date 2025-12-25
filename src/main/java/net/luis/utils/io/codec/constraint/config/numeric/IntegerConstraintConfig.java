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

import net.luis.utils.io.codec.constraint.numeric.IntegerConstraint;
import net.luis.utils.math.NumberType;
import net.luis.utils.util.result.Result;
import org.jspecify.annotations.NonNull;

import java.math.BigInteger;
import java.util.*;
import java.util.function.UnaryOperator;

/**
 * Configuration class for integer (whole number) numeric constraints.<br>
 * <p>
 *     This record holds constraint fields specific to integer numeric values, including numeric constraints,
 *     even/odd validation, divisibility checks, and power-of-base validation.<br>
 *     These constraints are applicable to all integer types (byte, short, int, long).
 * </p>
 * <p>
 *     Constraints are validated using the {@link #matches(NumberType, Number)} method, which checks whether
 *     a given integer value satisfies all configured requirements.<br>
 *     Constraints are immutable once created, but new configurations can be derived using the
 *     various {@code with*} methods.
 * </p>
 *
 * @see IntegerConstraint
 * @see NumericConstraintConfig
 *
 * @author Luis-St
 *
 * @param numericConfig The optional numeric constraint configuration (empty if unconstrained)
 * @param even The optional even/odd constraint (empty if unconstrained, true for even, false for odd)
 * @param divisor The optional divisor constraint (empty if unconstrained)
 * @param powerBase The optional power-of-base constraint (empty if unconstrained)
 * @param <T> The integer numeric type being constrained
 */
public record IntegerConstraintConfig<T extends Number & Comparable<T>>(
	@NonNull Optional<NumericConstraintConfig<T>> numericConfig,
	@NonNull Optional<Boolean> even,
	@NonNull OptionalLong divisor,
	@NonNull OptionalInt powerBase

) {

	/**
	 * A predefined unconstrained configuration with no constraints.<br>
	 * <p>
	 *     This constant represents a configuration where all integer values are valid.<br>
	 *     It can be used as a starting point to build constrained configurations using
	 *     the various {@code with*} methods.
	 * </p>
	 *
	 * @see #unconstrained()
	 * @see #withNumericConfig(UnaryOperator)
	 */
	private static final IntegerConstraintConfig<? extends Number> UNCONSTRAINED = new IntegerConstraintConfig<>(
		Optional.empty(),
		Optional.empty(),
		OptionalLong.empty(),
		OptionalInt.empty()
	);

	/**
	 * Constructs a new integer constraint configuration with the specified constraints.<br>
	 *
	 * @param numericConfig The optional numeric constraint configuration (empty if unconstrained)
	 * @param even The optional even/odd constraint (empty if unconstrained, true for even, false for odd)
	 * @param divisor The optional divisor constraint (empty if unconstrained)
	 * @param powerBase The optional power-of-base constraint (empty if unconstrained)
	 * @throws NullPointerException If any parameter is null
	 * @throws IllegalArgumentException If the divisor is zero when present, or if the power base is less than 2 when present
	 */
	public IntegerConstraintConfig {
		Objects.requireNonNull(numericConfig, "Numeric configuration must not be null");
		Objects.requireNonNull(even, "Even constraint must not be null");
		Objects.requireNonNull(divisor, "Divisor constraint must not be null");
		Objects.requireNonNull(powerBase, "Power base constraint must not be null");
		
		if (divisor.isPresent() && divisor.getAsLong() == 0) {
			throw new IllegalArgumentException("Divisor must not be zero when present");
		}
		if (powerBase.isPresent() && powerBase.getAsInt() < 2) {
			throw new IllegalArgumentException("Power base must be at least 2 when present");
		}
	}

	/**
	 * Returns an unconstrained configuration for the specified type.<br>
	 * <p>
	 *     This method provides a type-safe way to get an unconstrained configuration without raw type warnings.
	 * </p>
	 *
	 * @return An unconstrained integer constraint configuration
	 * @param <T> The integer numeric type
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Number & Comparable<T>> @NonNull IntegerConstraintConfig<T> unconstrained() {
		return (IntegerConstraintConfig<T>) UNCONSTRAINED;
	}

	/**
	 * Checks if a primitive long value is a power of the specified base.<br>
	 * <p>
	 *     This method determines whether the given value can be expressed as base^n for some non-negative integer n.<br>
	 *     Special cases: 1 is considered a power of any base (base^0 = 1), and values less than 1 are not powers of any base.
	 * </p>
	 *
	 * @param value The value to check
	 * @param base The base to check against
	 * @return True if the value is a power of the base, false otherwise
	 */
	private static boolean isPowerOf(long value, int base) {
		if (value < 1) {
			return false;
		}
		if (value == 1) {
			return true;
		}
		
		while (value > 1) {
			if (value % base != 0) {
				return false;
			}
			value /= base;
		}
		return true;
	}

	/**
	 * Checks if a big integer value is a power of the specified base.<br>
	 * <p>
	 *     This method determines whether the given value can be expressed as base^n for some non-negative integer n.<br>
	 *     Special cases: 1 is considered a power of any base (base^0 = 1), and values less than 1 are not powers of any base.
	 * </p>
	 *
	 * @param value The value to check
	 * @param base The base to check against
	 * @return True if the value is a power of the base, false otherwise
	 */
	private static boolean isPowerOf(@NonNull BigInteger value, int base) {
		if (value.compareTo(BigInteger.ONE) < 0) {
			return false;
		}
		if (value.equals(BigInteger.ONE)) {
			return true;
		}
		
		BigInteger baseBigInt = BigInteger.valueOf(base);
		BigInteger current = value;
		while (current.compareTo(BigInteger.ONE) > 0) {
			BigInteger[] divMod = current.divideAndRemainder(baseBigInt);
			if (!divMod[1].equals(BigInteger.ZERO)) {
				return false;
			}
			current = divMod[0];
		}
		return true;
	}

	/**
	 * Checks if the configuration is unconstrained (no constraints set).<br>
	 *
	 * @return True if unconstrained, false otherwise
	 */
	public boolean isUnconstrained() {
		return this == UNCONSTRAINED || (this.numericConfig.isEmpty() && this.even.isEmpty() && this.divisor.isEmpty() && this.powerBase.isEmpty());
	}

	/**
	 * Creates a new integer constraint configuration with the modified numeric configuration.<br>
	 * <p>
	 *     This method allows customizing the numeric constraints (min, max, equals) applied to integer values.<br>
	 *     If no numeric configuration is currently set, a new unconstrained configuration is created and modified.<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param numericConfigModifier A function that modifies the numeric configuration
	 * @return A new integer constraint configuration with the modified numeric configuration
	 * @throws NullPointerException If the modifier is null
	 * @see NumericConstraintConfig
	 */
	public @NonNull IntegerConstraintConfig<T> withNumericConfig(@NonNull UnaryOperator<NumericConstraintConfig<T>> numericConfigModifier) {
		Objects.requireNonNull(numericConfigModifier, "Numeric configuration modifier must not be null");
		
		return new IntegerConstraintConfig<>(Optional.of(numericConfigModifier.apply(this.numericConfig.orElse(NumericConstraintConfig.unconstrained()))), this.even, this.divisor, this.powerBase);
	}

	/**
	 * Creates a new integer constraint configuration with an even constraint.<br>
	 * <p>
	 *     The even constraint ensures that integer values are even (divisible by 2).<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @return A new integer constraint configuration with the even constraint applied
	 * @see #withOdd()
	 */
	public @NonNull IntegerConstraintConfig<T> withEven() {
		return new IntegerConstraintConfig<>(this.numericConfig, Optional.of(true), this.divisor, this.powerBase);
	}

	/**
	 * Creates a new integer constraint configuration with an odd constraint.<br>
	 * <p>
	 *     The odd constraint ensures that integer values are odd (not divisible by 2).<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @return A new integer constraint configuration with the odd constraint applied
	 * @see #withEven()
	 */
	public @NonNull IntegerConstraintConfig<T> withOdd() {
		return new IntegerConstraintConfig<>(this.numericConfig, Optional.of(false), this.divisor, this.powerBase);
	}

	/**
	 * Creates a new integer constraint configuration with a divisor constraint.<br>
	 * <p>
	 *     The divisor constraint ensures that integer values are divisible by the specified divisor.<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param divisor The divisor that values must be divisible by
	 * @return A new integer constraint configuration with the divisor constraint applied
	 * @throws IllegalArgumentException If the divisor is zero
	 */
	public @NonNull IntegerConstraintConfig<T> withDivisor(long divisor) {
		return new IntegerConstraintConfig<>(this.numericConfig, this.even, OptionalLong.of(divisor), this.powerBase);
	}

	/**
	 * Creates a new integer constraint configuration with a power-of-base constraint.<br>
	 * <p>
	 *     The power-of-base constraint ensures that integer values are powers of the specified base.<br>
	 *     For example, with base 2, valid values are 1, 2, 4, 8, 16, etc.<br>
	 *     All other constraints from the current configuration are preserved.
	 * </p>
	 *
	 * @param base The base that values must be a power of
	 * @return A new integer constraint configuration with the power-of-base constraint applied
	 * @throws IllegalArgumentException If the base is less than 2
	 */
	public @NonNull IntegerConstraintConfig<T> withPowerBase(int base) {
		return new IntegerConstraintConfig<>(this.numericConfig, this.even, this.divisor, OptionalInt.of(base));
	}

	/**
	 * Validates whether the given integer value matches all constraints defined in this configuration.<br>
	 * <p>
	 *     This method checks if the provided value satisfies all configured requirements including
	 *     numeric constraints, even/odd, divisibility, and power-of-base.<br>
	 *     If the value violates any constraint, an error result is returned with a descriptive message
	 *     indicating which constraint was violated.
	 * </p>
	 * <p>
	 *     If no constraints are set (unconstrained), this method always returns a success result.
	 * </p>
	 *
	 * @param type The number type being validated (must be an integer type)
	 * @param value The integer value to validate
	 * @return A success result if the value meets all constraints, or an error result with a descriptive message indicating the violation
	 * @throws NullPointerException If the type or value is null
	 * @throws IllegalArgumentException If the type is not an integer type or if it is a big number type
	 * @see Result
	 */
	public @NonNull Result<Void> matches(@NonNull NumberType type, @NonNull T value) {
		Objects.requireNonNull(type, "Number type must not be null");
		Objects.requireNonNull(value, "Value to match must not be null");
		if (type.isFloatingPoint()) {
			throw new IllegalArgumentException("Number type " + type + " is not an integer type");
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

		if (type == NumberType.BIG_INTEGER) {
			return this.matchesBigInteger((BigInteger) value);
		} else {
			return this.matchesPrimitive(value);
		}
	}

	/**
	 * Validates primitive integer-specific constraints against the given value.<br>
	 * <p>
	 *     This method checks even/odd, divisibility, and power-of-base constraints for primitive integer values.<br>
	 *     It uses long-based arithmetic for efficiency.
	 * </p>
	 *
	 * @param value The primitive integer value to validate
	 * @return A success result if the value meets all constraints, or an error result with a descriptive message
	 */
	private @NonNull Result<Void> matchesPrimitive(@NonNull T value) {
		if (this.even.isPresent()) {
			boolean isEven = value.longValue() % 2 == 0;
			if (this.even.get() && !isEven) {
				return Result.error("Violated even constraint: " + value + " is not even");
			}
			if (!this.even.get() && isEven) {
				return Result.error("Violated odd constraint: " + value + " is not odd");
			}
		}

		if (this.divisor.isPresent() && value.longValue() % this.divisor.getAsLong() != 0) {
			return Result.error("Violated divisibility constraint: " + value + " is not divisible by " + this.divisor.getAsLong());
		}

		if (this.powerBase.isPresent()) {
			int base = this.powerBase.getAsInt();
			if (!isPowerOf(value.longValue(), base)) {
				return Result.error("Violated power-of-" + base + " constraint: " + value + " is not a power of " + base);
			}
		}
		return Result.success();
	}

	/**
	 * Validates big integer specific constraints against the given value.<br>
	 * <p>
	 *     This method checks even/odd, divisibility, and power-of-base constraints for big integer values.<br>
	 *     It uses big integer arithmetic to handle arbitrarily large numbers.
	 * </p>
	 *
	 * @param value The big integer value to validate
	 * @return A success result if the value meets all constraints, or an error result with a descriptive message
	 */
	private @NonNull Result<Void> matchesBigInteger(@NonNull BigInteger value) {
		if (this.even.isPresent()) {
			boolean isEven = value.mod(BigInteger.TWO).equals(BigInteger.ZERO);
			if (this.even.get() && !isEven) {
				return Result.error("Violated even constraint: " + value + " is not even");
			}
			if (!this.even.get() && isEven) {
				return Result.error("Violated odd constraint: " + value + " is not odd");
			}
		}

		if (this.divisor.isPresent()) {
			BigInteger divisor = BigInteger.valueOf(this.divisor.getAsLong());
			if (!value.mod(divisor).equals(BigInteger.ZERO)) {
				return Result.error("Violated divisibility constraint: " + value + " is not divisible by " + this.divisor.getAsLong());
			}
		}

		if (this.powerBase.isPresent()) {
			int base = this.powerBase.getAsInt();
			if (!isPowerOf(value, base)) {
				return Result.error("Violated power-of-" + base + " constraint: " + value + " is not a power of " + base);
			}
		}
		return Result.success();
	}
	
	@Override
	public @NonNull String toString() {
		if (this.isUnconstrained()) {
			return "IntegerConstraintConfig[unconstrained]";
		}
		
		List<String> constraints = new ArrayList<>();
		this.numericConfig.ifPresent(config -> config.appendConstraints(constraints));
		this.even.ifPresent(v -> constraints.add(v ? "even" : "odd"));
		this.divisor.ifPresent(v -> constraints.add("divisibleBy=" + v));
		this.powerBase.ifPresent(v -> constraints.add("powerOf=" + v));
		return "IntegerConstraintConfig[" + String.join(",", constraints) + "]";
	}
}
