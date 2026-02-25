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
import net.luis.utils.io.database.exception.SqlException;
import net.luis.utils.io.database.query.async.SqlAsyncInsertQuery;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Interface representing a SQL insert query.<br>
 *
 * @author Luis-St
 *
 * @param <T> The type of the entity
 */
public interface SqlInsertQuery<T> extends SqlRenderable {
	
	/**
	 * Returns the parameter values for this query.<br>
	 * @return A list of parameter values in order
	 */
	@NonNull List<Object> getParameters();
	
	/**
	 * Executes the insert query.<br>
	 *
	 * @return The number of rows inserted
	 * @throws SqlException If a database access error occurs
	 */
	int execute() throws SqlException;
	
	/**
	 * Executes the insert query and returns the inserted entities.<br>
	 * Generates SQL: {@code INSERT INTO ... RETURNING *}.<br>
	 *
	 * @return The list of inserted entities
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull List<T> returning() throws SqlException;
	
	/**
	 * Executes the insert query and fetches the inserted entities.<br>
	 *
	 * @return The list of inserted entities
	 * @throws SqlException If a database access error occurs
	 */
	@NonNull List<T> fetchInserted() throws SqlException;
	
	/**
	 * Returns an asynchronous view of this query where all terminal operations return {@link java.util.concurrent.CompletableFuture}.<br>
	 * @return The asynchronous query
	 */
	@NonNull SqlAsyncInsertQuery<T> async();
}
