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
 * Represents the SQL {@code NULLIF} function.<br>
 * It returns {@code null} if the expression equals the compare value, otherwise it returns the expression.<br>
 *
 * @author Luis-St
 *
 * @param expression The expression to return if it does not equal the compare value
 * @param compareValue The value the expression is compared against
 * @param <T> The type of the resulting value
 */
public record SqlNullIfFunction<T>(
	@NonNull SqlExpression<T> expression,
	@NonNull SqlExpression<T> compareValue
) implements SqlFunction<T> {
	
	/**
	 * Constructs a new sql null-if function with the given expression and compare value.<br>
	 * @throws NullPointerException If the expression or compare value is null
	 */
	public SqlNullIfFunction {
		Objects.requireNonNull(expression, "Sql expression must not be null");
		Objects.requireNonNull(compareValue, "Sql compareValue expression must not be null");
	}
	
	@Override
	public @NonNull SqlType<T> type() {
		return this.expression.type();
	}
}
