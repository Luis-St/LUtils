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
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents the SQL {@code CAST} function.<br>
 * It converts the value of the given expression to the specified target type.<br>
 *
 * @author Luis-St
 *
 * @param expression The expression whose value is converted
 * @param type The target type to cast the value to
 * @param <T> The type of the casted result value
 */
public record SqlCastFunction<T>(
	@NonNull SqlExpression<?> expression,
	@NonNull SqlType<T> type
) implements SqlFunction<T> {
	
	/**
	 * Constructs a new sql cast function with the given expression and target type.<br>
	 * @throws NullPointerException If the expression or type is null
	 */
	public SqlCastFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(type, "Sql type must not be null");
	}
}
