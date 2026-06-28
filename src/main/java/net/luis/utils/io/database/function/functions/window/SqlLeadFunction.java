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

package net.luis.utils.io.database.function.functions.window;

import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.function.functions.SqlWindowFunction;
import net.luis.utils.io.database.function.window.SqlWindowClause;
import net.luis.utils.io.database.type.SqlType;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Represents the SQL {@code LEAD} window function.<br>
 * It accesses the value of the column from a following row within the window partition,<br>
 * offset by the given number of rows.<br>
 *
 * @author Luis-St
 *
 * @param column The expression whose value is accessed from the following row
 * @param offset The expression defining the number of rows to look ahead, may be null
 * @param defaultValue The value to return if there is no following row, may be null
 * @param over The window clause defining the partitioning and ordering
 * @param <T> The type of the resulting value
 */
public record SqlLeadFunction<T>(
	@NonNull SqlExpression<T> column,
	@Nullable SqlExpression<? extends Number> offset,
	@Nullable SqlExpression<T> defaultValue,
	@NonNull SqlWindowClause over
) implements SqlWindowFunction<T> {
	
	/**
	 * Constructs a new sql lead function with the given column, offset, default value and window clause.<br>
	 * @throws NullPointerException If the column or window clause is null
	 */
	public SqlLeadFunction {
		Objects.requireNonNull(column, "Sql column expression must not be null");
		Objects.requireNonNull(over, "Sql window clause must not be null");
	}
	
	@Override
	public @NonNull SqlType<T> type() {
		return this.column.type();
	}
}
