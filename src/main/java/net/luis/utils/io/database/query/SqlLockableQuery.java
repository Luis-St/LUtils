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
import org.jspecify.annotations.NonNull;

/**
 * Mixin interface for SQL select queries that support pessimistic row-level locking.<br>
 * Implemented by both {@link SqlSelectQuery} and {@link SqlSelectProjectionQuery}.<br>
 *
 * @author Luis-St
 *
 * @param <Q> The self-referencing query type for fluent API support
 */
public interface SqlLockableQuery<Q> {
	
	/**
	 * Adds {@code FOR UPDATE} clause to lock selected rows.<br>
	 * Prevents other transactions from modifying or locking the rows until this transaction completes.<br>
	 *
	 * @return This query for method chaining
	 */
	@NonNull Q forUpdate();
	
	/**
	 * Adds {@code SKIP LOCKED} modifier to skip rows that are already locked.<br>
	 * Must be used in combination with {@link #forUpdate()}.<br>
	 * Useful for job queue patterns where multiple workers process available rows.<br>
	 *
	 * @return This query for method chaining
	 */
	@NonNull Q skipLocked();
	
	/**
	 * Adds {@code FOR SHARE} clause to lock selected rows with a shared lock.<br>
	 * Allows other transactions to read the rows but prevents them from acquiring exclusive locks.<br>
	 * Must not be combined with {@link #forUpdate()}.<br>
	 *
	 * @return This query for method chaining
	 */
	@NonNull Q forShare();
	
	/**
	 * Adds {@code NOWAIT} modifier to fail immediately if rows are locked.<br>
	 * Must be used in combination with {@link #forUpdate()}.<br>
	 *
	 * @return This query for method chaining
	 * @throws SqlLockNotAvailableException If the rows are locked by another transaction
	 */
	@NonNull Q noWait() throws SqlLockNotAvailableException;
}
