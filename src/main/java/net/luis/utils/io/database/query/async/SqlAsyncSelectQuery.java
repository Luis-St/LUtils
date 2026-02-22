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

package net.luis.utils.io.database.query.async;

import net.luis.utils.io.database.exception.locking.SqlLockNotAvailableException;
import org.jspecify.annotations.NonNull;

/**
 * Interface representing an asynchronous SQL select query for entities.<br>
 * <p>
 *     This interface extends {@link SqlAsyncSelectQueryBase} with entity-specific locking methods for pessimistic concurrency control.<br>
 *     All terminal methods return {@link java.util.concurrent.CompletableFuture} for non-blocking execution.<br>
 * </p>
 *
 * @see SqlAsyncSelectQueryBase
 * @see SqlAsyncSelectProjectionQuery
 *
 * @author Luis-St
 *
 * @param <T> The type of the result entity
 */
public interface SqlAsyncSelectQuery<T> extends SqlAsyncSelectQueryBase<T, SqlAsyncSelectQuery<T>> {

	/**
	 * Adds {@code FOR UPDATE} clause to lock selected rows.<br>
	 * Prevents other transactions from modifying or locking the rows until this transaction completes.<br>
	 *
	 * @return This query for method chaining
	 */
	@NonNull SqlAsyncSelectQuery<T> forUpdate();

	/**
	 * Adds {@code SKIP LOCKED} modifier to skip rows that are already locked.<br>
	 * Must be used in combination with {@link #forUpdate()}.<br>
	 * Useful for job queue patterns where multiple workers process available rows.<br>
	 *
	 * @return This query for method chaining
	 */
	@NonNull SqlAsyncSelectQuery<T> skipLocked();

	/**
	 * Adds NOWAIT modifier to fail immediately if rows are locked.<br>
	 * Must be used in combination with {@link #forUpdate()}.<br>
	 *
	 * @return This query for method chaining
	 * @throws SqlLockNotAvailableException If the rows are locked by another transaction
	 */
	@NonNull SqlAsyncSelectQuery<T> noWait();
}
