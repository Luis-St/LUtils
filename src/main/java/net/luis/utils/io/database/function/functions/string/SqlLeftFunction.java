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
 * Represents the SQL {@code LEFT} function.<br>
 * Returns the leftmost characters of the given string expression up to the given length.<br>
 *
 * @author Luis-St
 *
 * @param expression The string expression to take characters from
 * @param length The number of leading characters to return
 * @param <T> The character sequence type of the expression
 */

public record SqlLeftFunction<T extends CharSequence>(
	@NonNull SqlExpression<T> expression,
	@NonNull SqlExpression<? extends Number> length
) implements SqlStringFunction<T>, SqlTypedNestedExpression<T> {
	
	/**
	 * Constructs a new left function with the given expression and length.<br>
	 * @throws NullPointerException If the expression or length expression is null
	 */
	public SqlLeftFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(length, "Sql length expression must not be null");
	}
}
