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
 * Constraint interface for Period types that provides date-based period validation operations.<br>
 * <p>
 *     This interface extends {@link SignedConstraint} with methods for constraining periods
 *     based on their day, month, and year components.<br>
 *     It allows fine-grained validation of date-based period values.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface PeriodConstraint<T, C> extends SignedConstraint<T, C> {
	
	/**
	 * Applies a day constraint to the period using a builder.<br>
	 * <p>
	 *     The returned type will validate that the day component of periods matches
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the day constraint using a numeric constraint builder
	 * @return A new type with the applied day constraint
	 * @throws NullPointerException If the builder is null
	 * @see #month(UnaryOperator)
	 * @see #year(UnaryOperator)
	 */
	@NonNull C day(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
	
	/**
	 * Applies a month constraint to the period using a builder.<br>
	 * <p>
	 *     The returned type will validate that the month component of periods matches
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the month constraint using a numeric constraint builder
	 * @return A new type with the applied month constraint
	 * @throws NullPointerException If the builder is null
	 * @see #day(UnaryOperator)
	 * @see #year(UnaryOperator)
	 */
	@NonNull C month(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
	
	/**
	 * Applies a year constraint to the period using a builder.<br>
	 * <p>
	 *     The returned type will validate that the year component of periods matches
	 *     the constraints defined by the builder.
	 * </p>
	 *
	 * @param builder A function that configures the year constraint using a numeric constraint builder
	 * @return A new type with the applied year constraint
	 * @throws NullPointerException If the builder is null
	 * @see #day(UnaryOperator)
	 * @see #month(UnaryOperator)
	 */
	@NonNull C year(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
}
