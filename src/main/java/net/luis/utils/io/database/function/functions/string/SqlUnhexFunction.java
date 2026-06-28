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
 * Represents the SQL {@code UNHEX} function.<br>
 * Converts the given hexadecimal string expression back to its original value.<br>
 *
 * @author Luis-St
 *
 * @param expression The hexadecimal string expression to convert
 * @param type The result type of the function
 * @param <T> The type the function evaluates to
 */

public record SqlUnhexFunction<T>(
	@NonNull SqlExpression<? extends CharSequence> expression,
	@NonNull SqlType<T> type
) implements SqlStringFunction<T> {
	
	/**
	 * Constructs a new unhex function with the given expression and result type.<br>
	 * @throws NullPointerException If the expression or type is null
	 */
	public SqlUnhexFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
