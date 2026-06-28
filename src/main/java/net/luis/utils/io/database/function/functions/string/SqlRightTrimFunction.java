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
import net.luis.utils.io.database.expression.SqlTypedNestedExpression;
import net.luis.utils.io.database.function.functions.SqlStringFunction;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the SQL {@code RTRIM} (right trim) function.<br>
 * Removes trailing whitespace from the given string expression.<br>
 *
 * @author Luis-St
 *
 * @param expression The string expression to trim on the right
 * @param <T> The character sequence type of the expression
 */

public record SqlRightTrimFunction<T extends CharSequence>(@NonNull SqlExpression<T> expression) implements SqlStringFunction<T>, SqlTypedNestedExpression<T> {
	
	/**
	 * Constructs a new right trim function with the given expression.<br>
	 * @throws NullPointerException If the expression is null
	 */
	public SqlRightTrimFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
	}
}
