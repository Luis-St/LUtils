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

package net.luis.utils.io.database.function.functions.generic;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.SqlFunction;
import net.luis.utils.io.database.type.SqlType;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Objects;

/**
 * Represents an unsafe raw SQL function.<br>
 * It serves as an escape hatch that embeds a raw SQL expression string together with its arguments,<br>
 * bypassing the type-safe function abstractions.<br>
 *
 * @author Luis-St
 *
 * @param expression The raw SQL expression string
 * @param arguments The list of arguments referenced by the expression
 * @param type The type of the resulting value
 * @param <T> The type of the resulting value
 */
public record SqlUnsafeFunction<T>(
	@NonNull String expression,
	@NonNull @Unmodifiable List<SqlExpression<?>> arguments,
	@NonNull SqlType<T> type
) implements SqlFunction<T> {
	
	/**
	 * Constructs a new sql unsafe function with the given expression, arguments and type.<br>
	 * The arguments list is copied to ensure immutability.<br>
	 *
	 * @throws NullPointerException If the expression, arguments or type is null
	 * @throws IllegalArgumentException If the expression is blank or the arguments contain null expressions
	 */
	public SqlUnsafeFunction {
		Objects.requireNonNull(expression, "Sql expression string must not be null");
		Objects.requireNonNull(arguments, "Sql function arguments must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
		
		if (expression.isBlank()) {
			throw new IllegalArgumentException("Sql expression string must not be blank");
		}
		if (!arguments.isEmpty() && arguments.stream().anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("Sql function arguments must not contain null sql expressions");
		}
		arguments = List.copyOf(arguments);
	}
}
