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

package net.luis.utils.io.codec.constraint_new.network;

import net.luis.utils.io.codec.constraint_new.BaseConstraint;
import net.luis.utils.io.codec.constraint_new.builder.EnumConstraintBuilder;
import net.luis.utils.io.codec.constraint_new.core.PortRange;
import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * Constraint interface for network port types that provides port validation operations.<br>
 * <p>
 *     This interface extends {@link BaseConstraint} with methods for constraining port numbers
 *     based on ranges and port types (system, registered, dynamic).<br>
 *     It allows validation of port numbers according to IANA port number ranges.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface PortConstraint<T, C> extends BaseConstraint<T, C> {
	
	/**
	 * Applies a port range constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that port numbers are within the specified range (inclusive).
	 * </p>
	 *
	 * @param min The minimum port number (inclusive)
	 * @param max The maximum port number (inclusive)
	 * @return A new type with the applied range constraint
	 * @throws IllegalArgumentException If min is greater than max or if values are outside valid port range (0-65535)
	 * @see #notInRange(int, int)
	 */
	@NonNull C inRange(int min, int max);
	
	/**
	 * Applies a negative port range constraint to the type.<br>
	 * <p>
	 *     The returned type will validate that port numbers are not within the specified range.
	 * </p>
	 *
	 * @param min The minimum port number of the excluded range (inclusive)
	 * @param max The maximum port number of the excluded range (inclusive)
	 * @return A new type with the applied negative range constraint
	 * @throws IllegalArgumentException If min is greater than max or if values are outside valid port range (0-65535)
	 * @see #inRange(int, int)
	 */
	@NonNull C notInRange(int min, int max);
	
	/**
	 * Applies a port type constraint to the type using a builder.<br>
	 * <p>
	 *     The returned type will validate that port numbers belong to the port ranges
	 *     defined by the builder (system ports 0-1023, registered ports 1024-49151, or dynamic ports 49152-65535).
	 * </p>
	 *
	 * @param builder A function that configures the port type constraint using an enum constraint builder
	 * @return A new type with the applied port type constraint
	 * @throws NullPointerException If the builder is null
	 * @see PortRange
	 */
	@NonNull C type(@NonNull UnaryOperator<EnumConstraintBuilder<PortRange>> builder);
}
