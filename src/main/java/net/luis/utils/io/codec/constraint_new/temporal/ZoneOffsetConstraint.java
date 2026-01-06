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

package net.luis.utils.io.codec.constraint_new.temporal;

import net.luis.utils.io.codec.constraint_new.SignedConstraint;
import net.luis.utils.io.codec.constraint_new.builder.NumericConstraintBuilder;
import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * Constraint interface for ZoneOffset types that provides offset validation operations.<br>
 * <p>
 *     This interface extends {@link SignedConstraint} with methods for constraining zone offsets
 *     based on their hour component and special values like UTC.<br>
 *     It allows validation of time zone offsets which can be positive (east of UTC) or negative (west of UTC).
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface ZoneOffsetConstraint<T, C> extends SignedConstraint<T, C> {
	
	/**
	 * Applies a UTC offset constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone offsets are UTC (zero offset).<br>
	 *     This is a convenience method equivalent to {@code zero()}.
	 * </p>
	 *
	 * @return A new type with the applied UTC constraint
	 * @see #zero()
	 */
	default @NonNull C utc() {
		return this.zero();
	}
	
	/**
	 * Applies a hours constraint to the zone offset using a builder.<br>
	 * <p>
	 *     The returned type will validate that the hour component of zone offsets matches
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the hours constraint using a numeric constraint builder
	 * @return A new type with the applied hours constraint
	 * @throws NullPointerException If the builder is null
	 */
	@NonNull C hours(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
}
