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

import net.luis.utils.io.codec.constraint_new.builder.NumericConstraintBuilder;
import org.jspecify.annotations.NonNull;

import java.util.function.UnaryOperator;

/**
 * Constraint interface for temporal types that provides time field validation operations.<br>
 * <p>
 *     This interface provides methods for constraining individual time fields such as
 *     hour, minute, second, millisecond, and nanosecond.<br>
 *     It allows fine-grained validation of time components within temporal values.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface TimeFieldConstraint<T, C> {
	
	/**
	 * Applies a constraint to the hour field of the temporal value.<br>
	 * <p>
	 *     The hour field represents the hour of day in the range 0-23.<br>
	 *     The constraint is defined using a builder function that configures the allowed hour values.
	 * </p>
	 *
	 * @param builder A function that configures the hour constraint using a builder
	 * @return A new type with the applied hour constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #minute(UnaryOperator)
	 * @see #second(UnaryOperator)
	 * @see #millisecond(UnaryOperator)
	 * @see #nanosecond(UnaryOperator)
	 */
	@NonNull C hour(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
	
	/**
	 * Applies a constraint to the minute field of the temporal value.<br>
	 * <p>
	 *     The minute field represents the minute of hour in the range 0-59.<br>
	 *     The constraint is defined using a builder function that configures the allowed minute values.
	 * </p>
	 *
	 * @param builder A function that configures the minute constraint using a builder
	 * @return A new type with the applied minute constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #hour(UnaryOperator)
	 * @see #second(UnaryOperator)
	 * @see #millisecond(UnaryOperator)
	 * @see #nanosecond(UnaryOperator)
	 */
	@NonNull C minute(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
	
	/**
	 * Applies a constraint to the second field of the temporal value.<br>
	 * <p>
	 *     The second field represents the second of minute in the range 0-59.<br>
	 *     The constraint is defined using a builder function that configures the allowed second values.
	 * </p>
	 *
	 * @param builder A function that configures the second constraint using a builder
	 * @return A new type with the applied second constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #hour(UnaryOperator)
	 * @see #minute(UnaryOperator)
	 * @see #millisecond(UnaryOperator)
	 * @see #nanosecond(UnaryOperator)
	 */
	@NonNull C second(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
	
	/**
	 * Applies a constraint to the millisecond field of the temporal value.<br>
	 * <p>
	 *     The millisecond field represents the millisecond of second in the range 0-999.<br>
	 *     The constraint is defined using a builder function that configures the allowed millisecond values.
	 * </p>
	 *
	 * @param builder A function that configures the millisecond constraint using a builder
	 * @return A new type with the applied millisecond constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #hour(UnaryOperator)
	 * @see #minute(UnaryOperator)
	 * @see #second(UnaryOperator)
	 * @see #nanosecond(UnaryOperator)
	 */
	@NonNull C millisecond(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
	
	/**
	 * Applies a constraint to the nanosecond field of the temporal value.<br>
	 * <p>
	 *     The nanosecond field represents the nanosecond of second in the range 0-999,999,999.<br>
	 *     The constraint is defined using a builder function that configures the allowed nanosecond values.
	 * </p>
	 *
	 * @param builder A function that configures the nanosecond constraint using a builder
	 * @return A new type with the applied nanosecond constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #hour(UnaryOperator)
	 * @see #minute(UnaryOperator)
	 * @see #second(UnaryOperator)
	 * @see #millisecond(UnaryOperator)
	 */
	@NonNull C nanosecond(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
}
