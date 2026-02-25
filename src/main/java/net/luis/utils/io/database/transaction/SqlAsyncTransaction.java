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

package net.luis.utils.io.database.transaction;

import net.luis.utils.io.database.SqlIsolationLevel;
import net.luis.utils.io.database.exception.SqlTransactionException;
import net.luis.utils.io.database.query.async.SqlAsyncQueryProvider;
import net.luis.utils.io.database.table.SqlTable;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 * Interface representing an asynchronous SQL transaction.<br>
 * <p>
 *     Provides the same operations as {@link SqlTransaction} but with all terminal operations
 *     returning {@link CompletableFuture} for non-blocking execution.<br>
 * </p>
 * <p>
 *     Internally, the transaction is pinned to a single dedicated connection from the pool.
 *     Operations are dispatched to the async executor but are sequential on that connection.<br>
 * </p>
 * <p>
 *     Implements {@link AutoCloseable} to support try-with-resources usage.
 *     If the transaction has not been explicitly committed or rolled back when
 *     {@link #close()} is called, an automatic rollback is performed.<br>
 * </p>
 *
 * @see SqlTransaction
 *
 * @author Luis-St
 */
public interface SqlAsyncTransaction extends AutoCloseable {
	
	/**
	 * Returns an asynchronous query provider bound to the specified table within this transaction.<br>
	 *
	 * @param table The table to query
	 * @param <T> The entity type
	 * @return An async query provider for the given table within this transaction
	 */
	<T> @NonNull SqlAsyncQueryProvider<T> from(@NonNull SqlTable<T> table);
	
	/**
	 * Returns whether this transaction is still active (neither committed nor rolled back).<br>
	 * @return Whether the transaction is active
	 */
	boolean isActive();
	
	/**
	 * Returns whether this transaction has been committed.<br>
	 * @return Whether the transaction was committed
	 */
	boolean isCommitted();
	
	/**
	 * Returns whether this transaction has been rolled back.<br>
	 * @return Whether the transaction was rolled back
	 */
	boolean isRolledBack();
	
	/**
	 * Asynchronously commits the current transaction.<br>
	 * Executes SQL: {@code COMMIT}.<br>
	 *
	 * @return A future that completes when the commit is finished
	 */
	@NonNull CompletableFuture<Void> commit();
	
	/**
	 * Asynchronously rolls back the current transaction.<br>
	 * Executes SQL: {@code ROLLBACK}.<br>
	 *
	 * @return A future that completes when the rollback is finished
	 */
	@NonNull CompletableFuture<Void> rollback();
	
	/**
	 * Asynchronously rolls back the current transaction to the given savepoint.<br>
	 * Executes SQL: {@code ROLLBACK TO SAVEPOINT name}.<br>
	 *
	 * @param savepoint The savepoint to roll back to
	 * @return A future that completes when the rollback is finished
	 */
	@NonNull CompletableFuture<Void> rollbackTo(@NonNull SqlSavepoint savepoint);
	
	/**
	 * Asynchronously creates a savepoint with the given name.<br>
	 * Executes SQL: {@code SAVEPOINT name}.<br>
	 *
	 * @param name The name of the savepoint
	 * @return A future that completes with the created savepoint
	 */
	@NonNull CompletableFuture<SqlSavepoint> savepoint(@NonNull String name);
	
	/**
	 * Sets the transaction to read-only mode.<br>
	 * Executes SQL: {@code SET TRANSACTION READ ONLY}.<br>
	 *
	 * @return This transaction
	 */
	@NonNull SqlAsyncTransaction readOnly();
	
	/**
	 * Sets the timeout for the transaction.<br>
	 *
	 * @param timeout The maximum duration for the transaction
	 * @return This transaction
	 */
	@NonNull SqlAsyncTransaction timeout(@NonNull Duration timeout);
	
	/**
	 * Sets the isolation level for the transaction.<br>
	 * Executes SQL: {@code SET TRANSACTION ISOLATION LEVEL ...}.<br>
	 *
	 * @param level The isolation level
	 * @return This transaction
	 */
	@NonNull SqlAsyncTransaction isolation(@NonNull SqlIsolationLevel level);
	
	/**
	 * Closes this async transaction.<br>
	 * If the transaction is still active (not committed or rolled back),
	 * an automatic rollback is performed synchronously.<br>
	 *
	 * @throws SqlTransactionException If the close or automatic rollback fails
	 */
	@Override
	void close() throws SqlTransactionException;
}

