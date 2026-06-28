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
 * Represents the SQL {@code TRIM} function with a set of trim characters.<br>
 * Removes the given characters from the leading and trailing ends of the string expression.<br>
 *
 * @author Luis-St
 *
 * @param expression The string expression to trim
 * @param characters The characters to remove from both ends of the expression
 * @param <T> The character sequence type of the expression
 */

public record SqlTrimCharsFunction<T extends CharSequence>(
	@NonNull SqlExpression<T> expression,
	@NonNull SqlExpression<? extends CharSequence> characters
) implements SqlStringFunction<T>, SqlTypedNestedExpression<T> {
	
	/**
	 * Constructs a new trim characters function with the given expression and trim characters.<br>
	 * @throws NullPointerException If the expression or characters expression is null
	 */
	public SqlTrimCharsFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(characters, "Sql characters expression must not be null");
	}
}
