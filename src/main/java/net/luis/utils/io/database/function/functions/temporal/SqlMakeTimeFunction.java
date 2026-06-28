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

package net.luis.utils.io.database.function.functions.temporal;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlTemporalFunction;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.time.temporal.Temporal;
import java.util.Objects;

/**
 * Represents the SQL {@code MAKE_TIME} function.<br>
 * Builds a time value from its hour, minute and second components.<br>
 *
 * @author Luis-St
 *
 * @param hour The expression providing the hour component
 * @param minute The expression providing the minute component
 * @param second The expression providing the second component
 * @param type The sql type of the result
 * @param <T> The temporal result type of the function
 */

public record SqlMakeTimeFunction<T extends Temporal>(
	@NonNull SqlExpression<? extends Number> hour,
	@NonNull SqlExpression<? extends Number> minute,
	@NonNull SqlExpression<? extends Number> second,
	@NonNull SqlType<T> type
) implements SqlTemporalFunction<T> {
	
	/**
	 * Constructs a new make time function with the given hour, minute, second and sql type.<br>
	 * @throws NullPointerException If the hour, minute, second or sql type is null
	 */
	public SqlMakeTimeFunction {
		Objects.requireNonNull(hour, "Sql hour expression must not be null");
		Objects.requireNonNull(minute, "Sql minute expression must not be null");
		Objects.requireNonNull(second, "Sql second expression must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
