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
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a SQL delete query.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlDeleteQuery<T> extends SqlRenderable, SqlJoinable<SqlDeleteQuery<T>> {
	
	/**
	 * Sets the condition for the delete query.<br>
	 * Generates SQL: {@code WHERE condition}.<br>
	 *
	 * @param condition The condition for the delete
	 * @return This delete query
	 */
	@NonNull SqlDeleteQuery<T> where(@NonNull SqlCondition condition);
	
	/**
	 * Sets the maximum number of rows to process per execution.<br>
	 * When set, the delete is applied in repeated executions of at most {@code batchSize} rows each,
	 * looping until no rows remain. The total number of affected rows is the sum across all executions.<br>
	 * <p>
	 *     When combined with {@link #returning()}, results from all batch executions are accumulated
	 *     into a single list before being returned.
	 * </p>
	 *
	 * @param batchSize The maximum number of rows per batch execution (must be positive)
	 * @return This delete query
	 */
	@NonNull SqlDeleteQuery<T> batchSize(int batchSize);
	
	/**
	 * Executes the delete query.<br>
	 * Generates SQL: {@code DELETE FROM table WHERE ...}.<br>
	 *
	 * @return The number of rows deleted
	 * @throws SqlException If a database access error occurs
	 */
	int execute() throws SqlException;
	
	/**
	 * Executes the delete query and returns the deleted rows.<br>
	 * Generates SQL: {@code DELETE FROM table WHERE ... RETURNING *}.<br>
	 *
	 * @return The list of deleted entities
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull List<T> returning() throws SqlException;
	
}
