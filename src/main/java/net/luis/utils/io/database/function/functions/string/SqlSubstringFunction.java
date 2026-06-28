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
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Represents the SQL {@code SUBSTRING} function.<br>
 * Extracts a substring from the given string expression starting at the given position with an optional length.<br>
 *
 * @author Luis-St
 *
 * @param expression The string expression to extract from
 * @param start The start position of the substring
 * @param length The length of the substring or {@code null} to extract to the end of the string
 * @param <T> The character sequence type of the expression
 */

public record SqlSubstringFunction<T extends CharSequence>(
	@NonNull SqlExpression<T> expression,
	@NonNull SqlExpression<? extends Number> start,
	@Nullable SqlExpression<? extends Number> length
) implements SqlStringFunction<T>, SqlTypedNestedExpression<T> {
	
	/**
	 * Constructs a new substring function with the given expression, start and optional length.<br>
	 * @throws NullPointerException If the expression or start expression is null
	 */
	public SqlSubstringFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(start, "Sql start expression must not be null");
	}
}
