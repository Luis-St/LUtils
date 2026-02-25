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

import net.luis.utils.io.database.exception.locking.SqlLockNotAvailableException;
import net.luis.utils.io.database.query.async.SqlAsyncSelectQuery;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * Interface representing a SQL select query for entities.<br>
 * This interface extends {@link SqlSelectQueryBase} with entity-specific locking methods for pessimistic concurrency control.<br>
 *
 * @see SqlSelectQueryBase
 * @see SqlSelectProjectionQuery
 *
 * @author Luis-St
 *
 * @param <T> The type of the result entity
 */
public interface SqlSelectQuery<T> extends SqlSelectQueryBase<T, SqlSelectQuery<T>> {
	
	/**
	 * Adds {@code FOR UPDATE} clause to lock selected rows.<br>
	 * Prevents other transactions from modifying or locking the rows until this transaction completes.<br>
	 *
	 * @return This query for method chaining
	 */
	@NonNull SqlSelectQuery<T> forUpdate();
	
	/**
	 * Adds {@code SKIP LOCKED} modifier to skip rows that are already locked.<br>
	 * Must be used in combination with {@link #forUpdate()}.<br>
	 * Useful for job queue patterns where multiple workers process available rows.<br>
	 *
	 * @return This query for method chaining
	 */
	@NonNull SqlSelectQuery<T> skipLocked();
	
	/**
	 * Adds NOWAIT modifier to fail immediately if rows are locked.<br>
	 * Must be used in combination with {@link #forUpdate()}.<br>
	 *
	 * @return This query for method chaining
	 * @throws SqlLockNotAvailableException If the rows are locked by another transaction
	 */
	@NonNull SqlSelectQuery<T> noWait() throws SqlLockNotAvailableException;
	
	/**
	 * Returns an asynchronous view of this query where all terminal operations return {@link CompletableFuture}.<br>
	 * @return The asynchronous query
	 */
	@NonNull SqlAsyncSelectQuery<T> async();
}
