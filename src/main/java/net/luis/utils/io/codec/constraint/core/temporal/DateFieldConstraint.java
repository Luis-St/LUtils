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

package net.luis.utils.io.codec.constraint.core.temporal;

import net.luis.utils.io.codec.constraint_new.builder.EnumConstraintBuilder;
import net.luis.utils.io.codec.constraint_new.builder.NumericConstraintBuilder;
import org.jspecify.annotations.NonNull;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.function.UnaryOperator;

/**
 * Constraint interface for temporal types that provides date field validation operations.<br>
 * <p>
 *     This interface provides methods for constraining individual date fields such as
 *     day of week, day of month, day of year, week of month, week of year, month, and year.<br>
 *     It allows fine-grained validation of date components within temporal values.
 * </p>
 *
 * @author Luis-St
 *
 * @param <T> The type of the constraint configuration
 * @param <C> The return type of the constraint method (for fluent method chaining)
 */
public interface DateFieldConstraint<T, C> {
	
	/**
	 * Applies a constraint to the day of week field of the temporal value.<br>
	 * <p>
	 *     The day of week field represents the day within the week, typically ranging from Monday to Sunday.<br>
	 *     The constraint is defined using a builder function that configures the allowed day values.
	 * </p>
	 *
	 * @param builder A function that configures the day of week constraint using a builder
	 * @return A new type with the applied day of week constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #dayOfMonth(UnaryOperator)
	 * @see #dayOfYear(UnaryOperator)
	 * @see #weekOfMonth(UnaryOperator)
	 * @see #weekOfYear(UnaryOperator)
	 * @see #month(UnaryOperator)
	 * @see #year(UnaryOperator)
	 */
	@NonNull C dayOfWeek(@NonNull UnaryOperator<EnumConstraintBuilder<DayOfWeek>> builder);
	
	/**
	 * Applies a constraint to the day of month field of the temporal value.<br>
	 * <p>
	 *     The day of month field represents the day within the month in the range 1-31.<br>
	 *     The constraint is defined using a builder function that configures the allowed day values.
	 * </p>
	 *
	 * @param builder A function that configures the day of month constraint using a builder
	 * @return A new type with the applied day of month constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #dayOfWeek(UnaryOperator)
	 * @see #dayOfYear(UnaryOperator)
	 * @see #weekOfMonth(UnaryOperator)
	 * @see #weekOfYear(UnaryOperator)
	 * @see #month(UnaryOperator)
	 * @see #year(UnaryOperator)
	 */
	@NonNull C dayOfMonth(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
	
	/**
	 * Applies a constraint to the day of year field of the temporal value.<br>
	 * <p>
	 *     The day of year field represents the day within the year, typically ranging from 1 to 365 (or 366 in leap years).<br>
	 *     The constraint is defined using a builder function that configures the allowed day values.
	 * </p>
	 *
	 * @param builder A function that configures the day of year constraint using a builder
	 * @return A new type with the applied day of year constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #dayOfWeek(UnaryOperator)
	 * @see #dayOfMonth(UnaryOperator)
	 * @see #weekOfMonth(UnaryOperator)
	 * @see #weekOfYear(UnaryOperator)
	 * @see #month(UnaryOperator)
	 * @see #year(UnaryOperator)
	 */
	@NonNull C dayOfYear(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
	
	/**
	 * Applies a constraint to the week of month field of the temporal value.<br>
	 * <p>
	 *     The week of month field represents the week within the month, typically ranging from 1 to 4 (or 5 in some months).<br>
	 *     The constraint is defined using a builder function that configures the allowed week values.
	 * </p>
	 *
	 * @param builder A function that configures the week of month constraint using a builder
	 * @return A new type with the applied week of month constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #dayOfWeek(UnaryOperator)
	 * @see #dayOfMonth(UnaryOperator)
	 * @see #dayOfYear(UnaryOperator)
	 * @see #weekOfYear(UnaryOperator)
	 * @see #month(UnaryOperator)
	 * @see #year(UnaryOperator)
	 */
	@NonNull C weekOfMonth(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
	
	/**
	 * Applies a constraint to the week of year field of the temporal value.<br>
	 * <p>
	 *     The week of year field represents the week within the year, typically ranging from 1 to 52 (or 53 in some years).<br>
	 *     The constraint is defined using a builder function that configures the allowed week values.
	 * </p>
	 *
	 * @param builder A function that configures the week of year constraint using a builder
	 * @return A new type with the applied week of year constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #dayOfWeek(UnaryOperator)
	 * @see #dayOfMonth(UnaryOperator)
	 * @see #dayOfYear(UnaryOperator)
	 * @see #weekOfMonth(UnaryOperator)
	 * @see #month(UnaryOperator)
	 * @see #year(UnaryOperator)
	 */
	@NonNull C weekOfYear(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
	
	/**
	 * Applies a constraint to the month field of the temporal value.<br>
	 * <p>
	 *     The month field represents the month within the year, typically ranging from January to December.<br>
	 *     The constraint is defined using a builder function that configures the allowed month values.
	 * </p>
	 *
	 * @param builder A function that configures the month constraint using a builder
	 * @return A new type with the applied month constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #dayOfWeek(UnaryOperator)
	 * @see #dayOfMonth(UnaryOperator)
	 * @see #dayOfYear(UnaryOperator)
	 * @see #weekOfMonth(UnaryOperator)
	 * @see #weekOfYear(UnaryOperator)
	 * @see #year(UnaryOperator)
	 */
	@NonNull C month(@NonNull UnaryOperator<EnumConstraintBuilder<Month>> builder);
	
	/**
	 * Applies a constraint to the year field of the temporal value.<br>
	 * <p>
	 *     The constraint is defined using a builder function that configures the allowed year values.
	 * </p>
	 *
	 * @param builder A function that configures the year constraint using a builder
	 * @return A new type with the applied year constraint
	 * @throws NullPointerException If the builder function is null
	 * @see #dayOfWeek(UnaryOperator)
	 * @see #dayOfMonth(UnaryOperator)
	 * @see #dayOfYear(UnaryOperator)
	 * @see #weekOfMonth(UnaryOperator)
	 * @see #weekOfYear(UnaryOperator)
	 * @see #month(UnaryOperator)
	 */
	@NonNull C year(@NonNull UnaryOperator<NumericConstraintBuilder> builder);
}
