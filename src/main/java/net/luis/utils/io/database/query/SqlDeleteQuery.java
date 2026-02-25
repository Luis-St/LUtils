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
import net.luis.utils.io.database.query.async.SqlAsyncDeleteQuery;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a SQL delete query.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlDeleteQuery<T> extends SqlRenderable {
	
	/**
	 * Returns the parameter values for this query.<br>
	 * @return A list of parameter values in order
	 */
	@NonNull List<Object> getParameters();
	
	/**
	 * Sets the condition for the delete query.<br>
	 * Generates SQL: {@code WHERE condition}.<br>
	 *
	 * @param condition The condition for the delete
	 * @return This delete query
	 */
	@NonNull SqlDeleteQuery<T> where(@NonNull SqlCondition condition);
	
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

	/**
	 * Returns an asynchronous view of this query where all terminal operations return {@link java.util.concurrent.CompletableFuture}.<br>
	 * @return The asynchronous query
	 */
	@NonNull SqlAsyncDeleteQuery<T> async();
}
