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

package net.luis.utils.io.database.query;

import net.luis.utils.io.database.SqlRenderable;
import net.luis.utils.io.database.condition.SqlCondition;
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.function.SqlExpression;
import net.luis.utils.io.database.table.SqlColumn;
import org.jspecify.annotations.NonNull;

import java.time.temporal.Temporal;
import java.util.List;

/**
 * Interface representing a SQL update query.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlUpdateQuery<T> extends SqlRenderable {
	
	/**
	 * Sets a column to the given value.<br>
	 * Generates SQL: {@code SET column = value}.<br>
	 *
	 * @param column The column to set
	 * @param value The value to set
	 * @param <V> The type of the column value
	 * @return This update query
	 */
	<V> @NonNull SqlUpdateQuery<T> set(@NonNull SqlColumn<V> column, @NonNull V value);
	
	/**
	 * Sets a column to the result of the given expression.<br>
	 * Generates SQL: {@code SET column = expression}.<br>
	 *
	 * @param column The column to set
	 * @param expression The expression to evaluate
	 * @param <V> The type of the column value
	 * @return This update query
	 */
	<V> @NonNull SqlUpdateQuery<T> set(@NonNull SqlColumn<V> column, @NonNull SqlExpression<V> expression);
	
	/**
	 * Increments a numeric column by the given amount.<br>
	 * Generates SQL: {@code SET column = column + amount}.<br>
	 *
	 * @param column The column to increment
	 * @param amount The amount to increment by
	 * @return This update query
	 */
	@NonNull SqlUpdateQuery<T> increment(@NonNull SqlColumn<? extends Number> column, @NonNull Number amount);
	
	/**
	 * Sets a column to the current timestamp.<br>
	 * Generates SQL: {@code SET column = NOW()}.<br>
	 *
	 * @param column The temporal column to set
	 * @return This update query
	 */
	@NonNull SqlUpdateQuery<T> setNow(@NonNull SqlColumn<? extends Temporal> column);
	
	/**
	 * Sets the condition for the update query.<br>
	 * Generates SQL: {@code WHERE condition}.<br>
	 *
	 * @param condition The condition for the update
	 * @return This update query
	 */
	@NonNull SqlUpdateQuery<T> where(@NonNull SqlCondition condition);
	
	/**
	 * Executes the update query.<br>
	 * Generates SQL: {@code UPDATE table SET ... WHERE ...}.<br>
	 *
	 * @return The number of rows updated
	 * @throws SqlException If a database access error occurs
	 */
	int execute() throws SqlException;
	
	/**
	 * Executes the update query and returns the updated entities.<br>
	 * Generates SQL: {@code UPDATE table SET ... WHERE ... RETURNING *}.<br>
	 *
	 * @return The list of updated entities
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull List<T> returning() throws SqlException;
	
}
