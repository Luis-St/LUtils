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

package net.luis.utils.io.codec.constraint_new.numeric;

import net.luis.utils.io.codec.constraint_new.ApplicableConstraint;
import net.luis.utils.io.codec.constraint_new.Constraint;
import net.luis.utils.io.codec.constraint_new.config.numeric.IntegerConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for integer types that provides integer-specific validation operations.<br>
 * <p>
 *     This interface extends {@link NumericConstraint} with methods specific to integer values.<br>
 *     It provides methods for validating divisibility, parity (even/odd), and power-of-base constraints.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The numeric type (Byte, Short, Integer, Long, BigInteger)
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
@FunctionalInterface
public interface IntegerConstraint<T extends Number & Comparable<T>, C> extends ApplicableConstraint<IntegerConstraintConfig<T>, C>, NumericConstraint<T, C> {
	
	@Override
	@NonNull C apply(@NonNull UnaryOperator<IntegerConstraintConfig<T>> configModifier);
	
	/**
	 * Applies an even number constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that integer values are even (divisible by 2).
	 * </p>
	 *
	 * @return A new type with the applied even constraint
	 * @see #odd()
	 * @see #divisibleBy(long)
	 */
	default @NonNull C even() {
		return this.apply(IntegerConstraintConfig::withEven);
	}
	
	/**
	 * Applies an odd number constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that integer values are odd (not divisible by 2).
	 * </p>
	 *
	 * @return A new type with the applied odd constraint
	 * @see #even()
	 */
	default @NonNull C odd() {
		return this.apply(IntegerConstraintConfig::withOdd);
	}
	
	/**
	 * Applies a divisibility constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that integer values are divisible by the specified divisor.
	 * </p>
	 *
	 * @param divisor The divisor that values must be divisible by
	 * @return A new type with the applied divisibility constraint
	 * @throws IllegalArgumentException If the divisor is zero
	 * @see #even()
	 */
	default @NonNull C divisibleBy(long divisor) {
		return this.apply(config -> config.withDivisibleBy(divisor));
	}
	
	/**
	 * Applies a power-of-two constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that integer values are powers of 2 (1, 2, 4, 8, 16, ...).<br>
	 *     This is a convenience method equivalent to {@code powerOf(2)}.
	 * </p>
	 *
	 * @return A new type with the applied power-of-two constraint
	 * @see #powerOf(int)
	 */
	default @NonNull C powerOfTwo() {
		return this.powerOf(2);
	}
	
	/**
	 * Applies a power-of-base constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that integer values are powers of the specified base.<br>
	 *     For example, {@code powerOf(3)} would accept 1, 3, 9, 27, 81, etc.
	 * </p>
	 *
	 * @param base The base that values must be a power of
	 * @return A new type with the applied power-of-base constraint
	 * @throws IllegalArgumentException If the base is less than 2
	 * @see #powerOfTwo()
	 */
	default @NonNull C powerOf(int base) {
		return this.apply(config -> config.withPowerOf(base));
	}
	
	// BaseConstraint methods
	@Override
	default @NonNull C equalTo(@NonNull T value) {
		return this.apply(config -> config.withEqualTo(value));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull T value) {
		return this.apply(config -> config.withNotEqualTo(value));
	}
	
	@Override
	default @NonNull C in(@NonNull Collection<T> values) {
		return this.apply(config -> config.withIn(values));
	}
	
	@Override
	default @NonNull C notIn(@NonNull Collection<T> values) {
		return this.apply(config -> config.withNotIn(values));
	}
	
	@Override
	default @NonNull C custom(@NonNull Constraint<T> constraint) {
		return this.apply(config -> config.withCustom(constraint));
	}
	
	// ComparableConstraint methods
	@Override
	default @NonNull C greaterThan(@NonNull T value) {
		return this.apply(config -> config.withGreaterThan(value));
	}
	
	@Override
	default @NonNull C greaterThanOrEqual(@NonNull T value) {
		return this.apply(config -> config.withGreaterThanOrEqual(value));
	}
	
	@Override
	default @NonNull C lessThan(@NonNull T value) {
		return this.apply(config -> config.withLessThan(value));
	}
	
	@Override
	default @NonNull C lessThanOrEqual(@NonNull T value) {
		return this.apply(config -> config.withLessThanOrEqual(value));
	}
	
	@Override
	default @NonNull C between(@NonNull T min, @NonNull T max) {
		return this.apply(config -> config.withBetween(min, max));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull T min, @NonNull T max) {
		return this.apply(config -> config.withBetweenOrEqual(min, max));
	}
	
	// SignedConstraint methods
	@Override
	default @NonNull C positive() {
		return this.apply(IntegerConstraintConfig::withPositive);
	}
	
	@Override
	default @NonNull C nonPositive() {
		return this.apply(IntegerConstraintConfig::withNonPositive);
	}
	
	@Override
	default @NonNull C negative() {
		return this.apply(IntegerConstraintConfig::withNegative);
	}
	
	@Override
	default @NonNull C nonNegative() {
		return this.apply(IntegerConstraintConfig::withNonNegative);
	}
	
	@Override
	default @NonNull C zero() {
		return this.apply(IntegerConstraintConfig::withZero);
	}
	
	@Override
	default @NonNull C nonZero() {
		return this.apply(IntegerConstraintConfig::withNonZero);
	}
	
	// NumericConstraint methods
	@Override
	default @NonNull C percentage() {
		return this.apply(IntegerConstraintConfig::withPercentage);
	}
}
