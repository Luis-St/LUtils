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

package net.luis.utils.io.database.query.util;

import net.luis.utils.io.database.Sql;
import net.luis.utils.io.database.expression.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

/**
 * Represents a single column assigned a value or expression, independent of any entity.<br>
 * Used to override a column's value on an otherwise entity-based insert, and as the building block of a non-entity-based,
 * column-value insert.<br>
 *
 * @see SqlColumn
 *
 * @author Luis-St
 *
 * @param <E> The type of the entity the column belongs to
 * @param <C> The type of the value held by the column
 * @param column The column that is assigned a value
 * @param expression The expression evaluating to the assigned value
 */
public record SqlColumnValue<E, C>(
	@NonNull SqlColumn<E, C> column,
	@NonNull SqlExpression<C> expression
) {
	
	/**
	 * Constructs a new sql column value with the given column and expression.<br>
	 * @throws NullPointerException If the column or expression is null
	 */
	public SqlColumnValue {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(expression, "Sql expression must not be null");
	}
	
	/**
	 * Creates a new sql column value assigning the given constant value to the given column.<br>
	 * The value is wrapped in a value expression using the column's sql type.<br>
	 *
	 * @param column The column to assign the value to
	 * @param value The constant value to assign
	 * @return The newly created column value
	 * @throws NullPointerException If the column or value is null
	 * @param <E> The type of the entity the column belongs to
	 * @param <C> The type of the value held by the column
	 */
	public static <E, C> @NonNull SqlColumnValue<E, C> of(@NonNull SqlColumn<E, C> column, @NonNull C value) {
		Objects.requireNonNull(column, "Sql column must not be null");
		Objects.requireNonNull(value, "Value must not be null");
		
		return new SqlColumnValue<>(column, Sql.of(value, column.type()));
	}
	
	/**
	 * Creates a new sql column value assigning the given expression to the given column.<br>
	 *
	 * @param column The column to assign the expression to
	 * @param expression The expression to assign
	 * @return The newly created column value
	 * @throws NullPointerException If the column or expression is null
	 * @param <E> The type of the entity the column belongs to
	 * @param <C> The type of the value held by the column
	 */
	public static <E, C> @NonNull SqlColumnValue<E, C> of(@NonNull SqlColumn<E, C> column, @NonNull SqlExpression<C> expression) {
		return new SqlColumnValue<>(column, expression);
	}
}
