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

package net.luis.utils.io.codec.constraint.numeric;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.numeric.IntegerConstraintConfig;
import net.luis.utils.io.codec.constraint.core.NumberProvider;
import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * A constraint interface for integer (whole number) numeric value validation.<br>
 * <p>
 *     This interface extends {@link NumericConstraint} with additional constraints specific to integer numbers,
 *     such as even/odd checks, divisibility tests, and power-of-n validations.
 * </p>
 * <p>
 *     All methods inherited from {@link NumericConstraint} and {@link net.luis.utils.io.codec.constraint.ComparableConstraint}
 *     have default implementations and do not need to be overridden.
 * </p>
 *
 * @see NumericConstraint
 *
 * @author Luis-St
 *
 * @param <T> The integer numeric type being constrained
 * @param <C> The codec type
 */
public interface IntegerConstraint<T extends Number & Comparable<T>, C extends Codec<T>> extends NumericConstraint<T, C, IntegerConstraintConfig<T>> {

	@Override
	@NonNull C applyConstraint(@NonNull UnaryOperator<IntegerConstraintConfig<T>> configModifier);

	@Override
	@NonNull NumberProvider<T> provider();
	
	@Override
	default @NonNull C greaterThan(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMin(value, false)));
	}
	
	@Override
	default @NonNull C greaterThanOrEqual(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMin(value, true)));
	}
	
	@Override
	default @NonNull C lessThan(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMax(value, false)));
	}
	
	@Override
	default @NonNull C lessThanOrEqual(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withMax(value, true)));
	}
	
	@Override
	default @NonNull C between(@NonNull T min, @NonNull T max) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withRange(min, max, false)));
	}
	
	@Override
	default @NonNull C betweenOrEqual(@NonNull T min, @NonNull T max) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withRange(min, max, true)));
	}
	
	@Override
	default @NonNull C equalTo(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withEquals(value, false)));
	}
	
	@Override
	default @NonNull C notEqualTo(@NonNull T value) {
		return this.applyConstraint(config -> config.withNumericConfig(numericConfig -> numericConfig.withEquals(value, true)));
	}
	
	/**
	 * Applies an even number constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that integer values are even (divisible by 2).
	 * </p>
	 *
	 * @return A new codec with the applied even constraint
	 * @see #odd()
	 * @see #divisibleBy(long)
	 */
	default @NonNull C even() {
		return this.applyConstraint(IntegerConstraintConfig::withEven);
	}

	/**
	 * Applies an odd number constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that integer values are odd (not divisible by 2).
	 * </p>
	 *
	 * @return A new codec with the applied odd constraint
	 * @see #even()
	 */
	default @NonNull C odd() {
		return this.applyConstraint(IntegerConstraintConfig::withOdd);
	}

	/**
	 * Applies a divisibility constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that integer values are divisible by the specified divisor.
	 * </p>
	 *
	 * @param divisor The divisor that values must be divisible by
	 * @return A new codec with the applied divisibility constraint
	 * @throws IllegalArgumentException If the divisor is zero
	 * @see #even()
	 */
	default @NonNull C divisibleBy(long divisor) {
		return this.applyConstraint(config -> config.withDivisor(divisor));
	}
	
	/**
	 * Applies a power-of-two constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that integer values are powers of 2 (1, 2, 4, 8, 16, ...).<br>
	 *     This is a convenience method equivalent to {@code powerOf(2)}.
	 * </p>
	 *
	 * @return A new codec with the applied power-of-two constraint
	 * @see #powerOf(int)
	 */
	default @NonNull C powerOfTwo() {
		return this.powerOf(2);
	}
	
	/**
	 * Applies a power-of-base constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that integer values are powers of the specified base.<br>
	 *     For example, {@code powerOf(3)} would accept 1, 3, 9, 27, 81, etc.
	 * </p>
	 *
	 * @param base The base that values must be a power of
	 * @return A new codec with the applied power-of-base constraint
	 * @throws IllegalArgumentException If the base is less than 2
	 * @see #powerOfTwo()
	 */
	default @NonNull C powerOf(int base) {
		return this.applyConstraint(config -> config.withPowerBase(base));
	}
}
