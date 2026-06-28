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

package net.luis.utils.io.database.function.functions.numeric.bitwise;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlNumericFunction;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the SQL bitwise {@code NOT} function.<br>
 *
 * @author Luis-St
 *
 * @param expression The operand expression
 * @param type The sql type of the result
 *
 * @param <T> The numeric type of the result
 */

public record SqlBitwiseNotFunction<T extends Number>(
	@NonNull SqlExpression<T> expression,
	@NonNull SqlType<T> type
) implements SqlNumericFunction<T> {
	
	/**
	 * Constructs a new bitwise NOT function inferring the sql type from the operand expression.<br>
	 *
	 * @param expression The operand expression
	 * @throws NullPointerException If the expression is null
	 */
	public SqlBitwiseNotFunction(@NonNull SqlExpression<T> expression) {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		
		this(expression, expression.type());
	}
	
	/**
	 * Constructs a new bitwise NOT function with the given operand and sql type.<br>
	 * @throws NullPointerException If the expression or type is null
	 */
	public SqlBitwiseNotFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
