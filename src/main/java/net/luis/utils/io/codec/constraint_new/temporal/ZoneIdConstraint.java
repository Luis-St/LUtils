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

import net.luis.utils.io.codec.constraint_new.BaseConstraint;
import net.luis.utils.io.codec.constraint_new.builder.StringConstraintBuilder;
import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * Constraint interface for ZoneId types that provides time zone validation operations.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for constraining time zones
 *     based on their type (region-based, offset-based), normalization, and availability.<br>
 *     It allows validation of zone identifiers according to various criteria.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface ZoneIdConstraint<T, C> extends BaseConstraint<T, C> {
	
	/**
	 * Applies a normalized zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs are in their normalized form.
	 * </p>
	 *
	 * @return A new type with the applied normalized constraint
	 */
	@NonNull C normalized();
	
	/**
	 * Applies a region-based zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs are region-based (e.g., "America/New_York", "Europe/London")
	 *     rather than offset-based.
	 * </p>
	 *
	 * @return A new type with the applied region-based constraint
	 * @see #offsetBased()
	 */
	@NonNull C regionBased();
	
	/**
	 * Applies an offset-based zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs are offset-based (e.g., "+02:00", "-05:00")
	 *     rather than region-based.
	 * </p>
	 *
	 * @return A new type with the applied offset-based constraint
	 * @see #regionBased()
	 * @see #fixedOffset()
	 */
	@NonNull C offsetBased();
	
	/**
	 * Applies a fixed offset zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs represent fixed offsets that do not observe daylight saving time.
	 * </p>
	 *
	 * @return A new type with the applied fixed offset constraint
	 * @see #offsetBased()
	 */
	@NonNull C fixedOffset();
	
	/**
	 * Applies a UTC zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs represent UTC (Coordinated Universal Time).
	 * </p>
	 *
	 * @return A new type with the applied UTC constraint
	 */
	@NonNull C utc();
	
	/**
	 * Applies a system default zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs match the system's default time zone.
	 * </p>
	 *
	 * @return A new type with the applied system default constraint
	 */
	@NonNull C systemDefault();
	
	/**
	 * Applies an available zone constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that zone IDs are among the available zone IDs known to the system.
	 * </p>
	 *
	 * @return A new type with the applied available constraint
	 */
	@NonNull C available();
	
	/**
	 * Applies a region constraint to the zone using a builder.<br>
	 * <p>
	 *     The returned type will validate that the region part of region-based zone IDs matches
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the region constraint using a string constraint builder
	 * @return A new type with the applied region constraint
	 * @throws NullPointerException If the builder is null
	 */
	@NonNull C region(@NonNull UnaryOperator<StringConstraintBuilder> builder);
}
