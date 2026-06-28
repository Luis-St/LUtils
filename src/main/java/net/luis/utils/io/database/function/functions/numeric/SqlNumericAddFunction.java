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

package net.luis.utils.io.database.function.functions.numeric;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.expression.SqlTypedNestedExpression;
import net.luis.utils.io.database.function.functions.SqlNumericFunction;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the SQL numeric addition ({@code +}) function.<br>
 *
 * @author Luis-St
 *
 * @param expression The base expression
 * @param addend The expression to add
 * @param <T> The numeric type of the expression
 */

public record SqlNumericAddFunction<T extends Number>(
	@NonNull SqlExpression<T> expression,
	@NonNull SqlExpression<? extends Number> addend
) implements SqlNumericFunction<T>, SqlTypedNestedExpression<T> {
	
	/**
	 * Constructs a new numeric add function with the given expression and addend.<br>
	 * @throws NullPointerException If the expression or addend is null
	 */
	public SqlNumericAddFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(addend, "Sql addend expression must not be null");
	}
}
