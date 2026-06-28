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

package net.luis.utils.io.database.function.functions.string;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlStringFunction;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the SQL {@code POSITION} function.<br>
 * Returns the position of the given substring within the given string expression.<br>
 *
 * @author Luis-St
 *
 * @param substring The substring expression to search for
 * @param expression The string expression to search within
 * @param type The numeric result type of the function
 * @param <T> The numeric type the function evaluates to
 */

public record SqlPositionFunction<T extends Number>(
	@NonNull SqlExpression<? extends CharSequence> substring,
	@NonNull SqlExpression<? extends CharSequence> expression,
	@NonNull SqlType<T> type
) implements SqlStringFunction<T> {
	
	/**
	 * Constructs a new position function with the given substring, expression and result type.<br>
	 * @throws NullPointerException If the substring expression, expression or type is null
	 */
	public SqlPositionFunction {
		Objects.requireNonNull(substring, "Sql substring expression must not be null");
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
