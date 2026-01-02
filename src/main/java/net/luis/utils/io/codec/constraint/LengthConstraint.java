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

package net.luis.utils.io.codec.constraint;

import net.luis.utils.io.codec.Codec;
import net.luis.utils.io.codec.constraint.config.LengthConstraintConfig;
import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * A constraint interface for length-based validation of strings, arrays, and other sequential types.<br>
 * <p>
 *     This interface provides a set of default methods to apply common length constraints to codecs.<br>
 *     The constraints are applied to values during encoding and decoding operations, ensuring that only values
 *     meeting the length requirements are successfully processed.
 * </p>
 * <p>
 *     Implementations of this interface must provide the {@link #applyConstraint(UnaryOperator)} method,
 *     which creates a new codec instance with the given length constraint configuration applied.
 * </p>
 *
 * @see LengthConstraintConfig
 * @see CodecConstraint
 *
 * @author Luis-St
 *
 * @param <T> The type being constrained
 * @param <C> The codec type
 */
@FunctionalInterface
public interface LengthConstraint<T, C extends Codec<T>> extends CodecConstraint<C, LengthConstraintConfig> {
	
	@Override
	@NonNull C applyConstraint(@NonNull UnaryOperator<LengthConstraintConfig> configModifier);
	
	/**
	 * Applies a minimum length constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values have a length greater than or equal to the specified minimum.<br>
	 *     Any attempt to encode or decode a value with fewer characters/elements will result in an error.
	 * </p>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @return A new codec with the applied minimum length constraint
	 * @throws IllegalArgumentException If the minimum length is negative
	 * @see #maxLength(int)
	 * @see #lengthBetween(int, int)
	 */
	default @NonNull C minLength(int minLength) {
		return this.applyConstraint(config -> config.withMinLength(minLength));
	}
	
	/**
	 * Applies a maximum length constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values have a length less than or equal to the specified maximum.<br>
	 *     Any attempt to encode or decode a value with more characters/elements will result in an error.
	 * </p>
	 *
	 * @param maxLength The maximum length (inclusive)
	 * @return A new codec with the applied maximum length constraint
	 * @throws IllegalArgumentException If the maximum length is negative
	 * @see #minLength(int)
	 * @see #lengthBetween(int, int)
	 */
	default @NonNull C maxLength(int maxLength) {
		return this.applyConstraint(config -> config.withMaxLength(maxLength));
	}
	
	/**
	 * Applies an exact length constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values have exactly the specified length.<br>
	 *     Any attempt to encode or decode a value with a different number of characters/elements will result in an error.
	 * </p>
	 *
	 * @param exactLength The exact length required
	 * @return A new codec with the applied exact length constraint
	 * @throws IllegalArgumentException If the exact length is negative
	 * @see #lengthBetween(int, int)
	 */
	default @NonNull C exactLength(int exactLength) {
		return this.applyConstraint(config -> config.withLength(exactLength, exactLength));
	}
	
	/**
	 * Applies a length range constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values have a length within the specified range (inclusive).<br>
	 *     Any attempt to encode or decode a value outside this range will result in an error.
	 * </p>
	 *
	 * @param minLength The minimum length (inclusive)
	 * @param maxLength The maximum length (inclusive)
	 * @return A new codec with the applied length range constraint
	 * @throws IllegalArgumentException If the minimum or maximum length is negative, or if the maximum length is less than the minimum length
	 * @see #minLength(int)
	 * @see #maxLength(int)
	 */
	default @NonNull C lengthBetween(int minLength, int maxLength) {
		return this.applyConstraint(config -> config.withLength(minLength, maxLength));
	}
	
	/**
	 * Applies an empty length constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are empty (length of 0).<br>
	 *     This is a convenience method equivalent to {@code exactLength(0)}.
	 * </p>
	 *
	 * @return A new codec with the applied empty length constraint
	 * @see #exactLength(int)
	 * @see #notEmpty()
	 */
	default @NonNull C empty() {
		return this.exactLength(0);
	}
	
	/**
	 * Applies a non-empty length constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values contain at least one character/element (length &gt;= 1).<br>
	 *     This is a convenience method equivalent to {@code minLength(1)}.
	 * </p>
	 *
	 * @return A new codec with the applied non-empty length constraint
	 * @see #minLength(int)
	 * @see #empty()
	 */
	default @NonNull C notEmpty() {
		return this.minLength(1);
	}
}
