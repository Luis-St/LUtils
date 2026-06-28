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

package net.luis.utils.io.database.function.functions.numeric.trigonometric;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlNumericFunction;
import net.luis.utils.io.database.type.SqlType;
import net.luis.utils.io.database.type.SqlTypes;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the SQL {@code SIN} trigonometric function.<br>
 *
 * @author Luis-St
 *
 * @param expression The operand expression
 */

public record SqlSinFunction(@NonNull SqlExpression<? extends Number> expression) implements SqlNumericFunction<Double> {
	
	/**
	 * Constructs a new sine function with the given operand expression.<br>
	 * @throws NullPointerException If the expression is null
	 */
	public SqlSinFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
	}
	
	@Override
	public @NonNull SqlType<Double> type() {
		return SqlTypes.DOUBLE;
	}
}
