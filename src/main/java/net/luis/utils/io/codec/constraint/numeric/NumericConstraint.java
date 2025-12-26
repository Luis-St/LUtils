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
import net.luis.utils.io.codec.constraint.CodecConstraint;
import net.luis.utils.io.codec.constraint.ComparableConstraint;
import net.luis.utils.io.codec.constraint.core.NumberProvider;
import org.jspecify.annotations.NonNull;

/**
 * A constraint interface for numeric value validation.<br>
 * <p>
 *     This interface provides a set of default methods to apply common numeric constraints to codecs.<br>
 *     The constraints are applied to values during encoding and decoding operations, ensuring that only values
 *     meeting the numeric requirements are successfully processed.
 * </p>
 * <p>
 *     Implementations of this interface must provide the {@link #provider()} method to supply numeric constants
 *     used by the default constraint implementations.
 * </p>
 *
 * @see NumberProvider
 * @see ComparableConstraint
 *
 * @author Luis-St
 *
 * @param <T> The numeric type being constrained
 * @param <C> The codec type
 * @param <V> The constraint configuration type
 */
public interface NumericConstraint<T extends Number & Comparable<T>, C extends Codec<T>, V> extends CodecConstraint<T, C, V>, ComparableConstraint<T, C> {
	
	/**
	 * Provides numeric constants for the constrained type.<br>
	 * <p>
	 *     This method returns a {@link NumberProvider} that supplies common numeric constants
	 *     (zero, one, hundred) for the specific numeric type being constrained.
	 * </p>
	 *
	 * @return The number provider for this constraint
	 */
	@NonNull NumberProvider<T> provider();
	
	/**
	 * Applies a positive value constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are greater than zero.<br>
	 *     This is a convenience method equivalent to {@code greaterThan(zero)}.
	 * </p>
	 *
	 * @return A new codec with the applied positive constraint
	 * @see #greaterThan(Comparable)
	 * @see #negative()
	 */
	default @NonNull C positive() {
		return this.greaterThan(this.provider().zero());
	}
	
	/**
	 * Applies a negative value constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are less than zero.<br>
	 *     This is a convenience method equivalent to {@code lessThan(zero)}.
	 * </p>
	 *
	 * @return A new codec with the applied negative constraint
	 * @see #lessThan(Comparable)
	 * @see #positive()
	 */
	default @NonNull C negative() {
		return this.lessThan(this.provider().zero());
	}
	
	/**
	 * Applies a non-negative value constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are greater than or equal to zero.<br>
	 *     This is a convenience method equivalent to {@code greaterThanOrEqual(zero)}.
	 * </p>
	 *
	 * @return A new codec with the applied non-negative constraint
	 * @see #greaterThanOrEqual(Comparable)
	 * @see #nonPositive()
	 */
	default @NonNull C nonNegative() {
		return this.greaterThanOrEqual(this.provider().zero());
	}
	
	/**
	 * Applies a non-positive value constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are less than or equal to zero.<br>
	 *     This is a convenience method equivalent to {@code lessThanOrEqual(zero)}.
	 * </p>
	 *
	 * @return A new codec with the applied non-positive constraint
	 * @see #lessThanOrEqual(Comparable)
	 * @see #nonNegative()
	 */
	default @NonNull C nonPositive() {
		return this.lessThanOrEqual(this.provider().zero());
	}
	
	/**
	 * Applies a zero value constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are exactly zero.<br>
	 *     This is a convenience method equivalent to {@code equalTo(zero)}.
	 * </p>
	 *
	 * @return A new codec with the applied zero constraint
	 * @see #equalTo(Comparable)
	 * @see #nonZero()
	 */
	default @NonNull C zero() {
		return this.equalTo(this.provider().zero());
	}
	
	/**
	 * Applies a non-zero value constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are not equal to zero.<br>
	 *     This is a convenience method equivalent to {@code notEqualTo(zero)}.
	 * </p>
	 *
	 * @return A new codec with the applied non-zero constraint
	 * @see #notEqualTo(Comparable)
	 * @see #zero()
	 */
	default @NonNull C nonZero() {
		return this.notEqualTo(this.provider().zero());
	}
	
	/**
	 * Applies a percentage value constraint to the codec.<br>
	 * <p>
	 *     The returned codec will validate that values are between zero and one hundred (inclusive).<br>
	 *     This is a convenience method equivalent to {@code between(zero, hundred)}.
	 * </p>
	 *
	 * @return A new codec with the applied percentage constraint
	 * @see #betweenOrEqual(Comparable, Comparable)
	 */
	default @NonNull C percentage() {
		return this.betweenOrEqual(this.provider().zero(), this.provider().hundred());
	}
}
